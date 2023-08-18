package com.globalitians.employees.utility;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.globalitians.employees.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;

import static com.globalitians.employees.utility.PhotoPickerFiles.singleFileList;
import static in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery.REQUEST_CODE;

public class PhotoPicker implements PhotoPickerConstants {
    public static final int SELECT_PHOTO = 1;
    public static final int SELECT_VIDEO = 2;
    public static final int SELECT_PHOTO_VIDEO = 3;
    private static final boolean SHOW_GALLERY_IN_CHOOSER = false;
    private static final String KEY_PHOTO_URI = "com.photopicker.photo_uri";
    private static final String KEY_LAST_CAMERA_PHOTO = "com.photopicker.last_photo";
    private static final String KEY_TYPE = "com.photopicker.type";
    private static String TAG = "PhotoPicker";

    private static Uri createCameraPictureFile(@NonNull Context context) throws IOException {
        File imagePath = PhotoPickerFiles.getCameraPicturesLocation(context);
        Uri uri = PhotoPickerFiles.getUriToFile(context, imagePath);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(KEY_PHOTO_URI, uri.toString());
        editor.putString(KEY_LAST_CAMERA_PHOTO, imagePath.toString());
        editor.apply();
        return uri;
    }

    private static Uri createVideoPictureFile(@NonNull Context context) throws IOException {
        File imagePath = PhotoPickerFiles.getVideoPicturesLocation(context);
        Uri uri = PhotoPickerFiles.getUriToFile(context, imagePath);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(KEY_PHOTO_URI, uri.toString());
        editor.putString(KEY_LAST_CAMERA_PHOTO, imagePath.toString());
        editor.apply();
        return uri;
    }

    public static File getCameraPictureLocation(@NonNull Context context) {
        try {
            return PhotoPickerFiles.getCameraPicturesLocation(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getCameraVideoLocation(@NonNull Context context) {
        try {
            return PhotoPickerFiles.getVideoPicturesLocation(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Intent createDocumentsIntent(@NonNull Context context, int type) {
        storeType(context, type);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        return intent;
    }

    private static Intent createGalleryIntent(@NonNull Context context, int type) {
        storeType(context, type);
        Intent intent = plainGalleryPickerIntent();
        if (Build.VERSION.SDK_INT >= 18) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, configuration(context).allowsMultiplePickingInGallery());
        }
        return intent;
    }

    private static Intent createVideoGalleryIntent(@NonNull Context context, int type) {
        storeType(context, type);
        Intent intent = plainGalleryPickerIntent();
        intent.setType("video/*");
        if (Build.VERSION.SDK_INT >= 18) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, configuration(context).allowsMultiplePickingInGallery());
        }
        return intent;
    }

    private static Intent createCameraIntent(@NonNull Context context, int type) {
        storeType(context, type);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri capturedImageUri = createCameraPictureFile(context);
            //We have to explicitly grant the write permission since Intent.setFlag works only on API Level >=20
            grantWritePermission(context, intent, capturedImageUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return intent;
    }

    private static void revokeWritePermission(@NonNull Context context, Uri uri) {
        context.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    private static void grantWritePermission(@NonNull Context context, Intent intent, Uri uri) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private static Intent createChooserIntent(@NonNull Context context, @Nullable String chooserTitle, int type) throws IOException {
        return createChooserIntent(context, chooserTitle, SHOW_GALLERY_IN_CHOOSER, type);
    }

    private static Intent createChooserIntent(@NonNull Context context, @Nullable String chooserTitle, boolean showGallery, int type) throws IOException {
        storeType(context, type);
        Uri outputFileUri = createCameraPictureFile(context);
        List<Intent> cameraIntents = new ArrayList<>();
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> camList = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : camList) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            grantWritePermission(context, intent, outputFileUri);
            cameraIntents.add(intent);
        }
        Intent galleryIntent;
        if (showGallery) {
            galleryIntent = createGalleryIntent(context, type);
        } else {
            galleryIntent = createDocumentsIntent(context, type);
        }

        Intent chooserIntent = Intent.createChooser(galleryIntent, chooserTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        return chooserIntent;
    }
    private static Intent createChooserIntentForGymImages(@NonNull Context context, @Nullable String chooserTitle, boolean showGallery, int type) throws IOException {
        storeType(context, type);
        Uri outputFileUri = createCameraPictureFile(context);
        List<Intent> cameraIntents = new ArrayList<>();
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> camList = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : camList) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            grantWritePermission(context, intent, outputFileUri);
            cameraIntents.add(intent);
        }

        //Opening AlbumSelect Activity for Selecting Images.
        Intent galleryIntent = new Intent(context, AlbumSelectActivity.class);
        galleryIntent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 5);//5 is limit to no. of images selectable
        //startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);

        Intent chooserIntent = Intent.createChooser(galleryIntent, chooserTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        return chooserIntent;
    }

    private static Intent createChooserIntentForVideo(@NonNull Context context, @Nullable String chooserTitle, boolean showGallery, int type) throws IOException {
        storeType(context, type);

        Uri outputFileUri = createVideoPictureFile(context);
        List<Intent> cameraIntents = new ArrayList<>();
        Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        captureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> camList = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : camList) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            grantWritePermission(context, intent, outputFileUri);
            cameraIntents.add(intent);
        }

