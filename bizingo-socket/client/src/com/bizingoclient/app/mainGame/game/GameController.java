package com.bizingoclient.app.mainGame.game;


import bizingo.commons.BizingoBoard;
import bizingo.commons.BizingoCell;
import bizingo.commons.CellColor;
import bizingo.commons.CellContent;
import com.bizingoclient.app.mainGame.MainGameController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.util.Objects;


public class GameController {

    @FXML
    public AnchorPane root;
    @FXML
    private StackPane boardBase;
    @FXML
    private ImageView playerColorIndicator;
    @FXML
    private Text playerTurnIndicator;
    private Group board;

    private MainGameController main;
    private BizingoBoard bizingoBoard;

    private CellColor playerColor;
    private boolean turnToPlay;

    private BizingoCell selectedCell;

    private final String DARK_COLOR = "#13C196";
    private final String LIGHT_COLOR = "#FFFFFF";
    private final Glow glowEffect = new Glow(1.0);

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

        drawBoard();

        //remover para versao final
        setTurnToPlay(true);
        setPlayerColor(CellColor.LIGHT);
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
                    t.setFill(Color.valueOf(DARK_COLOR));

                    t.getPoints().addAll(
                            currentX0, currentY0,
                            currentX0 - 25, currentY0 + 50,
                            currentX0 + 25, currentY0 + 50
                    );

                    cell.setColor(CellColor.DARK);

                    if (PiecesInitialPositions.getInstance().positionMap.containsKey(cellCoordinate)) {
                        CellContent content = PiecesInitialPositions.getInstance().positionMap.get(cellCoordinate);

                        cell.setContent(content);

                        piece = new Circle();
                        piece.setLayoutX(currentX0);
                        piece.setLayoutY(currentY0 + 30);
                        piece.setRadius(10);
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
                    t.setFill(Color.valueOf(LIGHT_COLOR));

                    t.getPoints().addAll(
                            currentX0, currentY0 + 50,
                            currentX0 - 25, currentY0,
                            currentX0 + 25, currentY0
                    );

                    cell.setColor(CellColor.LIGHT);

                    if (PiecesInitialPositions.getInstance().positionMap.containsKey(cellCoordinate)) {
                        CellContent content = PiecesInitialPositions.getInstance().positionMap.get(cellCoordinate);
                        cell.setContent(content);

                        piece = new Circle();
                        piece.setLayoutX(currentX0);
                        piece.setLayoutY(currentY0 + 20);
                        piece.setRadius(10);
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
            this.playerTurnIndicator.setFill(Color.valueOf("#000000"));
        }
    }

    public void addEventHandlersForCell(Polygon t) {
        t.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(turnToPlay) {
                    BizingoCell c = bizingoBoard.getCellMap().get(t);
                    if (c.getContent() != CellContent.EMPTY) {
                        if (c.getColor() == playerColor && selectedCell == null) {
                            highlightCell(t);
                        }
                    }
                    else{
                        if(selectedCell != null){
                            if(selectedCell.getNeighboursSameColor().contains(c)){
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
                if(!t.equals(bizingoBoard.getCellMap().getKey(selectedCell))){
                    force = true;
                }
                unhighlightCell(t, force);
            }
        });

        t.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(turnToPlay) {
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
                if(turnToPlay) {
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
                if(turnToPlay) {
                    Polygon t = bizingoBoard.getPieceMap().getKey(piece);
                    BizingoCell c = bizingoBoard.getCellMap().get(t);
                    handlePieceClicked(c);
                }
            }
        });
    }

    private void handlePieceClicked(BizingoCell c){
        if (c.getContent() != CellContent.EMPTY) {
            if (c.getColor() == playerColor) {
                if(selectedCell != null){
                    if(!selectedCell.equals(c)){
                        Polygon tOld = bizingoBoard.getCellMap().getKey(selectedCell);
                        selectedCell = null;
                        unhighlightCell(tOld, false);
                        selectedCell = c;
                        highlightCell(bizingoBoard.getCellMap().getKey(c));
                    }
                    else{
                        Polygon tOld = bizingoBoard.getCellMap().getKey(selectedCell);
                        selectedCell = null;
                        unhighlightCell(tOld, false);
                    }
                }
                else {
                    selectedCell = c;
                }
            }
        } else {
            if (selectedCell != null) { //movendo peca
                if (selectedCell.getNeighboursSameColor().contains(c)) {
                    movePieceOnBoard(selectedCell, c);
                    Polygon tOld = bizingoBoard.getCellMap().getKey(selectedCell);
                    unhighlightCell(tOld, true);
                    selectedCell = null;

                    //remover para a versao final
                    if(playerColor == CellColor.DARK){
                        setPlayerColor(CellColor.LIGHT);
                    }
                    else{
                        setPlayerColor(CellColor.DARK);
                    }
                    //setTurnToPlay(false);
                }
            }
        }
    }

    private void movePieceOnBoard(BizingoCell cellSource, BizingoCell cellDest) {
        Polygon old = bizingoBoard.getCellMap().getKey(cellSource);
        Circle piece = bizingoBoard.getPieceMap().get(old);
        Polygon tSource = bizingoBoard.getCellMap().getKey(cellDest);
        Double newX = tSource.getPoints().get(0);
        Double newY = tSource.getPoints().get(1);
        piece.setLayoutX(newX);
        if (cellDest.getColor() == CellColor.DARK) {
            piece.setLayoutY(newY + 30);
            piece.toFront();
        } else {
            piece.setLayoutY(newY + - 30);
            piece.toFront();
        }
        bizingoBoard.moveCellPiece(cellSource, cellDest);
    }

    private void highlightCell(Polygon t){
        t.setEffect(glowEffect);
    }

    private void unhighlightCell(Polygon t, boolean force){
        if(selectedCell == null || force){
            t.setEffect(null);
        }
    }

    private void highlightPossibleDestCell(Polygon t){
        ColorAdjust ca = new ColorAdjust();
        ca.setBrightness(-0.5);
        t.setEffect(ca);
    }

}