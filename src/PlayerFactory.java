import javafx.scene.shape.Shape3D;

/**
 * Created by ASUS on 03.05.2017.
 */
public class PlayerFactory {

    public PlayerFactory(){}

    public IPlayable getPlayer(Shape3D tvSet, String typeOfPlayer, String mediaURI){
        typeOfPlayer = typeOfPlayer.toLowerCase();
        switch (typeOfPlayer){
            case "webcam":
                return new WebcamTexturePlayer(tvSet);
            case "media":
                return new MediaTexturePlayer(tvSet, mediaURI);
        }

        throw new IllegalArgumentException("Unable to create the player of a non-specific type.");
    }
}
