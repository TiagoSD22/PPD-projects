package com.bizingoclient.app.mainGame.game;


import bizingo.commons.*;
import com.bizingoclient.Main;
import com.bizingoclient.app.mainGame.MainGameController;
import com.bizingoclient.app.services.AudioService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;

import java.util.ArrayList;


public class GameController {

    private final Glow glowEffect = new Glow(1.0);
    @FXML
    public AnchorPane root;
    @FXML
    private StackPane boardBase;
    @FXML
    private ImageView playerColorIndicator;
    @FXML
    private Text playerTurnIndicator;
    @FXML
    private Text piecesCounter;
    @FXML
    private GridPane hud1;
    @FXML
    private GridPane hud2;
    @FXML
    private JFXButton restartBT;
    @FXML
    private JFXButton soundBT;
    private Group board;
    private MainGameController main;
    private BizingoBoard bizingoBoard;
    private CellColor playerColor;
    private boolean turnToPlay;
    private int numberOfPlayersPieces;
    private int oponentsPieces;
    private BizingoCell selectedCell;
    private ImagePattern greenMarble;
    private ImagePattern whiteMarble;
    private DropShadow pieceShadow;
    private ScaleTransition capturedPieceAnimation;
    private JFXSnackbar notificationSnack;
    private SnackbarEvent ownPieceCapturedEvent;
    private SnackbarEvent otherPlayerPieceCapturedEvent;
    private JFXDialog giveupDialog;
    private JFXDialog endGameDialog;
    private JFXDialog otherPlayerDidntRestartedDialog;
    private JFXDialog restartSolicitationDialog;
    private JFXDialog otherPlayerRestartSolicitationDialog;
    private Text restartDialogText;
    private boolean gameFinished;
    private boolean playerRestarted;
    private boolean otherPlayerRestarted;
    private boolean otherPlayerDidntRestartedDialogOpened;
    private TranslateTransition movementAnimation;
    private JFXButton restartDialogBT;
    private Tooltip soundTt;
    private static final int MINIMUM_PIECES = 2;