        Intent galleryIntent;

        galleryIntent = createVideoGalleryIntent(context, type);

        Intent chooserIntent = Intent.createChooser(galleryIntent, chooserTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        return chooserIntent;
    }

    private static void storeType(@NonNull Context context, int type) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(KEY_TYPE, type).commit();
    }

    private static int restoreType(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_TYPE, 0);
    }

    public static void openChooserWithDocuments(Activity activity, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntent(activity, chooserTitle, type);
            activity.startActivityForResult(intent, RequestCodes.SOURCE_CHOOSER | RequestCodes.PICK_PICTURE_FROM_DOCUMENTS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openChooserWithDocuments(Fragment fragment, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntent(fragment.getActivity(), chooserTitle, type);
            fragment.startActivityForResult(intent, RequestCodes.SOURCE_CHOOSER | RequestCodes.PICK_PICTURE_FROM_DOCUMENTS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openChooserWithDocuments(android.app.Fragment fragment, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntent(fragment.getActivity(), chooserTitle, type);
            fragment.startActivityForResult(intent, RequestCodes.SOURCE_CHOOSER | RequestCodes.PICK_PICTURE_FROM_DOCUMENTS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void openChooserWithGymImages(Activity activity, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntentForGymImages(activity, chooserTitle, true, type);
            activity.startActivityForResult(intent,REQUEST_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openChooserWithVideoGallery(Activity activity, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntentForVideo(activity, chooserTitle, true, type);
            activity.startActivityForResult(intent, RequestCodes.SOURCE_CHOOSER | RequestCodes.PICK_PICTURE_FROM_VIDEO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openChooserWithVideoGallery(Fragment fragment, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntentForVideo(fragment.getActivity(), chooserTitle, true, type);
            fragment.startActivityForResult(intent, RequestCodes.SOURCE_CHOOSER | RequestCodes.PICK_PICTURE_FROM_VIDEO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openChooserWithGallery(Activity activity, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntent(activity, chooserTitle, true, type);
            activity.startActivityForResult(intent, RequestCodes.SOURCE_CHOOSER | RequestCodes.PICK_PICTURE_FROM_GALLERY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void openChooserWithGallery(Fragment fragment, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntent(fragment.getActivity(), chooserTitle, true, type);
            fragment.startActivityForResult(intent, RequestCodes.SOURCE_CHOOSER | RequestCodes.PICK_PICTURE_FROM_GALLERY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openChooserWithGallery(android.app.Fragment fragment, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntent(fragment.getActivity(), chooserTitle, true, type);
            fragment.startActivityForResult(intent, RequestCodes.SOURCE_CHOOSER | RequestCodes.PICK_PICTURE_FROM_GALLERY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openDocuments(Activity activity, int type) {
        Intent intent = createDocumentsIntent(activity, type);
        activity.startActivityForResult(intent, RequestCodes.PICK_PICTURE_FROM_DOCUMENTS);
    }

    public static void openDocuments(Fragment fragment, int type) {
        Intent intent = createDocumentsIntent(fragment.getContext(), type);
        fragment.startActivityForResult(intent, RequestCodes.PICK_PICTURE_FROM_DOCUMENTS);
    }

    public static void openDocuments(android.app.Fragment fragment, int type) {
        Intent intent = createDocumentsIntent(fragment.getActivity(), type);
        fragment.startActivityForResult(intent, RequestCodes.PICK_PICTURE_FROM_DOCUMENTS);
    }

    /**
     * Opens default galery or a available galleries picker if there is no default
     *
     * @param type Custom type of your choice, which will be returned with the images
     */
    public static void openGallery(Activity activity, int type) {
        Intent intent = createGalleryIntent(activity, type);
        activity.startActivityForResult(intent, RequestCodes.PICK_PICTURE_FROM_GALLERY);
    }

    /**
     * Opens default galery or a available galleries picker if there is no default
     *
     * @param type Custom type of your choice, which will be returned with the images
     */
    public static void openGallery(Fragment fragment, int type) {
        Intent intent = createGalleryIntent(fragment.getContext(), type);
        fragment.startActivityForResult(intent, RequestCodes.PICK_PICTURE_FROM_GALLERY);
    }

    /**
     * Opens default galery or a available galleries picker if there is no default
     *
     * @param type Custom type of your choice, which will be returned with the images
     */
    public static void openGallery(android.app.Fragment fragment, int type) {
        Intent intent = createGalleryIntent(fragment.getActivity(), type);
        fragment.startActivityForResult(intent, RequestCodes.PICK_PICTURE_FROM_GALLERY);
    }

    public static void openCamera(Activity activity, int type) {
        Intent intent = createCameraIntent(activity, type);
        activity.startActivityForResult(intent, RequestCodes.TAKE_PICTURE);
    }

    public static void openVideoCamera(Activity activity, int type) {
        try {
            Intent intent = createVideoIntent(activity, type);
            activity.startActivityForResult(intent, RequestCodes.TAKE_PICTURE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Intent createVideoIntent(@NonNull Context context, int type) throws IOException {
        storeType(context, type);

        Uri outputFileUri = createVideoPictureFile(context);

        List<Intent> cameraIntents = new ArrayList<>();

        Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        captureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> camList = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : camList) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);

            cameraIntents.add(intent);
        }
        return captureIntent;
    }

    public static void openCamera(Fragment fragment, int type) {
        Intent intent = createCameraIntent(fragment.getActivity(), type);
        fragment.startActivityForResult(intent, RequestCodes.TAKE_PICTURE);
    }

    public static void openCamera(android.app.Fragment fragment, int type) {
        Intent intent = createCameraIntent(fragment.getActivity(), type);
        fragment.startActivityForResult(intent, RequestCodes.TAKE_PICTURE);
    }

    public static void openPhotoVideoGallery(Activity activity, int type) {
        storeType(activity, type);

        if (Build.VERSION.SDK_INT < 19) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/* video/*");
            activity.startActivityForResult(photoPickerIntent, RequestCodes.PICK_PICTURE_VIDEO);
        } else {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerIntent.setType("image/* video/*");
            activity.startActivityForResult(photoPickerIntent, RequestCodes.PICK_PICTURE_VIDEO);
        }
    }

    @Nullable
    private static File takenCameraPicture(Context context) throws IOException, URISyntaxException {
        String lastCameraPhoto = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_LAST_CAMERA_PHOTO, null);
        if (lastCameraPhoto != null) {
            return new File(lastCameraPhoto);
        } else {
            return null;
        }
    }

    public static void handleActivityResult(int requestCode, int resultCode, Intent data, Activity activity, @NonNull Callbacks callbacks) {
        boolean isEasyImage = (requestCode & RequestCodes.PHOTPICKER_IDENTIFICATOR) > 0;
        if (isEasyImage) {
            requestCode &= ~RequestCodes.SOURCE_CHOOSER;
            if (requestCode == RequestCodes.PICK_PICTURE_FROM_GALLERY || requestCode == RequestCodes.TAKE_PICTURE || requestCode == RequestCodes.PICK_PICTURE_FROM_DOCUMENTS || requestCode == RequestCodes.PICK_PICTURE_FROM_VIDEO || requestCode == RequestCodes.PICK_PICTURE_VIDEO) {
                if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == RequestCodes.PICK_PICTURE_FROM_DOCUMENTS && !isPhoto(data)) {
                        onPictureReturnedFromDocuments(data, activity, callbacks);
                    } else if (requestCode == RequestCodes.PICK_PICTURE_FROM_GALLERY && !isPhoto(data)) {
                        LogUtil.i(TAG, "PICK_PICTURE_FROM_GALLERY");
                        onPictureReturnedFromGallery(data, activity, callbacks);

                    } else if (requestCode == RequestCodes.PICK_PICTURE_FROM_VIDEO && !isPhoto(data)) {
                        LogUtil.i("PhotoPicker", "PICK_PICTURE_FROM_VIDEO: + " + data);
                        onPictureReturnedFromGallery(data, activity, callbacks);

                    } else if (requestCode == RequestCodes.PICK_PICTURE_VIDEO && !isPhoto(data)) {
                        LogUtil.i("PhotoPicker", "PICK_PICTURE_VIDEO: " + data);
                        resolvedLibraryFile(data, activity, callbacks);

                    } else if (requestCode == RequestCodes.TAKE_PICTURE) {
                        LogUtil.i(TAG, "TAKE_PICTURE");
                        onPictureReturnedFromCamera(activity, callbacks);

                    } else if (isPhoto(data)) {
                        LogUtil.i(TAG, "else if");
                        onPictureReturnedFromCamera(activity, callbacks);

                    } else {
                        LogUtil.i(TAG, "else");
                        onPictureReturnedFromDocuments(data, activity, callbacks);
                    }
                } else {
                    callbacks.onCanceled(ImageSource.DOCUMENTS, restoreType(activity));
                    if (requestCode == RequestCodes.PICK_PICTURE_FROM_DOCUMENTS) {
                        callbacks.onCanceled(ImageSource.DOCUMENTS, restoreType(activity));
                    } else if (requestCode == RequestCodes.PICK_PICTURE_FROM_GALLERY) {
                        callbacks.onCanceled(ImageSource.GALLERY, restoreType(activity));
                    } else {
                        callbacks.onCanceled(ImageSource.CAMERA, restoreType(activity));
                    }
                }
            }
        }
    }
    public static void handleGymCameraImageResult(int requestCode, int resultCode, Intent data, Activity activity, @NonNull Callbacks callbacks) {
                if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == RequestCodes.PICK_PICTURE_FROM_DOCUMENTS && !isPhoto(data)) {
                        onPictureReturnedFromDocuments(data, activity, callbacks);
                    } else if (requestCode == RequestCodes.PICK_PICTURE_FROM_GALLERY && !isPhoto(data)) {
                        LogUtil.i(TAG, "PICK_PICTURE_FROM_GALLERY");
                        onPictureReturnedFromGallery(data, activity, callbacks);

                    } else if (requestCode == RequestCodes.PICK_PICTURE_FROM_VIDEO && !isPhoto(data)) {
                        LogUtil.i("PhotoPicker", "PICK_PICTURE_FROM_VIDEO: + " + data);
                        onPictureReturnedFromGallery(data, activity, callbacks);

                    } else if (requestCode == RequestCodes.PICK_PICTURE_VIDEO && !isPhoto(data)) {
                        LogUtil.i("PhotoPicker", "PICK_PICTURE_VIDEO: " + data);
                        resolvedLibraryFile(data, activity, callbacks);

                    } else if (requestCode == RequestCodes.TAKE_PICTURE) {
                        LogUtil.i(TAG, "TAKE_PICTURE");
                        onPictureReturnedFromCamera(activity, callbacks);

                    } else if (isPhoto(data)) {
                        LogUtil.i(TAG, "else if");
                        onPictureReturnedFromCamera(activity, callbacks);

                    } else {
                        LogUtil.i(TAG, "else");
                        onPictureReturnedFromDocuments(data, activity, callbacks);
                    }
                } else {
                    callbacks.onCanceled(ImageSource.DOCUMENTS, restoreType(activity));
                    if (requestCode == RequestCodes.PICK_PICTURE_FROM_DOCUMENTS) {
                        callbacks.onCanceled(ImageSource.DOCUMENTS, restoreType(activity));
                    } else if (requestCode == RequestCodes.PICK_PICTURE_FROM_GALLERY) {
                        callbacks.onCanceled(ImageSource.GALLERY, restoreType(activity));
                    } else {
                        callbacks.onCanceled(ImageSource.CAMERA, restoreType(activity));
                    }
                }
    }

    private static boolean isPhoto(Intent data) {
        return data == null || (data.getData() == null && data.getClipData() == null);
    }

    public static boolean willHandleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.SOURCE_CHOOSER || requestCode == RequestCodes.PICK_PICTURE_FROM_GALLERY || requestCode == RequestCodes.TAKE_PICTURE || requestCode == RequestCodes.PICK_PICTURE_FROM_DOCUMENTS) {
            return true;
        }
        return false;
    }

    private static Intent plainGalleryPickerIntent() {
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    public static boolean canDeviceHandleGallery(@NonNull Context context) {
        return plainGalleryPickerIntent().resolveActivity(context.getPackageManager()) != null;
    }

    /**
     * @param context context
     * @return File containing lastly taken (using camera) photo. Returns null if there was no photo taken or it doesn't exist anymore.
     */
    public static File lastlyTakenButCanceledPhoto(@NonNull Context context) {
        String filePath = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_LAST_CAMERA_PHOTO, null);
        if (filePath == null) return null;
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    private static void onPictureReturnedFromDocuments(Intent data, Activity activity, @NonNull Callbacks callbacks) {
        try {
            Uri photoPath = data.getData();
            File photoFile = PhotoPickerFiles.pickedExistingPicture(activity, photoPath);
            callbacks.onImagesPicked(singleFileList(photoFile), ImageSource.DOCUMENTS, restoreType(activity), restoreType(activity));

            if (configuration(activity).shouldCopyTakenPhotosToPublicGalleryAppFolder()) {
                PhotoPickerFiles.copyFilesInSeparateThread(activity, singleFileList(photoFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
            callbacks.onImagePickerError(e, ImageSource.DOCUMENTS, restoreType(activity));
        }
    }

    private static void onPictureReturnedFromGallery(Intent data, Activity activity, @NonNull Callbacks callbacks) {
        try {
            ClipData clipData = data.getClipData();
            List<File> files = new ArrayList<>();
            if (clipData == null) {
                Uri uri = data.getData();
                File file = PhotoPickerFiles.pickedExistingPicture(activity, uri);
                files.add(file);
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    File file = PhotoPickerFiles.pickedExistingPicture(activity, uri);
                    files.add(file);
                }
            }

            if (configuration(activity).shouldCopyTakenPhotosToPublicGalleryAppFolder()) {
                PhotoPickerFiles.copyFilesInSeparateThread(activity, files);
            }

            callbacks.onImagesPicked(files, ImageSource.GALLERY, restoreType(activity), restoreType(activity));
        } catch (Exception e) {
            e.printStackTrace();
            callbacks.onImagePickerError(e, ImageSource.GALLERY, restoreType(activity));
        }
    }

    private static void onPictureReturnedFromCamera(Activity activity, @NonNull Callbacks callbacks) {
        try {
            String lastImageUri = PreferenceManager.getDefaultSharedPreferences(activity).getString(KEY_PHOTO_URI, null);
            if (!TextUtils.isEmpty(lastImageUri)) {
                revokeWritePermission(activity, Uri.parse(lastImageUri));
            }

            File photoFile = PhotoPicker.takenCameraPicture(activity);
            List<File> files = new ArrayList<>();
            files.add(photoFile);

            if (photoFile == null) {
                Exception e = new IllegalStateException("Unable to get the picture returned from camera");
                callbacks.onImagePickerError(e, ImageSource.CAMERA, restoreType(activity));
            } else {
                if (configuration(activity).shouldCopyTakenPhotosToPublicGalleryAppFolder()) {
                    //PhotoPickerFiles.copyFilesInSeparateThread(activity, singleFileList(photoFile));
                }

                copyFileToFolder(activity, photoFile);

                callbacks.onImagesPicked(files, ImageSource.CAMERA, restoreType(activity), restoreType(activity));
            }

            PreferenceManager.getDefaultSharedPreferences(activity)
                    .edit()
                    .remove(KEY_LAST_CAMERA_PHOTO)
                    .remove(KEY_PHOTO_URI)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
            callbacks.onImagePickerError(e, ImageSource.CAMERA, restoreType(activity));
        }
    }

    private static void copyFileToFolder(Activity activity, File fileToCopy) {
        try {
            String fileExtension = "" + PhotoPickerFiles.getMimeType(activity, Uri.fromFile(fileToCopy));

            LogUtil.i(TAG, "copyFileToFolder fileExtension: " + fileExtension);

            ArrayList<String> arrValidImageFormat = CommonUtil.convertAarryToList(
                    ResourceUtils.getStringArray(R.array.photo_attachment_formats));

            ArrayList<String> arrValidVideoFormat = CommonUtil.convertAarryToList(
                    ResourceUtils.getStringArray(R.array.video_attachment_formats));

            /*if (!CommonUtil.isNullString(fileExtension) && arrValidImageFormat.contains("" + fileExtension)) {
                configuration(activity)
                        .setImagesFolderName(Constants.DIRECTORY_RECEIVE_PHOTO);

                File fileToSave = PhotoPicker.getCameraPictureLocation(activity);

                PhotoPicker.copyFile(fileToCopy, fileToSave);
            } else */
            if (!CommonUtil.isNullString(fileExtension) && arrValidVideoFormat.contains("" + fileExtension)) {
                LogUtil.i(TAG, "copyFileToFolder for video");
                PhotoPicker.configuration(activity)
                        .setImagesFolderName(Constants.DIRECTORY_RECEIVE_VIDEO);

                File fileToSave = PhotoPicker.getCameraVideoLocation(activity);

                PhotoPicker.copyFile(fileToCopy, fileToSave);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to clear configuration. Would likely be used in onDestroy(), onDestroyView()...
     *
     * @param context context
     */
    public static void clearConfiguration(@NonNull Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .remove(BundleKeys.FOLDER_NAME)
                .remove(BundleKeys.ALLOW_MULTIPLE)
                .remove(BundleKeys.COPY_TAKEN_PHOTOS)
                .remove(BundleKeys.COPY_PICKED_IMAGES)
                .apply();
    }

    public static PhotoPickerConfiguration configuration(@NonNull Context context) {
        return new PhotoPickerConfiguration(context);
    }

    /**
     * @param : {uri} uri of selected data
     * @return : {file} File of selected data
     * @throws :
     * @purpose : To distiguish photo and video from uri. To get original path from from uri
     * @Date : 18/10/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @Change History :
     * @since : 1.0.0
     */
    public static File resolvedLibraryFile(Context context, Uri uri, Fragment fragment) {
        try {
            if (uri.toString().contains("image")) { // If user select Image from library

                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = context.getContentResolver().query(uri, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String selectedImagePath = c.getString(columnIndex);
                c.close();

                File selectedFile = new File("" + selectedImagePath); // Selected file

                configuration(context)
                        .setImagesFolderName(Constants.DIRECTORY_SENT_PHOTO); // Set directory

                File fileToSave = PhotoPicker.getCameraPictureLocation(context); // Temp file to copy at this location

                LogUtil.i(TAG, "Selected image path: " + selectedImagePath);

                if (fileToSave != null) { // copy file to pre defined location
                    copyFile(selectedFile, fileToSave);
                    return fileToSave;
                } else {
                    return selectedFile;
                }
            } else if (uri.toString().contains("video")) { // If user select Video from library

                String selectedVideoPath = getVideoPath(uri, fragment);

                if (selectedVideoPath == null) {
                    selectedVideoPath = generatePath(uri, fragment);
                }

                if (selectedVideoPath != null) {
                    LogUtil.i(TAG, "Selected video path: " + selectedVideoPath);

                    File selectedFile = new File("" + selectedVideoPath);

                    PhotoPicker.configuration(context)
                            .setImagesFolderName(Constants.DIRECTORY_SENT_VIDEO);

                    File fileToSave = PhotoPicker.getCameraVideoLocation(context);

                    if (fileToSave != null) { // copy file to pre defined location
                        copyFile(selectedFile, fileToSave);
                        return fileToSave;
                    } else {
                        return selectedFile;
                    }
                } else {
                    LogUtil.i(TAG, "Selected video path failed.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File resolvedLibraryFile(Intent data, Activity activity, @NonNull Callbacks callbacks) {
        int type = -1;
        try {
            Uri dataUri = data.getData();
            String fileExtension = "" + PhotoPickerFiles.getMimeType(activity, dataUri);

            ArrayList<String> arrValidImageFormat = CommonUtil.convertAarryToList(
                    ResourceUtils.getStringArray(R.array.photo_attachment_formats));

            ArrayList<String> arrValidVideoFormat = CommonUtil.convertAarryToList(
                    ResourceUtils.getStringArray(R.array.video_attachment_formats));

            if (!CommonUtil.isNullString(fileExtension) && arrValidImageFormat.contains("" + fileExtension)) {
                type = SELECT_PHOTO;
                configuration(activity)
                        .setImagesFolderName(Constants.DIRECTORY_SENT_PHOTO); // Set directory
            } else if (!CommonUtil.isNullString(fileExtension) && arrValidVideoFormat.contains("" + fileExtension)) {
                type = SELECT_VIDEO;
                PhotoPicker.configuration(activity)
                        .setImagesFolderName(Constants.DIRECTORY_SENT_VIDEO);
            } else {
                return null;
            }

            ClipData clipData = data.getClipData();
            List<File> files = new ArrayList<>();
            if (clipData == null) {
                Uri uri = data.getData();
                File file = PhotoPickerFiles.pickedExistingPicture(activity, uri);
                files.add(file);
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    File file = PhotoPickerFiles.pickedExistingPicture(activity, uri);
                    files.add(file);
                }
            }
            callbacks.onImagesPicked(files, ImageSource.GALLERY, type, restoreType(activity));

        } catch (Exception e) {
            e.printStackTrace();
            callbacks.onImagePickerError(e, ImageSource.GALLERY, restoreType(activity));
        }
        return null;
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        FileUtils.copyFile(sourceFile, destFile);
        LogUtil.i(TAG, "Common source file exist: " + sourceFile.exists());
        LogUtil.i(TAG, "Common destination file exist: " + destFile.exists());
    }

    /**
     * @param : {uri} uri of selected data
     *          {fragment} current fragment
     * @return :
     * @throws :
     * @purpose : To get video path from uri
     * @Date : 18/10/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @Change History :
     * @since : 1.0.0
     */
    public static String getVideoPath(Uri uri, Fragment fragment) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = fragment.getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    /**
     * @param :{uri} uri of selected data
     *               {fragment} current fragment
     * @return :
     * @throws :
     * @purpose : Check and derive file path from uri
     * @Date : 18/10/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @Change History :
     * @since : 1.0.0
     */
    public static String generatePath(Uri uri, Fragment fragment) {
        String filePath = null;
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            filePath = generatePathFromKitkat(uri, fragment);
        }

        if (filePath != null) {
            return filePath;
        }

        Cursor cursor = fragment.getActivity().getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath == null ? uri.getPath() : filePath;
    }

    /**
     * @param : {uri} uri of selected data
     *          {fragment} current fragment
     * @return :
     * @throws :
     * @purpose : Check API version and derive file path from uri
     * @Date : 18/10/2017
     * @author : Harsh Patel
     * @version : 1.0.0
     * @Change History :
     * @since : 1.0.0
     */
    @TargetApi(19)
    public static String generatePathFromKitkat(Uri uri, Fragment fragment) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(fragment.getActivity(), uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);

            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = fragment.getActivity().getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);


            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();
        }
        return filePath;
    }

    public enum ImageSource {
        GALLERY, DOCUMENTS, CAMERA
    }

    public interface Callbacks {
        void onImagePickerError(Exception e, ImageSource source, int type);

        void onImagesPicked(@NonNull List<File> imageFiles, ImageSource source, int type, int absoluteType);

        void onCanceled(ImageSource source, int type);
    }
}