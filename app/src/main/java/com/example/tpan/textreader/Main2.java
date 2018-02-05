package com.example.tpan.textreader;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;

public class Main2 extends AppCompatActivity {

    static String TAG = "Main2";
    private CalendarView mCW;
    private static final String TAG1 = "list";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ImageButton Cam = (ImageButton)( findViewById(R.id.cam));

        ImageButton Setting  = (ImageButton)( findViewById(R.id.Setting));

        ImageButton list = (ImageButton) (findViewById(R.id.todoButton));

        Cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View c) {
                Log.i(TAG, "Camera was Clicked");
                Intent Camintent = new Intent(Main2.this,MainActivity.class);
                startActivity(Camintent);
            }
        });
        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View S) {
                Log.i(TAG, "Setting was Clicked");
                Intent Settingintent = new Intent(Main2.this,Setting.class);
                startActivity(Settingintent);
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View L) {
                Log.i(TAG, "List was Clicked");
                Intent Listintent = new Intent(Main2.this, ToDoListActivity.class);
                startActivity(Listintent);
            }
        });

        mCW = (CalendarView) findViewById(R.id.cW);
        mCW.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView cW, int i, int i1, int i2) {
                String date = (i1+1)+"/" + i2 + "/" + i;
                Log.d(TAG1, "onSelectedDateChange: " + date);
                Intent intent = new Intent(Main2.this, list.class);
                intent.putExtra("date", date);
                startActivity (intent);
                //String dateList = date;
            }
        });

    }
}
