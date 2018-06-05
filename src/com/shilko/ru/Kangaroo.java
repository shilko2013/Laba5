package com.shilko.ru;

import java.util.*;

@Jumping(maxHeight = 100)
public class Kangaroo extends Animal implements Mindable {
    private boolean decision;
    private final static int[] colour = new int[]{148,104,76};
    @Override public int[] getColour() {
        return colour;
    }
    private final static String colourSynonym = "Коричневый";
    @Override public String getColourSynonym() {return colourSynonym;}
    @Override
    public boolean think() {
        class Mind implements Mindable {
            @Override
            public boolean think() {
                nextThink();
                return decision;
            }
            private void nextThink() {
                decision = new Random().nextBoolean();
            }
        }
        return new Mind().think();
    }
    public Kangaroo(String name, String home, int coordX, int coordY, int weight) {
        super(name,home,coordX,coordY,weight);
    }
    public Kangaroo(String name, int coordX, int coordY, int weight) {
        super(name,coordX,coordY,weight);
    }
    /*@Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        Kangaroo kangaroo = (Kangaroo) obj;
        return getName().equals(kangaroo.getName())
                && getHome().equals(kangaroo.getHome())
                && getIngestion().equals(kangaroo.getIngestion())
                && getActions().equals(kangaroo.getActions())
                && Arrays.equals(getColour(),kangaroo.getColour());
    }*/
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    @Override
    public String work() {
        return getName()+super.work();
    }
    /*@Override
    public String toString() {
        return super.toString() +
                "LastDecision: " + decision;
    }*/
}
