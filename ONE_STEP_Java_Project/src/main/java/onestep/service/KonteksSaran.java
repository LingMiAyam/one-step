package onestep.service;

import onestep.model.Energi;
import onestep.model.Task;
import java.time.LocalDate;

/**
 * Menyimpan konteks saat Mesin Saran menghitung skor sebuah target.
 */
public class KonteksSaran {
    private final Task task;
    private final Energi energiHariIni;
    private final LocalDate hariIni;

    public KonteksSaran(Task task, Energi energiHariIni, LocalDate hariIni) {
        this.task = task;
        this.energiHariIni = energiHariIni;
        this.hariIni = hariIni;
    }

    public Task getTask() { return task; }
    public Energi getEnergiHariIni() { return energiHariIni; }
    public LocalDate getHariIni() { return hariIni; }
}
