package com.shilko.ru;
import javafx.util.Pair;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private final static int port = 11111;
    private static String password = "password";
    private final static int sizeOfPool = 5;
    private static String fileName;
    private final static Object monitor = new Object();
    private static boolean isLogin = false;
    private static AnimalCollection collection = new AnimalCollection();
    private static ExecutorService executor = Executors.newFixedThreadPool(sizeOfPool);
    public static void exit(JFrame frame) { //метод, вызывающий фрейм закрытия, принимает закрывающийся фрейм
        if (JOptionPane.showConfirmDialog(frame,"Вы действительно хотите выйти?","Закрытие программы",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE)==0) {
            try {collection.save(fileName); } catch (Exception w) {
                JOptionPane.showMessageDialog(null,"Сохранение не удалось!","Ошибка!",JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        }
    }
    private static class ServerGUI extends JFrame {
        private Font font = new Font("TimesRoman", Font.BOLD, 20);
        private Font font2 = new Font("Font", Font.PLAIN,15);
        private ServerGUI() {
            super("Server");
            UIManager.put("OptionPane.messageFont", font);
            UIManager.put("OptionPane.buttonFont", font);
            login(this,password);
        }
        private void login(JFrame frame, String password) { //метод, отвечающий за авторизацию, принимает блокируемый фрейм и пароль
            JFrame login = new JFrame("Авторизация");
            login.setFont(font);
            login.setSize(700,155);
            login.setResizable(false);
            login.setLocationRelativeTo(null);
            login.setFont(new Font("font", Font.BOLD,50));
            login.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            JPasswordField passwordField = new JPasswordField();
            passwordField.setFont(font);
            passwordField.setEchoChar('*');
            JRadioButton showPassword = new JRadioButton("Показывать пароль");
            showPassword.setFont(font);
            showPassword.addActionListener((event)->{
                passwordField.setEchoChar(showPassword.isSelected()?0:'*');
            });
            JLabel label = new JLabel("Введите пароль: ");
            label.setFont(font);
            JButton okey = new JButton("OK");
            okey.setFont(font);
            okey.addActionListener((event)-> {
                if (Arrays.equals(passwordField.getPassword(),password.toCharArray())) {
                    login.dispose();
                    frame.setEnabled(true);
                    frame.setVisible(true);
                    synchronized (monitor) {
                        isLogin = true;
                        monitor.notifyAll();
                    }
                    init();
                }
                else JOptionPane.showMessageDialog(login,"Неверно введен пароль!!!","Ошибка!", JOptionPane.ERROR_MESSAGE);
            });
            JButton cancel = new JButton("Отмена");
            cancel.addActionListener((event)->exit(login));
            cancel.setFont(font);
            login.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    exit(login);
                }
            });
            /*login.add(label);
            login.add(passwordField);
            login.add(okey);
            login.add(cancel);*/

            /*login.setLayout(new GridBagLayout());
            GridBagConstraints textFieldConstraints = new GridBagConstraints();
            textFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
            textFieldConstraints.ipady = 5;
            textFieldConstraints.gridwidth = 2;
            textFieldConstraints.gridx = 0;
            textFieldConstraints.gridy = 0;
            login.add(label, textFieldConstraints);
            textFieldConstraints.weightx = 0.5;
            textFieldConstraints.gridx = 1;
            login.add(passwordField, textFieldConstraints);
            textFieldConstraints.weightx = 0.4;
            textFieldConstraints.gridwidth = 1;
            textFieldConstraints.gridx = 0;
            textFieldConstraints.gridy = 1;
            textFieldConstraints.insets = new Insets(10,100,0,0);
            login.add(okey, textFieldConstraints);
            textFieldConstraints.gridx = 1;
            textFieldConstraints.gridy = 1;
            textFieldConstraints.insets = new Insets(10,10,0,0);
            login.add(cancel, textFieldConstraints);*/

            GroupLayout layout = new GroupLayout(login.getContentPane());
            login.getContentPane().setLayout(layout);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addComponent(label)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(passwordField)
                            .addComponent(showPassword))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(okey)
                            .addComponent(cancel))
            );
            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(label)
                            .addComponent(passwordField)
                            .addComponent(okey))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(showPassword)
                            .addComponent(cancel))
            );
            login.getRootPane().setDefaultButton(okey);
            login.setVisible(true);
            frame.setEnabled(false);
        }
        private void init() {
            this.setFont(font);
            this.setSize(800,800);
            this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            this.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    exit((JFrame)e.getComponent());
                }
            });
            FlowLayout layout = new FlowLayout(FlowLayout.CENTER,10,10);
            this.setLayout(layout);
            String[] columnNames = {
                    "Вид","Имя", "Координата Х", "Координата У", "Дом", "Вес", "Цвет"
            };
            class MyTable extends JTable {
                private DefaultTableModel model;
                @Override public boolean isCellEditable(int a, int b) {return false;}
                public MyTable(Object[][] data, Object[] columnNames) {
                    super(data,columnNames);
                    model = new DefaultTableModel(data,columnNames) {
                        @Override
                        public Class getColumnClass(int column) {
                            switch (column) {
                                case 2:
                                case 3:
                                case 5:
                                    return Double.class;
                                default:
                                    return String.class;

                            }
                        }
                    };
                    //model.setColumnIdentifiers(columnNames);
                    this.setModel(model);
                    getColumnModel().getColumn(0).setPreferredWidth(100);
                    getColumnModel().getColumn(2).setPreferredWidth(100);
                    getColumnModel().getColumn(3).setPreferredWidth(100);
                    getColumnModel().getColumn(4).setPreferredWidth(120);
                    getColumnModel().getColumn(6).setPreferredWidth(100);
                    setMinimumSize(getSize());
                    getTableHeader().setFont(font2);
                    setFont(font2);
                    setFillsViewportHeight(true);
                    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(model);
                    rowSorter.setRowFilter(null);
                    //setRowSorter(rowSorter);
                }
                public void addRow(Object[] row) {
                    model.addRow(row);
                    model.fireTableRowsInserted(model.getRowCount()-1,model.getRowCount()-1);
                }
                public void removeRow(int number) {
                    model.removeRow(number);
                    model.fireTableRowsDeleted(number,number);
                }
            }
            Object[][] data = collection.data(columnNames.length);
            MyTable table = new MyTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setPreferredSize(new Dimension(673,200));
            class HintTextField extends JTextArea implements FocusListener {

                private final String hint;
                private boolean showingHint;

                public HintTextField(final String hint) {
                    super(hint);
                    this.hint = hint;
                    this.showingHint = true;
                    this.setForeground(Color.GRAY);
                    super.addFocusListener(this);
                }

                @Override
                public void focusGained(FocusEvent e) {
                    if(this.getText().isEmpty()) {
                        super.setText("");
                        this.setForeground(Color.BLACK);
                        showingHint = false;
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if(this.getText().isEmpty()) {
                        super.setText(hint);
                        this.setForeground(Color.GRAY);
                        showingHint = true;
                    }
                }

                @Override
                public String getText() {
                    return showingHint ? "" : super.getText();
                }
            }
            //HintTextField textArea = new HintTextField("Введите элемент коллекции: ") ;
            //textArea.setFont(font2);
            //textArea.setLineWrap(true);
            //textArea.setWrapStyleWord(true);
            //JScrollPane scrollPane1 = new JScrollPane(textArea);
            //scrollPane1.setPreferredSize(new Dimension(673, 200));
            //this.add(scrollPane1);
            this.add(scrollPane);

            JPanel panel2 = new JPanel();
            GroupLayout groupLayout = new GroupLayout(panel2);
            panel2.setLayout(groupLayout);
            JLabel typeLabel = new JLabel("Выберите тип: ");
            typeLabel.setFont(font2);
            JComboBox<String> type = new JComboBox<>(new String[]{
                    "Tiger", "Kangaroo", "Rabbit", "RealAnimal"
            });
            type.setFont(font2);
            JLabel nameLabel = new JLabel("Введите имя: ");
            nameLabel.setFont(font2);
            JTextField name = new JTextField();
            name.setFont(font2);
            JLabel xLabel = new JLabel("Введите Х: ");
            xLabel.setFont(font2);
            JTextField x = new JTextField();
            x.setFont(font2);
            JLabel yLabel = new JLabel("Введите Y: ");
            yLabel.setFont(font2);
            JTextField y = new JTextField();
            y.setFont(font2);
            JLabel homeLabel = new JLabel("Введите дом: ");
            homeLabel.setFont(font2);
            JTextField home = new JTextField();
            home.setFont(font2);
            JLabel weightLabel = new JLabel("Введите вес: ");
            weightLabel.setFont(font2);
            JTextField weight = new JTextField();
            weight.setFont(font2);

            groupLayout.setAutoCreateGaps(true);
            groupLayout.setAutoCreateContainerGaps(true);
            groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(typeLabel)
                            .addComponent(nameLabel)
                            .addComponent(xLabel)
                            .addComponent(yLabel)
                            .addComponent(homeLabel)
                            .addComponent(weightLabel))
                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(type)
                            .addComponent(name)
                            .addComponent(x)
                            .addComponent(y)
                            .addComponent(home)
                            .addComponent(weight))
            );
            groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(typeLabel)
                            .addComponent(type))
                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(nameLabel)
                            .addComponent(name))
                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(xLabel)
                            .addComponent(x))
                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(yLabel)
                            .addComponent(y))
                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(homeLabel)
                            .addComponent(home))
                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(weightLabel)
                            .addComponent(weight))
            );
            this.add(panel2);

            Font font = new Font("Font", Font.BOLD,15);
            JMenuBar menuBar = new JMenuBar();
            JMenu collectionMenu = new JMenu("Collection");
            collectionMenu.setFont(font);
            JMenuItem loadItem = new JMenuItem("Load");
            loadItem.addActionListener((event)->{
                try {
                    collection.load(fileName);
                    for (int i = table.getRowCount() - 1; i >= 0; --i)
                        table.removeRow(i);
                    Arrays.stream(collection.data(7)).forEach(table::addRow);
                    scrollPane.revalidate();
                    JOptionPane.showMessageDialog(this,"Записано!","Load", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,"Загрузка не удалась!","Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });
            loadItem.setFont(font);
            collectionMenu.add(loadItem);
            JMenuItem saveItem = new JMenuItem("Save");
            saveItem.addActionListener((event)->{
                try {
                    collection.save(fileName);
                    JOptionPane.showMessageDialog(this,"Сохранено!","Save", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,"Запись не удалась!","Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });
            saveItem.setFont(font);
            collectionMenu.add(saveItem);
            collectionMenu.addSeparator();
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.setFont(font);
            collectionMenu.add(exitItem);
            exitItem.addActionListener((event)->exit(this));
            menuBar.add(collectionMenu);
            this.setJMenuBar(menuBar);


            JPanel panel = new JPanel();
            JButton insert = new JButton("Insert");
            insert.setFont(font2);
            insert.addActionListener((event)-> {
                    //table.addRow(data[0].length,collection.insert(textArea.getText().trim()));
                String type1 = (String)type.getSelectedItem();
                String name1 = name.getText().trim();
                String x1 = x.getText().trim();
                String y1 = y.getText().trim();
                String home1 = home.getText().trim();
                String weight1 = weight.getText().trim();
                if (!checkValues(type1,name1,x1,y1,home1,weight1))
                    return;
                try {
                    Object[] row = collection.insert(type1,name1,Integer.parseInt(x1),Integer.parseInt(y1),home1,Integer.parseInt(weight1));
                        if (row != null) {
                            table.addRow(row);
                            scrollPane.revalidate();
                            JOptionPane.showMessageDialog(this, "Животное успешно добавлено!", "Insert", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "Животное с такими координатами уже существует!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    catch (Exception e) {
                        JOptionPane.showMessageDialog(this,"Неверный формат команды!","Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                });
            panel.add(insert);

            JButton remove = new JButton("Remove");
            remove.setFont(font2);
            remove.addActionListener((event)->{
                try {
                    String x1 = x.getText().trim();
                    String y1 = y.getText().trim();
                    if (!checkValues(null,null,x1,y1,null,null))
                        return;
                    Pair<Boolean,Coord> pair = collection.remove(new Coord(Integer.parseInt(x1),Integer.parseInt(y1)));
                    if (!pair.getKey()) {
                        JOptionPane.showMessageDialog(this, "Животного с таким ключом нет!", "Remove", JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        Vector vector = table.model.getDataVector();
                        for (int i = 0; i < vector.size(); ++i) {
                            if (((Vector)(vector.elementAt(i))).elementAt(2).equals(pair.getValue().getX()) &&
                                    ((Vector)(vector.elementAt(i))).elementAt(3).equals(pair.getValue().getY()) ) {
                                table.removeRow(i);
                                scrollPane.revalidate();
                                JOptionPane.showMessageDialog(this, "Животное успешно удалено!", "Remove", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,"Неверный формат команды!","Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });
            panel.add(remove);

            JButton edit = new JButton("Edit");
            edit.setFont(font2);
            edit.addActionListener((event)->{
                String type1 = (String)type.getSelectedItem();
                String name1 = name.getText().trim();
                String x1 = x.getText().trim();
                String y1 = y.getText().trim();
                String home1 = home.getText().trim();
                String weight1 = weight.getText().trim();
                if (!checkValues(type1,name1,x1,y1,home1,weight1))
                    return;
                try {
                    Coord coord = new Coord(Integer.parseInt(x1),Integer.parseInt(y1));
                    Animal animal = collection.getAnimal(coord);
                    if (animal != null) {
                        collection.remove(coord);
                        Object[] row = collection.insert(type1,name1,Integer.parseInt(x1),Integer.parseInt(y1),home1,Integer.parseInt(weight1));
                        Vector vector = table.model.getDataVector();
                        for (int i = 0; i < vector.size(); ++i) {
                            if (((Vector) (vector.elementAt(i))).elementAt(2).equals(coord.getX()) &&
                                    ((Vector) (vector.elementAt(i))).elementAt(3).equals(coord.getY())) {
                                table.removeRow(i);
                                break;
                            }
                        }
                            table.addRow(row);
                            scrollPane.revalidate();
                            JOptionPane.showMessageDialog(this, "Животное успешно отредактировано!", "Edit", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Животное с такими координатами не существует!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,"Неверный формат команды!","Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });
            panel.add(edit);

            JButton removeAll = new JButton("RemoveAll");
            removeAll.setFont(font2);
            removeAll.addActionListener((event)-> {
                String type1 = (String)type.getSelectedItem();
                String name1 = name.getText().trim();
                String x1 = x.getText().trim();
                String y1 = y.getText().trim();
                String home1 = home.getText().trim();
                String weight1 = weight.getText().trim();
                if (!checkValues(type1,name1,x1,y1,home1,weight1))
                    return;
                try {
                    java.util.List<Coord> list = collection.removeAll(type1,name1,Integer.parseInt(x1),Integer.parseInt(y1),home1,Integer.parseInt(weight1));
                    if (list.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Таких животных не найдено!", "RemoveAll", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Vector vector = table.model.getDataVector();
                        list.forEach((e) -> {
                            for (int i = 0; i < vector.size(); ++i) {
                                if (((Vector) (vector.elementAt(i))).elementAt(2).equals(e.getX()) &&
                                        ((Vector) (vector.elementAt(i))).elementAt(3).equals(e.getY())) {
                                    table.removeRow(i);
                                    scrollPane.revalidate();
                                    break;
                                }
                            }
                        });
                        JOptionPane.showMessageDialog(this, "Животные успешно удалены!", "RemoveAll", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,"Неверный формат команды!","Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });
            panel.add(removeAll);

            JButton removeGreaterKey = new JButton("RemoveGreaterKey");
            removeGreaterKey.setFont(font2);
            removeGreaterKey.addActionListener((event)-> {
                String x1 = x.getText().trim();
                String y1 = y.getText().trim();
                if (!checkValues(null,null,x1,y1,null,null))
                    return;
                try {
                    java.util.List<Coord> list = collection.removeGreaterKey(new Coord(Integer.parseInt(x1),Integer.parseInt(y1)));
                    if (list.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Таких животных не найдено!", "RemoveGreaterKey", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Vector vector = table.model.getDataVector();
                        list.forEach((e) -> {
                            for (int i = 0; i < vector.size(); ++i) {
                                if (((Vector) (vector.elementAt(i))).elementAt(2).equals(e.getX()) &&
                                        ((Vector) (vector.elementAt(i))).elementAt(3).equals(e.getY())) {
                                    table.removeRow(i);
                                    scrollPane.revalidate();
                                    break;
                                }
                            }
                        });
                        JOptionPane.showMessageDialog(this, "Животные успешно удалены!", "RemoveAll", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,"Неверный формат команды!","Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });
            panel.add(removeGreaterKey);
            this.add(panel);

            this.pack();
            this.setMinimumSize(new Dimension(700,440));
            this.setSize(this.getMinimumSize());
            this.setLocationRelativeTo(null);

            //this.setResizable(false); ///???????

            this.setVisible(true);
        }
        private boolean checkValues(String type, String name, String x, String y, String home, String weight) {
            boolean result = true;
            if (type != null) {
                switch (type.trim().toLowerCase()) {
                    case "tiger":
                    case "kangaroo":
                    case "realanimal":
                    case "rabbit":
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Неверный тип!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        result = false;
                }
            }
            if (name != null && name.trim().length()==0) {
                JOptionPane.showMessageDialog(this,"Неверное имя!","Ошибка",JOptionPane.ERROR_MESSAGE);
                result = false;
            }
            if (x != null) {
                try {
                    if (Integer.parseInt(x) < 0)
                        throw new NumberFormatException();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Неверная координата Х!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    result = false;
                }
            }
            if (y != null) {
                try {
                    if (Integer.parseInt(y) < 0)
                        throw new NumberFormatException();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Неверная координата Y!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    result = false;
                }
            }
            if (home != null && home.trim().length()==0) {
                JOptionPane.showMessageDialog(this,"Неверный дом!","Ошибка",JOptionPane.ERROR_MESSAGE);
                result = false;
            }
            if (weight != null) {
                try {
                    if (Integer.parseInt(weight) < 0)
                        throw new NumberFormatException();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Неверный вес!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    result = false;
                }
            }
            return result;
        }
    }

    /*private static void createGUI() {
        JFrame jfrm = new JFrame("Server");
        jfrm.setSize(275,100);
        jfrm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JLabel jlab = new JLabel ("lolikek" );
        jfrm.add(jlab);
        jfrm.setVisible(true);
    }*/
    public static void main(String ... args) {
        fileName = args[0];
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(()->{try {collection.save(fileName);} catch (Exception e) {e.printStackTrace();}}));
            collection.load(fileName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Загрузка не удалась!","Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        Thread App = new Thread(Server.ServerGUI::new);
        javax.swing.SwingUtilities.invokeLater(App);
        synchronized (monitor) {
            try {
                while (!isLogin) {
                    monitor.wait();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Произошла ошибка!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
        try (ServerSocket server = new ServerSocket(port)) {
            while (!server.isClosed()) {
                /*if (br.ready()) {
                    try {
                        String output = collection.input(new Scanner(br), args[0], false);
                    }
                    catch (IllegalArgumentException e) {
                        System.out.println("Неверный формат команды!!!");
                    }
                }*/
                Socket client = server.accept();
                executor.execute(new ThreadServer(client,collection,fileName));
            }
            executor.shutdown();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Сервер уже открыт!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            try {collection.save(fileName); } catch (Exception w) {
                JOptionPane.showMessageDialog(null,"Сохранение не удалось!","Ошибка!",JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        }
    }
}
