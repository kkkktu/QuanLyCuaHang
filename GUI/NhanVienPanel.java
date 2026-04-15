package GUI;

import Human.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class NhanVienPanel extends JPanel {
    private JTextField txtHoTen, txtSdt, txtDiaChi, txtNgaySinh, txtEmail, txtCccd;
    private JTextField txtChucVu, txtLuong;
    private JTextField txtGioVaoLam, txtGioKetThuc;
    
    private JTable tableNhanVien;
    private DefaultTableModel tableModel;
    private List<NhanVien> danhSachNhanVien;

    public NhanVienPanel() {
        danhSachNhanVien = NhanVien.docTuSQL();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Nhập liệu (Grid Layout) ---
        JPanel pnlInput = new JPanel(new GridLayout(5, 4, 10, 10));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Nhập thông tin Nhân Viên"));

        // Cột 1 & 2
        pnlInput.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        pnlInput.add(txtHoTen);

        pnlInput.add(new JLabel("SĐT:"));
        txtSdt = new JTextField();
        pnlInput.add(txtSdt);

        // Cột 3 & 4
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
        JButton btnThem = new JButton("Thêm Nhân Viên");
        JButton btnClear = new JButton("Làm Mới");
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

        // --- Bảng hiển thị ---
        String[] columnNames = {
            "Mã NV", "Họ Tên", "SĐT", "Ngày Sinh", "Chức Vụ", "Lương", "Vào Làm", "Kết Thúc"
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

        JScrollPane scrollPane = new JScrollPane(tableNhanVien);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách Nhân Viên"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Xử lý sự kiện ---
        btnThem.addActionListener(this::xuLyThemNhanVien);
        btnClear.addActionListener(e -> xoaRongForm());
        btnLuuDL.addActionListener(this::xuLyLuuSQL);

        // Hiển thị dữ liệu ngay lúc tải màn hình lên
        capNhatBang();
    }

    private void xuLyThemNhanVien(ActionEvent e) {
        try {
            NhanVien nv = new NhanVien();
            // NhanVien.java đã có các hàm setter ném ra IllegalArgumentException,
            // nên ta cứ gán vào, nếu lỗi thì nhảy vào khối catch bên dưới.
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

            danhSachNhanVien.add(nv);
            capNhatBang();
            xoaRongForm();

            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

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
                nv.getChucVu(),
                nv.getLuong(),
                nv.getGioVaoLam() != null ? nv.getGioVaoLam().toString() : "",
                nv.getGioKetThuc() != null ? nv.getGioKetThuc().toString() : ""
            };
            tableModel.addRow(row);
        }
    }

    private void xuLyLuuSQL(ActionEvent e) {
        if (danhSachNhanVien.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Danh sách rỗng, không có gì để lưu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int count = 0;
        for (NhanVien nv : danhSachNhanVien) {
             try {
                nv.luuVaoSQL();
                count++;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu nhân viên " + nv.getHoTen() + ": " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            }
        }
        JOptionPane.showMessageDialog(this, "Đã gọi hàm lưu SQL cho " + count + " nhân viên.\n(Kiểm tra console để xem chi tiết)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}
