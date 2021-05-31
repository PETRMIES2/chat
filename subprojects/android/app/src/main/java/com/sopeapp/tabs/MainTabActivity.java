package com.sopeapp.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.sope.domain.websocket.WebSocketMessageType;
import com.sopeapp.R;
import com.sopeapp.SopeApplication;
import com.sopeapp.tabs.events.EventFragment;
import com.sopeapp.tabs.general.GeneralFragment;
import com.sopeapp.tabs.popular.PopularFragment;
import com.sopeapp.tabs.tvshow.TvFragment;
import com.sopeapp.websocket.WebSocketMessageManager;

import java.util.ArrayList;
import java.util.List;


public class MainTabActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static int lastSelectedPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintab_activity);
        ((SopeApplication) getApplication()).getRestComponent().inject(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                lastSelectedPage = position;
                String title = viewPager.getAdapter().getPageTitle(position).toString();

                if (title.equals(SopeApplication.getAppContext().getString(R.string.main_category_header_popular))) {
                    WebSocketMessageManager.getTabList(WebSocketMessageType.POPULAR_LIST);
                }
                if (title.equals(SopeApplication.getAppContext().getString(R.string.main_category_header_tv))) {
                    WebSocketMessageManager.getTabList(WebSocketMessageType.TVSHOWS_LIST);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        selectPage(lastSelectedPage);

        WebSocketMessageManager.getTabList(WebSocketMessageType.POPULAR_LIST);
        WebSocketMessageManager.getTabList(WebSocketMessageType.EVENTS_LIST);
        WebSocketMessageManager.getTabList(WebSocketMessageType.GENERAL_LIST);
    }

    private void selectPage(int pageIndex) {
        tabLayout.setScrollPosition(pageIndex, 0f, true);
        viewPager.setCurrentItem(pageIndex);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PopularFragment(), SopeApplication.getAppContext().getString(R.string.main_category_header_popular));
        adapter.addFragment(new GeneralFragment(), SopeApplication.getAppContext().getString(R.string.main_category_header_general));
        adapter.addFragment(new TvFragment(), SopeApplication.getAppContext().getString(R.string.main_category_header_tv));
        adapter.addFragment(new EventFragment(), SopeApplication.getAppContext().getString(R.string.main_category_header_events));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> tabList = new ArrayList<>();
        private final List<String> tabTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return tabList.get(position);
        }

        @Override
        public int getCount() {
            return tabList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            tabList.add(fragment);
            tabTitleList.add(title);
        }

        @Override
        public int getItemPosition(Object object) {
            Fragment tabFragment = (Fragment) object;
            if (getSupportFragmentManager().getFragments().contains(tabFragment))
                return POSITION_NONE;
            else
                return POSITION_UNCHANGED;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}