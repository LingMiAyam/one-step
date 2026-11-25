package onestep.model;

import onestep.exception.ValidationException;
import java.time.LocalDateTime;

/**
 * Model untuk satu target kecil di dalam sebuah Task.
 * Menerapkan Encapsulation (field private + validasi).
 */
public class Goal {
    private int id;
    private int taskId;
    private String description;
    private Ukuran ukuran;
    private Status status;
    private int position;
    private LocalDateTime updatedAt;
    private LocalDateTime finishedAt;

    public Goal(int id, int taskId, String description, Ukuran ukuran, Status status,
                int position, LocalDateTime updatedAt, LocalDateTime finishedAt) {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Nama target tidak boleh kosong.");
        }
        if (ukuran == null) {
            throw new ValidationException("Ukuran target harus dipilih.");
        }
        this.id = id;
        this.taskId = taskId;
        this.description = description.trim();
        this.ukuran = ukuran;
        this.status = (status == null) ? Status.BELUM_DIMULAI : status;
        this.position = position;
        this.updatedAt = (updatedAt == null) ? LocalDateTime.now() : updatedAt;
        this.finishedAt = finishedAt;
    }

    public Goal(int taskId, String description, Ukuran ukuran, int position) {
        this(0, taskId, description, ukuran, Status.BELUM_DIMULAI, position, LocalDateTime.now(), null);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Nama target tidak boleh kosong.");
        }
        this.description = description.trim();
    }

    public Ukuran getUkuran() { return ukuran; }
    public void setUkuran(Ukuran ukuran) { this.ukuran = ukuran; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        if (status == Status.SELESAI) {
            this.finishedAt = LocalDateTime.now();
        } else {
            this.finishedAt = null;
        }
    }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
}
