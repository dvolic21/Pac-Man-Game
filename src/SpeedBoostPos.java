import java.io.*;

/**
 * It's a class that holds the status of speed boots.
 */
public class SpeedBoostPos implements Serializable {

   private double boostPosX;
   private double boostPosY;

   public SpeedBoostPos(double boostPosX, double boostPosY) {
      this.boostPosX = boostPosX;
      this.boostPosY = boostPosY;
   }

   public double getBoostPosX() {
      return boostPosX;
   }

   public void setBoostPosX(double boostPosX) {
      this.boostPosX = boostPosX;
   }

   public double getBoostPosY() {
      return boostPosY;
   }

   public void setBoostPosY(double boostPosY) {
      this.boostPosY = boostPosY;
   }

}
