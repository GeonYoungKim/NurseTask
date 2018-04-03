package skuniv.ac.kr.nursetask.UI.Admin;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.Core.network.SafeAsyncTask;
import skuniv.ac.kr.nursetask.Core.provider.PatientProvider;
import skuniv.ac.kr.nursetask.R;

/**
 * Created by gunyoungkim on 2017-09-07.
 */

public class AdminPatientsListFragment extends ListFragment {


    private AdminPatientListArrayAdapter adminPatientListArrayAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adminPatientListArrayAdapter=new AdminPatientListArrayAdapter(getActivity());
        setListAdapter(adminPatientListArrayAdapter);
        new FatchAdminPatientListAsyncTask().execute();
    }

    @Nullable



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        GetSet.setAdminPatientsListFragment(this);

        return inflater.inflate(R.layout.fragment_admin_patients_list,container,false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().findViewById(R.id.insertPatientBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //환자 추가 소스
                Intent intent=new Intent(getContext(),AdminPatientInsesrtActivity.class);
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                realTimeUpdate();
            }
        }
    }
    public void realTimeUpdate(){
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ft.detach(AdminPatientsListFragment.this).attach(AdminPatientsListFragment.this).commitAllowingStateLoss();
    }
    public class FatchAdminPatientListAsyncTask extends SafeAsyncTask<List<Patient>> {

        @Override
        public List<Patient> call() throws Exception {
            List<Patient> Patients=new PatientProvider().FatchPatientList();
            return Patients;
        }
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.e("FatchUserListAsyncTask","arror"+e);
        }

        @Override
        protected void onSuccess(List<Patient> Patients) throws Exception {
            super.onSuccess(Patients);
            System.out.println(Patients);
            adminPatientListArrayAdapter.add(Patients);
            getView().findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

}
