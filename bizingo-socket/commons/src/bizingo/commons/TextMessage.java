package bizingo.commons;

public class TextMessage extends MessageContent {
    String text;

    public TextMessage(String text, String source, String destination){
        this.text = text;
        this.setSource(source);
        this.setDestination(destination);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
