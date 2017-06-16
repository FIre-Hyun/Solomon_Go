package com.gachon.kimhyun.solomon_go;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kimhyun on 2017. 6. 4..
 */

public class NearMember {

    private Drawable iconDrawable ;
    private String idStr;
    private String nameStr ;
    private String ageStr ;

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setId(String id) {
        idStr = id ;
    }
    public void setName(String name) {
        nameStr = name ;
    }
    public void setAge(String age) {
        ageStr = age ;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getId() {
        return this.idStr ;
    }
    public String getName() {
        return this.nameStr ;
    }
    public String getAge() {
        return this.ageStr ;
    }
}


class NearMemberAdapter extends BaseAdapter {

    private ArrayList<NearMember> NearMemberItemList = new ArrayList<NearMember>() ;

    public NearMemberAdapter() {

    }

    @Override
    public int getCount() {
        return NearMemberItemList.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list, parent, false);
        }

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView_list_picture) ;
        TextView idTextView = (TextView) convertView.findViewById(R.id.textView_list_id) ;
        TextView nameTextView = (TextView) convertView.findViewById(R.id.textView_list_name) ;
        TextView ageTextView = (TextView) convertView.findViewById(R.id.textView_list_age) ;

        NearMember listViewItem = NearMemberItemList.get(position);

        iconImageView.setImageDrawable(listViewItem.getIcon());
        idTextView.setText(listViewItem.getId());
        nameTextView.setText(listViewItem.getName());
        ageTextView.setText(listViewItem.getAge());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return NearMemberItemList.get(position) ;
    }

    public void addItem(Drawable icon, String id, String name, String age) {
        NearMember item = new NearMember();

        item.setId(id);
        item.setIcon(icon);
        item.setName(name);
        item.setAge(age);

        NearMemberItemList.add(item);
    }
}
