package model;


public interface QuanLySQL {

    /**
     * Lưu đối tượng vào cơ sở dữ liệu (INSERT).
     * @throws Exception nếu vi phạm ràng buộc SQL hoặc mất kết nối.
     */
    void luuVaoSQL() throws Exception;

    /**
     * Cập nhật thông tin đối tượng trong cơ sở dữ liệu (UPDATE).
     * @throws Exception nếu đối tượng không tồn tại hoặc mất kết nối.
     */
    void capNhatSQL() throws Exception;

    /**
     * Kiểm tra dữ liệu hợp lệ trước khi lưu / cập nhật.
     * Lớp implement override để thêm ràng buộc nghiệp vụ riêng.
     * @throws IllegalArgumentException nếu dữ liệu không hợp lệ.
     */
    default void kiemTraDuLieu() throws IllegalArgumentException {
        // Mặc định không làm gì – lớp con override nếu cần
    }
}