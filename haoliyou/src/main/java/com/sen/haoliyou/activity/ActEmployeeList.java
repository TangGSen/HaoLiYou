package com.sen.haoliyou.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.sen.haoliyou.R;
import com.sen.haoliyou.adapter.ActEmployeeAdapter;
import com.sen.haoliyou.base.BaseActivity;
import com.sen.haoliyou.mode.ActEmployeeHome;
import com.sen.haoliyou.mode.AssessmentItemBean;
import com.sen.haoliyou.mode.EventSubmitAnswerSucess;
import com.sen.haoliyou.tools.AcountManager;
import com.sen.haoliyou.tools.Constants;
import com.sen.haoliyou.tools.DialogUtils;
import com.sen.haoliyou.tools.NetUtil;
import com.sen.haoliyou.widget.RecyleViewItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Sen on 2016/3/3.
 */
public class ActEmployeeList extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String CHILD_ITEM_BEAN = "child_itembean";
    private static final String CHILDBUNDLE = "childBundle";
    private static final int GETDATA_ERROR = 0;
    private AssessmentItemBean child_itembean;
    @Bind(R.id.employee_back)
    AppCompatTextView mEmployeeBack;
    @Bind(R.id.tab_test_name)
    AppCompatTextView mTabTestName;
    @Bind(R.id.employee_recylerview)
    RecyclerView mEmployeeRecylerview;
    @Bind(R.id.employee_swipe_refresh_widget)
    SwipeRefreshLayout mEmployeeSwipeRefreshWidget;
    private boolean isLoad = false;
    private boolean isReFlesh = false;
    private List<ActEmployeeHome.EmployeeItemBean> examItemBeanList;
    private List<ActEmployeeHome.EmployeeItemBean> allExamItemBeanList;
    private ActEmployeeAdapter elopmyeeAdapter;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //下拉刷新和加载更多的时候就不用diaogle
            if (!isReFlesh)
                DialogUtils.closeDialog();
            switch (msg.what) {
                case 0:

                    Toast.makeText(ActEmployeeList.this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
                    break;

                case 1:
                    ActEmployeeHome homeBean = (ActEmployeeHome) msg.obj;
                    // 当返回的数据为空的时候，那么就要显示这个
                    if (homeBean == null) {
                        Toast.makeText(ActEmployeeList.this, "请求数据失败，刷新一下", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    examItemBeanList = homeBean.getCourseList();
                    if (examItemBeanList == null) {
                        Toast.makeText(ActEmployeeList.this, "没有数据", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if (examItemBeanList.size() == 0) {
                        Toast.makeText(ActEmployeeList.this, "没有数据", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    allExamItemBeanList.clear();
                    allExamItemBeanList.addAll(examItemBeanList);
                    examItemBeanList.clear();
                    showExamData(allExamItemBeanList);


                    break;
            }
            return false;
        }
    });


    private void showExamData(List<ActEmployeeHome.EmployeeItemBean> examItemBeanList) {


        if (elopmyeeAdapter == null) {
            elopmyeeAdapter = new ActEmployeeAdapter(ActEmployeeList.this, examItemBeanList);
            mEmployeeRecylerview.setAdapter(elopmyeeAdapter);
        } else {
            elopmyeeAdapter.notifyDataSetChanged();
        }

        elopmyeeAdapter.setOnItemClickListener(new ActEmployeeAdapter.OnItemClickListener() {


            @Override
            public void onItemClick(View view, int position, ActEmployeeHome.EmployeeItemBean employeeItemBean, int enterType) {
                if (enterType==0){
                    //进入评估
                    ActDoAssess.toThis(ActEmployeeList.this,child_itembean.getDemand_id(),child_itembean.getDemand_name());
                }else{
                    //进入查看

                }
            }
        });


    }


    @Override
    protected void dealAdaptationToPhone() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }
    public static void toThis(Context context, AssessmentItemBean child_itembean) {
        Intent intent = new Intent(context, ActEmployeeList.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(CHILD_ITEM_BEAN,child_itembean);
        intent.putExtra("childBundle",bundle);
        context.startActivity(intent);
    }



    @Override
    protected void init() {
        super.init();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(CHILDBUNDLE);
        child_itembean = (AssessmentItemBean) bundle.getSerializable(CHILD_ITEM_BEAN);


    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.act_employee_layout);
        ButterKnife.bind(this);
        settingRecyleView();

        if (savedInstanceState == null) {
            //去加载数据
            isLoad = true;
        } else {
            isLoad = false;
            Log.e("sen", "老数据");
            allExamItemBeanList = (List<ActEmployeeHome.EmployeeItemBean>) savedInstanceState.getSerializable("EmployeeListData");
            showExamData(allExamItemBeanList);
        }
    }



    private void settingRecyleView() {

        LinearLayoutManager linearnLayoutManager = new LinearLayoutManager(ActEmployeeList.this);
        mEmployeeRecylerview.setLayoutManager(linearnLayoutManager);
//        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mEmployeeRecylerview.setHasFixedSize(true);

        mEmployeeRecylerview.addItemDecoration(new RecyleViewItemDecoration(ActEmployeeList.this, R.drawable.shape_recycle_item_decoration));
        mEmployeeSwipeRefreshWidget.setColorSchemeResources(R.color.theme_color, R.color.theme_color);
        mEmployeeSwipeRefreshWidget.setOnRefreshListener(this);

        //判断RecycleView 上下滑的时候，mEmployeeSwipeRefreshWidget 的开关
        mEmployeeRecylerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mEmployeeSwipeRefreshWidget.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    public void onEvent(EventSubmitAnswerSucess event) {
       /* if (allExamItemBeanList != null) {
            allExamItemBeanList.clear();
        }else{
            allExamItemBeanList = new ArrayList<>();
        }*/
        getEmployeeListData();
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        allExamItemBeanList = new ArrayList<>();
        if (isLoad && child_itembean !=null) {
            getEmployeeListData();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("EmployeeListData", (Serializable) allExamItemBeanList);
    }

    private void getEmployeeListData() {
        Log.e("sen", AcountManager.getAcountId());
        if (!NetUtil.isNetworkConnected(ActEmployeeList.this)) {
            Toast.makeText(ActEmployeeList.this, R.string.has_not_net, Toast.LENGTH_SHORT).show();
            return;
        }
        //下拉刷新和加载更多就不用show
        if (!isReFlesh)
            DialogUtils.showDialog(ActEmployeeList.this, "请稍等");
        String url = Constants.PATH + Constants.PATH_GETLOW_EMPLOYEELISTS;
        OkHttpUtils.post()
                .url(url)
                .addParams("user_id", AcountManager.getAcountId())
                .addParams("tc_id", child_itembean.getTc_id())
                .addParams("demand_id", child_itembean.getDemand_id())
                .build()
                .execute(new Callback<ActEmployeeHome>() {
                    @Override
                    public void onBefore(Request request) {
                        super.onBefore(request);
                    }

                    @Override
                    public ActEmployeeHome parseNetworkResponse(Response response) throws Exception {

                        String string = response.body().string();
                        Log.e("sen", string);
                        ActEmployeeHome lesssonBean = JSON.parseObject(string, ActEmployeeHome.class);
                        return lesssonBean;
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mHandler.sendEmptyMessage(GETDATA_ERROR);
                    }

                    @Override
                    public void onResponse(ActEmployeeHome homeBeam) {
                        Message message = Message.obtain();
                        message.obj = homeBeam;
                        message.what = 1;
                        mHandler.sendMessage(message);

                    }
                });

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                isReFlesh = true;
                getEmployeeListData();
                mEmployeeSwipeRefreshWidget.setRefreshing(false);
                isReFlesh = false;
            }
        }, 1000);
    }


    @OnClick(R.id.employee_back)
    public void back(){
        finish();
    }


}
