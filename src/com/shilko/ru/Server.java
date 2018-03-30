package com.shilko.ru;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private final static int port = 11111;
    private final static int sizeOfPool = 5;
    private static AnimalCollection collection = new AnimalCollection();
    private static ExecutorService executor = Executors.newFixedThreadPool(sizeOfPool);
    public static void main(String ... args) {
        Runtime.getRuntime().addShutdownHook(new Thread(()->{collection.save(args[0]);}));
        collection.load(args[0]);
        try (ServerSocket server = new ServerSocket(port)) {
            while (!server.isClosed()) {
                Socket client = server.accept();
                executor.execute(new ThreadServer(client,collection,args[0]));
            }
            executor.shutdown();
        } catch (IOException e) {
            System.out.println("Произошла ошибка!!!");
        }
    }
}
