package onestep.model;

/**
 * Enum tingkat energi pengguna hari ini.
 */
public enum Energi {
    RENDAH("Rendah"),
    SEDANG("Sedang"),
    TINGGI("Tinggi");

    private final String label;

    Energi(String label) {
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
