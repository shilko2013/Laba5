package com.shilko.ru;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

public class Client {
    private final static int port = 11111;
    public static void main(String ... args) {
        SwingUtilities.invokeLater(ClientGUI::new);
        /*AnimalCollection collection;
        while (true) {
            collection = getCollection();
            if (collection != null)
                collection.work();
        }*/
    }
    public static class ClientGUI extends JFrame {
        private static Font font = new Font("Font", Font.PLAIN,15);
        private final static float RATIO = 1.5f;
        Map<Coord,Animal> collection;
        public void updateCollection() {
            while (true) {
                try {
                    collection = getCollection().getLikeMap();
                    break;
                } catch (Exception e) {
                    if (JOptionPane.showConfirmDialog(this,"Не удается получить коллекцию!\nПовторить попытку?","Ошибка!",JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE)==1)
                        System.exit(0);
                }
            }
        }
        public ClientGUI() {
            super("Client");
            UIManager.put("OptionPane.messageFont", font);
            UIManager.put("OptionPane.buttonFont", font);
            updateCollection();
            init();
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
                    Server.exit((JFrame)e.getComponent());
                }
            });

            JPanel border = new JPanel();
            border.setPreferredSize(new Dimension(800,1));
            border.setBackground(Color.BLACK);
            this.add(border,BorderLayout.PAGE_START);

            class AnimalButton extends JButton {
                class RoundedBorder implements Border {

                    private int radius;


                    private RoundedBorder(int radius) {
                        this.radius = radius;
                    }


                    public Insets getBorderInsets(Component c) {
                        return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
                    }


                    public boolean isBorderOpaque() {
                        return true;
                    }


                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        g.fillOval(x,y,Math.round(radius*RATIO),radius);
                    }
                }
                private Animal animal;
                private AnimalButton (Animal animal) {
                    super("");
                    System.out.println(animal);
                    this.animal = animal;
                    setBounds(animal.getCoord().getX(), animal.getCoord().getY(), Math.round(animal.getWeight()*RATIO), animal.getWeight());
                    setForeground(new Color(animal.getColour()[0],animal.getColour()[1],animal.getColour()[2]));
                    setBackground(Color.WHITE);
                    setBorder(new RoundedBorder(animal.getWeight()));
                    setToolTipText(this.animal.getName());
                    setOpaque(false);
                    setEnabled(false);
                }
                @Override
                public void paintComponent(Graphics g) {
                    //g.fillOval(this.getX()-this.getWidth()/2,this.getY()-this.getHeight()/2,this.getWidth(),this.getHeight());
                    g.fillOval(this.getX(),this.getY(),this.getWidth(),this.getHeight());
                }
            }
            class Canvas extends JComponent {
                private Graphics2D gr;
                private final static int SIZE = 50;
                public void paintComponent(Graphics g) {
                    gr = (Graphics2D) g;

                    // Делаем белый фон
                    Rectangle2D rect = new Rectangle2D.Double(0,0,this.getWidth(),this.getHeight());
                    gr.setPaint(Color.WHITE);
                    gr.fill(rect);
                    gr.draw(rect);

                    // Рисуем сетку
                    gr.setPaint(Color.LIGHT_GRAY);
                    gr.setStroke(new BasicStroke(0.2f));
                    for(int y=0;y<=this.getWidth();y+= SIZE){
                        gr.draw(new Line2D.Double(0,y  ,this.getWidth(),y));
                        gr.draw(new Line2D.Double(y* RATIO,0,y* RATIO,this.getHeight()));
                    }

                    //подписываем все это дело
                    gr.setPaint(Color.BLACK);
                    for (int x = 0; x < this.getWidth(); x += SIZE * RATIO)
                        gr.drawString(Integer.toString(x),x,10);
                    gr.drawString("X",this.getWidth()-10,10);
                    for (int y = 0; y < this.getHeight(); y += SIZE)
                        gr.drawString(Integer.toString(y),0,y);
                    gr.drawString("Y",0,this.getHeight()-10);

                    this.removeAll();
                    collection.keySet().forEach((e)-> {
                                this.add(new AnimalButton(collection.get(e)));
                            });
                    // Рисуем оси
                    /*
                    gr.setStroke(new BasicStroke((float) 1));
                    gr.draw(new Line2D.Double(ox,0,ox,this.getHeight()));
                    gr.draw(new Line2D.Double(0,oy,this.getWidth(),oy));*/
                }
            }

            Canvas canvas = new Canvas();
            canvas.setMinimumSize(new Dimension(500,500));
            canvas.setPreferredSize(canvas.getMinimumSize());
            //canvas.setBackground(Color.WHITE);
            this.add(canvas,BorderLayout.CENTER);

            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            JButton update = new JButton("Update");
            update.setFont(font);
            update.addActionListener((event)-> {
                updateCollection();
                canvas.repaint();
            });
            panel.add(update);

            JButton start = new JButton("Start");
            start.setFont(font);
            start.addActionListener((event)->{

            });
            panel.add(start);

            JButton stop = new JButton("Stop");
            stop.setFont(font);
            stop.addActionListener((event)-> {

            });
            panel.add(stop);

            JPanel filterPanel = new JPanel();
            filterPanel.setMinimumSize(new Dimension(800,300));
            JLabel type = new JLabel("Выберите тип:");
            type.setFont(font);
            JPanel typePanel = new JPanel();
            JComboBox<String> types = new JComboBox<>(new String[]{
                    "Tiger", "Kangaroo", "Rabbit", "RealAnimal", "Любой"
            });
            types.setFont(font);
            typePanel.add(type,BorderLayout.NORTH);
            typePanel.add(types,BorderLayout.SOUTH);
            filterPanel.add(typePanel);

            JPanel namePanel = new JPanel();
            JLabel nameLabel = new JLabel("Введите имя: ");
            nameLabel.setFont(font);
            JTextField nameField = new JTextField();
            nameField.setPreferredSize(new Dimension(100,20));
            nameField.setFont(font);
            namePanel.add(nameLabel);
            namePanel.add(nameField);
            filterPanel.add(namePanel);

            class MySlider extends JSlider {
                private JPanel panel;
                private JLabel label;
                private JSlider slider;
                private MySlider(String text,int min, int max, int value, int bigStep, int smallStep) {
                    panel = new JPanel();
                    label = new JLabel(text);
                    label.setFont(font);
                    panel.add(label);
                    slider = new JSlider(JSlider.HORIZONTAL, min,max,value);
                    slider.setMajorTickSpacing(bigStep);
                    slider.setMinorTickSpacing(smallStep);
                    slider.setPaintTicks(true);
                    slider.setPaintLabels(true);
                    panel.add(slider);
                }
                private JPanel getPanel() {
                    return panel;
                }
                private JSlider getSlider() {
                    return  slider;
                }
                private JLabel getLabel() {
                    return label;
                }
            }

            MySlider minX = new MySlider("Min X: ",0,1800,0,400,100);
            filterPanel.add(minX.getPanel());
            MySlider minY = new MySlider("Min Y: ",0,800,0,200,50);
            filterPanel.add(minY.getPanel());
            MySlider maxX = new MySlider("Max X: ",0,1800,0,400,100);
            filterPanel.add(maxX.getPanel());
            MySlider maxY = new MySlider("Max Y: ",0,800,0,200,50);
            filterPanel.add(maxY.getPanel());

            panel.add(filterPanel);
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

            this.pack();
            this.setMinimumSize(this.getPreferredSize());
            this.setLocationRelativeTo(null);
            this.setVisible(true);
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
            }
            else throw new ConnectException();
        /*} catch (ConnectException e) {
            System.out.println("Сервер не доступен!!!");
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Произошла ошибка!!!");
        }*/
        //return null;
    }
}
