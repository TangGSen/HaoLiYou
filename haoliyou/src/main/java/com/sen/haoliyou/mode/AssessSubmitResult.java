package com.sen.haoliyou.mode;

/**
 * Created by Administrator on 2016/5/24.
 */
public class AssessSubmitResult {

    /**
     * action_flag : 2
     * message : 请不要重复提交
     * success : false
     */

    private String action_flag;
    private String message;
    private String success;

    public String getAction_flag() {
        return action_flag;
    }

    public void setAction_flag(String action_flag) {
        this.action_flag = action_flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
