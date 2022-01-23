import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.canvas.*;
import javafx.animation.AnimationTimer;
import java.util.HashSet;
import java.util.Random;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.Set;

public class Main {

    public static class FrameRegulator {

        private Integer frameCounter = 0;
        private Long tick = 0L;
        private double frameLength = 0;
        private double fpsTimer = 0;
        private int fps = 0;

        public void updateFPS(long now, GraphicsContext gc) {

            Long tock = now / 1000000;
            if (tick != 0) {
                frameLength = (tock - tick) / 1000.0;
            }
            tick = tock;

            frameCounter += 1;
            fpsTimer += frameLength;
            if (fpsTimer >= 1) {
                fps = frameCounter;
                frameCounter = 0;
                fpsTimer -= 1;
            }

            if (fps > 0) {
                gc.strokeText("Java (JavaFX)   " + fps + " FPS", 20, 20);
            }
        }

        public double getFrameLength() {
            return frameLength;
        }

    }

    public static class Invader {

        protected Image image;
        protected double x;
        protected double y;
        protected double dx;
        protected double dy;
        protected double r;
        protected boolean expired;

        public Invader(double x, double y, double r, Image image) {
            this.x = x;
            this.y = y;
            this.image = image;
            this.r = r;
        }

        public void setVelocity(double dx, double dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public Image getImage() {
            return image;
        }

        public void update(double frameLength) {
            x += dx * frameLength;
            y += dy * frameLength;

            if (x < -100)
                x += Main.WINDOW_WIDTH + 200;
            if (y < -100)
                y += Main.WINDOW_HEIGHT + 200;
            if (x > 100 + Main.WINDOW_WIDTH)
                x -= Main.WINDOW_WIDTH + 200;
            if (y > 100 + Main.WINDOW_HEIGHT)
                y -= Main.WINDOW_HEIGHT + 200;

        }

        public static void clearUpExired(Set<Invader> entities) {
            var expiredEntityRemover = entities.iterator();
            while (expiredEntityRemover.hasNext()) {
                var s = expiredEntityRemover.next();
                if (s.expired)
                    expiredEntityRemover.remove();
            }
        }

        public boolean collidesWith(Invader other) {
            if (Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) < Math.pow(r + other.r, 2)) {
                return true;
            } else {
                return false;
            }
        }

    }

    public static final int WINDOW_WIDTH = 1920;
    public static final int WINDOW_HEIGHT = 1080;
    private static final String resourcesPath = "file:" + System.getProperty("user.dir") + "/resources/";

    static Set<KeyCode> keysPressed = new HashSet<>();

    public static void start() {
        System.out.println("Application Starting...");

        var fr = new FrameRegulator();
        var rnd = new Random();

        var root = new Group();
        var stage = new Stage();
        var scene = new Scene(root);
        var canvas = new Canvas();

        stage.setTitle("JavaFX Canvas Demo");
        stage.setResizable(false);
        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.setOnCloseRequest(we -> {
            System.out.println("Close button was clicked!");
            System.out.println("Terminating Application...");
            System.exit(0);
        });
        stage.show();
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> keysPressed.add(event.getCode()));
        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> keysPressed.remove(event.getCode()));

        canvas.setWidth(WINDOW_WIDTH);
        canvas.setHeight(WINDOW_HEIGHT);
        root.getChildren().add(canvas);

        var gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.WHITE);
        gc.setFont(new Font("Arial", 14));

        var image = new Image(resourcesPath + "sprite.png");

        var invaders = new HashSet<Invader>();

        for (var i = 0; i <= 100; i++) {
            var s = new Invader(rnd.nextInt(WINDOW_WIDTH), rnd.nextInt(WINDOW_HEIGHT), 32, image);
            s.setVelocity(rnd.nextDouble() * 100 - 50, rnd.nextDouble() * 100 - 50);
            invaders.add(s);

        }

        new AnimationTimer() {
            @Override
            public void handle(long now) {

                /* INPUT */

                for (var k : keysPressed) {
                    if (k == KeyCode.ESCAPE) {
                        System.out.println("Terminating Application...");
                        System.exit(0);
                    }
                }

                /* PROCESS */

                for (var s : invaders) {
                    s.update(fr.getFrameLength());
                }
                Invader.clearUpExired(invaders);

                /* OUTPUT */

                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

                for (var s : invaders) {
                    gc.drawImage(s.getImage(), s.getX() - s.getImage().getWidth() / 2,
                            s.getY() - s.getImage().getHeight() / 2);
                }
                fr.updateFPS(now, gc);

            }
        }.start();

    }

    public static void main(String args[]) {
        new JFXPanel();
        Platform.runLater(() -> start());
    }

}