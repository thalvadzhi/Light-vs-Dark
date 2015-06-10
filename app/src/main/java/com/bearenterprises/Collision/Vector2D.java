package com.bearenterprises.Collision;


public class Vector2D {
    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    private double x, y;

    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getLength(){
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    public Vector2D normalize(){
        return new Vector2D(this.x / this.getLength(), this.y / this.getLength());
    }

    public double dotProduct(Vector2D other){
        double x = this.x * other.x;
        double y = this.y * other.y;
        return x + y;
    }

    public double projectionOn(Vector2D axis) {
        return this.dotProduct(axis.normalize());
    }

    public Vector2D rotate(double degrees){
        double x = this.x * Math.cos(Math.toRadians(degrees)) - this.y * Math.sin(Math.toRadians(degrees));
        double y = this.x * Math.sin(Math.toRadians(degrees)) + this.y * Math.cos(Math.toRadians(degrees));
        return new Vector2D(x, y);
    }

}
