package bizingo.commons;

import javafx.scene.image.Image;

import java.io.Serializable;

public class Message implements Serializable {

    private MessageType type;
    private String text;
    private String source;
    private String destination;
    private String nickname;
    private String avatar;

    public Message(String type, String text, String source, String destination){
        this.type = MessageType.valueOf(type);
        this.text = text;
        this.source = source;
        this.destination = destination;
    }

    public Message(String type, String text, String source, String destination, String nickname, String avatar){
        this.type = MessageType.valueOf(type);
        this.text = text;
        this.source = source;
        this.destination = destination;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public MessageType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setType(String type) {
        this.type = MessageType.valueOf(type);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
