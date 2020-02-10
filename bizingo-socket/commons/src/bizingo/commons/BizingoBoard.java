package bizingo.commons;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.ArrayList;

public class BizingoBoard {
    private ArrayList<BizingoCell> cells;
    private BidiMap<Polygon, BizingoCell> cellMap;
    private BidiMap<Polygon, Circle> pieceMap;

    public BizingoBoard(){
        cells = new ArrayList<>();
        cellMap = new DualHashBidiMap<>();
        pieceMap = new DualHashBidiMap<>();
    }

    public void addCell(BizingoCell cell){
        cells.add(cell);
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
