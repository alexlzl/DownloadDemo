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
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import pl.droidsonroids.gif.GifImageView;
/**
 * @author lzl
 * @ describe
 * @ time 2020/11/11 16:03
 */
public class DownloadDialog extends DialogFragment  {
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
        mCopyProcessIv.setImageResource(R.drawable.download_success);

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

    }

    /**
     * @ describe 设置复制文案失败
     * @author lzl
     * @ time 2020/11/12 13:50
     * @ param
     * @ return
     */
    public void setCopyTextFail() {

    }

    /**
     * @ describe 设置加载视频成功
     * @author lzl
     * @ time 2020/11/12 13:52
     * @ param
     * @ return
     */
    public void setVideoLoadSuccess() {

    }

    /**
     * @ describe 设置加载视频失败
     * @author lzl
     * @ time 2020/11/12 13:52
     * @ param
     * @ return
     */
    public void setVideoLoadFail() {

    }

    /**
     * @ describe 设置小程序码下载成功
     * @author lzl
     * @ time 2020/11/12 13:58
     * @ param
     * @ return
     */
    public void setMiniProgramLoadSuccess() {

    }

    /**
     * @ describe  设置小程序码下载失败
     * @author lzl
     * @ time 2020/11/12 13:58
     * @ param
     * @ return
     */
    public void setMiniProgramLoadFail() {

    }

    public void setMaterialPicLoadSuccess() {

    }

    public void setMaterialPicLoadFail() {

    }

    private long mVideoTaskId;
    public void loadVideo(ShareReponseBean.VideoDownLoadBean videoDownLoadBean,final Activity activity) {
        PermissionsManagerUtils.getInstance().checkPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsManagerUtils.IPermissionsResult() {
            @Override
            public void passPermissions() {
                String fileName = SDCardManagerUtils.getSDCardCacheDir(activity) + "/demos/file/video/test.mp4";
                String folderName = SDCardManagerUtils.getSDCardCacheDir(activity) + "/demos/file/video";
                FileManagerUtils.createDir(folderName);
                mVideoTaskId = Aria.download(activity)
                        .load(Url.URL2)     //读取下载地址
                        .setFilePath(fileName) //设置文件保存的完整路径
                        .create();   //创建并启动下载
            }

            @Override
            public void forbidPermissions() {

            }
        });
    }

    private long mPicTaskId;
    public void loadMaterialPic(ShareReponseBean.ImageDownloadBean imageDownloadBean, final Activity activity) {
        PermissionsManagerUtils.getInstance().checkPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsManagerUtils.IPermissionsResult() {
            @Override
            public void passPermissions() {
                String fileName = SDCardManagerUtils.getSDCardCacheDir(activity) + "/demos/file/pic/gome.jpg";
                String folderName = SDCardManagerUtils.getSDCardCacheDir(activity) + "/demos/file/pic";
                FileManagerUtils.createDir(folderName);
                mPicTaskId = Aria.download(activity)
                        .load(Url.URL3)     //读取下载地址
                        .setFilePath(fileName) //设置文件保存的完整路径
                        .create();   //创建并启动下载
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
        if (Url.URL1.equals(task.getKey())) {
            //任务1
//            tv1.setText(String.format("%s%%", task.getPercent()));
        }
        if (Url.URL2.equals(task.getKey())) {
            //任务2
//            tv2.setText(String.format("%s%%", task.getPercent()));
            Log.e(TAG, "Over===========" + task.getPercent() + "设置加载视频成功======" );
            mVideoLoadProcessIv.setImageResource(R.drawable.download_success);
        }
        if (Url.URL3.equals(task.getKey())) {
            //任务3
//            tv3.setText(String.format("%s%%", task.getPercent()));
            Log.e(TAG, "Over===========" + task.getPercent() + "设置加载图片成功======" );
            mMaterialPicLoadProcessIv.setImageResource(R.drawable.download_success);
        }


    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Aria.download(this).unRegister();
    }
}
