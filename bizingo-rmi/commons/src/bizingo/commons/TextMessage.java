package bizingo.commons;

public class TextMessage extends MessageContent {
    private String text;

    public TextMessage(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
