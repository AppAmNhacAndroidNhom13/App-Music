package com.example.app_music;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.model.Song;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Song> list_music = new ArrayList<Song>();
    CustomAdapterSong customAdapterSong = null;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private Button btn_Huy, btn_ThemBaiHat;
    private EditText edit_tenbaihat, edit_tencasi, edit_duongdanbaihat;
    private String host;
    private int requestcode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Đọc dữ liệu
        getData();
        //Tải dữ liệu lên custom adapter
        customAdapterSong = new CustomAdapterSong(this
                , R.layout.my_list_song, list_music);
        ListView lv = findViewById(R.id.ListView1);
        lv.setAdapter(customAdapterSong);

        toolbar = findViewById(R.id.topAppBar);
        toolbar.inflateMenu(R.menu.app_bar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.add_song:
                        Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.layout_dialog_add_song);
                        edit_duongdanbaihat = dialog.findViewById(R.id.edit_duongdanbaihat);
                        edit_tenbaihat = dialog.findViewById(R.id.edit_tenbaihat);
                        edit_tencasi = dialog.findViewById(R.id.edit_tencasi);
                        btn_Huy = dialog.findViewById(R.id.btn_Huy);
                        btn_ThemBaiHat = dialog.findViewById(R.id.btn_ThemBaiHat);
                        btn_Huy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        btn_ThemBaiHat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DBHelperSong db = new DBHelperSong(MainActivity.this);
                                String name_song = edit_tenbaihat.getText().toString();
                                String name_artist = edit_tencasi.getText().toString();
                                String path_song = edit_duongdanbaihat.getText().toString();
                                String res = db.addRecord(name_song, name_artist, path_song);
                                Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
                                getData();
                                lv.setAdapter(customAdapterSong);
                                dialog.dismiss();
                            }
                        });
                        edit_duongdanbaihat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                OpenFileChooser();
                            }
                        });
                        dialog.show();
                        return true;
                }
                return false;
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.page_1:
                        int index_music = (int) lv.getItemIdAtPosition(0);
                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                        intent.putExtra("index_music", index_music);
                        if (SecondActivity.mediaPlayer != null) {
                            SecondActivity.mediaPlayer.stop();
                            SecondActivity.mediaPlayer.release();
                        };
                        startActivity(intent);
                        return true;
                    case R.id.page_2:
                        System.out.println("favorite");
                        return true;
                    case R.id.page_3:
                        System.out.println("album");
                        return true;
                    case R.id.page_4:
                        System.out.println("account");
                        return true;
                }
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int index_music = (int) lv.getItemIdAtPosition(position);
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("index_music", index_music);
                if (SecondActivity.mediaPlayer != null) {
                    SecondActivity.mediaPlayer.stop();
                    SecondActivity.mediaPlayer.release();
                }
                startActivity(intent);

            }
        });
    }

    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        super.onActivityResult(requestcode, resultcode, data);
        if (requestcode == requestcode && resultcode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            String pathSong_sdcard = uri.getLastPathSegment().split(":")[1];
            String pathSong = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + pathSong_sdcard;
            String tmp[] = pathSong.split("/");
            String pathSDcard = "/" + tmp[0] + tmp[1] + "/" + tmp[2] + "/";
            if (pathSDcard.trim().equals("/storage/sdcard/")) {
                edit_duongdanbaihat.setText(pathSong);
                Toast.makeText(this, "Chọn file thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hãy lựa chọn file audio nằm trong mục sdcard", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public void OpenFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        startActivityForResult(intent, requestcode);
    }

    public void getData() {
        DBHelperSong db = new DBHelperSong(this);
        Cursor res = db.getData();
        list_music.clear();
        if(res.getCount()==0) {
            Toast.makeText(this, "Không đọc được dữ liệu bài hát", Toast.LENGTH_SHORT).show();
            return;
        }
        while(res.moveToNext()){
            Song song = new Song();
            song.setSongName(res.getString(1));
            song.setArtistName(res.getString(2));
            song.setPathSong(res.getString(3));
            list_music.add(song);
        }
    }

}