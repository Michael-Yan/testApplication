package com.zzz.monitor.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.zzz.monitor.R;
import com.zzz.monitor.R2;
import com.zzz.monitor.adapter.CameraAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeActivity extends AppCompatActivity implements HomeContract.View {
    private final int REQUEST_CODE = 10000;
    HomePresenter mHomePresenter;
    @BindView(R2.id.tv_title_monitor)
    TextView mTvTitle;
    @BindView(R2.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;
    @BindView(R2.id.cameras)
    RecyclerView mRecyclerView;
    @BindView(R2.id.ll_empty_msg)
    LinearLayout mEmptyMsg;
    @BindView(R2.id.iv_empty)
    ImageView mIvEmpty;
    private MaterialDialog mFilterDialog;
    private boolean mVisible = false;
    private int mPage = 1;
    private int mWhich = 0;
    private String mKeyword = "";
    private boolean mLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_act);
        ButterKnife.bind(this);

        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ||
                PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }

        mHomePresenter = new HomePresenter(this, this);
//        mHomePresenter.getToken();
        init();
    }

    @Override
    protected void onResume() {
        mVisible = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        mVisible = false;
        super.onPause();
    }

    private void initDialogs() {
        mFilterDialog = new MaterialDialog.Builder(this)
                .title("搜索过滤")
                .items("全部", "在线")
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        mWhich = which;
                        mPage = 1;
                        mHomePresenter.getDevices(HomeActivity.this,1, mWhich, mKeyword);
                        return true;
                    }
                })
                .positiveText("选择")
                .build();
    }

    private void initViews() {
        mTvTitle.setText("实时监控列表");
        mSwipeContainer.setColorSchemeResources(R.color.md_orange_500, R.color.md_green_500, R.color.md_blue_500);
        SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                mHomePresenter.getDevices(HomeActivity.this,1, mWhich, mKeyword);
            }
        };

        mSwipeContainer.setOnRefreshListener(refreshListener);
        mSwipeContainer.setRefreshing(true);

        refreshListener.onRefresh();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!mLock && !mRecyclerView.canScrollVertically(1)) {
                    mSwipeContainer.setRefreshing(true);
                    mLock = true;
                    mPage++;
                    mHomePresenter.getDevices(HomeActivity.this,mPage, mWhich, mKeyword);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void init() {
        initDialogs();
        initViews();
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {

    }

    @Override
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setAdapter(CameraAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void hideSwipe() {
        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public void showEmptyMsg() {
        if (mWhich == 0 && mKeyword.length() == 0) {
            mIvEmpty.setImageResource(R.drawable.empty);
        } else {
            mIvEmpty.setImageResource(R.drawable.empty_filter);
        }

        mEmptyMsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyMsg() {
        if (mWhich == 0 && mKeyword.length() == 0) {
            mIvEmpty.setImageResource(R.drawable.empty);
        } else {
            mIvEmpty.setImageResource(R.drawable.empty_filter);
        }

        mEmptyMsg.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    @Override
    public void lock(boolean isLocked) {
        mLock = isLocked;
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();

            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                v.setFocusable(false);
                v.setFocusableInTouchMode(true);

                return true;
            }
        }

        return false;
    }

    @OnClick(R2.id.iv_left_monitor)
    public void onViewClicked() {
        finish();
    }
}
