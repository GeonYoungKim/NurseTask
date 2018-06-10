package skuniv.ac.kr.nursetask.UI.Nurse;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.NurseProvider;
import skuniv.ac.kr.nursetask.R;

/**
 * Created by gunyoungkim on 2017-09-04.
 */

public class NurseListFragment extends ListFragment {
    private ListArrayAdapter nurseListArrayAdapter;

    private static NurseListFragment nurseListFragment = null;
    public static synchronized NurseListFragment getInstance() {
        if (nurseListFragment == null) {
            nurseListFragment = new NurseListFragment();
        }
        return nurseListFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nurseListArrayAdapter=new ListArrayAdapter(getActivity());
        setListAdapter(nurseListArrayAdapter);
        new FatchNurseListAsyncTask().execute();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getInstance();
        return inflater.inflate(R.layout.fragment_nurse_list,container,false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView lv=((ListView)getView().findViewById(android.R.id.list));

    }

    public class FatchNurseListAsyncTask extends SafeAsyncTask<List<Nurse>> {

        @Override
        public List<Nurse> call() throws Exception {
            List<Nurse> Nurses=new NurseProvider().FatchNurseList();
            return Nurses;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("FatchUserListAsyncTask","arror"+e);
        }

        @Override
        protected void onSuccess(List<Nurse> Nurses) throws Exception {
            super.onSuccess(Nurses);

            System.out.println(Nurses);
            nurseListArrayAdapter.add(Nurses);
            getView().findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }
    public void realTimeUpdate(){
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.detach(NurseListFragment.this).attach(NurseListFragment.this).commitAllowingStateLoss();
    }
}
