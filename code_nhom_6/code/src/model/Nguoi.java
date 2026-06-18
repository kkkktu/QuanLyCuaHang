package model;

import java.util.UUID;

public abstract class Nguoi {

    protected String maDinhDanh;
    protected String hoTen;
    protected String soDienThoai;
    protected String diaChi;

    // ===== Constructor =====
    public Nguoi() {
        this.maDinhDanh = taoMaTuDong();
        this.diaChi = "";
    }

    public Nguoi(String hoTen, String soDienThoai, String diaChi) {
        this.maDinhDanh = taoMaTuDong();
        setHoTen(hoTen);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
    }

    public Nguoi(String maDinhDanh, String hoTen, String soDienThoai, String diaChi) {
        setMaDinhDanh(maDinhDanh);
        setHoTen(hoTen);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
    }

    // ===== Mỗi lớp con tự định nghĩa prefix: KH, NV =====
    protected abstract String layPrefix();

    private String taoMaTuDong() {
        String uuid = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        return layPrefix() + "-" + uuid;
    }

    // ===== Getter =====
    public String getMaDinhDanh() {
        return maDinhDanh;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public String getDiaChi() {
        return diaChi == null ? "" : diaChi;
    }

    // ===== Setter có ràng buộc =====
    public void setMaDinhDanh(String maDinhDanh) {
        if (maDinhDanh == null || maDinhDanh.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã định danh không được để trống.");
        }

        maDinhDanh = maDinhDanh.trim().toUpperCase();

        if (!maDinhDanh.matches(layPrefix() + "-[A-Z0-9]{6}")
                && !maDinhDanh.matches(layPrefix() + "-[0-9]{6}")) {
            throw new IllegalArgumentException(
                    "Mã định danh không hợp lệ. Định dạng đúng: " + layPrefix() + "-XXXXXX"
            );
        }

        this.maDinhDanh = maDinhDanh;
    }

    public void setHoTen(String hoTen) {
        
        this.hoTen = hoTen == null ? "" : hoTen.trim();
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }

        soDienThoai = soDienThoai.trim();

        if (!soDienThoai.matches("0[0-9]{9}")) {
            throw new IllegalArgumentException("Sdt gồm 10 chữ số nguyên dương và bắt đầu bằng số 0.");
        }

        this.soDienThoai = soDienThoai;
    }

    
    public void setDiaChi(String diaChi) {
        if (diaChi == null) {
            this.diaChi = "";
            return;
        }

        diaChi = diaChi.trim();

        if (diaChi.length() > 200) {
            throw new IllegalArgumentException("Địa chỉ không được vượt quá 200 ký tự.");
        }

        this.diaChi = diaChi;
    }

    // ===== Method chung cho lớp con tự xử lý =====
    public abstract void nhapThongTin();

    public abstract void suaThongTin();

    public abstract void luuVaoSQL();

    public void xuatThongTin() {
        System.out.println("Mã định danh:  " + maDinhDanh);
        System.out.println("Họ tên:        " + hoTen);
        System.out.println("Số điện thoại: " + soDienThoai);

        if (diaChi != null && !diaChi.isBlank()) {
            System.out.println("Địa chỉ:       " + diaChi);
        }
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