package GUI;
 
import model.KhachHang;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
 
public class KhachHangPanel extends JPanel {
 
    private JTextField txtHoTen;
    private JTextField txtSoDienThoai;
    private JTextField txtDiemTichLuy;
    private JTextField txtTimKiem;
 
    private JTable tableKhachHang;
    private DefaultTableModel tableModel;
    private JLabel lblSoKetQua;
 
    private List<KhachHang> danhSachKhachHang;
    private String maKhachHangDaChon = null;
 
    private String currentVaiTro = "ADMIN";
 
    private boolean laAdmin() {
        return "ADMIN".equalsIgnoreCase(currentVaiTro);
    }
 
    private boolean khongDuQuyen(String hanhDong) {
        if (!laAdmin()) {
            AppDialog.showWarning(
                    this,
                    "Không đủ quyền",
                    "Tài khoản nhân viên được thêm và sửa khách hàng, nhưng không được " + hanhDong + "."
            );
            return true;
        }
        return false;
    }
 
    private static final Color BG_PANEL = Color.WHITE;
    private static final Color CARD_DARK = Color.WHITE;
    private static final Color INPUT_DARK = Color.WHITE;
    private static final Color TABLE_DARK = Color.WHITE;
    private static final Color TABLE_DARK_2 = new Color(248, 250, 252);
    private static final Color GOLD_LIGHT = Color.BLACK;
    private static final Color GOLD_DARK = new Color(37, 99, 235);
    private static final Color TEXT_LIGHT = Color.BLACK;
    private static final Color BORDER = Color.BLACK;
 
    public KhachHangPanel() {
        this("ADMIN");
    }
 
    public KhachHangPanel(String vaiTro) {
        this.currentVaiTro = vaiTro == null || vaiTro.isBlank() ? "STAFF" : vaiTro.trim().toUpperCase();
 
        danhSachKhachHang = KhachHang.docTuSQL();
 
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_PANEL);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
 
