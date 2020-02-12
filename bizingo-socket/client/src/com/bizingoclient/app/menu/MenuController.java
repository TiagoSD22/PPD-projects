package com.bizingoclient.app.menu;


import com.bizingoclient.Main;
import com.bizingoclient.app.utils.Avatars;
import com.bizingoclient.app.enums.ConnectionConfig;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class MenuController {

    @FXML
    private AnchorPane menuRoot;
    @FXML
    private ImageView avatarPreview;
    @FXML
    private StackPane avatarRegion;
    private Circle clip;
    @FXML
    private ComboBox avatarSelect;
    private Map<String, Image> avatarMap = new HashMap<String, Image>();
    @FXML
    private JFXButton connectButton;
    @FXML
    private CustomTextField nicknameField;
    private JFXDialog warningDialog;


    @FXML
    public void initialize() {
        Image imProfile = new Image(getClass().getResourceAsStream("/assets/no_avatar.png"));
        avatarPreview.setImage(imProfile);

        clip = new Circle(24);
        avatarPreview.setFitWidth(32);
        avatarPreview.setFitHeight(32);
        clip.setStroke(Color.WHITE);
        clip.setStrokeWidth(1);
        clip.setFill(Color.rgb(0, 0, 0, 0.7));
        avatarRegion.getChildren().add(clip);
        clip.toBack();

        loadAvatarIcons();
        setAvatarIconOnSelectBox();
        avatarSelect.setPlaceholder(new Label("Avatar"));

        loadWarningDialog();
    }

    private void loadAvatarIcons() {
        for (String avatarName : Avatars.avatarNames) {
            Image icon = new Image(getClass().getResourceAsStream("/assets/avatars/" + avatarName));
            avatarMap.put(avatarName, icon);
        }
    }

    private ListCell<String> avatarCellFactory(ObservableList<String> collection, boolean setGraphic) {
        return new ListCell<String>() {

            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                imageView.setFitHeight(64);
                imageView.setFitWidth(64);
                imageView.setPreserveRatio(true);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setImage(avatarMap.get(name));
                    setPrefWidth(64);
                    setPrefHeight(64);
                    if (setGraphic) {
                        setGraphic(imageView);
                    } else {
                        setText("#" + String.valueOf(collection.indexOf(name) + 1));
                    }
                }
            }
        };
    }

    private void setAvatarIconOnSelectBox() {
        ObservableList<String> selectItems = FXCollections.observableArrayList(avatarMap.keySet());
        avatarSelect.setItems(selectItems);

        avatarSelect.setCellFactory(param -> avatarCellFactory(selectItems, true));
        avatarSelect.setButtonCell(avatarCellFactory(selectItems, false));


        avatarSelect.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                avatarSelect.setBorder(null);
                avatarPreview.setImage(avatarMap.get(newValue));
                clip.setStroke(Color.valueOf("#9CEAEF"));
                clip.setStrokeWidth(3);
                clip.setRadius(36);
                avatarPreview.setFitWidth(64);
                avatarPreview.setFitHeight(64);
            }
        });
    }

    public void connect(ActionEvent event) {
        if (!"".equalsIgnoreCase(nicknameField.getText()) && avatarSelect.getSelectionModel().getSelectedIndex() != -1) {
            try {
                String host = ConnectionConfig.HOST.getValue();
                int port = Integer.parseInt(ConnectionConfig.PORT.getValue());
                Socket socket = new Socket(host, port);
                menuRoot.setDisable(true);

                Map<String, Object> data = new HashMap<>();
                data.put("socket", socket);
                data.put("nickname", nicknameField.getText());
                data.put("avatar", avatarSelect.getSelectionModel().getSelectedItem());

                Main.changeScreen("game", data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            warningDialog.show();
        }
    }

    private void loadWarningDialog() {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Informe seu nome de jogador e selecione um avatar"));
        Text info = new Text("Por favor, digite um nome de jogador no campo Nickname e escolha um avatar para poder " +
                "iniciar o jogo");
        info.setWrappingWidth(500);
        info.setTextAlignment(TextAlignment.LEFT);
        content.setBody(info);
        StackPane stackPane = new StackPane();
        stackPane.setLayoutY(230);
        stackPane.setLayoutX(230);
        info.setWrappingWidth(500);
        warningDialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton button = new JFXButton("OK");
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setCursor(Cursor.HAND);
        button.setBackground(new Background(new BackgroundFill(Color.valueOf("#002D73"), CornerRadii.EMPTY, Insets.EMPTY)));
        button.setTextFill(Color.WHITE);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                warningDialog.close();
            }
        });
        content.setActions(button);
        menuRoot.getChildren().add(stackPane);
    }

}
