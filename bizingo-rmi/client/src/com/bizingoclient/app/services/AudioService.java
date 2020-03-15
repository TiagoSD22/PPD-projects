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
    private Media notificationSound;
    private Media winSound;
    private Media loseSound;
    private Media opponentPieceCapturedSound;
    private Media ownPieceCapturedSound;
    private MediaPlayer newMessageMediaPlayer;
    private MediaPlayer pieceSelectedMediaPlayer;
    private MediaPlayer pieceDeselectedMediaPlayer;
    private MediaPlayer pieceMovedMediaPlayer;
    private MediaPlayer notificationMediaPlayer;
    private MediaPlayer winSoundMediaPlayer;
    private MediaPlayer loseMediaPlayer;
    private MediaPlayer opponentPieceCapturedMediaPlayer;
    private MediaPlayer ownPieceCapturedMediaPlayer;

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
            newMessageSound = new Media(getClass().getResource("/assets/Sounds/incoming_message.mp3")
                    .toURI().toString()
            );
            pieceSelectedSound = new Media(getClass().getResource("/assets/Sounds/piece_selected.wav")
                    .toURI().toString()
            );
            pieceDeselectedSound = new Media(getClass().getResource("/assets/Sounds/piece_deselected.wav")
                    .toURI().toString()
            );
            pieceMovedSound = new Media(getClass().getResource("/assets/Sounds/piece_placed.mp3")
                    .toURI().toString()
            );
            notificationSound = new Media(getClass().getResource("/assets/Sounds/notification.mp3")
                    .toURI().toString()
            );
            winSound = new Media(getClass().getResource("/assets/Sounds/win.mp3")
                    .toURI().toString()
            );
            loseSound = new Media(getClass().getResource("/assets/Sounds/lose.mp3")
                    .toURI().toString()
            );
            ownPieceCapturedSound = new Media(getClass().getResource("/assets/Sounds/own_piece_captured.mp3")
                    .toURI().toString()
            );
            opponentPieceCapturedSound = new Media(getClass().getResource("/assets/Sounds/opponent_piece_captured.mp3")
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
        notificationMediaPlayer = new MediaPlayer(notificationSound);
        winSoundMediaPlayer = new MediaPlayer(winSound);
        loseMediaPlayer = new MediaPlayer(loseSound);
        ownPieceCapturedMediaPlayer = new MediaPlayer(ownPieceCapturedSound);
        opponentPieceCapturedMediaPlayer = new MediaPlayer(opponentPieceCapturedSound);
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

    public void playPieceSelectedSound(){
        playSound(pieceSelectedMediaPlayer);
    }

    public void playPieceDeselectedSound(){
        playSound(pieceDeselectedMediaPlayer);
    }

    public void playPieceMovedSound(){
        playSound(pieceMovedMediaPlayer);
    }

    public void playNotificationSound(){
        playSound(notificationMediaPlayer);
    }

    public void playWinSound(){
        playSound(winSoundMediaPlayer);
    }

    public void playLoseSound(){
        playSound(loseMediaPlayer);
    }

    public void playOpponentPieceCapturedSound(){
        playSound(opponentPieceCapturedMediaPlayer);
    }

    public void playOwnPieceCapturedSound(){
        playSound(ownPieceCapturedMediaPlayer);
    }

}