        taoGiaoDien();
        ganSuKien();
        capNhatBang();
    }
 
    private void taoGiaoDien() {
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlTimKiem.setBackground(CARD_DARK);
        pnlTimKiem.setBorder(taoTitledBorder("Tìm kiếm khách hàng"));
 
        pnlTimKiem.add(taoLabel("Tìm kiếm Mã/Tên/SĐT:"));
        txtTimKiem = taoTextField(25);
 
        JButton btnTimKiem = taoNut("Tìm Kiếm", new Color(218, 174, 88));
        JButton btnHienTatCa = taoNut("Hiển Thị Tất Cả", new Color(120, 105, 85));
 
        pnlTimKiem.add(txtTimKiem);
        pnlTimKiem.add(btnTimKiem);
        pnlTimKiem.add(btnHienTatCa);
 
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(CARD_DARK);
        pnlInput.setBorder(taoTitledBorder("Thông tin khách hàng"));
 
        txtHoTen = taoTextField();
        txtSoDienThoai = taoTextField();
        txtDiemTichLuy = taoTextField("0");
 
        addInputRow(pnlInput, 0, "Họ tên:", txtHoTen);
        addInputRow(pnlInput, 1, "Số điện thoại:", txtSoDienThoai);
        addInputRow(pnlInput, 2, "Điểm tích lũy:", txtDiemTichLuy);
 
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlButtons.setBackground(CARD_DARK);
 
        JButton btnThem = taoNut("Thêm Khách Hàng", new Color(70, 145, 210));
        JButton btnSua = taoNut("Sửa Thông Tin", new Color(218, 174, 88));
        JButton btnXoa = taoNut("Xóa Khách Hàng", new Color(210, 65, 55));
        JButton btnClear = taoNut("Làm Mới Form", new Color(120, 105, 85));
 
        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        if (laAdmin()) {
            pnlButtons.add(btnXoa);
        }
        pnlButtons.add(btnClear);
 
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setBackground(BG_PANEL);
        pnlTop.add(pnlTimKiem, BorderLayout.NORTH);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlButtons, BorderLayout.SOUTH);
 
        add(pnlTop, BorderLayout.NORTH);
 
        String[] columnNames = {
                "Mã KH", "Họ Tên", "SĐT", "Điểm Tích Lũy"
        };
 
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
 
        tableKhachHang = new JTable(tableModel);
        tableKhachHang.setRowHeight(28);
        tableKhachHang.setBackground(TABLE_DARK);
        tableKhachHang.setForeground(TEXT_LIGHT);
        tableKhachHang.setGridColor(BORDER);
        tableKhachHang.setSelectionBackground(GOLD_DARK);
        tableKhachHang.setSelectionForeground(Color.WHITE);
        tableKhachHang.setShowVerticalLines(true);
        tableKhachHang.setShowHorizontalLines(true);
        tableKhachHang.getTableHeader().setBackground(CARD_DARK);
        tableKhachHang.getTableHeader().setForeground(GOLD_LIGHT);
        tableKhachHang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableKhachHang.getTableHeader().setReorderingAllowed(false);
        canGiuaHeaderVaFixKhoangTrangBang(tableKhachHang);
        tableKhachHang.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
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
                setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                return this;
            }
        });
        tableKhachHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableKhachHang.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        datDoRongCotKhachHang();
 
        JScrollPane scrollPane = taoScrollPane(tableKhachHang, "Danh sách khách hàng");
        lblSoKetQua = taoLabelSoKetQua();
        JPanel tableArea = new JPanel(new BorderLayout(0, 6));
        tableArea.setBackground(BG_PANEL);
        tableArea.add(scrollPane, BorderLayout.CENTER);
        tableArea.add(lblSoKetQua, BorderLayout.SOUTH);
        add(tableArea, BorderLayout.CENTER);
 
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
 
        btnHienTatCa.addActionListener(e -> lamMoiDuLieuTuSQL());
 
        btnThem.addActionListener(this::xuLyThemKhachHang);
        btnSua.addActionListener(this::xuLySuaKhachHang);
        btnXoa.addActionListener(this::xuLyXoaKhachHang);
        btnClear.addActionListener(e -> xoaRongForm());
    }
 
    private void addInputRow(JPanel panel, int row, String labelText, JTextField field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = row;
        gbc.insets = new Insets(6, 20, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
 
        JLabel label = taoLabel(labelText);
        label.setBorder(new EmptyBorder(0, 0, 0, 6));
        label.setPreferredSize(new Dimension(150, 32));
 
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(label, gbc);
 
        field.setPreferredSize(new Dimension(520, 36));
        field.setMinimumSize(new Dimension(300, 34));
 
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 20);
        panel.add(field, gbc);
    }
 
    private void ganSuKien() {
        tableKhachHang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableKhachHang.getSelectedRow();
 
                if (selectedRow >= 0) {
                    maKhachHangDaChon = tableModel
                            .getValueAt(selectedRow, 0)
                            .toString();
 
                    for (KhachHang kh : danhSachKhachHang) {
                        if (kh.getMaDinhDanh().equals(maKhachHangDaChon)) {
                            napKhachHangLenForm(kh);
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
        txt.setCaretColor(Color.BLACK);
        txt.setOpaque(true);
        txt.setPreferredSize(new Dimension(columns > 0 ? 260 : 520, 36));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
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
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
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
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setBackground(TABLE_DARK);
        scrollPane.getHorizontalScrollBar().setBackground(TABLE_DARK);
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
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

    private void datDoRongCotKhachHang() {
        if (tableKhachHang == null || tableKhachHang.getColumnModel().getColumnCount() < 4) return;
        int[] widths = {180, 520, 240, 180};
        for (int i = 0; i < widths.length; i++) {
            tableKhachHang.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            tableKhachHang.getColumnModel().getColumn(i).setMinWidth(100);
        }
    }

    private JButton taoNut(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setBackground(color);
 
        boolean isDark = color.getRed() < 200 || (color.getRed() < 230 && color.getGreen() < 150);
        Color textColor = isDark ? Color.WHITE : Color.BLACK;
        btn.setForeground(textColor);
 
        Icon icon = taoIconNut(text, textColor);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(8);
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
 
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setMargin(new Insets(9, 18, 9, 18));
        btn.setPreferredSize(new Dimension(Math.max(165, text.length() * 10 + 66), 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        return btn;
    }
 
    private Icon taoIconNut(String text, Color color) {
        String lower = text == null ? "" : text.toLowerCase();
 
        ButtonIconType type;
        if (lower.contains("tìm")) {
            type = ButtonIconType.SEARCH;
        } else if (lower.contains("hiển thị") || lower.contains("tất cả")) {
            type = ButtonIconType.LIST;
        } else if (lower.contains("thêm")) {
            type = ButtonIconType.ADD;
        } else if (lower.contains("sửa")) {
            type = ButtonIconType.EDIT;
        } else if (lower.contains("xóa")) {
            type = ButtonIconType.DELETE;
        } else if (lower.contains("làm mới")) {
            type = ButtonIconType.REFRESH;
        } else {
            type = ButtonIconType.NONE;
        }
 
        return type == ButtonIconType.NONE ? null : new ButtonActionIcon(type, color, 16);
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
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
 
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 
            switch (type) {
                case SEARCH -> drawSearch(g2, x, y);
                case LIST -> drawList(g2, x, y);
                case ADD -> drawAdd(g2, x, y);
                case EDIT -> drawEdit(g2, x, y);
                case DELETE -> drawDelete(g2, x, y);
                case REFRESH -> drawRefresh(g2, x, y);
                default -> {
                }
            }
 
            g2.dispose();
        }
 
        private void drawSearch(Graphics2D g2, int x, int y) {
            g2.drawOval(x + 2, y + 2, 8, 8);
            g2.drawLine(x + 9, y + 9, x + 14, y + 14);
        }
 
        private void drawList(Graphics2D g2, int x, int y) {
            g2.drawLine(x + 4, y + 4, x + 14, y + 4);
            g2.drawLine(x + 4, y + 8, x + 14, y + 8);
            g2.drawLine(x + 4, y + 12, x + 14, y + 12);
            g2.fillOval(x + 1, y + 3, 2, 2);
            g2.fillOval(x + 1, y + 7, 2, 2);
            g2.fillOval(x + 1, y + 11, 2, 2);
        }
 
        private void drawAdd(Graphics2D g2, int x, int y) {
            g2.drawLine(x + 8, y + 2, x + 8, y + 14);
            g2.drawLine(x + 2, y + 8, x + 14, y + 8);
        }
 
        private void drawEdit(Graphics2D g2, int x, int y) {
            Polygon pencil = new Polygon();
            pencil.addPoint(x + 3, y + 12);
            pencil.addPoint(x + 11, y + 4);
            pencil.addPoint(x + 13, y + 6);
            pencil.addPoint(x + 5, y + 14);
            g2.drawPolygon(pencil);
            g2.drawLine(x + 10, y + 3, x + 13, y + 6);
            g2.fillOval(x + 3, y + 12, 2, 2);
        }
 
        private void drawDelete(Graphics2D g2, int x, int y) {
            g2.drawOval(x + 2, y + 2, 12, 12);
            g2.drawLine(x + 5, y + 5, x + 11, y + 11);
            g2.drawLine(x + 11, y + 5, x + 5, y + 11);
        }
 
        private void drawRefresh(Graphics2D g2, int x, int y) {
            g2.drawArc(x + 2, y + 3, 11, 10, 35, 285);
            Polygon arrow = new Polygon();
            arrow.addPoint(x + 12, y + 2);
            arrow.addPoint(x + 15, y + 3);
            arrow.addPoint(x + 12, y + 6);
            g2.fillPolygon(arrow);
        }
    }
 
    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
 
        if (keyword.isEmpty()) {
            AppDialog.showError(this, "Lỗi nhập liệu", "Vui lòng nhập từ khóa tìm kiếm");
            return;
        } else {
            danhSachKhachHang = KhachHang.timKiemSQL(keyword);
        }
 
        capNhatBang();
    }
 
    private void xuLyThemKhachHang(ActionEvent e) {
        try {
            // 1. Bắt buộc nhập đầy đủ thông tin trước
            kiemTraThongTinBatBuocKhachHang();
 
            String sdtMoi = txtSoDienThoai.getText().trim();
 
            // 2. Reload lại danh sách từ SQL để kiểm tra trùng bằng dữ liệu mới nhất
            danhSachKhachHang = KhachHang.docTuSQL();
 
            // 3. Kiểm tra trùng SĐT trước khi tạo khách hàng mới
            if (soDienThoaiDaTonTai(sdtMoi, null)) {
                AppDialog.showWarning(
                        this,
                        "Khách hàng đã tồn tại",
                        "Số điện thoại đã tồn tại."
                );
                return;
            }
 
            KhachHang kh = new KhachHang();
 
            kh.setHoTen(txtHoTen.getText().trim());
            kh.setSoDienThoai(sdtMoi);
            kh.setDiemTichLuy(docDiemTichLuy());
 
            kh.luuVaoSQL();
 
            danhSachKhachHang = KhachHang.docTuSQL();
            capNhatBang();
            xoaRongForm();
 
            AppDialog.showSuccess(this, "Thông báo", "Thêm khách hàng thành công!");
 
        } catch (NumberFormatException ex) {
            AppDialog.showError(this, "Lỗi nhập liệu", "Điểm tích lũy phải là số nguyên!");
 
        } catch (IllegalArgumentException ex) {
            AppDialog.showError(this, "Lỗi nhập liệu", ex.getMessage());
 
        } catch (Exception ex) {
            AppDialog.showError(this, "Lỗi", "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
    private void xuLySuaKhachHang(ActionEvent e) {
        if (maKhachHangDaChon == null) {
            AppDialog.showWarning(this, "Thông báo", "Vui lòng chọn khách hàng cần sửa!");
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
 
            if (kh == null) {
                AppDialog.showError(this, "Lỗi", "Không tìm thấy khách hàng đã chọn!");
                return;
            }
 
            // Khi sửa cũng bắt buộc nhập đầy đủ và không cho đổi sang SĐT của khách hàng khác
            kiemTraThongTinBatBuocKhachHang();
 
            String sdtMoi = txtSoDienThoai.getText().trim();
            danhSachKhachHang = KhachHang.docTuSQL();
 
            if (soDienThoaiDaTonTai(sdtMoi, maKhachHangDaChon)) {
                AppDialog.showWarning(
                        this,
                        "Số điện thoại đã tồn tại",
                        "Số điện thoại đã tồn tại."
                );
                return;
            }
 
            kh.setHoTen(txtHoTen.getText().trim());
            kh.setSoDienThoai(sdtMoi);
            kh.setDiemTichLuy(docDiemTichLuy());
 
            kh.capNhatSQL();
 
            danhSachKhachHang = KhachHang.docTuSQL();
            capNhatBang();
            xoaRongForm();
 
            AppDialog.showSuccess(this, "Thông báo", "Sửa thông tin khách hàng thành công!");
 
        } catch (NumberFormatException ex) {
            AppDialog.showError(this, "Lỗi nhập liệu", "Điểm tích lũy phải là số nguyên!");
 
        } catch (IllegalArgumentException ex) {
            AppDialog.showError(this, "Lỗi nhập liệu", ex.getMessage());
 
        } catch (Exception ex) {
            AppDialog.showError(this, "Lỗi", "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
    private void xuLyXoaKhachHang(ActionEvent e) {
        if (khongDuQuyen("xóa khách hàng")) return;
 
        if (maKhachHangDaChon == null) {
            AppDialog.showWarning(this, "Thông báo", "Vui lòng chọn khách hàng cần xóa!");
            return;
        }
 
        boolean xacNhan = AppDialog.showConfirm(
                this,
                "Xác nhận xóa",
                "Bạn có chắc chắn muốn xóa khách hàng " + maKhachHangDaChon + "?"
        );
 
        if (xacNhan) {
            try {
                KhachHang.xoaKhoiSQL(maKhachHangDaChon);
 
                danhSachKhachHang = KhachHang.docTuSQL();
                capNhatBang();
                xoaRongForm();
 
                AppDialog.showSuccess(this, "Thông báo", "Xóa khách hàng thành công!");
 
            } catch (Exception ex) {
                AppDialog.showError(this, "Lỗi", "Không thể xóa khách hàng. Khách hàng có thể đang được sử dụng trong hóa đơn.");
            }
        }
    }
 
    private void kiemTraThongTinBatBuocKhachHang() {
        
        if (txtHoTen.getText() == null || txtHoTen.getText().trim().isEmpty()
                || txtSoDienThoai.getText() == null || txtSoDienThoai.getText().trim().isEmpty()
                || txtDiemTichLuy.getText() == null || txtDiemTichLuy.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin khách hàng.");
        }

        String sdt = txtSoDienThoai.getText().trim();
        if (!sdt.matches("0[0-9]{9}")) {
            throw new IllegalArgumentException("Sdt gồm 10 chữ số nguyên dương và bắt đầu bằng số 0.");
        }

        int diem = Integer.parseInt(txtDiemTichLuy.getText().trim());
        if (diem < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không được âm.");
        }
    }
 
    private boolean soDienThoaiDaTonTai(String soDienThoai, String maKhachHangBoQua) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return false;
        }
 
        String sdt = soDienThoai.trim();
 
        if (danhSachKhachHang == null) {
            danhSachKhachHang = KhachHang.docTuSQL();
        }
 
        for (KhachHang kh : danhSachKhachHang) {
            if (kh == null || kh.getSoDienThoai() == null) {
                continue;
            }
 
            boolean trungSdt = sdt.equals(kh.getSoDienThoai().trim());
            boolean laKhachDangSua = maKhachHangBoQua != null
                    && kh.getMaDinhDanh() != null
                    && maKhachHangBoQua.equalsIgnoreCase(kh.getMaDinhDanh());
 
            if (trungSdt && !laKhachDangSua) {
                return true;
            }
        }
 
        return false;
    }
 
    private int docDiemTichLuy() {
        String diemStr = txtDiemTichLuy.getText();
 
        if (diemStr == null || diemStr.trim().isEmpty()) {
            return 0;
        }
 
        return Integer.parseInt(diemStr.trim());
    }
 
    /**
     * Reload lại khách hàng trực tiếp từ SQL.
     * Dùng cho nút "Làm Mới Form" để lấy điểm tích lũy mới nhất
     * sau khi hóa đơn đã cộng/trừ điểm.
     */
    public void lamMoiDuLieuTuSQL() {
        String maDangChon = maKhachHangDaChon;
 
        if (txtTimKiem != null) {
            txtTimKiem.setText("");
        }
 
        danhSachKhachHang = KhachHang.docTuSQL();
        capNhatBang();
 
        if (maDangChon != null && chonKhachHangTrongBang(maDangChon)) {
            return;
        }
 
        xoaRongForm();
    }
 
    private boolean chonKhachHangTrongBang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.isBlank()) {
            return false;
        }
 
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, 0);
            if (value != null && maKhachHang.equalsIgnoreCase(value.toString())) {
                tableKhachHang.setRowSelectionInterval(i, i);
                tableKhachHang.scrollRectToVisible(tableKhachHang.getCellRect(i, 0, true));
 
                maKhachHangDaChon = maKhachHang;
 
                for (KhachHang kh : danhSachKhachHang) {
                    if (maKhachHang.equalsIgnoreCase(kh.getMaDinhDanh())) {
                        napKhachHangLenForm(kh);
                        break;
                    }
                }
 
                return true;
            }
        }
 
        return false;
    }
 
    private void napKhachHangLenForm(KhachHang kh) {
        if (kh == null) {
            return;
        }
 
        txtHoTen.setText(kh.getHoTen() != null ? kh.getHoTen() : "");
        txtSoDienThoai.setText(kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "");
        txtDiemTichLuy.setText(String.valueOf(kh.getDiemTichLuy()));
    }
 
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
 
        // Khi chuyển từ màn hình hóa đơn sang khách hàng, tự reload lại dữ liệu.
        // Nhờ vậy điểm tích lũy mới không bị giữ theo danh sách cũ trong RAM.
        if (aFlag && tableModel != null) {
            SwingUtilities.invokeLater(this::lamMoiDuLieuTuSQL);
        }
    }
 
    private void xoaRongForm() {
        maKhachHangDaChon = null;
        tableKhachHang.clearSelection();
 
        txtHoTen.setText("");
        txtSoDienThoai.setText("");
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
                    kh.getDiemTichLuy()
            };
 
            tableModel.addRow(row);
        }
        datDoRongCotKhachHang();
        capNhatSoKetQua();
    }
}
