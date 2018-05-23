package com.shilko.ru;

import javafx.util.Pair;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
        if (JOptionPane.showConfirmDialog(frame, "Вы действительно хотите выйти?", "Закрытие программы", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == 0) {
            try {
                collection.save(fileName);
            } catch (Exception w) {
                JOptionPane.showMessageDialog(null, "Сохранение не удалось!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        }
    }

    private static class ServerGUI extends JFrame {
        private Font font = new Font("TimesRoman", Font.BOLD, 20);
        private Font font2 = new Font("Font", Font.PLAIN, 15);

        private ServerGUI() {
            super("Server");
            UIManager.put("OptionPane.messageFont", font);
            UIManager.put("OptionPane.buttonFont", font);
            login(this, password);
        }

        private void login(JFrame frame, String password) { //метод, отвечающий за авторизацию, принимает блокируемый фрейм и пароль
            JFrame login = new JFrame("Авторизация");
            login.setFont(font);
            login.setSize(700, 155);
            login.setResizable(false);
            login.setLocationRelativeTo(null);
            login.setFont(new Font("font", Font.BOLD, 50));
            login.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            JPasswordField passwordField = new JPasswordField();
            passwordField.setFont(font);
            passwordField.setEchoChar('*');
            JRadioButton showPassword = new JRadioButton("Показывать пароль");
            showPassword.setFont(font);
            showPassword.addActionListener((event) -> {
                passwordField.setEchoChar(showPassword.isSelected() ? 0 : '*');
            });
            JLabel label = new JLabel("Введите пароль: ");
            label.setFont(font);
            JButton okey = new JButton("OK");
            okey.setFont(font);
            okey.addActionListener((event) -> {
                if (Arrays.equals(passwordField.getPassword(), password.toCharArray())) {
                    login.dispose();
                    frame.setEnabled(true);
                    frame.setVisible(true);
                    synchronized (monitor) {
                        isLogin = true;
                        monitor.notifyAll();
                    }
                    init();
                } else
                    JOptionPane.showMessageDialog(login, "Неверно введен пароль!!!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
            });
            JButton cancel = new JButton("Отмена");
            cancel.addActionListener((event) -> exit(login));
            cancel.setFont(font);
            login.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
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
            class IntegerTextField extends JTextField {
                private IntegerTextField() {
                    super();
                    ((AbstractDocument)getDocument()).setDocumentFilter(new DocumentFilter() {
                        @Override
                        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
                                throws BadLocationException {
                            if (string == null) return;
                            replace(fb, offset, 0, string, attr);
                        }
                        @Override
                        public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
                            replace(fb, offset, length, "", null);
                        }
                        @Override
                        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                                throws BadLocationException {
                            fb.replace(offset, length, checkInput(text, offset), attrs);
                        }
                        private String checkInput(String proposedValue, int offset) throws BadLocationException {
                            // Убираем все пробелы из строки для вставки
                            StringBuilder temp = new StringBuilder(getText());
                            temp.insert(offset,proposedValue);
                            if (temp.length()>1 && temp.charAt(0)=='0') {
                                Toolkit.getDefaultToolkit().beep();
                                return "";
                            }
                            return proposedValue.replaceAll("(\\D)","");
                        }
                    });
                }
            }
            this.setFont(font);
            this.setSize(800, 800);
            this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    exit((JFrame) e.getComponent());
                }
            });
            FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 10, 10);
            this.setLayout(layout);
            String[] columnNames = {
                    "Вид", "Имя", "Координата Х", "Координата У", "Дом", "Вес", "Цвет"
            };
            class MyTable extends JTable {
                private DefaultTableModel model;

                @Override
                public boolean isCellEditable(int a, int b) {
                    return false;
                }

                private MyTable(Object[][] data, Object[] columnNames) {
                    super(data, columnNames);
                    model = new DefaultTableModel(data, columnNames) {
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
                    model.fireTableRowsInserted(model.getRowCount() - 1, model.getRowCount() - 1);
                }

                public void removeRow(int number) {
                    model.removeRow(number);
                    model.fireTableRowsDeleted(number, number);
                }
            }
            Object[][] data = collection.data(columnNames.length);
            MyTable table = new MyTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setPreferredSize(new Dimension(673, 200));
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
                    if (this.getText().isEmpty()) {
                        super.setText("");
                        this.setForeground(Color.BLACK);
                        showingHint = false;
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (this.getText().isEmpty()) {
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
                    "Tiger", "Kangaroo", "Rabbit", "RealAnimal", "All"
            });
            type.setFont(font2);
            JLabel nameLabel = new JLabel("Введите имя: ");
            nameLabel.setFont(font2);
            JTextField name = new JTextField();
            name.setFont(font2);
            JLabel xLabel = new JLabel("Введите Х: ");
            xLabel.setFont(font2);
            IntegerTextField x = new IntegerTextField();
            x.setFont(font2);
            JLabel yLabel = new JLabel("Введите Y: ");
            yLabel.setFont(font2);
            IntegerTextField y = new IntegerTextField();
            y.setFont(font2);
            JLabel homeLabel = new JLabel("Введите дом: ");
            homeLabel.setFont(font2);
            JTextField home = new JTextField();
            home.setFont(font2);
            JLabel weightLabel = new JLabel("Введите вес: ");
            weightLabel.setFont(font2);
            IntegerTextField weight = new IntegerTextField();
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

            Font font = new Font("Font", Font.BOLD, 15);
            JMenuBar menuBar = new JMenuBar();
            JMenu collectionMenu = new JMenu("Collection");
            collectionMenu.setFont(font);
            collectionMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JMenuItem loadItem = new JMenuItem("Load");
            loadItem.addActionListener((event) -> {
                try {
                    collection.load(fileName);
                    for (int i = table.getRowCount() - 1; i >= 0; --i)
                        table.removeRow(i);
                    Arrays.stream(collection.data(7)).forEach(table::addRow);
                    scrollPane.revalidate();
                    JOptionPane.showMessageDialog(this, "Записано!", "Load", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Загрузка не удалась!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });
            loadItem.setFont(font);
            collectionMenu.add(loadItem);
            JMenuItem saveItem = new JMenuItem("Save");
            saveItem.addActionListener((event) -> {
                try {
                    collection.save(fileName);
                    JOptionPane.showMessageDialog(this, "Сохранено!", "Save", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Запись не удалась!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });
            saveItem.setFont(font);
            collectionMenu.add(saveItem);
            collectionMenu.addSeparator();
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.setFont(font);
            collectionMenu.add(exitItem);
            exitItem.addActionListener((event) -> exit(this));
            menuBar.add(collectionMenu);
            this.setJMenuBar(menuBar);


            JPanel panel = new JPanel();
            JButton insert = new JButton("Insert");
            insert.setFont(font2);
            insert.addActionListener((event) -> {
                //table.addRow(data[0].length,collection.insert(textArea.getText().trim()));
                String type1 = (String) type.getSelectedItem();
                String name1 = name.getText().trim();
                String x1 = x.getText().trim();
                String y1 = y.getText().trim();
                String home1 = home.getText().trim();
                String weight1 = weight.getText().trim();
                if (!checkValues(type1, name1, x1, y1, home1, weight1,false))
                    return;
                try {
                    Object[] row = collection.insert(type1, name1, Integer.parseInt(x1), Integer.parseInt(y1), home1, Integer.parseInt(weight1));
                    if (row != null) {
                        table.addRow(row);
                        scrollPane.revalidate();
                        JOptionPane.showMessageDialog(this, "Животное успешно добавлено!", "Insert", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Животное с такими координатами уже существует!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат команды!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });
            panel.add(insert);

            JButton remove = new JButton("Remove");
            remove.setFont(font2);
            remove.addActionListener((event) -> {
                try {
                    String x1 = x.getText().trim();
                    String y1 = y.getText().trim();
                    if (!checkValues(null, null, x1, y1, null, null,false))
                        return;
                    Pair<Boolean, Coord> pair = collection.remove(new Coord(Integer.parseInt(x1), Integer.parseInt(y1)));
                    if (!pair.getKey()) {
                        JOptionPane.showMessageDialog(this, "Животного с таким ключом нет!", "Remove", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Vector vector = table.model.getDataVector();
                        for (int i = 0; i < vector.size(); ++i) {
                            if (((Vector) (vector.elementAt(i))).elementAt(2).equals(pair.getValue().getX()) &&
                                    ((Vector) (vector.elementAt(i))).elementAt(3).equals(pair.getValue().getY())) {
                                table.removeRow(i);
                                scrollPane.revalidate();
                                JOptionPane.showMessageDialog(this, "Животное успешно удалено!", "Remove", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Неверный формат команды!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });
            panel.add(remove);

            JButton edit = new JButton("Edit");
            edit.setFont(font2);
            edit.addActionListener((event) -> {
                String type1 = ((String) type.getSelectedItem()).length() == 0 ? null : (String) type.getSelectedItem();
                String name1 = name.getText().trim().length() == 0 ? null : name.getText().trim();
                String x1 = x.getText().trim().length() == 0 ? null : x.getText().trim();
                String y1 = y.getText().trim().length() == 0 ? null : y.getText().trim();
                String home1 = home.getText().trim().length() == 0 ? null : home.getText().trim();
                String weight1 = weight.getText().trim().length() == 0 ? null : weight.getText().trim();
                if (!checkValues(type1, name1, x1, y1, home1, weight1,true))
                    return;
                List<Integer> rows = Arrays.stream(table.getSelectedRows()).sorted().boxed().collect(Collectors.toList());
                Collections.reverse(rows);
                if (rows.size() == 0) {
                    JOptionPane.showMessageDialog(this, "Животные не выделены!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (x1 != null || y1 != null) {
                    JOptionPane.showMessageDialog(this, "Редактирование не может быть воспроизведено из-за возможной потери целостности данных!\nНевозможно редактировать координаты.", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                rows.forEach(e -> {
                    Vector vector = ((DefaultTableModel) table.getModel()).getDataVector();
                    Animal animal = collection.getAnimal(new Coord((int) ((Vector) vector.elementAt(e)).elementAt(2), (int) ((Vector) vector.elementAt(e)).elementAt(3)));
                    //collection.remove(new Coord((int) ((Vector) vector.elementAt(e)).elementAt(2), (int) ((Vector) vector.elementAt(e)).elementAt(3)));
                    //Class animalClass = animal.getClass();
                    Animal newAnimal = null;
                    try {
                        String type2 = type1;
                        if (type1.trim().toLowerCase().equals("all"))
                            type2 = animal.getClass().toString().substring(animal.getClass().toString().lastIndexOf(".")+1,animal.getClass().toString().length());
                        newAnimal = (Animal)Class.forName("com.shilko.ru."+type2).getConstructor(String.class,String.class,int.class,int.class,int.class).newInstance(name1 == null ? animal.getName() : name1, home1 == null ? animal.getHome() : home1,
                                x1 == null?animal.getCoord().getX():Integer.parseInt(x1), y1 == null?animal.getCoord().getY():Integer.parseInt(y1), weight1 == null ? animal.getWeight() : Integer.parseInt(weight1));
                    } catch (Exception w) {w.printStackTrace();}
                    if (collection.remove(newAnimal.getCoord()).getKey()) {
                        table.removeRow(e);
                        table.addRow(collection.insert(newAnimal));
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Животное с такими координатами уже существует!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                    }
                });
                });
                panel.add(edit);

                JButton removeFromTable = new JButton("RemoveFromTable");
                removeFromTable.setFont(font2);
                removeFromTable.addActionListener((event) -> {
                    List<Integer> rows = Arrays.stream(table.getSelectedRows()).sorted().boxed().collect(Collectors.toList());
                    Collections.reverse(rows);
                    if (rows.size() == 0) {
                        JOptionPane.showMessageDialog(this, "Животные не выделены!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                    rows.forEach(e -> {
                        Vector vector = tableModel.getDataVector();
                        collection.remove(new Coord((int) ((Vector) vector.elementAt(e)).elementAt(2), (int) ((Vector) vector.elementAt(e)).elementAt(3)));
                    });
                    rows.forEach(tableModel::removeRow);
                    JOptionPane.showMessageDialog(this, "Животные успешно удалены!", "RemoveFromTable", JOptionPane.INFORMATION_MESSAGE);
                });
                panel.add(removeFromTable);

                JButton removeGreaterKey = new JButton("RemoveGreaterKey");
                removeGreaterKey.setFont(font2);
                removeGreaterKey.addActionListener((event) -> {
                    String x1 = x.getText().trim();
                    String y1 = y.getText().trim();
                    if (!checkValues(null, null, x1, y1, null, null,false))
                        return;
                    try {
                        List<Coord> list = collection.removeGreaterKey(new Coord(Integer.parseInt(x1), Integer.parseInt(y1)));
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
                            JOptionPane.showMessageDialog(this, "Животные успешно удалены!", "RemoveFromTable", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Неверный формат команды!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                });
                panel.add(removeGreaterKey);
                this.add(panel);

                this.pack();
                this.setMinimumSize(new Dimension(700, 440));
                this.setSize(this.getMinimumSize());
                this.setLocationRelativeTo(null);

                //this.setResizable(false); ///???????

                this.setVisible(true);
            }

            private boolean checkValues (String type, String name, String x, String y, String home, String weight, boolean isEdited){
                boolean result = true;
                String message = "";
                if (type != null) {
                    switch (type.trim().toLowerCase()) {
                        case "tiger":
                        case "kangaroo":
                        case "realanimal":
                        case "rabbit":
                            break;
                        default:
                            if (!isEdited || !type.trim().toLowerCase().equalsIgnoreCase("all")) {
                                message += "тип, ";
                                result = false;
                            }
                    }
                }
                if (name != null && name.trim().length() == 0) {
                    message += "имя, ";
                    result = false;
                }
                if (x != null) {
                    try {
                        if (Integer.parseInt(x) < 0)
                            throw new NumberFormatException();
                        if (x.charAt(0)=='0'&&x.length()>1)
                            throw new NumberFormatException();
                    } catch (Exception e) {
                        message += "координата Х, ";
                        result = false;
                    }
                }
                if (y != null) {
                    try {
                        if (Integer.parseInt(y) < 0)
                            throw new NumberFormatException();
                        if (y.charAt(0)=='0'&&y.length()>1)
                            throw new NumberFormatException();
                    } catch (Exception e) {
                        message += "координата У, ";
                        result = false;
                    }
                }
                if (home != null && home.trim().length() == 0) {
                    message += "дом, ";
                    result = false;
                }
                if (weight != null) {
                    try {
                        if (Integer.parseInt(weight) < 0 || Integer.parseInt(weight) > 1000)
                            throw new NumberFormatException();
                        if (weight.charAt(0)=='0'&&weight.length()>1)
                            throw new NumberFormatException();
                    } catch (Exception e) {
                        message += "вес, ";
                        result = false;
                    }
                }
                if (!result) {
                    message = message.substring(0, 1).toUpperCase() + message.substring(1, message.length() - 2);
                    JOptionPane.showMessageDialog(this, message + " указаны неверно!", "Ошибка", JOptionPane.ERROR_MESSAGE);
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
        public static void main(String... args) {
            fileName = args[0];
            try {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        collection.save(fileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                collection.load(fileName);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Загрузка не удалась!", "Ошибка", JOptionPane.ERROR_MESSAGE);
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
                    executor.execute(new ThreadServer(client, collection, fileName));
                }
                executor.shutdown();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Сервер уже открыт!",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                try {
                    collection.save(fileName);
                } catch (Exception w) {
                    JOptionPane.showMessageDialog(null, "Сохранение не удалось!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                }
                System.exit(0);
            }
        }
    }
