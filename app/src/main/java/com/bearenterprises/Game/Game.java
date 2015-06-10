package com.bearenterprises.Game;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import com.bearenterprises.Collision.Collision;
import com.bearenterprises.EXTRAS.EXTRAS;
import com.bearenterprises.EXTRAS.Utilities;
import com.bearenterprises.Entities.LightSeeker;
import com.bearenterprises.Entities.ShadowSeeker;
import com.bearenterprises.light.LightSource;
import com.bearenterprises.light.Line;
import com.bearenterprises.light.Point;
import com.bearenterprises.shadowseeker.MenuActivity;
import com.bearenterprises.shadowseeker.R;

import java.util.ArrayList;
import java.util.logging.Level;

public class Game {
    private ArrayList<LightSource> lights;

    private ArrayList<ArrayList<Point>> obstacles;
    private ArrayList<LightSeeker> lightSeekers;
    private ArrayList<ShadowSeeker> shadowSeekers;
    private ArrayList<Path> paths;
    private ArrayList<Paint> visibilityPaints;
    private ArrayList<Line> segments;
    private boolean gameEnded;
    private int[] colors;
    private String levelName;
    private Context context;

    public Game(Context context, String levelName){
        this.loadLevel(context, levelName);
        this.levelName = levelName;
        this.context = context;
    }


    private void getSegments(){
        this.segments = this.lights.get(0).getSegments();
    }
    private void loadLevel(Context context, String levelName){
        LevelLoader.readLevelFile(context, levelName);

        this.gameEnded = false;

        this.lights = new ArrayList<>();

        this.obstacles = LevelLoader.getMobstacles();
        this.addScreenSizeToObstacles(context);


        ArrayList<LevelLoader.LightsCoordinates> lightsCoordinateses = LevelLoader.getLightsCoordinates();
        this.generateLights(lightsCoordinateses);



        this.lightSeekers = LevelLoader.getMlightSeekers();
        this.shadowSeekers = LevelLoader.getMshadowSeekers();
        this.colors = new int[] { 0x64FFFFFF, 0xFF323232 };
        this.initPaths();
        this.initVisibilityPaints();
        this.setInitialHappiness();
        this.getSegments();

    }


    private boolean checkForEndGameConditions(){
        for(ShadowSeeker shadowSeeker : this.shadowSeekers){
            if(!shadowSeeker.isHappy()){
                return false;
            }
        }
        for(LightSeeker lightSeeker : this.lightSeekers){
            if(!lightSeeker.isHappy()){
                return false;
            }
        }
        return true;
    }


    private void generateLights(ArrayList<LevelLoader.LightsCoordinates> lightsCoordinateses){
        for(LevelLoader.LightsCoordinates coordinates : lightsCoordinateses){
            this.lights.add(new LightSource(coordinates.x, coordinates.y, this.obstacles));
        }
    }

    @SuppressLint("NewApi")
    private void addScreenSizeToObstacles(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        android.graphics.Point point = new android.graphics.Point();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN){
            display.getRealSize(point);
        } else{
            display.getSize(point);
        }
        int width = point.x;
        int height = point.y;

