package model;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HoaDon {

    public static final String TRANG_THAI_CHO = "Chờ thanh toán";
    public static final String TRANG_THAI_DA_THANH = "Đã thanh toán";
    public static final String TRANG_THAI_TRA_MOT_PHAN = "Trả một phần hàng";
    public static final String TRANG_THAI_HET_HANG = "Hết hàng";
    public static final String TRANG_THAI_HUY = "Đã hủy";


    // Quy ước mới: 1 điểm = 1.000 VNĐ, 1 sản phẩm mua = 10 điểm tích lũy.
    public static final int TIEN_MOI_DIEM = 1000;
    public static final int DIEM_MOI_SAN_PHAM = 10;

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private String maHoaDon;
    private String maKhachHang;
    private String maNhanVien;
    private LocalDateTime ngayLap;
    private double giamGia;
    private boolean coSuDungDiem;
    private int diemSuDung;
    private double tienDoiDiem;
    private String trangThai;
    private LocalDateTime ngayThanhToan;
    private String ghiChu;


    // key = maSanPham|kichCo, value[0] = soLuong, value[1] = donGia
    private final Map<String, double[]> chiTiet = new LinkedHashMap<>();

    // key = maSanPham|kichCo, value[0] = soLuongTra, value[1] = donGia, value[2] = tienHoanTra thực tế
    private final Map<String, double[]> chiTietTraHang = new LinkedHashMap<>();

    private static String taoMaTuDong() {
        String uuid = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        return "HD-" + uuid;
    }

    public HoaDon() {
        this.maHoaDon = taoMaTuDong();
        this.ngayLap = LocalDateTime.now();
        this.giamGia = 0;
        this.coSuDungDiem = false;
        this.diemSuDung = 0;
        this.tienDoiDiem = 0;
        this.trangThai = TRANG_THAI_CHO;
        this.ghiChu = "";
    }

    public HoaDon(String maKhachHang, String maNhanVien) {
        this();
        setMaKhachHang(maKhachHang);
        setMaNhanVien(maNhanVien);
    }

    public HoaDon(String maHoaDon,
                  String maKhachHang,
                  String maNhanVien,
                  LocalDateTime ngayLap,
                  double giamGia,
                  String trangThai,
                  String ghiChu) {
        this(maHoaDon, maKhachHang, maNhanVien, ngayLap, giamGia,
                false, 0, 0, trangThai, null, ghiChu);
    }

    public HoaDon(String maHoaDon,
                  String maKhachHang,
                  String maNhanVien,
                  LocalDateTime ngayLap,
                  double giamGia,
                  boolean coSuDungDiem,
                  int diemSuDung,
                  double tienDoiDiem,
                  String trangThai,
                  LocalDateTime ngayThanhToan,
                  String ghiChu) {

        setMaHoaDon(maHoaDon);
        setMaKhachHang(maKhachHang);
        setMaNhanVien(maNhanVien);

        this.ngayLap = ngayLap != null ? ngayLap : LocalDateTime.now();
        this.ngayThanhToan = ngayThanhToan;

        setGiamGiaKhongCanTongTien(giamGia);
        setDiemTichLuyDaDung(coSuDungDiem, diemSuDung, tienDoiDiem);
        setTrangThai(trangThai);
        setGhiChu(ghiChu);
    }

    public String getMaHoaDon() { return maHoaDon; }
    public String getMaKhachHang() { return maKhachHang; }
    public String getMaNhanVien() { return maNhanVien; }
    public LocalDateTime getNgayLap() { return ngayLap; }
    public double getGiamGia() { return giamGia; }
    public boolean isCoSuDungDiem() { return coSuDungDiem; }
    public int getDiemSuDung() { return diemSuDung; }
    public double getTienDoiDiem() { return tienDoiDiem; }
    public String getTrangThai() { return trangThai; }
    public LocalDateTime getNgayThanhToan() { return ngayThanhToan; }
    public String getGhiChu() { return ghiChu; }

    public String getNgayLapText() {
        return ngayLap != null ? ngayLap.format(DT_FMT) : "";
    }

    public String getNgayThanhToanText() {
        return ngayThanhToan != null ? ngayThanhToan.format(DT_FMT) : "";
    }

    public double getTongTien() {
        double tong = 0;
        for (double[] ct : chiTiet.values()) {
            tong += ct[0] * ct[1];
        }
        return tong;
    }

    public int getTongSoLuongSanPham() {
        int tong = 0;
        for (double[] ct : chiTiet.values()) {
            tong += (int) ct[0];
        }
        return tong;
    }

    public int getDiemCongSauThanhToan() {
        return getTongSoLuongSanPham() * DIEM_MOI_SAN_PHAM;
    }

    public static double lamTronTienThanhToan(double soTien) {
        if (soTien <= 0) return 0;
        return Math.ceil(soTien / 1000.0) * 1000.0;
    }

    public static int tinhSoDiemCanDung(double soTienCanThanhToan, int diemHienCo) {
        if (diemHienCo <= 0 || soTienCanThanhToan <= 0) return 0;
        double soTienLamTron = lamTronTienThanhToan(soTienCanThanhToan);
        int diemCanDung = (int) Math.ceil(soTienLamTron / TIEN_MOI_DIEM);
        return Math.min(diemHienCo, Math.max(0, diemCanDung));
    }

    public double getThanhToan() {
        return lamTronTienThanhToan(Math.max(0, getTongTien() - giamGia - tienDoiDiem));
    }

    public Map<String, double[]> getChiTiet() {
        return Collections.unmodifiableMap(chiTiet);
    }

    public Map<String, double[]> getChiTietTraHang() {
        return Collections.unmodifiableMap(chiTietTraHang);
    }

    public Map<String, double[]> getChiTietConLaiSauTraHang() {
        Map<String, double[]> ketQua = new LinkedHashMap<>();
        for (Map.Entry<String, double[]> e : chiTiet.entrySet()) {
            String key = e.getKey();
            int soLuongGoc = (int) e.getValue()[0];
            double donGia = e.getValue()[1];
            double[] tra = chiTietTraHang.get(key);
            int daTra = tra == null ? 0 : (int) tra[0];
            int conLai = Math.max(0, soLuongGoc - daTra);
            if (conLai > 0) {
                ketQua.put(key, new double[]{conLai, donGia});
            }
        }
        return Collections.unmodifiableMap(ketQua);
    }

    public double getTongTienTraHangGoc() {
        double tong = 0;
        for (double[] ct : chiTietTraHang.values()) {
            tong += ct[0] * ct[1];
        }
        return tong;
    }

    public double getTienHoanTra() {
        double tong = 0;
        for (double[] ct : chiTietTraHang.values()) {
            tong += ct.length >= 3 ? ct[2] : ct[0] * ct[1];
        }
        return tong;
    }

    public double getTongTienSauTraHang() {
        return Math.max(0, getTongTien() - getTongTienTraHangGoc());
    }

    public double getThanhToanSauTraHang() {
        return lamTronTienThanhToan(Math.max(0, getThanhToan() - getTienHoanTra()));
    }

    public int getTongSoLuongSanPhamSauTraHang() {
        int tong = 0;
        for (double[] ct : getChiTietConLaiSauTraHang().values()) {
            tong += (int) ct[0];
        }
        return tong;
    }

    public static String taoChiTietKey(String maSanPham, String kichCo) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống.");
        }
        kichCo = kichCo == null || kichCo.trim().isEmpty() ? "M" : kichCo.trim().toUpperCase();
        return maSanPham.trim().toUpperCase() + "|" + kichCo;
    }

    public static String layMaSanPhamTuKey(String key) {
        if (key == null) return "";
        int idx = key.indexOf('|');
        return idx >= 0 ? key.substring(0, idx) : key;
    }

    public static String layKichCoTuKey(String key) {
        if (key == null) return "M";
        int idx = key.indexOf('|');
        return idx >= 0 ? key.substring(idx + 1) : "M";
    }

    public void clearChiTiet() {
        kiemTraTrangThaiCho();
        chiTiet.clear();
    }

    public void setMaHoaDon(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống.");
        }
        maHoaDon = maHoaDon.trim().toUpperCase();
        if (!maHoaDon.matches("HD-[A-Z0-9]{6}") && !maHoaDon.matches("HD-[0-9]{6}")) {
            throw new IllegalArgumentException("Mã hóa đơn không hợp lệ.");
        }
        this.maHoaDon = maHoaDon;
    }

    public void setMaKhachHang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khách hàng không được để trống.");
        }
        this.maKhachHang = maKhachHang.trim().toUpperCase();
    }

    public void setMaNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống.");
        }
        this.maNhanVien = maNhanVien.trim().toUpperCase();
    }

    public void setGiamGia(double giamGia) {
        if (giamGia < 0) {
            throw new IllegalArgumentException("Giảm giá không được âm.");
        }
        if (giamGia > getTongTien()) {
            throw new IllegalArgumentException("Giảm giá không được vượt quá tổng tiền.");
        }
        this.giamGia = giamGia;
    }

    private void setGiamGiaKhongCanTongTien(double giamGia) {
        if (giamGia < 0) {
            throw new IllegalArgumentException("Giảm giá không được âm.");
        }
        this.giamGia = giamGia;
    }

    public void setDiemTichLuyDaDung(boolean coSuDungDiem, int diemSuDung, double tienDoiDiem) {
        if (diemSuDung < 0) {
            throw new IllegalArgumentException("Điểm sử dụng không được âm.");
        }
        if (tienDoiDiem < 0) {
            throw new IllegalArgumentException("Tiền đổi điểm không được âm.");
        }
        this.coSuDungDiem = coSuDungDiem && diemSuDung > 0;
        this.diemSuDung = this.coSuDungDiem ? diemSuDung : 0;
        this.tienDoiDiem = this.coSuDungDiem ? tienDoiDiem : 0;
    }

    public void tinhDiemDoiTamThoi(int diemHienCo, boolean dungDiem) {
        if (!dungDiem || diemHienCo <= 0) {
            setDiemTichLuyDaDung(false, 0, 0);
            return;
        }

        double soTienCanThanhToan = lamTronTienThanhToan(Math.max(0, getTongTien() - giamGia));
        int diemCanDung = tinhSoDiemCanDung(soTienCanThanhToan, diemHienCo);
        double tienDoi = Math.min(diemCanDung * TIEN_MOI_DIEM, soTienCanThanhToan);

        // Chỉ trừ đủ số điểm để thanh toán về 0; điểm thừa vẫn giữ lại cho khách.
        setDiemTichLuyDaDung(diemCanDung > 0, diemCanDung, tienDoi);
    }

    public void setTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            this.trangThai = TRANG_THAI_CHO;
            return;
        }
        trangThai = trangThai.trim();
        if (!trangThai.equals(TRANG_THAI_CHO)
                && !trangThai.equals(TRANG_THAI_DA_THANH)
                && !trangThai.equals(TRANG_THAI_TRA_MOT_PHAN)
                && !trangThai.equals(TRANG_THAI_HET_HANG)
                && !trangThai.equals(TRANG_THAI_HUY)) {
            throw new IllegalArgumentException("Trạng thái hóa đơn không hợp lệ.");
        }
        this.trangThai = trangThai;
    }

    public void setGhiChu(String ghiChu) {
        if (ghiChu == null) {
            this.ghiChu = "";
            return;
        }
        ghiChu = ghiChu.trim();
        if (ghiChu.length() > 500) {
            throw new IllegalArgumentException("Ghi chú không được vượt quá 500 ký tự.");
        }
        this.ghiChu = ghiChu;
    }


    public void themSanPham(String maSanPham, String kichCo, int soLuong, double donGia) {
        kiemTraTrangThaiCho();
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống.");
        }
        if (kichCo == null || kichCo.trim().isEmpty()) {
            throw new IllegalArgumentException("Kích cỡ không được để trống.");
        }
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        if (donGia <= 0) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0.");
        }

        String key = taoChiTietKey(maSanPham, kichCo);
        if (chiTiet.containsKey(key)) {
            chiTiet.get(key)[0] += soLuong;
            chiTiet.get(key)[1] = donGia;
        } else {
            chiTiet.put(key, new double[]{soLuong, donGia});
        }
    }

    public void themSanPham(String maSanPham, int soLuong, double donGia) {
        themSanPham(maSanPham, "M", soLuong, donGia);
    }

    public void themHang(String maSanPham, int soLuong, double donGia) {
        themSanPham(maSanPham, soLuong, donGia);
    }

    public void xoaSanPham(String maSanPham, String kichCo) {
        kiemTraTrangThaiCho();
        String key = taoChiTietKey(maSanPham, kichCo);
        if (!chiTiet.containsKey(key)) {
            throw new IllegalArgumentException("Sản phẩm/kích cỡ không có trong hóa đơn.");
        }
        chiTiet.remove(key);
    }

    public void xoaSanPham(String maSanPham) {
        xoaSanPham(maSanPham, "M");
    }

    public void capNhatSoLuong(String maSanPham, String kichCo, int soLuongMoi) {
        kiemTraTrangThaiCho();
        String key = taoChiTietKey(maSanPham, kichCo);
        if (!chiTiet.containsKey(key)) {
            throw new IllegalArgumentException("Sản phẩm/kích cỡ không có trong hóa đơn.");
        }
        if (soLuongMoi <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        chiTiet.get(key)[0] = soLuongMoi;
    }

    public void capNhatSoLuong(String maSanPham, int soLuongMoi) {
        capNhatSoLuong(maSanPham, "M", soLuongMoi);
    }

    public void xacNhanThanhToan() {
        kiemTraTrangThaiCho();
        if (chiTiet.isEmpty()) {
            throw new IllegalStateException("Hóa đơn chưa có sản phẩm nào.");
        }
        this.trangThai = TRANG_THAI_DA_THANH;
        this.ngayThanhToan = LocalDateTime.now();
    }

    public void huyHoaDon() {
        if (TRANG_THAI_HUY.equals(trangThai)) {
            throw new IllegalStateException("Hóa đơn đã bị hủy trước đó.");
        }
        if (TRANG_THAI_DA_THANH.equals(trangThai)) {
            throw new IllegalStateException("Không thể hủy hóa đơn đã thanh toán.");
        }
        this.trangThai = TRANG_THAI_HUY;
    }

    private void kiemTraTrangThaiCho() {
        if (!TRANG_THAI_CHO.equals(trangThai)) {
            throw new IllegalStateException("Chỉ có thể thao tác khi hóa đơn ở trạng thái chờ thanh toán.");
        }
    }

    public void xuatThongTin() {
        System.out.println("Mã hóa đơn: " + maHoaDon);
        System.out.println("Mã khách hàng: " + maKhachHang);
        System.out.println("Mã nhân viên: " + maNhanVien);
        System.out.println("Ngày lập: " + getNgayLapText());
        System.out.println("Trạng thái: " + trangThai);
        System.out.println("--- Chi tiết hóa đơn ---");

        for (Map.Entry<String, double[]> e : chiTiet.entrySet()) {
            double soLuong = e.getValue()[0];
            double donGia = e.getValue()[1];
            System.out.printf("%-15s Size: %-8s SL: %.0f x %,.0f = %,.0f VNĐ%n",
                    layMaSanPhamTuKey(e.getKey()), layKichCoTuKey(e.getKey()), soLuong, donGia, soLuong * donGia);
        }

        System.out.printf("Tổng tiền: %,.0f VNĐ%n", getTongTien());
        System.out.printf("Giảm giá: %,.0f VNĐ%n", giamGia);
        System.out.printf("Điểm sử dụng: %d điểm%n", diemSuDung);
        System.out.printf("Tiền đổi điểm: %,.0f VNĐ%n", tienDoiDiem);
        System.out.printf("Thanh toán: %,.0f VNĐ%n", getThanhToan());
        System.out.println("Ghi chú: " + (ghiChu == null || ghiChu.isEmpty() ? "(không có)" : ghiChu));
    }

    public void luuVaoSQL() {
        if (chiTiet.isEmpty()) {
            throw new IllegalStateException("Hóa đơn chưa có sản phẩm nào, không thể lưu.");
        }

        this.trangThai = TRANG_THAI_CHO;
        this.ngayThanhToan = null;

        String sqlHD = """
                INSERT INTO HoaDon
                (maHoaDon, maKhachHang, maNhanVien, ngayLap,
                 tongTien, giamGia, thanhToan, coSuDungDiem,
                 diemSuDung, tienDoiDiem, trangThai, daTruTon, ngayThanhToan, ghiChu)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String sqlCTHD = """
                INSERT INTO ChiTietHoaDon
                (maHoaDon, maSanPham, kichCo, soLuong, donGia)
                VALUES (?, ?, ?, ?, ?)
                """;

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlHD)) {
                ps.setString(1, maHoaDon);
                ps.setString(2, maKhachHang);
                ps.setString(3, maNhanVien);
                ps.setTimestamp(4, Timestamp.valueOf(ngayLap));
                ps.setDouble(5, getTongTien());
                ps.setDouble(6, giamGia);
                ps.setDouble(7, getThanhToan());
                ps.setBoolean(8, coSuDungDiem);
                ps.setInt(9, diemSuDung);
                ps.setDouble(10, tienDoiDiem);
                ps.setString(11, trangThai);
                ps.setBoolean(12, true); // Hóa đơn chờ thanh toán vẫn giữ hàng bằng cách trừ tồn ngay.
                ps.setTimestamp(13, ngayThanhToan == null ? null : Timestamp.valueOf(ngayThanhToan));
                ps.setString(14, ghiChu);
                ps.executeUpdate();
            }

            luuChiTiet(conn);
            truTonKhoTheoChiTiet(conn, chiTiet);
            conn.commit();

        } catch (Exception e) {
            rollback(conn);
            throw new RuntimeException("Lưu hóa đơn thất bại: " + e.getMessage(), e);
        } finally {
            dongKetNoi(conn);
        }
    }

    public void capNhatSQL() {
        kiemTraTrangThaiCho();
        if (chiTiet.isEmpty()) {
            throw new IllegalStateException("Hóa đơn chưa có sản phẩm nào, không thể cập nhật.");
        }

        String sqlHD = """
                UPDATE HoaDon
                SET maKhachHang = ?, maNhanVien = ?, tongTien = ?,
                    giamGia = ?, thanhToan = ?, coSuDungDiem = ?,
                    diemSuDung = ?, tienDoiDiem = ?, ghiChu = ?
                WHERE maHoaDon = ? AND trangThai = N'Chờ thanh toán'
                """;

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);

            HoaDonTonInfo tonInfoCu = docHoaDonTonInfo(conn, maHoaDon);
            if (!TRANG_THAI_CHO.equals(tonInfoCu.trangThai)) {
                throw new IllegalStateException("Chỉ có thể sửa hóa đơn ở trạng thái chờ thanh toán.");
            }

            Map<String, double[]> chiTietCu = docChiTietHoaDonSQL(conn, maHoaDon);
            if (tonInfoCu.daTruTon) {
                congTonKhoTheoChiTiet(conn, chiTietCu);
            }

            int rows;
            try (PreparedStatement ps = conn.prepareStatement(sqlHD)) {
                ps.setString(1, maKhachHang);
                ps.setString(2, maNhanVien);
                ps.setDouble(3, getTongTien());
                ps.setDouble(4, giamGia);
                ps.setDouble(5, getThanhToan());
                ps.setBoolean(6, coSuDungDiem);
                ps.setInt(7, diemSuDung);
                ps.setDouble(8, tienDoiDiem);
                ps.setString(9, ghiChu);
                ps.setString(10, maHoaDon);
                rows = ps.executeUpdate();
            }

            if (rows == 0) {
                throw new IllegalStateException("Chỉ có thể sửa hóa đơn ở trạng thái chờ thanh toán.");
            }

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM ChiTietHoaDon WHERE maHoaDon = ?")) {
                ps.setString(1, maHoaDon);
                ps.executeUpdate();
            }
            luuChiTiet(conn);
            truTonKhoTheoChiTiet(conn, chiTiet);
            datDaTruTonHoaDon(conn, maHoaDon, true);
            conn.commit();

        } catch (Exception e) {
            rollback(conn);
            throw new RuntimeException("Cập nhật hóa đơn thất bại: " + e.getMessage(), e);
        } finally {
            dongKetNoi(conn);
        }
    }

    private void luuChiTiet(Connection conn) throws Exception {
        String sqlCTHD = """
                INSERT INTO ChiTietHoaDon
                (maHoaDon, maSanPham, kichCo, soLuong, donGia)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sqlCTHD)) {
            for (Map.Entry<String, double[]> e : chiTiet.entrySet()) {
                ps.setString(1, maHoaDon);
                ps.setString(2, layMaSanPhamTuKey(e.getKey()));
                ps.setString(3, layKichCoTuKey(e.getKey()));
                ps.setInt(4, (int) e.getValue()[0]);
                ps.setDouble(5, e.getValue()[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static class HoaDonTonInfo {
        final String trangThai;
        final boolean daTruTon;

        HoaDonTonInfo(String trangThai, boolean daTruTon) {
            this.trangThai = trangThai;
            this.daTruTon = daTruTon;
        }
    }

    private static HoaDonTonInfo docHoaDonTonInfo(Connection conn, String maHoaDon) throws Exception {
        String sql = """
                SELECT trangThai, ISNULL(daTruTon, 0) AS daTruTon
                FROM HoaDon WITH (UPDLOCK, ROWLOCK)
                WHERE maHoaDon = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon == null ? "" : maHoaDon.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("Không tìm thấy hóa đơn " + maHoaDon + ".");
                }
                return new HoaDonTonInfo(rs.getString("trangThai"), rs.getBoolean("daTruTon"));
            }
        }
    }

    private static Map<String, double[]> docChiTietHoaDonSQL(Connection conn, String maHoaDon) throws Exception {
        Map<String, double[]> ds = new LinkedHashMap<>();
        String sql = """
                SELECT maSanPham, ISNULL(kichCo, 'M') AS kichCo, soLuong, donGia
                FROM ChiTietHoaDon WITH (UPDLOCK, ROWLOCK)
                WHERE maHoaDon = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon == null ? "" : maHoaDon.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.put(taoChiTietKey(rs.getString("maSanPham"), rs.getString("kichCo")),
                            new double[]{rs.getInt("soLuong"), rs.getDouble("donGia")});
                }
            }
        }
        return ds;
    }

    private static void truTonKhoTheoChiTiet(Connection conn, Map<String, double[]> chiTietCanTru) throws Exception {
        if (chiTietCanTru == null || chiTietCanTru.isEmpty()) return;

        String sqlTon = """
                SELECT soLuongTon
                FROM SanPhamKichCo WITH (UPDLOCK, ROWLOCK)
                WHERE maSanPham = ? AND kichCo = ?
                """;
        String sqlTru = """
                UPDATE SanPhamKichCo
                SET soLuongTon = soLuongTon - ?
                WHERE maSanPham = ? AND kichCo = ? AND soLuongTon >= ?
                """;

        for (Map.Entry<String, double[]> entry : chiTietCanTru.entrySet()) {
            String maSP = layMaSanPhamTuKey(entry.getKey());
            String kichCo = layKichCoTuKey(entry.getKey());
            int soLuong = (int) entry.getValue()[0];
            if (soLuong <= 0) continue;

            int tonKho;
            try (PreparedStatement ps = conn.prepareStatement(sqlTon)) {
                ps.setString(1, maSP);
                ps.setString(2, kichCo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalStateException("Không tìm thấy sản phẩm " + maSP + " size " + kichCo + ".");
                    }
                    tonKho = rs.getInt("soLuongTon");
                }
            }

            if (tonKho < soLuong) {
                throw new IllegalStateException("Sản phẩm " + maSP + " size " + kichCo
                        + " không đủ tồn kho để giữ hàng. Tồn hiện tại: " + tonKho);
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlTru)) {
                ps.setInt(1, soLuong);
                ps.setString(2, maSP);
                ps.setString(3, kichCo);
                ps.setInt(4, soLuong);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new IllegalStateException("Không thể trừ tồn kho sản phẩm " + maSP + " size " + kichCo + ".");
                }
            }
        }
        dongBoTonKhoSanPham(conn, chiTietCanTru);
    }

    private static void congTonKhoTheoChiTiet(Connection conn, Map<String, double[]> chiTietCanCong) throws Exception {
        if (chiTietCanCong == null || chiTietCanCong.isEmpty()) return;

        String sqlCong = """
                UPDATE SanPhamKichCo
                SET soLuongTon = soLuongTon + ?, trangThai = N'Đang bán'
                WHERE maSanPham = ? AND kichCo = ?
                """;

        for (Map.Entry<String, double[]> entry : chiTietCanCong.entrySet()) {
            String maSP = layMaSanPhamTuKey(entry.getKey());
            String kichCo = layKichCoTuKey(entry.getKey());
            int soLuong = (int) entry.getValue()[0];
            if (soLuong <= 0) continue;

            try (PreparedStatement ps = conn.prepareStatement(sqlCong)) {
                ps.setInt(1, soLuong);
                ps.setString(2, maSP);
                ps.setString(3, kichCo);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new IllegalStateException("Không tìm thấy tồn kho sản phẩm " + maSP + " size " + kichCo + ".");
                }
            }
        }
        dongBoTonKhoSanPham(conn, chiTietCanCong);
    }

    private static void dongBoTonKhoSanPham(Connection conn, Map<String, double[]> chiTietCanDongBo) throws Exception {
        if (chiTietCanDongBo == null || chiTietCanDongBo.isEmpty()) return;

        Set<String> dsMaSP = new LinkedHashSet<>();
        for (String key : chiTietCanDongBo.keySet()) {
            dsMaSP.add(layMaSanPhamTuKey(key));
        }

        String sqlDongBo = """
                UPDATE SanPham
                SET soLuongTon = ISNULL((SELECT SUM(soLuongTon) FROM SanPhamKichCo WHERE maSanPham = ?), 0)
                WHERE maSanPham = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sqlDongBo)) {
            for (String maSP : dsMaSP) {
                ps.setString(1, maSP);
                ps.setString(2, maSP);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void datDaTruTonHoaDon(Connection conn, String maHoaDon, boolean daTruTon) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE HoaDon SET daTruTon = ? WHERE maHoaDon = ?")) {
            ps.setBoolean(1, daTruTon);
            ps.setString(2, maHoaDon == null ? "" : maHoaDon.trim().toUpperCase());
            ps.executeUpdate();
        }
    }

    public void xacNhanThanhToanSQL(boolean suDungDiem) throws Exception {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không hợp lệ.");
        }

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();

            if (conn == null) {
                throw new Exception("Không kết nối được database.");
            }

            conn.setAutoCommit(false);

            // 1. Khóa hóa đơn để tránh thanh toán 2 lần
            String sqlHoaDon = """
                    SELECT maKhachHang, tongTien, giamGia, trangThai, ISNULL(daTruTon, 0) AS daTruTon
                    FROM HoaDon WITH (UPDLOCK, ROWLOCK)
                    WHERE maHoaDon = ?
                    """;

            String maKhachHangDB;
            double tongTienDB;
            double giamGiaDB;
            String trangThaiDB;
            boolean daTruTonDB;

            try (PreparedStatement ps = conn.prepareStatement(sqlHoaDon)) {
                ps.setString(1, maHoaDon);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new Exception("Không tìm thấy hóa đơn " + maHoaDon + ".");
                    }

                    maKhachHangDB = rs.getString("maKhachHang");
                    tongTienDB = rs.getDouble("tongTien");
                    giamGiaDB = rs.getDouble("giamGia");
                    trangThaiDB = rs.getString("trangThai");
                    daTruTonDB = rs.getBoolean("daTruTon");
                }
            }

            if (!TRANG_THAI_CHO.equals(trangThaiDB)) {
                throw new Exception("Chỉ hóa đơn chờ thanh toán mới được xác nhận thanh toán.");
            }

            // 2. Đọc chi tiết hóa đơn từ database
            Map<String, double[]> chiTietDB = new LinkedHashMap<>();

            String sqlChiTiet = """
                    SELECT maSanPham, ISNULL(kichCo, 'M') AS kichCo, soLuong, donGia
                    FROM ChiTietHoaDon
                    WHERE maHoaDon = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sqlChiTiet)) {
                ps.setString(1, maHoaDon);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String maSP = rs.getString("maSanPham");
                        String kichCo = rs.getString("kichCo");
                        int soLuong = rs.getInt("soLuong");
                        double donGia = rs.getDouble("donGia");

                        chiTietDB.put(taoChiTietKey(maSP, kichCo), new double[]{soLuong, donGia});
                    }
                }
            }

            if (chiTietDB.isEmpty()) {
                throw new Exception("Hóa đơn chưa có sản phẩm nên không thể thanh toán.");
            }

            // 3. Tính lại tổng tiền và tổng số lượng sản phẩm
            int tongSoLuongSanPham = 0;
            double tongTienTinhLai = 0;

            for (double[] item : chiTietDB.values()) {
                int soLuong = (int) item[0];
                double donGia = item[1];

                tongSoLuongSanPham += soLuong;
                tongTienTinhLai += soLuong * donGia;
            }

            tongTienDB = tongTienTinhLai;

            // 4. Khóa khách hàng và lấy điểm mới nhất
            int diemHienCo;

            String sqlKhach = """
                    SELECT diemTichLuy
                    FROM KhachHang WITH (UPDLOCK, ROWLOCK)
                    WHERE maDinhDanh = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sqlKhach)) {
                ps.setString(1, maKhachHangDB);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new Exception("Không tìm thấy khách hàng " + maKhachHangDB + ".");
                    }

                    diemHienCo = rs.getInt("diemTichLuy");
                }
            }

            // 5. Kiểm tra giữ hàng. Theo nghiệp vụ mới, hóa đơn chờ thanh toán đã trừ tồn kho ngay khi tạo/sửa.
            // Nếu gặp hóa đơn dữ liệu cũ chưa trừ tồn, hệ thống sẽ trừ tồn một lần tại thời điểm thanh toán.
            if (!daTruTonDB) {
                truTonKhoTheoChiTiet(conn, chiTietDB);
                datDaTruTonHoaDon(conn, maHoaDon, true);
            }

            // 6. Tính điểm dùng, tiền đổi điểm, điểm cộng mới
            boolean coDungDiem = suDungDiem && diemHienCo > 0;

            double soTienSauGiamGia = lamTronTienThanhToan(Math.max(0, tongTienDB - giamGiaDB));

            int diemSuDungMoi = coDungDiem
                    ? tinhSoDiemCanDung(soTienSauGiamGia, diemHienCo)
                    : 0;

            double tienDoiDiemMoi = coDungDiem
                    ? Math.min(diemSuDungMoi * TIEN_MOI_DIEM, soTienSauGiamGia)
                    : 0;

            double thanhToanMoi = lamTronTienThanhToan(Math.max(0, soTienSauGiamGia - tienDoiDiemMoi));

            int diemCongMoi = tongSoLuongSanPham * DIEM_MOI_SAN_PHAM;

            // Điểm còn thừa sau khi đổi vẫn giữ lại, sau đó cộng điểm mới của hóa đơn.
            int diemSauThanhToan = Math.max(0, diemHienCo - diemSuDungMoi) + diemCongMoi;

            // 7. Cập nhật điểm tích lũy khách hàng
            String sqlUpdateKhach = """
                    UPDATE KhachHang
                    SET diemTichLuy = ?
                    WHERE maDinhDanh = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateKhach)) {
                ps.setInt(1, diemSauThanhToan);
                ps.setString(2, maKhachHangDB);

                int affected = ps.executeUpdate();

                if (affected == 0) {
                    throw new Exception("Không cập nhật được điểm tích lũy khách hàng.");
                }
            }

            // 8. Cập nhật hóa đơn
            String sqlUpdateHoaDon = """
                    UPDATE HoaDon
                    SET tongTien = ?,
                        giamGia = ?,
                        coSuDungDiem = ?,
                        diemSuDung = ?,
                        tienDoiDiem = ?,
                        thanhToan = ?,
                        trangThai = ?,
                        daTruTon = 1,
                        ngayThanhToan = GETDATE()
                    WHERE maHoaDon = ?
                      AND trangThai = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateHoaDon)) {
                ps.setDouble(1, tongTienDB);
                ps.setDouble(2, giamGiaDB);
                ps.setBoolean(3, coDungDiem);
                ps.setInt(4, diemSuDungMoi);
                ps.setDouble(5, tienDoiDiemMoi);
                ps.setDouble(6, thanhToanMoi);
                ps.setString(7, TRANG_THAI_DA_THANH);
                ps.setString(8, maHoaDon);
                ps.setString(9, TRANG_THAI_CHO);

                int affected = ps.executeUpdate();

                if (affected == 0) {
                    throw new Exception("Không cập nhật được trạng thái hóa đơn.");
                }
            }

            conn.commit();

            // 9. Cập nhật lại object hiện tại.
            // Không gán this.tongTien / this.thanhToan vì class này tính tổng từ chiTiet.
            this.maKhachHang = maKhachHangDB;
            this.giamGia = giamGiaDB;
            this.coSuDungDiem = coDungDiem;
            this.diemSuDung = diemSuDungMoi;
            this.tienDoiDiem = tienDoiDiemMoi;
            this.trangThai = TRANG_THAI_DA_THANH;
            this.ngayThanhToan = LocalDateTime.now();

        } catch (Exception e) {
            rollback(conn);
            throw e;

        } finally {
            dongKetNoi(conn);
        }
    }

    public static List<HoaDon> docTuSQL() {
        List<HoaDon> ds = new ArrayList<>();
        String sql = """
                SELECT maHoaDon, maKhachHang, maNhanVien, ngayLap,
                       tongTien, giamGia, thanhToan, coSuDungDiem,
                       diemSuDung, tienDoiDiem, trangThai, ngayThanhToan, ghiChu
                FROM HoaDon
                ORDER BY
                    CASE WHEN maHoaDon LIKE 'HD-[0-9][0-9][0-9][0-9][0-9][0-9]' THEN 0 ELSE 1 END ASC,
                    TRY_CONVERT(INT, SUBSTRING(maHoaDon, 4, 6)) ASC,
                    ngayLap ASC,
                    maHoaDon ASC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(taoHoaDonTuResultSet(rs));
            }
            napDuLieuLienQuanChoDanhSach(conn, ds);
        } catch (Exception e) {
            System.out.println("Đọc danh sách hóa đơn thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }
        return ds;
    }

    private static void napDuLieuLienQuanChoDanhSach(Connection conn, List<HoaDon> ds) throws Exception {
        if (conn == null || ds == null || ds.isEmpty()) return;

        Map<String, HoaDon> mapHoaDon = new LinkedHashMap<>();
        for (HoaDon hd : ds) {
            if (hd == null || hd.getMaHoaDon() == null || hd.getMaHoaDon().trim().isEmpty()) continue;
            hd.chiTiet.clear();
            hd.chiTietTraHang.clear();
            mapHoaDon.put(hd.getMaHoaDon().trim().toUpperCase(), hd);
        }
        if (mapHoaDon.isEmpty()) return;

        String placeholders = String.join(",", Collections.nCopies(mapHoaDon.size(), "?"));

        String sqlChiTiet = """
                SELECT maHoaDon, maSanPham, ISNULL(kichCo, 'M') AS kichCo, soLuong, donGia
                FROM ChiTietHoaDon
                WHERE maHoaDon IN (%s)
                """.formatted(placeholders);
        try (PreparedStatement ps = conn.prepareStatement(sqlChiTiet)) {
            ganThamSoMaHoaDon(ps, mapHoaDon.keySet());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HoaDon hd = mapHoaDon.get(rs.getString("maHoaDon").trim().toUpperCase());
                    if (hd == null) continue;
                    hd.chiTiet.put(
                            taoChiTietKey(rs.getString("maSanPham"), rs.getString("kichCo")),
                            new double[]{rs.getInt("soLuong"), rs.getDouble("donGia")}
                    );
                }
            }
        }

        String sqlTraHang = """
                SELECT maHoaDon, maSanPham, ISNULL(kichCo, 'M') AS kichCo,
                       SUM(soLuongTra) AS soLuongTra,
                       MAX(donGia) AS donGia,
                       SUM(tienHoanTra) AS tienHoanTra
                FROM PhieuTraHang
                WHERE maHoaDon IN (%s)
                GROUP BY maHoaDon, maSanPham, ISNULL(kichCo, 'M')
                """.formatted(placeholders);
        try (PreparedStatement ps = conn.prepareStatement(sqlTraHang)) {
            ganThamSoMaHoaDon(ps, mapHoaDon.keySet());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HoaDon hd = mapHoaDon.get(rs.getString("maHoaDon").trim().toUpperCase());
                    if (hd == null) continue;
                    hd.chiTietTraHang.put(
                            taoChiTietKey(rs.getString("maSanPham"), rs.getString("kichCo")),
                            new double[]{rs.getInt("soLuongTra"), rs.getDouble("donGia"), rs.getDouble("tienHoanTra")}
                    );
                }
            }
        } catch (Exception e) {
            if (e.getMessage() != null && !e.getMessage().toLowerCase().contains("phieutrahang")) {
                throw e;
            }
        }
    }

    private static void ganThamSoMaHoaDon(PreparedStatement ps, Collection<String> maHoaDonList) throws Exception {
        int index = 1;
        for (String maHoaDon : maHoaDonList) {
            ps.setString(index++, maHoaDon);
        }
    }

    private void napChiTietTuSQL() {
        String sql = """
                SELECT maSanPham, ISNULL(kichCo, 'M') AS kichCo, soLuong, donGia
                FROM ChiTietHoaDon
                WHERE maHoaDon = ?
                """;
        chiTiet.clear();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chiTiet.put(taoChiTietKey(rs.getString("maSanPham"), rs.getString("kichCo")),
                            new double[]{rs.getInt("soLuong"), rs.getDouble("donGia")});
                }
            }
        } catch (Exception e) {
            System.out.println("Nạp chi tiết hóa đơn thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private void napTraHangTuSQL() {
        String sql = """
                SELECT maSanPham, ISNULL(kichCo, 'M') AS kichCo,
                       SUM(soLuongTra) AS soLuongTra,
                       MAX(donGia) AS donGia,
                       SUM(tienHoanTra) AS tienHoanTra
                FROM PhieuTraHang
                WHERE maHoaDon = ?
                GROUP BY maSanPham, ISNULL(kichCo, 'M')
                """;
        chiTietTraHang.clear();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chiTietTraHang.put(
                            taoChiTietKey(rs.getString("maSanPham"), rs.getString("kichCo")),
                            new double[]{rs.getInt("soLuongTra"), rs.getDouble("donGia"), rs.getDouble("tienHoanTra")}
                    );
                }
            }
        } catch (Exception e) {
            // Nếu database cũ chưa có bảng PhieuTraHang thì vẫn cho chương trình đọc hóa đơn bình thường.
            if (e.getMessage() != null && !e.getMessage().toLowerCase().contains("phieutrahang")) {
                System.out.println("Nạp chi tiết trả hàng thất bại!");
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }

    public static List<HangTraHang> layDanhSachHangCoTheTraSQL(String maHoaDon) {
        List<HangTraHang> ds = new ArrayList<>();
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) return ds;

        String sql = """
                SELECT ct.maSanPham, sp.tenSanPham, ISNULL(ct.kichCo, 'M') AS kichCo,
                       ct.soLuong, ct.donGia,
                       ISNULL(SUM(th.soLuongTra), 0) AS soLuongDaTra
                FROM ChiTietHoaDon ct
                INNER JOIN SanPham sp ON sp.maSanPham = ct.maSanPham
                LEFT JOIN PhieuTraHang th
                    ON th.maHoaDon = ct.maHoaDon
                   AND th.maSanPham = ct.maSanPham
                   AND ISNULL(th.kichCo, 'M') = ISNULL(ct.kichCo, 'M')
                WHERE ct.maHoaDon = ?
                GROUP BY ct.maSanPham, sp.tenSanPham, ISNULL(ct.kichCo, 'M'), ct.soLuong, ct.donGia
                ORDER BY ct.maSanPham, ISNULL(ct.kichCo, 'M')
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int soLuongGoc = rs.getInt("soLuong");
                    int soLuongDaTra = rs.getInt("soLuongDaTra");
                    int soLuongConLai = Math.max(0, soLuongGoc - soLuongDaTra);
                    if (soLuongConLai > 0) {
                        ds.add(new HangTraHang(
                                rs.getString("maSanPham"),
                                rs.getString("tenSanPham"),
                                rs.getString("kichCo"),
                                soLuongGoc,
                                soLuongDaTra,
                                soLuongConLai,
                                rs.getDouble("donGia")
                        ));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Không đọc được danh sách sản phẩm trả hàng: " + e.getMessage(), e);
        }

        return ds;
    }

    private static boolean sanPhamDangNgungBan(Connection conn, String maSanPham, String kichCo) throws Exception {
        String sql = """
                SELECT ISNULL(trangThai, N'Đang bán') AS trangThai
                FROM SanPhamKichCo WITH (UPDLOCK, ROWLOCK)
                WHERE maSanPham = ? AND kichCo = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSanPham);
            ps.setString(2, kichCo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return true;
                }
                return "Ngừng bán".equalsIgnoreCase(rs.getString("trangThai"));
            }
        }
    }

    public static void traHangMotPhanSQL(String maHoaDon, String maSanPham, String kichCo, int soLuongTra, String lyDo, boolean congLaiTon) throws Exception {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không hợp lệ.");
        }
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm trả hàng không hợp lệ.");
        }
        if (kichCo == null || kichCo.trim().isEmpty()) {
            kichCo = "M";
        }
        if (soLuongTra <= 0) {
            throw new IllegalArgumentException("Số lượng trả phải lớn hơn 0.");
        }
        lyDo = lyDo == null ? "" : lyDo.trim();
        if (lyDo.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn lý do trả hàng.");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);

            String maHD = maHoaDon.trim().toUpperCase();
            String maSP = maSanPham.trim().toUpperCase();
            String size = kichCo.trim().toUpperCase();

            String trangThaiDB;
            Timestamp ngayLapDB;
            Timestamp ngayThanhToanDB;
            double tongTienDB;
            double thanhToanDB;
            String ghiChuCu;

            String sqlHD = """
                    SELECT trangThai, ngayLap, ngayThanhToan, tongTien, thanhToan, ISNULL(ghiChu, '') AS ghiChu
                    FROM HoaDon WITH (UPDLOCK, ROWLOCK)
                    WHERE maHoaDon = ?
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sqlHD)) {
                ps.setString(1, maHD);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new IllegalStateException("Không tìm thấy hóa đơn " + maHD + ".");
                    trangThaiDB = rs.getString("trangThai");
                    ngayLapDB = rs.getTimestamp("ngayLap");
                    ngayThanhToanDB = rs.getTimestamp("ngayThanhToan");
                    tongTienDB = rs.getDouble("tongTien");
                    thanhToanDB = rs.getDouble("thanhToan");
                    ghiChuCu = rs.getString("ghiChu");
                }
            }

            if (TRANG_THAI_HUY.equals(trangThaiDB) || TRANG_THAI_HET_HANG.equals(trangThaiDB)) {
                throw new IllegalStateException("Không thể trả hàng hóa đơn này.");
            }
            if (!TRANG_THAI_DA_THANH.equals(trangThaiDB) && !TRANG_THAI_TRA_MOT_PHAN.equals(trangThaiDB)) {
                throw new IllegalStateException("Chỉ có thể trả hàng với hóa đơn đã thanh toán.");
            }

            Timestamp moc = ngayThanhToanDB != null ? ngayThanhToanDB : ngayLapDB;
            if (moc == null) {
                throw new IllegalStateException("Hóa đơn không có ngày mua hợp lệ.");
            }
            LocalDate ngayMua = moc.toLocalDateTime().toLocalDate();
            if (ngayMua.plusDays(10).isBefore(LocalDate.now())) {
                throw new IllegalStateException("Hóa đơn đã hết hạn trả hàng.");
            }

            int soLuongGoc;
            int soLuongDaTra;
            double donGia;
            String sqlCT = """
                    SELECT ct.soLuong, ct.donGia,
                           ISNULL((SELECT SUM(th.soLuongTra)
                                   FROM PhieuTraHang th
                                   WHERE th.maHoaDon = ct.maHoaDon
                                     AND th.maSanPham = ct.maSanPham
                                     AND ISNULL(th.kichCo, 'M') = ISNULL(ct.kichCo, 'M')), 0) AS soLuongDaTra
                    FROM ChiTietHoaDon ct WITH (UPDLOCK, ROWLOCK)
                    WHERE ct.maHoaDon = ? AND ct.maSanPham = ? AND ISNULL(ct.kichCo, 'M') = ?
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sqlCT)) {
                ps.setString(1, maHD);
                ps.setString(2, maSP);
                ps.setString(3, size);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new IllegalStateException("Sản phẩm không có trong hóa đơn.");
                    soLuongGoc = rs.getInt("soLuong");
                    donGia = rs.getDouble("donGia");
                    soLuongDaTra = rs.getInt("soLuongDaTra");
                }
            }

            int soLuongConLai = soLuongGoc - soLuongDaTra;
            if (soLuongTra > soLuongConLai) {
                throw new IllegalStateException("Số lượng trả vượt quá số lượng còn lại của sản phẩm trong hóa đơn.");
            }

            boolean sanPhamNgungBan = sanPhamDangNgungBan(conn, maSP, size);
            boolean congLaiTonThucTe = congLaiTon && !sanPhamNgungBan;

            double tienGocTra = donGia * soLuongTra;
            double tiLeThanhToan = tongTienDB <= 0 ? 1.0 : Math.max(0, Math.min(1.0, thanhToanDB / tongTienDB));
            double tienHoanTra = Math.round(tienGocTra * tiLeThanhToan);

            String maTraHang = "TH-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            String sqlInsert = """
                    INSERT INTO PhieuTraHang
                    (maTraHang, maHoaDon, maSanPham, kichCo, soLuongTra, donGia, tienGocTra, tienHoanTra, lyDo, congLaiTonKho, ngayTra, ghiChu)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setString(1, maTraHang);
                ps.setString(2, maHD);
                ps.setString(3, maSP);
                ps.setString(4, size);
                ps.setInt(5, soLuongTra);
                ps.setDouble(6, donGia);
                ps.setDouble(7, tienGocTra);
                ps.setDouble(8, tienHoanTra);
                ps.setString(9, lyDo);
                ps.setBoolean(10, congLaiTonThucTe);
                ps.setString(11, "Trả hàng một phần từ hóa đơn " + maHD);
                ps.executeUpdate();
            }

            if (congLaiTonThucTe) {
                String sqlCongTon = """
                        UPDATE SanPhamKichCo
                        SET soLuongTon = soLuongTon + ?, trangThai = N'Đang bán'
                        WHERE maSanPham = ? AND kichCo = ?
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sqlCongTon)) {
                    ps.setInt(1, soLuongTra);
                    ps.setString(2, maSP);
                    ps.setString(3, size);
                    int rows = ps.executeUpdate();
                    if (rows == 0) throw new IllegalStateException("Không tìm thấy tồn kho sản phẩm " + maSP + " size " + size + ".");
                }

                String sqlDongBo = """
                        UPDATE SanPham
                        SET soLuongTon = ISNULL((SELECT SUM(soLuongTon) FROM SanPhamKichCo WHERE maSanPham = ?), 0)
                        WHERE maSanPham = ?
                        """;
                try (PreparedStatement ps = conn.prepareStatement(sqlDongBo)) {
                    ps.setString(1, maSP);
                    ps.setString(2, maSP);
                    ps.executeUpdate();
                }
            }

            String ghiChuMoi = (ghiChuCu == null || ghiChuCu.isBlank())
                    ? "Trả một phần hàng: " + maSP + " size " + size + " SL " + soLuongTra + " - " + lyDo
                    : ghiChuCu + " | Trả một phần hàng: " + maSP + " size " + size + " SL " + soLuongTra + " - " + lyDo;
            if (ghiChuMoi.length() > 500) ghiChuMoi = ghiChuMoi.substring(0, 500);

            String sqlUpdateHD = """
                    UPDATE HoaDon
                    SET trangThai = ?, ghiChu = ?
                    WHERE maHoaDon = ?
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateHD)) {
                ps.setString(1, TRANG_THAI_TRA_MOT_PHAN);
                ps.setString(2, ghiChuMoi);
                ps.setString(3, maHD);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            rollback(conn);
            throw e;
        } finally {
            dongKetNoi(conn);
        }
    }

    public void capNhatTrangThaiSQL() {
        String sql = "UPDATE HoaDon SET trangThai = ?, ngayThanhToan = ? WHERE maHoaDon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setTimestamp(2, ngayThanhToan == null ? null : Timestamp.valueOf(ngayThanhToan));
            ps.setString(3, maHoaDon);
            int rows = ps.executeUpdate();
            if (rows == 0) System.out.println("Không tìm thấy hóa đơn có mã: " + maHoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Cập nhật trạng thái hóa đơn thất bại: " + e.getMessage(), e);
        }
    }

    public static void huyHoaDonSQL(String maHoaDon, String lyDoHuy) throws Exception {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không hợp lệ.");
        }
        lyDoHuy = lyDoHuy == null ? "" : lyDoHuy.trim();
        if (lyDoHuy.isEmpty()) {
            throw new IllegalArgumentException("Lý do hủy không được để trống.");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);

            String trangThaiCu;
            String ghiChuCu;
            boolean daTruTonCu;
            try (PreparedStatement ps = conn.prepareStatement("SELECT trangThai, ISNULL(ghiChu, '') AS ghiChu, ISNULL(daTruTon, 0) AS daTruTon FROM HoaDon WITH (UPDLOCK, ROWLOCK) WHERE maHoaDon = ?")) {
                ps.setString(1, maHoaDon.trim().toUpperCase());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new IllegalStateException("Không tìm thấy hóa đơn " + maHoaDon + ".");
                    trangThaiCu = rs.getString("trangThai");
                    ghiChuCu = rs.getString("ghiChu");
                    daTruTonCu = rs.getBoolean("daTruTon");
                }
            }

            if (TRANG_THAI_HUY.equals(trangThaiCu)) {
                throw new IllegalStateException("Hóa đơn đã bị hủy trước đó.");
            }

            if (daTruTonCu) {
                Map<String, double[]> chiTietHuy = docChiTietHoaDonSQL(conn, maHoaDon.trim().toUpperCase());
                congTonKhoTheoChiTiet(conn, chiTietHuy);
            }

            String ghiChuMoi = (ghiChuCu == null || ghiChuCu.isBlank())
                    ? "Lý do hủy: " + lyDoHuy
                    : ghiChuCu + " | Lý do hủy: " + lyDoHuy;
            if (ghiChuMoi.length() > 500) {
                ghiChuMoi = ghiChuMoi.substring(0, 500);
            }

            String sqlUpdate = """
                    UPDATE HoaDon
                    SET trangThai = ?, ghiChu = ?, daTruTon = 0
                    WHERE maHoaDon = ?
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setString(1, TRANG_THAI_HUY);
                ps.setString(2, ghiChuMoi);
                ps.setString(3, maHoaDon.trim().toUpperCase());
                int rows = ps.executeUpdate();
                if (rows == 0) throw new IllegalStateException("Không cập nhật được trạng thái hóa đơn.");
            }

            conn.commit();
        } catch (Exception e) {
            rollback(conn);
            throw e;
        } finally {
            dongKetNoi(conn);
        }
    }

    public static void xoaKhoiSQL(String maHoaDon) {
        String sqlTrangThai = "SELECT trangThai, ISNULL(daTruTon, 0) AS daTruTon FROM HoaDon WITH (UPDLOCK, ROWLOCK) WHERE maHoaDon = ?";
        String sqlCT = "DELETE FROM ChiTietHoaDon WHERE maHoaDon = ?";
        String sqlHD = "DELETE FROM HoaDon WHERE maHoaDon = ? AND trangThai IN (N'Chờ thanh toán', N'Đã hủy')";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) throw new IllegalStateException("Không kết nối được database.");
            conn.setAutoCommit(false);

            String trangThaiDB = null;
            boolean daTruTonDB = false;
            try (PreparedStatement ps = conn.prepareStatement(sqlTrangThai)) {
                ps.setString(1, maHoaDon);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        trangThaiDB = rs.getString("trangThai");
                        daTruTonDB = rs.getBoolean("daTruTon");
                    }
                }
            }

            if (trangThaiDB == null) {
                throw new IllegalStateException("Không tìm thấy hóa đơn có mã: " + maHoaDon);
            }

            if (TRANG_THAI_DA_THANH.equals(trangThaiDB)) {
                throw new IllegalStateException("Không thể xóa hóa đơn đã thanh toán.");
            }

            if (!TRANG_THAI_CHO.equals(trangThaiDB) && !TRANG_THAI_HUY.equals(trangThaiDB)) {
                throw new IllegalStateException("Chỉ có thể xóa hóa đơn ở trạng thái chờ thanh toán hoặc đã hủy.");
            }

            if (daTruTonDB) {
                Map<String, double[]> chiTietXoa = docChiTietHoaDonSQL(conn, maHoaDon);
                congTonKhoTheoChiTiet(conn, chiTietXoa);
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlCT)) {
                ps.setString(1, maHoaDon);
                ps.executeUpdate();
            }
            int rows;
            try (PreparedStatement ps = conn.prepareStatement(sqlHD)) {
                ps.setString(1, maHoaDon);
                rows = ps.executeUpdate();
            }
            if (rows == 0) throw new IllegalStateException("Không tìm thấy hóa đơn có mã: " + maHoaDon);
            conn.commit();
        } catch (Exception e) {
            rollback(conn);
            throw new RuntimeException("Xóa hóa đơn thất bại: " + e.getMessage(), e);
        } finally {
            dongKetNoi(conn);
        }
    }

    public static List<HoaDon> timKiemSQL(String keyword) {
        List<HoaDon> ds = new ArrayList<>();
        String sql = """
                SELECT maHoaDon, maKhachHang, maNhanVien, ngayLap,
                       tongTien, giamGia, thanhToan, coSuDungDiem,
                       diemSuDung, tienDoiDiem, trangThai, ngayThanhToan, ghiChu
                FROM HoaDon
                WHERE maHoaDon LIKE ?
                   OR maKhachHang LIKE ?
                   OR maNhanVien LIKE ?
                   OR trangThai LIKE ?
                ORDER BY ngayLap ASC, maHoaDon ASC
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ps.setString(4, key);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(taoHoaDonTuResultSet(rs));
                }
            }
            napDuLieuLienQuanChoDanhSach(conn, ds);
        } catch (Exception e) {
            System.out.println("Tìm kiếm hóa đơn thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }
        return ds;
    }

    public static List<HoaDon> locTheoTrangThaiSQL(String trangThai) {
        List<HoaDon> ds = new ArrayList<>();
        String sql = """
                SELECT maHoaDon, maKhachHang, maNhanVien, ngayLap,
                       tongTien, giamGia, thanhToan, coSuDungDiem,
                       diemSuDung, tienDoiDiem, trangThai, ngayThanhToan, ghiChu
                FROM HoaDon
                WHERE trangThai = ?
                ORDER BY ngayLap ASC, maHoaDon ASC
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(taoHoaDonTuResultSet(rs));
                }
            }
            napDuLieuLienQuanChoDanhSach(conn, ds);
        } catch (Exception e) {
            System.out.println("Lọc hóa đơn thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }
        return ds;
    }

    private static HoaDon taoHoaDonTuResultSet(ResultSet rs) throws Exception {
        Timestamp ts = rs.getTimestamp("ngayLap");
        Timestamp tt = rs.getTimestamp("ngayThanhToan");
        LocalDateTime ngayLap = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
        LocalDateTime ngayThanhToan = tt != null ? tt.toLocalDateTime() : null;

        HoaDon hd = new HoaDon(
                rs.getString("maHoaDon"),
                rs.getString("maKhachHang"),
                rs.getString("maNhanVien"),
                ngayLap,
                rs.getDouble("giamGia"),
                rs.getBoolean("coSuDungDiem"),
                rs.getInt("diemSuDung"),
                rs.getDouble("tienDoiDiem"),
                rs.getString("trangThai"),
                ngayThanhToan,
                rs.getString("ghiChu")
        );
        return hd;
    }

    public static class HangTraHang {
        private final String maSanPham;
        private final String tenSanPham;
        private final String kichCo;
        private final int soLuongGoc;
        private final int soLuongDaTra;
        private final int soLuongConLai;
        private final double donGia;

        public HangTraHang(String maSanPham, String tenSanPham, String kichCo, int soLuongGoc, int soLuongDaTra, int soLuongConLai, double donGia) {
            this.maSanPham = maSanPham == null ? "" : maSanPham.trim().toUpperCase();
            this.tenSanPham = tenSanPham == null ? "" : tenSanPham.trim();
            this.kichCo = kichCo == null || kichCo.isBlank() ? "M" : kichCo.trim().toUpperCase();
            this.soLuongGoc = Math.max(0, soLuongGoc);
            this.soLuongDaTra = Math.max(0, soLuongDaTra);
            this.soLuongConLai = Math.max(0, soLuongConLai);
            this.donGia = Math.max(0, donGia);
        }

        public String getMaSanPham() { return maSanPham; }
        public String getTenSanPham() { return tenSanPham; }
        public String getKichCo() { return kichCo; }
        public int getSoLuongGoc() { return soLuongGoc; }
        public int getSoLuongDaTra() { return soLuongDaTra; }
        public int getSoLuongConLai() { return soLuongConLai; }
        public double getDonGia() { return donGia; }

        @Override
        public String toString() {
            String ten = tenSanPham.isBlank() ? maSanPham : tenSanPham;
            return maSanPham + " - " + ten + " | Size " + kichCo + " | Còn " + soLuongConLai + " | " + String.format("%,.0f VNĐ", donGia);
        }
    }

    private static void rollback(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private static void dongKetNoi(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void hienThiDanhSach(List<HoaDon> ds) {
        if (ds == null || ds.isEmpty()) {
            System.out.println("Danh sách hóa đơn rỗng.");
            return;
        }
        for (HoaDon hd : ds) {
            System.out.println("====================");
            hd.xuatThongTin();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HoaDon)) return false;
        HoaDon other = (HoaDon) obj;
        return maHoaDon != null && maHoaDon.equals(other.maHoaDon);
    }

    @Override
    public int hashCode() {
        return maHoaDon != null ? maHoaDon.hashCode() : 0;
    }
}
