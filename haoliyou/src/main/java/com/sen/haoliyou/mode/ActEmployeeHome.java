package com.sen.haoliyou.mode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/5/9.
 */
public class ActEmployeeHome implements Serializable {

    /**
     * courseList : [{"check_flag":"31","group_name":"营业培训","organization":"营业企划部","station_name":"经理","user_id":"3b9538c340e24a8da6fc0dee19b6c495","user_name":"刘志刚"},{"check_flag":"0","group_name":"营业培训","organization":"营业企划部","station_name":"主任","user_id":"6a3cb4c03ad040f3ad040342829b7f30","user_name":"尹珂"}]
     * success : true
     */

    private String success;
    /**
     * check_flag : 31
     * group_name : 营业培训
     * organization : 营业企划部
     * station_name : 经理
     * user_id : 3b9538c340e24a8da6fc0dee19b6c495
     * user_name : 刘志刚
     */

    private List<EmployeeItemBean> courseList;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<EmployeeItemBean> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<EmployeeItemBean> courseList) {
        this.courseList = courseList;
    }

    public  class EmployeeItemBean implements Serializable{
        private String check_flag;
        private String group_name;
        private String organization;
        private String station_name;
        private String user_id;
        private String user_name;

        public String getCheck_flag() {
            return check_flag;
        }

        public void setCheck_flag(String check_flag) {
            this.check_flag = check_flag;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public String getOrganization() {
            return organization;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }

        public String getStation_name() {
            return station_name;
        }

        public void setStation_name(String station_name) {
            this.station_name = station_name;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }
    }
}
