package bizingo.commons;

public enum CellContent {
    EMPTY("EMPTY"),
    REGULAR_PIECE("REGULAR_PIECE"),
    CAPTAIN_PIECE("CAPTAIN_PIECE");

    private String value;

    CellContent(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }
}
