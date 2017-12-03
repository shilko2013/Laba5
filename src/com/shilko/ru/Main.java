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
        System.out.print("Теперь нас не удивит, что " + Tigra.getName() + " поселился в " + Tigra.getHome() + " ");
        Animal Roo = new Kangaroo("Ру");
        Roo.addEat(Ingestion.BREAKFAST, "Каша");
        Arrays.stream(Ingestion.values()).forEach(ingest->Tigra.fillRandomEat(ingest,1, "Рыбий жир","Каша","Лекарство"));
        {
            int countOfFishFat = 0;
            boolean porrigeIsExist = false;
            Map<Ingestion, Set<String>> temp = Tigra.getIngestion();
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
            if (new Kangaroo("Кенга").think()&&(porrigeIsExist))
                System.out.println("Также иногда на завтрак он получал ложку-другую каши, которой завтракал Ру.");
        }
        Rabbit Bunny = new Rabbit("Кролик");
        Bunny.fillRandomActions(new String[]{"открыть глаза",
                "почувствовать зависимость окружающих",
                "почувствовать, что все на тебя рассчитывают",
                "написать письмо",
                "все проверить",
                "все выяснить",
                "все разъяснить",
                "что-то организовать"},new Random().nextInt(7)+2);
        String s;
        if (Bunny.isBusyDay())
            s = "Всё ";
        else
            s = "Ничего не ";
        if (Bunny.getActions().size()>0)
            System.out.println(Bunny.getName() + " почувствовал,  что сегодня все от него зависит и все на него рассчитывают.");
        System.out.println(s + "предвещало, что у " + Bunny.getName() + " опять будет очень занятой день.");
        System.out.println("Ему предстояло: " + Bunny.work());
    }
}