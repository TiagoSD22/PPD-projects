package com.bizingoclient.app.mainGame;


import com.bizingoclient.Main;
import com.bizingoclient.app.mainGame.game.GameController;
import com.bizingoclient.app.mainGame.chatToolbar.ChatToolbarController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class MainGameController {

    @FXML private AnchorPane mainPane;
    @FXML private GameController gameController;
    @FXML private ChatToolbarController chatToolbarController;


    public GameController getGameController() {
        return gameController;
    }

    public ChatToolbarController getChatToolbarController(){
        return chatToolbarController;
    }

    private Stage mainStage;

    @FXML public void initialize(){

        Main.addOnChangeScreenListener(new Main.OnChangeSceen() {
            @Override
            public void onScreenChanged(String newScreen, Object data, Stage stage) {
                mainStage = stage;
                System.out.println("nova tela :"+newScreen+", "+ data);
                initControllers();
            }
        });
    }

    public void initControllers(){
        chatToolbarController.init(this);
        gameController.init(this);
    }

}



