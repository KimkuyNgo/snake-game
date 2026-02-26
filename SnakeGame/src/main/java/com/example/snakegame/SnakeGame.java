package com.example.snakegame;

import javax.swing.*; // Imports GUI components like JPanel, JFrame, and Timer
import java.awt.*;  // Imports graphics tools like Color, Graphics, and Rectangle
import java.awt.event.*; // Imports listeners for keyboard and mouse input
import java.util.Random; // Imports the Random class to place apples

public class SnakeGame extends JPanel implements ActionListener { // JPanel is the "canvas"; ActionListener allows the game to update on a timer
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 600;
    private final int UNIT_SIZE = 25; // Size of one grid square (snake head/apple)
    private final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    //Total possible squares
    private final int DELAY = 85; // Speed of the game (speed of the snake)

    private enum State { MENU, RUNNING, GAMEOVER } // Defines the three modes of the game
    private State gameState = State.MENU; // Starts the game on the Menu screen

    private final int x[] = new int[GAME_UNITS]; // X coordinates of all snake body parts
    private final int y[] = new int[GAME_UNITS]; // Y coordinates of all snake body parts
    private int bodyParts; // Starting length of the snake
    private int applesEaten;
//    private int appleX, appleY; // Position of the current apple\
    private int[] appleX = new int[1];
    private int[] appleY = new int[1];
    private char direction = 'R'; // Initial direction: Right
    private Timer timer; // The "heartbeat" that triggers movements
    private int goldAppleX, goldAppleY;
    private boolean goldAppleActive = false; // Is the gold apple on screen?
    private boolean isBoosted = false;       // Is the snake currently powered up?
    private int boostTimer = 0;              // Countdown for the 5-second boost

//    private Image headImg, appleImg;

    // Button Areas and design
    private Rectangle startBtn = new Rectangle(225, 250, 150, 50);
    private Rectangle quitBtn = new Rectangle(225, 450, 150, 50);

    // Color Selection logic
    private Color snakeColor = new Color(100, 255, 50);
    private Color[] options = {new Color(100, 255, 50), Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.WHITE};
    private Rectangle[] colorBoxes = new Rectangle[5]; // Hitboxes for the color picker squares

    public SnakeGame() { //Constructor
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(15, 15, 25)); //Color of the background
        this.setFocusable(true); // Allows the panel to receive key presses W A S D
        this.addKeyListener(new MyKeyAdapter()); // Links keyboard controls
        this.addMouseListener(new MouseInput()); // Links mouse clicks

//        headImg = new ImageIcon("C:\\Users\\U-ser\\OneDrive\\Pictures\\Head.png").getImage(); // Make sure the file exists in your project
//        appleImg = new ImageIcon("C:\\Users\\U-ser\\OneDrive\\Pictures\\apple.png").getImage();

        // Initialize color picker hitboxes
        // Loops through and defines the positions for the color selection boxes
        for(int i = 0; i < options.length; i++) {

            colorBoxes[i] = new Rectangle(180 + (i * 50), 350, 40, 40);
        }

        timer = new Timer(DELAY, this); // Create timer that calls actionPerformed every 85ms
        timer.start();
    }

    public void startGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R'; // Reset snake position to top-left
        for(int i = 0; i < bodyParts; i++) {
            x[i] = 0; y[i] = 0; //snake start at coordinates 0 0
        }
//        newApple(); // Place first apple
//        gameState = State.RUNNING; // Switch screen
        newApple(0); // Spawn first apple
//        newApple(1);
//        newApple(2);
//        newApple(3);
//        newApple(4);
//        newApple(5);
//        newApple(6);
//        newApple(7);
//        newApple(8);
//        newApple(9);
//        newApple(10);// Spawn second apple
        gameState = State.RUNNING;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Clears the screen
//        Graphics2D g2d = (Graphics2D) g;


        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(20, 20, 40), 0,
                SCREEN_HEIGHT, new Color(5, 5, 10));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Makes graphics smoother
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Logic to decide what to draw based on game state
        if (gameState == State.MENU) drawMenu(g2d);
        else if (gameState == State.RUNNING) drawGame(g2d);
        else drawGameOver(g2d);


    }

    private void drawMenu(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("SNAKE GAME", 135, 120);

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
            // If this color is selected, draw a white border around it
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
        g.setColor(new Color(255, 255, 255, 5));
        for(int i=0; i<SCREEN_WIDTH/UNIT_SIZE; i++) {
            g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
            g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
        }
        // Apple
//        g.setColor(Color.RED);
//        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
        g.setColor(Color.RED);
        for (int i = 0; i < appleX.length; i++) {
            g.fillOval(appleX[i], appleY[i], UNIT_SIZE, UNIT_SIZE);
//            g.drawImage(appleImg, appleX[i], appleY[i], UNIT_SIZE, UNIT_SIZE, null);
        }

        // Draw Gold Apple
        if (goldAppleActive) {
            g.setColor(Color.YELLOW);
            g.fillOval(goldAppleX, goldAppleY, UNIT_SIZE, UNIT_SIZE);
        }

        // Draw Boost Status
        if (isBoosted) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 15));
            g.drawString("BOOST ACTIVE! (x2 Score + Speed)", 200, 30);
        }

        // Snake
        for (int i = 0; i < bodyParts; i++) {
//            if (i == 0) {
//                g.drawImage(headImg, x[i], y[i], UNIT_SIZE, UNIT_SIZE, null);
//            }
            if (isBoosted) {
                // Create a flickering effect using the timer
                // This makes the snake slightly transparent (Alpha 150)
                g.setColor(new Color(255, 215, 0, 150));
            } else {
                g.setColor(i == 0 ? snakeColor : snakeColor.darker());
            }

            if (isBoosted) {
                g.setColor(Color.YELLOW);
                // Draw a rectangle that shrinks as boostTimer decreases
                int barWidth = (int)((boostTimer / (5000.0 / DELAY)) * 100);
                g.fillRect(250, 550, barWidth, 10);
                g.drawRect(250, 550, 100, 10); // The outline
            }

            g.fillRoundRect(x[i], y[i], UNIT_SIZE-1, UNIT_SIZE-1, 10, 10);
            // Draw the "Glow" layer
            g.setColor(new Color(snakeColor.getRed(), snakeColor.getGreen(), snakeColor.getBlue(), 50)); // Alpha 50 = semi-transparent
            g.fillRoundRect(x[i]-3, y[i]-3, UNIT_SIZE+5, UNIT_SIZE+5, 15, 15);

            // Draw the actual snake part on top
            g.setColor(i == 0 ? snakeColor : snakeColor.darker());
            g.fillRoundRect(x[i], y[i], UNIT_SIZE-1, UNIT_SIZE-1, 10, 10);
        }
