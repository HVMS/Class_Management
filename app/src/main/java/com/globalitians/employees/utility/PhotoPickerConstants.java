package com.globalitians.employees.utility;

public interface PhotoPickerConstants {

    String DEFAULT_FOLDER_NAME = "PhotoPicker";
    String TEMP_FOLDER_NAME = "Temp";

    interface RequestCodes {
        int PHOTPICKER_IDENTIFICATOR = 0b1101101100; //876
        int SOURCE_CHOOSER = 1 << 14;

        int PICK_PICTURE_FROM_DOCUMENTS = PHOTPICKER_IDENTIFICATOR + (1 << 11);
        int PICK_PICTURE_FROM_GALLERY = PHOTPICKER_IDENTIFICATOR + (1 << 12);
        int PICK_PICTURE_FROM_VIDEO = PHOTPICKER_IDENTIFICATOR + (1 << 15);
        int TAKE_PICTURE = PHOTPICKER_IDENTIFICATOR + (1 << 13);
        int PICK_PICTURE_VIDEO = PHOTPICKER_IDENTIFICATOR + (1 << 10);
    }

    interface BundleKeys {
        String FOLDER_NAME = "com.photopicker.folder_name";
        String ALLOW_MULTIPLE = "com.photopicker.allow_multiple";
        String COPY_TAKEN_PHOTOS = "com.photopicker.copy_taken_photos";
        String COPY_PICKED_IMAGES = "com.photopicker.copy_picked_images";
    }
}