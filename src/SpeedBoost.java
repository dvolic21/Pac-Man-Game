import javafx.scene.image.*;
import javafx.scene.layout.*;
import java.io.*;

/**
 * A class for Boots that are SpeedBoosts
 */
public class SpeedBoost extends Pane {

   // private final PacmanDash pacmanDash;
   // private Image mapImage;

   public ImageView bootsView = null;

   private String bootsImage = "assets/boots/boots.png";

   public int bootsPosX;
   public int bootsPosY;

   private int animation = 0;

   /**
    * Creates SpeedBoost with random coordinates
    */
   public SpeedBoost() {

      // this.pacmanDash = pacmanDash;
      // this.mapImage = mapImage;

      try {
         bootsView = new ImageView(new Image(new FileInputStream(bootsImage)));
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

      randomCoords();

      this.getChildren().addAll(bootsView);
   }

   /**
    * It generates a random number between two values and sets the Boots's X and Y
    * coordinates to that number
    */
   public void randomCoords() {

      bootsPosX = this.randomNumber(20, 420);

      this.bootsView.setX(bootsPosX);

      bootsPosY = this.randomNumber(500, 2200);

      this.bootsView.setY(bootsPosY);
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
    * A small animation to make the boots move up and down
    */
   public void update() {

      if (animation <= 30) {

         this.bootsView.setY(bootsPosY + 3);

      } else if (animation <= 60 && animation > 30) {

         this.bootsView.setY(bootsPosY);

      } else if (animation > 90) {
         animation = 0;
      }

      animation++;

   }

}