package com.sen.haoliyou.activity.assess;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.alibaba.fastjson.JSON;
import com.sen.haoliyou.R;
import com.sen.haoliyou.base.BaseActivity;
import com.sen.haoliyou.mode.ActCheckAssessHome;
import com.sen.haoliyou.mode.AssessmentItemBean;
import com.sen.haoliyou.mode.EventNoThing;
import com.sen.haoliyou.mode.ExamUserAnswer;
import com.sen.haoliyou.tools.AcountManager;
import com.sen.haoliyou.tools.Constants;
import com.sen.haoliyou.tools.DialogUtils;
import com.sen.haoliyou.tools.NetUtil;
import com.sen.haoliyou.tools.ResourcesUtils;
import com.sen.haoliyou.tools.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/3/11.
 */
public class ActCheckAssess extends BaseActivity implements GestureDetector.OnGestureListener {

    private static final int SUBMIT_ANSWER_DATA = 2;
    private static final int SHOW_DATA_LEFTBTN_CLICK = 5;
    private static final int SHOW_DATA_RIGTHBTN_CLICK = 6;
    private static final int GETDATA_ERROR = 0;
    private static final int SHOW_DATA = 1;
    private static final int SUBMIT_ANSER_DEAL = 3;
    private static final int SUBMIT_ANSER_ERROR = 4;
    private static final String ASSESSMENT_ITEMBEAN = "assessment_itembean";
    private static final String CHILD_BUNDLE = "child_bundle";
    private static final String BE_USER_ID = "be_user_id";
    private static final String IS_ADD_OPINION = "isAddOpinion";
    private boolean isAddOpinion;
    private String be_user_id;
    private AssessmentItemBean mAssessmentItemBean;
    private GestureDetector detector;
    @Bind(R.id.testing_tv_theme)
    AppCompatTextView testing_tv_theme;
    @Bind(R.id.testing_imgbtn_close)
    AppCompatTextView testing_imgbtn_close;
    @Bind(R.id.testing_image_pre)
    AppCompatTextView testing_image_pre;
    @Bind(R.id.image_submit_result)
    AppCompatTextView image_submit_result;
    @Bind(R.id.testing_image_next)
    AppCompatTextView testing_image_next;
    @Bind(R.id.testing_tv_num)
    AppCompatTextView testing_tv_num;
    @Bind(R.id.exam_viewflipper)
    ViewFlipper exam_viewflipper;
    private String assessName;
    //当前的的题号
    private int currentNum;
    //总题数
    private int allQusSize;

    private boolean firstShow =true;


