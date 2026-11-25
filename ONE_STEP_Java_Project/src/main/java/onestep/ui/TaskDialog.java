package onestep.ui;

import onestep.exception.ValidationException;
import onestep.model.Task;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Dialog untuk menambah atau mengubah Tugas.
 */
public class TaskDialog extends JDialog {
    private final JTextField txtNama = new JTextField(25);
    private final JTextArea txtDeskripsi = new JTextArea(3, 25);
    private final JTextField txtDeadline = new JTextField(15);

    private Task resultTask;
    private final Task existingTask;

    public TaskDialog(Frame owner, Task existingTask) {
        super(owner, existingTask == null ? "Tambah Tugas Baru" : "Ubah Tugas", true);
        this.existingTask = existingTask;
        initUI();
        if (existingTask != null) {
            txtNama.setText(existingTask.getName());
            txtDeskripsi.setText(existingTask.getDescription());
            if (existingTask.getDeadline() != null) {
                txtDeadline.setText(existingTask.getDeadline().toString());
            }
        }
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
        panel.add(new JLabel("Nama Tugas *:"), gbc);
        gbc.gridx = 1;
        panel.add(txtNama, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Deskripsi:"), gbc);
        gbc.gridx = 1;
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        panel.add(new JScrollPane(txtDeskripsi), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtDeadline.setToolTipText("Contoh: 2026-12-05 (boleh dikosongkan)");
        panel.add(txtDeadline, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JLabel lblHint = new JLabel("Kosongkan jika tidak ada deadline.");
        lblHint.setFont(UiTheme.FONT_SMALL);
        lblHint.setForeground(UiTheme.TEXT_MUTED);
        panel.add(lblHint, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBatal = UiTheme.createSecondaryButton("Batal");
        JButton btnSimpan = UiTheme.createPrimaryButton("Simpan");

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
            String nama = txtNama.getText().trim();
            String deskripsi = txtDeskripsi.getText().trim();
            String dlText = txtDeadline.getText().trim();
            LocalDate deadline = null;

            if (!dlText.isEmpty()) {
                deadline = LocalDate.parse(dlText);
            }

            if (existingTask == null) {
                resultTask = new Task(nama, deskripsi, deadline);
            } else {
                existingTask.setName(nama);
                existingTask.setDescription(deskripsi);
                existingTask.setDeadline(deadline);
                resultTask = existingTask;
            }
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Format deadline salah! Gunakan format YYYY-MM-DD (contoh: 2026-12-05).",
                    "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        }
    }

    public Task getResultTask() {
        return resultTask;
    }
}
