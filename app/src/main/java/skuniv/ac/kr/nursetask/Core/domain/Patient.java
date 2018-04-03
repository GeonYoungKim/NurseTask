package skuniv.ac.kr.nursetask.Core.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gunyoungkim on 2017-08-31.
 */

public class Patient implements Parcelable {
    private String patientCode;
    private String name;
    private String birth;
    private String sex;
    private String disease;
    private String period;
    private String note;
    private String room;
    private String image;


    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static Creator<Patient> getCREATOR() {
        return CREATOR;
    }

    protected Patient(Parcel in) {
        patientCode = in.readString();
        name = in.readString();
        birth = in.readString();
        sex = in.readString();
        disease = in.readString();
        period = in.readString();
        note = in.readString();
        room = in.readString();
        image = in.readString();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(patientCode);
        dest.writeString(name);
        dest.writeString(birth);
        dest.writeString(sex);
        dest.writeString(disease);
        dest.writeString(period);
        dest.writeString(note);
        dest.writeString(room);
        dest.writeString(image);
    }
}
