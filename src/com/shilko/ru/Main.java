package com.shilko.ru;

public class Main {
    public static void main(String[] args) {
        /*Tiger Tigra = new Tiger("Тигра","Дом Кенги",5,5,5);
        Tigra.lookAtCeiling();
        Tigra.closeEyes();
        Tigra.checkFace();
        Tigra.workWithTongue();
        Tigra.smile();
        Tigra.like();
        Tigra.whereLive();
        Animal Roo = new Kangaroo("Ру", "Дом Кенги", 3,3,3);
        Roo.addEat(Ingestion.BREAKFAST, "Каша");
        Tigra.getEat();
        Rabbit Bunny = new Rabbit("Кролик","Дом Кролика", 10,10,10);
        Bunny.addSomeActions();
        Bunny.senseOfSelfReliance();
        Bunny.makeActions();*/


        /*Animal a = new Tiger("r", "t", 0, 0, 0);
        Animal b = new Kangaroo("q", "w", 3, 4, 5);
        Animal c = new Kangaroo("a", "s", 3, 4, 5);
        a.addAction("поехать вперед","упал");
        b.addAction("12");
        c.addAction("234");
        System.out.print(a.work());
        System.out.print(b.work());
        System.out.print(c.work());*/
        AnimalCollection collection = new AnimalCollection();
        Runtime.getRuntime().addShutdownHook(new Thread(()->{collection.save(args[0]);}));
        collection.load(args[0]);
        collection.work();
        collection.parse("{\n" +
                "\t\"type\": \"tiger\",\n" +
                "\t\"name\": \"Тигра\",\n" +
                "\t\"home\": \"Домик Кенги\",\n" +
                "\t\"coord\": {\n" +
                "\t\t\"x\": \"2\",\n" +
                "\t\t\"y\": \"2\",\n" +
                "\t\t\"z\": \"2\"\n" +
                "\t},\n" +
                "\t\"actions\": [\n" +
                "\t\t\"посмотрел на потолок\",\n" +
                "\t\t\"закрыл глаза\"\n" +
                "\t],\n" +
                "\t\"actionsForTongue\": [\n" +
                "\t\t\"начал ходить кругами вокруг мордочки\"\n" +
                "\t]\n" +
                "}\t");
    }
}