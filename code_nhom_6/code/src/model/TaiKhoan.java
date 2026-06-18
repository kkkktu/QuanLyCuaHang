package model;

import database.DBConnection;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TaiKhoan {

    // ===== Hằng số vai trò =====
    public static final String VAI_TRO_ADMIN = "ADMIN";
    public static final String VAI_TRO_STAFF = "STAFF";
    public static final String VAI_TRO_WAREHOUSE = "WAREHOUSE";
    public static final String VAI_TRO_ACCOUNTANT = "ACCOUNTANT";

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // ===== Thuộc tính =====
    private String maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;       // Lưu dạng SHA-256 hash
    private String vaiTro;
    private String hoTen;
    private String email;
    private boolean trangThai;    // true = hoạt động, false = bị khóa
    private LocalDateTime ngayTao;
    private LocalDateTime ngayDangNhapCuoi;

    // ===== Sinh mã tự động =====
    private static String taoMaTuDong() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "TK-" + uuid;
    }

    // ===== Constructor =====
    public TaiKhoan() {
        this.maTaiKhoan = taoMaTuDong();
        this.trangThai  = true;
        this.ngayTao    = LocalDateTime.now();
    }

    public TaiKhoan(String tenDangNhap, String matKhauThuan, String vaiTro, String hoTen) {
        this();
        setTenDangNhap(tenDangNhap);
        setMatKhauThuan(matKhauThuan);
        setVaiTro(vaiTro);
        setHoTen(hoTen);
    }

    public TaiKhoan(String tenDangNhap, String matKhauThuan, String vaiTro,
                    String hoTen, String email) {
        this(tenDangNhap, matKhauThuan, vaiTro, hoTen);
        setEmail(email);
    }

    // ===== Getter =====
    public String        getMaTaiKhoan()          { return maTaiKhoan; }
    public String        getTenDangNhap()          { return tenDangNhap; }
    public String        getVaiTro()               { return vaiTro; }
    public String        getHoTen()                { return hoTen; }
    public String        getEmail()                { return email; }
    public boolean       isTrangThai()             { return trangThai; }
    public LocalDateTime getNgayTao()              { return ngayTao; }
    public LocalDateTime getNgayDangNhapCuoi()     { return ngayDangNhapCuoi; }
    public String        getMatKhauHash()          { return matKhau; }

    // ===== Kiểm tra vai trò =====
    public boolean isAdmin() {
        return VAI_TRO_ADMIN.equals(vaiTro);
    }

    public boolean isStaff() {
        return VAI_TRO_STAFF.equals(vaiTro);
    }

    public boolean isWarehouse() {
        return VAI_TRO_WAREHOUSE.equals(vaiTro);
    }

    public boolean isAccountant() {
        return VAI_TRO_ACCOUNTANT.equals(vaiTro);
    }

    public void napTuResultSet(ResultSet rs) throws Exception {
        this.maTaiKhoan = rs.getString("maTaiKhoan");
        this.tenDangNhap = rs.getString("tenDangNhap");
        this.matKhau = rs.getString("matKhau");
        this.vaiTro = rs.getString("vaiTro");
        this.hoTen = rs.getString("hoTen");
        this.email = rs.getString("email");
        this.trangThai = rs.getBoolean("trangThai");

        try {
            String ngayTaoStr = rs.getString("ngayTao");
            if (ngayTaoStr != null && !ngayTaoStr.isBlank()) {
                this.ngayTao = LocalDateTime.parse(ngayTaoStr, DT_FMT);
            }
        } catch (Exception ignored) {
        }

        try {
            String ngayCuoiStr = rs.getString("ngayDangNhapCuoi");
            if (ngayCuoiStr != null && !ngayCuoiStr.isBlank()) {
                this.ngayDangNhapCuoi = LocalDateTime.parse(ngayCuoiStr, DT_FMT);
            }
        } catch (Exception ignored) {
        }
    }

    public static void capNhatDangNhapCuoi(String tenDangNhap) {
        String sql = "UPDATE TaiKhoan SET ngayDangNhapCuoi = ? WHERE tenDangNhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, LocalDateTime.now().format(DT_FMT));
            ps.setString(2, tenDangNhap);
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
    }


    // ===== Setter có ràng buộc =====
    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty())
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        if (!tenDangNhap.trim().matches("[a-zA-Z0-9_]{4,30}"))
            throw new IllegalArgumentException(
                "Tên đăng nhập chỉ chứa chữ cái, số, dấu gạch dưới, từ 4-30 ký tự.");
        this.tenDangNhap = tenDangNhap.trim().toLowerCase();
    }

    
    public void setMatKhauThuan(String matKhauThuan) {
        if (matKhauThuan == null || matKhauThuan.trim().isEmpty())
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        if (matKhauThuan.length() < 6)
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
        this.matKhau = hashSHA256(matKhauThuan);
    }

    public void setVaiTro(String vaiTro) {
        vaiTro = chuanHoaVaiTro(vaiTro);
        if (!VAI_TRO_ADMIN.equals(vaiTro)
                && !VAI_TRO_STAFF.equals(vaiTro)
                && !VAI_TRO_WAREHOUSE.equals(vaiTro)
                && !VAI_TRO_ACCOUNTANT.equals(vaiTro)) {
            throw new IllegalArgumentException("Vai trò phải là ADMIN, STAFF, WAREHOUSE hoặc ACCOUNTANT.");
        }
        this.vaiTro = vaiTro;
    }

    private static String chuanHoaVaiTro(String vaiTro) {
        if (vaiTro == null || vaiTro.trim().isEmpty()) return VAI_TRO_STAFF;
        String v = vaiTro.trim().toUpperCase();
        if (v.equals("QUẢN LÝ") || v.equals("QUAN LY") || v.equals("QL")) return VAI_TRO_ADMIN;
        if (v.equals("NHÂN VIÊN BÁN HÀNG") || v.equals("NHAN VIEN BAN HANG")) return VAI_TRO_STAFF;
        if (v.equals("NHÂN VIÊN KHO") || v.equals("NHAN VIEN KHO") || v.equals("KHO")) return VAI_TRO_WAREHOUSE;
        if (v.equals("KẾ TOÁN") || v.equals("KE TOAN")) return VAI_TRO_ACCOUNTANT;
        return v;
    }

    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty())
            throw new IllegalArgumentException("Họ tên không được để trống.");
        if (hoTen.trim().length() < 2 || hoTen.trim().length() > 100)
            throw new IllegalArgumentException("Họ tên phải từ 2 đến 100 ký tự.");
        this.hoTen = hoTen.trim();
    }

    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (!email.trim().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$"))
                throw new IllegalArgumentException("Email không hợp lệ.");
            this.email = email.trim().toLowerCase();
        } else {
            this.email = "";
        }
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    // ===== Đổi mật khẩu với xác minh mật khẩu cũ =====
    public void doiMatKhau(String matKhauCu, String matKhauMoi) {
        if (!hashSHA256(matKhauCu).equals(this.matKhau))
            throw new IllegalArgumentException("Mật khẩu cũ không đúng.");
        if (matKhauMoi == null || matKhauMoi.length() < 6)
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự.");
        if (matKhauCu.equals(matKhauMoi))
            throw new IllegalArgumentException("Mật khẩu mới phải khác mật khẩu cũ.");
        this.matKhau = hashSHA256(matKhauMoi);
    }

    // ===== Reset mật khẩu (chỉ ADMIN dùng) =====
    public void resetMatKhau(String matKhauMoi) {
        if (matKhauMoi == null || matKhauMoi.length() < 6)
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự.");
        this.matKhau = hashSHA256(matKhauMoi);
    }

    // ===== Hash SHA-256 =====
    public static String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi hash mật khẩu: " + e.getMessage());
        }
    }

    // ===== SQL: Đăng nhập (trả về TaiKhoan nếu đúng, null nếu sai) =====
    public static TaiKhoan dangNhap(String tenDangNhap, String matKhauThuan) {
        String matKhauHash = hashSHA256(matKhauThuan);
        String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap=? AND matKhau=? AND trangThai=1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap.trim().toLowerCase());
            ps.setString(2, matKhauHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TaiKhoan tk = mapFromResultSet(rs);
                    // Cập nhật ngày đăng nhập cuối
                    tk.capNhatNgayDangNhap();
                    return tk;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void capNhatNgayDangNhap() {
        this.ngayDangNhapCuoi = LocalDateTime.now();
        String sql = "UPDATE TaiKhoan SET ngayDangNhapCuoi=? WHERE maTaiKhoan=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, this.ngayDangNhapCuoi.format(DT_FMT));
            ps.setString(2, this.maTaiKhoan);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== SQL: Lưu tài khoản mới =====
    public void luuVaoSQL() {
        // Kiểm tra tên đăng nhập đã tồn tại chưa
        if (kiemTraTenDangNhapTonTai(this.tenDangNhap)) {
            throw new IllegalArgumentException("Tên đăng nhập '" + tenDangNhap + "' đã tồn tại.");
        }
        String sql = "INSERT INTO TaiKhoan VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTaiKhoan);
            ps.setString(2, tenDangNhap);
            ps.setString(3, matKhau);
            ps.setString(4, vaiTro);
            ps.setString(5, hoTen);
            ps.setString(6, email != null ? email : "");
            ps.setInt   (7, trangThai ? 1 : 0);
            ps.setString(8, ngayTao.format(DT_FMT));
            ps.setString(9, null);
            ps.executeUpdate();
            System.out.println("Tạo tài khoản thành công: " + tenDangNhap + " [" + vaiTro + "]");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi lưu tài khoản: " + e.getMessage(), e);
        }
    }

    // ===== SQL: Đọc tất cả =====
    public static List<TaiKhoan> docTuSQL() {
        List<TaiKhoan> ds = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan ORDER BY vaiTro, tenDangNhap";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(mapFromResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ===== SQL: Cập nhật thông tin (không đổi mật khẩu ở đây) =====
    public void capNhatSQL() {
        String sql = "UPDATE TaiKhoan SET hoTen=?, email=?, vaiTro=?, trangThai=? WHERE maTaiKhoan=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hoTen);
            ps.setString(2, email != null ? email : "");
            ps.setString(3, vaiTro);
            ps.setInt   (4, trangThai ? 1 : 0);
            ps.setString(5, maTaiKhoan);
            ps.executeUpdate();
            System.out.println("Cập nhật tài khoản: " + tenDangNhap);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi cập nhật tài khoản: " + e.getMessage(), e);
        }
    }

    // ===== SQL: Cập nhật mật khẩu (sau khi đã hash) =====
    public void capNhatMatKhauSQL() {
        String sql = "UPDATE TaiKhoan SET matKhau=? WHERE maTaiKhoan=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matKhau);
            ps.setString(2, maTaiKhoan);
            ps.executeUpdate();
            System.out.println("Cập nhật mật khẩu cho: " + tenDangNhap);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi cập nhật mật khẩu: " + e.getMessage(), e);
        }
    }

    // ===== SQL: Xóa tài khoản =====
    public static void xoaKhoiSQL(String maTaiKhoan) {
        String sql = "DELETE FROM TaiKhoan WHERE maTaiKhoan=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTaiKhoan);
            ps.executeUpdate();
            System.out.println("Xóa tài khoản: " + maTaiKhoan);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xóa tài khoản: " + e.getMessage(), e);
        }
    }

    // ===== Tiện ích =====
    private static boolean kiemTraTenDangNhapTonTai(String tenDangNhap) {
        String sql = "SELECT COUNT(*) FROM TaiKhoan WHERE tenDangNhap=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static TaiKhoan mapFromResultSet(ResultSet rs) throws Exception {
        TaiKhoan tk = new TaiKhoan();
        tk.maTaiKhoan    = rs.getString("maTaiKhoan");
        tk.tenDangNhap   = rs.getString("tenDangNhap");
        tk.matKhau       = rs.getString("matKhau");
        tk.vaiTro        = rs.getString("vaiTro");
        tk.hoTen         = rs.getString("hoTen");
        tk.email         = rs.getString("email");
        tk.trangThai     = rs.getInt("trangThai") == 1;
        String ngayTaoStr = rs.getString("ngayTao");
        if (ngayTaoStr != null) tk.ngayTao = LocalDateTime.parse(ngayTaoStr, DT_FMT);
        String ngayCuoi = rs.getString("ngayDangNhapCuoi");
        if (ngayCuoi != null) tk.ngayDangNhapCuoi = LocalDateTime.parse(ngayCuoi, DT_FMT);
        return tk;
    }

    @Override
    public String toString() {
        return "[" + vaiTro + "] " + tenDangNhap + " - " + hoTen;
    }
}