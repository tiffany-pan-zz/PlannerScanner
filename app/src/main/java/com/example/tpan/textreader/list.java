package com.example.tpan.textreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class list extends AppCompatActivity {

    private TextView theDate;
    public List<EventEntry> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        theDate = (TextView) findViewById(R.id.date);
        Intent incomingIntent = getIntent();
        StringBuilder strb = new StringBuilder();
        String date = incomingIntent.getStringExtra("date");
        strb.append("Schedule for " + date + "\n");

        readData();

        List<EventEntry> datesEvents = new ArrayList<>();
        List<String> startTimes = new ArrayList<>();
        for(int i= 0; i< events.size() && events!= null; i++) {
            if (events.get(i).getDate().compareTo(date) == 0) {
                datesEvents.add(events.get(i));
                startTimes.add((events.get(i).getStartTime()));
                //Log.i(this.getClass().getName(), i + " " + events.get(i).getDate() + " " + events.get(i).toString());
            }
        }

        //Object[] arrStartTimes = startTimes.toArray();
        //Arrays.sort(arrStartTimes);

        while(!datesEvents.isEmpty()) {
            String earliest = datesEvents.get(0).getStartTime();
            int index = 0;
            int j;
            for (j = 0; j< datesEvents.size(); j++) {
                if (earliest.compareTo(datesEvents.get(j).getStartTime()) > 0) {
                    earliest = datesEvents.get(j).getStartTime();
                    index = j;
                }
            }
            strb.append(datesEvents.get(index).toString() + "\n");
            datesEvents.remove(index);
        }

        //Log.i(this.getClass().getName(), strb.toString());
        if (strb != null) theDate.setText(strb);

    }

    public void readData() {  //add date param


        try {
            InputStream inputStream = this.openFileInput("test.csv");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";

                while ( (line = bufferedReader.readLine()) != null ) {
                    String[] token = line.split(",");
                    EventEntry data = new EventEntry(token[0], token[1], token[2], token[3]);
                    events.add(data);
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }
}
