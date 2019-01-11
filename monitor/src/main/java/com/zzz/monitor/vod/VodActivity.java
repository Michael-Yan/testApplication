package com.zzz.monitor.vod;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;
import com.zzz.monitor.R;
import com.zzz.monitor.R2;
import com.zzz.monitor.Timeline;
import com.zzz.monitor.bean.Snippet;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VodActivity extends AppCompatActivity implements VodContract.View {
    @BindView(R2.id.rl_monitor)
    RelativeLayout mRlTitle;
    @BindView(R2.id.tv_title_monitor)
    TextView mTvTitle;
    @BindView(R2.id.fl_wrap)
    FrameLayout mWrap;
    @BindView(R2.id.video_view)
    PLVideoTextureView mVideoView;
    @BindView(R2.id.ll_loading)
    LinearLayout mLoading;
    @BindView(R2.id.iv_cover)
    ImageView mCover;
    @BindView(R2.id.ib_fullscreen_open)
    ImageView mFullscreenOpen;
    @BindView(R2.id.ib_fullscreen_close)
    ImageView mFullscreenClose;
    @BindView(R2.id.timeline)
    Timeline mTimeline;

    private ProgressDialog mProgressDialog;
    private VodPresenter mVodPresenter;
    private String mVodUrl;
    private String mSn;
    private boolean mMic = true;
    private String mUniqueId;
    private Timer mTimer;
    private int mStart;
    private int mEnd;
    private List<Snippet> mSnippets;
    private Toast mToast;
    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            if (3 == what) {
                mProgressDialog.cancel();
                seek();
            }

            return false;
        }
    };
    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
            switch (errorCode) {
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                case PLMediaPlayer.ERROR_CODE_OPEN_FAILED:
                    toast("连接中...");
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    toast("网络异常");
                    break;
                case PLMediaPlayer.ERROR_CODE_SEEK_FAILED:
                    break;
                case PLMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
                    break;
                default:
                    break;
            }

            vodStart(mVodUrl);

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vod_act);
        ButterKnife.bind(this);
        initViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mVodPresenter.cancelPlayTimer();

        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
        }

        mProgressDialog.cancel();
    }

    private void initViews() {
        mTvTitle.setText("回放");

        mVodPresenter = new VodPresenter(this, this);

        SharedPreferences shared = getSharedPreferences("data", MODE_PRIVATE);
        mUniqueId = shared.getString("unique_id", UUID.randomUUID().toString());

        Intent intent = getIntent();
        mSn = intent.getStringExtra("sn");
        mMic = intent.getBooleanExtra("mic", true);
        mStart = intent.getIntExtra("start", 0);
        mEnd = intent.getIntExtra("end", 0);
        String json = intent.getStringExtra("snippets");
        mSnippets = new Gson().fromJson(json, new TypeToken<List<Snippet>>() {
        }.getType());

        mTimeline.setVodConfig(mStart, mEnd, mSnippets);

        mTimeline.setCallBackListener(new Timeline.OnTouchCallBackListener() {
            @Override
            public void onUp(long current) {
                mVodPresenter.playVod(mSn, mUniqueId, mTimeline.getCurrent());
            }

            @Override
            public void onStop() {
                mVideoView.stopPlayback();
                mVodPresenter.cancelPlayTimer();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoading.setVisibility(View.GONE);
                    }
                });
            }
        });

        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_HW_DECODE);
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 1000);
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 1000);

        mVideoView.setAVOptions(options);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        if (mMic) {
            mVideoView.setVolume(0.5f, 0.5f);
        } else {
            mVideoView.setVolume(0.0f, 0.0f);
        }

        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnErrorListener(mOnErrorListener);

        mVideoView.setCoverView(mCover);
        mVideoView.setBufferingIndicator(mLoading);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("播放准备中...");
        mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                }

                return false;
            }
        });
        initEvent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mTimer != null) {
                    mTimer.cancel();
                }
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(VodContract.Presenter presenter) {

    }

    private void seek() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTimeline.setCurrent(mVideoView.getCurrentPosition());
                    }
                });
            }
        }, 0, 10_000);
    }
    LinearLayout.LayoutParams lp1;
    FrameLayout.LayoutParams lp2;
    private void initEvent() {
        mFullscreenOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlTitle.setVisibility(View.GONE);
                mFullscreenOpen.setVisibility(View.GONE);
                mFullscreenClose.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                mWrap.setLayoutParams(lp1);
                mVideoView.setLayoutParams(lp2);
            }
        });

        mFullscreenClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlTitle.setVisibility(View.VISIBLE);
                mFullscreenClose.setVisibility(View.GONE);
                mFullscreenOpen.setVisibility(View.VISIBLE);
                getWindow().setFlags(~WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.video_view_height));
                mWrap.setLayoutParams(lp1);
                mVideoView.setLayoutParams(lp2);
            }
        });
    }

    @Override
    public void toast(String text) {
        final String msg = text;
        runOnUiThread(new Runnable() {
            public void run() {
                if (null != mToast) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    @Override
    public void showProgress(final boolean isShow) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    mLoading.setVisibility(View.VISIBLE);
                } else {
                    mLoading.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void vodStart(String url) {
        if (mVideoView != null) {
            mVodUrl = url;
            mVideoView.setVideoPath(mVodUrl);
            mVideoView.start();
            mLoading.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R2.id.iv_left_monitor)
    public void onViewClicked() {
        finish();
    }
}
