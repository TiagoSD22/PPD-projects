package com.bizingoclient.app.mainGame.game;


import bizingo.commons.BizingoBoard;
import bizingo.commons.BizingoCell;
import bizingo.commons.CellColor;
import bizingo.commons.CellContent;
import com.bizingoclient.app.mainGame.MainGameController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;


public class GameController {

    @FXML
    public AnchorPane root;
    @FXML
    private StackPane boardBase;
    @FXML
    private ImageView playerColorIndicator;
    @FXML
    private Text playerTurnIndicator;

    private MainGameController main;
    private BizingoBoard bizingoBoard;

    private CellColor playerColor;
    private boolean turnToPlay;


    private BizingoCell selectedCell;


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

        setTurnToPlay(true);
        setPlayerColor(CellColor.DARK);
    }

    private void drawBoard() {
        Group board = new Group();
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
                    t.setFill(Color.rgb(19, 193, 150));

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
                    t.setFill(Color.WHITE);

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

                    Circle finalPiece = piece;
                    piece.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            Polygon t = bizingoBoard.getPieceMap().getKey(finalPiece);
                            BizingoCell c = bizingoBoard.getCellMap().get(t);
                            if (c.getColor() == playerColor && selectedCell == null) {
                                t.setStroke(Color.rgb(81, 113, 165));
                            }
                        }
                    });
                }

                currentX0 += 25.0;

            }
        }

        boardBase.getChildren().add(board);
        board.setTranslateX(0);
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
        } else {
            this.playerTurnIndicator.setText("VEZ DO OPONENTE JOGAR");
        }
    }

    public void addEventHandlersForCell(Polygon t) {
        t.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                BizingoCell c = bizingoBoard.getCellMap().get(t);
                if (c.getContent() != CellContent.EMPTY) {
                    if (c.getColor() == playerColor && selectedCell == null) {
                        t.setStroke(Color.rgb(81, 113, 165));
                    }
                }
            }
        });

        t.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                t.setStroke(Color.BLACK);
            }
        });

        t.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                BizingoCell c = bizingoBoard.getCellMap().get(t);
                if (c.getContent() != CellContent.EMPTY) {
                    if (c.getColor() == playerColor) {
                        if (selectedCell == null) {
                            selectedCell = c;
                        } else {
                            //movendo peca
                        }
                    }
                }
            }
        });
    }

}