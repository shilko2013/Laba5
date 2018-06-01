package com.shilko.ru;

import java.lang.annotation.Annotation;

public class ManagerMoving implements Movable {
    private Movable movable;
    public ManagerMoving(Animal animal, int step, int imageHeight, int imageWidth, Client.ClientGUI.Canvas canvas) {
        Annotation[] annotations = animal.getClass().getAnnotations();
        for (Annotation annotation: annotations) {
            if (annotation instanceof Running) {
                movable = new ManagerRunningMoving(
                        step,
                        animal.getCoord(),
                        imageHeight,
                        imageWidth,
                        canvas,
                        animal.getWeight());
                return;
            }
            else if (annotation instanceof Jumping) {
                movable = new ManagerJumpingMoving(
                        step,
                        animal.getCoord(),
                        imageHeight,
                        imageWidth,
                        canvas,
                        animal.getWeight(),
                        ((Jumping)annotation).maxHeight());
                return;
            }
        }
        movable = new ManagerUnknownMoving(
                step,
                animal.getCoord(),
                imageHeight,
                imageWidth,
                canvas,
                animal.getWeight());
    }
    @Override
    public void nextStep() {
        movable.nextStep();
    }
    @Override
    public int nextX() {
        return movable.nextX();
    }
    @Override
    public int nextY() {
        return movable.nextY();
    }
}