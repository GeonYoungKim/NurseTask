package skuniv.ac.kr.nursetask.Core.domain;

/**
 * Created by gunyoungkim on 2017-09-27.
 */

public class NurseRoomVo {
    private String nurseId;
    private int roomNum;
    private String token;


    public String getnurseId() {
        return nurseId;
    }

    public void setnurseId(String nurseId) {
        this.nurseId = nurseId;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
