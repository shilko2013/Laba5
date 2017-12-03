package com.shilko.ru;

public class EmptyException extends Exception {
    public EmptyException() { super("ActionList is empty!!!");}
    public EmptyException(String message) { super(message);}
    @Override
    public String toString() {
        return "Перехвачено: " + super.toString();
    }
}
