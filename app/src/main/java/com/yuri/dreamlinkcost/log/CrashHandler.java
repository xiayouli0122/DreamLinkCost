package com.yuri.dreamlinkcost.log;

import android.app.Application;

import com.yuri.dreamlinkcost.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static CrashHandler mInstance = new CrashHandler();
    @SuppressWarnings("unused")
	private Application mContext;
    private UncaughtExceptionHandler mDefaultHandler;

    public static CrashHandler getInstance() {
        return mInstance;
    }

    public void init(Application application) {
        mContext = application;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(thread, ex);

        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    private boolean handleException(Thread thread, Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String log = writer.toString();
        printWriter.close();
        Log.e(TAG, log);

        String time = Utils.getTime();
        LogFile logFile = new LogFile("log_" + time + ".txt");
        logFile.open();
        logFile.writeLog(log);
        logFile.close();
        
        LogcatSaver logcatSaver = new LogcatSaver("logcat_" + time + ".txt");
        logcatSaver.start();
        
        return true;
    }

}
