package com.spatia.client.app.menu;



import com.spatia.client.Main;
import com.spatia.client.app.utils.Avatars;
import com.spatia.client.app.utils.ConnectionConfig;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.IOException;
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
    private JFXComboBox avatarSelect;
    private Map<String, Image> avatarMap = new HashMap<String, Image>();
    @FXML
    private JFXButton connectButton;
    @FXML
    private CustomTextField nicknameField;
    private JFXDialog warningDialog;
    private JFXDialog connectionSolicitationRejected;

    private String userName;
    private String avatarImageName;

    @FXML
    public void initialize() {
        Image imProfile = new Image(getClass().getResourceAsStream("/assets/Images/no_avatar.png"));
        avatarPreview.setImage(imProfile);

        clip = new Circle(24);
        avatarPreview.setFitWidth(64);
        avatarPreview.setFitHeight(64);
        clip.setStroke(Color.WHITE);
        clip.setStrokeWidth(1);
        clip.setFill(Color.rgb(0, 0, 0, 0.7));
        avatarRegion.getChildren().add(clip);
        clip.toBack();

        DropShadow ds = new DropShadow();
        ds.setOffsetX(1.3);
        ds.setOffsetY(1.3);
        ds.setColor(Color.BLACK);
        connectButton.setEffect(ds);

        loadAvatarIcons();
        setAvatarIconOnSelectBox();
        avatarSelect.setPlaceholder(new Label("Avatar"));

        loadWarningDialog();
        loadConnectionSolicitationRejectedDialog();

        Main.addOnChangeScreenListener(new Main.OnChangeSceen() {
            @Override
            public void onScreenChanged(String newScreen, Object data, Stage stage) {
                if (newScreen.equals("menu")) {
                    nicknameField.setDisable(false);
                    avatarSelect.setDisable(false);
                }
                else if(newScreen.equalsIgnoreCase("stop")){
                    /*if(client != null && !gameStarted){
                        System.out.println("Cliente desconectando antes de iniciar partida, enviando solicitacao de CLOSE");
                        client.sendCloseMessage();
                    }*/
                }
            }
        });
    }

    private void loadAvatarIcons() {
        for (String avatarName : Avatars.avatarNames) {
            Image icon = new Image(getClass().getResourceAsStream("/assets/Images/avatars/" + avatarName));
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
                this.setVisible(name != null || !empty);
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

                setTextFill(Color.valueOf("#fcfcfc"));
            }
        };
    }

    private void setAvatarIconOnSelectBox() {
        ObservableList<String> selectItems = FXCollections.observableArrayList(avatarMap.keySet());
        avatarSelect.setItems(selectItems);

        avatarSelect.setPromptText("Avatar");

        avatarSelect.setCellFactory(param -> avatarCellFactory(selectItems, true));
        avatarSelect.setButtonCell(avatarCellFactory(selectItems, false));


        avatarSelect.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                avatarSelect.setBorder(null);
                avatarPreview.setImage(avatarMap.get(newValue));
                clip.setStroke(Color.valueOf("#f7717d"));
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
                nicknameField.setDisable(true);
                avatarSelect.setDisable(true);

                userName = nicknameField.getText();
                avatarImageName = (String)avatarSelect.getSelectionModel().getSelectedItem();

                //msgHandler.sendConnectionSolicitation(userName, avatarImageName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            warningDialog.show();
        }
    }

    public void onConnectionSolicitationResponse(boolean accepted){
        if(!accepted){
            connectionSolicitationRejected.show();
        }
        else{
            /*Client c = new Client(userName, avatarImageName, Status.ONLINE);
            Map<String, Object> data = new HashMap<>();
            data.put("msgHandler", this.msgHandler);
            data.put("client", c);
            Main.changeScreen("chat-screen", data);*/
        }
    }

    private void loadWarningDialog() {
        JFXDialogLayout content = new JFXDialogLayout();
        Text title = new Text("Informe seu nome e selecione um avatar");
        title.setTextAlignment(TextAlignment.CENTER);
        content.setHeading(title);
        Text info = new Text("Por favor, digite um nome de usuario no campo Usuario e escolha um avatar para poder " +
                "se conectar");
        info.setTextAlignment(TextAlignment.LEFT);
        info.setWrappingWidth(364);
        content.setBody(info);
        content.setMinWidth(388);
        content.setMinHeight(200);

        StackPane stackPane = new StackPane();
        stackPane.setLayoutY(260);
        stackPane.setLayoutX(12);
        stackPane.setPrefWidth(388);

        warningDialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        warningDialog.setMinHeight(200);
        warningDialog.setMinWidth(388);
        warningDialog.setMaxSize(stackPane.getMaxWidth(), stackPane.getMaxHeight());

        JFXButton button = new JFXButton("OK");
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setCursor(Cursor.HAND);
        button.setBackground(new Background(new BackgroundFill(Color.valueOf("#f7717d"), CornerRadii.EMPTY, Insets.EMPTY)));
        button.setTextFill(Color.WHITE);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                warningDialog.close();
            }
        });
        button.setLayoutX(10);

        content.setActions(button);
        menuRoot.getChildren().add(stackPane);
    }

    private void loadConnectionSolicitationRejectedDialog() {
        JFXDialogLayout content = new JFXDialogLayout();
        Text title = new Text("Falha ao conectar");
        title.setTextAlignment(TextAlignment.CENTER);
        content.setHeading(title);
        Text info = new Text("Esse nome de usuario ja esta sendo usuado. Por favor, escolha outro");
        info.setTextAlignment(TextAlignment.LEFT);
        info.setWrappingWidth(364);

        content.setBody(info);
        content.setMinWidth(388);
        content.setMinHeight(200);
        content.setBody(info);

        StackPane stackPane = new StackPane();
        stackPane.setLayoutY(260);
        stackPane.setLayoutX(12);
        stackPane.setPrefWidth(388);

        connectionSolicitationRejected = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        connectionSolicitationRejected.setMinHeight(200);
        connectionSolicitationRejected.setMinWidth(388);
        connectionSolicitationRejected.setMaxSize(stackPane.getMaxWidth(), stackPane.getMaxHeight());

        JFXButton button = new JFXButton("OK");
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setCursor(Cursor.HAND);
        button.setBackground(new Background(new BackgroundFill(Color.valueOf("#f7717d"), CornerRadii.EMPTY, Insets.EMPTY)));
        button.setTextFill(Color.WHITE);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connectionSolicitationRejected.close();
                nicknameField.setDisable(false);
                avatarSelect.setDisable(false);
            }
        });
        content.setActions(button);
        menuRoot.getChildren().add(stackPane);
    }
}
