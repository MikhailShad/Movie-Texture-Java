import com.github.sarxos.webcam.Webcam;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by ASUS on 03.05.2017.
 */
public class MovieTextureApplication extends Application {
    private static final Color DEFAULT_BACKGROUND = Color.rgb(35, 39, 50);

    private Scene scene;
    private Box screenTV;
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);

    private PlayerFactory playerFactory;
    private IPlayable playerTV;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Virtual TV-set by Mikhail Shadrin");

        playerTV = new MediaTexturePlayer();
        playerFactory = new PlayerFactory();

        //Initializing TV panel
        screenTV = new Box(IPlayable.MOVIE_WIDTH, IPlayable.MOVIE_HEIGHT, 0);
        screenTV.setTranslateX(IPlayable.SCENE_WIDTH / 2);
        screenTV.setTranslateY(IPlayable.SCENE_HEIGHT / 2);
        screenTV.getTransforms().addAll(rotateZ, rotateY, rotateX);

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(IPlayable.SCENE_WIDTH / 2);
        pointLight.setTranslateY(IPlayable.SCENE_HEIGHT / 2);
        pointLight.setTranslateZ(-IPlayable.SCENE_WIDTH);

        AmbientLight ambientLight = new AmbientLight(Color.rgb(15, 15, 15));

        //Setting the menu and menu bar
        MenuBar menuBar = new MenuBar();
        Menu tvSetMenu = new Menu("TV-Set");

        MenuItem openFile = new MenuItem("Open video from file");
        openFile.setOnAction(event -> {
            FileChooser openFileChooser = new FileChooser();
            File movieFile = openFileChooser.showOpenDialog(stage);

            if (movieFile == null || !movieFile.isFile()){
                showErrorMessage(new FileNotFoundException("The file was not chosen or it was not a movie file"));
                return;
            }

            if (playerTV.getStatus() != IPlayable.Status.NONEXISTING)
                playerTV.stop();

            try {
                playerTV = playerFactory.getPlayer(screenTV, "media", movieFile.toURI().toString());
                playerTV.create();
                playerTV.play();
            } catch (Exception ex) {
                showErrorMessage(ex);
            }
        });

        MenuItem webcamTranslation = new MenuItem("Translate from webcam");
        webcamTranslation.setOnAction(event -> {
            if (playerTV.getStatus() != IPlayable.Status.NONEXISTING)
                playerTV.stop();

            playerTV = playerFactory.getPlayer(screenTV, "webcam", null);
            playerTV.create();
            playerTV.play();

        });
        tvSetMenu.getItems().addAll(openFile, webcamTranslation);

        menuBar.prefWidthProperty().bind(stage.widthProperty());
        menuBar.getMenus().add(tvSetMenu);

        Group group = new Group(screenTV, pointLight, ambientLight, menuBar);

        scene = new Scene(group, IPlayable.SCENE_WIDTH, IPlayable.SCENE_HEIGHT, true, SceneAntialiasing.BALANCED);
        scene.setFill(DEFAULT_BACKGROUND);

        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setFieldOfView(60);
        scene.setCamera(camera);

        //Organising interaction between mouse and camera
        handleMouseEvents();

        handleKeyboardEvents();

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    //Movie control with spacebar
    private void handleKeyboardEvents() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() != KeyCode.SPACE)
                    return;

                if (playerTV.getStatus() == IPlayable.Status.PLAYING) {
                    playerTV.pause();
                    return;
                }

                if (playerTV.getStatus() == IPlayable.Status.PAUSED) {
                    playerTV.play();
                }
            }
        });
    }

    //Camera rotation with mouse
    private double mouseX, mouseY;

    private void handleMouseEvents() {

        double affinianAxisCoefficicent = 360 * Math.PI / 180;

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
            }
        });

        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double dX = mouseX - event.getX();
                double dY = mouseY - event.getY();

                if (event.isPrimaryButtonDown()) {
                    rotateX.setAngle(rotateX.getAngle() - affinianAxisCoefficicent * dY / IPlayable.MOVIE_HEIGHT);
                    rotateY.setAngle(rotateY.getAngle() + affinianAxisCoefficicent * dX / IPlayable.MOVIE_WIDTH);
                }

                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
            }
        });
    }

    private void showErrorMessage(Exception ex) {
        Alert alertMessage = new Alert(Alert.AlertType.ERROR);
        String messageType = ex.getClass().toString();
        alertMessage.setTitle(messageType.substring(messageType.lastIndexOf('.')));
        alertMessage.setHeaderText("An error occured!");
        alertMessage.setContentText(ex.getMessage());
        alertMessage.showAndWait();
        alertMessage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
