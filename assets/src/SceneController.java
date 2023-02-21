import javafx.animation.*;
import javafx.application.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.media.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.*;
import java.io.*;
import java.util.*;

/**
 * A Controller class used for FXML files and any kind of methods associated
 * with it
 */
public class SceneController {

   // Scenes
   private static final String FXML_MAIN = "mainScreen.fxml";
   private static final String FXML_HOW_TO_PLAY = "howToPlay.fxml";
   private static final String FXML_SINGLE_MULTI_PLAYER = "singleMultiPlayer.fxml";
   private static final String FXML_ENTER_NAMES_SINGLEPLAYER = "enterNamesSingleplayer.fxml";
   private static final String FXML_ENTER_NAME_MULTIPLAYER = "enterNameMultiplayer.fxml";
   // private static final String FXML_PLAY_SINGLEPLAYER = "playSingleplayer.fxml";
   // private static final String FXML_PLAY_MULTIPLAYER = "playMultiplayer.fxml";
   private static final String FXML_CUSTOMIZE = "customize.fxml";
   private static final String FXML_CUSTOMIZE_PLAYER_2 = "customize2.fxml";
   private static final String FXML_SETTINGS = "settings.fxml";
   private static final String FXML_SET_NAMES = "setNames.fxml";
   private static final String FXML_ABOUT = "about.fxml";

   // Images related to music
   private static final String PACMAN_MUSIC_ON = "assets/music/musicPacman/pacmanMusicOn.png";
   private static final String PACMAN_MUSIC_OFF = "assets/music/musicPacman/pacmanMusicOff.png";

   private static String GHOST_SOUNDS_ON = "";
   private static String GHOST_SOUNDS_OFF = "assets/sounds/soundGhost/soundsOff.png";

   private Stage stage;
   private Scene scene;
   private Parent root;

   private static MediaPlayer musicPlayer;
   private static MediaPlayer soundPlayer;

   private static PacmanDash pacmanDash;

   // Player Attributes
   // ! These are gonna have to be passed to the main class
   // private String name;
   private String playerColor1 = "Yellow";
   private String playerHeadGear1 = "";
   private String playerColor2 = "Yellow";
   private String playerHeadGear2 = "";

   private String Pacman1 = "assets/custom/pacman/pacman" + this.playerColor1 + this.playerHeadGear1 + ".png";
   private String Pacman2 = "assets/custom/pacman/pacman" + this.playerColor2 + this.playerHeadGear2 + ".png";

   @FXML
   private ImageView pacmanCustom1;

   @FXML
   private ImageView pacmanCustom2;

   @FXML
   private Label lblSettings;

   @FXML
   private Slider sliderMusic;

   @FXML
   private Slider sliderSound;

   @FXML
   public ImageView pacmanMusic;

   @FXML
   public ImageView ghostSounds;

   @FXML
   private Label lblPlayer1Controls;

   @FXML
   private Label lblPlayer2Controls;

   @FXML
   private Label lblSetNames;

   @FXML
   public Label lblWinner;

   public void initialize() {

   }

