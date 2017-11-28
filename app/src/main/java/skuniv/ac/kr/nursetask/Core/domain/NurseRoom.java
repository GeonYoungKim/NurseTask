package skuniv.ac.kr.nursetask.Core.domain;

/**
 * Created by gunyoungkim on 2017-09-27.
 */

public class NurseRoom {
    private String nurseid;
    private int roomnum;
    private String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNurseid() {
        return nurseid;
    }

    public void setNurseid(String nurseid) {
        this.nurseid = nurseid;
    }

    public int getRoomnum() {
        return roomnum;
    }

    public void setRoomnum(int roomnum) {
        this.roomnum = roomnum;
    }
}
