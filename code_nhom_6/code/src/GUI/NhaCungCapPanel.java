package GUI;
 
import model.NhaCungCap;
import database.DBConnection;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.Normalizer;
import java.util.List;
 
public class NhaCungCapPanel extends JPanel {
 
    private JTextField txtTenCongTy;
    private JTextField txtSoDienThoai;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JTextField txtNguoiLienHe;
    private JTextField txtMoTa;
    private JTextField txtTimKiem;
    private JComboBox<String> cboTrangThaiLoc;
 
    private JTable tableNhaCungCap;
    private DefaultTableModel tableModel;
    private JLabel lblSoKetQua;
 
    private List<NhaCungCap> danhSachNCC;
    private String maNCCDaChon = null;
    private String currentVaiTro = "ADMIN";

    private boolean laAdmin() { return "ADMIN".equalsIgnoreCase(currentVaiTro); }
    private boolean laNhanVienKho() { return "WAREHOUSE".equalsIgnoreCase(currentVaiTro); }

    private boolean chanNeuKhongPhaiQuanLy(String hanhDong) {
        if (!laAdmin()) {
            AppDialog.showWarning(
                    this,
                    "Không đủ quyền",
                    "Nhân viên kho chỉ được xem chi tiết nhập hàng, không được " + hanhDong + "."
            );
            return true;
        }
        return false;
    }
 
    private static final Color BG_PANEL = Color.WHITE;
    private static final Color CARD_DARK = Color.WHITE;
    private static final Color CARD_DARK_2 = new Color(248, 250, 252);
    private static final Color INPUT_DARK = Color.WHITE;
    private static final Color TABLE_DARK = Color.WHITE;
    private static final Color TABLE_DARK_2 = new Color(248, 250, 252);
    private static final Color GOLD_LIGHT = Color.BLACK;
    private static final Color GOLD = new Color(218, 174, 88);
    private static final Color GOLD_DARK = Color.BLACK;
    private static final Color TEXT_LIGHT = Color.BLACK;
    private static final Color TEXT_MUTED = new Color(45, 45, 45);
    private static final Color BORDER = Color.BLACK;
    private static final Color TABLE_HEADER_BG = Color.WHITE;
    private static final Color TABLE_SELECTED_BG = new Color(216, 224, 236);
 
    public NhaCungCapPanel() {
        this("ADMIN");
    }

    public NhaCungCapPanel(String vaiTro) {
        this.currentVaiTro = vaiTro == null || vaiTro.isBlank() ? "STAFF" : vaiTro.trim().toUpperCase();
        danhSachNCC = NhaCungCap.docTuSQL();
 
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_PANEL);
        setBorder(new EmptyBorder(10, 10, 10, 10));
 
