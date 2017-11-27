package me.spirittalk.library.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Created by spirit on 2017/11/26.
 */

public class FileDownloadUtils {
    private static int MIN_PROGRESS_STEP = 61440;
    private static long MIN_PROGRESS_TIME = 2000;

    public static String generateFileName(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalStateException("can't generate file name, the url is null");
        }
        return md5(url);
    }

    public static int generateId(final String url, final String path) {
        return md5(String.format(Locale.ENGLISH, "%sp%s", url, path)).hashCode();
    }

    public static String generateFilePath(String directory, String filename) {
        if (filename == null) {
            throw new IllegalStateException("can't generate real path, the file name is null");
        }
        if (directory == null) {
            throw new IllegalStateException("can't generate real path, the directory is null");
        }

        return String.format(Locale.ENGLISH, "%s%s%s", directory, File.separator, filename);
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static boolean hasPermission(Context context, String permission) {
        int perm = context.checkCallingOrSelfPermission(permission);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isNetworkOnWifiType(Context context) {
        final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            FileDownloadLog.w(FileDownloadUtils.class, "failed to get connectivity manager!");
            return false;
        }

        final NetworkInfo info = manager.getActiveNetworkInfo();

        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static long convertContentLengthString(String s) {
        if (s == null) return -1;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static boolean isNeedSync(long bytesDelta, long timestampDelta) {
        return bytesDelta > FileDownloadUtils.MIN_PROGRESS_STEP &&
                timestampDelta > FileDownloadUtils.MIN_PROGRESS_TIME;
    }
}
