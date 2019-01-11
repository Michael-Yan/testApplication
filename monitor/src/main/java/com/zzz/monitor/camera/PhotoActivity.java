package com.zzz.monitor.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.zzz.monitor.R;
import com.zzz.monitor.adapter.PhotoAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class PhotoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_act);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int current = intent.getIntExtra("current", 0);
        ArrayList snapshots = intent.getStringArrayListExtra("snapshots");
        PhotoAdapter photoAdapter = new PhotoAdapter(snapshots);

        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(photoAdapter);
        viewPager.setCurrentItem(current);
    }
}
