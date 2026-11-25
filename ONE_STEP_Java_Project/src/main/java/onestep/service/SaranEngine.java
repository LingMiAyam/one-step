package onestep.service;

import onestep.model.Energi;
import onestep.model.Goal;
import onestep.model.Status;
import onestep.model.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Mesin Saran yang menerapkan Polymorphism:
 * Memanggil aturan.skorTertimbang() tanpa perlu tahu jenis aturan konkretnya.
 */
public class SaranEngine {
    private final List<AturanSaran> daftarAturan;

    public SaranEngine() {
        this.daftarAturan = new ArrayList<>();
        // Bobot 0.5 Urgensi + 0.5 Kecocokan Energi
        this.daftarAturan.add(new UrgencyRule(0.5));
        this.daftarAturan.add(new EnergyRule(0.5));
    }

    public List<HasilSaran> hitungSaran(List<Task> tasks, Energi energiHariIni) {
        List<HasilSaran> hasil = new ArrayList<>();
        LocalDate hariIni = LocalDate.now();

        for (Task task : tasks) {
            KonteksSaran konteks = new KonteksSaran(task, energiHariIni, hariIni);
            for (Goal goal : task.getGoals()) {
                // Hanya hitung target yang belum selesai
                if (goal.getStatus() == Status.SELESAI) {
                    continue;
                }

                double totalSkor = 0.0;
                AturanSaran aturanDominan = null;
                double nilaiTertinggi = -1.0;

                for (AturanSaran aturan : daftarAturan) {
                    double skor = aturan.skorTertimbang(goal, konteks);
                    totalSkor += skor;

                    double nilaiAturan = aturan.nilai(goal, konteks);
                    if (nilaiAturan > nilaiTertinggi) {
                        nilaiTertinggi = nilaiAturan;
                        aturanDominan = aturan;
                    }
                }

                String alasan = (aturanDominan != null) ? aturanDominan.alasan(goal, konteks) : "";
                hasil.add(new HasilSaran(task, goal, totalSkor, alasan));
            }
        }

        // Urutkan dari skor tertinggi ke terendah
        hasil.sort(Comparator.comparingDouble(HasilSaran::getTotalSkor).reversed());
        return hasil;
    }
}
