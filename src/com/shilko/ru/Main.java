package com.shilko.ru;

public class Main {
    public static void main(String[] args) {
        String DB_URL = "jdbc:postgresql://127.0.0.1:2222/postgres";
        String USER = "postgres";
        String PASS = "iaq150";
        ManagerORM<Animal> managerORM = new ManagerORM<>(Animal.class,DB_URL,USER,PASS,false);
        managerORM.create();
        managerORM.insert(new Kangaroo("name","home",5,5,5));
        managerORM.insertOrUpdate(new RealAnimal("name","t",5,6,5));
        managerORM.insertOrUpdate(new Kangaroo("name","3",5,5,12));
        managerORM.delete(new Tiger("name","home",5,5,124));
        managerORM.commit();
    }
}
