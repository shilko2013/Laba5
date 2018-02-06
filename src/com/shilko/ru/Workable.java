package com.shilko.ru;

import java.util.*;

public interface Workable {
    default public String work() {
            try {
                if (getActions().isEmpty())
                    throw new EmptyException();
                StringBuffer s = new StringBuffer();
                for (Object act: getActions())
                    s.append(act+", ");
                s.delete(s.length()-2,s.length());
                return " "+s.toString()+".\n";
            } catch (EmptyException e) {
                e.printStackTrace();
                return " ничего не делал.\n";
            }
    };
    default public boolean isBusyDay() { return false;};
    public <T extends Collection<?>> T getActions();
    public void addAction(String ... act);
    public void clearAction();
    default public void fillRandomActions(String[] act, int count) {
        ArrayList<String> temp = new ArrayList<>(Arrays.asList(act));
        Collections.shuffle(temp);
        addAction(temp.subList(0,count).toArray(new String[0]));
    }
    default public void addSomeActions() {fillRandomActions(new String[]{"открыть глаза",
            "почувствовать зависимость окружающих",
            "почувствовать, что все на тебя рассчитывают",
            "написать письмо",
            "все проверить",
            "все выяснить",
            "все разъяснить",
            "что-то организовать"},new Random().nextInt(7)+2);}
}
