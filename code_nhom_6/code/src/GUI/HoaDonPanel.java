package GUI;

import model.HoaDon;
import database.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.UUID;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HoaDonPanel extends JPanel {

    private JTextField txtMaKhachHang;
    private JTextField txtTenKhachHang;
    private JTextField txtDiemHienCo;
    private JCheckBox chkDungDiem;
    private JTextField txtMaNhanVien;
    private JTextField txtGiamGia;
    private JTextField txtGhiChu;

    private JTextField txtMaSanPham;
    private JTextField txtTenSanPham;
    private JTextField txtKichCo;
    private JTextField txtSoLuong;
    private JTextField txtDonGia;
    private JTextField txtTonKho;
    private JTextField txtGiamGiaSanPham;

    private JLabel lblTongTien;
    private JLabel lblGiamGia;
    private JLabel lblDiemSuDung;
    private JLabel lblTienDoiDiem;
    private JLabel lblThanhToan;
    private JLabel lblTrangThai;

    private JTextField txtTimKiem;
    private JComboBox<String> cboTrangThaiLoc;
    private JLabel lblSoKetQua;
    private JTable tableChiTietTam;
    private DefaultTableModel modelChiTietTam;
    private JTable tableHoaDon;
    private DefaultTableModel modelHoaDon;

    private List<HoaDon> danhSachHoaDon;
    private final Map<String, CartItem> gioHang = new LinkedHashMap<>();

    private String keyChiTietDangSua = null;

    private String maHoaDonDaChon = null;
    private boolean daHuyChonKichCoSanPham = false;
    private String currentVaiTro = "ADMIN";
    private String currentMaNhanVien = "";

    private String maKhachHangHienTai = null;
    private boolean khachHangMoiDangNhap = false;
    private String prefixTenKhachHangMoi = "";

    private static final Color BG_PANEL = new Color(18, 18, 16);
    private static final Color CARD_DARK = new Color(31, 28, 25);
    private static final Color CARD_DARK_2 = new Color(38, 34, 30);
    private static final Color INPUT_DARK = new Color(50, 45, 39);
    private static final Color TABLE_DARK = new Color(34, 31, 28);
    private static final Color TABLE_DARK_2 = new Color(41, 37, 33);
    private static final Color GOLD_LIGHT = new Color(223, 196, 162);
    private static final Color GOLD = new Color(210, 169, 103);
    private static final Color GOLD_DARK = new Color(124, 88, 49);
    private static final Color TEXT_LIGHT = new Color(245, 239, 230);
    private static final Color TEXT_MUTED = new Color(185, 175, 160);
    private static final Color BORDER = new Color(94, 76, 54);

    public HoaDonPanel() {
        this("ADMIN", "");
    }

    public HoaDonPanel(String vaiTro) {
        this(vaiTro, "");
    }

    public HoaDonPanel(String vaiTro, String maNhanVien) {
        this.currentVaiTro = vaiTro == null || vaiTro.isBlank() ? "STAFF" : vaiTro.trim().toUpperCase();
        this.currentMaNhanVien = maNhanVien == null ? "" : maNhanVien.trim().toUpperCase();
        this.danhSachHoaDon = HoaDon.docTuSQL();

        setLayout(new BorderLayout(12, 12));
        setBackground(BG_PANEL);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        taoGiaoDien();
        ganSuKien();
        capNhatBangHoaDon();
        capNhatBangChiTietTam();
        capNhatTongTien();
    }

    private boolean laAdmin() {
        return "ADMIN".equalsIgnoreCase(currentVaiTro);
    }

    private boolean laKeToan() {
        return "ACCOUNTANT".equalsIgnoreCase(currentVaiTro);
    }

    private boolean chiDuocXemHoaDon() {
        return laKeToan();
    }

    private void taoGiaoDien() {

        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setOpaque(true);
        leftPanel.setBackground(BG_PANEL);
        leftPanel.setBorder(new EmptyBorder(0, 0, 0, 8));
        leftPanel.setPreferredSize(new Dimension(392, 0));
        leftPanel.setMinimumSize(new Dimension(360, 0));

        JPanel leftContent = new JPanel();
        leftContent.setOpaque(true);
        leftContent.setBackground(BG_PANEL);
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel panelThongTin = taoPanelThongTinHoaDon();
        JPanel panelSanPham = taoPanelThemSanPham();

        panelThongTin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));
        panelSanPham.setMaximumSize(new Dimension(Integer.MAX_VALUE, 258));

        leftContent.add(panelThongTin);
        leftContent.add(Box.createVerticalStrut(10));
        leftContent.add(panelSanPham);
        leftContent.add(Box.createVerticalGlue());

        JScrollPane leftScroll = new JScrollPane(leftContent);
        leftScroll.setOpaque(true);
        leftScroll.setBackground(BG_PANEL);
        leftScroll.getViewport().setOpaque(true);
        leftScroll.getViewport().setBackground(BG_PANEL);
        leftScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        leftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        leftScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScroll.getVerticalScrollBar().setPreferredSize(new Dimension(22, 0));
        leftScroll.getVerticalScrollBar().setUnitIncrement(18);
        leftScroll.getVerticalScrollBar().setUI(new GoldScrollBarUI());
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        JPanel fixedBottom = new JPanel(new BorderLayout(0, 10));
        fixedBottom.setOpaque(true);
        fixedBottom.setBackground(BG_PANEL);
        fixedBottom.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel panelTongTien = taoPanelTongTien();
        JPanel panelNut = taoPanelNutChucNang();

        panelTongTien.setPreferredSize(new Dimension(0, 116));
        panelTongTien.setMinimumSize(new Dimension(0, 108));
        panelNut.setPreferredSize(new Dimension(0, 228));
        panelNut.setMinimumSize(new Dimension(0, 216));

        fixedBottom.add(panelTongTien, BorderLayout.NORTH);
        fixedBottom.add(panelNut, BorderLayout.CENTER);
        leftPanel.add(fixedBottom, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setOpaque(true);
        rightPanel.setBackground(BG_PANEL);
        rightPanel.setBorder(new EmptyBorder(0, 8, 0, 0));
        rightPanel.add(taoPanelTimKiem(), BorderLayout.NORTH);

        JScrollPane hoaDonScroll = taoBangHoaDon();
        JScrollPane chiTietScroll = taoBangChiTietTam();
        JPanel hoaDonBox = taoPanelDanhSachHoaDon(hoaDonScroll);
        hoaDonBox.setMinimumSize(new Dimension(0, 260));
        chiTietScroll.setMinimumSize(new Dimension(0, 170));

        JSplitPane splitTables = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hoaDonBox, chiTietScroll);
        splitTables.setOpaque(true);
        splitTables.setBackground(BG_PANEL);
        splitTables.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        splitTables.setDividerSize(7);
        splitTables.setResizeWeight(0.66);
        splitTables.setContinuousLayout(true);
        splitTables.setDividerLocation(390);
        splitTables.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI() {
            @Override
            public javax.swing.plaf.basic.BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(BORDER);
                        g.fillRect(0, 0, getWidth(), getHeight());
                        g.setColor(GOLD_DARK);
                        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
                    }
                };
            }
        });

        rightPanel.add(splitTables, BorderLayout.CENTER);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        mainSplit.setOpaque(true);
        mainSplit.setBackground(BG_PANEL);
        mainSplit.setBorder(null);
        mainSplit.setDividerSize(7);
        mainSplit.setContinuousLayout(true);
        mainSplit.setResizeWeight(0.5);
        mainSplit.setEnabled(false); // cố định chia đôi, không cho kéo lệch bố cục
        mainSplit.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI() {
            @Override
            public javax.swing.plaf.basic.BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(BORDER);
                        g.fillRect(0, 0, getWidth(), getHeight());
                        g.setColor(GOLD_LIGHT);
                        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
                    }
                };
            }
        });

        add(mainSplit, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> mainSplit.setDividerLocation(0.5));
        mainSplit.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                mainSplit.setDividerLocation(0.5);
            }
        });
    }

    private JPanel taoPanelTimKiem() {
        JPanel p = taoCardPanel();
        p.setLayout(new BorderLayout(0, 8));
        p.setBorder(taoTitledBorder("Tìm kiếm hóa đơn"));

        JLabel lbl = taoLabel("Mã HĐ / Mã NV / SĐT KH / Ngày(dd/mm/yyyy):");
        lbl.setBorder(new EmptyBorder(0, 4, 0, 0));

        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setOpaque(false);

        txtTimKiem = taoTextField();
        txtTimKiem.setPreferredSize(new Dimension(240, 38));
        txtTimKiem.setMinimumSize(new Dimension(180, 38));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);

        JButton btnTimKiem = taoNut("Tìm kiếm", new Color(214, 173, 103), Color.BLACK);
        JButton btnHienTatCa = taoNut("Hiển thị tất cả", CARD_DARK_2, GOLD_LIGHT);

        btnTimKiem.setPreferredSize(new Dimension(130, 38));
        btnHienTatCa.setPreferredSize(new Dimension(150, 38));

        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        btnHienTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            if (cboTrangThaiLoc != null)
                cboTrangThaiLoc.setSelectedItem("Tất cả");
            danhSachHoaDon = HoaDon.docTuSQL();
            capNhatBangHoaDon();
        });

        btns.add(btnTimKiem);
        btns.add(btnHienTatCa);

        inputRow.add(txtTimKiem, BorderLayout.CENTER);
        inputRow.add(btns, BorderLayout.EAST);

        cboTrangThaiLoc = new JComboBox<>(new String[] { "Tất cả", "Đã thanh toán", "Chờ xử lý", "Trả một phần hàng" });
        cboTrangThaiLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cboTrangThaiLoc.setPreferredSize(new Dimension(185, 36));
        cboTrangThaiLoc.setBackground(INPUT_DARK);
        cboTrangThaiLoc.setForeground(TEXT_LIGHT);
        cboTrangThaiLoc.addActionListener(e -> capNhatBangHoaDon());

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterRow.setOpaque(false);
        filterRow.add(taoLabel("Sắp xếp theo:"));
        filterRow.add(cboTrangThaiLoc);

        JPanel centerBox = new JPanel(new BorderLayout(0, 8));
        centerBox.setOpaque(false);
        centerBox.add(inputRow, BorderLayout.NORTH);
        centerBox.add(filterRow, BorderLayout.SOUTH);

        p.add(lbl, BorderLayout.NORTH);
        p.add(centerBox, BorderLayout.CENTER);
        return p;
    }

    private JPanel taoPanelThongTinHoaDon() {
        JPanel p = taoCardPanel();
        p.setBorder(taoTitledBorder("Thông tin hóa đơn"));
        p.setLayout(new GridBagLayout());

        txtMaKhachHang = taoTextField();
        txtTenKhachHang = taoTextField();
        txtTenKhachHang.setEditable(true);

        txtDiemHienCo = taoTextField("0");
        txtDiemHienCo.setEditable(false);
        txtDiemHienCo.setVisible(false);
        chkDungDiem = new JCheckBox("Dùng toàn bộ điểm hiện có");
        styleCheckBox(chkDungDiem);
        chkDungDiem.setSelected(false);
        chkDungDiem.setVisible(false);

        txtMaNhanVien = taoTextField(defaultMaNhanVien());
        txtMaNhanVien.setEditable(laAdmin());
        txtGiamGia = taoTextField("0");
        txtGhiChu = taoTextField();

        JButton btnTimKhach = taoNut("Tìm khách", new Color(214, 173, 103), Color.BLACK);
        btnTimKhach.addActionListener(e -> xuLyTimKhachHang());

        int row = 0;
        addRow(p, row++, "SĐT khách hàng:", wrapFieldWithButton(txtMaKhachHang, btnTimKhach));
        addRow(p, row++, "Tên khách hàng:", txtTenKhachHang);
        addRow(p, row++, "Mã nhân viên lập:", txtMaNhanVien);
        addRow(p, row, "Ghi chú:", txtGhiChu);

        txtMaKhachHang.addActionListener(e -> xuLyTimKhachHang());
        txtGiamGia.addActionListener(e -> capNhatTongTien());
        return p;
    }

    private JPanel taoPanelThemSanPham() {
        JPanel p = taoCardPanel();
        p.setBorder(taoTitledBorder("Thêm sản phẩm vào hóa đơn"));
        p.setLayout(new GridBagLayout());

        txtMaSanPham = taoTextField();
        txtTenSanPham = taoTextField();
        txtTenSanPham.setEditable(false);
        txtKichCo = taoTextField();
        txtSoLuong = taoTextField("1");
        txtDonGia = taoTextField("0");
        txtDonGia.setEditable(false);
        txtGiamGiaSanPham = taoTextField("0");
        txtGiamGiaSanPham.setEditable(false);
        txtTonKho = taoTextField("0");
        txtTonKho.setEditable(false);

        JButton btnTimSP = taoNut("Tìm SP", new Color(214, 173, 103), Color.BLACK);
        JButton btnThemSP = taoNut("Thêm sản phẩm", new Color(86, 167, 110), Color.WHITE);
        JButton btnXoaSP = taoNut("Xóa khỏi hóa đơn", new Color(184, 76, 60), Color.WHITE);

        btnTimSP.addActionListener(e -> xuLyTimSanPham());
        btnThemSP.addActionListener(e -> xuLyThemHoacCapNhatSanPham());
        btnXoaSP.addActionListener(e -> xoaSanPhamKhoiGio());

        int row = 0;
        addRow(p, row++, "Mã sản phẩm:", wrapFieldWithButton(txtMaSanPham, btnTimSP));
        addRow(p, row++, "Tên sản phẩm:", txtTenSanPham);
        addRow(p, row++, "Kích cỡ:", txtKichCo);
        addRow(p, row++, "Số lượng:", txtSoLuong);
        addRow(p, row++, "Giá bán:", txtDonGia);
        addRow(p, row++, "Giảm giá SP (%):", txtGiamGiaSanPham);
        addRow(p, row++, "Tồn kho:", txtTonKho);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);

        JPanel btnBox = new JPanel(new GridLayout(1, 2, 10, 0));
        btnBox.setOpaque(false);
        if (!chiDuocXemHoaDon()) {
            btnBox.add(btnThemSP);
            btnBox.add(btnXoaSP);
        } else {
            JLabel note = new JLabel("Kế toán chỉ được xem hóa đơn", SwingConstants.CENTER);
            note.setForeground(Color.BLACK);
            note.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnBox.add(note);
        }
        p.add(btnBox, gbc);

        txtMaSanPham.addActionListener(e -> xuLyTimSanPham());
        return p;
    }

    private JPanel taoPanelTongTien() {
        JPanel p = taoCardPanel();
        p.setBorder(taoTitledBorder("Tổng thanh toán"));
        p.setLayout(new BorderLayout());

        lblTrangThai = taoTongLabel("Trạng thái: Chờ thanh toán", 13, GOLD_LIGHT);
        lblTongTien = taoTongLabel("Tổng tiền: 0 VNĐ", 13, TEXT_LIGHT);
        lblGiamGia = taoTongLabel("Giảm giá: 0 VNĐ", 13, TEXT_LIGHT);
        lblDiemSuDung = taoTongLabel("Điểm dùng: 0 điểm", 13, TEXT_LIGHT);
        lblTienDoiDiem = taoTongLabel("Tiền đổi điểm: 0 VNĐ", 13, TEXT_LIGHT);
        lblThanhToan = taoTongLabel("Thanh toán: 0 VNĐ", 14, GOLD);

        JPanel grid = new JPanel(new GridLayout(3, 2, 18, 8));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(8, 14, 10, 14));

        grid.add(lblTrangThai);
        grid.add(lblDiemSuDung);
        grid.add(lblTongTien);
        grid.add(lblTienDoiDiem);
        grid.add(lblGiamGia);
        grid.add(lblThanhToan);

        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private JPanel taoPanelNutChucNang() {
        JPanel p = taoCardPanel();
        p.setLayout(new BorderLayout(0, 10));
        p.setBorder(taoTitledBorder("Thao tác"));

        JPanel grid = new JPanel(new GridLayout(5, 2, 10, 10));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(8, 10, 10, 10));

        JButton btnLuu = taoNut("Thêm hóa đơn", new Color(86, 167, 110), Color.WHITE);
        JButton btnSua = taoNut("Sửa hóa đơn", new Color(83, 145, 195), Color.WHITE);
        JButton btnThanhToan = taoNut("Xác nhận thanh toán", new Color(72, 132, 191), Color.WHITE);
        JButton btnTraHang = taoNut("Trả hàng", new Color(205, 157, 76), Color.BLACK);
        JButton btnIn = taoNut("In hóa đơn", new Color(214, 173, 103), Color.BLACK);
        JButton btnChiTiet = taoNut("Chi tiết hóa đơn", new Color(86, 167, 110), Color.WHITE);
        JButton btnXoa = taoNut("Xóa hóa đơn", new Color(184, 76, 60), Color.WHITE);
        JButton btnClear = taoNut("Làm mới form", CARD_DARK_2, GOLD_LIGHT);

        Dimension btnSize = new Dimension(165, 38);
        for (JButton btn : new JButton[] { btnLuu, btnSua, btnThanhToan, btnTraHang, btnIn, btnChiTiet, btnXoa,
                btnClear }) {
            btn.setPreferredSize(btnSize);
            btn.setMinimumSize(btnSize);
            btn.setMaximumSize(btnSize);
        }

        btnLuu.addActionListener(this::xuLyLuuMoi);
        btnSua.addActionListener(e -> xuLyLuuSua());
        btnThanhToan.addActionListener(e -> xuLyThanhToan());
        btnTraHang.addActionListener(e -> xuLyTraHang());
        btnIn.addActionListener(e -> xuLyInHoaDon());
        btnChiTiet.addActionListener(e -> xuLyChiTietHoaDon());
        btnXoa.addActionListener(e -> xuLyXoaHoaDon());
        btnClear.addActionListener(e -> xoaRongForm());

        if (chiDuocXemHoaDon()) {
            grid.add(btnChiTiet);
            grid.add(btnIn);
            grid.add(new JLabel());
            grid.add(new JLabel());
            grid.add(new JLabel());
            grid.add(new JLabel());
            grid.add(new JLabel());
            grid.add(new JLabel());
            grid.add(new JLabel());
            grid.add(new JLabel());
        } else {
            grid.add(btnLuu);
            grid.add(btnSua);
            grid.add(btnThanhToan);
            grid.add(btnTraHang);
            grid.add(btnIn);
            grid.add(btnChiTiet);
            grid.add(btnXoa);
            grid.add(btnClear);
            grid.add(new JLabel());
        }

        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private JScrollPane taoBangChiTietTam() {
        String[] cols = { "Mã SP", "Tên sản phẩm", "Kích cỡ", "Số lượng", "Giá bán", "Giảm giá", "Thành tiền" };
        modelChiTietTam = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tableChiTietTam = taoTable(modelChiTietTam);
        tableChiTietTam.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                napDongChiTietLenForm();
        });
        datDoRongCotChiTiet();
        return taoScrollPane(tableChiTietTam, "Chi tiết hóa đơn tạm / sản phẩm trong hóa đơn");
    }

    private JPanel taoPanelDanhSachHoaDon(JScrollPane hoaDonScroll) {
        lblSoKetQua = taoLabelSoKetQua();
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(true);
        panel.setBackground(BG_PANEL);
        panel.add(hoaDonScroll, BorderLayout.CENTER);
        panel.add(lblSoKetQua, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel taoLabelSoKetQua() {
        JLabel label = new JLabel("Số kết quả tìm được: 0", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(11, 23, 54));
        label.setOpaque(true);
        label.setBackground(new Color(248, 250, 252));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 224, 236), 1),
                new EmptyBorder(8, 12, 8, 12)));
        return label;
    }

    private void capNhatSoKetQuaHoaDon() {
        if (lblSoKetQua != null && modelHoaDon != null) {
            lblSoKetQua.setText("Số kết quả tìm được: " + modelHoaDon.getRowCount());
        }
    }

    private String layTrangThaiLocHoaDon() {
        if (cboTrangThaiLoc == null || cboTrangThaiLoc.getSelectedItem() == null)
            return "Tất cả";
        String v = cboTrangThaiLoc.getSelectedItem().toString();
        if ("Chờ xử lý".equalsIgnoreCase(v))
            return HoaDon.TRANG_THAI_CHO;
        return v;
    }

    private boolean phuHopTrangThaiLoc(HoaDon hd) {
        if (hd == null)
            return false;
        String loc = layTrangThaiLocHoaDon();
        if ("Tất cả".equalsIgnoreCase(loc))
            return true;
        return loc.equalsIgnoreCase(hd.getTrangThai());
    }

    private JScrollPane taoBangHoaDon() {
        String[] cols = {
                "Mã HĐ", "Mã KH", "Mã NV", "Ngày lập", "Tổng tiền", "Giảm giá",
                "Đổi điểm", "Thanh toán", "Tiền sau trả hàng", "Trạng thái", "Ghi chú"
        };
        modelHoaDon = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tableHoaDon = taoTable(modelHoaDon);
        tableHoaDon.setRowHeight(36);
        tableHoaDon.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        datDoRongCotHoaDon();
        return taoScrollPane(tableHoaDon, "Danh sách hóa đơn");
    }

    private void ganSuKien() {
        tableHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;
            int row = tableHoaDon.getSelectedRow();
            if (row < 0)
                return;
            maHoaDonDaChon = modelHoaDon.getValueAt(row, 0).toString();
            HoaDon hd = layHoaDonDangChon();
            if (hd != null)
                napHoaDonLenForm(hd);
        });
    }

    private void xuLyLuuMoi(ActionEvent e) {
        if (chiDuocXemHoaDon()) {
            showHoaDonWarning("Không đủ quyền",
                    "Kế toán chỉ được xem hóa đơn, không được thao tác thêm/sửa/xóa/thanh toán/trả hàng.");
            return;
        }

        try {
            HoaDon hd = taoHoaDonTuForm(false);
            hd.luuVaoSQL();

            String maHoaDonMoi = hd.getMaHoaDon();

            showHoaDonSuccess(
                    "Thành công",
                    "Lưu hóa đơn mới thành công.\n" +
                            "Hóa đơn đang ở trạng thái chờ thanh toán.\n" +
                            "Bạn có thể bấm Xác nhận thanh toán ngay.");

            if (txtTimKiem != null) {
                txtTimKiem.setText("");
            }
            if (cboTrangThaiLoc != null) {
                cboTrangThaiLoc.setSelectedItem("Tất cả");
            }

            taiLaiDuLieu();
            chonHoaDonTrongBang(maHoaDonMoi);

            dongBoDuLieuKhachHangVaSanPham();

        } catch (Exception ex) {
            showHoaDonError("Lỗi thêm hóa đơn", layThongBaoLoi(ex));
        }
    }

    private void xuLyLuuSua() {
        if (chiDuocXemHoaDon()) {
            showHoaDonWarning("Không đủ quyền",
                    "Kế toán chỉ được xem hóa đơn, không được thao tác thêm/sửa/xóa/thanh toán/trả hàng.");
            return;
        }

        HoaDon selected = layHoaDonDangChon();
        if (selected == null) {
            showHoaDonWarning("Thông báo", "Vui lòng chọn hóa đơn cần sửa.");
            return;
        }
        if (!HoaDon.TRANG_THAI_CHO.equals(selected.getTrangThai())) {
            if (HoaDon.TRANG_THAI_DA_THANH.equals(selected.getTrangThai())) {
                showHoaDonWarning("Không thể sửa", "Không thể sửa hóa đơn đã thanh toán");
            } else {
                showHoaDonWarning("Không thể sửa", "Chỉ có thể sửa hóa đơn ở trạng thái chờ thanh toán.");
            }
            return;
        }
        try {
            HoaDon hd = taoHoaDonTuForm(true);
            hd.capNhatSQL();
            showHoaDonSuccess("Thành công", "Sửa hóa đơn thành công.");
            taiLaiDuLieu();
            xoaRongForm();
        } catch (Exception ex) {
            showHoaDonError("Lỗi sửa hóa đơn", layThongBaoLoi(ex));
        }
    }

    private void xuLyThanhToan() {
        if (chiDuocXemHoaDon()) {
            showHoaDonWarning("Không đủ quyền",
                    "Kế toán chỉ được xem hóa đơn, không được thao tác thêm/sửa/xóa/thanh toán/trả hàng.");
            return;
        }

        HoaDon hd = layHoaDonDangChon();
        if (hd == null) {
            showHoaDonWarning("Thông báo", "Vui lòng chọn hóa đơn cần thanh toán.");
            return;
        }
        if (!HoaDon.TRANG_THAI_CHO.equals(hd.getTrangThai())) {
            showHoaDonWarning("Không thể thanh toán", "Chỉ hóa đơn chờ thanh toán mới được xác nhận thanh toán.");
            return;
        }

        int diemHienCo = layDiemKhachHang(hd.getMaKhachHang());
        boolean dungDiem = false;
        if (diemHienCo > 0) {
            double soTienCanThanhToan = HoaDon.lamTronTienThanhToan(Math.max(0, hd.getTongTien() - hd.getGiamGia()));
            int diemCanDung = HoaDon.tinhSoDiemCanDung(soTienCanThanhToan, diemHienCo);
            double tienQuyDoi = Math.min(diemCanDung * HoaDon.TIEN_MOI_DIEM, soTienCanThanhToan);

            dungDiem = showHoaDonConfirm(
                    "Sử dụng điểm tích lũy",
                    "Khách hàng hiện có " + diemHienCo + " điểm.\n" +
                            "Hệ thống chỉ trừ tối đa " + diemCanDung + " điểm = " + dinhDangTien(tienQuyDoi) + " VNĐ.\n"
                            +
                            "Điểm thừa vẫn giữ lại cho khách, sau đó cộng điểm mới của hóa đơn.\n" +
                            "Bạn có muốn sử dụng điểm tích lũy của khách không?");
        }

        String maHoaDonThanhToan = hd.getMaHoaDon();

        try {
            hd.xacNhanThanhToanSQL(dungDiem);

            showHoaDonSuccess(
                    "Thanh toán thành công",
                    "Xác nhận thanh toán thành công.\n" +
                            "Khách được cộng " + hd.getDiemCongSauThanhToan() + " điểm tích lũy.");

            if (txtTimKiem != null) {
                txtTimKiem.setText("");
            }

            taiLaiDuLieu();
            chonHoaDonTrongBang(maHoaDonThanhToan);

            dongBoDuLieuKhachHangVaSanPham();

        } catch (Exception ex) {
            showHoaDonError("Lỗi thanh toán", layThongBaoLoi(ex));
        }
    }

    private void xuLyChiTietHoaDon() {
        HoaDon hd = layHoaDonDangChon();
        if (hd == null) {
            showHoaDonWarning("Thông báo", "Vui lòng chọn hóa đơn cần xem chi tiết.");
            return;
        }
        AppDialog.showHoaDonDetails(this, hd);
    }

    private void xuLyInHoaDon() {
        HoaDon hd = layHoaDonDangChon();
        if (hd == null) {
            showHoaDonWarning("Thông báo", "Vui lòng chọn hóa đơn cần in.");
            return;
        }

        if (HoaDon.TRANG_THAI_TRA_MOT_PHAN.equals(hd.getTrangThai())) {
            int choice = AppDialog.showInvoicePrintChoice(this);
            if (choice == AppDialog.INVOICE_PRINT_BEFORE_RETURN) {
                AppDialog.luuHoaDonPdf(this, hd, false);
            } else if (choice == AppDialog.INVOICE_PRINT_AFTER_RETURN) {
                AppDialog.luuHoaDonPdf(this, hd, true);
            }
            return;
        }

        AppDialog.luuHoaDonPdf(this, hd);
    }

    private void xuLyXoaHoaDon() {
        if (chiDuocXemHoaDon()) {
            showHoaDonWarning("Không đủ quyền",
                    "Kế toán chỉ được xem hóa đơn, không được thao tác thêm/sửa/xóa/thanh toán/trả hàng.");
            return;
        }

        if (!laAdmin()) {
            showHoaDonWarning("Không đủ quyền", "Tài khoản nhân viên không được xóa hóa đơn.");
            return;
        }
        HoaDon hd = layHoaDonDangChon();
        if (hd == null) {
            showHoaDonWarning("Thông báo", "Vui lòng chọn hóa đơn cần xóa.");
            return;
        }
        if (HoaDon.TRANG_THAI_DA_THANH.equals(hd.getTrangThai())
                || HoaDon.TRANG_THAI_TRA_MOT_PHAN.equals(hd.getTrangThai())) {
            showHoaDonWarning("Không thể xóa", "Không thể xóa hóa đơn đã thanh toán hoặc đã trả một phần hàng.");
            return;
        }
        if (!HoaDon.TRANG_THAI_CHO.equals(hd.getTrangThai())
                && !HoaDon.TRANG_THAI_HUY.equals(hd.getTrangThai())) {
            showHoaDonWarning("Không thể xóa", "Chỉ có thể xóa hóa đơn ở trạng thái chờ thanh toán hoặc đã hủy.");
            return;
        }
        if (!showHoaDonConfirm("Xác nhận xóa", "Bạn có chắc muốn xóa hóa đơn " + hd.getMaHoaDon() + "?"))
            return;
        try {
            HoaDon.xoaKhoiSQL(hd.getMaHoaDon());
            showHoaDonSuccess("Thành công", "Xóa hóa đơn thành công.");
            taiLaiDuLieu();
            xoaRongForm();

            dongBoDuLieuKhachHangVaSanPham();
        } catch (Exception ex) {
            showHoaDonError("Lỗi", layThongBaoLoi(ex));
        }
    }

    private void xuLyTraHang() {
        if (chiDuocXemHoaDon()) {
            showHoaDonWarning("Không đủ quyền",
                    "Kế toán chỉ được xem hóa đơn, không được thao tác thêm/sửa/xóa/thanh toán/trả hàng.");
            return;
        }

        HoaDon hd = layHoaDonDangChon();
        if (hd == null) {
            showHoaDonWarning("Thông báo", "Vui lòng chọn hóa đơn cần trả hàng.");
            return;
        }

        if (HoaDon.TRANG_THAI_HUY.equals(hd.getTrangThai()) || HoaDon.TRANG_THAI_HET_HANG.equals(hd.getTrangThai())) {
            showHoaDonWarning("Không thể trả hàng", "Không thể trả hàng hóa đơn này.");
            return;
        }

        if (HoaDon.TRANG_THAI_CHO.equals(hd.getTrangThai())) {
            showHoaDonWarning("Không thể trả hàng", "Chỉ có thể trả hàng với hóa đơn đã thanh toán.");
            return;
        }

        if (!HoaDon.TRANG_THAI_DA_THANH.equals(hd.getTrangThai())
                && !HoaDon.TRANG_THAI_TRA_MOT_PHAN.equals(hd.getTrangThai())) {
            showHoaDonWarning("Không thể trả hàng", "Chỉ có thể trả hàng với hóa đơn đã thanh toán.");
            return;
        }

        try {
            List<HoaDon.HangTraHang> dsHangTra = HoaDon.layDanhSachHangCoTheTraSQL(hd.getMaHoaDon());
            if (dsHangTra.isEmpty()) {
                showHoaDonWarning("Không thể trả hàng", "Hóa đơn này không còn sản phẩm nào có thể trả.");
                return;
            }

            TraHangFormData data = showTraHangDialog(hd, dsHangTra);
            if (data == null) {
                return;
            }

            HoaDon.traHangMotPhanSQL(
                    hd.getMaHoaDon(),
                    data.hangTra.getMaSanPham(),
                    data.hangTra.getKichCo(),
                    data.soLuongTra,
                    data.lyDo,
                    data.congLaiTonKho);

            showHoaDonSuccess("Thành công",
                    "Trả hàng một phần thành công.\nDoanh thu và tồn kho đã được cập nhật theo lý do trả hàng.");
            taiLaiDuLieu();
            chonHoaDonTrongBang(hd.getMaHoaDon());
            dongBoDuLieuKhachHangVaSanPham();

        } catch (Exception ex) {
            showHoaDonError("Lỗi trả hàng", layThongBaoLoi(ex));
        }
    }

    private TraHangFormData showTraHangDialog(HoaDon hd, List<HoaDon.HangTraHang> dsHangTra) {
        final TraHangFormData[] result = { null };

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, "Trả hàng một phần", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(880, 520);
        dialog.setMinimumSize(new Dimension(820, 500));
        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(22, 28, 20, 28));

        JLabel title = new JLabel("TRẢ HÀNG MỘT PHẦN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(6, 26, 58));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                new EmptyBorder(16, 28, 16, 28)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblMaHD = taoTraHangLabel("Mã hóa đơn:");
        JLabel valMaHD = new JLabel(hd.getMaHoaDon());
        valMaHD.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        valMaHD.setForeground(Color.BLACK);

        JComboBox<HoaDon.HangTraHang> cboSanPham = new JComboBox<>(dsHangTra.toArray(new HoaDon.HangTraHang[0]));
        cboSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cboSanPham.setBackground(Color.WHITE);
        cboSanPham.setForeground(Color.BLACK);
        cboSanPham.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof HoaDon.HangTraHang item) {
                    setText(item.toString());
                }
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                return this;
            }
        });

        JTextField txtSoLuongTra = new JTextField("1");
        txtSoLuongTra.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        txtSoLuongTra.setForeground(Color.BLACK);
        txtSoLuongTra.setBackground(Color.WHITE);
        txtSoLuongTra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(170, 178, 190), 1),
                new EmptyBorder(7, 10, 7, 10)));

        JRadioButton rdoDoiSize = taoTraHangRadio("Khách muốn đổi size", true);
        JRadioButton rdoNhuCauKhac = taoTraHangRadio("Khách thay đổi nhu cầu sử dụng", false);
        JRadioButton rdoLoi = taoTraHangRadio("Sản phẩm bị lỗi", false);
        ButtonGroup group = new ButtonGroup();
        group.add(rdoDoiSize);
        group.add(rdoNhuCauKhac);
        group.add(rdoLoi);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        form.add(lblMaHD, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        form.add(valMaHD, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        form.add(taoTraHangLabel("Sản phẩm trả:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        form.add(cboSanPham, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        form.add(taoTraHangLabel("Số lượng trả:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        form.add(txtSoLuongTra, gbc);

        JPanel reasonPanel = new JPanel();
        reasonPanel.setOpaque(false);
        reasonPanel.setLayout(new BoxLayout(reasonPanel, BoxLayout.Y_AXIS));
        reasonPanel.add(rdoDoiSize);
        reasonPanel.add(Box.createVerticalStrut(8));
        reasonPanel.add(rdoNhuCauKhac);
        reasonPanel.add(Box.createVerticalStrut(8));
        reasonPanel.add(rdoLoi);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(taoTraHangLabel("Lý do:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(reasonPanel, gbc);

        JLabel note = new JLabel(
                "Chỉ đổi/trả trong 10 ngày kể từ khi khách mua hàng. Sản phẩm bị lỗi sẽ không cộng lại tồn kho.");
        note.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        note.setForeground(new Color(148, 80, 38));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(note, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        footer.setOpaque(false);
        JButton btnDong = taoNut("Đóng", Color.WHITE, Color.BLACK);
        JButton btnXacNhan = taoNut("Xác nhận trả hàng", new Color(214, 173, 103), Color.BLACK);
        btnDong.setPreferredSize(new Dimension(210, 42));
        btnXacNhan.setPreferredSize(new Dimension(230, 42));
        footer.add(btnDong);
        footer.add(btnXacNhan);
        root.add(footer, BorderLayout.SOUTH);

        btnDong.addActionListener(e -> dialog.dispose());
        btnXacNhan.addActionListener(e -> {
            Object selected = cboSanPham.getSelectedItem();
            if (!(selected instanceof HoaDon.HangTraHang hangTra)) {
                showHoaDonWarning("Lỗi nhập liệu", "Vui lòng chọn sản phẩm cần trả.");
                return;
            }

            int soLuong;
            try {
                soLuong = Integer.parseInt(txtSoLuongTra.getText().trim());
            } catch (Exception ex) {
                showHoaDonWarning("Lỗi nhập liệu", "Số lượng trả phải là số nguyên dương.");
                return;
            }

            if (soLuong <= 0 || soLuong > hangTra.getSoLuongConLai()) {
                showHoaDonWarning("Lỗi nhập liệu", "Số lượng trả phải từ 1 đến " + hangTra.getSoLuongConLai() + ".");
                return;
            }

            String lyDo;
            boolean congLaiTon;
            if (rdoDoiSize.isSelected()) {
                lyDo = "Khách muốn đổi size";
                congLaiTon = true;
            } else if (rdoNhuCauKhac.isSelected()) {
                lyDo = "Khách thay đổi nhu cầu sử dụng";
                congLaiTon = true;
            } else {
                lyDo = "Sản phẩm bị lỗi";
                congLaiTon = false;
            }

            result[0] = new TraHangFormData(hangTra, soLuong, lyDo, congLaiTon);
            dialog.dispose();
        });

        dialog.setContentPane(root);
        SwingUtilities.invokeLater(txtSoLuongTra::requestFocusInWindow);
        dialog.setVisible(true);
        return result[0];
    }

    private JLabel taoTraHangLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        label.setForeground(Color.BLACK);
        return label;
    }

    private JRadioButton taoTraHangRadio(String text, boolean selected) {
        JRadioButton radio = new JRadioButton(text, selected);
        radio.setOpaque(false);
        radio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        radio.setForeground(Color.BLACK);
        radio.setFocusPainted(false);
        return radio;
    }

    private boolean khongChoSuaHoaDonDaThanhToan() {
        HoaDon hd = layHoaDonDangChon();
        if (hd != null && HoaDon.TRANG_THAI_DA_THANH.equals(hd.getTrangThai())) {
            showHoaDonWarning("Không thể sửa", "Không thể sửa hóa đơn đã thanh toán");
            return true;
        }
        return false;
    }

    private HoaDon taoHoaDonTuForm(boolean dungMaDangChon) {
        napThongTinKhachHang();

        if (maKhachHangHienTai == null || maKhachHangHienTai.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập hoặc tìm SĐT khách hàng.");
        }

        if (gioHang.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng thêm ít nhất một sản phẩm vào hóa đơn.");
        }

        HoaDon hd;
        if (dungMaDangChon) {
            HoaDon selected = layHoaDonDangChon();
            if (selected == null)
                throw new IllegalArgumentException("Chưa chọn hóa đơn cần sửa.");
            capNhatTenKhachHangNeuCan();
            hd = new HoaDon(selected.getMaHoaDon(), maKhachHangHienTai, txtMaNhanVien.getText(),
                    selected.getNgayLap(), 0, HoaDon.TRANG_THAI_CHO, txtGhiChu.getText());
        } else {
            capNhatTenKhachHangNeuCan();
            hd = new HoaDon();
            hd.setMaKhachHang(maKhachHangHienTai);
            hd.setMaNhanVien(txtMaNhanVien.getText());
        }

        for (CartItem item : gioHang.values()) {
            // Lưu đơn giá gốc của sản phẩm vào chi tiết hóa đơn.
            // Phần giảm giá bán (%) được cộng vào cột giảm giá của hóa đơn.
            hd.themSanPham(item.maSanPham, item.kichCo, item.soLuong, item.giaBanGoc);
        }

        hd.setGiamGia(tinhGiamGiaSanPhamTrongGio());
        hd.setGhiChu(txtGhiChu.getText());
        // Khi tạo/sửa hóa đơn chỉ lưu giảm giá sản phẩm.
        // Điểm tích lũy chỉ được hỏi và xử lý ở bước Xác nhận thanh toán.
        hd.tinhDiemDoiTamThoi(0, false);
        return hd;
    }

    private void napHoaDonLenForm(HoaDon hd) {
        txtMaKhachHang.setText(hd.getMaKhachHang());
        txtMaNhanVien.setText(hd.getMaNhanVien());
        txtGiamGia.setText(String.valueOf((long) hd.getGiamGia()));
        txtGhiChu.setText(hd.getGhiChu());
        lblTrangThai.setText("Trạng thái: " + hd.getTrangThai());
        napThongTinKhachHang(false);

        gioHang.clear();
        for (Map.Entry<String, double[]> e : hd.getChiTiet().entrySet()) {
            String maSP = HoaDon.layMaSanPhamTuKey(e.getKey());
            String kichCo = HoaDon.layKichCoTuKey(e.getKey());
            SanPhamInfo sp = timSanPhamChoHienThi(maSP, kichCo);
            String ten = sp == null ? maSP : sp.tenSanPham;
            double giaBanGoc = e.getValue()[1];
            double giamGiaPhanTram = sp == null ? 0 : sp.giamGiaBan;
            gioHang.put(taoCartKey(maSP, kichCo),
                    new CartItem(maSP, ten, kichCo, (int) e.getValue()[0], giaBanGoc, giamGiaPhanTram));
        }
        chkDungDiem.setSelected(false);
        capNhatBangChiTietTam();
        capNhatTongTien();
        // Nếu hóa đơn đã thanh toán thì hiển thị đúng điểm/tiền đổi điểm đã lưu trong
        // SQL.
        if (HoaDon.TRANG_THAI_DA_THANH.equals(hd.getTrangThai())
                || HoaDon.TRANG_THAI_TRA_MOT_PHAN.equals(hd.getTrangThai())) {
            lblDiemSuDung.setText("Điểm dùng: " + hd.getDiemSuDung() + " điểm");
            lblTienDoiDiem.setText("Tiền đổi điểm: " + dinhDangTien(hd.getTienDoiDiem()) + " VNĐ");
            lblThanhToan.setText("Thanh toán: " + dinhDangTien(hd.getThanhToanSauTraHang()) + " VNĐ");
        }
    }

    private boolean dinhDangTimKiemHoaDonHopLe(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }

        String key = keyword.trim();

        return key.matches("(?i)^HD-[A-Z0-9]+$")
                || key.matches("(?i)^NV-[A-Z0-9]+$")
                || key.matches("0[0-9]{9}")
                || key.matches("[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}");
    }

    private String layThongBaoLoiDinhDangTimKiem(String keyword) {
        String key = keyword == null ? "" : keyword.trim();

        if (key.matches("[0-9]+") || key.startsWith("0")) {
            return "SĐT gồm 10 số nguyên dương và bắt đầu bằng số 0.";
        }

        return "Từ khóa tìm kiếm không đúng định dạng.\nNhập mã HĐ, mã NV, SĐT hoặc ngày dd/MM/yyyy.";
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();

        if (keyword.isEmpty()) {
            showHoaDonWarning("Lỗi nhập liệu", "Vui lòng nhập từ khóa tìm kiếm");
            return;
        } else {
            if (!dinhDangTimKiemHoaDonHopLe(keyword)) {
                showHoaDonError(
                        "Lỗi nhập liệu",
                        layThongBaoLoiDinhDangTimKiem(keyword));
                return;
            }

            danhSachHoaDon = timHoaDonTheoTieuChi(keyword);
        }

        capNhatBangHoaDon();

        if (danhSachHoaDon.isEmpty()) {
            showHoaDonInfo(
                    "Không tìm thấy",
                    "Không có hóa đơn phù hợp với: " + keyword);
        }
    }

    private List<HoaDon> timHoaDonTheoTieuChi(String keyword) {
        List<HoaDon> ketQua = new ArrayList<>();

        String key = keyword == null ? "" : keyword.trim();
        if (key.isEmpty()) {
            return HoaDon.docTuSQL();
        }

        String sql = """
                SELECT DISTINCT hd.maHoaDon
                FROM HoaDon hd
                LEFT JOIN KhachHang kh ON hd.maKhachHang = kh.maDinhDanh
                WHERE hd.maHoaDon LIKE ?
                   OR hd.maNhanVien LIKE ?
                   OR kh.soDienThoai LIKE ?
                   OR CONVERT(VARCHAR(10), hd.ngayLap, 103) LIKE ?
                ORDER BY hd.maHoaDon
                """;

        java.util.Set<String> maHoaDonPhuHop = new java.util.LinkedHashSet<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String like = "%" + key + "%";
            ps.setString(1, like); // mã hóa đơn
            ps.setString(2, like); // mã nhân viên
            ps.setString(3, like); // SĐT khách hàng
            ps.setString(4, like); // ngày dd/MM/yyyy

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    maHoaDonPhuHop.add(rs.getString("maHoaDon"));
                }
            }

        } catch (Exception ex) {
            showHoaDonError(
                    "Lỗi tìm kiếm hóa đơn",
                    "Không thể tìm kiếm hóa đơn: " + layThongBaoLoi(ex));
            return ketQua;
        }

        if (maHoaDonPhuHop.isEmpty()) {
            return ketQua;
        }

        for (HoaDon hd : HoaDon.docTuSQL()) {
            if (hd != null && maHoaDonPhuHop.contains(hd.getMaHoaDon())) {
                ketQua.add(hd);
            }
        }

        return ketQua;
    }

    private void taiLaiDuLieu() {
        danhSachHoaDon = HoaDon.docTuSQL();
        capNhatBangHoaDon();
    }

    private void chonHoaDonTrongBang(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.isBlank())
            return;
        if (tableHoaDon == null || modelHoaDon == null)
            return;

        for (int i = 0; i < modelHoaDon.getRowCount(); i++) {
            Object value = modelHoaDon.getValueAt(i, 0);

            if (value != null && maHoaDon.equalsIgnoreCase(value.toString())) {
                tableHoaDon.setRowSelectionInterval(i, i);
                tableHoaDon.scrollRectToVisible(tableHoaDon.getCellRect(i, 0, true));

                maHoaDonDaChon = maHoaDon;

                HoaDon hd = layHoaDonDangChon();
                if (hd != null) {
                    napHoaDonLenForm(hd);
                }

                return;
            }
        }
    }

    private void chonDongChiTietTam(String key) {
        if (key == null || key.isBlank() || tableChiTietTam == null || modelChiTietTam == null)
            return;

        for (int i = 0; i < modelChiTietTam.getRowCount(); i++) {
            String maSP = String.valueOf(modelChiTietTam.getValueAt(i, 0));
            String kichCo = String.valueOf(modelChiTietTam.getValueAt(i, 2));
            if (key.equalsIgnoreCase(taoCartKey(maSP, kichCo))) {
                int viewRow = tableChiTietTam.convertRowIndexToView(i);
                tableChiTietTam.setRowSelectionInterval(viewRow, viewRow);
                tableChiTietTam.scrollRectToVisible(tableChiTietTam.getCellRect(viewRow, 0, true));
                return;
            }
        }
    }

    private void luuHoaDonChoDangChonXuongSQL() {
        HoaDon selected = layHoaDonDangChon();

        // Nếu đang tạo hóa đơn mới chưa lưu thì chỉ cập nhật giỏ hàng tạm trên giao
        // diện.
        if (selected == null) {
            return;
        }

        if (!HoaDon.TRANG_THAI_CHO.equals(selected.getTrangThai())) {
            return;
        }

        if (gioHang.isEmpty()) {
            throw new IllegalStateException("Hóa đơn phải có ít nhất một sản phẩm.");
        }

        String maHoaDonDangSua = selected.getMaHoaDon();

        HoaDon hdCapNhat = taoHoaDonTuForm(true);
        hdCapNhat.capNhatSQL();

        danhSachHoaDon = HoaDon.docTuSQL();
        capNhatBangHoaDon();
        chonHoaDonTrongBang(maHoaDonDangSua);
        dongBoDuLieuKhachHangVaSanPham();
    }

    private void dongBoDuLieuKhachHangVaSanPham() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window == null) {
            return;
        }

        lamMoiPanelCon(window);
        if (window instanceof MainFrame) {
            ((MainFrame) window).lamMoiSauKhiHoaDonThayDoi();
        }
    }

    private void lamMoiPanelCon(Component component) {
        if (component == null) {
            return;
        }

        if (component instanceof KhachHangPanel) {
            ((KhachHangPanel) component).lamMoiDuLieuTuSQL();
        }

        if (component instanceof SanPhamPanel) {
            ((SanPhamPanel) component).lamMoiDuLieuTuSQL();
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                lamMoiPanelCon(child);
            }
        }
    }

    private void xuLyThemHoacCapNhatSanPham() {

        if (chiDuocXemHoaDon()) {
            showHoaDonWarning("Không đủ quyền",
                    "Kế toán chỉ được xem hóa đơn, không được thêm hoặc xóa sản phẩm trong hóa đơn.");
            return;
        }

        if (khongChoSuaHoaDonDaThanhToan()) {
            return;
        }

        String maSPTrenForm = txtMaSanPham.getText() == null ? "" : txtMaSanPham.getText().trim();
        String kichCoTrenForm = txtKichCo.getText() == null ? "" : txtKichCo.getText().trim();

        if (!maSPTrenForm.isEmpty() && !kichCoTrenForm.isEmpty()) {
            String keyTrenForm = taoCartKey(maSPTrenForm, kichCoTrenForm);

            if (keyChiTietDangSua != null
                    && keyChiTietDangSua.equalsIgnoreCase(keyTrenForm)
                    && gioHang.containsKey(keyTrenForm)) {
                capNhatSoLuongTheoKeyDangSua(keyTrenForm);
                return;
            }

            int row = tableChiTietTam == null ? -1 : tableChiTietTam.getSelectedRow();
            if (row >= 0) {
                int modelRow = tableChiTietTam.convertRowIndexToModel(row);
                String maSPTrongBang = modelChiTietTam.getValueAt(modelRow, 0).toString();
                String kichCoTrongBang = modelChiTietTam.getValueAt(modelRow, 2).toString();
                String keyTrongBang = taoCartKey(maSPTrongBang, kichCoTrongBang);

                if (keyTrongBang.equalsIgnoreCase(keyTrenForm) && gioHang.containsKey(keyTrenForm)) {
                    keyChiTietDangSua = keyTrenForm;
                    capNhatSoLuongTheoKeyDangSua(keyTrenForm);
                    return;
                }
            }
        }

        themSanPhamVaoGio();
    }

    private void themSanPhamVaoGio() {
        if (khongChoSuaHoaDonDaThanhToan()) {
            return;
        }

        String maHoaDonDangSua = maHoaDonDaChon;

        try {
            SanPhamInfo sp = napThongTinSanPham();
            if (sp == null)
                return;
            int soLuong = docSoLuong();
            String key = taoCartKey(sp.maSanPham, sp.kichCo);

            int daCo = gioHang.containsKey(key) ? gioHang.get(key).soLuong : 0;
            if (soLuong + daCo > sp.soLuongTon) {
                throw new IllegalArgumentException(
                        "Số lượng vượt quá tồn kho size " + sp.kichCo + ". Tồn hiện tại: " + sp.soLuongTon);
            }

            if (gioHang.containsKey(key)) {
                CartItem item = gioHang.get(key);
                item.soLuong += soLuong;
                item.giaBanGoc = sp.giaBan;
                item.giamGiaPhanTram = sp.giamGiaBan;
            } else {
                gioHang.put(key,
                        new CartItem(sp.maSanPham, sp.tenSanPham, sp.kichCo, soLuong, sp.giaBan, sp.giamGiaBan));
            }

            capNhatBangChiTietTam();
            capNhatTongTien();

            luuHoaDonChoDangChonXuongSQL();

            keyChiTietDangSua = null;
            txtMaSanPham.setText("");
            txtTenSanPham.setText("");
            txtKichCo.setText("");
            txtSoLuong.setText("1");
            txtDonGia.setText("0");
            if (txtGiamGiaSanPham != null)
                txtGiamGiaSanPham.setText("0");
            txtTonKho.setText("0");
            txtMaSanPham.requestFocus();

        } catch (Exception ex) {
            if (maHoaDonDangSua != null && !maHoaDonDangSua.isBlank()) {
                taiLaiDuLieu();
                chonHoaDonTrongBang(maHoaDonDangSua);
            }
            showHoaDonError("Lỗi thêm sản phẩm", layThongBaoLoi(ex));
        }
    }

    private void capNhatSoLuongDongDangChon() {
        int row = tableChiTietTam == null ? -1 : tableChiTietTam.getSelectedRow();
        if (row < 0) {
            showHoaDonWarning("Thông báo", "Vui lòng chọn sản phẩm cần cập nhật.");
            return;
        }

        int modelRow = tableChiTietTam.convertRowIndexToModel(row);
        String maSP = modelChiTietTam.getValueAt(modelRow, 0).toString();
        String kichCo = modelChiTietTam.getValueAt(modelRow, 2).toString();
        capNhatSoLuongTheoKeyDangSua(taoCartKey(maSP, kichCo));
    }

    private void capNhatSoLuongTheoKeyDangSua(String key) {
        if (khongChoSuaHoaDonDaThanhToan()) {
            return;
        }

        String maHoaDonDangSua = maHoaDonDaChon;

        try {
            if (key == null || key.isBlank() || !gioHang.containsKey(key)) {
                showHoaDonWarning("Thông báo", "Vui lòng chọn sản phẩm cần cập nhật.");
                return;
            }

            String maSP = HoaDon.layMaSanPhamTuKey(key);
            String kichCo = HoaDon.layKichCoTuKey(key);
            int soLuong = docSoLuong();

            SanPhamInfo sp = timSanPham(maSP, kichCo, false);
            CartItem itemDangSua = gioHang.get(key);
            int soLuongCu = itemDangSua == null ? 0 : itemDangSua.soLuong;
            int tonCoTheDung = (sp == null ? soLuong : sp.soLuongTon + soLuongCu);

            if (soLuong > tonCoTheDung) {
                throw new IllegalArgumentException("Số lượng vượt quá tồn kho. Tồn có thể dùng: " + tonCoTheDung);
            }

            itemDangSua.soLuong = soLuong;
            if (sp != null) {
                itemDangSua.giaBanGoc = sp.giaBan;
                itemDangSua.giamGiaPhanTram = sp.giamGiaBan;
            }

            capNhatBangChiTietTam();
            chonDongChiTietTam(key);
            capNhatTongTien();

            luuHoaDonChoDangChonXuongSQL();

            keyChiTietDangSua = null;
            tableChiTietTam.clearSelection();
            txtMaSanPham.setText("");
            txtTenSanPham.setText("");
            txtKichCo.setText("");
            txtSoLuong.setText("1");
            txtDonGia.setText("0");
            if (txtGiamGiaSanPham != null)
                txtGiamGiaSanPham.setText("0");
            txtTonKho.setText("0");

        } catch (Exception ex) {
            if (maHoaDonDangSua != null && !maHoaDonDangSua.isBlank()) {
                taiLaiDuLieu();
                chonHoaDonTrongBang(maHoaDonDangSua);
            }
            showHoaDonError("Lỗi cập nhật", layThongBaoLoi(ex));
        }
    }

    private void xoaSanPhamKhoiGio() {

        if (chiDuocXemHoaDon()) {
            showHoaDonWarning("Không đủ quyền",
                    "Kế toán chỉ được xem hóa đơn, không được thêm hoặc xóa sản phẩm trong hóa đơn.");
            return;
        }

        if (khongChoSuaHoaDonDaThanhToan()) {
            return;
        }

        int row = tableChiTietTam.getSelectedRow();
        if (row < 0) {
            showHoaDonWarning("Thông báo", "Vui lòng chọn sản phẩm cần xóa khỏi hóa đơn.");
            return;
        }

        String maHoaDonDangSua = maHoaDonDaChon;

        try {
            int modelRow = tableChiTietTam.convertRowIndexToModel(row);
            String maSP = modelChiTietTam.getValueAt(modelRow, 0).toString();
            String kichCo = modelChiTietTam.getValueAt(modelRow, 2).toString();
            String key = taoCartKey(maSP, kichCo);

            if (gioHang.size() <= 1) {
                showHoaDonWarning("Không thể xóa",
                        "Hóa đơn phải còn ít nhất một sản phẩm. Nếu không bán nữa, hãy dùng chức năng Xóa hóa đơn khi hóa đơn còn chờ thanh toán.");
                return;
            }

            gioHang.remove(key);
            keyChiTietDangSua = null;

            capNhatBangChiTietTam();
            capNhatTongTien();

            luuHoaDonChoDangChonXuongSQL();

        } catch (Exception ex) {
            if (maHoaDonDangSua != null && !maHoaDonDangSua.isBlank()) {
                taiLaiDuLieu();
                chonHoaDonTrongBang(maHoaDonDangSua);
            }
            showHoaDonError("Lỗi xóa sản phẩm", layThongBaoLoi(ex));
        }
    }

    private void napDongChiTietLenForm() {
        int row = tableChiTietTam.getSelectedRow();
        if (row < 0)
            return;

        int modelRow = tableChiTietTam.convertRowIndexToModel(row);
        String maSP = modelChiTietTam.getValueAt(modelRow, 0).toString();
        String kichCo = modelChiTietTam.getValueAt(modelRow, 2).toString();
        String key = taoCartKey(maSP, kichCo);

        CartItem item = gioHang.get(key);
        if (item == null)
            return;

        keyChiTietDangSua = key;

        txtMaSanPham.setText(item.maSanPham);
        txtTenSanPham.setText(item.tenSanPham);
        txtKichCo.setText(item.kichCo);
        txtSoLuong.setText(String.valueOf(item.soLuong));
        txtDonGia.setText(String.valueOf((long) item.giaBanGoc));

        SanPhamInfo sp = timSanPhamChoHienThi(item.maSanPham, item.kichCo);
        if (txtGiamGiaSanPham != null)
            txtGiamGiaSanPham.setText(sp == null ? "0" : String.valueOf(sp.giamGiaBan));
        txtTonKho.setText(sp == null ? "" : String.valueOf(sp.soLuongTon));
    }

    private void xuLyTimSanPham() {
        try {
            SanPhamInfo sp = napThongTinSanPham();
            if (sp == null)
                return;
            String key = taoCartKey(sp.maSanPham, sp.kichCo);

            if (keyChiTietDangSua != null && !keyChiTietDangSua.equalsIgnoreCase(key)) {
                keyChiTietDangSua = null;
                if (tableChiTietTam != null)
                    tableChiTietTam.clearSelection();
            }

        } catch (IllegalArgumentException ex) {
            showHoaDonError("Lỗi nhập liệu", ex.getMessage());
        } catch (Exception ex) {
            showHoaDonError("Lỗi", "Không thể tìm sản phẩm: " + layThongBaoLoi(ex));
        }
    }

    private void showHoaDonSuccess(String title, String message) {
        showHoaDonMessage(title, message, new Color(22, 101, 52), false, null);
    }

    private void showHoaDonInfo(String title, String message) {
        showHoaDonMessage(title, message, new Color(18, 59, 140), false, null);
    }

    private void showHoaDonWarning(String title, String message) {
        showHoaDonMessage(title, message, new Color(150, 40, 40), false, null);
    }

    private void showHoaDonError(String title, String message) {
        showHoaDonMessage(title, message, new Color(150, 40, 40), false, null);
    }

    private boolean showHoaDonConfirm(String title, String message) {
        final boolean[] result = { false };
        showHoaDonMessage(title, message, new Color(18, 59, 140), true, () -> result[0] = true);
        return result[0];
    }

    private void showHoaDonMessage(String title, String message, Color titleColor, boolean confirm, Runnable onOk) {

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(confirm ? 660 : 620, confirm ? 330 : 310);
        dialog.setResizable(false);

        JPanel root = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                LinearGradientPaint bg = new LinearGradientPaint(
                        0, 0, w, h,
                        new float[] { 0f, 0.45f, 1f },
                        new Color[] {
                                new Color(248, 252, 255),
                                new Color(220, 237, 255),
                                new Color(176, 211, 249)
                        });
                g2.setPaint(bg);
                g2.fillRect(0, 0, w, h);

                g2.setStroke(new BasicStroke(1.0f));
                g2.setColor(new Color(50, 90, 145, 45));
                for (int i = -h; i < w + h; i += 180) {
                    g2.drawLine(i, h, i + h, 0);
                }

                g2.setColor(new Color(77, 138, 202, 24));
                for (int i = 0; i < 5; i++) {
                    int y = (int) (h * 0.58 + i * 28);
                    java.awt.geom.Path2D.Double curve = new java.awt.geom.Path2D.Double();
                    curve.moveTo(-30, y);
                    curve.curveTo(w * 0.25, y - 26, w * 0.55, y + 32, w + 40, y - 8);
                    g2.draw(curve);
                }

                g2.dispose();
            }
        };
        root.setBorder(new EmptyBorder(18, 30, 24, 30));

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setPreferredSize(new Dimension(560, confirm ? 235 : 215));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(titleColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setPreferredSize(new Dimension(560, 45));
        titleLabel.setMaximumSize(new Dimension(560, 45));

        JPanel messageBox = new JPanel();
        messageBox.setOpaque(false);
        messageBox.setLayout(new BoxLayout(messageBox, BoxLayout.Y_AXIS));
        messageBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageBox.setPreferredSize(new Dimension(560, 78));
        messageBox.setMaximumSize(new Dimension(560, 78));

        java.util.List<String> lines = tachDongThongBao(message, 58);
        int lineHeight = lines.size() <= 1 ? 34 : 27;

        messageBox.add(Box.createVerticalGlue());
        for (String line : lines) {
            JLabel lineLabel = new JLabel(line, SwingConstants.CENTER);
            lineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            lineLabel.setForeground(new Color(11, 23, 54));
            lineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            lineLabel.setHorizontalAlignment(SwingConstants.CENTER);
            lineLabel.setPreferredSize(new Dimension(560, lineHeight));
            lineLabel.setMaximumSize(new Dimension(560, lineHeight));
            messageBox.add(lineLabel);
        }
        messageBox.add(Box.createVerticalGlue());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttons.setPreferredSize(new Dimension(560, 44));
        buttons.setMaximumSize(new Dimension(560, 44));

        if (confirm) {
            JButton cancel = createHoaDonPopupButton("Hủy", false);
            cancel.addActionListener(e -> dialog.dispose());
            buttons.add(cancel);
        }

        JButton ok = createHoaDonPopupButton(confirm ? "Đồng ý" : "Đóng", true);
        ok.addActionListener(e -> {
            if (onOk != null) {
                onOk.run();
            }
            dialog.dispose();
        });
        buttons.add(ok);

        box.add(Box.createVerticalGlue());
        box.add(titleLabel);
        box.add(Box.createVerticalStrut(14));
        box.add(messageBox);
        box.add(Box.createVerticalStrut(14));
        box.add(buttons);
        box.add(Box.createVerticalGlue());

        root.add(box, new GridBagConstraints());

        dialog.setContentPane(root);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private java.util.List<String> tachDongThongBao(String message, int maxChars) {
        java.util.List<String> result = new ArrayList<>();
        if (message == null || message.trim().isEmpty()) {
            result.add("");
            return result;
        }

        String[] rawLines = message.split("\\n");
        for (String raw : rawLines) {
            String line = raw.trim();
            if (line.length() <= maxChars) {
                result.add(line);
                continue;
            }

            StringBuilder current = new StringBuilder();
            String[] words = line.split("\\s+");
            for (String word : words) {
                if (current.length() == 0) {
                    current.append(word);
                } else if (current.length() + 1 + word.length() <= maxChars) {
                    current.append(" ").append(word);
                } else {
                    result.add(current.toString());
                    current.setLength(0);
                    current.append(word);
                }
            }

            if (current.length() > 0) {
                result.add(current.toString());
            }
        }

        if (result.isEmpty()) {
            result.add("");
        }

        return result;
    }

    private void datPrefixTenKhachHangMoi(String maKH, String tenDaNhap) {
        if (maKH == null || maKH.isBlank()) {
            boKhoaPrefixTenKhachHang();
            return;
        }

        prefixTenKhachHangMoi = maKH.trim().toUpperCase() + " - ";
        final String fixedPrefix = prefixTenKhachHangMoi;

        javax.swing.text.AbstractDocument doc = (javax.swing.text.AbstractDocument) txtTenKhachHang.getDocument();

        doc.setDocumentFilter(null);

        String ten = tenDaNhap == null ? "" : tenDaNhap.trim();
        if (ten.startsWith(fixedPrefix)) {
            ten = ten.substring(fixedPrefix.length()).trim();
        }

        txtTenKhachHang.setText(fixedPrefix + ten);
        txtTenKhachHang.setEditable(true);
        txtTenKhachHang.setCaretPosition(txtTenKhachHang.getText().length());

        doc.setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr)
                    throws javax.swing.text.BadLocationException {
                if (string == null)
                    return;
                if (offset < fixedPrefix.length())
                    offset = fixedPrefix.length();
                super.insertString(fb, offset, string, attr);
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length)
                    throws javax.swing.text.BadLocationException {
                if (offset < fixedPrefix.length()) {
                    int end = offset + length;
                    if (end <= fixedPrefix.length())
                        return;
                    length = end - fixedPrefix.length();
                    offset = fixedPrefix.length();
                }
                if (length > 0)
                    super.remove(fb, offset, length);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text,
                    javax.swing.text.AttributeSet attrs)
                    throws javax.swing.text.BadLocationException {
                if (text == null)
                    text = "";
                if (offset < fixedPrefix.length()) {
                    int end = offset + length;
                    length = Math.max(0, end - fixedPrefix.length());
                    offset = fixedPrefix.length();
                    if (end <= fixedPrefix.length() && text.isEmpty())
                        return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });
    }

    private void boKhoaPrefixTenKhachHang() {
        prefixTenKhachHangMoi = "";
        if (txtTenKhachHang != null && txtTenKhachHang.getDocument() instanceof javax.swing.text.AbstractDocument) {
            ((javax.swing.text.AbstractDocument) txtTenKhachHang.getDocument()).setDocumentFilter(null);
        }
    }

    private JButton createHoaDonPopupButton(String text, boolean primary) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (primary) {
                    GradientPaint gp = new GradientPaint(
                            0, 0, new Color(36, 86, 205),
                            getWidth(), getHeight(), new Color(3, 24, 64));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(new Color(70, 135, 255, 170));
                } else {
                    g2.setColor(new Color(248, 250, 252));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(new Color(11, 23, 54));
                }

                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(primary ? Color.WHITE : new Color(11, 23, 54));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(primary ? 145 : 125, 42));
        btn.setMinimumSize(new Dimension(primary ? 145 : 125, 42));
        btn.setMaximumSize(new Dimension(primary ? 145 : 125, 42));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private String safeHtml(String message) {
        return (message == null ? "" : message)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
    }

    private SanPhamInfo napThongTinSanPham() {
        SanPhamInfo sp = timSanPham(txtMaSanPham.getText().trim(), txtKichCo == null ? "" : txtKichCo.getText().trim(),
                true);
        if (sp == null) {
            if (daHuyChonKichCoSanPham)
                return null;
            throw new IllegalArgumentException("Mã sản phẩm/kích cỡ không tồn tại.");
        }
        txtMaSanPham.setText(sp.maSanPham);
        txtTenSanPham.setText(sp.tenSanPham);
        txtKichCo.setText(sp.kichCo);
        txtDonGia.setText(String.valueOf((long) sp.giaBan));
        if (txtGiamGiaSanPham != null)
            txtGiamGiaSanPham.setText(String.valueOf(sp.giamGiaBan));
        txtTonKho.setText(String.valueOf(sp.soLuongTon));
        return sp;
    }

    private void xuLyTimKhachHang() {
        try {
            napThongTinKhachHang(true);
        } catch (IllegalArgumentException ex) {
            showHoaDonError("Lỗi nhập liệu", ex.getMessage());
        } catch (Exception ex) {
            showHoaDonError("Lỗi", "Không thể tìm khách hàng: " + ex.getMessage());
        }
    }

    private void napThongTinKhachHang() {
        napThongTinKhachHang(true);
    }

    private void napThongTinKhachHang(boolean baoLoi) {
        String key = txtMaKhachHang.getText().trim();

        if (key.isEmpty()) {
            if (baoLoi) {
                throw new IllegalArgumentException("SĐT khách hàng không được để trống.");
            }
            return;
        }

        boolean nhapBangMaKhachHang = key.toUpperCase().startsWith("KH-");

        if (!nhapBangMaKhachHang && !laSoDienThoaiHopLe(key)) {
            throw new IllegalArgumentException("SĐT gồm 10 số nguyên dương và bắt đầu bằng số 0.");
        }

        KhachHangInfo kh = timKhachHang(key);

        if (kh != null) {
            maKhachHangHienTai = kh.maKhachHang;
            khachHangMoiDangNhap = false;

            if (kh.soDienThoai != null && !kh.soDienThoai.isBlank()) {
                txtMaKhachHang.setText(kh.soDienThoai);
            }

            boKhoaPrefixTenKhachHang();

            String tenCu = kh.hoTen == null ? "" : kh.hoTen.trim();
            if (tenCu.isBlank()) {
                tenCu = "(chưa có tên)";
            }

            txtTenKhachHang.setText(kh.maKhachHang + " - " + tenCu);
            txtTenKhachHang.setEditable(false);
            txtTenKhachHang.setToolTipText(null);

            txtDiemHienCo.setText(String.valueOf(kh.diemTichLuy));

        } else {

            String tenDangNhap = layTenKhachHangTuField();

            if (!khachHangMoiDangNhap || maKhachHangHienTai == null || maKhachHangHienTai.isBlank()) {
                maKhachHangHienTai = taoMaKhachHangTuDong();
            }

            khachHangMoiDangNhap = true;

            txtMaKhachHang.setText(key);
            datPrefixTenKhachHangMoi(maKhachHangHienTai, tenDangNhap);
            txtTenKhachHang.setToolTipText("Mã KH mới: " + maKhachHangHienTai);
            txtDiemHienCo.setText("0");
        }

        // Không cho chọn điểm ngay trên form. Hệ thống sẽ hỏi lại khi thanh toán.
        if (chkDungDiem != null) {
            chkDungDiem.setSelected(false);
            chkDungDiem.setEnabled(false);
        }

        capNhatTongTien();
    }

    private void capNhatBangChiTietTam() {
        if (modelChiTietTam == null)
            return;
        modelChiTietTam.setRowCount(0);
        for (CartItem item : gioHang.values()) {
            modelChiTietTam.addRow(new Object[] {
                    item.maSanPham,
                    item.tenSanPham,
                    item.kichCo,
                    item.soLuong,
                    dinhDangTien(item.giaBanGoc),
                    dinhDangTien(item.tienGiamGia()),
                    dinhDangTien(item.thanhTienSauGiam())
            });
        }
        datDoRongCotChiTiet();
    }

    private void capNhatTongTien() {
        if (lblTongTien == null)
            return;
        double tong = 0;
        for (CartItem item : gioHang.values())
            tong += item.tongTienGoc();
        double giamGiaSanPham = tinhGiamGiaSanPhamTrongGio();
        int diemSuDung = 0;
        double tienSauGiamSanPham = Math.max(0, tong - giamGiaSanPham);
        double tienDoiDiem = 0;
        double thanhToan = HoaDon.lamTronTienThanhToan(Math.max(0, tienSauGiamSanPham));

        lblTongTien.setText("Tổng tiền: " + dinhDangTien(tong) + " VNĐ");
        lblGiamGia.setText("Giảm giá SP: " + dinhDangTien(giamGiaSanPham) + " VNĐ");
        lblDiemSuDung.setText("Điểm dùng: " + diemSuDung + " điểm");
        lblTienDoiDiem.setText("Tiền đổi điểm: " + dinhDangTien(tienDoiDiem) + " VNĐ");
        lblThanhToan.setText("Thanh toán: " + dinhDangTien(thanhToan) + " VNĐ");
    }

    private double tinhGiamGiaSanPhamTrongGio() {
        double tongGiam = 0;
        for (CartItem item : gioHang.values()) {
            tongGiam += item.tienGiamGia();
        }
        return tongGiam;
    }

    private void capNhatBangHoaDon() {
        if (modelHoaDon == null)
            return;
        modelHoaDon.setRowCount(0);
        for (HoaDon hd : danhSachHoaDon) {
            if (!phuHopTrangThaiLoc(hd))
                continue;
            modelHoaDon.addRow(new Object[] {
                    hd.getMaHoaDon(),
                    hd.getMaKhachHang(),
                    hd.getMaNhanVien(),
                    hd.getNgayLapText(),
                    dinhDangTien(hd.getTongTien()),
                    dinhDangTien(hd.getGiamGia()),
                    dinhDangTien(hd.getTienDoiDiem()),
                    dinhDangTien(hd.getThanhToan()),
                    dinhDangTien(hd.getThanhToanSauTraHang()),
                    hd.getTrangThai(),
                    hd.getGhiChu()
            });
        }
        datDoRongCotHoaDon();
        capNhatSoKetQuaHoaDon();
    }

    private void xoaRongForm() {
        maHoaDonDaChon = null;
        keyChiTietDangSua = null;
        if (tableHoaDon != null)
            tableHoaDon.clearSelection();
        if (tableChiTietTam != null)
            tableChiTietTam.clearSelection();
        gioHang.clear();
        capNhatBangChiTietTam();
        maKhachHangHienTai = null;
        khachHangMoiDangNhap = false;
        boKhoaPrefixTenKhachHang();
        txtMaKhachHang.setText("");
        txtTenKhachHang.setText("");
        txtTenKhachHang.setEditable(true);
        txtDiemHienCo.setText("0");
        chkDungDiem.setSelected(false);
        chkDungDiem.setEnabled(false);
        txtMaNhanVien.setText(defaultMaNhanVien());
        txtGiamGia.setText("0");
        txtGhiChu.setText("");
        txtMaSanPham.setText("");
        txtTenSanPham.setText("");
        txtKichCo.setText("");
        txtSoLuong.setText("1");
        txtDonGia.setText("0");
        txtTonKho.setText("0");
        lblTrangThai.setText("Trạng thái: Chờ thanh toán");
        capNhatTongTien();
        txtMaKhachHang.requestFocus();
    }

    private HoaDon layHoaDonDangChon() {
        if (maHoaDonDaChon == null)
            return null;
        for (HoaDon hd : danhSachHoaDon) {
            if (hd.getMaHoaDon().equals(maHoaDonDaChon))
                return hd;
        }
        return null;
    }

    private boolean laSoDienThoaiHopLe(String sdt) {
        return sdt != null && sdt.trim().matches("0[0-9]{9}");
    }

    private String taoMaKhachHangTuDong() {
        return "KH-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }

    private String layTenKhachHangTuField() {
        String ten = txtTenKhachHang.getText() == null ? "" : txtTenKhachHang.getText().trim();

        if (prefixTenKhachHangMoi != null && !prefixTenKhachHangMoi.isBlank()) {
            String prefix = prefixTenKhachHangMoi.trim();
            if (ten.startsWith(prefix)) {
                ten = ten.substring(prefix.length()).trim();
                if (ten.startsWith("-")) {
                    ten = ten.substring(1).trim();
                }
            }
        }

        if (maKhachHangHienTai != null && !maKhachHangHienTai.isBlank()) {
            String prefix = maKhachHangHienTai + " - ";
            if (ten.startsWith(prefix)) {
                ten = ten.substring(prefix.length()).trim();
            }

            String suffix = " - " + maKhachHangHienTai;
            if (ten.endsWith(suffix)) {
                ten = ten.substring(0, ten.length() - suffix.length()).trim();
            }
        }

        return ten;
    }

    private void capNhatTenKhachHangNeuCan() {
        String sdt = txtMaKhachHang.getText().trim();

        if (!laSoDienThoaiHopLe(sdt)) {
            throw new IllegalArgumentException("SĐT gồm 10 số nguyên dương và bắt đầu bằng số 0.");
        }

        if (maKhachHangHienTai == null || maKhachHangHienTai.isBlank()) {
            KhachHangInfo kh = timKhachHang(sdt);
            if (kh != null) {
                maKhachHangHienTai = kh.maKhachHang;
                khachHangMoiDangNhap = false;
            } else {
                maKhachHangHienTai = taoMaKhachHangTuDong();
                khachHangMoiDangNhap = true;
            }
        }

        String tenMoi = layTenKhachHangTuField();

        if (khachHangMoiDangNhap) {
            if (tenMoi.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập tên khách hàng mới.");
            }

            String sql = "INSERT INTO KhachHang(maDinhDanh, hoTen, soDienThoai, diemTichLuy) VALUES (?, ?, ?, 0)";
            try (Connection conn = DBConnection.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maKhachHangHienTai);
                ps.setString(2, tenMoi);
                ps.setString(3, sdt);
                ps.executeUpdate();
                khachHangMoiDangNhap = false;
                boKhoaPrefixTenKhachHang();
                txtTenKhachHang.setEditable(false);
                txtTenKhachHang.setText(tenMoi + " - " + maKhachHangHienTai);
            } catch (Exception e) {
                throw new RuntimeException("Không thể thêm khách hàng mới: " + e.getMessage(), e);
            }
            return;
        }

        if (tenMoi.isEmpty()) {
            return;
        }

        String sql = "UPDATE KhachHang SET hoTen = ? WHERE maDinhDanh = ? AND ISNULL(hoTen, '') <> ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenMoi);
            ps.setString(2, maKhachHangHienTai);
            ps.setString(3, tenMoi);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Không thể cập nhật tên khách hàng: " + e.getMessage(), e);
        }
    }

    private KhachHangInfo timKhachHang(String key) {
        String sql = "SELECT maDinhDanh, hoTen, soDienThoai, diemTichLuy FROM KhachHang WHERE maDinhDanh = ? OR soDienThoai = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key.trim().toUpperCase());
            ps.setString(2, key.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new KhachHangInfo(
                            rs.getString("maDinhDanh"),
                            rs.getString("hoTen"),
                            rs.getString("soDienThoai"),
                            rs.getInt("diemTichLuy"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tìm khách hàng: " + e.getMessage(), e);
        }
        return null;
    }

    private KhachHangInfo taoKhachHangMoiBangSDT(String sdt) {
        if (!laSoDienThoaiHopLe(sdt)) {
            throw new IllegalArgumentException("SĐT gồm 10 số nguyên dương và bắt đầu bằng số 0.");
        }

        String maKH = taoMaKhachHangTuDong();
        String ten = "";
        String sql = "INSERT INTO KhachHang(maDinhDanh, hoTen, soDienThoai, diemTichLuy) VALUES (?, ?, ?, 0)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            ps.setString(2, ten);
            ps.setString(3, sdt.trim());
            ps.executeUpdate();
            return new KhachHangInfo(maKH, ten, sdt.trim(), 0);
        } catch (Exception e) {
            throw new RuntimeException("Không thể tự tạo khách hàng mới: " + e.getMessage(), e);
        }
    }

    private int layDiemKhachHang(String maKH) {
        KhachHangInfo kh = timKhachHang(maKH);
        return kh == null ? 0 : kh.diemTichLuy;
    }

    private String taoCartKey(String maSP, String kichCo) {
        kichCo = kichCo == null || kichCo.trim().isEmpty() ? "M" : kichCo.trim().toUpperCase();
        return maSP.trim().toUpperCase() + "|" + kichCo;
    }

    private SanPhamInfo timSanPham(String maSP) {
        return timSanPham(maSP, txtKichCo == null ? "" : txtKichCo.getText().trim(), true);
    }

    private SanPhamInfo timSanPham(String maSP, String kichCo, boolean choChonSizeNeuThieu) {
        return timSanPhamNoiBo(maSP, kichCo, choChonSizeNeuThieu, false);
    }

    private SanPhamInfo timSanPhamChoHienThi(String maSP, String kichCo) {
        return timSanPhamNoiBo(maSP, kichCo, false, true);
    }

    private SanPhamInfo timSanPhamNoiBo(String maSP, String kichCo, boolean choChonSizeNeuThieu,
            boolean choPhepSanPhamNgungBan) {
        daHuyChonKichCoSanPham = false;
        if (maSP == null || maSP.trim().isEmpty())
            return null;

        String sql = """
                SELECT sp.maSanPham, sp.tenSanPham, sk.kichCo, sp.giaBan,
                       ISNULL(sp.giamGiaBan, 0) AS giamGiaBan, sk.soLuongTon,
                       ISNULL(sk.trangThai, N'Đang bán') AS trangThai
                FROM SanPham sp
                INNER JOIN SanPhamKichCo sk ON sp.maSanPham = sk.maSanPham
                WHERE sp.maSanPham = ?
                  AND (? = '' OR sk.kichCo = ?)
                ORDER BY sk.kichCo
                """;

        java.util.List<SanPhamInfo> ds = new java.util.ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            String kc = kichCo == null ? "" : kichCo.trim().toUpperCase();
            ps.setString(1, maSP.trim().toUpperCase());
            ps.setString(2, kc);
            ps.setString(3, kc);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new SanPhamInfo(
                            rs.getString("maSanPham"),
                            rs.getString("tenSanPham"),
                            rs.getString("kichCo"),
                            rs.getDouble("giaBan"),
                            rs.getDouble("giamGiaBan"),
                            rs.getInt("soLuongTon"),
                            rs.getString("trangThai")));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tìm sản phẩm: " + e.getMessage(), e);
        }

        if (ds.isEmpty())
            return null;

        if (ds.size() == 1 || !choChonSizeNeuThieu) {
            SanPhamInfo sp = ds.get(0);
            if (sp.daNgungBan() && !choPhepSanPhamNgungBan) {
                throw new IllegalArgumentException("Sản phẩm hiện đã ngừng bán.");
            }
            return sp;
        }

        java.util.List<SanPhamInfo> dsDangBan = new java.util.ArrayList<>();
        for (SanPhamInfo item : ds) {
            if (!item.daNgungBan()) {
                dsDangBan.add(item);
            }
        }
        if (dsDangBan.isEmpty()) {
            if (choPhepSanPhamNgungBan) {
                return ds.get(0);
            }
            throw new IllegalArgumentException("Sản phẩm hiện đã ngừng bán.");
        }

        SanPhamInfo selectedInfo = showChonKichCoSanPham(dsDangBan);
        if (selectedInfo == null) {
            daHuyChonKichCoSanPham = true;
            return null;
        }
        return selectedInfo;
    }

    private SanPhamInfo showChonKichCoSanPham(java.util.List<SanPhamInfo> ds) {
        if (ds == null || ds.isEmpty()) {
            return null;
        }

        final SanPhamInfo[] result = { null };

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, "Chọn kích cỡ sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(560, 300);
        dialog.setResizable(false);

        JPanel root = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                LinearGradientPaint bg = new LinearGradientPaint(
                        0, 0, w, h,
                        new float[] { 0f, 0.45f, 1f },
                        new Color[] {
                                new Color(248, 252, 255),
                                new Color(220, 237, 255),
                                new Color(176, 211, 249)
                        });
                g2.setPaint(bg);
                g2.fillRect(0, 0, w, h);

                g2.setColor(new Color(50, 90, 145, 42));
                for (int i = -h; i < w + h; i += 160) {
                    g2.drawLine(i, h, i + h, 0);
                }

                g2.dispose();
            }
        };
        root.setBorder(new EmptyBorder(20, 30, 24, 30));

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setPreferredSize(new Dimension(480, 220));

        JLabel title = new JLabel("Chọn kích cỡ sản phẩm", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(18, 59, 140));
        title.setMaximumSize(new Dimension(480, 36));

        JLabel message = new JLabel("Sản phẩm này có nhiều kích cỡ. Vui lòng chọn kích cỡ:", SwingConstants.CENTER);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        message.setHorizontalAlignment(SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        message.setForeground(new Color(11, 23, 54));
        message.setMaximumSize(new Dimension(480, 30));

        JComboBox<SanPhamInfo> combo = new JComboBox<>(ds.toArray(new SanPhamInfo[0]));
        combo.setMaximumSize(new Dimension(420, 40));
        combo.setPreferredSize(new Dimension(420, 40));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(new Color(11, 23, 54));
        combo.setBackground(Color.WHITE);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof SanPhamInfo) {
                    SanPhamInfo item = (SanPhamInfo) value;
                    setText(item.kichCo + "  -  tồn " + item.soLuongTon);
                }

                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setBorder(new EmptyBorder(7, 10, 7, 10));

                if (isSelected) {
                    setBackground(new Color(36, 86, 205));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(new Color(11, 23, 54));
                }

                return this;
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setOpaque(false);
        buttons.setMaximumSize(new Dimension(480, 44));

        JButton cancel = createHoaDonPopupButton("Hủy", false);
        JButton ok = createHoaDonPopupButton("Đồng ý", true);

        cancel.addActionListener(e -> dialog.dispose());
        ok.addActionListener(e -> {
            Object selected = combo.getSelectedItem();
            if (selected instanceof SanPhamInfo) {
                result[0] = (SanPhamInfo) selected;
            }
            dialog.dispose();
        });

        buttons.add(cancel);
        buttons.add(ok);

        box.add(Box.createVerticalGlue());
        box.add(title);
        box.add(Box.createVerticalStrut(14));
        box.add(message);
        box.add(Box.createVerticalStrut(14));
        box.add(combo);
        box.add(Box.createVerticalStrut(18));
        box.add(buttons);
        box.add(Box.createVerticalGlue());

        root.add(box, new GridBagConstraints());

        dialog.setContentPane(root);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return result[0];
    }

    private JComboBox<String> taoComboBox(String[] values) {
        JComboBox<String> combo = new JComboBox<>(values);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(INPUT_DARK);
        combo.setForeground(TEXT_LIGHT);
        combo.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        combo.setPreferredSize(new Dimension(0, 34));
        return combo;
    }

    private String layComboText(JComboBox<String> combo) {
        Object item = combo == null ? null : combo.getSelectedItem();
        return item == null ? "" : item.toString();
    }

    private String defaultMaNhanVien() {
        if (currentMaNhanVien != null && !currentMaNhanVien.isBlank())
            return currentMaNhanVien;
        return laAdmin() ? "NV-000001" : "";
    }

    private double docGiamGia() {
        String text = txtGiamGia == null ? "" : txtGiamGia.getText().trim();
        if (text.isEmpty())
            return 0;
        double value = Double.parseDouble(text);
        if (value < 0)
            throw new IllegalArgumentException("Giảm giá không được âm.");
        return value;
    }

    private int docSoLuong() {
        String text = txtSoLuong.getText().trim();
        if (text.isEmpty())
            throw new NumberFormatException("Số lượng không được để trống.");
        int value = Integer.parseInt(text);
        if (value <= 0)
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        return value;
    }

    private int docDiemHienCo() {
        try {
            return Integer.parseInt(txtDiemHienCo.getText().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String dinhDangTien(double value) {
        return String.format("%,.0f", value);
    }

    private String layThongBaoLoi(Exception ex) {
        Throwable t = ex;
        while (t.getCause() != null)
            t = t.getCause();
        return t.getMessage() == null ? ex.getMessage() : t.getMessage();
    }

    private JPanel taoCardPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD_DARK);
        p.setOpaque(true);
        return p;
    }

    private void addRow(JPanel parent, int row, String text, Component field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = row;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.weightx = 0.34;
        gbc.insets = new Insets(0, 0, 10, 10);
        parent.add(taoLabel(text), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.66;
        gbc.insets = new Insets(0, 0, 10, 0);
        parent.add(field, gbc);
    }

    private JPanel wrapFieldWithButton(JTextField field, JButton button) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        panel.add(field, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);
        return panel;
    }

    private void styleCheckBox(JCheckBox cb) {
        cb.setOpaque(false);
        cb.setForeground(TEXT_LIGHT);
        cb.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cb.setFocusPainted(false);
        cb.setBorder(new EmptyBorder(0, 2, 0, 0));
    }

    private JLabel taoLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_LIGHT);
        label.setOpaque(false);
        return label;
    }

    private JLabel taoTongLabel(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, size));
        label.setForeground(color);
        label.setBorder(new EmptyBorder(0, 8, 0, 8));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField taoTextField() {
        return taoTextField(0, "");
    }

    private JTextField taoTextField(String text) {
        return taoTextField(0, text);
    }

    private JTextField taoTextField(int columns, String text) {
        JTextField txt = columns > 0 ? new JTextField(columns) : new JTextField();
        txt.setText(text);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBackground(INPUT_DARK);
        txt.setForeground(TEXT_LIGHT);
        txt.setCaretColor(GOLD_LIGHT);
        txt.setOpaque(true);
        txt.setPreferredSize(new Dimension(180, 36));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_DARK, 1),
                new EmptyBorder(6, 10, 6, 10)));
        txt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (txt == txtGiamGia)
                    capNhatTongTien();
            }
        });
        return txt;
    }

    private TitledBorder taoTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        new EmptyBorder(6, 6, 6, 6)),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                GOLD_LIGHT);
    }

    private JButton taoNut(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(9, 12, 9, 12)));
        Icon icon = taoIconNut(text);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(8);
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        btn.setPreferredSize(new Dimension(140, 38));
        return btn;
    }

    private Icon taoIconNut(String text) {
        String lower = text == null ? "" : text.toLowerCase();
        ButtonIconType type;
        if (lower.contains("xóa") || lower.contains("hủy"))
            type = ButtonIconType.DELETE;
        else if (lower.contains("sửa"))
            type = ButtonIconType.EDIT;
        else if (lower.contains("thêm") || lower.contains("tạo"))
            type = ButtonIconType.ADD;
        else if (lower.contains("thanh toán") || lower.contains("xác nhận"))
            type = ButtonIconType.PAYMENT;
        else if (lower.contains("trả hàng") || lower.contains("tra hang"))
            type = ButtonIconType.RETURN;
        else if (lower.contains("in") || lower.contains("xuất"))
            type = ButtonIconType.PRINT;
        else if (lower.contains("chi tiết") || lower.contains("xem"))
            type = ButtonIconType.VIEW;
        else if (lower.contains("làm mới"))
            type = ButtonIconType.REFRESH;
        else if (lower.contains("hiển thị") || lower.contains("tất cả"))
            type = ButtonIconType.LIST;
        else if (lower.contains("tìm"))
            type = ButtonIconType.SEARCH;
        else
            type = ButtonIconType.NONE;
        return type == ButtonIconType.NONE ? null : new SimpleButtonIcon(type, 15);
    }

    private enum ButtonIconType {
        ADD, EDIT, DELETE, PAYMENT, PRINT, VIEW, REFRESH, SEARCH, LIST, RETURN, NONE
    }

    private class SimpleButtonIcon implements Icon {
        private final ButtonIconType type;
        private final int size;

        SimpleButtonIcon(ButtonIconType type, int size) {
            this.type = type;
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
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(c.getForeground());

            int s = size;
            int cx = x + s / 2;
            int cy = y + s / 2;

            switch (type) {
                case ADD -> {
                    g2.drawLine(cx, y + 3, cx, y + s - 3);
                    g2.drawLine(x + 3, cy, x + s - 3, cy);
                }
                case EDIT -> {
                    g2.drawLine(x + 4, y + s - 4, x + s - 4, y + 4);
                    g2.drawLine(x + 3, y + s - 3, x + 7, y + s - 2);
                    g2.drawLine(x + s - 5, y + 3, x + s - 2, y + 6);
                }
                case DELETE -> {
                    g2.drawOval(x + 2, y + 2, s - 4, s - 4);
                    g2.drawLine(x + 6, y + 6, x + s - 6, y + s - 6);
                    g2.drawLine(x + s - 6, y + 6, x + 6, y + s - 6);
                }
                case PAYMENT -> {
                    g2.drawRoundRect(x + 2, y + 4, s - 4, s - 8, 3, 3);
                    g2.drawLine(x + 3, y + 8, x + s - 3, y + 8);
                    g2.drawLine(x + 5, y + s - 5, x + 10, y + s - 5);
                }
                case PRINT -> {
                    g2.drawRect(x + 4, y + 2, s - 8, 5);
                    g2.drawRoundRect(x + 2, y + 6, s - 4, 7, 3, 3);
                    g2.drawRect(x + 4, y + 11, s - 8, s - 12);
                    g2.drawLine(x + 6, y + 14, x + s - 6, y + 14);
                }
                case VIEW -> {
                    g2.drawOval(x + 2, y + 4, s - 4, s - 8);
                    g2.fillOval(cx - 2, cy - 2, 4, 4);
                }
                case REFRESH -> {
                    g2.drawArc(x + 3, y + 3, s - 6, s - 6, 40, 260);
                    g2.drawLine(x + s - 4, y + 5, x + s - 2, y + 10);
                    g2.drawLine(x + s - 4, y + 5, x + s - 9, y + 6);
                }
                case SEARCH -> {
                    g2.drawOval(x + 2, y + 2, s - 7, s - 7);
                    g2.drawLine(x + s - 5, y + s - 5, x + s - 1, y + s - 1);
                }
                case LIST -> {

                    for (int i = 0; i < 3; i++) {
                        int yy = y + 4 + i * 5;
                        g2.fillOval(x + 2, yy - 1, 2, 2);
                        g2.drawLine(x + 6, yy, x + s - 2, yy);
                    }
                }
                case RETURN -> {

                    g2.drawArc(x + 3, y + 4, s - 6, s - 7, 45, 260);
                    g2.drawLine(x + 4, y + 7, x + 4, y + 2);
                    g2.drawLine(x + 4, y + 7, x + 9, y + 7);
                }
                default -> {
                }
            }
            g2.dispose();
        }
    }

    private JTable taoTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setBackground(TABLE_DARK);
        table.setForeground(TEXT_LIGHT);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(126, 92, 53));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);
        header.setBackground(CARD_DARK_2);
        header.setForeground(GOLD_LIGHT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        header.setDefaultRenderer(new DarkHeaderRenderer());

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(col == 1 || col == 9 ? SwingConstants.LEFT : SwingConstants.CENTER);
                if (isSelected) {
                    setBackground(new Color(126, 92, 53));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? TABLE_DARK : TABLE_DARK_2);
                    setForeground(TEXT_LIGHT);
                }
                setBorder(new EmptyBorder(4, 10, 4, 10));
                return this;
            }
        });
        return table;
    }

    private JScrollPane taoScrollPane(JTable table, String title) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(true);
        scrollPane.setBackground(CARD_DARK);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(TABLE_DARK);
        scrollPane.setBorder(taoTitledBorder(title));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setBackground(TABLE_DARK);
        scrollPane.getHorizontalScrollBar().setBackground(TABLE_DARK);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(24, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 22));
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(18);
        scrollPane.getVerticalScrollBar().setUI(new GoldScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new GoldScrollBarUI());
        return scrollPane;
    }

    private void datDoRongCotChiTiet() {
        if (tableChiTietTam == null || tableChiTietTam.getColumnModel().getColumnCount() < 7)
            return;
        tableChiTietTam.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableChiTietTam.getColumnModel().getColumn(0).setPreferredWidth(105);
        tableChiTietTam.getColumnModel().getColumn(1).setPreferredWidth(300);
        tableChiTietTam.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableChiTietTam.getColumnModel().getColumn(3).setPreferredWidth(75);
        tableChiTietTam.getColumnModel().getColumn(4).setPreferredWidth(120);
        tableChiTietTam.getColumnModel().getColumn(5).setPreferredWidth(120);
        tableChiTietTam.getColumnModel().getColumn(6).setPreferredWidth(140);
    }

    private void datDoRongCotHoaDon() {
        if (tableHoaDon == null || tableHoaDon.getColumnModel().getColumnCount() < 11)
            return;
        int[] widths = { 120, 120, 120, 160, 130, 120, 130, 140, 160, 150, 300 };
        for (int i = 0; i < widths.length; i++) {
            tableHoaDon.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private class DarkHeaderRenderer extends DefaultTableCellRenderer {
        public DarkHeaderRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    new EmptyBorder(7, 10, 7, 10)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(CARD_DARK_2);
            setForeground(GOLD_LIGHT);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            return this;
        }
    }

    private static class TraHangFormData {
        final HoaDon.HangTraHang hangTra;
        final int soLuongTra;
        final String lyDo;
        final boolean congLaiTonKho;

        TraHangFormData(HoaDon.HangTraHang hangTra, int soLuongTra, String lyDo, boolean congLaiTonKho) {
            this.hangTra = hangTra;
            this.soLuongTra = soLuongTra;
            this.lyDo = lyDo;
            this.congLaiTonKho = congLaiTonKho;
        }
    }

    private static class CartItem {
        String maSanPham;
        String tenSanPham;
        String kichCo;
        int soLuong;
        double giaBanGoc;
        double giamGiaPhanTram;

        CartItem(String maSanPham, String tenSanPham, String kichCo, int soLuong, double giaBanGoc,
                double giamGiaPhanTram) {
            this.maSanPham = maSanPham;
            this.tenSanPham = tenSanPham;
            this.kichCo = kichCo == null || kichCo.isBlank() ? "M" : kichCo.trim().toUpperCase();
            this.soLuong = soLuong;
            this.giaBanGoc = giaBanGoc;
            this.giamGiaPhanTram = Math.max(0, Math.min(100, giamGiaPhanTram));
        }

        double tongTienGoc() {
            return soLuong * giaBanGoc;
        }

        double tienGiamGia() {
            return Math.round(tongTienGoc() * giamGiaPhanTram / 100.0);
        }

        double thanhTienSauGiam() {
            return Math.max(0, tongTienGoc() - tienGiamGia());
        }
    }

    private static class SanPhamInfo {
        String maSanPham;
        String tenSanPham;
        String kichCo;
        double giaBan;
        double giamGiaBan;
        int soLuongTon;
        String trangThai;

        SanPhamInfo(String maSanPham, String tenSanPham, String kichCo, double giaBan, double giamGiaBan,
                int soLuongTon, String trangThai) {
            this.maSanPham = maSanPham;
            this.tenSanPham = tenSanPham;
            this.kichCo = kichCo == null || kichCo.isBlank() ? "M" : kichCo;
            this.giaBan = giaBan;
            this.giamGiaBan = giamGiaBan;
            this.soLuongTon = soLuongTon;
            this.trangThai = trangThai == null || trangThai.isBlank() ? "Đang bán" : trangThai;
        }

        boolean daNgungBan() {
            return "Ngừng bán".equalsIgnoreCase(trangThai);
        }

        String tenSanPhamKemKichCo() {
            return tenSanPham + " - Size " + kichCo;
        }

        double giaBanSauGiam() {
            return Math.max(0, Math.round(giaBan * (100.0 - giamGiaBan) / 100.0));
        }
    }

    private static class KhachHangInfo {
        String maKhachHang;
        String hoTen;
        String soDienThoai;
        int diemTichLuy;

        KhachHangInfo(String maKhachHang, String hoTen, String soDienThoai, int diemTichLuy) {
            this.maKhachHang = maKhachHang;
            this.hoTen = hoTen;
            this.soDienThoai = soDienThoai;
            this.diemTichLuy = diemTichLuy;
        }
    }

    private static class GoldScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(205, 158, 92);
            trackColor = new Color(33, 29, 25);
        }

        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(24, 48);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled())
                return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(215, 170, 105));
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
                    thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);
            g2.setColor(new Color(255, 220, 160, 110));
            g2.drawRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
                    thumbBounds.width - 5, thumbBounds.height - 5, 10, 10);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(30, 26, 22));
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.setColor(new Color(80, 62, 42));
            g2.drawRect(trackBounds.x, trackBounds.y, trackBounds.width - 1, trackBounds.height - 1);
            g2.dispose();
        }
    }
}
