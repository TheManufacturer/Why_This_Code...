package mino;

import java.awt.*;

public class Mino_Square extends Mino{
    public Mino_Square(){
        create(Color.yellow);
    }

    public void setXY(int x, int y) {
        // " Default and Only Direction Mino_Square "
        //    o o
        //    o o
        //
        b[0].x = x;
        b[0].y = y;
        b[1].x = b[0].x;
        b[1].y = b[0].y + Block.SIZE;
        b[2].x = b[0].x + Block.SIZE;
        b[2].y = b[0].y;
        b[3].x = b[0].x + Block.SIZE;
        b[3].y = b[0].y + Block.SIZE;
    }
    // It's a Square... xD Cannot Rotate
    public void getDirection1(){}

    public void getDirection2(){}

    public void getDirection3(){}

    public void getDirection4(){}

}
