package com.shilko.ru;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

public class Client {
    private final static int port = 11111;
    public static void main(String ... args) {
        AnimalCollection collection;
        while (true) {
            collection = getCollection();
            if (collection != null)
                collection.work();
        }
    }
    private static AnimalCollection getCollection() {
        try {
            SocketChannel sChannel = SocketChannel.open();
            sChannel.configureBlocking(true);
            if (sChannel.connect(new InetSocketAddress("localhost", port))) {
                ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
                oos.writeObject("list");
                Object o = ois.readObject();
                try {
                    return (AnimalCollection) o;
                } catch (ClassCastException e) {
                    System.out.println((String) o);
                }
            }
            else throw new ConnectException();
        } catch (ConnectException e) {
            System.out.println("Сервер не доступен!!!");
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Произошла ошибка!!!");
        }
        return null;
    }
}
