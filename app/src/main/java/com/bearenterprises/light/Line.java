package com.bearenterprises.light;

/**
 * Created by Teodor on 07/01/15.
 */
public class Line {
    private Point point1, point2;

    public Line(Point point1, Point point2){
        this.point1 = point1;
        this.point2 = point2;
    }

    public Point getPoint1() {
        return point1;
    }

    public Point getPoint2() {
        return point2;
    }
}
