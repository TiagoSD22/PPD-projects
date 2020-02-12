package bizingo.commons;

public class Handshake extends MessageContent {

    private String nickname;
    private String avatar;

    public Handshake(String nickname, String avatar, String source, String destination) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.setSource(source);
        this.setDestination(destination);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
