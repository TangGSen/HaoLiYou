package com.sen.haoliyou.mode;

import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class ActDoAssessHome {

    /**
     * qu_list : [{"big_title":"培训方式","id":"d87615f3aaa34b4d9998597e8d84382e","option":"e718f072c1bb40f4884a89c6e68c8a7b-6|73125e8214dd4d6e9a52d2638a50cb8c-7|6e9b3f47ba62460a8fb180e81158d001-8|cdfbcfc8c5054520a1b2f1a6a79326b9-9|b2834d772a6c4099907f70f5719b5b13-10","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"培训较好的分配了讲课、练习和反馈的比重","term_type":"1"},{"big_title":"培训方式","id":"ec6ed72ca98044f18b33e1881bc89b49","option":"7cb28baeaed24ab28b092897160022bd-6|df73a99196474407b63a9d7dbaafc8b1-7|b5b963326b154bf3bad5db4389d5f180-8|d6c5c62682504bea829c0132a8818819-9|8ae27d1e666b4c55839540a8b851d7e7-10","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"培训方式具有多样化特点，有助于我对新的知识和技能的理解和掌握","term_type":"1"},{"big_title":"培训内容","id":"07c1ba68a64e496ca379c5171982dcd0","option":"e60654100b064526bbecfeb37c8629fd-6|8ecfd9d3d39042259985c70a67930808-7|7d7e5f7ad5564f70bd59fef9b6f92462-8|28fbb9de1e614d0980dd68081905f95f-9|858b1a1ec5b546a496186fb9fb757dd9-10","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"培训内容安排符合逻辑，案例有助于我理解新的知识和技能","term_type":"1"},{"big_title":"培训内容","id":"6c8dbed7acc74d379c838cc8e286196f","option":"c3771c6f3da14a8fb894e48c6acb9d64-6|d4e7069d93fa43fb8b12c7d3be09ec7f-7|cd877aa41b504c7899b42411dafc06c8-8|1fca10446c5e428690b9eca38af1f449-9|aae5363aadf14cc0a177c6ee5cdd7522-10","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"培训中的知识、技能，方法和工具有实战性，我可以用到实际工作中并改进我的绩效","term_type":"1"},{"big_title":"培训时机","id":"7f4e9f9537d94f31ac2fc2128c79496b","option":"92f16d12077048e8838c265ec00f92c9-6|64d0b24c8b7a46fcad53827ac23aad32-7|a944c64e0b6b4d45bffa205bb4922c46-8|a036ebb892ef495badaba6a174e8309d-9|587993b41aa6481e9752478eb22b651f-10","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"培训的时间长度是适合的","term_type":"1"},{"big_title":"培训时机","id":"e276affc5c024ecf926f517190f890ba","option":"f233ac444dd04f8d8efd6a32d31e21bd-6|e789bac8305b4e3ca3a0e4ff1760bfad-7|f4f4dd9d73b84452aaebc0d096d30e57-8|01ddabdfa49344058088d432ab09093b-9|fe0924e5d98a4c6d96f44a3f2d2b5673-10","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"出于我工作的需要，我参加本次培训的时机是合适的","term_type":"1"},{"big_title":"讲师评价","id":"273e3b9a7ed146979d2e14ac25bd3791","option":"c82457d88a9f48c488bebd3570d7a81c-6|8bcdbdf93b334f5cad59228541dbd69b-7|b330abb949f44a8db7fe2f40eb54d163-8|69e94bf83f60411096ed6e824f1aed9b-9|791c711c5163423ba6bf60fc4e0e60e1-10","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"与实际业务联系紧密，引导学员反思和解决问题","term_type":"1"},{"big_title":"讲师评价","id":"bbe6e18814d5495986d37e75399dc52f","option":"f5a41b6f0dc5422cb80f40a550817309-6|10723a1c9e7b4dda82478357a5136f3e-7|fe1306d2f0dd49358e21fd54478a29e8-8|d8dd24bf788b47bc9fcdb871a6d68d0b-9|b5dd313168634bab869f89bc90b74f53-10","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"准确、有效讲述课程内容，鼓励学员提问和讨论","term_type":"1"},{"big_title":"培训组织","id":"83beaad688c44e0197d4f1c7101fcccb","option":"e56846d7df9f4118b0b799f0bb9e3e7f-6|f97f14bc72364505851d7e38eb05c242-7|382877c66e734662b25fef42c8019945-8|e89fcde56d114821a13cf79c7c8e80d5-9|877efc3377e1414ab8c0f37268311783-10","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"本次培训准备充分、组织有序","term_type":"1"},{"big_title":"推荐度","id":"1d82cb91afab4238842d06ade71b65c3","option":"a23dc2c6735845eea601f6b9507fc78e-6|8ac0a8c9af0245edb2cbe855eb1a03bb-7|300787b10e5645c1a847a9f3bde23f6a-8|d9a296a7cc424ef9b705ef47f5debc87-9|293307f1f4bf4b46ae7d3bf5784d56da-10","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"你是否愿意将培训中学到的方法和工具分享给其他同事：","term_type":"1"},{"big_title":"改善点","id":"7e50006d3e874f2cb279119643d82634","template_id":"1eb459b59d5041eb8fdb2f61f4719a4b","term_title":"你认为本次培训的不足是什么？有何建议？","term_type":"3"}]
     * success : true
     */

    private String success;
    /**
     * big_title : 培训方式
     * id : d87615f3aaa34b4d9998597e8d84382e
     * option : e718f072c1bb40f4884a89c6e68c8a7b-6|73125e8214dd4d6e9a52d2638a50cb8c-7|6e9b3f47ba62460a8fb180e81158d001-8|cdfbcfc8c5054520a1b2f1a6a79326b9-9|b2834d772a6c4099907f70f5719b5b13-10
     * template_id : 1eb459b59d5041eb8fdb2f61f4719a4b
     * term_title : 培训较好的分配了讲课、练习和反馈的比重
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
        private String big_title;
        private String id;
        private String option;
        private String template_id;
        private String term_title;
        private String term_type;

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
