package us.xingrz.nfc.yct;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class YctInfo implements Parcelable {

    private String id = "";

    private float balance = 0;

    private Date currentMonth;
    private int monthlyTotalCount = 0;
    private int monthlyBusCount = 0;
    private int monthlyMetroCount = 0;

    private Date expiresAt;

    private List<YctTransaction> transactions = new ArrayList<YctTransaction>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public Date getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(Date currentMonth) {
        this.currentMonth = currentMonth;
    }

    public int getMonthlyTotalCount() {
        return monthlyTotalCount;
    }

    public void setMonthlyTotalCount(int monthlyTotalCount) {
        this.monthlyTotalCount = monthlyTotalCount;
    }

    public int getMonthlyBusCount() {
        return monthlyBusCount;
    }

    public void setMonthlyBusCount(int monthlyBusCount) {
        this.monthlyBusCount = monthlyBusCount;
    }

    public int getMonthlyMetroCount() {
        return monthlyMetroCount;
    }

    public void setMonthlyMetroCount(int monthlyMetroCount) {
        this.monthlyMetroCount = monthlyMetroCount;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public List<YctTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<YctTransaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeFloat(balance);

        if (currentMonth == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            parcel.writeLong(currentMonth.getTime());
            parcel.writeInt(monthlyTotalCount);
            parcel.writeInt(monthlyBusCount);
            parcel.writeInt(monthlyMetroCount);
        }

        if (expiresAt == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            parcel.writeLong(expiresAt.getTime());
        }

        parcel.writeTypedList(transactions);
    }

    public static final Creator<YctInfo> CREATOR = new Creator<YctInfo>() {
        @Override
        public YctInfo createFromParcel(Parcel parcel) {
            YctInfo yctInfo = new YctInfo();
            yctInfo.id = parcel.readString();
            yctInfo.balance = parcel.readFloat();

            if (parcel.readInt() != 0) {
                yctInfo.currentMonth = new Date();
                yctInfo.currentMonth.setTime(parcel.readLong());
                yctInfo.monthlyTotalCount = parcel.readInt();
                yctInfo.monthlyBusCount = parcel.readInt();
                yctInfo.monthlyMetroCount = parcel.readInt();
            }

            if (parcel.readInt() != 0) {
                yctInfo.expiresAt = new Date();
                yctInfo.expiresAt.setTime(parcel.readLong());
            }

            parcel.readTypedList(yctInfo.transactions, YctTransaction.CREATOR);

            return yctInfo;
        }

        @Override
        public YctInfo[] newArray(int i) {
            return new YctInfo[0];
        }
    };

}
