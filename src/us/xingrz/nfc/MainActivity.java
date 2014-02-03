package us.xingrz.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.*;
import android.nfc.tech.*;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity {

    private static final String TAG = "NFCReader";

    private NfcAdapter nfcAdapter;

    private View hint;
    private RecordListView records;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        hint = findViewById(R.id.hint);
        records = (RecordListView) findViewById(R.id.records);

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.i(TAG, "Tag: " + String.valueOf(tag));

            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            Log.i(TAG, "Id: " + hex(id));

            try {
                MifareClassic mifare = MifareClassic.get(tag);
                if (mifare == null) {
                    Log.e(TAG, "Fuck");
                    return;
                }

                mifare.connect();

                ArrayList<byte[]> blocks = new ArrayList<byte[]>();

                for (int i = 12; i < 16; i++) {
                    // Key-A is a secret, get it your self bitch.
                    mifare.authenticateSectorWithKeyA(i, SectorKeyA.KEY);
                    int block = mifare.sectorToBlock(i);
                    for (int j = block; j < block + 3; j++) {
                        byte[] data = mifare.readBlock(j);
                        blocks.add(data);
                    }
                }

                records.clear();

                for (byte[] record : blocks) {
                    records.add(Record.parse(record));
                }

                records.setVisibility(View.VISIBLE);
                hint.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String hex(byte[] bytes) {
        String result = "";
        for (byte b : bytes) {
            result += Integer.toString((b & 0xff) + 0x100, 16).substring(1) + " ";
        }
        return result.trim();
    }

}
