package skuniv.ac.kr.nursetask.Core.domain;

import android.os.Parcel;
import android.os.Parcelable;

import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;

/**
 * Created by gunyoungkim on 2017-08-30.
 */

public class Nurse implements Parcelable {

    private String nurseId;
    private String password;
    private String name;
    private String birth;
    private String phone;
    private String address;
    private String image;
    private String todaySchedule;
    private String token;


    private static Nurse nurse = null;
    public static synchronized Nurse getInstance() {
        if (nurse == null) {
            nurse = new Nurse(Parcel.obtain());
        }
        return nurse;
    }

    public String getnurseId() {
        return nurseId;
    }

    public void setnurseId(String nurseId) {
        this.nurseId = nurseId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTodaySchedule() {
        return todaySchedule;
    }

    public void setTodaySchedule(String todaySchedule) {
        this.todaySchedule = todaySchedule;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static Creator<Nurse> getCREATOR() {
        return CREATOR;
    }

    protected Nurse(Parcel in) {
        nurseId = in.readString();
        password = in.readString();
        name = in.readString();
        birth = in.readString();
        phone = in.readString();
        address = in.readString();
        image = in.readString();
        todaySchedule = in.readString();
        token = in.readString();
    }

    public static final Creator<Nurse> CREATOR = new Creator<Nurse>() {
        @Override
        public Nurse createFromParcel(Parcel in) {
            return new Nurse(in);
        }

        @Override
        public Nurse[] newArray(int size) {
            return new Nurse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nurseId);
        dest.writeString(password);
        dest.writeString(name);
        dest.writeString(birth);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(image);
        dest.writeString(todaySchedule);
        dest.writeString(token);
    }
}
