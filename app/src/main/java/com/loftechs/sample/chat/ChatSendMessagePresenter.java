package com.loftechs.sample.chat;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.google.common.base.Strings;
import com.loftechs.sample.SampleApp;
import com.loftechs.sample.model.MessageFlowManager;
import com.loftechs.sdk.im.channels.LTChannelType;
import com.loftechs.sdk.im.message.LTFileMessageResponse;
import com.loftechs.sdk.im.message.LTFileMessageStatus;
import com.loftechs.sdk.im.message.LTFileTransferResult;
import com.loftechs.sdk.im.message.LTFileType;
import com.loftechs.sdk.im.message.LTSendMessageResponse;
import com.loftechs.sdk.storage.LTStorageManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.loftechs.sdk.im.message.LTFileMessageStatus.STATUS_FILE;

public class ChatSendMessagePresenter implements ChatSendMessageContract.Presenter {
    private static final String TAG = ChatSendMessagePresenter.class.getSimpleName();
    private ChatSendMessageContract.View mView;
    private String mReceiverID;
    private String mChID;
    private LTChannelType mChType;

    public ChatSendMessagePresenter(ChatSendMessageContract.View view, String receiverID, String chID, LTChannelType channelType) {
        this.mView = view;
        this.mReceiverID = receiverID;
        this.mChID = chID;
        this.mChType = channelType;
    }

    @Override
    public void create() {
    }


    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void sendTextMessage(String message) {
        MessageFlowManager.getInstance().sendTextMessage(mReceiverID, mChID, mChType, message)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTSendMessageResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTSendMessageResponse sendMessageResponse) {
                        Log.i("sendTextMessage", sendMessageResponse.toString());
                        mView.showShortToast("SendTextMessage success");
                        goBack();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showShortToast("SendTextMessage fail: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void sendImageMessage(Uri uri, Uri thUri) {
        String path = getRealPathFromURI(SampleApp.context, uri);
        File file = new File(path);
        Uri imageUri = Uri.fromFile(file);
        String thPath = getRealPathFromURI(SampleApp.context, thUri);
        File thFile = new File(thPath);
        Uri thImageUri = Uri.fromFile(thFile);
        mView.setProgress(10);
        MessageFlowManager.getInstance().sendImageMessage(mReceiverID, mChID, mChType, imageUri, thImageUri, file.getName())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTFileMessageResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTFileMessageResponse ltFileMessageResponse) {
                        LTFileMessageStatus fileMessageStatus = ltFileMessageResponse.getFileMessageStatus();
                        if (fileMessageStatus == STATUS_FILE) {
                            for (LTFileTransferResult result : ltFileMessageResponse.getFileTransferResults()) {
                                LTFileType fileType = result.getFileType();
                                LTStorageManager.StorageStatus status = result.getStatus();
                                if (fileType == LTFileType.TYPE_FILE && status == LTStorageManager.StorageStatus.UPLOAD_LOADING) {
                                    Log.i("sendImageMessage", "transfer: " + (int) (((double) result.getLoadingBytes() / (double) result.getTotalLength()) * 100) + "%");
                                    mView.setProgress((int) (((double) result.getLoadingBytes() / (double) result.getTotalLength()) * 100));
                                }
                            }
                        } else {
                            Log.i("sendImageMessage", ltFileMessageResponse.toString());
                            mView.showShortToast("sendImageMessage success");
                            goBack();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showShortToast("sendImageMessage fail: " + e.getMessage());
                        mView.setProgress(-1);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void sendDocumentMessage(Uri uri) {
        String path = getPathFromUri(uri);
        File file = new File(path);
        Uri fileUri = Uri.fromFile(file);
        mView.setProgress(10);
        MessageFlowManager.getInstance().sendDocumentMessage(mReceiverID, mChID, mChType, fileUri, file.getName())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LTFileMessageResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LTFileMessageResponse ltFileMessageResponse) {
                        LTFileMessageStatus fileMessageStatus = ltFileMessageResponse.getFileMessageStatus();
                        if (fileMessageStatus == STATUS_FILE) {
                            for (LTFileTransferResult result : ltFileMessageResponse.getFileTransferResults()) {
                                LTFileType fileType = result.getFileType();
                                LTStorageManager.StorageStatus status = result.getStatus();
                                if (fileType == LTFileType.TYPE_FILE && status == LTStorageManager.StorageStatus.UPLOAD_LOADING) {
                                    Log.i("sendDocumentMessage", "transfer: " + (int) (((double) result.getLoadingBytes() / (double) result.getTotalLength()) * 100) + "%");
                                    mView.setProgress((int) (((double) result.getLoadingBytes() / (double) result.getTotalLength()) * 100));
                                }
                            }
                        } else {
                            Log.i("sendDocumentMessage", ltFileMessageResponse.toString());
                            mView.showShortToast("sendDocumentMessage success");
                            goBack();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showShortToast("sendDocumentMessage fail: " + e.getMessage());
                        mView.setProgress(-1);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void goBack() {
        Schedulers.newThread().createWorker().schedule(new Runnable() {
            @Override
            public void run() {
                mView.backStack();
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getPathFromUri(Uri uri) {
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            return getPathFromContentResolver(SampleApp.context, uri);
        }
        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            return uri.getPath();
        }
        return null;
    }

    private static String getPathFromContentResolver(Context context, Uri uri) {
        String path = "";
        // file://
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
            return path;
        }
        // 4.4及之後 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    if (!Strings.isNullOrEmpty(docId)) {
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        if ("primary".equalsIgnoreCase(type)) {
                            return Environment.getExternalStorageDirectory() + "/" + split[1];
                        }
                    }
                } else if (isDownloadsDocument(uri)) {
                    /*
                     * DownloadsProvider
                     * 8.0以後系統getDocumentId會直接帶回路徑
                     * ex : raw:/storage/emulated/0/Download/10_My Juiker Account.zip
                     * 直接把"raw:"取代就是路徑
                     */
                    final String id = DocumentsContract.getDocumentId(uri);
                    if (!Strings.isNullOrEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:", "");
                        }
                        String[] contentUriPrefixesToTry = new String[]{
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads", // Samsung
                                "content://downloads/all_downloads"
                        };
                        try {
                            for (String contentUriPrefix : contentUriPrefixesToTry) {
                                Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));
                                path = getDataColumn(context, contentUri, null, null);
                                if (!Strings.isNullOrEmpty(path)) {
                                    return path;
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "getPathFromContentResolver e : " + e.toString());
                        }
                    }
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    if (!Strings.isNullOrEmpty(docId)) {
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        Uri contentUri = null;
                        if ("image".equals(type)) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[]{split[1]};
                        return getDataColumn(context, contentUri, selection, selectionArgs);
                    }
                }
            } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
                // 圖片 Uri
                return getDataColumn(context, uri, null, null);
            } else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                if (!Strings.isNullOrEmpty(uri.getPath())) {
                    return uri.getPath();
                }
            }
        }
        return "";
    }
}
