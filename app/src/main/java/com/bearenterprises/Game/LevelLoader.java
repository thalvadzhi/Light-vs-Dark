package com.bearenterprises.Game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.bearenterprises.EXTRAS.EXTRAS;
import com.bearenterprises.Entities.LightSeeker;
import com.bearenterprises.Entities.ShadowSeeker;
import com.bearenterprises.light.LightSource;
import com.bearenterprises.light.Point;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;


public class LevelLoader {
    private static ArrayList<LightSource> mlights;
    private static ArrayList<ArrayList<Point>> mobstacles;
    private static ArrayList<LightSeeker> mlightSeekers;
    private static ArrayList<ShadowSeeker> mshadowSeekers;
    private static ArrayList<LightsCoordinates> lightsCoordinates;
    private static ArrayList<ShadowSeekerCoordinates> shadowSeekerCoordinateses;
    private static ArrayList<LightSeekerCoordinates> lightSeekerCoordinateses;

    public static ArrayList<ShadowSeekerCoordinates> getShadowSeekerCoordinateses() {
        return shadowSeekerCoordinateses;
    }

    public static ArrayList<LightSeekerCoordinates> getLightSeekerCoordinateses() {
        return lightSeekerCoordinateses;
    }

    private static float logicalDensity;

    public static ArrayList<LightsCoordinates> getLightsCoordinates() {
        return lightsCoordinates;
    }

    public static ArrayList<ShadowSeeker> getMshadowSeekers() {
        return mshadowSeekers;
    }

    public static ArrayList<LightSeeker> getMlightSeekers() {
        return mlightSeekers;
    }

    public static ArrayList<ArrayList<Point>> getMobstacles() {
        return mobstacles;
    }

    public static ArrayList<LightSource> getMlights() {
        return mlights;
    }


    public static class LightsCoordinates{
        public double x, y;
        public LightsCoordinates(double x, double y){
            this.x = x;
            this.y = y;
        }
    }
    public static class ShadowSeekerCoordinates{
        public double x, y;
        public ShadowSeekerCoordinates(double x, double y){
            this.x = x;
            this.y = y;
        }
    }
    public static class LightSeekerCoordinates{
        public double x, y;
        public LightSeekerCoordinates(double x, double y){
            this.x = x;
            this.y = y;
        }
    }


    @SuppressLint("NewApi")
    public static void setLogicalDensity(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(metrics);
        logicalDensity = metrics.density;
    }

    private static Point convertToDependencyPoint(Point point){


        double x = point.getX() / logicalDensity;
        double y = point.getY() / logicalDensity;
        double angle = point.getAngle();
        double distance = point.getDistance();
        return new Point(x, y, angle, distance);
    }

    private static Point convertFromDependencyPoint(Point point){
        double x = point.getX() * logicalDensity;
        double y = point.getY() * logicalDensity;
        double angle = point.getAngle();
        double distance = point.getDistance();
        return new Point(x, y, angle, distance);
    }

    public static void saveLevelFile(Context ctx, ArrayList<LightSource> lights, ArrayList<ArrayList<Point>> obstacles, ArrayList<LightSeeker> lightSeekers,ArrayList<ShadowSeeker> shadowSeekers, Point controlPoint , String levelName ){
        setLogicalDensity(ctx);

        Gson gson = new Gson();
        ArrayList<LightsCoordinates> localLightsCoordinates = new ArrayList<>();
        for(LightSource light : lights){
            localLightsCoordinates.add(new LightsCoordinates(light.getX() / logicalDensity, light.getY() / logicalDensity));
        }

        ArrayList<ShadowSeekerCoordinates> localShadowSeekerCoordinates = new ArrayList<>();
        for(ShadowSeeker shadowSeeker : shadowSeekers){
            localShadowSeekerCoordinates.add(new ShadowSeekerCoordinates(shadowSeeker.getX() / logicalDensity, shadowSeeker.getY() / logicalDensity));
        }

        ArrayList<LightSeekerCoordinates> localLightSeekerCoordinates = new ArrayList<>();
        for(LightSeeker lightSeeker : lightSeekers){
            localLightSeekerCoordinates.add(new LightSeekerCoordinates(lightSeeker.getX() / logicalDensity, lightSeeker.getY() / logicalDensity));
        }
        ArrayList<ArrayList<Point>> dependencyObstacles = new ArrayList<>();
        for(ArrayList<Point> obstacle : obstacles){
            ArrayList<Point> dpObstacle = new ArrayList<>();
            for(Point point : obstacle){
                dpObstacle.add(convertToDependencyPoint(point));
            }
            dependencyObstacles.add(dpObstacle);
        }

        //String lightsJS = gson.toJson(lights);
        String lightsJS = gson.toJson(localLightsCoordinates);
        String obstaclesJS = gson.toJson(dependencyObstacles);
        String shadowSeeekersJS = gson.toJson(localShadowSeekerCoordinates);
        String lightSeekersJS = gson.toJson(localLightSeekerCoordinates);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput(levelName, Context.MODE_PRIVATE));
            outputStreamWriter.write(controlPoint.getX()+"");
            outputStreamWriter.write("\n");
            outputStreamWriter.write(lightsJS);
            outputStreamWriter.write("\n");
            outputStreamWriter.write(obstaclesJS);
            outputStreamWriter.write("\n");
            outputStreamWriter.write(shadowSeeekersJS);
            outputStreamWriter.write("\n");
            outputStreamWriter.write(lightSeekersJS);
            outputStreamWriter.write("\n");
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getWidthOfWindow(Context context){
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
        return width;
    }

