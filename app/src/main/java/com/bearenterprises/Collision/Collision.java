package com.bearenterprises.Collision;

import android.util.Log;

import com.bearenterprises.light.Line;
import com.bearenterprises.light.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

public class Collision {

    public static boolean collideCircleSegments(ArrayList<Line> segments, double x, double y, double radius){
        // collide circle with segments
        for(Line segment : segments){
            double x1 = segment.getPoint1().getX();
            double x2 = segment.getPoint2().getX();
            double y1 = segment.getPoint1().getY();
            double y2 = segment.getPoint2().getY();

            Vector2D d = new Vector2D(x2-x1, y2-y1);
            Vector2D f = new Vector2D(x1-x,y1-y);
            double a = (d.getX()) * (d.getX()) + (d.getY()) * (d.getY());
            double b = 2 * ((f.getX() * d.getX()) + (f.getY() * d.getY()));
            double c = (f.getX() * f.getX()) + (f.getY() * f.getY()) - (radius * radius);
            double delta = b * b  - (4 * a * c);

            if (delta >= 0){


                delta = Math.sqrt(delta);
                double t1 = (-b - delta)/(2*a);
                double t2 = (-b + delta)/(2*a);
                if( t1 >= 0 && t1 <= 1 ){
                    return true;
                }
                if( t2 >= 0 && t2 <= 1 )
                {

                    return true ;
                }


            }
        }
        return false;
    }

    public static boolean all (ArrayList<Boolean> allPoints){
        for(Boolean isInside : allPoints){
            if(!isInside){
                return false;
            }
        }
        return true;
    }

    public static boolean any (ArrayList<Boolean> allPoints){
        for(Boolean isInside : allPoints){
            if(isInside){
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Boolean> collidePolygonPoints(ArrayList<Point> entity, ArrayList<Point> visibility){
        int visibilitySize = visibility.size();
        ArrayList<Boolean> allPoints = new ArrayList<>(entity.size());
        for(Point point : entity){
            double playerX = point.getX();
            double playerY = point.getY();

            boolean isInside = false;
            double point1X = visibility.get(0).getX();
            double point1Y = visibility.get(0).getY();

            for(int i = 0; i < visibilitySize + 1; i++){
                double intersection = 0;
                double point2X = visibility.get(i % visibilitySize).getX();
                double point2Y = visibility.get(i % visibilitySize).getY();

                if (playerY > Math.min(point1Y, point2Y)){
                    if (playerY <= Math.max(point1Y, point2Y)){
                        if(playerX <= Math.max(point1X, point2X)){
                            if(point1Y != point2Y){
                                intersection = (playerY - point1Y) * (point2X - point1X) / (point2Y - point1Y) + point1X;
                            }
                            if(point1X == point2X || playerX <= intersection){
                                isInside = !isInside;
                            }
                        }
                    }
                }
                point1X = point2X;
                point1Y = point2Y;
            }
            allPoints.add(isInside);
        }
        return allPoints;
    }





}
