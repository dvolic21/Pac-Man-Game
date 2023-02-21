import javafx.application.*;
// import javafx.beans.value.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
// import javafx.scene.transform.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import javafx.util.*;
import javafx.animation.*;
import java.net.*;
import java.util.*;
import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Main class where the main screen is initialized, as well as singleplayer and
 * multiplayer. Furthermore, it sets reads and writes a xml file depending on
 * the settings set by the user in-game. The class also checks for collision depending on the Objects given.
 */

public class PacmanDash extends Application implements PacmanDashConstants {

   // TODO:
   // ! Better Server-Client communication
   // ! Includes position of the pacman, name etc.
   // ! Hats on pacman during gameplay

   // Window attributes
   private Stage stage;
   public Scene scene;
   private Parent root;

   // Settings
   private static final String SETTINGS_FILE = "settings.xml";

   // Music & Sounds
   public MediaPlayer musicPlayer;
   public MediaPlayer soundPlayer;

   // Video
   public MediaPlayer videoPlayer;

   // Music Mute
   private static String musicMute;
   private static String soundsMute;

   public static double musicVolume;
   public static double soundsVolume;

   // Player Values
   private static String player1Name;
   private static String player1Color;
   private static String player1HeadGear;

   private static String player2Name;
   private static String player2Color;
   private static String player2HeadGear;

   // Game
   private static final int GHOST_COUNT = 4;
   private static final int SPEED_BOOST_COUNT = 3;
   public List<Ghost> ghostVectorList1 = new Vector<Ghost>();
   public List<Ghost> ghostVectorList2 = new Vector<Ghost>();
   public List<SpeedBoost> speedBoostList1 = new Vector<SpeedBoost>();
   public List<SpeedBoost> speedBoostList2 = new Vector<SpeedBoost>();

   // Maps
   private String pathToMap = "assets/maps/";
   public String[] maps = { "BeachMap.png", "LavaMap.png", "OGMap.png" };

   public Pane map1 = new Pane();
   public Pane map2 = new Pane();
   private ImageView imageMapView;
   private ImageView imageMapView2;
   private Image imageMap;
   private Image imageMap2;

   // Gameplay
   public static boolean winner = false;
   public static String winnerName;

   // private boolean gameOn = false;
   private String vsName = "";
   private String vsColor = "";
   private String vsHeadgear = "";

   private AnimationTimer timer; // timer to control animation

   // Networking Stuff
   private Socket socket = null;
   private ObjectInputStream ois = null;
   private ObjectOutputStream oos = null;
   private String multiplayerMap;

   // Extra Stuff
   private static String[] args;
   private String css = this.getClass().getResource("style.css").toExternalForm();

   // private int iconWidth; // width (in pixels) of the icon
   // private int iconHeight; // height (in pixels) or the icon
   // private PacmanRacer racer = null; // array of racers

   // References
   // (For JavaFX)
   // https://www.youtube.com/c/BroCodez
   // (For JavaFX Video Game logic)
   // https://www.youtube.com/channel/UCS94AD0gxLakurK-6jnqV1w
   // (XML Files)
   // https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
   // Countdown Timer
   // https://asgteach.com/2011/10/javafx-animation-and-binding-simple-countdown-timer-2/

   /**
    * main program
    * 
    * @param _args
    */
   public static void main(String[] _args) {
      args = _args;
      launch(args);
   }

