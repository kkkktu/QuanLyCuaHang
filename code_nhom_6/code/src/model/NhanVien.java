package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import database.DBConnection;

public class NhanVien extends Nguoi {

    private LocalDate ngaySinh;
    private LocalDate ngayVaoLam;
    private String email;
    private String cccd;
    private String chucVu;
    private double luong;
    private int soCaNghi;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected String layPrefix() {
        return "NV";
    }

    public NhanVien() {
        super();
    }

    public NhanVien(String hoTen, String soDienThoai, String diaChi,
                    String ngaySinh, String email, String cccd,
                    String chucVu, double luong) {
        super(hoTen, soDienThoai, diaChi);
        setNgaySinh(ngaySinh);
        setNgayVaoLam(LocalDate.now().format(DATE_FMT));
        setEmail(email);
        setCccd(cccd);
        setChucVu(chucVu);
        setLuong(luong);
    }

    public NhanVien(String maDinhDanh, String hoTen, String soDienThoai, String diaChi,
                    String ngaySinh, String email, String cccd,
                    String chucVu, double luong) {
        this(maDinhDanh, hoTen, soDienThoai, diaChi, ngaySinh, LocalDate.now().format(DATE_FMT),
                email, cccd, chucVu, luong);
    }

    public NhanVien(String maDinhDanh, String hoTen, String soDienThoai, String diaChi,
                    String ngaySinh, String ngayVaoLam,
                    String email, String cccd,
                    String chucVu, double luong) {
        super(maDinhDanh, hoTen, soDienThoai, diaChi);
        setNgaySinh(ngaySinh);
        setNgayVaoLam(ngayVaoLam);
        setEmail(email);
        setCccd(cccd);
        setChucVu(chucVu);
        setLuong(luong);
    }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public String getNgaySinhText() { return ngaySinh != null ? ngaySinh.format(DATE_FMT) : ""; }
    public String getNgayVaoLamText() { return ngayVaoLam != null ? ngayVaoLam.format(DATE_FMT) : ""; }
    public String getEmail() { return email; }
    public String getCccd() { return cccd; }
    public String getChucVu() { return chucVu; }
    public double getLuong() { return luong; }
    public int getSoCaNghi() { return soCaNghi; }


    private static boolean laKho(String chucVu) {
        String cv = chucVu == null ? "" : chucVu.trim().toLowerCase();
        return cv.contains("kho");
    }

    private static boolean laKeToan(String chucVu) {
        String cv = chucVu == null ? "" : chucVu.trim().toLowerCase();
        return cv.contains("kế toán") || cv.contains("ke toan");
    }

    public int getSoNamKinhNghiem() {
        if (ngayVaoLam == null) return 0;
        return Math.max(0, Period.between(ngayVaoLam, LocalDate.now()).getYears());
    }

    public double getDonGiaTheoGio() {
        return tinhDonGiaTheoGio(chucVu, getNgayVaoLamText());
    }

    public static double tinhDonGiaTheoGio(String chucVu, String ngayVaoLamText) {
        LocalDate nvl;
        try {
            nvl = LocalDate.parse(ngayVaoLamText == null ? "" : ngayVaoLamText.trim(), DATE_FMT);
        } catch (Exception e) {
            nvl = LocalDate.now();
        }

        LocalDate today = LocalDate.now();
        int bacKinhNghiem;
        if (today.isBefore(nvl.plusYears(1))) {
            bacKinhNghiem = 0; // Dưới 1 năm
        } else if (today.isBefore(nvl.plusYears(2))) {
            bacKinhNghiem = 1; // 1 - 2 năm
        } else if (today.isBefore(nvl.plusYears(3))) {
            bacKinhNghiem = 2; // 2 - 3 năm
        } else if (today.isBefore(nvl.plusYears(5))) {
            bacKinhNghiem = 3; // 3 - 5 năm
        } else {
            bacKinhNghiem = 4; // Trên 5 năm
        }

        if (laKeToan(chucVu)) {
            if (bacKinhNghiem == 0) return 40000;
            if (bacKinhNghiem == 1) return 45000;
            if (bacKinhNghiem == 2) return 50000;
            if (bacKinhNghiem == 3) return 55000;
            return 60000;
        }
        if (laKho(chucVu)) {
            if (bacKinhNghiem == 0) return 32000;
            if (bacKinhNghiem == 1) return 36000;
            if (bacKinhNghiem == 2) return 42000;
            if (bacKinhNghiem == 3) return 47000;
            return 52000;
        }
        if (bacKinhNghiem == 0) return 30000;
        if (bacKinhNghiem == 1) return 35000;
        if (bacKinhNghiem == 2) return 40000;
        if (bacKinhNghiem == 3) return 45000;
        return 50000;
    }

