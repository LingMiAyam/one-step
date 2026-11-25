package onestep.service;

import onestep.model.Goal;

/**
 * Abstract class untuk aturan penilaian saran (Abstraction & Inheritance).
 */
public abstract class AturanSaran {
    private final double bobot;

    public AturanSaran(double bobot) {
        this.bobot = bobot;
    }

    public double getBobot() {
        return bobot;
    }

    // Rentang nilai 0 sampai 100
    public abstract double nilai(Goal g, KonteksSaran k);

    // Penjelasan singkat kenapa target ini mendapat nilai tersebut
    public abstract String alasan(Goal g, KonteksSaran k);

    public double skorTertimbang(Goal g, KonteksSaran k) {
        return bobot * nilai(g, k);
    }
}
