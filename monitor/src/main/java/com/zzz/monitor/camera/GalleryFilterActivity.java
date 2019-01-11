package com.zzz.monitor.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.zzz.monitor.R;
import com.zzz.monitor.R2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GalleryFilterActivity extends AppCompatActivity {
    @BindView(R2.id.iv_left_monitor)
    ImageView mIvLeft;
    @BindView(R2.id.tv_title_monitor)
    TextView mTvTitle;
    @BindView(R2.id.txt_right_monitor)
    TextView mTvRight;
    @BindView(R2.id.start_date)
    EditText mStartDate;
    @BindView(R2.id.end_date)
    EditText mEndDate;
    @BindView(R2.id.btn_submit)
    Button mSubmit;

    private DatePicker mDatePicker;
    private Date mStartDateTime = null;
    private Date mEndDateTime = null;
    private String mSn = null;
    private int mStartTime = 0;
    private int mEndTime = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_filter);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTvTitle.setText("筛选");
        mTvRight.setText("清空");

        Intent intent = getIntent();
        mSn = intent.getStringExtra("sn");

        String startTimeStr = intent.getStringExtra("start_time");
        String endTimeStr = intent.getStringExtra("end_time");

        if (startTimeStr.length() > 0) {
            mStartTime = Integer.parseInt(startTimeStr);
        }

        if (endTimeStr.length() > 0) {
            mEndTime = Integer.parseInt(endTimeStr);
        }

        if (mStartTime > 0) {
            mStartDateTime = new Date((long) mStartTime * 1000);
            mStartDate.setText(sdf.format(mStartDateTime.getTime()));
        }

        if (mEndTime > 0) {
            mEndDateTime = new Date((long) mEndTime * 1000);
            mEndDate.setText(sdf.format(mEndDateTime.getTime()));
        }
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
                mStartDate.setText("");
                mEndDate.setText("");
            }
        });
        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("start", mStartDateTime);
            }
        });

        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("end", mEndDateTime);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStartDateTime != null) {
                    mStartTime = (int) (mStartDateTime.getTime() / 1000);
                }

                if (mEndDateTime != null) {
                    mEndTime = (int) (mEndDateTime.getTime() / 1000);
                }

                if (mStartTime != 0 && mEndTime != 0 && mStartTime > mEndTime) {
                    Toast.makeText(getApplicationContext(), "结束日期不能大于开始日期", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                    intent.putExtra("sn", mSn);

                    if (mStartTime > 0) {
                        intent.putExtra("start_time", mStartTime + "");
                    } else {
                        intent.putExtra("start_time", "");
                    }

                    if (mEndTime > 0) {
                        intent.putExtra("end_time", mEndTime + "");
                    } else {
                        intent.putExtra("end_time", "");
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    private void showDialog(final String type, Date date) {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.filter_photo)
                .customView(R.layout.dialog_datepicker, false)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel).build();

        mDatePicker = dialog.getCustomView().findViewById(R.id.date_picker);

        if (null != date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            mDatePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                }
            });
        }

        dialog.show();

        View dialogPositiveButton = dialog.getActionButton(DialogAction.POSITIVE);
        dialogPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();
                if (type.equals("start")) {
                    calendar.set(year, month, day, 0, 0, 0);
                    mStartDateTime = calendar.getTime();
                    mStartDate.setText(sdf.format(calendar.getTime()));
                } else {
                    calendar.set(year, month, day, 23, 59, 59);
                    mEndDateTime = calendar.getTime();
                    mEndDate.setText(sdf.format(calendar.getTime()));
                }

                dialog.dismiss();
            }
        });
    }
}