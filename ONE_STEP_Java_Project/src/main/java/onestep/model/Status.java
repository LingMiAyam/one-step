package onestep.model;

/**
 * Enum status pengerjaan target.
 */
public enum Status {
    BELUM_DIMULAI("Belum Dimulai"),
    SEDANG_DIKERJAKAN("Sedang Dikerjakan"),
    SELESAI("Selesai");

    private final String label;

    Status(String label) {
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
