package com.gome.download;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arialyy.annotations.Download;
import com.arialyy.annotations.DownloadGroup;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.task.DownloadGroupTask;
import com.arialyy.aria.core.task.DownloadTask;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import pl.droidsonroids.gif.GifImageView;

/**
 * @author lzl
 * @ describe
 * @ time 2020/11/11 16:03
 */
public class DownloadDialog extends DialogFragment {
    private GifImageView mCopyProcessIv, mVideoLoadProcessIv, mMiniProgramLoadProcessIv, mMaterialPicLoadProcessIv;
    private static final String TAG = "TAG";
    private Activity mactivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Aria.download(this).register(); // 正确的写法
        mactivity = (Activity) context;//mCtx 是成员变量，上下文引用
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.download_dialog, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*
        此方法在视图View已经创建后返回的，但是这个view 还没有添加到父级中。
        我们在这里可以重新设定view的各个数据，但是不能修改对话框最外层的ViewGroup的布局参数。
        因为这里的view还没添加到父级中，我们需要在下面onStart生命周期里修改对话框尺寸参数
         */
        mCopyProcessIv = view.findViewById(R.id.download_copy_process);
        mVideoLoadProcessIv = view.findViewById(R.id.download_video_process);
        mMiniProgramLoadProcessIv = view.findViewById(R.id.download_mini_program_process);
        mMaterialPicLoadProcessIv = view.findViewById(R.id.download_material_pic_process);


    }

    @Override
    public void onStart() {
        /*
            因为View在添加后,对话框最外层的ViewGroup并不知道我们导入的View所需要的的宽度。 所以我们需要在onStart生命周期里修改对话框尺寸参数
         */

        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        }
        dialog.setOnDismissListener(this);
        super.onStart();
    }

    /**
     * @ describe 设置复制文案成功
     * @author lzl
     * @ time 2020/11/12 13:50
     * @ param
     * @ return
     */
    public void setCopyTextSuccess() {
        mCopyProcessIv.setImageResource(R.drawable.download_success);
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
    }

    /**
     * @ describe 设置小程序码下载成功
     * @author lzl
     * @ time 2020/11/12 13:58
     * @ param
     * @ return
     */
    public void setMiniProgramLoadSuccess() {
        mMiniProgramLoadProcessIv.setImageResource(R.drawable.download_success);
    }

    /**
     * @ describe  设置小程序码下载失败
     * @author lzl
     * @ time 2020/11/12 13:58
     * @ param
     * @ return
     */
    public void setMiniProgramLoadFail() {
        mMiniProgramLoadProcessIv.setImageResource(R.drawable.download_error);
    }

    public void setMaterialPicLoadSuccess() {
        mMaterialPicLoadProcessIv.setImageResource(R.drawable.download_success);
    }

    public void setMaterialPicLoadFail() {
        mMaterialPicLoadProcessIv.setImageResource(R.drawable.download_error);
    }

    private long mVideoTaskId;
   /**
    * @ describe 视频下载
    *
    * @author lzl
    *
    * @ time 2020/11/13 11:48
    *
    * @ param
    *
    * @ return
    */
    public void loadVideo(final ShareResponseBean.VideoDownLoadBean videoDownLoadBean, final Activity activity) {
        PermissionsManagerUtils.getInstance().checkPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsManagerUtils.IPermissionsResult() {
            @Override
            public void passPermissions() {
                String fileName = SDCardManagerUtils.getSDCardCacheDir(activity) + "/demos/file/video/test.mp4";
                String folderName = SDCardManagerUtils.getSDCardCacheDir(activity) + "/demos/file/video";
                FileManagerUtils.createDir(folderName);
                mVideoTaskId = Aria.download(activity)
                        .load(videoDownLoadBean.getVideoUrl())     //读取下载地址
                        .setFilePath(fileName) //设置文件保存的完整路径
                        .create();   //创建并启动下载
            }

            @Override
            public void forbidPermissions() {

            }
        });
    }
    private  List<String> mPicUrlList;
    private long mPicTaskId;
    /**
     * @ describe 图片素材下载
     *
     * @author lzl
     *
     * @ time 2020/11/13 11:48
     *
     * @ param
     *
     * @ return
     */
    public void loadMaterialPic(final ShareResponseBean.ImageDownloadBean imageDownloadBean, final Activity activity) {

        PermissionsManagerUtils.getInstance().checkPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsManagerUtils.IPermissionsResult() {
            @Override
            public void passPermissions() {
                mPicUrlList = new ArrayList<>(); // 创建一个http url集合
                for(int i=0;i<imageDownloadBean.getImageList().size();i++){

                    mPicUrlList.add(imageDownloadBean.getImageList().get(i).getImageUrl());  // 添加一个视频地址

                }

                String folderName = SDCardManagerUtils.getSDCardCacheDir(activity) + "/demos/file/multi";
                FileManagerUtils.createDir(folderName);
                long taskId = Aria.download(activity)
                        .loadGroup(mPicUrlList) // 设置url集合
                        .setDirPath(folderName)   // 设置该组合任务的文件夹路径
                        .unknownSize().ignoreFilePathOccupy()            // 如果你不知道组合任务的长度请设置这个，需要注意的是，恢复任务时也有加上这个
                        .create();
            }

            @Override
            public void forbidPermissions() {

            }
        });
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
        Log.e(TAG, "Percent===========" + task.getPercent() + "======" + task.getKey());
        if (Url.URL1.equals(task.getKey())) {
            //任务1
//            tv1.setText(String.format("%s%%", task.getPercent()));
        }
        if (Url.URL2.equals(task.getKey())) {
            //任务2
//            tv2.setText(String.format("%s%%", task.getPercent()));
        }
        if (Url.URL3.equals(task.getKey())) {
            //任务3
//            tv3.setText(String.format("%s%%", task.getPercent()));
        }

        int p = task.getPercent();    //任务进度百分比
        String speed = task.getConvertSpeed();    //转换单位后的下载速度，单位转换需要在配置文件中打开
        long speed1 = task.getSpeed(); //原始byte长度速度
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
        if (Url.URL1.equals(task.getKey())) {
        }
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
        Log.e(TAG, "Over===========" + task.getPercent() + "测试======" + task.getKey());

        if (Url.URL2.equals(task.getKey())) {
            //视频下载
            Log.e(TAG, "Over===========" + task.getPercent() + "设置加载视频成功======");
            mVideoLoadProcessIv.setImageResource(R.drawable.download_success);
        }


        if (mPicUrlList.contains(task.getKey())) {
            //素材图片下载
            Log.e(TAG, "Over===========" + task.getPercent() + "设置加载图片成功======");
        }
    }
    /*
     * 任务执行中
     */
    @DownloadGroup.onTaskRunning()
    protected void running(DownloadGroupTask task) {
//        task1.setText("group running, p = "
//                + task.getPercent()
//                + ", speed = "
//                + task.getConvertSpeed()
//                + "current_p = "
//                + task.getCurrentProgress());
        Log.e(TAG, "Percent===========" + task.getPercent() + "图片组下载======" + task.getKey());
    }

    /*
     * 任务完成
     */
    @DownloadGroup.onTaskComplete()
    protected void taskComplete(DownloadGroupTask task) {
        Log.e(TAG, "Percent===========" + task.getPercent() + "图片组下载完成======" + task.getKey());
    }
    @DownloadGroup.onSubTaskRunning
    void onSubTaskRunning(DownloadGroupTask groupTask, DownloadEntity subEntity) {

    }
  /**
   * @ describe 图片数组子任务下载完成回调
   *
   * @author lzl
   *
   * @ time 2020/11/13 15:19
   *
   * @ param
   *
   * @ return
   */
  private int mLoadOverPiceNum;
    @DownloadGroup.onSubTaskComplete
    void onSubTaskComplete(DownloadGroupTask groupTask, DownloadEntity subEntity) {
        // 子任务完成的回调

        if(mPicUrlList.contains(subEntity.getKey())){
            mLoadOverPiceNum++;
            Log.e(TAG,  "子图片组下载完成======"+ mLoadOverPiceNum+"==" + subEntity.getKey());

        }


    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Aria.download(this).unRegister();
    }
}
