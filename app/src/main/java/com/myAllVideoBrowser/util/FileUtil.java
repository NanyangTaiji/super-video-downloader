package com.myAllVideoBrowser.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.net.UriKt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class FileUtil {

    public static boolean INITIALIZED = false;

    // For downloads and tmp data
    public static boolean IS_EXTERNAL_STORAGE_USE = true;

    // For downloads
    public static boolean IS_APP_DATA_DIR_USE = false;

    public static final String FOLDER_NAME = "SuperX";
    public static final String TMP_DATA_FOLDER_NAME = "superx_tmp_data";

    private static final int KB = 1024;
    private static final int MB = 1024 * 1024;
    private static final int GB = 1024 * 1024 * 1024;

    // 10MB
    private static final long FREE_SPACE_THRESHOLD = 10 * 1024 * 1024;

    @Inject
    public FileUtil() {
    }

    public static String getFileSizeReadable(double length) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        if (length > GB) {
            return decimalFormat.format(length / GB) + " GB";
        } else if (length > MB) {
            return decimalFormat.format(length / MB) + " MB";
        } else if (length > KB) {
            return decimalFormat.format(length / KB) + " KB";
        } else {
            return decimalFormat.format(length) + " B";
        }
    }

    public static long getFreeDiskSpace(File path) {
        if (!path.exists()) {
            throw new IllegalArgumentException("Path does not exist");
        }

        StatFs stats = new StatFs(path.getAbsolutePath());
        return stats.getAvailableBlocksLong() * stats.getBlockSizeLong();
    }

    public static long calculateFolderSize(File directory) {
        long length = 0L;
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    length += calculateFolderSize(file);
                }
            }
        } else {
            length += directory.length();
        }
        return length;
    }

    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public File getFolderDir() {
        if (!INITIALIZED) {
            throw new RuntimeException("File Util Not Initialized");
        }

        Context context = ContextUtils.getApplicationContext();

        if (IS_EXTERNAL_STORAGE_USE && !IS_APP_DATA_DIR_USE) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        } else if (IS_EXTERNAL_STORAGE_USE && IS_APP_DATA_DIR_USE) {
            return new File(context.getExternalFilesDir(null), FOLDER_NAME);
        } else {
            return new File(context.getFilesDir().getAbsolutePath(), FOLDER_NAME);
        }
    }

    public File getTmpDir() {
        if (!INITIALIZED) {
            throw new RuntimeException("File Util Not Initialized");
        }

        Context context = ContextUtils.getApplicationContext();
        return getTmpDataDir(context, IS_EXTERNAL_STORAGE_USE);
    }

    public Map<String, Pair<Long, Uri>> getListFiles() {
        Context context = ContextUtils.getApplicationContext();
        Map<String, Pair<Long, Uri>> result = new HashMap<>();

        Map<String, Pair<Long, Uri>> externalPrivateFilesObjs = getPrivateDownloadsDirFilesObj(context, true);
        Map<String, Pair<Long, Uri>> internalPrivateFilesObjs = getPrivateDownloadsDirFilesObj(context, false);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            Map<String, Pair<Long, Uri>> externalPublicFilesObjs = getPublicDownloadsDirFilesObjOld(context, true);
            Map<String, Pair<Long, Uri>> internalPublicFilesObjs = getPublicDownloadsDirFilesObjOld(context, false);
            result.putAll(externalPublicFilesObjs);
            result.putAll(internalPublicFilesObjs);
        } else {
            Map<String, Pair<Long, Uri>> externalPublicFilesObjsNew = getPublicDownloadsDirFilesObjNew();
            result.putAll(externalPublicFilesObjsNew);
        }

        result.putAll(externalPrivateFilesObjs);
        result.putAll(internalPrivateFilesObjs);

        return result;
    }

    public boolean isFreeSpaceAvailable() {
        return getFreeDiskSpace(getFolderDir()) > FREE_SPACE_THRESHOLD;
    }

    public boolean isFileWithNameNotExists(Context context, Uri uri, String newName) {
        if (isFileApiSupportedByUri(context, uri)) {
            File parentFile = UriKt.toFile(uri).getParentFile();
            return !new File(parentFile, newName).exists();
        } else {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                return !isDownloadedVideoContentExistsByName(context.getContentResolver(), uri, newName);
            } else {
                throw new RuntimeException("File api support ERROR");
            }
        }
    }

    public boolean moveMedia(Context context, Uri from, Uri to) {
        if (isFileApiSupportedByUri(context, to)) {
            AppLogger.d("IS_FILE_API: TRUE -- from " + from + " to " + to);
            File newFile = UriKt.toFile(to);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Files.move(UriKt.toFile(from).toPath(), newFile.toPath());
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return renameWithLock(UriKt.toFile(from), newFile);
        } else {
            AppLogger.d("IS_FILE_API: FALSE -- from " + from + " to " + to);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                return moveFileToDownloadsFolder(context.getContentResolver(),
                        UriKt.toFile(from), UriKt.toFile(to).getName());
            } else {
                throw new RuntimeException("File API support ERROR!!!");
            }
        }
    }

    public Pair<String, Uri> renameMedia(Context context, Uri from, String newName) {
        try {
            String originExtension = getFileExtension(UriKt.toFile(from));
            String cleanedFileName = FileNameCleaner.cleanFileName(newName) + "." + originExtension;
            boolean isNewFileNotExists = isFileWithNameNotExists(context, from, newName);

            if (cleanedFileName.isEmpty()) {
                throw new RuntimeException("Empty file name");
            }

            if (!isUriExists(context, from)) {
                throw new FileNotFoundException("File not found: " + from);
            }

            if (!isNewFileNotExists) {
                throw new RuntimeException("File already exists");
            }

            if (isFileApiSupportedByUri(context, from)) {
                File fromFile = UriKt.toFile(from);
                File toFile = new File(fromFile.getParentFile(), cleanedFileName);
                if (toFile.exists()) {
                    throw new RuntimeException("File already exists: " + toFile);
                }
                fromFile.renameTo(toFile);

                return new Pair<>(toFile.getName(), Uri.fromFile(toFile));
            } else {
                Uri newUri = renameVideoContentFromDownloads(context, from, cleanedFileName);
                return new Pair<>(cleanedFileName, newUri != null ? newUri : from);
            }
        } catch (Throwable e) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    public void deleteMedia(Context context, Uri uri) {
        try {
            if (!isUriExists(context, uri)) {
                throw new FileNotFoundException("File not found: " + uri);
            }

            if (isFileApiSupportedByUri(context, uri)) {
                UriKt.toFile(uri).delete();
            } else {
                deleteDownloadedVideoContent(context, uri);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isUriExists(Context context, Uri uri) {
        if (isFileApiSupportedByUri(context, uri)) {
            return UriKt.toFile(uri).exists();
        }

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (Exception e) {
            // Handle other exceptions as needed
        }

        // If there were no exceptions, the URI exists
        return true;
    }

    public long getContentLength(Context context, Uri uri) {
        if (isFileApiSupportedByUri(context, uri)) {
            return UriKt.toFile(uri).length();
        } else {
            return getContentSize(context, uri);
        }
    }

    public boolean isFileApiSupportedByUri(Context context, Uri uri) {
        boolean isExternalTo = isExternalUri(uri);
        File privateDir = getPrivateDownloadsDir(context, isExternalTo);
        boolean isAppDir = uri.toString().startsWith(Uri.fromFile(privateDir).toString());

        return !(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q && !isAppDir);
    }

    // WITHOUT LOCK EXISTS PROBABILITY OF CORRUPTED FILE AFTER renameTo()
    private boolean renameWithLock(File sourceFile, File targetFile) {
        try {
            // 1. Acquire a lock on the source file
            RandomAccessFile randomAccessFile = new RandomAccessFile(sourceFile, "rw");
            FileChannel fileChannel = randomAccessFile.getChannel();
            FileLock fileLock = fileChannel.lock();

            try {
                // 2. Perform the renameTo() operation while holding the lock
                return sourceFile.renameTo(targetFile);
            } finally {
                // 3. Release the lock in the finally block
                fileLock.release();
                randomAccessFile.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppLogger.d(e.getMessage());
            return false;
        }
    }

    private long getContentSize(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (cursor.moveToFirst() && !cursor.isNull(sizeIndex)) {
                    return cursor.getLong(sizeIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1; // Return -1 if size is unknown or an error occurred
    }

    private Uri renameVideoContentFromDownloads(Context context, Uri uri, String newName) {
        // Check if the URI is a document URI
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // Rename the document using the DocumentsContract API
            try {
                return DocumentsContract.renameDocument(context.getContentResolver(), uri, newName);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Rename the file using the ContentResolver
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, newName);
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            context.getContentResolver().update(uri, values, null, null);
            return Uri.parse(uri.toString());
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private boolean isDownloadedVideoContentExistsByName(ContentResolver contentResolver, Uri contentOrig, String fileName) {
        boolean isExternal = isExternalUri(contentOrig);
        Uri contentUri = isExternal ?
                MediaStore.Downloads.EXTERNAL_CONTENT_URI :
                MediaStore.Downloads.INTERNAL_CONTENT_URI;

        // Query the Downloads collection for files with the given name
        String[] projection = {MediaStore.Downloads._ID};
        String selection = MediaStore.Downloads.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {fileName};

        Cursor cursor = contentResolver.query(contentUri, projection, selection, selectionArgs, null);

        // Check if the cursor is not null and has at least one row
        boolean exists = cursor != null && cursor.getCount() > 0;

        // Close the cursor
        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }

    private File getTmpDataDir(Context context, boolean isExternal) {
        String path = isExternal ?
                context.getExternalFilesDir(null) + "/" + TMP_DATA_FOLDER_NAME :
                context.getFilesDir().getAbsolutePath() + "/" + TMP_DATA_FOLDER_NAME;

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    private Map<String, Pair<Long, Uri>> getPrivateDownloadsDirFilesObj(Context context, boolean isExternal) {
        Map<String, Pair<Long, Uri>> filesMap = new HashMap<>();

        String path = getPrivateDownloadsDir(context, isExternal).getAbsolutePath();
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }

        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                filesMap.put(f.getName(), new Pair<>(f.length(), Uri.fromFile(f)));
            }
        }

        return filesMap;
    }

    private File getPrivateDownloadsDir(Context context, boolean isExternal) {
        String path = isExternal ?
                context.getExternalFilesDir(null) + "/" + FOLDER_NAME :
                context.getFilesDir().getAbsolutePath() + "/" + FOLDER_NAME;

        return new File(path);
    }

    private Map<String, Pair<Long, Uri>> getPublicDownloadsDirFilesObjNew() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] filesList = downloadsDir.listFiles();
        Map<String, Pair<Long, Uri>> filesMap = new HashMap<>();

        if (filesList != null) {
            for (File file : filesList) {
                if (file.isFile() && (getFileExtension(file).equals("mp4") || getFileExtension(file).equals("mp3"))) {
                    filesMap.put(file.getName(), new Pair<>((long) file.getName().hashCode(), Uri.fromFile(file)));
                }
            }
        }

        return filesMap;
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private Map<String, Pair<Long, Uri>> getPublicDownloadsDirFilesObjOld(Context context, boolean isExternalStorage) {
        Map<String, Pair<Long, Uri>> filesMap = new HashMap<>();
        Uri targetUri = isExternalStorage ?
                MediaStore.Downloads.EXTERNAL_CONTENT_URI :
                MediaStore.Downloads.INTERNAL_CONTENT_URI;

        Cursor cursor = context.getContentResolver().query(
                targetUri,
                new String[]{MediaStore.Downloads._ID, MediaStore.Downloads.DISPLAY_NAME},
                null, null, null
        );

        if (cursor != null) {
            try {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);

                    Uri contentUri = ContentUris.withAppendedId(targetUri, id);

                    if (isUriExists(context, contentUri)) {
                        filesMap.put(name, new Pair<>(id, contentUri));
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return filesMap;
    }

    private void deleteDownloadedVideoContent(Context context, Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            try {
                DocumentsContract.deleteDocument(context.getContentResolver(), uri);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            context.getContentResolver().delete(uri, null, null);
        }
    }

    private boolean isExternalUri(Uri uri) {
        Context context = ContextUtils.getApplicationContext();

        Uri ext1 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ext1 = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        }

        Uri ext2 = Uri.fromFile(context.getExternalFilesDir(null));
        Uri ext3 = Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

        String uriString = uri.toString();
        return (ext1 != null && uriString.contains(ext1.toString())) ||
                uriString.contains(ext2.toString()) ||
                uriString.contains(ext3.toString());
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private boolean moveFileToDownloadsFolder(ContentResolver contentResolver, File sourceFile, String fileName) {
        AppLogger.d("moveFileToDownloadsFoldermoveFileToDownloadsFolder " + sourceFile + " " + fileName);

        boolean isAudio = getFileExtension(sourceFile).equals("mp3");

        // Check if there is enough free space in the Downloads folder
        File downloadsDirectory = getFolderDir();
        boolean isFolderExternal = isExternalUri(Uri.fromFile(downloadsDirectory));
        long availableSpace = downloadsDirectory.getFreeSpace();

        if (availableSpace < sourceFile.length()) {
            // Handle the case where there is not enough free space
            throw new RuntimeException("Not available space " + availableSpace + ", file size: " + sourceFile.length());
        }

        // Create a ContentValues object to specify the file details
        String name = fileName;
        int counter = 1;
        while (isDownloadExists(contentResolver, name)) {
            name = fileName + "(" + counter + ")";
            counter++;
        }

        String cleaned = FileNameCleaner.cleanFileName(name);
        ContentValues values = new ContentValues();
        String mimeType = isAudio ? "audio/mpeg" : "video/mp4";
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, cleaned);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        // Insert the file into the Downloads collection
        Uri collectionUri = isFolderExternal ?
                MediaStore.Downloads.EXTERNAL_CONTENT_URI :
                MediaStore.Downloads.INTERNAL_CONTENT_URI;

        Uri fileUri = contentResolver.insert(collectionUri, values);
        if (fileUri == null) {
            values.put(MediaStore.MediaColumns.DISPLAY_NAME,
                    cleaned.replace("mp4", "").replace("mp3", "") + "_e");
            fileUri = contentResolver.insert(collectionUri, values);
        }

        // Copy the file to the Downloads folder
        if (fileUri != null) {
            try {
                // 1. Acquire a lock on the source file
                RandomAccessFile randomAccessFile = new RandomAccessFile(sourceFile, "rw");
                FileChannel fileChannel = randomAccessFile.getChannel();
                FileLock fileLock = fileChannel.lock();

                try {
                    OutputStream outputStream = contentResolver.openOutputStream(fileUri);
                    if (outputStream != null) {
                        InputStream inputStream = new java.io.FileInputStream(sourceFile);
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        long totalCopied = 0;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            totalCopied += bytesRead;
                        }

                        inputStream.close();
                        outputStream.close();

                        if (totalCopied > 0) {
                            AppLogger.d("Source removing... " + sourceFile);
                            // Delete the source file
                            sourceFile.delete();
                            return true;
                        } else {
                            AppLogger.d("Source move error " + sourceFile);
                            return false;
                        }
                    }
                } finally {
                    // 3. Release the lock in the finally block
                    fileLock.release();
                    randomAccessFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppLogger.d("Source move error " + sourceFile + " " + e);
                return false;
            }
        }

        return false;
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private boolean isDownloadExists(ContentResolver contentResolver, String displayName) {
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        String selection = MediaStore.Downloads.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {displayName};

        Uri uri = isExternalUri(Uri.fromFile(getFolderDir())) ?
                MediaStore.Downloads.EXTERNAL_CONTENT_URI :
                MediaStore.Downloads.INTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

    public static class FileNameCleaner {
        private static final int MAX_FILE_NAME_LENGTH = 100;
        private static final int[] illegalChars = {
                34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47
        };

        static {
            Arrays.sort(illegalChars);
        }

        public static String cleanFileName(String badFileName) {
            StringBuilder cleanName = new StringBuilder();
            for (char c : badFileName.toCharArray()) {
                int charCode = (int) c;
                if (Arrays.binarySearch(illegalChars, charCode) < 0) {
                    cleanName.append(c);
                }
            }

            String finalName = cleanName.toString()
                    .replace(".mp3", "")
                    .replace(".mp4", "")
                    .replace("/", "")
                    .replace("\\", "")
                    .replace(":", "")
                    .replace("*", "")
                    .replace("?", "")
                    .replace("\"", "")
                    .replace("`", "")
                    .replace("'", "")
                    .replace("<", "")
                    .replace(">", "")
                    .replace(".", "_")
                    .replace("|", "")
                    .replaceAll("\\s*-\\s*", "-")
                    .replace(" ", "_")
                    .trim();

            if (finalName.isEmpty()) {
                finalName = "Untitled";
            }

            if (finalName.length() > MAX_FILE_NAME_LENGTH) {
                return finalName.substring(0, MAX_FILE_NAME_LENGTH);
            }

            return finalName;
        }
    }
}
