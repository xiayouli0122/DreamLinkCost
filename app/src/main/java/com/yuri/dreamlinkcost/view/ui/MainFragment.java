package com.yuri.dreamlinkcost.view.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.ContextMenuRecyclerView;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.bean.Bmob.BmobCost;
import com.yuri.dreamlinkcost.bean.table.Cost;
import com.yuri.dreamlinkcost.interfaces.RecyclerViewClickListener;
import com.yuri.dreamlinkcost.model.CommitResultListener;
import com.yuri.dreamlinkcost.model.OnDeleteItemListener;
import com.yuri.dreamlinkcost.presenter.MainFragmentPresenter;
import com.yuri.dreamlinkcost.rx.RxBus;
import com.yuri.dreamlinkcost.rx.RxBusTag;
import com.yuri.dreamlinkcost.utils.SharedPreferencesUtil;
import com.yuri.dreamlinkcost.utils.TimeUtil;
import com.yuri.dreamlinkcost.view.adapter.CardViewAdapter;
import com.yuri.dreamlinkcost.view.impl.IMainFragmentView;
import com.yuri.xlog.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class MainFragment extends Fragment implements RecyclerViewClickListener, IMainFragmentView {

    @BindView(R.id.my_recycler_view)
    ContextMenuRecyclerView mRecyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.emptyView)
    TextView mEmptyView;

    private LinearLayoutManager mLayoutManager;

    private CardViewAdapter mAdapter;

    private ProgressDialog mProgressDialog;

    private OnMainFragmentListener mListener;

    /**云端列表*/
    private List<BmobCost> mNetCostList = new ArrayList<>();
    /**本地列表*/
    private List<Cost> mLocalCostList = new ArrayList<>();

    private Observable<Integer> mSortObservable;

    public static final int SORT_BY_DATE_ASC = 0;
    public static final int SORT_BY_DATE_DESC = 1;
    public static final int SORT_BY_PRICE_ASC  = 2;
    public static final int SORT_BY_PRICE_DESC  = 3;

    private MainFragmentPresenter mPresenter;

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

        mSortObservable = RxBus.get().register(RxBusTag.TAG_MAIN_FRAGEMNT, Integer.class);
        mSortObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        switch (integer) {
                            case SORT_BY_DATE_ASC:
                                mAdapter.sortByDateAsc();
                                break;
                            case SORT_BY_DATE_DESC:
                                mAdapter.sortByDateDesc();
                                break;
                            case SORT_BY_PRICE_ASC:
                                mAdapter.sortByPriceAsc();
                                break;
                            case SORT_BY_PRICE_DESC:
                                mAdapter.sortByPriceDesc();
                                break;
                        }
                    }
                });

        mPresenter = new MainFragmentPresenter(getActivity().getApplicationContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    public void init() {
        Log.d();
        // Handle Toolbar
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CardViewAdapter(getActivity(), new ArrayList<Cost>(), new ArrayList<BmobCost>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        mAdapter.setOnScrollIdle(true);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        mAdapter.setOnScrollIdle(false);
                        break;
                }
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_3,
                R.color.refresh_progress_2, R.color.refresh_progress_1);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doGetDataFromNet();
            }
        });

        //SwipeRefreshLayout想要实现一进入页面就实现自动刷新一次，并显示刷新动画
        //光靠setRefreshing(true)并不能实现这一目的，你需要使用如下的方法才能将动画显示出来
