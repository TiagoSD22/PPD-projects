package com.bizingoclient.app.game.body;


import com.bizingoclient.app.game.MainController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;


public class BodyController {

    @FXML
    public AnchorPane root;


    private MainController main;

    public void init(MainController mainController) {
        main = mainController;
    }

}