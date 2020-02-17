package com.bizingoclient;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ArrayList;


public class Main extends Application {

    private static Stage stage;
    private static Scene menuScene;
    private static Scene mainGameScene;
    private static ArrayList<OnChangeSceen> listeners = new ArrayList<>();

    public static void changeScreen(String scr, Object data) {
        switch (scr) {
            case "menu":
                stage.setScene(menuScene);
                stage.setResizable(false);
                notifyAllListeners("menu", data, stage);
                break;

            case "game":
                stage.setScene(mainGameScene);
                stage.setResizable(false);
                notifyAllListeners("game", data, stage);
                break;
        }
    }

    public static void changeScreen(String scr) {
        changeScreen(scr, null);
    }

    public static void addOnChangeScreenListener(OnChangeSceen newListener) {
        listeners.add(newListener);
    }

    private static void notifyAllListeners(String newScreen, Object data, Stage stage) {
        for (OnChangeSceen l : listeners)
            l.onScreenChanged(newScreen, data, stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/icone.png")));
        stage.setTitle("Bizingo");

        Parent fxmlMenu = FXMLLoader.load(getClass().getResource("app/menu/menuController.fxml"));
        menuScene = new Scene(fxmlMenu, 960, 720);
        Parent fxmlMainGame = FXMLLoader.load(getClass().getResource("app/mainGame/mainGameController.fxml"));

        mainGameScene = new Scene(fxmlMainGame, 1280, 720);

        stage.setTitle("Bizingo");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/icone.png")));
        stage.setScene(menuScene);
        stage.show();
    }

    @Override
    public void stop() {
        if (stage.getScene().equals(mainGameScene)) {
            notifyAllListeners("stop", null, null);
        }

        System.out.println("Parando aplicação");
        Platform.exit();
        System.exit(0);
    }

    public static interface OnChangeSceen {
        void onScreenChanged(String newScreen, Object data, Stage stage);
    }
}
