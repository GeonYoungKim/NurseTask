package skuniv.ac.kr.nursetask.UI.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import skuniv.ac.kr.nursetask.R;
import skuniv.ac.kr.nursetask.UI.Nurse.ChoiceRoomActivity;
import skuniv.ac.kr.nursetask.UI.Nurse.LongTermScheduleShowActivity;
import skuniv.ac.kr.nursetask.UI.Nurse.MainActivity;
import skuniv.ac.kr.nursetask.UI.Nurse.MainTabsAdapter;
import skuniv.ac.kr.nursetask.UI.Nurse.MainTabsConfig;
import skuniv.ac.kr.nursetask.UI.Nurse.TodayScheduleShowActivity;

public class AdminMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private AdminMainTabsAdapter mainTabsAdapter;
    private NavigationView navigationView;
    private String nurseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        Intent intent=getIntent();
        nurseId=(String)intent.getExtras().get("nurseId");

        navigationView=(NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        mainTabsAdapter = new AdminMainTabsAdapter(this, (TabHost) findViewById(android.R.id.tabhost), (ViewPager) findViewById(R.id.pager));
        mainTabsAdapter.selectTab(MainTabsConfig.TABINDEX.FIRST);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        findViewById(R.id.menubtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            new AlertDialog.Builder(AdminMainActivity.this).setTitle("로그아웃하시겠습니까?").setIcon(android.R.drawable.ic_delete).setView(R.layout.logout).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences preferences = getSharedPreferences("Text_number_store", MODE_PRIVATE);
                    preferences.edit().remove("nurse").commit();
                    UpdateToken updateToken=new UpdateToken(nurseId,"0");
                    updateToken.execute();
                    Intent intent=new Intent(AdminMainActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).show();

        }else if(id==R.id.today_Work){
            //오늘 스케쥴 보여주기
            Intent intent=new Intent(AdminMainActivity.this,TodayScheduleShowActivity.class);
            intent.putExtra("nurseId",nurseId);
            startActivity(intent);
        }else if(id==R.id.long_term_Work){
            //장기 스케쥴 보여주기
            Intent intent=new Intent(AdminMainActivity.this,LongTermScheduleShowActivity.class);
            intent.putExtra("nurseId",nurseId);
            startActivity(intent);
        }else if(id==R.id.charge_Patient_List){
            //담당 환자 보여주기
            Toast.makeText(getApplicationContext(),"담당환자",Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}

