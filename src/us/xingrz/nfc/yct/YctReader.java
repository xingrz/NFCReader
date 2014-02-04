package us.xingrz.nfc.yct;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;

import java.io.IOException;
import java.util.*;

public class YctReader {

    private static final String TAG = YctReader.class.getSimpleName();

    public static class YctReadResult {

        private String id;
        private List<YctRecord> records;

        public YctReadResult(String id, List<YctRecord> records) {
            this.id = id;
            this.records = records;
        }

        public String getId() {
            return id;
        }

        public List<YctRecord> getRecords() {
            return records;
        }

    }

    private NfcAdapter nfcAdapter;

    public YctReader(NfcAdapter nfcAdapter) {
        this.nfcAdapter = nfcAdapter;
    }

    public YctReadResult read(Tag tag) throws IOException {
        MifareClassic mifare = MifareClassic.get(tag);
        if (mifare == null) {
            Log.e(TAG, "Can't get tag");
            return null;
        }

        mifare.connect();

        mifare.authenticateSectorWithKeyA(1, SectorKeyA.KEY);
        byte[] sector1 = mifare.readBlock(mifare.sectorToBlock(1));
        String id = hex(Arrays.copyOfRange(sector1, 5, 10));

        List<YctRecord> records = new ArrayList<YctRecord>();

        for (int sector = 12; sector < 16; sector++) {
            // Key-A is a secret, get it your self bitch.
            mifare.authenticateSectorWithKeyA(sector, SectorKeyA.KEY);

            int first = mifare.sectorToBlock(sector);

            for (int block = first; block < first + 3; block++) {
                records.add(YctRecord.parse(mifare.readBlock(block)));
            }
        }

        Collections.sort(records, new Comparator<YctRecord>() {
            @Override
            public int compare(YctRecord record1, YctRecord record2) {
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

        return new YctReadResult(id, records);
    }

    private String hex(byte[] bytes) {
        String result = "";
        for (byte b : bytes) {
            result += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

}
