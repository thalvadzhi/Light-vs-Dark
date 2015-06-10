package com.bearenterprises.Entities;

import android.util.Log;

import com.bearenterprises.Collision.Collision;
import com.bearenterprises.light.LightSource;
import com.bearenterprises.light.Point;

import java.util.ArrayList;
import java.util.Arrays;


public abstract class Seeker {
    protected double x, y, radius;
    protected boolean isHappy;
    protected ArrayList<Point> entityApproximation;

    public Seeker(double x, double y){
        this.radius = 15;
        this.x = x;
        this.y = y;
        this.isHappy = true;
        entityApproximation = new ArrayList<>();
        this.generateEntityApproximation();
    }

    private void generateEntityApproximation(){
        for (double i = 0; i < 360; i+=16){
            double x = this.x + this.radius * Math.cos(Math.toDegrees(i));
            double y = this.y + this.radius * Math.sin(Math.toDegrees(i));
            entityApproximation.add(new Point(x, y));
        }
    }

    public boolean isHappy() {
        return isHappy;
    }

    public abstract  void rethinkHappiness(ArrayList<LightSource> lights);

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }
}
