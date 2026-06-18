package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import database.DBConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;

public class SanPham {

    public static final double TY_LE_LAI_BAN = 0.35;

    private String maSanPham;
    private String tenSanPham;
    private String kichCo;
    private String donVi;
    private double giaNhap;
    private double giaBan;
    private double giamGiaBan;
    private int soLuongTon;
    private int soLuongDaBan;
    private String loaiSanPham;
    private String thuongHieu;
    private String xuatXu;
    private String maNCC;
    private String moTa;
    private String trangThai;

    private static String taoMaTuDong() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "SP-" + uuid;
    }

    public SanPham() {
        this.maSanPham = taoMaTuDong();
        this.kichCo = "M";
        this.donVi = "cái";
        this.soLuongTon = 0;
        this.soLuongDaBan = 0;
        this.giamGiaBan = 0;
        this.moTa = "";
        this.trangThai = "Đang bán";
    }

    public SanPham(String tenSanPham, String donVi, double giaNhap, double giaBan,
                   int soLuongTon, String loaiSanPham, String thuongHieu,
                   String xuatXu, String maNCC, String moTa) {
        this();
        setTenSanPham(tenSanPham);
        setKichCo("M");
        setDonVi(donVi);
        setGiaNhap(giaNhap);
        setGiaBan(giaBan);
        setSoLuongTon(soLuongTon);
        setLoaiSanPham(loaiSanPham);
        setThuongHieu(thuongHieu);
        setXuatXu(xuatXu);
        setMaNCC(maNCC);
        setMoTa(moTa);
    }

    public SanPham(String maSanPham, String tenSanPham, String donVi,
                   double giaNhap, double giaBan, int soLuongTon,
                   String loaiSanPham, String thuongHieu, String xuatXu,
                   String maNCC, String moTa) {
        this(maSanPham, tenSanPham, "M", donVi, giaNhap, giaBan, 0, soLuongTon,
                loaiSanPham, thuongHieu, xuatXu, maNCC, moTa);
    }

    public SanPham(String maSanPham, String tenSanPham, String donVi,
                   double giaNhap, double giaBan, double giamGiaBan, int soLuongTon,
                   String loaiSanPham, String thuongHieu, String xuatXu,
                   String maNCC, String moTa) {
        this(maSanPham, tenSanPham, "M", donVi, giaNhap, giaBan, giamGiaBan, soLuongTon,
                loaiSanPham, thuongHieu, xuatXu, maNCC, moTa);
    }

    public SanPham(String maSanPham, String tenSanPham, String kichCo, String donVi,
                   double giaNhap, double giaBan, double giamGiaBan, int soLuongTon,
                   String loaiSanPham, String thuongHieu, String xuatXu,
                   String maNCC, String moTa) {
        setMaSanPham(maSanPham);
        setTenSanPham(tenSanPham);
        setKichCo(kichCo);
        setDonVi(donVi);
        setGiaNhap(giaNhap);
        setGiamGiaBan(giamGiaBan);
        setGiaBan(giaBan);
        setSoLuongTon(soLuongTon);
        setLoaiSanPham(loaiSanPham);
        setThuongHieu(thuongHieu);
        setXuatXu(xuatXu);
        setMaNCC(maNCC);
        setMoTa(moTa);
    }

    public String getMaSanPham() { return maSanPham; }
    public String getTenSanPham() { return tenSanPham; }
    public String getKichCo() { return chuanHoaKichCo(kichCo); }
    public String getDonVi() { return donVi; }
    public double getGiaNhap() { return giaNhap; }
    public double getGiaBan() { return giaBan; }
    public double getGiamGiaBan() { return giamGiaBan; }
    public int getSoLuongTon() { return soLuongTon; }
    public int getSoLuongDaBan() { return Math.max(0, soLuongDaBan); }
    public String getLoaiSanPham() { return loaiSanPham; }
    public String getThuongHieu() { return thuongHieu; }
    public String getXuatXu() { return xuatXu; }
    public String getMaNCC() { return maNCC; }
    public String getMoTa() { return moTa; }
    public String getTrangThai() { return trangThai == null || trangThai.isBlank() ? "Đang bán" : trangThai; }

    public String getTrangThaiHienThi() {
        if ("Ngừng bán".equalsIgnoreCase(getTrangThai())) {
            return "Ngừng bán";
        }
        if (soLuongTon == 0) {
            return "Hết hàng";
        }
        return "Còn hàng";
    }

    public String getTenSanPhamKemKichCo() {
        return getTenSanPham() + " - Size " + getKichCo();
    }

    public void setMaSanPham(String maSanPham) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) throw new IllegalArgumentException("Mã sản phẩm không được để trống.");
        maSanPham = maSanPham.trim().toUpperCase();
        if (!maSanPham.matches("SP-[A-Z0-9]{6}") && !maSanPham.matches("SP-[0-9]{3,6}")) throw new IllegalArgumentException("Mã sản phẩm không hợp lệ.");
        this.maSanPham = maSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        if (tenSanPham == null || tenSanPham.trim().isEmpty()) throw new IllegalArgumentException("Tên sản phẩm không được để trống.");
        this.tenSanPham = tenSanPham.trim();
    }

    public static String chuanHoaKichCo(String kichCo) {
        if (kichCo == null || kichCo.trim().isEmpty()) return "M";
        return kichCo.trim().toUpperCase();
    }

    public void setKichCo(String kichCo) {
        kichCo = chuanHoaKichCo(kichCo);
        if (kichCo.length() > 20) throw new IllegalArgumentException("Kích cỡ không được vượt quá 20 ký tự.");
        if (!kichCo.matches("[A-Z0-9./+-]+")) {
            throw new IllegalArgumentException("Kích cỡ chỉ được chứa chữ, số và ký tự ./+- . Ví dụ: S, M, L, XL, 30, 31, 40, FREESIZE.");
        }
        this.kichCo = kichCo;
    }

    public void setDonVi(String donVi) {
        if (donVi == null || donVi.trim().isEmpty()) donVi = "cái";
        donVi = donVi.trim();
        if (donVi.length() > 50) throw new IllegalArgumentException("Đơn vị không được vượt quá 50 ký tự.");
        this.donVi = donVi;
    }

    public void setGiaNhap(double giaNhap) {
        if (giaNhap < 0) throw new IllegalArgumentException("Giá nhập không được âm.");
        this.giaNhap = giaNhap;
    }

    public void setGiaBan(double giaBan) {
        if (giaBan < 0) throw new IllegalArgumentException("Giá bán không được âm.");
        this.giaBan = giaBan;
    }

    public void setGiamGiaBan(double giamGiaBan) {
        if (giamGiaBan < 0 || giamGiaBan > 100) throw new IllegalArgumentException("Giảm giá bán phải từ 0 đến 100%.");
        this.giamGiaBan = giamGiaBan;
    }

    public double getGiaBanSauGiam() { return Math.round(giaBan * (100.0 - giamGiaBan) / 100.0); }
    public double tinhGiaBanTuDong() { return Math.round(giaNhap * (1.0 + TY_LE_LAI_BAN)); }
    public void apDungGiaBanTuDong() { this.giaBan = tinhGiaBanTuDong(); }

    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0) throw new IllegalArgumentException("Số lượng tồn không được âm.");
        this.soLuongTon = soLuongTon;
    }

    public void setSoLuongDaBan(int soLuongDaBan) {
        this.soLuongDaBan = Math.max(0, soLuongDaBan);
    }

    public void setLoaiSanPham(String loaiSanPham) {
        if (loaiSanPham == null || loaiSanPham.trim().isEmpty()) throw new IllegalArgumentException("Loại sản phẩm không được để trống.");
        this.loaiSanPham = loaiSanPham.trim();
    }

    public void setThuongHieu(String thuongHieu) {
        if (thuongHieu == null || thuongHieu.trim().isEmpty()) throw new IllegalArgumentException("Thương hiệu không được để trống.");
        this.thuongHieu = thuongHieu.trim();
    }

    public void setXuatXu(String xuatXu) {
        if (xuatXu == null || xuatXu.trim().isEmpty()) throw new IllegalArgumentException("Xuất xứ không được để trống.");
        this.xuatXu = xuatXu.trim();
    }

    public void setMaNCC(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty()) throw new IllegalArgumentException("Mã nhà cung cấp không được để trống.");
        this.maNCC = maNCC.trim().toUpperCase();
    }

    public void setMoTa(String moTa) { this.moTa = moTa == null ? "" : moTa.trim(); }

    public void setTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            this.trangThai = "Đang bán";
            return;
        }
        trangThai = trangThai.trim();
        if (!trangThai.equals("Đang bán") && !trangThai.equals("Ngừng bán")) {
            throw new IllegalArgumentException("Trạng thái sản phẩm chỉ được là Đang bán hoặc Ngừng bán.");
        }
        this.trangThai = trangThai;
    }

    public void nhapThem(int soLuong) { if (soLuong <= 0) throw new IllegalArgumentException("Số lượng nhập thêm phải lớn hơn 0."); this.soLuongTon += soLuong; }
    public void xuatHang(int soLuong) { if (soLuong <= 0) throw new IllegalArgumentException("Số lượng xuất phải lớn hơn 0."); if (soLuong > this.soLuongTon) throw new IllegalArgumentException("Số lượng xuất vượt quá tồn kho."); this.soLuongTon -= soLuong; }
    public double tinhLoiNhuan() { return getGiaBanSauGiam() - giaNhap; }
    public boolean conHang() { return soLuongTon > 0; }

    public void nhapThongTin() {}
    public void suaThongTin() {}
    public void xuatThongTin() {}

    public void luuVaoSQL() throws Exception {
        String sqlInsertSp = """
                INSERT INTO SanPham
                (maSanPham, tenSanPham, donVi, giaNhap, giaBan, giamGiaBan, soLuongTon,
                 loaiSanPham, thuongHieu, xuatXu, maNCC, moTa)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String sqlInsertSize = """
                MERGE SanPhamKichCo AS target
                USING (SELECT ? AS maSanPham, ? AS kichCo) AS src
                ON target.maSanPham = src.maSanPham AND target.kichCo = src.kichCo
                WHEN MATCHED THEN UPDATE SET soLuongTon = ?
                WHEN NOT MATCHED THEN INSERT(maSanPham, kichCo, soLuongTon) VALUES(?, ?, ?);
                """;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertSp)) {
                    ps.setString(1, maSanPham);
                    ps.setString(2, tenSanPham);
                    ps.setString(3, donVi);
                    ps.setDouble(4, giaNhap);
                    ps.setDouble(5, giaBan);
                    ps.setDouble(6, giamGiaBan);
                    ps.setInt(7, soLuongTon);
                    ps.setString(8, loaiSanPham);
                    ps.setString(9, thuongHieu);
                    ps.setString(10, xuatXu);
                    ps.setString(11, maNCC);
                    ps.setString(12, moTa);
                    ps.executeUpdate();
                }
                upsertSize(conn, sqlInsertSize, maSanPham, getKichCo(), soLuongTon);
                dongBoTongTon(conn, maSanPham);
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static void upsertSize(Connection conn, String sql, String maSP, String kichCo, int soLuongTon) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            ps.setString(2, chuanHoaKichCo(kichCo));
            ps.setInt(3, soLuongTon);
            ps.setString(4, maSP);
            ps.setString(5, chuanHoaKichCo(kichCo));
            ps.setInt(6, soLuongTon);
            ps.executeUpdate();
        }
    }

    private static void upsertSizeTrangThai(Connection conn, String sql, String maSP, String kichCo, int soLuongTon, String trangThai) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String kc = chuanHoaKichCo(kichCo);
            String tt = (trangThai == null || trangThai.isBlank()) ? "Đang bán" : trangThai.trim();
            ps.setString(1, maSP);
            ps.setString(2, kc);
            ps.setInt(3, soLuongTon);
            ps.setString(4, tt);
            ps.setString(5, maSP);
            ps.setString(6, kc);
            ps.setInt(7, soLuongTon);
            ps.setString(8, tt);
            ps.executeUpdate();
        }
    }

    private static void dongBoTongTon(Connection conn, String maSP) throws Exception {
        String sql = """
                UPDATE SanPham
                SET soLuongTon = ISNULL((SELECT SUM(soLuongTon) FROM SanPhamKichCo WHERE maSanPham = ?), 0)
                WHERE maSanPham = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            ps.setString(2, maSP);
            ps.executeUpdate();
        }
    }

    private static SanPham mapSanPham(ResultSet rs) throws Exception {
        SanPham sp = new SanPham(
                rs.getString("maSanPham"),
                rs.getString("tenSanPham"),
                rs.getString("kichCo"),
                rs.getString("donVi"),
                rs.getDouble("giaNhap"),
                rs.getDouble("giaBan"),
                rs.getDouble("giamGiaBan"),
                rs.getInt("soLuongTon"),
                rs.getString("loaiSanPham"),
                rs.getString("thuongHieu"),
                rs.getString("xuatXu"),
                rs.getString("maNCC"),
                rs.getString("moTa")
        );
        try {
            sp.setSoLuongDaBan(rs.getInt("soLuongDaBan"));
        } catch (Exception ignored) {
            sp.setSoLuongDaBan(0);
        }
        try {
            sp.setTrangThai(rs.getString("trangThai"));
        } catch (Exception ignored) {
            sp.setTrangThai("Đang bán");
        }
        return sp;
    }

    private static String selectSanPhamSizeBase() {
        return """
                WITH tra AS (
                    SELECT maHoaDon, maSanPham, ISNULL(kichCo, 'M') AS kichCo, SUM(soLuongTra) AS soLuongTra
                    FROM PhieuTraHang
                    GROUP BY maHoaDon, maSanPham, ISNULL(kichCo, 'M')
                ), daBan AS (
                    SELECT ct.maSanPham, ISNULL(ct.kichCo, 'M') AS kichCo,
                           SUM(CASE WHEN hd.trangThai IN (N'Đã thanh toán', N'Trả một phần hàng')
                                    THEN CASE WHEN ct.soLuong - ISNULL(tra.soLuongTra, 0) < 0 THEN 0 ELSE ct.soLuong - ISNULL(tra.soLuongTra, 0) END
                                    ELSE 0 END) AS soLuongDaBan
                    FROM ChiTietHoaDon ct
                    JOIN HoaDon hd ON hd.maHoaDon = ct.maHoaDon
                    LEFT JOIN tra ON tra.maHoaDon = ct.maHoaDon AND tra.maSanPham = ct.maSanPham AND tra.kichCo = ISNULL(ct.kichCo, 'M')
                    GROUP BY ct.maSanPham, ISNULL(ct.kichCo, 'M')
                )
                SELECT sp.maSanPham, sp.tenSanPham, sk.kichCo, sp.donVi, sp.giaNhap, sp.giaBan,
                       ISNULL(sp.giamGiaBan, 0) AS giamGiaBan, ISNULL(sk.soLuongTon, 0) AS soLuongTon,
                       ISNULL(daBan.soLuongDaBan, 0) AS soLuongDaBan,
                       sp.loaiSanPham, sp.thuongHieu, sp.xuatXu, sp.maNCC, ISNULL(sp.moTa, '') AS moTa,
                       ISNULL(sk.trangThai, N'Đang bán') AS trangThai
                FROM SanPham sp
                INNER JOIN SanPhamKichCo sk ON sp.maSanPham = sk.maSanPham
                LEFT JOIN daBan ON daBan.maSanPham = sk.maSanPham AND daBan.kichCo = sk.kichCo
                """;
    }

    public static List<SanPham> docTuSQL() {
        List<SanPham> ds = new ArrayList<>();
        String sql = selectSanPhamSizeBase() + " ORDER BY sp.maSanPham, sk.kichCo";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ds.add(mapSanPham(rs));
        } catch (Exception e) { System.out.println("Đọc danh sách sản phẩm thất bại! Lỗi: " + e.getMessage()); }
        return ds;
    }

    public void capNhatSQL() throws Exception {
        capNhatSQL(getKichCo());
    }

    public void capNhatSQL(String kichCoCu) throws Exception {
        kichCoCu = chuanHoaKichCo(kichCoCu);

        
        String sqlSp = """
                UPDATE SanPham
                SET tenSanPham=?, donVi=?, giaBan=?, giamGiaBan=?,
                    loaiSanPham=?, thuongHieu=?, xuatXu=?, maNCC=?, moTa=?
                WHERE maSanPham=?
                """;

        String sqlDeleteOldSize = "DELETE FROM SanPhamKichCo WHERE maSanPham = ? AND kichCo = ? AND kichCo <> ?";
        String sqlMergeSize = """
                MERGE SanPhamKichCo AS target
                USING (SELECT ? AS maSanPham, ? AS kichCo) AS src
                ON target.maSanPham = src.maSanPham AND target.kichCo = src.kichCo
                WHEN MATCHED THEN UPDATE SET soLuongTon = ?, trangThai = ?
                WHEN NOT MATCHED THEN INSERT(maSanPham, kichCo, soLuongTon, trangThai) VALUES(?, ?, ?, ?);
                """;
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlSp)) {
                    ps.setString(1, tenSanPham);
                    ps.setString(2, donVi);
                    ps.setDouble(3, giaBan);
                    ps.setDouble(4, giamGiaBan);
                    ps.setString(5, loaiSanPham);
                    ps.setString(6, thuongHieu);
                    ps.setString(7, xuatXu);
                    ps.setString(8, maNCC);
                    ps.setString(9, moTa);
                    ps.setString(10, maSanPham);
                    ps.executeUpdate();
                }
                upsertSizeTrangThai(conn, sqlMergeSize, maSanPham, getKichCo(), soLuongTon, getTrangThai());
                try (PreparedStatement ps = conn.prepareStatement(sqlDeleteOldSize)) {
                    ps.setString(1, maSanPham);
                    ps.setString(2, kichCoCu);
                    ps.setString(3, getKichCo());
                    ps.executeUpdate();
                }
                dongBoTongTon(conn, maSanPham);
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void capNhatSoLuongSQL() {
        String sql = "UPDATE SanPhamKichCo SET soLuongTon = ? WHERE maSanPham = ? AND kichCo = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, soLuongTon);
                ps.setString(2, maSanPham);
                ps.setString(3, getKichCo());
                ps.executeUpdate();
                dongBoTongTon(conn, maSanPham);
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) { System.out.println("Cập nhật số lượng tồn theo kích cỡ thất bại! Lỗi: " + e.getMessage()); }
    }

    public static void xoaKhoiSQL(String maSanPham, String kichCo) throws Exception {
        maSanPham = maSanPham == null ? "" : maSanPham.trim().toUpperCase();
        kichCo = chuanHoaKichCo(kichCo);

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);
            try {
                boolean daPhatSinh = daPhatSinhGiaoDich(conn, maSanPham, kichCo);

                if (daPhatSinh) {
                    
                    try (PreparedStatement ps = conn.prepareStatement("""
                            UPDATE SanPhamKichCo
                            SET trangThai = N'Ngừng bán', soLuongTon = 0
                            WHERE maSanPham = ? AND kichCo = ?
                            """)) {
                        ps.setString(1, maSanPham);
                        ps.setString(2, kichCo);
                        ps.executeUpdate();
                    }
                    dongBoTongTon(conn, maSanPham);
                } else {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM SanPhamKichCo WHERE maSanPham = ? AND kichCo = ?")) {
                        ps.setString(1, maSanPham);
                        ps.setString(2, kichCo);
                        ps.executeUpdate();
                    }
                    int conSize = 0;
                    try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM SanPhamKichCo WHERE maSanPham = ?")) {
                        ps.setString(1, maSanPham);
                        try (ResultSet rs = ps.executeQuery()) { if (rs.next()) conSize = rs.getInt(1); }
                    }
                    if (conSize == 0) {
                        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM SanPham WHERE maSanPham = ?")) {
                            ps.setString(1, maSanPham);
                            ps.executeUpdate();
                        }
                    } else {
                        dongBoTongTon(conn, maSanPham);
                    }
                }
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public static void xoaKhoiSQL(String maSanPham) throws Exception {
        maSanPham = maSanPham == null ? "" : maSanPham.trim().toUpperCase();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);
            try {
                boolean daPhatSinh = daPhatSinhGiaoDich(conn, maSanPham, null);
                if (daPhatSinh) {
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE SanPhamKichCo SET trangThai = N'Ngừng bán', soLuongTon = 0 WHERE maSanPham = ?")) {
                        ps.setString(1, maSanPham);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE SanPhamKichCo SET soLuongTon = 0 WHERE maSanPham = ?")) {
                        ps.setString(1, maSanPham);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM SanPham WHERE maSanPham = ?")) {
                        ps.setString(1, maSanPham);
                        ps.executeUpdate();
                    }
                }
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static boolean daPhatSinhGiaoDich(Connection conn, String maSanPham, String kichCo) throws Exception {
        String sqlHoaDon;
        String sqlNhapHang;
        if (kichCo == null || kichCo.isBlank()) {
            sqlHoaDon = "SELECT COUNT(*) FROM ChiTietHoaDon WHERE maSanPham = ?";
            sqlNhapHang = "SELECT COUNT(*) FROM NhapHang WHERE maSanPham = ?";
        } else {
            sqlHoaDon = "SELECT COUNT(*) FROM ChiTietHoaDon WHERE maSanPham = ? AND kichCo = ?";
            sqlNhapHang = "SELECT COUNT(*) FROM NhapHang WHERE maSanPham = ? AND kichCo = ?";
        }

        try (PreparedStatement ps = conn.prepareStatement(sqlHoaDon)) {
            ps.setString(1, maSanPham);
            if (kichCo != null && !kichCo.isBlank()) ps.setString(2, kichCo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) return true;
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(sqlNhapHang)) {
            ps.setString(1, maSanPham);
            if (kichCo != null && !kichCo.isBlank()) ps.setString(2, kichCo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static void khoiPhucSanPhamSQL(String maSanPham, String kichCo) throws Exception {
        maSanPham = maSanPham == null ? "" : maSanPham.trim().toUpperCase();
        kichCo = chuanHoaKichCo(kichCo);
        if (maSanPham.isBlank()) throw new IllegalArgumentException("Mã sản phẩm không hợp lệ.");

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement("""
                        UPDATE SanPhamKichCo
                        SET trangThai = N'Đang bán'
                        WHERE maSanPham = ? AND kichCo = ?
                        """)) {
                    ps.setString(1, maSanPham);
                    ps.setString(2, kichCo);
                    int rows = ps.executeUpdate();
                    if (rows == 0) {
                        throw new IllegalArgumentException("Không tìm thấy sản phẩm " + maSanPham + " size " + kichCo + ".");
                    }
                }
                dongBoTongTon(conn, maSanPham);
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public static List<SanPham> timKiemSQL(String keyword) {
        List<SanPham> ds = new ArrayList<>();
        String sql = selectSanPhamSizeBase() + """
                WHERE sp.maSanPham LIKE ? OR sp.tenSanPham LIKE ? OR sk.kichCo LIKE ? OR sp.loaiSanPham LIKE ?
                   OR sp.thuongHieu LIKE ? OR sp.xuatXu LIKE ? OR sp.maNCC LIKE ? OR sk.trangThai LIKE ?
                ORDER BY sp.maSanPham, sk.kichCo
                """;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String key = "%" + keyword + "%";
            for (int i = 1; i <= 8; i++) ps.setString(i, key);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) ds.add(mapSanPham(rs)); }
        } catch (Exception e) { System.out.println("Tìm kiếm sản phẩm thất bại! Lỗi: " + e.getMessage()); }
        return ds;
    }

    public static List<SanPham> timHangSapHetSQL(int nguongCanhBao) {
        List<SanPham> ds = new ArrayList<>();
        String sql = selectSanPhamSizeBase() + " WHERE sk.soLuongTon <= ? AND ISNULL(sk.trangThai, N'Đang bán') = N'Đang bán' ORDER BY sk.soLuongTon ASC, sp.maSanPham";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nguongCanhBao); try (ResultSet rs = ps.executeQuery()) { while (rs.next()) ds.add(mapSanPham(rs)); }
        } catch (Exception e) { System.out.println("Tìm sản phẩm sắp hết thất bại! Lỗi: " + e.getMessage()); }
        return ds;
    }

    public static void nhapHangSQL(String thangNhap, String maNCC, String nguoiLienHe, String maSanPham,
                                   String tenSanPham, String kichCo, int soLuongNhap, double giaNhap) throws Exception {
        if (soLuongNhap <= 0) throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0.");
        if (giaNhap < 0) throw new IllegalArgumentException("Giá nhập không được âm.");
        kichCo = chuanHoaKichCo(kichCo);

        String sqlInsertNhap = """
                INSERT INTO NhapHang(ngayNhap, maNCC, maSanPham, kichCo, soLuongNhap, giaNhap)
                VALUES (GETDATE(), ?, ?, ?, ?, ?)
                """;

        String sqlUpdateSp = """
                UPDATE SanPham
                SET tenSanPham = ?, maNCC = ?, giaNhap = ?
                WHERE maSanPham = ?
                """;

        String sqlMergeSize = """
                MERGE SanPhamKichCo AS target
                USING (SELECT ? AS maSanPham, ? AS kichCo) AS src
                ON target.maSanPham = src.maSanPham AND target.kichCo = src.kichCo
                WHEN MATCHED THEN UPDATE SET soLuongTon = target.soLuongTon + ?, trangThai = N'Đang bán'
                WHEN NOT MATCHED THEN INSERT(maSanPham, kichCo, soLuongTon, trangThai) VALUES(?, ?, ?, N'Đang bán');
                """;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlInsertNhap);
                 PreparedStatement ps2 = conn.prepareStatement(sqlUpdateSp);
                 PreparedStatement ps3 = conn.prepareStatement(sqlMergeSize)) {

                ps1.setString(1, maNCC);
                ps1.setString(2, maSanPham);
                ps1.setString(3, kichCo);
                ps1.setInt(4, soLuongNhap);
                ps1.setDouble(5, giaNhap);
                ps1.executeUpdate();

                ps2.setString(1, tenSanPham);
                ps2.setString(2, maNCC);
                ps2.setDouble(3, giaNhap);
                ps2.setString(4, maSanPham);
                int rows = ps2.executeUpdate();
                if (rows == 0) throw new IllegalArgumentException("Không tìm thấy sản phẩm để nhập hàng: " + maSanPham);

                ps3.setString(1, maSanPham);
                ps3.setString(2, kichCo);
                ps3.setInt(3, soLuongNhap);
                ps3.setString(4, maSanPham);
                ps3.setString(5, kichCo);
                ps3.setInt(6, soLuongNhap);
                ps3.executeUpdate();

                dongBoTongTon(conn, maSanPham);
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public static void nhapHangSQL(String thangNhap, String maNCC, String nguoiLienHe, String maSanPham,
                                   String tenSanPham, int soLuongNhap, double giaNhap) throws Exception {
        nhapHangSQL(thangNhap, maNCC, nguoiLienHe, maSanPham, tenSanPham, "M", soLuongNhap, giaNhap);
    }

    private void nhapCoRangBuoc(Scanner sc, String prompt, Consumer<String> setter) {
        while (true) { System.out.print(prompt); try { setter.accept(sc.nextLine()); break; } catch (Exception e) { System.out.println("[Lỗi] " + e.getMessage()); } }
    }
}