//        for (int i = 0; i < bodyParts; i++) {
//            g.setColor(i == 0 ? snakeColor : snakeColor.darker());
//            g.fillRoundRect(x[i], y[i], UNIT_SIZE-1, UNIT_SIZE-1, 100, 100);
//            //arcWidth arcHieght corner
//        }
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
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1]; y[i] = y[i - 1];
        } // Shift all body parts forward (tail follows head)
        switch (direction) { // Move the head in the current direction
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
        }
        // Screen Wrapping (if snake goes off edge, it appears on the other side)
        if (x[0] < 0) x[0] = SCREEN_WIDTH - UNIT_SIZE;
        else if (x[0] >= SCREEN_WIDTH) x[0] = 0;
        if (y[0] < 0) y[0] = SCREEN_HEIGHT - UNIT_SIZE;
        else if (y[0] >= SCREEN_HEIGHT) y[0] = 0;
//        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
//                gameState = State.GAMEOVER;
//            }
    }
//    public void checkApple() { // If head coordinate matches apple coordinate
//        if (x[0] == appleX && y[0] == appleY) {
//            bodyParts++; applesEaten++; newApple();
//        }
//    }
    public void checkApple() {
        for (int i = 0; i < appleX.length; i++) {
            if (x[0] == appleX[i] && y[0] == appleY[i]) {
                bodyParts++;
//                applesEaten++;
//                newApple(i); // Only respawn the specific apple that was eaten
                applesEaten += (isBoosted) ? 2 : 1;
                newApple(i);

                // 2. Spawn Golden Apple every 5 points
                if (applesEaten % 5 == 0 && !goldAppleActive) {
                    spawnGoldApple();
                }
            }
        }
        // 3. Check Golden Apple Collision
        if (goldAppleActive && x[0] == goldAppleX && y[0] == goldAppleY) {
            goldAppleActive = false;
            startBoost();
        }
    }

    public void checkCollisions() { // If head touches any part of the body
//        for (int i = bodyParts; i > 0; i--) {
//            if (x[0] == x[i] && y[0] == y[i]) gameState = State.GAMEOVER;
//        }
        if (!isBoosted) {
            for (int i = bodyParts; i > 0; i--) {
                if (x[0] == x[i] && y[0] == y[i]) {
                    gameState = State.GAMEOVER;
                }
            }
        }
    }
//    public void newApple() {
//        appleX = new Random().nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
//        appleY = new Random().nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
//    }
    public void newApple(int index) {
        Random random = new Random();
        appleX[index] = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY[index] = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    private void spawnGoldApple() {
        goldAppleX = new Random().nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        goldAppleY = new Random().nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        goldAppleActive = true;
    }

    private void startBoost() {
        isBoosted = true;
        boostTimer = 5000 / DELAY; // Convert 5 seconds into "game ticks"
        timer.setDelay(DELAY / 2); // Double the speed (halve the delay)
    }

    @Override
    public void actionPerformed(ActionEvent e) { // This runs every timer tick (85ms)
        if (gameState == State.RUNNING) {
            move();
            checkApple();
            checkCollisions();

            // If boosted, decrease the timer
            if (isBoosted) {
                boostTimer--;
                if (boostTimer <= 0) {
                    isBoosted = false;
                    timer.setDelay(DELAY); // Reset speed to normal
                }
            }
        }
        repaint(); // Tells the window to redraw everything
    }

    // MOUSE INPUT CLASS
    private class MouseInput extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            Point p = e.getPoint(); // Get where the user clicked

            if (gameState == State.MENU) {
                if (startBtn.contains(p)) startGame();
                if (quitBtn.contains(p)) System.exit(0); // Check if user clicked a color box
                for (int i = 0; i < colorBoxes.length; i++) {
                    if (colorBoxes[i].contains(p)) snakeColor = options[i];
                }
            } else if (gameState == State.GAMEOVER) {
                gameState = State.MENU; // Return to menu on click
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
        JFrame frame = new JFrame("Snake Game"); // Create the window
        frame.add(new SnakeGame()); // Add the game panel to the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Stop program when closed
        frame.pack(); // Size the window to the panel's preferred size
        frame.setVisible(true); // Show the window
        frame.setLocationRelativeTo(null); // Center window on screen
    }
}
