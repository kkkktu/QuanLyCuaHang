package Human;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import database.DBConnection;

public class NhanVien extends Nguoi {

    private LocalDate ngaySinh;
    private String    email;
    private String    cccd;
    private String    chucVu;
    private double    luong;
    private LocalTime gioVaoLam;
    private LocalTime gioKetThuc;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // ===== Prefix cho mã tự động =====
    @Override
    protected String layPrefix() { return "NV"; }

    // ===== Constructor =====
    public NhanVien() {
        super();
    }

    public NhanVien(String hoTen, String sdt, String diaChi,
                    String ngaySinh, String email, String cccd,
                    String chucVu, double luong,
                    String gioVaoLam, String gioKetThuc) {
        super(hoTen, sdt, diaChi);
        setNgaySinh(ngaySinh);
        setEmail(email);
        setCccd(cccd);
        setChucVu(chucVu);
        setLuong(luong);
        setGioVaoLam(gioVaoLam);
        setGioKetThuc(gioKetThuc);
    }

    // ===== Getter =====
    public LocalDate getNgaySinh()    { return ngaySinh; }
    public String    getEmail()       { return email; }
    public String    getCccd()        { return cccd; }
    public String    getChucVu()      { return chucVu; }
    public double    getLuong()       { return luong; }
    public LocalTime getGioVaoLam()   { return gioVaoLam; }
    public LocalTime getGioKetThuc()  { return gioKetThuc; }

    // ===== Setter có ràng buộc =====
    public void setNgaySinh(String str) {
        if (str == null || str.trim().isEmpty())
            throw new IllegalArgumentException("Ngày sinh không được để trống.");
        try {
            LocalDate d = LocalDate.parse(str.trim(), DATE_FMT);
            if (d.isAfter(LocalDate.now()))
                throw new IllegalArgumentException("Ngày sinh không thể là ngày trong tương lai.");
            if (LocalDate.now().getYear() - d.getYear() < 18)
                throw new IllegalArgumentException("Nhân viên phải đủ 18 tuổi.");
            this.ngaySinh = d;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày sinh không đúng định dạng dd/MM/yyyy.");
        }
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Email không được để trống.");
        if (!email.trim().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Email không hợp lệ.");
        this.email = email.trim().toLowerCase();
    }

    /**
     * Gán CCCD: kiểm tra định dạng 12 số + đăng ký vào Registry để đảm bảo độc nhất.
     * Nếu là lần CẬP NHẬT, registry tự giải phóng CCCD cũ.
     */
    public void setCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty())
            throw new IllegalArgumentException("CCCD không được để trống.");
        if (!cccd.trim().matches("\\d{12}"))
            throw new IllegalArgumentException("CCCD phải gồm đúng 12 chữ số.");

        String cccdMoi = cccd.trim();
        NguoiRegistry reg = NguoiRegistry.getInstance();

