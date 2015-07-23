/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gfsnake;

import gameframe.Direction;
import gameframe.api.GFGame;
import gameframe.api.GFTestFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author vegard
 */
public class GFSnake extends GFGame {

    private static final Color GREEN = new Color(0, 175, 0);
    private static final int GRID_SIZE = 40;

    private Random r;
    private boolean paused;
    private ArrayList<Goodie> goodies;
    private LinkedList<Point> snake;
    private final int cellSize;
    private long points;
    private int newPieces = 0;
    private int speed;
    private long lastUpdate;
    private boolean crashed;
    private long now;
    private long timeSinceLastUpdate;
    private boolean timeToUpdate;
    private Direction newDirection = null;
    private long startTime;
    private long pauseTime;
    private long delta;
    private long playTime;

    public GFSnake(Dimension size) {
        super(size);

        r = new Random();
        cellSize = getSize().width / GRID_SIZE;
        
        initGame();
    }

    private void initGame() {
        crashed = false;
        lastUpdate = 0;
        points = 0;
        speed = 1;
        paused = false;
        startTime = System.nanoTime();
        pauseTime = 0;
        goodies = new ArrayList<>();
        snake = new LinkedList<>();
        snake.add(new Point(getSize().width/2, getSize().height/2));
    }

    @Override
    public void update(Graphics g) {

        // Update time based on speed
        now = System.nanoTime();
        delta = now-lastUpdate;
        
        if (paused)
            pauseTime += delta;

        playTime = (now-startTime-pauseTime)/1000000000;

        if (points/(speed*speed) > 100)
            speed++;

        timeSinceLastUpdate += delta;

        if (timeSinceLastUpdate > 100000000-(speed*10000000) && !paused) {
            timeToUpdate = true;
            timeSinceLastUpdate = 0;

            if (newDirection != null) {
                super.setDirection(newDirection);
                newDirection = null;
            }
        }

        lastUpdate = now;

        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getSize().width, getSize().height);

        // Check if the snake has hit any walls or itself
        if (timeToUpdate && !crashed) {
            crashed = checkCollisions();
        }

        if (!crashed) {

            // Goodies
            g.setColor(Color.red);
            for (Goodie goodie : goodies) {
                if (goodie.getImage() != null) {
                    g.drawImage(goodie.getImage(), goodie.getPosition().x,
                            goodie.getPosition().y, cellSize, cellSize, null);
                } else {
                    g.fillRect(goodie.getPosition().x, goodie.getPosition().y,
                            cellSize, cellSize);
                }
            }

            // Snake
            g.setColor(GREEN);
            for (Point p : snake) {
                g.fillRoundRect(p.x, p.y, cellSize, cellSize, cellSize/2, cellSize/2);
            }

            // Stats
            String pointsStr = "Points: " + points;
            String speedStr = "Speed: " + speed;
            String runTimeStr = "Run time: " + playTime;

            FontMetrics metrics = g.getFontMetrics();
            Rectangle2D pointsBounds = metrics.getStringBounds(pointsStr, g);
            Rectangle2D speedBounds = metrics.getStringBounds(speedStr, g);
            Rectangle2D runTimeBounds = metrics.getStringBounds(runTimeStr, g);

            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, getSize().height-cellSize,
                    getSize().width, cellSize);

            g.setColor(Color.WHITE);

            int padding = 5;
            int x = padding;

            g.drawString(pointsStr, x, getSize().height-padding);

            x += (int)pointsBounds.getWidth()+padding;
            g.drawString(speedStr, x, getSize().height-padding);

            x += (int)speedBounds.getWidth()+padding;
            g.drawString(runTimeStr, x, getSize().height-padding);

            x = getSize().width/2;
            g.drawImage(GoodieFactory.IMAGES.get(Goodie.BANANA), x,
                    getSize().height-cellSize, cellSize, cellSize, null);
            x += cellSize;
            g.drawString("="+Goodie.VALUE_BANANA, x, getSize().height-padding);

            x += cellSize*4;
            g.drawImage(GoodieFactory.IMAGES.get(Goodie.APPLE), x,
                    getSize().height-cellSize, cellSize, cellSize, null);
            x += cellSize;
            g.drawString("="+Goodie.VALUE_APPLE, x, getSize().height-padding);

