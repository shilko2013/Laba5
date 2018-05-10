package com.shilko.ru;

import javafx.util.Pair;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

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
        Map<Coord, Animal> collection;

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
                panel.add(label);
                this.setMajorTickSpacing(bigStep);
                this.setMinorTickSpacing(smallStep);
                this.setPaintTicks(true);
                this.setPaintLabels(true);
                panel.add(this);
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
            class RoundedBorder implements Border {

                private int radius;


                private RoundedBorder(int radius) {
                    this.radius = radius;
                }


                public Insets getBorderInsets(Component c) {
                    return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
                }


                public boolean isBorderOpaque() {
                    return true;
                }


                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    g.fillOval(x, y, Math.round(radius * RATIO), radius);
                    g.setColor(Color.BLUE);
                    g.drawOval(x, y, Math.round(radius * RATIO), radius);
                }
            }

            private Animal animal;
            private double weight;

            private double getWeight() {
                return weight;
            }

            private void setWeight(double weight) {
                this.weight = weight;
            }

            private Animal getAnimal() {
                return animal;
            }

            private AnimalButton(Animal animal) {
                super("");
                this.animal = animal;
                weight = animal.getWeight();
                setBackground(Color.WHITE);
                setBounds((int) (animal.getCoord().getX() - Math.round(getWeight() * RATIO / 2)), animal.getCoord().getY() - (int) (getWeight() / 2), (int) Math.round(getWeight() * RATIO), (int) (getWeight()));
                setForeground(new Color(animal.getColour()[0], animal.getColour()[1], animal.getColour()[2]));
                setBorder(new RoundedBorder((int) Math.round(getWeight())));
                setToolTipText(this.animal.getName());
                setOpaque(false);
                setEnabled(false);
            }

            private void reBounds() {
                setBounds((int) (animal.getCoord().getX() - Math.round(weight * RATIO / 2)), (int) (animal.getCoord().getY() - weight / 2), (int) (Math.round(weight * RATIO)), (int) weight);
            }

            private void reBorder() {
                setBorder(new RoundedBorder((int) weight));
            }

            @Override
            public void paintComponent(Graphics g) {
                //g.fillOval(this.getX()-this.getWidth()/2,this.getY()-this.getHeight()/2,this.getWidth(),this.getHeight());
                g.fillOval((int) (this.getX() - Math.round(weight * RATIO / 2)), (int) (this.getY() - weight), (int) (Math.round(weight * RATIO)), (int) weight);
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
            private Map<Animal, ScheduledFuture<?>> tasks;

            private MyExecutor() {
                init(50);
            }

            private void init(int sizeOfPool) {
                executor = Executors.newScheduledThreadPool(sizeOfPool);
                tasks = new HashMap<>();
            }

            private void addTask(Animal animal, Runnable runnable1, Runnable runnable2, int delay, int n1, int n2) {
                if (!tasks.containsKey(animal) || (tasks.containsKey(animal) && tasks.get(animal).isDone())) {
                    tasks.put(animal, executor.scheduleWithFixedDelay(new DoubleRunNTimes(runnable1, runnable2, n1, n2), 0, delay, TimeUnit.MILLISECONDS));
                }
            }

            private boolean isWorked() {
                return tasks.values().stream().anyMatch(e -> !e.isDone() && !e.isCancelled());
            }

            private void shotdown() {
                executor.shutdownNow();
                //backup();
            }

            /*private void backup() {
                tasks.keySet().forEach(e -> e.setWeight(tasks.get(e).getValue()));
            }*/
        }

        List<AnimalButton> list = new ArrayList<>();

        private void updateCollection() {
            while (true) {
                try {
                    collection = getCollection().getLikeMap();
                    break;
                } catch (Exception e) {
                    if (JOptionPane.showConfirmDialog(this, "Не удается получить коллекцию!\nПовторить попытку?", "Ошибка!", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE) == 1)
                        System.exit(0);
                }
            }
        }

        private void initList() {
            list.clear();
            collection.keySet().forEach((e) -> {
                list.add(new AnimalButton(collection.get(e)));
            });
        }

        private ClientGUI() {
            super("Client");
            UIManager.put("OptionPane.messageFont", font);
            UIManager.put("OptionPane.buttonFont", font);
            updateCollection();
            init();
        }

        private void init() {
            MyExecutor executor = new MyExecutor();
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

            JPanel border = new JPanel();
            border.setPreferredSize(new Dimension(800, 1));
            border.setBackground(Color.BLACK);
            this.add(border, BorderLayout.PAGE_START);

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
                    list.sort((a, b) -> Double.compare(a.getWeight(), b.getWeight()));
                    list.forEach(this::add);
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
            this.add(canvas, BorderLayout.CENTER);

            JPanel panel = new JPanel();
            panel.setPreferredSize(new Dimension(900, 230));
            //panel.setLayout(new FlowLayout());

            JButton start = new JButton("Start");
            start.setFont(font);
            panel.add(start);

            JButton stop = new JButton("Stop");
            stop.setFont(font);
            stop.addActionListener((event) -> {
                if (executor.isWorked()) {
                    executor.shotdown();
                    executor.init(50);
                    canvas.repaint();
                    JOptionPane.showMessageDialog(this, "Анимация остановлена!", "Stop", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Анимация не запущена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                canvas.setStaticDraw(true);
                canvas.repaint();
            });
            panel.add(stop);

            JButton update = new JButton("Update");
            update.setFont(font);
            update.addActionListener((event) -> {
                updateCollection();
                initList();
                if (!canvas.isStaticDraw())
                    stop.doClick();
                canvas.repaint();
            });
            panel.add(update);

            JLabel type = new JLabel("Выберите тип:");
            type.setFont(font);
            JPanel typePanel = new JPanel();
            JComboBox<String> types = new JComboBox<>(new String[]{
                    "Tiger", "Kangaroo", "Rabbit", "RealAnimal", "Любой"
            });
            types.setFont(font);
            typePanel.add(type, BorderLayout.NORTH);
            typePanel.add(types, BorderLayout.SOUTH);
            panel.add(typePanel);

            JPanel namePanel = new JPanel();
            JLabel nameLabel = new JLabel("Введите имя: ");
            nameLabel.setFont(font);
            JTextField nameField = new JTextField();
            nameField.setPreferredSize(new Dimension(100, 20));
            nameField.setFont(font);
            namePanel.add(nameLabel);
            namePanel.add(nameField);
            panel.add(namePanel);

            JPanel minPanel = new JPanel();
            JPanel maxPanel = new JPanel();
            MySlider minX = new MySlider("Min X: ", 0, 1800, 0, 400, 100);
            minPanel.add(minX.getPanel());
            MySlider minY = new MySlider("Min Y: ", 0, 800, 0, 200, 50);
            minPanel.add(minY.getPanel());
            MySlider maxX = new MySlider("Max X: ", 0, 1800, 0, 400, 100);
            maxPanel.add(maxX.getPanel());
            MySlider maxY = new MySlider("Max Y: ", 0, 800, 0, 200, 50);
            maxPanel.add(maxY.getPanel());
            panel.add(minPanel);
            panel.add(maxPanel);

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
            panel.add(homePanel);

            JPanel weightPanel = new JPanel();
            MySlider minWeight = new MySlider("Min Weight: ", 0, 500, 0, 100, 10);
            weightPanel.add(minWeight.getPanel());
            MySlider maxWeight = new MySlider("Max Weight: ", 0, 500, 0, 100, 10);
            weightPanel.add(maxWeight.getPanel());
            panel.add(weightPanel);

            JPanel colorPanel = new JPanel();
            JCheckBox orange = new JCheckBox("Оранжевый");
            orange.setFont(font);
            JCheckBox brown = new JCheckBox("Коричневый");
            brown.setFont(font);
            JCheckBox grey = new JCheckBox("Серый");
            grey.setFont(font);
            JCheckBox black = new JCheckBox("Черный");
            black.setFont(font);
            colorPanel.add(orange);
            colorPanel.add(brown);
            colorPanel.add(grey);
            colorPanel.add(black);
            panel.add(colorPanel);

            start.addActionListener((event) -> {
                if (executor.isWorked()) {
                    JOptionPane.showMessageDialog(this, "Анимация уже запущена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                canvas.setStaticDraw(false);
                if (!check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown, true)) {
                    JOptionPane.showMessageDialog(this, "Нет подходящих животных!", "Ошибка", JOptionPane.ERROR_MESSAGE);
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

            this.add(panel, BorderLayout.SOUTH);
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
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            nameField.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            minX.addChangeListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            minY.addChangeListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            maxX.addChangeListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            maxY.addChangeListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            homeOfKenga.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            homeOfRabbit.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            australia.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            other.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            minWeight.addChangeListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            maxWeight.addChangeListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            orange.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            grey.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            black.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            brown.addActionListener((event) -> {
                if (canvas.isStaticDraw())
                    return;
                check(list, executor, canvas, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown,false);
            });
            this.pack();
            this.setMinimumSize(new Dimension(this.getWidth() + 200, this.getHeight()));
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        }

        public boolean checkFilter(Animal animal, JComboBox<String> types, JTextField nameField, MySlider
                minX, MySlider maxX, MySlider minY, MySlider maxY,
                                   JRadioButton homeOfKenga, JRadioButton homeOfRabbit, JRadioButton australia, JRadioButton
                                           other, MySlider minWeight, MySlider maxWeight,
                                   JCheckBox orange, JCheckBox grey, JCheckBox black, JCheckBox brown) {
            return (animal.getClass().toString().substring(getClass().toString().lastIndexOf(".") + 1).equalsIgnoreCase(((String) types.getSelectedItem()).trim()) ||
                    ((String) types.getSelectedItem()).trim().equalsIgnoreCase("Любой")) &&
                    (animal.getName().trim().equalsIgnoreCase(nameField.getText().trim()) || nameField.getText().trim().equals("")) &&
                    animal.getCoord().getX() > minX.getMyValue() &&
                    animal.getCoord().getX() < maxX.getMyValue() &&
                    animal.getCoord().getY() > minY.getMyValue() &&
                    animal.getCoord().getY() < maxY.getMyValue() &&
                    (
                            (animal.getHome().trim().equalsIgnoreCase(homeOfKenga.getText().trim()) &&
                                    homeOfKenga.isSelected()) ||
                                    (animal.getHome().trim().equalsIgnoreCase(homeOfRabbit.getText().trim()) &&
                                            homeOfRabbit.isSelected()) ||
                                    (animal.getHome().trim().equalsIgnoreCase(australia.getText().trim()) &&
                                            australia.isSelected()) ||
                                    (animal.getHome().trim().equalsIgnoreCase(other.getText().trim()) &&
                                            other.isSelected())
                    ) &&
                    animal.getWeight() > minWeight.getMyValue() &&
                    animal.getWeight() < maxWeight.getMyValue() &&
                    (
                            (animal.getColourSynonym().trim().equalsIgnoreCase(orange.getText().trim()) &&
                                    orange.isSelected()) ||
                                    (animal.getColourSynonym().trim().equalsIgnoreCase(grey.getText().trim()) &&
                                            grey.isSelected()) ||
                                    (animal.getColourSynonym().trim().equalsIgnoreCase(black.getText().trim()) &&
                                            black.isSelected()) ||
                                    (animal.getColourSynonym().trim().equalsIgnoreCase(brown.getText().trim()) &&
                                            brown.isSelected())
                    );

        }

        public boolean check(List<AnimalButton> list, MyExecutor executor, JComponent canvas, JComboBox<String> types, JTextField nameField, MySlider
                minX, MySlider maxX, MySlider minY, MySlider maxY,
                             JRadioButton homeOfKenga, JRadioButton homeOfRabbit, JRadioButton australia, JRadioButton
                                     other, MySlider minWeight, MySlider maxWeight,
                             JCheckBox orange, JCheckBox grey, JCheckBox black, JCheckBox brown, boolean firstIncluding) {
            if (!firstIncluding && !executor.isWorked())
                return false;
            class Bool {
                private boolean b = false;

                private void setTrue() {
                    b = true;
                }

                private boolean get() {
                    return b;
                }
            }
            Bool b = new Bool();
            list.forEach((e) -> {
                Animal animal = e.getAnimal();
                int weight = animal.getWeight();
                System.out.println(maxX.getMyValue());
                if (checkFilter(animal, types, nameField, minX, maxX, minY, maxY,
                        homeOfKenga, homeOfRabbit, australia, other, minWeight, maxWeight,
                        orange, grey, black, brown)) {
                    b.setTrue();
                    executor.addTask(animal, () -> {
                        e.setWeight(e.getWeight() + weight / 200.);
                        e.reBounds();
                        e.reBorder();
                        e.revalidate();
                        canvas.revalidate();
                        canvas.repaint();
                    }, () -> {
                        e.setWeight(e.getWeight() - weight / 400.);
                        e.reBounds();
                        e.reBorder();
                        e.revalidate();
                        canvas.revalidate();
                        canvas.repaint();
                    }, 10, 200, 400);
                }
            });
            return b.get();
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
