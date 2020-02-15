package bizingo.commons;

public enum MessageType {
    HANDSHAKE("HANDSHAKE"),
    TEXT("TEXT"),
    MOVEMENT("MOVEMENT"),
    CONFIG("CONFIG"),
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
