package com.shilko.ru;

import javax.swing.*;
import javax.xml.stream.*;
import java.io.*;
import java.util.*;
import com.jayway.jsonpath.*;
import javafx.util.Pair;
import java.util.concurrent.*;
import java.sql.*;

/**
 * Класс, инкапсулирующий в себе управление коллекцией животных.
 * @author Шилко Даниил
 * @version 1.5
 */
public class AnimalCollection implements Serializable {
    /** Поле коллекция животных */
    private Map<Coord,Animal> collection;
    private final String dataBaseURL;
    private final String user;
    private final String password;
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,"Не удалось подключиться к БД!","Ошибка",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    public AnimalCollection(String dataBaseURL, String user, String password) {
        collection = new ConcurrentSkipListMap<>();
        this.dataBaseURL = dataBaseURL;
        this.user = user;
        this.password = password;
    }
    public void load() throws Exception {
        collection.clear();
        Connection connection = DriverManager.getConnection(dataBaseURL,user,password);
        if (connection == null)
            throw new SQLException();
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery( "SELECT * FROM ANIMALS;" );
        while (resultSet.next()) {
            putAnimal(resultSet.getString("type"),
                    resultSet.getString("name"),
                    resultSet.getString("home"),
                    resultSet.getInt("coord_x"),
                    resultSet.getInt("coord_y"),
                    resultSet.getInt("weight"),
                    new ArrayList<>(), new ArrayList<>());
        }
        resultSet.close();
        statement.close();
        connection.close();
    }
    public void save() throws Exception {
        Connection connection = DriverManager.getConnection(dataBaseURL,user,password);
        if (connection == null)
            throw new SQLException();
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        statement.executeUpdate("TRUNCATE ANIMALS;");
        collection.forEach((key,animal)->{
            try {
                statement.executeUpdate("INSERT INTO ANIMALS(type,name,coord_x,coord_y,home,weight) VALUES " +
                        "('" + animal.getClass().toString().substring(getClass().toString().lastIndexOf(".") + 1).toLowerCase() +
                        "','"+animal.getName()+
                        "',"+animal.getCoord().getX()+
                        ","+animal.getCoord().getY()+
                        ",'"+animal.getHome()+
                        "',"+animal.getWeight()+");");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        statement.close();
        connection.commit();
        connection.close();
    }
    public int size() {
        return collection.size();
    }
    /**
     * Метод, предназначенный для загрузки элементов коллеции из файла
     * и записи их в коллекцию {@link AnimalCollection#collection}.
     * Файл должен быть в формате <i>XML</i>.
     * @param fileName путь к файлу
     */
    public void load(String fileName) throws Exception {

        collection.clear();

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
                int weight = Integer.parseInt(xmlr.getAttributeValue(0));
                xmlr.nextTag();
                xmlr.nextTag();
                int x = Integer.parseInt(xmlr.getAttributeValue(0));
                int y = Integer.parseInt(xmlr.getAttributeValue(1));
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
                putAnimal(type,name,home,x,y,weight,actions,actionsForTongue);
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
    }
    /**
     * Метод, вызывающий метод work() у всех элементов коллекции
     * {@link AnimalCollection#collection}.
     */
    public void work() {
        collection.forEach((n,e)->System.out.print(e.work()));
    }
    /**
     * Метод, сохраняющий коллекцию {@link AnimalCollection#collection}
     * в файл в формате <i>XML</i>..
     * @param fileName путь к файлу
     */
    public void save(String fileName) throws Exception {
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(new FileWriter(fileName));
            writer.writeStartDocument("UTF-8","1.0");
            writer.writeStartElement("DATA");
            for ( Coord e : collection.keySet()) {
                Animal temp = collection.get(e);
                writer.writeStartElement("ANIMAL");
                writer.writeAttribute("type",temp.getClass().toString().replaceAll("class com.shilko.ru.","").toLowerCase());
                writer.writeStartElement("NAME");
                writer.writeAttribute("name",temp.getName());
                writer.writeEndElement();
                writer.writeStartElement("HOME");
                writer.writeAttribute("home",temp.getHome());
                writer.writeEndElement();
                writer.writeStartElement("WEIGHT");
                writer.writeAttribute("weight",temp.getWeight()+"");
                writer.writeEndElement();
                writer.writeStartElement("COORD");
                writer.writeAttribute("x",Integer.toString(temp.getCoord().getX()));
                writer.writeAttribute("y",Integer.toString(temp.getCoord().getY()));
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
    /**
     * Метод, создающий и возвращающий животное по заданному
     * типу и характеристикам. <b>Реализован для классов Kangaroo,
     * Tiger, Rabbit</b>
     * @since 1.5
     * @param type тип животного
     * @param name имя животного
     * @param home дом животного
     * @param x x координата
     * @param y y координата
     * @param actions List&lt;String&gt; действий, которое может
     *                совершать животное
     * @param actionsForTongue List&lt;String&gt; действий, которое может
     *                совершать тигр, <b>если животное не тигр,
     *                не учавствует в работе метода</b>
     * @return возращает животное, полученное по данным характеристикам
     */
    private Animal parseAnimal(String type, String name, String home, int x, int y, int weight, List<String> actions, List<String> actionsForTongue) {
        switch (type.toLowerCase().trim()) {
            case "tiger":
                Tiger tiger = new Tiger(name, home, x, y, weight);
                tiger.addAction(actions.toArray(new String[0]));
                tiger.addActionForTongue(actionsForTongue.toArray(new String[0]));
                return tiger;
            case "rabbit":
                Rabbit rabbit = new Rabbit(name, home, x, y, weight);
                rabbit.addAction(actions.toArray(new String[0]));
                return rabbit;
            case "kangaroo":
                Kangaroo kangaroo = new Kangaroo(name, home, x, y, weight);
                kangaroo.addAction(actions.toArray(new String[0]));
                return kangaroo;
            default:
                RealAnimal realAnimal = new RealAnimal(name,home,x,y, weight);
                realAnimal.addAction(actions.toArray(new String[0]));
                return realAnimal;
        }
    }
    private Animal parseAnimal(String type, String name, int x, int y, String home, int weight) {
        return parseAnimal(type,name,home,x,y,weight,new ArrayList<>(),new ArrayList<>());
    }
    /**
     * Метод, добавляющий животное в коллекцию
     * {@link AnimalCollection#collection}по его характеристикам.
     * Для своей работы использует метод
     * {@link AnimalCollection#parseAnimal(String, String, String, int, int, int, List, List)}
     * @param type тип животного
     * @param name имя животного
     * @param home дом животного
     * @param x x координата
     * @param y y координата
     * @param actions List&lt;String&gt; действий, которое может
     *                совершать животное
     * @param actionsForTongue List&lt;String&gt; действий, которое может
     *                совершать тигр, <b>если животное не тигр,
     *                не учавствует в работе метода</b>
     * @see AnimalCollection#parseAnimal(String, String, String, int, int, int, List, List)
     */
    private void putAnimal(String type, String name, String home, int x, int y, int weight, List<String> actions, List<String> actionsForTongue) {
        Animal temp = parseAnimal(type,name,home,x,y,weight,actions,actionsForTongue);
        collection.put(temp.getCoord(),temp);
    }
    /**
     * Метод, принимающий строку в формате <i>JSON</i> и возвращающий
     * животное, которое в ней содержалось.
     * Работает с библиотекой com.jayway.jsonpath
     * @param in входная строка
     * @return животное, содержащееся в строке
     */
    private Animal read(String in) {
        String type = JsonPath.read(in, "$.type");
        String name = JsonPath.read(in,"$.name");
        String home = JsonPath.read(in,"$.home");
        int weight = Integer.parseInt(JsonPath.read(in,"$.weight"));
        int x = JsonPath.read(in,"$.coord.x");
        int y = JsonPath.read(in,"$.coord.y");
        List<String> actions = JsonPath.read(in,"$.actions[*]");
        List<String> actionsForTongue = null;
        if (type.equalsIgnoreCase("tiger"))
            actionsForTongue = JsonPath.read(in,"$.actionsForTongue[*]");
        /*try  {
            JsonReader rdr = Json.createReader(in);
            JsonObject obj = rdr.readObject();
            String type = obj.getString("type");
            String name = obj.getString("name");
            String home = obj.getString("home");
            int x = obj.getJsonObject("coord").getInt("x");
            int y = obj.getJsonObject("coord").getInt("y");
            int z = obj.getJsonObject("coord").getInt("z");
            List<String> actions = obj.getJsonArray("actions").getValuesAs(JsonValue::toString);
            actions = actions.stream().map(s->s.substring(1,s.length()-1)).collect(Collectors.toList());
            List<String> actionsForTongue = null;
            if (type.equalsIgnoreCase("tiger")) {
                actionsForTongue = obj.getJsonArray("actionsForTongue").getValuesAs(JsonValue::toString);
                actionsForTongue = actionsForTongue.stream().map(s->s.substring(1,s.length()-1)).collect(Collectors.toList());
            }

            */return parseAnimal(type,name,home,x,y,weight,actions,actionsForTongue);/*
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }*/
        //return null;
    }
    /**
     * Метод, удаляющий из коллекции {@link AnimalCollection#collection}
     * все экземпляры, которые соответствуют входному параметру в формате <i>JSON</i>.
     * Животное парсится методом
     * {@link AnimalCollection#parseAnimal(String, String, String, int, int, int, List, List)}
     * @param in входная строка
     * @see AnimalCollection#parseAnimal(String, String, String, int, int, int, List, List)
     */
    public List<Coord> removeAll(String in) {
        Animal animal = read(in);
        return removeAll(animal);
        /*List<Coord> temp = new ArrayList<>();
        Iterator<Coord> i = collection.keySet().iterator();
        for (;i.hasNext();i.next()) {
            i.remove();
        }
        collection.forEach((k,v)->{
            if (v.equals(animal))
                temp.add(k);
        });
        temp.forEach(k->collection.remove(k));*/
    }
    public List<Coord> removeAll(Animal animal) {
        List<Coord> list = new ArrayList<>();
        collection.keySet().forEach((e)->{
            if (collection.get(e).equals(animal))
                list.add(e);
        });
        collection.values().removeAll(Arrays.asList(animal));
        return list;
    }
    public List<Coord> removeAll(String type, String name, int x, int y, String home, int weight) {
        return  removeAll(parseAnimal(type,name,x,y,home,weight));
    }
    /**
     * Метод, добавляющий в коллекцию {@link AnimalCollection#collection}
     * животноев формате <i>JSON</i>, или заменяющее его,
     * если животное с такими координатами уже есть
     * @param in входная строка
     */
    public Object[] insert(String in) {
        Coord coord = Coord.read(in);
        Animal animal = read(in.substring(in.indexOf("{"),in.length()));
        animal.setCoord(coord.getX(),coord.getY());
        return insert(animal);
    }
    public  Object[] insert(Animal animal) {
        Object[] result = animal.toRow();
        if (collection.containsKey(animal.getCoord()))
            result = null;
        collection.put(animal.getCoord(),animal);
        return result;
    }
    public  Object[] insert(String type, String name, int x, int y, String home, int weight) {
        return  insert(parseAnimal(type,name,x,y,home,weight));
    }
    /**
     * Метод, удаляющий из коллекции {@link AnimalCollection#collection}
     * все экземпляры, произведение кооординаты которых
     * больше произведения координат животного,
     * закодированного в строке в формате <i>JSON</i>.
     * Животное парсится методом
     * {@link AnimalCollection#parseAnimal(String, String, String, int, int, int, List, List)}
     * @param in входная строка
     * @see AnimalCollection#parseAnimal(String, String, String, int, int, int, List, List)
     */
    public List<Coord> removeGreaterKey(String in) {
        Coord coord = Coord.read(in);
        return removeGreaterKey(coord);
        /*List<Coord> temp = new ArrayList<>();
        collection.forEach((k,v)->{
            if (k.compareTo(coord)>0)
                temp.add(k);
        });
        temp.forEach(k->collection.remove(k));*/
    }
    public List<Coord> removeGreaterKey(Coord coord) {
        List<Coord> list = new ArrayList<>();
        collection.keySet().forEach((e)->{
            if (e.compareTo(coord)>0)
                list.add(e);
        });
        collection.keySet().removeIf(a->a.compareTo(coord)>0);
        return list;
    }
    /**
     * Метод, удаляющий из коллекции {@link AnimalCollection#collection}
     * животное, находящееся в коориднатах,
     * заданных во входной строке.
     * @param in входная строка
     */
    public Pair<Boolean,Coord> remove(String in) {
        Coord coord = Coord.read(in);
        return remove(coord);
    }
    public Pair<Boolean,Coord> remove(Coord coord) {
        if (!collection.containsKey(coord))
            return new Pair<>(false,null);
        else {
            collection.remove(coord);
            return new Pair<>(true,coord);
        }
    }
    public Animal getAnimal(Coord coord) {
        return collection.getOrDefault(coord,null);
    }
    public void putAnimal(Coord coord, Animal animal) {
        collection.put(coord,animal);
    }
    public void list(OutputStream out) {
        new PrintStream(out).println(list());
    }
    public String list() {
        return collection.keySet().stream().map((t)-> {
            Animal v = collection.get(t);
            String type = v.getClass().toString().substring(v.getClass().toString().lastIndexOf(".")+1).toLowerCase();
            String s = "{\t\"type\": \""
                    +v.getClass().toString().substring(v.getClass().toString().lastIndexOf(".")+1).toLowerCase()+
                    "\",\t\"name\": \""+
                    v.getName()+"\",\t\"home\": \""+
                    v.getHome()+"\",\t\"weight\": \""+
                    v.getWeight()+"\",\t\"coord\": {\t\"x\": "+
                    v.getCoord().getX()+",\t\t\"y\": "+
                    v.getCoord().getY()+"\t}";
            if (v.getActions().size()>0) {
                s += ",\t\"actions\": [";
                for (String string: v.getActions()) {
                    s += "\t\t\"" + string + "\",";
                }
                s = s.substring(0,s.length()-1);
                s += "\t]";
            }
            if (v.getClass().equals(Tiger.class)&&((Tiger)v).getActionsForTongue().size()>0) {
                Tiger tiger = (Tiger)v;
                s += ",\t\"actionsForTongue\": [";
                for (String string: tiger.getActionsForTongue()) {
                    s += "\t\t\"" + string + "\",";
                }
                s = s.substring(0,s.length()-1);
                s += "\t]";
            }
            s += "}";
            return s;
        }).reduce((s1,s2)->s1+s2).orElse("Коллекция пуста!");
        /*collection.forEach((k,v)->{
            String type = v.getClass().toString().substring(v.getClass().toString().lastIndexOf(".")+1).toLowerCase();
            String s = "{\t\"type\": \""
                    +v.getClass().toString().substring(v.getClass().toString().lastIndexOf(".")+1).toLowerCase()+
                    "\",\t\"name\": \""+
                    v.getName()+"\",\t\"home\": \""+
                    v.getHome()+"\",\t\"weight\": \""+
                    v.getWeight()+"\",\t\"coord\": {\t\"x\": "+
                    v.getCoord().getX()+",\t\t\"y\": "+
                    v.getCoord().getY()+",\t\t\"z\": "+
                    v.getCoord().getZ()+"\t}";
            if (v.getActions().size()>0) {
                s += ",\t\"actions\": [";
                for (String string: v.getActions()) {
                    s += "\t\t\"" + string + "\",";
                }
                s = s.substring(0,s.length()-1);
                s += "\t]";
            }
            if (v.getClass().equals(Tiger.class)&&((Tiger)v).getActionsForTongue().size()>0) {
                Tiger tiger = (Tiger)v;
                s += ",\t\"actionsForTongue\": [";
                for (String string: tiger.getActionsForTongue()) {
                    s += "\t\t\"" + string + "\",";
                }
                s = s.substring(0,s.length()-1);
                s += "\t]";
            }
            s += "}";
            result += s;
        });*/
    }
    /**
     * Метод, инкапсулирующий в себе интерактивное взаимодествие
     * с пользователем через заранее заданные команды-методы
     * класса {@link AnimalCollection}, организуя изменение коллекции
     * {@link AnimalCollection#collection} и ее ввод-вывод в файл.
     * @param in поток ввода данных
     * @param way путь к файлу, хранящему коллекцию {@link AnimalCollection#collection}
     * @see AnimalCollection#removeAll(String)
     * @see AnimalCollection#insert(String)
     * @see AnimalCollection#save(String)
     * @see AnimalCollection#remove(String)
     * @see AnimalCollection#removeGreaterKey(String)
     */
    public String input(Scanner in, String way, boolean output) throws IllegalArgumentException {
        String lexeme = in.nextLine();
        return input(lexeme,way,output);
    }
    public String input(String lexeme, String way, boolean output) throws IllegalArgumentException {
        switch (lexeme.contains(" ")?lexeme.toLowerCase().substring(0,lexeme.indexOf(" ")):lexeme.toLowerCase()) {
            case "remove_all":
                removeAll(lexeme.substring(lexeme.indexOf(" "),lexeme.length()).trim());
                return null;
            case "insert":
                insert(lexeme.substring(lexeme.indexOf(" "),lexeme.length()).trim());
                return null;
            case "save":
                try {save(way); } catch (Exception e) {e.printStackTrace();}
                return null;
            case "remove_greater_key":
                removeGreaterKey(lexeme.substring(lexeme.indexOf(" "),lexeme.length()).trim());
                return null;
            case "remove":
                remove(lexeme.substring(lexeme.indexOf(" "),lexeme.length()).trim());
                return null;
            case "list":
                if (output)
                    return list();
                else {
                    list(System.out);
                    return null;
                }
            case "exit":
                System.exit(0);
            default:
                throw new IllegalArgumentException();
                //обработка неправильного jsona
                //default Тип животного
                //парсер
        }
    }
    public Object[][] data(int size) {
        List<Animal> list = new ArrayList<>(collection.values());
        Object[][] data = new Object[list.size()][size];
        for (int i = 0; i < list.size(); ++i) {
            data[i] = list.get(i).toRow();
        }
        return data;
    }
    public Map<Coord,Animal> getLikeMap() {
        return collection;
    }
}
