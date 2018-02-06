package com.shilko.ru;

import java.util.*;
import java.io.*;
import javax.xml.stream.*;

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
        Map<Long,Animal> collection = new TreeMap<>();
        String fileName = "Data.xml";
        try {
            XMLStreamReader xmlr = XMLInputFactory.newInstance().createXMLStreamReader(fileName, new BufferedReader(new FileReader(fileName)));

            xmlr.nextTag();
            /*if(!xmlr.getLocalName().equalsIgnoreCase("DATA"))
                throw new IllegalArgumentException();*/
            xmlr.nextTag();
            while (!xmlr.isEndElement()) {
                /*if(!xmlr.getLocalName().equalsIgnoreCase("ANIMAL"))
                    throw new IllegalArgumentException();*/
                String type = xmlr.getAttributeValue(0);
                xmlr.nextTag();
                /*if(!xmlr.getLocalName().equalsIgnoreCase("NAME"))
                    throw new IllegalArgumentException();*/
                String name = xmlr.getAttributeValue(0);
                xmlr.nextTag();
                xmlr.nextTag();
                String home = xmlr.getAttributeValue(0);
                xmlr.nextTag();
                xmlr.nextTag();
                int x = Integer.parseInt(xmlr.getAttributeValue(0));
                int y = Integer.parseInt(xmlr.getAttributeValue(1));
                int z = Integer.parseInt(xmlr.getAttributeValue(2));
                xmlr.nextTag();
                xmlr.nextTag();
                List<String> actions = new ArrayList<>();
                xmlr.nextTag();
                List<String> actionsForTongue = new ArrayList<>();
                while (!xmlr.isEndElement()) {
                    actions.add(xmlr.getAttributeValue(0));
                    xmlr.nextTag();
                    xmlr.nextTag();
                }
                if (type.equalsIgnoreCase("tiger")) {
                    xmlr.nextTag();
                    xmlr.nextTag();
                    while (!xmlr.isEndElement()) {
                        actionsForTongue.add(xmlr.getAttributeValue(0));
                        xmlr.nextTag();
                        xmlr.nextTag();
                    }
                }
                xmlr.nextTag();
                xmlr.nextTag();
                switch (type) {
                    case "tiger":
                        Tiger tiger = new Tiger(name,home,x,y,z);
                        tiger.addAction(actions.toArray(new String[0]));
                        tiger.addActionForTongue(actionsForTongue.toArray(new String[0]));
                        collection.put(tiger.getID(),tiger);
                        break;
                    case "rabbit":
                        Rabbit rabbit = new Rabbit(name,home,x,y,z);
                        rabbit.addAction(actions.toArray(new String[0]));
                        collection.put(rabbit.getID(),rabbit);
                        break;
                    case "kangaroo":
                        Kangaroo kangaroo = new Kangaroo(name,home,x,y,z);
                        kangaroo.addAction(actions.toArray(new String[0]));
                        collection.put(kangaroo.getID(),kangaroo);
                        break;
                }
            }
                /*if(!xmlr.getLocalName().equalsIgnoreCase("ANIMAL"))
                    throw new IllegalArgumentException();*/

                /*if (xmlr.isStartElement()) {
                    System.out.println(xmlr.getLocalName());
                } else if (xmlr.isEndElement()) {
                    System.out.println("/" + xmlr.getLocalName());
                } else if (xmlr.hasText() && xmlr.getText().trim().length() > 0) {
                    System.out.println("   " + xmlr.getText());
                }*/

        } catch (FileNotFoundException | IllegalArgumentException | XMLStreamException ex) {
            ex.printStackTrace();
        }
        collection.forEach((n,e)->System.out.print(e.work()));
    }
}