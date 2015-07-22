package com.yuri.dreamlinkcost;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.yuri.dreamlinkcost.Bmob.BmobCost;
import com.yuri.dreamlinkcost.Bmob.BmobTitle;
import com.yuri.dreamlinkcost.adapter.CardViewAdapter;
import com.yuri.dreamlinkcost.model.Cost;
import com.yuri.dreamlinkcost.model.Title;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.my_recycler_view)
    RecyclerView mRecyclerView;

    @ViewById(R.id.fab_button)
    TextView mFabButton;

    @ViewById(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;

    @ViewById(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @ViewById(R.id.emptyView)
    protected TextView mEmptyView;

    private LinearLayoutManager mLayoutManager;

    private CardViewAdapter mAdapter;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, Constant.BMOB_APP_ID);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, Constant.BMOB_APP_ID);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("同步中...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        doGetTitleFromNet();
        List<Title> titles = new Select().from(Title.class).execute();
        if (titles == null || titles.size() == 0) {
            String[] titleArrays = getResources().getStringArray(R.array.title_arrays);
            Title title;
            for (int i = 0; i < titleArrays.length; i++) {
                title = new Title();
                title.mTitle = titleArrays[i];
                title.save();
            }
        }
    }

    @AfterViews
    public void init() {
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
        List<Cost> costList = new Select().from(Cost.class).where("clear=?", false)
                .orderBy("id desc").execute();
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter = new CardViewAdapter(costList, this);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_accent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doGetDataFromNet();
            }
        });

        if (costList == null || costList.size() == 0) {
            mProgressDialog.show();
            doGetDataFromNet();
        }

        mProgressBar.setVisibility(View.GONE);

        mDrawerToggle  = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        FragmentManager fm = getFragmentManager();
        LeftMenuFragment leftMenuFragment = LeftMenuFragment.newInstance("", "");
        fm.beginTransaction().replace(R.id.left_menu_container, leftMenuFragment).commit();
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

    private void doGetTitleFromNet() {
        BmobQuery<BmobTitle> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(getApplicationContext(), new FindListener<BmobTitle>() {
            @Override
            public void onSuccess(List<BmobTitle> list) {
                String objectId;
                for (BmobTitle bmobTitle : list) {
                    objectId = bmobTitle.getObjectId();
                    Title title = new Select().from(Title.class).where("objectId=?", objectId).executeSingle();
                    if (title != null) {
                        //
                    } else {
                        title = bmobTitle.getTitle();
                        title.save();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void doGetDataFromNet() {
        BmobQuery<BmobCost> bmobQuery = new BmobQuery<>("cost");
        bmobQuery.findObjects(getApplicationContext(), new FindListener<BmobCost>() {
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

    @Click(R.id.fab_button)
    void doAddNew() {
        Intent intent = new Intent();
        intent.setClass(this, AddNewActivity_.class);
        startActivityForResult(intent, 11);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Cost> costs = new Select().from(Cost.class).where("status=?", Constant.STATUS_COMMIT_FAILURE).execute();
        BmobCost bmobCost;
        for ( final Cost cost: costs) {
            bmobCost = cost.getCostBean();
            bmobCost.save(getApplicationContext(), new SaveListener() {
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
        bmobCost.save(getApplicationContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                Log.d("Yuri", "upload success:" + cost.title);
                Toast.makeText(getApplicationContext(), "upload success", Toast.LENGTH_SHORT).show();
                cost.status = Constant.STATUS_COMMIT_SUCCESS;
                cost.objectId = bmobCost.getObjectId();
                cost.save();
                Log.d("Yuri", cost.toString());
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getApplicationContext(), "upload failure.errorCode:" + i
                        + ",msg:" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            List<Cost> costList = new Select().from(Cost.class).execute();
            mAdapter.setmCostList(costList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        List<Cost> list = mAdapter.getmCostList();
        int id = item.getItemId();
        switch (id) {
            case R.id.action_statistics:
                float totalPay = 0;
                float liuchengPay = 0;
                float xiaofeiPay = 0;
                float yuriPay = 0;
                for (Cost cos : list) {
                    totalPay += cos.totalPay;
                    liuchengPay += cos.payLC;
                    xiaofeiPay += cos.payXF;
                    yuriPay += cos.payYuri;
                }

                String startTime = Utils.getDate(list.get(list.size() - 1).createDate);
                String endTime = Utils.getDate(list.get(0).createDate);

                StringBuilder sb = new StringBuilder();
                sb.append("StartDate:" + startTime + "\n");
                sb.append("EndDate:" + endTime + "\n\n");
                sb.append("TotalPay(¥):" + totalPay + "\n");
                sb.append("LiuCheng(¥):"+ liuchengPay + "\n");
                sb.append("XiaoFei(¥):" + xiaofeiPay + "\n");
                sb.append("Yuri(¥):" + yuriPay);
                showDialog("Statistics", sb.toString());
                break;
            case R.id.action_about:
                String title = "About";
                String message = "Version:" + Utils.getAppVersion(this);
                showDialog(title, message);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create().show();
    }

}
