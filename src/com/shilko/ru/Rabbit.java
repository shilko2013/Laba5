package com.shilko.ru;

import java.util.*;

public class Rabbit extends Animal implements Workable {
    private Queue<String> actions;
    {
        actions = new LinkedList<>();
    }
    public Rabbit(String name) {
        super(name);
    }
    @Override
    public Queue<String> getActions() {
        return actions;
    }
    @Override
    public void addAction(Collection<? super String> act) {
        for (Object a: act)
            actions.add((String)a);
    }
    @Override
    public void clearAction() {
        actions.clear();
    }
    @Override
    public boolean isBusyDay() {
        return actions.size()>3;
    }
    @Override
    public String work() {
        try {
            if (actions.isEmpty())
                throw new EmptyException();
            StringBuffer s = new StringBuffer();
            for (String act: actions)
                s.append(act+", ");
            s.delete(s.length()-2,s.length());
            return s.toString() + ".\n";
        } catch (EmptyException e) {
            e.printStackTrace();
            return "";
        }
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
                && getActions().equals(rabbit.getActions());
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
