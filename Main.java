import java.util.ArrayList;
import java.util.Scanner;
import java.io.Console;

// ==========================================
// 1. INTERFACE & ABSTRACT
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
    public String getRole() { return role; }
}

// ==========================================
// 2. CONCRETE USER CLASSES
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

    public void prosesPesanan(Pesanan p, boolean terima) {
        if (terima) {
            p.setStatus("SEDANG DIKERJAKAN");
        } else {
            p.setStatus("DITOLAK");
        }
    }
}

// ==========================================
// 3. CORE OBJECTS
// ==========================================
class Jasa implements Info {
    String namaJasa; 
    double harga;
    Freelancer freelancer; // [UPDATE] Menambahkan pemilik jasa

    // [UPDATE] Constructor menerima object Freelancer
    public Jasa(String nama, double harga, Freelancer freelancer) {
        this.namaJasa = nama;
        this.harga = harga;
        this.freelancer = freelancer;
    }

    public Freelancer getFreelancer() {
        return freelancer;
    }

    @Override
    public void cetakInfo() {
        // [UPDATE] Menampilkan nama freelancer di daftar jasa
        System.out.println("- " + namaJasa + " | " + freelancer.getNama() + " | Rp " + (int)harga + ")");
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
        this.status = "MENUNGGU KONFIRMASI";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void cetakInfo() {
        System.out.println("\n=== STRUK / DETAIL PESANAN ===");
        System.out.println("Pemesan    : " + client.getNama());
        // [UPDATE] Menampilkan info freelancer tujuan
        System.out.println("Freelancer : " + jasa.getFreelancer().getNama()); 
        System.out.println("Jasa       : " + jasa.namaJasa);
        System.out.println("Catatan    : " + catatan);
        System.out.println("Status     : [" + status + "]");
        System.out.println("==============================");
    }
}

// ==========================================
// 4. DATABASE DUMMY
// ==========================================
class Database {
    private static ArrayList<Jasa> listJasa = new ArrayList<>();
    private static ArrayList<Pesanan> listPesananMasuk = new ArrayList<>();

    public static void initData() {
        if (!listJasa.isEmpty()) return; 

        // [UPDATE] Membuat object Freelancer pemilik jasa
        Freelancer f1 = new Freelancer("Ridwan Desain");
        Freelancer f2 = new Freelancer("Afiq Akademik");
        Freelancer f3 = new Freelancer("Hanif Tekno");

        // [UPDATE] Mengaitkan Jasa dengan Freelancer
        Jasa j1 = new Jasa("Desain Logo", 50000, f1);
        Jasa j2 = new Jasa("Joki Tugas", 25000, f2);
        Jasa j3 = new Jasa("Install Windows", 75000, f3);
        
        listJasa.add(j1);
        listJasa.add(j2);
        listJasa.add(j3);

        // Data Pesanan Dummy
        Client cDummy = new Client("Mahasiswa Client");
        listPesananMasuk.add(new Pesanan(cDummy, j1, "Logo warna merah menyala"));
        listPesananMasuk.add(new Pesanan(cDummy, j1, "Logo himpunan mahasiswa elektro"));
    }

    public static ArrayList<Jasa> getDaftarJasa() { return listJasa; }
    public static ArrayList<Pesanan> getPesananMasuk() { return listPesananMasuk; }
}

// ==========================================
// 5. LOGIN SYSTEM
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

        String password;
        Console console = System.console();
        if (console != null) {
            char[] passChar = console.readPassword("Password: ");
            password = new String(passChar);
        } else {
            System.out.print("Password: ");
            password = scanner.nextLine();
        }

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
// 6. MAIN PROGRAM
// ==========================================
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Database.initData();

        Akun user = LoginSystem.loginFlow(scanner);

        if (user == null) {
            System.out.println("Aplikasi ditutup.");
            return;
        }

        if (user instanceof Client) {
            Client clientLog = (Client) user;
            
            System.out.println("\nHalo, " + clientLog.getNama() + "!");
            System.out.println("=== PILIH JASA TERSEDIA ===");
            
            ArrayList<Jasa> listJasa = Database.getDaftarJasa();
            for (int i = 0; i < listJasa.size(); i++) {
                System.out.print((i + 1) + ". ");
                listJasa.get(i).cetakInfo(); // Sekarang menampilkan nama freelancer juga
            }

            System.out.print("\nMasukkan Nomor Jasa: ");
            int pilihan = scanner.nextInt();
            scanner.nextLine(); 

            if (pilihan > 0 && pilihan <= listJasa.size()) {
                System.out.print("Masukkan Catatan Pesanan: ");
                String catatan = scanner.nextLine();
                
                Pesanan pesananBaru = new Pesanan(clientLog, listJasa.get(pilihan-1), catatan);
                pesananBaru.cetakInfo();
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        } 
        else if (user instanceof Freelancer) {
            Freelancer freeLog = (Freelancer) user;

            System.out.println("\nHalo, " + freeLog.getNama() + "!");
            System.out.println("=== DAFTAR PESANAN MASUK ===");

            ArrayList<Pesanan> listPesanan = Database.getPesananMasuk();
            
            if (listPesanan.isEmpty()) {
                System.out.println("Belum ada pesanan masuk.");
            } else {
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

                    if (aksi == 1) {
                        freeLog.prosesPesanan(targetPesanan, true);
                        System.out.println("\n>> Pesanan BERHASIL DITERIMA!");
                    } else {
                        freeLog.prosesPesanan(targetPesanan, false);
                        System.out.println("\n>> Pesanan TELAH DITOLAK.");
                    }
                    targetPesanan.cetakInfo();
                } else {
                    System.out.println("Nomor pesanan salah.");
                }
            }
        }

        scanner.close();
    }
}