package com.zzz.monitor.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.zzz.monitor.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class PhotoAdapter extends PagerAdapter {
    private List<String> list;

    public PhotoAdapter(List<String> list) {
        this.list = list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        final String item = list.get(position);
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.photo_item, null);

        Glide.with(container.getContext())
                .load(item)
                .into((PhotoView) view.findViewById(R.id.snapshot));

        container.addView(view);

        TextView btnSavePhoto = view.findViewById(R.id.btn_save_photo);
        TextView btnSharePhoto = view.findViewById(R.id.btn_share_photo);

        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(container.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(container.getContext(), "需要开启应用存储空间权限才能使用该功能", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Bitmap bitmap = Glide.with(container.getContext()).asBitmap().load(item).submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        File file = saveImage(container.getContext(), bitmap);
                        Toast.makeText(container.getContext(), "图片已经保存到 " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnSharePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(container.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(container.getContext(), "需要开启应用存储空间权限才能使用该功能", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Bitmap bitmap = Glide.with(container.getContext()).asBitmap().load(item).submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        File file = saveImage(container.getContext(), bitmap);

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        container.getContext().startActivity(Intent.createChooser(intent, "分享图片"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private File saveImage(Context context, Bitmap bitmap) {
        File directory = new File(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + "Mokan");
        if (!directory.isDirectory() && !directory.mkdirs()) {
            return null;
        }

        File file = new File(directory.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg");

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

        return file;
    }
}
