package onestep.ui;

import onestep.exception.ValidationException;
import onestep.model.Task;
import onestep.model.TemplateTugas;
import onestep.service.BuiltInTemplateProvider;
import onestep.service.TemplateProvider;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog untuk menambah atau mengubah Tugas.
 */
public class TaskDialog extends JDialog {
    private final JTextField txtNama = new JTextField(25);
    private final JTextArea txtDeskripsi = new JTextArea(3, 25);
    private final JTextField txtDeadline = new JTextField(15);

   
    private final JComboBox<String> cmbTemplate = new JComboBox<>();
    private final JTextArea txtPratinjau = new JTextArea(9, 25);
    private final List<TemplateTugas> daftarTemplate;
    private String namaOtomatis = "";   // nama yang diisi otomatis dari template (untuk dikenali saat template diganti)
  

    private Task resultTask;
    private final Task existingTask;

  
    // Konstruktor lama dipertahankan agar pemanggil lain (DetailPanel) tidak perlu diubah
    public TaskDialog(Frame owner, Task existingTask) {
        this(owner, existingTask, new BuiltInTemplateProvider());
    }

    public TaskDialog(Frame owner, Task existingTask, TemplateProvider provider) {
        super(owner, existingTask == null ? "Tambah Tugas Baru" : "Ubah Tugas", true);
        this.existingTask = existingTask;
       
        this.daftarTemplate = provider.daftarTemplate();
       
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

        // >>> TEMPLATE (awal) <<<
        // Baris 0-1: pilihan template + pratinjau (hanya saat MEMBUAT tugas baru).
        // Baris form lain digeser ke bawah (gridy +2).
        if (existingTask == null) {
            cmbTemplate.addItem("Kosong (tanpa template)");
            for (TemplateTugas t : daftarTemplate) {
                cmbTemplate.addItem(t.getNama());
            }
            cmbTemplate.addActionListener(e -> templateBerubah());

            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Mulai dari template:"), gbc);
            gbc.gridx = 1;
            panel.add(cmbTemplate, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            panel.add(new JLabel("Isi template:"), gbc);
            gbc.gridx = 1;
            txtPratinjau.setEditable(false);
            txtPratinjau.setLineWrap(true);
            txtPratinjau.setWrapStyleWord(true);
            txtPratinjau.setBackground(UiTheme.BG_MAIN);
            txtPratinjau.setForeground(UiTheme.TEXT_MUTED);
            panel.add(new JScrollPane(txtPratinjau), gbc);
            gbc.anchor = GridBagConstraints.WEST;

            tampilkanPratinjau();
        }
        // >>> TEMPLATE (akhir) <<<

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Nama Tugas *:"), gbc);
        gbc.gridx = 1;
        panel.add(txtNama, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Deskripsi:"), gbc);
        gbc.gridx = 1;
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        panel.add(new JScrollPane(txtDeskripsi), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtDeadline.setToolTipText("Contoh: 2026-12-05 (boleh dikosongkan)");
        panel.add(txtDeadline, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
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

   
    // Template yang dipilih pengguna, atau null kalau "Kosong" (atau saat mode ubah tugas)
    public TemplateTugas getSelectedTemplate() {
        if (existingTask != null) return null;
        int idx = cmbTemplate.getSelectedIndex();
        return (idx <= 0) ? null : daftarTemplate.get(idx - 1);
    }

    private void templateBerubah() {
        TemplateTugas t = getSelectedTemplate();

        // Isi nama otomatis hanya kalau kolom nama masih kosong atau masih berisi nama otomatis sebelumnya
        String namaSekarang = txtNama.getText().trim();
        if (namaSekarang.isEmpty() || namaSekarang.equals(namaOtomatis)) {
            namaOtomatis = (t != null) ? t.getNama() : "";
            txtNama.setText(namaOtomatis);
        }
        tampilkanPratinjau();
    }

    private void tampilkanPratinjau() {
        TemplateTugas t = getSelectedTemplate();
        if (t == null) {
            txtPratinjau.setText("Tugas dibuat kosong. Kamu menambah target sendiri di halaman detail.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(t.getJumlahTarget()).append(" target, ").append(t.getTotalPoin()).append(" poin\n\n");
            int no = 1;
            for (TemplateTugas.Target target : t.getTargets()) {
                sb.append(no++).append(". ").append(target.getNama())
                  .append("  [").append(target.getUkuran().name()).append("]\n");
            }
            txtPratinjau.setText(sb.toString());
        }
        txtPratinjau.setCaretPosition(0);
    }
    
}