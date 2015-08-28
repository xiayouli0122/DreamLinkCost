package com.yuri.dreamlinkcost;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.yuri.dreamlinkcost.Bmob.BmobCost;
import com.yuri.dreamlinkcost.adapter.CardViewAdapter;
import com.yuri.dreamlinkcost.interfaces.RecyclerViewClickListener;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.model.Cost;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


@EFragment(R.layout.fragment_main)
public class MainFragment extends Fragment implements RecyclerViewClickListener {
    @ViewById(R.id.my_recycler_view)
    ContextMenuRecyclerView mRecyclerView;

    @ViewById(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;

    @ViewById(R.id.emptyView)
    protected TextView mEmptyView;

    private LinearLayoutManager mLayoutManager;

    private CardViewAdapter mAdapter;

    private ProgressDialog mProgressDialog;


    private OnMainFragmentListener mListener;

    private DecimalFormat mDecimalFormat = new DecimalFormat(".00");

    public MainFragment() {
        // Required empty public constructor
    }

    private static final int MSG_GET_LOCAL_DATA = 0;
    private static final int MSG_GET_NET_DATA = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("同步中...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return null;
    }

    @AfterViews
    public void init() {
        Log.d();
        // Handle Toolbar
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
        mAdapter = new CardViewAdapter(new ArrayList<Cost>(), new ArrayList<BmobCost>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_accent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doGetDataFromNet();
            }
        });

        mProgressDialog.show();
        doGetDataFromNet();
        mProgressBar.setVisibility(View.GONE);

