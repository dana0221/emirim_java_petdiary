package com.example.emirim_java_petdiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class DiaryList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        setListView();
    }

    private void setListView(){
        Intent intent = getIntent();
        String data = intent.getStringExtra("제목");
        ArrayList title = new ArrayList();
        title.add(data);

        ArrayAdapter listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, title);
        ListView listView = (ListView) findViewById(R.id.list_diary);
        listView.setAdapter(listAdapter);
    }
}