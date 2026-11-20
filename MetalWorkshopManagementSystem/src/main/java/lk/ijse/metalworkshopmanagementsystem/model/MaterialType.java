package lk.ijse.metalworkshopmanagementsystem.model;

public enum MaterialType {
 
    HOLLOW_BAR("Hollow bar"),
    L_IRON("L-iron (angle)"),
    FLAT_BAR("Flat bar"),
    ROUND_BAR("Round bar"),
    SQUARE_BAR("Square bar"),
    PIPE("Pipe"),
    GI_SHEET("GI sheet"),
    ROOFING_SHEET("Roofing sheet"),
    ALUMINIUM_SECTION("Aluminium section"),
    ALUMINIUM_GUTTER("Aluminium gutter"),
    MESH_WIRE("Mesh / wire"),
    HARDWARE_FITTINGS("Hardware and fittings"),
    WELDING_ROD("Welding rod"),
    PAINT_CONSUMABLES("Paint and consumables");

    private final String label;

    MaterialType(String label) {
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
