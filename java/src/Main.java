import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.canvas.*;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Main {

    public static final int WINDOW_WIDTH = 1920;
    public static final int WINDOW_HEIGHT = 1080;

    public static class Invader {

        private double x;
        private double y;
        private double dx;
        private double dy;

        public Invader(double x, double y) {
            this.x = x;
            this.y = y;
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
    }

    private static final String resourcesPath = "file:" + System.getProperty("user.dir") + "/resources/";

    static Set<KeyCode> keysPressed = new HashSet<>();

    private static Integer frameCounter = 0;
    private static Long tick = 0L;
    private static double frameLength = 0;
    private static double fpsTimer = 0;
    private static int fps = 0;

    private static Set<Invader> invaders;
    private static Image invaderImage;

    private static void inputs() {

        for (var k : keysPressed) {
            if (k == KeyCode.ESCAPE) {
                System.out.println("Terminating Application...");
                System.exit(0);
            }
        }

    }

    private static void processes() {
        for (var s : invaders) {
            s.update(frameLength);
        }

    }

    private static void outputs(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        for (var s : invaders) {
            gc.drawImage(invaderImage, s.getX() - invaderImage.getWidth() / 2,
                    s.getY() - invaderImage.getHeight() / 2);
        }

        if (fps > 0) {
            gc.setFill(Color.WHITE);
            gc.fillText("Java (JavaFX)   " + fps + " FPS", 20, 20);
        }
    }

    public static void start() {
        System.out.println("Application Starting...");

        var rnd = new Random();

        var root = new Group();
        var stage = new Stage();
        var scene = new Scene(root);
        var canvas = new Canvas();

        stage.setTitle("Polygame Project - Java");
        stage.setResizable(false);
        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.setOnCloseRequest(we -> {
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
        gc.setFont(new Font("Arial", 14));

        invaderImage = new Image(resourcesPath + "sprite.png");
        invaders = new HashSet<Invader>();

        for (var i = 0; i <= 100; i++) {
            var s = new Invader(rnd.nextInt(WINDOW_WIDTH), rnd.nextInt(WINDOW_HEIGHT));
            s.setVelocity(rnd.nextDouble() * 100 - 50, rnd.nextDouble() * 100 - 50);
            invaders.add(s);
        }

        new AnimationTimer() {
            @Override
            public void handle(long now) {

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

                inputs();
                processes();
                outputs(gc);

            }
        }.start();

    }

    public static void main(String args[]) {
        new JFXPanel();
        Platform.runLater(() -> start());
    }

}