import java.io.*;

/**
 * Registering the pacman
 */
public class PacmanDashRegisterPacman implements Serializable {

   private String name;
   private String color;
   private String headgear;

   public PacmanDashRegisterPacman(String name, String color, String headgear) {
      this.name = name;
      this.color = color;
      this.headgear = headgear;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getColor() {
      return color;
   }

   public void setColor(String color) {
      this.color = color;
   }

   public String getHeadgear() {
      return headgear;
   }

   public void setHeadgear(String headgear) {
      this.headgear = headgear;
   }

}