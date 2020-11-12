package com.gome.download;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
}
