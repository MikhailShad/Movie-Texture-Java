import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;

/**
 * Created by ASUS on 03.05.2017.
 */
public class MediaTexturePlayer implements IPlayable {
    private Shape3D tvSet;
    private MediaPlayer mediaPlayer;
    private String mediaURI;
    private AnimationTimer timer;

    private Status playerStatus = Status.NONEXISTING;

    @Override
    public Status getStatus() {
        return playerStatus;
    }

    public MediaTexturePlayer(){
        this(null, null);
    }

    public MediaTexturePlayer(Shape3D tvSet, String mediaURI){
        this.tvSet = tvSet;
        this.mediaURI = mediaURI;
        mediaPlayer = null;

        playerStatus = Status.NONEXISTING;
    }

    @Override
    public void create() {
        mediaPlayer = new MediaPlayer(new Media(mediaURI));
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(MOVIE_WIDTH);
        mediaView.setFitHeight(MOVIE_HEIGHT);
        mediaView.setPreserveRatio(false);

        PhongMaterial movieTextureMaterial = new PhongMaterial();
        tvSet.setMaterial(movieTextureMaterial);

        SnapshotParameters snapshotParameters = new SnapshotParameters();
        Rectangle2D rectTV = new Rectangle2D(0, 0, MOVIE_WIDTH, MOVIE_HEIGHT);
        snapshotParameters.setViewport(rectTV);
        WritableImage movieTextureImage = mediaView.snapshot(snapshotParameters, null);
        movieTextureMaterial.setDiffuseMap(movieTextureImage);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                mediaView.snapshot(snapshotParameters, movieTextureImage);
            }
        };

        mediaPlayer.setVolume(0.7);

        playerStatus = Status.CREATED;
    }

    @Override
    public void play() {
        if(playerStatus != Status.CREATED && playerStatus != Status.PAUSED)
            throw new IllegalStateException("Unable to play movie texture until the player is not created.");

        playerStatus = Status.PLAYING;
        timer.start();
        mediaPlayer.play();
    }

    @Override
    public void pause() {
        if (playerStatus != Status.PLAYING)
            throw new IllegalStateException("Unable to pause movie texture until the player is not created or played.");

        playerStatus = Status.PAUSED;
        timer.stop();
        mediaPlayer.pause();
    }

    @Override
    public void stop() {
        if (playerStatus == Status.NONEXISTING)
            throw new IllegalStateException("Unable to stop movie texture until the player is not created.");

        timer.stop();
        mediaPlayer.stop();
        mediaPlayer.dispose();

        playerStatus = Status.NONEXISTING;
    }
}
