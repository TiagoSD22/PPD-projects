package bizingo.commons;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import java.util.ArrayList;
import java.util.Arrays;

public class BizingoBoard {
    private ArrayList<BizingoCell> cells;
    private BidiMap<Polygon, BizingoCell> cellMap;
    private BidiMap<Polygon, Circle> pieceMap;

    private BidiMap<String, BizingoCell> positionCellMap;

    public BizingoBoard(){
        cells = new ArrayList<>();
        cellMap = new DualHashBidiMap<>();
        pieceMap = new DualHashBidiMap<>();
        positionCellMap = new DualHashBidiMap<>();
    }

    public void addCell(BizingoCell cell){
        cells.add(cell);
        String row = String.valueOf(cell.getRow());
        String col = String.valueOf(cell.getColumn());
        String key = row + "," + col;
        positionCellMap.put(key, cell);
    }

    public void moveCellPiece(BizingoCell source, BizingoCell dest){
        CellContent sourceContent = source.getContent();
        dest.setContent(sourceContent);
        source.setContent(CellContent.EMPTY);

        Polygon tSource = cellMap.getKey(source);
        Polygon tDest = cellMap.getKey(dest);

        pieceMap.put(tDest, pieceMap.get(tSource));
        pieceMap.remove(tSource);
    }

    public void moveCellPiece(String source, String dest){
        BizingoCell sourceCell = positionCellMap.get(source);
        BizingoCell destCell = positionCellMap.get(dest);
        moveCellPiece(sourceCell, destCell);
    }

    private void addSameColorNeighbourhood(BizingoCell cell){
        int previousRowLeftDiff = -2;
        int previousRowRightDiff = 0;
        int nextRowLeftDiff = 0;
        int nextRowRightDiff = 2;
        if(cell.getRow() >= 8){
            nextRowLeftDiff = -2 + (9 - cell.getRow());
            nextRowRightDiff = 9 - cell.getRow();
            if(cell.getRow() > 8) {
                previousRowLeftDiff = cell.getRow() - 10;
                previousRowRightDiff = 2 + (cell.getRow() - 10);
            }
        }
        ArrayList<String> neighboursCoords = new ArrayList<>(Arrays.asList(
                (cell.getRow() - 1) + "," + (cell.getColumn() + previousRowLeftDiff),
                (cell.getRow() - 1) + "," + (cell.getColumn() + previousRowRightDiff),
                (cell.getRow()) + "," + (cell.getColumn() - 2),
                (cell.getRow()) + "," + (cell.getColumn() + 2),
                (cell.getRow() + 1) + "," + (cell.getColumn() + nextRowLeftDiff),
                (cell.getRow() + 1) + "," + (cell.getColumn() + nextRowRightDiff)
        ));

        ArrayList<BizingoCell> neighbourhood = new ArrayList<>();

        for(String coord : neighboursCoords){
            BizingoCell neighbour = positionCellMap.get(coord);
            if(neighbour != null){  // coordenada existe
                neighbourhood.add(neighbour);
            }
        }

        cell.setNeighboursSameColor(neighbourhood);
    }

    private void addOppositeColorNeighbourhood(BizingoCell cell){
        int nextRowDiff = 1;
        int previousRowDiff = -1;
        int otherRow = cell.getColor() == CellColor.DARK? 1 : -1;
        if(cell.getRow() >= 8){
            nextRowDiff = 8 - cell.getRow();
        }
        if(cell.getRow() > 8){
            previousRowDiff = cell.getRow() - 9;
        }
        ArrayList<String> neighboursCoords = new ArrayList<>(Arrays.asList(
                (cell.getRow()) + "," + (cell.getColumn() - 1),
                (cell.getRow()) + "," + (cell.getColumn() + 1),
                (cell.getRow() + otherRow) + "," + (cell.getColumn() + (cell.getColor() == CellColor.DARK?
                        nextRowDiff: previousRowDiff))
        ));

        ArrayList<BizingoCell> neighbourhood = new ArrayList<>();

        for(String coord : neighboursCoords){
            BizingoCell neighbour = positionCellMap.get(coord);
            if(neighbour != null){  // coordenada existe
                neighbourhood.add(neighbour);
            }
        }

        cell.setNeighboursOppositeColor(neighbourhood);
    }

    public void addNeighbourhood(){
        this.cells.forEach(cell -> {
            addSameColorNeighbourhood(cell);
            addOppositeColorNeighbourhood(cell);
        });
    }

    public boolean isSurrounded(BizingoCell cell){
        if(cell.getNeighboursOppositeColor().size() == 2){ //casa de borda
            int numberOfCaptains = 0;
            int numberOfEnemies = 0;
            for(BizingoCell neighbour : cell.getNeighboursOppositeColor()){
                if(neighbour.getContent() != CellContent.EMPTY){
                    numberOfEnemies++;
                    if(neighbour.getContent() == CellContent.CAPTAIN_PIECE){
                        numberOfCaptains++;
                    }
                }
            }
            if(numberOfEnemies == 2 && numberOfCaptains >= 1){
                cell.setContent(CellContent.EMPTY);
                return true;
            }
        }
        else{
            int numberOfEnemies = 0;
            int numberOfCaptains = 0;
            for(BizingoCell neighbour : cell.getNeighboursOppositeColor()){
                if(neighbour.getContent() != CellContent.EMPTY){
                    numberOfEnemies++;
                    if(neighbour.getContent() == CellContent.CAPTAIN_PIECE){
                        numberOfCaptains++;
                    }
                }
            }
            if(numberOfEnemies == 3 && (cell.getContent() != CellContent.CAPTAIN_PIECE || numberOfCaptains >= 1)){
                cell.setContent(CellContent.EMPTY);
                return true;
            }
        }
        return false;
    }

    public ArrayList<BizingoCell> cellHasSurrounded(BizingoCell cell){
        ArrayList<BizingoCell> surrounded = new ArrayList<>();
        for(BizingoCell neighbour : cell.getNeighboursOppositeColor()){
            if(neighbour.getContent() != CellContent.EMPTY){
                if(isSurrounded(neighbour)){
                    neighbour.setContent(CellContent.EMPTY);
                    surrounded.add(neighbour);
                }
            }
        }
        return surrounded;
    }

    public BidiMap<String, BizingoCell> getPositionCellMap() {
        return positionCellMap;
    }

    public ArrayList<BizingoCell> getCells() {
        return cells;
    }

    public void putCellOnMap(Polygon t, BizingoCell cell){
        this.cellMap.put(t, cell);
    }

    public BidiMap<Polygon, BizingoCell> getCellMap(){
        return this.cellMap;
    }

    public void putPieceOnMap(Polygon t, Circle c){
        this.pieceMap.put(t, c);
    }

    public BidiMap<Polygon, Circle> getPieceMap(){
        return this.pieceMap;
    }
}
