package main;

import mino.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PlayManager{

    //Main Play Area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    //Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlock = new ArrayList<>();


    //Others
    public static int dropInterval = 60; //mino drops in every 60 frames
    boolean gameOver;

    // Effect
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    // Score
    int level = 1;
    int lines;
    int score;

    public PlayManager(){

        //Main Play Area Frame
        left_x = (GamePanel.WIDTH/2)-(WIDTH/2); // 1280/2 - 360/2 = 460
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

        //Set the starting Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }

    private Mino pickMino(){
        //Pick a random mino
        Mino mino = null;
        int i = new Random().nextInt(7);

        switch (i){
            case 0: mino = new Mino_L1();break;
            case 1: mino = new Mino_L2();break;
            case 2: mino = new Mino_Square();break;
            case 3: mino = new Mino_Bar();break;
            case 4: mino = new Mino_T();break;
            case 5: mino = new Mino_Z1();break;
            case 6: mino = new Mino_Z2();break;
        }
        return mino;
    }


    public void update(){

        //Check if the nextMino is active
        if (currentMino.active == false) {

            //if the mino is not active, put it into the staticBlocks
            staticBlock.add(currentMino.b[0]);
            staticBlock.add(currentMino.b[1]);
            staticBlock.add(currentMino.b[2]);
            staticBlock.add(currentMino.b[3]);

            //check if the game is over
            if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y){

                // this means the currentMino immediately collided  a block and couldn't move at all
                // so it's xy are the same with the nexMino's
                gameOver = true;
                GamePanel.music.stop();
                GamePanel.se.play(2,false);
            }

            currentMino.deactivating= false;

            //Replace the currentMino with the nextMino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            //when a mino becomes inactive, check if line(s) can be deleted
            checkDelete();
        }else {
            currentMino.update();
        }
    }

    private void checkDelete(){

        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while(x < right_x && y < bottom_y) {

            for (int i = 0; i < staticBlock.size(); i++) {
                if (staticBlock.get(i).x == x && staticBlock.get(i).y == y) {
                    //increase the count if there is a static block
                    blockCount++;
                }
            }

            x += Block.SIZE;

            if (x == right_x) {
                if (blockCount == 12) {

                    effectCounterOn = true;
                    effectY.add(y);


                    //if the blockCount hits 12, that means the current YLine is all filled with Blocks
                    //so we can delete them
                    for (int i = staticBlock.size()-1; i > -1; i--) {
                        //remove all the blocks in the current y line
                        if (staticBlock.get(i).y == y){
                            staticBlock.remove(i);
                        }
                    }
                    lineCount++;
                    lines++;
                    // Drop speed
                    // if the line score hits a certain number, increase the drp speed
                    // 1 is the fastest
                    if (lines % 10 == 0 && dropInterval >1) {
                        //every 10 lines, level increase
                        level++;

                        //Score Mechanics
                        if (dropInterval > 10){
                            dropInterval -= 10;
                        }else{
                            dropInterval -= 1;
                        }
                    }



                    // a line has been deleted so need to slide down blocks that are above it
                    for (int i = 0; i < staticBlock.size(); i++) {
                        //if a block is above the current y, move it down by the block size
                        if (staticBlock.get(i).y < y){
                            staticBlock.get(i).y += Block.SIZE;
                        }
                    }
                }

                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }

        // Add Score
        if (lineCount > 0) {
            GamePanel.se.play(1,false);
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
        }

    }

    public void draw(Graphics2D g2){

        //Draw Play Area Frame
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-4, top_y-4,WIDTH+8,HEIGHT+8);

        //Draw Next Mino Frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x,y,200,200);
        g2.setFont(new Font("Arial",Font.PLAIN,30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x+60,y+60);

        // Draw Score Frame
        g2.drawRect(x,top_y,250,300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL: "+ level ,x,y); y+=70;
        g2.drawString("LINES: "+ lines ,x,y); y+=70;
        g2.drawString("SCORE:"+ score, x, y);


        //Draw the currentMino
        if (currentMino != null){
            currentMino.draw(g2);
        }

        //Draw the nextMino
        nextMino.draw(g2);

        //Draw Static Blocks
        for(int i = 0; i < staticBlock.size(); i++) {
            staticBlock.get(i).draw(g2);
        }

        // Draw Effect
        if(effectCounterOn){
            effectCounter++;

            g2.setColor(Color.red);
            for(int i = 0; i < effectY.size(); i++) {
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }

            if (effectCounter == 10) {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }

        }

        //Draw Pause/Game Over
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if (gameOver) {
            x= left_x + 25;
            y= top_y + 320;
            g2.drawString("GAME OVER, Loser ;)",  x, y);
        }
        if (KeyHandler.pausePressed){
            x= left_x + 70;
            y= top_y + 320;
            g2.drawString("PAUSED",  x, y);
        }

        // Draw Game Title
        x = 35;
        y = top_y + 320;
        g2.setColor(Color.white);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 55));
        g2.drawString("Simple Tetris", x+20, y);

        // g2.setFont(new Font("Times New Roman", Font.BOLD, 55));
        //g2.drawString("Useless MF", x+20, y+75);

    }
}
