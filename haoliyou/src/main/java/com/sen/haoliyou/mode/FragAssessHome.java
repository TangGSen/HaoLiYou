package com.sen.haoliyou.mode;

import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class FragAssessHome {
    private List<AssessmentItemBean> courseList;
    private String success;

    public List<AssessmentItemBean> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<AssessmentItemBean> courseList) {
        this.courseList = courseList;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
