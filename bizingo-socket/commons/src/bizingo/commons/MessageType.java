package bizingo.commons;

public enum MessageType {
    START("START"),
    HANDSHAKE("HANDSHAKE"),
    TEXT("TEXT"),
    TYPING_STATUS("TYPING_STATUS"),
    MOVEMENT("MOVEMENT"),
    CONFIG("CONFIG"),
    RESTART("RESTART"),
    CLOSE("CLOSE"),
    QUIT("QUIT");

    private String value;

    MessageType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }
}
