package model;

import java.util.HashMap;
import java.util.Map;


public class NguoiRegistry {

    // ===== Singleton =====
    private static NguoiRegistry instance;

    private NguoiRegistry() {}

    public static NguoiRegistry getInstance() {
        if (instance == null) {
            instance = new NguoiRegistry();
        }
        return instance;
    }

    // ===== Kho lưu trữ: value -> chủ sở hữu =====
    // key = số điện thoại, value = maDinhDanh của người đang dùng
    private final Map<String, String> sdtDaDung   = new HashMap<>();
    // key = cccd, value = maDinhDanh
    private final Map<String, String> cccdDaDung  = new HashMap<>();

    // ===== Đăng ký SĐT =====
    /**
     * Gọi khi gán SĐT lần đầu cho một đối tượng.
     * @throws IllegalArgumentException nếu SĐT đã thuộc về người khác.
     */
    public void dangKySdt(Nguoi nguoi, String sdt) {
        kiemTraSdtTrungLap(nguoi.getMaDinhDanh(), sdt);
        sdtDaDung.put(sdt, nguoi.getMaDinhDanh());
    }

    /**
     * Gọi khi cập nhật SĐT (xóa cái cũ, đăng ký cái mới).
     */
    public void capNhatSdt(Nguoi nguoi, String sdtMoi) {
        // Xóa SĐT cũ nếu có
        sdtDaDung.values().remove(nguoi.getMaDinhDanh());
        kiemTraSdtTrungLap(nguoi.getMaDinhDanh(), sdtMoi);
        sdtDaDung.put(sdtMoi, nguoi.getMaDinhDanh());
    }

    private void kiemTraSdtTrungLap(String maChuSoHuu, String sdt) {
        if (sdtDaDung.containsKey(sdt)) {
            String maNguoiKhac = sdtDaDung.get(sdt);
            if (!maNguoiKhac.equals(maChuSoHuu)) {
                throw new IllegalArgumentException(
                    "Số điện thoại " + sdt + " đã được sử dụng bởi [" + maNguoiKhac + "].");
            }
        }
    }

    // ===== Đăng ký CCCD =====
    /**
     * Gọi khi gán CCCD lần đầu cho một NhanVien.
     * @throws IllegalArgumentException nếu CCCD đã thuộc về người khác.
     */
    public void dangKyCccd(Nguoi nguoi, String cccd) {
        kiemTraCccdTrungLap(nguoi.getMaDinhDanh(), cccd);
        cccdDaDung.put(cccd, nguoi.getMaDinhDanh());
    }

    /**
     * Gọi khi cập nhật CCCD (trường hợp hiếm gặp).
     */
    public void capNhatCccd(Nguoi nguoi, String cccdMoi) {
        cccdDaDung.values().remove(nguoi.getMaDinhDanh());
        kiemTraCccdTrungLap(nguoi.getMaDinhDanh(), cccdMoi);
        cccdDaDung.put(cccdMoi, nguoi.getMaDinhDanh());
    }

    private void kiemTraCccdTrungLap(String maChuSoHuu, String cccd) {
        if (cccdDaDung.containsKey(cccd)) {
            String maNguoiKhac = cccdDaDung.get(cccd);
            if (!maNguoiKhac.equals(maChuSoHuu)) {
                throw new IllegalArgumentException(
                    "CCCD " + cccd + " đã được đăng ký bởi nhân viên [" + maNguoiKhac + "].");
            }
        }
    }

    // ===== Hủy đăng ký (khi xóa đối tượng) =====
    public void huyDangKy(Nguoi nguoi) {
        sdtDaDung.values().remove(nguoi.getMaDinhDanh());
        cccdDaDung.values().remove(nguoi.getMaDinhDanh());
    }

    // ===== Tiện ích kiểm tra nhanh =====
    public boolean sdtDaTonTai(String sdt) {
        return sdtDaDung.containsKey(sdt);
    }

    public boolean cccdDaTonTai(String cccd) {
        return cccdDaDung.containsKey(cccd);
    }
}