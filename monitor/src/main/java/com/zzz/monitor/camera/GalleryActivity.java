package com.zzz.monitor.camera;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzz.monitor.R;
import com.zzz.monitor.R2;
import com.zzz.monitor.adapter.GalleryAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GalleryActivity extends AppCompatActivity implements GalleryContract.View {
    @BindView(R2.id.iv_left_monitor)
    ImageView mIvLeft;
    @BindView(R2.id.tv_title_monitor)
    TextView mTvTitle;
    @BindView(R2.id.txt_right_monitor)
    TextView mTvRight;
    @BindView(R2.id.snapshots)
    RecyclerView mRecyclerView;
    @BindView(R2.id.ll_empty_msg)
    LinearLayout mEmptyMsg;
    @BindView(R2.id.iv_empty)
    ImageView mIvEmpty;

    private GalleryPresenter mGalleryPresenter;
    private ProgressDialog mProgressDialog;
    private boolean mLock = false;
    private String mSn;
    private String mStartTime = "";
    private String mEndTime = "";
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTvTitle.setText("相册");
        mTvRight.setText("筛选");

        mGalleryPresenter = new GalleryPresenter(this, this);

        Intent intent = getIntent();
        mSn = intent.getStringExtra("sn");
        mStartTime = intent.getStringExtra("start_time");
        mEndTime = intent.getStringExtra("end_time");

        mGalleryPresenter.getGallery(mSn, mPage, mStartTime, mEndTime);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!mLock && !mRecyclerView.canScrollVertically(1)) {
                    mLock = true;
                    mPage++;
                    mGalleryPresenter.getGallery(mSn, mPage, mStartTime, mEndTime);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        initEvent();
    }

    private void initEvent() {
        mIvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
         mTvRight.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(GalleryActivity.this, GalleryFilterActivity.class);
                 intent.putExtra("sn", mSn);
                 intent.putExtra("start_time", mStartTime);
                 intent.putExtra("end_time", mEndTime);
                 startActivity(intent);
             }
         });
    }


    @Override
    public void setPresenter(GalleryContract.Presenter presenter) {
    }

    @Override
    public void toast(String text) {
        final String msg = text;
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void setAdapter(GalleryAdapter adapter) {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void showProgress(boolean isShow) {
        if (isShow) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("等待服务响应...");
            mProgressDialog.show();
        } else {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showEmptyMsg() {
        if (mPage == 1) {
            mEmptyMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideEmptyMsg() {
    }

    @Override
    public void lock(boolean isLocked) {
        mLock = isLocked;
    }

}