        ArrayList<Point> obstacle = new ArrayList<>(4);
        obstacle.add(new Point(0, 0));
        obstacle.add(new Point(width ,0 ));
        obstacle.add(new Point(width, height));
        obstacle.add(new Point(0, height));
        this.obstacles.add(obstacle);
    }

    private void setInitialHappiness(){
        for(LightSeeker lightSeeker : this.lightSeekers){
            lightSeeker.rethinkHappiness(this.lights);
        }
        for(ShadowSeeker shadowSeeker : this.shadowSeekers){
            shadowSeeker.rethinkHappiness(this.lights);
        }
    }

    private void initVisibilityPaints(){
        this.visibilityPaints = new ArrayList<>(this.lights.size());
        for(int i = 0; i < this.lights.size(); i++){
            Paint paint = new Paint();
            double x = this.lights.get(i).getX();
            double y = this.lights.get(i).getY();
            double range = this.lights.get(i).getRange();
            paint.setShader(new RadialGradient((float)x, (float)y, (float)range, colors, null, Shader.TileMode.CLAMP));
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            paint.setAlpha(60);
            this.visibilityPaints.add(paint);
        }

    }

    private void playWinningSound(Context context){
        MediaPlayer player = Utilities.getMainPlayer();
        player.setVolume(0.2f, 0.2f);
        MediaPlayer tadaPlayer = MediaPlayer.create(context, R.raw.tada);
        tadaPlayer.start();
        while (tadaPlayer.isPlaying());
        tadaPlayer.stop();
        player.setVolume(1, 1);
    }

    private void initPaths(){
        this.paths = new ArrayList<>(this.lights.size());
        for(int i = 0; i < this.lights.size(); i++){
            this.paths.add(new Path());
        }
        for(int i = 0; i < this.lights.size(); i++){
            this.recalculatePath(this.lights.get(i).getVisibility(), i);
        }
    }

    private void updateVisibilityPaints(int position){
        double x = this.lights.get(position).getX();
        double y = this.lights.get(position).getY();
        double range = this.lights.get(position).getRange();
        this.visibilityPaints.get(position).setShader(new RadialGradient((float)x, (float)y, (float)range, colors, null, Shader.TileMode.CLAMP));
        this.visibilityPaints.get(position).setDither(true);
        //TODO adjust alpha value
        this.visibilityPaints.get(position).setAlpha(60);
    }

    public int selectLight(double x, double y){
        //-1 if touch not inside any light
        Point touch = new Point(x, y);
        for(int i = 0; i < this.lights.size(); i++){
            double x1 = this.lights.get(i).getX();
            double y1 = this.lights.get(i).getY();
            double radius = this.lights.get(i).getRadius();
            Point center = new Point(x1, y1);


            if (distanceBetween(touch, center) <= radius * 2){
                return i;
            }
        }
        return -1;
    }

    private double distanceBetween(Point first, Point second){
        double x = first.getX();
        double y = first.getY();
        double x1 = second.getX();
        double y1 = second.getY();
        return Math.sqrt(Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2));
    }

    private void recalculatePath(ArrayList<Point> visibility, int position){
        Path path = this.paths.get(position);
        path.rewind();
        path.moveTo((float) visibility.get(0).getX(), (float) visibility.get(0).getY());
        for (Point point : visibility) {
            path.lineTo((float) point.getX(), (float) point.getY());
        }

    }

    private void writeLevelPassed(){
        SharedPreferences passedLevel = this.context.getSharedPreferences(EXTRAS.RESULT_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = passedLevel.edit();
        editor.putBoolean(levelName, true);
        editor.commit();
    }




    public void update(double dx, double dy, int position){
        if(gameEnded) return;

        double x = this.lights.get(position).getX();
        double y = this.lights.get(position).getY();
        double radius = this.lights.get(position).getRadius();

        if(Collision.collideCircleSegments(this.segments, x + dx, y + dy, radius)) return;


        this.lights.get(position).move(dx, dy);
        ArrayList<Point> visibility = this.lights.get(position).getVisibility();

        for(LightSeeker lightSeeker : this.lightSeekers){
            lightSeeker.rethinkHappiness(this.lights);
        }
        for(ShadowSeeker shadowSeeker : this.shadowSeekers){
            shadowSeeker.rethinkHappiness(this.lights);
        }
        this.recalculatePath(visibility, position);
        this.updateVisibilityPaints(position);
        gameEnded = this.checkForEndGameConditions();

        if(gameEnded){

            new playWinningSound(this.context).start();
            writeLevelPassed();
        }
    }

    public ArrayList<Path> getPaths() {
        return paths;
    }

    public ArrayList<ArrayList<Point>> getObstacles() {
        return obstacles;
    }

    public ArrayList<LightSeeker> getLightSeekers() {
        return lightSeekers;
    }

    public ArrayList<ShadowSeeker> getShadowSeekers() {
        return shadowSeekers;
    }

    public ArrayList<LightSource> getLights() {
        return lights;
    }

    public ArrayList<Paint> getVisibilityPaints() {
        return visibilityPaints;
    }

    public boolean hasEnded() {
        return gameEnded;
    }

    private class playWinningSound extends Thread{
        Context context;
        public playWinningSound(Context context){
            this.context = context;
        }

        @Override
        public void run(){
            Game.this.playWinningSound(this.context);
            Activity activity = (Activity) this.context;
            activity.getFragmentManager().popBackStack();
        }
    }

}
