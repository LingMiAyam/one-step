package onestep.ui;

import onestep.exception.ValidationException;
import onestep.model.Goal;
import onestep.model.Ukuran;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog untuk menambah Target kecil baru pada suatu Tugas.
 */
public class GoalDialog extends JDialog {
    private final JTextField txtNamaTarget = new JTextField(25);
    private final JComboBox<Ukuran> cmbUkuran = new JComboBox<>(Ukuran.values());

    private final int taskId;
    private final int nextPosition;
    private Goal resultGoal;

    public GoalDialog(Frame owner, int taskId, int nextPosition) {
        super(owner, "Tambah Target Baru", true);
        this.taskId = taskId;
        this.nextPosition = nextPosition;
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nama Target *:"), gbc);
        gbc.gridx = 1;
        panel.add(txtNamaTarget, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ukuran (Bobot Poin):"), gbc);
        gbc.gridx = 1;
        panel.add(cmbUkuran, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBatal = UiTheme.createSecondaryButton("Batal");
        JButton btnSimpan = UiTheme.createPrimaryButton("Tambah");

        btnBatal.addActionListener(e -> dispose());
        btnSimpan.addActionListener(e -> simpan());

        btnPanel.add(btnBatal);
        btnPanel.add(btnSimpan);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void simpan() {
        try {
            String desc = txtNamaTarget.getText().trim();
            Ukuran ukuran = (Ukuran) cmbUkuran.getSelectedItem();
            resultGoal = new Goal(taskId, desc, ukuran, nextPosition);
            dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        }
    }

    public Goal getResultGoal() {
        return resultGoal;
    }
}
