package onestep.ui;

import onestep.exception.DataAccessException;
import onestep.model.Goal;
import onestep.model.Status;
import onestep.model.Task;
import onestep.repository.TaskRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Layar 2: Detail Tugas.
 * Mengelola daftar target di dalam JTable, mengubah status, urutan, dan melihat progres berbobot.
 */
public class DetailPanel extends JPanel {
    private final MainFrame mainFrame;
    private final TaskRepository taskRepo;

    private Task currentTask;

    private final JLabel lblJudul = new JLabel("-");
    private final JLabel lblInfo = new JLabel("-");
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    private final DefaultTableModel tableModel;
    private final JTable tableTarget;

    public DetailPanel(MainFrame mainFrame, TaskRepository taskRepo) {
        this.mainFrame = mainFrame;
        this.taskRepo = taskRepo;

        // JTable dibuat non-editable agar data selalu konsisten lewat tombol/dialog
        String[] kolom = {"No", "Target", "Ukuran", "Poin", "Status"};
        this.tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tableTarget = new JTable(tableModel);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UiTheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // --- NORTH: Navigasi + Judul Tugas + Progress Bar ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setOpaque(false);

        JButton btnKembali = UiTheme.createSecondaryButton("<- Kembali ke Dashboard");
        btnKembali.addActionListener(e -> mainFrame.bukaDashboard());

        JPanel taskActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        taskActions.setOpaque(false);
        JButton btnEditTugas = UiTheme.createSecondaryButton("Edit Tugas");
        JButton btnHapusTugas = UiTheme.createSecondaryButton("Hapus Tugas");
        btnHapusTugas.setForeground(UiTheme.DANGER);

        btnEditTugas.addActionListener(e -> editTugas());
        btnHapusTugas.addActionListener(e -> hapusTugas());

        taskActions.add(btnEditTugas);
        taskActions.add(btnHapusTugas);

        navBar.add(btnKembali, BorderLayout.WEST);
        navBar.add(taskActions, BorderLayout.EAST);

        lblJudul.setFont(UiTheme.FONT_TITLE);
        lblJudul.setForeground(UiTheme.TEAL_DARK);
        lblInfo.setFont(UiTheme.FONT_SMALL);
        lblInfo.setForeground(UiTheme.TEXT_MUTED);

        progressBar.setStringPainted(true);
        progressBar.setForeground(UiTheme.TEAL_MAIN);
        progressBar.setPreferredSize(new Dimension(0, 26));

        JPanel infoBox = new JPanel(new BorderLayout(0, 4));
        infoBox.setOpaque(false);
        infoBox.add(lblJudul, BorderLayout.NORTH);
        infoBox.add(lblInfo, BorderLayout.CENTER);
        infoBox.add(progressBar, BorderLayout.SOUTH);

        topPanel.add(navBar);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(infoBox);

        add(topPanel, BorderLayout.NORTH);

        // --- CENTER: JTable Target ---
        tableTarget.setFont(UiTheme.FONT_BODY);
        tableTarget.setRowHeight(28);
        tableTarget.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableTarget.getColumnModel().getColumn(0).setMaxWidth(45);
        tableTarget.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableTarget.getColumnModel().getColumn(3).setMaxWidth(55);
        tableTarget.getColumnModel().getColumn(4).setPreferredWidth(140);

        JScrollPane scrollPane = new JScrollPane(tableTarget);
        add(scrollPane, BorderLayout.CENTER);

        // --- SOUTH: Tombol Kelola Target & Ubah Status ---
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setOpaque(false);

        JPanel leftBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftBtns.setOpaque(false);
        JButton btnTambahTarget = UiTheme.createPrimaryButton("+ Target");
        JButton btnHapusTarget = UiTheme.createSecondaryButton("Hapus");
        JButton btnNaik = UiTheme.createSecondaryButton("Naik");
        JButton btnTurun = UiTheme.createSecondaryButton("Turun");

        btnTambahTarget.addActionListener(e -> tambahTarget());
        btnHapusTarget.addActionListener(e -> hapusTarget());
        btnNaik.addActionListener(e -> geserTarget(-1));
        btnTurun.addActionListener(e -> geserTarget(1));

        leftBtns.add(btnTambahTarget);
        leftBtns.add(btnHapusTarget);
        leftBtns.add(btnNaik);
        leftBtns.add(btnTurun);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightBtns.setOpaque(false);
        JButton btnMulai = UiTheme.createSecondaryButton("Mulai Kerjakan");
        JButton btnSelesai = UiTheme.createPrimaryButton("Tandai Selesai");

        btnMulai.addActionListener(e -> ubahStatusTarget(Status.SEDANG_DIKERJAKAN));
        btnSelesai.addActionListener(e -> ubahStatusTarget(Status.SELESAI));

        rightBtns.add(btnMulai);
        rightBtns.add(btnSelesai);

        bottomPanel.add(leftBtns, BorderLayout.WEST);
        bottomPanel.add(rightBtns, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadTask(int taskId) {
        try {
            currentTask = taskRepo.findTaskById(taskId);
            if (currentTask == null) {
                mainFrame.bukaDashboard();
                return;
            }
            refreshView();
        } catch (DataAccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshView() {
        lblJudul.setText(currentTask.getName());
        String dl = (currentTask.getDeadline() != null) ? "Deadline: " + currentTask.getDeadline() : "Tanpa deadline";
        String desc = currentTask.getDescription().isEmpty() ? "" : "  |  " + currentTask.getDescription();
        lblInfo.setText(dl + desc);

        int persen = currentTask.getProgresPersen();
        progressBar.setValue(persen);
        progressBar.setString(String.format("Progres Berbobot: %d%% (%d / %d poin)",
                persen, currentTask.getPoinSelesai(), currentTask.getTotalPoin()));

        tableModel.setRowCount(0);
        List<Goal> goals = currentTask.getGoals();
        for (int i = 0; i < goals.size(); i++) {
            Goal g = goals.get(i);
            tableModel.addRow(new Object[]{
                    i + 1,
                    g.getDescription(),
                    g.getUkuran().getLabel(),
                    g.getUkuran().getPoin(),
                    g.getStatus().getLabel()
            });
        }
    }

    private Goal getSelectedGoal() {
        int row = tableTarget.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu target di tabel terlebih dahulu!",
                    "Peringatan", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return currentTask.getGoals().get(row);
    }

    private void tambahTarget() {
        int nextPos = currentTask.getGoals().size() + 1;
        GoalDialog dialog = new GoalDialog(mainFrame, currentTask.getId(), nextPos);
        dialog.setVisible(true);
        Goal baru = dialog.getResultGoal();
        if (baru != null) {
            try {
                taskRepo.saveGoal(baru);
                loadTask(currentTask.getId());
            } catch (DataAccessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusTarget() {
        Goal selected = getSelectedGoal();
        if (selected == null) return;

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin hapus target \"" + selected.getDescription() + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                taskRepo.deleteGoal(selected.getId());
                loadTask(currentTask.getId());
            } catch (DataAccessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ubahStatusTarget(Status statusBaru) {
        Goal selected = getSelectedGoal();
        if (selected == null) return;

        try {
            taskRepo.updateGoalStatus(selected.getId(), statusBaru);
            loadTask(currentTask.getId());
        } catch (DataAccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void geserTarget(int arah) {
        int row = tableTarget.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih target yang ingin dipindahkan!");
            return;
        }
        int targetRow = row + arah;
        List<Goal> goals = currentTask.getGoals();
        if (targetRow < 0 || targetRow >= goals.size()) {
            return;
        }
        try {
            taskRepo.swapGoalPositions(goals.get(row), goals.get(targetRow));
            loadTask(currentTask.getId());
            tableTarget.setRowSelectionInterval(targetRow, targetRow);
        } catch (DataAccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editTugas() {
        TaskDialog dialog = new TaskDialog(mainFrame, currentTask);
        dialog.setVisible(true);
        Task updated = dialog.getResultTask();
        if (updated != null) {
            try {
                taskRepo.saveTask(updated);
                loadTask(currentTask.getId());
            } catch (DataAccessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusTugas() {
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin hapus tugas \"" + currentTask.getName() + "\" beserta semua targetnya?",
                "Konfirmasi Hapus Tugas", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                // Hapus di SQLite dulu (ON DELETE CASCADE otomatis hapus goals)
                taskRepo.deleteTask(currentTask.getId());
                mainFrame.bukaDashboard();
            } catch (DataAccessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