   /**
    * Creates the main stage
    * 
    * @param _stage
    */
   public void start(Stage _stage) {

      // stage setup
      stage = _stage;
      stage.setTitle("PacMan Dash");
      stage.setOnCloseRequest(evt -> {
         if (musicPlayer != null) {
            musicPlayer.stop();
         }
         saveSettings();
         Platform.exit();
         System.exit(0);
      });

      // root pane
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainScreen.fxml"));

      // System.out.println(loader.getLocation());

      try {
         this.root = loader.load();
      } catch (IOException e) {
         e.printStackTrace();
      }

      readSettings();

      String music = "assets/music/Pacman.mp3";
      String musicFile = new File(music).toURI().toString();
      Media sound = new Media(musicFile);
      this.musicPlayer = new MediaPlayer(sound);
      this.musicPlayer.play();
      this.musicPlayer.setVolume(getMusicVolumeValue());
      // System.out.println(musicPlayer.getVolume());

      this.musicPlayer.setOnEndOfMedia(() -> {
         this.musicPlayer.seek(Duration.ZERO);
         this.musicPlayer.play();
      });

      this.soundPlayer = new MediaPlayer(sound);
      this.soundPlayer.setVolume(getSoundVolumeValue());

      SceneController scn = loader.getController();
      scn.setSoundsGhost(selectRandomGhost());

      if (musicMute.equals("Mute")) {
         musicPlayer.setVolume(0);
         try {
            scn.pacmanMusic.setImage(new Image(new FileInputStream("assets/music/musicPacman/pacmanMusicOff.png")));
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }
      }

      if (soundsMute.equals("Mute")) {
         soundPlayer.setVolume(0);
         try {
            scn.ghostSounds.setImage(new Image(new FileInputStream("assets/sounds/soundGhost/soundsOff.png")));
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }
      }

      String video = "assets/video/pacmanDash.mp4";
      String videoFile = new File(video).toURI().toString();
      Media media = new Media(videoFile);
      this.videoPlayer = new MediaPlayer(media);

      this.videoPlayer.play();

      scn.mView.setMediaPlayer(videoPlayer);

      this.videoPlayer.setOnEndOfMedia(() -> {
         this.videoPlayer.seek(Duration.ZERO);
         this.videoPlayer.play();
      });

      scn.mView.setFitHeight(800);
      scn.mView.setFitWidth(1050);

      SceneController.setMediaPlayer(musicPlayer);
      SceneController.setSoundPlayer(soundPlayer);
      SceneController.setPacmanDashGame(this);

      // Font.loadFont(this.getClass().getResourceAsStream("../assets/font/unispace%20bold.ttf"),
      // 20);

      // System.out.println(css);

      scene = new Scene(root);
      scene.getStylesheets().add(css);

      root.getStylesheets().add(css);

      stage.setScene(scene);
      stage.show();

      try {
         stage.getIcons().addAll(new Image(new FileInputStream("assets/logo.png")));
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

      // this.root = new VBox();

      // create an array of Racers (Panes) and start
      // initializeScene();

      // Taken from
      // https://stackoverflow.com/questions/16606162/javafx-fullscreen-resizing-elements-based-upon-screen-size
      // letterbox(scene, loader.getRoot());
      stage.setFullScreenExitHint("");
      stage.setResizable(false);
   }

   /**
    * Get the color of the pixel at the given x and y coordinates of the given
    * image.
    * 
    * @param x        The x coordinate of the pixel you want to get the color of.
    * @param y        The y coordinate of the pixel to get the color of.
    * @param mapImage The image that you want to get the color from.
    * @return A Color object.
    */
   public Color getColor(int x, int y, Image mapImage) {
      PixelReader px = mapImage.getPixelReader();
      return px.getColor(x, y);
   }

   /**
    * It connects to the server, waits for the other player to connect, and then
    * starts the game
    */
   public void initializeMultiplayer() {

      // gameOn = false;
      winner = false;
      vsName = "";
      vsColor = "";
      vsHeadgear = "";

      connectToServer();

      // waitForPlayer();

      // ScheduledExecutorService service =
      // Executors.newSingleThreadScheduledExecutor();
      // service.scheduleAtFixedRate(() -> {
      // System.out.println("Heartbeat");

      // if (gameOn) {
      // System.out.println("GameOn is true!");
      // service.shutdown();
      // startMultiplayer();
      // return;
      // }

      // Heartbeat h = new Heartbeat();
      // h.start();

      // }, 1, 2, TimeUnit.SECONDS);

      startMultiplayer();

   }

   /**
    * It's a thread that reads messages from the server and displays them in the
    * chat area
    */
   public class PacmanDashChatThread extends Thread {

      private SceneController scn;

      // Creating a new thread for the chat.
      public PacmanDashChatThread(SceneController scn) {
         this.scn = scn;
      }

      /**
       * The function runs in a loop, reading objects from the input stream, and if
       * the object is a
       * string, it appends it to the chat text area
       */
      @Override
      public void run() {

         try {

            while (true) {

               Object obj = ois.readObject();

               if (obj instanceof String) {

                  String message = (String) obj;

                  // System.out.println(message);

                  Platform.runLater(() -> {
                     scn.taChat.appendText(message + "\n");
                     scn.tfMessage.clear();
                  });

               }

            }

         } catch (SocketException se) {

         } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
         }

      }

   }

   // /**
   // * PacmanDashGameplay
   // */
   // public class PacmanDashGameplay extends Thread {
   // @Override
   // public void run() {
   // try {
   // while (true) {
   // Object obj = ois.readObject();
   // if (obj instanceof PacmanDashPlayer2) {
   // PacmanDashPlayer2 player2 = (PacmanDashPlayer2) obj;
   // vsName = player2.getName();
   // vsColor = player2.getColor();
   // vsHeadgear = player2.getHeadgear();
   // }
   // }
   // } catch (SocketException se) {
   // } catch (ClassNotFoundException e) {
   // e.printStackTrace();
   // } catch (IOException e) {
   // e.printStackTrace();
   // }
   // }
   // }

   /**
    * It checks if the string is null, empty, or blank.
    * 
    * @param validate The string to validate
    * @return A boolean value.
    */
   private boolean validate(String validate) {
      return validate != null && !validate.equals("null") && !validate.isBlank() && !validate.isEmpty();
   }

