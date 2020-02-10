package bizingo.commons;

import java.util.ArrayList;

public class BizingoCell {
    private int row;
    private int column;
    private ArrayList<BizingoCell> neighboursSameColor;
    private ArrayList<BizingoCell> neighboursOppositeColor;
    private CellColor color;
    private CellContent content;

    public BizingoCell(int row, int collumn, CellColor color, CellContent content){
        this.row = row;
        this.column = collumn;
        this.content = content;
        this.color = color;
    }

    public BizingoCell(){}

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public ArrayList<BizingoCell> getNeighboursSameColor() {
        return neighboursSameColor;
    }

    public void setNeighboursSameColor(ArrayList<BizingoCell> neighboursSameColor) {
        this.neighboursSameColor = neighboursSameColor;
    }

    public ArrayList<BizingoCell> getNeighboursOppositeColor() {
        return neighboursOppositeColor;
    }

    public void setNeighboursOppositeColor(ArrayList<BizingoCell> neighboursOppositeColor) {
        this.neighboursOppositeColor = neighboursOppositeColor;
    }

    public CellColor getColor() {
        return color;
    }

    public void setColor(CellColor color) {
        this.color = color;
    }

    public CellContent getContent() {
        return content;
    }

    public void setContent(CellContent content) {
        this.content = content;
    }
}
