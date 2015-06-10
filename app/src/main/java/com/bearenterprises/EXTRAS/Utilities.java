package com.bearenterprises.EXTRAS;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;

public class Utilities {

    private static MediaPlayer mainPlayer;
    public static ArrayList<String> getAllLevels(Context context)  {
        AssetManager levels = context.getAssets();
        String[] allAssets = null;
        try {
            allAssets = levels.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> allLevels = new ArrayList<>();
        for (String asset : allAssets){
            if(asset.startsWith("level")){
                allLevels.add(asset);
            }
        }
        return allLevels;
    }

    public static void setMainPlayer(MediaPlayer player){
        mainPlayer = player;
    }

    public static MediaPlayer getMainPlayer(){
        return mainPlayer;
    }
}
