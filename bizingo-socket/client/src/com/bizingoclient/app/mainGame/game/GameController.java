package com.bizingoclient.app.mainGame.game;


import bizingo.commons.*;
import com.bizingoclient.Main;
import com.bizingoclient.app.mainGame.MainGameController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSnackbar;
import io.vavr.Tuple2;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
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

    public void init(MainGameController mainGameController) {
        main = mainGameController;
        root.setBackground(Background.EMPTY);

        Image image = new Image(getClass().getResourceAsStream("/assets/board_base.jpg"));
        BackgroundSize backgroundSize = new BackgroundSize(900, 670, false,
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

        greenMarble = new ImagePattern(new Image(getClass().getResourceAsStream("/assets/green_marble.jpg")));
        whiteMarble = new ImagePattern(new Image(getClass().getResourceAsStream("/assets/white_marble.jpg")));

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

        Image hudImage = new Image(getClass().getResourceAsStream("/assets/hud.png"));
        BackgroundSize hudBackgroundSize = new BackgroundSize(250, 100, false,
                false, false, false);
        BackgroundImage hudBackgroundImage = new BackgroundImage(hudImage, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, hudBackgroundSize);
        Background hudBackground = new Background(hudBackgroundImage);
        hud1.setBackground(hudBackground);
        hud2.setBackground(hudBackground);

        updatePiecesCounter();
        drawBoard();

        loadOtherPlayerGiveUpDialog();

        //setTurnToPlay(true);
        //setPlayerColor(CellColor.DARK);
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
            this.playerColorIndicator.setImage(new Image(getClass().getResourceAsStream("/assets/black_piece.png")));
        } else {
            this.playerColorIndicator.setImage(new Image(getClass().getResourceAsStream("/assets/light_piece.png")));
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
                    } else {
                        Polygon tOld = bizingoBoard.getCellMap().getKey(selectedCell);
                        selectedCell = null;
                        unhighlightCell(tOld, false);
                    }
                } else {
                    selectedCell = c;
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

                    main.getMessageHandler().sendPlayerMovement(source, dest);
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
        piece.setLayoutX(newX - 2);
        if (cellDest.getColor() == CellColor.LIGHT) {
            yOffset *= -1;
        }

        piece.setLayoutY(newY + yOffset);
        Platform.runLater(piece::toFront);

        bizingoBoard.moveCellPiece(cellSource, cellDest);

        Tuple2<Boolean, BizingoCell> surrounded = bizingoBoard.cellHasSurrounded(cellDest);
        if (surrounded._1) { //peca capturou alguma outra
            BizingoCell captured = surrounded._2;
            Polygon t = bizingoBoard.getCellMap().getKey(captured);
            Circle c = bizingoBoard.getPieceMap().get(t);
            playPieceCapturedAnimation(c);
            notifyCapture(captured);
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
            notificationSnack.enqueue(otherPlayerPieceCapturedEvent);
            oponentsPieces--;
            if(oponentsPieces == 2){
                System.out.println("Jogador venceu!");
            }
        } else {
            System.out.println("Uma peca sua foi capturada");
            notificationSnack.enqueue(ownPieceCapturedEvent);
            numberOfPlayersPieces--;
            updatePiecesCounter();
            if (numberOfPlayersPieces == 2) {
                System.out.println("Jogador perdeu!");
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
        button.setBackground(new Background(new BackgroundFill(Color.valueOf("#002D73"), CornerRadii.EMPTY, Insets.EMPTY)));
        button.setTextFill(Color.WHITE);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                main.getMessageHandler().sendQuitMessage();
                main.getChatToolbarController().clearMessages();
                giveupDialog.close();
                Main.changeScreen("menu", null);
            }
        });
        content.setActions(button);
        root.getChildren().add(stackPane);
    }

    public void showGiveUpDialog(){
        giveupDialog.show();
    }

}