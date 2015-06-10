package com.bearenterprises.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bearenterprises.EXTRAS.EXTRAS;
import com.bearenterprises.shadowseeker.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LevelListAdapter extends BaseAdapter {

    private ArrayList<String> levels;
    private Context context;

    public LevelListAdapter(Context context, ArrayList<String> levels){
        this.levels = levels;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.levels.size();
    }

    @Override
    public String getItem(int position) {
        return this.levels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(context);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.level_list_item, parent, false);

            holder = new ViewHolder();
            holder.levelNameView = (TextView) convertView.findViewById(R.id.textView_level_name);
            holder.levelPicture = (ImageView) convertView.findViewById(R.id.imageView_level_preview);
            holder.isPassedPicture = (ImageView) convertView.findViewById(R.id.imageView_level_passed);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        SharedPreferences passedLevel = this.context.getSharedPreferences(EXTRAS.RESULT_FILE, Context.MODE_PRIVATE);

        String level = getItem(position);
        boolean isPassed = passedLevel.getBoolean(level, false);

        Character levelNumber = level.charAt(level.length() - 1);

        holder.levelNameView.setText("Level " + levelNumber.toString());

        if(isPassed){
            Drawable drawable = parent.getResources().getDrawable(R.drawable.checkmark);
            holder.isPassedPicture.setImageDrawable(drawable);
        }
        AssetManager manager = this.context.getResources().getAssets();
        try {
            InputStream stream = manager.open("previews/"+level+"_preview.png");
            Drawable drawable = Drawable.createFromStream(stream, null);
            holder.levelPicture.setImageDrawable(drawable);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return convertView;
    }

    static class ViewHolder {
        ImageView levelPicture;
        ImageView isPassedPicture;
        TextView levelNameView;

    }
}
