package bizingo.commons;

public enum CellColor {
    LIGHT("LIGHT"),
    DARK("DARK");

    private String color;

    CellColor(String value){
        this.color = value;
    }

    public String getValue() {
        return color;
    }

    public void setValue(String value){
        this.color = value;
    }
}
