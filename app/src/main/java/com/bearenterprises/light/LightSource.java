package com.bearenterprises.light;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Teodor on 07/01/15.
 */
public class LightSource {
    private double x, y;
    private ArrayList<ArrayList<Point>> obstacles;
    private ArrayList<Point> points;
    private double radius;
    private double range;
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public ArrayList<Line> getSegments() {

        return segments;
    }

    private ArrayList<Line> segments;
    private ArrayList<Point> visibility;

    public double getRadius() {
        return this.radius;
    }

    public double getRange() {
        return range;
    }

    public LightSource(double x, double y, ArrayList<ArrayList<Point>> obstacles){
        this.x = x;
        this.y = y;
        this.radius = 25.0;
        this.range = 750;

        this.obstacles = new ArrayList<>(obstacles);
        this.segments = new ArrayList<>();
        this.points = new ArrayList<>();
        this.generateAllSegments();
        this.uniquify();
        this.visibility = castLight();
    }

    private ArrayList<Line> generateSegments(ArrayList<Point> obstacle){
        ArrayList<Line> segments = new ArrayList<>();
        int i = 0;
        while(i < obstacle.size() - 1){
            Point point1 = obstacle.get(i);
            Point point2 = obstacle.get(i + 1);
            segments.add(new Line(point1, point2));
            i++;
        }
        segments.add(new Line(obstacle.get(0), obstacle.get(obstacle.size() - 1)));
        return segments;
    }

    private void generateAllSegments(){
        for (ArrayList<Point> obstacle : this.obstacles){
            for (Line segment : this.generateSegments(obstacle)){
                this.segments.add(segment);
            }
        }
    }

    private Point getIntersection(Line ray, Line segment){
        //ray in parametric
        double r_px = ray.getPoint1().getX();
        double r_py = ray.getPoint1().getY();
        double r_dx = ray.getPoint2().getX() - ray.getPoint1().getX();
        double r_dy = ray.getPoint2().getY() - ray.getPoint1().getY();

        //segment in parametric
        double s_px = segment.getPoint1().getX();
        double s_py = segment.getPoint1().getY();
        double s_dx = segment.getPoint2().getX() - segment.getPoint1().getX();
        double s_dy = segment.getPoint2().getY() - segment.getPoint1().getY();

        double r_mag = Math.sqrt(r_dx * r_dx + r_dy * r_dy);
        double s_mag = Math.sqrt(s_dx * s_dx + s_dy * s_dy);

        double T_ONE, T_TWO;
        try{
            if (r_dx / r_mag == s_dx / s_mag && r_dy / r_mag == s_dy / s_mag){
                return null;
            }
            T_TWO = (r_dx*(s_py-r_py) + r_dy*(r_px-s_px))/(s_dx*r_dy - s_dy*r_dx);
            T_ONE = (s_px+s_dx*T_TWO-r_px)/r_dx;
        }catch (IllegalArgumentException p){
            return null;
        }

        if (T_ONE < 0){
            return null;
        }
        if (T_TWO < 0 || T_TWO > 1){
            return null;
        }

        return new Point(r_px+r_dx*T_ONE, r_py+r_dy*T_ONE, 0, T_ONE);
    }

    private void uniquify(){
        //flatten list

        for (ArrayList<Point> obstacle : this.obstacles){
            for (Point point : obstacle){
                this.points.add(point);
            }
        }
        //this might not work!!!!
        //get only unique points
        Set<Point> set = new HashSet<Point>(this.points);
        ArrayList<Point> pointsNew = new ArrayList<Point>(set);
        this.points = pointsNew;
    }

    private ArrayList<Double> getAllAngles(){
        ArrayList<Double> uniqueAngles = new ArrayList<>();

        for (Point point : this.points){
            double angle = Math.atan2(point.getY() - this.y, point.getX() - this.x);
            point.setAngle(angle);
            uniqueAngles.add(angle - 0.00001);
            uniqueAngles.add(angle);
            uniqueAngles.add(angle + 0.00001);
        }
        return uniqueAngles;
    }

    public ArrayList<Point> castLight(){
        //call this to get visibilty polygon
        ArrayList<Point> intersections = new ArrayList<>();
        ArrayList<Double> uniqueAngles = this.getAllAngles();
        Point light = new Point(this.x, this.y);
        for (double angle : uniqueAngles){
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);

            Line ray = new Line(light, new Point(this.x + dx, this.y + dy));

            Point closest = null;

            for (Line segment : this.segments){
                Point intersection = this.getIntersection(ray, segment);
                if (intersection == null){
                    continue;
                }
                if (closest == null || intersection.getDistance() < closest.getDistance()){
                    closest = intersection;
                }
            }
            if (closest == null){
                continue;
            }
            closest.setAngle(angle);
            intersections.add(closest);
        }
        Collections.sort(intersections, Point.Comparators.ANGLE);
        return intersections;
    }

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
        synchronized (visibility) {
            this.visibility.clear();
            this.visibility = this.castLight();
        }
    }

    public void setPos(double x, double y){
        this.x = x;
        this.y = y;
    }


    public ArrayList<Point> getVisibility(){
        return this.visibility;
    }

    public void move(double dx, double dy){
        this.x += dx;
        this.y += dy;
        this.visibility.clear();
        this.visibility = this.castLight();
    }



}
