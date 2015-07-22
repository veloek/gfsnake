/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gfsnake;

import static gfsnake.Goodie.APPLE;
import static gfsnake.Goodie.BANANA;
import static gfsnake.Goodie.GRAPES;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * GoodieFactory
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class GoodieFactory {
    private static final Random R;
    private static final Hashtable<Integer, Image> IMAGES;

    static {
        R = new Random();
        IMAGES = new Hashtable<>();

        try {
            IMAGES.put(BANANA, ImageIO.read(GoodieFactory.class.getResource("banana.png")));
            IMAGES.put(APPLE, ImageIO.read(GoodieFactory.class.getResource("apple.png")));
            IMAGES.put(GRAPES, ImageIO.read(GoodieFactory.class.getResource("grapes.png")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Goodie createRandom(Point position) {
        int type = R.nextInt(3) + 1;

        return createType(type, position);
    }

    public static Goodie createType(int type, Point position) {
        int value;
        Image image;

        switch (type) {
            case BANANA:
                value = 1;
                image = IMAGES.get(BANANA);
                break;
            case APPLE:
                value = 5;
                image = IMAGES.get(APPLE);
                break;
            case GRAPES:
                value = 10;
                image = IMAGES.get(GRAPES);
                break;
            default:
                value = 0;
                image = null;
        }

        return new Goodie(position, value, image);
    }
}
