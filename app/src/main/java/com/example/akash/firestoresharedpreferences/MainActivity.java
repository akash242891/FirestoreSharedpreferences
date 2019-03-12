package com.example.akash.firestoresharedpreferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

public class MainActivity extends AppCompatActivity {

    public SharedPreferences quesSP,ansSP;
    ListView listview;
    TextView questext;
    FirebaseFirestore db;
    ArrayAdapter adapter;
    Button prevButton, nextButton, reviewButton;
    int presentquesno = 1;
    int presentansstate = 0;
    int presentreviewstate = 0;
    String[] ansstring;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        quesSP = getSharedPreferences("localQuestionDB", MODE_PRIVATE);
        ansSP = getSharedPreferences("localAnsDB", MODE_PRIVATE);
        listview = findViewById(R.id.listview);
        questext = findViewById(R.id.questiontext);
        prevButton = findViewById(R.id.prevButton);
        prevButton.setVisibility(View.INVISIBLE);
        nextButton = findViewById(R.id.nextButton);
        reviewButton = findViewById(R.id.reviewButton);

        ansstring = new String[4];
            //Retrieving all the documents from database which will give a queryDocumentSnapshots(a list of document snapshots)
            //By default the documents might not be arranged in ascending order. So the documents are retrieved in ascending order
            //of their questionnumber. In database documentId 10 comes before 2. That's why this technique is used
            db.collection("Questions").orderBy("number", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    //Opening a shared preferences editor which will first store all the data in itself.
                    //When commit or apply function is called. All the data is transferred from editor to sharedpreferences
                    SharedPreferences.Editor queseditor = quesSP.edit();
                    //Using the for loop, all the questions are retrieved and saved in local memory.
                    //Thus, even if the internet goes away, the question can be accessed.
                    for (int i = 1; i<= queryDocumentSnapshots.getDocuments().size();i++){
                        queseditor.putString(Integer.toString(i)+".q", queryDocumentSnapshots.getDocuments().get(i-1).getString("question"));
                        queseditor.putString(Integer.toString(i)+".a", queryDocumentSnapshots.getDocuments().get(i-1).getString("optionA"));
                        queseditor.putString(Integer.toString(i)+".b", queryDocumentSnapshots.getDocuments().get(i-1).getString("optionB"));
                        queseditor.putString(Integer.toString(i)+".c", queryDocumentSnapshots.getDocuments().get(i-1).getString("optionC"));
                        queseditor.putString(Integer.toString(i)+".d", queryDocumentSnapshots.getDocuments().get(i-1).getString("optionD"));

                    }
                    queseditor.apply();
                    displayFirstQuestion();
                    //The previous answer state is retrieved, so that next time when the user logs in, his previous session resumes
                    //A separate sharedprefrence is used to store all the answers.
                    //In the end, all the answers are written into the database using batch writing
                    db.collection("Answers").orderBy("number",Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            SharedPreferences.Editor anseditor = ansSP.edit();
                            for (int i = 1; i<= queryDocumentSnapshots.getDocuments().size();i++){
                                anseditor.putInt(Integer.toString(i)+".ans",queryDocumentSnapshots.getDocuments().get(i-1).getLong("ans").intValue());
                                anseditor.putInt(Integer.toString(i)+".review",queryDocumentSnapshots.getDocuments().get(i-1).getLong("review").intValue());
                                anseditor.putInt(Integer.toString(i)+".seen",queryDocumentSnapshots.getDocuments().get(i-1).getLong("seen").intValue());

                            }
                            anseditor.apply();
                            loadAnswerState(1);

                        }
                    });
                }
            });
        //Once previous button is clicked, the answerstate of the present question is saved
        //questionnumber is reduced, question is changed by accessing the previous question from shared preferences
        //If the question has already been attempted, then previous answerstate is loaded and highlighted.
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presentquesno != 1){
                    if (presentquesno == 10){
                        nextButton.setVisibility(View.VISIBLE);
                    }
                    if (presentquesno == 2){
                        prevButton.setVisibility(View.INVISIBLE);
                    }
                    saveAnswerState(presentquesno, presentansstate, presentreviewstate);
                    presentquesno--;
                    changeQuestion(presentquesno);
                    loadAnswerState(presentquesno);
                }
            }
        });
        // Once next button is clicked, present answer is saved, question is changed to next one,
        //If the question has already been attempted, then it will load the answer state
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presentquesno != 10){
                    if (presentquesno == 1){
                        prevButton.setVisibility(View.VISIBLE);
                    }
                    if (presentquesno == 9){
                        nextButton.setVisibility(View.INVISIBLE);
                    }
                    saveAnswerState(presentquesno, presentansstate, presentreviewstate);
                    presentquesno++;
                    changeQuestion(presentquesno);
                    loadAnswerState(presentquesno);
                }
            }
        });
        //If the question is unattempted, then it will highlight the answerstate and also save the answer state in local integer
        //If already attempted and clicked again on the same option then the answer is deselected
        //If already attempted and clicked on different option then answer state is changed.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (presentansstate == 0){
                    listview.getChildAt(i).setBackgroundColor(Color.RED);
                    presentansstate = i+1;
                }
                else {
                    if (presentansstate == i+1){
                        listview.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                        presentansstate = 0;
                    }
                    else {
                        listview.getChildAt(i).setBackgroundColor(Color.RED);
                        listview.getChildAt(presentansstate - 1).setBackgroundColor(Color.TRANSPARENT);
                        presentansstate = i+1;
                    }
                }
            }
        });
        //for the time being review button does a batch writing of all the answers to the database.
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presentreviewstate = 1 - presentreviewstate;
                Toast.makeText(MainActivity.this, "Review state:" + Integer.toString(presentreviewstate), Toast.LENGTH_SHORT).show();

            }
        });

    }
    
    public void changeQuestion(int questionnumber){
        questext.setText(quesSP.getString(Integer.toString(questionnumber) + ".q", null));
        ansstring[0] = quesSP.getString(Integer.toString(questionnumber) + ".a", null);
        ansstring[1] = quesSP.getString(Integer.toString(questionnumber) + ".b", null);
        ansstring[2] = quesSP.getString(Integer.toString(questionnumber) + ".c", null);
        ansstring[3] = quesSP.getString(Integer.toString(questionnumber) + ".d", null);
        adapter.notifyDataSetChanged();

    }

    public void displayFirstQuestion(){
        questext.setText(quesSP.getString("1.q",null));

        ansstring[0] = quesSP.getString("1.a",null);
        ansstring[1] = quesSP.getString("1.b",null);
        ansstring[2] = quesSP.getString("1.c",null);
        ansstring[3] = quesSP.getString("1.d",null);

        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, ansstring);
        listview.setAdapter(adapter);
    }


    public void saveAnswerState(int questionnumber, int answerstate, int reviewstate){
        SharedPreferences.Editor editor = ansSP.edit();
        editor.putInt(Integer.toString(questionnumber) + ".ans",answerstate );
        editor.putInt(Integer.toString(questionnumber) + ".seen",1 );
        editor.putInt(Integer.toString(questionnumber) + ".review", reviewstate);
        editor.apply();
    }
    public void loadAnswerState(int questionnumber){
        presentansstate = ansSP.getInt(Integer.toString(questionnumber)+ ".ans", 0);
        presentreviewstate = ansSP.getInt(Integer.toString(questionnumber)+".review",0);
        for (int i=1;i<=4;i++){
            listview.getChildAt(i-1).setBackgroundColor(Color.TRANSPARENT);
        }
        if (presentansstate != 0){
            listview.getChildAt(presentansstate - 1).setBackgroundColor(Color.RED);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.review_icon){
            saveAnswerState(presentquesno, presentansstate, presentreviewstate);
            Intent intent = new Intent(MainActivity.this, ReviewActivity.class);
            startActivityForResult(intent, 1);
        }
        if (id == R.id.submit){
            WriteBatch batch = db.batch();
            int size = ansSP.getAll().size()/3;
            for (int i = 1; i <= size ; i++){
                batch.update(db.collection("Answers").document(Integer.toString(i)),"ans", ansSP.getInt(Integer.toString(i)+".ans", 0));
                batch.update(db.collection("Answers").document(Integer.toString(i)),"seen", ansSP.getInt(Integer.toString(i)+".seen", 0));
                batch.update(db.collection("Answers").document(Integer.toString(i)),"review", ansSP.getInt(Integer.toString(i)+".review", 0));
            }
            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MainActivity.this, "Successfull writing!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            if( resultCode == RESULT_OK){
                presentquesno = data.getIntExtra("Question", 1);
                if (presentquesno != 1){
                    prevButton.setVisibility(View.VISIBLE);
                }
                else {
                    prevButton.setVisibility(View.INVISIBLE);
                }
                if (presentquesno != 10){
                    nextButton.setVisibility(View.VISIBLE);
                }
                else {
                    nextButton.setVisibility(View.INVISIBLE);
                }
                changeQuestion(presentquesno);
                loadAnswerState(presentquesno);
            }
        }

    }

    @Override
    public void onBackPressed() {

    }
}
