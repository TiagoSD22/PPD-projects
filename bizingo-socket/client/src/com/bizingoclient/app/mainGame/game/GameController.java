package com.bizingoclient.app.mainGame.game;


import com.bizingoclient.app.mainGame.MainGameController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;


public class GameController {

    @FXML
    public AnchorPane root;


    private MainGameController main;

    public void init(MainGameController mainGameController) {
        main = mainGameController;
    }

}