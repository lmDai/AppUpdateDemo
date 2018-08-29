package com.cq.dlm.appupdatedemo;

/**
 * 下载回调
 */

public interface DownloadCallBack {

    void onProgress(int progress);

    void onCompleted();

    void onError(String msg);

}
