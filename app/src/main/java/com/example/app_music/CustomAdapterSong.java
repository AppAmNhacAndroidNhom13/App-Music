package com.example.app_music;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.model.Song;

import java.util.List;

public class CustomAdapterSong extends ArrayAdapter<Song> {
    private TextView textView5,textView8;
    public CustomAdapterSong(@NonNull Context context, int resource, @NonNull List<Song> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        //inflate layout
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.my_list_song, parent, false); //với layout_my_list_item là cái sườn để ráp dô listview
        }
        //view holder
        textView5 = view.findViewById(R.id.textView5);
        textView8 = view.findViewById(R.id.textView8);
        if (view != null) {
            textView5.setText(MainActivity.list_music.get(position).getSongName());
            textView8.setText(MainActivity.list_music.get(position).getArtistName());
        }
        return view;
    }
}
