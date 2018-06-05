package com.shilko.ru;

import java.util.Random;

class ManagerUnknownMoving extends AbstractManagerMoving {
    private int randomWidth;
    private int numberOfCircles;
    private int randomAmplitude;
    private double cos;
    private double sin;
    private double x;
    private int savedX, savedY;
    private int count;
    private double myY;

    public ManagerUnknownMoving(int step, Coord coord, int imageHeight, int imageWidth, Client.ClientGUI.Canvas canvas, int weight) {
        super(step, coord, imageHeight, imageWidth, canvas, weight);
        reRandom();
    }

    private void reRandom() {
        x = coord.getX();
        savedX = (int) x;
        savedY = coord.getY();
        numberOfCircles = new Random().nextInt(3) + 3;
        randomAmplitude = new Random().nextInt(50) + 25;
        int magicConstant = imageHeight / (imageWidth / weight);
        randomWidth = new Random().nextInt(canvas.getWidth() - weight) + weight / 2;
        int randomHeight = new Random().nextInt(canvas.getHeight() - magicConstant) + magicConstant / 2;
        cos = (randomWidth - savedX) / (Math.pow(Math.pow(randomWidth - savedX, 2) + Math.pow(randomHeight - savedY, 2), 1. / 2));
        sin = (randomHeight - savedY) / (Math.pow(Math.pow(randomWidth - savedX, 2) + Math.pow(randomHeight - savedY, 2), 1. / 2));
        count = 0;
    }

    @Override
    public void nextStep() {
        count++;
        if (count == step) {
            reRandom();
            nextStep();
        }
        if (randomWidth > savedX)
            x += (randomWidth - savedX) / (double) step;
        else
            x -= (randomWidth - savedX) / (double) step;
        myY = (savedY + randomAmplitude * Math.sin(Math.PI * numberOfCircles * x / (randomWidth - savedX)));
    }

    @Override
    public int nextX() {
        return (int) ((x - savedX) * cos - (myY - savedY) * sin + savedX);
    }

    @Override
    public int nextY() {
        return (int) ((x - savedX) * sin + (myY - savedY) * cos + savedY);
    }
}
