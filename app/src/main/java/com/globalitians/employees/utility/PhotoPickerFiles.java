package com.globalitians.employees.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class PhotoPickerFiles implements PhotoPickerConstants {

    private static String getFolderName(@NonNull Context context) {
        return PhotoPicker.configuration(context).getFolderName();
    }

    private static File tempImageDirectory(@NonNull Context context) {
        File privateTempDir = new File(Environment.getExternalStorageDirectory(), getFolderName(context));
        if (!privateTempDir.exists()) privateTempDir.mkdirs();

        // We have need to hide sent directory like whats app.
        createNoMediaFile();

        return privateTempDir;
    }

    private static void writeToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        writeToFile(in, dst);
    }

    static void copyFilesInSeparateThread(final Context context, final List<File> filesToCopy) {
        LogUtil.i("PhotoPickerFiles","copyFilesInSeparateThread");
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<File> copiedFiles = new ArrayList<>();
                for (File fileToCopy : filesToCopy) {
                    File dstDir = new File(Environment.getExternalStorageDirectory(), getFolderName(context));
                    if (!dstDir.exists()) dstDir.mkdirs();
                    File dstFile = new File(dstDir, fileToCopy.getName());
                    try {
                        dstFile.createNewFile();
                        copyFile(fileToCopy, dstFile);
                        copiedFiles.add(dstFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                scanCopiedImages(context, copiedFiles);
            }
        }).run();
    }

    static List<File> singleFileList(File file) {
        List<File> list = new ArrayList<>();
        list.add(file);
        return list;
    }

    static void scanCopiedImages(Context context, List<File> copiedImages) {
        String[] paths = new String[copiedImages.size()];
        for (int i = 0; i < copiedImages.size(); i++) {
            paths[i] = copiedImages.get(i).toString();
        }

        MediaScannerConnection.scanFile(context,
                paths, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        //Timber.d("Scanned " + path + ":");
                        //Timber.d("-> uri=" + uri);
                    }
                });
    }

    static File pickedExistingPicture(@NonNull Context context, Uri photoUri) throws IOException {
        InputStream pictureInputStream = context.getContentResolver().openInputStream(photoUri);
        File directory = tempImageDirectory(context);
        File photoFile = new File(directory, UUID.randomUUID().toString() + "." + getMimeType(context, photoUri));
        photoFile.createNewFile();
        writeToFile(pictureInputStream, photoFile);
        return photoFile;
    }

    static File getCameraPicturesLocation(@NonNull Context context) throws IOException {
        File dir = tempImageDirectory(context);
        return new File(dir.getAbsolutePath() + File.separator + UUID.randomUUID().toString() + ".jpg");
        //return File.createTempFile(UUID.randomUUID().toString(), ".jpg", dir);
    }

    static File getVideoPicturesLocation(@NonNull Context context) throws IOException {
        File dir = tempImageDirectory(context);
        return new File(dir.getAbsolutePath() + File.separator + UUID.randomUUID().toString() + ".mp4");
        //return File.createTempFile(UUID.randomUUID().toString(), ".jpg", dir);
    }

    /**
     * To find out the extension of required object in given uri
     * Solution by http://stackoverflow.com/a/36514823/1171484
     */
    public static String getMimeType(@NonNull Context context, @NonNull Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    static Uri getUriToFile(@NonNull Context context, @NonNull File file) {
        String packageName = context.getApplicationContext().getPackageName();
        String authority = packageName + ".fileprovider";
        //return Uri.fromFile(file);
        return FileProvider.getUriForFile(context, authority, file);
    }

    private static void createNoMediaFile() {
        try {
            // No media folder for profile directory
            File privateProfileDir = new File(Environment.getExternalStorageDirectory(), Constants.DIRECTORY_REGISTER_PROFILE);
            if (!privateProfileDir.exists()) privateProfileDir.mkdirs();

            File privateProfileNoMediaFile = new File(privateProfileDir.getAbsolutePath() + File.separator + Constants.DIRECTORY_FILE_NOMEDIA);
            if (!privateProfileNoMediaFile.exists()) {
                privateProfileNoMediaFile.createNewFile();
            }

            // No media folder for photo sent directory
            File privateImageDir = new File(Environment.getExternalStorageDirectory(), Constants.DIRECTORY_SENT_PHOTO);
            if (!privateImageDir.exists()) privateImageDir.mkdirs();

            File privateImageNoMediaFile = new File(privateImageDir.getAbsolutePath() + File.separator + Constants.DIRECTORY_FILE_NOMEDIA);
            if (!privateImageNoMediaFile.exists()) {
                privateImageNoMediaFile.createNewFile();
            }

            // No media folder for video sent directory
            File privateVideoDir = new File(Environment.getExternalStorageDirectory(), Constants.DIRECTORY_SENT_VIDEO);
            if (!privateVideoDir.exists()) privateVideoDir.mkdirs();

            File privateVideoNoMediaFile = new File(privateVideoDir.getAbsolutePath() + File.separator + Constants.DIRECTORY_FILE_NOMEDIA);
            if (!privateVideoNoMediaFile.exists()) {
                privateVideoNoMediaFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
