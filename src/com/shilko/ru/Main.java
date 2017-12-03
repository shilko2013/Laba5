package com.shilko.ru;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Tiger Tigra = new Tiger("Тигра");
        Tigra.lookAtCeiling();
        Tigra.closeEyes();
        Tigra.checkFace();
        Tigra.workWithTongue();
        Tigra.smile();
        Tigra.like();
        Tigra.setHome("Дом Кенги");
        Tigra.whereLive();
        Animal Roo = new Kangaroo("Ру");
        Roo.addEat(Ingestion.BREAKFAST, "Каша");
        Tigra.getEat();
        Rabbit Bunny = new Rabbit("Кролик");
        Bunny.addSomeActions();
        Bunny.senseOfSelfReliance();
        Bunny.makeActions();
    }
}