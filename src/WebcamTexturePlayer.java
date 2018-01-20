import com.github.sarxos.webcam.Webcam;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by ASUS on 03.05.2017.
 */
public class WebcamTexturePlayer implements IPlayable {
    private Shape3D tvSet;
    private Webcam webcam;
    private AnimationTimer timer;

    private Status playerStatus = Status.NONEXISTING;

    @Override
    public Status getStatus() {
        return playerStatus;
    }

    public WebcamTexturePlayer(){
        this(null);
    }

    public WebcamTexturePlayer(Shape3D tvSet){
        this.tvSet = tvSet;
        playerStatus = Status.NONEXISTING;
    }

    private BufferedImage transformWebcamImage(BufferedImage webcamImage){
        int scaledHeight = (int)MOVIE_HEIGHT,
                scaledWidth = (int)(MOVIE_HEIGHT / webcamImage.getHeight() * webcamImage.getWidth());
        Image scaledInstance = webcamImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        webcamImage = new BufferedImage((int)MOVIE_WIDTH, (int)MOVIE_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

        int scaledX = ((int)MOVIE_WIDTH - scaledWidth)/2, scaledY = 0;
        webcamImage.getGraphics().drawImage(scaledInstance, scaledX, scaledY, java.awt.Color.black, null);

        return webcamImage;
    }

    @Override
    public void create() {
        webcam = Webcam.getDefault();
        if(webcam == null)
            throw new RuntimeException("Unable to connect to webcamera. Please, check its connection to your computer.");

        webcam.open();

        PhongMaterial movieTextureMaterial = new PhongMaterial();
        tvSet.setMaterial(movieTextureMaterial);

        WritableImage movieTextureImage = new WritableImage((int)MOVIE_WIDTH, (int)MOVIE_HEIGHT);
        movieTextureMaterial.setDiffuseMap(movieTextureImage);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                BufferedImage image = transformWebcamImage(webcam.getImage());
                SwingFXUtils.toFXImage(image, movieTextureImage);
            }
        };

        playerStatus = Status.CREATED;
    }

    @Override
    public void play() {
        if(playerStatus != Status.CREATED && playerStatus != Status.PAUSED)
            throw new IllegalStateException("Unable to play movie texture until the player is not created or set on pause.");

        playerStatus = Status.PLAYING;
        timer.start();
    }

    @Override
    public void pause() {
        if (playerStatus != Status.PLAYING)
            throw new IllegalStateException("Unable to pause movie texture until the player is not created or played.");

        playerStatus = Status.PAUSED;
        timer.stop();
    }

    @Override
    public void stop() {
        if (playerStatus == Status.NONEXISTING)
            throw new IllegalStateException("Unable to stop movie texture until the player is not created.");

        timer.stop();
        webcam.close();

        playerStatus = Status.NONEXISTING;
    }
}
