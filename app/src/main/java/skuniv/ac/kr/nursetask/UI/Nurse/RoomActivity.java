package skuniv.ac.kr.nursetask.UI.Nurse;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import skuniv.ac.kr.nursetask.Core.domain.Patient;
import skuniv.ac.kr.nursetask.R;

public class RoomActivity extends AppCompatActivity implements View.OnClickListener{
    private List<Patient> roomPatientList;
    private Button patientButtons[];
    public static Map<Integer,Patient> seats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Intent intent = getIntent();
        roomPatientList = (List<Patient>) intent.getExtras().get("roomPatientList");

        patientButtons = new Button[]{(Button) findViewById(R.id.one), (Button) findViewById(R.id.two), (Button) findViewById(R.id.three), (Button) findViewById(R.id.four)
                , (Button) findViewById(R.id.five), (Button) findViewById(R.id.six), (Button) findViewById(R.id.seven), (Button) findViewById(R.id.eight)};
        for(int i=0;i<patientButtons.length;i++){
            patientButtons[i].setText("");
            patientButtons[i].setOnClickListener(this);
        }

        findViewById(R.id.door).setOnClickListener(this);

        seats=new HashMap<Integer,Patient>();

        for (Patient patient : roomPatientList) {
            seats.put(Integer.parseInt(patient.getRoom().trim().charAt(4)+""),patient);
        }
        Iterator<Integer> keys = seats.keySet().iterator();
        while ( keys.hasNext() ) {
            int key = keys.next();
            patientButtons[key-1].setText(seats.get(key).getName()+"");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.one:
                if(patientButtons[0].getText().equals("")) { break;}
                else {
                    new MyAlertDialog(this,1);
                    break;
                }
            case R.id.two:
                if(patientButtons[1].getText().equals("")) { break;}
                else {
                    new MyAlertDialog(this,2);
                    break;
                }
            case R.id.three:
                if(patientButtons[2].getText().equals("")) { break;}
                else {
                    new MyAlertDialog(this,3);
                    break;
                }
            case R.id.four:
                if(patientButtons[3].getText().equals("")) { break;}
                else {
                    new MyAlertDialog(this,4);
                    break;
                }
            case R.id.five:
                if(patientButtons[4].getText().equals("")) { break;}
                else {
                    new MyAlertDialog(this,5);
                    break;
                }
            case R.id.six:
                if(patientButtons[5].getText().equals("")) { break;}
                else {
                    new MyAlertDialog(this,6);
                    break;
                }
            case R.id.seven:
                if(patientButtons[6].getText().equals("")) { break;}
                else {
                    new MyAlertDialog(this,7);
                    break;
                }
            case R.id.eight:
                if(patientButtons[7].getText().equals("")) { break;}
                else {
                    new MyAlertDialog(this,8);
                    break;
                }
            case R.id.door:
                finish();
                break;
        }
    }
}