    private List<ActCheckAssessHome.QuListBean> questionLists;
    private final int viewChaceSize = 3;
    private LinkedHashMap<String, View> viewChace = new LinkedHashMap<>();
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    Toast.makeText(ActCheckAssess.this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
                    break;
                case 1:

                    ActCheckAssessHome homeBeam = (ActCheckAssessHome) msg.obj;

                    questionLists = homeBeam.getQu_list();
                    if (questionLists == null) {
                        DialogUtils.closeDialog();
                        DialogUtils.closeUnCancleDialog();
                        Toast.makeText(ActCheckAssess.this, "获取数据失败，请重试", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if (questionLists.size() == 0) {
                        DialogUtils.closeDialog();
                        DialogUtils.closeUnCancleDialog();
                        Toast.makeText(ActCheckAssess.this, "获取数据失败，请重试", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    settingBtnAble(true);
                    showExamQuestion();
                    //第一次就显示答案，以后就是在点下或者上显示
                    if (firstShow){
                        showCurrentQuestion();
                        firstShow =!firstShow;
                    }
                    break;

                case 2:

                    break;
                case 3:

                    break;
                case 4:
                    break;
                case 5:
                    showPreQuestion();

                    break;
                case 6:
                    showNextQuestion();
                    break;
            }
            DialogUtils.closeDialog();
            DialogUtils.closeUnCancleDialog();
            return false;
        }
    });



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    public static void toThis(Context context, AssessmentItemBean child_itembean, String be_user_id, boolean isAddOpinion) {
        Intent intent = new Intent(context, ActCheckAssess.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ASSESSMENT_ITEMBEAN, child_itembean);
        bundle.putString(BE_USER_ID, be_user_id);
        bundle.putBoolean(IS_ADD_OPINION, isAddOpinion);
        intent.putExtra(CHILD_BUNDLE, bundle);
        context.startActivity(intent);
    }


    @Override
    protected void init() {
        super.init();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(CHILD_BUNDLE);
        be_user_id = bundle.getString(BE_USER_ID);
        isAddOpinion = bundle.getBoolean(IS_ADD_OPINION);


        mAssessmentItemBean = (AssessmentItemBean) bundle.getSerializable(ASSESSMENT_ITEMBEAN);
        if (mAssessmentItemBean == null) {
            Toast.makeText(ActCheckAssess.this, "获取试题失败，请重试", Toast.LENGTH_SHORT).show();
            return;
        }
        assessName = mAssessmentItemBean.getDemand_name();
        if (TextUtils.isEmpty(assessName)) {
            assessName = "";
        }

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.activity_enter_exam_testing);
        detector = new GestureDetector(ActCheckAssess.this, this);
        ButterKnife.bind(this);
        settingBtnAble(false);
        testing_tv_theme.setText(assessName);
        image_submit_result.setVisibility(View.GONE);

    }

    private void settingBtnAble(boolean ifCan) {
        testing_image_pre.setEnabled(ifCan);
        image_submit_result.setEnabled(ifCan);
        testing_image_next.setEnabled(ifCan);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        getAssessData();


    }