    public void setNgaySinh(String str) {
        if (str == null || str.trim().isEmpty()) throw new IllegalArgumentException("Ngày sinh không được để trống.");
        try {
            LocalDate d = LocalDate.parse(str.trim(), DATE_FMT);
            if (d.isAfter(LocalDate.now())) throw new IllegalArgumentException("Ngày sinh không được ở tương lai.");
            if (Period.between(d, LocalDate.now()).getYears() < 18) throw new IllegalArgumentException("Nhân viên phải đủ 18 tuổi.");
            this.ngaySinh = d;
            if (this.ngayVaoLam != null && this.ngayVaoLam.isBefore(this.ngaySinh.plusYears(18))) {
                throw new IllegalArgumentException("Thời gian vào làm phải sau ngày sinh ít nhất 18 năm.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày sinh phải đúng định dạng dd/MM/yyyy.");
        }
    }

    public void setNgayVaoLam(String str) {
        if (str == null || str.trim().isEmpty()) throw new IllegalArgumentException("Thời gian vào làm không được để trống.");
        try {
            LocalDate d = LocalDate.parse(str.trim(), DATE_FMT);
            if (d.isAfter(LocalDate.now())) throw new IllegalArgumentException("Thời gian vào làm không được vượt quá ngày hiện tại.");
            if (ngaySinh != null && d.isBefore(ngaySinh.plusYears(18))) {
                throw new IllegalArgumentException("Thời gian vào làm phải sau ngày sinh ít nhất 18 năm.");
            }
            this.ngayVaoLam = d;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Thời gian vào làm phải đúng định dạng dd/MM/yyyy.");
        }
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("Email không được để trống.");
        email = email.trim().toLowerCase();
        if (email.length() > 150) throw new IllegalArgumentException("Email không được vượt quá 150 ký tự.");
        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[A-Za-z]{2,}$")) throw new IllegalArgumentException("Email không hợp lệ.");
        this.email = email;
    }

    public void setCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) throw new IllegalArgumentException("CCCD không được để trống.");
        cccd = cccd.trim();
        if (!cccd.matches("0\\d{11}")) throw new IllegalArgumentException("CCCD phải đủ 12 số nguyên dương và bắt đầu bằng số 0.");
        this.cccd = cccd;
    }

    public void setChucVu(String chucVu) {
        if (chucVu == null || chucVu.trim().isEmpty()) throw new IllegalArgumentException("Chức vụ không được để trống.");
        chucVu = chucVu.trim();
        if (chucVu.length() > 100) throw new IllegalArgumentException("Chức vụ không được vượt quá 100 ký tự.");
        this.chucVu = chucVu;
    }

    public void setLuong(double luong) {
        if (luong < 0) throw new IllegalArgumentException("Lương không được âm.");
        this.luong = luong;
    }

    public void setSoCaNghi(int soCaNghi) {
        if (soCaNghi < 0) throw new IllegalArgumentException("Số ca nghỉ phải là số nguyên dương hoặc bằng 0.");
        this.soCaNghi = soCaNghi;
    }

    private void nhapCoRangBuoc(Scanner sc, String prompt, Consumer<String> setter) {
        while (true) {
            System.out.print(prompt);
            try {
                setter.accept(sc.nextLine());
                return;
            } catch (Exception e) {
                System.out.println("[Lỗi] " + e.getMessage());
            }
        }
    }

    @Override
    public void nhapThongTin() {
        Scanner sc = new Scanner(System.in);
        System.out.println(">>> Mã nhân viên được tự động tạo: " + maDinhDanh);
        nhapCoRangBuoc(sc, "Họ tên: ", val -> setHoTen(val));
        nhapCoRangBuoc(sc, "Số điện thoại: ", val -> setSoDienThoai(val));
        nhapCoRangBuoc(sc, "Địa chỉ: ", val -> setDiaChi(val));
        nhapCoRangBuoc(sc, "Ngày sinh (dd/MM/yyyy): ", val -> setNgaySinh(val));
        nhapCoRangBuoc(sc, "Thời gian vào làm (dd/MM/yyyy): ", val -> setNgayVaoLam(val));
        nhapCoRangBuoc(sc, "Email: ", val -> setEmail(val));
        nhapCoRangBuoc(sc, "CCCD: ", val -> setCccd(val));
        nhapCoRangBuoc(sc, "Chức vụ: ", val -> setChucVu(val));
        this.luong = 0;
    }

    @Override
    public void suaThongTin() { nhapThongTin(); }

    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        System.out.println("Ngày sinh: " + getNgaySinhText());
        System.out.println("Thời gian vào làm: " + getNgayVaoLamText());
        System.out.println("Email: " + email);
        System.out.println("CCCD: " + cccd);
        System.out.println("Chức vụ: " + chucVu);
        System.out.println("Số ca nghỉ: " + soCaNghi);
        System.out.printf("Lương: %,.0f VNĐ%n", luong);
    }



    private static void damBaoCotSoCaNghi() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     IF COL_LENGTH('dbo.NhanVien', 'soCaNghi') IS NULL
                         ALTER TABLE dbo.NhanVien ADD soCaNghi INT NOT NULL CONSTRAINT DF_NhanVien_SoCaNghi DEFAULT 0;
                     IF COL_LENGTH('dbo.NhanVienNghiViec', 'soCaNghi') IS NULL
                         ALTER TABLE dbo.NhanVienNghiViec ADD soCaNghi INT NOT NULL CONSTRAINT DF_NhanVienNghiViec_SoCaNghi DEFAULT 0;
                     """)) {
            ps.execute();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void luuVaoSQL() {
        String sql = """
                INSERT INTO NhanVien
                (maDinhDanh, hoTen, soDienThoai, diaChi, ngaySinh, ngayVaoLam, email, cccd, chucVu, luong, soCaNghi)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        damBaoCotSoCaNghi();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);
            try {
                kiemTraTrungNhanVien(conn, null);
                if (maNhanVienDaTonTai(conn, maDinhDanh)) this.maDinhDanh = taoMaNhanVienTiepTheo(conn);
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, maDinhDanh);
                    ps.setString(2, hoTen);
                    ps.setString(3, soDienThoai);
                    ps.setString(4, diaChi);
                    ps.setString(5, getNgaySinhText());
                    ps.setString(6, getNgayVaoLamText());
                    ps.setString(7, email);
                    ps.setString(8, cccd);
                    ps.setString(9, chucVu);
                    ps.setDouble(10, luong);
                    ps.setInt(11, soCaNghi);
                    ps.executeUpdate();
                }
                conn.commit();
                System.out.println("Thêm nhân viên thành công! Mã: " + maDinhDanh);
            } catch (Exception ex) {
                try { conn.rollback(); } catch (Exception ignored) {}
                throw ex;
            } finally {
                try { conn.setAutoCommit(true); } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            throw new RuntimeException("Lưu nhân viên thất bại: " + chuanHoaLoiSQL(e), e);
        }
    }

    private void kiemTraTrungNhanVien(Connection conn, String maBoQua) throws Exception {
        String sql = """
                SELECT TOP 1 maDinhDanh, soDienThoai, email, cccd
                FROM NhanVien
                WHERE (soDienThoai = ? OR email = ? OR cccd = ?)
                  AND (? IS NULL OR maDinhDanh <> ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, soDienThoai);
            ps.setString(2, email);
            ps.setString(3, cccd);
            ps.setString(4, maBoQua);
            ps.setString(5, maBoQua);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String ma = rs.getString("maDinhDanh");
                    if (soDienThoai.equals(rs.getString("soDienThoai"))) throw new IllegalArgumentException("Số điện thoại đã tồn tại ở nhân viên " + ma + ".");
                    if (email.equalsIgnoreCase(rs.getString("email"))) throw new IllegalArgumentException("Email đã tồn tại ở nhân viên " + ma + ".");
                    if (cccd.equals(rs.getString("cccd"))) throw new IllegalArgumentException("CCCD đã tồn tại ở nhân viên " + ma + ".");
                }
            }
        }
    }

    private boolean maNhanVienDaTonTai(Connection conn, String ma) throws Exception {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE maDinhDanh = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        }
    }

    private String taoMaNhanVienTiepTheo(Connection conn) throws Exception {
        String sql = "SELECT MAX(CAST(SUBSTRING(maDinhDanh, 4, 6) AS INT)) FROM NhanVien WHERE maDinhDanh LIKE 'NV-[0-9][0-9][0-9][0-9][0-9][0-9]'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            int next = 1;
            if (rs.next()) next = rs.getInt(1) + 1;
            return String.format("NV-%06d", next);
        }
    }

    public static List<NhanVien> docTuSQL() {
        damBaoCotSoCaNghi();
        List<NhanVien> ds = new ArrayList<>();
        String sql = """
                SELECT maDinhDanh, hoTen, soDienThoai, diaChi, ngaySinh, ngayVaoLam, email, cccd,
                       chucVu, luong, ISNULL(soCaNghi, 0) AS soCaNghi
                FROM NhanVien
                WHERE ISNULL(trangThai, N'Đang làm') <> N'Đã nghỉ'
                ORDER BY maDinhDanh
                """;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhanVien nv = new NhanVien(
                        rs.getString("maDinhDanh"), rs.getString("hoTen"), rs.getString("soDienThoai"), rs.getString("diaChi"),
                        rs.getString("ngaySinh"), rs.getString("ngayVaoLam"), rs.getString("email"), rs.getString("cccd"),
                        rs.getString("chucVu"), rs.getDouble("luong"));
                nv.setSoCaNghi(rs.getInt("soCaNghi"));
                ds.add(nv);
            }
        } catch (Exception e) {
            System.out.println("Đọc danh sách nhân viên thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }
        return ds;
    }

    public void capNhatSQL() {
        damBaoCotSoCaNghi();
        String sql = """
                UPDATE NhanVien
                SET hoTen = ?, soDienThoai = ?, diaChi = ?, ngaySinh = ?, ngayVaoLam = ?, email = ?,
                    cccd = ?, chucVu = ?, luong = ?, soCaNghi = ?
                WHERE maDinhDanh = ?
                """;
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);
            try {
                kiemTraTrungNhanVien(conn, maDinhDanh);
                int rows;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, hoTen);
                    ps.setString(2, soDienThoai);
                    ps.setString(3, diaChi);
                    ps.setString(4, getNgaySinhText());
                    ps.setString(5, getNgayVaoLamText());
                    ps.setString(6, email);
                    ps.setString(7, cccd);
                    ps.setString(8, chucVu);
                    ps.setDouble(9, luong);
                    ps.setInt(10, soCaNghi);
                    ps.setString(11, maDinhDanh);
                    rows = ps.executeUpdate();
                }
                if (rows == 0) throw new IllegalStateException("Không tìm thấy nhân viên có mã: " + maDinhDanh);
                conn.commit();
            } catch (Exception ex) {
                try { conn.rollback(); } catch (Exception ignored) {}
                throw ex;
            } finally {
                try { conn.setAutoCommit(true); } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            throw new RuntimeException("Cập nhật nhân viên thất bại: " + chuanHoaLoiSQL(e), e);
        }
    }

    public static void xoaKhoiSQL(String maDinhDanh) {
        damBaoCotSoCaNghi();
        String sqlKiemTra = """
                SELECT maDinhDanh FROM NhanVien WHERE maDinhDanh = ?
                """;
        String sqlLuuNghiViec = """
                INSERT INTO NhanVienNghiViec
                (maDinhDanh, hoTen, soDienThoai, diaChi, ngaySinh, ngayVaoLam, email, cccd,
                 chucVu, luong, soCaNghi, ngayNghi, ghiChu)
                SELECT maDinhDanh, hoTen, soDienThoai, diaChi, ngaySinh, ngayVaoLam, email, cccd,
                       chucVu, luong, ISNULL(soCaNghi, 0), GETDATE(), N'Cho nghỉ việc từ màn quản lý nhân viên'
                FROM NhanVien nv
                WHERE nv.maDinhDanh = ?
                  AND NOT EXISTS (SELECT 1 FROM NhanVienNghiViec nvv WHERE nvv.maDinhDanh = nv.maDinhDanh)
                """;
        String sqlCapNhatTrangThai = "UPDATE NhanVien SET trangThai = N'Đã nghỉ' WHERE maDinhDanh = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlKiemTra)) {
                    ps.setString(1, maDinhDanh);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Không tìm thấy nhân viên có mã: " + maDinhDanh);
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(sqlLuuNghiViec)) { ps.setString(1, maDinhDanh); ps.executeUpdate(); }
                try (PreparedStatement ps = conn.prepareStatement(sqlCapNhatTrangThai)) { ps.setString(1, maDinhDanh); ps.executeUpdate(); }
                conn.commit();
            } catch (Exception ex) {
                try { conn.rollback(); } catch (Exception ignored) {}
                throw ex;
            } finally {
                try { conn.setAutoCommit(true); } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            throw new RuntimeException("Xóa/cho nghỉ nhân viên thất bại: " + chuanHoaLoiSQL(e), e);
        }
    }

    public static List<NhanVien> timKiemSQL(String keyword) {
        damBaoCotSoCaNghi();
        List<NhanVien> ds = new ArrayList<>();
        String sql = """
                SELECT maDinhDanh, hoTen, soDienThoai, diaChi, ngaySinh, ngayVaoLam, email, cccd,
                       chucVu, luong, ISNULL(soCaNghi, 0) AS soCaNghi
                FROM NhanVien
                WHERE ISNULL(trangThai, N'Đang làm') <> N'Đã nghỉ'
                  AND (maDinhDanh LIKE ? OR hoTen LIKE ? OR soDienThoai LIKE ? OR email LIKE ? OR cccd LIKE ?
                       OR chucVu LIKE ? OR ngayVaoLam LIKE ?)
                ORDER BY maDinhDanh
                """;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String key = "%" + keyword + "%";
            for (int i = 1; i <= 7; i++) ps.setString(i, key);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = new NhanVien(
                            rs.getString("maDinhDanh"), rs.getString("hoTen"), rs.getString("soDienThoai"), rs.getString("diaChi"),
                            rs.getString("ngaySinh"), rs.getString("ngayVaoLam"), rs.getString("email"), rs.getString("cccd"),
                            rs.getString("chucVu"), rs.getDouble("luong"));
                    nv.setSoCaNghi(rs.getInt("soCaNghi"));
                    ds.add(nv);
                }
            }
        } catch (Exception e) {
            System.out.println("Tìm kiếm nhân viên thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }
        return ds;
    }

    private static String chuanHoaLoiSQL(Exception e) {
        String msg = e == null ? "" : e.getMessage();
        Throwable cause = e == null ? null : e.getCause();
        while ((msg == null || msg.isBlank()) && cause != null) {
            msg = cause.getMessage();
            cause = cause.getCause();
        }
        if (msg == null) return "Không xác định được lỗi.";
        String lower = msg.toLowerCase();
        if (lower.contains("duplicate") || lower.contains("unique")) return "Dữ liệu nhân viên bị trùng. Vui lòng kiểm tra SĐT, email hoặc CCCD.";
        return msg;
    }
}
