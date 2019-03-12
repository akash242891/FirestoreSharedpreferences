package com.example.akash.firestoresharedpreferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class ReviewActivity extends AppCompatActivity {

    SharedPreferences ansSP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ansSP = getSharedPreferences("localAnsDB", MODE_PRIVATE);
        int size = ansSP.getAll().size()/3;
        int[] indexes = new int[size];

        for (int i=1;i<=size;i++){

            if (ansSP.getInt(Integer.toString(i)+".seen", 0) == 0){
                indexes[i-1] = 0;
            }
            else {
                if (ansSP.getInt(Integer.toString(i)+".review",0) == 0){
                    if (ansSP.getInt(Integer.toString(i)+".ans",0) == 0){
                        indexes[i-1] = 3;
                    }
                    else {
                        indexes[i-1] = 1;
                    }
                }
                else {
                    if (ansSP.getInt(Integer.toString(i)+".ans",0) == 0){
                        indexes[i-1] = 4;
                    }
                    else {
                        indexes[i-1] = 2;
                    }
                }
            }

        }
        GridView gridView = findViewById(R.id.gridview);
        GridAdapter adapter = new GridAdapter(this, indexes);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(ReviewActivity.this, "Item: " + Integer.toString(i+1), Toast.LENGTH_SHORT).show();

                Intent jumpintent = new Intent();
                setResult(RESULT_OK, jumpintent);
                jumpintent.putExtra("Question", i+1);
                finish();
            }
        });


    }
}
