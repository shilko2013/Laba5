package com.shilko.ru;

import com.sun.istack.internal.NotNull;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Client {
    private final static int port = 11111;

    public static void main(String... args) {
        SwingUtilities.invokeLater(ClientGUI::new);
        /*AnimalCollection collection;
        while (true) {
            collection = getCollection();
            if (collection != null)
                collection.work();
        }*/
    }

    public static class ClientGUI extends JFrame {
        private static Font font = new Font("Font", Font.PLAIN, 15);
        private final static float RATIO = 1.5f;
        Map<Long, Animal> collection = new ConcurrentHashMap<>();
        List<BufferedImage> images = new ArrayList<>();
        MyExecutor executor = new MyExecutor();

        private void loadImages() {
            try {
                images.add(ImageIO.read(new File("tiger_with_bounds.png")));
                images.add(ImageIO.read(new File("kangaroo_with_bounds.png")));
                images.add(ImageIO.read(new File("rabbit_with_bounds1.png")));
                images.add(ImageIO.read(new File("question_mark_with_bounds.png")));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,"Загрузка изображений не удалась!","Ошибка",JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
        class MySlider extends JSlider {
            private JPanel panel;
            private JLabel label;
            private int myValue = 0;

            private int getMyValue() {
                return myValue;
            }

            private void setMyValue(int myValue) {
                this.myValue = myValue;
            }

            private MySlider(String text, int min, int max, int value, int bigStep, int smallStep) {
                super(JSlider.HORIZONTAL, min, max, value);
                panel = new JPanel();
                label = new JLabel(text);
                label.setFont(new Font("Font", Font.PLAIN, 15));
                panel.add(label,BorderLayout.EAST);
                this.setMajorTickSpacing(bigStep);
                this.setMinorTickSpacing(smallStep);
                this.setPaintTicks(true);
                this.setPaintLabels(true);
                panel.add(this,BorderLayout.WEST);
                this.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        JSlider source = (JSlider) e.getSource();
                        setMyValue(source.getValue());
                    }
                });
            }

            private JPanel getPanel() {
                return panel;
            }

            private JLabel getLabel() {
                return label;
            }
        }

        class AnimalButton extends JButton {

            private Animal animal;
            private double weight;
            private int iconNumber;

            private double getWeight() {
                return weight;
            }

            private double getMyX() {
                return animal.getCoord().getX();
            }

            private void setMyX(double x) {
                animal.getCoord().setX((int)x);
            }

            private double getMyY() {
                return animal.getCoord().getY();
            }

            private void setMyY(double y) {
                animal.getCoord().setY((int)y);
            }

            private int getIconNumber() {
                return iconNumber;
            }

            private Animal getAnimal() {
                return animal;
            }

            private AnimalButton(@NotNull Animal animal) {
                super();
                //super(new ImageIcon(images.get(0).getScaledInstance(animal.getWeight(), (int)(images.get(0).getHeight()/(images.get(0).getWidth()/(animal.getWeight()+0.))), Image.SCALE_SMOOTH)));
                init(animal);
            }

            private void init(Animal animal) {
                this.animal = animal;
                weight = animal.getWeight();
                switch (animal.getClass().toString().substring(getClass().toString().lastIndexOf(".") + 1).toLowerCase()) {
                    case "tiger":
                        iconNumber = 0;
                        break;
                    case "kangaroo":
                        iconNumber = 1;
                        break;
                    case "rabbit":
                        iconNumber = 2;
                        break;
                    default:
                        iconNumber = 3;
                        break;
                }
                setIcon(new ImageIcon(images.get(iconNumber).getScaledInstance(animal.getWeight(), (int)(images.get(iconNumber).getHeight()/(images.get(iconNumber).getWidth()/(animal.getWeight()+0.))), Image.SCALE_SMOOTH)));
                setBackground(Color.WHITE);
                //ImageIcon icon = new ImageIcon(images.get(0).getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                setBounds(animal.getCoord().getX()-animal.getWeight()/2,animal.getCoord().getY()-(int)(images.get(iconNumber).getHeight()/(images.get(iconNumber).getWidth()/(animal.getWeight()+0.)))/2,animal.getWeight(), (int)(images.get(iconNumber).getHeight()/(images.get(iconNumber).getWidth()/(animal.getWeight()+0.))));
                //setBounds((int) (animal.getCoord().getX() - Math.round(getWeight() * RATIO / 2)), animal.getCoord().getY() - (int) (getWeight() / 2), (int) Math.round(getWeight() * RATIO), (int) (getWeight()));
                //setForeground(new Color(animal.getColour()[0], animal.getColour()[1], animal.getColour()[2]));
                //setBorder(new RoundedBorder((int) Math.round(getWeight())));
                setToolTipText(this.animal.getName());
                setOpaque(false);
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                //setEnabled(false);
            }

            private void reBounds() {
                setBounds((int)(animal.getCoord().getX()-weight/2),animal.getCoord().getY()-(int)(images.get(iconNumber).getHeight()/(images.get(iconNumber).getWidth()/(weight+0.)))/2,(int)weight, (int)(images.get(iconNumber).getHeight()/(images.get(iconNumber).getWidth()/(weight+0.))));
                //setBounds((int) (animal.getCoord().getX() - Math.round(weight * RATIO / 2)), (int) (animal.getCoord().getY() - weight / 2), (int) (Math.round(weight * RATIO)), (int) weight);
            }

            private void reBorder() {
                //setIcon(new ImageIcon(images.get(iconNumber).getScaledInstance((int)weight, (int)(images.get(iconNumber).getHeight()/(images.get(iconNumber).getWidth()/(weight+0.))), Image.SCALE_SMOOTH)));
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                getIcon().paintIcon(this,g,(int)(animal.getCoord().getX()-(weight/2)),(int)(animal.getCoord().getY()-(images.get(iconNumber).getHeight()/(images.get(iconNumber).getWidth()/(weight+0.)))));
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null)
                    return false;
                if (this == obj)
                    return true;
                if (this.getClass() != obj.getClass())
                    return false;
                AnimalButton animalButton = (AnimalButton) obj;
                return animal.getID()==animalButton.animal.getID();
            }
        }

        class DoubleRunNTimes implements Runnable {
            private Runnable runnable1;
            private Runnable runnable2;
            private int n1;
            private int n2;

            private DoubleRunNTimes(Runnable runnable1, Runnable runnable2, int n1, int n2) {
                this.runnable1 = runnable1;
                this.runnable2 = runnable2;
                this.n1 = n1;
                this.n2 = n2;
            }

            @Override
            public void run() {
                if (n1 > 0) {
                    runnable1.run();
                    n1--;
                } else if (n2 > 0) {
                    runnable2.run();
                    n2--;
                } else n1 = 5 / 0;
            }
        }

        class MyExecutor {
            private ScheduledExecutorService executor;
            private Map<Long, Future<?>> tasks;

            private MyExecutor() {
                init(50);
            }

            private void init(int sizeOfPool) {
                executor = Executors.newScheduledThreadPool(sizeOfPool);
                tasks = new HashMap<>();
            }

            private void addTask(Long ID, Runnable runnable1, Runnable runnable2, int delay, int n1, int n2) {
                if (!tasks.containsKey(ID) || (tasks.containsKey(ID) && tasks.get(ID).isDone())) {
                    tasks.put(ID, executor.scheduleWithFixedDelay(new DoubleRunNTimes(runnable1, runnable2, n1, n2), 0, delay, TimeUnit.MICROSECONDS));
                }
            }

            private void addTask(Long ID, Runnable runnable) {
                if (!tasks.containsKey(ID) || (tasks.containsKey(ID) && tasks.get(ID).isDone()))
                    tasks.put(ID,executor.scheduleWithFixedDelay(runnable,0,10,TimeUnit.MILLISECONDS));
            }

            private boolean isWorked() {
                return tasks.values().stream().anyMatch(e -> !e.isDone() && !e.isCancelled());
            }

            private void shutdown() {
                executor.shutdownNow();
            }

            private List<Long> working() {
                List<Long> list = new ArrayList<>();
                tasks.keySet().forEach((e)-> {
                    if (!tasks.get(e).isDone())
                        list.add(e);
                });
                return list;
            }
        }

        Set<AnimalButton> set = new TreeSet<>((a, b) -> Double.compare(a.getWeight(), b.getWeight()));

        private void updateCollection(JPanel panel) {
            while (true) {
                try {
                    Map<Long,Animal> newCollection = new ConcurrentHashMap<>();
                    List<Long> working = executor.working();
                    getCollection().getLikeMap().forEach((a,b)->{
                        newCollection.put(b.getID(),b);
                        if (working.contains(b.getID())) {
                            b.setCoord(collection.get(b.getID()).getCoord());
                        }
                    });
                    collection = newCollection;
                    break;
                } catch (Exception e) {
                    if (JOptionPane.showConfirmDialog(null, "Не удается получить коллекцию!\nПовторить попытку?", "Ошибка!", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE) == 1)
                        System.exit(0);
                }
            }
            List<Pair<Integer,Integer>> pairs = new ArrayList<>();
            collection.values().forEach((e) -> {
                if ((e.getCoord().getX()>panel.getWidth()-(e).getWeight()/2) || (e.getCoord().getY()>panel.getHeight()-new AnimalButton(e).getHeight()/2)) {
                    pairs.add(new Pair<>(e.getCoord().getX(),e.getCoord().getY()));
                    collection.values().remove(e);
                }
            });
            StringBuilder message = new StringBuilder("Животные со следующими координатами не могут быть отображены:\n");
            pairs.forEach(e-> {
                message.append("x: " +e.getKey()+", y: "+e.getValue()+"\n");
            });
            //message.append(" не могут быть отображены!");
            JTextArea messageArea = new JTextArea(message.toString());
            messageArea.setFont(font);
            messageArea.setEditable(false);
            JScrollPane messageScrollPane = new JScrollPane(messageArea);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageScrollPane.setPreferredSize( new Dimension( 250, 100 ) );
            if (!pairs.isEmpty())
                JOptionPane.showMessageDialog(panel,messageScrollPane,"Ошибка",JOptionPane.ERROR_MESSAGE);
        }

        private void initList() {
            //list.clear();
            collection.keySet().forEach((e) -> {
                set.forEach((e1)->{
                    if (e1.animal.getID()==e)
                        e1.init(collection.get(e));
                });
                if (set.stream().noneMatch((e1)->e1.animal.getID()==e))
                    set.add(new AnimalButton(collection.get(e)));
            });
            set = set.stream().filter((e1)->collection.containsKey(e1.animal.getID())).collect(Collectors.toSet());
        }

        private ClientGUI() {
            super("Client");
            UIManager.put("OptionPane.messageFont", font);
            UIManager.put("OptionPane.buttonFont", font);
            init();
        }

        private void init() {
            loadImages();
            this.setFont(font);
            this.setSize(800, 800);
            this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (JOptionPane.showConfirmDialog(e.getComponent(), "Вы действительно хотите выйти?", "Закрытие программы", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == 0) {
                        System.exit(0);
                    }
                }
            });

            //Font font = new Font("Font", Font.BOLD, 15);
            JMenuBar menuBar = new JMenuBar();
            JMenu collectionMenu = new JMenu("Menu");
            collectionMenu.setFont(font);
            collectionMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JMenu language = new JMenu("Language");
            language.setFont(font);
            collectionMenu.add(language);
            JRadioButtonMenuItem russian = new JRadioButtonMenuItem("Russian");
            russian.setFont(font);
            language.add(russian);
            russian.addActionListener((event) -> {

            });
            russian.setSelected(true);
            JRadioButtonMenuItem norwegian = new JRadioButtonMenuItem("Norwegian");
            norwegian.setFont(font);
            language.add(norwegian);
            norwegian.addActionListener((event) -> {

            });
            JRadioButtonMenuItem albanian = new JRadioButtonMenuItem("Albanian");
            albanian.setFont(font);
            language.add(albanian);
            albanian.addActionListener((event) -> {

            });
            JRadioButtonMenuItem english = new JRadioButtonMenuItem("English");
            english.setFont(font);
            language.add(english);
            english.addActionListener((event) -> {

            });
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(russian);
            buttonGroup.add(norwegian);
            buttonGroup.add(albanian);
            buttonGroup.add(english);
            collectionMenu.addSeparator();
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.setFont(font);
            collectionMenu.add(exitItem);
            exitItem.addActionListener((event) -> {
                if (JOptionPane.showConfirmDialog(this, "Вы действительно хотите выйти?", "Закрытие программы", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == 0) {
                    System.exit(0);
                }
            });
            menuBar.add(collectionMenu);
            //this.setJMenuBar(menuBar); для 8 лабы!!!!!!!!

            JPanel border = new JPanel();
            border.setPreferredSize(new Dimension(800, 1));
            border.setBackground(Color.BLACK);
            this.add(border,BorderLayout.NORTH);

            class Canvas extends JPanel {
                private Graphics2D gr;
                private int size;
                private boolean staticDraw;

                private boolean isStaticDraw() {
                    return staticDraw;
                }

                private void setStaticDraw(boolean staticDraw) {
                    this.staticDraw = staticDraw;
                }

                private Canvas(int size, boolean staticDraw) {
                    this.size = size;
                    this.staticDraw = staticDraw;
                    this.setLayout(null);
                }

                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    gr = (Graphics2D) g;

                    // Делаем белый фон
                    Rectangle2D rect = new Rectangle2D.Double(0, 0, this.getWidth(), this.getHeight());
                    gr.setPaint(Color.WHITE);
                    gr.fill(rect);
                    gr.draw(rect);

                    // Рисуем сетку
                    gr.setPaint(Color.LIGHT_GRAY);
                    gr.setStroke(new BasicStroke(0.2f));
                    for (int y = 0; y <= this.getWidth(); y += size) {
                        gr.draw(new Line2D.Double(0, y, this.getWidth(), y));
                        gr.draw(new Line2D.Double(y * RATIO, 0, y * RATIO, this.getHeight()));
                    }

                    //подписываем все это дело
                    gr.setPaint(Color.BLACK);
                    for (int x = 0; x < this.getWidth(); x += size * RATIO)
                        gr.drawString(Integer.toString(x), x, 10);
                    gr.drawString("X", this.getWidth() - 10, 10);
                    for (int y = 0; y < this.getHeight(); y += size)
                        gr.drawString(Integer.toString(y), 0, y);
                    gr.drawString("Y", 0, this.getHeight() - 10);

                    this.removeAll();
                    if (staticDraw)
                        initList();
                    //set.sort((a, b) -> Double.compare(a.getWeight(), b.getWeight()));
                    set.forEach(this::add);
                    // Рисуем оси
                    /*
                    gr.setStroke(new BasicStroke((float) 1));
                    gr.draw(new Line2D.Double(ox,0,ox,this.getHeight()));
                    gr.draw(new Line2D.Double(0,oy,this.getWidth(),oy));*/
                }
            }

            Canvas canvas = new Canvas(50, true);
            canvas.setMinimumSize(new Dimension(500, 500));
            canvas.setPreferredSize(canvas.getMinimumSize());
            //canvas.setBackground(Color.WHITE);
            this.add(canvas,BorderLayout.CENTER);
            JPanel panel = new JPanel();
            GroupLayout panelLayout = new GroupLayout(panel);
            panel.setLayout(panelLayout);
            panel.setPreferredSize(new Dimension(900, 230));
            //panel.setLayout(new FlowLayout());

            JPanel buttonPanel = new JPanel();
            //buttonPanel.setPreferredSize(new Dimension(300,150));
            buttonPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(0,2,2,2);
            c.gridx = 0; c.gridy = 0; c.gridwidth = 3;

            JLabel buttonLabel = new JLabel("Панель управления");
            buttonLabel.setFont(font);
            buttonPanel.add(buttonLabel,c);
            c.gridy = 1;

            JButton start = new JButton("Start");
            start.setFont(font);
            start.setPreferredSize(new Dimension(120,50));
            buttonPanel.add(start,c);
            c.gridy = 2;

            JButton stop = new JButton("Stop");
            stop.setFont(font);
            stop.addActionListener((event) -> {
                if (executor.isWorked()) {
                    executor.shutdown();
                    executor.init(50);
                    canvas.repaint();
                    JOptionPane.showMessageDialog(this, "Анимация остановлена!", "Stop", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Анимация не запущена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                canvas.setStaticDraw(true);
                canvas.repaint();
            });
            stop.setPreferredSize(new Dimension(120,50));
            buttonPanel.add(stop,c);
            c.gridy = 3;

            JButton update = new JButton("Update");
            update.setFont(font);
            update.addActionListener((event) -> {
                updateCollection(canvas);
                initList();
                /*if (!canvas.isStaticDraw())
                    stop.doClick();*/
                canvas.repaint();
            });
            update.setPreferredSize(new Dimension(120,50));
            buttonPanel.add(update,c);

            JPanel toolKitPanel1 = new JPanel();
            //toolKitPanel1.setPreferredSize(new Dimension(300,500));

            JLabel type = new JLabel("Тип животного:");
            type.setFont(font);
            JPanel typePanel = new JPanel();
            JComboBox<String> types = new JComboBox<>(new String[]{
                    "Tiger", "Kangaroo", "Rabbit", "RealAnimal", "Любой"
            });
            types.setFont(font);
            typePanel.add(type);
            typePanel.add(types);

            JPanel namePanel = new JPanel();
            JLabel nameLabel = new JLabel("Имя животного: ");
            nameLabel.setFont(font);
            JTextField nameField = new JTextField();
            nameField.setPreferredSize(new Dimension(100, 20));
            nameField.setFont(font);
            namePanel.add(nameLabel);
            namePanel.add(nameField);

            JLabel homeLabel = new JLabel("Дом животного: ");
            homeLabel.setFont(font);
            JRadioButton homeOfKenga = new JRadioButton("Домик Кенги");
            homeOfKenga.setFont(font);
            JRadioButton homeOfRabbit = new JRadioButton("Домик Кролика");
            homeOfRabbit.setFont(font);
            JRadioButton australia = new JRadioButton("Австралия");
            australia.setFont(font);
            JRadioButton other = new JRadioButton("Другой дом");
            other.setFont(font);
            ButtonGroup home = new ButtonGroup();
            home.add(homeOfKenga);
            home.add(homeOfRabbit);
            home.add(australia);
            home.add(other);
            JPanel homePanel = new JPanel();
            homePanel.add(homeOfKenga);
            homePanel.add(homeOfRabbit);
            homePanel.add(australia);
            homePanel.add(other);

            JLabel colourLabel = new JLabel("Цвет животного: ");
            colourLabel.setFont(font);
            JPanel colourPanel = new JPanel();
            JCheckBox orange = new JCheckBox("Оранжевый");
            orange.setFont(font);
            JCheckBox brown = new JCheckBox("Коричневый");
            brown.setFont(font);
            JCheckBox white = new JCheckBox("Белый");
            white.setFont(font);
            JCheckBox black = new JCheckBox("Черный");
            black.setFont(font);
            colourPanel.add(orange);
            colourPanel.add(brown);
            colourPanel.add(white);
            colourPanel.add(black);

            GridBagLayout gridBagLayoutToolkit1 = new GridBagLayout();
            toolKitPanel1.setLayout(gridBagLayoutToolkit1);
            GridBagConstraints gridBagConstraintsToolkit1 = new GridBagConstraints();
            gridBagConstraintsToolkit1.gridx = 0; gridBagConstraintsToolkit1.gridy = 0;
            //gridBagConstraintsToolkit1.insets = new Insets(0,5,0,0);
            JPanel typeNamePanel = new JPanel();
            typeNamePanel.add(typePanel);
            typeNamePanel.add(namePanel);
            toolKitPanel1.add(typeNamePanel,gridBagConstraintsToolkit1);
            gridBagConstraintsToolkit1.gridx = 0;
            gridBagConstraintsToolkit1.gridy = 1;
            gridBagConstraintsToolkit1.gridwidth = 2;
            toolKitPanel1.add(homeLabel,gridBagConstraintsToolkit1);
            gridBagConstraintsToolkit1.gridy = 2;
            toolKitPanel1.add(homePanel,gridBagConstraintsToolkit1);
            gridBagConstraintsToolkit1.gridy = 3;
            toolKitPanel1.add(colourLabel,gridBagConstraintsToolkit1);
            gridBagConstraintsToolkit1.gridy = 4;
            toolKitPanel1.add(colourPanel,gridBagConstraintsToolkit1);
            toolKitPanel1.setPreferredSize(new Dimension(300,300));

            JPanel toolKitPanel2 = new JPanel();
            GridBagLayout gridBagLayoutToolkit2 = new GridBagLayout();
            toolKitPanel2.setLayout(gridBagLayoutToolkit2);
            GridBagConstraints gridBagConstraintsToolkit2 = new GridBagConstraints();

            JLabel coordLabel = new JLabel("Координаты животного: ");
            coordLabel.setFont(font);
            MySlider minX = new MySlider("Min X: ", 0, 1800, 0, 400, 100);
            MySlider minY = new MySlider("Min Y: ", 0, 800, 0, 200, 50);
            MySlider maxX = new MySlider("Max X: ", 0, 1800, 0, 400, 100);
            MySlider maxY = new MySlider("Max Y: ", 0, 800, 0, 200, 50);

            JLabel weightLabel = new JLabel("Вес животного: ");
            weightLabel.setFont(font);
            MySlider minWeight = new MySlider("Min: ", 0, 500, 0, 100, 10);
            MySlider maxWeight = new MySlider("Max: ", 0, 500, 0, 100, 10);

            gridBagConstraintsToolkit2.gridy = 0;
            gridBagConstraintsToolkit2.gridx = 0;
            gridBagConstraintsToolkit2.gridwidth = 2;
            int savedIpadX = gridBagConstraintsToolkit2.ipadx;
            toolKitPanel2.add(coordLabel,gridBagConstraintsToolkit2);
            gridBagConstraintsToolkit2.gridwidth = 1;
            gridBagConstraintsToolkit2.gridy = 1;
            gridBagConstraintsToolkit2.ipadx = 180;
            toolKitPanel2.add(minX.getPanel(),gridBagConstraintsToolkit2);
            gridBagConstraintsToolkit2.gridx = 1;
            toolKitPanel2.add(maxX.getPanel(),gridBagConstraintsToolkit2);
            gridBagConstraintsToolkit2.gridx = 0;
            gridBagConstraintsToolkit2.gridy = 2;
            toolKitPanel2.add(minY.getPanel(),gridBagConstraintsToolkit2);
            gridBagConstraintsToolkit2.gridx = 1;
            toolKitPanel2.add(maxY.getPanel(),gridBagConstraintsToolkit2);
            gridBagConstraintsToolkit2.gridy = 3;
            gridBagConstraintsToolkit2.gridx = 0;
            gridBagConstraintsToolkit2.gridwidth = 2;
            gridBagConstraintsToolkit2.ipadx = savedIpadX;
            toolKitPanel2.add(weightLabel,gridBagConstraintsToolkit2);
            gridBagConstraintsToolkit2.ipadx = 180;
            gridBagConstraintsToolkit2.gridwidth = 1;
            gridBagConstraintsToolkit2.gridy = 4;
            toolKitPanel2.add(minWeight.getPanel(),gridBagConstraintsToolkit2);
            gridBagConstraintsToolkit2.gridx = 1;
            toolKitPanel2.add(maxWeight.getPanel(),gridBagConstraintsToolkit2);

            panelLayout.setAutoCreateGaps(true);
            panelLayout.setAutoCreateContainerGaps(true);
            panelLayout.setHorizontalGroup(panelLayout.createSequentialGroup()
                    .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(buttonPanel))
                    .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(toolKitPanel1))
                    .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(toolKitPanel2))
            );
            panelLayout.setVerticalGroup(panelLayout.createSequentialGroup()
                    .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(buttonPanel)
                            .addComponent(toolKitPanel1)
                            .addComponent(toolKitPanel2))
            );

            start.addActionListener((event) -> {
                if (executor.isWorked()) {
                    JOptionPane.showMessageDialog(this, "Анимация уже запущена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                canvas.setStaticDraw(false);
                String message = check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown, true);
                if (message.length()>0) {
                    JOptionPane.showMessageDialog(this, "Нет животных, подходящих по "+message.substring(0,message.length()-2)+".", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    canvas.setStaticDraw(true);
                }
                /*timer.set((event1) -> {
                            animation.forEach((e) -> {
                                e.getKey().setWeight(e.getKey().getWeight() + e.getValue() / 20);
                                e.getKey().reBounds();
                                e.getKey().reBorder();
                                e.getKey().revalidate();
                                canvas.revalidate();
                                canvas.repaint();
                            });
                        }
                        , 100, (event2) -> {
                            animation.forEach((e) -> {
                                e.getKey().setWeight(e.getKey().getWeight() - e.getValue() / 20);
                                if (Double.compare(e.getKey().getWeight(), e.getValue()) < 0) {
                                    animation.forEach(l -> l.getKey().setWeight(l.getValue()));
                                    canvas.setStaticDraw(true);
                                    if (timer.isRunning())
                                        timer.stop2();
                                    timer.reset();
                                    return;
                                }
                                e.getKey().reBounds();
                                e.getKey().reBorder();
                                e.getKey().revalidate();
                                canvas.revalidate();
                                canvas.repaint();
                            });
                        }, 200);
                timer.start1();
                timer.start2();*/
            });

            this.add(panel,BorderLayout.SOUTH);
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            //panel.setPreferredSize(new Dimension(800,200));

            //Map<Coord,Animal> collection = null;
            /*try {
                final Map<Coord,Animal> collection = getCollection().getLikeMap();
                collection.keySet().forEach((e)-> {
                    canvas.add(new AnimalButton(collection.get(e)));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            /*AnimalButton button = new AnimalButton(new Kangaroo("Name","Home",8,8,305));

            canvas.add(button);*/

            types.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            nameField.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            minX.addChangeListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            minY.addChangeListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            maxX.addChangeListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            maxY.addChangeListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            homeOfKenga.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            homeOfRabbit.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            australia.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            other.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            minWeight.addChangeListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            maxWeight.addChangeListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            orange.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            white.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            black.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            brown.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(set, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, white, black, brown,false);
            });
            this.pack();
            this.setMinimumSize(new Dimension(this.getWidth() + 350, this.getHeight()));
            this.setLocationRelativeTo(null);
            this.setVisible(true);
            updateCollection(canvas);
            initList();
            this.repaint();
        }

        private List<Boolean> checkFilter(Animal animal, JComboBox<String> types, JTextField nameField, MySlider
                minX, MySlider maxX, MySlider minY, MySlider maxY,
                                   JRadioButton homeOfKenga, JRadioButton homeOfRabbit, JRadioButton australia, JRadioButton
                                           other, MySlider minWeight, MySlider maxWeight,
                                   JCheckBox orange, JCheckBox grey, JCheckBox black, JCheckBox brown) {
            List<Boolean> booleans = new ArrayList<>();
            booleans.add(((animal.getClass().toString().substring(getClass().toString().lastIndexOf(".") + 1).equalsIgnoreCase(((String) types.getSelectedItem()).trim()) ||
                    ((String) types.getSelectedItem()).trim().equalsIgnoreCase("Любой"))));
            booleans.add((animal.getName().trim().equalsIgnoreCase(nameField.getText().trim()) || nameField.getText().trim().equals("")));
            booleans.add((animal.getCoord().getX() > minX.getMyValue() &&
                    animal.getCoord().getX() < maxX.getMyValue()));
            booleans.add((animal.getCoord().getY() > minY.getMyValue() &&
                    animal.getCoord().getY() < maxY.getMyValue()));
            booleans.add((
                    (animal.getHome().trim().equalsIgnoreCase(homeOfKenga.getText().trim()) &&
                            homeOfKenga.isSelected()) ||
                            (animal.getHome().trim().equalsIgnoreCase(homeOfRabbit.getText().trim()) &&
                                    homeOfRabbit.isSelected()) ||
                            (animal.getHome().trim().equalsIgnoreCase(australia.getText().trim()) &&
                                    australia.isSelected()) ||
                                    other.isSelected()
                    ));
            booleans.add((animal.getWeight() > minWeight.getMyValue() &&
                    animal.getWeight() < maxWeight.getMyValue()));
            booleans.add((
                            (animal.getColourSynonym().trim().equalsIgnoreCase(orange.getText().trim()) &&
                                    orange.isSelected()) ||
                                    (animal.getColourSynonym().trim().equalsIgnoreCase(grey.getText().trim()) &&
                                            grey.isSelected()) ||
                                    (animal.getColourSynonym().trim().equalsIgnoreCase(black.getText().trim()) &&
                                            black.isSelected()) ||
                                    (animal.getColourSynonym().trim().equalsIgnoreCase(brown.getText().trim()) &&
                                            brown.isSelected())
                    ));
            if (booleans.stream().allMatch(e->e))
                booleans = null;
            return booleans;
        }

        private String check(Collection<AnimalButton> list, MyExecutor executor, JComponent canvas, JComboBox<String> types, JTextField nameField, MySlider
                minX, MySlider maxX, MySlider minY, MySlider maxY,
                             JRadioButton homeOfKenga, JRadioButton homeOfRabbit, JRadioButton australia, JRadioButton
                                     other, MySlider minWeight, MySlider maxWeight,
                             JCheckBox orange, JCheckBox grey, JCheckBox black, JCheckBox brown, boolean firstIncluding) {
            if (!firstIncluding && !executor.isWorked())
                return "";
            StringBuilder message = new StringBuilder();
            List<Boolean> booleans = new ArrayList<>(Arrays.asList(false,false,false,false,false,false,false));
            boolean[] somebodyExist = new boolean[1];
            somebodyExist[0] = false;
            list.forEach((e) -> {
                Animal animal = e.getAnimal();
                int weight = animal.getWeight();
                System.out.println(maxX.getMyValue());
                class ManagerMoving {
                    private int randomWidth;
                    private int randomHeight;
                    private int numberOfCircles;
                    private int randomAmplitude;
                    private double cos;
                    private double sin;
                    private int step;
                    private double x;
                    private int savedX, savedY;
                    private int count;
                    private double myY;
                    private Coord coord;
                    private ManagerMoving(int step, Coord coord) {
                        this.step = step;
                        this.coord = coord;
                        reRandom();
                    }
                    private void reRandom() {
                        reCoord();
                        savedX = (int)x;
                        savedY = coord.getY();
                        numberOfCircles = new Random().nextInt(3)+3;
                        randomAmplitude = new Random().nextInt(50)+25;
                        int magicConstant = (int)(images.get(e.getIconNumber()).getHeight()/(images.get(e.getIconNumber()).getWidth()/(animal.getWeight()+0.)));
                        randomWidth = new Random().nextInt(canvas.getWidth()-weight)+weight/2;
                        randomHeight = new Random().nextInt(canvas.getHeight()-magicConstant)+magicConstant/2;
                        cos = (randomWidth-savedX)/(Math.pow(Math.pow(randomWidth-savedX,2)+Math.pow(randomHeight-savedY,2),1./2));
                        sin = (randomHeight-savedY)/(Math.pow(Math.pow(randomWidth-savedX,2)+Math.pow(randomHeight-savedY,2),1./2));
                        count = 0;
                    }
                    private void nextStep() {
                        count++;
                        if (count==step) {
                            reCoord();
                            reRandom();
                            nextStep();
                        }
                        if (randomWidth>savedX)
                            x += (randomWidth-savedX)/(double)step;
                        else
                            x -= (randomWidth-savedX)/(double)step;
                        myY = (savedY+randomAmplitude*Math.sin(Math.PI*numberOfCircles*x/(randomWidth-savedX)));
                    }
                    private void reCoord() {
                        //coord = new Coord((int)e.getMyX(),(int)e.getMyY());
                        x = coord.getX();
                    }
                    private int nextX() {
                        return (int)((x-savedX)*cos-(myY-savedY)*sin+savedX);
                    }
                    private int nextY() {
                        return (int)((x-savedX)*sin+(myY-savedY)*cos+savedY);
                    }
                }
                ManagerMoving managerMoving = new ManagerMoving(400,animal.getCoord());
                if (checkFilter(animal, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown) == null) {
                    executor.addTask(animal.getID(), () -> {
                        managerMoving.nextStep();
                        e.setMyX(managerMoving.nextX());
                        e.setMyY(managerMoving.nextY());
                        //e.setMyX(e.getStepX());
                        //e.setMyY(myY);
                        e.reBounds();
                        e.reBorder();
                        e.revalidate();
                        canvas.revalidate();
                        canvas.repaint();
                    });
                    somebodyExist[0] = true;
                }
                else {
                    List<Boolean> booleans1 = checkFilter(animal, types, nameField, minX, maxX, minY, maxY,
                            homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                            orange, grey, black, brown);
                    for (int i = 0; i < booleans.size(); ++i) {
                        booleans.set(i,(booleans.get(i)||(booleans1.get(i))));
                    }
                }
            });
            if (somebodyExist[0])
                return "";
            else {
                if (booleans.stream().anyMatch(e->!e)) {
                    List<String> errors = new ArrayList<>(Arrays.asList("типу, ", "имени, ", "координате Х, ", "координате У, ", "дому, ", "весу, ", "цвету, "));
                    for (int i = 0; i < booleans.size(); ++i) {
                        if (!booleans.get(i))
                            message.append(errors.get(i));
                    }
                    return message.toString();
                }
                else
                    return "всем параметрам одновременно  ";
            }
        }
    }

    private static AnimalCollection getCollection() throws ClassCastException, ClassNotFoundException, IOException {
        //try {
        SocketChannel sChannel = SocketChannel.open();
        sChannel.configureBlocking(true);
        if (sChannel.connect(new InetSocketAddress("localhost", port))) {
            ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
            oos.writeObject("list");
            Object o = ois.readObject();
            if (o instanceof AnimalCollection)
                return (AnimalCollection) o;
            else
                throw new ClassCastException();
        } else throw new ConnectException();
        /*} catch (ConnectException e) {
            System.out.println("Сервер не доступен!!!");
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Произошла ошибка!!!");
        }*/
        //return null;
    }
}