   /**
    * Method is in charge of switching scenes
    * depending on the text inside of the button or an css id of an image
    * 
    * @param event
    */
   public void switchScenes(Event event) {

      Object obj = event.getSource();

      if (obj instanceof Button) {

         Button btn = (Button) obj;

         ActionEvent aEvent = (ActionEvent) event;

         try {

            switch (btn.getText()) {

               case "How To Play": {
                  // System.out.println("How to Play Works!");

                  FXMLLoader loader = newLoader(FXML_HOW_TO_PLAY);
                  this.root = loader.load();
                  SceneController scn = loader.getController();

                  String name1 = PacmanDash.getPlayer1Name();
                  String name2 = PacmanDash.getPlayer2Name();

                  if (this.validateName(name1)) {
                     scn.lblPlayer1Controls.setText(name1 + ": WASD");
                  }

                  if (this.validateName(name2)) {
                     scn.lblPlayer2Controls.setText(name2 + ": Arrow Keys");
                  }

                  showScreen(aEvent);

                  break;
               }

               case "Play": {
                  // System.out.println("Play Works!");

                  FXMLLoader loader = newLoader(FXML_SINGLE_MULTI_PLAYER);
                  this.root = loader.load();
                  showScreen(aEvent);

                  break;
               }

               case "Singleplayer (Local)": {
                  // System.out.println("Singleplayer Works!");

                  String name1 = PacmanDash.getPlayer1Name();
                  String name2 = PacmanDash.getPlayer2Name();

                  if (this.validateName(name1) && this.validateName(name2)) {

                     System.out.println("Names are set for singleplayer");

                     pacmanDash.startSingleplayer();

                     return;
                  }

                  FXMLLoader loader = newLoader(FXML_ENTER_NAMES_SINGLEPLAYER);
                  this.root = loader.load();

                  SceneController scn = loader.getController();

                  if (this.validateName(name1)) {
                     scn.tfPlayerName1.setText(name1);
                  }

                  if (this.validateName(name2)) {
                     scn.tfPlayerName2.setText(name2);
                  }

                  showCustomPlayers(scn);

                  showScreen(aEvent);

                  break;
               }

               case "Multiplayer (Online)": {
                  // System.out.println("Multiplayer Works!");

                  String name1 = PacmanDash.getPlayer1Name();

                  if (this.validateName(name1)) {

                     System.out.println("Name is set for multiplayer");

                     // ! Main Multiplayer Game starts here

                     // ! Code a lobby
                     pacmanDash.initializeMultiplayer();

                     return;
                  }

                  FXMLLoader loader = newLoader(FXML_ENTER_NAME_MULTIPLAYER);
                  this.root = loader.load();
                  showScreen(aEvent);

                  break;
               }

               case "Let's Dash!": {

                  setPlayerNames();

                  String name1 = PacmanDash.getPlayer1Name();
                  String name2 = PacmanDash.getPlayer2Name();

                  if (this.validateName(name1) && this.validateName(name2)) {

                     pacmanDash.startSingleplayer();

                  }

                  break;
               }

               case "Back to Menu": {

                  FXMLLoader loader = newLoader(FXML_MAIN);
                  this.root = loader.load();

                  pacmanDash.terminateClient();

                  SceneController scn = loader.getController();
                  scn.setPacMan(musicPlayer.getVolume());
                  scn.setGhost(soundPlayer.getVolume());
                  scn.mView.setMediaPlayer(pacmanDash.videoPlayer);
                  scn.mView.setFitHeight(800);
                  scn.mView.setFitWidth(1050);
                  // pacmanDash.videoPlayer.play();

                  showScreen(aEvent);

                  break;
               }

               case "Customization": {
                  // System.out.println("Customize Works!");

                  FXMLLoader loader = newLoader(FXML_CUSTOMIZE);
                  this.root = loader.load();
                  SceneController scn = loader.getController();
                  scn.loadCustomPlayer1(scn);

                  showScreen(event);

                  break;
               }

               case "Customize Player 2": {
                  // System.out.println("Customize 2 Works!");

                  FXMLLoader loader = newLoader(FXML_CUSTOMIZE_PLAYER_2);
                  this.root = loader.load();
                  SceneController scn = loader.getController();
                  scn.loadCustomPlayer2(scn);

                  showScreen(event);

                  break;
               }

               case "Set Player Names": {
                  // System.out.println("Names Works!");

                  FXMLLoader loader = newLoader(FXML_SET_NAMES);
                  this.root = loader.load();

                  SceneController scn = loader.getController();

                  showCustomPlayers(scn);

                  String name1 = PacmanDash.getPlayer1Name();
                  String name2 = PacmanDash.getPlayer2Name();

                  if (this.validateName(name1)) {
                     scn.tfPlayerName1.setText(PacmanDash.getPlayer1Name());
                  }

                  if (this.validateName(name2)) {
                     scn.tfPlayerName2.setText(PacmanDash.getPlayer2Name());
                  }

                  showScreen(aEvent);

                  break;
               }

               case "About": {
                  // System.out.println("About Works!");

                  FXMLLoader loader = newLoader(FXML_ABOUT);
                  this.root = loader.load();
                  showScreen(aEvent);

                  break;
               }

               case "Exit": {

                  musicPlayer.stop();

                  pacmanDash.videoPlayer.pause();

                  PacmanDash.saveSettings();

                  Thread.sleep(200);

                  String music = "assets/sounds/pacman_death.wav";
                  String musicFile = new File(music).toURI().toString();
                  Media sound = new Media(musicFile);
                  soundPlayer = new MediaPlayer(sound);
                  soundPlayer.setVolume(PacmanDash.getSoundVolumeValue());
                  soundPlayer.play();

                  soundPlayer.setOnEndOfMedia(() -> {
                     Platform.exit();
                     System.exit(0);
                  });

                  break;
               }

            }

         } catch (IOException e) {
            e.printStackTrace();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }

      }

      else if (obj instanceof ImageView) {

         ImageView img = (ImageView) obj;

         MouseEvent mEvent = (MouseEvent) event;

         try {

            switch (img.getId()) {

               case "settings": {

                  FXMLLoader loader = newLoader(FXML_SETTINGS);
                  this.root = loader.load();

                  SceneController scn = loader.getController();

                  scn.sliderMusic.setValue((musicPlayer.getVolume() * 100));
                  scn.setPacMan(musicPlayer.getVolume());

                  scn.sliderSound.setValue((soundPlayer.getVolume() * 100));
                  scn.setGhost(soundPlayer.getVolume());

                  showScreen(mEvent);

                  break;
               }

               case "backToMenu": {

                  FXMLLoader loader = newLoader(FXML_MAIN);
                  this.root = loader.load();

                  SceneController scn = loader.getController();
                  scn.setPacMan(musicPlayer.getVolume());
                  scn.setGhost(soundPlayer.getVolume());
                  scn.mView.setMediaPlayer(pacmanDash.videoPlayer);
                  scn.mView.setFitHeight(800);
                  scn.mView.setFitWidth(1050);
                  // pacmanDash.videoPlayer.play();

                  showScreen(mEvent);

                  break;
               }

               case "backToPlay": {

                  FXMLLoader loader = newLoader(FXML_SINGLE_MULTI_PLAYER);
                  this.root = loader.load();
                  showScreen(mEvent);

                  break;
               }

               case "backToPlayFromServer": {

                  pacmanDash.terminateClient();

                  FXMLLoader loader = newLoader(FXML_SINGLE_MULTI_PLAYER);
                  this.root = loader.load();
                  showScreen(mEvent);

                  break;
               }

               case "customize": {

                  FXMLLoader loader = newLoader(FXML_CUSTOMIZE);
                  this.root = loader.load();

                  SceneController scn = loader.getController();
                  scn.loadCustomPlayer1(scn);

                  showScreen(mEvent);

                  break;
               }

            }

         } catch (IOException e) {
            e.printStackTrace();
         }

      }

   } // End of switchScenes

