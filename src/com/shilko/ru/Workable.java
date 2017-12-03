package com.shilko.ru;

import java.util.*;

public interface Workable {
    public String work();
    default public boolean isBusyDay() { return false;};
    public <T extends Collection<?>> T getActions();
    public void addAction(Collection<? super String> act);
    public void clearAction();
    default public void fillRandomActions(String[] act, int count) {
        ArrayList<String> temp = new ArrayList<>(Arrays.asList(act));
        Collections.shuffle(temp);
        addAction(temp.subList(0,count));
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
