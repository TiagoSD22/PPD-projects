package com.spatia.client.app.services;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class AudioService {

    private static AudioService instance;
    private Media newMessageSound;
    private Media newClientSound;
    private MediaPlayer newMessageMediaPlayer;
    private MediaPlayer newClientMediaPlayer;

    public static boolean sound;

    private AudioService() {
        loadMedias();
    }

    public static AudioService getInstance() {
        if (instance == null) {
            instance = new AudioService();
            sound = true;
        }
        return instance;
    }

    private void loadMedias() {
        try {
            newMessageSound = new Media(getClass().getResource("/assets/Sounds/new_message.mp3")
                    .toURI().toString()
            );
            newClientSound = new Media(getClass().getResource("/assets/Sounds/new_client_connected.mp3")
                    .toURI().toString()
            );
        } catch (/*URISyntaxException*/ Exception e) {
            System.out.println("Falha ao carregar audio");
            e.printStackTrace();
        }
        newMessageMediaPlayer = new MediaPlayer(newMessageSound);
        newClientMediaPlayer = new MediaPlayer(newClientSound);
    }

    public void onMute(){
        sound = !sound;
    }

    private void playSound(MediaPlayer player){
        player.setVolume(sound? 1.0 : 0.0);
        player.seek(Duration.seconds(0));
        player.play();
    }

    public void playIncomingMessageSound() {
        System.out.println("Tocando som de nova mensagem recebida");
        playSound(newMessageMediaPlayer);
    }

    public void playNewClientConnectedSound(){
        playSound(newClientMediaPlayer);
    }
}
