package com.shilko.ru;

import java.util.*;

public class Rabbit extends Animal{
    private final static int[] colour = new int[]{105,93,82};
    @Override public int[] getColour() {
        return colour;
    }
    private final static String colourSynonym = "Серый";
    @Override public String getColourSynonym() {return colourSynonym;}
    public Rabbit(String name, String home, int x, int y, int weight) {
        super(name,home,x,y,weight);
    }
    public Rabbit(String name, int x, int y, int weight) {
        super(name,x,y,weight);
    }
    public void senseOfSelfReliance() {
        if (getActions().size()>0)
            System.out.println(getName() + " почувствовал,  что сегодня все от него зависит и все на него рассчитывают.");
    }
    public void makeActions() {
        String s;
        if (isBusyDay())
            s = "Всё ";
        else
            s = "Ничего не ";
        System.out.println(s + "предвещало, что у " + getName() + " опять будет очень занятой день.");
        System.out.println("Ему предстояло: " + work());
    }
    @Override
    public boolean isBusyDay() {
        return getActions().size()>3;
    }
    @Override
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
                && Arrays.equals(getColour(),rabbit.getColour());
    }
    @Override
    public String work() {
        return getName()+super.work();
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),getActions());
    }
    @Override
    public String toString() {
        return super.toString() +
                "Actions: " + work();
    }
}