        registerForContextMenu(mRecyclerView);
    }

    private void doGetDataFromNet() {
        Log.d();
        mAdapter.clearList();
        BmobQuery<BmobCost> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("clear", false);
        bmobQuery.order("-createDate");//按日期倒序排序
        bmobQuery.findObjects(getActivity().getApplicationContext(), new FindListener<BmobCost>() {
            @Override
            public void onSuccess(List<BmobCost> list) {
                Log.d("serverSize=" + list.size());
                List<Cost> localList = new Select().from(Cost.class).where("clear=?", 0).orderBy("id desc").execute();
                Log.d("localSize=" + localList.size());
                if (mProgressDialog != null) {
                    mProgressDialog.cancel();
                }

                mSwipeRefreshLayout.setRefreshing(false);

                if (list.size() + localList.size() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
//                    mAdapter.addLocalCostList(localList);
//                    mAdapter.addCostList(list);
                    mAdapter.setCostList(localList, list);
                    //这个方法有Bug
                    //java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder{2f92781d position=16 id=-1, oldPos=0, pLpos:0 scrap tmpDetached not recyclable(1) no parent}
//                    mAdapter.notifyItemRangeInserted(0, mAdapter.getItemCount() - 1);
                    mAdapter.notifyDataSetChanged();

                //统计一下
                    float liuchengPay = 0;
                    float xiaofeiPay = 0;
                    float yuriPay = 0;
                    for (BmobCost cos : list) {
                        liuchengPay += cos.payLC;
                        xiaofeiPay += cos.payXF;
                        yuriPay += cos.payYuri;
                    }

                    for (Cost cost : localList) {
                        liuchengPay += cost.payLC;
                        xiaofeiPay += cost.payXF;
                        yuriPay += cost.payYuri;
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("L:" + mDecimalFormat.format(liuchengPay) + ",");
                    sb.append("X:" + mDecimalFormat.format(xiaofeiPay) + ",");
                    sb.append("Y:" + mDecimalFormat.format(yuriPay));
                    if (mListener != null) {
                        mListener.onUpdateMoney(sb.toString());
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d("onError.errorCode:" + i + ",errorMsg:" + s);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(s + "\n" + "请下拉重试");
                mSwipeRefreshLayout.setRefreshing(false);
                if (mProgressDialog != null) {
                    mProgressDialog.cancel();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //提交本地数据到服务器
        List<Cost> costs = new Select().from(Cost.class).where("status=?", Constant.STATUS_COMMIT_FAILURE).execute();
        BmobCost bmobCost;
        for (final Cost cost : costs) {
            bmobCost = cost.getCostBean();
            bmobCost.save(getActivity().getApplicationContext(), new SaveListener() {
                @Override
                public void onSuccess() {
                    Log.d("Yuri", "upload success:" + cost.title);
                    //上传成功后，删除本地数据
                    cost.delete();
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_GET_LOCAL_DATA));
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.d("Yuri", "upload failure:" + cost.title);
                }
            });
        }
    }

    public void refresh() {
        Log.d();
        doGetDataFromNet();
    }


    public void checkItem(Object object) {
        if (object instanceof BmobCost) {
            BmobCost cost = (BmobCost) object;
            String title = cost.title;
            String status = "Commited";
            String author;
            if (cost.author == Constant.Author.LIUCHENG) {
                author = "LiuCheng";
            } else if (cost.author == Constant.Author.XIAOFEI) {
                author = "XiaoFei";
            } else if (cost.author == Constant.Author.YURI) {
                author = "Yuri";
            } else {
                author = "UNKNOWN";
            }
            String message = "TotalPay(¥):" + cost.totalPay + "\n"
                    + "LiuCheng(¥):" + cost.payLC + "\n"
                    + "XiaoFei(¥):" + cost.payXF + "\n"
                    + "Yuri(¥):" + cost.payYuri + "\n\n"
                    + "Status:" + status + "\n"
                    + "Date:" + Utils.getDate(cost.createDate) + "\n"
                    + "Author:" + author;
            showDialog(title, message);
            return;
        }

        Cost cost = (Cost) object;
        String title = cost.title;
        String status = "unCommited";
        String author;
        if (cost.author == Constant.Author.LIUCHENG) {
            author = "LiuCheng";
        } else if (cost.author == Constant.Author.XIAOFEI) {
            author = "XiaoFei";
        } else if (cost.author == Constant.Author.YURI) {
            author = "Yuri";
        } else {
            author = "UNKNOWN";
        }
        String message = "TotalPay(¥):" + cost.totalPay + "\n"
                + "LiuCheng(¥):" + cost.payLC + "\n"
                + "XiaoFei(¥):" + cost.payXF + "\n"
                + "Yuri(¥):" + cost.payYuri + "\n\n"
                + "Status:" + status + "\n"
                + "Date:" + Utils.getDate(cost.createDate) + "\n"
                + "Author:" + author;
        showDialog(title, message);

    }

    public void doCommit(long id) {
        final Cost cost = Cost.load(Cost.class, id);
        final BmobCost bmobCost = cost.getCostBean();
        bmobCost.save(getActivity().getApplicationContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                Log.d("upload success:" + cost.title);
                Toast.makeText(getActivity().getApplicationContext(), "upload success", Toast.LENGTH_SHORT).show();
                cost.status = Constant.STATUS_COMMIT_SUCCESS;
                cost.objectId = bmobCost.getObjectId();
                cost.save();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getActivity().getApplicationContext(), "upload failure.errorCode:" + i
                        + ",msg:" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultCOde:" + resultCode);
        if (Activity.RESULT_OK == resultCode) {
            doGetDataFromNet();
        }
    }

    public List<BmobCost> getCostList() {
        if (mAdapter != null) {
            return mAdapter.getCostList();
        }
        return null;
    }

    public List<Cost> getLocalList() {
        if (mAdapter != null) {
            return mAdapter.getLocalList();
        }
        return null;
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create().show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMainFragmentListener) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler.hasMessages(MSG_GET_LOCAL_DATA)) {
            mHandler.removeMessages(MSG_GET_LOCAL_DATA);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Object object = mAdapter.getItem(position);
        checkItem(object);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ContextMenuRecyclerView.RecyclerContextMenuInfo adapterContextMenuInfo = (ContextMenuRecyclerView.RecyclerContextMenuInfo) menuInfo;
        Log.d("adapterContextMenuInfo:" + adapterContextMenuInfo);
        //获取弹出菜单时，用户选择的ListView的位置
        int position = adapterContextMenuInfo.position;
        Object object = mAdapter.getItem(position);
        if (object instanceof Cost) {
            getActivity().getMenuInflater().inflate(R.menu.menu_main_context2, menu);
        } else {
            getActivity().getMenuInflater().inflate(R.menu.menu_main_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerContextMenuInfo menuInfo = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        final int position = menuInfo.position;
        Object object = mAdapter.getItem(position);
        if (object instanceof BmobCost) {
            BmobCost bmobCost = (BmobCost) object;
            switch (item.getItemId()) {
                case R.id.action_delete_all:
                    if (mProgressDialog != null) {
                        mProgressDialog.setMessage("删除中...");
                        mProgressDialog.show();
                    }
                    bmobCost.delete(getActivity(), new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            mAdapter.remove(position);
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                            if (mProgressDialog != null) {
                                mProgressDialog.cancel();
                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                            if (mProgressDialog != null) {
                                mProgressDialog.cancel();
                            }
                        }
                    });
                    break;
            }
        } else {
            final Cost cost = (Cost) object;
            switch (item.getItemId()) {
                case R.id.action_delete_local:
                    new AlertDialog.Builder(getActivity())
                            .setTitle(cost.title)
                            .setMessage("将从本地数据移除")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    cost.delete();
                                    mAdapter.remove(position);
                                }
                            })
                            .create().show();
                    break;
                case R.id.action_commit:
                    doCommit(cost.getId());
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }

    public interface OnMainFragmentListener {
        void onUpdateMoney(String detail);
    }

}
