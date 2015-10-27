package com.ihs.message.types;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.commons.config.HSConfig;
import com.ihs.commons.connection.HSHttpMultiPart;
import com.ihs.commons.connection.HSServerAPIConnection;
import com.ihs.commons.utils.HSLog;
import com.ihs.message.managers.DownloadManager;
import com.ihs.message.managers.DownloadManager.DownloadOperationType;
import com.ihs.message.utils.ImageUtils;
import com.ihs.message.utils.ImageUtils.ImageClass;
import com.ihs.message.utils.ImageUtils.ImageCompressionSetting;
import com.ihs.message.utils.ImageUtils.ImageSizeClass;
import com.ihs.message.utils.ImageUtils.Size;
import com.ihs.message.utils.Utils;

/**
 * 图片消息类
 */
@SuppressLint("Assert")
public class HSImageMessage extends HSBaseMessage implements IMediaProtocol {

    private static final String TAG = HSImageMessage.class.getName();

    private static final int DO_NOT_UPLOAD = 0;
    private static final int NO_COMPRESSION = -1;

    private Size thumbnailSize; // 缩略图尺寸
    private Size normalImageSize; // 大图尺寸
    private Size originalImageSize; // 原图尺寸（当前版本不适用）

    /**
     * @return 获取缩略图宽度
     */
    public int getThumbnailWidth() {
        return thumbnailSize.getWidth();
    }

    /**
     * @return 获取缩略图高度
     */
    public int getThumbnailHeight() {
        return thumbnailSize.getHeight();
    }

    public void setThumbnailWidth(int width) {
        thumbnailSize.setWidth(width);
    }

    public void setThumbnailHeight(int height) {
        thumbnailSize.setHeight(height);
    }

    /**
     * @return 获取大图宽度
     */
    public int getNormalImageWidth() {
        return normalImageSize.getWidth();
    }

    /**
     * @return 获取大图高度
     */
    public int getNormalImageHeight() {
        return normalImageSize.getHeight();
    }

    public void setNormalImageWidth(int width) {
        normalImageSize.setWidth(width);
    }

    public void setNormalImageHeight(int height) {
        normalImageSize.setHeight(height);
    }

    public int getOriginalImageWidth() {
        return originalImageSize.getWidth();
    }

    public int getOriginalImageHeight() {
        return originalImageSize.getHeight();
    }

    public void setOriginalImageWidth(int width) {
        originalImageSize.setWidth(width);
    }

    public void setOriginalImageHeight(int height) {
        originalImageSize.setHeight(height);
    }

    /**
     * @return 小图文件下载状态
     */
    public HSMessageMediaStatus getThumbnailMediaStatus() {
        return HSMessageMediaStatus.valueOf(getMediaStatusBackend() % 4);
    }

    public void setThumbnailMediaStatus(HSMessageMediaStatus mediaStatus) {
        setMediaStatusBackend(mediaStatus.getValue() | (getNormalImageMediaStatus().getValue() << 2) | (getOriginalImageMediaStatus().getValue() << 4));
    }

    /**
     * @return 大图文件下载状态
     */
    public HSMessageMediaStatus getNormalImageMediaStatus() {
        return HSMessageMediaStatus.valueOf((getMediaStatusBackend() >> 2) & 0x3);
    }

    public void setNormalImageMediaStatus(HSMessageMediaStatus mediaStatus) {
        setMediaStatusBackend((mediaStatus.getValue() << 2) | (getMediaStatus().getValue()) | (getOriginalImageMediaStatus().getValue() << 4));
    }

    public HSMessageMediaStatus getOriginalImageMediaStatus() {
        return HSMessageMediaStatus.valueOf((getMediaStatusBackend() >> 4) & 0x3);
    }

    public void setOriginalImageMediaStatus(HSMessageMediaStatus mediaStatus) {
        setMediaStatusBackend((getMediaStatus().getValue()) | (getNormalImageMediaStatus().getValue() << 2) | (mediaStatus.getValue() << 4));
    }

    /**
     * 图片消息的构造方法（简版）
     * 
     * 图片消息中会包含两幅图像：缩略图（thumbnail）和大图（normal image）。缩略图会按比例将图片长边缩小到 200 像素，大图会按比例将图片长边缩小到 960
     * 像素（若原图即小于此尺寸，则不缩小）。当前版本暂不支持原图发送
     * 
     * @param to 消息接收者的 mid
     * @param imageFilePath 图片文件的本地路径。调用构造方法前，应当在 imageFilePath 路径处准备好发送的图片原文件（支持的原文件格式包括 bmp, gif, jpg, tif,
     *            png），构造方法会对图片进行缩小、压缩、格式转换（jpg 格式的原图保留 jpg 格式，其余一律转换为 png 格式）和重命名，复制到统一的媒体文件路径下进行管理
     */
    public HSImageMessage(String to, String imageFilePath) {
        this(to, imageFilePath, false, true);
    }

