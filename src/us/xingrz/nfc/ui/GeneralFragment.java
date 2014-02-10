package us.xingrz.nfc.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;

import android.widget.TextView;
import us.xingrz.nfc.R;
import us.xingrz.nfc.yct.YctInfo;

import java.text.SimpleDateFormat;

public class GeneralFragment extends Fragment {

    private View rootView;
    private ReaderActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.general_fragment, container, false);
        activity = (ReaderActivity) getActivity();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        YctInfo result = activity.getYctInfo();

        ((TextView) rootView.findViewById(R.id.detail_id)).setText(result.getId());
        ((TextView) rootView.findViewById(R.id.detail_balance)).setText(formatBalance(result.getBalance()));

        ((TextView) rootView.findViewById(R.id.detail_expires)).setText(
                result.getExpiresAt() == null
                        ? getString(R.string.unknown)
                        : new SimpleDateFormat("yyyy-MM-dd").format(result.getExpiresAt())
        );

        ((TextView) rootView.findViewById(R.id.detail_month)).setText(
                result.getCurrentMonth() == null
                        ? getString(R.string.unknown)
                        : new SimpleDateFormat("yyyy-MM").format(result.getCurrentMonth())
        );

        ((TextView) rootView.findViewById(R.id.detail_count_bus)).setText(
                result.getCurrentMonth() == null
                        ? getString(R.string.unknown)
                        : String.valueOf(result.getMonthlyBusCount())
        );

        ((TextView) rootView.findViewById(R.id.detail_count_metro)).setText(
                result.getCurrentMonth() == null
                        ? getString(R.string.unknown)
                        : String.valueOf(result.getMonthlyMetroCount())
        );

        ((TextView) rootView.findViewById(R.id.detail_count)).setText(
                result.getCurrentMonth() == null
                        ? getString(R.string.unknown)
                        : String.valueOf(result.getMonthlyTotalCount())
        );
    }

    private String formatBalance(float price) {
        String result = String.valueOf(price);

        if (result.substring(result.indexOf(".") + 1).length() == 1) {
            result += "0";
        }

        return result;
    }

}