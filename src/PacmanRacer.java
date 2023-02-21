import java.io.*;
import java.util.*;

import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;

/**
 * It's a class that creates a pacman racer and allows it to move around the map
 */
class PacmanRacer extends Pane {

    private final PacmanDash pacmanDash;

    private String path = "assets/gifPacman/pacman";
    private String color = "cyan";

    public String ICON_IMAGE = path + "-" + color + ".gif";

    private int racePosX = 0; // x position of the racer
    private int racePosY = 0; // y position of the racer
    // private int raceROT = 0; // x position of the racer
    public ImageView aPicView; // a view of the icon ... used to display and move the image

    // Player movement
    public boolean UpPressed = false;
    public boolean DownPressed = false;
    public boolean LeftPressed = false;
    public boolean RightPressed = true;

    public boolean collision = false;
    public boolean bootCollision = false;

    public double speed;
    private int boost = 0;

    // Controls
    public KeyCode KEYCODE_RIGHT;
    public KeyCode KEYCODE_LEFT;
    public KeyCode KEYCODE_UP;
    public KeyCode KEYCODE_DOWN;

    private Pane map;
    private Image mapImage;

    private ImageView hatForPacman = null;

    private String name;

    /**
     * General constructor that takes in the inputs of the user
     * 
     * @param pacmanDash
     * @param name
     * @param color
     * @param right
     * @param left
     * @param up
     * @param down
     * @param map1
     * @param mapImage
     * @param setY
     * @param setX
     */
    public PacmanRacer(PacmanDash pacmanDash, String name, String color, KeyCode right, KeyCode left, KeyCode up,
            KeyCode down,
            Pane map1, Image mapImage, int setY, int setX) {

        this.color = color;

        ICON_IMAGE = path + "-" + color + ".gif";

        this.pacmanDash = pacmanDash;
        // Draw the icon for the racer

        KEYCODE_RIGHT = right;
        KEYCODE_LEFT = left;
        KEYCODE_UP = up;
        KEYCODE_DOWN = down;

        map = map1;
        this.mapImage = mapImage;
        this.name = name;

        racePosY = setY;
        racePosX = setX;

        try {
            aPicView = new ImageView(new Image(new FileInputStream(this.ICON_IMAGE)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.aPicView.setY(setY);
        this.aPicView.setX(setX);

        this.getChildren().add(aPicView);

        // aPicView.setFitHeight(50);
        // aPicView.setFitWidth(50);
    }

    /**
     * Constructor that creates a dummy pacman for the server updates
     * 
     * @param pacmanDash2
     * @param vsName
     * @param vsColor
     * @param map2
     * @param imageMap2
     * @param setY
     * @param setX
     */
    public PacmanRacer(PacmanDash pacmanDash2, String vsName, String vsColor, Pane map2, Image imageMap2, int setY,
            int setX) {

        this.name = vsName;
        this.color = vsColor;

        ICON_IMAGE = path + "-" + vsColor + ".gif";

        this.pacmanDash = pacmanDash2;
        // Draw the icon for the racer

        map = map2;
        this.mapImage = imageMap2;

        racePosY = setY;
        racePosX = setX;

        try {
            aPicView = new ImageView(new Image(new FileInputStream(this.ICON_IMAGE)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.aPicView.setY(setY);
        this.aPicView.setX(setX);

        this.getChildren().add(aPicView);

    }

    @Deprecated
    public void setHeadGear(String headgear) {

        if (headgear.isBlank() || headgear.isEmpty()) {
            // System.out.println("its blank!");
            return;
        }

        String file = "assets/custom/pacman/" + headgear + ".png";

        try {
            hatForPacman = new ImageView(new Image(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        switch (headgear) {
            case "PropHat": {

                hatForPacman.setFitWidth(40.0);
                hatForPacman.setFitHeight(45.0);

                hatForPacman.setX(245);
                hatForPacman.setY(2373);

                break;
            }

        }

        this.getChildren().add(hatForPacman);

    }

    /**
     * Updates the pacman position when being updated
     */
    public void update() {

        if (isOnBlue(0.6) || isOnRed(0.89)) {
            // System.out.println("I am on blue!");
            speed = 2;
        } else {
            speed = 4;
        }

        if (isOnGreen(0.9)) {
            // System.out.println("You win!! :D");
            speed = 0;
            PacmanDash.winner = true;
            PacmanDash.winnerName = name;

        }

        if (aPicView.getX() >= 450) {
            RightPressed = false;
        }

        if (aPicView.getX() <= 4) {
            LeftPressed = false;
        }

        if (bootCollision) {

            if (boost == 1) {

                PlayRandomSoundFile random = new PlayRandomSoundFile();
                random.start();

            }

            speed = 15;

            if (aPicView.getX() >= 440) {
                RightPressed = false;
            }

            if (aPicView.getX() <= 15) {
                LeftPressed = false;
            }

            if (boost == 10) {
                bootCollision = false;
                boost = 0;
            }

            boost++;
        }

        if (collision) {
            speed = 1;
        }

        if (UpPressed) {

            if (map.getTranslateY() >= 0) {
                racePosY -= speed;
                aPicView.setY(racePosY);

            } else {
                map.setTranslateY(map.getTranslateY() + speed);
                racePosY -= speed;
                aPicView.setY(racePosY);
            }
        }

        if (DownPressed) {

            if (map.getTranslateY() <= -1748) {
                speed = 0;
                return;
            }

            if (map.getTranslateY() >= 0) {

                racePosY += speed;
                aPicView.setY(racePosY);

            } else {

                map.setTranslateY(map.getTranslateY() - speed);
                racePosY += speed;
                aPicView.setY(racePosY);
                return;

            }

            if (racePosY > 600) {
                map.setTranslateY(map.getTranslateY() - speed);
                return;
            }

        }

        // hatForPacman.setX(245);
        // hatForPacman.setY(2373);

        if (LeftPressed) {
            racePosX -= speed;
            aPicView.setX(racePosX);
            if (hatForPacman != null) {

            }
        }

        if (RightPressed) {
            racePosX += speed;
            aPicView.setX(racePosX);
            if (hatForPacman != null) {

            }
        }

    } // end update()

    /**
     * If any of the four corners of the pacman are on green, return true.
     * 
     * @param value the value of the green color
     * @return The method isOnGreen() returns a boolean value.
     */
    private boolean isOnGreen(double value) {
        return pacmanDash.getColor(racePosX + 10, racePosY + 10, mapImage).getGreen() > value
                || pacmanDash.getColor(racePosX + 10, racePosY + 40, mapImage)
                        .getGreen() > value
                || pacmanDash.getColor(racePosX + 40, racePosY + 10, mapImage)
                        .getGreen() > value
                || pacmanDash.getColor(racePosX + 40, racePosY + 40, mapImage)
                        .getGreen() > value;
    }

    /**
     * If any of the four corners of the pacman are on blue, return true.
     * 
     * @param value the value of the blue color
     * @return The method isOnBlue() returns a boolean value.
     */
    private boolean isOnBlue(double value) {

        return pacmanDash.getColor(racePosX + 10, racePosY + 10, mapImage).getBlue() > value
                || pacmanDash.getColor(racePosX + 10, racePosY + 40, mapImage)
                        .getBlue() > value
                || pacmanDash.getColor(racePosX + 40, racePosY + 10, mapImage)
                        .getBlue() > value
                || pacmanDash.getColor(racePosX + 40, racePosY + 40, mapImage)
                        .getBlue() > value;

    }

    /**
     * If any of the four corners of the pacman are on red, return true.
     * 
     * 
     * @param value the value of the red color
     * @return The method is returning a boolean value.
     */
    private boolean isOnRed(double value) {
        return pacmanDash.getColor(racePosX + 10, racePosY + 10, mapImage).getRed() > value
                || pacmanDash.getColor(racePosX + 40, racePosY + 40, mapImage)
                        .getRed() > value
                || pacmanDash.getColor(racePosX + 40, racePosY + 10, mapImage)
                        .getRed() > value
                || pacmanDash.getColor(racePosX + 10, racePosY + 40, mapImage)
                        .getRed() > value;

    }

  
    /**
     * It plays a random sound file from a folder
     */
    public class PlayRandomSoundFile extends Thread {

       /**
        * It plays a random sound from a folder of sounds
        */
        @Override
        public void run() {

            int fileCount = (new Random().nextInt(4) + 1);

            String music = "assets/sounds/SpeedBoost/woosh" + fileCount + ".mp3";
            String musicFile = new File(music).toURI().toString();
            Media sound = new Media(musicFile);
            MediaPlayer woosh = new MediaPlayer(sound);
            woosh.setVolume(pacmanDash.soundPlayer.getVolume());
            woosh.play();

        }

    }

} // end inner class Racer