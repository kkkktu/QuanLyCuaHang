package GUI;
 
import model.NhanVien;
import model.TaiKhoan;
import database.DBConnection;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
 
public class NhanVienPanel extends JPanel {
 
    private JTextField txtHoTen;
    private JTextField txtSoDienThoai;
    private JTextField txtDiaChi;
    private JTextField txtNgaySinh;
    private JTextField txtNgayVaoLam;
    private JTextField txtEmail;
    private JTextField txtCccd;
    private JComboBox<String> cboChucVu;
    private JTextField txtLuong;
    private JTextField txtSoCaNghi;
    private JTextField txtTimKiem;
    private JComboBox<String> cboChucVuLoc;
 
    private JTable tableNhanVien;
    private DefaultTableModel tableModel;
    private JLabel lblSoKetQua;
 
    private List<NhanVien> danhSachNhanVien;
    private String maNhanVienDaChon = null;
    private final boolean chiXem;
    private final boolean laKeToanNhanSu;
    private boolean dangTaiNhanVienNen = false;
    private boolean dangCapNhatLuongNen = false;
 
   
    private static final Color BG_PANEL = new Color(248, 250, 252);      
    private static final Color BG_PANEL_2 = new Color(241, 244, 248);    
    private static final Color CARD_DARK = new Color(255, 255, 255);     
    private static final Color CARD_SOFT = new Color(248, 250, 252);
    private static final Color INPUT_DARK = new Color(255, 255, 255);
    private static final Color TABLE_DARK = new Color(255, 255, 255);
    private static final Color TABLE_DARK_2 = new Color(246, 248, 252);
    private static final Color GOLD_LIGHT = new Color(138, 100, 30);
    private static final Color GOLD_DARK = new Color(18, 59, 140);
    private static final Color TEXT_LIGHT = new Color(11, 23, 54);       
    private static final Color TEXT_MUTED = new Color(71, 85, 105);
    private static final Color BORDER = new Color(216, 224, 236);
    private static final Color HEADER_BG = new Color(235, 240, 248);
    private static final Color SELECT_BG = new Color(18, 59, 140);
 
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static boolean laVaiTroKeToan(String vaiTro) {
        if (vaiTro == null) return false;
        String v = vaiTro.trim().toUpperCase();
        return v.equals("ACCOUNTANT") || v.equals("KẾ TOÁN") || v.equals("KE TOAN");
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(
                0, 0, BG_PANEL,
                getWidth(), getHeight(), BG_PANEL_2
        );
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
 
    public NhanVienPanel() {
        this(false, false);
    }

    public NhanVienPanel(boolean chiXem) {
        this(chiXem, false);
    }

    public NhanVienPanel(String vaiTro) {
        this(false, laVaiTroKeToan(vaiTro));
    }

    private NhanVienPanel(boolean chiXem, boolean laKeToanNhanSu) {
        this.chiXem = chiXem;
        this.laKeToanNhanSu = laKeToanNhanSu;
        danhSachNhanVien = new java.util.ArrayList<>();
 
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        setBackground(BG_PANEL);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
 
        taoGiaoDien();
        ganSuKien();
        capNhatBang();
        taiNhanVienTuSQLNen(false, true);
    }
 
    private void taoGiaoDien() {
        JPanel pnlTimKiem = new GradientCardPanel(new GridBagLayout());
        pnlTimKiem.setBackground(CARD_DARK);
        pnlTimKiem.setBorder(taoTitledBorder("Tìm kiếm nhân viên"));
        GridBagConstraints gbcTk = new GridBagConstraints();
        gbcTk.gridy = 0;
        gbcTk.insets = new Insets(6, 8, 6, 8);
        gbcTk.anchor = GridBagConstraints.WEST;
        gbcTk.fill = GridBagConstraints.HORIZONTAL;
 
        txtTimKiem = taoTextField(24);
 
        JButton btnTimKiem = taoNut("Tìm Kiếm", new Color(218, 174, 88));
        JButton btnHienTatCa = taoNut("Hiển Thị Tất Cả", new Color(120, 105, 85));
        cboChucVuLoc = new JComboBox<>(new String[]{"Tất cả", "Nhân viên bán hàng", "Nhân viên kho", "Kế toán"});
        cboChucVuLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cboChucVuLoc.setPreferredSize(new Dimension(180, 36));
 
        gbcTk.gridx = 0; gbcTk.weightx = 0; pnlTimKiem.add(taoLabel("Tìm kiếm Mã/Tên/SĐT/Email/CCCD/Chức vụ:"), gbcTk);
        gbcTk.gridx = 1; gbcTk.weightx = 1; pnlTimKiem.add(txtTimKiem, gbcTk);
        gbcTk.gridx = 2; gbcTk.weightx = 0; pnlTimKiem.add(btnTimKiem, gbcTk);
        gbcTk.gridx = 3; pnlTimKiem.add(btnHienTatCa, gbcTk);
        gbcTk.gridx = 4; pnlTimKiem.add(taoLabel("Sắp xếp theo:"), gbcTk);
        gbcTk.gridx = 5; pnlTimKiem.add(cboChucVuLoc, gbcTk);
 
        JPanel pnlInput = new GradientCardPanel(new GridLayout(6, 4, 10, 10));
        pnlInput.setBackground(CARD_DARK);
        pnlInput.setBorder(taoTitledBorder("Thông tin nhân viên"));
 
        pnlInput.add(taoLabel("Họ tên:"));
        txtHoTen = taoTextField();
        pnlInput.add(txtHoTen);
 
        pnlInput.add(taoLabel("Số điện thoại:"));
        txtSoDienThoai = taoTextField();
        pnlInput.add(txtSoDienThoai);
 
        pnlInput.add(taoLabel("Địa chỉ:"));
        txtDiaChi = taoTextField();
        pnlInput.add(txtDiaChi);
 
        pnlInput.add(taoLabel("Ngày sinh (dd/MM/yyyy):"));
        txtNgaySinh = taoTextField();
        pnlInput.add(txtNgaySinh);

        pnlInput.add(taoLabel("Thời gian vào làm (dd/MM/yyyy):"));
        txtNgayVaoLam = taoTextField();
        pnlInput.add(txtNgayVaoLam);
 
        pnlInput.add(taoLabel("Email:"));
        txtEmail = taoTextField();
        pnlInput.add(txtEmail);
 
        pnlInput.add(taoLabel("CCCD:"));
        txtCccd = taoTextField();
        pnlInput.add(txtCccd);
 
        pnlInput.add(taoLabel("Chức vụ:"));
        cboChucVu = taoComboBoxChucVu();
        pnlInput.add(cboChucVu);
 
        pnlInput.add(taoLabel("Lương/giờ:"));
        txtLuong = taoTextField();
        txtLuong.setEditable(false);
        txtLuong.setFocusable(false);
        txtLuong.setEnabled(true);
        txtLuong.setOpaque(true);
        txtLuong.setBackground(new Color(238, 244, 252));
        txtLuong.setForeground(TEXT_LIGHT);
        txtLuong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtLuong.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(213, 222, 236), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        txtLuong.setToolTipText("Lương/giờ được tính tự động theo chức vụ và thời gian vào làm.");
        pnlInput.add(txtLuong);

        pnlInput.add(taoLabel("Số ca nghỉ:"));
        txtSoCaNghi = taoTextField();
        txtSoCaNghi.setText("0");
        txtSoCaNghi.setToolTipText("Nhập số ca nghỉ trong kỳ 5 tuần, phải là số nguyên >= 0.");
        pnlInput.add(txtSoCaNghi);

        cboChucVu.addActionListener(e -> capNhatLuongGioTuForm());
        txtNgayVaoLam.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                capNhatLuongGioTuForm();
            }
        });
 
        pnlInput.add(new JLabel());
        pnlInput.add(new JLabel());
 
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlButtons.setOpaque(false);
 
        JButton btnThem = taoNut("Thêm Nhân Viên", new Color(70, 145, 210));
        JButton btnSua = taoNut("Sửa Thông Tin", new Color(218, 174, 88));
        JButton btnXoa = taoNut("Xóa Nhân Viên", new Color(210, 65, 55));
        JButton btnChiTietNgayLam = taoNut("Chi Tiết Ngày Làm", new Color(70, 150, 85));
        JButton btnXemNgayLam = taoNut("Xem Ngày Làm", new Color(18, 59, 140));
        JButton btnClear = taoNut("Làm Mới Form", new Color(120, 105, 85));
        JButton btnSuaCaNghi = taoNut("Sửa Ca Nghỉ", new Color(218, 174, 88));
 
        if (laKeToanNhanSu) {
            pnlButtons.add(btnSuaCaNghi);
            pnlButtons.add(btnXemNgayLam);
            pnlButtons.add(btnClear);
        } else if (!chiXem) {
            pnlButtons.add(btnThem);
            pnlButtons.add(btnSua);
            pnlButtons.add(btnXoa);
            pnlButtons.add(btnChiTietNgayLam);
            pnlButtons.add(btnXemNgayLam);
            pnlButtons.add(btnClear);
        }
 
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setOpaque(false);
        pnlTop.setBackground(BG_PANEL);
        pnlTop.add(pnlTimKiem, BorderLayout.NORTH);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        if (!chiXem || laKeToanNhanSu) {
            pnlTop.add(pnlButtons, BorderLayout.SOUTH);
        }
 
        if (laKeToanNhanSu) {
            datFormKeToanNhanSu();
        } else if (chiXem) {
            datFormChiXem();
        }
 
        add(pnlTop, BorderLayout.NORTH);
 
        String[] columnNames = {
                "Mã NV", "Họ Tên", "SĐT", "Địa Chỉ", "Ngày Sinh", "Vào Làm",
                "Email", "CCCD", "Chức Vụ", "Lương/giờ", "Số Ca Nghỉ", "Lương"
        };
 
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
 
        tableNhanVien = new JTable(tableModel);
        tableNhanVien.setRowHeight(28);
        tableNhanVien.setBackground(TABLE_DARK);
        tableNhanVien.setForeground(TEXT_LIGHT);
        tableNhanVien.setGridColor(BORDER);
        tableNhanVien.setSelectionBackground(SELECT_BG);
        tableNhanVien.setSelectionForeground(Color.WHITE);
        tableNhanVien.setShowVerticalLines(true);
        tableNhanVien.setShowHorizontalLines(true);
        tableNhanVien.getTableHeader().setBackground(HEADER_BG);
        tableNhanVien.getTableHeader().setForeground(TEXT_LIGHT);
        tableNhanVien.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableNhanVien.getTableHeader().setReorderingAllowed(false);
        canGiuaHeaderVaFixKhoangTrangBang(tableNhanVien);
        tableNhanVien.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    setBackground(SELECT_BG);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? TABLE_DARK : TABLE_DARK_2);
                    setForeground(TEXT_LIGHT);
                }
                setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8));
                return this;
            }
        });
        tableNhanVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableNhanVien.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        datDoRongCotNhanVien();
 
        JScrollPane scrollPane = taoScrollPane(tableNhanVien, "Danh sách nhân viên");
        lblSoKetQua = taoLabelSoKetQua();
        JPanel tableArea = new JPanel(new BorderLayout(0, 6));
        tableArea.setOpaque(false);
        tableArea.add(scrollPane, BorderLayout.CENTER);
        tableArea.add(lblSoKetQua, BorderLayout.SOUTH);
        add(tableArea, BorderLayout.CENTER);
 
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
 
        btnHienTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            if (cboChucVuLoc != null) cboChucVuLoc.setSelectedItem("Tất cả");
            taiNhanVienTuSQLNen(true, true);
        });
        if (cboChucVuLoc != null) cboChucVuLoc.addActionListener(e -> capNhatBang());
 
        if (laKeToanNhanSu) {
            btnSuaCaNghi.addActionListener(e -> xuLySuaCaNghi());
            btnXemNgayLam.addActionListener(e -> hienThiNhanVienLamTheoNgay());
            btnClear.addActionListener(e -> xoaRongForm());
        } else if (!chiXem) {
            btnThem.addActionListener(this::xuLyThemNhanVien);
            btnSua.addActionListener(this::xuLySuaNhanVien);
            btnXoa.addActionListener(this::xuLyXoaNhanVien);
            btnChiTietNgayLam.addActionListener(e -> hienThiChiTietNgayLam());
            btnXemNgayLam.addActionListener(e -> hienThiNhanVienLamTheoNgay());
            btnClear.addActionListener(e -> xoaRongForm());
        }
    }
 
    private void ganSuKien() {
        tableNhanVien.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableNhanVien.getSelectedRow();
 
                if (row >= 0) {
                    maNhanVienDaChon = tableModel.getValueAt(row, 0).toString();
 
                    for (NhanVien nv : danhSachNhanVien) {
                        if (nv.getMaDinhDanh().equals(maNhanVienDaChon)) {
                            txtHoTen.setText(nv.getHoTen() != null ? nv.getHoTen() : "");
                            txtSoDienThoai.setText(nv.getSoDienThoai() != null ? nv.getSoDienThoai() : "");
                            txtDiaChi.setText(nv.getDiaChi() != null ? nv.getDiaChi() : "");
                            txtNgaySinh.setText(nv.getNgaySinh() != null ? DATE_FMT.format(nv.getNgaySinh()) : "");
                            txtNgayVaoLam.setText(nv.getNgayVaoLam() != null ? DATE_FMT.format(nv.getNgayVaoLam()) : "");
                            txtEmail.setText(nv.getEmail() != null ? nv.getEmail() : "");
                            txtCccd.setText(nv.getCccd() != null ? nv.getCccd() : "");
                            chonChucVu(nv.getChucVu());
                            txtLuong.setText(String.format("%,.0f", NhanVien.tinhDonGiaTheoGio(nv.getChucVu(), nv.getNgayVaoLamText())));
                            txtSoCaNghi.setText(String.valueOf(nv.getSoCaNghi()));
                            break;
                        }
                    }
                }
            }
        });
    }
 
 

    private void capNhatLuongGioTuForm() {
        if (txtLuong == null || cboChucVu == null || txtNgayVaoLam == null) return;
        String ngayVaoLam = txtNgayVaoLam.getText() == null ? "" : txtNgayVaoLam.getText().trim();
        if (ngayVaoLam.isEmpty()) {
            txtLuong.setText("");
            return;
        }
        txtLuong.setText(String.format("%,.0f", NhanVien.tinhDonGiaTheoGio(layChucVuDangChon(), ngayVaoLam)));
    }

    private void datFormChiXem() {
        JTextField[] fields = {
                txtHoTen, txtSoDienThoai, txtDiaChi, txtNgaySinh, txtNgayVaoLam, txtEmail,
                txtCccd, txtLuong, txtSoCaNghi
        };
        for (JTextField field : fields) {
            if (field != null) {
                field.setEditable(false);
                field.setFocusable(false);
                field.setBackground(new Color(241, 244, 248));
                field.setForeground(TEXT_LIGHT);
            }
        }
        if (cboChucVu != null) {
            cboChucVu.setEnabled(false);
            cboChucVu.setBackground(new Color(241, 244, 248));
            cboChucVu.setForeground(TEXT_LIGHT);
        }
    }

    private void datFormKeToanNhanSu() {
        JTextField[] fields = {
                txtHoTen, txtSoDienThoai, txtDiaChi, txtNgaySinh, txtNgayVaoLam, txtEmail,
                txtCccd, txtLuong
        };
        for (JTextField field : fields) {
            if (field != null) {
                field.setEditable(false);
                field.setFocusable(false);
                field.setBackground(new Color(241, 244, 248));
                field.setForeground(TEXT_LIGHT);
            }
        }
        if (txtSoCaNghi != null) {
            txtSoCaNghi.setEditable(true);
            txtSoCaNghi.setFocusable(true);
            txtSoCaNghi.setBackground(Color.WHITE);
            txtSoCaNghi.setForeground(TEXT_LIGHT);
        }
        if (cboChucVu != null) {
            cboChucVu.setEnabled(false);
            cboChucVu.setBackground(new Color(241, 244, 248));
            cboChucVu.setForeground(TEXT_LIGHT);
        }
    }
 
    private JLabel taoLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_LIGHT);
        label.setOpaque(false);
        label.setBorder(new EmptyBorder(0, 16, 0, 8));
        return label;
    }
 
    private JTextField taoTextField() {
        return taoTextField(0, "");
    }
 
    private JTextField taoTextField(int columns) {
        return taoTextField(columns, "");
    }
 
    private JTextField taoTextField(String text) {
        return taoTextField(0, text);
    }
 
    private JTextField taoTextField(int columns, String text) {
        JTextField txt = columns > 0 ? new JTextField(columns) : new JTextField();
        txt.setText(text);
        txt.setHorizontalAlignment(SwingConstants.LEFT);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBackground(INPUT_DARK);
        txt.setForeground(TEXT_LIGHT);
        txt.setCaretColor(GOLD_DARK);
        txt.setOpaque(true);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_DARK, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return txt;
    }
 
    private JComboBox<String> taoComboBoxChucVu() {
        JComboBox<String> combo = new JComboBox<>(new String[]{
                "Nhân viên bán hàng",
                "Nhân viên kho",
                "Kế toán"
        });
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(INPUT_DARK);
        combo.setForeground(TEXT_LIGHT);
        combo.setOpaque(true);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_DARK, 1),
                new EmptyBorder(2, 4, 2, 4)
        ));
        return combo;
    }

    private String layChucVuDangChon() {
        Object selected = cboChucVu == null ? null : cboChucVu.getSelectedItem();
        return selected == null ? "" : selected.toString().trim();
    }

    private void chonChucVu(String chucVu) {
        if (cboChucVu == null) return;
        String cv = chucVu == null ? "" : chucVu.trim();
        for (int i = 0; i < cboChucVu.getItemCount(); i++) {
            if (cboChucVu.getItemAt(i).equalsIgnoreCase(cv)) {
                cboChucVu.setSelectedIndex(i);
                return;
            }
        }
        cboChucVu.setSelectedIndex(0);
    }

    private String layVaiTroTaiKhoanTheoChucVu(String chucVu) {
        String cv = chucVu == null ? "" : chucVu.trim().toLowerCase();
        if (cv.contains("kho")) return "WAREHOUSE";
        if (cv.contains("kế toán") || cv.contains("ke toan")) return "ACCOUNTANT";
        if (cv.contains("quản lý") || cv.contains("quan ly")) return TaiKhoan.VAI_TRO_ADMIN;
        return TaiKhoan.VAI_TRO_STAFF;
    }

    private TitledBorder taoTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                title,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                TEXT_LIGHT
        );
        return border;
    }
 
    private void canGiuaHeaderVaFixKhoangTrangBang(JTable table) {
        table.setFillsViewportHeight(true);
        table.setOpaque(true);
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(HEADER_BG);
                setForeground(TEXT_LIGHT);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, BORDER));
                return this;
            }
        });
    }
 
    private JScrollPane taoScrollPane(JTable table, String title) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(true);
        scrollPane.setBackground(BG_PANEL);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(TABLE_DARK);
        scrollPane.setBorder(taoTitledBorder(title));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setBackground(BG_PANEL_2);
        scrollPane.getHorizontalScrollBar().setBackground(BG_PANEL_2);
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 18));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(18, 0));
        return scrollPane;
    }
 

    private JLabel taoLabelSoKetQua() {
        JLabel label = new JLabel("Số kết quả tìm được: 0", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_LIGHT);
        label.setOpaque(true);
        label.setBackground(new Color(248, 250, 252));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return label;
    }

    private void capNhatSoKetQua() {
        if (lblSoKetQua != null && tableModel != null) {
            lblSoKetQua.setText("Số kết quả tìm được: " + tableModel.getRowCount());
        }
    }

    private void datDoRongCotNhanVien() {
        if (tableNhanVien == null || tableNhanVien.getColumnModel().getColumnCount() < 12) return;
        int[] widths = {120, 180, 115, 235, 110, 110, 200, 135, 145, 105, 105, 140};
        for (int i = 0; i < widths.length; i++) {
            tableNhanVien.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            tableNhanVien.getColumnModel().getColumn(i).setMinWidth(75);
        }
    }

    private JButton taoNut(String text, Color color) {
        final Color normalColor = color;
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                Color paintColor = normalColor;
                if (getModel().isPressed()) {
                    paintColor = normalColor.darker();
                } else if (getModel().isRollover()) {
                    paintColor = normalColor.brighter();
                }

                g2.setColor(paintColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                g2.setColor(new Color(255, 255, 255, 70));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setBackground(color);
        boolean isDark = color.getRed() < 200 || (color.getRed() < 230 && color.getGreen() < 150);
        btn.setForeground(isDark ? Color.WHITE : Color.BLACK);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setMargin(new Insets(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
 
        Icon icon = taoIconNut(text, btn.getForeground());
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(8);
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
            btn.setVerticalTextPosition(SwingConstants.CENTER);
        }
 
        return btn;
    }
 
    private Icon taoIconNut(String text, Color iconColor) {
        ButtonIconType type = xacDinhLoaiNut(text);
        if (type == ButtonIconType.NONE) {
            return null;
        }
        return new ButtonActionIcon(type, iconColor, 16);
    }
 
    private ButtonIconType xacDinhLoaiNut(String text) {
        if (text == null) {
            return ButtonIconType.NONE;
        }
 
        String lower = text.trim().toLowerCase();
 
        if (lower.contains("tìm") || lower.contains("kiem") || lower.contains("kiếm")) {
            return ButtonIconType.SEARCH;
        }
 
        if (lower.contains("hiển thị") || lower.contains("hien thi") || lower.contains("tất cả") || lower.contains("tat ca")) {
            return ButtonIconType.LIST;
        }
 
        if (lower.contains("thêm") || lower.contains("them")) {
            return ButtonIconType.ADD;
        }
 
        if (lower.contains("sửa") || lower.contains("sua")) {
            return ButtonIconType.EDIT;
        }
 
        if (lower.contains("xóa") || lower.contains("xoa")) {
            return ButtonIconType.DELETE;
        }

        if (lower.contains("chi tiết") || lower.contains("chi tiet") || lower.contains("ngày làm") || lower.contains("ngay lam")) {
            return ButtonIconType.LIST;
        }
 
        if (lower.contains("làm mới") || lower.contains("lam moi") || lower.contains("mới") || lower.contains("moi")) {
            return ButtonIconType.REFRESH;
        }
 
        return ButtonIconType.NONE;
    }
 
    private enum ButtonIconType {
        ADD, EDIT, DELETE, SEARCH, LIST, REFRESH, NONE
    }
 
    private static class ButtonActionIcon implements Icon {
        private final ButtonIconType type;
        private final Color color;
        private final int size;
 
        ButtonActionIcon(ButtonIconType type, Color color, int size) {
            this.type = type;
            this.color = color == null ? Color.BLACK : color;
            this.size = size;
        }
 
        @Override
        public int getIconWidth() {
            return size;
        }
 
        @Override
        public int getIconHeight() {
            return size;
        }
 
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.translate(x, y);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 
            switch (type) {
                case ADD:
                    drawAdd(g2);
                    break;
                case EDIT:
                    drawEdit(g2);
                    break;
                case DELETE:
                    drawDelete(g2);
                    break;
                case SEARCH:
                    drawSearch(g2);
                    break;
                case LIST:
                    drawList(g2);
                    break;
                case REFRESH:
                    drawRefresh(g2);
                    break;
                default:
                    break;
            }
 
            g2.dispose();
        }
 
        private void drawAdd(Graphics2D g2) {
            int mid = size / 2;
            g2.drawLine(mid, 3, mid, size - 3);
            g2.drawLine(3, mid, size - 3, mid);
        }
 
        private void drawEdit(Graphics2D g2) {
            g2.drawLine(4, size - 4, size - 5, 5);
            g2.drawLine(size - 7, 3, size - 3, 7);
            g2.drawLine(4, size - 4, 3, size - 1);
            g2.drawLine(4, size - 4, 7, size - 3);
        }
 
        private void drawDelete(Graphics2D g2) {
            g2.drawOval(2, 2, size - 4, size - 4);
            g2.drawLine(5, 5, size - 5, size - 5);
            g2.drawLine(size - 5, 5, 5, size - 5);
        }
 
        private void drawSearch(Graphics2D g2) {
            int d = size - 7;
            g2.drawOval(2, 2, d, d);
            g2.drawLine(size - 6, size - 6, size - 2, size - 2);
        }
 
        private void drawList(Graphics2D g2) {
            for (int i = 0; i < 3; i++) {
                int yy = 4 + i * 5;
                g2.fillOval(2, yy - 1, 3, 3);
                g2.drawLine(7, yy, size - 2, yy);
            }
        }
 
        private void drawRefresh(Graphics2D g2) {
            g2.drawArc(3, 3, size - 6, size - 6, 35, 260);
            Polygon arrow = new Polygon();
            arrow.addPoint(size - 4, 5);
            arrow.addPoint(size - 2, 10);
            arrow.addPoint(size - 8, 8);
            g2.fillPolygon(arrow);
        }
    }
 
    public void lamMoiDuLieuTuSQL() {
        taiNhanVienTuSQLNen(false, true);
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
 
        if (keyword.isEmpty()) {
            AppDialog.showError(this, "Lỗi nhập liệu", "Vui lòng nhập từ khóa tìm kiếm");
            return;
        } else {
            danhSachNhanVien = NhanVien.timKiemSQL(keyword);
        }
 
        capNhatBang();
    }
 
    private void xuLySuaCaNghi() {
        try {
            NhanVien nv = layNhanVienDangChon();
            if (nv == null) {
                AppDialog.showWarning(this, "Thông báo", "Vui lòng chọn nhân viên cần sửa số ca nghỉ.");
                return;
            }

            int soCaNghi = docSoCaNghiTuForm();
            String sql = "UPDATE NhanVien SET soCaNghi = ? WHERE maDinhDanh = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, soCaNghi);
                ps.setString(2, nv.getMaDinhDanh());
                ps.executeUpdate();
            }

            capNhatLuongNhanVienTheoLich(nv.getMaDinhDanh(), nv.getChucVu(), nv.getNgayVaoLamText());
            danhSachNhanVien = NhanVien.docTuSQL();
            capNhatBang();
            AppDialog.showSuccess(this, "Thông báo", "Đã cập nhật số ca nghỉ và lương nhân viên.");
        } catch (NumberFormatException ex) {
            AppDialog.showError(this, "Lỗi", "Số ca nghỉ phải là số nguyên >= 0.");
        } catch (Exception ex) {
            AppDialog.showError(this, "Lỗi", "Không thể cập nhật số ca nghỉ: " + ex.getMessage());
        }
    }

    private int docSoCaNghiTuForm() {
        String text = txtSoCaNghi == null ? "" : txtSoCaNghi.getText().trim();
        if (text.isEmpty()) return 0;
        int value = Integer.parseInt(text);
        if (value < 0) throw new NumberFormatException("Số ca nghỉ âm");
        return value;
    }

    private void xuLyThemNhanVien(ActionEvent e) {
        try {
            kiemTraNhapDayDuNhanVien();

            NhanVien nv = taoNhanVienTuForm(null);

            kiemTraTrungNhanVienTruocKhiLuu(nv, null);

            nv.luuVaoSQL();
            taoHoacCapNhatTaiKhoanChoNhanVien(nv);

            danhSachNhanVien = NhanVien.docTuSQL();
            capNhatBang();
            xoaRongForm();

            AppDialog.showSuccess(
                    this,
                    "Thông báo",
                    "Thêm nhân viên thành công!\nTài khoản: " + nv.getSoDienThoai() + "\nMật khẩu: " + nv.getCccd()
            );

        } catch (NumberFormatException ex) {
            AppDialog.showError(this, "Lỗi nhập liệu", "Lương phải là số!");

        } catch (IllegalArgumentException ex) {
            AppDialog.showError(this, "Lỗi nhập liệu", ex.getMessage());

        } catch (Exception ex) {
            AppDialog.showError(this, "Lỗi", "Lỗi thêm nhân viên: " + layThongBaoLoiSQLNhanVien(ex));
            ex.printStackTrace();
        }
    }

    private void xuLySuaNhanVien(ActionEvent e) {
        if (maNhanVienDaChon == null) {
            AppDialog.showWarning(this, "Thông báo", "Vui lòng chọn nhân viên cần sửa!");
            return;
        }

        try {
            NhanVien nvCu = null;

            for (NhanVien item : danhSachNhanVien) {
                if (item.getMaDinhDanh().equals(maNhanVienDaChon)) {
                    nvCu = item;
                    break;
                }
            }

            if (nvCu == null) {
                AppDialog.showError(this, "Lỗi", "Không tìm thấy nhân viên đã chọn!");
                return;
            }

            kiemTraNhapDayDuNhanVien();

            String soDienThoaiCu = nvCu.getSoDienThoai();
            NhanVien nvMoi = taoNhanVienTuForm(nvCu.getMaDinhDanh());
            kiemTraTrungNhanVienTruocKhiLuu(nvMoi, nvMoi.getMaDinhDanh());

            nvMoi.capNhatSQL();
            capNhatLuongNhanVienTheoLich(nvMoi.getMaDinhDanh(), nvMoi.getChucVu(), nvMoi.getNgayVaoLamText());
            capNhatTaiKhoanNhanVien(soDienThoaiCu, nvMoi);

            danhSachNhanVien = NhanVien.docTuSQL();
            capNhatBang();
            xoaRongForm();

            AppDialog.showSuccess(this, "Thông báo", "Sửa thông tin nhân viên thành công!");

        } catch (NumberFormatException ex) {
            AppDialog.showError(this, "Lỗi nhập liệu", "Lương phải là số!");

        } catch (IllegalArgumentException ex) {
            AppDialog.showError(this, "Lỗi nhập liệu", ex.getMessage());

        } catch (Exception ex) {
            AppDialog.showError(this, "Lỗi", "Lỗi sửa nhân viên: " + layThongBaoLoiSQLNhanVien(ex));
            ex.printStackTrace();
        }
    }

    private void xuLyXoaNhanVien(ActionEvent e) {
        if (maNhanVienDaChon == null) {
            AppDialog.showWarning(this, "Thông báo", "Vui lòng chọn nhân viên cần xóa!");
            return;
        }
 
        boolean xacNhan = AppDialog.showConfirm(
                this,
                "Xác nhận xóa",
                "Bạn có chắc chắn muốn cho nhân viên " + maNhanVienDaChon + " nghỉ việc?"
                        + "\nNhân viên sẽ biến mất khỏi danh sách hiện tại và tài khoản đăng nhập sẽ bị khóa."
        );
 
        if (xacNhan) {
            try {
                String soDienThoaiCanKhoa = laySoDienThoaiNhanVienDangChon();
 
                NhanVien.xoaKhoiSQL(maNhanVienDaChon);
                khoaTaiKhoanNhanVien(soDienThoaiCanKhoa);
 
                danhSachNhanVien = NhanVien.docTuSQL();
                capNhatBang();
                xoaRongForm();
 
                AppDialog.showSuccess(this, "Thông báo", "Đã cho nhân viên nghỉ việc và khóa tài khoản đăng nhập!");
 
            } catch (Exception ex) {
                AppDialog.showError(this, "Lỗi", "Không thể cho nhân viên nghỉ việc: " + ex.getMessage());
            }
        }
    }
 
 
    private void taoTaiKhoanChoNhanVien(NhanVien nv) {
        taoHoacCapNhatTaiKhoanChoNhanVien(nv);
    }

    private void taoHoacCapNhatTaiKhoanChoNhanVien(NhanVien nv) {
        String sqlUpdate = """
                UPDATE TaiKhoan
                SET matKhau = ?, hoTen = ?, email = ?, vaiTro = ?, trangThai = 1
                WHERE tenDangNhap = ?
                """;

        String sqlInsert = """
                INSERT INTO TaiKhoan
                (maTaiKhoan, tenDangNhap, matKhau, vaiTro, hoTen, email, trangThai, ngayTao, ngayDangNhapCuoi)
                VALUES (?, ?, ?, ?, ?, ?, 1, ?, NULL)
                """;

        String tenDangNhap = nv.getSoDienThoai().trim().toLowerCase();
        String matKhauHash = TaiKhoan.hashSHA256(nv.getCccd());

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                int rows;
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setString(1, matKhauHash);
                    ps.setString(2, nv.getHoTen());
                    ps.setString(3, nv.getEmail() == null ? "" : nv.getEmail());
                    ps.setString(4, layVaiTroTaiKhoanTheoChucVu(nv.getChucVu()));
                    ps.setString(5, tenDangNhap);
                    rows = ps.executeUpdate();
                }

                if (rows == 0) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                        ps.setString(1, taoMaTaiKhoanMoi(conn));
                        ps.setString(2, tenDangNhap);
                        ps.setString(3, matKhauHash);
                        ps.setString(4, layVaiTroTaiKhoanTheoChucVu(nv.getChucVu()));
                        ps.setString(5, nv.getHoTen());
                        ps.setString(6, nv.getEmail() == null ? "" : nv.getEmail());
                        ps.setString(7, java.time.LocalDateTime.now().format(
                                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                        ));
                        ps.executeUpdate();
                    }
                }

                conn.commit();

            } catch (Exception ex) {
                try {
                    conn.rollback();
                } catch (Exception ignored) {
                }
                throw ex;
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (Exception ignored) {
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo/cập nhật tài khoản nhân viên: " + layThongBaoLoiSQLNhanVien(ex), ex);
        }
    }

    private String taoMaTaiKhoanMoi(Connection conn) throws Exception {
        String sql = "SELECT ISNULL(MAX(TRY_CONVERT(INT, SUBSTRING(maTaiKhoan, 4, 20))), 0) + 1 FROM TaiKhoan WHERE maTaiKhoan LIKE 'TK-%'";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int next = 1;
            if (rs.next()) {
                next = Math.max(1, rs.getInt(1));
            }

            String ma;
            do {
                ma = String.format("TK-%08d", next++);
            } while (maTaiKhoanDaTonTai(conn, ma));

            return ma;
        }
    }

    private boolean maTaiKhoanDaTonTai(Connection conn, String maTaiKhoan) throws Exception {
        String sql = "SELECT COUNT(*) FROM TaiKhoan WHERE maTaiKhoan = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTaiKhoan);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void capNhatTaiKhoanNhanVien(String soDienThoaiCu, NhanVien nv) {
        if (soDienThoaiCu != null
                && !soDienThoaiCu.trim().isEmpty()
                && !soDienThoaiCu.trim().equalsIgnoreCase(nv.getSoDienThoai().trim())) {
            khoaTaiKhoanNhanVien(soDienThoaiCu);
        }

        taoHoacCapNhatTaiKhoanChoNhanVien(nv);
    }

    private String laySoDienThoaiNhanVienDangChon() {
        if (maNhanVienDaChon == null) return "";
 
        for (NhanVien nv : danhSachNhanVien) {
            if (nv.getMaDinhDanh().equals(maNhanVienDaChon)) {
                return nv.getSoDienThoai();
            }
        }
        return "";
    }
 
    private void khoaTaiKhoanNhanVien(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) return;
 
        String sql = "UPDATE TaiKhoan SET trangThai=0 WHERE tenDangNhap=?";
 
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, soDienThoai.trim().toLowerCase());
            ps.executeUpdate();
 
        } catch (Exception ex) {
            AppDialog.showWarning(
                    this,
                    "Cảnh báo tài khoản",
                    "Nhân viên đã được xóa nhưng tài khoản đăng nhập chưa khóa được. Lý do: " + ex.getMessage()
            );
        }
    }
 
    private double docLuong() {
        return 0;
    }

    private void xoaRongForm() {
        maNhanVienDaChon = null;
        tableNhanVien.clearSelection();
 
        txtHoTen.setText("");
        txtSoDienThoai.setText("");
        txtDiaChi.setText("");
        txtNgaySinh.setText("");
        txtNgayVaoLam.setText("");
        txtEmail.setText("");
        txtCccd.setText("");
        if (cboChucVu != null) cboChucVu.setSelectedIndex(0);
        txtLuong.setText("");
        if (txtSoCaNghi != null) txtSoCaNghi.setText("0");
 
        txtHoTen.requestFocus();
    }
 
 
    private void kiemTraNhapDayDuNhanVien() {
        if (isBlank(txtHoTen) || isBlank(txtSoDienThoai) || isBlank(txtDiaChi)
                || isBlank(txtNgaySinh) || isBlank(txtNgayVaoLam) || isBlank(txtEmail) || isBlank(txtCccd)
                || isBlank(txtSoCaNghi) || layChucVuDangChon().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin nhân viên.");
        }
        laySoCaNghiTuForm();
    }

    private int laySoCaNghiTuForm() {
        String text = txtSoCaNghi == null || txtSoCaNghi.getText() == null ? "0" : txtSoCaNghi.getText().trim();
        if (text.isEmpty()) text = "0";
        try {
            int value = Integer.parseInt(text);
            if (value < 0) throw new NumberFormatException();
            return value;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Số ca nghỉ phải là số nguyên >= 0.");
        }
    }

    private NhanVien taoNhanVienTuForm(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            NhanVien nv = new NhanVien();
            nv.setHoTen(txtHoTen.getText());
            nv.setSoDienThoai(txtSoDienThoai.getText());
            nv.setDiaChi(txtDiaChi.getText());
            nv.setNgaySinh(txtNgaySinh.getText());
            nv.setNgayVaoLam(txtNgayVaoLam.getText());
            nv.setEmail(txtEmail.getText());
            nv.setCccd(txtCccd.getText());
            nv.setChucVu(layChucVuDangChon());
            nv.setLuong(0);
            nv.setSoCaNghi(laySoCaNghiTuForm());
            return nv;
        }

        NhanVien nv = new NhanVien(
                maNhanVien.trim(),
                txtHoTen.getText(),
                txtSoDienThoai.getText(),
                txtDiaChi.getText(),
                txtNgaySinh.getText(),
                txtNgayVaoLam.getText(),
                txtEmail.getText(),
                txtCccd.getText(),
                layChucVuDangChon(),
                0
        );
        nv.setSoCaNghi(laySoCaNghiTuForm());
        return nv;
    }

    private void kiemTraTrungNhanVienTruocKhiLuu(NhanVien nv, String maNhanVienBoQua) throws Exception {
        if (nhanVienDaTonTaiTheoCot("soDienThoai", nv.getSoDienThoai(), maNhanVienBoQua)) {
            throw new IllegalArgumentException("Số điện thoại " + nv.getSoDienThoai() + " đã tồn tại trong hệ thống.");
        }

        if (nhanVienDaTonTaiTheoCot("email", nv.getEmail(), maNhanVienBoQua)) {
            throw new IllegalArgumentException("Email " + nv.getEmail() + " đã tồn tại trong hệ thống.");
        }

        if (nhanVienDaTonTaiTheoCot("cccd", nv.getCccd(), maNhanVienBoQua)) {
            throw new IllegalArgumentException("CCCD " + nv.getCccd() + " đã tồn tại trong hệ thống.");
        }
    }

    private boolean nhanVienDaTonTaiTheoCot(String tenCot, String giaTri, String maNhanVienBoQua) throws Exception {
        if (giaTri == null || giaTri.trim().isEmpty()) return false;

        if (!tenCot.equals("soDienThoai") && !tenCot.equals("email") && !tenCot.equals("cccd")) {
            throw new IllegalArgumentException("Cột kiểm tra trùng không hợp lệ.");
        }

        String sql = "SELECT COUNT(*) FROM NhanVien WHERE " + tenCot + " = ? AND (? = '' OR maDinhDanh <> ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String maBoQua = maNhanVienBoQua == null ? "" : maNhanVienBoQua.trim();
            ps.setString(1, giaTri.trim());
            ps.setString(2, maBoQua);
            ps.setString(3, maBoQua);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private String layThongBaoLoiSQLNhanVien(Exception ex) {
        String msg = ex == null ? "" : ex.getMessage();
        Throwable cause = ex == null ? null : ex.getCause();

        while ((msg == null || msg.isBlank()) && cause != null) {
            msg = cause.getMessage();
            cause = cause.getCause();
        }

        if (msg == null) msg = "";

        String lower = msg.toLowerCase();

        if (lower.contains("duplicate") || lower.contains("unique")) {
            if (lower.contains("sodienthoai") || lower.contains("số điện thoại")) {
                return "Số điện thoại đã tồn tại trong hệ thống.";
            }
            if (lower.contains("email")) {
                return "Email đã tồn tại trong hệ thống.";
            }
            if (lower.contains("cccd")) {
                return "CCCD đã tồn tại trong hệ thống.";
            }
            if (lower.contains("matkhau") || lower.contains("tendangnhap") || lower.contains("taikhoan")) {
                return "Tài khoản đăng nhập đã tồn tại. Hệ thống sẽ cập nhật lại tài khoản theo SĐT/CCCD.";
            }
            return "Dữ liệu nhân viên bị trùng. Vui lòng kiểm tra SĐT, email hoặc CCCD.";
        }

        return msg.isBlank() ? "Không xác định được lỗi." : msg;
    }

    private boolean isBlank(JTextField field) {
        return field == null || field.getText() == null || field.getText().trim().isEmpty();
    }
 

    private void hienThiNhanVienLamTheoNgay() {
        NhanVien nhanVienDangChon = layNhanVienDangChonTuBang();
        boolean xemTheoNhanVien = nhanVienDangChon != null;

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Xem ngày làm nhân viên", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(980, 620);
        dialog.setLocationRelativeTo(this);

        JPanel root = new GradientCardPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel(xemTheoNhanVien
                ? "XEM NGÀY LÀM: " + nhanVienDangChon.getHoTen()
                : "XEM NHÂN VIÊN LÀM THEO NGÀY", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_LIGHT);

        JPanel search = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        search.setOpaque(false);

        JTextField txtNgay = null;
        JComboBox<String> cboLocChucVu = null;

        if (xemTheoNhanVien) {
            JLabel lblNhanVien = taoLabel("Nhân viên đã chọn: " + nhanVienDangChon.getMaDinhDanh()
                    + " - " + nhanVienDangChon.getHoTen());
            search.add(lblNhanVien);
        } else {
            JLabel lblNgay = taoLabel("Nhập ngày (dd/MM/yyyy):");
            txtNgay = taoTextField(12);
            txtNgay.setPreferredSize(new Dimension(160, 36));

            JLabel lblSapXep = taoLabel("Sắp xếp theo:");
            cboLocChucVu = new JComboBox<>(new String[]{
                    "Tất cả", "Nhân viên bán hàng", "Kế toán", "Nhân viên kho"
            });
            cboLocChucVu.setPreferredSize(new Dimension(175, 36));
            cboLocChucVu.setFont(new Font("Segoe UI", Font.BOLD, 13));
            cboLocChucVu.setBackground(Color.WHITE);
            cboLocChucVu.setForeground(TEXT_LIGHT);

            search.add(lblNgay);
            search.add(txtNgay);
            search.add(lblSapXep);
            search.add(cboLocChucVu);
        }

        JButton btnXem = taoNut("Xem Kết Quả", new Color(218, 174, 88));
        JButton btnDong = taoNut("Đóng", new Color(120, 105, 85));
        search.add(btnXem);
        search.add(btnDong);

        DefaultTableModel modelNgay = new DefaultTableModel(new String[]{
                "Mã NV", "Họ tên", "SĐT", "Chức vụ", "Thứ", "Ca làm", "Số giờ", "Tiền/ngày"
        }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tableNgay = new JTable(modelNgay);
        tableNgay.setRowHeight(30);
        tableNgay.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableNgay.setBackground(TABLE_DARK);
        tableNgay.setForeground(TEXT_LIGHT);
        tableNgay.setGridColor(BORDER);
        tableNgay.setSelectionBackground(SELECT_BG);
        tableNgay.setSelectionForeground(Color.WHITE);
        tableNgay.setShowGrid(true);
        tableNgay.getTableHeader().setBackground(HEADER_BG);
        tableNgay.getTableHeader().setForeground(TEXT_LIGHT);
        tableNgay.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableNgay.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (selected) {
                    setBackground(SELECT_BG);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? TABLE_DARK : TABLE_DARK_2);
                    setForeground(TEXT_LIGHT);
                }
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        int[] widths = {105, 185, 115, 150, 85, 270, 85, 125};
        for (int i = 0; i < widths.length; i++) {
            tableNgay.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            tableNgay.getColumnModel().getColumn(i).setMinWidth(i == 5 ? 180 : 75);
        }

        JScrollPane scroll = taoScrollPane(tableNgay, "Danh sách nhân viên làm trong ngày");
        JLabel lblCount = taoLabelSoKetQua();

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(scroll, BorderLayout.CENTER);
        center.add(lblCount, BorderLayout.SOUTH);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(search, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        final JTextField txtNgayFinal = txtNgay;
        final JComboBox<String> cboLocChucVuFinal = cboLocChucVu;

        btnXem.addActionListener(e -> {
            if (xemTheoNhanVien) {
                napBangNgayLamCuaNhanVien(nhanVienDangChon, modelNgay, lblCount);
            } else {
                String loc = cboLocChucVuFinal == null || cboLocChucVuFinal.getSelectedItem() == null
                        ? "Tất cả"
                        : cboLocChucVuFinal.getSelectedItem().toString();
                napBangNhanVienLamTheoNgay(txtNgayFinal == null ? "" : txtNgayFinal.getText(), loc, modelNgay, lblCount);
            }
        });
        btnDong.addActionListener(e -> dialog.dispose());

        if (xemTheoNhanVien) {
            napBangNgayLamCuaNhanVien(nhanVienDangChon, modelNgay, lblCount);
        }

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private NhanVien layNhanVienDangChonTuBang() {
        if (tableNhanVien == null || tableModel == null) return null;
        int viewRow = tableNhanVien.getSelectedRow();
        if (viewRow < 0) return null;

        int modelRow = tableNhanVien.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= tableModel.getRowCount()) return null;

        Object value = tableModel.getValueAt(modelRow, 0);
        if (value == null) return null;

        String maNV = value.toString().trim();
        for (NhanVien nv : danhSachNhanVien) {
            if (nv != null && maNV.equalsIgnoreCase(nv.getMaDinhDanh())) {
                return nv;
            }
        }
        return null;
    }

    private String layThuTiengViet(LocalDate ngay) {
        return switch (ngay.getDayOfWeek()) {
            case MONDAY -> "Thứ 2";
            case TUESDAY -> "Thứ 3";
            case WEDNESDAY -> "Thứ 4";
            case THURSDAY -> "Thứ 5";
            case FRIDAY -> "Thứ 6";
            case SATURDAY -> "Thứ 7";
            case SUNDAY -> "Chủ nhật";
        };
    }

    private void napBangNhanVienLamTheoNgay(String ngayText, DefaultTableModel modelNgay, JLabel lblCount) {
        napBangNhanVienLamTheoNgay(ngayText, "Tất cả", modelNgay, lblCount);
    }

    private void napBangNhanVienLamTheoNgay(String ngayText, String locChucVu, DefaultTableModel modelNgay, JLabel lblCount) {
        String text = ngayText == null ? "" : ngayText.trim();
        if (text.isEmpty()) {
            AppDialog.showWarning(this, "Lỗi nhập liệu", "Vui lòng nhập ngày cần xem theo định dạng dd/MM/yyyy.");
            return;
        }

        LocalDate ngay;
        try {
            ngay = LocalDate.parse(text, DateTimeFormatter.ofPattern("d/M/yyyy"));
        } catch (Exception ex) {
            AppDialog.showError(this, "Sai định dạng", "Ngày phải đúng định dạng dd/MM/yyyy. Ví dụ: 10/06/2026.");
            return;
        }

        String thu = layThuTiengViet(ngay);
        String loc = locChucVu == null || locChucVu.isBlank() ? "Tất cả" : locChucVu.trim();
        modelNgay.setRowCount(0);
        String sql = """
                SELECT nv.maDinhDanh, nv.hoTen, nv.soDienThoai, nv.chucVu,
                       ll.ca1, ll.ca2, ll.ca3, ll.soGio, ll.thanhTien
                FROM LichLamNhanVien ll
                JOIN NhanVien nv ON nv.maDinhDanh = ll.maNhanVien
                WHERE ll.thuLam = ?
                  AND nv.trangThai = N'Đang làm'
                  AND (ll.ca1 = 1 OR ll.ca2 = 1 OR ll.ca3 = 1)
                ORDER BY nv.maDinhDanh
                """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, thu);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String chucVu = rs.getString("chucVu");
                    if (!phuHopLocXemNgayLam(chucVu, loc)) continue;

                    modelNgay.addRow(new Object[]{
                            rs.getString("maDinhDanh"),
                            rs.getString("hoTen"),
                            rs.getString("soDienThoai"),
                            chucVu,
                            thu,
                            taoChuoiCaLam(rs.getBoolean("ca1"), rs.getBoolean("ca2"), rs.getBoolean("ca3")),
                            String.format("%.1f", rs.getDouble("soGio")),
                            String.format("%,.0f", rs.getDouble("thanhTien"))
                    });
                }
            }
            lblCount.setText("Số kết quả tìm được: " + modelNgay.getRowCount());
        } catch (Exception ex) {
            AppDialog.showError(this, "Lỗi", "Không đọc được lịch làm trong ngày: " + ex.getMessage());
        }
    }

    private void napBangNgayLamCuaNhanVien(NhanVien nv, DefaultTableModel modelNgay, JLabel lblCount) {
        modelNgay.setRowCount(0);
        if (nv == null) {
            lblCount.setText("Số kết quả tìm được: 0");
            return;
        }

        String sql = """
                SELECT thuLam, ca1, ca2, ca3, soGio, thanhTien
                FROM LichLamNhanVien
                WHERE maNhanVien = ?
                  AND (ca1 = 1 OR ca2 = 1 OR ca3 = 1)
                ORDER BY CASE thuLam
                    WHEN N'Thứ 2' THEN 1
                    WHEN N'Thứ 3' THEN 2
                    WHEN N'Thứ 4' THEN 3
                    WHEN N'Thứ 5' THEN 4
                    WHEN N'Thứ 6' THEN 5
                    WHEN N'Thứ 7' THEN 6
                    WHEN N'Chủ nhật' THEN 7
                    ELSE 8 END
                """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getMaDinhDanh());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelNgay.addRow(new Object[]{
                            nv.getMaDinhDanh(),
                            nv.getHoTen(),
                            nv.getSoDienThoai(),
                            nv.getChucVu(),
                            rs.getString("thuLam"),
                            taoChuoiCaLam(rs.getBoolean("ca1"), rs.getBoolean("ca2"), rs.getBoolean("ca3")),
                            String.format("%.1f", rs.getDouble("soGio")),
                            String.format("%,.0f", rs.getDouble("thanhTien"))
                    });
                }
            }
            lblCount.setText("Số kết quả tìm được: " + modelNgay.getRowCount());
        } catch (Exception ex) {
            AppDialog.showError(this, "Lỗi", "Không đọc được ngày làm của nhân viên: " + ex.getMessage());
        }
    }

    private String taoChuoiCaLam(boolean ca1, boolean ca2, boolean ca3) {
        StringBuilder ca = new StringBuilder();
        if (ca1) ca.append("Ca 1 (07:00-12:00)");
        if (ca2) {
            if (ca.length() > 0) ca.append(", ");
            ca.append("Ca 2 (12:00-17:00)");
        }
        if (ca3) {
            if (ca.length() > 0) ca.append(", ");
            ca.append("Ca 3 (17:00-22:00)");
        }
        return ca.toString();
    }

    private boolean phuHopLocXemNgayLam(String chucVu, String locChucVu) {
        String loc = locChucVu == null ? "Tất cả" : locChucVu.trim().toLowerCase();
        if (loc.isEmpty() || loc.equals("tất cả")) return true;

        String cv = chucVu == null ? "" : chucVu.trim().toLowerCase();
        if (loc.equals("nhân viên bán hàng")) return cv.contains("bán hàng") || cv.contains("ban hang");
        if (loc.equals("kế toán")) return cv.contains("kế toán") || cv.contains("ke toan");
        if (loc.equals("nhân viên kho")) return cv.contains("kho");
        return true;
    }

    private static final String[] THU_TRONG_TUAN = {
            "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"
    };

    private static final class LichLamChiTiet {
        boolean ca1;
        boolean ca2;
        boolean ca3;
    }

    private Map<String, LichLamChiTiet> docLichLamNhanVienChiTiet(String maNhanVien) {
        Map<String, LichLamChiTiet> result = new LinkedHashMap<>();
        for (String thu : THU_TRONG_TUAN) result.put(thu, new LichLamChiTiet());
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) return result;

        String sqlMoi = "SELECT thuLam, ca1, ca2, ca3 FROM LichLamNhanVien WHERE maNhanVien = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlMoi)) {
            ps.setString(1, maNhanVien.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String thu = rs.getString("thuLam");
                    if (thu != null && result.containsKey(thu.trim())) {
                        LichLamChiTiet item = result.get(thu.trim());
                        item.ca1 = rs.getBoolean("ca1");
                        item.ca2 = rs.getBoolean("ca2");
                        item.ca3 = rs.getBoolean("ca3");
                    }
                }
            }
            return result;
        } catch (Exception ignored) {
            
        }
        return result;
    }

    private void taoLichLamMacDinhNeuCan(String maNhanVien, String chucVu, String ngayVaoLam) throws Exception {
        String sqlCount = "SELECT COUNT(*) FROM LichLamNhanVien WHERE maNhanVien = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlCount)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) return;
            }
        }
        for (String thu : THU_TRONG_TUAN) {
            capNhatLichLamChoThu(maNhanVien, thu, false, false, false, chucVu, ngayVaoLam);
        }
    }

    private void capNhatLichLamChoThu(String maNhanVien, String thuLam,
                                      boolean ca1, boolean ca2, boolean ca3,
                                      String chucVu, String ngayVaoLam) throws Exception {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) return;
        if (thuLam == null || thuLam.trim().isEmpty()) thuLam = "Thứ 2";

        double donGia = NhanVien.tinhDonGiaTheoGio(chucVu, ngayVaoLam);
        double soGio = (ca1 ? 5 : 0) + (ca2 ? 5 : 0) + (ca3 ? 5 : 0);
        double thanhTien = soGio * donGia;

        String sql = """
                MERGE LichLamNhanVien AS target
                USING (SELECT ? AS maNhanVien, ? AS thuLam) AS src
                ON target.maNhanVien = src.maNhanVien AND target.thuLam = src.thuLam
                WHEN MATCHED THEN
                    UPDATE SET ca1 = ?, ca2 = ?, ca3 = ?, soGio = ?, donGiaTheoGio = ?, thanhTien = ?
                WHEN NOT MATCHED THEN
                    INSERT (maNhanVien, thuLam, ca1, ca2, ca3, soGio, donGiaTheoGio, thanhTien)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            ps.setString(2, thuLam.trim());
            ps.setBoolean(3, ca1);
            ps.setBoolean(4, ca2);
            ps.setBoolean(5, ca3);
            ps.setDouble(6, soGio);
            ps.setDouble(7, donGia);
            ps.setDouble(8, thanhTien);
            ps.setString(9, maNhanVien);
            ps.setString(10, thuLam.trim());
            ps.setBoolean(11, ca1);
            ps.setBoolean(12, ca2);
            ps.setBoolean(13, ca3);
            ps.setDouble(14, soGio);
            ps.setDouble(15, donGia);
            ps.setDouble(16, thanhTien);
            ps.executeUpdate();
        }
    }

    private void taiNhanVienTuSQLNen(boolean xoaFormSauKhiTai, boolean capNhatLuongSauKhiTai) {
        if (dangTaiNhanVienNen) return;
        dangTaiNhanVienNen = true;

        new SwingWorker<List<NhanVien>, Void>() {
            @Override
            protected List<NhanVien> doInBackground() {
                return NhanVien.docTuSQL();
            }

            @Override
            protected void done() {
                dangTaiNhanVienNen = false;
                try {
                    danhSachNhanVien = get();
                    capNhatBang();
                    if (xoaFormSauKhiTai) xoaRongForm();
                    if (capNhatLuongSauKhiTai) capNhatLuongTatCaNhanVienNen();
                } catch (Exception ex) {
                    AppDialog.showError(NhanVienPanel.this, "Lỗi", "Không thể tải dữ liệu nhân viên: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void capNhatLuongTatCaNhanVienNen() {
        if (dangCapNhatLuongNen) return;
        dangCapNhatLuongNen = true;

        new SwingWorker<List<NhanVien>, Void>() {
            @Override
            protected List<NhanVien> doInBackground() {
                capNhatLuongTatCaNhanVienTheoLich();
                return NhanVien.docTuSQL();
            }

            @Override
            protected void done() {
                dangCapNhatLuongNen = false;
                try {
                    danhSachNhanVien = get();
                    capNhatBang();
                } catch (Exception ignored) {
                    
                }
            }
        }.execute();
    }

    private void capNhatLuongNhanVienTheoLich(String maNhanVien, String chucVu, String ngayVaoLam) throws Exception {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) return;

        Map<String, Double> tienTheoThu = new LinkedHashMap<>();
        for (String thu : THU_TRONG_TUAN) tienTheoThu.put(thu, 0.0);

        int soCaNghi = 0;
        String sqlTong = "SELECT thuLam, ISNULL(thanhTien, 0) AS thanhTien FROM LichLamNhanVien WHERE maNhanVien = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlTong)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String thu = rs.getString("thuLam");
                    if (thu != null) tienTheoThu.put(thu.trim(), rs.getDouble("thanhTien"));
                }
            }
        }

        String sqlCaNghi = "SELECT ISNULL(soCaNghi, 0) FROM NhanVien WHERE maDinhDanh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlCaNghi)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) soCaNghi = Math.max(0, rs.getInt(1));
            }
        }

        YearMonth thangLuong = thangLuongHienTai();
        LocalDate ngayBatDau = thangLuong.atDay(1);
        LocalDate ngayKetThuc = thangLuong.atEndOfMonth();
        try {
            LocalDate ngayVao = LocalDate.parse(ngayVaoLam == null ? "" : ngayVaoLam.trim(), DATE_FMT);
            if (ngayVao.isAfter(ngayBatDau)) ngayBatDau = ngayVao;
        } catch (Exception ignored) {
            // Nếu không đọc được ngày vào làm thì tính từ đầu tháng lương hiện tại.
        }

        double luongThang = 0;
        if (!ngayBatDau.isAfter(ngayKetThuc)) {
            for (LocalDate d = ngayBatDau; !d.isAfter(ngayKetThuc); d = d.plusDays(1)) {
                luongThang += tienTheoThu.getOrDefault(thuTrongTuanTuNgay(d), 0.0);
            }
        }

        double donGia = NhanVien.tinhDonGiaTheoGio(chucVu, ngayVaoLam);
        double luongTheoNgayThucTe = Math.max(0, luongThang - soCaNghi * 5 * donGia);

        String sqlUpdate = "UPDATE NhanVien SET luong = ? WHERE maDinhDanh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
            ps.setDouble(1, luongTheoNgayThucTe);
            ps.setString(2, maNhanVien);
            ps.executeUpdate();
        }
    }

    private void capNhatLuongTatCaNhanVienTheoLich() {
        try {
            for (NhanVien nv : NhanVien.docTuSQL()) {
                capNhatLuongNhanVienTheoLich(nv.getMaDinhDanh(), nv.getChucVu(), nv.getNgayVaoLamText());
            }
        } catch (Exception ignored) {
          
        }
    }

    private YearMonth thangLuongHienTai() {
        LocalDate today = LocalDate.now();
        return today.getDayOfMonth() == 1
                ? YearMonth.from(today.minusMonths(1))
                : YearMonth.from(today);
    }

    private String thuTrongTuanTuNgay(LocalDate ngay) {
        DayOfWeek dow = ngay.getDayOfWeek();
        return switch (dow) {
            case MONDAY -> "Thứ 2";
            case TUESDAY -> "Thứ 3";
            case WEDNESDAY -> "Thứ 4";
            case THURSDAY -> "Thứ 5";
            case FRIDAY -> "Thứ 6";
            case SATURDAY -> "Thứ 7";
            case SUNDAY -> "Chủ nhật";
        };
    }

    private NhanVien layNhanVienDangChon() {
        if (maNhanVienDaChon == null) return null;
        for (NhanVien nv : danhSachNhanVien) {
            if (maNhanVienDaChon.equals(nv.getMaDinhDanh())) return nv;
        }
        return null;
    }

    private void hienThiChiTietNgayLam() {
        NhanVien nv = layNhanVienDangChon();
        if (nv == null) {
            AppDialog.showWarning(this, "Thông báo", "Vui lòng chọn nhân viên cần xem ngày làm.");
            return;
        }

        Map<String, LichLamChiTiet> lich = docLichLamNhanVienChiTiet(nv.getMaDinhDanh());
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Chi tiết ngày làm - " + nv.getHoTen(), Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(980, 620);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel root = new GradientCardPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);

        JLabel title = new JLabel("Chi tiết ngày làm: " + nv.getHoTen(), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_LIGHT);

        JLabel note = new JLabel("Tích ca làm theo từng ngày, hệ thống tự tính số giờ và tiền/ngày.", SwingConstants.CENTER);
        note.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        note.setForeground(TEXT_MUTED);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        note.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(note);

        header.add(Box.createHorizontalStrut(38), BorderLayout.WEST);
        header.add(titleBox, BorderLayout.CENTER);
        header.add(Box.createHorizontalStrut(38), BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(THU_TRONG_TUAN.length + 1, 6, 10, 10));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(4, 0, 2, 0));

        String[] headers = {"Thứ", "Ca 1 (7-12h)", "Ca 2 (12-17h)", "Ca 3 (17-22h)", "Số giờ", "Tiền/ngày"};
        for (String h : headers) grid.add(taoLabelBangLich(h, true));

        Map<String, JCheckBox> ca1TheoThu = new LinkedHashMap<>();
        Map<String, JCheckBox> ca2TheoThu = new LinkedHashMap<>();
        Map<String, JCheckBox> ca3TheoThu = new LinkedHashMap<>();
        Map<String, JLabel> soGioLabels = new LinkedHashMap<>();
        Map<String, JLabel> tienLabels = new LinkedHashMap<>();

        for (String thu : THU_TRONG_TUAN) {
            grid.add(taoLabelBangLich(thu, false));
            LichLamChiTiet item = lich.getOrDefault(thu, new LichLamChiTiet());

            JCheckBox c1 = taoCheckBoxBangLich(item.ca1);
            JCheckBox c2 = taoCheckBoxBangLich(item.ca2);
            JCheckBox c3 = taoCheckBoxBangLich(item.ca3);
            if (laKeToanNhanSu) {
                c1.setEnabled(false);
                c2.setEnabled(false);
                c3.setEnabled(false);
            }
            JLabel sg = taoLabelBangLich("0.0", false);
            JLabel tt = taoLabelBangLich("0", false);

            ca1TheoThu.put(thu, c1);
            ca2TheoThu.put(thu, c2);
            ca3TheoThu.put(thu, c3);
            soGioLabels.put(thu, sg);
            tienLabels.put(thu, tt);

            grid.add(taoCheckCell(c1));
            grid.add(taoCheckCell(c2));
            grid.add(taoCheckCell(c3));
            grid.add(sg);
            grid.add(tt);

            Runnable update = () -> capNhatDongLich(c1, c2, c3, sg, tt, nv);
            c1.addActionListener(e -> update.run());
            c2.addActionListener(e -> update.run());
            c3.addActionListener(e -> update.run());
            update.run();
        }

        root.add(grid, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(4, 0, 0, 0));

        JPanel leftHelp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftHelp.setOpaque(false);
        JButton btnBangLuong = taoNutHoiBangLuong();
        btnBangLuong.setToolTipText("Xem bảng lương theo giờ");
        leftHelp.add(btnBangLuong);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setOpaque(false);
        JButton btnLuu = taoNutLichLamPopup("Lưu Lịch Làm", new Color(18, 59, 140), Color.WHITE);
        JButton btnDong = taoNutLichLamPopup("Đóng", new Color(120, 105, 85), Color.WHITE);
        if (!laKeToanNhanSu) {
            buttons.add(btnLuu);
        }
        buttons.add(btnDong);

        footer.add(leftHelp, BorderLayout.WEST);
        footer.add(buttons, BorderLayout.CENTER);
        footer.add(Box.createHorizontalStrut(42), BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);

        btnBangLuong.addActionListener(e -> hienBangLuongTheoKinhNghiem(dialog));
        btnLuu.addActionListener(e -> {
            try {
                taoLichLamMacDinhNeuCan(nv.getMaDinhDanh(), nv.getChucVu(), nv.getNgayVaoLamText());
                for (String thu : THU_TRONG_TUAN) {
                    capNhatLichLamChoThu(nv.getMaDinhDanh(), thu,
                            ca1TheoThu.get(thu).isSelected(),
                            ca2TheoThu.get(thu).isSelected(),
                            ca3TheoThu.get(thu).isSelected(),
                            nv.getChucVu(), nv.getNgayVaoLamText());
                }
                capNhatLuongNhanVienTheoLich(nv.getMaDinhDanh(), nv.getChucVu(), nv.getNgayVaoLamText());
                danhSachNhanVien = NhanVien.docTuSQL();
                capNhatBang();
                AppDialog.showSuccess(this, "Thông báo", "Đã cập nhật lịch làm và lương nhân viên.");
                dialog.dispose();
            } catch (Exception ex) {
                AppDialog.showError(this, "Lỗi", "Không thể lưu lịch làm: " + ex.getMessage());
            }
        });
        btnDong.addActionListener(e -> dialog.dispose());

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private JPanel taoCheckCell(JCheckBox checkBox) {
        JPanel cell = new RoundedScheduleCell(checkBox);
        cell.setLayout(new GridBagLayout());
        cell.setOpaque(false);
        cell.setBorder(new EmptyBorder(0, 0, 0, 0));
        cell.add(checkBox);
        return cell;
    }

    private JCheckBox taoCheckBoxBangLich(boolean selected) {
        JCheckBox cb = new JCheckBox();
        cb.setSelected(selected);
        cb.setOpaque(false);
        cb.setHorizontalAlignment(SwingConstants.CENTER);
        cb.setFocusPainted(false);
        cb.setContentAreaFilled(false);
        cb.setBorderPainted(false);
        cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Icon icon = new ScheduleCheckIcon(22);
        cb.setIcon(icon);
        cb.setSelectedIcon(icon);
        cb.addActionListener(e -> {
            Container parent = cb.getParent();
            if (parent != null) {
                parent.repaint();
            }
        });
        return cb;
    }

    private JButton taoNutHoiBangLuong() {
        JButton btn = new JButton("?") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color base = getModel().isPressed()
                        ? new Color(3, 24, 64)
                        : (getModel().isRollover() ? new Color(36, 86, 205) : new Color(18, 59, 140));

                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(244, 210, 122));
                g2.setStroke(new BasicStroke(1.6f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(40, 40));
        btn.setMinimumSize(new Dimension(40, 40));
        btn.setMaximumSize(new Dimension(40, 40));
        btn.setBorder(new EmptyBorder(0, 0, 0, 0));
        return btn;
    }

    private JButton taoNutLichLamPopup(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color base = getModel().isPressed() ? bg.darker() : bg;
                if (getModel().isRollover()) {
                    base = base.brighter();
                }

                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        int width = text.toLowerCase().contains("lưu") ? 160 : 120;
        btn.setPreferredSize(new Dimension(width, 42));
        btn.setMinimumSize(btn.getPreferredSize());
        btn.setMaximumSize(btn.getPreferredSize());
        btn.setBorder(new EmptyBorder(9, 20, 9, 20));
        return btn;
    }

    private static class RoundedScheduleCell extends JPanel {
        private final JCheckBox checkBox;

        RoundedScheduleCell(JCheckBox checkBox) {
            this.checkBox = checkBox;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            boolean selected = checkBox != null && checkBox.isSelected();
            Color bg = selected ? new Color(229, 240, 255) : Color.WHITE;
            Color stroke = selected ? new Color(18, 59, 140) : new Color(216, 224, 236);

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(stroke);
            g2.setStroke(new BasicStroke(selected ? 1.8f : 1.1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class ScheduleCheckIcon implements Icon {
        private final int size;

        ScheduleCheckIcon(int size) {
            this.size = size;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            boolean selected = c instanceof AbstractButton && ((AbstractButton) c).isSelected();

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (selected) {
                g2.setPaint(new GradientPaint(x, y, new Color(42, 104, 224),
                        x + size, y + size, new Color(3, 24, 64)));
                g2.fillRoundRect(x, y, size, size, 8, 8);

                g2.setColor(new Color(255, 255, 255, 210));
                g2.setStroke(new BasicStroke(2.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int x1 = x + 6;
                int y1 = y + 11;
                int x2 = x + 10;
                int y2 = y + 15;
                int x3 = x + 17;
                int y3 = y + 7;
                g2.drawLine(x1, y1, x2, y2);
                g2.drawLine(x2, y2, x3, y3);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, y, size, size, 8, 8);
                g2.setColor(new Color(125, 139, 162));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(x + 1, y + 1, size - 3, size - 3, 8, 8);
            }

            g2.dispose();
        }
    }

    private void hienBangLuongTheoKinhNghiem(Component parent) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Bảng lương theo giờ", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(860, 470);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parent);

        JPanel root = new GradientCardPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("Bảng lương theo giờ theo năm kinh nghiệm", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(TEXT_LIGHT);
        root.add(title, BorderLayout.NORTH);

        String[] cols = {"Số năm kinh nghiệm", "Nhân viên bán hàng", "Nhân viên kho", "Kế toán"};
        Object[][] data = {
                {"Dưới 1 năm", "30.000đ/giờ", "32.000đ/giờ", "40.000đ/giờ"},
                {"1 - 2 năm", "35.000đ/giờ", "36.000đ/giờ", "45.000đ/giờ"},
                {"2 - 3 năm", "40.000đ/giờ", "42.000đ/giờ", "50.000đ/giờ"},
                {"3 - 5 năm", "45.000đ/giờ", "47.000đ/giờ", "55.000đ/giờ"},
                {"Trên 5 năm", "50.000đ/giờ", "52.000đ/giờ", "60.000đ/giờ"}
        };
        JTable table = new JTable(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setGridColor(BORDER);
        table.setShowGrid(true);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                setHorizontalAlignment(col == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
                setBackground(row % 2 == 0 ? Color.WHITE : TABLE_DARK_2);
                setForeground(TEXT_LIGHT);
                setBorder(new EmptyBorder(4, 10, 4, 10));
                return this;
            }
        });
        canGiuaHeaderVaFixKhoangTrangBang(table);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton close = taoNutLichLamPopup("Đóng", new Color(120, 105, 85), Color.WHITE);
        close.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(8, 0, 0, 0));
        bottom.add(close);
        root.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private JLabel taoLabelBangLich(String text, boolean header) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setFont(new Font("Segoe UI", header ? Font.BOLD : Font.PLAIN, 13));
        label.setForeground(TEXT_LIGHT);
        label.setBackground(header ? HEADER_BG : Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return label;
    }

    private void capNhatDongLich(JCheckBox ca1, JCheckBox ca2, JCheckBox ca3, JLabel sg, JLabel tt, NhanVien nv) {
        double soGio = (ca1.isSelected() ? 5 : 0) + (ca2.isSelected() ? 5 : 0) + (ca3.isSelected() ? 5 : 0);
        double donGia = NhanVien.tinhDonGiaTheoGio(nv.getChucVu(), nv.getNgayVaoLamText());
        sg.setText(String.format("%.1f", soGio));
        tt.setText(String.format("%,.0f", soGio * donGia));
    }

    private boolean phuHopChucVuLoc(NhanVien nv) {
        if (nv == null || cboChucVuLoc == null) return true;
        Object selected = cboChucVuLoc.getSelectedItem();
        String loc = selected == null ? "Tất cả" : selected.toString();
        if ("Tất cả".equalsIgnoreCase(loc)) return true;
        return loc.equalsIgnoreCase(nv.getChucVu());
    }

    private void capNhatBang() {
        tableModel.setRowCount(0);
 
        for (NhanVien nv : danhSachNhanVien) {
            if (!phuHopChucVuLoc(nv)) continue;
            Object[] row = {
                    nv.getMaDinhDanh(),
                    nv.getHoTen(),
                    nv.getSoDienThoai(),
                    nv.getDiaChi(),
                    nv.getNgaySinh() != null ? DATE_FMT.format(nv.getNgaySinh()) : "",
                    nv.getNgayVaoLam() != null ? DATE_FMT.format(nv.getNgayVaoLam()) : "",
                    nv.getEmail(),
                    nv.getCccd(),
                    nv.getChucVu(),
                    String.format("%,.0f", NhanVien.tinhDonGiaTheoGio(nv.getChucVu(), nv.getNgayVaoLamText())),
                    nv.getSoCaNghi(),
                    String.format("%,.0f", nv.getLuong())
            };
 
            tableModel.addRow(row);
        }
        datDoRongCotNhanVien();
        capNhatSoKetQua();
    }

    private static class GradientCardPanel extends JPanel {
        GradientCardPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(
                    0, 0, Color.WHITE,
                    getWidth(), getHeight(), CARD_SOFT
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            g2.setColor(new Color(216, 224, 236));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            g2.dispose();
            super.paintComponent(g);
        }
    }
 
}
