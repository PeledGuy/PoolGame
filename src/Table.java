import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;

public class Table extends JPanel {
    private ArrayList<Ball> balls;
    private Ball cueBall;

    private final double DELTA_TIME = 0.16;

    private Point clickStart;
    private Point currentDrag;
    private boolean isAiming = false;

    public Table() {
        balls = new ArrayList<>();

        cueBall = new Ball(100, 300, 0, 0, 15, Color.WHITE);
        balls.add(cueBall);
        balls.add(new Ball(600, 300, 0, 0, 15, Color.RED));

        Timer timer = new Timer(16, e -> gameLoop());
        timer.start();

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clickStart = e.getPoint();
                currentDrag = e.getPoint();
                isAiming = true;
            }
        
            @Override
            public void mouseDragged(MouseEvent e) {
                currentDrag = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isAiming = false;

                double dx = clickStart.x - e.getX();
                double dy = clickStart.y - e.getY();
                Vector2D dragVec = new Vector2D(dx, dy);

                double maxDrag = 200.0;
                if (dragVec.magnitude() > maxDrag) {
                    dragVec = dragVec.normalize();
                    dragVec.multiply(maxDrag);
                }

                Vector2D force = new Vector2D(dx * 50, dy * 50); 
                cueBall.applyImpulse(force);
            }
        };

        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
    
    }

    private void gameLoop() {
        for (Ball b : balls) {
            b.update(DELTA_TIME);
            checkWallCollisions(b);
        }
        checkBallCollisions();

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw the green table felt
        g.setColor(new Color(34, 139, 34)); 
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw all the balls
        for (Ball b : balls) {
            b.draw(g);
        }

        // Draw aiming line trajectory
        if (isAiming && clickStart != null && currentDrag != null) {
            g.setColor(Color.WHITE);

            Vector2D dragVec = new Vector2D(currentDrag.x - clickStart.x, currentDrag.y - clickStart.y);

            double maxDrag = 200.0;
            if (dragVec.magnitude() > maxDrag) {
                dragVec = dragVec.normalize();
                dragVec.multiply(maxDrag);
            }

            int drawX = (int) (dragVec.x / 2);
            int drawY = (int) (dragVec.y / 2);

            int cueX = (int) cueBall.position.x;
            int cueY = (int) cueBall.position.y;

            g.drawLine(cueX, cueY, cueX + drawX, cueY + drawY);        }
    }

    private void checkWallCollisions(Ball b) {
        // Left Wall
        if (b.position.x - b.radius < 0) {
            b.position.x = b.radius; 
            b.velocity.x *= -0.8;      
        }
        // Right Wall
        else if (b.position.x + b.radius > getWidth()) {
            b.position.x = getWidth() - b.radius;
            b.velocity.x *= -0.8;
        }

        // Top Wall
        if (b.position.y - b.radius < 0) {
            b.position.y = b.radius;
            b.velocity.y *= -0.8;
        }
        // Bottom Wall
        else if (b.position.y + b.radius > getHeight()) {
            b.position.y = getHeight() - b.radius;
            b.velocity.y *= -0.8;
        }
    }

    private void checkBallCollisions() {
        for (int i = 0; i < balls.size(); i ++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball b1 = balls.get(i);
                Ball b2 = balls.get(j);

                double dist = Vector2D.distance(b1.position, b2.position);
                double minDistance = b1.radius + b2.radius;
                
                if (dist < minDistance) {
                    double overlap = minDistance - dist;

                    Vector2D normal = Vector2D.subtract(b2.position, b1.position);
                    normal = normal.normalize();

                    b1.position.x -= normal.x * (overlap / 2);
                    b1.position.y -= normal.y * (overlap / 2);
                    b2.position.x += normal.x * (overlap / 2);
                    b2.position.y += normal.y * (overlap / 2);

                    double v1n = b1.velocity.dot(normal);
                    double v2n = b2.velocity.dot(normal);

                    if (v1n < 0 && v2n > 0) continue;

                    double deltaV1 = v2n - v1n;
                    double deltaV2 = v1n - v2n;

                    b1.velocity.x += deltaV1 * normal.x;
                    b1.velocity.y += deltaV1 * normal.y;
                
                    b2.velocity.x += deltaV2 * normal.x;
                    b2.velocity.y += deltaV2 * normal.y;
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Java Pool Physics Engine");
        Table table = new Table();
        
        frame.add(table);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
