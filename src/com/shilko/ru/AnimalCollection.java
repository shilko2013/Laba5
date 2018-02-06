package com.shilko.ru;

import javax.xml.stream.*;
import java.io.*;
import java.util.*;

public class AnimalCollection {
    private Map<Long,Animal> collection = new TreeMap<>();
    public void load(String fileName) {
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
                xmlr.close();
        } catch (FileNotFoundException | IllegalArgumentException | XMLStreamException ex) {
            ex.printStackTrace();
        }
    }
    public void work() {
        collection.forEach((n,e)->System.out.print(e.work()));
    }
    public void save(String fileName) {
        try {
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(new FileWriter(fileName));
            writer.writeStartDocument("UTF-8","1.0");
            writer.writeStartElement("DATA");
            for ( Long e : collection.keySet()) {
                Animal temp = collection.get(e);
                writer.writeStartElement("ANIMAL");
                writer.writeAttribute("type",temp.getClass().toString().replaceAll("class com.shilko.ru.","").toLowerCase());
                writer.writeStartElement("NAME");
                writer.writeAttribute("name",temp.getName());
                writer.writeEndElement();
                writer.writeStartElement("HOME");
                writer.writeAttribute("home",temp.getHome());
                writer.writeEndElement();
                writer.writeStartElement("COORD");
                writer.writeAttribute("x",Integer.toString(temp.getX()));
                writer.writeAttribute("y",Integer.toString(temp.getY()));
                writer.writeAttribute("z",Integer.toString(temp.getZ()));
                writer.writeEndElement();
                writer.writeStartElement("ACTIONS");
                for (String s: temp.getActions()) {
                    writer.writeStartElement("ACTION");
                    writer.writeAttribute("act",s);
                    writer.writeEndElement();
                }
                writer.writeEndElement();
                if (temp.getClass().toString().endsWith("Tiger")) {
                    writer.writeStartElement("ACTIONSFORTONGUE");
                    for (String s:((Tiger) temp).getActionsForTongue()) {
                        writer.writeStartElement("ACTION");
                        writer.writeAttribute("act",s);
                        writer.writeEndElement();
                    }
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();
            /*writer.writeStartElement("Book");

                // Заполняем все тэги для книги
                // Title
                writer.writeStartElement("Title");
                writer.writeCharacters("Book #" + i);
                writer.writeEndElement();
                // Author
                writer.writeStartElement("Author");
                writer.writeCharacters("Author #" + i);
                writer.writeEndElement();
                // Date
                writer.writeStartElement("Date");
                writer.writeCharacters(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                writer.writeEndElement();
                // ISBN
                writer.writeStartElement("ISBN");
                writer.writeCharacters("ISBN #" + i);
                writer.writeEndElement();
                // Publisher
                writer.writeStartElement("Publisher");
                writer.writeCharacters("Publisher #" + i);
                writer.writeEndElement();
                // Cost
                writer.writeStartElement("Cost");
                writer.writeAttribute("currency", "USD");
                writer.writeCharacters("" + (i+10));
                writer.writeEndElement();

                // Закрываем тэг Book
                writer.writeEndElement();*/
        }
        catch (IOException | IllegalArgumentException | XMLStreamException ex) {
            ex.printStackTrace();
        }
    }
}
