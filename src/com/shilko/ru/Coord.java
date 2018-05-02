package com.shilko.ru;

import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

public class Coord implements Comparable<Coord>,Serializable {
    private int x,y;
    public Coord(int x, int y) throws IllegalArgumentException {
        if ((x < 0) || (y < 0))
            throw new IllegalArgumentException();
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    private long amount() {
        return x*y;
    }
    public static Coord read(InputStream in) {
        Scanner ins = new Scanner(in);
        int x = ins.nextInt();
        int y = ins.nextInt();
        return new Coord(x,y);
    }
    public static Coord read(String s){
        Scanner ins = new Scanner(s);
        int x = ins.nextInt();
        int y = ins.nextInt();
        return new Coord(x,y);
    }
    @Override
    public int compareTo(Coord coord) {
        if (Long.compare(getX(),coord.getX())!=0)
            return Long.compare(getX(),coord.getX());
        else return Long.compare(getY(),coord.getY());
    }
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return getX() == coord.getX() &&
                getY() == coord.getY();
    }
    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }
}
