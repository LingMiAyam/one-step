package onestep.service;

import onestep.model.Goal;
import onestep.model.Task;

/**
 * Menyimpan hasil perhitungan skor untuk satu kandidat target.
 */
public class HasilSaran {
    private final Task task;
    private final Goal goal;
    private final double totalSkor;
    private final String alasanUtama;

    public HasilSaran(Task task, Goal goal, double totalSkor, String alasanUtama) {
        this.task = task;
        this.goal = goal;
        this.totalSkor = totalSkor;
        this.alasanUtama = alasanUtama;
    }

    public Task getTask() { return task; }
    public Goal getGoal() { return goal; }
    public double getTotalSkor() { return totalSkor; }
    public String getAlasanUtama() { return alasanUtama; }
}
