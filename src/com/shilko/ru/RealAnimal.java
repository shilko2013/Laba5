package com.shilko.ru;

import java.util.Objects;

public class RealAnimal extends Animal {
    private final static int[] colour = new int[]{0,0,0};
    @Override public int[] getColour() {
        return colour;
    }
    private final static String colourSynonym = "Черный";
    @Override public String getColourSynonym() {return colourSynonym;}
    public RealAnimal(String name, String home, int x, int y, int weight) { super(name,home,x,y,weight); }
    public RealAnimal(String name, int x, int y, int weight) {
        super(name,x,y,weight);
    }
    /*@Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (this.getClass() != obj.getClass())
            return false;
        Rabbit rabbit = (Rabbit) obj;
        return getName().equals(rabbit.getName())
                && getHome().equals(rabbit.getHome())
                && getIngestion().equals(rabbit.getIngestion())
                && getActions().equals(rabbit.getActions())
                && getCoord().equals(rabbit.getCoord());
    }*/
    @Override
    public String work() {
        return getName()+super.work();
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),getActions());
    }
    /*@Override
    public String toString() {
        return super.toString() +
                "Actions: " + work();
    }*/
}
