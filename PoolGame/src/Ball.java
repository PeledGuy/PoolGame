import java.awt.Color;
import java.awt.Graphics;

public class Ball {
    public Vector2D position;
    public Vector2D velocity;
    public double radius;
    public double mass;
    public Color color;

    public enum BallType {
        CUE, SOLID, STRIPE, EIGHT_BALL
    }

    public Ball(double x, double y, double vx, double vy, double radius, Color color) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(vx, vy);
        this.radius = radius;
        this.mass = radius * radius;
        this.color = color;
    }

    public void update(double dt) {
        velocity.multiply(0.985);

        if (velocity.magnitude() < 0.5) {
            velocity.x = 0;
            velocity.y = 0;
        }

        Vector2D displacement = new Vector2D(velocity.x, velocity.y);
        displacement.multiply(dt);

        this.position.add(displacement);
    }

    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval((int)(position.x - radius), (int)(position.y - radius), (int)(radius * 2), (int)(radius * 2));
    }

    public void applyImpulse(Vector2D force) {
        Vector2D velocityChange = new Vector2D(force.x / mass, force.y / mass);
        this.velocity.add(velocityChange);
    }
}