            x += cellSize*4;
            g.drawImage(GoodieFactory.IMAGES.get(Goodie.GRAPES), x,
                    getSize().height-cellSize, cellSize, cellSize, null);
            x += cellSize;
            g.drawString("="+Goodie.VALUE_GRAPES, x, getSize().height-padding);

            if (paused) {
                String pauseStr = "PAUSED";
                Rectangle2D pauseBounds = metrics.getStringBounds(pauseStr, g);
                g.drawString(pauseStr,
                        getSize().width/2-(int)pauseBounds.getWidth()/2,
                        getSize().height/2-(int)pauseBounds.getHeight()/2);
            }

            if (timeToUpdate) {
                move();
                dropGoodies();
            }

        } else {

            String gameOver = "GAME OVER";

            g.setColor(Color.WHITE);
            Rectangle2D bounds = g.getFontMetrics().getStringBounds(gameOver, g);
            g.drawString(gameOver, getSize().width/2-(int)bounds.getWidth()/2,
                    getSize().height/2-(int)bounds.getHeight()/2);

        }

        timeToUpdate = false;
    }
    
    private boolean checkCollisions() {
        Point head = snake.get(0);

        if (head.x < 0 || head.x >= getSize().width ||
                head.y < 0 || head.y >= getSize().height-cellSize) {
            return true;
        }

        // Check for snake body hit
        for (int i=snake.size()-1; i>0; i--) {
            if (head.equals(snake.get(i)))
                return true;
        }

        // Check for goodie hit
        ArrayList<Goodie> toRemove = new ArrayList<>();
        for (Goodie g : goodies) {
            if (g.getPosition().distance(head) == 0) {

                newPieces += GRID_SIZE / 10;
                points += g.getValue();

                Point tail = snake.getLast();
                for (int i=0; i<newPieces; i++) {
                    snake.add(tail.getLocation());
                }

                toRemove.add(g);
            }
        }

        goodies.removeAll(toRemove);

        return false;
    }

    private void move() {
        for (int i=snake.size()-1; i>0; i--) {
            
            // New pieces shouldn't move (yet)
            if (newPieces > 0) {
                newPieces--;
            } else {
                snake.get(i).setLocation(snake.get(i-1).getLocation());
            }
        }

        // Move the snake's head in the direction given
        if (getDirection() != null) {
            Point head = snake.get(0);

            switch (getDirection()) {
                case UP:
                    head.y -= cellSize;
                    break;
                case DOWN:
                    head.y += cellSize;
                    break;
                case LEFT:
                    head.x -= cellSize;
                    break;
                case RIGHT:
                    head.x += cellSize;
                    break;
            }
        }
    }

    private void dropGoodies() {
        int rand = r.nextInt((goodies.size()+1)*10*speed);
        int count = 0;
        if (rand == 0) {

            // Find a random position in a free space
            Point randomPosition;
            
            boolean hit = false;
            do {
                randomPosition = new Point(r.nextInt(getSize().width/cellSize)*cellSize,
                        r.nextInt((getSize().height/cellSize)-1)*cellSize);

                for (Point p : snake) {
                    if (p.equals(randomPosition)) {
                        hit = true;
                        break;
                    }
                }

                if (!hit) {
                    for (Goodie g : goodies) {
                        if (g.getPosition().equals(randomPosition)) {
                            hit = true;
                            break;
                        }
                    }
                }

                // Safety
                if (count++ > 1000) {
                    break;
                }
            } while (hit);

            // Do this more controlled
            goodies.add(GoodieFactory.createRandom(randomPosition));
        }
    }

    @Override
    public void setDirection(Direction direction) {
        boolean ok = true;

        if (getDirection() != null) {
            Direction old = getDirection();

            if ((old == Direction.LEFT && direction == Direction.RIGHT) ||
                    (old == Direction.RIGHT && direction == Direction.LEFT) ||
                    (old == Direction.UP && direction == Direction.DOWN) ||
                    (old == Direction.DOWN && direction == Direction.UP))
                ok = false;
        }

        if (ok)
            newDirection = direction;
    }

    @Override
    public void onAction() {
        if (crashed)
            initGame();
    }

    @Override
    public void onAlternate() {
        togglePause();
    }

    private void togglePause() {
        paused = !paused;
    }

    public static void main(String[] args) {
        new GFTestFrame(new GFSnake(
                new Dimension(GFTestFrame.WIDTH, GFTestFrame.HEIGHT)), true);
    }

}
