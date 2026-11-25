package onestep.repository;

import onestep.exception.DataAccessException;
import onestep.model.Goal;
import onestep.model.Status;
import onestep.model.Task;
import onestep.model.Ukuran;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi TaskRepository menggunakan database SQLite.
 * Selalu mengaktifkan PRAGMA foreign_keys = ON dan memakai PreparedStatement.
 */
public class SqliteTaskRepository implements TaskRepository {

    private final String dbUrl;

    public SqliteTaskRepository(String dbFileName) {
        this.dbUrl = "jdbc:sqlite:" + dbFileName;
    }

    private Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(dbUrl);
        // WAJIB di SQLite agar ON DELETE CASCADE berfungsi
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    @Override
    public void initDatabase() {
        String sqlTasks = "CREATE TABLE IF NOT EXISTS tasks ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "description TEXT, "
                + "deadline TEXT, "
                + "created_at TEXT NOT NULL"
                + ");";

        String sqlGoals = "CREATE TABLE IF NOT EXISTS goals ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "task_id INTEGER NOT NULL, "
                + "description TEXT NOT NULL, "
                + "ukuran TEXT NOT NULL, "
                + "status TEXT NOT NULL, "
                + "position INTEGER NOT NULL, "
                + "updated_at TEXT NOT NULL, "
                + "finished_at TEXT, "
                + "FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlTasks);
            stmt.execute(sqlGoals);
            seedSampleDataIfEmpty();
        } catch (SQLException e) {
            throw new DataAccessException("Gagal menginisialisasi database SQLite.", e);
        }
    }

    /**
     * Mengisi data contoh otomatis saat aplikasi baru pertama kali dijalankan
     * agar langsung siap didemokan.
     */
    private void seedSampleDataIfEmpty() {
        if (!findAllTasksWithGoals().isEmpty()) {
            return;
        }
        Task t1 = saveTask(new Task(
                "Laporan Praktikum PBO",
                "Laporan akhir praktikum Java GUI & OOP",
                LocalDate.now().plusDays(3)
        ));
        saveGoal(new Goal(t1.getId(), "Rapikan daftar pustaka", Ukuran.S, 1));
        saveGoal(new Goal(t1.getId(), "Buat tabel hasil uji coba", Ukuran.M, 2));
        saveGoal(new Goal(t1.getId(), "Tulis pembahasan analisis OOP", Ukuran.L, 3));

        Task t2 = saveTask(new Task(
                "Presentasi Final Project",
                "Slide PPT dan latihan demo aplikasi",
                LocalDate.now().plusDays(1)
        ));
        saveGoal(new Goal(t2.getId(), "Cek link GitHub di makalah", Ukuran.S, 1));
        saveGoal(new Goal(t2.getId(), "Susun slide masalah & solusi", Ukuran.M, 2));
    }

    @Override
    public List<Task> findAllTasksWithGoals() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, name, description, deadline, created_at FROM tasks ORDER BY id DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Task task = mapTask(rs);
                task.setGoals(findGoalsByTaskId(conn, task.getId()));
                tasks.add(task);
            }
            return tasks;
        } catch (SQLException e) {
            throw new DataAccessException("Gagal memuat daftar tugas.", e);
        }
    }

    @Override
    public Task findTaskById(int taskId) {
        String sql = "SELECT id, name, description, deadline, created_at FROM tasks WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Task task = mapTask(rs);
                    task.setGoals(findGoalsByTaskId(conn, task.getId()));
                    return task;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Gagal memuat detail tugas.", e);
        }
    }

    private List<Goal> findGoalsByTaskId(Connection conn, int taskId) throws SQLException {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT id, task_id, description, ukuran, status, position, updated_at, finished_at "
                   + "FROM goals WHERE task_id = ? ORDER BY position ASC, id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapGoal(rs));
                }
            }
        }
        return goals;
    }

    @Override
    public Task saveTask(Task task) {
        if (task.getId() == 0) {
            String sql = "INSERT INTO tasks (name, description, deadline, created_at) VALUES (?, ?, ?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, task.getName());
                ps.setString(2, task.getDescription());
                ps.setString(3, task.getDeadline() != null ? task.getDeadline().toString() : null);
                ps.setString(4, task.getCreatedAt().toString());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        task.setId(keys.getInt(1));
                    }
                }
                return task;
            } catch (SQLException e) {
                throw new DataAccessException("Gagal menyimpan tugas baru.", e);
            }
        } else {
            String sql = "UPDATE tasks SET name = ?, description = ?, deadline = ? WHERE id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, task.getName());
                ps.setString(2, task.getDescription());
                ps.setString(3, task.getDeadline() != null ? task.getDeadline().toString() : null);
                ps.setInt(4, task.getId());
                ps.executeUpdate();
                return task;
            } catch (SQLException e) {
                throw new DataAccessException("Gagal memperbarui tugas.", e);
            }
        }
    }

    @Override
    public void deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Gagal menghapus tugas.", e);
        }
    }

    @Override
    public Goal saveGoal(Goal goal) {
        String sql = "INSERT INTO goals (task_id, description, ukuran, status, position, updated_at, finished_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, goal.getTaskId());
            ps.setString(2, goal.getDescription());
            ps.setString(3, goal.getUkuran().name());
            ps.setString(4, goal.getStatus().name());
            ps.setInt(5, goal.getPosition());
            ps.setString(6, goal.getUpdatedAt().toString());
            ps.setString(7, goal.getFinishedAt() != null ? goal.getFinishedAt().toString() : null);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    goal.setId(keys.getInt(1));
                }
            }
            return goal;
        } catch (SQLException e) {
            throw new DataAccessException("Gagal menyimpan target baru.", e);
        }
    }

    @Override
    public void updateGoalStatus(int goalId, Status status) {
        String sql = "UPDATE goals SET status = ?, updated_at = ?, finished_at = ? WHERE id = ?";
        LocalDateTime now = LocalDateTime.now();
        String finishedAt = (status == Status.SELESAI) ? now.toString() : null;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, now.toString());
            ps.setString(3, finishedAt);
            ps.setInt(4, goalId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Gagal mengubah status target.", e);
        }
    }

    @Override
    public void deleteGoal(int goalId) {
        String sql = "DELETE FROM goals WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, goalId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Gagal menghapus target.", e);
        }
    }

    @Override
    public void swapGoalPositions(Goal g1, Goal g2) {
        String sql = "UPDATE goals SET position = ? WHERE id = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, g2.getPosition());
                ps.setInt(2, g1.getId());
                ps.executeUpdate();

                ps.setInt(1, g1.getPosition());
                ps.setInt(2, g2.getId());
                ps.executeUpdate();

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Gagal mengubah urutan target.", e);
        }
    }

    private Task mapTask(ResultSet rs) throws SQLException {
        String dl = rs.getString("deadline");
        LocalDate deadline = (dl != null && !dl.isEmpty()) ? LocalDate.parse(dl) : null;
        LocalDateTime createdAt = LocalDateTime.parse(rs.getString("created_at"));
        return new Task(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                deadline,
                createdAt
        );
    }

    private Goal mapGoal(ResultSet rs) throws SQLException {
        String fin = rs.getString("finished_at");
        LocalDateTime finishedAt = (fin != null && !fin.isEmpty()) ? LocalDateTime.parse(fin) : null;
        return new Goal(
                rs.getInt("id"),
                rs.getInt("task_id"),
                rs.getString("description"),
                Ukuran.valueOf(rs.getString("ukuran")),
                Status.valueOf(rs.getString("status")),
                rs.getInt("position"),
                LocalDateTime.parse(rs.getString("updated_at")),
                finishedAt
        );
    }
}
