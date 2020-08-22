package com.example.workmanagerlesson;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    UUID id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//               WorkManager.getInstance(view.getContext()).cancelAllWork();
                WorkManager.getInstance(view.getContext()).cancelAllWorkByTag("Super Tag");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_chain) {
            startChain();
            return true;
        } else if(id == R.id.action_simple_worker){
            startSimpleWork();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSimpleWork() {

        Constraints constraints =
                new Constraints
                        .Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .build();

        OneTimeWorkRequest request =
                new OneTimeWorkRequest
                        .Builder(BgSimple.class)
                        .addTag("Super Tag")
                        .addTag("tag1")
                        .setConstraints(constraints)
                        .build();
        id = request.getId();

        WorkManager
                .getInstance(this)
                .getWorkInfoByIdLiveData(id)
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.d(BgSimple.TAG, "onChanged:" + workInfo.getState());
                    }
                });

        WorkManager
                .getInstance(this)
                .enqueue(request);
    }

    private void startChain(){
        WorkManager manager = WorkManager.getInstance(this);

        OneTimeWorkRequest workA1 =
                new OneTimeWorkRequest
                        .Builder(BgSimple.class)
                        .addTag("Super Tag")
                        .addTag("tag1")
                        .build();
        WorkManager
                .getInstance(this)
                .getWorkInfoByIdLiveData(workA1.getId() )
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.d(BgSimple.TAG, "workA1:" + workInfo.getState());
                    }
                });

        OneTimeWorkRequest workA2 =
                new OneTimeWorkRequest
                        .Builder(BgSimple2.class)
                        .addTag("Super Tag")
                        .addTag("tag1")
                        .build();

        WorkManager
                .getInstance(this)
                .getWorkInfoByIdLiveData(workA2.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.d(BgSimple.TAG, "workA2 :" + workInfo.getState());
                    }
                });
        WorkContinuation chain1 = manager
                .beginWith(workA1)
                .then(workA2);

        chain1.enqueue();
    }
}