   /**
    * Displays custom set Pacman's on the scene
    * 
    * @param scn
    * @throws FileNotFoundException
    */
   public void showCustomPlayers(SceneController scn) throws FileNotFoundException {

      this.playerColor1 = PacmanDash.getColorPlayer1();
      this.playerHeadGear1 = PacmanDash.getHeadGearPlayer1();
      this.playerColor2 = PacmanDash.getColorPlayer2();
      this.playerHeadGear2 = PacmanDash.getHeadGearPlayer2();

      this.Pacman1 = "assets/custom/pacman/pacman" + this.playerColor1 + this.playerHeadGear1 + ".png";
      this.Pacman2 = "assets/custom/pacman/pacman" + this.playerColor2 + this.playerHeadGear2 + ".png";

      scn.pacmanCustom1.setImage(new Image(new FileInputStream(Pacman1)));
      scn.pacmanCustom2.setImage(new Image(new FileInputStream(Pacman2)));
   }

   /**
    * Validates the name, checks if its not null, not "null", blank or empty
    * 
    * @param name
    * @return boolean
    */
   private boolean validateName(String name) {
      return name != null && !name.equals("null") && !name.isBlank() && !name.isEmpty();
   }

   /**
    * Takes in the event as a parameter to get the window from the stage, in which
    * it shows the screen to the user
    * 
    * @param event
    */
   public void showScreen(Event event) {

      Node node = (Node) event.getSource();
      this.stage = (Stage) node.getScene().getWindow();
      this.scene = new Scene(root);
      stage.setScene(this.scene);
      stage.show();

   }

