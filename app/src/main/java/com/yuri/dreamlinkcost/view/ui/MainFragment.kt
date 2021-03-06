package com.yuri.dreamlinkcost.view.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toast
import com.yuri.dreamlinkcost.Constant
import com.yuri.dreamlinkcost.ContextMenuRecyclerView
import com.yuri.dreamlinkcost.R
import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri
import com.yuri.dreamlinkcost.interfaces.RecyclerViewClickListener
import com.yuri.dreamlinkcost.model.OnDeleteItemListener
import com.yuri.dreamlinkcost.presenter.MainFragmentPresenter
import com.yuri.dreamlinkcost.rx.RxBus
import com.yuri.dreamlinkcost.rx.RxBusTag
import com.yuri.dreamlinkcost.utils.SharedPreferencesUtil
import com.yuri.dreamlinkcost.utils.TimeUtil
import com.yuri.dreamlinkcost.view.adapter.CardViewAdapter
import com.yuri.dreamlinkcost.view.impl.IMainFragmentView
import com.yuri.xlog.Log
import kotlinx.android.synthetic.main.fragment_main.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.*

class MainFragment : Fragment(), RecyclerViewClickListener, IMainFragmentView {

    private var mLayoutManager: LinearLayoutManager? = null

    var adapter: CardViewAdapter? = null
        private set

    private var mProgressDialog: ProgressDialog? = null

    private var mListener: OnMainFragmentListener? = null

    /**云端列表 */
    private var mNetCostList: List<BmobCostYuri> = ArrayList()

    private var mSortObservable: Observable<Int>? = null

    private var mPresenter: MainFragmentPresenter? = null

    //两种get方式
//    val costList: List<BmobCost>?
//        get() = if (adapter != null) {
//            adapter!!.costList
//        } else null

