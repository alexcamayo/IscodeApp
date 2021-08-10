package com.proyecto.iscodeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.proyecto.iscodeapp.Fragments.ChatsFragment;
import com.proyecto.iscodeapp.Fragments.UsersFragment;

import java.util.ArrayList;

public class NotificacionesActivity extends AppCompatActivity {

    //ActionBar actionBar;
    TabLayout tabLayout;
    ViewPager viewPager;

    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);
        //actionBar = getSupportActionBar();
        //actionBar.setTitle("Notificaciones");

        //Notificaciones
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        //updateToken(FirebaseInstallations.getInstance().getToken(false).toString());

        //ventana de chats y usuarios
        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_pager);
        NotificacionesActivity.ViewPagerAdapter viewPagerAdapter= new NotificacionesActivity.ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ChatsFragment(),"Chats");
        viewPagerAdapter.addFragment(new UsersFragment(),"Personas");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            new FragmentPagerAdapter(fm, 1) {
                @Override
                public int getCount() {
                    return 0;
                }

                @NonNull
                @Override
                public Fragment getItem(int position) {
                    return null;
                }
            };
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}