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
        ArrayList<String> neighboursCoords = new ArrayList<>(Arrays.asList(
                (cell.getRow() - 1) + "," + (cell.getColumn() - 2),
                (cell.getRow() - 1) + "," + (cell.getColumn()),
                (cell.getRow()) + "," + (cell.getColumn() - 2),
                (cell.getRow()) + "," + (cell.getColumn() + 2),
                (cell.getRow() + 1) + "," + (cell.getColumn()),
                (cell.getRow() + 1) + "," + (cell.getColumn() + 2)
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
        ArrayList<String> neighboursCoords = new ArrayList<>(Arrays.asList(
                (cell.getRow()) + "," + (cell.getColumn() - 1),
                (cell.getRow()) + "," + (cell.getColumn() + 1),
                (cell.getRow() + 1) + "," + (cell.getColumn() + 1)
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
}
