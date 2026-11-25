package onestep.ui;

import onestep.exception.DataAccessException;
import onestep.model.Energi;
import onestep.model.Status;
import onestep.model.Task;
import onestep.repository.TaskRepository;
import onestep.service.HasilSaran;
import onestep.service.SaranEngine;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Layar 1: Dashboard.
 * Menampilkan pemilih energi, kartu rekomendasi satu langkah, dan daftar kartu tugas.
 */
public class DashboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private final TaskRepository taskRepo;
    private final SaranEngine saranEngine;

    private Energi energiTerpilih = Energi.SEDANG;
    private List<Task> daftarTugas = new ArrayList<>();
    private List<HasilSaran> daftarSaran = new ArrayList<>();
    private int indeksSaran = 0;

    // Komponen Kartu Saran
    private final JLabel lblSaranJudul = new JLabel("-");
    private final JLabel lblSaranMeta = new JLabel("-");
    private final JLabel lblSaranAlasan = new JLabel("-");
    private final JButton btnKerjakan = UiTheme.createPrimaryButton("Kerjakan Ini");
    private final JButton btnSaranLain = UiTheme.createSecondaryButton("Saran Lain");

    // Panel Daftar Kartu Tugas Dinamis
    private final JPanel panelDaftarTugas = new JPanel();

    public DashboardPanel(MainFrame mainFrame, TaskRepository taskRepo, SaranEngine saranEngine) {
        this.mainFrame = mainFrame;
        this.taskRepo = taskRepo;
        this.saranEngine = saranEngine;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UiTheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // --- NORTH: Header + Pemilih Energi + Kartu Saran ---
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // Baris Judul & Tombol Tambah Tugas
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setOpaque(false);
        JLabel lblAppTitle = new JLabel("ONE STEP");
        lblAppTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblAppTitle.setForeground(UiTheme.TEAL_DARK);

        JButton btnTambahTugas = UiTheme.createPrimaryButton("+ Tugas Baru");
        btnTambahTugas.addActionListener(e -> bukaDialogTambahTugas());

        headerBar.add(lblAppTitle, BorderLayout.WEST);
        headerBar.add(btnTambahTugas, BorderLayout.EAST);
        topContainer.add(headerBar);
        topContainer.add(Box.createVerticalStrut(12));

        // Baris Pemilih Energi
        JPanel energiBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        energiBar.setOpaque(false);
        JLabel lblEnergi = new JLabel("Energi kamu hari ini:");
        lblEnergi.setFont(UiTheme.FONT_SUBTITLE);
        lblEnergi.setForeground(UiTheme.TEXT_DARK);

        JRadioButton rbRendah = new JRadioButton("Rendah");
        JRadioButton rbSedang = new JRadioButton("Sedang", true);
        JRadioButton rbTinggi = new JRadioButton("Tinggi");
        for (JRadioButton rb : new JRadioButton[]{rbRendah, rbSedang, rbTinggi}) {
            rb.setOpaque(false);
            rb.setFont(UiTheme.FONT_BODY);
        }

        ButtonGroup bgEnergi = new ButtonGroup();
        bgEnergi.add(rbRendah);
        bgEnergi.add(rbSedang);
        bgEnergi.add(rbTinggi);

        // Lambda ActionListener saat energi diganti
        rbRendah.addActionListener(e -> ubahEnergi(Energi.RENDAH));
        rbSedang.addActionListener(e -> ubahEnergi(Energi.SEDANG));
        rbTinggi.addActionListener(e -> ubahEnergi(Energi.TINGGI));

        energiBar.add(lblEnergi);
        energiBar.add(rbRendah);
        energiBar.add(rbSedang);
        energiBar.add(rbTinggi);
        energiBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        topContainer.add(energiBar);
        topContainer.add(Box.createVerticalStrut(8));

        // Kartu Saran Utama
        JPanel kartuSaran = new JPanel(new BorderLayout(10, 8));
        kartuSaran.setBackground(UiTheme.TEAL_LIGHT);
        kartuSaran.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.TEAL_MAIN, 2, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblTag = new JLabel("SATU LANGKAH PALING PAS SEKARANG");
        lblTag.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTag.setForeground(UiTheme.TEAL_DARK);

        lblSaranJudul.setFont(UiTheme.FONT_TITLE);
        lblSaranJudul.setForeground(UiTheme.TEXT_DARK);
        lblSaranMeta.setFont(UiTheme.FONT_SMALL);
        lblSaranMeta.setForeground(UiTheme.TEXT_MUTED);
        lblSaranAlasan.setFont(UiTheme.FONT_BODY);
        lblSaranAlasan.setForeground(UiTheme.TEAL_DARK);

        JPanel saranInfo = new JPanel();
        saranInfo.setLayout(new BoxLayout(saranInfo, BoxLayout.Y_AXIS));
        saranInfo.setOpaque(false);
        saranInfo.add(lblTag);
        saranInfo.add(Box.createVerticalStrut(4));
        saranInfo.add(lblSaranJudul);
        saranInfo.add(Box.createVerticalStrut(2));
        saranInfo.add(lblSaranMeta);
        saranInfo.add(Box.createVerticalStrut(6));
        saranInfo.add(lblSaranAlasan);

        JPanel saranBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        saranBtns.setOpaque(false);
        saranBtns.add(btnSaranLain);
        saranBtns.add(btnKerjakan);

        btnKerjakan.addActionListener(e -> kerjakanSaranSaatIni());
        btnSaranLain.addActionListener(e -> tampilkanSaranBerikutnya());

        kartuSaran.add(saranInfo, BorderLayout.CENTER);
        kartuSaran.add(saranBtns, BorderLayout.SOUTH);
        topContainer.add(kartuSaran);

        add(topContainer, BorderLayout.NORTH);

        // --- CENTER: Daftar Kartu Tugas ---
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 6));
        centerWrapper.setOpaque(false);
        JLabel lblDaftar = new JLabel("Daftar Tugas (Klik kartu untuk buka detail & target)");
        lblDaftar.setFont(UiTheme.FONT_SUBTITLE);
        lblDaftar.setForeground(UiTheme.TEXT_DARK);
        centerWrapper.add(lblDaftar, BorderLayout.NORTH);

        panelDaftarTugas.setLayout(new BoxLayout(panelDaftarTugas, BoxLayout.Y_AXIS));
        panelDaftarTugas.setBackground(UiTheme.BG_MAIN);

        JScrollPane scrollPane = new JScrollPane(panelDaftarTugas);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerWrapper.add(scrollPane, BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);
    }

    public void refreshData() {
        try {
            daftarTugas = taskRepo.findAllTasksWithGoals();
            muatDaftarTugas();
            hitungUlangSaran();
        } catch (DataAccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ubahEnergi(Energi energi) {
        this.energiTerpilih = energi;
        hitungUlangSaran();
    }

    private void hitungUlangSaran() {
        daftarSaran = saranEngine.hitungSaran(daftarTugas, energiTerpilih);
        indeksSaran = 0;
        tampilkanKartuSaran();
    }

    private void tampilkanKartuSaran() {
        if (daftarSaran.isEmpty()) {
            lblSaranJudul.setText("Semua target sudah selesai atau belum ada target!");
            lblSaranMeta.setText("Tambahkan tugas dan target baru untuk mulai mendapat saran.");
            lblSaranAlasan.setText(" ");
            btnKerjakan.setEnabled(false);
            btnSaranLain.setEnabled(false);
            return;
        }

        HasilSaran h = daftarSaran.get(indeksSaran);
        lblSaranJudul.setText(h.getGoal().getDescription());

        String dlInfo = (h.getTask().getDeadline() != null)
                ? "Deadline: " + h.getTask().getDeadline()
                : "Tanpa deadline";
        lblSaranMeta.setText(String.format("Tugas: %s  |  Ukuran: %s  |  %s  |  Skor: %.0f",
                h.getTask().getName(), h.getGoal().getUkuran().getLabel(), dlInfo, h.getTotalSkor()));
        lblSaranAlasan.setText("Alasan: " + h.getAlasanUtama());

        btnKerjakan.setEnabled(true);
        btnSaranLain.setEnabled(daftarSaran.size() > 1);
    }

    private void tampilkanSaranBerikutnya() {
        if (daftarSaran.size() <= 1) return;
        indeksSaran = (indeksSaran + 1) % daftarSaran.size();
        tampilkanKartuSaran();
    }

    private void kerjakanSaranSaatIni() {
        if (daftarSaran.isEmpty()) return;
        HasilSaran h = daftarSaran.get(indeksSaran);
        try {
            taskRepo.updateGoalStatus(h.getGoal().getId(), Status.SEDANG_DIKERJAKAN);
            mainFrame.bukaDetailTugas(h.getTask().getId());
        } catch (DataAccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void muatDaftarTugas() {
        panelDaftarTugas.removeAll();

        if (daftarTugas.isEmpty()) {
            JLabel kosong = new JLabel("Belum ada tugas. Klik '+ Tugas Baru' untuk membuat tugas pertama.");
            kosong.setFont(UiTheme.FONT_BODY);
            kosong.setForeground(UiTheme.TEXT_MUTED);
            kosong.setBorder(new EmptyBorder(16, 4, 16, 4));
            panelDaftarTugas.add(kosong);
        } else {
            for (Task task : daftarTugas) {
                panelDaftarTugas.add(buatKartuTugas(task));
                panelDaftarTugas.add(Box.createVerticalStrut(10));
            }
        }

        // WAJIB di Swing setelah menambah/menghapus komponen dinamis
        panelDaftarTugas.revalidate();
        panelDaftarTugas.repaint();
    }

    private JPanel buatKartuTugas(Task task) {
        JPanel card = new JPanel(new BorderLayout(10, 6));
        card.setBackground(UiTheme.CARD_BG);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        card.setBorder(new CompoundBorder(
                new LineBorder(UiTheme.BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel lblNama = new JLabel(task.getName());
        lblNama.setFont(UiTheme.FONT_SUBTITLE);
        lblNama.setForeground(UiTheme.TEXT_DARK);

        String dlStr = (task.getDeadline() != null) ? "Deadline: " + task.getDeadline() : "Tanpa deadline";
        JLabel lblDl = new JLabel(dlStr);
        lblDl.setFont(UiTheme.FONT_SMALL);
        lblDl.setForeground(UiTheme.TEXT_MUTED);

        topRow.add(lblNama, BorderLayout.WEST);
        topRow.add(lblDl, BorderLayout.EAST);

        int persen = task.getProgresPersen();
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(persen);
        progressBar.setStringPainted(true);
        progressBar.setString(String.format("%d%% (%d/%d poin)", persen, task.getPoinSelesai(), task.getTotalPoin()));
        progressBar.setForeground(UiTheme.TEAL_MAIN);

        card.add(topRow, BorderLayout.NORTH);
        card.add(progressBar, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.bukaDetailTugas(task.getId());
            }
        });

        return card;
    }

    private void bukaDialogTambahTugas() {
        TaskDialog dialog = new TaskDialog(mainFrame, null);
        dialog.setVisible(true);
        Task baru = dialog.getResultTask();
        if (baru != null) {
            try {
                taskRepo.saveTask(baru);
                refreshData();
            } catch (DataAccessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
