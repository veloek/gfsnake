/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gfsnake;

import java.awt.Image;
import java.awt.Point;

/**
 * Goodie
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class Goodie {
    public static final int BANANA = 1;
    public static final int APPLE = 2;
    public static final int GRAPES = 3;

    public static final int VALUE_BANANA = 10;
    public static final int VALUE_APPLE = 50;
    public static final int VALUE_GRAPES = 100;

    private Point position;
    private int value;
    private Image image;

    public Goodie(Point position, int value, Image image) {
        this.position = position;
        this.value = value;
        this.image = image;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
