package us.xingrz.nfc.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.support.v4.app.*;
import android.support.v4.view.ViewPager;

import android.view.MenuItem;
import us.xingrz.nfc.R;

public class ReaderActivity extends FragmentActivity
        implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

    ViewPager viewPager;
    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_activity);

        viewPager = (ViewPager) findViewById(R.id.reader_pager);
        viewPager.setAdapter(new ResultFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(this);

        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);

        for (String text : getResources().getStringArray(R.array.modules)) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(text)
                            .setTabListener(this)
            );
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // no-op
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // no-op
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        // no-op
    }

    @Override
    public void onPageSelected(int i) {
        actionBar.setSelectedNavigationItem(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        // no-op
    }

    private class ResultFragmentPagerAdapter extends FragmentPagerAdapter {

        public ResultFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new GeneralFragment();
                case 1:
                    return new TransactionsFragment();
                case 2:
                    return new MetroFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }
    }

}