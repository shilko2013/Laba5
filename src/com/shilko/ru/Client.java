package com.shilko.ru;

import com.sun.istack.internal.NotNull;
import javafx.util.Pair;
import javax.swing.*;
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
import java.util.logging.*;

public class Client {
    private final static int port;
    private static final Logger logger;
    static {
        port = 11111;
        logger = Logger.getLogger(Client.class.getName());
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("log.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Логирование в файл не будет произведено!","Ошибка",JOptionPane.ERROR_MESSAGE);
        }
        if (fileHandler != null) {
            logger.addHandler(fileHandler);
        }
    }

    public static void main(String... args) {
        logger.info("Program has been started");
        SwingUtilities.invokeLater(ClientGUI::new);
    }

    public static class ClientGUI extends JFrame {
        private Font font = new Font("Font", Font.PLAIN, 15);
        private final float RATIO = 1.5f;
        private Map<Long, Animal> collection = new ConcurrentHashMap<>();
        private List<BufferedImage> images = new ArrayList<>();
        private MyExecutor executor = new MyExecutor();
        private Set<AnimalButton> set = new TreeSet<>((a, b) -> Double.compare(a.getWeight(), b.getWeight()));

        private JMenuBar menuBar;
        private JPanel border;
        private Canvas canvas;
        private JPanel panel;
        private JPanel buttonPanel;
        private JLabel buttonLabel;
        private JButton stop,start,update;
        private JPanel toolKitPanel1;
        private JLabel typeLabel;
        private JComboBox<String> types;
        private JLabel nameLabel;
        private JTextField nameField;
        private JLabel homeLabel;
        private JRadioButton homeOfKanga,homeOfRabbit,australia,other;
        private JLabel colourLabel;
        private JCheckBox orange,brown,white,black;
        private JPanel toolKitPanel2;
        private JLabel coordLabel;
        private MySlider minX,maxX,minY,maxY;
        private JLabel weightLabel;
        private MySlider minWeight,maxWeight;

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
                setBounds(animal.getCoord().getX()-animal.getWeight()/2,animal.getCoord().getY()-(int)(images.get(iconNumber).getHeight()/(images.get(iconNumber).getWidth()/(animal.getWeight()+0.)))/2,animal.getWeight(), (int)(images.get(iconNumber).getHeight()/(images.get(iconNumber).getWidth()/(animal.getWeight()+0.))));
                setToolTipText(this.animal.getName());
                setOpaque(false);
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
            }

            private void reBounds() {
                setBounds((int)(animal.getCoord().getX()-weight/2),animal.getCoord().getY()-(int)(images.get(iconNumber).getHeight()/(images.get(iconNumber).getWidth()/(weight+0.)))/2,(int)weight, (int)(images.get(iconNumber).getHeight()/(images.get(iconNumber).getWidth()/(weight+0.))));
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
                set.forEach(this::add);
            }
        }

        private void loadImages() {
            try {
                images.add(ImageIO.read(new File("tiger_with_bounds.png")));
                images.add(ImageIO.read(new File("kangaroo_with_bounds.png")));
                images.add(ImageIO.read(new File("rabbit_with_bounds1.png")));
                images.add(ImageIO.read(new File("question_mark_with_bounds.png")));
                logger.info("Loading images has been loaded successfully");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,"Загрузка изображений не удалась!","Ошибка",JOptionPane.ERROR_MESSAGE);
                logger.severe("Loading images hasn't been done successfully");
                logger.severe("Program is closed");
                System.exit(0);
            }
        }

        private void updateCollection(JPanel panel) {
            while (true) {
                try {
                    Map<Long,Animal> newCollection = new ConcurrentHashMap<>();
                    List<Long> working = executor.working();
                    getCollection().getLikeMap().forEach((a,b)->{
                        newCollection.put(b.getID(),b);
                        logger.info("Animal "+b+" has been added in collection");
                        if (working.contains(b.getID())) {
                            b.setCoord(collection.get(b.getID()).getCoord());
                        }
                    });
                    collection = newCollection;
                    logger.info("Collection has been updated");
                    break;
                } catch (Exception e) {
                    logger.severe("Updating of collection hasn't been done");
                    if (JOptionPane.showConfirmDialog(null, "Не удается получить коллекцию!\nПовторить попытку?", "Ошибка!", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE) == 1) {
                        logger.severe("Program is closed");
                        System.exit(0);
                    }
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
            JTextArea messageArea = new JTextArea(message.toString());
            messageArea.setFont(font);
            messageArea.setEditable(false);
            JScrollPane messageScrollPane = new JScrollPane(messageArea);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageScrollPane.setPreferredSize( new Dimension( 250, 100 ) );
            if (!pairs.isEmpty()) {
                logger.info("Message about error of unsuitable coordinates has been showed");
                JOptionPane.showMessageDialog(panel, messageScrollPane, "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void initList() {
            collection.keySet().forEach((e) -> {
                set.forEach((e1)->{
                    if (e1.animal.getID()==e) {
                        logger.info("Animal "+collection.get(e)+" has been updated in buttons");
                        e1.init(collection.get(e));
                    }
                });
                if (set.stream().noneMatch((e1)->e1.animal.getID()==e)) {
                    set.add(new AnimalButton(collection.get(e)));
                    logger.info("Animal "+collection.get(e)+" has been added in buttons");
                }
            });
            set = set.stream().filter((e1)->collection.containsKey(e1.animal.getID())).collect(Collectors.toSet());
            logger.info("Animals for mapping has been updated");
        }

        private ClientGUI() {
            super("Client");
            logger.info("Initialization of GUI has been started");
            UIManager.put("OptionPane.messageFont", font);
            UIManager.put("OptionPane.buttonFont", font);
            init();
        }

        private void addMenu() {
            logger.info("Initialization menu bar has been started");
            menuBar = new JMenuBar();
            JMenu collectionMenu = new JMenu("Menu");
            collectionMenu.setFont(font);
            collectionMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JMenu language = new JMenu("Language");
            language.setFont(font);
            collectionMenu.add(language);
            JRadioButtonMenuItem russian = new JRadioButtonMenuItem("Russian");
            logger.fine("Russian button has been added");
            russian.setFont(font);
            language.add(russian);
            russian.addActionListener((event) -> {

            });
            russian.setSelected(true);
            JRadioButtonMenuItem norwegian = new JRadioButtonMenuItem("Norwegian");
            logger.fine("Norwegian button has been added");
            norwegian.setFont(font);
            language.add(norwegian);
            norwegian.addActionListener((event) -> {

            });
            JRadioButtonMenuItem albanian = new JRadioButtonMenuItem("Albanian");
            logger.fine("Albanian button has been added");
            albanian.setFont(font);
            language.add(albanian);
            albanian.addActionListener((event) -> {

            });
            JRadioButtonMenuItem english = new JRadioButtonMenuItem("English");
            logger.fine("English button has been added");
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
            logger.fine("Exit button has been added");
            exitItem.setFont(font);
            collectionMenu.add(exitItem);
            exitItem.addActionListener((event) -> {
                logger.info("Frame with exit option has been opened");
                if (JOptionPane.showConfirmDialog(this, "Вы действительно хотите выйти?", "Закрытие программы", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == 0) {
                    logger.severe("Program has been closed");
                    System.exit(0);
                }
            });
            menuBar.add(collectionMenu);
            this.setJMenuBar(menuBar); //для 8 лабы!!!!!!!!
            logger.info("Menu bar was added");
        }

        private void addBorder() {
            border = new JPanel();
            border.setPreferredSize(new Dimension(800, 1));
            border.setBackground(Color.BLACK);
            this.add(border,BorderLayout.NORTH);
            logger.info("High border has been added");
        }

        private void addCanvas() {
            canvas = new Canvas(50, true);
            canvas.setMinimumSize(new Dimension(500, 500));
            canvas.setPreferredSize(canvas.getMinimumSize());
            this.add(canvas,BorderLayout.CENTER);
            logger.info("Canvas with coordinates has been added");
        }

        private void initButtonPanel() {
            logger.info("Initialization of button panel has been started");
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(0,2,2,2);
            c.gridx = 0; c.gridy = 0; c.gridwidth = 3;

            buttonLabel = new JLabel("Панель управления");
            buttonLabel.setFont(font);
            buttonPanel.add(buttonLabel,c);
            c.gridy = 1;

            start = new JButton("Start");
            start.setFont(font);
            start.setPreferredSize(new Dimension(120,50));
            buttonPanel.add(start,c);
            logger.fine("Start button has been added");
            c.gridy = 2;

            start.addActionListener((event) -> {
                logger.info("Start button has been pressed");
                if (executor.isWorked()) {
                    logger.warning("Trying of starting animation, which is working, has been failed");
                    JOptionPane.showMessageDialog(this, "Анимация уже запущена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                canvas.setStaticDraw(false);
                String message = check(true);
                if (message.length()>0) {
                    logger.warning("Trying of starting animation with unsuitable filters has been failed");
                    JOptionPane.showMessageDialog(this, "Нет животных, подходящих по "+message.substring(0,message.length()-2)+".", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    canvas.setStaticDraw(true);
                }
            });

            stop = new JButton("Stop");
            stop.setFont(font);
            stop.addActionListener((event) -> {
                logger.info("Stop button has been pressed");
                if (executor.isWorked()) {
                    logger.info("Animation has been stopped successfully");
                    executor.shutdown();
                    executor.init(50);
                    canvas.repaint();
                    JOptionPane.showMessageDialog(this, "Анимация остановлена!", "Stop", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    logger.warning("Trying of stopping non-working animation has been failed");
                    JOptionPane.showMessageDialog(this, "Анимация не запущена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                canvas.setStaticDraw(true);
                canvas.repaint();
            });
            stop.setPreferredSize(new Dimension(120,50));
            buttonPanel.add(stop,c);
            logger.fine("Stop button has been added");
            c.gridy = 3;

            update = new JButton("Update");
            update.setFont(font);
            update.addActionListener((event) -> {
                logger.info("Update button has been pressed");
                updateCollection(canvas);
                initList();
                canvas.repaint();
            });
            update.setPreferredSize(new Dimension(120,50));
            buttonPanel.add(update,c);
            logger.fine("Update button has been added");
        }

        private void initToolkitPanel1() {
            toolKitPanel1 = new JPanel();

            typeLabel = new JLabel("Тип животного:");
            typeLabel.setFont(font);
            JPanel typePanel = new JPanel();
            types = new JComboBox<>(new String[]{
                    "Tiger", "Kangaroo", "Rabbit", "RealAnimal", "Любой"
            });
            types.setFont(font);
            typePanel.add(typeLabel);
            typePanel.add(types);
            logger.fine("Type panel has been added in first toolkit panel");

            JPanel namePanel = new JPanel();
            nameLabel = new JLabel("Имя животного: ");
            nameLabel.setFont(font);
            nameField = new JTextField();
            nameField.setPreferredSize(new Dimension(100, 20));
            nameField.setFont(font);
            namePanel.add(nameLabel);
            namePanel.add(nameField);
            logger.fine("Name panel has been added in first toolkit panel");


            homeLabel = new JLabel("Дом животного: ");
            homeLabel.setFont(font);
            homeOfKanga = new JRadioButton("Домик Кенги");
            homeOfKanga.setFont(font);
            homeOfRabbit = new JRadioButton("Домик Кролика");
            homeOfRabbit.setFont(font);
            australia = new JRadioButton("Австралия");
            australia.setFont(font);
            other = new JRadioButton("Другой дом");
            other.setFont(font);
            ButtonGroup home = new ButtonGroup();
            home.add(homeOfKanga);
            home.add(homeOfRabbit);
            home.add(australia);
            home.add(other);
            JPanel homePanel = new JPanel();
            homePanel.add(homeOfKanga);
            homePanel.add(homeOfRabbit);
            homePanel.add(australia);
            homePanel.add(other);
            logger.fine("Home panel has been added in first toolkit panel");


            colourLabel = new JLabel("Цвет животного: ");
            colourLabel.setFont(font);
            JPanel colourPanel = new JPanel();
            orange = new JCheckBox("Оранжевый");
            orange.setFont(font);
            brown = new JCheckBox("Коричневый");
            brown.setFont(font);
            white = new JCheckBox("Белый");
            white.setFont(font);
            black = new JCheckBox("Черный");
            black.setFont(font);
            colourPanel.add(orange);
            colourPanel.add(brown);
            colourPanel.add(white);
            colourPanel.add(black);
            logger.fine("Colour panel has been added in first toolkit panel");


            GridBagLayout gridBagLayoutToolkit1 = new GridBagLayout();
            toolKitPanel1.setLayout(gridBagLayoutToolkit1);
            GridBagConstraints gridBagConstraintsToolkit1 = new GridBagConstraints();
            gridBagConstraintsToolkit1.gridx = 0; gridBagConstraintsToolkit1.gridy = 0;
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
        }

        private void initToolkitPanel2() {
            toolKitPanel2 = new JPanel();
            GridBagLayout gridBagLayoutToolkit2 = new GridBagLayout();
            toolKitPanel2.setLayout(gridBagLayoutToolkit2);
            GridBagConstraints gridBagConstraintsToolkit2 = new GridBagConstraints();

            coordLabel = new JLabel("Координаты животного: ");
            coordLabel.setFont(font);
            minX = new MySlider("Min X: ", 0, 1800, 0, 400, 100);
            minY = new MySlider("Min Y: ", 0, 800, 0, 200, 50);
            maxX = new MySlider("Max X: ", 0, 1800, 0, 400, 100);
            maxY = new MySlider("Max Y: ", 0, 800, 0, 200, 50);

            weightLabel = new JLabel("Вес животного: ");
            weightLabel.setFont(font);
            minWeight = new MySlider("Min: ", 0, 500, 0, 100, 10);
            maxWeight = new MySlider("Max: ", 0, 500, 0, 100, 10);

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
        }

        private void addPanel() {
            panel = new JPanel();
            GroupLayout panelLayout = new GroupLayout(panel);
            panel.setLayout(panelLayout);
            panel.setPreferredSize(new Dimension(900, 230));

            initButtonPanel();
            logger.info("Button panel has been initialized");

            initToolkitPanel1();
            logger.info("First toolkit panel has been initialized");

            initToolkitPanel2();
            logger.info("Second toolkit panel has been initialized");


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

            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            this.add(panel,BorderLayout.SOUTH);
            logger.info("Toolkit panel has been added on general frame");
        }

        private void listenChanges() {
            types.addActionListener((event) -> {
                logger.info("Type has been changed on "+types.getSelectedItem());
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            nameField.addActionListener((event) -> {
                logger.info("Name has been changed on "+nameField.getText());
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            minX.addChangeListener((event) -> {
                logger.info("Min X has been changed on "+minX.getMyValue());
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            minY.addChangeListener((event) -> {
                logger.info("Min Y has been changed on "+minY.getMyValue());
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            maxX.addChangeListener((event) -> {
                logger.info("Max X has been changed on "+maxX.getMyValue());
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            maxY.addChangeListener((event) -> {
                logger.info("Max Y has been changed on "+maxY.getMyValue());
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            homeOfKanga.addActionListener((event) -> {
                logger.info("Home has been changed on home of Kanga");
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            homeOfRabbit.addActionListener((event) -> {
                logger.info("Home has been changed on home of Rabbit");
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            australia.addActionListener((event) -> {
                logger.info("Home has been changed on home of Australia");
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            other.addActionListener((event) -> {
                logger.info("Home has been changed on other");
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            minWeight.addChangeListener((event) -> {
                logger.info("Min weight has been changed on "+minWeight.getMyValue());
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            maxWeight.addChangeListener((event) -> {
                logger.info("Max weight has been changed on "+maxWeight.getMyValue());
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            orange.addActionListener((event) -> {
                logger.info("Orange button has been changed");
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            white.addActionListener((event) -> {
                logger.info("White button has been changed");
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            black.addActionListener((event) -> {
                logger.info("Black button has been changed");
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            brown.addActionListener((event) -> {
                logger.info("Brown button has been changed");
                if (canvas.isStaticDraw())
                    return;
                check(false);
            });
            logger.fine("Listening of changes for animation has been done");
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
            //addMenu();
            addBorder();
            addCanvas();
            addPanel();
            listenChanges();
            this.pack();
            this.setMinimumSize(new Dimension(this.getWidth() + 350, this.getHeight()));
            this.setLocationRelativeTo(null);
            this.setVisible(true);
            logger.info("General frame has been showed");
            updateCollection(canvas);
            initList();
            this.repaint();
        }

        private List<Boolean> checkFilter(Animal animal) {
            List<Boolean> booleans = new ArrayList<>();
            booleans.add(((animal.getClass().toString().substring(getClass().toString().lastIndexOf(".") + 1).equalsIgnoreCase(((String) types.getSelectedItem()).trim()) ||
                    ((String) types.getSelectedItem()).trim().equalsIgnoreCase("Любой"))));
            booleans.add((animal.getName().trim().equalsIgnoreCase(nameField.getText().trim()) || nameField.getText().trim().equals("")));
            booleans.add((animal.getCoord().getX() > minX.getMyValue() &&
                    animal.getCoord().getX() < maxX.getMyValue()));
            booleans.add((animal.getCoord().getY() > minY.getMyValue() &&
                    animal.getCoord().getY() < maxY.getMyValue()));
            booleans.add((
                    (animal.getHome().trim().equalsIgnoreCase(homeOfKanga.getText().trim()) &&
                            homeOfKanga.isSelected()) ||
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
                                    (animal.getColourSynonym().trim().equalsIgnoreCase(white.getText().trim()) &&
                                            white.isSelected()) ||
                                    (animal.getColourSynonym().trim().equalsIgnoreCase(black.getText().trim()) &&
                                            black.isSelected()) ||
                                    (animal.getColourSynonym().trim().equalsIgnoreCase(brown.getText().trim()) &&
                                            brown.isSelected())
                    ));
            if (booleans.stream().allMatch(e->e))
                booleans = null;
            logger.info("Filter has been checked");
            return booleans;
        }

        private String check(boolean firstIncluding) {
            if (!firstIncluding && !executor.isWorked()) {
                logger.info("Checking hasn't been done because animation isn't working");
                return "";
            }
            StringBuilder message = new StringBuilder();
            List<Boolean> booleans = new ArrayList<>(Arrays.asList(false,false,false,false,false,false,false));
            boolean[] somebodyExist = new boolean[1];
            somebodyExist[0] = false;
            set.forEach((e) -> {
                Animal animal = e.getAnimal();
                int weight = animal.getWeight();
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
                if (checkFilter(animal) == null) {
                    executor.addTask(animal.getID(), () -> {
                        managerMoving.nextStep();
                        e.setMyX(managerMoving.nextX());
                        e.setMyY(managerMoving.nextY());
                        e.reBounds();
                        e.revalidate();
                        canvas.revalidate();
                        canvas.repaint();
                        logger.finer("Animal "+e.animal+" has been moving");
                    });
                    somebodyExist[0] = true;
                }
                else {
                    List<Boolean> booleans1 = checkFilter(animal);
                    for (int i = 0; i < booleans.size(); ++i) {
                        booleans.set(i,(booleans.get(i)||(booleans1.get(i))));
                    }
                }
            });
            if (somebodyExist[0]) {
                return "";
            }
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
        SocketChannel sChannel = SocketChannel.open();
        sChannel.configureBlocking(true);
        if (sChannel.connect(new InetSocketAddress("localhost", port))) {
            logger.info("Connection with server has been connected");
            ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
            oos.writeObject("list");
            Object o = ois.readObject();
            if (o instanceof AnimalCollection)
                return (AnimalCollection) o;
            else {
                logger.severe("Data from server isn't valid");
                throw new ClassCastException();
            }
        } else  {
            logger.severe("Connection with server doesn't exist");
            throw new ConnectException();
        }
    }
}
