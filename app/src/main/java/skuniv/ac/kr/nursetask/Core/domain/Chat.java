package skuniv.ac.kr.nursetask.Core.domain;

/**
 * Created by gunyoungkim on 2017-09-27.
 */

public class Chat {
    private int roomno;
    private String nurseid2;
    private int chatno;
    private String chatcontent;
    public int getRoomno() {
        return roomno;
    }
    public void setRoomno(int roomno) {
        this.roomno = roomno;
    }
    public String getNurseid2() {
        return nurseid2;
    }
    public void setNurseid2(String nurseid2) {
        this.nurseid2 = nurseid2;
    }
    public int getChatno() {
        return chatno;
    }
    public void setChatno(int chatno) {
        this.chatno = chatno;
    }
    public String getChatcontent() {
        return chatcontent;
    }
    public void setChatcontent(String chatcontent) {
        this.chatcontent = chatcontent;
    }
}
