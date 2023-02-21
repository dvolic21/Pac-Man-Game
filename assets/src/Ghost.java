import java.io.*;
import java.util.*;

import javafx.scene.image.*;
import javafx.scene.layout.*;

/**
 * It's a class that creates a ghost object that moves around the screen, spooky
 */
public class Ghost extends Pane {

   // private final PacmanDash pacmanDash;
   // String[] soundGhosts = { "Blue", "Orange", "Pink", "Red" };
   // String path = "assets/sounds/soundGhost/soundsOn";
   // String color = soundGhosts[new Random().nextInt(soundGhosts.length)];

   private String[] soundGhosts = { "cyan", "orange", "pink", "red" };

   private String color = soundGhosts[new Random().nextInt(soundGhosts.length)];

   private String path = "assets/ghosts/gifGhosts/";

   private String ghostImage = path + color + "ghost.gif";
   private int speed = 1;

   private boolean up = false;
   private boolean down = false;
   private boolean left = false;
   private boolean right = true;

   public ImageView ghostView = null;

   public int ghostPosX;
   public int ghostPosY;

   private int i;
   // private Image imageMap;

   public Ghost() {

      try {
         ghostView = new ImageView(new Image(new FileInputStream(ghostImage)));
         // ghostView.setCache(true);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

      randomCoords();

      this.getChildren().addAll(ghostView);

   }

   /**
    * It generates a random number between two values and sets the ghost's X and Y
    * coordinates to that
    * number
    */
   public void randomCoords() {

      ghostPosX = this.randomNumber(20, 420);
      // boundaryXPos = 450 - ghostPosX;
      // boundaryXNeg = boundaryXPos - 450;

      this.ghostView.setX(ghostPosX);

      // System.out.println(color + "Ghost Pos X: " + ghostView.getX() + ", XPos: " +
      // boundaryXPos +
      // ", XNeg: " + boundaryXNeg);

      ghostPosY = this.randomNumber(300, 1500);

      // boundaryYPos = 2450 - ghostPosY;
      // boundaryYNeg = boundaryYPos - 2450;

      this.ghostView.setY(ghostPosY);

      // System.out.println(color + "Ghost Pos Y: " + ghostView.getY() + ", YPos: " +
      // boundaryYPos +
      // ", YNeg: " + boundaryYNeg);

   }

   /**
    * `return (int) ((Math.random() * (max - min)) + min);`
    * 
    * The above function will return a random number between the min and max values
    * 
    * @param min The minimum number that the random number can be.
    * @param max The maximum number that can be generated.
    * @return The random number between the min and max.
    */
   public int randomNumber(int min, int max) {
      return (int) ((Math.random() * (max - min)) + min);
   }

   /**
    * The ghost moves randomly, but it can't go off the screen
    */
   public void update() {

      if (up) {
         ghostPosY -= speed * 2;
         ghostView.setY(ghostPosY);
      }

      if (down) {
         ghostPosY += speed * 2;
         ghostView.setY(ghostPosY);
      }

      if (ghostView.getX() <= 0) {
         right = true;
         left = false;
      }

      if (ghostView.getX() >= 450) {
         right = false;
         left = true;
      }

      if (ghostView.getY() <= 100) {
         down = true;
         up = false;
      }

      if (ghostView.getY() >= 2400) {
         down = false;
         up = true;
      }

      if (right) {
         ghostPosX += speed;
         ghostView.setX(ghostPosX);
      }

      if (left) {
         ghostPosX -= speed;
         ghostView.setX(ghostPosX);
      }

      // System.out.println(color + ": X: " + ghostPosX + " Y: " + ghostPosY);

      // How the ghost moves
      if (i > 30) {

         double move = Math.random();

         if (move < 0.2) {
            up = true;
            down = false;

         } else if (move > 0.9) {
            up = false;
            down = true;

         } else if (move < 0.7 && move > 0.4) {

            up = false;
            down = false;
            left = false;
            right = false;

            double moveLR = Math.random();

            if (moveLR < 0.1) {
               left = true;
               right = false;
            } else if (moveLR > 0.9) {
               left = false;
               right = true;
            }

         }

         i = 0;
      }

      i++;

   }

}