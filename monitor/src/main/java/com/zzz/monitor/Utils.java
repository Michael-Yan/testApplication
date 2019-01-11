package com.zzz.monitor;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class Utils {
    private static final String TAG = "===debug===";

    /**
     * md5
     *
     * @param text text
     * @return string
     */
    public static String md5(String text) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(text.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte i : array) {
                sb.append(Integer.toHexString((i & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * file 2 base64
     *
     * @param filename filename
     * @return base64 string
     */
    public static String file2Base64(String filename) {
        File file = new File(filename);
        FileInputStream fileInputStream = null;
        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                int len = (int) file.length();
                byte bytes[] = new byte[len];
                int count, total = 0;
                while ((count = fileInputStream.read(bytes, total, len - total)) > 0) {
                    total += count;
                }
                return Base64.encodeToString(bytes, Base64.DEFAULT);
            } catch (Exception ex) {
                Log.e(TAG, "e", ex);
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        return null;
    }
}
