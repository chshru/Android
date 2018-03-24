package com.chshru.music.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.chshru.music.datautil.Music;
import com.chshru.music.R;

import java.util.List;


/**
 * Created by chshru on 2017/2/17.
 */
public class ListAdapter extends BaseAdapter {

    private List<Music> lists;
    private Context context;

    public ListAdapter(List<Music> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_music,null);
            viewHolder = new ViewHolder();
            viewHolder.musicName = (TextView) convertView.findViewById(R.id.musicname);
            viewHolder.musicName.setText(lists.get(position).getName());
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.musicName.setText(lists.get(position).getName());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView musicName;
    }
}