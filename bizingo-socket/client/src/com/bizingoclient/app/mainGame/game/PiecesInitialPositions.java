package com.bizingoclient.app.mainGame.game;

import bizingo.commons.CellContent;

import java.util.HashMap;
import java.util.Map;

public class PiecesInitialPositions {
    Map<String, CellContent> positionMap = new HashMap<>();

    private static PiecesInitialPositions instance = null;

    public static PiecesInitialPositions getInstance(){
        if(instance == null){
            instance = new PiecesInitialPositions();
        }
        return instance;
    }

    private PiecesInitialPositions(){
        positionMap.put("2,2", CellContent.REGULAR_PIECE);
        positionMap.put("2,4", CellContent.REGULAR_PIECE);
        positionMap.put("2,6", CellContent.REGULAR_PIECE);
        positionMap.put("3,2", CellContent.REGULAR_PIECE);
        positionMap.put("3,4", CellContent.REGULAR_PIECE);
        positionMap.put("3,6", CellContent.REGULAR_PIECE);
        positionMap.put("3,8", CellContent.REGULAR_PIECE);
        positionMap.put("4,2", CellContent.REGULAR_PIECE);
        positionMap.put("4,4", CellContent.REGULAR_PIECE);
        positionMap.put("4,6", CellContent.REGULAR_PIECE);
        positionMap.put("4,8", CellContent.REGULAR_PIECE);
        positionMap.put("4,10", CellContent.REGULAR_PIECE);
        positionMap.put("5,2", CellContent.REGULAR_PIECE);
        positionMap.put("5,4", CellContent.CAPTAIN_PIECE);
        positionMap.put("5,6", CellContent.REGULAR_PIECE);
        positionMap.put("5,8", CellContent.REGULAR_PIECE);
        positionMap.put("5,10", CellContent.CAPTAIN_PIECE);
        positionMap.put("5,12", CellContent.REGULAR_PIECE);

        positionMap.put("7,3", CellContent.REGULAR_PIECE);
        positionMap.put("7,5", CellContent.CAPTAIN_PIECE);
        positionMap.put("7,7", CellContent.REGULAR_PIECE);
        positionMap.put("7,9", CellContent.REGULAR_PIECE);
        positionMap.put("7,11", CellContent.REGULAR_PIECE);
        positionMap.put("7,13", CellContent.CAPTAIN_PIECE);
        positionMap.put("7,15", CellContent.REGULAR_PIECE);
        positionMap.put("8,5", CellContent.REGULAR_PIECE);
        positionMap.put("8,7", CellContent.REGULAR_PIECE);
        positionMap.put("8,9", CellContent.REGULAR_PIECE);
        positionMap.put("8,11", CellContent.REGULAR_PIECE);
        positionMap.put("8,13", CellContent.REGULAR_PIECE);
        positionMap.put("8,15", CellContent.REGULAR_PIECE);
        positionMap.put("9,6", CellContent.REGULAR_PIECE);
        positionMap.put("9,8", CellContent.REGULAR_PIECE);
        positionMap.put("9,10", CellContent.REGULAR_PIECE);
        positionMap.put("9,12", CellContent.REGULAR_PIECE);
        positionMap.put("9,14", CellContent.REGULAR_PIECE);
    }
}
