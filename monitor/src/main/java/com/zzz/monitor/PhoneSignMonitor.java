package com.zzz.monitor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class PhoneSignMonitor {

    //获取手机的唯一标识
    public static String getPhoneSign(Context context) {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            assert tm != null;

            @SuppressLint("MissingPermission") String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //序列号（sn）
            @SuppressLint("MissingPermission") String sn = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID(context);
            if (!TextUtils.isEmpty(uuid)) {
                deviceId.append("id");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append("id").append(getUUID(context));
        }
        return deviceId.toString();
    }

    /**
     * 得到全局唯一UUID
     */

    private static String getUUID(Context context) {
        String uuid ="";
        SharedPreferences mShare = context.getSharedPreferences("uuid", MODE_PRIVATE);
        if (mShare != null) {
            uuid = mShare.getString("uuid", "");
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid", uuid).commit();
        }
        return uuid;
    }

    /**
     * 获取CPU序列号(16位)
     **/
    public static String getCpuNO() {
        String str;
        String cpuNo = "0000000000000000";
        String cpuAddress = "0000000000000000";
        try {
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {  // 查找到序列号所在行
                    if (str.indexOf("Serial") > -1) {
                        cpuNo = str.substring(str.indexOf(":") + 1, str.length());  // 提取序列号
                        cpuAddress = cpuNo.trim(); // 去空格
                        break;
                    }
                } else { // 文件结尾
                    break;
                }
            }
        } catch (Exception e) {
        }
        return cpuAddress;
    }


    /**
     * 获取CPU名字
     **/
    public static String getCpuName() {
        String cpuName = "";
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            cpuName = array[1];
        } catch (Exception e) {
            Log.e("xuxu", e.getMessage());
        }
        Log.e("xuxu", "CPU名字: " + cpuName);
        return cpuName;
    }

    /**
     * 获取CPU最小频率（单位KHZ）
     **/
    public static String getMinCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        Log.e("xuxu", "获取CPU最小频率: " + result.trim() + "KHZ");
        return result.trim();
    }
}
