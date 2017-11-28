package skuniv.ac.kr.nursetask.Core.domain;

/**
 * Created by gunyoungkim on 2017-09-27.
 */

public class Room {
    private int roomno;
    private String roomname;
    private int count;

    public int getRoomno() {
        return roomno;
    }

    public void setRoomno(int roomno) {
        this.roomno = roomno;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
