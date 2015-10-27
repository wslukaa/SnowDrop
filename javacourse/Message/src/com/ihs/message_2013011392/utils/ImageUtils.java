package com.ihs.message_2013011392.utils;

import java.io.FileInputStream;
import java.io.InputStream;

import android.annotation.SuppressLint;

public class ImageUtils {

    public static class Size {
        private int width;
        private int height;

        public Size(int width, int height) {
            super();
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static enum ImageSizeClass {
        SMALL(0),
        MEDIUM(1),
        LARGE(2);

        private int value;

        private ImageSizeClass(int value) {
            this.value = value;
        }

        public static ImageSizeClass valueOf(int value) {
            switch (value) {
                case 0:
                    return SMALL;
                case 1:
                    return MEDIUM;
                case 2:
                    return LARGE;
            }
            return null;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static enum ImageCompressionSetting {
        NO_COMPRESSION(0),
        COMPRESSED(1);

        private int value;

        private ImageCompressionSetting(int value) {
            this.value = value;
        }

        public static ImageCompressionSetting valueOf(int value) {
            switch (value) {
                case 0:
                    return NO_COMPRESSION;
                case 1:
                    return COMPRESSED;
            }
            return null;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static class ImageClass {

        private int value;

        public ImageClass() {
        }

        public ImageClass(ImageSizeClass sizeClass, ImageCompressionSetting compressionSetting) {
            this.value = sizeClass.getValue() * 2 + compressionSetting.getValue();
        }

        public String getFileNameSuffix() {
            switch (this.value) {
                case 0:
                    return "-small-nocmprs"; // Size class = "small", no compression
                case 1:
                    return "-small-cmprs"; // Size class = "small", compressed
                case 2:
                    return "-medium-nocmprs"; // Size class = "medium", no compression
                case 3:
                    return "-medium-cmprs"; // Size class = "medium", compressed
                case 4:
                    return "-large-nocmprs"; // Size class = "large", no compression
                case 5:
                    return "-large-cmprs"; // Size class = "large", compressed
            }
            return null;
        }
    }

    public ImageUtils() {
    }

    public static float getScaleWithSizeLimit(Size originalImageSize, int sizeLimit) {
        if (sizeLimit <= 0) {
            return (float) 1.0;
        }
        float fLimit = (float) sizeLimit;
        float widthScale = (float) 1.0, heightScale = (float) 1.0;
        if (originalImageSize.getWidth() > fLimit) {
            widthScale = fLimit / originalImageSize.getWidth();
        }
        if (originalImageSize.getHeight() > fLimit) {
            heightScale = fLimit / originalImageSize.getHeight();
        }
        return widthScale < heightScale ? widthScale : heightScale;
    }

    public static Size getSizeWithSizeLimit(Size originalImageSize, int sizeLimit) {
        float scale = getScaleWithSizeLimit(originalImageSize, sizeLimit);
        return new Size((int) (scale * originalImageSize.getWidth()), (int) (scale * originalImageSize.getHeight()));
    }

    @SuppressLint("Assert")
    public static String getTypeOfImageFile(String filePath) {
        final byte[] bmp = { 'B', 'M' };
        final byte[] gif = { 'G', 'I', 'F' };
        final byte[] jpg = { (byte) 0xff, (byte) 0xd8, (byte) 0xff };
        final byte[] tif_ii = { 'I', 'I', (byte) 0x2A, (byte) 0x00 };
        final byte[] tif_mm = { 'M', 'M', (byte) 0x00, (byte) 0x2A };
        final byte[] png = { (byte) 0x89, (byte) 0x50, (byte) 0x4e, (byte) 0x47, (byte) 0x0d, (byte) 0x0a, (byte) 0x1a, (byte) 0x0a };

        byte[] buffer = new byte[8];
        try {
            InputStream inputStream = new FileInputStream(filePath);
            if (inputStream.read(buffer) != buffer.length) {
                assert false : "Something wrong with reading the header of the image file.";
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (memcmp(buffer, bmp, 2) == 0) {
            return "bmp";
        }
        if (memcmp(buffer, gif, 3) == 0) {
            return "gif";
        }
        if (memcmp(buffer, jpg, 3) == 0) {
            return "jpg";
        }
        if (memcmp(buffer, tif_ii, 4) == 0) {
            return "tif";
        }
        if (memcmp(buffer, tif_mm, 4) == 0) {
            return "tif";
        }
        if (memcmp(buffer, png, 8) == 0) {
            return "png";
        }
        return "image/unsupported";
    }

    private static int memcmp(byte[] b1, byte[] b2, int size) {
        for (int i = 0; i < size; i++) {
            if (b1[i] != b2[i]) {
                if ((b1[i] >= 0 && b2[i] >= 0) || (b1[i] < 0 && b2[i] < 0))
                    return b1[i] - b2[i];
                if (b1[i] < 0 && b2[i] >= 0)
                    return 1;
                if (b2[i] < 0 && b1[i] >= 0)
                    return -1;
            }
        }
        return 0;
    }
}
