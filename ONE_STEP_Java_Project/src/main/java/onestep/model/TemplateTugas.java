package onestep.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Template tugas: nama + daftar target siap pakai (nama target dan ukuran S/M/L).
 * Objek ini tidak bisa diubah setelah dibuat (immutable), jadi aman dibagikan antar layar.
 */
public final class TemplateTugas {

    // Satu target di dalam template
    public static final class Target {
        private final String nama;
        private final Ukuran ukuran;

        public Target(String nama, Ukuran ukuran) {
            if (nama == null || nama.trim().isEmpty()) {
                throw new IllegalArgumentException("Nama target template tidak boleh kosong.");
            }
            if (ukuran == null) {
                throw new IllegalArgumentException("Ukuran target template tidak boleh kosong.");
            }
            this.nama = nama.trim();
            this.ukuran = ukuran;
        }

        public String getNama() { return nama; }
        public Ukuran getUkuran() { return ukuran; }
    }

    private final String nama;
    private final List<Target> targets;

    public TemplateTugas(String nama, List<Target> targets) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama template tidak boleh kosong.");
        }
        if (targets == null || targets.isEmpty()) {
            throw new IllegalArgumentException("Template harus punya minimal satu target.");
        }
        this.nama = nama.trim();
        this.targets = Collections.unmodifiableList(new ArrayList<>(targets));
    }

    public String getNama() { return nama; }

    public List<Target> getTargets() { return targets; }

    public int getJumlahTarget() { return targets.size(); }

    // Total poin semua target (S=1, M=2, L=3).
    public int getTotalPoin() {
        int total = 0;
        for (Target t : targets) {
            total += t.getUkuran().getPoin();
        }
        return total;
    }
}
