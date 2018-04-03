package skuniv.ac.kr.nursetask.Core.domain;

/**
 * Created by gunyoungkim on 2017-09-27.
 */

public class ChatVo {
    private int roomNo;
    private String nurseId2;
    private int chatNo;
    private String chatContent;


    public int getroomNo() {
        return roomNo;
    }

    public void setroomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public String getnurseId2() {
        return nurseId2;
    }

    public void setnurseId2(String nurseId2) {
        this.nurseId2 = nurseId2;
    }

    public int getChatNo() {
        return chatNo;
    }

    public void setChatNo(int chatNo) {
        this.chatNo = chatNo;
    }

    public String getChatContent() {
        return chatContent;
    }

    public void setChatContent(String chatContent) {
        this.chatContent = chatContent;
    }
}
