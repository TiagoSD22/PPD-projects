package bizingo.commons;

public class PlayerMovement extends MessageContent {
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
