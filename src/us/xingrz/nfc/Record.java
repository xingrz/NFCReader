package us.xingrz.nfc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Record {

    public enum Mean {
        BUS,
        METRO
    }

    public static Record parse(byte[] raw) {
        // offset 0 - 3 is not clear

        // offset 0 or 1 : Mean?
        Mean mean = raw[0] == 0x00 ? Mean.BUS : Mean.METRO;

        // offset 2 == 20 is Charge?
        boolean isCharge = raw[2] == 20;

        // offset 4 - 5 : Amount
        float amount = parseAmount(raw);
        if (!isCharge) amount = -amount;

        // offset 6 - 9 : Terminal ID
        String terminalId = parseTerminalId(raw);

        // offset 10 - 15 : Date
        Date date = parseDate(raw);

        return new Record(mean, amount, date, terminalId, Arrays.copyOfRange(raw, 0, 4));
    }

    private static float parseAmount(byte[] bytes) {
        return ((0xff & bytes[4]) << 8 | (0xff & bytes[5])) / 100F;
    }

    private static String parseTerminalId(byte[] bytes) {
        final int offset = 6;
        final int length = 4;

        final char a[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'
        };

        char ac[] = new char[length * 2];

        for (int i = 0; i < length; i++) {
            byte byte0 = bytes[offset + i];
            ac[i * 2] = a[0xf & byte0 >> 4];
            ac[i * 2 + 1] = a[0xf & byte0];
        }

        return new String(ac);
    }

    private static Date parseDate(byte[] bytes) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return format.parse(String.format("20%02x/%02x/%02x %02x:%02x:%02x", bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15]));
        } catch (ParseException e) {
            return new Date();
        }
    }

    private Mean mean;
    private float amount;
    private Date date;
    private String terminalId;
    private byte[] raw;

    public Record(Mean mean, float amount, Date date, String terminalId, byte[] raw) {
        this.mean = mean;
        this.amount = amount;
        this.date = date;
        this.terminalId = terminalId;
        this.raw = raw;
    }

    public Mean getMean() {
        return mean;
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
