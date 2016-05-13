package com.sen.haoliyou.mode;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/12.
 */
public class ExamUserAnswer implements Serializable {
    private String id;
    private String answer;
    private String type;
    private String opinion;

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public ExamUserAnswer(String id, String answer, String type) {
        this.id = id;
        this.answer = answer;
        this.type = type;
    }

    public ExamUserAnswer() {

    }


    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
