package com.bearenterprises.shadowseeker;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import com.bearenterprises.EXTRAS.EXTRAS;
import com.bearenterprises.EXTRAS.Utilities;
import com.bearenterprises.Game.Game;

import java.io.IOException;
import java.util.ArrayList;


public class MenuActivity extends Activity implements ButtonFragment.OnButtonClickedListener,
        LevelFragment.onLevelListInteraction{

    ArrayList<String> levels;
    private MediaPlayer mediaPlayer;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }
    public void setMediaPlayerVolume(float volume){
        mediaPlayer.setVolume(volume, volume);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setButtonFragment();
        levels = Utilities.getAllLevels(this);
        playMusic(R.raw.lightvsdark);
        Utilities.setMainPlayer(this.mediaPlayer);
    }

    private void setButtonFragment(){
        ButtonFragment buttonFragment = new ButtonFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, buttonFragment)
                .commit();
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            finish();
        }
    }
    @Override
    public void onPlayButtonClicked(String button) {
        LevelFragment levelFragment = new LevelFragment();
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right, R.animator.slide_out_left, R.animator.slide_in_right)
                .replace(R.id.content_frame, levelFragment)
                .addToBackStack(button).commit();
    }

    @Override
    public void onExitButtonClicked(String button) {
        finish();
    }

    @Override
    public void onLevelClicked(int position) {
        GameFragment gameFragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(EXTRAS.GET_LEVEL_NAME, this.levels.get(position));
        gameFragment.setArguments(args);
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right, R.animator.slide_out_left, R.animator.slide_in_right)
                .replace(R.id.content_frame, gameFragment)
                .addToBackStack(position+"").commit();
    }

    private void playMusic(int id){
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = MediaPlayer.create(this, id);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

    }

}
