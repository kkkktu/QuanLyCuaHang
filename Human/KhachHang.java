package Human;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import database.DBConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class KhachHang extends Nguoi {

    private int diemTichLuy;

    // ===== Prefix cho mã tự động =====
    @Override
    protected String layPrefix() { return "KH"; }

    // ===== Constructor =====
    public KhachHang() {
        super();
        this.diemTichLuy = 0;
    }

    public KhachHang(String hoTen, String sdt, String diaChi) {
        super(hoTen, sdt, diaChi);
        this.diemTichLuy = 0;
    }

    public KhachHang(String hoTen, String sdt, String diaChi, int diemTichLuy) {
        super(hoTen, sdt, diaChi);
        setDiemTichLuy(diemTichLuy);
    }

    // ===== Getter / Setter có ràng buộc =====
    public int getDiemTichLuy() { return diemTichLuy; }

    public void setDiemTichLuy(int diem) {
        if (diem < 0)
            throw new IllegalArgumentException("Điểm tích lũy không được âm.");
        this.diemTichLuy = diem;
    }

    // ===== Nhập thông tin =====
    @Override
    public void nhapThongTin() {
        Scanner sc = new Scanner(System.in);

        System.out.println(">>> Mã khách hàng được tự động tạo: " + maDinhDanh);

        nhapChuoiCoRangBuoc(sc, "Họ tên: ",   val -> setHoTen(val));
        nhapChuoiCoRangBuoc(sc, "SĐT: ",      val -> setSoDienThoai(val));
        nhapChuoiCoRangBuoc(sc, "Địa chỉ: ",  val -> setDiaChi(val));

        this.diemTichLuy = 0; // khách mới mặc định 0 điểm
        System.out.println("  Điểm tích lũy ban đầu: 0");
    }

    // ===== Sửa thông tin =====
    @Override
    public void suaThongTin() {
        Scanner sc = new Scanner(System.in);

        nhapChuoiCoRangBuoc(sc, "Họ tên mới: ",   val -> setHoTen(val));
        nhapChuoiCoRangBuoc(sc, "SĐT mới: ",      val -> setSoDienThoai(val));
        nhapChuoiCoRangBuoc(sc, "Địa chỉ mới: ",  val -> setDiaChi(val));
    }

    // ===== Xuất thông tin =====
    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        System.out.println("Điểm tích lũy: " + diemTichLuy);
    }

    // ===== Cập nhật điểm tích lũy =====
    public void capNhatDiemTichLuy(int diemCong) {
        if (diemCong <= 0)
            throw new IllegalArgumentException("Số điểm cộng phải lớn hơn 0.");
        this.diemTichLuy += diemCong;
    }

    public void truDiemTichLuy(int diemTru) {
        if (diemTru <= 0)
            throw new IllegalArgumentException("Số điểm trừ phải lớn hơn 0.");
        if (diemTru > this.diemTichLuy)
            throw new IllegalArgumentException("Số điểm trừ vượt quá điểm hiện có (" + diemTichLuy + ").");
        this.diemTichLuy -= diemTru;
    }

    // ===== Hiển thị danh sách =====
    public static void hienThiDanhSach(List<KhachHang> ds) {
        for (KhachHang kh : ds) {
            System.out.println("--------------------");
            kh.xuatThongTin();
        }
    }

    // ===== SQL =====
    @Override
    public void luuVaoSQL() {
        /*
         * String sql = "INSERT INTO KhachHang VALUES (?,?,?,?,?)";
         * ps.setString(1, maDinhDanh);
         * ps.setString(2, hoTen);
         * ps.setString(3, soDienThoai);
         * ps.setString(4, diaChi);
         * ps.setInt(5, diemTichLuy);
         */
        System.out.println("Lưu khách hàng vào SQL... Mã: " + maDinhDanh);
    }

    public static List<KhachHang> docTuSQL() {
        List<KhachHang> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.maDinhDanh = rs.getString(1);
                kh.hoTen = rs.getString(2);
                kh.soDienThoai = rs.getString(3);
                kh.diaChi = rs.getString(4);
                kh.diemTichLuy = rs.getInt(5);
                danhSach.add(kh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Đọc danh sách khách hàng từ SQL thành công.");
        return danhSach;
    }

    // ===== Tiện ích nhập có vòng lặp kiểm tra =====
    private void nhapChuoiCoRangBuoc(Scanner sc, String label, java.util.function.Consumer<String> setter) {
        while (true) {
            System.out.print(label);
            try {
                setter.accept(sc.nextLine());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("  [Lỗi] " + e.getMessage());
            }
        }
    }
}
