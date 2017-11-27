package me.spirittalk.library.util;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import me.spirittalk.library.model.DownloadModel;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Created by spirit on 2017/11/26.
 */

public class FileUtils {
    private static final String TAG = "Utils";

    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(getCacheDirectory(context), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    public static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true);
    }

    private static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
            Log.d(TAG, "Can't define system cache directory! 'context.getCacheDir()' will be used.");
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        return FileDownloadUtils.hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private static File getExternalCacheDir(Context context) {
        File appCacheDir = context.getExternalCacheDir();
        if (appCacheDir == null) {
            File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
            File cacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
            if (makeNoMedia(cacheDir)) return null;
            return cacheDir;
        } else {
            if (makeNoMedia(appCacheDir)) return null;
            return appCacheDir;
        }
    }

    private static boolean makeNoMedia(File cacheDir) {
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                Log.d(TAG, "Unable to create external cache directory");
                return true;
            }
            try {
                new File(cacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                Log.d(TAG, "Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return false;
    }

    public static String getTempFilePath(String targetPath) {
        return String.format("%s.temp", targetPath);
    }

    public static boolean deleteFile(String filePath) {
        if (filePath != null) {
            final File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
        }
        return false;
    }

    public static void deleteFiles(String... filePaths) {
        for (String path : filePaths) {
            deleteFile(path);
        }
    }

    public static boolean isBreakpointAvailable(final DownloadModel model) {
        String tempFilePath = getTempFilePath(model.getPath());
        if (tempFilePath == null) {
            FileDownloadLog.d(FileDownloadUtils.class, "can't continue %d path = null", model.getId());
            return false;
        }

        File file = new File(tempFilePath);
        final boolean isExists = file.exists();
        final boolean isDirectory = file.isDirectory();
        if (!isExists || isDirectory) {
            FileDownloadLog.d(FileDownloadUtils.class, "can't continue %d file not suit, exists[%B], directory[%B]",
                    model.getId(), isExists, isDirectory);
            return false;
        }

        final long fileLength = file.length();
        final long currentOffset = model.getSofar();
        final long totalLength = model.getTotal();
        if (fileLength < currentOffset ||
                (totalLength != -1 && (fileLength > totalLength || currentOffset >= totalLength))) {
            // dirty data.
            FileDownloadLog.d(FileDownloadUtils.class, "can't continue %d dirty data" +
                            " fileLength[%d] sofar[%d] total[%d]",
                    model.getId(), fileLength, currentOffset, totalLength);
            return false;
        }

        return true;
    }

    public static void renameTempFile(String targetPath, String tempPath) throws IOException {
        final File tempFile = new File(tempPath);
        try {
            final File targetFile = new File(targetPath);

            if (targetFile.exists()) {
                final long oldTargetFileLength = targetFile.length();
                if (!targetFile.delete()) {
                    throw new IOException(String.format(
                            "Can't delete the old file([%s], [%d]), " +
                                    "so can't replace it with the new downloaded one.",
                            targetPath, oldTargetFileLength
                    ));
                } else {
                    FileDownloadLog.w("FileUtils", "The target file([%s], [%d]) will be replaced with" +
                                    " the new downloaded file[%d]",
                            targetPath, oldTargetFileLength, tempFile.length());
                }
            }

            if (!tempFile.renameTo(targetFile)) {
                throw new IOException(String.format(
                        "Can't rename the  temp downloaded file(%s) to the target file(%s)",
                        tempPath, targetPath
                ));
            }
        } finally {
            if (tempFile.exists()) {
                if (!tempFile.delete()) {
                    FileDownloadLog.w("FileUtils",
                            "delete the temp file(%s) failed, on completed downloading.",
                            tempPath);
                }
            }
        }
    }
}
