package com.yuri.dreamlinkcost.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;

/**
 * Custom SpannaleStringBuilder
 * Created by Yuri on 2015/7/31.
 */
public class Spanny extends SpannableStringBuilder {

    private int flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;

    public Spanny() {
        super("");
    }

    public Spanny(CharSequence text) {
        super(text);
    }

    public Spanny(CharSequence text, Object... spans) {
        super(text);
        for (Object span : spans) {
            setTextSpan(span, 0, length());
        }
    }

    public Spanny(CharSequence text, Object span) {
        super(text);
        setTextSpan(span, 0, text.length());
    }

    /**
     * 给一段文件添加一个或多个样式
     * @param text 需要设定样式的文本
     * @param spans 文本的样式，一个或多个，像{@link ForegroundColorSpan},{@link AbsoluteSizeSpan}等等.
     * @return this {@code Spanny}.
     */
    public Spanny append(CharSequence text, Object... spans) {
        append(text);
        for (Object span : spans) {
            setTextSpan(span, length() - text.length(), length());
        }
        return this;
    }

    public Spanny append(CharSequence text, Object span) {
        append(text);
        setTextSpan(span, length() - text.length(), length());
        return this;
    }

    /**
     * 在文本的开始处追加一个图片,实现图文混排
     * @return this {@code Spanny}.
     */
    public Spanny append(CharSequence text, ImageSpan imageSpan) {
        text = "." + text;
        append(text);
        setTextSpan(imageSpan, length() - text.length(), length() - text.length() + 1);
        return this;
    }

    /**
     * Append plain text.
     * @return this {@code Spanny}.
     */
    @Override
    public Spanny append(CharSequence text) {
        super.append(text);
        return this;
    }

    /**
     * 重新设置Flag. 默认是SPAN_EXCLUSIVE_EXCLUSIVE.
     * @param flag see {@link Spanned}.
     */
    public void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * 给指定位置的文本的设定样式
     */
    public Spanny setSpan(Object span, int start, int end) {
        setTextSpan(span, start, end);
        return this;
    }

    /**
     * 给指定位置的文本的设定样式
     */
    private void setTextSpan(Object span, int start, int end) {
        setSpan(span, start, end, flag);
    }

    /**
     * Sets a span object to all appearances of specified text in the spannable.
     * A new instance of a span object must be provided for each iteration
     * because it can't be reused.
     *
     * @param textToSpan Case-sensitive text to span in the current spannable.
     * @param getSpan    Interface to get a span for each spanned string.
     * @return {@code Spanny}.
     */
    public Spanny findAndSpan(CharSequence textToSpan, GetSpan getSpan) {
        int lastIndex = 0;
        while (lastIndex != -1) {
            lastIndex = toString().indexOf(textToSpan.toString(), lastIndex);
            if (lastIndex != -1) {
                setSpan(getSpan.getSpan(), lastIndex, lastIndex + textToSpan.length());
                lastIndex += textToSpan.length();
            }
        }
        return this;
    }

    /**
     * Interface to return a new span object when spanning multiple parts in the text.
     */
    public interface GetSpan {
        /**
         * @return A new span object should be returned.
         */
        Object getSpan();
    }


    /**
     * Sets span objects to the text. This is more efficient than creating a new instance of Spanny
     * or SpannableStringBuilder.
     * @return {@code SpannableString}.
     */
    public static SpannableString spanText(CharSequence text, Object span, int start, int end) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}