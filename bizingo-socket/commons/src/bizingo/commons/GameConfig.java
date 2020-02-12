package bizingo.commons;


public class GameConfig extends MessageContent{
    private CellColor playerPieceColor;
    private boolean firstTurn;

    public CellColor getPlayerPieceColor() {
        return playerPieceColor;
    }

    public void setPlayerPieceColor(CellColor playerPieceColor) {
        this.playerPieceColor = playerPieceColor;
    }

    public boolean isFirstTurn() {
        return firstTurn;
    }

    public void setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
    }

    public GameConfig(CellColor playerPieceColor, boolean firstTurn, String source, String destination){
        this.playerPieceColor = playerPieceColor;
        this.firstTurn = firstTurn;
        this.setSource(source);
        this.setDestination(destination);
    }

    public GameConfig(){}
}
