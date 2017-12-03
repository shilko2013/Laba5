package com.shilko.ru;

import java.util.*;

public class Tiger extends Animal implements Workable, Sayable {
    private static class Tongue implements Workable {
        private Queue<String> action;
        {
            action = new LinkedList<>();
        }
        @Override
        public Queue<String> getActions() {
            return action;
        }
        @Override
        public void addAction(Collection<? super String> act) {
            for (Object a: act)
                action.add((String)a);
        }
        @Override
        public void clearAction() {
            action.clear();
        }
        @Override
        public boolean isBusyDay() {
            return action.size()>3;
        }
        @Override
        public String work() {
            try {
                if (action.isEmpty())
                    throw new EmptyException();
                StringBuffer s = new StringBuffer();
                for (String act: action)
                    s.append(act+", ");
                s.delete(s.length()-2,s.length());
                return s.toString() + "\n";
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
            Tongue tongue = (Tongue) obj;
            return getActions().equals(tongue.getActions());
        }
        @Override
        public int hashCode() {
            return action.hashCode();
        }
        @Override
        public String toString() {
            return getClass() + "\n"
                    +"Actions: " + work();
        }
    }
    private boolean lastSthOutside = false;
    private Tongue tongue;
    private Queue<String> actions;
    {
        tongue = new Tongue();
        actions = new LinkedList<>();
    }
    public Tiger(String name) {
        super(name);
    }
    @Override
    public void say(String message) {
        System.out.println(getName() + " сказал:\" " + message + "\"");
    }
    public void workWithTongue() {
        System.out.print("Тут Тигра " + work() + " и язык его " + workForTongue());
        clearAction();
    }
    public void lookAtCeiling() {
        addAction(Arrays.asList("посмотрел на потолок"));
    }
    public void like() {
        if (lastSthOutside)
            say("Так вот что Тигры действительно любят!");
    }
    public void smile() {
        addAction(Arrays.asList("Затем его озарила умиротворенная улыбка и "));
    }
    public void closeEyes() {
        addAction(Arrays.asList("закрыл глаза"));
    }
    public void checkFace() {
        String s = "пошел ходить кругами вокруг мордочки на тот случай, если что-нибудь осталось снаружи";
        if (isSthOutside())
            s += ".";
        else
            s += ", но на ней ничего не осталось.";
        addActionForTongue(Arrays.asList(s));
    }
    public void addActionForTongue(Collection<? super String> act) {
        tongue.addAction(act);
    }
    public void clearActionForTongue() {
        tongue.clearAction();
    }
    public boolean isBusyDayForTongue() {
        return tongue.isBusyDay();
    }
    public String workForTongue() {
        return tongue.work();
    }
    public Queue<String> getActionsForTongue() {
        return tongue.getActions();
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
            return s.toString();
        } catch (EmptyException e) {
            e.printStackTrace();
            return "";
        }
    }
    public boolean isSthOutside() {
        lastSthOutside = new Random().nextBoolean();
        return lastSthOutside;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (this.getClass() != obj.getClass())
            return false;
        Tiger tiger = (Tiger) obj;
        return getName().equals(tiger.getName())
                && getHome().equals(tiger.getHome())
                && getIngestion().equals(tiger.getIngestion())
                && getActions().equals(tiger.getActions())
                && tongue.equals(tiger.tongue);
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),getActions(),tongue.hashCode());
    }
    @Override
    public String toString() {
        return super.toString() +
                "Actions: " + work()
                + tongue.toString();
    }
}
