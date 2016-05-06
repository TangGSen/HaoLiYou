package com.sen.haoliyou.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.sen.haoliyou.R;
import com.sen.haoliyou.activity.ActItemAssessDetail;
import com.sen.haoliyou.adapter.AssessmentListAdapter;
import com.sen.haoliyou.base.BaseFragment;
import com.sen.haoliyou.imgloader.AnimateFirstDisplayListener;
import com.sen.haoliyou.mode.AssessmentItemBean;
import com.sen.haoliyou.mode.EventComentCountForStudy;
import com.sen.haoliyou.mode.EventKillPositonStudy;
import com.sen.haoliyou.mode.FragAssessHome;
import com.sen.haoliyou.tools.AcountManager;
import com.sen.haoliyou.tools.Constants;
import com.sen.haoliyou.tools.DialogUtils;
import com.sen.haoliyou.tools.NetUtil;
import com.sen.haoliyou.tools.ResourcesUtils;
import com.sen.haoliyou.widget.RecyleViewItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Sen on 2016/3/3.
 */
public class FragmentAssessment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    @Bind(R.id.assessment_toolbar)
    Toolbar study_toolbar;
    @Bind(R.id.assess_recyclerview)
    RecyclerView assess_recyclerview;
    @Bind(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipe_refresh_widget;


    private List<AssessmentItemBean> mAssessData;
    private List<AssessmentItemBean> allAssessData;
    private AssessmentListAdapter adapter;
    private boolean isLoad = false;
    private boolean isReFlesh = false;

    private static final int GETDATA_ERROR = 0;

    private Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //下拉刷新和加载更多的时候就不用diaogle

            switch (msg.what) {
                case 0:
                    Toast.makeText(mActivity, R.string.net_error_retry, Toast.LENGTH_SHORT).show();
                    break;

                case 1:

                    FragAssessHome homeBeam = (FragAssessHome) msg.obj;
                    mAssessData = homeBeam.getCourseList();
                    // 当返回的数据为空的时候，那么就要显示这个
                    if (mAssessData == null) {
                        DialogUtils.closeDialog();
                        return false;
                    }
//

                    allAssessData.clear();
                    allAssessData.addAll(mAssessData);
                    mAssessData.clear();

                    showRecyclerviewItemData(allAssessData);

                    break;

            }
            DialogUtils.closeDialog();
            return false;
        }
    });




    private void showRecyclerviewItemData(List<AssessmentItemBean> assessData) {
        if (adapter == null) {
            //创建并设置Adapter
            adapter = new AssessmentListAdapter(mActivity, assessData);
            assess_recyclerview.setAdapter(adapter);

            adapter.setOnItemClickListener(new AssessmentListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, AssessmentItemBean childItemBean) {
//                    Intent intent = new Intent(mActivity, ActStudyDetail.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("itemBean",  childItemBean);
//                    bundle.putInt("itemPosition", position);
//                    intent.putExtra("FragmentAssessBundle", bundle);
//                    mActivity.startActivity(intent);
                    ActItemAssessDetail.toActItemAssessDetail(mActivity,childItemBean,position);
                }
            });
        } else {
            adapter.notifyDataSetChanged();
        }

    }



    @Override
    protected void dealAdaptationToPhone() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        Log.e("sen",AcountManager.getAcountId());
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_assessment, container, false);
        ButterKnife.bind(this, rootView);
        settingRecyleView();
        if (savedInstanceState == null) {
            //去加载数据
            isLoad = true;
            isReFlesh = false;
        } else {
            isLoad = false;
            Log.e("sen", "老数据");
            allAssessData = (List<AssessmentItemBean>) savedInstanceState.getSerializable("AssessListData");
            if (allAssessData != null) {
                showRecyclerviewItemData(allAssessData);
            }
        }

        return rootView;
    }

    public void onEventMainThread(AssessmentItemBean childItemBean) {


    }

    public void onEvent(EventComentCountForStudy event) { //接收方法  在发关事件的线程接收

    }


    public void onEvent(EventKillPositonStudy event) { //接收方法  在发关事件的线程接收


    }


    private void settingRecyleView() {
        mAssessData = new ArrayList<>();
        mAssessData.clear();
        LinearLayoutManager linearnLayoutManager = new LinearLayoutManager(mActivity);
        assess_recyclerview.setLayoutManager(linearnLayoutManager);
//        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
//        assess_recyclerview.setHasFixedSize(true);
        assess_recyclerview.setItemAnimator(new DefaultItemAnimator());
        assess_recyclerview.addItemDecoration(new RecyleViewItemDecoration(getContext(), R.drawable.shape_recycle_item_decoration));
        //填一个的时候不认
        swipe_refresh_widget.setColorSchemeResources(R.color.theme_color, R.color.theme_color);
        swipe_refresh_widget.setOnRefreshListener(this);

        //判断RecycleView 上下滑的时候，swipe_refresh_widget 的开关
        assess_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipe_refresh_widget.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });



    }

    @Override
    protected void initData() {
        allAssessData = new ArrayList<>();
        if (isLoad) {
            getAssessData();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("AssessListData", (Serializable) allAssessData);
    }

    private void getAssessData() {
        boolean hasNet = NetUtil.isNetworkConnected(mActivity);
        if (hasNet) {
            getDataFromNet(AcountManager.getAcountId());
        } else {
            Toast.makeText(getContext(), R.string.has_not_net, Toast.LENGTH_SHORT).show();
        }
    }


    private void getDataFromNet(String userid) {
        //下拉刷新和加载更多就不用show
        if (!isReFlesh)
            DialogUtils.showDialog(mActivity, ResourcesUtils.getResString(getContext(), R.string.dialog_show_wait));
        String url = Constants.PATH + Constants.PATH_GETASSESSMENTS;
        OkHttpUtils.post()
                .url(url)
                .addParams("user_id", userid)
                .build()
                .execute(new Callback<FragAssessHome>() {
                    @Override
                    public void onBefore(Request request) {
                        super.onBefore(request);
                    }

                    @Override
                    public FragAssessHome parseNetworkResponse(Response response) throws Exception {

                        String string = response.body().string();
                        Log.e("sen", string);
                        FragAssessHome lesssonBean = JSON.parseObject(string, FragAssessHome.class);
                        return lesssonBean;
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mHandler.sendEmptyMessage(GETDATA_ERROR);
                    }

                    @Override
                    public void onResponse(FragAssessHome homeBeam) {
                        Message message = Message.obtain();
                        message.obj = homeBeam;
                        message.what = 1;
                        mHandler.sendMessage(message);

                    }
                });
    }
//


 



    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        AnimateFirstDisplayListener.displayedImages.clear();
    }


    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                isReFlesh = true;
                getAssessData();
                swipe_refresh_widget.setRefreshing(false);
                isReFlesh = false;
            }
        }, 1000);
    }
}