    fun getCostList(): List<BmobCostYuri>? {
        if (adapter != null) {
            return adapter!!.costList
        }
        return null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutManager = LinearLayoutManager(context)
        mLayoutManager!!.orientation = LinearLayoutManager.VERTICAL

        mProgressDialog = ProgressDialog(context)
        mProgressDialog!!.setMessage("同步中...")
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

        mSortObservable = RxBus.get().register(RxBusTag.TAG_MAIN_FRAGEMNT, Int::class.java)
        mSortObservable!!.observeOn(AndroidSchedulers.mainThread())
                .subscribe { integer ->
                    when (integer) {
                        SORT_BY_DATE_ASC -> adapter!!.sortByDateAsc()
                        SORT_BY_DATE_DESC -> adapter!!.sortByDateDesc()
                        SORT_BY_PRICE_ASC -> adapter!!.sortByPriceAsc()
                        SORT_BY_PRICE_DESC -> adapter!!.sortByPriceDesc()
                    }
                }

        mPresenter = MainFragmentPresenter(activity.applicationContext, this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        Log.d()
        // Handle Toolbar
        my_recycler_view!!.layoutManager = mLayoutManager
        adapter = CardViewAdapter(activity, ArrayList())
        my_recycler_view!!.adapter = adapter
        adapter!!.setOnItemClickListener(this)

        my_recycler_view!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> adapter!!.setOnScrollIdle(true)
                    RecyclerView.SCROLL_STATE_DRAGGING -> adapter!!.setOnScrollIdle(false)
                }
            }
        })

        swipe_container!!.setColorSchemeResources(R.color.refresh_progress_3,
                R.color.refresh_progress_2, R.color.refresh_progress_1)
        swipe_container!!.setOnRefreshListener { doGetDataFromNet() }

        //SwipeRefreshLayout想要实现一进入页面就实现自动刷新一次，并显示刷新动画
        //光靠setRefreshing(true)并不能实现这一目的，你需要使用如下的方法才能将动画显示出来
        //        mSwipeRefreshLayout.setRefreshing(true);
        swipe_container!!.post { swipe_container!!.isRefreshing = true }
        doGetDataFromNet()

        registerForContextMenu(my_recycler_view)
    }

    /**
     * 更新列表UI
     * @param result 从服务器端获取数据结果，true：成功；false：失败
     * @param serverList 服务端列表
     * @param localList 本地列表
     */
    override fun updateList(result: Boolean, serverList: List<BmobCostYuri>?) {
        if (!result) {
            Snackbar.make(my_recycler_view!!, "加载失败，请重试", Snackbar.LENGTH_LONG)
                    .setAction("重试") {
                        swipe_container!!.post { swipe_container!!.isRefreshing = true }
                        doGetDataFromNet()
                    }.show()

            swipe_container!!.isRefreshing = false
            return
        }

        adapter!!.clearList()
        mNetCostList = serverList!!
        swipe_container!!.isRefreshing = false

        if (serverList.isEmpty()) {
            emptyView!!.visibility = View.VISIBLE
            emptyView!!.text = "记录为空"
            adapter!!.notifyDataSetChanged()
        } else {
            emptyView!!.visibility = View.GONE

            showAll()
        }
    }

    override fun updateTitleMoney(result: String) {
        if (mListener != null) {
            mListener!!.onUpdateMoney(result)
        }
    }

    private fun doGetDataFromNet() {
        Log.d()
        mPresenter!!.syncData()
    }

    fun refresh() {
        Log.d()
        doGetDataFromNet()
    }


    fun checkItem(cost: BmobCostYuri) {
        val title = cost.title
        val status = "Commited"
        val author = "Yuri"
        val message = ("TotalPay(¥):" + cost.totalPay + "\n"
                + "Status:" + status + "\n"
                + "Date:" + TimeUtil.getDate(cost.createDate) + "\n"
                + "Author:" + author)
        showDialog(title, message)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("resultCOde:$resultCode")
        if (Activity.RESULT_OK == resultCode) {
            doGetDataFromNet()
        }
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create().show()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as OnMainFragmentListener
        } catch (e: ClassCastException) {
            //do nothing
        }

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.get().unregister(RxBusTag.TAG_MAIN_FRAGEMNT, mSortObservable)
    }

    override fun onItemClick(view: View, position: Int) {
        val `object` = adapter!!.getItem(position)
        checkItem(`object`)
    }

    override fun onItemLongClick(view: View, position: Int) {

    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        //获取弹出菜单时，用户选择的ListView的位置
        activity.menuInflater.inflate(R.menu.menu_main_context, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val menuInfo = item.menuInfo as ContextMenuRecyclerView.RecyclerContextMenuInfo
        val position = menuInfo.position
        val costItem = adapter!!.getItem(position)
        when (item.itemId) {
            R.id.action_delete_all -> {
                if (mProgressDialog != null) {
                    mProgressDialog!!.setMessage("删除中...")
                    mProgressDialog!!.show()
                }

                mPresenter!!.deleteItem(costItem, object : OnDeleteItemListener {
                    override fun onDeleteSucess() {
                        adapter!!.remove(position)
                        adapter!!.notifyDataSetChanged()
                        Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show()
                        if (mProgressDialog != null) {
                            mProgressDialog!!.cancel()
                        }
                        refresh()
                    }

                    override fun onDeleteFail(msg: String) {
                        Toast.makeText(activity, "删除失败", Toast.LENGTH_SHORT).show()
                        if (mProgressDialog != null) {
                            mProgressDialog!!.cancel()
                        }
                    }
                })
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun showError(message: String) {

    }


    interface OnMainFragmentListener {
        fun onUpdateMoney(detail: String)
    }

    fun showAll() {
        //      mAdapter.addLocalCostList(localList);
        //      mAdapter.addCostList(list);
        if (!isAdded) {
            return
        }
        val sort = SharedPreferencesUtil.get(activity, Constant.Extra.KEY_SORT, 0)
//        if (sort == SORT_BY_DATE_DESC) {
//            Collections.sort(mLocalCostList, Cost.DATE_DESC_COMPARATOR)
//            Collections.sort(mNetCostList, BmobCostYuri.DATE_DESC_COMPARATOR)
//        } else if (sort == SORT_BY_PRICE_ASC) {
//            Collections.sort(mLocalCostList, Cost.PRICE_ASC_COMPARATOR)
//            Collections.sort(mNetCostList, BmobCostYuri.PRICE_ASC_COMPARATOR)
//        } else if (sort == SORT_BY_PRICE_DESC) {
//            Collections.sort(mLocalCostList, Cost.PRICE_DESC_COMPARATOR)
//            Collections.sort(mNetCostList, BmobCostYuri.PRICE_DESC_COMPARATOR)
//        }
        adapter!!.costList = mNetCostList
        //这个方法有Bug
        //java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder{2f92781d position=16 id=-1, oldPos=0, pLpos:0 scrap tmpDetached not recyclable(1) no parent}
        //                    mAdapter.notifyItemRangeInserted(0, mAdapter.getItemCount() - 1);
        adapter!!.notifyDataSetChanged()
    }

    fun showAuthor(author: Int) {
        val list = getNetItemList(author)
        adapter!!.setCostList(list)
        adapter!!.notifyDataSetChanged()
    }

    fun getNetItemList(author: Int): List<BmobCostYuri> {
        val itemList = ArrayList<BmobCostYuri>()
        if (mNetCostList.isEmpty()) {
            return itemList
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

        for (cost in mNetCostList) {
            itemList.add(cost)
//            when (author) {
//                Constant.Author.LIUCHENG -> if (cost.payLC > 0) {
//                    itemList.add(cost)
//                }
//                Constant.Author.XIAOFEI -> if (cost.payXF > 0) {
//                    itemList.add(cost)
//                }
//                Constant.Author.YURI -> if (cost.payYuri > 0) {
//                    itemList.add(cost)
//                }
//            }
        }
        return itemList
    }

    companion object {

        val SORT_BY_DATE_ASC = 0
        val SORT_BY_DATE_DESC = 1
        val SORT_BY_PRICE_ASC = 2
        val SORT_BY_PRICE_DESC = 3

        private val MSG_GET_LOCAL_DATA = 0
        private val MSG_GET_NET_DATA = 1
    }

}// Required empty public constructor