    private static Point translatePoint(Point point, String controlPoint, int width){
        double control = Double.parseDouble(controlPoint);
        double translateBy = (double)width / control;
        return new Point(point.getX() - ((translateBy - 50) / 2) * logicalDensity, point.getY(), point.getAngle(), point.getDistance());

    }

    private static double translateX(double x, String controlPoint, int width){
        double control = Double.parseDouble(controlPoint);
        double translateBy = (double)width / control;
        return x - (((translateBy - 50)) / 2) * logicalDensity ;
    }

    public static void readLevelFile(Context ctx, String levelName) {
        setLogicalDensity(ctx);

        int width = getWidthOfWindow(ctx);

        String lightsJS = null;
        String obstaclesJS = null;
        String shadowSeekersJS = null;
        String lightSeekersJS = null;
        String control = null;
        Gson gson = new Gson();
        try {
            AssetManager manager = ctx.getResources().getAssets();


            InputStream inputStream = manager.open(levelName);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                control = bufferedReader.readLine();
                lightsJS = bufferedReader.readLine();
                obstaclesJS = bufferedReader.readLine();
                shadowSeekersJS = bufferedReader.readLine();
                lightSeekersJS = bufferedReader.readLine();

                inputStream.close();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Type lightsType = new TypeToken<ArrayList<LightsCoordinates>>(){}.getType();

        Type obstaclesType = new TypeToken<ArrayList<ArrayList<Point>>>(){}.getType();
        Type shadowsType = new TypeToken<ArrayList<ShadowSeekerCoordinates>>(){}.getType();
        Type lightsSeekerType = new TypeToken<ArrayList<LightSeekerCoordinates>>(){}.getType();




        ArrayList<LightsCoordinates> localLightsCoordinates = new ArrayList<>();
        ArrayList<LightSeekerCoordinates> localLightSeekerCoordinates = new ArrayList<>();
        ArrayList<ShadowSeekerCoordinates> localShadowSeekerCoordinates = new ArrayList<>();
        ArrayList<ArrayList<Point>> localObstacles = new ArrayList<>();
        localLightsCoordinates = gson.fromJson(lightsJS, lightsType);
        localShadowSeekerCoordinates = gson.fromJson(shadowSeekersJS, shadowsType);
        localLightSeekerCoordinates = gson.fromJson(lightSeekersJS, lightsSeekerType);
        localObstacles = gson.fromJson(obstaclesJS, obstaclesType);

        lightsCoordinates = new ArrayList<>();
        mobstacles = new ArrayList<>();
        mshadowSeekers = new ArrayList<>();
        mlightSeekers  = new ArrayList<>();
        for(ArrayList<Point> obstacle : localObstacles){
            ArrayList<Point> dpFreeObstacle = new ArrayList<>();
            for(Point point : obstacle){
                dpFreeObstacle.add(translatePoint(convertFromDependencyPoint(point), control, width));
            }
           mobstacles.add(dpFreeObstacle);
        }


        for(LightsCoordinates coord : localLightsCoordinates){
            lightsCoordinates.add(new LightsCoordinates(translateX(coord.x * logicalDensity, control, width), coord.y * logicalDensity));
        }

        for(ShadowSeekerCoordinates coord : localShadowSeekerCoordinates){
            mshadowSeekers.add(new ShadowSeeker(translateX(coord.x * logicalDensity, control, width), coord.y * logicalDensity));
        }

        for(LightSeekerCoordinates coord : localLightSeekerCoordinates){
            mlightSeekers.add(new LightSeeker(translateX(coord.x * logicalDensity, control, width), coord.y * logicalDensity));
        }

       // mshadowSeekers = gson.fromJson(shadowSeekersJS, shadowsType);
       // mlightSeekers = gson.fromJson(lightSeekersJS, lightsSeekerType);
    }



}
