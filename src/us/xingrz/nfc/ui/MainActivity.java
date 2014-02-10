package us.xingrz.nfc.ui;

import android.app.Activity;
import android.content.Intent;
import android.nfc.*;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import us.xingrz.nfc.R;
import us.xingrz.nfc.yct.YctInfo;
import us.xingrz.nfc.yct.YctTransaction;
import us.xingrz.nfc.yct.YctReader;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {

    private static final String TAG = "NFCReader";

    NfcAdapter nfcAdapter;
    YctReader yctReader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /* setup nfc adapter */
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.toast_no_nfc), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        /* setup yct reader */
        yctReader = new YctReader(nfcAdapter);

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.i(TAG, "Tag: " + String.valueOf(tag));

            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            Log.i(TAG, "ID: " + hex(id));

            YctInfo yctInfo;

            try {
                yctInfo = yctReader.read(tag);
            } catch (IOException e) {
                Log.e(TAG, String.format("Failed to read card %s", hex(id)), e);
                Toast.makeText(this, R.string.toast_read_failed, Toast.LENGTH_LONG).show();
                return;
            }

            if (yctInfo == null || TextUtils.isEmpty(yctInfo.getId()) || yctInfo.getTransactions() == null) {
                Toast.makeText(this, R.string.toast_read_failed, Toast.LENGTH_LONG).show();
                return;
            }

            vibrate();

            Intent resultIntent = new Intent(this, ReaderActivity.class);
            resultIntent.putExtra("result", yctInfo);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(resultIntent);
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(100);
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
