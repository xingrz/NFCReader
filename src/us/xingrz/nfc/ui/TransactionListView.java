package us.xingrz.nfc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import us.xingrz.nfc.R;
import us.xingrz.nfc.yct.YctTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TransactionListView extends ListView {

    public class RecordAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return records.size();
        }

        @Override
        public Object getItem(int i) {
            return records.get(i);
        }

        @Override
        public long getItemId(int i) {
            return (long) i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Context context = getContext();

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.transactions_list_item, viewGroup, false);
            }

            YctTransaction record = records.get(i);

            ((TextView) view.findViewById(R.id.type)).setText(formatMean(context, record.getRaw()));
            ((TextView) view.findViewById(R.id.amount)).setText(formatAmount(record.getAmount()));
            ((TextView) view.findViewById(R.id.date)).setText(formatDate(record.getDate()));
            ((TextView) view.findViewById(R.id.terminal)).setText(record.getTerminalId());
            ((TextView) view.findViewById(R.id.raw)).setText(formatRaw(record.getRaw()));

            return view;
        }

        private String formatRaw(byte[] bytes) {
            String result = "";
            for (byte b : Arrays.copyOfRange(bytes, 0, 4)) {
                result += Integer.toString((b & 0xff) + 0x100, 16).substring(1) + " ";
            }
            return result.trim();
        }

        private String formatAmount(float price) {
            String result = String.valueOf(price);

            if (result.substring(result.indexOf(".") + 1).length() == 1) {
                result += "0";
            }

            if (price > 0) {
                result = "+" + result;
            }

            return result;
        }

        private String formatDate(Date date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }

        private String formatMean(Context context, byte[] record) {
            if (record[0] == 0x00 && record[1] == 0x00 && record[2] == 0x17) {
                return context.getString(R.string.record_type_shop);
            }

            if (record[0] == 0x00 && record[1] == 0x01 && record[2] == 0x17) {
                return context.getString(R.string.record_type_bus);
            }

            if (record[0] == 0x10 && record[1] == 0x00 && record[2] == 0x12) {
                return context.getString(R.string.record_type_metro);
            }

            return context.getString(R.string.unknown);
        }

    }

    private List<YctTransaction> records = new ArrayList<YctTransaction>();
    private RecordAdapter recordAdapter = new RecordAdapter();

    public TransactionListView(Context context) {
        super(context);
        init();
    }

    public TransactionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TransactionListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        clear();
        setAdapter(recordAdapter);
    }

    public void add(YctTransaction record) {
        records.add(record);
        recordAdapter.notifyDataSetChanged();
    }

    public void clear() {
        records.clear();
        recordAdapter.notifyDataSetChanged();
    }

}
