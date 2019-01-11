package com.zzz.monitor.camera;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;
import com.zzz.monitor.R;
import com.zzz.monitor.R2;
import com.zzz.monitor.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;


public class PlayActivity extends AppCompatActivity implements PlayContract.View {
    private final String TAG = "DEBUG - " + this.getClass().getName();

    @BindView(R2.id.control)
    FrameLayout mControl;
    @BindView(R2.id.video_view)
    PLVideoTextureView mVideoView;
    @BindView(R2.id.btn_up)
    ImageView mUp;
    @BindView(R2.id.btn_right)
    ImageView mRight;
    @BindView(R2.id.btn_down)
    ImageView mDown;
    @BindView(R2.id.btn_left)
    ImageView mLeft;
    @BindView(R2.id.btn_talk)
    ImageView mTalk;
    @BindView(R2.id.btn_loading)
    ImageView mLoading;
    @BindView(R2.id.btn_camera)
    ImageView mCamera;
    private PlayPresenter mPlayPresenter;
    private String mLiveUrl;
    private boolean mReconnect = false;
    private ProgressDialog mProgressDialog;
    private MediaRecorder mRecorder;
    private Timer mTimer;
    private int mSeconds = 20;
    private String mMessage = "";
    private boolean mSendCancel = false;
    private Toast mToast;
    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            if (3 == what) {
                mProgressDialog.cancel();
                mControl.setVisibility(View.VISIBLE);
            }

            return false;
        }
    };
    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
            boolean isNeedReconnect = false;
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                case PLMediaPlayer.ERROR_CODE_OPEN_FAILED:
                    toast("连接中...");
                    isNeedReconnect = true;
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

            if (isNeedReconnect && !mReconnect) {
                mReconnect = false;
                liveStart(mLiveUrl);
            } else {
                Log.e(TAG, "error code: " + errorCode);
            }

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_act);
        ButterKnife.bind(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPlayPresenter.cancelPlay();
        mPlayPresenter.cancelPlayTimer();

        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView = null;
        }

        mProgressDialog.cancel();
    }

    private void initView() {
        Intent intent = getIntent();
        String sn = intent.getStringExtra("sn");

//        mVideoView = findViewById(R.id.video_view);
        // videoView.setVideoPath("http://zv.3gv.ifeng.com/live/zhongwen800k.m3u8");
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(PlayActivity.this).load(R.drawable.living).apply(requestOptions).into(mLoading);
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_HW_DECODE);
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 1000);
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 1000);

        mVideoView.setAVOptions(options);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnErrorListener(mOnErrorListener);

        mPlayPresenter = new PlayPresenter(this, this);
        mPlayPresenter.play(sn);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("播放准备中...");
        mProgressDialog.show();
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

    private void initEvent() {
        mUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayPresenter.ptz("up");
            }
        });
        mRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayPresenter.ptz("right");
            }
        });
        mDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayPresenter.ptz("down");
            }
        });
        mLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayPresenter.ptz("left");
            }
        });
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayPresenter.takePhoto();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnTouch(R2.id.btn_talk)
    public boolean onTouch(MotionEvent event) {
        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            toast("需要开启麦克风权限才能使用该功能");
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null != mVideoView) {
                    mVideoView.setVolume(0.0f, 0.0f);
                }
                mSeconds = 21;
                File file = new File(this.getCacheDir(), "msg.amr");
                if (file.exists() && !file.delete()) {
                    break;
                }
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile(file.getAbsolutePath());
                try {
                    mRecorder.prepare();
                } catch (IOException e) {
                    Log.e(TAG, "error", e);
                }
                mRecorder.start();
                mProgressDialog.show();
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMessage = "正在录音，松开发送... " + mSeconds + "\n(手指上滑，取消发送)";
                                mProgressDialog.setMessage(mMessage);
                            }
                        });
                        mSeconds = mSeconds - 1;
                        if (mSeconds == 0) {
                            mTimer.cancel();
                            mTimer.purge();
                            if (mRecorder != null) {
                                try {
                                    mRecorder.setOnErrorListener(null);
                                    mRecorder.setOnInfoListener(null);
                                    mRecorder.setPreviewDisplay(null);
                                    mRecorder.stop();
                                } catch (Exception e) {
                                    Log.e(TAG, "error", e);
                                }
                                mRecorder.release();
                                mRecorder = null;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressDialog.hide();
                                    }
                                });

                                toast("语音发送中...");
                                String data = Utils.file2Base64(getCacheDir().getAbsolutePath() + "/msg.amr");
                                mPlayPresenter.sendAudio(data);
                            }
                        }
                    }
                }, 0, 1000);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                if (-moveY > 200) {
                    mSendCancel = true;
                    mMessage = "松开手指, 取消发送... " + mSeconds;
                } else {
                    mSendCancel = false;
                    mMessage = "正在录音，松开发送... " + mSeconds + "\n(手指上滑，取消发送)";
                }
                mProgressDialog.setMessage(mMessage);
                break;

            case MotionEvent.ACTION_UP:
                mTimer.cancel();
                mTimer.purge();
                if (mRecorder != null) {
                    try {
                        mRecorder.setOnErrorListener(null);
                        mRecorder.setOnInfoListener(null);
                        mRecorder.setPreviewDisplay(null);
                        mRecorder.stop();
                    } catch (Exception e) {
                        Log.e(TAG, "error", e);
                    }
                    mRecorder.release();
                    mRecorder = null;
                    mProgressDialog.hide();
                    if (mSeconds > 19) {
                        toast("录音时间太短");
                    } else {
                        if (!mSendCancel) {
                            toast("语音发送中...");
                            String data = Utils.file2Base64(getCacheDir().getAbsolutePath() + "/msg.amr");
                            mPlayPresenter.sendAudio(data);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mTimer.cancel();
                mTimer.purge();
                if (mRecorder != null) {
                    try {
                        mRecorder.setOnErrorListener(null);
                        mRecorder.setOnInfoListener(null);
                        mRecorder.setPreviewDisplay(null);
                        mRecorder.stop();
                    } catch (Exception e) {
                        Log.e(TAG, "error", e);
                    }
                    mRecorder.release();
                    mRecorder = null;
                    mProgressDialog.hide();
                }
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void liveStart(String url) {
        mLiveUrl = url;

        mVideoView.setVideoPath(url);
        mVideoView.start();
    }

    @Override
    public void setPresenter(PlayContract.Presenter presenter) {
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
    public void liveFailure() {
        finish();
        toast("设备不在线");
    }

}
