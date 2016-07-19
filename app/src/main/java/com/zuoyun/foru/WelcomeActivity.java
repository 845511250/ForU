package com.zuoyun.foru;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.zuoyun.foru.adapter.MyViewPagerAdapter;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;


public class WelcomeActivity extends MyBaseActivity {
    private ViewPager viewPager;
    private View view1, view2, view3;//需要滑动的页卡
    Button bt_welcome_done;
    CircleIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initview();

    }

    public void initview() {
        //--------------------------------------------------------initviewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        LayoutInflater lf = getLayoutInflater().from(this);
        view1 = lf.inflate(R.layout.viewpage1, null);
        view2 = lf.inflate(R.layout.viewpage2, null);
        view3 = lf.inflate(R.layout.viewpage3, null);

        ArrayList<View> viewList=new ArrayList();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

        MyViewPagerAdapter adapter = new MyViewPagerAdapter(viewList);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        //----------------------------------------------------------findviewbyid

        bt_welcome_done = (Button) view3.findViewById(R.id.bt_welcome_done);
        //----------------------------------------------------------
        bt_welcome_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("ISFIRSTSTART", MODE_WORLD_WRITEABLE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("ISFIRSTSTART", false).commit();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }

}
