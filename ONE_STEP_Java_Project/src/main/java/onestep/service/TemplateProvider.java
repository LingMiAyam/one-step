package onestep.service;

import onestep.model.TemplateTugas;

import java.util.List;

// Sumber daftar template. Dibuat sebagai interface supaya sumbernya bisa diganti (misalnya dari database) tanpa mengubah tampilan.
 
public interface TemplateProvider {
    List<TemplateTugas> daftarTemplate();
}