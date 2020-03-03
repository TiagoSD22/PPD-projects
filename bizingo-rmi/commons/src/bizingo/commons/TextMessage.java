package bizingo.commons;

import java.io.Serializable;

public class TextMessage implements Serializable {
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
