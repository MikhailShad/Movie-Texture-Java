import javafx.scene.shape.Shape3D;

/**
 * Created by ASUS on 03.05.2017.
 */
public interface IPlayable {
    int SCENE_WIDTH = 640;
    int SCENE_HEIGHT = 400;
    double MOVIE_WIDTH = 1366 / 4.0;
    double MOVIE_HEIGHT = 768 / 4.0;

    enum Status {CREATED, PLAYING, PAUSED, NONEXISTING};
    Status getStatus();

    void create();
    void play();
    void pause();
    void stop();
}
