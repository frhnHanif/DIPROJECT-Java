import java.util.ArrayList;
import java.util.Scanner;
import java.io.Console;

// ==========================================
// 1. INTERFACE & ABSTRACT (Polymorphism Base)
// ==========================================
interface Info {
    void cetakInfo();
}

abstract class Akun {
    protected String nama;
    protected String role;

    public Akun(String nama, String role) {
        this.nama = nama;
        this.role = role;
    }
    public String getNama() { return nama; }
}

// ==========================================
// 2. CONCRETE USER CLASSES (Inheritance)
// ==========================================
class Client extends Akun {
    public Client(String nama) {
        super(nama, "Client");
    }
}

class Freelancer extends Akun {
    public Freelancer(String nama) {
        super(nama, "Freelancer");
    }

    // Method khusus Freelancer untuk memproses status pesanan
    public void prosesPesanan(Pesanan p, boolean terima) {
        if (terima) {
            p.setStatus("SEDANG DIKERJAKAN");
        } else {
            p.setStatus("DITOLAK");
        }
    }
}

// ==========================================
// 3. CORE OBJECTS (Jasa & Pesanan)
// ==========================================
class Jasa implements Info {
    String namaJasa;
    double harga;

    public Jasa(String nama, double harga) {
        this.namaJasa = nama;
        this.harga = harga;
    }

    @Override
    public void cetakInfo() {
        System.out.println("- " + namaJasa + " (Rp " + (int)harga + ")");
    }
}

class Pesanan implements Info {
    private Client client;
    private Jasa jasa;
    private String catatan;
    private String status;

    public Pesanan(Client client, Jasa jasa, String catatan) {
        this.client = client;
        this.jasa = jasa;
        this.catatan = catatan;
        this.status = "MENUNGGU KONFIRMASI"; // Status Awal
    }

    // Encapsulation: Setter untuk mengubah status
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void cetakInfo() {
        System.out.println("\n=== STRUK / DETAIL PESANAN ===");
        System.out.println("Pemesan : " + client.getNama());
        System.out.println("Jasa    : " + jasa.namaJasa);
        System.out.println("Catatan : " + catatan);
        System.out.println("Status  : [" + status + "]");
        System.out.println("==============================");
    }
}

// ==========================================
// 4. DATABASE DUMMY (Menampung Jasa & Pesanan)
// ==========================================
class DBJasa {
    private static ArrayList<Jasa> listJasa = new ArrayList<>();
    private static ArrayList<Pesanan> listPesananMasuk = new ArrayList<>();

    // Static Block: Inisialisasi Data Dummy saat program jalan
    static {
        // Data Jasa
        Jasa j1 = new Jasa("Desain Logo", 50000);
        Jasa j2 = new Jasa("Joki Tugas", 25000);
        Jasa j3 = new Jasa("Install Windows", 75000);
        
        listJasa.add(j1);
        listJasa.add(j2);
        listJasa.add(j3);

        // Data Pesanan Dummy (Agar Freelancer punya kerjaan saat login)
        Client cDummy = new Client("Budi (Client Lama)");
        listPesananMasuk.add(new Pesanan(cDummy, j1, "Logo warna merah menyala"));
        listPesananMasuk.add(new Pesanan(cDummy, j2, "Tugas Kalkulus Hal 50"));
    }

    public static ArrayList<Jasa> getDaftarJasa() { return listJasa; }
    public static ArrayList<Pesanan> getPesananMasuk() { return listPesananMasuk; }
}