   /**
    * Starts the multiplayer by creating the map and objects on the map
    */
   private void startMultiplayer() {

      // if (!gameOn) {
      // System.out.println("GameOn is false!");
      // return;
      // }

      // PacmanDashGameplay game = new PacmanDashGameplay();
      // game.start();

      clearVectorLists();

      Pane gameRoot = new StackPane();

      try {

         imageMap = new Image(new FileInputStream(multiplayerMap));
         imageMapView = new ImageView(imageMap);
         imageMapView.setFitWidth(500);
         imageMapView.setPreserveRatio(true);

         imageMap2 = new Image(new FileInputStream(multiplayerMap));

         imageMapView2 = new ImageView(imageMap2);
         imageMapView2.setFitWidth(500);
         imageMapView2.setPreserveRatio(true);

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

      if (!validate(vsName) && !validate(vsColor) && !validate(vsHeadgear)) {
         vsName = "Enemy";
         vsColor = "Yellow";
      }

      PacmanRacer racer1 = new PacmanRacer(this, PacmanDash.getPlayer1Name(), PacmanDash.getColorPlayer1(),
            KeyCode.D,
            KeyCode.A, KeyCode.W,
            KeyCode.S, map1,
            imageMap, 2400, 240);

      PacmanRacer racer2 = new PacmanRacer(this, vsName, vsColor, map2, imageMap2, 2400, 240);

      // racer1.setHeadGear(PacmanDash.getHeadGearPlayer1());

      map1.getChildren().addAll(imageMapView, racer1);

      map2.getChildren().addAll(imageMapView2, racer2);

      for (int i = 0; i < GHOST_COUNT; i++) {
         Ghost g = new Ghost();
         map1.getChildren().addAll(g);
         ghostVectorList1.add(g);
      }

      // for (int i = 0; i < GHOST_COUNT; i++) {
      // Ghost g = new Ghost(this, imageMap2);
      // map2.getChildren().addAll(g);
      // ghostVectorList2.add(g);
      // }

      for (int i = 0; i < SPEED_BOOST_COUNT; i++) {
         SpeedBoost sb = new SpeedBoost();
         map1.getChildren().addAll(sb);
         speedBoostList1.add(sb);
         // SpeedBoostPos sbPos = new SpeedBoostPos(sb.bootsView.getX(),
         // sb.bootsView.getY());
         // try {
         // oos.writeObject(sbPos);
         // oos.flush();
         // } catch (IOException e) {
         // e.printStackTrace();
         // }
      }

      // for (int i = 0; i < SPEED_BOOST_COUNT; i++) {
      // SpeedBoostPos sbPos = null;
      // try {
      // sbPos = (SpeedBoostPos) ois.readObject();
      // } catch (ClassNotFoundException | IOException e) {
      // e.printStackTrace();
      // }
      // SpeedBoost sb = new SpeedBoost();
      // sb.bootsView.setX(sbPos.getBoostPosX());
      // sb.bootsView.setY(sbPos.getBoostPosY());
      // map2.getChildren().addAll(sb);
      // speedBoostList2.add(sb);
      // }

      gameRoot.getChildren().addAll(map1, map2);

      map1.setTranslateY(-1750);

      map2.setTranslateY(-1750);
      map2.setTranslateX(500);

      stage.setTitle("PacMan Dash - Multiplayer VS");

      scene = new Scene(gameRoot, 1000, 750);
      stage.setScene(scene);
      stage.show();

      timer = new AnimationTimer() {
         public void handle(long now) {

            if (!winner) {

               racer1.update();

               PacmanDashPacStatus pDashPacStatus = new PacmanDashPacStatus(PacmanDash.getPlayer1Name(),
                     racer1.aPicView.getX(),
                     racer1.aPicView.getY(), racer1.UpPressed, racer1.DownPressed, racer1.LeftPressed,
                     racer1.RightPressed, map1.getTranslateY(), winner);
               try {
                  oos.writeObject(pDashPacStatus);
                  oos.flush();
               } catch (IOException e) {
                  e.printStackTrace();
               }

               try {
                  PacmanDashPacStatus pacStatus = (PacmanDashPacStatus) ois.readObject();

                  if (pacStatus.isWinner()) {
                     System.out.println("Winner!" + PacmanDash.getPlayer1Name());
                     winnerName = pacStatus.getName();
                     System.out.println(pacStatus.getName());
                     winner = true;
                     return;
                  }

                  racer2.aPicView.setX(pacStatus.getPosX());
                  racer2.aPicView.setY(pacStatus.getPosY());

                  map2.setTranslateY(pacStatus.getMapY());

                  if (pacStatus.isRight()) {

                     racer2.aPicView.setScaleX(1);
                     racer2.aPicView.setRotate(0);

                  } else if (pacStatus.isLeft()) {

                     racer2.aPicView.setRotate(0);
                     racer2.aPicView.setScaleX(-1);

                  } else if (pacStatus.isUp()) {

                     racer2.aPicView.setScaleX(1);
                     racer2.aPicView.setRotate(-90);

                  } else if (pacStatus.isDown()) {

                     racer2.aPicView.setRotate(90);
                     racer2.aPicView.setScaleX(1);

                  }

               } catch (ClassNotFoundException | IOException e) {
                  e.printStackTrace();
               }

               checkCollision(racer1);

               for (Ghost g : ghostVectorList1) {
                  g.update();
               }

               for (Ghost g : ghostVectorList2) {
                  g.update();
               }

               for (SpeedBoost sb : speedBoostList1) {
                  sb.update();
               }

               for (SpeedBoost sb : speedBoostList2) {
                  sb.update();
               }

            } else if (winner) {

               timer.stop();

               FXMLLoader loader = new FXMLLoader(
                     this.getClass().getClassLoader().getResource("winnerMultiPlayer.fxml"));

               try {
                  root = loader.load();
               } catch (IOException e) {
                  e.printStackTrace();
               }

               SceneController scn = loader.getController();
               PacmanDashChatThread pdCThread = new PacmanDashChatThread(scn);
               pdCThread.start();
               scn.lblWinner.setText("Winner: " + winnerName + "!");
               scn.setupNetworking(oos, ois);

               try {
                  scn.showCustomPlayers(scn);
               } catch (FileNotFoundException e) {
                  e.printStackTrace();
               }

               scene = new Scene(root);
               scene.getStylesheets().add(css);

               root.getStylesheets().add(css);

               stage.setTitle("PacMan Dash");
               stage.setScene(scene);
               stage.show();

               System.out.println(winnerName);

            }

         }
      };

      // TimerTask to delay start of race for 2 seconds
      TimerTask task = new TimerTask() {
         public void run() {
            timer.start();
         }
      };

      Timer startTimer = new Timer();
      long delay = 1000L;
      startTimer.schedule(task, delay);

      // ! Countdown timer
      // Pane timerPane = new Pane();
      // final int countdown = 3;
      // Timeline tl = new Timeline();
      // int down = countdown;
      // Label lblTimer = new Label(down + "");
      // lblTimer.setTextFill(Color.RED);
      // lblTimer.setStyle("-fx-font-size: 15em;");
      // timerPane.getChildren().addAll(lblTimer);
      // gameRoot.getChildren().add(timerPane);

      scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

         @Override
         public void handle(KeyEvent event) {

            if (event.getCode() == racer1.KEYCODE_RIGHT) {

               racer1.aPicView.setScaleX(1);
               racer1.aPicView.setRotate(0);

               racer1.UpPressed = false;
               racer1.DownPressed = false;
               racer1.LeftPressed = false;
               racer1.RightPressed = true;

            } else if (event.getCode() == racer1.KEYCODE_LEFT) {

               racer1.aPicView.setRotate(0);
               racer1.aPicView.setScaleX(-1);

               racer1.UpPressed = false;
               racer1.DownPressed = false;
               racer1.LeftPressed = true;
               racer1.RightPressed = false;

            } else if (event.getCode() == racer1.KEYCODE_UP) {

               racer1.aPicView.setScaleX(1);
               racer1.aPicView.setRotate(-90);

               racer1.UpPressed = true;
               racer1.DownPressed = false;
               racer1.LeftPressed = false;
               racer1.RightPressed = false;

            } else if (event.getCode() == racer1.KEYCODE_DOWN) {

               racer1.aPicView.setRotate(90);
               racer1.aPicView.setScaleX(1);

               racer1.UpPressed = false;
               racer1.DownPressed = true;
               racer1.LeftPressed = false;
               racer1.RightPressed = false;

            }

         }

      });

