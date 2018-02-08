package com.shilko.ru;

import java.util.*;

public abstract class Animal implements Eating, Workable, Comparable<Animal> {
    private class HomelessException extends RuntimeException {
        public HomelessException() {super("Home of " + getName() + " is epsend!!!");}
        public HomelessException(String message) {super(message);}
        @Override
        public String toString() {
            return "Перехвачено: " + super.toString();
        }
    }
    private String name;
    private static long ID = 0;
    private long myID;
    private Coord coord;
    private String home;
    private Queue<String> actions;
    private Map<Ingestion, Set<String>> ingestion;
    {
        ingestion = new TreeMap<>();
        home = null;
        actions = new LinkedList<>();
    }
    public Animal (String name, String home, int x, int y, int z) {
        this(name,x,y,z);
        this.home = home;
    }
    public Animal (String name, int x, int y, int z) {
        this.name = name;
        this.myID = ID;
        ID++;
        coord = new Coord(x,y,z);
    }
    public void setHome(String home) {
        this.home = home;
    }
    @Override
    public int compareTo(Animal animal) {
        return Long.compare(getID(),animal.getID());
    }
    @Override
    public void addEat(Ingestion ingest, String ... arg) {
        if (!ingestion.containsKey(ingest))
            ingestion.put(ingest,new LinkedHashSet<>());
        ingestion.get(ingest).addAll(Arrays.asList(arg));
    }
    @Override
    public void clearEat() {
        ingestion.clear();
    }
    public String getName() {
        return name;
    }
    public String getHome() {
        if (home == null)
            throw new HomelessException();
        return home;
    }
    public void whereLive() {
        System.out.print("Теперь нас не удивит, что "+ getName() + " поселился в " + getHome() + " ");
    }
    public Map<Ingestion, Set<String>> getIngestion() {
        return ingestion;
    }
    @Override
    public String eat() {
        if (ingestion.isEmpty())
            return "Empty";
        StringBuffer s = new StringBuffer();
        ingestion.forEach((k,v)-> {
            s.append("На " + k + ": ");
            v.forEach((e)->s.append(e+", "));
            s.delete(s.length()-2,s.length());
            s.append("\n");
        });
        return s.toString();
    }
    @Override
    public Queue<String> getActions() {
        return actions;
    }
    @Override
    public void addAction(String ... act) {
        for (String a: act)
            actions.add(a);
    }
    public long getID() {
        return myID;
    }
    @Override
    public void clearAction() {
        actions.clear();
    }
    public Coord getCoord() {
        return  coord;
    }
    public void setCoord(int x, int y, int z) {
        coord = new Coord(x,y,z);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (this.getClass() != obj.getClass())
            return false;
        Animal animal = (Animal) obj;
        return name.equals(animal.getName())
                && home.equals(animal.getHome())
                && ingestion.equals(animal.getIngestion())
                && actions.equals(animal.getActions())
                && coord.equals(animal.getCoord());
    }
    @Override
    public int hashCode() {
        return Objects.hash(name,home,ingestion,actions,coord,myID);
    }
    @Override
    public String toString() {
        return getClass() + "\n" +
                "Name: " + getName() + "\n" +
                "Home: " + getHome() + "\n" +
                "Eat: " + eat();
    }
}
