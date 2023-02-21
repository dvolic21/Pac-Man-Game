import java.io.*;

/**
 * It's a class that holds the status of a player.
 */
public class PacmanDashPacStatus implements Serializable {

   private String name;
   private double posX;
   private double posY;
   private boolean up;
   private boolean down;
   private boolean left;
   private boolean right;
   private double mapY;
   private boolean winner;

   /**
    * Creates a PacmanDashStatus which is being sent to the server
    * 
    * @param name
    * @param d
    * @param e
    * @param up
    * @param down
    * @param left
    * @param right
    * @param mapY
    * @param winner
    */
   public PacmanDashPacStatus(String name, double d, double e, boolean up, boolean down, boolean left, boolean right,
         double mapY, boolean winner) {
      this.posX = d;
      this.posY = e;
      this.up = up;
      this.down = down;
      this.left = left;
      this.right = right;
      this.mapY = mapY;
      this.winner = winner;
   }

   public double getPosX() {
      return posX;
   }

   public void setPosX(double posX) {
      this.posX = posX;
   }

   public double getPosY() {
      return posY;
   }

   public void setPosY(double posY) {
      this.posY = posY;
   }

   public boolean isUp() {
      return up;
   }

   public void setUp(boolean up) {
      this.up = up;
   }

   public boolean isDown() {
      return down;
   }

   public void setDown(boolean down) {
      this.down = down;
   }

   public boolean isLeft() {
      return left;
   }

   public void setLeft(boolean left) {
      this.left = left;
   }

   public boolean isRight() {
      return right;
   }

   public void setRight(boolean right) {
      this.right = right;
   }

   public double getMapY() {
      return mapY;
   }

   public void setMapY(double mapY) {
      this.mapY = mapY;
   }

   public boolean isWinner() {
      return winner;
   }

   public void setWinner(boolean winner) {
      this.winner = winner;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

}
