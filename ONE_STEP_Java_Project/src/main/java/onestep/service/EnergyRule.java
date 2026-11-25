package onestep.service;

import onestep.model.Energi;
import onestep.model.Goal;
import onestep.model.Ukuran;

/**
 * Aturan 2: Menilai kecocokan ukuran target (S/M/L) dengan energi hari ini.
 */
public class EnergyRule extends AturanSaran {

    public EnergyRule(double bobot) {
        super(bobot);
    }

    @Override
    public double nilai(Goal g, KonteksSaran k) {
        Energi energi = k.getEnergiHariIni();
        Ukuran ukuran = g.getUkuran();

        if (energi == Energi.RENDAH) {
            if (ukuran == Ukuran.S) return 100.0;
            if (ukuran == Ukuran.M) return 60.0;
            return 20.0;
        } else if (energi == Energi.SEDANG) {
            if (ukuran == Ukuran.S) return 70.0;
            if (ukuran == Ukuran.M) return 100.0;
            return 60.0;
        } else { // Energi.TINGGI
            if (ukuran == Ukuran.S) return 50.0;
            if (ukuran == Ukuran.M) return 80.0;
            return 100.0;
        }
    }

    @Override
    public String alasan(Goal g, KonteksSaran k) {
        Energi energi = k.getEnergiHariIni();
        Ukuran ukuran = g.getUkuran();
        if (energi == Energi.RENDAH && ukuran == Ukuran.S) {
            return "Ukurannya kecil (S), pas dikerjakan saat energi rendah.";
        }
        if (energi == Energi.SEDANG && ukuran == Ukuran.M) {
            return "Ukurannya sedang (M), seimbang untuk energi sedang.";
        }
        if (energi == Energi.TINGGI && ukuran == Ukuran.L) {
            return "Ukurannya besar (L), mumpung energimu sedang tinggi.";
        }
        return "Ukuran " + ukuran.name() + " cukup sesuai dengan energimu saat ini (" + energi.getLabel() + ").";
    }
}
