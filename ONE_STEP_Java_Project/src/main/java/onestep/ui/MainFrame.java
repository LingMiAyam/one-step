package onestep.ui;

import onestep.repository.TaskRepository;
import onestep.service.SaranEngine;

import javax.swing.*;
import java.awt.*;

/**
 * Jendela utama (JFrame) menggunakan CardLayout untuk berpindah antara Dashboard dan Detail Tugas.
 */
public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private final DashboardPanel dashboardPanel;
    private final DetailPanel detailPanel;

    public MainFrame(TaskRepository taskRepo, SaranEngine saranEngine) {
        super("ONE STEP — Manajemen Tugas & Rekomendasi Langkah");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 620);
        setMinimumSize(new Dimension(700, 520));
        setLocationRelativeTo(null);

        this.dashboardPanel = new DashboardPanel(this, taskRepo, saranEngine);
        this.detailPanel = new DetailPanel(this, taskRepo);

        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(detailPanel, "DETAIL");

        setContentPane(mainPanel);
        bukaDashboard();
    }

    public void bukaDashboard() {
        dashboardPanel.refreshData();
        cardLayout.show(mainPanel, "DASHBOARD");
    }

    public void bukaDetailTugas(int taskId) {
        detailPanel.loadTask(taskId);
        cardLayout.show(mainPanel, "DETAIL");
    }
}
