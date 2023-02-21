import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.stage.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * It's a server that hosts a game of PacmanDash.
 */
public class PacmanDashServer extends Application {

   // Window attributes
   private Stage stage;
   public Scene scene;
   private Parent root;

   // Network Stuff
   private ServerSocket sSocket = null;
   public List<ObjectOutputStream> players = new Vector<ObjectOutputStream>();

   // ServerController
   private ServerController serverController;

   private static String[] args;

   // Map Choosing
   private String pathToMap = "assets/maps/";
   public String[] maps = { "BeachMap.png", "LavaMap.png", "OGMap.png" };

   public String map;

   /**
    * The main function is the entry point of the program
    * 
    * @param _args The arguments passed to the program.
    */
   public static void main(String[] _args) {
      args = _args;
      launch(args);

   }

   /**
    * It loads the FXML file and sets the scene
    * 
    * @param _stage The stage that the server is running on.
    */
   @Override
   public void start(Stage _stage) {

      stage = _stage;

      stage.setTitle("PacMan Dash");

      stage.setOnCloseRequest(evt -> {
         Platform.exit();
         System.exit(0);
      });

      FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("PacmanDashServer.fxml"));

      try {
         this.root = loader.load();
      } catch (IOException e) {
         e.printStackTrace();
      }

      this.serverController = loader.getController();

      ServerController.setServer(this);

      scene = new Scene(root);

      stage.setScene(scene);
      stage.show();

      try {
         stage.getIcons().addAll(new Image(new FileInputStream("assets/logo.png")));
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

   }

   /**
    * This function takes a string as a parameter and calls the logPlayer1 function
    * in the
    * serverController class.
    * 
    * @param message The message to be logged
    */
   public void logPlayer1(String message) {
      serverController.logPlayer1(message);
   }

   /**
    * Starts the server Thread
    */
   public void runServer() {
      PacmanDashServerThread sdSR = new PacmanDashServerThread();
      sdSR.start();
   }

   /**
    * The function stops the server by closing the server socket
    */
   public void stopServer() {

      try {

         if (sSocket != null) {
            sSocket.close();
         }

         System.out.println("Server Stopped");

      } catch (IOException ioe) {
         ioe.printStackTrace();
      }

   }

   /**
    * It creates a server socket, waits for a client to connect, and then creates a
    * new thread for that
    * client
    */
   protected class PacmanDashServerThread extends Thread implements PacmanDashConstants {

      /**
       * This function is called when the server is started, and it waits for a client
       * to connect, and
       * then creates a new thread for that client.
       */
      @Override
      public void run() {

         Socket cSocket = null;

         try {

            sSocket = new ServerSocket(SERVER_PORT);

            while (!sSocket.isClosed()) {

               logPlayer1("Waiting for the next client...");

               cSocket = sSocket.accept();

               PacmanDashClientThread pdCT = new PacmanDashClientThread(cSocket);
               pdCT.start();

            }

         } catch (SocketException e) {
            // e.printStackTrace();

         } catch (IOException e) {
            e.printStackTrace();
         } finally {
            try {
               if (cSocket != null) {
                  cSocket.close();
               }
            } catch (IOException e) {
               e.printStackTrace();
            }
         }

      }

   }

   /**
    * It's a thread that handles the client's connection to the server
    */
   protected class PacmanDashClientThread extends Thread {

      // Network stuff
      private ObjectInputStream ois = null;
      private ObjectOutputStream oos = null;
      private Socket cSocket;

      // Player attributes
      private String name;
      private String color;
      // private String headgear; Not used

      /**
       * Constructor that takes in socket parameter
       * 
       * @param socket
       */
      public PacmanDashClientThread(Socket socket) {

         this.cSocket = socket;

      }

      /**
       * It's a function that handles the server side of the game
       */
      @Override
      public void run() {

         try {

            this.ois = new ObjectInputStream(cSocket.getInputStream());
            this.oos = new ObjectOutputStream(cSocket.getOutputStream());

            players.add(oos);

            while (!cSocket.isClosed()) {

               Object obj = ois.readObject();

               if (obj instanceof PacmanDashChat) {

                  PacmanDashChat chat = (PacmanDashChat) obj;

                  String name = chat.getName();
                  String message = chat.getMessage();

                  for (ObjectOutputStream oStream : players) {

                     oStream.writeObject(name + ": " + message);
                     oStream.flush();

                  }

               } else if (obj instanceof PacmanDashPacStatus) {

                  PacmanDashPacStatus pacStatus = (PacmanDashPacStatus) obj;

                  for (ObjectOutputStream player : players) {

                     if (player != this.oos) {

                        player.writeObject(pacStatus);
                        player.flush();

                     }

                  }

               } else if (obj instanceof SpeedBoostPos) {

                  SpeedBoostPos speed = (SpeedBoostPos) obj;

                  for (ObjectOutputStream player : players) {

                     if (player != this.oos) {

                        player.writeObject(speed);
                        player.flush();

                     }

                  }

               } else if (obj instanceof PacmanDashRegisterPacman) {

                  PacmanDashRegisterPacman registerPacman = (PacmanDashRegisterPacman) obj;

                  this.name = registerPacman.getName();
                  this.color = registerPacman.getColor();
                  // this.headgear = registerPacman.getHeadgear(); Not used

                  logPlayer1(name + ": Connected. Their color is: " + color);

                  if (players.size() % 2 == 0) {

                     oos.writeObject(map);
                     oos.flush();

                     logPlayer1("Map chosen: " + map);

                  } else if (players.size() % 2 == 1) {

                     map = pathToMap + maps[new Random().nextInt(maps.length)];
                     oos.writeObject(map);
                     oos.flush();

                  }

               } else if (obj instanceof String) {

                  String command = (String) obj;

                  switch (command) {

                     case "SEND_CLIENT_COUNT": {

                        int playersCount = players.size();
                        System.out.println(playersCount);

                        oos.writeObject(playersCount + "");
                        oos.flush();

                        break;
                     }

                  }

               }
            }

         } catch (SocketException se) {

         } catch (IOException e) {
            e.printStackTrace();
         } catch (ClassNotFoundException e) {
            e.printStackTrace();
         } finally {
            try {
               players.remove(oos);
               if (cSocket != null) {
                  cSocket.close();
               }
               logPlayer1(name + ": Disconnected.");
            } catch (IOException e) {
               e.printStackTrace();
            }
         }

      }

   }

}
