package onestep.service;

import onestep.model.Goal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Aturan 1: Menilai target berdasarkan kedekatan deadline tugas.
 */
public class UrgencyRule extends AturanSaran {

    public UrgencyRule(double bobot) {
        super(bobot);
    }

    @Override
    public double nilai(Goal g, KonteksSaran k) {
        LocalDate deadline = k.getTask().getDeadline();
        if (deadline == null) {
            return 20.0;
        }
        long sisaHari = ChronoUnit.DAYS.between(k.getHariIni(), deadline);
        if (sisaHari <= 1) return 100.0;
        if (sisaHari <= 3) return 70.0;
        if (sisaHari <= 7) return 40.0;
        return 10.0;
    }

    @Override
    public String alasan(Goal g, KonteksSaran k) {
        LocalDate deadline = k.getTask().getDeadline();
        if (deadline == null) {
            return "Tugas belum punya deadline, tetap bagus dicicil sekarang.";
        }
        long sisaHari = ChronoUnit.DAYS.between(k.getHariIni(), deadline);
        if (sisaHari < 0) return "Deadline sudah terlewat, perlu segera diselesaikan.";
        if (sisaHari <= 1) return "Deadline sangat mendesak (<= 1 hari lagi).";
        if (sisaHari <= 3) return "Deadline sudah dekat (" + sisaHari + " hari lagi).";
        if (sisaHari <= 7) return "Deadline dalam minggu ini (" + sisaHari + " hari lagi).";
        return "Deadline masih cukup panjang (" + sisaHari + " hari lagi).";
    }
}
