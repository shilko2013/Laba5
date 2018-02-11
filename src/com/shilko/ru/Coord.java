package com.shilko.ru;

import java.io.InputStream;
import java.util.*;

public class Coord implements Comparable<Coord> {
    private int x,y,z;
    public Coord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
    public int getZ() {
        return z;
    }
    public void setZ(int z) {
        this.z = z;
    }
    private long amount() {
        return x*y*z;
    }
    public static Coord read(InputStream in) {
        Scanner ins = new Scanner(in);
        int x = ins.nextInt();
        int y = ins.nextInt();
        int z = ins.nextInt();
        return new Coord(x,y,z);
    }
    public static Coord read(String s) {
        Scanner ins = new Scanner(s);
        int x = ins.nextInt();
        int y = ins.nextInt();
        int z = ins.nextInt();
        return new Coord(x,y,z);
    }
    @Override
    public int compareTo(Coord coord) {
        return Long.compare(amount(),coord.amount());
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
                getY() == coord.getY() &&
                getZ() == coord.getZ();
    }
    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ());
    }
}
