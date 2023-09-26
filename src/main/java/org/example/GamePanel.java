package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.swing.JPanel;

import static javax.imageio.ImageIO.read;


public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 5;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    private BufferedImage appleImage, backgroundImage;
    private BufferedImage snakeHeadUp, snakeHeadDown, snakeHeadLeft, snakeHeadRight;
    private BufferedImage snakeBodyVertical, snakeBodyHorizontal;
    private BufferedImage snakeTailUp, snakeTailDown, snakeTailLeft, snakeTailRight;
    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        try {
            snakeHeadUp = read(new File("src/main/resources/snakeHeadUp.png"));
            snakeHeadDown = read(new File("src/main/resources/snakeHeadDown.png"));
            snakeHeadLeft = read(new File("src/main/resources/snakeHeadLeft.png"));
            snakeHeadRight = read(new File("src/main/resources/snakeHeadRight.png"));

            snakeBodyHorizontal = read(new File("src/main/resources/snakeBody2Horizontal.png"));
            snakeBodyVertical = read(new File("src/main/resources/snakeBody2Vertical.png"));

            snakeTailUp = read(new File("src/main/resources/snakeTail2Up.png"));
            snakeTailDown = read(new File("src/main/resources/snakeTail2Down.png"));
            snakeTailLeft = read(new File("src/main/resources/snakeTail2Left.png"));
            snakeTailRight = read(new File("src/main/resources/snakeTail2Right.png"));

            backgroundImage = read(new File("src/main/resources/grassBackground.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        this.setBackground(new Color(0, 110, 54));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        try {
            appleImage = read(new File("src/main/resources/appleImage.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        startGame();
    }
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
        draw(g);

    }
    public void draw(Graphics g) {
        if (running) {

            float visualAppleSize = UNIT_SIZE * 1.5f;
            float adjustedAppleX = appleX + (UNIT_SIZE - visualAppleSize) / 2;
            float adjustedAppleY = appleY + (UNIT_SIZE - visualAppleSize) / 2;

            g.drawImage(appleImage, (int) adjustedAppleX, (int)adjustedAppleY,(int)visualAppleSize, (int)visualAppleSize, null);

            for (int i = 0; i < bodyParts; i++) {
                BufferedImage currentImage;
                if (i == 0) {
                    currentImage = getSnakeHeadImage();
                } else if (i ==bodyParts -1) {
                    currentImage = getSnakeTailImage();
                } else {
                    currentImage = getSnakeBodyImage(i);
                }
                g.drawImage(currentImage, x[i], y[i], UNIT_SIZE, UNIT_SIZE, null);
            }
            g.setColor(new Color(192, 13, 13));
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("" + applesEaten, (metrics.stringWidth("" + applesEaten))/2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    private BufferedImage getSnakeHeadImage() {
        switch (direction) {
            case 'U' -> {
                return snakeHeadUp;
            }
            case 'D' -> {
                return snakeHeadDown;
            }
            case 'L' -> {
                return snakeHeadLeft;
            }
            case 'R' -> {
                return snakeHeadRight;
            }
            default -> {
                return snakeHeadRight;
            }
        }
    }

    private BufferedImage getSnakeTailImage() {
        switch (direction) {
            case 'U' -> {
                return snakeTailUp;
            }
            case 'D' -> {
                return snakeTailDown;
            }
            case 'L' -> {
                return snakeTailLeft;
            }
            case 'R' -> {
                return snakeTailRight;
            }
            default -> {
                return snakeTailRight;
            }
        }
    }

    private BufferedImage getSnakeBodyImage(int index) {
        if (direction == 'L' || direction == 'R') {
            return snakeBodyHorizontal;
        } else {
            return snakeBodyVertical;
        }
    }

    public void newApple() {

        appleX = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
    }
    public void move() {

        for(int i = bodyParts; i>0;i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }
    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }
    public void checkCollisions() {
        for(int i = bodyParts;i>0;i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        if(x[0] < 0) {
            running =false;
        }
        if(x[0] > SCREEN_WIDTH) {
            running =false;
        }
        if(y[0] < 0) {
            running =false;
        }
        if(y[0] > SCREEN_HEIGHT) {
            running =false;
        }

        if(!running) {
            timer.stop();
        }
    }
    public void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // score over Game over screen
        g.setColor(new Color(192, 13, 13));
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
        // Game Over text
        g.setColor(new Color(192, 13, 13));
        g.setFont(new Font("Ink Free", Font.BOLD, 100));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') {
                        direction = 'L';
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') {
                        direction = 'R';
                    }
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') {
                        direction = 'U';
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') {
                        direction = 'D';
                    }
                }
            }
        }
    }
}
