package com.example.snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 600;
    private final int UNIT_SIZE = 25;
    private final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private final int DELAY = 85;

    private enum State { MENU, RUNNING, GAMEOVER }
    private State gameState = State.MENU;

    private final int x[] = new int[GAME_UNITS];
    private final int y[] = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int applesEaten;
    private int appleX, appleY;
    private char direction = 'R';
    private Timer timer;

    // Button Areas
    private Rectangle startBtn = new Rectangle(225, 250, 150, 50);
    private Rectangle quitBtn = new Rectangle(225, 450, 150, 50);

    // Color Selection logic
    private Color snakeColor = new Color(100, 255, 50);
    private Color[] options = {new Color(100, 255, 50), Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.WHITE};
    private Rectangle[] colorBoxes = new Rectangle[5];

    public SnakeGame() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(15, 15, 25));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.addMouseListener(new MouseInput());

        // Initialize color picker hitboxes
        for(int i = 0; i < options.length; i++) {

            colorBoxes[i] = new Rectangle(180 + (i * 50), 350, 40, 40);
        }

        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void startGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        for(int i = 0; i < bodyParts; i++) { x[i] = 0; y[i] = 0; }
        newApple();
        gameState = State.RUNNING;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameState == State.MENU) drawMenu(g2d);
        else if (gameState == State.RUNNING) drawGame(g2d);
        else drawGameOver(g2d);
    }

    private void drawMenu(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("SNAKE GAME", 165, 120);

        // Start Button
        g.setColor(new Color(50, 200, 50));
        g.fill(startBtn);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("START", 260, 283);

        // Color Select Text
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Select Snake Color:", 220, 335);
        for(int i = 0; i < options.length; i++) {
            g.setColor(options[i]);
            g.fill(colorBoxes[i]);
            // Draw highlight if selected
            if(snakeColor.equals(options[i])) {
                g.setColor(Color.WHITE);
                g.drawRect(colorBoxes[i].x - 2, colorBoxes[i].y - 2, 44, 44);
            }
        }

        // Quit Button
        g.setColor(new Color(200, 50, 50));
        g.fill(quitBtn);
        g.setColor(Color.WHITE);
        g.drawString("QUIT", 275, 482);
    }

    private void drawGame(Graphics2D g) {
        // Simple Grid
        g.setColor(new Color(255, 255, 255, 20));
        for(int i=0; i<SCREEN_WIDTH/UNIT_SIZE; i++) {
            g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
            g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
        }
        // Apple
        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
        // Snake
        for (int i = 0; i < bodyParts; i++) {
            g.setColor(i == 0 ? snakeColor : snakeColor.darker());
            g.fillRoundRect(x[i], y[i], UNIT_SIZE-1, UNIT_SIZE-1, 10, 10);
        }
        g.setColor(Color.WHITE);
        g.drawString("Score: " + applesEaten, 20, 30);
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("GAME OVER", 130, 250);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + applesEaten + " - Click anywhere to Menu",     160, 320);
    }

    // Logic Methods (move, checkApple, checkCollisions, newApple) remain the same...
    public void move() {
        for (int i = bodyParts; i > 0; i--) { x[i] = x[i - 1]; y[i] = y[i - 1]; }
        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
        }
        if (x[0] < 0) x[0] = SCREEN_WIDTH - UNIT_SIZE;
        else if (x[0] >= SCREEN_WIDTH) x[0] = 0;
        if (y[0] < 0) y[0] = SCREEN_HEIGHT - UNIT_SIZE;
        else if (y[0] >= SCREEN_HEIGHT) y[0] = 0;
    }
    public void checkApple() { if (x[0] == appleX && y[0] == appleY) { bodyParts++; applesEaten++; newApple(); } }
    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) { if (x[0] == x[i] && y[0] == y[i]) gameState = State.GAMEOVER; }
    }
    public void newApple() {
        appleX = new Random().nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = new Random().nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == State.RUNNING) { move(); checkApple(); checkCollisions(); }
        repaint();
    }

    // MOUSE INPUT CLASS
    private class MouseInput extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            Point p = e.getPoint();

            if (gameState == State.MENU) {
                if (startBtn.contains(p)) startGame();
                if (quitBtn.contains(p)) System.exit(0);
                for (int i = 0; i < colorBoxes.length; i++) {
                    if (colorBoxes[i].contains(p)) snakeColor = options[i];
                }
            } else if (gameState == State.GAMEOVER) {
                gameState = State.MENU;
            }
        }
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameState == State.RUNNING) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> { if (direction != 'R') direction = 'L'; }
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> { if (direction != 'L') direction = 'R'; }
                    case KeyEvent.VK_W, KeyEvent.VK_UP -> { if (direction != 'D') direction = 'U'; }
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN -> { if (direction != 'U') direction = 'D'; }
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Mouse Edition");
        frame.add(new SnakeGame());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}