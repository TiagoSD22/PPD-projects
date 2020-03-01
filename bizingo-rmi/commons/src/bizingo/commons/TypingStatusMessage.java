package bizingo.commons;

public class TypingStatusMessage extends MessageContent {
    private TypingStatus status;

    public TypingStatusMessage(TypingStatus status){
        this.status = status;
    }

    public TypingStatus getStatus() {
        return status;
    }

    public void setStatus(TypingStatus status) {
        this.status = status;
    }
}
