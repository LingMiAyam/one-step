package onestep.model;

import onestep.exception.ValidationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model untuk Tugas utama yang memiliki banyak Goal (target).
 */
public class Task {
    private int id;
    private String name;
    private String description;
    private LocalDate deadline; // Boleh null (tanpa deadline)
    private LocalDateTime createdAt;
    private List<Goal> goals;

    public Task(int id, String name, String description, LocalDate deadline, LocalDateTime createdAt) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nama tugas tidak boleh kosong.");
        }
        this.id = id;
        this.name = name.trim();
        this.description = (description == null) ? "" : description.trim();
        this.deadline = deadline;
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
        this.goals = new ArrayList<>();
    }

    public Task(String name, String description, LocalDate deadline) {
        this(0, name, description, deadline, LocalDateTime.now());
    }

    // --- Perhitungan Progres Berbobot ---
    public int getTotalPoin() {
        int total = 0;
        for (Goal g : goals) {
            total += g.getUkuran().getPoin();
        }
        return total;
    }

    public int getPoinSelesai() {
        int selesai = 0;
        for (Goal g : goals) {
            if (g.getStatus() == Status.SELESAI) {
                selesai += g.getUkuran().getPoin();
            }
        }
        return selesai;
    }

    public int getProgresPersen() {
        int total = getTotalPoin();
        if (total == 0) return 0;
        return (int) Math.round((getPoinSelesai() * 100.0) / total);
    }

    // --- Getter & Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nama tugas tidak boleh kosong.");
        }
        this.name = name.trim();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = (description == null) ? "" : description.trim();
    }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<Goal> getGoals() { return goals; }
    public void setGoals(List<Goal> goals) {
        this.goals = (goals == null) ? new ArrayList<>() : goals;
    }
}
