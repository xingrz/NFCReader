package us.xingrz.nfc.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;

import us.xingrz.nfc.R;
import us.xingrz.nfc.yct.YctTransaction;

public class TransactionsFragment extends Fragment {

    private View rootView;
    private ReaderActivity activity;

    private TransactionListView transactionListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.transactions_fragment, container, false);
        activity = (ReaderActivity) getActivity();

        transactionListView = (TransactionListView) rootView.findViewById(R.id.transactions);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        transactionListView.clear();

        for (YctTransaction transaction : activity.getYctInfo().getTransactions()) {
            transactionListView.add(transaction);
        }
    }

}