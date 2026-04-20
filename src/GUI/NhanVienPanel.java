package GUI;

import Human.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class NhanVienPanel extends JPanel {
    private JTextField txtHoTen, txtSdt, txtDiaChi, txtNgaySinh, txtEmail, txtCccd;
    private JTextField txtChucVu, txtLuong;
    private JTextField txtGioVaoLam, txtGioKetThuc;
    private JTextField txtTimKiem;
    
    private JTable tableNhanVien;
    private DefaultTableModel tableModel;
    private List<NhanVien> danhSachNhanVien;
    private String maNhanVienDaChon = null;

    public NhanVienPanel() {
        danhSachNhanVien = NhanVien.docTuSQL();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Tìm kiếm ---
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlTimKiem.add(new JLabel("Tìm kiếm (Mã/Tên):"));
        txtTimKiem = new JTextField(20);
        JButton btnTimKiem = new JButton("Tìm Kiếm");
        JButton btnHienTatCa = new JButton("Hiển Thị Tất Cả");
        pnlTimKiem.add(txtTimKiem);
        pnlTimKiem.add(btnTimKiem);
        pnlTimKiem.add(btnHienTatCa);

        // --- Panel Nhập liệu (Grid Layout) ---
        JPanel pnlInput = new JPanel(new GridLayout(5, 4, 10, 10));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Nhập thông tin Nhân Viên"));

        pnlInput.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        pnlInput.add(txtHoTen);

        pnlInput.add(new JLabel("SĐT:"));
        txtSdt = new JTextField();
        pnlInput.add(txtSdt);

        pnlInput.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        pnlInput.add(txtDiaChi);

        pnlInput.add(new JLabel("Chức vụ:"));
        txtChucVu = new JTextField();
        pnlInput.add(txtChucVu);

        pnlInput.add(new JLabel("Ngày sinh (dd/MM/yyyy):"));
        txtNgaySinh = new JTextField();
        pnlInput.add(txtNgaySinh);

        pnlInput.add(new JLabel("Lương:"));
        txtLuong = new JTextField();
        pnlInput.add(txtLuong);

        pnlInput.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        pnlInput.add(txtEmail);

        pnlInput.add(new JLabel("Giờ vào làm (HH:mm):"));
        txtGioVaoLam = new JTextField();
        pnlInput.add(txtGioVaoLam);

        pnlInput.add(new JLabel("CCCD:"));
        txtCccd = new JTextField();
        pnlInput.add(txtCccd);

        pnlInput.add(new JLabel("Giờ kết thúc (HH:mm):"));
        txtGioKetThuc = new JTextField();
        pnlInput.add(txtGioKetThuc);

        // --- Panel Nút chức năng ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnClear = new JButton("Làm Mới");

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

        // --- Bảng hiển thị ---
        String[] columnNames = {
            "Mã NV", "Họ Tên", "SĐT", "Ngày Sinh", "CCCD", "Chức Vụ", "Lương", "Vào Làm", "Kết Thúc"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableNhanVien = new JTable(tableModel);
        tableNhanVien.setRowHeight(25);
        tableNhanVien.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        tableNhanVien.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableNhanVien.getSelectedRow();
                if (row >= 0) {
                    maNhanVienDaChon = tableModel.getValueAt(row, 0).toString();
                    
                    // Lấy dữ liệu từ object list cho chính xác
                    for (NhanVien nv : danhSachNhanVien) {
                        if (nv.getMaDinhDanh().equals(maNhanVienDaChon)) {
                            txtHoTen.setText(nv.getHoTen() != null ? nv.getHoTen() : "");
                            txtSdt.setText(nv.getSoDienThoai() != null ? nv.getSoDienThoai() : "");
                            txtDiaChi.setText(nv.getDiaChi() != null ? nv.getDiaChi() : "");
                            txtNgaySinh.setText(nv.getNgaySinh() != null ? java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy").format(nv.getNgaySinh()) : "");
                            txtEmail.setText(nv.getEmail() != null ? nv.getEmail() : "");
                            txtCccd.setText(nv.getCccd() != null ? nv.getCccd() : "");
                            txtChucVu.setText(nv.getChucVu() != null ? nv.getChucVu() : "");
                            txtLuong.setText(String.format("%.0f", nv.getLuong()));
                            txtGioVaoLam.setText(nv.getGioVaoLam() != null ? nv.getGioVaoLam().toString() : "");
                            txtGioKetThuc.setText(nv.getGioKetThuc() != null ? nv.getGioKetThuc().toString() : "");
                            break;
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableNhanVien);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách Nhân Viên"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Xử lý sự kiện ---
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        btnHienTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            danhSachNhanVien = NhanVien.docTuSQL();
            capNhatBang();
            xoaRongForm();
        });
        btnThem.addActionListener(this::xuLyThemNhanVien);
        btnSua.addActionListener(this::xuLySuaNhanVien);
        btnXoa.addActionListener(this::xuLyXoaNhanVien);
        btnClear.addActionListener(e -> xoaRongForm());

        capNhatBang();
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            danhSachNhanVien = NhanVien.docTuSQL();
        } else {
            danhSachNhanVien = NhanVien.timKiemSQL(keyword);
        }
        capNhatBang();
    }

    private void xuLyThemNhanVien(ActionEvent e) {
        try {
            NhanVien nv = new NhanVien();
            nv.setHoTen(txtHoTen.getText());
            nv.setSoDienThoai(txtSdt.getText());
            nv.setDiaChi(txtDiaChi.getText());
            nv.setNgaySinh(txtNgaySinh.getText());
            nv.setEmail(txtEmail.getText());
            nv.setCccd(txtCccd.getText());
            nv.setChucVu(txtChucVu.getText());
            
            String luongStr = txtLuong.getText();
            if (luongStr == null || luongStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Lương không được để trống.");
            }
            try {
                nv.setLuong(Double.parseDouble(luongStr.trim()));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Lương phải là số.");
            }
            
            nv.setGioVaoLam(txtGioVaoLam.getText());
            nv.setGioKetThuc(txtGioKetThuc.getText());

            nv.luuVaoSQL();

            danhSachNhanVien = NhanVien.docTuSQL();
            capNhatBang();
            xoaRongForm();

            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void xuLySuaNhanVien(ActionEvent e) {
        if (maNhanVienDaChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            NhanVien nv = null;
            for (NhanVien item : danhSachNhanVien) {
                if (item.getMaDinhDanh().equals(maNhanVienDaChon)) {
                    nv = item;
                    break;
                }
            }
            if (nv == null) return;
            
            nv.setHoTen(txtHoTen.getText());
            nv.setSoDienThoai(txtSdt.getText());
            nv.setDiaChi(txtDiaChi.getText());
            nv.setNgaySinh(txtNgaySinh.getText());
            nv.setEmail(txtEmail.getText());
            // CCCD in memory can just be updated without full checking from NguoiRegistry in UI because it will update DB directly.
            // But if it needs NguoiRegistry uniqueness, it might throw error. Ensure it doesn't break if CCCD is same.
            nv.setCccd(txtCccd.getText());
            nv.setChucVu(txtChucVu.getText());
            
            String luongStr = txtLuong.getText();
            try {
                nv.setLuong(Double.parseDouble(luongStr.trim()));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Lương phải là số.");
            }
            
            nv.setGioVaoLam(txtGioVaoLam.getText());
            nv.setGioKetThuc(txtGioKetThuc.getText());

            nv.capNhatSQL();

            danhSachNhanVien = NhanVien.docTuSQL();
            capNhatBang();
            xoaRongForm();
            
            JOptionPane.showMessageDialog(this, "Sửa thông tin nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyXoaNhanVien(ActionEvent e) {
        if (maNhanVienDaChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên " + maNhanVienDaChon + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            NhanVien.xoaKhoiSQL(maNhanVienDaChon);
            danhSachNhanVien = NhanVien.docTuSQL();
            capNhatBang();
            xoaRongForm();
            JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xoaRongForm() {
        maNhanVienDaChon = null;
        tableNhanVien.clearSelection();
        txtHoTen.setText("");
        txtSdt.setText("");
        txtDiaChi.setText("");
        txtNgaySinh.setText("");
        txtEmail.setText("");
        txtCccd.setText("");
        txtChucVu.setText("");
        txtLuong.setText("");
        txtGioVaoLam.setText("");
        txtGioKetThuc.setText("");
        txtHoTen.requestFocus();
    }

    private void capNhatBang() {
        tableModel.setRowCount(0);
        for (NhanVien nv : danhSachNhanVien) {
            Object[] row = {
                nv.getMaDinhDanh(),
                nv.getHoTen(),
                nv.getSoDienThoai(),
                nv.getNgaySinh() != null ? java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy").format(nv.getNgaySinh()) : "",
                nv.getCccd(),
                nv.getChucVu(),
                nv.getLuong(),
                nv.getGioVaoLam() != null ? nv.getGioVaoLam().toString() : "",
                nv.getGioKetThuc() != null ? nv.getGioKetThuc().toString() : ""
            };
            tableModel.addRow(row);
        }
    }
}
