package com.shilko.ru;

import java.io.*;
import java.net.*;

public class ThreadServer implements Runnable {
    private Socket client;
    private AnimalCollection collection;
    private String way;
    public ThreadServer(Socket client, AnimalCollection collection, String way) {
        this.client = client;
        this.collection = collection;
        this.way = way;
    }
    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(client.getInputStream());)
        {
            if (!client.isClosed()) {
                try {
                    String command = (String) in.readObject();
                    String output = collection.input(command,way,true);
                    out.writeObject(collection);
                    if (output != null)
                        out.writeObject(output);
                } catch (ClassNotFoundException e) {
                    out.writeObject("Ошибка передачи файла!!!");
                } catch (IllegalArgumentException e) {
                    out.writeObject("Неверный формат команды!");
                } finally {
                    out.flush();
                    in.close();
                    out.close();
                    client.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}