   /**
    * Shorter way of writing the whole loader
    * 
    * @param scene
    * @return FXMLLoader
    */
   private FXMLLoader newLoader(String scene) {
      return new FXMLLoader(this.getClass().getClassLoader().getResource(scene));
   }

   /**
    * When hovering over the imageView
    * 
    * @param event
    */
   public void imageViewHover(MouseEvent event) {
      ImageView iView = (ImageView) event.getSource();
      iView.setTranslateY(-2);
   }

   /**
    * When un-hovering over the imageView
    * 
    * @param event
    */
   public void imageViewUnHover(MouseEvent event) {
      ImageView iView = (ImageView) event.getSource();
      iView.setTranslateY(0);
   }

   /**
    * Takes in the main game as the parameter and set's it to the variable set in
    * the Controller class
    * 
    * @param pacmanDashGame The Main Game
    */
   public static void setPacmanDashGame(PacmanDash pacmanDashGame) {
      pacmanDash = pacmanDashGame;
   }

   /**
    * In charge of Turning Music on and off by clicking on the pacman ImageView
    * 
    * @param event
    */
   public void turnMusicOnOff(Event event) {

      ImageView iView = (ImageView) event.getSource();

      try {

         if (musicPlayer.getVolume() > 0) {

            PacmanDash.setMusicVolumeValue(musicPlayer.getVolume());

            if (sliderMusic != null) {
               sliderMusic.setValue(0);
            }

            musicPlayer.setVolume(0);

            iView.setImage(new Image(new FileInputStream(PACMAN_MUSIC_OFF)));

            PacmanDash.setMusicMute(true);

         } else if (musicPlayer.getVolume() == 0.0) {

            musicPlayer.setVolume(PacmanDash.getMusicVolumeValue());

            if (sliderMusic != null) {
               sliderMusic.setValue(PacmanDash.getMusicVolumeValue() * 100);
            }

            iView.setImage(new Image(new FileInputStream(PACMAN_MUSIC_ON)));

            PacmanDash.setMusicMute(false);

         }

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

   }

   /**
    * In charge of Turning Music on and off by clicking on the ghost ImageView
    *
    * @param event
    */
   public void turnSoundsOnOff(Event event) {

      ImageView iView = (ImageView) event.getSource();

      try {

         if (soundPlayer.getVolume() > 0) {

            PacmanDash.setSoundVolumeValue(soundPlayer.getVolume());

            if (sliderSound != null) {
               sliderSound.setValue(0);
            }

            soundPlayer.setVolume(0);

            iView.setImage(new Image(new FileInputStream(GHOST_SOUNDS_OFF)));

            PacmanDash.setSoundsMute(true);

         } else if (soundPlayer.getVolume() == 0.0) {

            soundPlayer.setVolume(PacmanDash.getSoundVolumeValue());

            if (sliderSound != null) {
               sliderSound.setValue(PacmanDash.getSoundVolumeValue() * 100);
            }

            iView.setImage(new Image(new FileInputStream(GHOST_SOUNDS_ON)));

            PacmanDash.setSoundsMute(false);

         }

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

   }

   /**
    * In charge of setting the pacman image
    * depending on the volume of the mediaPlayer
    * 
    * @param volume volume of the MediaPlayer
    */
   public void setPacMan(double volume) {

      try {

         if (musicPlayer.getVolume() == 0) {

            pacmanMusic.setImage(new Image(new FileInputStream(PACMAN_MUSIC_OFF)));
         } else if (musicPlayer.getVolume() > 0.0) {

            pacmanMusic.setImage(new Image(new FileInputStream(PACMAN_MUSIC_ON)));
         }

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

   }

   /**
    * This function sets the value of the musicPlayer variable to the value of the
    * mp parameter
    * 
    * @param mp The media player.
    */
   public static void setMediaPlayer(MediaPlayer mp) {
      musicPlayer = mp;
   }

   /**
    * Method called to accurately display and set the music volume on screen
    */
   public void setMusicVolume() {

      double musicValue = sliderMusic.getValue();

      Platform.runLater(() -> {
         lblSettings.setText("Music Volume: " + String.format("%2.0f", musicValue) + "%");
      });

      double musicVolume = musicValue / 100;

      System.out.println("Music Volume: " + musicVolume);

      PacmanDash.setMusicVolumeValue(musicVolume);

      musicPlayer.setVolume(musicVolume);

      setPacMan(musicVolume);

   }

   /**
    * This function sets the value of the soundPlayer variable to the value of the
    * mp parameter
    * 
    * @param mp The media player.
    */
   public static void setSoundPlayer(MediaPlayer mp) {
      soundPlayer = mp;
   }

   /**
    * In charge of setting the pacman image
    * depending on the volume of the music player
    * 
    * @param volume volume of the MediaPlayer
    */
   public void setGhost(double volume) {

      try {

         if (soundPlayer.getVolume() == 0) {

            ghostSounds.setImage(new Image(new FileInputStream(GHOST_SOUNDS_OFF)));
         } else if (soundPlayer.getVolume() > 0.0) {

            ghostSounds.setImage(new Image(new FileInputStream(GHOST_SOUNDS_ON)));
         }

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

   }

   /**
    * This function sets the value of the GHOST_SOUNDS_ON variable to the value of
    * the ghost parameter
    *
    * @param ghost a String path to the ghost
    */
   public void setSoundsGhost(String ghost) {
      GHOST_SOUNDS_ON = ghost;
      try {
         ghostSounds.setImage(new Image(new FileInputStream(ghost)));
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }

   /**
    * Method called to accurately display and set the sound volume on screen
    */
   public void setSoundVolume() {

      double soundValue = sliderSound.getValue();

      Platform.runLater(() -> {
         lblSettings.setText("Sound Volume: " + String.format("%2.0f", soundValue) + "%");
      });

      double soundsVolume = soundValue / 100;

      System.out.println("Sounds Volume: " + soundsVolume);

      PacmanDash.setSoundVolumeValue(soundsVolume);

      soundPlayer.setVolume(soundsVolume);

      setGhost(soundsVolume);

   }

   /**
    * Function called when the mouse is away from any of the slider
    */
   public void mouseOffSlider() {

      Platform.runLater(() -> {
         PauseTransition pause = new PauseTransition(Duration.seconds(2));
         pause.setOnFinished(ae -> lblSettings.setText("Settings"));
         pause.play();
      });

   }

   /**
    * Selecting the color the user wishes to have for their pacman
    * 
    * @param event from where the event happened
    */
   public void selectColor(Event event) {

      Rectangle rect = (Rectangle) event.getSource();

      switch (rect.getId()) {
         case "yellow": {
            // System.out.println("Yellow!");
            this.setColor("Yellow");
            break;
         }
         case "red": {
            // System.out.println("Red!");
            this.setColor("Red");
            break;
         }
         case "pink": {
            // System.out.println("Pink!");
            this.setColor("Pink");
            break;
         }
         case "lightblue": {
            // System.out.println("Light Blue!");
            this.setColor("LightBlue");
            break;
         }
         case "green": {
            // System.out.println("Green!");
            this.setColor("Green");
            break;
         }
         case "purple": {
            // System.out.println("Purple!");
            this.setColor("Purple");
            break;
         }

      }

   }

   /**
    * Selecting the color the user wishes to have for their pacman
    * 
    * @param color Name of the color - String
    */
   public void setColor(String color) {

      try {

         if (pacmanCustom1 != null) {
            this.playerColor1 = color;
            this.Pacman1 = "assets/custom/pacman/pacman" + this.playerColor1 + this.playerHeadGear1 + ".png";
            pacmanCustom1.setImage(new Image(new FileInputStream(Pacman1)));
            PacmanDash.setColorPlayer1(color);
         }

         if (pacmanCustom2 != null) {
            this.playerColor2 = color;
            this.Pacman2 = "assets/custom/pacman/pacman" + this.playerColor2 + this.playerHeadGear2 + ".png";
            pacmanCustom2.setImage(new Image(new FileInputStream(Pacman2)));
            PacmanDash.setColorPlayer2(color);
         }

      } catch (FileNotFoundException e) {
         // e.printStackTrace();
      }

   }

   /**
    * Selecting the Headgear the user wishes to have on their pacman
    * 
    * @param event from where the event happened
    */
   public void selectHeadgear(Event event) {

      ImageView iView = (ImageView) event.getSource();

      switch (iView.getId()) {

         case "nothing": {
            // System.out.println("Nothing!");
            this.setHeadGear("");
            break;
         }
         case "propeller hat": {
            // System.out.println("A Propeller Hat!");
            this.setHeadGear("PropHat");
            break;
         }
         case "top hat": {
            // System.out.println("A TopHat!");
            this.setHeadGear("TopHat");
            break;
         }
         case "sweat band": {
            // System.out.println("A Sweat Band!");
            this.setHeadGear("SweatBand");
            break;
         }

      }

   }

   /**
    * Setting the headgear the user wishes to have on their pacman
    * 
    * @param headgear Name of the Headgear - String
    */
   public void setHeadGear(String headgear) {

      try {

         if (pacmanCustom1 != null) {
            this.playerHeadGear1 = headgear;
            this.Pacman1 = "assets/custom/pacman/pacman" + this.playerColor1 + this.playerHeadGear1 + ".png";
            pacmanCustom1.setImage(new Image(new FileInputStream(Pacman1)));
            PacmanDash.setHeadGearPlayer1(headgear);
         }

         if (pacmanCustom2 != null) {
            this.playerHeadGear2 = headgear;
            this.Pacman2 = "assets/custom/pacman/pacman" + this.playerColor2 + this.playerHeadGear2 + ".png";
            pacmanCustom2.setImage(new Image(new FileInputStream(Pacman2)));
            PacmanDash.setHeadGearPlayer2(headgear);
         }

      } catch (FileNotFoundException e) {
         // e.printStackTrace();
      }

   }

   /**
    * Method for random choosing of a color and headgear for the pacman. In case if
    * the color or headgear matches with the color or headgear in the main class,
    * the method calls itself to randomly generate another random color and
    * headgear
    */
   public void randomPacMan() {

      String[] pacmanColor = { "Yellow", "Red", "Pink", "LightBlue", "Green", "Purple" };
      String[] pacmanHeadGear = { "", "PropHat", "TopHat", "SweatBand" };

      String ranColor = pacmanColor[new Random().nextInt(pacmanColor.length)];
      String ranHeadGear = pacmanHeadGear[new Random().nextInt(pacmanHeadGear.length)];

      if (pacmanCustom1 != null) {
         if (ranColor == PacmanDash.getColorPlayer1()) {
            randomPacMan();
         }
         if (ranHeadGear == PacmanDash.getHeadGearPlayer1()) {
            randomPacMan();
         }
         setHeadGear(ranHeadGear);
         setColor(ranColor);
      }

      if (pacmanCustom2 != null) {
         if (ranColor == PacmanDash.getColorPlayer2()) {
            randomPacMan();
         }
         if (ranHeadGear == PacmanDash.getHeadGearPlayer2()) {
            randomPacMan();
         }
         setHeadGear(ranHeadGear);
         setColor(ranColor);
      }

   }

   /**
    * Method for loading the custom player 1, which loads the color and the
    * Headgear of the player
    * 
    * @param scn SceneController
    */
   private void loadCustomPlayer1(SceneController scn) {
      if (PacmanDash.getColorPlayer1() != null) {
         scn.setColor(PacmanDash.getColorPlayer1());
      }
      if (PacmanDash.getHeadGearPlayer1() != null) {
         scn.setHeadGear(PacmanDash.getHeadGearPlayer1());
      }
   }

   /**
    * Method for loading the custom player 2, which loads the color and the
    * Headgear of the player
    * 
    * @param scn SceneController
    */
   private void loadCustomPlayer2(SceneController scn) {
      if (PacmanDash.getColorPlayer2() != null) {
         scn.setColor(PacmanDash.getColorPlayer2());
      }
      if (PacmanDash.getHeadGearPlayer2() != null) {
         scn.setHeadGear(PacmanDash.getHeadGearPlayer2());
      }
   }

   @FXML
   private TextField tfPlayerName1;

   @FXML
   private TextField tfPlayerName2;

   /**
    * Sets names of the player's, depending on which TextField has been set or not.
    * Furthermore it validates it automatically to see if its not blank or empty
    */
   @FXML
   private void setPlayerNames() {

      String name1 = tfPlayerName1.getText();
      String name2 = tfPlayerName2.getText();

      if (!(name1.isBlank() || name1.isEmpty())) {

         PacmanDash.setPlayer1Name(name1);

         if (!(name2.isBlank() || name2.isEmpty())) {

            PacmanDash.setPlayer2Name(name2);

            Platform.runLater(() -> {
               lblSetNames.setText("Player names have been set");
               PauseTransition pause = new PauseTransition(Duration.seconds(2));
               pause.setOnFinished(ae -> lblSetNames.setText("Set Player Names"));
               pause.play();
            });

            return;
         }

         Platform.runLater(() -> {
            lblSetNames.setText("Player 1 name have been set");
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ae -> lblSetNames.setText("Set Player Names"));
            pause.play();
         });
         return;
      }

      if (!(name2.isBlank() || name2.isEmpty())) {

         PacmanDash.setPlayer2Name(name2);

         if (!(name1.isBlank() || name1.isEmpty())) {

            PacmanDash.setPlayer1Name(name1);

            Platform.runLater(() -> {
               lblSetNames.setText("Player names have been set");
               PauseTransition pause = new PauseTransition(Duration.seconds(2));
               pause.setOnFinished(ae -> lblSetNames.setText("Set Player Names"));
               pause.play();
            });

         }

         Platform.runLater(() -> {
            lblSetNames.setText("Player 2 name have been set");
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ae -> lblSetNames.setText("Set Player Names"));
            pause.play();
         });
         return;
      }

      Platform.runLater(() -> {
         lblSetNames.setText("Please set a valid name/s");
         PauseTransition pause = new PauseTransition(Duration.seconds(2));
         pause.setOnFinished(ae -> lblSetNames.setText("Set Player Names"));
         pause.play();
      });

   }

   @FXML
   private TextField tfUserName;

   /**
    * Gets the name from the text field and sets it to the main class and starts
    * the multiplayer
    * game
    */
   @FXML
   public void setName() {

      String name = tfUserName.getText();

      if (this.validateName(name)) {

         PacmanDash.setPlayer1Name(name);

         pacmanDash.initializeMultiplayer();

      }

   }

   // Networking
   private ObjectOutputStream oos;
   // private ObjectInputStream ois;

   /**
    * Setting up the Network for the SceneController
    * 
    * @param oos - ObjectOutputStream
    * @param ois - ObjectInputStream
    */
   public void setupNetworking(ObjectOutputStream oos, ObjectInputStream ois) {

      this.oos = oos;
      // this.ois = ois;

   }

   @FXML
   public TextArea taChat;

   @FXML
   public TextField tfMessage;

   /**
    * Gets the message from the TextField and creates a new class which contains
    * the name and the chat message to be sent to the server.
    */
   @FXML
   public void sendMessage() {

      String message = tfMessage.getText();

      try {

         PacmanDashChat chat = new PacmanDashChat(PacmanDash.getPlayer1Name(), message);

         oos.writeObject(chat);
         oos.flush();

      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   @FXML
   public MediaView mView;

}
