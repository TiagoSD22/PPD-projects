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
    private static  Scene menuScene;
    private static  Scene mainGameScene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/icone.png")));
        stage.setTitle("Bizingo");

        Parent fxmlmain = FXMLLoader.load(getClass().getResource("app/menu/menuController.fxml"));
        menuScene = new Scene(fxmlmain, 960, 720);
        Parent fxmlListImage = FXMLLoader.load(getClass().getResource("app/game/mainController.fxml"));

        mainGameScene = new Scene(fxmlListImage, 1366, 768);

        primaryStage.setTitle("Bizingo");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/icone.png")));
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    public static void changeScreen(String scr, Object data){
        switch (scr){
            case "menu":
                stage.setScene(menuScene);
                stage.setResizable(false);
                notifyAllListeners("menu",data, stage);
                break;

            case "game":
                notifyAllListeners("game",data, stage);
                stage.setScene(mainGameScene);
                stage.setResizable(false);
                break;
        }
    }

    @Override
    public void stop(){
        if(stage.getScene().equals(mainGameScene)){
            System.out.println("Parando aplicação");
            Platform.exit();
            System.exit(0);
        }
    }

    public static void changeScreen(String scr) {
        changeScreen(scr,null);
    }

    private static ArrayList<OnChangeSceen> listeners = new ArrayList<>();

    public static interface OnChangeSceen{
        void onScreenChanged(String newScreen, Object data, Stage stage);
    }

    public static void addOnChangeScreenListener(OnChangeSceen newListener){
        listeners.add(newListener);
    }

    private static void notifyAllListeners(String newScreen, Object data, Stage stage){
        for(OnChangeSceen l : listeners)
            l.onScreenChanged(newScreen,data, stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
