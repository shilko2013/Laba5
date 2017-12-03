package com.shilko.ru;

import java.util.*;

public class Kangaroo extends Animal implements Mindable {
    private boolean decision;
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
    public Kangaroo(String name) {
        super(name);
    }
    @Override
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
                && getIngestion().equals(kangaroo.getIngestion());
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    @Override
    public String toString() {
        return super.toString() +
                "LastDecision: " + decision;
    }
}
