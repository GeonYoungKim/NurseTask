package skuniv.ac.kr.nursetask.UI.Admin;

import android.os.Bundle;

import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Nurse.ChatRoomListFragment;
import skuniv.ac.kr.nursetask.UI.Nurse.ChoiceRoomFragment;
import skuniv.ac.kr.nursetask.UI.Nurse.MainTabsConfig;
import skuniv.ac.kr.nursetask.UI.Nurse.NurseListFragment;

/**
 * Created by gunyoungkim on 2017-09-07.
 */

public class AdminMainTabsConfig {
    public static AdminMainTabsConfig.TabInfo[] TABINFOS = {
            new AdminMainTabsConfig.TabInfo( "사용자", R.drawable.ic_online, R.drawable.ic_online, AdminNursesListFragment.class, null),
            new AdminMainTabsConfig.TabInfo( "알람1", R.drawable.ic_online, R.drawable.ic_online, AdminChatRoomListFragment.class, null),
            new AdminMainTabsConfig.TabInfo( "알람2", R.drawable.ic_online, R.drawable.ic_online, AdminPatientsListFragment.class, null),
            new AdminMainTabsConfig.TabInfo( "알람2", R.drawable.ic_online, R.drawable.ic_online, ChoiceRoomFragment.class, null)
    };

    public static final class TABINDEX {
        public static final int USERLIST = 0;
        public static final int CHANNELLIST = 1;
        public static final int SETTINGS = 2;
        public static final int SECOND = 1;
        public static final int THIRD = 2;
        public static final int FIRST = 0;
        public static final int LAST = TABINFOS.length;
    };

    public static final int COUNT_TABS() {
        return TABINFOS.length;
    }

    public static final AdminMainTabsConfig.TabInfo TABINFO(int index ) {
        return ( index < 0 || index >= COUNT_TABS() )  ? null : TABINFOS[ index ];
    }

    public static final class TabInfo {
        public final String tag;
        public final int drawableNormal;
        public final int drawableSelected;
        public final Class<?> klass;
        public final Bundle bundle;
        TabInfo( String tag, int drawableNormal, int drawableSelected, Class<?> klass, Bundle bundle ) {
            this.tag = tag;
            this.drawableNormal = drawableNormal;
            this.drawableSelected = drawableSelected;
            this.klass = klass;
            this.bundle = bundle;
        }
    }
}
