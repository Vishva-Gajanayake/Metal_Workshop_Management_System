package lk.ijse.metalworkshopmanagementsystem.model;

public enum UnitOfMaterial {
    
    METER("m"),
    FEET("ft"),
    LENGTH_6M("length (6 m)"),
    PIECE("pcs"),
    SHEET("sheet"),
    SET("set"),
    KILOGRAM("kg"),
    BUNDLE("bundle"),
    ROLL("roll"),
    LITRE("litre"),
    TIN("tin"),
    BOX("box"),
    PAIR("pair");

    private final String label;

    UnitOfMaterial(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
