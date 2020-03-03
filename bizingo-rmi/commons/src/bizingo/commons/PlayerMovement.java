package bizingo.commons;

import java.io.Serializable;

public class PlayerMovement implements Serializable {
    private String coordSource;
    private String coordDest;

    public PlayerMovement(String coordSource, String coordDest){
        this.coordSource = coordSource;
        this.coordDest = coordDest;
    }

    public String getCoordSource() {
        return coordSource;
    }

    public void setCoordSource(String coordSource) {
        this.coordSource = coordSource;
    }

    public String getCoordDest() {
        return coordDest;
    }

    public void setCoordDest(String coordDest) {
        this.coordDest = coordDest;
    }
}
