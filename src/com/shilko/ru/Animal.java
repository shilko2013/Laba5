package com.shilko.ru;

import java.util.*;

public abstract class Animal implements Eating {
    private class HomelessException extends RuntimeException {
        public HomelessException() {super("Home of " + getName() + " is epsend!!!");}
        public HomelessException(String message) {super(message);}
        @Override
        public String toString() {
            return "Перехвачено: " + super.toString();
        }
    }
    private String name;
    private String home;
    private Map<Ingestion, Set<String>> ingestion;
    {
        ingestion = new TreeMap<>();
        home = null;
    }
    public Animal(String name) {
        this.name = name;
    }
    public void setHome(String home) {
        this.home = home;
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
                && ingestion.equals(animal.getIngestion());
    }
    @Override
    public int hashCode() {
        return Objects.hash(name,home,ingestion);
    }
    @Override
    public String toString() {
        return getClass() + "\n" +
                "Name: " + getName() + "\n" +
                "Home: " + getHome() + "\n" +
                "Eat: " + eat();
    }
}
