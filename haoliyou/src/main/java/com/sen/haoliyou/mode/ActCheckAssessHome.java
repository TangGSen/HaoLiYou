package com.sen.haoliyou.mode;

import java.util.List;

/**
 * Created by Administrator on 2016/5/13.
 */
public class ActCheckAssessHome {

    /**
     * qu_list : [{"answer":"b330abb949f44a8db7fe2f40eb54d163","big_title":"讲师评价","id":"273e3b9a7ed146979d2e14ac25bd3791","opinion":"辅导意见：","option":"c82457d88a9f48c488bebd3570d7a81c-6|8bcdbdf93b334f5cad59228541dbd69b-7|b330abb949f44a8db7fe2f40eb54d163-8|69e94bf83f60411096ed6e824f1aed9b-9|791c711c5163423ba6bf60fc4e0e60e1-10","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"与实际业务联系紧密，引导学员反思和解决问题","term_type":"1"},{"answer":"fe1306d2f0dd49358e21fd54478a29e8","big_title":"讲师评价","id":"bbe6e18814d5495986d37e75399dc52f","opinion":"辅导意见：","option":"f5a41b6f0dc5422cb80f40a550817309-6|10723a1c9e7b4dda82478357a5136f3e-7|fe1306d2f0dd49358e21fd54478a29e8-8|d8dd24bf788b47bc9fcdb871a6d68d0b-9|b5dd313168634bab869f89bc90b74f53-10","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"准确、有效讲述课程内容，鼓励学员提问和讨论","term_type":"1"},{"answer":"改善点","big_title":"改善点","id":"7e50006d3e874f2cb279119643d82634","opinion":"辅导意见：","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"你认为本次培训的不足是什么？有何建议？","term_type":"3"}]
     * success : true
     */

    private String success;
    /**
     * answer : b330abb949f44a8db7fe2f40eb54d163
     * big_title : 讲师评价
     * id : 273e3b9a7ed146979d2e14ac25bd3791
     * opinion : 辅导意见：
     * option : c82457d88a9f48c488bebd3570d7a81c-6|8bcdbdf93b334f5cad59228541dbd69b-7|b330abb949f44a8db7fe2f40eb54d163-8|69e94bf83f60411096ed6e824f1aed9b-9|791c711c5163423ba6bf60fc4e0e60e1-10
     * template_id : 1eb459b59d5041eb8fdb2f61f4719a4b
     * term_title : 与实际业务联系紧密，引导学员反思和解决问题
     * term_type : 1
     */

    private List<QuListBean> qu_list;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<QuListBean> getQu_list() {
        return qu_list;
    }

    public void setQu_list(List<QuListBean> qu_list) {
        this.qu_list = qu_list;
    }

    public  class QuListBean {
        private String answer;
        private String big_title;
        private String id;
        private String opinion;
        private String option;
        private String template_id;
        private String term_title;
        private String term_type;

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getBig_title() {
            return big_title;
        }

        public void setBig_title(String big_title) {
            this.big_title = big_title;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOpinion() {
            return opinion;
        }

        public void setOpinion(String opinion) {
            this.opinion = opinion;
        }

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public String getTemplate_id() {
            return template_id;
        }

        public void setTemplate_id(String template_id) {
            this.template_id = template_id;
        }

        public String getTerm_title() {
            return term_title;
        }

        public void setTerm_title(String term_title) {
            this.term_title = term_title;
        }

        public String getTerm_type() {
            return term_type;
        }

        public void setTerm_type(String term_type) {
            this.term_type = term_type;
        }
    }
}
