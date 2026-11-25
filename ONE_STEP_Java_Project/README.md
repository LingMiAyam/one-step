# ONE STEP — Aplikasi Manajemen Tugas Java GUI

Aplikasi desktop berbasis Java Swing & SQLite yang memecah tugas besar menjadi target kecil (S/M/L) dan menyarankan **satu langkah paling pas** berdasarkan energi hari ini dan kedekatan deadline.

## Cara Menjalankan (Paling Praktis dengan Maven)
1. Buka folder `ONE_STEP_Java_Project` di **IntelliJ IDEA**, **NetBeans**, atau **VS Code** (dengan Extension Pack for Java).
2. Tunggu Maven mengunduh dependency `sqlite-jdbc` secara otomatis.
3. Jalankan file `src/main/java/onestep/Main.java`.
4. Database `onestep.db` akan otomatis dibuat beserta 2 contoh tugas siap demo.

## Cara Build File `.jar` untuk Dikumpulkan ke SPADA
Jalankan perintah ini di terminal (di dalam folder proyek):
```bash
mvn clean package
```
File executable `.jar` yang sudah memuat driver SQLite akan muncul di:
`target/ONE-STEP-App.jar`

Untuk mengetes `.jar` langsung dari terminal:
```bash
java -jar target/ONE-STEP-App.jar
```

## Struktur Package
- `onestep.model`: Class model (`Task`, `Goal`) dan `Enum` (`Ukuran`, `Status`, `Energi`).
- `onestep.exception`: Custom exception (`ValidationException`, `DataAccessException`).
- `onestep.repository`: Interface `TaskRepository` dan implementasi `SqliteTaskRepository`.
- `onestep.service`: Mesin Saran (`AturanSaran`, `UrgencyRule`, `EnergyRule`, `SaranEngine`).
- `onestep.ui`: Antarmuka Swing (`MainFrame`, `DashboardPanel`, `DetailPanel`, `TaskDialog`, `GoalDialog`).
