package skuniv.ac.kr.nursetask.Core.domain;

/**
 * Created by gunyoungkim on 2017-11-18.
 */

public class LongTermScheduleVo {

        private int scheduleId;
        private String startDay;
        private String endDay;
        private String content;
        private String longnurseId;

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLongnurseId() {
        return longnurseId;
    }

    public void setLongnurseId(String longnurseId) {
        this.longnurseId = longnurseId;
    }
}



