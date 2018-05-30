package com.shilko.ru;

import java.io.*;
import java.net.*;

public class ThreadServer implements Runnable { //передает коллекцию данному клиенту
    private Socket client;
    private AnimalCollection collection;
    public ThreadServer(Socket client, AnimalCollection collection) {
        this.client = client;
        this.collection = collection;
    }
    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(client.getInputStream());)
        {
            if (!client.isClosed()) {
                try {
                    String command = (String) in.readObject();
                    String output;
                    if (command.equalsIgnoreCase("list"))
                        output = collection.input(command,null,true);
                    else throw new IllegalArgumentException();
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