package us.xingrz.nfc.yct;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class YctRecord {

    private static final String TAG = YctRecord.class.getSimpleName();

    public static YctRecord parse(byte[] raw) {
        // offset 0 - 3 is not clear

        // offset 0 or 1 : Mean?
        //Mean mean = raw[0] == 0x00 ? Mean.BUS : Mean.METRO;

        // offset 2 == 20 is Charge?
        boolean isCharge = raw[2] == 20;

        // offset 4 - 5 : Amount
        float amount = parseAmount(raw);
        if (!isCharge) amount = -amount;

        // offset 6 - 9 : Terminal ID
        String terminalId = parseTerminalId(raw);

        // offset 10 - 15 : Date
        Date date = parseDate(raw);

        Log.d(TAG, String.format(
                "Parsed: YctRecord[%s amount/%s terminal/%s date/%s]",
                hex(Arrays.copyOfRange(raw, 0, 4)),
                formatAmount(amount),
                terminalId,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
        ));

        return new YctRecord(amount, date, terminalId, raw);
    }

    private static String hex(byte[] bytes) {
        String result = "";
        for (byte b : bytes) {
            result += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    private static String formatAmount(float price) {
        String result = String.valueOf(price);

        if (result.substring(result.indexOf(".") + 1).length() == 1) {
            result += "0";
        }

        if (price > 0) {
            result = "+" + result;
        }

        return result;
    }

    private static float parseAmount(byte[] bytes) {
        return ((0xff & bytes[4]) << 8 | (0xff & bytes[5])) / 100F;
    }

    private static String parseTerminalId(byte[] bytes) {
        return hex(Arrays.copyOfRange(bytes, 6, 10));
    }

    private static Date parseDate(byte[] bytes) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return format.parse(String.format("20%02x/%02x/%02x %02x:%02x:%02x", bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15]));
        } catch (ParseException e) {
            return new Date();
        }
    }

    private float amount;
    private Date date;
    private String terminalId;
    private byte[] raw;

    public YctRecord(float amount, Date date, String terminalId, byte[] raw) {
        this.amount = amount;
        this.date = date;
        this.terminalId = terminalId;
        this.raw = raw;
    }

    public float getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public byte[] getRaw() {
        return raw;
    }
}
