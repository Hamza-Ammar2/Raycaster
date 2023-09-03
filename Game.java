import java.awt.*;

public class Game {
    static final int SCREEN_WIDTH = 500;
    static final int SCREEN_HEIGHT = 500;

    Renderer renderer;
    Game() {
        renderer = new Renderer();
    }


    public void update() {
        renderer.cameraControl.update();
    }

    public void draw(Graphics g) {
        renderer.draw(g);
    }
}
