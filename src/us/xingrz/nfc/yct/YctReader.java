package us.xingrz.nfc.yct;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class YctReader {

    private static final String TAG = YctReader.class.getSimpleName();

    private NfcAdapter nfcAdapter;

    public YctReader(NfcAdapter nfcAdapter) {
        this.nfcAdapter = nfcAdapter;
    }

    public YctInfo read(Tag tag) throws IOException {
        MifareClassic mifare = MifareClassic.get(tag);
        if (mifare == null) {
            Log.e(TAG, "Can't get tag");
            return null;
        }

        mifare.connect();

        YctInfo info = new YctInfo();

        byte[] buffer;
        DateFormat date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        /* read id */
        try {
            mifare.authenticateSectorWithKeyA(1, YctPam.KEY_A_BASIC);
            buffer = mifare.readBlock(mifare.sectorToBlock(1));
            info.setId(hex(Arrays.copyOfRange(buffer, 5, 10)));
        } catch (IOException e) {
            Log.w(TAG, "Failed reading id", e);
        }

        /* read balance */
        try {
            mifare.authenticateSectorWithKeyA(2, YctPam.KEY_A_BALANCE);
            buffer = mifare.readBlock(mifare.sectorToBlock(2) + 1);
            info.setBalance((
                    (buffer[0] & 0xff) |
                    (buffer[1] & 0xff) << 8 |
                    (buffer[2] & 0xff) << 16 |
                    (buffer[3] & 0xff) << 24
            ) / 100f);
        } catch (IOException e) {
            Log.w(TAG, "Failed reading balance", e);
        }

        /* read expires */
        try {
            mifare.authenticateSectorWithKeyA(3, YctPam.KEY_A_EXPIRES);
            buffer = mifare.readBlock(mifare.sectorToBlock(3));
            info.setExpiresAt(date.parse(String.format("20%02x/%02x/%02x 00:00:00", buffer[3], buffer[4], buffer[5])));
        } catch (IOException e) {
            Log.w(TAG, "Failed reading expires", e);
        } catch (ParseException e) {
            Log.w(TAG, "Failed parsing date of expires");
        }

        /* read usages */
        try {
            mifare.authenticateSectorWithKeyA(4, YctPam.KEY_A_USAGES);
            buffer = mifare.readBlock(mifare.sectorToBlock(4));
            info.setCurrentMonth(date.parse(String.format("20%02x/%02x/01 00:00:00", buffer[11], buffer[12])));
            info.setMonthlyBusCount(buffer[13] & 0xff);
            info.setMonthlyMetroCount(buffer[14] & 0xff);
            info.setMonthlyTotalCount(buffer[15] & 0xff);
        } catch (IOException e) {
            Log.w(TAG, "Failed reading usages", e);
        } catch (ParseException e) {
            Log.w(TAG, "Failed parsing current month");
        }

        /* read transactions */
        List<YctTransaction> transactions = new ArrayList<YctTransaction>();

        for (int sector = 12; sector < 16; sector++) {
            try {
                mifare.authenticateSectorWithKeyA(sector, YctPam.KEY_A_TRANSACTIONS);
                int first = mifare.sectorToBlock(sector);
                for (int block = first; block < first + 3; block++) {
                    try {
                        transactions.add(YctTransaction.parse(mifare.readBlock(block)));
                    } catch (IOException e) {
                        Log.w(TAG, String.format("Failed reading transaction block %s of sector %d", block, sector), e);
                    }
                }
            } catch (IOException e) {
                Log.w(TAG, String.format("Failed to authenticate transaction sector %d", sector), e);
            }
        }

        Collections.sort(transactions, new Comparator<YctTransaction>() {
            @Override
            public int compare(YctTransaction record1, YctTransaction record2) {
                long time1 = record1.getDate().getTime();
                long time2 = record2.getDate().getTime();

                if (time1 > time2) {
                    return -1;
                } else if (time1 < time2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        info.setTransactions(transactions);

        return info;
    }

    private String hex(byte[] bytes) {
        String result = "";
        for (byte b : bytes) {
            result += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

}
