package onestep.model;

/**
 * Enum ukuran target beserta bobot poinnya.
 * S = 1 poin, M = 2 poin, L = 3 poin.
 */
public enum Ukuran {
    S("S (Kecil)", 1),
    M("M (Sedang)", 2),
    L("L (Besar)", 3);

    private final String label;
    private final int poin;

    Ukuran(String label, int poin) {
        this.label = label;
        this.poin = poin;
    }

    public String getLabel() {
        return label;
    }

    public int getPoin() {
        return poin;
    }

    @Override
    public String toString() {
        return label;
    }
}
