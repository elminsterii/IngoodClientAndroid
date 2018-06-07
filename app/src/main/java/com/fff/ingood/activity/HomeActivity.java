package com.fff.ingood.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.fff.ingood.R;
import com.fff.ingood.adapter.ActivityListAdapter;
import com.fff.ingood.data.Activity;
import com.fff.ingood.flow.FlowManager;

import java.util.ArrayList;

/**
 * Created by yoie7 on 2018/5/24.
 */

public class HomeActivity extends BaseActivity {

    private RecyclerView mRecyclerViewActivityList;
    private RecyclerView.Adapter mActivityListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout mDrawerLayoutMenu;
    private NavigationView mNavigationViewMenu;
    private ImageView mImageViewMenuBtn;
    private TabLayout mTabLayoutTagBar;

    ArrayList<Activity> m_lsActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_homepage);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();

        mRecyclerViewActivityList = findViewById(R.id.recycleViewActivityList);
        mDrawerLayoutMenu = findViewById(R.id.layoutDrawer);
        mNavigationViewMenu = findViewById(R.id.nvView);
        mImageViewMenuBtn = findViewById(R.id.imageViewMenuBtn);
        mTabLayoutTagBar = findViewById(R.id.tabLayout_TagBar);
    }

    @Override
    protected void initData() {
        super.initData();

        m_lsActivities = new ArrayList<>();

        //@@ test code
        final int TEST_SIZE = 20;
        for(int i=0; i<TEST_SIZE; i++)
            m_lsActivities.add(new Activity());

        //@@ test code
        mTabLayoutTagBar.addTab(mTabLayoutTagBar.newTab().setText("Sport"));
        mTabLayoutTagBar.addTab(mTabLayoutTagBar.newTab().setText("Music"));
        mTabLayoutTagBar.addTab(mTabLayoutTagBar.newTab().setText("Culture"));

        mLayoutManager = new LinearLayoutManager(this);
        mActivityListAdapter = new ActivityListAdapter(m_lsActivities);

        mRecyclerViewActivityList.setLayoutManager(mLayoutManager);
        mRecyclerViewActivityList.setNestedScrollingEnabled(true);
        mRecyclerViewActivityList.setHasFixedSize(true);
        mRecyclerViewActivityList.setAdapter(mActivityListAdapter);
    }

    @Override
    protected void initListener() {
        super.initListener();

        mRecyclerViewActivityList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mNavigationViewMenu.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);

                        switch(menuItem.getItemId()) {
                            case R.id.menuItemPersonal :
                                //TODO - go to personal information page
                                break;
                            case R.id.menuItemLogout :
                                FlowManager.getInstance().goLogoutFlow(mActivity);
                                break;
                        }

                        mDrawerLayoutMenu.closeDrawers();
                        return true;
                    }
                });

        mDrawerLayoutMenu.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        mImageViewMenuBtn.setClickable(true);
        mImageViewMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mDrawerLayoutMenu.isDrawerOpen(GravityCompat.START))
                    mDrawerLayoutMenu.openDrawer(Gravity.LEFT);
            }
        });
    }
}
