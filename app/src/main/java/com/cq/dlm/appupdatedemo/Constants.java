package com.cq.dlm.appupdatedemo;

import android.os.Environment;

/**
 * 作者：uiho_mac
 * 时间：2018/8/7
 * 描述：
 * 版本：1.0
 * 修订历史：
 */
public class Constants {
    public final static String APP_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + MainApplication.getInstance().getPackageName();
    public final static String DOWNLOAD_DIR = "/downlaod/";
}
