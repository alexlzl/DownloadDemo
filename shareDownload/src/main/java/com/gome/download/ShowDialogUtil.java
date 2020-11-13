package com.gome.download;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

import androidx.fragment.app.FragmentManager;

/**
 * @author lzl
 * @ describe
 * @ time 2020/11/12 10:46
 */
public class ShowDialogUtil {
    private DownloadDialog mDownloadDialog;
    private Activity mActivity;

    public ShowDialogUtil(Activity activity) {
        mActivity = activity;
    }

    public void showDialog(FragmentManager fragmentManager) {
        mDownloadDialog = new DownloadDialog();
//        mDownloadDialog.show(fragmentManager, "aa");
        mDownloadDialog.showNow(fragmentManager, "aa");
    }

    /**
     * @ describe 资源下载入口方法
     * @author lzl
     * @ time 2020/11/13 11:34
     * @ param
     * @ return
     */
    public void startLoadResource(ShareResponseBean shareResponseBean) {
        loadCopyText(shareResponseBean.getCopyString());
        loadVideo(shareResponseBean.getVideoDownLoad());
        loadMaterialPic(shareResponseBean.getImageDownload());
        //for test
        Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.test);
        loadMiniProgram(bitmap, shareResponseBean.getMiniProgramDownLaod());
    }

    /**
     * @ describe 文案复制
     * @author lzl
     * @ time 2020/11/12 10:47
     * @ param
     * @ return
     */
    public void loadCopyText(ShareResponseBean.CopyStringBean copyStringBean) {
        ClipboardUtil.copyText(mActivity, copyStringBean.getCopyStr());
        mDownloadDialog.setCopyTextSuccess(copyStringBean.getCopyStr());
    }

    /**
     * @ describe 加载视频
     * @author lzl
     * @ time 2020/11/12 15:30
     * @ param
     * @ return
     */
    public void loadVideo(ShareResponseBean.VideoDownLoadBean videoDownLoadBean) {
        mDownloadDialog.loadVideo(videoDownLoadBean, mActivity);

    }

    /**
     * @ describe 存储小程序图片
     * @author lzl
     * @ time 2020/11/12 15:31
     * @ param
     * @ return
     */
    public void loadMiniProgram(Bitmap bitmap, ShareResponseBean.MiniProgramDownLaodBean miniProgramDownLaodBean) {
        String folderName = SDCardManagerUtils.getSDCardCacheDir(mActivity) + "/demos/file/pic";
                FileManagerUtils.createDir(folderName);
        FileByteManagerUtils.writeBytesToFile(new File(SDCardManagerUtils.getSDCardCacheDir(mActivity) + "/demos/file/pic/test.jpg"), BitmapUtil.bitmapToByte(bitmap), true);
        mDownloadDialog.setMiniProgramLoadSuccess(miniProgramDownLaodBean);
    }

    /**
     * @ describe 加载素材图片
     * @author lzl
     * @ time 2020/11/12 15:32
     * @ param
     * @ return
     */
    public void loadMaterialPic(ShareResponseBean.ImageDownloadBean imageDownloadBean) {
        mDownloadDialog.loadMaterialPic(imageDownloadBean, mActivity);
    }
}
