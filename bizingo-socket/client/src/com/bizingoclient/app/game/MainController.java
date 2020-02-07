package com.bizingoclient.app.game;


import com.bizingoclient.Main;
import com.bizingoclient.app.game.body.BodyController;
import com.bizingoclient.app.game.chatToolbar.ChatToolbarController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;


public class MainController {

    Logger logger = Logger.getLogger(MainController.class.getName());

    @FXML private AnchorPane principal;
    @FXML BodyController bodyController;
    @FXML
    ChatToolbarController chatToolbarController;
    @FXML public VBox vboxBody;

    @FXML public List<File> listOfFiles;

    public BodyController getBodyController() {
        return bodyController;
    }

    public ChatToolbarController getChatToolbarController(){
        return chatToolbarController;
    }

    private Stage principalStage;

    @FXML public void initialize(){

        Main.addOnChangeScreenListener(new Main.OnChangeSceen() {
            @Override
            public void onScreenChanged(String newScreen, Object data, Stage stage) {
                initControllers();
                principalStage = stage;
                System.out.println("nova tela :"+newScreen+", "+ data);
            }
        });
    }

    public void initControllers(){
        chatToolbarController.init(this);
        bodyController.init(this);
    }

}



