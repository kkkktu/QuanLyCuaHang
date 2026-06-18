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

public class NhaCungCap {

    private String maNCC;
    private String tenCongTy;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String nguoiLienHe;
    private String moTa;
    private String trangThai;

    private static String taoMaTuDong() {
        String uuid = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        return "NCC-" + uuid;
    }

    public NhaCungCap() {
        this.maNCC = taoMaTuDong();
        this.moTa = "";
        this.trangThai = "Đang hợp tác";
    }

    public NhaCungCap(String tenCongTy, String soDienThoai, String email,
                      String diaChi, String nguoiLienHe, String moTa) {
        this.maNCC = taoMaTuDong();
        setTenCongTy(tenCongTy);
        setSoDienThoai(soDienThoai);
        setEmail(email);
        setDiaChi(diaChi);
        setNguoiLienHe(nguoiLienHe);
        setMoTa(moTa);
        setTrangThai("Đang hợp tác");
    }

    public NhaCungCap(String maNCC, String tenCongTy, String soDienThoai, String email,
                      String diaChi, String nguoiLienHe, String moTa) {
        this(maNCC, tenCongTy, soDienThoai, email, diaChi, nguoiLienHe, moTa, "Đang hợp tác");
    }

    public NhaCungCap(String maNCC, String tenCongTy, String soDienThoai, String email,
                      String diaChi, String nguoiLienHe, String moTa, String trangThai) {
        setMaNCC(maNCC);
        setTenCongTy(tenCongTy);
        setSoDienThoai(soDienThoai);
        setEmail(email);
        setDiaChi(diaChi);
        setNguoiLienHe(nguoiLienHe);
        setMoTa(moTa);
        setTrangThai(trangThai);
    }

    public String getMaNCC() {
        return maNCC;
    }

