package skuniv.ac.kr.nursetask.UI.Nurse;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.NurseProvider;
import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Admin.GetSet;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gunyoungkim on 2017-09-04.
 */

public class NurseListFragment extends ListFragment {
    private ListArrayAdapter nurseListArrayAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        nurseListArrayAdapter=new ListArrayAdapter(getActivity());
        setListAdapter(nurseListArrayAdapter);
        GetSet.setNurseListFragment(this);
        return inflater.inflate(R.layout.fragment_nurse_list,container,false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView lv=((ListView)getView().findViewById(android.R.id.list));
        new FatchNurseListAsyncTask().execute();
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
    public void realTimeupdate(){
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.detach(NurseListFragment.this).attach(NurseListFragment.this).commitAllowingStateLoss();
    }
}
