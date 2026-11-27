package onestep;

import onestep.repository.SqliteTaskRepository;
import onestep.repository.TaskRepository;
import onestep.service.SaranEngine;
import onestep.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // fix bug tampilan
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> {
            TaskRepository repo = new SqliteTaskRepository("onestep.db");
            repo.initDatabase();

            SaranEngine engine = new SaranEngine();
            MainFrame frame = new MainFrame(repo, engine);
            frame.setVisible(true);
        });
    }
}