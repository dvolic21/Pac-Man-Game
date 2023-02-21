import java.io.*;

/**
 * It's a simple Java class that contains two String variables, a constructor,
 * and getters and setters
 * for those variables.
 * 
 * It is used to send the chat message to the server and back to the clients.
 */
public class PacmanDashChat implements Serializable {

   private String name;
   private String message;

   public PacmanDashChat(String name, String message) {
      this.name = name;
      this.message = message;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

}