    public void getAssessData() {
        if (!NetUtil.isNetworkConnected(this)) {
            Toast.makeText(ActCheckAssess.this, "网络未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        DialogUtils.showunCancleDialog(this, "请稍后");
        String url = Constants.PATH + Constants.PATH_CHECKDEMAND;
        OkHttpUtils.post()
                .url(url)
                .addParams("user_id", AcountManager.getAcountId())
                .addParams("demand_id", mAssessmentItemBean.getDemand_id())
                .addParams("be_user_id", be_user_id)
                .build()
                .execute(new Callback<ActCheckAssessHome>() {
                    @Override
                    public void onBefore(Request request) {
                        super.onBefore(request);
                    }

                    @Override
                    public ActCheckAssessHome parseNetworkResponse(Response response) throws Exception {

                        String string = response.body().string();
                        Log.e("sen", string);
                        ActCheckAssessHome lesssonBean = JSON.parseObject(string, ActCheckAssessHome.class);
                        return lesssonBean;
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        mHandler.sendEmptyMessage(GETDATA_ERROR);

                    }

                    @Override
                    public void onResponse(ActCheckAssessHome homeBeam) {
                        Message message = Message.obtain();
                        message.obj = homeBeam;
                        message.what = SHOW_DATA;
                        mHandler.sendMessage(message);

                    }
                });
    }

    //防止重提交，直到出错为止
    public void setSubmitTestBtn(boolean isCan) {
        image_submit_result.setEnabled(isCan);
    }

    // 显示试题
    protected void showExamQuestion() {
        allQusSize = questionLists.size();
        testing_tv_num.setText((1 + currentNum) + "/" + allQusSize);

        exam_viewflipper.addView(addCustomView(currentNum));
    }

    /**
     * viewFlipper相关操作
     *
     * @param typeQuestion
     * @param options
     * @param question
     */
    AppCompatTextView tv_test_title;
    //单选，判断
    RadioGroup radio_group_single;

    //这个linearLayout 可以包含填空，多选，论述，简答题
    LinearLayout layout_other_type_exam;


    // 记录的答案,answerMap 第一个参数是选了没道题的哪一个选项，第二个是答案
    private HashMap<String, ExamUserAnswer> answerMap = new HashMap<String, ExamUserAnswer>();


    //ps:这样分组，因为我在点击保存 论述，简答 ，填空频繁需要题的类型，
    private int currtentType = -1;

    private void setQuestionType(String type) {
        if ("1".equals(type)) {
            currtentType = 1;
        } else if ("2".equals(type)) {
            currtentType = 2;
        } else if ("3".equals(type)) {
            currtentType = 3;
        }
    }


    private View addCustomView(final int currentNum) {
        // 找控件（viewflipper里的所有控件）
        //先在ViewChace 找，如果能找到
        //显示大标题
        ActCheckAssessHome.QuListBean questionItem = questionLists.get(currentNum);
        String bitTile = questionItem.getBig_title();
        String typeQuestion = questionItem.getTerm_type();
        setQuestionType(typeQuestion);
        View view = null;
        view = viewChace.get(questionLists.get(currentNum).getId());
        if (view != null) {
//            Log.e("sen", "存在view" + currentNum + 1);
            return view;
        }
//        Log.e("sen", "新建view" + currentNum + 1);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.viewflipper_assess, null);
        LinearLayout root_viewflipper_layout = (LinearLayout) view.findViewById(R.id.root_viewflipper_layout);
        tv_test_title = (AppCompatTextView) view.findViewById(R.id.tv_test_title);
        radio_group_single = (RadioGroup) view.findViewById(R.id.radio_group_single);
        layout_other_type_exam = (LinearLayout) view.findViewById(R.id.layout_other_type_exam);
        AppCompatEditText et_assess_lead = (AppCompatEditText) view.findViewById(R.id.et_assess_lead);
        et_assess_lead.setEnabled(false);
        if (isAddOpinion){
            et_assess_lead.setVisibility(View.VISIBLE);
        }else {
            et_assess_lead.setVisibility(View.GONE);
        }
        String options_show = questionLists.get(currentNum).getOption();

        String tv_question_string = (currentNum + 1) + ".[" + bitTile + "]" + questionLists.get(currentNum).getTerm_title();
        tv_test_title.setText(tv_question_string);

        if (currtentType == 1) {
            showSingleChoose(currentNum, options_show, radio_group_single, root_viewflipper_layout);
        } else if (currtentType == 2) {
            showMutileChoose(currentNum, options_show, layout_other_type_exam, root_viewflipper_layout);
        } else if (currtentType == 3) {
            showSubjectiveQuestions(currentNum, options_show, layout_other_type_exam, root_viewflipper_layout);
        }



        addViewChace(questionLists.get(currentNum).getId(), view);

        return view;
    }

    private void addViewChace(String key, View view) {
        if (viewChace.size() > viewChaceSize) {
            Set<String> keySet = viewChace.keySet();
            Iterator<String> iterator = keySet.iterator();
            String firstKey = "";
            if (iterator.hasNext()) {
                firstKey = iterator.next();
            }
            if (!"".equals(firstKey)) {
                viewChace.remove(firstKey);
            }

        }
        viewChace.put(key, view);
    }

//    //创建EditeText
//    private void createEditTextView(ViewGroup parentView) {
//        AppCompatEditText edit = new AppCompatEditText(this);
//        parentView.addView(edit);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250);
//        edit.setLayoutParams(params);
//        edit.setGravity(Gravity.LEFT);
//        edit.setTextSize(15);
//        edit.setHint("请写下辅导意见");
//        edit.setHintTextColor(ResourcesUtils.getResColor(ActCheckAssess.this, R.color.font_h2));
//        edit.setPadding(16, 16, 16, 16);
//        edit.setTextColor(ResourcesUtils.getResColor(this, R.color.primary_text));
//        params.setMargins(32, 32, 32, 32);
//        edit.setBackgroundDrawable(ResourcesUtils.getResDrawable(this, R.drawable.bg_exam_blank));
//
//    }


    //论述和简答题
    private void showSubjectiveQuestions(final int currentNum, String options_show, LinearLayout layout_other_type_exam, LinearLayout root_viewflipper_layout) {
        showVisbityAble(false, true);
        AppCompatEditText edit = new AppCompatEditText(this);
        edit.setEnabled(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        edit.setLayoutParams(params);
        edit.setMinHeight(250);
        edit.setGravity(Gravity.LEFT);
        edit.setTextSize(14);
        edit.setPadding(16, 16, 16, 16);
        edit.setTextColor(ResourcesUtils.getResColor(this, R.color.primary_text));
        params.setMargins(0, 32, 0, 32);
        edit.setBackgroundDrawable(ResourcesUtils.getResDrawable(this, R.drawable.bg_exam_blank));


        layout_other_type_exam.addView(edit);
    }



    //显示单选题
    private void showSingleChoose(final int currentNum, String options, RadioGroup radioGroup, LinearLayout root_viewflipper_layout) {
        showVisbityAble(true, false);
        String[] array_options = options.split("\\|");
        createChooseView(array_options, radioGroup);

    }

    //把题分成两类，1单选，判断   2.多选，填空，简答，论述
    public void showVisbityAble(boolean isSingle, boolean isShowBtn) {
        radio_group_single.setVisibility(isSingle ? View.VISIBLE : View.GONE);
        layout_other_type_exam.setVisibility(!isSingle ? View.VISIBLE : View.GONE);
    }

    private void createChooseView(String[] options, RadioGroup radioGroup) {
        int size = options.length;
        for (int i = 0; i < size; i++) {
            final AppCompatRadioButton radioButton = new AppCompatRadioButton(ActCheckAssess.this);
            radioButton.setEnabled(false);
            final String[] idAndQestion = options[i].split("\\-");
            radioButton.setText(idAndQestion[1]);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            radioButton.setPadding(0, 40, 0, 40);
            radioButton.setLayoutParams(params);
            radioButton.setGravity(Gravity.CENTER_VERTICAL);
            radioGroup.addView(radioButton);
            radioButton.setTextSize(14);
            radioButton.setButtonDrawable(R.drawable.seletor_single_choose_exam);
            final int temp = i;

        }
    }


    private void showMutileChoose(final int currentNum, String options, LinearLayout layout_other_type_exam, LinearLayout root_viewflipper_layout) {
        showVisbityAble(false, false);
        String[] array_options = options.split("\\|");
        int size = array_options.length;
        final String[] mMutilChoose = new String[size];
        for (int i = 0; i < size; i++) {
            final AppCompatCheckBox checkBox = new AppCompatCheckBox(ActCheckAssess.this);
            final String[] answerAndQues = array_options[i].split("\\-");
            checkBox.setText(answerAndQues[1]);
            checkBox.setEnabled(false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            checkBox.setPadding(0, 40, 0, 40);
            checkBox.setLayoutParams(params);
            checkBox.setGravity(Gravity.CENTER_VERTICAL);
            checkBox.setTextSize(14);
            checkBox.setButtonDrawable(R.drawable.down_checkbos_style);
            layout_other_type_exam.addView(checkBox);
            final int temp = i;
        }

    }

    private void showNextQuestion() {
        exam_viewflipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        exam_viewflipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
        currentNum++;
        if (currentNum > allQusSize - 1) {
            currentNum = allQusSize - 1;
            ToastUtils.showTextToast(ActCheckAssess.this,"已经是最后一题啦");
        } else {
            exam_viewflipper.removeAllViews();
            showExamQuestion();
            showCurrentQuestion();
            exam_viewflipper.showNext();
        }
    }


    private void showPreQuestion() {

        /** 显示上一个界面 */
        exam_viewflipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
        exam_viewflipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
        currentNum--;
        if (currentNum < 0) {
            currentNum = 0;
            ToastUtils.showTextToast(ActCheckAssess.this,"已经是第一题了");
        } else {
            exam_viewflipper.removeAllViews();
            showExamQuestion();
            showCurrentQuestion();
            exam_viewflipper.showPrevious();
        }

    }

    // 显示当前用户的答案
    private void showCurrentQuestion() {
        String key = questionLists.get(currentNum).getId();

        View view = viewChace.get(key);
        String userAnswerStr =  questionLists.get(currentNum).getAnswer();
        if (userAnswerStr == null) {
            return;
        }
        //把训后的意见也要找到并显示
        if (isAddOpinion) {
            String userStr = questionLists.get(currentNum).getOpinion();
            AppCompatEditText et = (AppCompatEditText)findViewById(R.id.et_assess_lead);
            if (TextUtils.isEmpty(userStr)) {
                et.setText("");
            } else {
                et.setText(userStr);

            }

        }
        switch (currtentType) {
            case 1:
                String[] quesitonAnawser = questionLists.get(currentNum).getOption().split("\\|");
                //从缓存view中找回以前的父控件，这个很关键，要不崩溃的
                radio_group_single = (RadioGroup) view.findViewById(R.id.radio_group_single);
                int ques = quesitonAnawser.length;
                for (int i = 0; i < ques; i++) {
                    String checkedItem = quesitonAnawser[i].split("\\-")[0];
                    if (userAnswerStr.equals(checkedItem)) {
                        AppCompatRadioButton childAt = (AppCompatRadioButton) radio_group_single.getChildAt(i);
                        childAt.setChecked(true);
                        break;
                    }
                }
                break;
            case 2:
                String[] quesitonAnawsers = questionLists.get(currentNum).getOption().split("\\|");
                layout_other_type_exam = (LinearLayout) view.findViewById(R.id.layout_other_type_exam);
                //用户的多选题是用 | 分割的
                String[] userAns = userAnswerStr.split("\\|");
                int questions = quesitonAnawsers.length;

                int userQues = userAns.length;
                for (int y = 0; y < questions; y++) {

                    for (int i = 0; i < userQues; i++) {

                        String checkedItem = quesitonAnawsers[y].split("\\-")[0];
                        if (userAns[i].equals(checkedItem)) {
                            AppCompatCheckBox childAt = (AppCompatCheckBox) layout_other_type_exam.getChildAt(y);
                            childAt.setChecked(true);
                        }
                    }
                }
                break;
            case 3:
                layout_other_type_exam = (LinearLayout) view.findViewById(R.id.layout_other_type_exam);
                AppCompatEditText child = (AppCompatEditText) layout_other_type_exam.getChildAt(0);
                child.setText(userAnswerStr);
                break;

        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {

        if (arg0.getX() - arg1.getX() > 120) {
            showNextQuestion();
            return true;
        }
        if (arg0.getX() - arg1.getX() < -120) {
            showPreQuestion();
            return true;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {

    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;

    }

    //点击事件

    /**
     * 当用户快速点击 上一题，下一题的时候，很容易界面上很乱，所以要控制时间
     */


    boolean isLeft = false;

    @OnClick(R.id.testing_image_pre)
    public void showPre() {
        mHandler.removeMessages(SHOW_DATA_RIGTHBTN_CLICK);
        //绑定一个msg，内容为接下来需要的button的ID，
        Message msg = Message.obtain();
        msg.what = SHOW_DATA_LEFTBTN_CLICK;
        //发送消息间隔1秒
        mHandler.sendMessageDelayed(msg, 200);

    }


    @OnClick(R.id.testing_image_next)
    public void showNext() {
        mHandler.removeMessages(SHOW_DATA_LEFTBTN_CLICK);
        //绑定一个msg，内容为接下来需要的button的ID，
        Message msg = Message.obtain();
        msg.what = SHOW_DATA_RIGTHBTN_CLICK;
        //发送消息间隔1秒
        mHandler.sendMessageDelayed(msg, 200);

    }

    @OnClick(R.id.testing_imgbtn_close)
    public void closeExam() {
        finish();

    }

    public void onEvent(EventNoThing eventNoThing) {


    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


}
