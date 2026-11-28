package onestep.service;

import onestep.model.TemplateTugas;
import onestep.model.TemplateTugas.Target;
import onestep.model.Ukuran;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 Template bawaan yang ditulis langsung di kode (tidak butuh tabel database).
Urutan target mengikuti alur kerja yang wajar, sehingga langsung cocok dengan
aturan urutan di Mesin Saran **/
public class BuiltInTemplateProvider implements TemplateProvider {

    private final List<TemplateTugas> daftar;

    public BuiltInTemplateProvider() {
        List<TemplateTugas> list = new ArrayList<>();

        list.add(new TemplateTugas("Laporan Praktikum", Arrays.asList(
                new Target("Baca modul praktikum", Ukuran.S),
                new Target("Siapkan alat dan data", Ukuran.S),
                new Target("Kerjakan percobaan", Ukuran.L),
                new Target("Olah data jadi tabel atau grafik", Ukuran.M),
                new Target("Tulis pembahasan", Ukuran.L),
                new Target("Susun kesimpulan", Ukuran.S),
                new Target("Rapikan format dan daftar pustaka", Ukuran.S)
        )));

        list.add(new TemplateTugas("Presentasi Kelompok", Arrays.asList(
                new Target("Bagi tugas antar anggota", Ukuran.S),
                new Target("Susun kerangka alur presentasi", Ukuran.S),
                new Target("Buat slide masalah dan solusi", Ukuran.M),
                new Target("Buat slide fitur dan demo", Ukuran.M),
                new Target("Tulis skrip bicara", Ukuran.M),
                new Target("Latihan presentasi", Ukuran.M),
                new Target("Siapkan file cadangan", Ukuran.S)
        )));

        list.add(new TemplateTugas("Persiapan Ujian", Arrays.asList(
                new Target("Kumpulkan catatan dan slide", Ukuran.S),
                new Target("Ringkas materi per bab", Ukuran.L),
                new Target("Kerjakan latihan soal", Ukuran.L),
                new Target("Tandai bagian yang belum paham", Ukuran.S),
                new Target("Ulang materi yang sulit", Ukuran.M),
                new Target("Simulasi soal dengan batas waktu", Ukuran.M)
        )));

        list.add(new TemplateTugas("Makalah atau Esai", Arrays.asList(
                new Target("Tentukan topik dan rumusan masalah", Ukuran.S),
                new Target("Cari referensi", Ukuran.M),
                new Target("Buat outline", Ukuran.S),
                new Target("Tulis pendahuluan", Ukuran.M),
                new Target("Tulis isi", Ukuran.L),
                new Target("Tulis kesimpulan", Ukuran.S),
                new Target("Cek sitasi dan format", Ukuran.M)
        )));

        list.add(new TemplateTugas("Proyek Coding", Arrays.asList(
                new Target("Tentukan fitur utama", Ukuran.S),
                new Target("Rancang class dan tampilan", Ukuran.M),
                new Target("Implementasi fitur inti", Ukuran.L),
                new Target("Uji dan perbaiki bug", Ukuran.M),
                new Target("Tulis README", Ukuran.S)
        )));

        this.daftar = Collections.unmodifiableList(list);
    }

    @Override
    public List<TemplateTugas> daftarTemplate() {
        return daftar;
    }
}