// ==========================================
// 5. LOGIN SYSTEM (Validasi & Masking)
// ==========================================
class LoginSystem {
    public static Akun loginFlow(Scanner scanner) {
        System.out.println("=== SISTEM LOGIN ===");
        System.out.println("1. Login sebagai Client");
        System.out.println("2. Login sebagai Freelancer");
        System.out.print("Pilih Peran (1/2): ");
        int peran = 0;
        try {
            peran = Integer.parseInt(scanner.nextLine());
        } catch(Exception e) { return null; }

        String targetUser = (peran == 1) ? "client" : "freelancer";
        String targetPass = (peran == 1) ? "client" : "freelancer";

        System.out.print("Username: ");
        String username = scanner.nextLine();

        // Logika Masking Password
        String password;
        Console console = System.console();
        if (console != null) {
            char[] passChar = console.readPassword("Password: ");
            password = new String(passChar);
        } else {
            System.out.print("Password: ");
            password = scanner.nextLine();
        }

        // Validasi Sederhana
        if (username.equals(targetUser) && password.equals(targetPass)) {
            System.out.println("\n>> Login Berhasil sebagai " + targetUser.toUpperCase());
            if (peran == 1) return new Client("Mahasiswa Client");
            else return new Freelancer("Mahasiswa Freelancer");
        } else {
            System.out.println("\n>> Login Gagal! Username/Password salah.");
            return null;
        }
    }
}

// ==========================================
// 6. MAIN PROGRAM (Alur Utama)
// ==========================================
public class AplikasiFreelance {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // STEP 1: LOGIN
        Akun user = LoginSystem.loginFlow(scanner);

        if (user == null) {
            System.out.println("Aplikasi ditutup.");
            return; // Keluar jika login gagal
        }

        // STEP 2: PENGECEKAN TIPE USER (INSTANCEOF)
        
        // ================= ALUR CLIENT =================
        if (user instanceof Client) {
            Client clientLog = (Client) user; // Casting
            
            System.out.println("\nHalo, " + clientLog.getNama() + "!");
            System.out.println("=== PILIH JASA TERSEDIA ===");
            
            ArrayList<Jasa> listJasa = DBJasa.getDaftarJasa();
            for (int i = 0; i < listJasa.size(); i++) {
                System.out.print((i + 1) + ". ");
                listJasa.get(i).cetakInfo();
            }

            System.out.print("\nMasukkan Nomor Jasa: ");
            int pilihan = scanner.nextInt();
            scanner.nextLine(); 

            if (pilihan > 0 && pilihan <= listJasa.size()) {
                System.out.print("Masukkan Catatan Pesanan: ");
                String catatan = scanner.nextLine();
                
                // Buat Pesanan Baru
                Pesanan pesananBaru = new Pesanan(clientLog, listJasa.get(pilihan-1), catatan);
                
                // Tampilkan Struk (Status masih MENUNGGU)
                pesananBaru.cetakInfo();
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        } 
        
        // ================= ALUR FREELANCER =================
        else if (user instanceof Freelancer) {
            Freelancer freeLog = (Freelancer) user; // Casting

            System.out.println("\nHalo, " + freeLog.getNama() + "!");
            System.out.println("=== DAFTAR PESANAN MASUK ===");

            ArrayList<Pesanan> listPesanan = DBJasa.getPesananMasuk();
            
            if (listPesanan.isEmpty()) {
                System.out.println("Belum ada pesanan masuk.");
            } else {
                // Tampilkan semua pesanan dummy
                for (int i = 0; i < listPesanan.size(); i++) {
                    System.out.print("No " + (i + 1));
                    listPesanan.get(i).cetakInfo();
                }

                System.out.print("\nPilih Nomor Pesanan untuk diproses: ");
                int idx = scanner.nextInt();
                scanner.nextLine();

                if (idx > 0 && idx <= listPesanan.size()) {
                    Pesanan targetPesanan = listPesanan.get(idx - 1);
                    
                    System.out.println("Aksi: 1. Terima (Kerjakan) | 2. Tolak");
                    System.out.print("Pilihan Anda: ");
                    int aksi = scanner.nextInt();

                    // Proses perubahan status
                    if (aksi == 1) {
                        freeLog.prosesPesanan(targetPesanan, true);
                        System.out.println("\n>> Pesanan BERHASIL DITERIMA!");
                    } else {
                        freeLog.prosesPesanan(targetPesanan, false);
                        System.out.println("\n>> Pesanan TELAH DITOLAK.");
                    }

                    // Tampilkan Struk Akhir dengan Status Baru
                    targetPesanan.cetakInfo();
                } else {
                    System.out.println("Nomor pesanan salah.");
                }
            }
        }

        scanner.close();
    }
}