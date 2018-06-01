package com.shilko.ru;

import java.util.Random;

public class ManagerRunningMoving extends AbstractManagerMoving {

    private int randomWidth;
    private int randomHeight;
    private int savedX, savedY;
    private int count;
    private double x,y;

    public ManagerRunningMoving(int step, Coord coord, int imageHeight, int imageWidth, Client.ClientGUI.Canvas canvas, int weight) {
        super(step, coord, imageHeight, imageWidth, canvas, weight);
        x = coord.getX();
        y = coord.getY();
        reRandom();
    }

    private void reRandom() {
        savedX = (int)x;
        savedY = (int)y;
        int magicConstant = imageHeight/(imageWidth/weight);
        randomWidth = new Random().nextInt(canvas.getWidth()-weight)+weight/2;
        randomHeight = new Random().nextInt(canvas.getHeight()-magicConstant)+magicConstant/2;
        count = 0;
    }

    @Override
    public void nextStep() {
        count++;
        if (count==step) {
            reRandom();
            nextStep();
        }
        x += (randomWidth - savedX) / (double)step;
        y += (randomHeight - savedY) / (double)step;
    }
    @Override
    public int nextX() {
        return (int)x;
    }
    @Override
    public int nextY() {
        return (int)y;
    }
}