      // letterbox(scene, gameRoot);

      // Stopping the music player and saving the settings when the window is closed.
      stage.setOnCloseRequest(evt -> {
         if (musicPlayer != null) {
            musicPlayer.stop();
         }
         saveSettings();
         Platform.exit();
         System.exit(0);
      });

   }

   /**
    * It clears the lists of ghost vectors and speed boosts
    */
   private void clearVectorLists() {
      ghostVectorList1.clear();
      ghostVectorList2.clear();
      speedBoostList1.clear();
      speedBoostList2.clear();
   }

   /**
    * The Heartbeat class is a thread that sends a message to the server to get the
    * number of clients
    * connected to the server. If the number of clients is even, the game is on. If
    * the number of
    * clients is odd, the game is off
    */
   public class Heartbeat extends Thread {

      @Override
      public void run() {

         try {

            oos.writeObject("SEND_CLIENT_COUNT");
            oos.flush();

            int count = Integer.parseInt((String) ois.readObject());

            if (count % 2 == 0) {

               // gameOn = true;

            } else if (count % 2 == 1) {
               // gameOn = false;
            }

         } catch (IOException | NumberFormatException | ClassNotFoundException e) {
            e.printStackTrace();
         }

      }

   }

   /**
    * Lobby / Loading screen
    */
   // private void waitForPlayer() {
   // FXMLLoader loader = new
   // FXMLLoader(getClass().getClassLoader().getResource("waitingForPlayerMultiplayer.fxml"));
   // try {
   // this.root = loader.load();
   // } catch (IOException e) {
   // e.printStackTrace();
   // }
   // scene = new Scene(root);
   // stage.setScene(this.scene);
   // stage.show();
   // }

   /**
    * Terminates the connection from the server by
    * closing the input and output streams, and then closes the socket
    */
   public void terminateClient() {

      try {
         if (ois != null) {
            this.ois.close();
         }
         if (oos != null) {
            this.oos.close();
         }
         if (socket != null) {
            this.socket.close();
         }
      } catch (SocketException se) {

      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   /**
    * It creates a socket, connects to the server, creates an ObjectOutputStream
    * and ObjectInputStream,
    * sends the server a PacmanDashRegisterPacman object, and receives the
    * multiplayer map from the
    * server
    */
   private void connectToServer() {

      try {

         this.socket = new Socket("localhost", SERVER_PORT);

         System.out.println("Connected");

         this.oos = new ObjectOutputStream(socket.getOutputStream());
         this.ois = new ObjectInputStream(socket.getInputStream());

         PacmanDashRegisterPacman pdRP = new PacmanDashRegisterPacman(PacmanDash.getPlayer1Name(),
               PacmanDash.getColorPlayer1(), PacmanDash.getHeadGearPlayer1());

         oos.writeObject(pdRP);
         oos.flush();

         this.multiplayerMap = (String) ois.readObject();

      } catch (ClassNotFoundException | IOException e) {
         e.printStackTrace();
      }

   }

   /**
    * This function starts a singleplayer game with two players
    */
   public void startSingleplayer() {

      clearVectorLists();

      winner = false;

      String map = pathToMap + maps[new Random().nextInt(maps.length)];

      Pane gameRoot = new StackPane();

      try {

         imageMap = new Image(new FileInputStream(map));
         imageMapView = new ImageView(imageMap);
         imageMapView.setFitWidth(500);
         imageMapView.setPreserveRatio(true);

         imageMap2 = new Image(new FileInputStream(map));

         imageMapView2 = new ImageView(imageMap2);
         imageMapView2.setFitWidth(500);
         imageMapView2.setPreserveRatio(true);

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

      PacmanRacer racer1 = new PacmanRacer(this, PacmanDash.getPlayer1Name(), PacmanDash.getColorPlayer1(), KeyCode.D,
            KeyCode.A, KeyCode.W,
            KeyCode.S, map1,
            imageMap, 2400, 240);
      PacmanRacer racer2 = new PacmanRacer(this, PacmanDash.getPlayer2Name(), PacmanDash.getColorPlayer2(),
            KeyCode.RIGHT, KeyCode.LEFT, KeyCode.UP,
            KeyCode.DOWN, map2,
            imageMap2, 2400, 240);

      // racer1.setPrefWidth(iconWidth);
      // racer1.setPrefHeight(iconHeight);

      // racer2.setPrefWidth(iconWidth);
      // racer2.setPrefHeight(iconHeight);

      // racer1.setHeadGear(PacmanDash.getHeadGearPlayer1());

      map1.getChildren().addAll(imageMapView, racer1);

      map2.getChildren().addAll(imageMapView2, racer2);

      for (int i = 0; i < GHOST_COUNT; i++) {
         Ghost g = new Ghost();
         map1.getChildren().addAll(g);
         ghostVectorList1.add(g);
      }

      for (int i = 0; i < GHOST_COUNT; i++) {
         Ghost g = new Ghost();
         map2.getChildren().addAll(g);
         ghostVectorList2.add(g);
      }

      for (int i = 0; i < SPEED_BOOST_COUNT; i++) {
         SpeedBoost sb = new SpeedBoost();
         map1.getChildren().addAll(sb);
         speedBoostList1.add(sb);
      }

      for (int i = 0; i < SPEED_BOOST_COUNT; i++) {
         SpeedBoost sb = new SpeedBoost();
         map2.getChildren().addAll(sb);
         speedBoostList2.add(sb);
      }

      gameRoot.getChildren().addAll(map1, map2);

      map1.setTranslateY(-1750);

      map2.setTranslateY(-1750);
      map2.setTranslateX(500);

      stage.setTitle("PacMan Dash - 2P Singleplayer VS");

      scene = new Scene(gameRoot, 1000, 750);
      stage.setScene(scene);
      stage.show();

      System.out.println("Starting race...");

      timer = new AnimationTimer() {
         public void handle(long now) {

            if (!winner) {

               racer1.update();
               racer2.update();

               checkCollision(racer1, racer2);

               for (Ghost g : ghostVectorList1) {
                  g.update();
               }

               for (Ghost g : ghostVectorList2) {
                  g.update();
               }

               for (SpeedBoost sb : speedBoostList1) {
                  sb.update();
               }

               for (SpeedBoost sb : speedBoostList2) {
                  sb.update();
               }

            } else if (winner) {

               timer.stop();

               FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("winnerSinglePlayer.fxml"));

               try {
                  root = loader.load();
               } catch (IOException e) {
                  e.printStackTrace();
               }

               SceneController scn = loader.getController();
               scn.lblWinner.setText("Winner: " + winnerName + "!");

               try {
                  scn.showCustomPlayers(scn);
               } catch (FileNotFoundException e) {
                  e.printStackTrace();
               }

               scene = new Scene(root);
               scene.getStylesheets().add(css);

               root.getStylesheets().add(css);

               stage.setTitle("PacMan Dash");
               stage.setScene(scene);
               stage.show();

               System.out.println(winnerName);

            }

         }
      };

      // TimerTask to delay start of race for 2 seconds
      TimerTask task = new TimerTask() {
         public void run() {
            timer.start();
         }
      };

      Timer startTimer = new Timer();
      long delay = 1000L;
      startTimer.schedule(task, delay);

      // ! Countdown timer
      // Pane timerPane = new Pane();
      // final int countdown = 3;
      // Timeline tl = new Timeline();
      // int down = countdown;
      // Label lblTimer = new Label(down + "");
      // lblTimer.setTextFill(Color.RED);
      // lblTimer.setStyle("-fx-font-size: 15em;");
      // timerPane.getChildren().addAll(lblTimer);
      // gameRoot.getChildren().add(timerPane);

      scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

         @Override
         public void handle(KeyEvent event) {

            if (event.getCode() == racer1.KEYCODE_RIGHT) {

               racer1.aPicView.setScaleX(1);
               racer1.aPicView.setRotate(0);

               racer1.UpPressed = false;
               racer1.DownPressed = false;
               racer1.LeftPressed = false;
               racer1.RightPressed = true;

            } else if (event.getCode() == racer1.KEYCODE_LEFT) {

               racer1.aPicView.setRotate(0);
               racer1.aPicView.setScaleX(-1);

               racer1.UpPressed = false;
               racer1.DownPressed = false;
               racer1.LeftPressed = true;
               racer1.RightPressed = false;

            } else if (event.getCode() == racer1.KEYCODE_UP) {

               racer1.aPicView.setScaleX(1);
               racer1.aPicView.setRotate(-90);

               racer1.UpPressed = true;
               racer1.DownPressed = false;
               racer1.LeftPressed = false;
               racer1.RightPressed = false;

            } else if (event.getCode() == racer1.KEYCODE_DOWN) {

               racer1.aPicView.setRotate(90);
               racer1.aPicView.setScaleX(1);

               racer1.UpPressed = false;
               racer1.DownPressed = true;
               racer1.LeftPressed = false;
               racer1.RightPressed = false;

            }

            if (event.getCode() == racer2.KEYCODE_RIGHT) {

               racer2.aPicView.setScaleX(1);
               racer2.aPicView.setRotate(0);

               racer2.UpPressed = false;
               racer2.DownPressed = false;
               racer2.LeftPressed = false;
               racer2.RightPressed = true;

            } else if (event.getCode() == racer2.KEYCODE_LEFT) {

               racer2.aPicView.setRotate(0);
               racer2.aPicView.setScaleX(-1);

               racer2.UpPressed = false;
               racer2.DownPressed = false;
               racer2.LeftPressed = true;
               racer2.RightPressed = false;

            } else if (event.getCode() == racer2.KEYCODE_UP) {

               racer2.aPicView.setScaleX(1);
               racer2.aPicView.setRotate(-90);

               racer2.UpPressed = true;
               racer2.DownPressed = false;
               racer2.LeftPressed = false;
               racer2.RightPressed = false;

            } else if (event.getCode() == racer2.KEYCODE_DOWN) {

               racer2.aPicView.setRotate(90);
               racer2.aPicView.setScaleX(1);

               racer2.UpPressed = false;
               racer2.DownPressed = true;
               racer2.LeftPressed = false;
               racer2.RightPressed = false;

            }

         }

      });

      // letterbox(scene, gameRoot);

      stage.setOnCloseRequest(evt -> {
         if (musicPlayer != null) {
            musicPlayer.stop();
         }
         saveSettings();
         Platform.exit();
         System.exit(0);
      });

   }

   /**
    * This function checks for collisions between the PacmanRacer and the Ghost
    * objects and the speed boost objects
    * 
    * @param racer1 The first PacmanRacer object
    * @param racer2 The second PacmanRacer object
    */
   private void checkCollision(PacmanRacer racer1, PacmanRacer racer2) {

      for (Ghost ghost : ghostVectorList1) {

         // System.out.println("PacmanY: " + racer1.aPicView.getY() + ", PacmanX: " +
         // racer1.aPicView.getX() + ", GhostY: "
         // + ghost.ghostView.getY() + ", GhostX: " + ghost.ghostView.getX());

         if (ghost.ghostView.getBoundsInLocal().intersects(racer1.aPicView.getBoundsInLocal())) {
            // System.out.println("Collision!");
            racer1.collision = true;
            return;
         }

         racer1.collision = false;

      }

      for (Ghost ghost : ghostVectorList2) {

         // System.out.println("PacmanY: " + racer2.aPicView.getY() + ", PacmanX: " +
         // racer2.aPicView.getX() + ", GhostY: "
         // + ghost.ghostView.getY() + ", GhostX: " + ghost.ghostView.getX());

         if (ghost.ghostView.getBoundsInLocal().intersects(racer2.aPicView.getBoundsInLocal())) {
            // System.out.println("Collision!");
            racer2.collision = true;
            return;
         }

         racer2.collision = false;

      }

      for (SpeedBoost sb : speedBoostList1) {

         if (sb.bootsView.getBoundsInLocal().intersects(racer1.aPicView.getBoundsInLocal())) {
            // System.out.println("Collision with boots!");
            racer1.bootCollision = true;
            Platform.runLater(() -> {
               sb.getChildren().remove(sb.bootsView);
               speedBoostList1.remove(sb);
            });
            return;
         }
      }

      for (SpeedBoost sb : speedBoostList2) {
         if (sb.bootsView.getBoundsInLocal().intersects(racer2.aPicView.getBoundsInLocal())) {
            racer2.bootCollision = true;
            Platform.runLater(() -> {
               sb.getChildren().remove(sb.bootsView);
               speedBoostList2.remove(sb);
            });
            return;
         }
      }

   }

   /**
    * This function checks for collisions between the PacmanRacer and the Ghost
    * objects
    * 
    * @param racer1 The PacmanRacer object that is being checked for collisions.
    */
   private void checkCollision(PacmanRacer racer1) {

      for (Ghost ghost : ghostVectorList1) {

         // System.out.println("PacmanY: " + racer1.aPicView.getY() + ", PacmanX: " +
         // racer1.aPicView.getX() + ", GhostY: "
         // + ghost.ghostView.getY() + ", GhostX: " + ghost.ghostView.getX());

         if (ghost.ghostView.getBoundsInLocal().intersects(racer1.aPicView.getBoundsInLocal())) {
            // System.out.println("Collision!");
            racer1.collision = true;
            return;
         }

         racer1.collision = false;

      }

      for (SpeedBoost sb : speedBoostList1) {

         if (sb.bootsView.getBoundsInLocal().intersects(racer1.aPicView.getBoundsInLocal())) {
            // System.out.println("Collision with boots!");
            racer1.bootCollision = true;
            Platform.runLater(() -> {
               sb.getChildren().remove(sb.bootsView);
               speedBoostList1.remove(sb);
            });
            return;
         }
      }

   }

   /**
    * It creates a new XML file and writes the game settings to it
    */
   public static void saveSettings() {

      try {

         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

         Document doc = dBuilder.newDocument();

         Element rootElement = doc.createElement("PacmanDash-Configuration");
         doc.appendChild(rootElement);

         Element players = doc.createElement("Players");
         rootElement.appendChild(players);

         // Player 1 and their settings
         Element player1 = doc.createElement("Player1");
         players.appendChild(player1);

         Element player1name = doc.createElement("Name");
         player1name.appendChild(doc.createTextNode(getPlayer1Name()));
         player1.appendChild(player1name);

         Element player1headgear = doc.createElement("Headgear");
         player1headgear.appendChild(doc.createTextNode(getHeadGearPlayer1()));
         player1.appendChild(player1headgear);

         Element player1color = doc.createElement("Color");
         player1color.appendChild(doc.createTextNode(getColorPlayer1()));
         player1.appendChild(player1color);

         // Player 2 and their settings
         Element player2 = doc.createElement("Player2");
         players.appendChild(player2);

         Element player2name = doc.createElement("Name");
         player2name.appendChild(doc.createTextNode(getPlayer2Name()));
         player2.appendChild(player2name);

         Element player2headgear = doc.createElement("Headgear");
         player2headgear.appendChild(doc.createTextNode(getHeadGearPlayer2()));
         player2.appendChild(player2headgear);

         Element player2color = doc.createElement("Color");
         player2color.appendChild(doc.createTextNode(getColorPlayer2()));
         player2.appendChild(player2color);

         Element musicSounds = doc.createElement("MusicSounds");
         rootElement.appendChild(musicSounds);

         Element musicVolume = doc.createElement("MusicVolume");
         musicVolume.appendChild(doc.createTextNode(getMusicVolumeValue() + ""));
         musicSounds.appendChild(musicVolume);

         Element soundsVolume = doc.createElement("SoundsVolume");
         soundsVolume.appendChild(doc.createTextNode(getSoundVolumeValue() + ""));
         musicSounds.appendChild(soundsVolume);

         Element musicMute = doc.createElement("MusicMute");
         musicMute.appendChild(doc.createTextNode(PacmanDash.musicMute));
         musicSounds.appendChild(musicMute);

         Element soundsMute = doc.createElement("SoundsMute");
         soundsMute.appendChild(doc.createTextNode(PacmanDash.soundsMute));
         musicSounds.appendChild(soundsMute);

         FileOutputStream dos = new FileOutputStream(new File(SETTINGS_FILE));

         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();

         DOMSource source = new DOMSource(doc);

         StreamResult result = new StreamResult(dos);

         transformer.transform(source, result);

      } catch (ParserConfigurationException | FileNotFoundException | TransformerException e) {
         e.printStackTrace();
      }
   }

   /**
    * It reads the settings file and sets the values of the variables in the class
    */
   private void readSettings() {

      if (!(new File(SETTINGS_FILE).exists())) {
         this.setDefaultValues();
         return;
      }

      DocumentBuilder documentBuilder = null;

      try {

         documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         Document doc = documentBuilder.parse(new File(SETTINGS_FILE));

         doc.getDocumentElement().normalize();

         NodeList list = doc.getElementsByTagName("PacmanDash-Configuration");

         for (int i = 0; i < list.getLength(); i++) {

            org.w3c.dom.Node node = list.item(i);

            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

               Element element = (Element) node;

               setPlayer1Name(this.getConfiguration(element, "Name", 0));
               setColorPlayer1(this.getConfiguration(element, "Color", 0));
               setHeadGearPlayer1(this.getConfiguration(element, "Headgear", 0));
               setPlayer2Name(this.getConfiguration(element, "Name", 1));
               setColorPlayer2(this.getConfiguration(element, "Color", 1));
               setHeadGearPlayer2(this.getConfiguration(element, "Headgear", 1));
               setMusicVolumeValue(Double.parseDouble(this.getConfiguration(element, "MusicVolume", 0)));
               setSoundVolumeValue(Double.parseDouble(this.getConfiguration(element, "SoundsVolume", 0)));
               musicMute = this.getConfiguration(element, "MusicMute", 0);
               soundsMute = this.getConfiguration(element, "SoundsMute", 0);

            }

         }

      } catch (ParserConfigurationException | SAXException | IOException | NullPointerException e) {
         System.out.println("Error reading file, wrong values. Running default values.");
         this.setDefaultValues();
      }

   }

   /**
    * > This function takes in an element, a configuration, and an index, and
    * returns the text content
    * of the element's configuration at the given index
    * 
    * @param element       The element that contains the configuration.
    * @param configuration The name of the configuration you want to get.
    * @param index         The index of the configuration you want to get.
    * @return The text content of the element.
    */
   private String getConfiguration(Element element, String configuration, int index) {
      return element.getElementsByTagName(configuration).item(index).getTextContent();
   }

   /**
    * This function sets the default values for the game
    */
   private void setDefaultValues() {
      setColorPlayer1("Yellow");
      setHeadGearPlayer1("");
      setColorPlayer2("Yellow");
      setHeadGearPlayer2("");
      setMusicVolumeValue(0.7);
      setSoundVolumeValue(0.7);
      setMusicMute(false);
      setSoundsMute(false);
   }

   /**
    * It returns a random ghost image for the soundGhost
    * 
    * @return The path of the image of the ghost.
    */
   private String selectRandomGhost() {

      String[] soundGhosts = { "Blue", "Orange", "Pink", "Red" };
      String path = "assets/sounds/soundGhost/soundsOn";

      return path + soundGhosts[new Random().nextInt(soundGhosts.length)] + ".png";
   }

   /**
    * This function sets the value of the musicVolume variable to the value of the
    * mv parameter
    * 
    * @param mv The value of the music volume.
    */
   public static void setMusicVolumeValue(double mv) {
      musicVolume = mv;
   }

   /**
    * This function returns the value of the musicVolume variable
    * 
    * @return The value of the musicVolume variable.
    */
   public static double getMusicVolumeValue() {
      return musicVolume;
   }

   /**
    * This function sets the value of the soundsVolume variable to the value of the
    * sv parameter
    * 
    * @param sv The value of the sound volume.
    */
   public static void setSoundVolumeValue(double sv) {
      soundsVolume = sv;
   }

   /**
    * This function returns the value of the soundsVolume variable
    * 
    * @return The value of the soundsVolume variable.
    */
   public static double getSoundVolumeValue() {
      return soundsVolume;
   }

   /**
    * This function sets the value of the variable player1Name to the value of the
    * parameter name1
    * 
    * @param name1 The name of the first player.
    */
   public static void setPlayer1Name(String name1) {
      player1Name = name1;
   }

   /**
    * If the player1Name variable is null, return an empty string. Otherwise,
    * return the value of the
    * player1Name variable
    * 
    * @return The value of the player1Name variable.
    */
   public static String getPlayer1Name() {
      if (player1Name == null) {
         return "";
      }
      return player1Name;
   }

   /**
    * This function sets the value of the variable player2Name to the value of the
    * parameter name2
    * 
    * @param name2 The name of the second player.
    */
   public static void setPlayer2Name(String name2) {
      player2Name = name2;
   }

   /**
    * If the player2Name variable is null, return an empty string. Otherwise,
    * return the value of the
    * player2Name variable
    * 
    * @return The player2Name variable is being returned.
    */
   public static String getPlayer2Name() {
      if (player2Name == null) {
         return "";
      }
      return player2Name;
   }

   /**
    * This function sets the color of player 1 to the color that is passed in as a
    * parameter
    * 
    * @param p1Color The color of the player 1's pieces.
    */
   public static void setColorPlayer1(String p1Color) {
      player1Color = p1Color;
   }

   /**
    * This function sets the value of the variable player1HeadGear to the value of
    * the parameter
    * p1HeadGear
    * 
    * @param p1HeadGear The name of the headgear that player 1 is wearing.
    */
   public static void setHeadGearPlayer1(String p1HeadGear) {
      player1HeadGear = p1HeadGear;
   }

   /**
    * This function sets the color of player 2 to the color that is passed in as a
    * parameter
    * 
    * @param p2Color The color of the player 2's pieces.
    */
   public static void setColorPlayer2(String p2Color) {
      player2Color = p2Color;
   }

   /**
    * This function sets the value of the variable player2HeadGear to the value of
    * the parameter
    * p2HeadGear
    * 
    * @param p2HeadGear The name of the headgear that player 2 is wearing.
    */
   public static void setHeadGearPlayer2(String p2HeadGear) {
      player2HeadGear = p2HeadGear;
   }

   /**
    * This function returns the color of player 1
    * 
    * @return The color of player 1.
    */
   public static String getColorPlayer1() {
      return player1Color;
   }

   /**
    * This function returns the value of the variable player1HeadGear
    * 
    * @return The player1HeadGear variable is being returned.
    */
   public static String getHeadGearPlayer1() {
      return player1HeadGear;
   }

   /**
    * This function returns the color of player 2
    * 
    * @return The color of player 2.
    */
   public static String getColorPlayer2() {
      return player2Color;
   }

   /**
    * It returns the value of the variable player2HeadGear
    * 
    * @return The headgear of player 2
    */
   public static String getHeadGearPlayer2() {
      return player2HeadGear;
   }

   /**
    * This function sets the musicMute variable to either "Mute" or "UnMute"
    * depending on the boolean
    * value passed to it
    * 
    * @param b boolean value, true or false
    */
   public static void setMusicMute(boolean b) {
      if (b) {
         musicMute = "Mute";
      } else if (!(b)) {
         musicMute = "UnMute";
      }
   }

   /**
    * This function sets the soundsMute variable to either "Mute" or "UnMute"
    * depending on the boolean
    * value passed to it
    * 
    * @param b boolean value, true or false
    */
   public static void setSoundsMute(boolean b) {
      if (b) {
         soundsMute = "Mute";
      } else if (!(b)) {
         soundsMute = "UnMute";
      }
   }

   // Straight out
   // yoinked from https://
   // stackoverflow.com/questions/16606162/javafx-fullscreen-resizing-elements-based-upon-screen-size
   // private void letterbox(final Scene scene, final Pane contentPane) {
   // final double initWidth = scene.getWidth();
   // final double initHeight = scene.getHeight();
   // final double ratio = initWidth / initHeight;
   // SceneSizeChangeListener sizeListener = new SceneSizeChangeListener(scene,
   // ratio, initHeight, initWidth,
   // contentPane);
   // scene.widthProperty().addListener(sizeListener);
   // scene.heightProperty().addListener(sizeListener);
   // }

   // private static class SceneSizeChangeListener implements
   // ChangeListener<Number> {
   // private final Scene scene;
   // private final double ratio;
   // private final double initHeight;
   // private final double initWidth;
   // private final Pane contentPane;

   // public SceneSizeChangeListener(Scene scene, double ratio, double initHeight,
   // double initWidth, Pane contentPane) {
   // this.scene = scene;
   // this.ratio = ratio;
   // this.initHeight = initHeight;
   // this.initWidth = initWidth;
   // this.contentPane = contentPane;
   // }

   // @Override
   // public void changed(ObservableValue<? extends Number> observableValue, Number
   // oldValue, Number newValue) {
   // final double newWidth = scene.getWidth();
   // final double newHeight = scene.getHeight();
   // double scaleFactor = newWidth / newHeight > ratio ? newHeight / initHeight :
   // newWidth / initWidth;
   // if (scaleFactor >= 1) {
   // Scale scale = new Scale(scaleFactor, scaleFactor);

   // scale.setPivotX(0);
   // scale.setPivotY(0);
   // scene.getRoot().getTransforms().setAll(scale);
   // contentPane.setPrefWidth(newWidth / scaleFactor);
   // contentPane.setPrefHeight(newHeight / scaleFactor);
   // } else {
   // contentPane.setPrefWidth(Math.max(initWidth, newWidth));
   // contentPane.setPrefHeight(Math.max(initHeight, newHeight));
   // }
   // }
   // }

} // end class PacmanDash