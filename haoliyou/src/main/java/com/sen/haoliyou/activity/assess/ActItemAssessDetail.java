package com.sen.haoliyou.activity.assess;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.activeandroid.util.Log;
import com.sen.haoliyou.R;
import com.sen.haoliyou.base.BaseActivity;
import com.sen.haoliyou.mode.AssessmentItemBean;
import com.sen.haoliyou.tools.ResourcesUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/3/11.
 */
public class ActItemAssessDetail extends BaseActivity {


    @Bind(R.id.assess_imgbtn_close)
    AppCompatTextView mAssessImgbtnClose;
    @Bind(R.id.tv_head_name)
    AppCompatTextView mTvHeadName;
    @Bind(R.id.assess_detail_toolbar)
    Toolbar mAssessDetailToolbar;
    @Bind(R.id.tv_assess_title)
    AppCompatTextView mTvAssessTitle;
    @Bind(R.id.tv_begin_time)
    AppCompatTextView mTvBeginTime;
    @Bind(R.id.tv_assess_class)
    AppCompatTextView mTvAssessClass;
    @Bind(R.id.tv_belong_class)
    AppCompatTextView mTvBelongClass;
    @Bind(R.id.tv_assess_introduce)
    AppCompatTextView mTvAssessIntroduce;
    @Bind(R.id.btn_enter_assess)
    AppCompatButton mBtnEnterAssess;

    AssessmentItemBean childItemBean;

    @Override
    protected void init() {
        super.init();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("FragmentAssessBundle");
        childItemBean = (AssessmentItemBean) bundle.getSerializable("itemBean");

        if (childItemBean == null) {
            Toast.makeText(this, "获取考试详情出错", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public static void toActItemAssessDetail(Context context, AssessmentItemBean childItemBean, int position) {
        Intent intent = new Intent(context, ActItemAssessDetail.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("itemBean", childItemBean);
        bundle.putInt("itemPosition", position);
        intent.putExtra("FragmentAssessBundle", bundle);
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.activity_assess_item_detail);
        ButterKnife.bind(this);


    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        showData();
    }

    private void showData() {
        mTvAssessTitle.setText(childItemBean.getDemand_name());
        mTvBeginTime.setText("时间：" + childItemBean.getBegin_date() + "至" + childItemBean.getEnd_date());

        //设置btn 的样式
        setBtnAssessStyle();
        //类型判断  评估类型标识，3为满意度评估，4为训前评估，5为训后评估
        int assessType = Integer.parseInt(childItemBean.getDe_flag());
        String assessTypeStr = "";
        if (assessType == 3) {
            assessTypeStr = "满意度评估";

        } else if (assessType == 4) {
            assessTypeStr = "训前360评估";

        } else if (assessType == 5) {
            assessTypeStr = "训后评估";

        }

        mTvAssessClass.setText("类型：" + assessTypeStr);
        mTvBelongClass.setText("所属培训班：" + childItemBean.getTc_name());
        mTvAssessIntroduce.setText("评估说明：" + childItemBean.getRemark());

    }

    private void setBtnAssessStyle(){
        int examType = Integer.parseInt(childItemBean.getTime_flag());
        String demand_user_type = childItemBean.getDemand_user_type();
        String is_submit =childItemBean.getIs_submit();
        if (examType == 0) {
            //未开始
            mBtnEnterAssess.setEnabled(false);
            mBtnEnterAssess.setText("参加评估");
        } else if (examType == 1) {
                //进行中
            if(is_submit.equals("1")){
                mBtnEnterAssess.setBackgroundDrawable(ResourcesUtils.getResDrawable(ActItemAssessDetail.this,R.drawable.bg_exam_unenter));
                mBtnEnterAssess.setText("查看评估");
            }else{
                mBtnEnterAssess.setEnabled(true);
                mBtnEnterAssess.setText("参加评估");
            }
        } else if (examType == 2) {
            //已结束
            if ("1".equals(demand_user_type)) {
                // 操作：训前学员
                if(is_submit.equals("1")){
                    mBtnEnterAssess.setBackgroundDrawable(ResourcesUtils.getResDrawable(ActItemAssessDetail.this,R.drawable.bg_exam_unenter));
                    mBtnEnterAssess.setText("查看评估");
                }else{
                    mBtnEnterAssess.setEnabled(false);
                    mBtnEnterAssess.setText("参加评估");
                }
            } else {
                // 领导 ，如果是
                    mBtnEnterAssess.setBackgroundDrawable(ResourcesUtils.getResDrawable(ActItemAssessDetail.this,R.drawable.bg_exam_unenter));
                    mBtnEnterAssess.setText("查看评估");

            }
        }
    }

    @OnClick(R.id.btn_enter_assess)
    public void enterAssess() {
//        Intent intent = new Intent(this, ActExamTest.class);
//        intent.putExtra("examId", childItemBean.getExamid());
//        intent.putExtra("examName", childItemBean.getExamname());
//        startActivity(intent);
        enterAssessType();

    }

    private void enterAssessType() {

        //类型判断  评估类型标识，3为满意度评估，4为训前评估，5为训后评估
        int de_flag = Integer.parseInt(childItemBean.getDe_flag());
        String demand_user_type = childItemBean.getDemand_user_type();
        switch (de_flag) {
            case 3:
//                类型：满意度评估
//                操作：进入答题界面
//                领导评估时的学员ID；学员自评和满意度评估时，传“0” false,"0 双重保障
                ActDoAssess.toThis(ActItemAssessDetail.this,childItemBean,"0", false);


                break;
            case 4:

                if ("1".equals(demand_user_type)) {
                    // 操作：训前学员评估直接进入答题界面
                    Log.e("sen",childItemBean.getDemand_id());
                    ActDoAssess.toThis(ActItemAssessDetail.this,childItemBean,"0", false);

                } else {
                    // 操作：训前领导评估进入下级人员列表界面
                    ActEmployeeList.toThis(ActItemAssessDetail.this,childItemBean,false);
                }
                break;
            case 5:

                if ("1".equals(demand_user_type)) {
                    // 操作：训后学员评估直接进入答题界面
                    Log.e("sen",childItemBean.getDemand_id());
                    ActDoAssess.toThis(ActItemAssessDetail.this,childItemBean,"0", false);

                } else {
                    // 操作：训后
                    // 领导评估进入下级人员列表界面
                    ActEmployeeList.toThis(ActItemAssessDetail.this,childItemBean,true);
                }
                break;
        }

    }

    @OnClick(R.id.assess_imgbtn_close)
    public void exitExam() {
        exit();
    }

    private void exit() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
    }


}
