package skuniv.ac.kr.nursetask.UI.Admin;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatActivity;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatRoomListFragment;
import skuniv.ac.kr.nursetask.UI.Nurse.NurseListFragment;

/**
 * Created by gunyoungkim on 2017-09-11.
 */

public class GetSet {
    private  static AdminPatientsListFragment adminPatientsListFragment;
    private  static ChatRoomListFragment chatRoomListFragment;
    private static AdminChatRoomListFragment adminChatRoomListFragment;
    private static ChatActivity chatActivity;
    private static Nurse nurse;
    private static NurseListFragment nurseListFragment;
    private static AdminNursesListFragment adminNursesListFragment;

    public static NurseListFragment getNurseListFragment() {
        return nurseListFragment;
    }

    public static void setNurseListFragment(NurseListFragment nurseListFragment) {
        GetSet.nurseListFragment = nurseListFragment;
    }

    public static AdminNursesListFragment getAdminNursesListFragment() {
        return adminNursesListFragment;
    }

    public static void setAdminNursesListFragment(AdminNursesListFragment adminNursesListFragment) {
        GetSet.adminNursesListFragment = adminNursesListFragment;
    }

    public static Nurse getNurse() {
        return nurse;
    }

    public static void setNurse(Nurse nurse) {
        GetSet.nurse = nurse;
    }

    public static AdminChatRoomListFragment getAdminChatRoomListFragment() {
        return adminChatRoomListFragment;
    }

    public static void setAdminChatRoomListFragment(AdminChatRoomListFragment adminChatRoomListFragment) {
        GetSet.adminChatRoomListFragment = adminChatRoomListFragment;
    }

    public static ChatActivity getChatActivity() {
        return chatActivity;
    }

    public static void setChatActivity(ChatActivity chatActivity) {
        GetSet.chatActivity = chatActivity;
    }

    public static ChatRoomListFragment getChatRoomListFragment() {
        return chatRoomListFragment;
    }

    public static void setChatRoomListFragment(ChatRoomListFragment chatRoomListFragment) {
        GetSet.chatRoomListFragment = chatRoomListFragment;
    }

    public static AdminPatientsListFragment getAdminPatientsListFragment() {
        return adminPatientsListFragment;
    }
    public static void setAdminPatientsListFragment(AdminPatientsListFragment adminPatientsListFragment) {
        GetSet.adminPatientsListFragment = adminPatientsListFragment;
    }

}
