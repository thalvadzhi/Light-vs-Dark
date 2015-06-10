package com.bearenterprises.Entities;

import android.util.Log;

import com.bearenterprises.Collision.Collision;
import com.bearenterprises.light.LightSource;
import com.bearenterprises.light.Point;

import java.util.ArrayList;
import java.util.Arrays;

public class ShadowSeeker extends Seeker {

    public ShadowSeeker(double x, double y){
       super(x, y);

    }

    public void rethinkHappiness(ArrayList<LightSource> lights){
        for(LightSource light : lights){
            if(Collision.any(Collision.collidePolygonPoints(this.entityApproximation, light.getVisibility()))){
               this.isHappy = false;
                return;
            }
        }
        this.isHappy = true;


    }

}
