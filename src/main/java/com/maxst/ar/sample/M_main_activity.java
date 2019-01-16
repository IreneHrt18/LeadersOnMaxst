package com.maxst.ar.sample;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.maxst.ar.sample.dijkstra.Dij_Main;
import com.maxst.ar.sample.dijkstra.Point;
import com.maxst.ar.sample.imageTracker.ImageTrackerActivity;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 *系统主界面，在此类中获取地图编号，起点，终点
 */
public class M_main_activity extends Activity {

    private SlidingMenu menu;//渚ф粦鑿滃崟
    String city = null, map = null, startID, destination;
    int mapID ;
    LinearLayout[] layout;
    TextView[] textview;
    Button start;
    Spinner sp1, sp2;
    AutoCompleteTextView auto1, auto2;
    CheckBox Tips;
    boolean tips = false;
    int F[] = {545, 484, 566, 797, 619, 354};

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main_activity);

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setBehindOffsetRes(R.dimen.offset);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slidingmenu);
        layout=new LinearLayout[4];
        textview=new TextView[3];
        //闅愯棌
        layout[0] = (LinearLayout) findViewById(R.id.linear);
        layout[0].setVisibility(View.INVISIBLE);
        layout[1] = (LinearLayout) findViewById(R.id.linearLayout2);
        layout[1].setVisibility(View.INVISIBLE);
        layout[2] = (LinearLayout) findViewById(R.id.linearLayout4);
        layout[2].setVisibility(View.INVISIBLE);
        layout[3] = (LinearLayout) findViewById(R.id.linearLayout5);
        layout[3].setVisibility(View.INVISIBLE);
        start = (Button) findViewById(R.id.start);
        start.setVisibility(View.INVISIBLE);
        textview[0] = (TextView) findViewById(R.id.prompt);
        textview[1] = (TextView) findViewById(R.id.city);
        textview[2] = (TextView) findViewById(R.id.map);

        //Spinner
        sp1 = (Spinner) findViewById(R.id.spinner1);
        sp2 = (Spinner) findViewById(R.id.spinner2);
        sp1.setAlpha((float) 0.6);
        sp2.setAlpha((float) 0.6);
        sp1.setPrompt("请选择城市");
        sp2.setPrompt("请选择场所");
        ArrayList citylist = new ArrayList<String>();
        citylist.add(" ");
        citylist.add("北京");
        citylist.add("其他");
        ArrayAdapter<String> sp1adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citylist);
        sp1adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(sp1adapter);
        ArrayList maplist = new ArrayList<String>();
        maplist.add(" ");
        maplist.add("爱琴海购物中心");
        maplist.add("中国地质大学科研楼");
        ArrayAdapter<String> sp2adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, maplist);
        sp2adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(sp2adapter);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = sp1.getSelectedItem().toString();
                if (city != " " && map != " ")
                    visible();
                else
                    hidden();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                map = sp2.getSelectedItem().toString();
                if (city != " " && map != " ") {
                    visible();
                    mapID = sp2.getSelectedItemPosition();
                    Log.i("mapID", "onItemSelected: "+mapID);

                    setDesAdapter();
                }
                else
                    hidden();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //TIPS
        Tips =  findViewById(R.id.checkBox);
        Tips.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tips = Tips.isChecked();
            }
        });
        //AutoCompleteTextView
        auto1 =  findViewById(R.id.auto1);
        auto2 = findViewById(R.id.auto2);
        auto1.setAlpha((float) 0.6);
        auto2.setAlpha((float) 0.6);
        auto1.setThreshold(2);
        auto2.setThreshold(1);
        auto1.setSingleLine(true);
        auto2.setSingleLine(true);
        ArrayList startIDlist = new ArrayList<String>();
        for (int i = 1; i < 7; i++)
            for (int j = 1; j <= F[i - 1]; j++) {
                int z = 1000 * i + j;
                startIDlist.add(z + "");
            }
        ArrayAdapter<String> startadapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, startIDlist);
        auto1.setAdapter(startadapter);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start is like
                startID = auto1.getText().toString();
                destination = auto2.getText().toString();


                Intent intent=new Intent(M_main_activity.this,ImageTrackerActivity.class);
                intent.putExtra("startID",startID);
                intent.putExtra("destination",destination);

                String mClassToLaunchPackage=getPackageName();
                //intent.setClassName(mClassToLaunchPackage, ".ImageTrackerActivity",);

                startActivity(intent);
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setDesAdapter(){


        ArrayList deslist = new ArrayList<String>();
        String res1[] = {"1"};
        AssetManager ass = getAssets();
        String packageName=Integer.toString(mapID);
        int start=1;
        int end=6;
        if(mapID==2)
        {
            start=9;
            end=11;
        }

        for (int i = start; i <= end ; i++) {
            try {
                InputStream des = ass.open(packageName+"/F" + i + "name.txt");

                int length = des.available();

                Log.i("filelength", "onCreate: "+length);
                byte[] buffer = new byte[length];
                des.read(buffer);
                String result = new String(buffer, "utf8");
                res1 = result.split("\n");

            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
            for (int j = 0; j < res1.length; j++)
            {
                String name[]=new String[3];
                name=res1[j].split(" ");
                deslist.add(name[0]);
            }
        }
        ArrayAdapter<String> desadapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, deslist);
        auto2.setAdapter(desadapter);
    }

    public void visible() {
        layout[0].setVisibility(View.VISIBLE);
        layout[1].setVisibility(View.VISIBLE);
        layout[2].setVisibility(View.VISIBLE);
        layout[3].setVisibility(View.VISIBLE);
        start.setVisibility(View.VISIBLE);
        textview[0].setVisibility(View.GONE);
        textview[1].setText(city);
        textview[2].setText(map);
    }

    public void hidden() {
        layout[0].setVisibility(View.INVISIBLE);
        layout[1].setVisibility(View.INVISIBLE);
        layout[2].setVisibility(View.INVISIBLE);
        layout[3].setVisibility(View.INVISIBLE);
        start.setVisibility(View.INVISIBLE);
        textview[0].setVisibility(View.VISIBLE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                menu.toggle(true);
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