        taoGiaoDien();
        ganSuKien();
        capNhatBang();
    }
 
    private void taoGiaoDien() {
        JPanel pnlTimKiem = new JPanel(new GridBagLayout());
        pnlTimKiem.setBackground(CARD_DARK);
        pnlTimKiem.setBorder(taoTitledBorder("Tìm kiếm nhà cung cấp"));
        GridBagConstraints gbcTk = new GridBagConstraints();
        gbcTk.gridy = 0;
        gbcTk.insets = new Insets(6, 8, 6, 8);
        gbcTk.anchor = GridBagConstraints.WEST;
        gbcTk.fill = GridBagConstraints.HORIZONTAL;
 
        txtTimKiem = taoTextField(25);
 
        JButton btnTimKiem = taoNut("Tìm Kiếm", new Color(218, 174, 88));
        JButton btnHienTatCa = taoNut("Hiển Thị Tất Cả", new Color(120, 105, 85));
        cboTrangThaiLoc = new JComboBox<>(new String[]{"Tất cả", "Đang hợp tác", "Ngừng hợp tác"});
        cboTrangThaiLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cboTrangThaiLoc.setPreferredSize(new Dimension(165, 36));
 
        gbcTk.gridx = 0; gbcTk.weightx = 0; pnlTimKiem.add(taoLabel("Tìm kiếm Mã/Tên/SĐT/Email/Người liên hệ:"), gbcTk);
        gbcTk.gridx = 1; gbcTk.weightx = 1; pnlTimKiem.add(txtTimKiem, gbcTk);
        gbcTk.gridx = 2; gbcTk.weightx = 0; pnlTimKiem.add(btnTimKiem, gbcTk);
        gbcTk.gridx = 3; pnlTimKiem.add(btnHienTatCa, gbcTk);
        gbcTk.gridx = 4; pnlTimKiem.add(taoLabel("Sắp xếp theo:"), gbcTk);
        gbcTk.gridx = 5; pnlTimKiem.add(cboTrangThaiLoc, gbcTk);
 
        JPanel pnlInput = new JPanel(new GridLayout(3, 4, 10, 10));
        pnlInput.setBackground(CARD_DARK);
        pnlInput.setBorder(taoTitledBorder("Thông tin nhà cung cấp"));
 
        pnlInput.add(taoLabel("Tên công ty:"));
        txtTenCongTy = taoTextField();
        pnlInput.add(txtTenCongTy);
 
        pnlInput.add(taoLabel("Số điện thoại:"));
        txtSoDienThoai = taoTextField();
        pnlInput.add(txtSoDienThoai);
 
        pnlInput.add(taoLabel("Email:"));
        txtEmail = taoTextField();
        pnlInput.add(txtEmail);
 
        pnlInput.add(taoLabel("Địa chỉ:"));
        txtDiaChi = taoTextField();
        pnlInput.add(txtDiaChi);
 
        pnlInput.add(taoLabel("Người liên hệ:"));
        txtNguoiLienHe = taoTextField();
        pnlInput.add(txtNguoiLienHe);
 
        pnlInput.add(taoLabel("Mô tả:"));
        txtMoTa = taoTextField();
        pnlInput.add(txtMoTa);
 
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlButtons.setBackground(CARD_DARK);
 
        JButton btnThem = taoNut("Thêm Nhà Cung Cấp", new Color(70, 145, 210));
        JButton btnSua = taoNut("Sửa Thông Tin", new Color(218, 174, 88));
        JButton btnXoa = taoNut("Ngừng Hợp Tác", new Color(210, 65, 55));
        JButton btnChiTiet = taoNut("Chi Tiết Nhập Hàng", new Color(70, 150, 85));
        JButton btnClear = taoNut("Làm Mới Form", new Color(120, 105, 85));
 
        if (laAdmin()) {
            pnlButtons.add(btnThem);
            pnlButtons.add(btnSua);
            pnlButtons.add(btnXoa);
        }
        pnlButtons.add(btnChiTiet);
        if (laAdmin()) {
            pnlButtons.add(btnClear);
        }
 
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setBackground(BG_PANEL);
        pnlTop.add(pnlTimKiem, BorderLayout.NORTH);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlButtons, BorderLayout.SOUTH);

        if (laNhanVienKho()) {
            khoaFormNhaCungCapChoNhanVienKho();
        }
 
        add(pnlTop, BorderLayout.NORTH);
 
        String[] columnNames = {
                "Mã NCC", "Tên Công Ty", "SĐT", "Email",
                "Địa Chỉ", "Người Liên Hệ", "Mô Tả", "Trạng Thái"
        };
 
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
 
        tableNhaCungCap = new JTable(tableModel);
        tableNhaCungCap.setRowHeight(32);
        tableNhaCungCap.setBackground(Color.WHITE);
        tableNhaCungCap.setForeground(Color.BLACK);
        tableNhaCungCap.setGridColor(Color.BLACK);
        tableNhaCungCap.setSelectionBackground(TABLE_SELECTED_BG);
        tableNhaCungCap.setSelectionForeground(Color.BLACK);
        tableNhaCungCap.setShowGrid(true);
        tableNhaCungCap.setShowVerticalLines(true);
        tableNhaCungCap.setShowHorizontalLines(true);
        tableNhaCungCap.setIntercellSpacing(new Dimension(1, 1));
        tableNhaCungCap.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        tableNhaCungCap.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableNhaCungCap.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
 
        JTableHeader header = tableNhaCungCap.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
 
        canGiuaHeaderVaFixKhoangTrangBang(tableNhaCungCap);
        datDoRongCotBangChinh();
 
        JScrollPane scrollPane = taoScrollPane(tableNhaCungCap, "Danh sách nhà cung cấp");
        lblSoKetQua = taoLabelSoKetQua();
        JPanel tableArea = new JPanel(new BorderLayout(0, 6));
        tableArea.setBackground(BG_PANEL);
        tableArea.add(scrollPane, BorderLayout.CENTER);
        tableArea.add(lblSoKetQua, BorderLayout.SOUTH);
        add(tableArea, BorderLayout.CENTER);
 
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
 
        btnHienTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            if (cboTrangThaiLoc != null) cboTrangThaiLoc.setSelectedItem("Tất cả");
            danhSachNCC = NhaCungCap.docTuSQL();
            capNhatBang();
            xoaRongForm();
        });
 
        btnThem.addActionListener(this::xuLyThem);
        btnSua.addActionListener(this::xuLySua);
        btnXoa.addActionListener(this::xuLyXoa);
        btnChiTiet.addActionListener(e -> xuLyXemChiTietNhapHang());
        btnClear.addActionListener(e -> xoaRongForm());
        if (cboTrangThaiLoc != null) cboTrangThaiLoc.addActionListener(e -> capNhatBang());
    }
 
    private void ganSuKien() {
        tableNhaCungCap.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                capNhatNCCDaChonTuBang();
            }
        });
    }
 
    private void capNhatNCCDaChonTuBang() {
        int selectedRow = tableNhaCungCap.getSelectedRow();
 
        if (selectedRow < 0) {
            return;
        }
 
        int modelRow = tableNhaCungCap.convertRowIndexToModel(selectedRow);
        maNCCDaChon = tableModel.getValueAt(modelRow, 0).toString();
 
        for (NhaCungCap ncc : danhSachNCC) {
            if (ncc.getMaNCC().equals(maNCCDaChon)) {
                txtTenCongTy.setText(ncc.getTenCongTy() != null ? ncc.getTenCongTy() : "");
                txtSoDienThoai.setText(ncc.getSoDienThoai() != null ? ncc.getSoDienThoai() : "");
                txtEmail.setText(ncc.getEmail() != null ? ncc.getEmail() : "");
                txtDiaChi.setText(ncc.getDiaChi() != null ? ncc.getDiaChi() : "");
                txtNguoiLienHe.setText(ncc.getNguoiLienHe() != null ? ncc.getNguoiLienHe() : "");
                txtMoTa.setText(ncc.getMoTa() != null ? ncc.getMoTa() : "");
                break;
            }
        }
    }
 
    private void khoaFormNhaCungCapChoNhanVienKho() {
        txtTenCongTy.setEditable(false);
        txtSoDienThoai.setEditable(false);
        txtEmail.setEditable(false);
        txtDiaChi.setEditable(false);
        txtNguoiLienHe.setEditable(false);
        txtMoTa.setEditable(false);
    }
 
    private void xuLyXemChiTietNhapHang() {
        capNhatNCCDaChonTuBang();
 
        if (maNCCDaChon == null || maNCCDaChon.isBlank()) {
            AppDialog.showWarning(
                    this,
                    "Chưa chọn nhà cung cấp",
                    "Vui lòng chọn một nhà cung cấp trong bảng trước khi bấm Chi Tiết Nhập Hàng."
            );
            return;
        }
 
        String thang = showNhapThangDialog();
 
        if (thang == null) {
            return;
        }
 
        thang = thang.trim();
 
        if (!thang.isBlank() && !thang.matches("(0[1-9]|1[0-2])/\\d{4}")) {
            AppDialog.showWarning(
                    this,
                    "Sai định dạng",
                    "Vui lòng nhập tháng cần lọc theo định dạng MM/yyyy. Ví dụ: 05/2026."
            );
            return;
        }
 
        showChiTietNhaCungCapDialog(maNCCDaChon, thang);
    }
 
    private String showNhapThangDialog() {
        JDialog dialog = taoDialog("Lọc chi tiết nhập hàng", 520, 250);
 
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BG_PANEL);
        root.setBorder(new EmptyBorder(18, 22, 18, 22));
        dialog.setContentPane(root);
 
        JLabel title = new JLabel("Chọn tháng cần xem");
        title.setForeground(GOLD_LIGHT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
 
        JLabel sub = new JLabel("Nhập tháng dạng MM/yyyy. Để trống nếu muốn xem tất cả.");
        sub.setForeground(TEXT_MUTED);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
 
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setOpaque(false);
        header.add(title);
        header.add(sub);
 
        root.add(header, BorderLayout.NORTH);
 
        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setOpaque(false);
 
        JLabel lbl = taoLabel("Tháng nhập hàng:");
        JTextField txtThang = taoTextField("");
        txtThang.setToolTipText("Ví dụ: 05/2026. Để trống nếu xem tất cả.");
 
        center.add(lbl, BorderLayout.NORTH);
        center.add(txtThang, BorderLayout.CENTER);
 
        root.add(center, BorderLayout.CENTER);
 
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        footer.setOpaque(false);
 
        JButton btnXem = taoNutDialog("Xem", true);
        JButton btnHuy = taoNutDialog("Hủy", false);
 
        final String[] result = {null};
 
        btnXem.addActionListener(e -> {
            result[0] = txtThang.getText().trim();
            dialog.dispose();
        });
 
        btnHuy.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });
 
        footer.add(btnXem);
        footer.add(btnHuy);
 
        root.add(footer, BorderLayout.SOUTH);
 
        dialog.getRootPane().setDefaultButton(btnXem);
        dialog.setVisible(true);
 
        return result[0];
    }
 
    private void showChiTietNhaCungCapDialog(String maNCC, String thang) {
        NhaCungCap nccDangChon = timNhaCungCapTheoMa(maNCC);
        String nguoiLienHe = nccDangChon != null ? nccDangChon.getNguoiLienHe() : "";
        String tenCongTy = nccDangChon != null ? nccDangChon.getTenCongTy() : "";
 
        JDialog dialog = taoDialog("Chi tiết nhập hàng nhà cung cấp", 1120, 650);
 
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(BG_PANEL);
        root.setBorder(new EmptyBorder(16, 18, 16, 18));
        dialog.setContentPane(root);
 
        JPanel header = new JPanel(new BorderLayout(12, 8));
        header.setOpaque(false);
 
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
 
        JLabel title = new JLabel("Chi tiết nhập hàng nhà cung cấp");
        title.setForeground(GOLD_LIGHT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JLabel sub = new JLabel("Dữ liệu lấy trực tiếp từ bảng NhapHang, giá bán lấy từ bảng SanPham");
        sub.setForeground(TEXT_MUTED);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(sub);
 
        header.add(titleBox, BorderLayout.WEST);
 
        root.add(header, BorderLayout.NORTH);
 
        JPanel info = new JPanel(new GridLayout(1, 4, 12, 0));
        info.setOpaque(false);
        info.add(taoInfoCard("Mã NCC", maNCC));
        info.add(taoInfoCard("Tên công ty", tenCongTy));
        info.add(taoInfoCard("Người liên hệ", nguoiLienHe));
        info.add(taoInfoCard("Tháng xem", thang == null || thang.isBlank() ? "Tất cả" : thang));
 
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(info, BorderLayout.NORTH);
 
        String[] cols = {
                "Mã NCC", "Người liên hệ", "Mã SP", "Tên SP", "Kích cỡ",
                "Số lượng nhập", "Giá nhập", "Giá bán", "Ngày nhập"
        };
 
        DefaultTableModel detailModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
 
        JTable detailTable = new JTable(detailModel);
        detailTable.setRowHeight(32);
        detailTable.setBackground(Color.WHITE);
        detailTable.setForeground(Color.BLACK);
        detailTable.setGridColor(Color.BLACK);
        detailTable.setSelectionBackground(TABLE_SELECTED_BG);
        detailTable.setSelectionForeground(Color.BLACK);
        detailTable.setShowGrid(true);
        detailTable.setShowVerticalLines(true);
        detailTable.setShowHorizontalLines(true);
        detailTable.setIntercellSpacing(new Dimension(1, 1));
        detailTable.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        detailTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
 
        canGiuaHeaderVaFixKhoangTrangBang(detailTable);
        canGiuaToanBoNoiDungBang(detailTable);
        napChiTietNhapHang(detailModel, maNCC, thang);
 
        if (detailModel.getRowCount() == 0) {
            detailModel.addRow(new Object[]{
                    maNCC,
                    nguoiLienHe,
                    "",
                    "Không có dữ liệu nhập hàng trong tháng đã chọn",
                    "",
                    "",
                    "",
                    "",
                    thang == null || thang.isBlank() ? "Tất cả" : thang
            });
        }
 
        datDoRongCotChiTiet(detailTable);
 
        JScrollPane scroll = taoScrollPane(detailTable, "Danh sách sản phẩm nhập từ nhà cung cấp");
        center.add(scroll, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
 
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        footer.setOpaque(false);
 
        JButton btnIn = taoNutDialog("In hóa đơn", true);
        btnIn.setPreferredSize(new Dimension(170, 40));
        btnIn.setIcon(new ActionButtonIcon(IconType.PRINT, 16));
        btnIn.setIconTextGap(8);
        btnIn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnIn.addActionListener(e -> xuatChiTietNhaCungCapPDF(detailModel, maNCC, tenCongTy, nguoiLienHe, thang));

        JButton btnDong = taoNutDialog("Đóng", true);
        btnDong.setPreferredSize(new Dimension(150, 40));
        btnDong.addActionListener(e -> dialog.dispose());

        footer.add(btnIn);
        footer.add(btnDong);
        root.add(footer, BorderLayout.SOUTH);
 
        dialog.setVisible(true);
    }
 

    private void xuatChiTietNhaCungCapPDF(DefaultTableModel model,
                                          String maNCC,
                                          String tenCongTy,
                                          String nguoiLienHe,
                                          String thang) {
        try {
            String noiDung = taoNoiDungInChiTietNhaCungCap(model, maNCC, tenCongTy, nguoiLienHe, thang);

            String maThuMuc = maNCC == null || maNCC.isBlank() ? "NCC" : maNCC.replaceAll("[^A-Za-z0-9_-]", "_");
            String thuMucCon = (thang == null || thang.isBlank())
                    ? "tonghop"
                    : thang.trim().replace("/", "-").replaceAll("[^0-9-]", "_");
            File dir = new File(System.getProperty("user.dir"),
                    "nhacungcap" + File.separator + maThuMuc + File.separator + thuMucCon);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String thoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss_SSS"));
            String maFile = maNCC == null ? "NCC" : maNCC.replaceAll("[^A-Za-z0-9_-]", "_");
            File out = new File(dir, maFile + "_" + thoiGian + ".pdf");

            PdfExporter.exportTextToPdf(out, "CHI TIẾT NHẬP HÀNG NHÀ CUNG CẤP", noiDung);

            AppDialog.showSuccess(
                    this,
                    "In hóa đơn",
                    "In chi tiết nhập hàng nhà cung cấp thành công."
            );

        } catch (Exception ex) {
            AppDialog.showError(
                    this,
                    "Lỗi in hóa đơn",
                    "Không thể in chi tiết nhập hàng.\nLỗi: " + ex.getMessage()
            );
        }
    }

    private String taoNoiDungInChiTietNhaCungCap(DefaultTableModel model,
                                                 String maNCC,
                                                 String tenCongTy,
                                                 String nguoiLienHe,
                                                 String thang) {
        StringBuilder sb = new StringBuilder();

        sb.append("BILUXURY FASHION\n");
        sb.append("CHI TIẾT NHẬP HÀNG NHÀ CUNG CẤP\n");
        sb.append("Mã NCC: ").append(maNCC == null ? "" : maNCC).append("\n");
        sb.append("Tên công ty: ").append(tenCongTy == null ? "" : tenCongTy).append("\n");
        sb.append("Người liên hệ: ").append(nguoiLienHe == null ? "" : nguoiLienHe).append("\n");
        sb.append("Tháng xem: ").append(thang == null || thang.isBlank() ? "Tất cả" : thang).append("\n");
        sb.append("Ngày in: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");

        int soDong = model == null ? 0 : model.getRowCount();
        int tongSoLuong = 0;

        if (model != null) {
            for (int r = 0; r < model.getRowCount(); r++) {
                try {
                    tongSoLuong += Integer.parseInt(layGiaTriBang(model, r, 5).replaceAll("[^0-9]", ""));
                } catch (Exception ignored) {
                }
            }
        }

        sb.append("Số dòng nhập: ").append(soDong).append("\n");
        sb.append("Tổng số lượng nhập: ").append(tongSoLuong).append("\n");
        sb.append("\n");

       
        if (model != null) {
            for (int r = 0; r < model.getRowCount(); r++) {
                sb.append("NCC_ROW").append("\t")
                        .append(layGiaTriBang(model, r, 0)).append("\t")
                        .append(layGiaTriBang(model, r, 1)).append("\t")
                        .append(layGiaTriBang(model, r, 2)).append("\t")
                        .append(layGiaTriBang(model, r, 3)).append("\t")
                        .append(layGiaTriBang(model, r, 4)).append("\t")
                        .append(layGiaTriBang(model, r, 5)).append("\t")
                        .append(layGiaTriBang(model, r, 6)).append("\t")
                        .append(layGiaTriBang(model, r, 7)).append("\t")
                        .append(layGiaTriBang(model, r, 8)).append("\n");
            }
        }

        return sb.toString();
    }

    private String layGiaTriBang(DefaultTableModel model, int row, int col) {
        Object value = model.getValueAt(row, col);
        return value == null ? "" : value.toString();
    }

    private String catChuoi(String value, int max) {
        if (value == null) {
            return "";
        }

        if (value.length() <= max) {
            return value;
        }

        return value.substring(0, Math.max(0, max - 3)) + "...";
    }

    private void napChiTietNhapHang(DefaultTableModel model, String maNCC, String thang) {
        String sql = """
                SELECT
                    ncc.maNCC,
                    ncc.nguoiLienHe,
                    sp.maSanPham,
                    sp.tenSanPham,
                    ISNULL(nh.kichCo, N'M') AS kichCo,
                    nh.soLuongNhap,
                    nh.giaNhap,
                    sp.giaBan,
                    CONVERT(VARCHAR(10), nh.ngayNhap, 103) + ' ' + CONVERT(VARCHAR(8), nh.ngayNhap, 108) AS ngayNhapText
                FROM NhapHang nh
                INNER JOIN NhaCungCap ncc ON nh.maNCC = ncc.maNCC
                INNER JOIN SanPham sp ON nh.maSanPham = sp.maSanPham
                WHERE ncc.maNCC = ?
                  AND (
                        ? = ''
                        OR RIGHT('0' + CAST(MONTH(nh.ngayNhap) AS VARCHAR(2)), 2)
                           + '/'
                           + CAST(YEAR(nh.ngayNhap) AS VARCHAR(4)) = ?
                  )
                ORDER BY nh.ngayNhap DESC, sp.maSanPham ASC
                """;
 
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            String thangLoc = thang == null ? "" : thang.trim();
 
            ps.setString(1, maNCC);
            ps.setString(2, thangLoc);
            ps.setString(3, thangLoc);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("maNCC"),
                            rs.getString("nguoiLienHe"),
                            rs.getString("maSanPham"),
                            rs.getString("tenSanPham"),
                            rs.getString("kichCo"),
                            rs.getInt("soLuongNhap"),
                            formatMoney(rs.getDouble("giaNhap")),
                            formatMoney(rs.getDouble("giaBan")),
                            rs.getString("ngayNhapText")
                    });
                }
            }
 
        } catch (Exception ex) {
            model.setRowCount(0);
            model.addRow(new Object[]{
                    maNCC,
                    "",
                    "",
                    "Lỗi đọc chi tiết nhập hàng: " + ex.getMessage(),
                    "",
                    "",
                    "",
                    "",
                    thang == null || thang.isBlank() ? "Tất cả" : thang
            });
        }
    }
 
    private NhaCungCap timNhaCungCapTheoMa(String maNCC) {
        if (maNCC == null) return null;
 
        for (NhaCungCap ncc : danhSachNCC) {
            if (maNCC.equalsIgnoreCase(ncc.getMaNCC())) {
                return ncc;
            }
        }
 
        return null;
    }
 
    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
 
        if (keyword.isEmpty()) {
            AppDialog.showError(this, "Lỗi nhập liệu", "Vui lòng nhập từ khóa tìm kiếm");
            return;
        } else {
            danhSachNCC = NhaCungCap.timKiemSQL(keyword);
        }
 
        capNhatBang();
    }
 
 
    private String layText(JTextField txt) {
        return txt == null ? "" : txt.getText().trim();
    }
 
    private void kiemTraNhapDayDuNhaCungCap() {
        String tenCongTy = layText(txtTenCongTy);
        String soDienThoai = layText(txtSoDienThoai);
        String email = layText(txtEmail);
        String diaChi = layText(txtDiaChi);
        String nguoiLienHe = layText(txtNguoiLienHe);
        String moTa = layText(txtMoTa);

        if (tenCongTy.isEmpty() || soDienThoai.isEmpty() || email.isEmpty()
                || diaChi.isEmpty() || nguoiLienHe.isEmpty() || moTa.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin nhà cung cấp.");
        }

        if (!soDienThoai.matches("0[0-9]{9}")) {
            throw new IllegalArgumentException("SĐT gồm 10 số nguyên dương và bắt đầu bằng số 0.");
        }

        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }
    }
 
    private String chuanHoaTenNhaCungCap(String tenCongTy) {
        if (tenCongTy == null) return "";

        String normalized = Normalizer.normalize(tenCongTy, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase()
                .trim();

        return normalized.replaceAll("\\s+", "");
    }

    private boolean tonTaiTenCongTyNhaCungCap(String tenCongTy, String maNCCBoQua) throws Exception {
        String tenCanKiemTra = chuanHoaTenNhaCungCap(tenCongTy);
        if (tenCanKiemTra.isEmpty()) return false;

        String sql = """
                SELECT maNCC, tenCongTy
                FROM NhaCungCap
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maNCC = rs.getString("maNCC");
                String tenDaCo = rs.getString("tenCongTy");

                if (maNCCBoQua != null && maNCC.equalsIgnoreCase(maNCCBoQua)) {
                    continue;
                }

                if (tenCanKiemTra.equals(chuanHoaTenNhaCungCap(tenDaCo))) {
                    return true;
                }
            }
        }

        return false;
    }
 
    private boolean tonTaiSoDienThoaiNhaCungCap(String soDienThoai, String maNCCBoQua) throws Exception {
        String sql = """
                SELECT maNCC
                FROM NhaCungCap
                WHERE soDienThoai = ?
                """;
 
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, soDienThoai);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maNCC = rs.getString("maNCC");
 
                    if (maNCCBoQua == null || !maNCC.equalsIgnoreCase(maNCCBoQua)) {
                        return true;
                    }
                }
            }
        }
 
        return false;
    }
 
    private void xuLyThem(ActionEvent e) {
        if (chanNeuKhongPhaiQuanLy("thêm nhà cung cấp")) return;

        try {
            kiemTraNhapDayDuNhaCungCap();
 
            String tenCongTy = layText(txtTenCongTy);
            if (tonTaiTenCongTyNhaCungCap(tenCongTy, null)) {
                AppDialog.showWarning(
                        this,
                        "Nhà cung cấp đã tồn tại",
                        "NCC đã tồn tại. Vui lòng kiểm tra lại tên nhà cung cấp."
                );
                return;
            }
 
            String soDienThoai = layText(txtSoDienThoai);
 
            if (tonTaiSoDienThoaiNhaCungCap(soDienThoai, null)) {
                AppDialog.showWarning(
                        this,
                        "Nhà cung cấp đã tồn tại",
                        "Số điện thoại " + soDienThoai + " đã thuộc về một nhà cung cấp trong hệ thống. Vui lòng kiểm tra lại."
                );
                return;
            }
 
            NhaCungCap ncc = new NhaCungCap();
            ncc.setTenCongTy(layText(txtTenCongTy));
            ncc.setSoDienThoai(soDienThoai);
            ncc.setEmail(layText(txtEmail));
            ncc.setDiaChi(layText(txtDiaChi));
            ncc.setNguoiLienHe(layText(txtNguoiLienHe));
            ncc.setMoTa(layText(txtMoTa));
            String maNCCMoi = ncc.getMaNCC();
            ncc.luuVaoSQL();

            txtTimKiem.setText("");
            if (cboTrangThaiLoc != null) cboTrangThaiLoc.setSelectedItem("Tất cả");
            danhSachNCC = NhaCungCap.docTuSQL();
            xoaRongForm();
            capNhatBang();
            chonVaCuonDenNhaCungCap(maNCCMoi);
            AppDialog.showSuccess(this, "Thông báo", "Thêm nhà cung cấp thành công!");
        } catch (IllegalArgumentException ex) {
            AppDialog.showError(this, "Lỗi nhập liệu", ex.getMessage());
        } catch (Exception ex) {
            AppDialog.showError(this, "Lỗi", "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
    private void xuLySua(ActionEvent e) {
        if (chanNeuKhongPhaiQuanLy("sửa nhà cung cấp")) return;

        if (maNCCDaChon == null && tableNhaCungCap.getSelectedRow() >= 0) {
            int modelRow = tableNhaCungCap.convertRowIndexToModel(tableNhaCungCap.getSelectedRow());
            maNCCDaChon = tableModel.getValueAt(modelRow, 0).toString();
        }

        if (maNCCDaChon == null) {
            AppDialog.showWarning(this, "Thông báo", "Vui lòng chọn nhà cung cấp cần sửa!");
            return;
        }

        try {
            
            kiemTraNhapDayDuNhaCungCap();

            String soDienThoai = layText(txtSoDienThoai);
            String email = layText(txtEmail);

            if (tonTaiSoDienThoaiNhaCungCap(soDienThoai, maNCCDaChon)) {
                AppDialog.showWarning(
                        this,
                        "Nhà cung cấp đã tồn tại",
                        "Số điện thoại " + soDienThoai + " đã thuộc về một nhà cung cấp khác trong hệ thống. Vui lòng kiểm tra lại."
                );
                return;
            }

            NhaCungCap ncc = timNhaCungCapTheoMa(maNCCDaChon);

            if (ncc == null) {
                AppDialog.showError(this, "Lỗi", "Không tìm thấy nhà cung cấp đã chọn!");
                return;
            }

            ncc.setTenCongTy(layText(txtTenCongTy));
            ncc.setSoDienThoai(soDienThoai);
            ncc.setEmail(email);
            ncc.setDiaChi(layText(txtDiaChi));
            ncc.setNguoiLienHe(layText(txtNguoiLienHe));
            ncc.setMoTa(layText(txtMoTa));

            capNhatNhaCungCapSQL(ncc);

            danhSachNCC = NhaCungCap.docTuSQL();
            capNhatBang();
            xoaRongForm();
            AppDialog.showSuccess(this, "Thông báo", "Sửa nhà cung cấp thành công!");
        } catch (IllegalArgumentException ex) {
            AppDialog.showError(this, "Lỗi nhập liệu", ex.getMessage());
        } catch (Exception ex) {
            AppDialog.showError(this, "Lỗi", "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 

    private void capNhatNhaCungCapSQL(NhaCungCap ncc) throws Exception {
        String sql = """
                UPDATE NhaCungCap
                SET tenCongTy = ?, soDienThoai = ?, email = ?, diaChi = ?, nguoiLienHe = ?, moTa = ?
                WHERE maNCC = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ncc.getTenCongTy());
            ps.setString(2, ncc.getSoDienThoai());
            ps.setString(3, ncc.getEmail());
            ps.setString(4, ncc.getDiaChi());
            ps.setString(5, ncc.getNguoiLienHe());
            ps.setString(6, ncc.getMoTa());
            ps.setString(7, ncc.getMaNCC());

            int rows = ps.executeUpdate();
            if (rows <= 0) {
                throw new Exception("Không tìm thấy nhà cung cấp có mã: " + ncc.getMaNCC());
            }
        }
    }

    private void xuLyXoa(ActionEvent e) {
        if (chanNeuKhongPhaiQuanLy("ngừng hợp tác nhà cung cấp")) return;

        capNhatNCCDaChonTuBang();
 
        if (maNCCDaChon == null) {
            AppDialog.showWarning(this, "Thông báo", "Vui lòng chọn nhà cung cấp cần xóa!");
            return;
        }
 
        boolean xacNhan = AppDialog.showConfirm(
                this,
                "Xác nhận ngừng hợp tác",
                "Bạn có chắc chắn muốn chuyển nhà cung cấp " + maNCCDaChon + " sang trạng thái ngừng hợp tác?"
                        + "\nDữ liệu nhập hàng cũ vẫn được giữ để tra cứu."
        );
 
        if (xacNhan) {
            try {
                NhaCungCap.xoaKhoiSQL(maNCCDaChon);
                danhSachNCC = NhaCungCap.docTuSQL();
                capNhatBang();
                xoaRongForm();
                AppDialog.showSuccess(this, "Thông báo", "Đã chuyển nhà cung cấp sang trạng thái ngừng hợp tác!");
            } catch (Exception ex) {
                AppDialog.showError(this, "Lỗi", "Không thể cập nhật trạng thái nhà cung cấp: " + ex.getMessage());
            }
        }
    }
 
    private void xoaRongForm() {
        maNCCDaChon = null;
        tableNhaCungCap.clearSelection();
        txtTenCongTy.setText("");
        txtSoDienThoai.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        txtNguoiLienHe.setText("");
        txtMoTa.setText("");
        txtTenCongTy.requestFocus();
    }

    private void chonVaCuonDenNhaCungCap(String maNCC) {
        if (maNCC == null || maNCC.isBlank() || tableModel == null || tableNhaCungCap == null) {
            return;
        }

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, 0);
            if (value != null && maNCC.equalsIgnoreCase(value.toString())) {
                int viewRow = tableNhaCungCap.convertRowIndexToView(i);
                if (viewRow >= 0) {
                    tableNhaCungCap.setRowSelectionInterval(viewRow, viewRow);
                    tableNhaCungCap.scrollRectToVisible(tableNhaCungCap.getCellRect(viewRow, 0, true));
                    maNCCDaChon = maNCC;
                }
                return;
            }
        }
    }
 


    private JLabel taoLabelSoKetQua() {
        JLabel label = new JLabel("Số kết quả tìm được: 0", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(11, 23, 54));
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

    private boolean phuHopTrangThaiLoc(NhaCungCap ncc) {
        if (ncc == null || cboTrangThaiLoc == null) return true;
        Object selected = cboTrangThaiLoc.getSelectedItem();
        String loc = selected == null ? "Tất cả" : selected.toString();
        if ("Tất cả".equalsIgnoreCase(loc)) return true;
        return loc.equalsIgnoreCase(ncc.getTrangThai());
    }

    public void lamMoiDuLieuTuSQL() {
        if (cboTrangThaiLoc != null) cboTrangThaiLoc.setSelectedItem("Tất cả");
        danhSachNCC = NhaCungCap.docTuSQL();
        capNhatBang();
    }

    private void capNhatBang() {
        tableModel.setRowCount(0);
        if (danhSachNCC == null) return;
 
        for (NhaCungCap ncc : danhSachNCC) {
            if (!phuHopTrangThaiLoc(ncc)) continue;
            tableModel.addRow(new Object[]{
                    ncc.getMaNCC(),
                    ncc.getTenCongTy(),
                    ncc.getSoDienThoai(),
                    ncc.getEmail(),
                    ncc.getDiaChi(),
                    ncc.getNguoiLienHe(),
                    ncc.getMoTa(),
                    ncc.getTrangThai()
            });
        }
        capNhatSoKetQua();
    }
 
    private JLabel taoLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(11, 23, 54));
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
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                title,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                GOLD_LIGHT
        );
    }
 
    private JButton taoNut(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setUI(new BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(laMauNutSang(bg) ? Color.BLACK : Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setMargin(new Insets(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(4, 8, 4, 8)
        ));
        btn.setIcon(new ActionButtonIcon(xacDinhLoaiIcon(text), 16));
        btn.setIconTextGap(8);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        return btn;
    }
 
    private boolean laMauNutSang(Color color) {
        if (color == null) return false;
        int brightness = (int) (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
        return brightness > 155;
    }
 
    private IconType xacDinhLoaiIcon(String text) {
        String lower = text == null ? "" : text.toLowerCase();
        if (lower.contains("thêm")) return IconType.ADD;
        if (lower.contains("sửa")) return IconType.EDIT;
        if (lower.contains("xóa") || lower.contains("ngừng")) return IconType.DELETE;
        if (lower.contains("chi tiết")) return IconType.DETAIL;
        if (lower.contains("in")) return IconType.PRINT;
        if (lower.contains("làm mới") || lower.contains("hiển thị")) return IconType.REFRESH;
        if (lower.contains("tìm")) return IconType.SEARCH;
        return IconType.NONE;
    }
 
    private JButton taoNutDialog(String text, boolean primary) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setUI(new BasicButtonUI());
 
        if (primary) {
            btn.setBackground(GOLD);
            btn.setForeground(new Color(30, 22, 12));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GOLD_LIGHT, 1),
                    new EmptyBorder(8, 26, 8, 26)
            ));
        } else {
            btn.setBackground(CARD_DARK_2);
            btn.setForeground(TEXT_LIGHT);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    new EmptyBorder(8, 26, 8, 26)
            ));
        }
 
        return btn;
    }
 
    private JButton taoNutDongNho() {
        JButton btn = new JButton("×");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btn.setForeground(TEXT_MUTED);
        btn.setBackground(BG_PANEL);
        btn.setBorder(null);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
 
    private JScrollPane taoScrollPane(JTable table, String title) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(true);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                title,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                Color.BLACK
        ));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(20, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 20));
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(18);
        scrollPane.getVerticalScrollBar().setUI(new GoldScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new GoldScrollBarUI());
        return scrollPane;
    }
 

    private void canGiuaToanBoNoiDungBang(JTable table) {
        if (table == null) return;
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);

                if (isSelected) {
                    setBackground(TABLE_SELECTED_BG);
                    setForeground(Color.BLACK);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    setForeground(Color.BLACK);
                }

                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                return this;
            }
        });
    }
 
    private void canGiuaHeaderVaFixKhoangTrangBang(JTable table) {
        table.setFillsViewportHeight(true);
        table.setOpaque(true);
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setGridColor(Color.BLACK);
        table.setShowGrid(true);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
 
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
 
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                return this;
            }
        };
 
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
 
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
 
                setHorizontalAlignment(col == 1 || col == 3 || col == 4 || col == 6
                        ? SwingConstants.LEFT
                        : SwingConstants.CENTER);
 
                if (isSelected) {
                    setBackground(TABLE_SELECTED_BG);
                    setForeground(Color.BLACK);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    setForeground(Color.BLACK);
                }
 
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                return this;
            }
        });
    }
 
    private void datDoRongCotBangChinh() {
        int[] widths = {110, 230, 120, 220, 260, 170, 300, 140};
        for (int i = 0; i < widths.length && i < tableNhaCungCap.getColumnCount(); i++) {
            tableNhaCungCap.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }
 
    private void datDoRongCotChiTiet(JTable table) {
        int[] widths = {110, 150, 110, 260, 90, 120, 130, 130, 170};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }
 
    private JPanel taoInfoCard(String label, String value) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(CARD_DARK);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
 
        JLabel lbl = new JLabel(label);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
 
        JLabel val = new JLabel(value == null || value.isBlank() ? "-" : value);
        val.setForeground(GOLD_LIGHT);
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
 
        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }
 
    private JDialog taoDialog(String title, int width, int height) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(width, height);
        dialog.setMinimumSize(new Dimension(Math.min(width, 480), Math.min(height, 240)));
        dialog.setLocationRelativeTo(this);
        return dialog;
    }
 
    private String formatMoney(double value) {
        return String.format("%,.0f", value);
    }
 
    private enum IconType {
        ADD, EDIT, DELETE, DETAIL, PRINT, REFRESH, SEARCH, NONE
    }
 
    private static class ActionButtonIcon implements Icon {
        private final IconType type;
        private final int size;
 
        ActionButtonIcon(IconType type, int size) {
            this.type = type == null ? IconType.NONE : type;
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
            if (type == IconType.NONE) return;
 
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
            Color iconColor = c != null ? c.getForeground() : Color.BLACK;
            g2.setColor(iconColor);
            g2.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
 
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
                    g2.drawLine(x + 4, y + s - 4, x + 3, y + s - 1);
                    g2.drawLine(x + 4, y + s - 4, x + 7, y + s - 3);
                    g2.drawLine(x + s - 6, y + 3, x + s - 3, y + 6);
                }
                case DELETE -> {
                    g2.drawOval(x + 3, y + 3, s - 6, s - 6);
                    g2.drawLine(x + 6, y + 6, x + s - 6, y + s - 6);
                    g2.drawLine(x + s - 6, y + 6, x + 6, y + s - 6);
                }
                case DETAIL -> {
                    g2.drawRoundRect(x + 3, y + 3, s - 6, s - 6, 4, 4);
                    g2.drawLine(x + 6, y + 7, x + s - 6, y + 7);
                    g2.drawLine(x + 6, y + 11, x + s - 6, y + 11);
                    g2.drawLine(x + 6, y + 15, x + s - 9, y + 15);
                }
                case PRINT -> {
                    // Icon máy in giống nút In hóa đơn
                    g2.drawRect(x + 4, y + 2, s - 8, 5);
                    g2.drawRoundRect(x + 2, y + 6, s - 4, 7, 3, 3);
                    g2.drawRect(x + 4, y + 11, s - 8, s - 12);
                    g2.drawLine(x + 6, y + 14, x + s - 6, y + 14);
                }
                case REFRESH -> {
                    g2.drawArc(x + 3, y + 3, s - 6, s - 6, 35, 250);
                    g2.drawLine(x + s - 5, y + 5, x + s - 3, y + 10);
                    g2.drawLine(x + s - 5, y + 5, x + s - 10, y + 6);
                }
                case SEARCH -> {
                    g2.drawOval(x + 3, y + 3, s - 8, s - 8);
                    g2.drawLine(x + s - 6, y + s - 6, x + s - 2, y + s - 2);
                }
                default -> { }
            }
 
            g2.dispose();
        }
    }
 
    private static class GoldScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = Color.BLACK;
            trackColor = Color.WHITE;
        }
 
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) return;
 
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.fillRoundRect(r.x + 4, r.y + 4, r.width - 8, r.height - 8, 10, 10);
            g2.dispose();
        }
 
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(Color.WHITE);
            g.fillRect(r.x, r.y, r.width, r.height);
            g.setColor(Color.BLACK);
            g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
        }
 
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return invisibleButton();
        }
 
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return invisibleButton();
        }
 
        private JButton invisibleButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            btn.setMinimumSize(new Dimension(0, 0));
            btn.setMaximumSize(new Dimension(0, 0));
            return btn;
        }
    }
 
}
