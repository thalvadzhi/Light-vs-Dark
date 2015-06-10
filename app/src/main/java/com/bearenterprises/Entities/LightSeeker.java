package com.bearenterprises.Entities;


import android.util.Log;

import com.bearenterprises.Collision.Collision;
import com.bearenterprises.light.LightSource;
import com.bearenterprises.light.Point;

import java.util.ArrayList;
import java.util.Arrays;

public class LightSeeker extends Seeker {

    public LightSeeker(double x, double y){
        super(x, y);

    }

    @Override
    public void rethinkHappiness(ArrayList<LightSource> lights) {
        for(LightSource light : lights){
            if(Collision.all(Collision.collidePolygonPoints(this.entityApproximation, light.getVisibility()))){
               this.isHappy = true;
               return;
            }
        }
        this.isHappy = false;

    }
}
