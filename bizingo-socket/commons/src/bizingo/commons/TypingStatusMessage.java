package bizingo.commons;

public class TypingStatusMessage extends MessageContent {
    private TypingStatus status;

    public TypingStatusMessage(TypingStatus status, String source, String destination){
        this.status = status;
        this.setSource(source);
        this.setDestination(destination);
    }

    public TypingStatus getStatus() {
        return status;
    }

    public void setStatus(TypingStatus status) {
        this.status = status;
    }
}
