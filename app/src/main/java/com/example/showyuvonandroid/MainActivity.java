package com.example.showyuvonandroid;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Handler handler = new Handler();
    Timer timer;
    TimerTask timerTask;

    ActivityResultLauncher<Intent> filePickerStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Uri uri = intent.getData();
                        Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_SHORT).show();
                        ShowYUVView view = findViewById(R.id.view);
                        view.setYUVFileURL(getContentResolver(), uri);
                        view.invalidate();
                        findViewById(R.id.play_button).setEnabled(true);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.play_button).setOnClickListener(this);
        findViewById(R.id.file_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.file_button) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/octet-stream");
            filePickerStartForResult.launch(intent);
        }
        else if (view.getId() == R.id.play_button) {
            timerTask = new ShowYUVTimerTask();
            timer = new Timer(true);
            long period = getResources().getInteger(R.integer.period);
            timer.schedule(timerTask, period, period);
            view.setEnabled(false);
            findViewById(R.id.file_button).setEnabled(false);
        }
    }

    protected class ShowYUVTimerTask extends TimerTask {

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ShowYUVView view = findViewById(R.id.view);
                    if (view.isAvailable()) {
                        findViewById(R.id.view).invalidate();
                    }
                    else {
                        timer.cancel();
                        findViewById(R.id.file_button).setEnabled(true);
                    }
                }
            });
        }
    }
}
