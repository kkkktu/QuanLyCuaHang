package GUI;

import Human.KhachHang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class KhachHangPanel extends JPanel {
    private JTextField txtHoTen, txtSdt, txtDiaChi, txtDiemTichLuy;
    private JTable tableKhachHang;
    private DefaultTableModel tableModel;
    private List<KhachHang> danhSachKhachHang;

    public KhachHangPanel() {
        danhSachKhachHang = KhachHang.docTuSQL();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Nhập liệu ---
        JPanel pnlInput = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Nhập thông tin Khách Hàng"));

        pnlInput.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        pnlInput.add(txtHoTen);

        pnlInput.add(new JLabel("Số điện thoại:"));
        txtSdt = new JTextField();
        pnlInput.add(txtSdt);

        pnlInput.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        pnlInput.add(txtDiaChi);

        pnlInput.add(new JLabel("Điểm tích lũy (Cộng thêm):"));
        txtDiemTichLuy = new JTextField("0");
        pnlInput.add(txtDiemTichLuy);

        // --- Panel Nút chức năng ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnThem = new JButton("Thêm Khách Hàng");
        JButton btnClear = new JButton("Làm Mới Form");
        JButton btnLuuDL = new JButton("Lưu vào SQL");

        btnThem.setBackground(new Color(46, 204, 113));
        btnClear.setBackground(new Color(241, 196, 15));
        btnLuuDL.setBackground(new Color(52, 152, 219));

        pnlButtons.add(btnThem);
        pnlButtons.add(btnClear);
        pnlButtons.add(btnLuuDL);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlButtons, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);

        // --- Bảng hiển thị (JTable) ---
        String[] columnNames = {"Mã KH", "Họ Tên", "SĐT", "Địa Chỉ", "Điểm Tích Lũy"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa trực tiếp trên bảng
            }
        };
        tableKhachHang = new JTable(tableModel);
        tableKhachHang.setRowHeight(25);
        tableKhachHang.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(tableKhachHang);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách Khách Hàng"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Xử lý Sự kiện ---
        btnThem.addActionListener(this::xuLyThemKhachHang);
        btnClear.addActionListener(e -> xoaRongForm());
        btnLuuDL.addActionListener(this::xuLyLuuSQL);

        // Cập nhật bảng ngay lần đầu bật Panel
        capNhatBang();
    }

    private void xuLyThemKhachHang(ActionEvent e) {
        try {
            // Lấy dữ liệu từ giao diện
            String hoTen = txtHoTen.getText();
            String sdt = txtSdt.getText();
            String diaChi = txtDiaChi.getText();
            String diemStr = txtDiemTichLuy.getText();

            int diemCong = 0;
            if (!diemStr.trim().isEmpty()) {
                try {
                    diemCong = Integer.parseInt(diemStr.trim());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Điểm tích lũy phải là số nguyên.");
                }
            }

            // Tạo đối tượng thông qua constructor 3 tham số và dùng capNhatDiemTichLuy
            // KhachHang sẽ tự kiểm tra ràng buộc bằng Setter bên trong model
            KhachHang kh = new KhachHang();
            kh.setHoTen(hoTen);
            kh.setSoDienThoai(sdt);
            kh.setDiaChi(diaChi);
            if (diemCong > 0) {
                kh.capNhatDiemTichLuy(diemCong);
            }

            // Thêm vào danh sách tạm và cập nhật bảng
            danhSachKhachHang.add(kh);
            capNhatBang();
            xoaRongForm();

            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void xoaRongForm() {
        txtHoTen.setText("");
        txtSdt.setText("");
        txtDiaChi.setText("");
        txtDiemTichLuy.setText("0");
        txtHoTen.requestFocus();
    }

    private void capNhatBang() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        for (KhachHang kh : danhSachKhachHang) {
            Object[] row = {
                    kh.getMaDinhDanh(),
                    kh.getHoTen(),
                    kh.getSoDienThoai(),
                    kh.getDiaChi(),
                    kh.getDiemTichLuy()
            };
            tableModel.addRow(row);
        }
    }

    private void xuLyLuuSQL(ActionEvent e) {
        if (danhSachKhachHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Danh sách rỗng, không có gì để lưu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int count = 0;
        for (KhachHang kh : danhSachKhachHang) {
            try {
                kh.luuVaoSQL();
                count++;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu khách hàng " + kh.getHoTen() + ": " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            }
        }
        JOptionPane.showMessageDialog(this, "Đã gọi hàm lưu SQL cho " + count + " khách hàng.\n(Kiểm tra console để xem chi tiết)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}
