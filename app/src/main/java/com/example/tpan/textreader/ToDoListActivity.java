package com.example.tpan.textreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ToDoListActivity extends AppCompatActivity {

    private TextView tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        tasks = (TextView) findViewById(R.id.Todolisttext);

        try {
            InputStream inputStream = this.openFileInput("ToDo.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder str = new StringBuilder();
                String line ="";

                while ((line = bufferedReader.readLine()) != null) {
                    str.append(line + "\n");
                    Log.i(this.getClass().getName(), line);
                }
                inputStream.close();

                tasks.setText(str);
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }
}
