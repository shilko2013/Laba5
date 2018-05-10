package com.shilko.ru;

import java.io.Serializable;
import java.util.*;

public class Tiger extends Animal implements Workable, Sayable {
    private final static int[] colour = new int[]{247,107,0};
    @Override public int[] getColour() {
        return colour;
    }
    private final static String colourSynonym = "Оранжевый";
    @Override public String getColourSynonym() {return colourSynonym;}
    private class Tongue implements Workable,Serializable {
        private Queue<String> action;
        {
            action = new LinkedList<>();
        }
        @Override
        public Queue<String> getActions() {
            return action;
        }
        @Override
        public void addAction(String ... act) {
            for (String a: act)
                action.add(a);
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
                return s.toString() + ".\n";
            } catch (EmptyException e) {
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
    {
        tongue = new Tongue();
    }
    public Tiger(String name, String home, int x, int y, int weight) {
        super(name,home,x,y,weight);
    }
    public Tiger(String name, int x, int y, int weight) {
        super(name,x,y,weight);
    }
    @Override
    public void say(String message) {
        System.out.println(" сказал:\" " + message + "\"");
    }
    public void workWithTongue() {
        System.out.print("Тут Тигра " + work() + " и язык его " + workForTongue());
        clearAction();
    }
    public void lookAtCeiling() {
        addAction("посмотрел на потолок");
    }
    public void like() {
        if (lastSthOutside)
            say("Так вот что Тигры действительно любят!");
        else
            say("Жаль, что ничего не осталось!");
    }
    public void smile() {
        if (lastSthOutside)
            addAction("Затем его озарила умиротворенная улыбка и");
        else
            addAction("Затем он огорчился и");
        System.out.print(work());
    }
    public void getEat() {
        Arrays.stream(Ingestion.values()).forEach(ingest->fillRandomEat(ingest,1, "Рыбий жир","Каша","Лекарство"));
        int countOfFishFat = 0;
        boolean porrigeIsExist = false;
        Map<Ingestion, Set<String>> temp = getIngestion();
        for (Ingestion ingest : temp.keySet()) {
            if (temp.get(ingest).contains("Рыбий жир"))
                ++countOfFishFat;
            else if (temp.get(ingest).contains("Каша"))
                porrigeIsExist = true;
        }
        String s;
        switch(countOfFishFat) {
            case 2:
                s = "иногда";
                break;
            case 1:
                s = "редко";
                break;
            case 0:
                s = "никогда не";
                break;
            default:
                s = "всегда";
        }
        System.out.println("и " + s + " получал рыбий жир на завтрак, обед и ужин.");
        if (new Kangaroo("Кенга","Домик Кенги",3,3,50).think()&&(porrigeIsExist))
            System.out.println("Также иногда на завтрак он получал ложку-другую каши, которой завтракал Ру.");
    }
    public void closeEyes() {
        addAction("закрыл глаза");
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
        tongue.addAction(act.toArray(new String[0]));
    }
    public void clearActionForTongue() {
        tongue.clearAction();
    }
    public void addActionForTongue(String ... args) {
        tongue.addAction(args);
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
    public boolean isSthOutside() {
        lastSthOutside = new Random().nextBoolean();
        return lastSthOutside;
    }
    /*@Override
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
                && tongue.equals(tiger.tongue)
                && Arrays.equals(getColour(),tiger.getColour());
    }*/
    @Override
    public String work() {
        String s = getName() + super.work();
        if (workForTongue().length()>0)
            s += "Язык " + workForTongue();
        return s;
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
