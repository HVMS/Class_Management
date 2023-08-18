package com.globalitians.employees.utility;

public abstract class PhotoPickerCallback implements PhotoPicker.Callbacks {

    @Override
    public void onImagePickerError(Exception e, PhotoPicker.ImageSource source, int type) {
    }

    @Override
    public void onCanceled(PhotoPicker.ImageSource source, int type) {
    }
}