        if (this.cccd == null) {
            // Lần đầu gán → đăng ký mới
            reg.dangKyCccd(this, cccdMoi);
        } else if (!this.cccd.equals(cccdMoi)) {
            // Cập nhật → giải phóng cũ, đăng ký mới
            reg.capNhatCccd(this, cccdMoi);
        }
        this.cccd = cccdMoi;
    }

    public void setChucVu(String chucVu) {
        if (chucVu == null || chucVu.trim().isEmpty())
            throw new IllegalArgumentException("Chức vụ không được để trống.");
        this.chucVu = chucVu.trim();
    }

    public void setLuong(double luong) {
        if (luong < 0)
            throw new IllegalArgumentException("Lương không được âm.");
        if (luong < 2_000_000)
            throw new IllegalArgumentException("Lương tối thiểu là 2,000,000 VNĐ.");
        this.luong = luong;
    }

    public void setGioVaoLam(String str) {
        if (str == null || str.trim().isEmpty())
            throw new IllegalArgumentException("Giờ vào làm không được để trống.");
        try {
            this.gioVaoLam = LocalTime.parse(str.trim(), TIME_FMT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Giờ vào làm không đúng định dạng HH:mm.");
        }
    }

    public void setGioKetThuc(String str) {
        if (str == null || str.trim().isEmpty())
            throw new IllegalArgumentException("Giờ kết thúc không được để trống.");
        try {
            LocalTime kt = LocalTime.parse(str.trim(), TIME_FMT);
            if (gioVaoLam != null && !kt.isAfter(gioVaoLam))
                throw new IllegalArgumentException("Giờ kết thúc phải sau giờ vào làm.");
            this.gioKetThuc = kt;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Giờ kết thúc không đúng định dạng HH:mm.");
        }
    }

    // ===== Nhập thông tin =====
    @Override
    public void nhapThongTin() {
        Scanner sc = new Scanner(System.in);
        System.out.println(">>> Mã nhân viên được tự động tạo: " + maDinhDanh);

        nhapCoRangBuoc(sc, "Họ tên: ",                   val -> setHoTen(val));
        nhapCoRangBuoc(sc, "SĐT: ",                      val -> setSoDienThoai(val));
        nhapCoRangBuoc(sc, "Địa chỉ: ",                  val -> setDiaChi(val));
        nhapCoRangBuoc(sc, "Ngày sinh (dd/MM/yyyy): ",   val -> setNgaySinh(val));
        nhapCoRangBuoc(sc, "Email: ",                    val -> setEmail(val));
        nhapCoRangBuoc(sc, "CCCD: ",                     val -> setCccd(val));
        nhapCoRangBuoc(sc, "Chức vụ: ",                  val -> setChucVu(val));

        while (true) {
            System.out.print("Lương: ");
            try { setLuong(Double.parseDouble(sc.nextLine().trim())); break; }
            catch (IllegalArgumentException e) { System.out.println("  [Lỗi] " + e.getMessage()); }
        }

        nhapCoRangBuoc(sc, "Giờ vào làm (HH:mm): ",     val -> setGioVaoLam(val));
        nhapCoRangBuoc(sc, "Giờ kết thúc (HH:mm): ",    val -> setGioKetThuc(val));
    }

    // ===== Sửa thông tin =====
    @Override
    public void suaThongTin() {
        Scanner sc = new Scanner(System.in);
        nhapCoRangBuoc(sc, "Email mới: ",     val -> setEmail(val));
        nhapCoRangBuoc(sc, "SĐT mới: ",       val -> setSoDienThoai(val));
        nhapCoRangBuoc(sc, "Địa chỉ mới: ",   val -> setDiaChi(val));
        nhapCoRangBuoc(sc, "Chức vụ mới: ",   val -> setChucVu(val));

        while (true) {
            System.out.print("Lương mới: ");
            try { setLuong(Double.parseDouble(sc.nextLine().trim())); break; }
            catch (IllegalArgumentException e) { System.out.println("  [Lỗi] " + e.getMessage()); }
        }
    }

    // ===== Xuất thông tin =====
    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        System.out.println("Ngày sinh:      " + (ngaySinh != null ? ngaySinh.format(DATE_FMT) : ""));
        System.out.println("Email:          " + email);
        System.out.println("CCCD:           " + cccd);
        System.out.println("Chức vụ:        " + chucVu);
        System.out.printf ("Lương:          %,.0f VNĐ%n", luong);
        System.out.println("Ca làm:         " + gioVaoLam.format(TIME_FMT)
                           + " – " + gioKetThuc.format(TIME_FMT));
    }

    public void hienThiChiTiet() {
        System.out.println("===== THÔNG TIN CHI TIẾT NHÂN VIÊN =====");
        xuatThongTin();
    }

    // ===== Kiểm tra đang làm việc =====
    public boolean dangLamViec() {
        LocalTime now = LocalTime.now();
        return !now.isBefore(gioVaoLam) && !now.isAfter(gioKetThuc);
    }

    public static void hienThiNhanVienDangLam(List<NhanVien> ds) {
        System.out.println("===== NHÂN VIÊN ĐANG LÀM VIỆC =====");
        for (NhanVien nv : ds) {
            if (nv.dangLamViec()) { nv.xuatThongTin(); System.out.println("---"); }
        }
    }

    public static void hienThiDanhSach(List<NhanVien> ds) {
        for (NhanVien nv : ds) { System.out.println("---"); nv.xuatThongTin(); }
    }

    // ===== SQL =====
    @Override
    public void luuVaoSQL() {
        String sql = "INSERT INTO NhanVien VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  maDinhDanh);
            ps.setString(2,  hoTen);
            ps.setString(3,  soDienThoai);
            ps.setString(4,  diaChi);
            ps.setString(5,  ngaySinh.format(DATE_FMT));
            ps.setString(6,  email);
            ps.setString(7,  cccd);
            ps.setString(8,  chucVu);
            ps.setDouble(9,  luong);
            ps.setTime(10,   java.sql.Time.valueOf(gioVaoLam));
            ps.setTime(11,   java.sql.Time.valueOf(gioKetThuc));
            ps.executeUpdate();
            System.out.println("Lưu nhân viên thành công! Mã: " + maDinhDanh);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<NhanVien> docTuSQL() {
        List<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.maDinhDanh = rs.getString(1);
                nv.hoTen = rs.getString(2);
                nv.soDienThoai = rs.getString(3);
                nv.diaChi = rs.getString(4);
                
                String ngayStr = rs.getString(5);
                if (ngayStr != null) nv.ngaySinh = LocalDate.parse(ngayStr, DATE_FMT);
                
                nv.email = rs.getString(6);
                nv.cccd = rs.getString(7);
                nv.chucVu = rs.getString(8);
                nv.luong = rs.getDouble(9);
                
                java.sql.Time vaoLam = rs.getTime(10);
                if (vaoLam != null) nv.gioVaoLam = vaoLam.toLocalTime();
                
                java.sql.Time ketThuc = rs.getTime(11);
                if (ketThuc != null) nv.gioKetThuc = ketThuc.toLocalTime();
                
                ds.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    public void capNhatSQL() {
        String sql = "UPDATE NhanVien SET hoTen=?, soDienThoai=?, diaChi=?, ngaySinh=?, email=?, cccd=?, chucVu=?, luong=?, gioVaoLam=?, gioKetThuc=? WHERE maDinhDanh=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  hoTen);
            ps.setString(2,  soDienThoai);
            ps.setString(3,  diaChi);
            ps.setString(4,  ngaySinh.format(DATE_FMT));
            ps.setString(5,  email);
            ps.setString(6,  cccd);
            ps.setString(7,  chucVu);
            ps.setDouble(8,  luong);
            ps.setTime(9,   java.sql.Time.valueOf(gioVaoLam));
            ps.setTime(10,   java.sql.Time.valueOf(gioKetThuc));
            ps.setString(11, maDinhDanh);
            ps.executeUpdate();
            System.out.println("Cập nhật nhân viên thành công! Mã: " + maDinhDanh);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void xoaKhoiSQL(String maDinhDanh) {
        String sql = "DELETE FROM NhanVien WHERE maDinhDanh=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDinhDanh);
            ps.executeUpdate();
            System.out.println("Xóa nhân viên thành công! Mã: " + maDinhDanh);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<NhanVien> timKiemSQL(String keyword) {
        List<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE hoTen LIKE ? OR maDinhDanh LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = new NhanVien();
                    nv.maDinhDanh = rs.getString(1);
                    nv.hoTen = rs.getString(2);
                    nv.soDienThoai = rs.getString(3);
                    nv.diaChi = rs.getString(4);
                    
                    String ngayStr = rs.getString(5);
                    if (ngayStr != null) nv.ngaySinh = LocalDate.parse(ngayStr, DATE_FMT);
                    
                    nv.email = rs.getString(6);
                    nv.cccd = rs.getString(7);
                    nv.chucVu = rs.getString(8);
                    nv.luong = rs.getDouble(9);
                    
                    java.sql.Time vaoLam = rs.getTime(10);
                    if (vaoLam != null) nv.gioVaoLam = vaoLam.toLocalTime();
                    
                    java.sql.Time ketThuc = rs.getTime(11);
                    if (ketThuc != null) nv.gioKetThuc = ketThuc.toLocalTime();
                    
                    ds.add(nv);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ===== Tiện ích =====
    private void nhapCoRangBuoc(Scanner sc, String label, java.util.function.Consumer<String> setter) {
        while (true) {
            System.out.print(label);
            try { setter.accept(sc.nextLine()); break; }
            catch (IllegalArgumentException e) { System.out.println("  [Lỗi] " + e.getMessage()); }
        }
    }
}
