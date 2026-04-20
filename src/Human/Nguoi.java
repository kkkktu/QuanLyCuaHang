package Human;

import java.util.UUID;

public abstract class Nguoi {
    protected String maDinhDanh;
    protected String hoTen;
    protected String soDienThoai;
    protected String diaChi;

    // ===== Constructor =====
    public Nguoi() {
        this.maDinhDanh = taoMaTuDong();
    }

    public Nguoi(String hoTen, String soDienThoai, String diaChi) {
        this.maDinhDanh = taoMaTuDong();
        setHoTen(hoTen);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
    }

    // ===== Sinh mã tự động (lớp con định nghĩa prefix) =====
    protected abstract String layPrefix();

    private String taoMaTuDong() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return layPrefix() + "-" + uuid;
    }

    // ===== Getter =====
    public String getMaDinhDanh() { return maDinhDanh; }
    public String getHoTen()      { return hoTen; }
    public String getSoDienThoai(){ return soDienThoai; }
    public String getDiaChi()     { return diaChi; }

    // ===== Setter có ràng buộc =====
    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty())
            throw new IllegalArgumentException("Họ tên không được để trống.");
        if (hoTen.trim().length() < 2 || hoTen.trim().length() > 100)
            throw new IllegalArgumentException("Họ tên phải từ 2 đến 100 ký tự.");
        if (!hoTen.trim().matches("[\\p{L} ]+"))
            throw new IllegalArgumentException("Họ tên chỉ được chứa chữ cái và khoảng trắng.");
        this.hoTen = hoTen.trim();
    }

    /**
     * Gán SĐT: kiểm tra định dạng + đăng ký vào Registry để đảm bảo độc nhất.
     * Nếu đây là lần CẬP NHẬT (đã có SĐT cũ), registry tự giải phóng SĐT cũ.
     */
    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty())
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        if (!soDienThoai.trim().matches("(0[35789])[0-9]{8}"))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (VD: 0912345678).");

        String sdtMoi = soDienThoai.trim();
        NguoiRegistry reg = NguoiRegistry.getInstance();

        if (this.soDienThoai == null) {
            // Lần đầu gán → đăng ký mới
            reg.dangKySdt(this, sdtMoi);
        } else if (!this.soDienThoai.equals(sdtMoi)) {
            // Cập nhật → giải phóng cũ, đăng ký mới
            reg.capNhatSdt(this, sdtMoi);
        }
        this.soDienThoai = sdtMoi;
    }

    public void setDiaChi(String diaChi) {
        if (diaChi == null || diaChi.trim().isEmpty())
            throw new IllegalArgumentException("Địa chỉ không được để trống.");
        if (diaChi.trim().length() > 200)
            throw new IllegalArgumentException("Địa chỉ không được vượt quá 200 ký tự.");
        this.diaChi = diaChi.trim();
    }

    // ===== Method chung =====
    public abstract void nhapThongTin();
    public abstract void suaThongTin();
    public abstract void luuVaoSQL();

    public void xuatThongTin() {
        System.out.println("Mã:      " + maDinhDanh);
        System.out.println("Họ tên:  " + hoTen);
        System.out.println("SĐT:     " + soDienThoai);
        System.out.println("Địa chỉ: " + diaChi);
    }

    // ===== So sánh theo mã định danh =====
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Nguoi)) return false;
        Nguoi other = (Nguoi) obj;
        return maDinhDanh != null && maDinhDanh.equals(other.maDinhDanh);
    }

    @Override
    public int hashCode() {
        return maDinhDanh != null ? maDinhDanh.hashCode() : 0;
    }
}
