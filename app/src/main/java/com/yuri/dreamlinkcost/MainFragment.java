package com.yuri.dreamlinkcost;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.yuri.dreamlinkcost.Bmob.BmobCost;
import com.yuri.dreamlinkcost.adapter.CardViewAdapter;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.model.Cost;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


@EFragment(R.layout.fragment_main)
public class MainFragment extends Fragment {
    @ViewById(R.id.my_recycler_view)
    RecyclerView mRecyclerView;

    @ViewById(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;

    @ViewById(R.id.emptyView)
    protected TextView mEmptyView;

    private LinearLayoutManager mLayoutManager;

    private CardViewAdapter mAdapter;

    private ProgressDialog mProgressDialog;


    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

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
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new CardViewAdapter(new ArrayList<Cost>(), this);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_accent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doGetDataFromNet();
            }
        });

        List<Cost> costList = new Select().from(Cost.class).where("clear=?", false)
                .orderBy("id desc").execute();
        if (costList == null || costList.size() == 0) {
            mProgressDialog.show();
            doGetDataFromNet();
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);

            mAdapter.addCostList(costList);
        }

        mProgressBar.setVisibility(View.GONE);
    }

    private void doGetDataFromLocal() {
        List<Cost> costList = new Select().from(Cost.class).orderBy("id desc").execute();
        if (costList == null || costList.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText("Empty");
        } else {
            mEmptyView.setVisibility(View.GONE);
            mAdapter.setmCostList(costList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void doGetDataFromNet() {
        BmobQuery<BmobCost> bmobQuery = new BmobQuery<>("cost");
        bmobQuery.findObjects(getActivity().getApplicationContext(), new FindListener<BmobCost>() {
            @Override
            public void onSuccess(List<BmobCost> list) {
                int count = 0;
                String objectId;
                for (BmobCost bmobcost : list) {
                    objectId = bmobcost.getObjectId();
                    Cost cost = new Select().from(Cost.class).where("objectId=?", objectId).executeSingle();
                    if (cost == null) {
                        cost = bmobcost.getCost();
                        cost.save();
                        count++;
                    }
                }
                if (count == 0) {
                    if (mProgressDialog != null) {
                        mProgressDialog.cancel();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                    return;
                }
                doGetDataFromLocal();
                mSwipeRefreshLayout.setRefreshing(false);
                if (mProgressDialog != null) {
                    mProgressDialog.cancel();
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d("Yuri", "onError.errorCode:" + i + ",errorMsg:" + s);
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
        List<Cost> costs = new Select().from(Cost.class).where("status=?", Constant.STATUS_COMMIT_FAILURE).execute();
        BmobCost bmobCost;
        for ( final Cost cost: costs) {
            bmobCost = cost.getCostBean();
            bmobCost.save(getActivity().getApplicationContext(), new SaveListener() {
                @Override
                public void onSuccess() {
                    Log.d("Yuri", "upload success:" + cost.title);
                    cost.status = Constant.STATUS_COMMIT_SUCCESS;
                    cost.save();
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.d("Yuri", "upload failure:" + cost.title);
                }
            });
        }
    }

    public void checkItem(Cost cost) {
        Log.d("Yuri", "checkItem.:" + cost.toString());
        String title = cost.title;
        String status = (cost.status == Constant.STATUS_COMMIT_SUCCESS) ? "Commited" : "unCommited";
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
                + "Status:" +  status + "\n"
                + "Date:"  + Utils.getDate(cost.createDate) + "\n"
                + "Author:" + author;
        showDialog(title, message);

    }

    public void doCommit(long id) {
        final Cost cost = Cost.load(Cost.class, id);
        final BmobCost bmobCost = cost.getCostBean();
        bmobCost.save(getActivity().getApplicationContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                Log.d("Yuri", "upload success:" + cost.title);
                Toast.makeText(getActivity().getApplicationContext(), "upload success", Toast.LENGTH_SHORT).show();
                cost.status = Constant.STATUS_COMMIT_SUCCESS;
                cost.objectId = bmobCost.getObjectId();
                cost.save();
                Log.d("Yuri", cost.toString());
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
        if (Activity.RESULT_OK == resultCode) {
            List<Cost> costList = new Select().from(Cost.class).execute();
            mAdapter.setmCostList(costList);
            mAdapter.notifyDataSetChanged();
        }
    }

    public List<Cost> getCostList() {
        if (mAdapter != null) {
            return mAdapter.getCostList();
        }

        return  null;
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create().show();
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
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
