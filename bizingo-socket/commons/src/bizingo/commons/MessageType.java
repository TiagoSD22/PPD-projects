package bizingo.commons;

public enum MessageType {
    HANDSHAKE("HANDSHAKE"),
    TEXT("TEXT");

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
