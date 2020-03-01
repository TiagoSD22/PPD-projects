package com.bizingoclient.app.services;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URISyntaxException;

public class AudioService {

    private static AudioService instance;
    private Media newMessageSound;
    private Media pieceSelectedSound;
    private Media pieceDeselectedSound;
    private Media pieceMovedSound;
    private MediaPlayer newMessageMediaPlayer;
    private MediaPlayer pieceSelectedMediaPlayer;
    private MediaPlayer pieceDeselectedMediaPlayer;
    private MediaPlayer pieceMovedMediaPlayer;

    private AudioService() {
        loadMedias();
    }

    public static AudioService getInstance() {
        if (instance == null) {
            instance = new AudioService();
        }
        return instance;
    }

    private void loadMedias() {
        try {
            newMessageSound = new Media(getClass().getResource("/assets/Sounds/incoming_message.mp3")
                    .toURI().toString()
            );
            pieceSelectedSound = new Media(getClass().getResource("/assets/Sounds/piece_selected.mp3")
                    .toURI().toString()
            );
            pieceDeselectedSound = new Media(getClass().getResource("/assets/Sounds/piece_deselected.mp3")
                    .toURI().toString()
            );
            pieceMovedSound = new Media(getClass().getResource("/assets/Sounds/piece_placed.mp3")
                    .toURI().toString()
            );
        } catch (URISyntaxException e) {
            System.out.println("Falha ao carregar audio");
            e.printStackTrace();
        }
        newMessageMediaPlayer = new MediaPlayer(newMessageSound);
        pieceSelectedMediaPlayer = new MediaPlayer(pieceSelectedSound);
        pieceDeselectedMediaPlayer = new MediaPlayer(pieceDeselectedSound);
        pieceMovedMediaPlayer = new MediaPlayer(pieceMovedSound);
    }

    public void playIncomingMessageSound() {
        System.out.println("Tocando som de nova mensagem recebida");
        newMessageMediaPlayer.seek(Duration.seconds(0));
        newMessageMediaPlayer.play();
    }

    public void playPieceSelectedSound(){
        pieceSelectedMediaPlayer.seek(Duration.seconds(0));
        pieceSelectedMediaPlayer.play();
    }

    public void playPieceDeselectedSound(){
        pieceDeselectedMediaPlayer.seek(Duration.seconds(0));
        pieceDeselectedMediaPlayer.play();
    }

    public void playPieceMovedSound(){
        pieceMovedMediaPlayer.seek(Duration.seconds(0));
        pieceMovedMediaPlayer.play();
    }
}
