package com.dewa.pam_tugas2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayMusic extends AppCompatActivity {

    ImageButton btnPlay, btnStop, btnNext, btnPrevious;
    TextView txtDisplayDuration, txtSongName;
    SeekBar playerControl;

    private double startTime = 0;
    private double finalTime = 0;

    private boolean isPlaying = false;

    private Handler syncHandler = new Handler();

    String songName;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        btnPlay = findViewById(R.id.btn_play);
        btnStop = findViewById(R.id.btn_stop);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);

        txtDisplayDuration = findViewById(R.id.display_duration);
        txtSongName = findViewById(R.id.titleSong);

        playerControl = findViewById(R.id.player_control);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList)bundle.getParcelableArrayList("songs");
        String sName = intent.getStringExtra("songname");
        position = bundle.getInt("pos", 0);
        txtSongName.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        songName = mySongs.get(position).getName();
        txtSongName.setText(songName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        //mediaPlayer.start(); jika ingin lagu diklik musik langsung mulai

        playerControl.setProgress(0);

        btnPlay.setOnClickListener(view -> {
            if (!isPlaying) {
                isPlaying = true;

                btnPlay.setImageResource(R.drawable.ic_pause);

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                txtDisplayDuration.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes((long) startTime)
                                )
                ));

                playerControl.setMax((int) finalTime);
                playerControl.setProgress((int) startTime);

                mediaPlayer.start();

                syncHandler.postDelayed(updateDuration, 100);
            }
            else {
                isPlaying = false;

                btnPlay.setImageResource(R.drawable.ic_play);
                mediaPlayer.pause();
            }
        });

        btnStop.setOnClickListener(view -> {
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
            playerControl.setProgress(0);
            btnPlay.setImageResource(R.drawable.ic_play);
            isPlaying = false;
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position + 1) % mySongs.size());

                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                songName = mySongs.get(position).getName().toString();
                txtSongName.setText(songName);

                if (mediaPlayer.isPlaying()) {
                    btnPlay.setImageResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                } else {
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position - 1) % mySongs.size());

                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                songName = mySongs.get(position).getName().toString();
                txtSongName.setText(songName);

                if (mediaPlayer.isPlaying()) {
                    btnPlay.setImageResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                } else {
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        playerControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private Runnable updateDuration = new Runnable() {
        @Override
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            txtDisplayDuration.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes((long) startTime)
                            )
            ));
            playerControl.setProgress((int) startTime);
            syncHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}