package com.yuri.dreamlinkcost;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.yuri.dreamlinkcost.Bmob.Version;
import com.yuri.dreamlinkcost.adapter.LeftMenuAdapter;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.notification.MMNotificationManager;
import com.yuri.dreamlinkcost.notification.NotificationBuilder;
import com.yuri.dreamlinkcost.notification.NotificationReceiver;
import com.yuri.dreamlinkcost.notification.pendingintent.ClickPendingIntentBroadCast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


public class LeftMenuFragment extends Fragment implements LeftMenuAdapter.OnItemClickListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;
    private View mCheckNewVersionView;
    private TextView mNewVersionView;

    private SharedPreferences mSharedPrefences;

    private OnFragmentInteractionListener mListener;

    private static final int MSG_UPDATE_VERSION_VIEW = 0;
    private static final int MSG_NO_VERSION_UPDATE = 1;
    private static final int MSG_SHOW_UPDATE_NOTIFICATION = 2;
    private Handler mUIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_NO_VERSION_UPDATE:
                    Toast.makeText(getActivity(), "已经是最新版本了", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_UPDATE_VERSION_VIEW:
                    mNewVersionView.setVisibility(View.VISIBLE);
                    break;
                case MSG_SHOW_UPDATE_NOTIFICATION:
                    String version = msg.getData().getString("version");
                    String url = msg.getData().getString("url");
                    showUpdateNotification(version, url);
                    break;
            }
        }
    };

    public static LeftMenuFragment newInstance(String param1, String param2) {
        LeftMenuFragment fragment = new LeftMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LeftMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mSharedPrefences = getActivity().getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_left_menu, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.left_menu_recycler_view);
        mCheckNewVersionView = rootView.findViewById(R.id.ll_check_new_version);
        mCheckNewVersionView.setOnClickListener(this);
        mNewVersionView = (TextView) rootView.findViewById(R.id.tv_version_new);

        int mAuthor = mSharedPrefences.getInt(Constant.Extra.KEY_LOGIN, Constant.Author.YURI);
        if (mAuthor == Constant.Author.YURI) {
            rootView.findViewById(R.id.upload_apk).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.upload_apk).setOnClickListener(this);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("LeftMenuFragment.onActivityCreated");
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        LeftMenuAdapter adapter = new LeftMenuAdapter();
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        //checkUpdate
        checkUpdate(false);
    }

    private void checkUpdate(final boolean byuser) {
        BmobQuery<Version> bmobQuery = new BmobQuery();
        bmobQuery.findObjects(getActivity(), new FindListener<Version>() {
            @Override
            public void onSuccess(List<Version> list) {
                if (list != null && list.size() > 0) {
                    String serverVersion = list.get(0).version;
                    Log.d("getActivity():" + getActivity());
                    String currentVersion = Utils.getAppVersion(getActivity());
                    Log.d("serverVersion:" + serverVersion + ",currentVersion:" + currentVersion);
                    if (!TextUtils.isEmpty(currentVersion) && !currentVersion.equals(serverVersion)) {
                        Log.d("Need to update");
                        mUIHandler.sendMessage(mUIHandler.obtainMessage(MSG_UPDATE_VERSION_VIEW));
                        String url = list.get(0).apkUrl;
                        //has new version
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("version", serverVersion);
                        bundle.putString("url", url);
                        message.setData(bundle);
                        message.what = MSG_SHOW_UPDATE_NOTIFICATION;
                        mUIHandler.sendMessage(message);
                    } else {
                        if (byuser) {
                            mUIHandler.sendMessage(mUIHandler.obtainMessage(MSG_NO_VERSION_UPDATE));
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void showUpdateNotification(String serverVersion,  String url) {
        NotificationBuilder builder = MMNotificationManager.getInstance(getActivity().getApplicationContext()).load();
        builder.setNotificationId(Constant.NotificationID.VERSION_UPDAET);
        builder.setContentTitle("有新版本了:" + serverVersion);
        ClickPendingIntentBroadCast cancelBroadcast = new ClickPendingIntentBroadCast(
                NotificationReceiver.ACTION_NOTIFICATION_CANCEL);
        Bundle bundle = new Bundle();
        bundle.putInt("id", Constant.NotificationID.VERSION_UPDAET);
        cancelBroadcast.setBundle(bundle);

        ClickPendingIntentBroadCast downloadBroadcast = new ClickPendingIntentBroadCast(NotificationReceiver.ACTION_NOTIFICATION_VERSION_UPDATE);
        Bundle bundle1 = new Bundle();
        bundle1.putString("versionUrl", url);
        downloadBroadcast.setBundle(bundle1);
        builder.setProfit(NotificationCompat.PRIORITY_MAX);
        builder.addAction(R.mipmap.ic_cancel, "稍后查看", cancelBroadcast);
        builder.addAction(R.mipmap.ic_download, "立即下载", downloadBroadcast);

        builder.setFullScreenIntent(PendingIntent.getBroadcast(getActivity(), 0,
                new Intent(NotificationReceiver.ACTION_NOTIFICATION_CLICK_INTENT), PendingIntent.FLAG_UPDATE_CURRENT), true);

        builder.getSimpleNotification().build(true);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            Log.d(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(), "Not Finished.", Toast.LENGTH_SHORT).show();
        switch (position) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_check_new_version:
                checkUpdate(true);
                break;
            case R.id.upload_apk:
                String filePath = "/sdcard/DreamLinkCost_3.0.apk";
                final ProgressDialog progressDialog  = new ProgressDialog(getActivity());
                progressDialog.setMessage("上传文件中...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setProgress(0);
                progressDialog.setMax(100);
                progressDialog.show();
                BTPFileResponse response = BmobProFile.getInstance(getActivity()).upload(filePath, new UploadListener() {
                    @Override
                    public void onSuccess(String s, String s1) {
                        Log.d("success.name:" + s + ",url:" + s1);
                        progressDialog.setProgress(100);
                        progressDialog.setMessage("上传完成");
                        progressDialog.cancel();
                    }

                    @Override
                    public void onProgress(int i) {
                        Log.d("progress：" + i);
                        progressDialog.setProgress(i);
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.d("error:" + s);
                        progressDialog.setMessage("上传失败:" + s);
                        progressDialog.cancel();
                    }
                });
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
