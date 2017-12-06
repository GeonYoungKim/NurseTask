package skuniv.ac.kr.nursetask.UI.Nurse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.R;

public class InformationActivity extends AppCompatActivity {

    Nurse nurse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Intent intent=getIntent();
        nurse=(Nurse) intent.getExtras().get("nurse");

        ((TextView)findViewById(R.id.nurse_name)).setText(nurse.getName());
        ((TextView)findViewById(R.id.nurse_birth)).setText(nurse.getBirth());
        ((TextView)findViewById(R.id.nurse_phone)).setText(nurse.getPhone());
        ((TextView)findViewById(R.id.nurse_address)).setText(nurse.getAddress());
    }
}
