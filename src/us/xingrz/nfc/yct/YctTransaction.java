package us.xingrz.nfc.yct;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class YctTransaction implements Parcelable {

    private static final String TAG = YctTransaction.class.getSimpleName();

    public static YctTransaction parse(byte[] raw) {
        // offset 0 - 3 is not clear

        // offset 2 == 20 is Charge?
        boolean isCharge = raw[2] == 20;

        // offset 4 - 5 : Amount
        float amount = parseAmount(raw);
        if (!isCharge) amount = -amount;

        // offset 6 - 9 : Terminal ID
        String terminalId = parseTerminalId(raw);

        // offset 10 - 15 : Date
        Date date = parseDate(raw);

        return new YctTransaction(amount, date, terminalId, raw);
    }

    private static String hex(byte[] bytes) {
        String result = "";
        for (byte b : bytes) {
            result += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
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

    public YctTransaction(float amount, Date date, String terminalId, byte[] raw) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(amount);
        parcel.writeLong(date.getTime());
        parcel.writeString(terminalId);
        parcel.writeValue(raw);
    }

    public static final Creator<YctTransaction> CREATOR = new Creator<YctTransaction>() {
        @Override
        public YctTransaction createFromParcel(Parcel parcel) {
            float amount = parcel.readFloat();

            Date date = new Date();
            date.setTime(parcel.readLong());

            String terminalId = parcel.readString();

            byte[] raw = (byte[]) parcel.readValue(byte.class.getClassLoader());

            return new YctTransaction(amount, date, terminalId, raw);
        }

        @Override
        public YctTransaction[] newArray(int i) {
            return new YctTransaction[0];
        }
    };

}
