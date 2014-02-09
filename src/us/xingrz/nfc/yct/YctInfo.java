package us.xingrz.nfc.yct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class YctInfo {

    private String id = "";

    private float balance = 0;

    private Date currentMonth;

    private int monthlyBusCount = 0;
    private int monthlyMetroCount = 0;
    private int monthlyTotalCount = 0;

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

    public int getMonthlyTotalCount() {
        return monthlyTotalCount;
    }

    public void setMonthlyTotalCount(int monthlyTotalCount) {
        this.monthlyTotalCount = monthlyTotalCount;
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

}