    public String getTenCongTy() {
        return tenCongTy;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public String getNguoiLienHe() {
        return nguoiLienHe;
    }

    public String getMoTa() {
        return moTa;
    }

    public String getTrangThai() {
        return trangThai == null || trangThai.isBlank() ? "Đang hợp tác" : trangThai;
    }

    public void setTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            this.trangThai = "Đang hợp tác";
            return;
        }
        trangThai = trangThai.trim();
        if (!trangThai.equals("Đang hợp tác") && !trangThai.equals("Ngừng hợp tác")) {
            throw new IllegalArgumentException("Trạng thái nhà cung cấp chỉ được là Đang hợp tác hoặc Ngừng hợp tác.");
        }
        this.trangThai = trangThai;
    }

    public void setMaNCC(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhà cung cấp không được để trống.");
        }

        maNCC = maNCC.trim().toUpperCase();

        if (!maNCC.matches("NCC-[A-Z0-9]{6}")) {
            throw new IllegalArgumentException("Mã NCC không hợp lệ. Định dạng đúng: NCC-XXXXXX.");
        }

        this.maNCC = maNCC;
    }

    public void setTenCongTy(String tenCongTy) {
        if (tenCongTy == null || tenCongTy.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên công ty không được để trống.");
        }

        // Bỏ điều kiện độ dài 2-200 ký tự theo yêu cầu.
        this.tenCongTy = tenCongTy.trim();
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }

        soDienThoai = soDienThoai.trim();

        if (!soDienThoai.matches("0[0-9]{9}")) {
            throw new IllegalArgumentException("SĐT gồm 10 số nguyên dương và bắt đầu bằng số 0.");
        }

        this.soDienThoai = soDienThoai;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }

        email = email.trim().toLowerCase();

        if (email.length() > 150) {
            throw new IllegalArgumentException("Email không được vượt quá 150 ký tự.");
        }

        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }

        this.email = email;
    }

    public void setDiaChi(String diaChi) {
        if (diaChi == null || diaChi.trim().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ không được để trống.");
        }

        diaChi = diaChi.trim();

        if (diaChi.length() > 200) {
            throw new IllegalArgumentException("Địa chỉ không được vượt quá 200 ký tự.");
        }

        this.diaChi = diaChi;
    }

    public void setNguoiLienHe(String nguoiLienHe) {
        if (nguoiLienHe == null || nguoiLienHe.trim().isEmpty()) {
            throw new IllegalArgumentException("Người liên hệ không được để trống.");
        }

        nguoiLienHe = nguoiLienHe.trim();

        // Bỏ điều kiện độ dài 2-100 ký tự theo yêu cầu.
        // Vẫn giữ điều kiện chỉ gồm chữ cái và khoảng trắng.
        if (!nguoiLienHe.matches("[\\p{L}\\s]+")) {
            throw new IllegalArgumentException("Người liên hệ chỉ được chứa chữ cái và khoảng trắng.");
        }

        this.nguoiLienHe = nguoiLienHe;
    }

    public void setMoTa(String moTa) {
        if (moTa == null) {
            this.moTa = "";
            return;
        }

        moTa = moTa.trim();

        if (moTa.length() > 500) {
            throw new IllegalArgumentException("Mô tả không được vượt quá 500 ký tự.");
        }

        this.moTa = moTa;
    }

    public void nhapThongTin() {
        Scanner sc = new Scanner(System.in);

        System.out.println(">>> Mã nhà cung cấp được tự động tạo: " + maNCC);

        nhapCoRangBuoc(sc, "Tên công ty: ", val -> setTenCongTy(val));
        nhapCoRangBuoc(sc, "Số điện thoại: ", val -> setSoDienThoai(val));
        nhapCoRangBuoc(sc, "Email: ", val -> setEmail(val));
        nhapCoRangBuoc(sc, "Địa chỉ: ", val -> setDiaChi(val));
        nhapCoRangBuoc(sc, "Người liên hệ: ", val -> setNguoiLienHe(val));
        nhapCoRangBuoc(sc, "Mô tả: ", val -> setMoTa(val));
    }

    public void suaThongTin() {
        Scanner sc = new Scanner(System.in);

        System.out.println(">>> Sửa thông tin nhà cung cấp [" + maNCC + "]");

        nhapCoRangBuoc(sc, "Tên công ty mới: ", val -> setTenCongTy(val));
        nhapCoRangBuoc(sc, "Số điện thoại mới: ", val -> setSoDienThoai(val));
        nhapCoRangBuoc(sc, "Email mới: ", val -> setEmail(val));
        nhapCoRangBuoc(sc, "Địa chỉ mới: ", val -> setDiaChi(val));
        nhapCoRangBuoc(sc, "Người liên hệ mới: ", val -> setNguoiLienHe(val));
        nhapCoRangBuoc(sc, "Mô tả mới: ", val -> setMoTa(val));
    }

    public void xuatThongTin() {
        System.out.println("Mã NCC: " + maNCC);
        System.out.println("Tên công ty: " + tenCongTy);
        System.out.println("Số điện thoại: " + soDienThoai);
        System.out.println("Email: " + email);
        System.out.println("Địa chỉ: " + diaChi);
        System.out.println("Người liên hệ: " + nguoiLienHe);
        System.out.println("Mô tả: " + (moTa == null || moTa.isEmpty() ? "(không có)" : moTa));
        System.out.println("Trạng thái: " + getTrangThai());
    }

    public void luuVaoSQL() {
        String sql = """
                INSERT INTO NhaCungCap
                (maNCC, tenCongTy, soDienThoai, email, diaChi, nguoiLienHe, moTa, trangThai)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNCC);
            ps.setString(2, tenCongTy);
            ps.setString(3, soDienThoai);
            ps.setString(4, email);
            ps.setString(5, diaChi);
            ps.setString(6, nguoiLienHe);
            ps.setString(7, moTa);
            ps.setString(8, getTrangThai());

            ps.executeUpdate();
            System.out.println("Lưu nhà cung cấp thành công! Mã: " + maNCC);

        } catch (Exception e) {
            System.out.println("Lưu nhà cung cấp thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public static List<NhaCungCap> docTuSQL() {
        List<NhaCungCap> ds = new ArrayList<>();

        String sql = """
                SELECT maNCC, tenCongTy, soDienThoai, email, diaChi, nguoiLienHe, moTa,
                       ISNULL(trangThai, N'Đang hợp tác') AS trangThai
                FROM NhaCungCap
                ORDER BY maNCC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap(
                        rs.getString("maNCC"),
                        rs.getString("tenCongTy"),
                        rs.getString("soDienThoai"),
                        rs.getString("email"),
                        rs.getString("diaChi"),
                        rs.getString("nguoiLienHe"),
                        rs.getString("moTa"),
                        rs.getString("trangThai")
                );

                ds.add(ncc);
            }

        } catch (Exception e) {
            System.out.println("Đọc danh sách nhà cung cấp thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }

        return ds;
    }

    public void capNhatSQL() {
        String sql = """
                UPDATE NhaCungCap
                SET tenCongTy = ?, soDienThoai = ?, email = ?, diaChi = ?, nguoiLienHe = ?, moTa = ?, trangThai = ?
                WHERE maNCC = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenCongTy);
            ps.setString(2, soDienThoai);
            ps.setString(3, email);
            ps.setString(4, diaChi);
            ps.setString(5, nguoiLienHe);
            ps.setString(6, moTa);
            ps.setString(7, getTrangThai());
            ps.setString(8, maNCC);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Cập nhật nhà cung cấp thành công! Mã: " + maNCC);
            } else {
                System.out.println("Không tìm thấy nhà cung cấp có mã: " + maNCC);
            }

        } catch (Exception e) {
            System.out.println("Cập nhật nhà cung cấp thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    public static void xoaKhoiSQL(String maNCC) {
        String sql = "UPDATE NhaCungCap SET trangThai = N'Ngừng hợp tác' WHERE maNCC = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNCC);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Đã chuyển nhà cung cấp sang trạng thái ngừng hợp tác! Mã: " + maNCC);
            } else {
                throw new IllegalArgumentException("Không tìm thấy nhà cung cấp có mã: " + maNCC);
            }

        } catch (Exception e) {
            throw new RuntimeException("Ngừng hợp tác nhà cung cấp thất bại: " + e.getMessage(), e);
        }
    }

    public static List<NhaCungCap> timKiemSQL(String keyword) {
        List<NhaCungCap> ds = new ArrayList<>();

        String sql = """
                SELECT maNCC, tenCongTy, soDienThoai, email, diaChi, nguoiLienHe, moTa,
                       ISNULL(trangThai, N'Đang hợp tác') AS trangThai
                FROM NhaCungCap
                WHERE maNCC LIKE ?
                   OR tenCongTy LIKE ?
                   OR soDienThoai LIKE ?
                   OR email LIKE ?
                   OR nguoiLienHe LIKE ?
                   OR trangThai LIKE ?
                ORDER BY maNCC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String key = "%" + keyword + "%";

            for (int i = 1; i <= 6; i++) {
                ps.setString(i, key);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhaCungCap ncc = new NhaCungCap(
                            rs.getString("maNCC"),
                            rs.getString("tenCongTy"),
                            rs.getString("soDienThoai"),
                            rs.getString("email"),
                            rs.getString("diaChi"),
                            rs.getString("nguoiLienHe"),
                            rs.getString("moTa"),
                            rs.getString("trangThai")
                    );

                    ds.add(ncc);
                }
            }

        } catch (Exception e) {
            System.out.println("Tìm kiếm nhà cung cấp thất bại!");
            System.out.println("Lỗi: " + e.getMessage());
        }

        return ds;
    }


    public static void capNhatTrangThaiSQL(String maNCC, String trangThaiMoi) {
        if (maNCC == null || maNCC.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhà cung cấp không được để trống.");
        }
        if (trangThaiMoi == null || trangThaiMoi.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái nhà cung cấp không được để trống.");
        }

        trangThaiMoi = trangThaiMoi.trim();
        if (!trangThaiMoi.equals("Đang hợp tác") && !trangThaiMoi.equals("Ngừng hợp tác")) {
            throw new IllegalArgumentException("Trạng thái nhà cung cấp chỉ được là Đang hợp tác hoặc Ngừng hợp tác.");
        }

        String sql = "UPDATE NhaCungCap SET trangThai = ? WHERE maNCC = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThaiMoi);
            ps.setString(2, maNCC.trim().toUpperCase());

            int rows = ps.executeUpdate();
            if (rows <= 0) {
                throw new IllegalArgumentException("Không tìm thấy nhà cung cấp có mã: " + maNCC);
            }

        } catch (Exception e) {
            throw new RuntimeException("Cập nhật trạng thái nhà cung cấp thất bại: " + e.getMessage(), e);
        }
    }

    public static void hienThiDanhSach(List<NhaCungCap> ds) {
        if (ds == null || ds.isEmpty()) {
            System.out.println("Danh sách nhà cung cấp rỗng.");
            return;
        }

        for (NhaCungCap ncc : ds) {
            System.out.println("--------------------");
            ncc.xuatThongTin();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NhaCungCap)) return false;

        NhaCungCap other = (NhaCungCap) obj;
        return maNCC != null && maNCC.equals(other.maNCC);
    }

    @Override
    public int hashCode() {
        return maNCC != null ? maNCC.hashCode() : 0;
    }

    private void nhapCoRangBuoc(Scanner sc, String label, Consumer<String> setter) {
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