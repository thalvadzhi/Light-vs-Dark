package com.bearenterprises.light;

import java.util.Comparator;

/**
 * Created by Teodor on 07/01/15.
 */
public class Point implements Comparable<Point>{
    private double x, y, angle, distance;


    public Point(double x, double y){
        this.x = x;
        this.y = y;
        this.angle = 0;
        this.distance = 0;
    }

    public Point(double x, double y, double angle, double distance){
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        if (y != point.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public void setAngle(double angle) {

        this.angle = angle;
    }

    @Override
    public String toString() {
        return this.x + " " + this.y;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(Point another) {
        Double angle = this.angle;
        Double otherAngle = another.getAngle();
        return angle.compareTo(otherAngle);
    }

    public static class Comparators {
        public static final Comparator<Point> ANGLE = new Comparator<Point>() {
            @Override
            public int compare(Point lhs, Point rhs) {
                return lhs.compareTo(rhs);
            }
        };

    }
}
