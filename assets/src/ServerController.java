import javafx.application.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;

/**
 * ServerController for PacmanDashServer class, used for FXML
 */
public class ServerController {

   static PacmanDashServer pacmanDashServer;

   @FXML
   private TextArea taPlayer1;

   @FXML
   private TextArea taPlayer2;

   @FXML
   private Label lblPlayer1;

   @FXML
   private Label lblPlayer2;

   /**
    * Gets the source of the button and does depending on the text of the button
    * 
    * @param event
    */
   @FXML
   private void btnClick(Event event) {

      Object obj = event.getSource();

      if (obj instanceof Button) {

         Button btn = (Button) obj;

         switch (btn.getText()) {

            case "Start Server": {

               pacmanDashServer.runServer();

               btn.setText("Stop Server");

               break;
            }

            case "Stop Server": {

               pacmanDashServer.stopServer();

               btn.setText("Start Server");

               break;
            }
            case "Exit": {

               Platform.exit();
               System.exit(0);

               break;
            }

         }

      }

   }

   /**
    * Thread-safe logging to the GUI
    * 
    * @param message
    */
   public void logPlayer1(String message) {
      Platform.runLater(() -> {
         taPlayer1.appendText(message + "\n");
      });
   }

   /**
    * Sets the main class to the controller
    * 
    * @param server
    */
   public static void setServer(PacmanDashServer server) {
      pacmanDashServer = server;
   }

}
