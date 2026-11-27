package onestep.service;

import onestep.model.Goal;
import onestep.model.Status;

import java.util.List;

/**
 * Aturan 3: Menghargai urutan target yang diatur user (tombol Naik/Turun).
 *
 * Target paling atas yang BELUM selesai bernilai 100, berikutnya 80, 60, dst (minimal 20).
 * Urutan dihitung dari posisi target di daftar tugas (daftar sudah diurutkan dari database).
 */
public class OrderRule extends AturanSaran {

    public OrderRule(double bobot) {
        super(bobot);
    }

    // Menghitung ada berapa target BELUM selesai yang berada di atas target ini (0 = paling atas)
    private int urutanAktif(Goal g, KonteksSaran k) {
        int urutan = 0;
        List<Goal> goals = k.getTask().getGoals();
        for (Goal lain : goals) {
            if (lain.getId() == g.getId()) {
                return urutan;
            }
            if (lain.getStatus() != Status.SELESAI) {
                urutan++;
            }
        }
        return urutan;
    }

    @Override
    public double nilai(Goal g, KonteksSaran k) {
        return Math.max(20.0, 100.0 - 20.0 * urutanAktif(g, k));
    }

    @Override
    public String alasan(Goal g, KonteksSaran k) {
        int urutan = urutanAktif(g, k);
        if (urutan == 0) {
            return "Ini target teratas di urutan yang kamu atur.";
        }
        return "Ini target ke-" + (urutan + 1) + " yang belum selesai di urutan kamu.";
    }
}