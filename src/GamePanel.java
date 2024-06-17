//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.awt.*;
//import java.awt.Graphics;
//import java.awt.event.KeyAdapter;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener{
    static final int SCREEN_WIDTH =600;
    static final int SCREEN_HEIGHT =600;
    static final int UNIT_SIZE =25;
    static final int GAME_UNITS =(SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 60;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 3;
    int applesEaten;
    int appleX;
    int appleY;
    char direction='R';
    boolean running=false;
    Timer timer;
    Random random;
    boolean specialApplePresent = false;
    int specialAppleX;
    int specialAppleY;
    Timer specialAppleTimer;
    int specialAppleTimerDelay = 12000; // 12 seconds
    int specialAppleDuration = 3000; // 3 seconds
    boolean shrinkingApplePresent = false;
    int shrinkingAppleX;
    int shrinkingAppleY;
    int shrinkingAppleTimerDelay = 8000; // 8 seconds
    int shrinkingAppleDuration = 4000; // 4 seconds
    Timer shrinkingAppleTimer;

    GamePanel(){
        random=new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame(){
        newApple();
        running=true;
        timer = new Timer(DELAY,this);
        timer.start();
        startSpecialAppleTimer();
        startShrinkingAppleTimer();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);

    }

    public void draw(Graphics g){
        if(running) {
            /* for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }*/
            g.setColor(Color.MAGENTA);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free",Font.BOLD,40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("SCORE:"+applesEaten,(SCREEN_WIDTH-metrics.stringWidth("SCORE:"+applesEaten))/2,g.getFont().getSize());
        }
        else{
            gameOver(g);
        }
        if (specialApplePresent) {
            g.setColor(Color.ORANGE); // Change color for special apple
            g.fillOval(specialAppleX, specialAppleY, UNIT_SIZE * 2, UNIT_SIZE * 2); // Double the size
        }
        if (shrinkingApplePresent) {
            g.setColor(Color.CYAN); // Change color for shrinking apple
            g.fillOval(shrinkingAppleX, shrinkingAppleY, UNIT_SIZE / 2, UNIT_SIZE / 2); // Half the size
        }
    }

    public void newApple(){
        //generate coordinates for new apple when it is called
        appleX = random.nextInt((int)SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
        appleY = random.nextInt((int)SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;

    }
    public void move(){
        for(int i=bodyParts;i>0;i--){
            x[i]=x[i-1];
            y[i]=y[i-1];
        }
        switch(direction){
            case 'U':
                y[0]=y[0]-UNIT_SIZE;
                break;
            case 'D':
                y[0]=y[0]+UNIT_SIZE;
                break;
            case 'L':
                x[0]=x[0]-UNIT_SIZE;
                break;
            case 'R':
                x[0]=x[0]+UNIT_SIZE;
                break;
        }

    }
    public void checkApple(){
        if((x[0]==appleX) && (y[0]==appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }

    }

    public void checkCollisions(){
        for(int i=bodyParts;i>0;i--){
            if((x[0] ==x[i]) && (y[0]==y[i])){
                running = false;
            }
        }
        if(x[0]<0) running=false;   //left border
        if(x[0]>SCREEN_WIDTH) running=false;   //right border
        if(y[0]<0) running=false; // collision at top
        if(y[0]>SCREEN_HEIGHT) running =false; // collision at bottom
        if(!running) timer.stop();

    }

    public void gameOver(Graphics g){
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free",Font.BOLD,40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("SCORE:"+applesEaten,(SCREEN_WIDTH-metrics1.stringWidth("SCORE:"+applesEaten))/2,g.getFont().getSize());

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free",Font.BOLD,75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("GAME OVER",(SCREEN_WIDTH-metrics2.stringWidth("GAME OVER"))/2,SCREEN_HEIGHT/2);
        specialApplePresent = false;
        shrinkingApplePresent = false;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            checkApple();
            checkCollisions();
            checkSpecialAppleCollision();
            checkShrinkingAppleCollision();
        }
        repaint();

    }

    public void startSpecialAppleTimer() {
        specialAppleTimer = new Timer(specialAppleTimerDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) { // Only spawn special apple if the game is running
                    spawnSpecialApple();
                }
            }
        });
        specialAppleTimer.start();
    }

    public void spawnSpecialApple() {
        if (!specialApplePresent) {
            specialAppleX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            specialAppleY = random.nextInt((int) SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            specialApplePresent = true;
            repaint();
            // Schedule the removal of the special apple after specialAppleDuration milliseconds
            Timer removeSpecialAppleTimer = new Timer(specialAppleDuration, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    specialApplePresent = false;
                    repaint();
                }
            });
            removeSpecialAppleTimer.setRepeats(false); // Ensure it only runs once
            removeSpecialAppleTimer.start();
        }
    }

    public void checkSpecialAppleCollision() {
        if (specialApplePresent && x[0] == specialAppleX && y[0] == specialAppleY) {
            applesEaten *= 2; // Double the score
            specialApplePresent = false;
            repaint();
        }
    }

    public void startShrinkingAppleTimer() {
        shrinkingAppleTimer = new Timer(shrinkingAppleTimerDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) { // Only spawn shrinking apple if the game is running
                    spawnShrinkingApple();
                }
            }
        });
        shrinkingAppleTimer.start();
    }

    public void spawnShrinkingApple() {
        if (!shrinkingApplePresent) {
            shrinkingAppleX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            shrinkingAppleY = random.nextInt((int) SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            shrinkingApplePresent = true;

            // Schedule the removal of the shrinking apple after shrinkingAppleDuration milliseconds
            Timer removeShrinkingAppleTimer = new Timer(shrinkingAppleDuration, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    shrinkingApplePresent = false;
                    repaint();
                }
            });
            removeShrinkingAppleTimer.setRepeats(false);
            removeShrinkingAppleTimer.start();
        }
    }

    public void checkShrinkingAppleCollision() {
        if (shrinkingApplePresent && x[0] == shrinkingAppleX && y[0] == shrinkingAppleY) {
            applesEaten /= 2; // Decrease the score by half
            shrinkingApplePresent = false;
            repaint();
        }
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // Implement your key press handling here
            switch(e.getKeyCode()){
            case KeyEvent.VK_LEFT:
                if(direction !='R'){
                    direction = 'L';
                }
                break;
                case KeyEvent.VK_RIGHT:
                    if(direction !='L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction !='D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction !='U'){
                        direction = 'D';
                    }
                    break;
            }
        }
    }


}
