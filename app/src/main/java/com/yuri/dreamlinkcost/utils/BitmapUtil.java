package com.yuri.dreamlinkcost.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

    /**
     * Drawable convert to bytes[]
     */
    public static synchronized byte[] drawableToByte(Drawable drawable) {
        return drawableToByte(drawable, Bitmap.CompressFormat.PNG);
    }

    /**
     * Drawable convert to bytes[]
     *
     * @param drawable drawable
     * @param format   bitmap format,like png,jpeg and so son </br>
     *                 you can check through Bitmap.CompressFormat class
     * @return drawable bytes[]
     */
    public static byte[] drawableToByte(Drawable drawable, Bitmap.CompressFormat format) {
        if (drawable != null) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;
            // 创建一个字节数组输出流,流的大小为size
            ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            // 将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();
            return imagedata;
        }
        return null;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        return bitmapToByteArray(bitmap, Bitmap.CompressFormat.PNG);
    }

    /**
     * bitmap转byte[]
     *
     * @param bitmap  图片
     * @param format  图片格式
     * @param quality 压缩质量
     * @return
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(format, quality, out);
        return out.toByteArray();
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap,
                                           Bitmap.CompressFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(format, 100, out);
        return out.toByteArray();
    }

    /**
     * 将图片字节流转换成bitmap
     *
     * @param data bytes
     * @return bitmap
     */
    public static Bitmap byteArrayToBitmap(byte[] data) {
        if (data.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }


    /**
     * a drawable convert to bitmap
     *
     * @param drawable drawable
     * @return bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 圆角图片
     *
     * @param bitmap bitmap
     * @return round bitmap
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return round bitmap
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;

            left = 0;
            top = 0;
            right = width;
            bottom = width;

            height = width;

            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;

            float clip = (width - height) / 2;

            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;

            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

        // 以下有两种方法画圆,drawRounRect和drawCircle
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        // canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output;
    }

    /**
     * 计算图片的缩放比例
     *
     * @param options   参数
     * @param reqWidth  目标缩放宽度
     * @param reqHeight 目标缩放高度
     * @return 缩放比例 inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //得到原图的宽搞
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;//初始为1，不缩放
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            //取缩放比例较小的那个，保证不失真的严重
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 计算按指定宽高度缩放后的bitmap的宽高度
     *
     * @param options   参数
     * @param reqWidth  目标缩放宽度
     * @param reqHeight 目标缩放高度
     * @return [“宽度”，“高度”]
     */
    private static int[] calculateSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //得到原图的宽高
        int height = options.outHeight;
        int width = options.outWidth;
        if (height > reqHeight || width > reqWidth) {
            float heightRatio = (float) height / (float) reqHeight;
            float widthRatio = (float) width / (float) reqWidth;
            if (heightRatio > widthRatio) {
                width = reqWidth;
                height = (int) (height / widthRatio);
            } else {
                width = (int) (width / heightRatio);
                height = reqHeight;
            }
        }
        return new int[]{width, height};
    }

    /**
     * 获取按比例压缩的缩略图
     *
     * @param filePath 原图地址
     * @return 缩略图bitmap
     */
    public static Bitmap getThumbnailBitmap(String filePath) {
        //第一步大小压缩
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //给定一个指定的分辨率，计算压缩比率
        options.inSampleSize = calculateInSampleSize(options, 320, 320);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        //第二步 质量压缩
        return getCompressBitmap(bitmap);
    }

    /**
     * 获取按比例压缩的预览图
     *
     * @param filePath 原图地址
     * @return 预览图bitmap
     */
    public static Bitmap getPreviewBitmap(String filePath) {
        //第一步大小压缩
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //给定一个指定的分辨率，计算压缩比率
        options.inSampleSize = calculateInSampleSize(options, 720, 1280);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        //第二步 质量压缩
        return getCompressBitmap(bitmap);
    }

    /**
     * 获取按指定宽高压缩的缩略图
     *
     * @param filePath 原图地址
     * @return 缩略图bitmap
     */
    public static Bitmap getThumbnailBitmap2(String filePath) {
        //第一步大小压缩
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //按512 * 512的来
        //开始计算压缩后的宽高度
        int[] result = calculateSize(options, 512, 512);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, result[0], result[1]);
        //第二步 质量压缩
        return getCompressBitmap(bitmap);
    }

    /**
     * 获取按指定宽高压缩的预览图
     *
     * @param filePath 原图地址
     * @return 预览图bitmap
     */
    public static Bitmap getPreviewBitmap2(String filePath) {
        //第一步大小压缩
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //按720 * 1280的来
        //开始计算压缩后的宽高度
        int[] result = calculateSize(options, 720, 1280);
        //给定一个指定的分辨率，计算压缩比率
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, result[0], result[1]);
        //第二步 质量压缩
        return getCompressBitmap(bitmap);
    }

    /**
     * 质量压缩
     *
     * @param bitmap 需要压缩的bitmap
     * @return 压缩后的字节流
     */
    public static byte[] getCompressBytes(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //设置压缩质量，1-100，100表示不压缩
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        return bos.toByteArray();
    }

    /**
     * 质量压缩
     *
     * @param bitmap 需要压缩的bitmap
     * @return 压缩后的字节流
     */
    public static Bitmap getCompressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //设置压缩质量，1-100，100表示不压缩
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        return byteArrayToBitmap(bos.toByteArray());
    }

    /**
     * 保存bitmap 字节流到sdcard
     *
     * @param desPath 目标图片文件地址
     * @param bytes   bitmap字节流
     * @return false，保存失败
     */
    public static boolean saveBitmap(String desPath, byte[] bytes) {
        Bitmap bitmap = byteArrayToBitmap(bytes);
        return saveBitmap(desPath, bitmap);
    }

    /**
     * 保存bitmap到sdcard
     *
     * @param desPath 目标图片文件地址
     * @param bitmap  bitmap
     * @return false，保存失败
     */
    public static boolean saveBitmap(String desPath, Bitmap bitmap) {
        File file = new File(desPath);
        try {
            boolean ret = file.createNewFile();
            if (!ret) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
