package com.zzz.monitor.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.zzz.monitor.R;
import com.zzz.monitor.R2;
import com.zzz.monitor.api.HttpClient;
import com.zzz.monitor.bean.Camera;
import com.zzz.monitor.bean.LiveUrl;
import com.zzz.monitor.bean.Response;
import com.zzz.monitor.bean.Snippet;
import com.zzz.monitor.bean.VodConfig;
import com.zzz.monitor.camera.GalleryActivity;
import com.zzz.monitor.camera.PlayActivity;
import com.zzz.monitor.vod.VodActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {

    private List<Camera> mCameraList;
    private int mCurrentCamera = -1;
    private Context mContext;
    private ProgressDialog mProgressDialog;


    public CameraAdapter(List<Camera> cameras) {
        this.mCameraList = cameras;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.camera_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position, List payloads) {
        View clickLayout = holder.itemView.findViewById(R.id.click_layout);
        LinearLayout moreLayout = holder.itemView.findViewById(R.id.more_layout);

        if (payloads.isEmpty()) {
            Camera camera = mCameraList.get(position);

            holder.mName.setText(camera.getName()+camera.getSn());
            holder.mOp.setText(camera.getOp());

            if (camera.isIsOnline()) {
                if (camera.getPowerType().equals("out")) {
                    holder.mBattery.setImageResource(R.drawable.hub);
                } else {
                    holder.mBattery.setImageResource(R.drawable.battery_3);
                }
                holder.mTime.setText("运行中");
                holder.mPlay.setEnabled(true);
                holder.mVod.setEnabled(true);
            } else {
                holder.mBattery.setImageResource(R.drawable.off);
                holder.mTime.setText("关机于" + camera.getOnlineAt());
                holder.mPlay.setEnabled(false);
                holder.mVod.setEnabled(false);
            }

            String simResOneName = "signal_" + camera.getSimOneNetworkType().toLowerCase() + "_" + camera.getSimOneSignalLevel();
            String simResTwoName = "signal_" + camera.getSimTwoNetworkType().toLowerCase() + "_" + camera.getSimTwoSignalLevel();
            mContext = holder.mOp.getContext();
            int simResOneId = mContext.getResources().getIdentifier(simResOneName, "drawable", mContext.getPackageName());
            int simResTwoId = mContext.getResources().getIdentifier(simResTwoName, "drawable", mContext.getPackageName());

            holder.mSignalOne.setImageResource(simResOneId);
            holder.mSignalTwo.setImageResource(simResTwoId);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .override(640, 360)
                    .placeholder(R.drawable.loading_monitor)
                    .error(R.drawable.nocamra_default);
            Glide.with(holder.mSnapshot.getContext())
                    .load(camera.getSnapshot())
                    .apply(options)
                    .into(holder.mSnapshot);
            clickLayout.setTag(position);

            if (mCurrentCamera == position) {
                moreLayout.setVisibility(View.VISIBLE);
                clickLayout.setVisibility(View.GONE);
            } else {
                moreLayout.setVisibility(View.GONE);
                clickLayout.setVisibility(View.VISIBLE);
            }

            clickLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int prevPosition = mCurrentCamera;
                    mCurrentCamera = (Integer) view.getTag();
                    notifyItemChanged(position, 0);
                    notifyItemChanged(prevPosition, 0);
                }
            });

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;
                    Camera camera = mCameraList.get(position);
                    int i = view.getId();
                    if (i != R.id.play_button) {
                        if (i == R.id.vod_button) {
                            getVodSnippets(camera.getSn());

                        } else if (i == R.id.gallery_button) {
                            intent = new Intent(mContext, GalleryActivity.class);
                            intent.putExtra("sn", camera.getSn());
                            intent.putExtra("start_time", "");
                            intent.putExtra("end_time", "");
                            mContext.startActivity(intent);

                        } else {
                        }
                    } else {
                        intent = new Intent(mContext, PlayActivity.class);
                        intent.putExtra("sn", camera.getSn());
                        prePlay(intent);

                    }
                }
            };

            holder.mPlay.setOnClickListener(listener);
            holder.mVod.setOnClickListener(listener);
            holder.mGallery.setOnClickListener(listener);
        } else {
            if (mCurrentCamera == position) {
                moreLayout.setVisibility(View.VISIBLE);
                clickLayout.setVisibility(View.GONE);
            } else {
                moreLayout.setVisibility(View.GONE);
                clickLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCameraList.size();
    }

    private void prePlay(final Intent intent) {
        HttpClient.getInstance()
                .live(intent.getStringExtra("sn"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LiveUrl>() {
                    @Override
                    public void onStart() {
                        showProgress(mContext, true);
                    }

                    @Override
                    public void onCompleted() {
                        showProgress(mContext, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showProgress(mContext, false);
                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            try {
                                String errorBody = httpException.response().errorBody().string();
                                Response response = new Gson().fromJson(errorBody, Response.class);
                                toast(mContext, response.getMessage());
                            } catch (Exception ex) {
                                toast(mContext, "无法连接服务器，请检查网络");
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(LiveUrl liveUrl) {
                        mContext.startActivity(intent);
                    }
                });
    }

    private void getVodSnippets(final String sn) {
        HttpClient.getInstance()
                .getVodSnippets(sn)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VodConfig>() {
                    @Override
                    public void onStart() {
                        showProgress(mContext, true);
                    }

                    @Override
                    public void onCompleted() {
                        showProgress(mContext, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showProgress(mContext, false);
                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            try {
                                String errorBody = httpException.response().errorBody().string();
                                Response response = new Gson().fromJson(errorBody, Response.class);
                                toast(mContext, response.getMessage());
                            } catch (Exception ex) {
                                toast(mContext, "无法连接服务器，请检查网络");
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(VodConfig vodConfig) {
                        Intent intent = new Intent(mContext, VodActivity.class);
                        intent.putExtra("sn", sn);
                        intent.putExtra("start", vodConfig.getStart());
                        intent.putExtra("end", vodConfig.getEnd());
                        List<Snippet> snippets = vodConfig.getObjects();
                        String json = new Gson().toJson(snippets);
                        intent.putExtra("snippets", json);

                        mContext.startActivity(intent);
                    }
                });
    }

    private void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void showProgress(Context context, boolean isShow) {
        if (isShow) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("等待服务响应...");
            mProgressDialog.show();
        } else {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.name)
        TextView mName;
        @BindView(R2.id.time)
        TextView mTime;
        @BindView(R2.id.op)
        TextView mOp;
        @BindView(R2.id.signal_one)
        ImageView mSignalOne;
        @BindView(R2.id.signal_two)
        ImageView mSignalTwo;
        @BindView(R2.id.battery)
        ImageView mBattery;
        @BindView(R2.id.snapshot)
        ImageView mSnapshot;
        @BindView(R2.id.play_button)
        Button mPlay;
        @BindView(R2.id.vod_button)
        Button mVod;
        @BindView(R2.id.gallery_button)
        Button mGallery;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
