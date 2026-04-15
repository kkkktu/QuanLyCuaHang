package GUI;

import Human.KhachHang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class KhachHangPanel extends JPanel {
    private JTextField txtHoTen, txtSdt, txtDiaChi, txtDiemTichLuy;
    private JTextField txtTimKiem;
    private JTable tableKhachHang;
    private DefaultTableModel tableModel;
    private List<KhachHang> danhSachKhachHang;
    private String maKhachHangDaChon = null;

    public KhachHangPanel() {
        danhSachKhachHang = KhachHang.docTuSQL();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Tìm Kiếm ---
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlTimKiem.add(new JLabel("Tìm kiếm (Mã/Tên):"));
        txtTimKiem = new JTextField(20);
        JButton btnTimKiem = new JButton("Tìm Kiếm");
        JButton btnHienTatCa = new JButton("Hiển Thị Tất Cả");
        pnlTimKiem.add(txtTimKiem);
        pnlTimKiem.add(btnTimKiem);
        pnlTimKiem.add(btnHienTatCa);

        // --- Panel Nhập liệu ---
        JPanel pnlInput = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin Khách Hàng"));

        pnlInput.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        pnlInput.add(txtHoTen);

        pnlInput.add(new JLabel("Số điện thoại:"));
        txtSdt = new JTextField();
        pnlInput.add(txtSdt);

        pnlInput.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        pnlInput.add(txtDiaChi);

        pnlInput.add(new JLabel("Điểm tích lũy:"));
        txtDiemTichLuy = new JTextField("0");
        pnlInput.add(txtDiemTichLuy);

        // --- Panel Nút chức năng ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnThem = new JButton("Thêm Khách Hàng");
        JButton btnSua = new JButton("Sửa Thông Tin");
        JButton btnXoa = new JButton("Xóa Khách Hàng");
        JButton btnClear = new JButton("Làm Mới Form");

        btnThem.setBackground(new Color(46, 204, 113));
        btnSua.setBackground(new Color(52, 152, 219));
        btnXoa.setBackground(new Color(231, 76, 60));
        btnClear.setBackground(new Color(241, 196, 15));

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnClear);

        // Ghép phần trên
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlTimKiem, BorderLayout.NORTH);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlButtons, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);

        // --- Bảng hiển thị (JTable) ---
        String[] columnNames = {"Mã KH", "Họ Tên", "SĐT", "Địa Chỉ", "Điểm Tích Lũy"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableKhachHang = new JTable(tableModel);
        tableKhachHang.setRowHeight(25);
        tableKhachHang.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        tableKhachHang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableKhachHang.getSelectedRow();
                if (selectedRow >= 0) {
                    maKhachHangDaChon = tableModel.getValueAt(selectedRow, 0).toString();
                    
                    for (KhachHang kh : danhSachKhachHang) {
                        if (kh.getMaDinhDanh().equals(maKhachHangDaChon)) {
                            txtHoTen.setText(kh.getHoTen() != null ? kh.getHoTen() : "");
                            txtSdt.setText(kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "");
                            txtDiaChi.setText(kh.getDiaChi() != null ? kh.getDiaChi() : "");
                            txtDiemTichLuy.setText(String.valueOf(kh.getDiemTichLuy()));
                            break;
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableKhachHang);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách Khách Hàng"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Xử lý Sự kiện ---
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        btnHienTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            danhSachKhachHang = KhachHang.docTuSQL();
            capNhatBang();
            xoaRongForm();
        });
        btnThem.addActionListener(this::xuLyThemKhachHang);
        btnSua.addActionListener(this::xuLySuaKhachHang);
        btnXoa.addActionListener(this::xuLyXoaKhachHang);
        btnClear.addActionListener(e -> xoaRongForm());

        capNhatBang();
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            danhSachKhachHang = KhachHang.docTuSQL();
        } else {
            danhSachKhachHang = KhachHang.timKiemSQL(keyword);
        }
        capNhatBang();
    }

    private void xuLyThemKhachHang(ActionEvent e) {
        try {
            String hoTen = txtHoTen.getText();
            String sdt = txtSdt.getText();
            String diaChi = txtDiaChi.getText();
            String diemStr = txtDiemTichLuy.getText();

            int diem = 0;
            if (!diemStr.trim().isEmpty()) {
                try {
                    diem = Integer.parseInt(diemStr.trim());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Điểm tích lũy phải là số nguyên.");
                }
            }

            KhachHang kh = new KhachHang();
            kh.setHoTen(hoTen);
            kh.setSoDienThoai(sdt);
            kh.setDiaChi(diaChi);
            if (diem > 0) {
                kh.setDiemTichLuy(diem);
            }

            kh.luuVaoSQL();
            
            danhSachKhachHang = KhachHang.docTuSQL();
            capNhatBang();
            xoaRongForm();

            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void xuLySuaKhachHang(ActionEvent e) {
        if (maKhachHangDaChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            KhachHang kh = null;
            for (KhachHang item : danhSachKhachHang) {
                if (item.getMaDinhDanh().equals(maKhachHangDaChon)) {
                    kh = item;
                    break;
                }
            }
            if (kh == null) return;
            
            kh.setHoTen(txtHoTen.getText());
            kh.setSoDienThoai(txtSdt.getText());
            kh.setDiaChi(txtDiaChi.getText());
            
            String diemStr = txtDiemTichLuy.getText();
            if (!diemStr.trim().isEmpty()) {
                kh.setDiemTichLuy(Integer.parseInt(diemStr.trim()));
            }

            kh.capNhatSQL();

            danhSachKhachHang = KhachHang.docTuSQL();
            capNhatBang();
            xoaRongForm();
            
            JOptionPane.showMessageDialog(this, "Sửa thông tin khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyXoaKhachHang(ActionEvent e) {
        if (maKhachHangDaChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khách hàng " + maKhachHangDaChon + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            KhachHang.xoaKhoiSQL(maKhachHangDaChon);
            danhSachKhachHang = KhachHang.docTuSQL();
            capNhatBang();
            xoaRongForm();
            JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xoaRongForm() {
        maKhachHangDaChon = null;
        tableKhachHang.clearSelection();
        txtHoTen.setText("");
        txtSdt.setText("");
        txtDiaChi.setText("");
        txtDiemTichLuy.setText("0");
        txtHoTen.requestFocus();
    }

    private void capNhatBang() {
        tableModel.setRowCount(0); 
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
}
