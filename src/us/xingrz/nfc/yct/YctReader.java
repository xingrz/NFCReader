package us.xingrz.nfc.yct;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;

import java.io.IOException;
import java.util.*;

public class YctReader {

    private static final String TAG = YctReader.class.getSimpleName();

    private NfcAdapter nfcAdapter;

    public YctReader(NfcAdapter nfcAdapter) {
        this.nfcAdapter = nfcAdapter;
    }

    public List<YctRecord> readTag(Tag tag) throws IOException {
        MifareClassic mifare = MifareClassic.get(tag);
        if (mifare == null) {
            Log.e(TAG, "Can't get tag");
            return null;
        }

        mifare.connect();

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

        return records;
    }

}
