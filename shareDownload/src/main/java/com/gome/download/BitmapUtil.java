package com.gome.download;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * @author lzl
 * @ describe
 * @ time 2020/11/13 17:40
 */
class BitmapUtil {
    public static byte[] bitmapToByte(Bitmap bitmap) {
        //将Bitmap转换成字符串
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
//        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return bStream.toByteArray();
    }
}
