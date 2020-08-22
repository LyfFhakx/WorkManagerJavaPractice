package com.example.workmanagerlesson;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BgSimple2 extends Worker {

   public final static String TAG = "WorkManager";

    public BgSimple2(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG,this.getClass().getSimpleName()+" doWork() started");
        for (int i =0; i< 5; ++i){
            try{
                Thread.sleep( 1000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            if(isStopped()){
                Log.d(TAG,this.getClass().getSimpleName()+" isStopped() == true");
                break;
            }
        }
        return Result.success();
    }

    @Override
    public void onStopped() {
        Log.d(TAG,this.getClass().getSimpleName()+" onStopped() started");
        super.onStopped();
    }
}
