package com.gome.download;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.annotations.DownloadGroup;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.task.DownloadGroupTask;
import com.arialyy.aria.core.task.DownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import pl.droidsonroids.gif.GifImageView;

/**
 * @author lzl
 * @ describe 下载弹窗
 * @ time 2020/11/11 16:03
 */
public class DownloadDialog extends DialogFragment implements View.OnClickListener {
    private GifImageView mCopyProcessIv, mVideoLoadProcessIv, mMiniProgramLoadProcessIv, mMaterialPicLoadProcessIv;
    private static final String TAG = "TAG";
    private Activity mActivity;
    private Button mCancelDownloadBtn, mShareBtn;
    private TextView mMaterialPicTv, mCopyTextTv, mVideoLoadTv, mMiniProgramTv, mCancelShareTv;
    private boolean isLoadVideoOver, isMiniProgramLoadOver, isMaterialPicLoadOver, isCopyTextOver;
    private static final String VIDEO_PATH = "/share/video";
    private static final String MATERIAL_PIC_PATH = "/share/material/pictures/";
    public static final String MINI_PROGRAM_PIC_PATH = "/share/miniProgram/pictures/";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Aria.download(this).register();
        mActivity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.download_dialog, container, false);
    }

    /**
     * @ describe  此方法在视图View已经创建后返回的，但是这个view 还没有添加到父级中。
     * 我们在这里可以重新设定view的各个数据，但是不能修改对话框最外层的ViewGroup的布局参数。
     * 因为这里的view还没添加到父级中，我们需要在下面onStart生命周期里修改对话框尺寸参数
     * @author lzl
     * @ time 2020/11/17 14:55
     * @ param
     * @ return
     */
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCopyProcessIv = view.findViewById(R.id.download_copy_process);
        mVideoLoadProcessIv = view.findViewById(R.id.download_video_process);
        mMiniProgramLoadProcessIv = view.findViewById(R.id.download_mini_program_process);
        mMaterialPicLoadProcessIv = view.findViewById(R.id.download_material_pic_process);
        mCancelDownloadBtn = view.findViewById(R.id.download_cancel_bt);
        mCopyTextTv = view.findViewById(R.id.download_copy_text_tv);
        mVideoLoadTv = view.findViewById(R.id.download_load_video_tv);
        mMiniProgramTv = view.findViewById(R.id.download_load_mini_program_tv);
        mMaterialPicTv = view.findViewById(R.id.download_material_pic_tv);
        mShareBtn = view.findViewById(R.id.download_share_button);
        mCancelShareTv = view.findViewById(R.id.download_share_cancel_tv);
        setViewListener();

    }

    private void setViewListener() {
        mCancelDownloadBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
        mCancelShareTv.setOnClickListener(this);
    }


    /**
     * @ describe  因为View在添加后,对话框最外层的ViewGroup并不知道我们导入的View所需要的的宽度。 所以我们需要在onStart生命周期里修改对话框尺寸参数
     * @author lzl
     * @ time 2020/11/17 14:55
     * @ param
     * @ return
     */
    @Override
    public void onStart() {

        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setOnDismissListener(this);
        }

        super.onStart();
    }

    /**
     * @ describe 请求权限
     * @author lzl
     * @ time 2020/11/18 16:47
     * @ param
     * @ return
     */
    public void getPermission(Activity activity) {

        PermissionsManagerUtils.getInstance().checkPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsManagerUtils.IPermissionsResult() {
            @Override
            public void passPermissions() {
                mIPermissionCallBack.success();
            }

            @Override
            public void forbidPermissions() {
                mIPermissionCallBack.fail();
            }
        });
    }

    private IPermissionCallBack mIPermissionCallBack;

    public void setPermissionCallBack(IPermissionCallBack iPermissionCallBack) {
        mIPermissionCallBack = iPermissionCallBack;
    }

    public interface IPermissionCallBack {
        void success();

        void fail();
    }

    private long mVideoTaskId;
    private ShareResponseBean.VideoDownLoadBean mVideoDownLoadBean;
    private String mVideoLoadUrl;
    private String mVideoFilePath;
    private String mVideoFile;
    private String mVideoFileName;
    private File mVideoFileP;

    /**
     * @ describe 视频下载
     * @author lzl
     * @ time 2020/11/13 11:48
     * @ param
     * @ return
     */
    public void loadVideo(final ShareResponseBean.VideoDownLoadBean videoDownLoadBean, final Activity activity) {
        mVideoDownLoadBean = videoDownLoadBean;
        mVideoLoadTv.setText(String.format("%s正在下载", videoDownLoadBean.getDisplayeStr()));
        mVideoLoadUrl = videoDownLoadBean.getVideoUrl();
        String fileName = DateUtil.getFileName(activity);
        mVideoFileName = fileName + videoDownLoadBean.getVideoSuffix();
        File sd1 = activity.getExternalFilesDir("");
        String path1 = sd1.getPath() + "/share";
        File myfile1 = new File(path1);
        if (!myfile1.exists()) {
            myfile1.mkdir();
        }
        mVideoFileP = new File(myfile1, mVideoFileName);

        mVideoTaskId = Aria.download(activity)
                .load(videoDownLoadBean.getVideoUrl())     //读取下载地址
                .setFilePath(path1 + "/" + mVideoFileName) //设置文件保存的完整路径
                .create();   //创建并启动下载
    }

    private List<String> mPicUrlList;
    private long mPicTaskId;
    private ShareResponseBean.ImageDownloadBean mImageDownloadBean;
    private List<String> mMaterialPicFileName;
    private List<File> mMaterialPicFile;

    /**
     * @ describe 图片素材下载
     * @author lzl
     * @ time 2020/11/13 11:48
     * @ param
     * @ return
     */
    public void loadMaterialPic(final ShareResponseBean.ImageDownloadBean imageDownloadBean, final Activity activity) {
        mMaterialPicTv.setText(String.format("%s正在下载", imageDownloadBean.getDisplayeStr()));
        mImageDownloadBean = imageDownloadBean;
        mPicUrlList = new ArrayList<>(); // 创建一个http url集合
        mMaterialPicFileName = new ArrayList<>();
        mMaterialPicFile = new ArrayList<>();
        for (int i = 0; i < imageDownloadBean.getImageList().size(); i++) {

            mPicUrlList.add(imageDownloadBean.getImageList().get(i).getImageUrl());  // 添加一个视频地址
            String fileName = DateUtil.getFileName(activity) + imageDownloadBean.getImageList().get(i).getImageSuffix();
            mMaterialPicFileName.add(fileName);
            File file = new File(SDCardManagerUtils.getSDCardCacheDir(activity) + MATERIAL_PIC_PATH + fileName);
            mMaterialPicFile.add(file);
        }
        String folderName = SDCardManagerUtils.getSDCardCacheDir(activity) + MATERIAL_PIC_PATH;
        FileManagerUtils.createDir(folderName);
        mPicTaskId = Aria.download(activity)
                .loadGroup(mPicUrlList) // 设置url集合
                .setDirPath(folderName).setSubFileName(mMaterialPicFileName)  // 设置该组合任务的文件夹路径
                .unknownSize().ignoreFilePathOccupy()  // 如果你不知道组合任务的长度请设置这个，需要注意的是，恢复任务时也有加上这个
                .create();

    }

    /**
     * @ describe 存储小程序图片
     * @author lzl
     * @ time 2020/11/17 14:44
     * @ param
     * @ return
     */
    public void saveMiniProgramPic(Bitmap bitmap, ShareResponseBean.MiniProgramDownLaodBean miniProgramDownLaodBean, Activity activity) {
        String folderName = SDCardManagerUtils.getSDCardCacheDir(activity) + MINI_PROGRAM_PIC_PATH;
        FileManagerUtils.createDir(folderName);
        String picName = DateUtil.getFileName(activity) + ".jpg";
        File file = new File(SDCardManagerUtils.getSDCardCacheDir(activity) + MINI_PROGRAM_PIC_PATH + picName);
        FileByteManagerUtils.writeBytesToFile(file, BitmapUtil.bitmapToByte(bitmap), true);
        setMiniProgramLoadSuccess(miniProgramDownLaodBean);
        BitmapUtil.saveImageToSystemGallery(activity, file, picName);
    }

    /**
     * @ describe 设置复制文案成功
     * @author lzl
     * @ time 2020/11/12 13:50
     * @ param
     * @ return
     */
    public void setCopyTextSuccess(String content) {
        mCopyProcessIv.setImageResource(R.drawable.download_success);
        mCopyTextTv.setText(content);
        isCopyTextOver = true;
        checkIsAllTaskOver();
    }

    /**
     * @ describe 设置复制文案失败
     * @author lzl
     * @ time 2020/11/12 13:50
     * @ param
     * @ return
     */
    public void setCopyTextFail() {
        mCopyProcessIv.setImageResource(R.drawable.download_error);
        isCopyTextOver = true;
        checkIsAllTaskOver();
    }

    /**
     * @ describe 设置加载视频成功
     * @author lzl
     * @ time 2020/11/12 13:52
     * @ param
     * @ return
     */
    public void setVideoLoadSuccess() {
        mVideoLoadProcessIv.setImageResource(R.drawable.download_success);
        mVideoLoadTv.setText(String.format("%s下载成功", mVideoDownLoadBean.getDisplayeStr()));
        isLoadVideoOver = true;
        checkIsAllTaskOver();
    }

    /**
     * @ describe 设置加载视频失败
     * @author lzl
     * @ time 2020/11/12 13:52
     * @ param
     * @ return
     */
    public void setVideoLoadFail() {
        mVideoLoadProcessIv.setImageResource(R.drawable.download_error);
        isLoadVideoOver = true;
        checkIsAllTaskOver();
    }

    /**
     * @ describe 设置小程序码下载成功
     * @author lzl
     * @ time 2020/11/12 13:58
     * @ param
     * @ return
     */
    public void setMiniProgramLoadSuccess(ShareResponseBean.MiniProgramDownLaodBean miniProgramDownLaodBean) {
        mMiniProgramLoadProcessIv.setImageResource(R.drawable.download_success);
        mMiniProgramTv.setText(String.format("%s下载成功", miniProgramDownLaodBean.getDisplayeStr()));
        isMiniProgramLoadOver = true;
        checkIsAllTaskOver();
    }

    /**
     * @ describe  设置小程序码下载失败
     * @author lzl
     * @ time 2020/11/12 13:58
     * @ param
     * @ return
     */
    public void setMiniProgramLoadFail(ShareResponseBean.MiniProgramDownLaodBean miniProgramDownLaodBean) {
        mMiniProgramLoadProcessIv.setImageResource(R.drawable.download_error);
        mMiniProgramTv.setText(String.format("%s下载失败", miniProgramDownLaodBean.getDisplayeStr()));
        isMiniProgramLoadOver = true;
        checkIsAllTaskOver();
    }

    /**
     * @ describe 素材图片下载成功
     * @author lzl
     * @ time 2020/11/17 14:28
     * @ param
     * @ return
     */
    public void setMaterialPicLoadSuccess() {
        mMaterialPicLoadProcessIv.setImageResource(R.drawable.download_success);
        mMaterialPicTv.setText(String.format("%s下载完成", mImageDownloadBean.getDisplayeStr()));
        checkIsAllTaskOver();
    }

    /**
     * @ describe 素材图片下载失败
     * @author lzl
     * @ time 2020/11/17 14:28
     * @ param
     * @ return
     */
    public void setMaterialPicLoadFail() {
        mMaterialPicLoadProcessIv.setImageResource(R.drawable.download_error);
        checkIsAllTaskOver();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManagerUtils.getInstance().onRequestPermissionsResult(getActivity(), requestCode, permissions, grantResults);
    }

    /**
     * @ describe 预处理的注解，在任务为开始前回调（一般在此处预处理UI界面）
     * @author lzl
     * @ time 2020/11/5 20:26
     * @ param
     * @ return
     */

    @Download.onPre
    protected void downloadPre(DownloadTask task) {
        Log.e(TAG, "Pre===========" + task.getKey());
    }

    /**
     * @ describe 任务开始时的注解，新任务开始时进行回调
     * @author lzl
     * @ time 2020/11/5 20:27
     * @ param
     * @ return
     */
    @Download.onTaskStart
    protected void downloadStart(DownloadTask task) {
        Log.e(TAG, "Start===========" + task.getKey());
    }

    /**
     * @ describe 任务恢复时的注解，任务从停止恢复到运行前进行回调
     * @author lzl
     * @ time 2020/11/5 20:28
     * @ param
     * @ return
     */
    @Download.onTaskResume
    protected void downloadResume(DownloadTask task) {
        Log.e(TAG, "Resume===========" + task.getKey());
    }


    //在这里处理任务执行中的状态，如进度进度条的刷新
    @Download.onTaskRunning
    protected void running(DownloadTask task) {
        Log.e(TAG, "任务执行中===========" + task.getPercent() + "======" + task.getKey());

    }

    /**
     * @ describe 队列已经满了，继续创建新任务，将会回调该方法
     * @author lzl
     * @ time 2020/11/5 20:31
     * @ param
     * @ return
     */
    @Download.onWait
    protected void downloadWait(DownloadTask task) {
        Log.e(TAG, "Wait===========" + task.getKey());
    }

    /**
     * @ describe 任务停止时的注解，任务停止时进行回调
     * @author lzl
     * @ time 2020/11/5 20:34
     * @ param
     * @ return
     */
    @Download.onTaskStop
    protected void downloadStop(DownloadTask task) {
        Log.e(TAG, "Stop===========" + task.getKey());

    }

    /**
     * @ describe 任务被删除时的注解，任务被删除时进行回调
     * @author lzl
     * @ time 2020/11/5 20:36
     * @ param
     * @ return
     */
    @Download.onTaskCancel
    protected void downloadCancel(DownloadTask task) {
        Log.e(TAG, "Cancel===========" + task.getKey());
    }

    /**
     * @ describe 任务失败时的注解，任务执行失败时进行回调
     * @author lzl
     * @ time 2020/11/5 20:36
     * @ param
     * @ return
     */
    @Download.onTaskFail
    protected void downloadTaskFail(DownloadTask task) {
        Log.e(TAG, "Fail===========" + task.getKey());
        if (mVideoLoadUrl != null && mVideoLoadUrl.equals(task.getKey())) {
            //视频下载失败
            Log.e(TAG, "FAIL===========视频任务执行失败");
            setVideoLoadFail();
        }
    }

    /**
     * @ describe 任务完成时的注解，任务完成时进行回调
     * @author lzl
     * @ time 2020/11/5 20:37
     * @ param
     * @ return
     */
    @Download.onTaskComplete
    protected void taskComplete(DownloadTask task) {
        //在这里处理任务完成的状态
        Log.e(TAG, "Over===========" + task.getPercent() + task.getKey());
        if (mVideoLoadUrl != null && mVideoLoadUrl.equals(task.getKey())) {
            //视频下载完成
            Log.e(TAG, "Over===========" + task.getPercent() + "设置加载视频成功======");
            setVideoLoadSuccess();
            BitmapUtil.saveVideoToSystem(mActivity, mVideoFileP);
        }

    }

    /**
     * @ describe 任务组进行中回调
     * @author lzl
     * @ time 2020/11/13 17:17
     * @ param
     * @ return
     */
    @DownloadGroup.onTaskRunning()
    protected void running(DownloadGroupTask task) {

//        Log.e(TAG, "图片组下载======" + task.getPercent() + task.getKey());
    }

    /**
     * @ describe 任务组完成回调
     * @author lzl
     * @ time 2020/11/13 17:17
     * @ param
     * @ return
     */
    @DownloadGroup.onTaskComplete()
    protected void taskComplete(DownloadGroupTask task) {
        Log.e(TAG, "图片组下载完成======" + task.getPercent() + task.getKey());
    }

    /**
     * @ describe 任务组中单个任务进行中回调
     * @author lzl
     * @ time 2020/11/13 17:16
     * @ param
     * @ return
     */
    @DownloadGroup.onSubTaskRunning
    void onSubTaskRunning(DownloadGroupTask groupTask, DownloadEntity subEntity) {

    }

    /**
     * @ describe 组合任务中的子任务失败
     * @author lzl
     * @ time 2020/11/13 18:31
     * @ param
     * @ return
     */
    @DownloadGroup.onSubTaskFail
    void onSubTaskFail(DownloadGroupTask groupTask, DownloadEntity subEntity) {
        if (mPicUrlList != null && subEntity != null && mPicUrlList.contains(subEntity.getKey())) {
            mLoadOverPicNum++;
            Log.e(TAG, "图片子任务失败" + mLoadOverPicNum + "==" + subEntity.getKey());
            if (mPicUrlList.size() == mLoadOverPicNum) {
                /**
                 * 所有图片子任务执行完成
                 */
                setMaterialPicLoadSuccess();
                mLoadOverPicNum = 0;
                isMaterialPicLoadOver = true;
            }
        }
    }

    /**
     * @ describe 图片数组子任务下载完成回调
     * @author lzl
     * @ time 2020/11/13 15:19
     * @ param
     * @ return
     */
    private int mLoadOverPicNum;

    @DownloadGroup.onSubTaskComplete
    void onSubTaskComplete(DownloadGroupTask groupTask, DownloadEntity subEntity) {
        // 子任务完成的回调

        if (mPicUrlList != null && subEntity != null && mPicUrlList.contains(subEntity.getKey())) {
            mLoadOverPicNum++;
            Log.e(TAG, "某个子图片下载完成======" + mLoadOverPicNum + "==" + subEntity.getKey());

        }

        if (mPicUrlList != null && mPicUrlList.size() == mLoadOverPicNum) {
            /**
             * 所有子任务下载图片完成
             */
            Log.e(TAG, "所有图片子任务下载完成======" + mLoadOverPicNum);
            setMaterialPicLoadSuccess();
            mLoadOverPicNum = 0;
            isMaterialPicLoadOver = true;
            for (int i = 0; i < mMaterialPicFile.size(); i++) {
                BitmapUtil.saveImageToSystemGallery(mActivity, mMaterialPicFile.get(i), mMaterialPicFileName.get(i));
            }

        } else {
            mMaterialPicTv.setText(String.format("%s正在下载 (%d/%d)", mImageDownloadBean.getDisplayeStr(), mLoadOverPicNum, mPicUrlList.size()));
        }


    }

    /**
     * @ describe 检测是否所有的任务执行完成，显示分享视图
     * @author lzl
     * @ time 2020/11/13 18:36
     * @ param
     * @ return
     */
    private void checkIsAllTaskOver() {
        Log.e(TAG, "检测是否所有任务完成" + "isLoadVideoOver==" + isLoadVideoOver + "==isMiniProgramLoadOver==" + isMiniProgramLoadOver + "==isMaterialPicLoadOver==" + isMaterialPicLoadOver + "==isCopyTextOver==" + isCopyTextOver);
        if (isLoadVideoOver && isMiniProgramLoadOver && isMaterialPicLoadOver && isCopyTextOver) {
            showShareView();
        }
    }

    /**
     * @ describe 显示分享视图
     * @author lzl
     * @ time 2020/11/13 18:14
     * @ param
     * @ return
     */
    private void showShareView() {
        mShareBtn.setVisibility(View.VISIBLE);
        mCancelShareTv.setVisibility(View.VISIBLE);
        mCancelDownloadBtn.setVisibility(View.GONE);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Aria.download(this).unRegister();
    }

    @Override
    public void onClick(View v) {
        if (R.id.download_cancel_bt == v.getId()) {
            //取消资源下载事件
            if (getDialog() != null) {
                getDialog().dismiss();
                Aria.download(this)
                        .load(mPicTaskId)
                        .cancel();
                Aria.download(this)
                        .load(mVideoTaskId)
                        .cancel();
            }

        }
        if (R.id.download_share_button == v.getId()) {
            //进行分享事件
            Toast.makeText(mActivity, "cancel", Toast.LENGTH_LONG).show();
            /**
             * TODO 分享方法调用
             */
        }
        if (R.id.download_share_cancel_tv == v.getId()) {
            //取消分享事件
            if (getDialog() != null) {
                getDialog().dismiss();
            }

        }
    }
}
