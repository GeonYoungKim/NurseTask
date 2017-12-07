package skuniv.ac.kr.nursetask.UI.Admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import skuniv.ac.kr.nursetask.Core.domain.Nurse;
import skuniv.ac.kr.nursetask.R;

public class AdminScheduleChoiceActivity extends AppCompatActivity {

    Nurse nurse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_schedule_choice);

        Intent intent=getIntent();
        nurse=(Nurse) intent.getExtras().get("Nurse");



        findViewById(R.id.today_schedule_choicebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"오늘 스케쥴 버튼",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AdminScheduleChoiceActivity.this,AdminTodayScheduleActivity.class);
                intent.putExtra("nurse",nurse);
                startActivity(intent);
                finish();

            }
        });
        findViewById(R.id.long_term_schedule_choicebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"장기 스케쥴 버튼",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AdminScheduleChoiceActivity.this,AdminLongTermScheduleInputActivity.class);
                intent.putExtra("nurse",nurse);
                startActivity(intent);
            }
        });



    }
}
