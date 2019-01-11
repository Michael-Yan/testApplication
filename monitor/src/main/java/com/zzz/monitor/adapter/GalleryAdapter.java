package com.zzz.monitor.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zzz.monitor.R;
import com.zzz.monitor.R2;
import com.zzz.monitor.bean.Snapshot;
import com.zzz.monitor.camera.PhotoActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Object> mObjectList;
    private ArrayList<String> mPhotoList;

    public GalleryAdapter(List<Object> objects) {
        mObjectList = objects;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mObjectList.get(position);

        if (item instanceof Snapshot) {
            return 1;
        }

        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.date_item, parent, false);
            return new DateViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.gallery_item, parent, false);
            return new PhotoViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Object item = mObjectList.get(position);
        if (holder instanceof DateViewHolder) {
            mContext = ((DateViewHolder) holder).mDate.getContext();
            ((DateViewHolder) holder).mDate.setText((String) item);
        } else {
            mContext = ((PhotoViewHolder) holder).mSnapshot.getContext();
            final Snapshot snapshot = (Snapshot) item;

            RequestOptions options = new RequestOptions()
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.loading_square)
                    .error(R.drawable.nocamra_default_square)
                    .override(400, 400)
                    .centerCrop();
            Glide.with(mContext)
                    .load(snapshot.getSmall())
                    .apply(options)
                    .into(((PhotoViewHolder) holder).mSnapshot);

            ((PhotoViewHolder) holder).mSnapshot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPhotoList = new ArrayList<>();
                    for (Object object : mObjectList) {
                        if (object instanceof Snapshot) {
                            mPhotoList.add(((Snapshot) object).getOriginal());
                        }
                    }
                    Intent intent;
                    intent = new Intent(mContext, PhotoActivity.class);
                    intent.putExtra("current", mPhotoList.indexOf(snapshot.getOriginal()));
                    intent.putStringArrayListExtra("snapshots", mPhotoList);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mObjectList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if (getItemViewType(position) > 0) {
                    return 1;
                }

                return 4;
            }
        });
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.date)
        TextView mDate;

        public DateViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.snapshot)
        ImageView mSnapshot;

        public PhotoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
