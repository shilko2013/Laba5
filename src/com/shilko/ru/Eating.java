package com.shilko.ru;

import java.util.*;

public interface Eating {
    public String eat();
    public void addEat(Ingestion ingest, String ... arg);
    public void clearEat();
    public <T extends Map<?,?>> T getIngestion();
    default public void fillRandomEat(Ingestion ingest, int count, String ... arg) {
        ArrayList<String> temp = new ArrayList<>(Arrays.asList(arg));
        Collections.shuffle(temp);
        addEat(ingest,temp.subList(0,count).toArray(new String[0]));
    }
}
