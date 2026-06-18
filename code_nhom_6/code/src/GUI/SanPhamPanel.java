package GUI;

import model.SanPham;
import model.NhaCungCap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SanPhamPanel extends JPanel {

    private JTextField txtTenSanPham;
    private JTextField txtKichCo;
    private JTextField txtDonVi;
    private JTextField txtGiaNhap;
    private JTextField txtGiaBan;
    private JTextField txtSoLuongTon;
    private JTextField txtLoaiSanPham;
    private JTextField txtThuongHieu;
    private JTextField txtXuatXu;
    private JTextField txtMaNCC;
    private JTextField txtGiamGiaBan;
    private JTextField txtTimKiem;
    private JComboBox<String> cboTrangThaiLoc;
    private JComboBox<String> cboKichCoLoc;

    private JTable tableSanPham;
    private DefaultTableModel tableModel;
    private JLabel lblSoKetQua;

    private List<SanPham> danhSachSanPham;
    private String maSanPhamDaChon = null;
    private String kichCoDaChon = null;

    private String currentVaiTro = "ADMIN";

    private boolean laAdmin() { return "ADMIN".equalsIgnoreCase(currentVaiTro); }
    private boolean laNhanVienKho() { return "WAREHOUSE".equalsIgnoreCase(currentVaiTro); }
    private boolean coQuyenQuanLySanPham() { return laAdmin() || laNhanVienKho(); }

    private boolean khongDuQuyen(String hanhDong) {
        if (!coQuyenQuanLySanPham()) {
            AppDialog.showWarning(
                    this,
                    "Không đủ quyền",
                    "Tài khoản này chỉ được xem và tìm kiếm sản phẩm, không được " + hanhDong + "."
            );
            return true;
        }
        return false;
    }

    private static final Color BG_PANEL = new Color(28, 25, 22);
    private static final Color CARD_DARK = new Color(39, 37, 34);
    private static final Color INPUT_DARK = new Color(48, 43, 38);
    private static final Color TABLE_DARK = new Color(35, 32, 29);
    private static final Color TABLE_DARK_2 = new Color(42, 39, 35);
    private static final Color GOLD_LIGHT = new Color(223, 196, 162);
    private static final Color GOLD_DARK = new Color(124, 88, 49);
    private static final Color TEXT_LIGHT = new Color(245, 239, 230);
    private static final Color BORDER = new Color(84, 72, 56);

    public SanPhamPanel() {
        this("ADMIN");
    }

    public SanPhamPanel(String vaiTro) {
        this.currentVaiTro = vaiTro == null || vaiTro.isBlank() ? "STAFF" : vaiTro.trim().toUpperCase();

        danhSachSanPham = SanPham.docTuSQL();

        setLayout(new BorderLayout(10, 10));
        setBackground(BG_PANEL);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        taoGiaoDien();
        ganSuKien();
        capNhatBang();
    }

    private void taoGiaoDien() {
        JPanel pnlTimKiem = new JPanel(new GridBagLayout());
        pnlTimKiem.setBackground(CARD_DARK);
        pnlTimKiem.setBorder(taoTitledBorder("Tìm kiếm sản phẩm"));
        GridBagConstraints gbcTk = new GridBagConstraints();
        gbcTk.insets = new Insets(6, 8, 6, 8);
        gbcTk.gridy = 0;
        gbcTk.anchor = GridBagConstraints.WEST;
        gbcTk.fill = GridBagConstraints.HORIZONTAL;

        txtTimKiem = taoTextField(24);

        JButton btnTimKiem = taoNut("Tìm Kiếm", new Color(218, 174, 88));
        JButton btnHienTatCa = taoNut("Hiển Thị Tất Cả", new Color(120, 105, 85));
        JButton btnHangSapHet = taoNut("Sản Phẩm Sắp Hết", new Color(210, 65, 55));
        cboTrangThaiLoc = new JComboBox<>(new String[]{"Tất cả", "Còn hàng", "Hết hàng", "Ngừng bán", "S", "M", "L", "XL", "XXL", "XXXL", "FREESIZE"});
        cboTrangThaiLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cboTrangThaiLoc.setPreferredSize(new Dimension(190, 36));
        cboKichCoLoc = null;

        gbcTk.gridx = 0; gbcTk.weightx = 0; pnlTimKiem.add(taoLabel("Tìm kiếm Mã/Tên/Kích cỡ/Loại/Thương hiệu/Xuất xứ:"), gbcTk);
        gbcTk.gridx = 1; gbcTk.weightx = 1; pnlTimKiem.add(txtTimKiem, gbcTk);
        gbcTk.gridx = 2; gbcTk.weightx = 0; pnlTimKiem.add(btnTimKiem, gbcTk);
        gbcTk.gridx = 3; pnlTimKiem.add(btnHienTatCa, gbcTk);
        gbcTk.gridx = 4; pnlTimKiem.add(btnHangSapHet, gbcTk);
        gbcTk.gridx = 5; pnlTimKiem.add(taoLabel("Sắp xếp theo:"), gbcTk);
        gbcTk.gridx = 6; pnlTimKiem.add(cboTrangThaiLoc, gbcTk);

        JPanel pnlInput = new JPanel(new GridLayout(6, 4, 10, 10));
        pnlInput.setBackground(CARD_DARK);
        pnlInput.setBorder(taoTitledBorder("Thông tin sản phẩm"));
        pnlInput.setPreferredSize(new Dimension(0, 255));

        pnlInput.add(taoLabel("Tên sản phẩm:"));
        txtTenSanPham = taoTextField();
        pnlInput.add(txtTenSanPham);

        pnlInput.add(taoLabel("Kích cỡ:"));
        txtKichCo = taoTextField("M");
        pnlInput.add(txtKichCo);

        pnlInput.add(taoLabel("Đơn vị:"));
        txtDonVi = taoTextField("cái");
        pnlInput.add(txtDonVi);

        pnlInput.add(taoLabel("Giá nhập:"));
        txtGiaNhap = taoTextField("0");
        txtGiaNhap.setToolTipText("Giá nhập chỉ cập nhật qua chức năng Nhập Hàng.");
        setGiaNhapEditable(false);
        pnlInput.add(txtGiaNhap);

        pnlInput.add(taoLabel("Giá bán:"));
        txtGiaBan = taoTextField("0");
        pnlInput.add(txtGiaBan);

        pnlInput.add(taoLabel("Số lượng tồn:"));
        txtSoLuongTon = taoTextField("0");
        txtSoLuongTon.setToolTipText("Số lượng tồn chỉ cập nhật qua chức năng Nhập Hàng.");
        setSoLuongTonEditable(false);
        pnlInput.add(txtSoLuongTon);

        pnlInput.add(taoLabel("Loại sản phẩm:"));
        txtLoaiSanPham = taoTextField();
        pnlInput.add(txtLoaiSanPham);

        pnlInput.add(taoLabel("Thương hiệu:"));
        txtThuongHieu = taoTextField();
        pnlInput.add(txtThuongHieu);

        pnlInput.add(taoLabel("Xuất xứ:"));
        txtXuatXu = taoTextField();
        pnlInput.add(txtXuatXu);

        pnlInput.add(taoLabel("Mã nhà cung cấp:"));
        txtMaNCC = taoTextField();
        pnlInput.add(txtMaNCC);

        pnlInput.add(taoLabel("Giảm giá bán (%):"));
        txtGiamGiaBan = taoTextField("0");
        pnlInput.add(txtGiamGiaBan);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlButtons.setBackground(CARD_DARK);

        JButton btnThem = taoNut("Thêm Sản Phẩm", new Color(70, 145, 210));
        JButton btnSua = taoNut("Sửa Thông Tin", new Color(218, 174, 88));
        JButton btnXoa = taoNut("Xóa Sản Phẩm", new Color(210, 65, 55));
        JButton btnClear = taoNut("Làm Mới Form", new Color(120, 105, 85));
        JButton btnNhapHang = taoNut("Nhập Hàng", new Color(86, 167, 110));
        JButton btnKhoiPhuc = taoNut("Khôi Phục Sản Phẩm", new Color(95, 155, 210));

        if (coQuyenQuanLySanPham()) {
            pnlButtons.add(btnThem);
            pnlButtons.add(btnSua);
            if (laAdmin()) {
                pnlButtons.add(btnXoa);
            }
            pnlButtons.add(btnKhoiPhuc);
            pnlButtons.add(btnNhapHang);
            pnlButtons.add(btnClear);
        }

        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setBackground(BG_PANEL);
        pnlTop.add(pnlTimKiem, BorderLayout.NORTH);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        if (coQuyenQuanLySanPham()) {
            pnlTop.add(pnlButtons, BorderLayout.SOUTH);
        }

        add(pnlTop, BorderLayout.NORTH);

        String[] columnNames = {
                "Mã SP", "Tên SP", "Kích Cỡ", "Đơn Vị", "Giá Bán",
                "Giảm Giá Bán (%)", "Tồn Kho", "Đã Bán", "Loại SP", "Thương Hiệu", "Xuất Xứ", "Mã NCC", "Trạng Thái"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableSanPham = new JTable(tableModel);
        tableSanPham.setRowHeight(32);
        tableSanPham.setBackground(TABLE_DARK);
        tableSanPham.setForeground(TEXT_LIGHT);
        tableSanPham.setGridColor(BORDER);
        tableSanPham.setSelectionBackground(GOLD_DARK);
        tableSanPham.setSelectionForeground(Color.WHITE);
        tableSanPham.setShowVerticalLines(true);
        tableSanPham.setShowHorizontalLines(true);
        tableSanPham.getTableHeader().setBackground(CARD_DARK);
        tableSanPham.getTableHeader().setForeground(GOLD_LIGHT);
        tableSanPham.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableSanPham.getTableHeader().setReorderingAllowed(false);
        canGiuaHeaderVaFixKhoangTrangBang(tableSanPham);
        tableSanPham.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    setBackground(GOLD_DARK);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? TABLE_DARK : TABLE_DARK_2);
                    setForeground(TEXT_LIGHT);
                }
                setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8));
                return this;
            }
        });
        tableSanPham.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSanPham.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        datDoRongCotSanPham();

        JScrollPane scrollPane = taoScrollPane(tableSanPham, "Danh sách sản phẩm");
        lblSoKetQua = taoLabelSoKetQua();
        JPanel tableArea = new JPanel(new BorderLayout(0, 6));
        tableArea.setBackground(BG_PANEL);
        tableArea.add(scrollPane, BorderLayout.CENTER);
        tableArea.add(lblSoKetQua, BorderLayout.SOUTH);
        add(tableArea, BorderLayout.CENTER);

        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        txtTimKiem.addActionListener(e -> xuLyTimKiem());

        btnHienTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            if (cboTrangThaiLoc != null) cboTrangThaiLoc.setSelectedItem("Tất cả");
            danhSachSanPham = SanPham.docTuSQL();
            capNhatBang();
            xoaRongForm();
        });

        btnHangSapHet.addActionListener(e -> xuLyHangSapHet());
        if (cboTrangThaiLoc != null) cboTrangThaiLoc.addActionListener(e -> capNhatBang());
        btnThem.addActionListener(this::xuLyThem);
        btnSua.addActionListener(this::xuLySua);
        btnXoa.addActionListener(this::xuLyXoa);
        btnKhoiPhuc.addActionListener(e -> xuLyKhoiPhucSanPham());
        btnNhapHang.addActionListener(e -> xuLyNhapHang());
        btnClear.addActionListener(e -> xoaRongForm());
    }

    private void ganSuKien() {
        tableSanPham.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableSanPham.getSelectedRow();

                if (selectedRow >= 0) {
                    int modelRow = tableSanPham.convertRowIndexToModel(selectedRow);
                    maSanPhamDaChon = tableModel.getValueAt(modelRow, 0).toString();
                    kichCoDaChon = tableModel.getValueAt(modelRow, 2).toString();

                    for (SanPham sp : danhSachSanPham) {
                        if (sp.getMaSanPham().equals(maSanPhamDaChon)
                                && sp.getKichCo().equalsIgnoreCase(kichCoDaChon)) {
                            txtTenSanPham.setText(sp.getTenSanPham());
                            txtKichCo.setText(sp.getKichCo());
                            txtDonVi.setText(sp.getDonVi());
                            txtGiaNhap.setText(dinhDangTienNhap(sp.getGiaNhap()));
                            setGiaNhapEditable(false);
                            txtGiaBan.setText(dinhDangTienNhap(sp.getGiaBan()));
                            txtSoLuongTon.setText(String.valueOf(sp.getSoLuongTon()));
                            setSoLuongTonEditable(false);
                            txtLoaiSanPham.setText(sp.getLoaiSanPham());
                            txtThuongHieu.setText(sp.getThuongHieu());
                            txtXuatXu.setText(sp.getXuatXu());
                            txtMaNCC.setText(sp.getMaNCC());
                            setMaNCCEditable(false);
                            txtGiamGiaBan.setText(String.valueOf(sp.getGiamGiaBan()));
                            break;
                        }
                    }
                }
            }
        });
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
        txt.setCaretColor(GOLD_LIGHT);
        txt.setOpaque(true);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD_DARK, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return txt;
    }

    private TitledBorder taoTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                title,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                GOLD_LIGHT
        );
        return border;
    }

    private void canGiuaHeaderVaFixKhoangTrangBang(JTable table) {
        table.setFillsViewportHeight(true);
        table.setOpaque(true);
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(CARD_DARK);
                setForeground(GOLD_LIGHT);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setBorder(BorderFactory.createLineBorder(BORDER, 1));
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
        scrollPane.setPreferredSize(new Dimension(0, 235));
        scrollPane.setMinimumSize(new Dimension(0, 220));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
        vertical.setBackground(TABLE_DARK);
        horizontal.setBackground(TABLE_DARK);
        vertical.setPreferredSize(new Dimension(16, 0));
        horizontal.setPreferredSize(new Dimension(0, 16));
        vertical.setUnitIncrement(18);
        horizontal.setUnitIncrement(18);
        vertical.setUI(new GoldScrollBarUI());
        horizontal.setUI(new GoldScrollBarUI());

        return scrollPane;
    }

    private static class GoldScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(205, 158, 92);
            trackColor = new Color(33, 29, 25);
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
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
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

    private JButton taoNut(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setBackground(color);
        boolean isDark = color.getRed() < 200 || (color.getRed() < 230 && color.getGreen() < 150);
        btn.setForeground(isDark ? Color.WHITE : Color.BLACK);
        Icon icon = taoIconNut(text);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(8);
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setMargin(new Insets(7, 14, 7, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(84, 72, 56), 1));
        return btn;
    }


    private JButton taoNutPopupNhapHang(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color base = getModel().isPressed() ? color.darker() : color;
                if (getModel().isRollover()) {
                    base = base.brighter();
                }

                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                g2.setColor(new Color(255, 255, 255, 95));
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boolean isDark = color.getRed() < 200 || (color.getRed() < 230 && color.getGreen() < 150);
        btn.setForeground(isDark ? Color.WHITE : Color.BLACK);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(9, 18, 9, 18));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        btn.setPreferredSize(new Dimension(text.length() > 5 ? 170 : 92, 42));
        btn.setMinimumSize(btn.getPreferredSize());

        Icon icon = taoIconNut(text);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(9);
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        return btn;
    }

    private Icon taoIconNut(String text) {
        String lower = text == null ? "" : text.toLowerCase();
        ButtonIconType type;
        if (lower.contains("xóa")) type = ButtonIconType.DELETE;
        else if (lower.contains("hủy")) type = ButtonIconType.CANCEL;
        else if (lower.contains("sửa")) type = ButtonIconType.EDIT;
        else if (lower.contains("thêm") || lower.contains("xác nhận")) type = ButtonIconType.ADD;
        else if (lower.contains("khôi phục") || lower.contains("khoi phuc")) type = ButtonIconType.RESTORE;
        else if (lower.contains("nhập")) type = ButtonIconType.WAREHOUSE;
        else if (lower.contains("tìm")) type = ButtonIconType.SEARCH;
        else if (lower.contains("xem")) type = ButtonIconType.VIEW;
        else if (lower.contains("hiển thị") || lower.contains("tất cả")) type = ButtonIconType.LIST;
        else if (lower.contains("làm mới")) type = ButtonIconType.REFRESH;
        else if (lower.contains("sắp hết")) type = ButtonIconType.WARNING;
        else type = ButtonIconType.NONE;
        return type == ButtonIconType.NONE ? null : new SimpleButtonIcon(type, 15);
    }

    private enum ButtonIconType {
        ADD, EDIT, DELETE, CANCEL, SEARCH, LIST, REFRESH, WARNING, WAREHOUSE, RESTORE, VIEW, NONE
    }

    private class SimpleButtonIcon implements Icon {
        private final ButtonIconType type;
        private final int size;

        SimpleButtonIcon(ButtonIconType type, int size) {
            this.type = type;
            this.size = size;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

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
                case CANCEL -> {
                    g2.drawLine(x + 4, y + 4, x + s - 4, y + s - 4);
                    g2.drawLine(x + s - 4, y + 4, x + 4, y + s - 4);
                }
                case SEARCH -> {
                    g2.drawOval(x + 2, y + 2, s - 7, s - 7);
                    g2.drawLine(x + s - 5, y + s - 5, x + s - 1, y + s - 1);
                }
                case LIST -> {
                    for (int i = 4; i <= s - 4; i += 5) {
                        g2.drawLine(x + 4, y + i, x + s - 3, y + i);
                    }
                }
                case REFRESH -> {
                    g2.drawArc(x + 3, y + 3, s - 6, s - 6, 40, 260);
                    g2.drawLine(x + s - 4, y + 5, x + s - 2, y + 10);
                    g2.drawLine(x + s - 4, y + 5, x + s - 9, y + 6);
                }
                case WARNING -> {
                    g2.drawLine(cx, y + 2, x + s - 2, y + s - 3);
                    g2.drawLine(x + s - 2, y + s - 3, x + 2, y + s - 3);
                    g2.drawLine(x + 2, y + s - 3, cx, y + 2);
                    g2.drawLine(cx, y + 6, cx, y + s - 7);
                    g2.fillOval(cx - 1, y + s - 5, 2, 2);
                }
                case WAREHOUSE -> {
                    g2.drawRect(x + 3, y + 6, s - 6, s - 8);
                    g2.drawLine(x + 3, y + 6, cx, y + 2);
                    g2.drawLine(cx, y + 2, x + s - 3, y + 6);
                    g2.drawLine(x + 6, y + 10, x + s - 6, y + 10);
                    g2.drawLine(cx, y + 8, cx, y + s - 3);
                }
                case RESTORE -> {
                    g2.drawArc(x + 3, y + 3, s - 6, s - 6, 35, 280);
                    g2.drawLine(x + 4, y + 6, x + 4, y + 2);
                    g2.drawLine(x + 4, y + 6, x + 8, y + 6);
                    g2.drawLine(cx, y + 5, cx, y + s - 5);
                    g2.drawLine(x + 5, cy, x + s - 5, cy);
                }
                case VIEW -> {
                    
                    g2.drawOval(x + 2, y + 4, s - 4, s - 8);
                    g2.fillOval(cx - 2, cy - 2, 4, 4);
                }
                default -> { }
            }
            g2.dispose();
        }
    }


    private JLabel taoLabelSoKetQua() {
        JLabel label = new JLabel("Số kết quả tìm được: 0", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(11, 23, 54));
        label.setOpaque(true);
        label.setBackground(new Color(248, 250, 252));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 224, 236), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return label;
    }

    private void capNhatSoKetQua() {
        if (lblSoKetQua != null && tableModel != null) {
            lblSoKetQua.setText("Số kết quả tìm được: " + tableModel.getRowCount());
        }
    }

    private boolean phuHopTrangThaiLoc(SanPham sp) {
        if (sp == null || cboTrangThaiLoc == null) return true;
        Object selected = cboTrangThaiLoc.getSelectedItem();
        String loc = selected == null ? "Tất cả" : selected.toString();
        if ("Tất cả".equalsIgnoreCase(loc)) return true;
        if ("Còn hàng".equalsIgnoreCase(loc)) {
            return !"Ngừng bán".equalsIgnoreCase(sp.getTrangThai()) && sp.getSoLuongTon() > 0;
        }
        if ("Hết hàng".equalsIgnoreCase(loc)) {
            return !"Ngừng bán".equalsIgnoreCase(sp.getTrangThai()) && sp.getSoLuongTon() == 0;
        }
        if ("Ngừng bán".equalsIgnoreCase(loc)) {
            return "Ngừng bán".equalsIgnoreCase(sp.getTrangThai());
        }
        return loc.equalsIgnoreCase(sp.getKichCo());
    }

    private boolean phuHopKichCoLoc(SanPham sp) {
        return true;
    }

    private void datDoRongCotSanPham() {
        if (tableSanPham == null || tableSanPham.getColumnModel().getColumnCount() < 13) return;
        int[] widths = {105, 230, 90, 90, 120, 135, 95, 95, 140, 130, 120, 115, 120};
        for (int i = 0; i < widths.length; i++) {
            tableSanPham.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            tableSanPham.getColumnModel().getColumn(i).setMinWidth(75);
        }
    }

    private JButton taoNutPopupLon(String text, Color bg, Color fg) {
        JButton btn = taoNut(text, bg);
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(text.length() > 5 ? 165 : 96, 44));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(new RoundedButtonBorder(Color.BLACK, 14, 2, 12, 18));
        return btn;
    }

    private static class RoundedButtonBorder extends javax.swing.border.AbstractBorder {
        private final Color color;
        private final int arc;
        private final int thickness;
        private final int verticalPadding;
        private final int horizontalPadding;

        RoundedButtonBorder(Color color, int arc, int thickness, int verticalPadding, int horizontalPadding) {
            this.color = color == null ? Color.BLACK : color;
            this.arc = arc;
            this.thickness = thickness;
            this.verticalPadding = verticalPadding;
            this.horizontalPadding = horizontalPadding;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = verticalPadding;
            insets.left = horizontalPadding;
            insets.bottom = verticalPadding;
            insets.right = horizontalPadding;
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + thickness / 2, y + thickness / 2, width - thickness, height - thickness, arc, arc);
            g2.dispose();
        }
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText() == null ? "" : txtTimKiem.getText().trim();

        if (keyword.isEmpty()) {
            AppDialog.showError(this, "Lỗi nhập liệu", "Vui lòng nhập từ khóa tìm kiếm");
            return;
        }

        if (cboTrangThaiLoc != null) {
            cboTrangThaiLoc.setSelectedItem("Tất cả");
        }

        thucHienLocSanPhamTheoTuKhoa(keyword);
    }

    private void thucHienLocSanPhamTheoTuKhoa(String keyword) {
        danhSachSanPham = locSanPhamTheoTuKhoa(SanPham.docTuSQL(), keyword);
        tableSanPham.clearSelection();
        maSanPhamDaChon = null;
        kichCoDaChon = null;
        capNhatBang();
    }

    private List<SanPham> locSanPhamTheoTuKhoa(List<SanPham> danhSachGoc, String keyword) {
        List<SanPham> ketQua = new ArrayList<>();
        String key = chuanHoaTimKiem(keyword);
        String keyRutGon = rutGonTimKiem(key);
        List<String> tokens = tachTokenTimKiem(key);

        if ((key.isEmpty() && keyRutGon.isEmpty()) || danhSachGoc == null) {
            return ketQua;
        }

        for (SanPham sp : danhSachGoc) {
            if (sp == null) continue;
            if (phuHopSanPhamTheoTuKhoa(sp, key, keyRutGon, tokens)) {
                ketQua.add(sp);
            }
        }

        return ketQua;
    }

    private boolean phuHopSanPhamTheoTuKhoa(SanPham sp, String key, String keyRutGon, List<String> tokens) {
        String[] truongTimKiem = {
                sp.getMaSanPham(),
                sp.getTenSanPham(),
                sp.getKichCo(),
                sp.getLoaiSanPham(),
                sp.getThuongHieu(),
                sp.getXuatXu()
        };

        for (String value : truongTimKiem) {
            if (truongPhuHopTuKhoa(value, key, keyRutGon)) {
                return true;
            }
        }

        if (tokens == null || tokens.isEmpty()) {
            return false;
        }

        for (String token : tokens) {
            if (!tokenPhuHopBatKyTruongNao(truongTimKiem, token)) {
                return false;
            }
        }
        return true;
    }

    private boolean truongPhuHopTuKhoa(String value, String key, String keyRutGon) {
        String valueChuanHoa = chuanHoaTimKiem(value);
        String valueRutGon = rutGonTimKiem(valueChuanHoa);
        if (valueChuanHoa.isEmpty() && valueRutGon.isEmpty()) return false;

        if (!key.isEmpty()) {
            if (key.length() == 1 && !Character.isDigit(key.charAt(0))) {
                if (tuDonLeKhopChinhXac(valueChuanHoa, key)) return true;
            } else if (valueChuanHoa.contains(key)) {
                return true;
            }
        }

        if (!keyRutGon.isEmpty()) {
            if (keyRutGon.length() == 1 && !Character.isDigit(keyRutGon.charAt(0))) {
                return valueRutGon.equals(keyRutGon);
            }
            return valueRutGon.contains(keyRutGon);
        }
        return false;
    }

    private boolean tokenPhuHopBatKyTruongNao(String[] truongTimKiem, String token) {
        if (token == null || token.isBlank()) return true;
        for (String value : truongTimKiem) {
            if (tokenPhuHopTrongTruong(value, token)) {
                return true;
            }
        }
        return false;
    }

    private boolean tokenPhuHopTrongTruong(String value, String token) {
        String valueChuanHoa = chuanHoaTimKiem(value);
        String valueRutGon = rutGonTimKiem(valueChuanHoa);
        String tokenRutGon = rutGonTimKiem(token);

        if (valueChuanHoa.isEmpty() && valueRutGon.isEmpty()) return false;
        if (token.length() == 1 && !Character.isDigit(token.charAt(0))) {
            return tuDonLeKhopChinhXac(valueChuanHoa, token) || valueRutGon.equals(tokenRutGon);
        }

        return (!token.isEmpty() && valueChuanHoa.contains(token))
                || (!tokenRutGon.isEmpty() && valueRutGon.contains(tokenRutGon));
    }

    private boolean tuDonLeKhopChinhXac(String valueChuanHoa, String token) {
        if (valueChuanHoa == null || token == null) return false;
        String[] words = valueChuanHoa.split("[^a-z0-9]+");
        for (String word : words) {
            if (token.equals(word)) {
                return true;
            }
        }
        return false;
    }

    private List<String> tachTokenTimKiem(String key) {
        List<String> tokens = new ArrayList<>();
        if (key == null || key.isBlank()) return tokens;

        String[] parts = key.split("[^a-z0-9]+");
        for (String part : parts) {
            if (part == null) continue;
            String token = part.trim();
            if (!token.isEmpty()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private String chuanHoaTimKiem(String text) {
        String raw = text == null ? "" : text.trim();
        String normalized = Normalizer.normalize(raw, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase();
        return normalized.replaceAll("\\s+", " ").trim();
    }

    private String rutGonTimKiem(String text) {
        String raw = text == null ? "" : text;
        return raw.replaceAll("[^a-z0-9]", "");
    }

    private boolean tenSanPhamDaTonTai(String tenSanPhamMoi) {
        String tenMoi = chuanHoaTimKiem(tenSanPhamMoi);
        if (tenMoi.isEmpty()) return false;

        for (SanPham sp : SanPham.docTuSQL()) {
            if (sp == null) continue;
            String tenCu = chuanHoaTimKiem(sp.getTenSanPham());
            if (tenCu.equals(tenMoi)) {
                return true;
            }
        }
        return false;
    }

    private void xuLyHangSapHet() {
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Sản phẩm sắp hết", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(520, 250);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        Color bgLight = new Color(248, 250, 252);
        Color cardLight = Color.WHITE;
        Color borderLight = new Color(216, 224, 236);
        Color navy = new Color(11, 23, 54);
        Color textSlate = new Color(100, 116, 139);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(bgLight);
        root.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel("KIỂM TRA SẢN PHẨM SẮP HẾT", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(navy);
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(cardLight);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel lbl = new JLabel("Ngưỡng tồn kho:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(navy);

        JTextField txtNguong = new JTextField("5");
        txtNguong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNguong.setForeground(navy);
        txtNguong.setBackground(Color.WHITE);
        txtNguong.setCaretColor(navy);
        txtNguong.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNguong.setPreferredSize(new Dimension(190, 36));
        form.add(txtNguong, gbc);

        JLabel note = new JLabel("Ví dụ: nhập 5 để lọc các sản phẩm tồn kho thấp.");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        note.setForeground(textSlate);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 6, 0, 6);
        form.add(note, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actions.setOpaque(false);

        JButton ok = taoNutPopupLon("Xem Kết Quả", new Color(218, 174, 88), Color.BLACK);
        JButton cancel = taoNutPopupLon("Hủy", new Color(120, 105, 85), Color.WHITE);
        actions.add(ok);
        actions.add(cancel);
        root.add(actions, BorderLayout.SOUTH);

        ok.addActionListener(e -> {
            String input = txtNguong.getText();

            if (input == null) {
                return;
            }

            try {
                int nguong = Integer.parseInt(input.trim());

                if (nguong < 0) {
                    thongBaoLoi("Lỗi nhập liệu", "Ngưỡng tồn kho không được âm!");
                    return;
                }

                danhSachSanPham = SanPham.timHangSapHetSQL(nguong);
                capNhatBang();
                dialog.dispose();

            } catch (NumberFormatException ex) {
                thongBaoLoi("Lỗi", "Ngưỡng tồn kho phải là số nguyên!");
            }
        });

        cancel.addActionListener(e -> dialog.dispose());

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }


    private void setMaNCCEditable(boolean editable) {
        txtMaNCC.setEditable(editable);
        txtMaNCC.setFocusable(editable);

        if (editable) {
            txtMaNCC.setBackground(INPUT_DARK);
            txtMaNCC.setForeground(TEXT_LIGHT);
            txtMaNCC.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtMaNCC.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GOLD_DARK, 1),
                    new EmptyBorder(5, 8, 5, 8)
            ));
        } else {
            
            txtMaNCC.setBackground(new Color(241, 244, 248));
            txtMaNCC.setForeground(new Color(11, 23, 54));
            txtMaNCC.setFont(new Font("Segoe UI", Font.BOLD, 13));
            txtMaNCC.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(216, 224, 236), 1),
                    new EmptyBorder(5, 8, 5, 8)
            ));
        }
    }

    private void setGiaNhapReadonlyStyle() {
        txtGiaNhap.setBackground(new Color(241, 244, 248));
        txtGiaNhap.setForeground(new Color(11, 23, 54));
        txtGiaNhap.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtGiaNhap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 224, 236), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void setGiaNhapEditable(boolean editable) {
        txtGiaNhap.setEditable(editable);
        txtGiaNhap.setFocusable(editable);

        if (editable) {
            txtGiaNhap.setBackground(INPUT_DARK);
            txtGiaNhap.setForeground(TEXT_LIGHT);
            txtGiaNhap.setCaretColor(GOLD_LIGHT);
            txtGiaNhap.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtGiaNhap.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GOLD_DARK, 1),
                    new EmptyBorder(5, 8, 5, 8)
            ));
        } else {
            setGiaNhapReadonlyStyle();
        }
    }

    private void setSoLuongTonEditable(boolean editable) {
        txtSoLuongTon.setEditable(editable);
        txtSoLuongTon.setFocusable(editable);

        if (editable) {
            txtSoLuongTon.setBackground(INPUT_DARK);
            txtSoLuongTon.setForeground(TEXT_LIGHT);
            txtSoLuongTon.setCaretColor(GOLD_LIGHT);
            txtSoLuongTon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtSoLuongTon.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GOLD_DARK, 1),
                    new EmptyBorder(5, 8, 5, 8)
            ));
        } else {
            txtSoLuongTon.setBackground(new Color(241, 244, 248));
            txtSoLuongTon.setForeground(new Color(11, 23, 54));
            txtSoLuongTon.setFont(new Font("Segoe UI", Font.BOLD, 13));
            txtSoLuongTon.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(216, 224, 236), 1),
                    new EmptyBorder(5, 8, 5, 8)
            ));
        }
    }

    private void kiemTraDuLieuSanPhamHopLe() {
        if (thieuThongTinSanPham()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin sản phẩm.");
        }

        double giaNhap;
        double giaBan;
        int soLuongTon;

        try {
            giaNhap = docTienNhap(txtGiaNhap.getText());
            giaBan = docTienNhap(txtGiaBan.getText());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Giá nhập và giá bán phải là số.");
        }

        try {
            String soLuongText = txtSoLuongTon.getText().trim();
            if (!soLuongText.matches("\\d+")) {
                throw new NumberFormatException();
            }
            soLuongTon = Integer.parseInt(soLuongText);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số lượng tồn phải là số nguyên >= 0");
        }

        if (soLuongTon < 0) {
            throw new IllegalArgumentException("Số lượng tồn phải là số nguyên >= 0");
        }
    }

    private boolean thieuThongTinSanPham() {
        return txtTenSanPham.getText().trim().isEmpty()
                || txtKichCo.getText().trim().isEmpty()
                || txtDonVi.getText().trim().isEmpty()
                || txtGiaNhap.getText().trim().isEmpty()
                || txtGiaBan.getText().trim().isEmpty()
                || txtSoLuongTon.getText().trim().isEmpty()
                || txtLoaiSanPham.getText().trim().isEmpty()
                || txtThuongHieu.getText().trim().isEmpty()
                || txtXuatXu.getText().trim().isEmpty()
                || txtMaNCC.getText().trim().isEmpty()
                || txtGiamGiaBan.getText().trim().isEmpty();
    }

    private void xuLyThem(ActionEvent e) {
        if (khongDuQuyen("thêm sản phẩm")) return;

        try {
            kiemTraDuLieuSanPhamHopLe();

            if (tenSanPhamDaTonTai(txtTenSanPham.getText())) {
                thongBaoCanhBao("Sản phẩm đã tồn tại", "Sản phẩm hiện đã tồn tại trong cửa hàng");
                txtTenSanPham.requestFocus();
                return;
            }

            SanPham sp = new SanPham();

            sp.setTenSanPham(txtTenSanPham.getText());
            sp.setKichCo(txtKichCo.getText());
            sp.setDonVi(txtDonVi.getText());
            sp.setGiaNhap(0);
            txtGiaNhap.setText("0");
            
            sp.setGiaBan(Math.round(docTienNhap(txtGiaBan.getText())));
            txtGiaBan.setText(dinhDangTienNhap(sp.getGiaBan()));
            sp.setGiamGiaBan(Double.parseDouble(txtGiamGiaBan.getText().trim()));
            sp.setSoLuongTon(0);
            txtSoLuongTon.setText("0");
            sp.setLoaiSanPham(txtLoaiSanPham.getText());
            sp.setThuongHieu(txtThuongHieu.getText());
            sp.setXuatXu(txtXuatXu.getText());
            sp.setMaNCC(txtMaNCC.getText());
            sp.setMoTa("");

            sp.luuVaoSQL();

            danhSachSanPham = SanPham.docTuSQL();
            capNhatBang();
            xoaRongForm();
            dongBoTongQuanSauThayDoiSanPham();

            thongBaoThanhCong("Thêm sản phẩm thành công!");

        } catch (NumberFormatException ex) {
            thongBaoLoi("Lỗi nhập liệu", "Vui lòng nhập đúng định dạng số.");

        } catch (IllegalArgumentException ex) {
            thongBaoLoi("Lỗi nhập liệu", ex.getMessage());

        } catch (Exception ex) {
            thongBaoLoi("Lỗi", "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void xuLySua(ActionEvent e) {
        if (khongDuQuyen("sửa sản phẩm")) return;

        if (maSanPhamDaChon == null) {
            thongBaoCanhBao("Thông báo", "Vui lòng chọn sản phẩm cần sửa!");
            return;
        }

        try {
            kiemTraDuLieuSanPhamHopLe();

            SanPham sp = null;

            for (SanPham item : danhSachSanPham) {
                if (item.getMaSanPham().equals(maSanPhamDaChon)
                        && item.getKichCo().equalsIgnoreCase(kichCoDaChon)) {
                    sp = item;
                    break;
                }
            }

            if (sp == null) {
                thongBaoLoi("Lỗi", "Không tìm thấy sản phẩm đã chọn!");
                return;
            }

            if ("Ngừng bán".equalsIgnoreCase(sp.getTrangThai())) {
                thongBaoCanhBao("Không thể sửa", "Không thể sửa sản phẩm ngừng bán.");
                return;
            }

            sp.setTenSanPham(txtTenSanPham.getText());
            sp.setKichCo(txtKichCo.getText());
            sp.setDonVi(txtDonVi.getText());
            // Không sửa giá nhập ở form sản phẩm; giá nhập chỉ thay đổi khi nhập hàng.
            sp.setGiaBan(Math.round(docTienNhap(txtGiaBan.getText())));
            txtGiaBan.setText(dinhDangTienNhap(sp.getGiaBan()));
            sp.setGiamGiaBan(Double.parseDouble(txtGiamGiaBan.getText().trim()));
            // Không sửa số lượng tồn ở form sản phẩm; tồn kho chỉ thay đổi qua chức năng Nhập Hàng.
            sp.setSoLuongTon(sp.getSoLuongTon());
            sp.setLoaiSanPham(txtLoaiSanPham.getText());
            sp.setThuongHieu(txtThuongHieu.getText());
            sp.setXuatXu(txtXuatXu.getText());
            sp.setMaNCC(txtMaNCC.getText());
            sp.setMoTa("");

            sp.capNhatSQL(kichCoDaChon);

            danhSachSanPham = SanPham.docTuSQL();
            capNhatBang();
            xoaRongForm();
            dongBoTongQuanSauThayDoiSanPham();

            thongBaoThanhCong("Sửa sản phẩm thành công!");

        } catch (NumberFormatException ex) {
            thongBaoLoi("Lỗi nhập liệu", "Vui lòng nhập đúng định dạng số.");

        } catch (IllegalArgumentException ex) {
            thongBaoLoi("Lỗi nhập liệu", ex.getMessage());

        } catch (Exception ex) {
            thongBaoLoi("Lỗi", "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void xuLyKhoiPhucSanPham() {
        if (khongDuQuyen("khôi phục sản phẩm")) return;

        if (maSanPhamDaChon == null || kichCoDaChon == null) {
            thongBaoCanhBao("Thông báo", "Vui lòng chọn sản phẩm cần khôi phục!");
            return;
        }

        try {
            SanPham.khoiPhucSanPhamSQL(maSanPhamDaChon, kichCoDaChon);

            danhSachSanPham = SanPham.docTuSQL();
            capNhatBang();
            xoaRongForm();
            dongBoTongQuanSauThayDoiSanPham();

            thongBaoThanhCong("Khôi phục sản phẩm thành công!");
        } catch (Exception ex) {
            thongBaoLoi("Lỗi khôi phục", "Không thể khôi phục sản phẩm: " + ex.getMessage());
        }
    }

    private void xuLyXoa(ActionEvent e) {
        if (!laAdmin()) {
            thongBaoCanhBao("Không đủ quyền", "Chỉ quản lý mới được xóa/ngừng bán sản phẩm.");
            return;
        }

        if (maSanPhamDaChon == null) {
            thongBaoCanhBao("Thông báo", "Vui lòng chọn sản phẩm cần xóa!");
            return;
        }

        boolean xacNhan = xacNhanHanhDong(
                "Xác nhận xóa/ngừng bán",
                "Bạn có chắc chắn muốn xóa sản phẩm " + maSanPhamDaChon + " - Size " + kichCoDaChon + "?\n"
                        + "Nếu sản phẩm đã phát sinh hóa đơn/nhập hàng, hệ thống sẽ chuyển sang trạng thái Ngừng bán để giữ lịch sử."
        );

        if (xacNhan) {
            try {
                SanPham.xoaKhoiSQL(maSanPhamDaChon, kichCoDaChon);

                danhSachSanPham = SanPham.docTuSQL();
                capNhatBang();
                xoaRongForm();
                dongBoTongQuanSauThayDoiSanPham();

                thongBaoThanhCong("Xử lý sản phẩm thành công! Nếu đã phát sinh giao dịch, sản phẩm được chuyển sang trạng thái Ngừng bán.");

            } catch (Exception ex) {
                thongBaoLoi("Lỗi", "Lỗi khi xóa: " + ex.getMessage());
            }
        }
    }


    private void xuLyNhapHang() {
        if (khongDuQuyen("nhập hàng")) return;
        if (maSanPhamDaChon == null) {
            thongBaoCanhBao("Thông báo", "Vui lòng chọn sản phẩm cần nhập hàng!");
            return;
        }

        String maNccHienTai = txtMaNCC.getText() == null ? "" : txtMaNCC.getText().trim().toUpperCase();
        if (!damBaoNCCDangHopTac(maNccHienTai)) {
            return;
        }

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Nhập hàng", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(760, 430);
        dialog.setMinimumSize(new Dimension(760, 430));
        dialog.setLocationRelativeTo(this);

        Color bgLight = new Color(248, 250, 252);
        Color cardLight = Color.WHITE;
        Color borderLight = new Color(216, 224, 236);
        Color navy = new Color(11, 23, 54);
        Color textSlate = new Color(100, 116, 139);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(bgLight);
        root.setBorder(new EmptyBorder(18, 18, 20, 18));

        JLabel title = new JLabel("NHẬP HÀNG SẢN PHẨM", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(navy);
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(cardLight);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JTextField maNcc = taoTextField(txtMaNCC.getText());
        JTextField nguoiLienHe = taoTextField(layNguoiLienHeNCC(txtMaNCC.getText()));
        JTextField maSp = taoTextField(maSanPhamDaChon);
        JTextField tenSp = taoTextField(txtTenSanPham.getText());
        JTextField kichCo = taoTextField(txtKichCo.getText());
        JTextField soLuong = taoTextField("1");
        JTextField giaNhap = taoTextField(txtGiaNhap.getText());
        ganSuKienDinhDangTien(giaNhap);

        maNcc.setEditable(false);
        nguoiLienHe.setEditable(false);
        maSp.setEditable(false);
        tenSp.setEditable(false);
        kichCo.setEditable(true);

        styleLightDialogField(maNcc, false);
        styleLightDialogField(nguoiLienHe, false);
        styleLightDialogField(maSp, false);
        styleLightDialogField(tenSp, false);
        styleLightDialogField(kichCo, true);
        styleLightDialogField(soLuong, true);
        styleLightDialogField(giaNhap, true);

        addDialogRow(form, 0, 0, "Mã NCC:", maNcc);
        addDialogRow(form, 0, 2, "Người liên hệ:", nguoiLienHe);
        addDialogRow(form, 1, 0, "Mã SP:", maSp);
        addDialogRow(form, 1, 2, "Tên SP:", tenSp);
        addDialogRow(form, 2, 0, "Kích cỡ:", kichCo);
        addDialogRow(form, 2, 2, "Số lượng nhập:", soLuong);
        addDialogRow(form, 3, 0, "Giá nhập:", giaNhap);

        JLabel note = new JLabel("Có thể nhập thêm size mới cho cùng mã sản phẩm. Ví dụ áo: S/M/L/XL, quần: 30/31/32, giày: 40/41/42.");
        note.setForeground(textSlate);
        note.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        GridBagConstraints noteGbc = new GridBagConstraints();
        noteGbc.gridx = 0;
        noteGbc.gridy = 4;
        noteGbc.gridwidth = 4;
        noteGbc.insets = new Insets(10, 8, 2, 8);
        noteGbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(note, noteGbc);

        root.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 4));
        actions.setOpaque(false);
        JButton ok = taoNutPopupNhapHang("Xác nhận nhập", new Color(86, 167, 110));
        JButton cancel = taoNutPopupNhapHang("Hủy", new Color(120, 105, 85));
        actions.add(ok);
        actions.add(cancel);
        root.add(actions, BorderLayout.SOUTH);

        ok.addActionListener(e -> {
            try {
                SanPham.nhapHangSQL(
                        "", maNcc.getText().trim(), nguoiLienHe.getText().trim(),
                        maSp.getText().trim(), tenSp.getText().trim(), kichCo.getText().trim(),
                        Integer.parseInt(soLuong.getText().trim()), docTienNhap(giaNhap.getText())
                );
                dialog.dispose();
                danhSachSanPham = SanPham.docTuSQL();
                capNhatBang();
                xoaRongForm();
                dongBoTongQuanSauThayDoiSanPham();
                thongBaoThanhCong("Nhập hàng thành công! Tồn kho và giá nhập đã cập nhật. Giá bán được giữ nguyên.");
            } catch (Exception ex) {
                thongBaoLoi("Lỗi nhập hàng", ex.getMessage());
            }
        });
        cancel.addActionListener(e -> dialog.dispose());
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }


    private void styleLightDialogField(JTextField txt, boolean editable) {
        Color bg = editable ? Color.WHITE : new Color(241, 244, 248);
        Color fg = new Color(11, 23, 54);
        Color border = new Color(216, 224, 236);

        txt.setBackground(bg);
        txt.setForeground(fg);
        txt.setCaretColor(fg);
        txt.setOpaque(true);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private JLabel taoLightDialogLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(11, 23, 54));
        label.setOpaque(false);
        label.setBorder(new EmptyBorder(0, 16, 0, 8));
        return label;
    }

    private double docTienNhap(String text) {
        String raw = text == null ? "" : text.trim().toLowerCase();
        raw = raw.replace("vnđ", "")
                 .replace("vnd", "")
                 .replace("đ", "")
                 .replaceAll("\\s+", "");
        if (raw.isEmpty()) return 0;

        raw = raw.replace(',', '.');
        if (!raw.matches("\\d+(\\.\\d+)*")) {
            throw new NumberFormatException("Giá tiền không hợp lệ.");
        }

        
        if (raw.contains(".")) {
            String[] parts = raw.split("\\.");
            boolean laDangPhanNhomHangNghin = parts.length > 1;
            for (int i = 1; i < parts.length; i++) {
                if (parts[i].length() != 3) {
                    laDangPhanNhomHangNghin = false;
                    break;
                }
            }

            if (laDangPhanNhomHangNghin) {
                return Math.round(Double.parseDouble(raw.replace(".", "")));
            }

            if (parts.length == 2) {
                String phanNguyen = parts[0];
                String phanSau = parts[1];
                if (phanSau.length() == 3) {
                    return Math.round(Double.parseDouble(phanNguyen + phanSau));
                }
                if (phanSau.length() > 3) {
                    return Math.round(Double.parseDouble(phanNguyen + phanSau.substring(0, 3)));
                }
                return Math.round(Double.parseDouble(raw) * 1000.0);
            }

            
            long n = Long.parseLong(parts[0]);
            return (n > 0 && n < 10000) ? n * 1000.0 : n;
        }

        long n = Long.parseLong(raw);
        return (n > 0 && n < 10000) ? n * 1000.0 : n;
    }

    private String dinhDangTienNhap(double value) {
        return String.format(java.util.Locale.US, "%,d", Math.round(value)).replace(',', '.');
    }

    private void ganSuKienDinhDangTien(JTextField field) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                try {
                    field.setText(dinhDangTienNhap(docTienNhap(field.getText())));
                } catch (Exception ignored) {
                }
            }
        });
    }

    private double docDoubleAnToan(String text) {
        try { return Double.parseDouble(text == null || text.trim().isEmpty() ? "0" : text.trim()); }
        catch (Exception e) { return 0; }
    }

    private void addDialogRow(JPanel form, int row, int col, String labelText, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = row;
        gbc.gridx = col;
        gbc.weightx = 0;
        JLabel label = taoLightDialogLabel(labelText);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        form.add(label, gbc);
        gbc.gridx = col + 1;
        gbc.weightx = 1;
        field.setPreferredSize(new Dimension(190, 38));
        form.add(field, gbc);
    }

    private boolean damBaoNCCDangHopTac(String maNCC) {
        if (maNCC == null || maNCC.isBlank()) {
            thongBaoCanhBao("Thiếu nhà cung cấp", "Sản phẩm chưa có mã nhà cung cấp để nhập hàng.");
            return false;
        }

        String trangThai = layTrangThaiNCC(maNCC);
        if (trangThai == null || trangThai.isBlank() || "Đang hợp tác".equalsIgnoreCase(trangThai)) {
            return true;
        }

        if ("Ngừng hợp tác".equalsIgnoreCase(trangThai)) {
            boolean dongYHopTacLai = AppDialog.showOkCancelConfirm(
                    this,
                    "Nhà cung cấp đã ngừng hợp tác",
                    "NCC này hiện đã ngừng hợp tác, bạn có muốn hợp tác lại không?"
            );

            if (!dongYHopTacLai) {
                return false;
            }

            try {
                NhaCungCap.capNhatTrangThaiSQL(maNCC, "Đang hợp tác");
                return true;
            } catch (Exception ex) {
                thongBaoLoi("Lỗi cập nhật NCC", "Không thể chuyển nhà cung cấp về trạng thái đang hợp tác: " + ex.getMessage());
                return false;
            }
        }

        return true;
    }

    private String layTrangThaiNCC(String maNCC) {
        if (maNCC == null || maNCC.isBlank()) return "";
        String sql = "SELECT ISNULL(trangThai, N'Đang hợp tác') AS trangThai FROM NhaCungCap WHERE maNCC = ?";
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC.trim().toUpperCase());
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("trangThai");
            }
        } catch (Exception ex) {
            thongBaoLoi("Lỗi đọc NCC", "Không kiểm tra được trạng thái nhà cung cấp: " + ex.getMessage());
        }
        return "";
    }

    private String layNguoiLienHeNCC(String maNCC) {
        if (maNCC == null || maNCC.isBlank()) return "";
        String sql = "SELECT nguoiLienHe FROM NhaCungCap WHERE maNCC = ?";
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC.trim().toUpperCase());
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("nguoiLienHe");
            }
        } catch (Exception ignored) { }
        return "";
    }

    private void thongBaoThanhCong(String message) {
        AppDialog.showSuccess(this, "Thông báo", message);
    }

    private void thongBaoCanhBao(String title, String message) {
        AppDialog.showWarning(this, title, message);
    }

    private void thongBaoLoi(String title, String message) {
        AppDialog.showError(this, title, message);
    }

    private boolean xacNhanHanhDong(String title, String message) {
        return AppDialog.showConfirm(this, title, message);
    }


    public void lamMoiDuLieuTuSQL() {
        if (txtTimKiem != null) {
            txtTimKiem.setText("");
        }
        if (cboTrangThaiLoc != null) cboTrangThaiLoc.setSelectedItem("Tất cả");

        danhSachSanPham = SanPham.docTuSQL();
        capNhatBang();
        xoaRongForm();
    }

    private void dongBoTongQuanSauThayDoiSanPham() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainFrame) {
            ((MainFrame) window).lamMoiTongQuanHeThong();
        }
    }

    private void xoaRongForm() {
        maSanPhamDaChon = null;
        kichCoDaChon = null;
        tableSanPham.clearSelection();

        txtTenSanPham.setText("");
        txtKichCo.setText("M");
        txtDonVi.setText("cái");
        txtGiaNhap.setText("0");
        setGiaNhapEditable(false);
        txtGiaBan.setText("0");
        txtSoLuongTon.setText("0");
        setSoLuongTonEditable(false);
        txtLoaiSanPham.setText("");
        txtThuongHieu.setText("");
        txtXuatXu.setText("");
        txtMaNCC.setText("");
        setMaNCCEditable(true);
        txtGiamGiaBan.setText("0");

        txtTenSanPham.requestFocus();
    }

    private void capNhatBang() {
        tableModel.setRowCount(0);

        for (SanPham sp : danhSachSanPham) {
            if (!phuHopTrangThaiLoc(sp) || !phuHopKichCoLoc(sp)) continue;
            Object[] row = {
                    sp.getMaSanPham(),
                    sp.getTenSanPham(),
                    sp.getKichCo(),
                    sp.getDonVi(),
                    dinhDangTienNhap(sp.getGiaBan()),
                    sp.getGiamGiaBan(),
                    sp.getSoLuongTon(),
                    sp.getSoLuongDaBan(),
                    sp.getLoaiSanPham(),
                    sp.getThuongHieu(),
                    sp.getXuatXu(),
                    sp.getMaNCC(),
                    sp.getTrangThaiHienThi()
            };

            tableModel.addRow(row);
        }
        datDoRongCotSanPham();
        capNhatSoKetQua();
    }
}