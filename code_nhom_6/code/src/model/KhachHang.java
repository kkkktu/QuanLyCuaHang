package model;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class KhachHang extends Nguoi {

    private int diemTichLuy;

    @Override
    protected String layPrefix() {
        return "KH";
    }

    // ===== Constructor =====
    public KhachHang() {
        super();
        this.diemTichLuy = 0;
        this.diaChi = "";
    }

    // Constructor mới: không cần địa chỉ
    public KhachHang(String hoTen, String soDienThoai) {
        super(hoTen, soDienThoai, "");
        this.diemTichLuy = 0;
    }

    // Constructor mới: không cần địa chỉ
    public KhachHang(String hoTen, String soDienThoai, int diemTichLuy) {
        super(hoTen, soDienThoai, "");
        setDiemTichLuy(diemTichLuy);
    }



    // Constructor mới: không cần địa chỉ
    public KhachHang(String maDinhDanh, String hoTen, String soDienThoai, int diemTichLuy) {
        super(maDinhDanh, hoTen, soDienThoai, "");
        setDiemTichLuy(diemTichLuy);
    }


    
    @Override
    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống.");
        }

        hoTen = hoTen.trim();

        if (hoTen.length() < 2 || hoTen.length() > 100) {
            throw new IllegalArgumentException("Tên khách hàng phải từ 2 đến 100 ký tự.");
        }

        if (!hoTen.matches("[\\p{L}0-9\\s]+")) {
            throw new IllegalArgumentException("Tên khách hàng chỉ được chứa chữ cái, số và khoảng trắng.");
        }

        this.hoTen = hoTen;
    }

    // ===== Getter / Setter =====
    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        if (diemTichLuy < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không được âm.");
        }

        this.diemTichLuy = diemTichLuy;
    }

    // ===== Nhập thông tin console =====
    @Override
    public void nhapThongTin() {
        Scanner sc = new Scanner(System.in);

        System.out.println(">>> Mã khách hàng được tự động tạo: " + maDinhDanh);

        nhapChuoiCoRangBuoc(sc, "Họ tên: ", val -> setHoTen(val));
        nhapChuoiCoRangBuoc(sc, "Số điện thoại: ", val -> setSoDienThoai(val));

        // Đã bỏ nhập địa chỉ
        this.diaChi = "";
        this.diemTichLuy = 0;

        System.out.println("Điểm tích lũy ban đầu: 0");
    }

    // ===== Sửa thông tin console =====
    @Override
    public void suaThongTin() {
        Scanner sc = new Scanner(System.in);

        nhapChuoiCoRangBuoc(sc, "Họ tên mới: ", val -> setHoTen(val));
        nhapChuoiCoRangBuoc(sc, "Số điện thoại mới: ", val -> setSoDienThoai(val));

        // Đã bỏ sửa địa chỉ
        this.diaChi = "";
    }

    // ===== Xuất thông tin =====
    @Override
    public void xuatThongTin() {
        System.out.println("Mã KH:          " + maDinhDanh);
        System.out.println("Họ tên:         " + hoTen);
        System.out.println("Số điện thoại:  " + soDienThoai);
        System.out.println("Điểm tích lũy:  " + diemTichLuy);
    }

    // ===== Xử lý điểm tích lũy trong object =====
    public void congDiemTichLuy(int diemCong) {
        if (diemCong <= 0) {
            throw new IllegalArgumentException("Số điểm cộng phải lớn hơn 0.");
        }

        this.diemTichLuy += diemCong;
    }

    public void truDiemTichLuy(int diemTru) {
        if (diemTru <= 0) {
            throw new IllegalArgumentException("Số điểm trừ phải lớn hơn 0.");
        }

        if (diemTru > this.diemTichLuy) {
            throw new IllegalArgumentException("Số điểm trừ không được vượt quá điểm hiện có.");
        }

        this.diemTichLuy -= diemTru;
    }

    // Giữ lại tên cũ nếu GUI đang gọi hàm này
    public void capNhatDiemTichLuy(int diemCong) {
        congDiemTichLuy(diemCong);
    }

    // ===== SQL: INSERT =====
    @Override
    public void luuVaoSQL() {
        String sql = """
                INSERT INTO KhachHang
                (maDinhDanh, hoTen, soDienThoai, diemTichLuy)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maDinhDanh);
            ps.setString(2, hoTen);
            ps.setString(3, soDienThoai);
            ps.setInt(4, diemTichLuy);

            ps.executeUpdate();

            System.out.println("Lưu khách hàng thành công! Mã: " + maDinhDanh);

        } catch (Exception e) {
            System.out.println("Lưu khách hàng thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // ===== SQL: SELECT =====
    public static List<KhachHang> docTuSQL() {
        List<KhachHang> danhSach = new ArrayList<>();

        String sql = """
                SELECT maDinhDanh, hoTen, soDienThoai, diemTichLuy
                FROM KhachHang
                ORDER BY maDinhDanh
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maDinhDanh"),
                        rs.getString("hoTen"),
                        rs.getString("soDienThoai"),
                        rs.getInt("diemTichLuy")
                );

                danhSach.add(kh);
            }

        } catch (Exception e) {
            System.out.println("Đọc danh sách khách hàng thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }

        return danhSach;
    }

    // ===== SQL: UPDATE =====
    public void capNhatSQL() {
        String sql = """
                UPDATE KhachHang
                SET hoTen = ?, soDienThoai = ?, diemTichLuy = ?
                WHERE maDinhDanh = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hoTen);
            ps.setString(2, soDienThoai);
            ps.setInt(3, diemTichLuy);
            ps.setString(4, maDinhDanh);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Cập nhật khách hàng thành công! Mã: " + maDinhDanh);
            } else {
                System.out.println("Không tìm thấy khách hàng có mã: " + maDinhDanh);
            }

        } catch (Exception e) {
            System.out.println("Cập nhật khách hàng thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // ===== SQL: HỖ TRỢ CẬP NHẬT ĐIỂM TÍCH LŨY =====
    public void taiLaiDiemTichLuyTuSQL() throws Exception {
        this.diemTichLuy = layDiemTichLuySQL(this.maDinhDanh);
    }

    public static int layDiemTichLuySQL(String maDinhDanh) throws Exception {
        if (maDinhDanh == null || maDinhDanh.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khách hàng không được để trống.");
        }

        String sql = "SELECT diemTichLuy FROM KhachHang WHERE maDinhDanh = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maDinhDanh.trim().toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("diemTichLuy");
                }
            }
        }

        throw new IllegalArgumentException("Không tìm thấy khách hàng: " + maDinhDanh);
    }

    public static void capNhatDiemTichLuySQL(String maDinhDanh, int diemMoi) throws Exception {
        if (maDinhDanh == null || maDinhDanh.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khách hàng không được để trống.");
        }

        if (diemMoi < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không được âm.");
        }

        String sql = "UPDATE KhachHang SET diemTichLuy = ? WHERE maDinhDanh = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, diemMoi);
            ps.setString(2, maDinhDanh.trim().toUpperCase());

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new IllegalArgumentException("Không tìm thấy khách hàng: " + maDinhDanh);
            }
        }
    }

    public static void congDiemTichLuySQL(String maDinhDanh, int diemCong) throws Exception {
        if (diemCong <= 0) {
            throw new IllegalArgumentException("Số điểm cộng phải lớn hơn 0.");
        }

        int diemHienCo = layDiemTichLuySQL(maDinhDanh);
        capNhatDiemTichLuySQL(maDinhDanh, diemHienCo + diemCong);
    }

    public static void truHetDiemVaCongDiemSQL(String maDinhDanh, int diemCongMoi) throws Exception {
        if (diemCongMoi < 0) {
            throw new IllegalArgumentException("Số điểm cộng mới không được âm.");
        }

        // Theo nghiệp vụ hóa đơn: nếu dùng điểm thì điểm cũ về 0, sau đó cộng điểm mới.
        capNhatDiemTichLuySQL(maDinhDanh, diemCongMoi);
    }

    // ===== SQL: DELETE =====
    public static void xoaKhoiSQL(String maDinhDanh) {
        String sql = "DELETE FROM KhachHang WHERE maDinhDanh = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maDinhDanh);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Xóa khách hàng thành công! Mã: " + maDinhDanh);
            } else {
                System.out.println("Không tìm thấy khách hàng có mã: " + maDinhDanh);
            }

        } catch (Exception e) {
            System.out.println("Xóa khách hàng thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // ===== SQL: SEARCH =====
    public static List<KhachHang> timKiemSQL(String keyword) {
        List<KhachHang> ds = new ArrayList<>();

        String sql = """
                SELECT maDinhDanh, hoTen, soDienThoai, diemTichLuy
                FROM KhachHang
                WHERE maDinhDanh LIKE ?
                   OR hoTen LIKE ?
                   OR soDienThoai LIKE ?
                ORDER BY maDinhDanh
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String key = "%" + (keyword == null ? "" : keyword.trim()) + "%";

            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KhachHang kh = new KhachHang(
                            rs.getString("maDinhDanh"),
                            rs.getString("hoTen"),
                            rs.getString("soDienThoai"),
                            rs.getInt("diemTichLuy")
                    );

                    ds.add(kh);
                }
            }

        } catch (Exception e) {
            System.out.println("Tìm kiếm khách hàng thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }

        return ds;
    }

    // ===== Tìm theo SĐT =====
    public static KhachHang timTheoSoDienThoaiSQL(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return null;
        }

        String sql = """
                SELECT maDinhDanh, hoTen, soDienThoai, diemTichLuy
                FROM KhachHang
                WHERE soDienThoai = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, soDienThoai.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                            rs.getString("maDinhDanh"),
                            rs.getString("hoTen"),
                            rs.getString("soDienThoai"),
                            rs.getInt("diemTichLuy")
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("Tìm khách hàng theo SĐT thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }

        return null;
    }

    // ===== Tạo khách lẻ theo SĐT nếu chưa tồn tại =====
    public static KhachHang taoKhachLeTheoSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại khách hàng không được để trống.");
        }

        KhachHang daCo = timTheoSoDienThoaiSQL(soDienThoai);
        if (daCo != null) {
            return daCo;
        }

        KhachHang kh = new KhachHang(
                "Khách lẻ " + soDienThoai.trim(),
                soDienThoai.trim(),
                0
        );

        kh.luuVaoSQL();
        return kh;
    }

    // ===== Hiển thị danh sách =====
    public static void hienThiDanhSach(List<KhachHang> ds) {
        if (ds == null || ds.isEmpty()) {
            System.out.println("Danh sách khách hàng rỗng.");
            return;
        }

        for (KhachHang kh : ds) {
            System.out.println("--------------------");
            kh.xuatThongTin();
        }
    }

    // ===== Tiện ích nhập =====
    private void nhapChuoiCoRangBuoc(
            Scanner sc,
            String label,
            java.util.function.Consumer<String> setter
    ) {
        while (true) {
            System.out.print(label);

            try {
                setter.accept(sc.nextLine());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("[Lỗi] " + e.getMessage());
            }
        }
    }
}