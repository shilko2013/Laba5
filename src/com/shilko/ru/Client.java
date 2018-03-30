package com.shilko.ru;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

public class Client {
    private final static int port = 11111;
    public static void main(String ... args) {
        Scanner in = new Scanner(System.in);
        AnimalCollection collection;
        while (true) {
            try {
                String command = in.nextLine();
                SocketChannel sChannel = SocketChannel.open();
                sChannel.configureBlocking(true);
                if (sChannel.connect(new InetSocketAddress("localhost", port))) {
                    ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());
                    ObjectOutputStream oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
                    if (command.equalsIgnoreCase("exit"))
                        System.exit(0);
                    oos.writeObject(command);
                    collection = (AnimalCollection) ois.readObject();
                    if (command.startsWith("list"))
                        System.out.println((String) ois.readObject());
                    collection.work();
                }
                else throw new ConnectException();
            } catch (ConnectException e) {
                System.out.println("Сервер не доступен!!!");
            } catch (ClassNotFoundException | IOException e) {
                System.out.println("Произошла ошибка!!!");
            }
        }
    }
}