    /**
     * 图片消息的构造方法，当前版本的用户不应调用此方法，请调用上面的简版构造方法 HSImageMessage(String to, String imageFilePath)
     * 
     * @param to 消息接收者的 mid
     * @param imageFilePath 图片文件的本地路径。调用构造方法前，应当在 imageFilePath 路径处准备好发送的图片原文件（支持的原文件格式包括 bmp, gif, jpg, tif,
     *            png），构造方法会将图片按照配置文件指定的方式，进行缩小、压缩、格式转换（jpg 格式的原图保留 jpg 格式，其余一律转换为 png
     *            格式）和重命名，复制到统一的媒体文件路径下进行管理。当根据配置，其中存在两种或三种图片的尺寸和压缩情况完全一致时，将合并只存储一份本地文件
     * @param uploadOrigin 是否发送原图，设置为 true 发送原图，设置为 false 不发送原图
     * @param thuJavaProject 临时参数，简版构造方法开关
     */
    public HSImageMessage(String to, String imageFilePath, boolean uploadOrigin, boolean thuJavaProject) {
        // @formatter:off
        super(HSMessageType.IMAGE,                                        // type
              null,                                                       // content
              0,                                                          // msgServerID
              null,                                                       // msgID
              HSBaseMessage.PUSH_TAG_IMAGE,                               // pushTag
              true,                                                       // isMessageOriginate
              HSAccountManager.getInstance().getMainAccount().getMID(),   // from
              to,                                                         // to
              new Date(HSAccountManager.getInstance().getServerTime()),   // timestamp
              HSBaseMessage.HSMessageStatus.SENDING,                      // status
              HSBaseMessage.HSMessageMediaStatus.DOWNLOADED,              // mediaStatus
              1);                                                         // downloadProgress
        // @formatter:on

        // Get configurations
        int thumbnailParam, normalParam;
        float compressionQuality;
        if (!thuJavaProject) {
            thumbnailParam = Integer.parseInt(HSConfig.getString("libMessage", "Image", "Thumbnail"));
            normalParam = Integer.parseInt(HSConfig.getString("libMessage", "Image", "Normal"));
            compressionQuality = Float.parseFloat(HSConfig.getString("libMessage", "Image", "JPGCompressionQuality"));
        } else {
            thumbnailParam = 200;
            normalParam = 960;
            compressionQuality = (float) 0.6;
        }
        
        // Check parameters' validity
        if (thumbnailParam > 0 && normalParam > 0) {
            assert (thumbnailParam <= normalParam) : "Configuration error: thumbnail cannot be larger than normal image.";
        }
        assert (0.0 <= compressionQuality && compressionQuality <= 1.0) : "Configuration error: compression quality must between 0 and 1.";

        // Calculate size of images
        // Note that not all sizes are used if some of the image types do not exist
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(imageFilePath, options);
        Size originalImageSize = new Size(options.outWidth, options.outHeight);
        Size normalImageSize = ImageUtils.getSizeWithSizeLimit(originalImageSize, normalParam);
        Size thumbnailSize = ImageUtils.getSizeWithSizeLimit(originalImageSize, thumbnailParam);

        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        if (!thuJavaProject) {
            bitmap = BitmapFactory.decodeFile(imageFilePath, options);
        } else {
            options.inSampleSize = (int) Math.floor(1.0 / ImageUtils.getScaleWithSizeLimit(originalImageSize, normalParam));
            bitmap = BitmapFactory.decodeFile(imageFilePath, options);
            bitmap = Bitmap.createScaledBitmap(bitmap, normalImageSize.getWidth(), normalImageSize.getHeight(), false);
        }

        ImageSizeClass originalSizeClass = ImageSizeClass.LARGE;
        ImageClass thumbnailImageClass = new ImageClass();
        ImageClass normalImageClass = new ImageClass();
        ImageClass originalImageClass = new ImageClass();

        JSONObject content = new JSONObject();

        // For each type of image,
        //
        //   1. Prepare its message `content`
        //   2. Calculate its `image class` for filename generation and file merging
        //
        // @formatter:off
        if (normalParam > 0 && 
                originalImageSize.getWidth() <= normalParam && originalImageSize.getHeight() <= normalParam) {
                originalSizeClass = ImageSizeClass.MEDIUM;
        }
        if (thumbnailParam > 0 &&
            originalImageSize.getWidth() <= thumbnailParam && originalImageSize.getHeight() <= thumbnailParam) {
            originalSizeClass = ImageSizeClass.SMALL;
        }
        try {
            if (uploadOrigin) { // Original image
                JSONObject originalInfo = new JSONObject();
                this.originalImageSize = new Size(originalImageSize.getWidth(), originalImageSize.getHeight());
                originalInfo.put(Constants.WIDTH, originalImageSize.getWidth());
                originalInfo.put(Constants.HEIGHT, originalImageSize.getHeight());
                content.put(Constants.ORIGIN, originalInfo);

                ImageCompressionSetting originalCompressionSetting = ImageCompressionSetting.NO_COMPRESSION;
                originalImageClass = new ImageClass(originalSizeClass, originalCompressionSetting);
            }

            if (normalParam != DO_NOT_UPLOAD) { // Normal image
                JSONObject normalInfo = new JSONObject();
                this.normalImageSize = new Size(normalImageSize.getWidth(), normalImageSize.getHeight());
                normalInfo.put(Constants.WIDTH, normalImageSize.getWidth());
                normalInfo.put(Constants.HEIGHT, normalImageSize.getHeight());
                content.put(Constants.NORMAL, normalInfo);
                
                ImageSizeClass normalSizeClass = (normalParam == NO_COMPRESSION) ?
                        originalSizeClass : ImageSizeClass.MEDIUM;
                if (originalSizeClass == ImageSizeClass.SMALL) {
                    normalSizeClass = ImageSizeClass.SMALL;
                }
                ImageCompressionSetting normalCompressionSetting = (normalParam == NO_COMPRESSION) ?
                        ImageCompressionSetting.NO_COMPRESSION : ImageCompressionSetting.COMPRESSED;
                normalImageClass = new ImageClass(normalSizeClass, normalCompressionSetting);
            }

            if (thumbnailParam != DO_NOT_UPLOAD) { // Thumbnail
                JSONObject thumbnailInfo = new JSONObject();
                this.thumbnailSize = new Size(thumbnailSize.getWidth(), thumbnailSize.getHeight());
                thumbnailInfo.put(Constants.WIDTH, thumbnailSize.getWidth());
                thumbnailInfo.put(Constants.HEIGHT, thumbnailSize.getHeight());
                content.put(Constants.THUMBNAIL, thumbnailInfo);
                
                ImageSizeClass thumbnailSizeClass = (thumbnailParam == NO_COMPRESSION) ?
                        originalSizeClass : ImageSizeClass.SMALL;
                ImageCompressionSetting thumbnailCompressionSetting = (thumbnailParam == NO_COMPRESSION) ?
                        ImageCompressionSetting.NO_COMPRESSION : ImageCompressionSetting.COMPRESSED;
                thumbnailImageClass = new ImageClass(thumbnailSizeClass, thumbnailCompressionSetting);
            }
        // @formatter:on
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setContent(content);
        String msgID = Utils.getOneUUID();

        // File format
        String format = ImageUtils.getTypeOfImageFile(imageFilePath);
        assert (!TextUtils.equals(format, "image/unsupported")) : "The file provided is not a valid image file supported by libMessage.";
        // Save as JPG for JPG files given, PNG for everything else
        String extension = "png";
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
        if (TextUtils.equals(format, "jpg")) {
            extension = "jpg";
            compressFormat = Bitmap.CompressFormat.JPEG;
        }

        // Prepare filename and localFileInfo
        JSONObject localFileInfo = new JSONObject();
        String thumbnailName = "";
        String normalImageName = "";
        String originalImageName = "";
        if (thumbnailParam != DO_NOT_UPLOAD) { // Thumbnail
            thumbnailName = msgID + thumbnailImageClass.getFileNameSuffix() + "." + extension;
            try {
                localFileInfo.put(Constants.THUMBNAIL, thumbnailName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (normalParam != DO_NOT_UPLOAD) { // Normal
            normalImageName = msgID + normalImageClass.getFileNameSuffix() + "." + extension;
            try {
                localFileInfo.put(Constants.NORMAL, normalImageName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (uploadOrigin) { // Original
            originalImageName = msgID + originalImageClass.getFileNameSuffix() + "." + extension;
            try {
                localFileInfo.put(Constants.ORIGIN, originalImageName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        setLocalFileInfo(localFileInfo);
        HSLog.d(TAG, "localFileInfo" + getLocalFileInfo());

        setMsgID(msgID);
        setGoHttp(true);

        // Write image files to file system
        HashSet<String> imageFiles = new HashSet<String>();
        if (thumbnailParam != DO_NOT_UPLOAD) { // Thumbnail
            if (!imageFiles.contains(thumbnailName)) {
                String thumbnailPath = this.getThumbnailFilePath();
                try {
                    FileOutputStream out = new FileOutputStream(thumbnailPath);
                    if (thumbnailParam == NO_COMPRESSION) {
                        bitmap.compress(compressFormat, 100, out);
                    } else {
                        Bitmap thumbnailBitmap = Bitmap.createScaledBitmap(bitmap, thumbnailSize.getWidth(), thumbnailSize.getHeight(), false);
                        // PNG will ignore quality setting, so it's fine to just pass in a 0~100 ranged quality for JPG
                        thumbnailBitmap.compress(compressFormat, (int) (compressionQuality * 100), out);
                    }
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageFiles.add(thumbnailName);
            }
        }
        if (normalParam != DO_NOT_UPLOAD) { // Normal
            if (!imageFiles.contains(normalImageName)) {
                String normalImagePath = this.getNormalImageFilePath();
                try {
                    FileOutputStream out = new FileOutputStream(normalImagePath);
                    if (normalParam == NO_COMPRESSION) {
                        bitmap.compress(compressFormat, 100, out);
                    } else {
                        Bitmap normalImageBitmap = Bitmap.createScaledBitmap(bitmap, normalImageSize.getWidth(), normalImageSize.getHeight(), false);
                        // PNG will ignore quality setting, so it's fine to just pass in a 0~100 ranged quality for JPG
                        normalImageBitmap.compress(compressFormat, (int) (compressionQuality * 100), out);
                    }
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageFiles.add(normalImageName);
            }
        }
        if (uploadOrigin) { // Original
            if (!imageFiles.contains(originalImageName)) {
                String originalImagePath = this.getOriginalImageFilePath();
                try {
                    FileOutputStream out = new FileOutputStream(originalImagePath);
                    // PNG will ignore quality setting, so it's fine to just pass in a 0~100 ranged quality for JPG
                    bitmap.compress(compressFormat, 100, out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageFiles.add(originalImageName);
            }
        }
    }

    public String getThumbnailFilePath() {
        JSONObject localFileInfo = getLocalFileInfo();
        String localFileName = null;
        if (localFileInfo != null) {
            localFileName = localFileInfo.optString(Constants.THUMBNAIL);
            if (TextUtils.isEmpty(localFileName) == false) {
                return Utils.getPath(localFileName);
            }
        }
        String remoteFilePath = getContent().optJSONObject(Constants.FILES).optString(Constants.THUMBNAIL);
        return Utils.getLocalFilePathOfFileRemotePath(remoteFilePath, getMsgID());
    }

    public String getThumbnailRemotePath() {
        JSONObject files = getContent().optJSONObject(Constants.FILES);
        if (files != null) {
            return files.optString(Constants.THUMBNAIL);
        }
        return null;
    }

    public String getNormalImageFilePath() {
        JSONObject localFileInfo = getLocalFileInfo();
        String localFileName = null;
        if (localFileInfo != null) {
            localFileName = localFileInfo.optString(Constants.NORMAL);
            if (TextUtils.isEmpty(localFileName) == false) {
                return Utils.getPath(localFileName);
            }
        }
        String remoteFilePath = getContent().optJSONObject(Constants.FILES).optString(Constants.NORMAL);
        return Utils.getLocalFilePathOfFileRemotePath(remoteFilePath, getMsgID());
    }

    public String getNormalImageRemotePath() {
        JSONObject files = getContent().optJSONObject(Constants.FILES);
        if (files != null) {
            return files.optString(Constants.NORMAL);
        }
        return null;
    }

    public String getOriginalImageFilePath() {
        JSONObject localFileInfo = getLocalFileInfo();
        String localFileName = null;
        if (localFileInfo != null) {
            localFileName = localFileInfo.optString(Constants.ORIGIN);
            if (TextUtils.isEmpty(localFileName) == false) {
                return Utils.getPath(localFileName);
            }
        }
        String remoteFilePath = getContent().optJSONObject(Constants.FILES).optString(Constants.ORIGIN);
        return Utils.getLocalFilePathOfFileRemotePath(remoteFilePath, getMsgID());
    }

    public String getOriginalImageRemotePath() {
        JSONObject files = getContent().optJSONObject(Constants.FILES);
        if (files != null) {
            return files.optString(Constants.ORIGIN);
        }
        return null;
    }

    @Override
    public HSServerAPIConnection getServerAPIRequest() {
        ArrayList<HSHttpMultiPart> bodyParts = new ArrayList<HSHttpMultiPart>();
        if (!getLocalFileInfo().optString(Constants.THUMBNAIL).isEmpty()) {
            bodyParts.add(new HSHttpMultiPart(Constants.THUMBNAIL, Constants.THUMBNAIL, "jpg", new File(getThumbnailFilePath())));
        }
        if (!getLocalFileInfo().optString(Constants.NORMAL).isEmpty()) {
            bodyParts.add(new HSHttpMultiPart(Constants.NORMAL, Constants.NORMAL, "jpg", new File(getNormalImageFilePath())));
        }
        if (!getLocalFileInfo().optString(Constants.ORIGIN).isEmpty()) {
            bodyParts.add(new HSHttpMultiPart(Constants.ORIGIN, Constants.ORIGIN, "jpg", new File(getOriginalImageFilePath())));
        }
        return new HSServerAPIConnection(Utils.getMessageSendingURL(), getDataBody(), bodyParts);
    }

    @Override
    public void initMessageSpecialProperties() {
        JSONObject ct = getContent();
        JSONObject thumbnailInfo = ct.optJSONObject(Constants.THUMBNAIL);
        JSONObject normalImageInfo = ct.optJSONObject(Constants.NORMAL);
        JSONObject originalImageInfo = ct.optJSONObject(Constants.ORIGIN);
        if (thumbnailInfo != null) {
            this.thumbnailSize = new Size(thumbnailInfo.optInt(Constants.WIDTH), thumbnailInfo.optInt(Constants.HEIGHT));
        }
        else {
            this.thumbnailSize = new Size(0, 0);
        }
        if (normalImageInfo != null) {
            this.normalImageSize = new Size(normalImageInfo.optInt(Constants.WIDTH), normalImageInfo.optInt(Constants.HEIGHT));
        }
        else {
            this.normalImageSize = new Size(0, 0);
        }
        if (originalImageInfo != null) {
            this.originalImageSize = new Size(originalImageInfo.optInt(Constants.WIDTH), originalImageInfo.optInt(Constants.HEIGHT));
        }
        else {
            this.originalImageSize = new Size(0, 0);
        }
        if (getThumbnailMediaStatus() == HSMessageMediaStatus.DOWNLOADED) {
            setDownloadProgress(1);
        } else {
            setThumbnailMediaStatus(HSMessageMediaStatus.TO_DOWNLOAD);
            setDownloadProgress(0);
        }
        this.setGoHttp(true);
    }

    public HSImageMessage(JSONObject info) {
        super(info);
        initMessageSpecialProperties();
    }

    public HSImageMessage(Cursor cursor) {
        super(cursor);
        initMessageSpecialProperties();
    }

    @Override
    public void download() {
        DownloadManager.getInstance().downloadMessage(this, getThumbnailRemotePath(), getThumbnailFilePath(), DownloadOperationType.THUMBNAIL);
    }

    @Override
    public void cancelDownlad() {
        DownloadManager.getInstance().cancelDownloading(this.getMsgID(), getThumbnailFilePath(), false);
    }

    public void downloadNormalImage() {
        DownloadManager.getInstance().downloadMessage(this, getNormalImageRemotePath(), getNormalImageFilePath(), DownloadOperationType.NORMAL_IMAGE);
    }

    public void cancelNormalImageDownload() {
        DownloadManager.getInstance().cancelDownloading(this.getMsgID(), getNormalImageRemotePath(), false);
    }

    public void downloadOrigignalImage() {
        DownloadManager.getInstance().downloadMessage(this, getOriginalImageRemotePath(), getOriginalImageFilePath(), DownloadOperationType.ORIGINAL_IMAGE);
    }

    public void cancelOriginalImageDownload() {
        DownloadManager.getInstance().cancelDownloading(this.getMsgID(), getOriginalImageRemotePath(), false);
    }
}
