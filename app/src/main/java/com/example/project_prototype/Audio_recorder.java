package com.example.project_prototype;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Audio_recorder extends AppCompatActivity {

    private int PERMISSION_CODE = 21 ;  //random individual number
    private String recordPermission = Manifest.permission.RECORD_AUDIO;

    private Context context;
    //private Activity activity;

    private MediaRecorder myAudioRecorder;

    private String outputFile = null;


    private ImageButton recorder_button;
    private TextView textView4;

    private boolean isRecording = false;

    private Chronometer timer;

    private Button for8K;
    private Button for16K;
    private Button for44K;

    private int samplerate = 8000;
    private String samplerate_show = "8000\n";

    private TextView see_time_start;
    private TextView see_time_end;

    private String outputFile_timeinfo = null;
    private FileWriter time_imformation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        context = this;
        //activity = this;

        recorder_button = (ImageButton) findViewById(R.id.recorder_button);
        textView4 = (TextView) findViewById(R.id.textView4);

        see_time_start = (TextView) findViewById(R.id.see_time_start);
        see_time_end = (TextView) findViewById(R.id.see_time_end);

        timer = (Chronometer) findViewById(R.id.record_timer2);
        for8K = (Button) findViewById((R.id.for8k2)) ;
        for16K = (Button) findViewById((R.id.for16k2)) ;
        for44K = (Button) findViewById((R.id.for44_1k2)) ;

        recorder_button.setOnClickListener(click);
        for8K.setOnClickListener(click);
        for16K.setOnClickListener(click);
        for44K.setOnClickListener(click);


        askForPermissions();
        checkPermission();

    }

    private View.OnClickListener click = new View.OnClickListener(){
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.recorder_button:
                    if(isRecording){

                        stopRecording();
                        isRecording = false;
                    }
                    else{
                        record();
                        isRecording = true;
                    }
                    break;
                case R.id.for8k2:
                    if(isRecording == false) {
                        //can change the frame rate
                        samplerate = 8000; //default
                        samplerate_show = "8000\n";
                        textView4.setText("Select 8KHZ");
                    }
                    break;
                case R.id.for16k2:
                    if(isRecording == false) {
                        //can change the frame rate
                        samplerate = 16000; //default
                        samplerate_show = "16000\n";
                        textView4.setText("Select 16KHZ");
                    }
                    break;
                case R.id.for44_1k2:
                    if(isRecording == false) {
                        //can change the frame rate
                        samplerate = 44100; //default
                        samplerate_show = "44100\n";
                        textView4.setText("Select 44.1KHZ");
                    }
                    break;
            }
        }
    };
    private void record() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.TAIWAN);
        Date now = new Date();

        File audio = new File(getExternalFilesDir("/").getAbsolutePath(), "Audio");
        if (!audio.exists()) {
            audio.mkdirs();
        }

        outputFile = getExternalFilesDir("/Audio").getAbsolutePath() + "/" + formatter.format(now) + ".3gp";
        //the file name
        outputFile_timeinfo = "audio_time_info" + formatter.format(now) + ".txt";
        //outputFile_timeinfo = getExternalFilesDir("/").getAbsolutePath() + "/" + formatter.format(now) + ".txt";


        try {
            File root = new File(getExternalFilesDir("/").getAbsolutePath(), "Audio_time_info");
            if (!root.exists()) {
                root.mkdirs();
            }
            File filepath = new File(root, outputFile_timeinfo);
            time_imformation = new FileWriter(filepath);

            //Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }





        //outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + formatter.format(now) + ".3gp";
        //outputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + formatter.format(now) + ".3gp";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setOutputFile(outputFile);
        //Toast.makeText(context, outputFile, Toast.LENGTH_SHORT).show();

        if(samplerate == 16000){
            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            myAudioRecorder.setAudioSamplingRate(samplerate);
        }
        else if(samplerate == 44100){
            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            myAudioRecorder.setAudioEncodingBitRate(384000);
            myAudioRecorder.setAudioSamplingRate(samplerate);
        }
        else{
            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            myAudioRecorder.setAudioSamplingRate(8000);

        }

        try {
            myAudioRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        SimpleDateFormat formatter_start = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS", Locale.TAIWAN);

        try {
            Date now_start = new Date();
            see_time_start.setText(formatter_start.format(now_start));

            try {
                //time_imformation.append(sBody);
                time_imformation.write(formatter_start.format(now_start)+"\n");
                time_imformation.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            myAudioRecorder.start();

        } catch(IllegalStateException e){
            e.printStackTrace();
        }



        isRecording = true;
        textView4.setText(samplerate_show + "recording");
    }

    private void stopRecording() {

        if(myAudioRecorder != null) {
            timer.stop();
            SimpleDateFormat formatter_end = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS", Locale.TAIWAN);
            try {
                myAudioRecorder.stop();
                Date now_end = new Date();
                see_time_end.setText(formatter_end.format(now_end));

                try {
                    time_imformation.write(formatter_end.format(now_end) + "\n");
                    time_imformation.flush();
                    time_imformation.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IllegalStateException e) {
                // handle cleanup here
                e.printStackTrace();
            }
            //myAudioRecorder.reset();
            myAudioRecorder.release();
            myAudioRecorder = null;
            textView4.setText("stop! SAVE IN " + outputFile);
        }

    }




    private void askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                //no permission than go to permission manager to change
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, 200);
                    return;
                }
            }
        }
    }
}