//        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        doGetDataFromNet();

        registerForContextMenu(mRecyclerView);
    }

    /**
     * 更新列表UI
     * @param result 从服务器端获取数据结果，true：成功；false：失败
     * @param serverList 服务端列表
     * @param localList 本地列表
     */
    @Override
    public void updateList(boolean result, List<BmobCost> serverList, List<Cost> localList) {
        if (!result) {
            Snackbar.make(mRecyclerView, "加载失败，请重试", Snackbar.LENGTH_LONG)
                    .setAction("重试", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    mSwipeRefreshLayout.setRefreshing(true);
                                }
                            });
                            doGetDataFromNet();
                        }
                    }).show();

            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        mAdapter.clearList();
        mNetCostList = serverList;
        mLocalCostList = localList;
        mSwipeRefreshLayout.setRefreshing(false);

        if (serverList.size() + localList.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText("记录为空");
            mAdapter.notifyDataSetChanged();
        } else {
            mEmptyView.setVisibility(View.GONE);

            showAll();
        }
    }

    @Override
    public void updateTitleMoney(String result) {
        if (mListener != null) {
            mListener.onUpdateMoney(result);
        }
    }

    private void doGetDataFromNet() {
        Log.d();
        mPresenter.syncData();
    }

    @Override
    public void onResume() {
        super.onResume();
        //提交本地数据到服务器
        mPresenter.commitLocalData();
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
                    + "Date:" + TimeUtil.getDate(cost.createDate) + "\n"
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
                + "Date:" + TimeUtil.getDate(cost.createDate) + "\n"
                + "Author:" + author;
        showDialog(title, message);

    }

    public void doCommit(long id) {
        mPresenter.commitItem(id, new CommitResultListener() {
            @Override
            public void onCommitSuccess() {
                Toast.makeText(getActivity().getApplicationContext(), "upload success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCommitFail(int errorCode, String msg) {
                Toast.makeText(getActivity().getApplicationContext(), "upload failure.errorCode:" + errorCode
                        + ",msg:" + msg, Toast.LENGTH_SHORT).show();
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
            //do nothing
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

        RxBus.get().unregister(RxBusTag.TAG_MAIN_FRAGEMNT, mSortObservable);
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

                    mPresenter.deleteItem(bmobCost, new OnDeleteItemListener() {
                        @Override
                        public void onDeleteSucess() {
                            mAdapter.remove(position);
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                            if (mProgressDialog != null) {
                                mProgressDialog.cancel();
                            }
                            refresh();
                        }

                        @Override
                        public void onDeleteFail(String msg) {
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

    @Override
    public void showError(String message) {

    }


    public interface OnMainFragmentListener {
        void onUpdateMoney(String detail);
    }

    public void showAll() {
//      mAdapter.addLocalCostList(localList);
//      mAdapter.addCostList(list);
        if (!isAdded()) {
            return;
        }
        int sort = SharedPreferencesUtil.get(getActivity(), Constant.Extra.KEY_SORT, 0);
        if (sort == SORT_BY_DATE_DESC) {
            Collections.sort(mLocalCostList, Cost.DATE_DESC_COMPARATOR);
            Collections.sort(mNetCostList, BmobCost.DATE_DESC_COMPARATOR);
        } else if (sort == SORT_BY_PRICE_ASC) {
            Collections.sort(mLocalCostList, Cost.PRICE_ASC_COMPARATOR);
            Collections.sort(mNetCostList, BmobCost.PRICE_ASC_COMPARATOR);
        } else if (sort == SORT_BY_PRICE_DESC){
            Collections.sort(mLocalCostList, Cost.PRICE_DESC_COMPARATOR);
            Collections.sort(mNetCostList, BmobCost.PRICE_DESC_COMPARATOR);
        }
        mAdapter.setCostList(mLocalCostList, mNetCostList);
        //这个方法有Bug
        //java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder{2f92781d position=16 id=-1, oldPos=0, pLpos:0 scrap tmpDetached not recyclable(1) no parent}
//                    mAdapter.notifyItemRangeInserted(0, mAdapter.getItemCount() - 1);
        mAdapter.notifyDataSetChanged();
    }

    public void showAuthor(int author) {
        List<Cost> localList = getItemLocalList(author);
        List<BmobCost> list = getNetItemList(author);
        mAdapter.setCostList(localList, list);
        mAdapter.notifyDataSetChanged();
    }

    public List<Cost> getItemLocalList(final int author) {
        final List<Cost> itemList = new ArrayList<>();
        if (mLocalCostList.size() == 0) {
            return itemList;
        }

        for (Cost cost: mLocalCostList) {
            switch (author) {
                case Constant.Author.LIUCHENG:
                    if (cost.payLC > 0) {
                        itemList.add(cost);
                    }
                    break;
                case Constant.Author.XIAOFEI:
                    if (cost.payXF > 0) {
                        itemList.add(cost);
                    }
                    break;
                case Constant.Author.YURI:
                    if (cost.payYuri > 0) {
                        itemList.add(cost);
                    }
                    break;
            }
        }
        return  itemList;
    }

    public List<BmobCost> getNetItemList(final int author) {
        final List<BmobCost> itemList = new ArrayList<>();
        if (mNetCostList.size() == 0) {
            return itemList;
        }

        //采用RxJava写的过滤方法
//        Observable.from(mNetCostList).filter(new Func1<BmobCost, Boolean>() {
//            @Override
//            public Boolean call(BmobCost cost) {
//                switch (author) {
//                    case Constant.Author.LIUCHENG:
//                        return  cost.payLC > 0;
//                    case Constant.Author.XIAOFEI:
//                        return  cost.payXF > 0;
//                    case Constant.Author.YURI:
//                        return  cost.payYuri > 0;
//                }
//                return null;
//            }
//        }).subscribe(new Subscriber<BmobCost>() {
//            @Override
//            public void onCompleted() {
//                Log.d();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(BmobCost cost) {
//                Log.d(cost.author + "");
//                itemList.add(cost);
//            }
//        });

        for (BmobCost cost: mNetCostList) {
            switch (author) {
                case Constant.Author.LIUCHENG:
                    if (cost.payLC > 0) {
                        itemList.add(cost);
                    }
                    break;
                case Constant.Author.XIAOFEI:
                    if (cost.payXF > 0) {
                        itemList.add(cost);
                    }
                    break;
                case Constant.Author.YURI:
                    if (cost.payYuri > 0) {
                        itemList.add(cost);
                    }
                    break;
            }
        }
        return  itemList;
    }

    public CardViewAdapter getAdapter() {
        return  mAdapter;
    }

}
