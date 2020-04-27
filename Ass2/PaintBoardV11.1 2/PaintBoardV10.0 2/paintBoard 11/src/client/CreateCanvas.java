package client;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;

public class CreateCanvas extends Canvas {

    private Image image=null;

    @Override
    public void paint(Graphics g) {
        // TODO Auto-generated method stub
        g.drawImage(image, 0, 0, null);
        //System.out.println("Canvas drawed");

    }

    @Override
    public void update(Graphics g) {
        // TODO Auto-generated method stub
        paint(g);
        //System.out.println("Canvas updated");
    }


    public void setImage(Image image) {
        this.image = image;
        //System.out.println("Image Set");
    }

    
}
