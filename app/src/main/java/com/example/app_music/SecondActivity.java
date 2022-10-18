package com.example.app_music;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.model.Song;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class SecondActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, View.OnClickListener, MediaPlayer.OnTimedTextListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener {
    private static int flag_music = 0;
    private static final String TAG = "TimedTextTest";
    private boolean flag_play = true;
    private boolean flag_random_music = false;
    private boolean flag_repeat_music = false;
    public static MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView tv3_txt, tv2_txt, tv4_txt;
    private LinearLayout linearLayout;
    private TabLayout tabLayout;
    private View lyrics_view;
    ImageButton imageButton, imageButton2, imageButton3, imageButton4, imageButton5, imageButton6;
    ImageView imageView;
    private Runnable runnable;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent = getIntent();
        handler = new Handler();
        mediaPlayer = new MediaPlayer();
        //get object
        tv2_txt = findViewById(R.id.textView2);
        tv3_txt = findViewById(R.id.textView3);
        seekBar = findViewById(R.id.seekBar);
        imageButton = findViewById(R.id.imageButton);
        imageButton2 = findViewById(R.id.imageButton2);
        imageButton3 = findViewById(R.id.imageButton3);
        imageButton4 = findViewById(R.id.imageButton4);
        imageButton5 = findViewById(R.id.imageButton5);
        imageButton6 = findViewById(R.id.imageButton6);
        imageView = findViewById(R.id.imageView);
        linearLayout = findViewById(R.id.linearLayout);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        showTabPlaying();
                        hintTabLyrics();
                        break;
                    case 1:
                        showTabLyrics();
                        hintTabPlaying();
                        try {
                            floatLyrics();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //get value from intent 1
        flag_music = intent.getIntExtra("index_music", 0);
        //solve
        tv2_txt.setText(MainActivity.list_music.get(flag_music).getSongName());
        imageButton.setImageResource(R.drawable.ic_baseline_pause_24);
        flag_play = false;
        try {
            playSong();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        imageButton.setOnClickListener(this);
        imageButton2.setOnClickListener(this);
        imageButton3.setOnClickListener(this);
        imageButton4.setOnClickListener(this);
        imageButton5.setOnClickListener(this);
        imageButton6.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    updateTextProgressSeekBar();
                    seekBar.setProgress(progress);
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


    private void clearMediaPlayer(MediaPlayer mp) {
        if (mp != null) {
            mp.release();// this will clear memory
        }
    }


    public void playSong() throws IOException {
        clearMediaPlayer(mediaPlayer);
        imageButton.setImageResource(R.drawable.ic_baseline_pause_24);
        mediaPlayer = MediaPlayer.create(this, Uri.parse(MainActivity.list_music.get(flag_music).getPathSong()));
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.start();
    }

    public void pauseMusic() {
        imageButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        mediaPlayer.pause();
    }

    public void playMusic() {
        imageButton.setImageResource(R.drawable.ic_baseline_pause_24);
        mediaPlayer.start();
    }

    public void nextMusic() {
        mediaPlayer.release();
        if (flag_music < MainActivity.list_music.size() - 1) {
            flag_music = flag_music + 1;
        } else {
            flag_music = 0;
        }

        imageButton.setImageResource(R.drawable.ic_baseline_pause_24);
        mediaPlayer = MediaPlayer.create(this, Uri.parse(MainActivity.list_music.get(flag_music).getPathSong()));
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.start();
    }

    public void previousMusic() {
        mediaPlayer.release();
        if (flag_music > 0 && flag_music <= MainActivity.list_music.size() - 1) {
            flag_music = flag_music - 1;
        } else {
            flag_music = MainActivity.list_music.size() - 1;
        }
        imageButton.setImageResource(R.drawable.ic_baseline_pause_24);
        mediaPlayer = MediaPlayer.create(this, Uri.parse(MainActivity.list_music.get(flag_music).getPathSong()));
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.start();

    }

    private void updateSeekbar() {
        int currPos = mediaPlayer.getCurrentPosition();
        updateTextProgressSeekBar();
        tv3_txt.setVisibility(View.VISIBLE);
        seekBar.setProgress(currPos);
        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekbar();
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    public String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

        buf
                .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));
        return buf.toString();
    }

    public void updateTextProgressSeekBar() {
        int val = (seekBar.getProgress() * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
        tv3_txt.setText(getTimeString(mediaPlayer.getCurrentPosition()));
        tv3_txt.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        clearMediaPlayer(mp);
        try {
            if (flag_random_music == true) {
                double random_double = Math.random() * (MainActivity.list_music.size() - 1 + 1) + 1;
                flag_music = (int) random_double;
            }
            if (flag_repeat_music == true) {
                playSong();
            } else {
                nextMusic();
                if (imageView.getVisibility() == View.VISIBLE) {
                    animRotateRight();
                }
                tv2_txt.setText(MainActivity.list_music.get(flag_music).getSongName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        seekBar.setMax(mp.getDuration());
        mp.start();
        updateSeekbar();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        double ratio = percent / 100.0;
        int bufferingLevel = (int) (mp.getDuration() * ratio);
        seekBar.setSecondaryProgress(bufferingLevel);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButton) {
            if (flag_play == false) {
                flag_play = true;
                pauseMusic();
            } else {
                flag_play = false;
                playMusic();
            }
        } else if (v.getId() == R.id.imageButton2) {
            if (flag_random_music == true) {
                double random_double = Math.random() * (MainActivity.list_music.size() - 1 + 1) + 1;
                flag_music = (int) random_double;
            }
//            if (flag_repeat_music == true) {
//                flag_repeat_music = false;
//                imageButton5.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
//                try {
//                    playSong();
//                    return;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
            nextMusic();
            if (imageView.getVisibility() == View.VISIBLE) {
                animRotateRight();
            }
            tv2_txt.setText(MainActivity.list_music.get(flag_music).getSongName());
        } else if (v.getId() == R.id.imageButton3) {
            if (flag_random_music == true) {
                double random_double = Math.random() * (MainActivity.list_music.size() - 1 + 1) + 1;
                flag_music = (int) random_double;
            }
            previousMusic();
            if (imageView.getVisibility() == View.VISIBLE) {
                animRotateLeft();
            }
            tv2_txt.setText(MainActivity.list_music.get(flag_music).getSongName());
        } else if (v.getId() == R.id.imageButton4) {
            if (flag_random_music == false) {
                flag_random_music = true;
                flag_repeat_music = false;
                imageButton4.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                imageButton5.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            } else {
                flag_random_music = false;
                imageButton4.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            }
        } else if (v.getId() == R.id.imageButton5) {
            if (flag_repeat_music == false) {
                flag_repeat_music = true;
                flag_random_music = false;
                imageButton5.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                imageButton4.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            } else {
                flag_repeat_music = false;
                imageButton5.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            }
        } else if (v.getId() == R.id.imageButton6) {
            Intent intent = new Intent(SecondActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

    public void hintTabPlaying() {
        imageView.setVisibility(View.INVISIBLE);
        tv2_txt.setVisibility(View.INVISIBLE);
    }

    public void showTabPlaying() {
        imageView.setVisibility(View.VISIBLE);
        tv2_txt.setVisibility(View.VISIBLE);
    }

    public void animRotateRight() {
        Animation animation =
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_to_right);
        imageView.startAnimation(animation);
    }

    public void animRotateLeft() {
        Animation animation =
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_to_left);
        imageView.startAnimation(animation);
    }

    public void showTabLyrics() {
        ConstraintLayout constraintLayout_second = findViewById(R.id.contraintLayout);
        lyrics_view = getLayoutInflater().inflate(R.layout.lyrics_view, null, true);
        lyrics_view.setX(220);
        lyrics_view.setY(450);
        constraintLayout_second.addView(lyrics_view);
    }

    public void hintTabLyrics() {
        lyrics_view.setVisibility(View.INVISIBLE);
    }

    public void floatLyrics() throws IOException {
        tv4_txt = lyrics_view.findViewById(R.id.textView4);
        mediaPlayer.addTimedTextSource(getSubtitleFile(R.raw.f_sub), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
        int textTrackIndex = findTrackIndexFor(
                MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mediaPlayer.getTrackInfo());
        if (textTrackIndex >= 0) {
            mediaPlayer.selectTrack(textTrackIndex);
            System.out.println(mediaPlayer);
        } else {
            Log.w(TAG, "Cannot find text track!");
        }
        mediaPlayer.setOnTimedTextListener(this);
    }

    private int findTrackIndexFor(int mediaTrackType, MediaPlayer.TrackInfo[] trackInfo) {
        int index = -1;
        for (int i = 0; i < trackInfo.length; i++) {
            if (trackInfo[i].getTrackType() == mediaTrackType) {
                return i;
            }
        }
        return index;
    }

    public String getSubtitleFile(int resId) {
        String fileName = getResources().getResourceEntryName(resId);
        File subtitleFile = getFileStreamPath(fileName);
        if (subtitleFile.exists()) {
            Log.d(TAG, "Subtitle already exists");
            return subtitleFile.getAbsolutePath();
        }
        Log.d(TAG, "Subtitle does not exists, copy it from res/raw");

        // Copy the file from the res/raw folder to your app folder on the
        // device
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = getResources().openRawResource(resId);
            outputStream = new FileOutputStream(subtitleFile, false);
            copyFile(inputStream, outputStream);
            return subtitleFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStreams(inputStream, outputStream);
        }
        return "";
    }

    private void copyFile(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = -1;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
    }

    // A handy method I use to close all the streams
    private void closeStreams(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable stream : closeables) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onTimedText(final MediaPlayer mp, final TimedText text) {
        if (text != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tv4_txt.setText(text.getText());
                }
            });
        }
    }

}
