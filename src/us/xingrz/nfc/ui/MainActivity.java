package us.xingrz.nfc.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.*;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import us.xingrz.nfc.R;
import us.xingrz.nfc.yct.YctRecord;
import us.xingrz.nfc.yct.YctReader;

import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "NFCReader";

    private NfcAdapter nfcAdapter;
    private YctReader yctReader;

    private Vibrator vibrator;

    private RecordListView recordListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.toast_no_nfc), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        yctReader = new YctReader(nfcAdapter);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        recordListView = (RecordListView) findViewById(R.id.records);

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            recordListView.setVisibility(View.GONE);

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.i(TAG, "Tag: " + String.valueOf(tag));

            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            Log.i(TAG, "ID: " + hex(id));

            setTitle(hex(id));

            List<YctRecord> records;

            try {
                records = yctReader.readTag(tag);
            } catch (IOException e) {
                Log.e(TAG, String.format("Failed to read card %s", hex(id)), e);
                Toast.makeText(this, R.string.toast_read_failed, Toast.LENGTH_LONG).show();
                return;
            }

            if (records == null) {
                return;
            }

            recordListView.clear();
            for (YctRecord record : records) {
                recordListView.add(record);
            }

            recordListView.setVisibility(View.VISIBLE);

            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(100);
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
