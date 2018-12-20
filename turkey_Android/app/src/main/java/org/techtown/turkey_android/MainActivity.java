package org.techtown.turkey_android;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;


import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    MenuItem prevMenuItem;
    FragmentA fragment_A;
    FragmentB fragment_B;
    FragmentC fragment_C;
    static final int G_NOTIFY_NUM = 1;
    private ViewPager mViewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fr = null;
            FragmentManager fm =null;
            FragmentTransaction fragmentTransaction=null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mViewPager.setCurrentItem(0);
                    return true ;

                case R.id.navigation_dashboard:
                    mViewPager.setCurrentItem(1);
                    return true;

                case R.id.navigation_notifications:
                    mViewPager.setCurrentItem(2);
                    return true;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(
                getApplicationContext(),//현재제어권자
                VacancyNotification.class); // 이동할 컴포넌트
        startService(intent); // 서비스 시작

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mViewPager=(ViewPager)findViewById(R.id.fragment_container);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(prevMenuItem!=null){
                    prevMenuItem.setChecked(false);
                }
                else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(i).setChecked(true);
                prevMenuItem=navigation.getMenu().getItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        setupViewPager(mViewPager);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragment_A = new FragmentA();
        fragment_B = new FragmentB();
        fragment_C = new FragmentC();
        viewPagerAdapter.addFragment(fragment_A);
        viewPagerAdapter.addFragment(fragment_B);
        viewPagerAdapter.addFragment(fragment_C);
        viewPager.setAdapter(viewPagerAdapter);
    }


}
