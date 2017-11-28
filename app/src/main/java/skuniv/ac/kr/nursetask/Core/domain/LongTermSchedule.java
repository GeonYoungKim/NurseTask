package skuniv.ac.kr.nursetask.Core.domain;

/**
 * Created by gunyoungkim on 2017-11-18.
 */

public class LongTermSchedule {

        private int scheduleid;
        private String startday;
        private String endday;
        private String content;
        private String longnurseid;


        public int getScheduleid() {
            return scheduleid;
        }
        public void setScheduleid(int scheduleid) {
            this.scheduleid = scheduleid;
        }
        public String getStartday() {
            return startday;
        }
        public void setStartday(String startday) {
            this.startday = startday;
        }
        public String getEndday() {
            return endday;
        }
        public void setEndday(String endday) {
            this.endday = endday;
        }
        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }
        public String getLongnurseid() {
            return longnurseid;
        }
        public void setLongnurseid(String longnurseid) {
            this.longnurseid = longnurseid;
        }

}



