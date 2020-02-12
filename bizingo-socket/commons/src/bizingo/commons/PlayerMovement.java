package bizingo.commons;

public class PlayerMovement extends MessageContent {
    private String coordSource;
    private String coordDest;

    public PlayerMovement(String coordSource, String coordDest, String source, String dest){
        this.coordSource = coordSource;
        this.coordDest = coordDest;
        setSource(source);
        setDestination(dest);
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