    public void init(MainGameController mainGameController) {
        main = mainGameController;
        root.setBackground(Background.EMPTY);

        Image image = new Image(getClass().getResourceAsStream("/assets/Images/board_base.jpg"));
        BackgroundSize backgroundSize = new BackgroundSize(844, 628, false,
                false, false, false);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        Background background = new Background(backgroundImage);

        boardBase.setBackground(background);

        this.bizingoBoard = new BizingoBoard();
        numberOfPlayersPieces = 18;
        oponentsPieces = 18;

        pieceShadow = new DropShadow();
        pieceShadow.setOffsetX(6.0);
        pieceShadow.setOffsetY(4.0);

        capturedPieceAnimation = new ScaleTransition(Duration.seconds(1.5));
        capturedPieceAnimation.setFromX(1.0);
        capturedPieceAnimation.setFromY(1.0);
        capturedPieceAnimation.setToX(0.0);
        capturedPieceAnimation.setToY(0.0);

        greenMarble = new ImagePattern(new Image(getClass().getResourceAsStream("/assets/Images/green_marble.jpg")));
        whiteMarble = new ImagePattern(new Image(getClass().getResourceAsStream("/assets/Images/white_marble.jpg")));

        notificationSnack = new JFXSnackbar(root);
        notificationSnack.setPrefWidth(300);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0, 0, 0));
        notificationSnack.setEffect(dropShadow);

        Text txt = new Text("Você capturou uma peça do oponente");
        txt.setFill(Color.WHITE);
        otherPlayerPieceCapturedEvent = new SnackbarEvent(txt);

        Text txt2 = new Text("Uma peça sua foi capturada");
        txt2.setFill(Color.WHITE);
        ownPieceCapturedEvent = new SnackbarEvent(txt2);

        Image hudImage = new Image(getClass().getResourceAsStream("/assets/Images/hud.png"));
        BackgroundSize hudBackgroundSize = new BackgroundSize(250, 100, false,
                false, false, false);
        BackgroundImage hudBackgroundImage = new BackgroundImage(hudImage, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, hudBackgroundSize);
        Background hudBackground = new Background(hudBackgroundImage);
        hud1.setBackground(hudBackground);
        hud2.setBackground(hudBackground);

        restartBT.setGraphic(new ImageView(new Image(getClass()
                .getResourceAsStream("/assets/Images/restart.png"))));
        DropShadow ds = new DropShadow();
        ds.setOffsetX(1.3);
        ds.setOffsetY(1.3);
        ds.setColor(Color.BLACK);
        restartBT.setEffect(ds);

        Tooltip tt = new Tooltip();
        tt.setText("Reiniciar partida");
        tt.setStyle("-fx-font: normal bold 12 Langdon; "
                + "-fx-base: #AE3522; "
                + "-fx-text-fill: orange;");

        restartBT.setTooltip(tt);

        soundBT.setGraphic(new ImageView(new Image(getClass()
                .getResourceAsStream("/assets/Images/sound.png"))));
        soundBT.setEffect(ds);

        soundTt = new Tooltip();
        soundTt.setText("Desativar efeitos sonoros");
        soundTt.setStyle("-fx-font: normal bold 12 Langdon; "
                + "-fx-base: #AE3522; "
                + "-fx-text-fill: orange;");
        soundBT.setTooltip(soundTt);

        this.movementAnimation = new TranslateTransition(Duration.millis(650));

        updatePiecesCounter();
        drawBoard();

        loadOtherPlayerGiveUpDialog();
        loadRestartSolicitationDialog();
        loadOtherPlayerRestartSolicitationDialog();
        if(otherPlayerDidntRestartedDialogOpened) {
            otherPlayerDidntRestartedDialog.close();
            otherPlayerDidntRestartedDialogOpened = false;
        }

        gameFinished = false;
        playerRestarted = false;
        otherPlayerRestarted = false;
        board.setDisable(false);
    }

    private void drawBoard() {
        board = new Group();
        Double x0, y0;
        x0 = 0.0;
        y0 = -50.0;

        for (int row = 0; row < 11; row++) {
            int limit = 5 + 2 * row;
            if (row == 9) {
                limit = 21;
            } else if (row == 10) {
                limit = 19;
            }
            Double currentX0 = x0 - (25.0 * row);
            Double currentY0 = y0 + (50.0 * row);
            if (row == 9) {
                currentX0 += 25;
            } else if (row == 10) {
                currentX0 += 75;
            }
            for (int column = 0; column < limit; column++) {
                int initialDarkIndex = 0;
                if (row > 8) {
                    initialDarkIndex = 1;
                }
                Polygon t = new Polygon();

                String cellCoordinate = row + "," + column;

                Circle piece = null;

                BizingoCell cell = new BizingoCell();
                cell.setRow(row);
                cell.setColumn(column);

                if (column % 2 == initialDarkIndex) { //peca escura
                    t.setFill(greenMarble);

                    t.getPoints().addAll(
                            currentX0, currentY0,
                            currentX0 - 25, currentY0 + 50,
                            currentX0 + 25, currentY0 + 50
                    );

                    cell.setColor(CellColor.DARK);

                    if (PiecesInitialPositions.getInstance().getPositionMap().containsKey(cellCoordinate)) {
                        CellContent content = PiecesInitialPositions.getInstance().getPositionMap().get(cellCoordinate);

                        cell.setContent(content);

                        piece = new Circle();
                        piece.setLayoutX(currentX0 - 2);
                        piece.setLayoutY(currentY0 + 30);
                        piece.setRadius(10);
                        piece.setEffect(pieceShadow);
                        if (content.equals(CellContent.REGULAR_PIECE)) {
                            piece.setFill(Color.BLACK);
                        } else {
                            piece.setFill(Color.BLUE);
                        }
                    } else {
                        CellContent content = CellContent.EMPTY;
                        cell.setContent(content);
                    }
                } else { //peca clara
                    t.setFill(whiteMarble);

                    t.getPoints().addAll(
                            currentX0, currentY0 + 50,
                            currentX0 - 25, currentY0,
                            currentX0 + 25, currentY0
                    );

                    cell.setColor(CellColor.LIGHT);

                    if (PiecesInitialPositions.getInstance().getPositionMap().containsKey(cellCoordinate)) {
                        CellContent content = PiecesInitialPositions.getInstance().getPositionMap().get(cellCoordinate);
                        cell.setContent(content);

                        piece = new Circle();
                        piece.setLayoutX(currentX0 - 2);
                        piece.setLayoutY(currentY0 + 20);
                        piece.setRadius(10);

                        piece.setEffect(pieceShadow);
                        if (content.equals(CellContent.REGULAR_PIECE)) {
                            piece.setFill(Color.RED);
                        } else {
                            piece.setFill(Color.YELLOW);
                        }
                    } else {
                        CellContent content = CellContent.EMPTY;
                        cell.setContent(content);
                    }
                }

                t.setStroke(Color.BLACK);

                addEventHandlersForCell(t);

                this.bizingoBoard.addCell(cell);
                this.bizingoBoard.putCellOnMap(t, cell);

                board.getChildren().add(t);
                if (piece != null) {
                    board.getChildren().add(piece);
                    this.bizingoBoard.putPieceOnMap(t, piece);
                    addMouseHandlerToPiece(piece);
                }

                t.toBack();

                currentX0 += 25.0;

            }
        }

        bizingoBoard.addNeighbourhood();

        boardBase.getChildren().add(board);
        board.setTranslateY(-40);
    }

    public CellColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(CellColor playerColor) {
        this.playerColor = playerColor;
        if (this.playerColor == CellColor.DARK) {
            this.playerColorIndicator.setImage(new Image(getClass().getResourceAsStream("/assets/Images/black_piece.png")));
        } else {
            this.playerColorIndicator.setImage(new Image(getClass().getResourceAsStream("/assets/Images/light_piece.png")));
        }
    }

    public boolean isTurnToPlay() {
        return turnToPlay;
    }

    public void setTurnToPlay(boolean turnToPlay) {
        this.turnToPlay = turnToPlay;
        if (this.turnToPlay) {
            this.playerTurnIndicator.setText("SUA VEZ DE JOGAR");
            this.playerTurnIndicator.setFill(Color.valueOf("#13C196"));
        } else {
            this.playerTurnIndicator.setText("VEZ DO OPONENTE JOGAR");
            this.playerTurnIndicator.setFill(Color.valueOf("#FFFFFF"));
        }
    }

    public void updatePiecesCounter(){
        piecesCounter.setText("x" + numberOfPlayersPieces);
    }

    public void addEventHandlersForCell(Polygon t) {
        t.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (turnToPlay) {
                    BizingoCell c = bizingoBoard.getCellMap().get(t);
                    if (c.getContent() != CellContent.EMPTY) {
                        if (c.getColor() == playerColor && selectedCell == null) {
                            highlightCell(t);
                        }
                    } else {
                        if (selectedCell != null) {
                            if (selectedCell.getNeighboursSameColor().contains(c)) {
                                highlightPossibleDestCell(t); // indicando que a peca pode ser movida para aquela casa
                            }
                        }
                    }
                }
            }
        });

        t.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                boolean force = false;
                if (!t.equals(bizingoBoard.getCellMap().getKey(selectedCell))) {
                    force = true;
                }
                unhighlightCell(t, force);
            }
        });

        t.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (turnToPlay) {
                    BizingoCell c = bizingoBoard.getCellMap().get(t);
                    handlePieceClicked(c);
                }
            }
        });
    }

    private void addMouseHandlerToPiece(Circle piece) {
        piece.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(bizingoBoard.getCellMap().get(bizingoBoard.getPieceMap().getKey(piece)).getColor() == playerColor){
                    if(turnToPlay) {
                        piece.setCursor(Cursor.HAND);
                    }
                    else{
                        piece.setCursor(Cursor.DEFAULT);
                    }
                }
                if (turnToPlay) {
                    Polygon t = bizingoBoard.getPieceMap().getKey(piece);
                    BizingoCell c = bizingoBoard.getCellMap().get(t);
                    if (c.getColor() == playerColor && selectedCell == null) {
                        highlightCell(t);
                    }
                }
            }
        });

        piece.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (turnToPlay) {
                    Polygon t = bizingoBoard.getPieceMap().getKey(piece);
                    BizingoCell c = bizingoBoard.getCellMap().get(t);
                    handlePieceClicked(c);
                }
            }
        });
    }

    private void handlePieceClicked(BizingoCell c) {
        if (c.getContent() != CellContent.EMPTY) {
            if (c.getColor() == playerColor) {
                if (selectedCell != null) {
                    if (!selectedCell.equals(c)) {
                        Polygon tOld = bizingoBoard.getCellMap().getKey(selectedCell);
                        selectedCell = null;
                        unhighlightCell(tOld, false);
                        selectedCell = c;
                        highlightCell(bizingoBoard.getCellMap().getKey(c));
                        AudioService.getInstance().playPieceSelectedSound();
                    } else {
                        Polygon tOld = bizingoBoard.getCellMap().getKey(selectedCell);
                        selectedCell = null;
                        unhighlightCell(tOld, false);
                        AudioService.getInstance().playPieceDeselectedSound();
                    }
                } else {
                    selectedCell = c;
                    AudioService.getInstance().playPieceSelectedSound();
                }
            }
        } else {
            if (selectedCell != null) { //movendo peca
                if (selectedCell.getNeighboursSameColor().contains(c)) {
                    String source = bizingoBoard.getPositionCellMap().getKey(selectedCell);
                    String dest = bizingoBoard.getPositionCellMap().getKey(c);

                    movePieceOnBoard(selectedCell, c);
                    Polygon tOld = bizingoBoard.getCellMap().getKey(selectedCell);
                    unhighlightCell(tOld, true);
                    selectedCell = null;

                    setTurnToPlay(false);

                    main.getClientStub().sendPlayerMovement(source, dest);
                }
            }
        }
    }

    public void showOponentMove(String source, String dest) {
        BizingoCell cellSource = bizingoBoard.getPositionCellMap().get(source);
        BizingoCell cellDest = bizingoBoard.getPositionCellMap().get(dest);

        movePieceOnBoard(cellSource, cellDest);
        setTurnToPlay(true);
    }

    private void movePieceOnBoard(BizingoCell cellSource, BizingoCell cellDest) {
        Polygon old = bizingoBoard.getCellMap().getKey(cellSource);
        Circle piece = bizingoBoard.getPieceMap().get(old);
        Polygon tSource = bizingoBoard.getCellMap().getKey(cellDest);
        Double newX = tSource.getPoints().get(0);
        Double newY = tSource.getPoints().get(1);
        int yOffset = 30;

        if (cellDest.getColor() == CellColor.LIGHT) {
            yOffset *= -1;
        }

        Platform.runLater(piece::toFront);

        this.movementAnimation.setToX(newX - 2 - piece.getLayoutX());
        this.movementAnimation.setToY(newY + yOffset - piece.getLayoutY());
        this.movementAnimation.setNode(piece);
        this.movementAnimation.play();
        this.movementAnimation.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AudioService.getInstance().playPieceMovedSound();
                bizingoBoard.moveCellPiece(cellSource, cellDest);
                analyseMovement(cellDest);
            }
        });
    }

    private void analyseMovement(BizingoCell cellDest){
        ArrayList<BizingoCell> surrounded = bizingoBoard.cellHasSurrounded(cellDest);
        if (!surrounded.isEmpty()) { //peca capturou alguma outra
            for(BizingoCell captured : surrounded) {
                Polygon t = bizingoBoard.getCellMap().getKey(captured);
                Circle c = bizingoBoard.getPieceMap().get(t);

                ScaleTransition capturedPieceAnimation = new ScaleTransition(Duration.seconds(1.5));
                capturedPieceAnimation.setFromX(1.0);
                capturedPieceAnimation.setFromY(1.0);
                capturedPieceAnimation.setToX(0.0);
                capturedPieceAnimation.setToY(0.0);
                capturedPieceAnimation.setNode(c);
                capturedPieceAnimation.play();

                notifyCapture(captured);
            }
        } else {
            if (bizingoBoard.isSurrounded(cellDest)) { //peca se moveu para um cerco
                Polygon t = bizingoBoard.getCellMap().getKey(cellDest);
                Circle c = bizingoBoard.getPieceMap().get(t);
                playPieceCapturedAnimation(c);
                notifyCapture(cellDest);
            }
        }
    }

    private void playPieceCapturedAnimation(Circle piece) {
        capturedPieceAnimation.setNode(piece);
        capturedPieceAnimation.play();
    }

    private void notifyCapture(BizingoCell captured) {
        if (captured.getColor() != playerColor) {
            System.out.println("Voce capturou uma peca do oponente");
            AudioService.getInstance().playOpponentPieceCapturedSound();
            notificationSnack.enqueue(otherPlayerPieceCapturedEvent);
            oponentsPieces--;
            if(oponentsPieces == MINIMUM_PIECES){
                System.out.println("Jogador venceu!");
                gameFinished = true;
                Platform.runLater(() -> {
                    loadEndGameDialog(true);
                    onGameFinished();
                    AudioService.getInstance().playWinSound();
                });
            }
        } else {
            System.out.println("Uma peca sua foi capturada");
            AudioService.getInstance().playOwnPieceCapturedSound();
            notificationSnack.enqueue(ownPieceCapturedEvent);
            numberOfPlayersPieces--;
            updatePiecesCounter();
            if (numberOfPlayersPieces == MINIMUM_PIECES) {
                System.out.println("Jogador perdeu!");
                gameFinished = true;
                Platform.runLater(() -> {
                    loadEndGameDialog(false);
                    onGameFinished();
                    AudioService.getInstance().playLoseSound();
                });
            }
        }
    }

    private void highlightCell(Polygon t) {
        t.setEffect(glowEffect);
        t.setCursor(Cursor.HAND);
    }

    private void unhighlightCell(Polygon t, boolean force) {
        if (selectedCell == null || force) {
            t.setEffect(null);
            t.setCursor(Cursor.DEFAULT);
        }
    }

    private void highlightPossibleDestCell(Polygon t) {
        ColorAdjust ca = new ColorAdjust();
        ca.setBrightness(-0.5);
        t.setEffect(ca);
    }

    private void loadOtherPlayerGiveUpDialog() {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("OPONENTE DESISTIU"));
        Text info = new Text("Seu oponente desistiu da partida e ela será encerrada, " +
                "clique no botão abaixo para voltar ao menu.");

        info.setWrappingWidth(500);
        info.setTextAlignment(TextAlignment.LEFT);
        content.setBody(info);
        StackPane stackPane = new StackPane();
        stackPane.setLayoutY(230);
        stackPane.setLayoutX(230);
        info.setWrappingWidth(500);
        giveupDialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton button = new JFXButton("VOLTAR");
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setCursor(Cursor.HAND);
        button.setBackground(new Background(new BackgroundFill(Color.valueOf("#13C196"), CornerRadii.EMPTY, Insets.EMPTY)));
        button.setTextFill(Color.WHITE);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                main.getClientStub().sendCloseMessage();
                main.getChatToolbarController().clearMessages();
                giveupDialog.close();
                Main.changeScreen("menu", null);
            }
        });
        content.setActions(button);
        root.getChildren().add(stackPane);
    }

    private void loadEndGameDialog(boolean won) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("FIM DE PARTIDA"));
        Text info = new Text();

        if(won){
            info.setText("Parabéns, você venceu a partida! Deseja iniciar outra partida ou voltar ao menu?");
        }
        else{
            info.setText("Seu oponente levou a melhor e você perdeu. Deseja iniciar outra partida ou voltar ao menu?");
        }
        

        info.setWrappingWidth(500);
        info.setTextAlignment(TextAlignment.LEFT);
        content.setBody(info);
        StackPane stackPane = new StackPane();
        stackPane.setLayoutY(230);
        stackPane.setLayoutX(230);
        info.setWrappingWidth(500);
        endGameDialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        endGameDialog.setOverlayClose(false);
        JFXButton menuBt = new JFXButton("VOLTAR");
        menuBt.setButtonType(JFXButton.ButtonType.RAISED);
        menuBt.setCursor(Cursor.HAND);
        menuBt.setBackground(new Background(new BackgroundFill(Color.valueOf("#30343F"), CornerRadii.EMPTY, Insets.EMPTY)));
        menuBt.setTextFill(Color.WHITE);
        menuBt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                main.getClientStub().sendQuitMessage();
                main.getChatToolbarController().clearMessages();
                endGameDialog.close();
                Main.changeScreen("menu", null);
            }
        });

        JFXButton restartBt = new JFXButton("REINICIAR");
        restartBt.setButtonType(JFXButton.ButtonType.RAISED);
        restartBt.setCursor(Cursor.HAND);
        restartBt.setBackground(new Background(new BackgroundFill(Color.valueOf("#13C196"), CornerRadii.EMPTY, Insets.EMPTY)));
        restartBt.setTextFill(Color.WHITE);
        restartBt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                restartBt.setVisible(false);
                menuBt.setVisible(false);
                main.getClientStub().sendRestartMessage();
                info.setText("Aguardando confirmação do outro jogador...");
                playerRestarted = true;
                handleRestart();
            }
        });
        content.setActions(menuBt, restartBt);
        root.getChildren().add(stackPane);
    }

    private void loadOtherPlayerDidntRestartedDialog(){
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("FIM DE PARTIDA"));
        Text info = new Text("O outro jogador decidiu voltar ao menu, clique em voltar para voltar ao menu também.");


        info.setWrappingWidth(500);
        info.setTextAlignment(TextAlignment.LEFT);
        content.setBody(info);
        StackPane stackPane = new StackPane();
        stackPane.setLayoutY(230);
        stackPane.setLayoutX(230);
        info.setWrappingWidth(500);
        otherPlayerDidntRestartedDialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        otherPlayerDidntRestartedDialog.setOverlayClose(false);
        JFXButton menuBt = new JFXButton("VOLTAR");
        menuBt.setButtonType(JFXButton.ButtonType.RAISED);
        menuBt.setCursor(Cursor.HAND);
        menuBt.setBackground(new Background(new BackgroundFill(Color.valueOf("#13C196"), CornerRadii.EMPTY, Insets.EMPTY)));
        menuBt.setTextFill(Color.WHITE);
        menuBt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                main.getClientStub().sendCloseMessage();
                main.getChatToolbarController().clearMessages();
                endGameDialog.close();
                Main.changeScreen("menu", null);
            }
        });

        content.setActions(menuBt);
        root.getChildren().add(stackPane);
    }

    private void loadRestartSolicitationDialog(){
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("SOLICITAÇÃO DE REINICIO"));
        restartDialogText = new Text("Aguardando resposta do outro jogador");


        restartDialogText.setWrappingWidth(500);
        restartDialogText.setTextAlignment(TextAlignment.LEFT);
        content.setBody(restartDialogText);
        StackPane stackPane = new StackPane();
        stackPane.setLayoutY(230);
        stackPane.setLayoutX(230);
        restartDialogText.setWrappingWidth(500);
        restartSolicitationDialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        restartSolicitationDialog.setOverlayClose(false);

        restartDialogBT = new JFXButton("OK");
        restartDialogBT.setButtonType(JFXButton.ButtonType.RAISED);
        restartDialogBT.setCursor(Cursor.HAND);
        restartDialogBT.setBackground(new Background(new BackgroundFill(Color.valueOf("#13C196"), CornerRadii.EMPTY, Insets.EMPTY)));
        restartDialogBT.setTextFill(Color.WHITE);
        restartDialogBT.setVisible(false);
        restartDialogBT.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                restartSolicitationDialog.close();
                restartBT.setDisable(false);
                board.setDisable(false);
            }
        });

        content.setActions(restartDialogBT);
        root.getChildren().add(stackPane);
    }

    private void loadOtherPlayerRestartSolicitationDialog(){
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("SOLICITAÇÃO DE REINICIO"));
        Text info = new Text("O outro jogador solicitou reiniciar a partida. Confirmar reinicio?");

        info.setWrappingWidth(500);
        info.setTextAlignment(TextAlignment.LEFT);
        content.setBody(info);
        StackPane stackPane = new StackPane();
        stackPane.setLayoutY(230);
        stackPane.setLayoutX(230);
        info.setWrappingWidth(500);
        otherPlayerRestartSolicitationDialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        otherPlayerRestartSolicitationDialog.setOverlayClose(false);
        JFXButton noBT = new JFXButton("NÃO");
        noBT.setButtonType(JFXButton.ButtonType.RAISED);
        noBT.setCursor(Cursor.HAND);
        noBT.setBackground(new Background(new BackgroundFill(Color.valueOf("#30343F"), CornerRadii.EMPTY, Insets.EMPTY)));
        noBT.setTextFill(Color.WHITE);
        noBT.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                main.getClientStub().sendDenyRestartMessage();
                board.setDisable(false);
                restartBT.setDisable(false);
                otherPlayerRestartSolicitationDialog.close();
            }
        });

        JFXButton restartBt = new JFXButton("SIM");
        restartBt.setButtonType(JFXButton.ButtonType.RAISED);
        restartBt.setCursor(Cursor.HAND);
        restartBt.setBackground(new Background(new BackgroundFill(Color.valueOf("#13C196"), CornerRadii.EMPTY, Insets.EMPTY)));
        restartBt.setTextFill(Color.WHITE);
        restartBt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                main.getClientStub().sendRestartMessage();
                otherPlayerRestartSolicitationDialog.close();
                board.setDisable(false);
                restartBT.setDisable(false);
                restart();
            }
        });
        content.setActions(noBT, restartBt);
        root.getChildren().add(stackPane);
    }

    public void showGiveUpDialog(){
        if(!gameFinished) {
            giveupDialog.show();
            AudioService.getInstance().playNotificationSound();
            board.setDisable(true);
        }
        else{
            loadOtherPlayerDidntRestartedDialog();
            otherPlayerDidntRestartedDialogOpened = true;
            otherPlayerDidntRestartedDialog.show();
            AudioService.getInstance().playNotificationSound();
            board.setDisable(true);
        }
    }

    private void onGameFinished(){
        board.setDisable(true);
        endGameDialog.show();
    }

    private void restart(){
        bizingoBoard = new BizingoBoard();
        numberOfPlayersPieces = 18;
        oponentsPieces = 18;
        gameFinished = false;
        board.getChildren().clear();
        drawBoard();
        updatePiecesCounter();
        if(gameFinished) {
            endGameDialog.close();
        }
        if(otherPlayerDidntRestartedDialogOpened) {
            otherPlayerDidntRestartedDialog.close();
        }
        playerRestarted = false;
        otherPlayerRestarted = false;
        board.setDisable(false);
        notificationSnack.enqueue(new SnackbarEvent(new Text("Iniciando nova partida")));
        AudioService.getInstance().playNotificationSound();
    }

    private void handleRestart(){
        if(playerRestarted && otherPlayerRestarted){
            restart();
        }
    }

    public void otherPlayerWannaRestart(){
        if(gameFinished) {
            otherPlayerRestarted = true;
            handleRestart();
        }
        else{
            if(playerRestarted){ //outro jogador confirmou
                restartDialogBT.setVisible(true);
                playerRestarted = false;
                restartDialogText.setText("Outro jogador concordou em reiniciar. Clique no botão abaixo para continuar");
                restart();
            }
            else{ //outro jogador pediu para reiniciar durante a partida
                otherPlayerRestartSolicitationDialog.show();
                AudioService.getInstance().playNotificationSound();
                board.setDisable(true);
                restartBT.setDisable(true);
            }
        }
    }

    public void onRestartSolicitationDenied(){
        restartDialogText.setText("Outro jogador recusou reiniciar partida. Clique no botão abaixo para continuar");
        playerRestarted = false;
        board.setDisable(false);
        AudioService.getInstance().playNotificationSound();
        restartDialogBT.setVisible(true);
    }

    public void onRestartSolicitation(){
        main.getClientStub().sendRestartMessage();
        playerRestarted = true;
        restartDialogBT.setVisible(false);
        restartBT.setDisable(true);
        restartDialogText.setText("Aguardando resposta do outro jogador");
        restartSolicitationDialog.show();
        board.setDisable(true);
    }

    public void onSoundBtClicked(){
        if(AudioService.getInstance().sound){
            soundBT.setGraphic(new ImageView(new Image(getClass()
                    .getResourceAsStream("/assets/Images/no_sound.png"))));
            soundTt.setText("Reativar efeitos sonoros");
        }
        else{
            soundBT.setGraphic(new ImageView(new Image(getClass()
                    .getResourceAsStream("/assets/Images/sound.png"))));
            soundTt.setText("Desativar efeitos sonoros");
        }
        AudioService.getInstance().onMute();
    }

}