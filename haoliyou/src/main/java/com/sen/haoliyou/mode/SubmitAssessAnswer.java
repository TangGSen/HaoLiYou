package com.sen.haoliyou.mode;

/**
 * Created by Administrator on 2016/5/11.
 */
public class SubmitAssessAnswer {
//    user_id： 用户ID
//    demand_id：评估ID
//    template_id: 模板ID
//    de_flag: 评估类型
//    demand_user_type: 评估对象
//    be_user_id：被评估的ID（领导评估时的学员ID；学员自评和满意度评估时，传“0”）
//    answer（json格式）

    private String user_id;
    private String demand_id;
    private String template_id;
    private String de_flag;
    private String demand_user_type;
    private String be_user_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDemand_id() {
        return demand_id;
    }

    public void setDemand_id(String demand_id) {
        this.demand_id = demand_id;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getDe_flag() {
        return de_flag;
    }

    public void setDe_flag(String de_flag) {
        this.de_flag = de_flag;
    }

    public String getDemand_user_type() {
        return demand_user_type;
    }

    public void setDemand_user_type(String demand_user_type) {
        this.demand_user_type = demand_user_type;
    }

    public String getBe_user_id() {
        return be_user_id;
    }

    public void setBe_user_id(String be_user_id) {
        this.be_user_id = be_user_id;
    }

    public String getAnswerJson() {
        return answerJson;
    }

    public void setAnswerJson(String answerJson) {
        this.answerJson = answerJson;
    }

    private String answerJson ;



}
