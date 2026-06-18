package GUI;

import database.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayDeque;

public class LoginFrame extends JFrame {

    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhau;
    private JCheckBox chkHienMatKhau;

    private String vaiTroDaChon = "";
    private String tenVaiTroHienThi = "";

    private final Color NAVY_1 = new Color(2, 8, 20);
    private final Color NAVY_2 = new Color(6, 26, 58);
    private final Color NAVY_3 = new Color(18, 59, 140);
    private final Color BLUE = new Color(29, 78, 216);
    private final Color GOLD = new Color(216, 170, 69);
    private final Color GOLD_LIGHT = new Color(244, 210, 122);
    private final Color TEXT_DARK = new Color(11, 23, 54);
    private final Color TEXT_MUTED = new Color(38, 53, 85);
    private final Color BORDER = new Color(18, 20, 45);

    public LoginFrame() {
        setTitle("Đăng nhập hệ thống BiLuxury");
        setSize(980, 820);
        setMinimumSize(new Dimension(900, 760));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        hienThiChonVaiTro();
    }

    private boolean dangPhongTo() {
        return (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
    }

    private void apDungKichThuocNeuCan(int width, int height) {
        if (!dangPhongTo()) {
            setSize(width, height);
            setLocationRelativeTo(null);
        }
    }

    private JPanel taoKhungChung(String titleText, String subtitleText) {
        JPanel root = new LoginBackgroundPanel();
        root.setLayout(new BorderLayout());
        root.setBorder(new EmptyBorder(26, 52, 26, 52));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("BILUXURY", SwingConstants.CENTER);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 46));
        brand.setForeground(TEXT_DARK);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        JLabel logo = createLogoLabel(250, 130);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel slogan = new JLabel("PREMIUM MEN'S WEAR MANAGEMENT", SwingConstants.CENTER);
        slogan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        slogan.setForeground(TEXT_DARK);
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComponent goldLine = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int y = getHeight() / 2;

                GradientPaint gp = new GradientPaint(
                        0,
                        y,
                        new Color(138, 100, 30, 0),
                        getWidth() / 2f,
                        y,
                        GOLD_LIGHT,
                        true
                );

                g2.setPaint(gp);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(0, y, getWidth(), y);
                g2.dispose();
            }
        };

        goldLine.setMaximumSize(new Dimension(300, 16));
        goldLine.setPreferredSize(new Dimension(300, 16));
        goldLine.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 29));
        title.setForeground(Color.BLACK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(subtitleText, SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(Box.createVerticalStrut(2));
        content.add(brand);
        content.add(Box.createVerticalStrut(4));
        content.add(logo);
        content.add(Box.createVerticalStrut(4));
        content.add(slogan);
        content.add(Box.createVerticalStrut(8));
        content.add(goldLine);
        content.add(Box.createVerticalStrut(18));
        content.add(title);
        content.add(Box.createVerticalStrut(6));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(30));

        root.add(content, BorderLayout.CENTER);
        return root;
    }

    private void hienThiChonVaiTro() {
        setTitle("Chọn vai trò đăng nhập");

        int oldState = getExtendedState();
        apDungKichThuocNeuCan(980, 780);

        JPanel root = taoKhungChung(
                "CHỌN VAI TRÒ ĐĂNG NHẬP",
                "Chọn quyền sử dụng trước khi nhập tài khoản"
        );

        JPanel content = (JPanel) root.getComponent(0);

        JPanel rolePanel = new JPanel(new GridLayout(1, 2, 34, 0));
        rolePanel.setOpaque(false);
        rolePanel.setMaximumSize(new Dimension(820, 270));
        rolePanel.setPreferredSize(new Dimension(820, 270));
        rolePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnQuanLy = taoNutVaiTro("QUẢN LÝ", "Toàn quyền hệ thống", true);
        JButton btnNhanVien = taoNutVaiTro("NHÂN VIÊN", "Quyền nghiệp vụ cơ bản", false);

        btnQuanLy.addActionListener(e -> {
            vaiTroDaChon = "ADMIN";
            tenVaiTroHienThi = "QUẢN LÝ";
            hienThiFormDangNhap();
        });

        btnNhanVien.addActionListener(e -> {
            vaiTroDaChon = "STAFF";
            tenVaiTroHienThi = "NHÂN VIÊN";
            hienThiFormDangNhap();
        });

        rolePanel.add(btnQuanLy);
        rolePanel.add(btnNhanVien);
        content.add(rolePanel);

        setContentPane(root);
        revalidate();
        repaint();
        setExtendedState(oldState);
    }

    private JButton taoNutVaiTro(String role, String subText, boolean admin) {
        JButton btn = new RoleCardButton(role, subText);

        btn.setText(""); // tránh JButton tự vẽ chữ gây đè chữ
        btn.setFont(new Font("Segoe UI", Font.BOLD, 23));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setIconTextGap(18);
        btn.setPreferredSize(new Dimension(385, 260));
        btn.setBorder(new EmptyBorder(26, 20, 24, 20));

        Image roleImage = loadMenuImage(admin ? "admin.png" : "nhanvien.png", 108, 108);

        if (roleImage != null) {
            btn.setIcon(new ImageIcon(roleImage));
        } else {
            btn.setIcon(new SimpleRoleIcon(admin, 108));
        }

        return btn;
    }

    private void hienThiFormDangNhap() {
        setTitle("Đăng nhập - " + tenVaiTroHienThi);

        int oldState = getExtendedState();

        
        apDungKichThuocNeuCan(980, 860);

        JPanel root = taoKhungChung(
                "ĐĂNG NHẬP - " + tenVaiTroHienThi,
                "Bạn đang đăng nhập với quyền " + tenVaiTroHienThi.toLowerCase()
        );

        JPanel content = (JPanel) root.getComponent(0);
        content.add(createFormPanel());

        JScrollPane scroll = new JScrollPane(root);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        setContentPane(scroll);
        revalidate();
        repaint();
        setExtendedState(oldState);

        SwingUtilities.invokeLater(() -> txtTaiKhoan.requestFocus());
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBackground(new Color(255, 255, 255, 210));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 2),
                
                new EmptyBorder(24, 54, 20, 54)
        ));

       
        int formHeight = "STAFF".equalsIgnoreCase(vaiTroDaChon) ? 370 : 350;
        form.setMaximumSize(new Dimension(860, formHeight));
        form.setPreferredSize(new Dimension(860, formHeight));

        txtTaiKhoan = createTextField();
        txtMatKhau = createPasswordField();

        txtTaiKhoan.setPreferredSize(new Dimension(0, 48));
        txtMatKhau.setPreferredSize(new Dimension(0, 48));

        chkHienMatKhau = new JCheckBox("Hiện mật khẩu");
        chkHienMatKhau.setOpaque(false);
        chkHienMatKhau.setForeground(Color.BLACK);
        chkHienMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkHienMatKhau.setFocusPainted(false);
        chkHienMatKhau.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkHienMatKhau.addActionListener(e ->
                txtMatKhau.setEchoChar(chkHienMatKhau.isSelected() ? (char) 0 : '•')
        );

        JButton btnQuayLai = createFormButton("Quay lại", false);
        btnQuayLai.setPreferredSize(new Dimension(176, 52));
        btnQuayLai.addActionListener(e -> {
            int oldState = getExtendedState();
            hienThiChonVaiTro();
            setExtendedState(oldState);
        });

        JButton btnDangNhap = createFormButton("Đăng nhập", true);
        btnDangNhap.setPreferredSize(new Dimension(230, 52));
        btnDangNhap.addActionListener(e -> dangNhap());

        txtTaiKhoan.addActionListener(e -> txtMatKhau.requestFocus());
        txtMatKhau.addActionListener(e -> dangNhap());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.28;
        form.add(createLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.72;
        form.add(txtTaiKhoan, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.28;
        form.add(createLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.72;
        form.add(txtMatKhau, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(4, 8, 6, 8);
        form.add(chkHienMatKhau, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        buttons.setOpaque(false);
        buttons.add(btnQuayLai);
        buttons.add(btnDangNhap);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 4, 8);
        form.add(buttons, gbc);

        if ("STAFF".equalsIgnoreCase(vaiTroDaChon)) {
            JButton btnQuenMatKhau = createForgotButton("Quên mật khẩu nhân viên?");
            btnQuenMatKhau.addActionListener(e -> moQuenMatKhauNhanVien());

            JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            forgotPanel.setOpaque(false);
            forgotPanel.add(btnQuenMatKhau);

            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(4, 8, 0, 8);
            form.add(forgotPanel, gbc);
        }

        return form;
    }

    private void dangNhap() {
        String taiKhoan = txtTaiKhoan.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (taiKhoan.isEmpty() || matKhau.isEmpty()) {
            showModernNoticeDialog(
                    this,
                    "Thiếu thông tin",
                    "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.",
                    false
            );
            return;
        }

        try {
            LoginResult result = xacThucTaiKhoan(taiKhoan, matKhau);

            if (result == null) {
                baoSaiDangNhap();
                return;
            }

            boolean chonAdmin = "ADMIN".equalsIgnoreCase(vaiTroDaChon);
            if ((chonAdmin && !"ADMIN".equalsIgnoreCase(result.vaiTro))
                    || (!chonAdmin && "ADMIN".equalsIgnoreCase(result.vaiTro))) {
                showModernNoticeDialog(
                        this,
                        "Sai vai trò",
                        "Tài khoản này không thuộc vai trò " + tenVaiTroHienThi + ".",
                        false
                );
                return;
            }

            ghiLichSu(result.taiKhoan, result.vaiTro, "Đăng nhập hệ thống");
            hienThongBaoDangNhapThanhCongVaMoMain(result.taiKhoan, result.vaiTro, result.maNhanVien);

        } catch (Exception e) {
            showModernNoticeDialog(
                    this,
                    "Lỗi đăng nhập",
                    "Không thể đăng nhập.\nLỗi: " + e.getMessage(),
                    false
            );
        }
    }

    private void baoSaiDangNhap() {
        showErrorInlineDialog("Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không đúng.");
    }


    private void showErrorInlineDialog(String titleText, String messageText) {
        
        JDialog dialog = new JDialog(this, titleText, true);
        dialog.setSize(640, 260);
        dialog.setResizable(false);

        JPanel root = new LoginBackgroundPanel();
        root.setLayout(new GridBagLayout());
        root.setBorder(new EmptyBorder(22, 30, 22, 30));

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(150, 40, 40));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel icon = new JLabel(new ErrorXIcon(38));
        JLabel msg = new JLabel(messageText, SwingConstants.LEFT);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        msg.setForeground(TEXT_DARK);

        row.add(icon);
        row.add(msg);

        JButton ok = createFormButton("Đã hiểu", true);
        ok.setPreferredSize(new Dimension(160, 48));
        ok.setMaximumSize(new Dimension(160, 48));
        ok.setMinimumSize(new Dimension(160, 48));
        ok.setAlignmentX(Component.CENTER_ALIGNMENT);
        ok.addActionListener(e -> dialog.dispose());

        box.add(title);
        box.add(Box.createVerticalStrut(26));
        box.add(row);
        box.add(Box.createVerticalStrut(30));
        box.add(ok);

        root.add(box, new GridBagConstraints());
        dialog.setContentPane(root);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private class ErrorXIcon implements Icon {
        private final int size;

        ErrorXIcon(int size) {
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

            g2.setColor(new Color(190, 55, 55));
            g2.fillOval(x, y, size, size);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(4.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int p = Math.max(10, size / 3);
            g2.drawLine(x + p, y + p, x + size - p, y + size - p);
            g2.drawLine(x + size - p, y + p, x + p, y + size - p);

            g2.dispose();
        }
    }

    private LoginResult xacThucTaiKhoan(String taiKhoan, String matKhau) throws Exception {
        
        LoginResult ngoaiLe = dangNhapTaiKhoanNgoaiLe(taiKhoan, matKhau);
        if (ngoaiLe != null) {
            return ngoaiLe;
        }

        try {
            LoginResult result = dangNhapBangTaiKhoan(taiKhoan, matKhau);

            if (result != null) {
                return result;
            }

        } catch (Exception e) {
            System.out.println("Không đăng nhập được bằng bảng TaiKhoan: " + e.getMessage());
        }

        // Fallback chắc chắn theo yêu cầu: nhân viên đăng nhập bằng SĐT + CCCD.
        // Nếu bảng TaiKhoan chưa đồng bộ hoặc mật khẩu đang lưu dạng cũ, vẫn cho đăng nhập đúng theo bảng NhanVien.
        LoginResult nvResult = dangNhapBangNhanVien(taiKhoan, matKhau);
        if (nvResult != null) {
            return nvResult;
        }

        return null;
    }


    private LoginResult dangNhapTaiKhoanNgoaiLe(String taiKhoan, String matKhau) {
        if (!"12345".equals(matKhau)) {
            return null;
        }

        String tk = taiKhoan == null ? "" : taiKhoan.trim();

        switch (tk) {
            case "admin":
                return new LoginResult("admin", "ADMIN", layMaNhanVienAdminMacDinh());
            case "banhang":
                return new LoginResult("banhang", "STAFF", layMaNhanVienTheoChucVu("STAFF"));
            case "kho":
                return new LoginResult("kho", "WAREHOUSE", layMaNhanVienTheoChucVu("WAREHOUSE"));
            case "ketoan":
                return new LoginResult("ketoan", "ACCOUNTANT", layMaNhanVienTheoChucVu("ACCOUNTANT"));
            default:
                return null;
        }
    }

    private LoginResult dangNhapBangNhanVien(String taiKhoan, String matKhau) throws Exception {
        String sql = """
                SELECT maDinhDanh, hoTen, soDienThoai, cccd, email, chucVu
                FROM NhanVien
                WHERE soDienThoai = ? AND cccd = ?
                  AND ISNULL(trangThai, N'Đang làm') <> N'Đã nghỉ'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, taiKhoan.trim());
            ps.setString(2, matKhau.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maNV = rs.getString("maDinhDanh");
                    String sdt = rs.getString("soDienThoai");
                    String cccd = rs.getString("cccd");
                    String hoTen = rs.getString("hoTen");
                    String email = rs.getString("email");
                    String vaiTro = layVaiTroTheoChucVu(rs.getString("chucVu"));

                    dongBoTaiKhoanNhanVien(sdt, cccd, hoTen, email == null ? "" : email, vaiTro);
                    capNhatDangNhapCuoi(sdt);
                    return new LoginResult(sdt, vaiTro, maNV);
                }
            }
        }

        return null;
    }

    private LoginResult dangNhapBangTaiKhoan(String taiKhoan, String matKhau) throws Exception {
        String sql = """
                SELECT tenDangNhap, matKhau, vaiTro, trangThai
                FROM TaiKhoan
                WHERE tenDangNhap COLLATE Latin1_General_BIN2 = ?
                """;

        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            throw new SQLException("Không kết nối được cơ sở dữ liệu.");
        }

        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, taiKhoan.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                boolean trangThai = rs.getBoolean("trangThai");

                if (!trangThai) {
                    showModernNoticeDialog(
                            this,
                            "Tài khoản bị khóa",
                            "Tài khoản này đang bị khóa, vui lòng liên hệ quản lý.",
                            false
                    );
                    return null;
                }

                String hashTrongDB = rs.getString("matKhau");
                String hashNhap = hashSHA256(matKhau);

                if (hashTrongDB == null || !hashTrongDB.equalsIgnoreCase(hashNhap)) {
                    return null;
                }

                String tenDangNhapDB = rs.getString("tenDangNhap");
                String vaiTro = chuanHoaVaiTro(rs.getString("vaiTro"));
                String maNV = layMaNhanVienTheoTaiKhoan(tenDangNhapDB, vaiTro);

                capNhatDangNhapCuoi(tenDangNhapDB);

                return new LoginResult(tenDangNhapDB, vaiTro, maNV);
            }
        }
    }

    private String layMaNhanVienTheoTaiKhoan(String taiKhoan, String vaiTro) {
        if ("ADMIN".equalsIgnoreCase(vaiTro)) {
            return layMaNhanVienAdminMacDinh();
        }

        String sql = """
                SELECT maDinhDanh
                FROM NhanVien
                WHERE soDienThoai = ?
                  AND ISNULL(trangThai, N'Đang làm') <> N'Đã nghỉ'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) return "";

            ps.setString(1, taiKhoan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("maDinhDanh");
                }
            }

        } catch (Exception ignored) {
        }

        return layMaNhanVienTheoChucVu(vaiTro);
    }

    private String layMaNhanVienTheoChucVu(String vaiTro) {
        String dieuKien;
        String v = chuanHoaVaiTro(vaiTro);

        if ("WAREHOUSE".equals(v)) {
            dieuKien = "chucVu LIKE N'%kho%'";
        } else if ("ACCOUNTANT".equals(v)) {
            dieuKien = "chucVu LIKE N'%Kế toán%' OR chucVu LIKE N'%Ke toan%'";
        } else if ("STAFF".equals(v)) {
            dieuKien = "chucVu LIKE N'%bán hàng%' OR chucVu LIKE N'%ban hang%'";
        } else {
            return layMaNhanVienAdminMacDinh();
        }

        String sql = "SELECT TOP 1 maDinhDanh "
                + "FROM NhanVien "
                + "WHERE (" + dieuKien + ") "
                + "AND ISNULL(trangThai, N'Đang làm') <> N'Đã nghỉ' "
                + "ORDER BY maDinhDanh";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString("maDinhDanh");
            }

        } catch (Exception ignored) {
        }

        return "";
    }

    private String layMaNhanVienAdminMacDinh() {
        String sql1 = """
                SELECT TOP 1 maDinhDanh
                FROM NhanVien
                WHERE (chucVu LIKE N'%Quản lý%' OR chucVu LIKE N'%Quan ly%')
                  AND ISNULL(trangThai, N'Đang làm') <> N'Đã nghỉ'
                ORDER BY maDinhDanh
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql1);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString("maDinhDanh");
            }

        } catch (Exception ignored) {
        }

        String sql2 = """
                SELECT TOP 1 maDinhDanh
                FROM NhanVien
                WHERE ISNULL(trangThai, N'Đang làm') <> N'Đã nghỉ'
                ORDER BY maDinhDanh
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql2);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString("maDinhDanh");
            }

        } catch (Exception ignored) {
        }

        return "";
    }

    private void capNhatDangNhapCuoi(String taiKhoan) {
        String sql = """
                UPDATE TaiKhoan
                SET ngayDangNhapCuoi = ?
                WHERE tenDangNhap COLLATE Latin1_General_BIN2 = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) return;

            ps.setString(1, java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            ps.setString(2, taiKhoan.trim());
            ps.executeUpdate();

        } catch (Exception ignored) {
        }
    }

    private void moQuenMatKhauNhanVien() {
        JDialog dialog = new JDialog(this, "Khôi phục mật khẩu nhân viên", true);
        dialog.setSize(720, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel root = new LoginBackgroundPanel();
        root.setLayout(new BorderLayout());
        root.setBorder(new EmptyBorder(24, 32, 24, 32));

        JLabel title = new JLabel("KHÔI PHỤC MẬT KHẨU NHÂN VIÊN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(24, 20, 14, 20));

        JTextField txtMaNhanVien = createTextFieldSmall();
        JTextField txtTaiKhoanNV = createTextFieldSmall();
        JTextField txtSdt = createTextFieldSmall();
        JTextField txtCccd = createTextFieldSmall();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addForgotRow(form, gbc, 0, "Mã nhân viên:", txtMaNhanVien);
        addForgotRow(form, gbc, 1, "Tài khoản nhân viên:", txtTaiKhoanNV);
        addForgotRow(form, gbc, 2, "Số điện thoại:", txtSdt);
        addForgotRow(form, gbc, 3, "CCCD:", txtCccd);

        root.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttons.setOpaque(false);

        JButton btnDong = createFormButton("Đóng", false);
        btnDong.setPreferredSize(new Dimension(150, 50));
        btnDong.addActionListener(e -> dialog.dispose());

        JButton btnKhoiPhuc = createFormButton("Khôi phục", true);
        btnKhoiPhuc.setPreferredSize(new Dimension(180, 50));
        btnKhoiPhuc.addActionListener(e -> {
            String maNV = txtMaNhanVien.getText().trim().toUpperCase();
            String taiKhoanNV = txtTaiKhoanNV.getText().trim().toLowerCase();
            String sdt = txtSdt.getText().trim();
            String cccd = txtCccd.getText().trim();

            if (maNV.isEmpty() || taiKhoanNV.isEmpty() || sdt.isEmpty() || cccd.isEmpty()) {
                showModernNoticeDialog(
                        this,
                        "Thiếu thông tin",
                        "Nhập đủ mã NV, tài khoản, SĐT và CCCD.",
                        false
                );
                return;
            }

            String mk = layMatKhauNhanVien(maNV, taiKhoanNV, sdt, cccd);

            if (mk == null) {
                showErrorInlineDialog(
                        "Không tìm thấy",
                        "Thông tin không đúng. Vui lòng kiểm tra lại."
                );
            } else {
                showModernNoticeDialog(
                        this,
                        "Khôi phục thành công",
                        "Mã NV: " + maNV
                                + "\nTK: " + taiKhoanNV
                                + "\nMK: " + mk,
                        true
                );
            }
        });

        buttons.add(btnDong);
        buttons.add(btnKhoiPhuc);

        root.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private String layMatKhauNhanVien(String maNV, String taiKhoanNV, String sdt, String cccd) {
        String sql = """
                SELECT maDinhDanh, soDienThoai, cccd, hoTen, email
                FROM NhanVien
                WHERE maDinhDanh = ? AND soDienThoai = ? AND cccd = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return null;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maNV.trim().toUpperCase());
                ps.setString(2, sdt.trim());
                ps.setString(3, cccd.trim());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String sdtTrongDB = rs.getString("soDienThoai");

                        // Trong hệ thống này tài khoản nhân viên đang dùng chính số điện thoại để đăng nhập.
                        if (!taiKhoanNV.equalsIgnoreCase(sdtTrongDB)) {
                            showModernNoticeDialog(
                                    this,
                                    "Sai tài khoản nhân viên",
                                    "Tài khoản nhân viên phải trùng với số điện thoại đã đăng ký trong hồ sơ.",
                                    false
                            );
                            return null;
                        }

                        dongBoTaiKhoanNhanVien(
                                taiKhoanNV,
                                cccd,
                                rs.getString("hoTen"),
                                rs.getString("email"),
                                layVaiTroTheoChucVu(layChucVuNhanVien(maNV))
                        );
                        return cccd;
                    }
                }
            }

        } catch (Exception e) {
            showModernNoticeDialog(this, "Lỗi", "Không thể khôi phục mật khẩu.\nLỗi: " + e.getMessage(), false);
        }

        return null;
    }

    private void dongBoTaiKhoanNhanVien(String taiKhoanNV, String cccd, String hoTen, String email, String vaiTro) throws Exception {
        String check = "SELECT COUNT(*) FROM TaiKhoan WHERE tenDangNhap = ?";
        String insert = """
                INSERT INTO TaiKhoan
                (maTaiKhoan, tenDangNhap, matKhau, vaiTro, hoTen, email, trangThai, ngayTao, ngayDangNhapCuoi)
                VALUES (?, ?, ?, ?, ?, ?, 1, ?, NULL)
                """;
        String update = """
                UPDATE TaiKhoan
                SET matKhau = ?, vaiTro = ?, hoTen = ?, email = ?, trangThai = 1
                WHERE tenDangNhap COLLATE Latin1_General_BIN2 = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Không kết nối được cơ sở dữ liệu.");

            boolean daCoTaiKhoan = false;
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setString(1, taiKhoanNV);
                try (ResultSet rs = ps.executeQuery()) {
                    daCoTaiKhoan = rs.next() && rs.getInt(1) > 0;
                }
            }

            if (daCoTaiKhoan) {
                try (PreparedStatement ps = conn.prepareStatement(update)) {
                    ps.setString(1, hashSHA256(cccd));
                    ps.setString(2, chuanHoaVaiTro(vaiTro));
                    ps.setString(3, hoTen);
                    ps.setString(4, email);
                    ps.setString(5, taiKhoanNV);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(insert)) {
                    ps.setString(1, taoMaTaiKhoanTuDong());
                    ps.setString(2, taiKhoanNV);
                    ps.setString(3, hashSHA256(cccd));
                    ps.setString(4, chuanHoaVaiTro(vaiTro));
                    ps.setString(5, hoTen);
                    ps.setString(6, email);
                    ps.setString(7, java.time.LocalDateTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                    ps.executeUpdate();
                }
            }
        }
    }


    private String layVaiTroTheoChucVu(String chucVu) {
        String cv = chucVu == null ? "" : chucVu.trim().toLowerCase();
        if (cv.contains("kho")) return "WAREHOUSE";
        if (cv.contains("kế toán") || cv.contains("ke toan")) return "ACCOUNTANT";
        return "STAFF";
    }

    private String layChucVuNhanVien(String maNV) {
        String sql = "SELECT chucVu FROM NhanVien WHERE maDinhDanh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("chucVu");
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private void hienThongBaoDangNhapThanhCongVaMoMain(String taiKhoan, String vaiTro, String maNhanVien) {
        
        final int trangThaiManHinh = getExtendedState();

        JDialog dialog = new JDialog(this, "Đăng nhập thành công", true);
        dialog.setSize(430, 180);
        dialog.setResizable(false);

        JPanel root = new LoginBackgroundPanel();
        root.setLayout(new GridBagLayout());
        root.setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel box = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        box.setOpaque(false);

        JLabel checkIcon = new JLabel(new SuccessCheckIcon(42), SwingConstants.CENTER);
        JLabel message = new JLabel("Chuyển vào hệ thống...", SwingConstants.LEFT);
        message.setFont(new Font("Segoe UI", Font.BOLD, 22));
        message.setForeground(TEXT_DARK);

        box.add(checkIcon);
        box.add(message);

        root.add(box, new GridBagConstraints());
        dialog.setContentPane(root);
        dialog.setLocationRelativeTo(this);

        Timer timer = new Timer(750, e -> {
            ((Timer) e.getSource()).stop();
            dialog.dispose();

            MainFrame main = new MainFrame(taiKhoan, vaiTro, maNhanVien);
            if ((trangThaiManHinh & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                main.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            main.setVisible(true);

            dispose();
        });
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    private void showModernNoticeDialog(Component parent, String titleText, String messageText, boolean success) {
        
        JDialog dialog = new JDialog(this, titleText, true);
        dialog.setSize(680, 320);
        dialog.setResizable(false);

        JPanel root = new LoginBackgroundPanel();
        root.setLayout(new GridBagLayout());
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(success ? new Color(20, 100, 60) : new Color(150, 40, 40));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel msg = new JLabel(toCenterHtml(messageText), SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        msg.setForeground(TEXT_DARK);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        msg.setVerticalAlignment(SwingConstants.CENTER);

        JButton ok = createFormButton("Đã hiểu", true);
        ok.setPreferredSize(new Dimension(185, 54));
        ok.setMaximumSize(new Dimension(185, 54));
        ok.setMinimumSize(new Dimension(185, 54));
        ok.setAlignmentX(Component.CENTER_ALIGNMENT);
        ok.addActionListener(e -> dialog.dispose());

        box.add(title);
        box.add(Box.createVerticalStrut(28));
        box.add(msg);
        box.add(Box.createVerticalStrut(34));
        box.add(ok);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        root.add(box, gbc);

        dialog.setContentPane(root);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private String toCenterHtml(String text) {
        String safe = text == null ? "" : text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");

        return "<html><div style='text-align:center; width:520px;'>" + safe + "</div></html>";
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        label.setForeground(Color.BLACK);
        return label;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        txt.setForeground(TEXT_DARK);
        txt.setBackground(Color.WHITE);
        txt.setCaretColor(TEXT_DARK);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        return txt;
    }

    private JTextField createTextFieldSmall() {
        JTextField txt = createTextField();
        txt.setPreferredSize(new Dimension(260, 46));
        return txt;
    }

    private JPasswordField createPasswordField() {
        JPasswordField txt = new JPasswordField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        txt.setForeground(TEXT_DARK);
        txt.setBackground(Color.WHITE);
        txt.setCaretColor(TEXT_DARK);
        txt.setEchoChar('•');
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        return txt;
    }

    private JButton createFormButton(String text, boolean primary) {
        JButton btn = new LoginFormButton(text, primary);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(primary ? Color.WHITE : TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createForgotButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(BLUE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void addForgotRow(JPanel form, GridBagConstraints gbc, int row, String text, JTextField txt) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        form.add(createLabel(text), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        form.add(txt, gbc);
    }

    private JLabel createLogoLabel(int w, int h) {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setOpaque(false);
        label.setPreferredSize(new Dimension(w, h));
        label.setMaximumSize(new Dimension(w, h));
        label.setMinimumSize(new Dimension(w, h));

        Image img = loadLogoImage(w, h);

        if (img != null) {
            label.setIcon(new ImageIcon(img));
        } else {
            label.setText("BL");
            label.setFont(new Font("Segoe UI", Font.BOLD, 46));
            label.setForeground(TEXT_DARK);
        }

        return label;
    }

    private Image loadLogoImage(int w, int h) {
        try {
            BufferedImage source = loadImageAnyPath("logo.png");

            if (source == null) {
                System.out.println("Không tìm thấy logo.png");
                System.out.println("Thư mục đang chạy: " + System.getProperty("user.dir"));
                return null;
            }

            System.out.println("Đã load được logo.png");

           
            BufferedImage logo = prepareLogoImage(source);
            return resizeImageToFit(logo, w, h);

        } catch (Exception e) {
            System.out.println("Lỗi load logo.png: " + e.getMessage());
            return null;
        }
    }

    private Image loadMenuImage(String fileName, int w, int h) {
        try {
            BufferedImage source = loadImageAnyPath(fileName);

            if (source == null) {
                System.out.println("Không tìm thấy ảnh: " + fileName);
                return null;
            }

            return resizeImageToFit(source, w, h);

        } catch (Exception e) {
            System.out.println("Lỗi load ảnh " + fileName + ": " + e.getMessage());
            return null;
        }
    }

    private BufferedImage loadImageAnyPath(String fileName) {
        try {
            java.net.URL url = getClass().getResource("/image/" + fileName);

            if (url != null) {
                System.out.println("Đã tìm thấy ảnh trong resource: /image/" + fileName);
                return toBufferedImage(new ImageIcon(url).getImage());
            }

            File file = findImageFile(fileName);

            if (file != null && file.exists()) {
                System.out.println("Đã tìm thấy ảnh: " + file.getAbsolutePath());
                return toBufferedImage(new ImageIcon(file.getAbsolutePath()).getImage());
            }

        } catch (Exception e) {
            System.out.println("Lỗi tìm ảnh " + fileName + ": " + e.getMessage());
        }

        return null;
    }

    private File findImageFile(String fileName) {
        String userDir = System.getProperty("user.dir");
        File current = new File(userDir);

        for (int i = 0; i < 8 && current != null; i++) {
            File f1 = new File(current, "src/image/" + fileName);
            if (f1.exists()) return f1;

            File f2 = new File(current, "image/" + fileName);
            if (f2.exists()) return f2;

            File f3 = new File(current, "kkk/src/image/" + fileName);
            if (f3.exists()) return f3;

            File f4 = new File(current, "kk/src/image/" + fileName);
            if (f4.exists()) return f4;

            current = current.getParentFile();
        }

        return searchImageFile(new File(userDir), fileName, 0, 6);
    }

    private File searchImageFile(File dir, String fileName, int depth, int maxDepth) {
        if (dir == null || !dir.exists() || depth > maxDepth) {
            return null;
        }

        if (dir.isFile()) {
            if (dir.getName().equalsIgnoreCase(fileName)
                    && dir.getParentFile() != null
                    && dir.getParentFile().getName().equalsIgnoreCase("image")) {
                return dir;
            }
            return null;
        }

        File[] files = dir.listFiles();
        if (files == null) return null;

        for (File f : files) {
            String name = f.getName().toLowerCase();

            if (f.isDirectory()
                    && (name.equals("bin")
                    || name.equals("lib")
                    || name.equals(".git")
                    || name.equals(".idea")
                    || name.equals(".vscode")
                    || name.equals("out")
                    || name.equals("target"))) {
                continue;
            }

            File found = searchImageFile(f, fileName, depth + 1, maxDepth);
            if (found != null) return found;
        }

        return null;
    }

    private BufferedImage toBufferedImage(Image raw) {
        if (raw == null || raw.getWidth(null) <= 0 || raw.getHeight(null) <= 0) {
            return null;
        }

        BufferedImage img = new BufferedImage(
                raw.getWidth(null),
                raw.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(raw, 0, 0, null);
        g2.dispose();

        return img;
    }

    
    private BufferedImage prepareLogoImage(BufferedImage src) {
        if (src == null) return null;

        BufferedImage transparent = new BufferedImage(
                src.getWidth(),
                src.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = transparent.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();

        removeBorderBackground(transparent);

        return cropTransparentPadding(transparent, 4);
    }

    private void removeBorderBackground(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        if (w <= 0 || h <= 0) return;

        boolean[][] visited = new boolean[h][w];
        ArrayDeque<Point> queue = new ArrayDeque<>();

        int[] cornerColors = new int[]{
                img.getRGB(0, 0),
                img.getRGB(w - 1, 0),
                img.getRGB(0, h - 1),
                img.getRGB(w - 1, h - 1)
        };

        for (int x = 0; x < w; x++) {
            addBackgroundPoint(img, visited, queue, x, 0, cornerColors);
            addBackgroundPoint(img, visited, queue, x, h - 1, cornerColors);
        }

        for (int y = 0; y < h; y++) {
            addBackgroundPoint(img, visited, queue, 0, y, cornerColors);
            addBackgroundPoint(img, visited, queue, w - 1, y, cornerColors);
        }

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!queue.isEmpty()) {
            Point p = queue.removeFirst();

            img.setRGB(p.x, p.y, 0x00000000);

            for (int i = 0; i < 4; i++) {
                int nx = p.x + dx[i];
                int ny = p.y + dy[i];

                if (nx < 0 || ny < 0 || nx >= w || ny >= h || visited[ny][nx]) {
                    continue;
                }

                addBackgroundPoint(img, visited, queue, nx, ny, cornerColors);
            }
        }
    }

    private void addBackgroundPoint(BufferedImage img,
                                    boolean[][] visited,
                                    ArrayDeque<Point> queue,
                                    int x,
                                    int y,
                                    int[] cornerColors) {
        if (visited[y][x]) return;

        visited[y][x] = true;

        int argb = img.getRGB(x, y);
        int a = (argb >> 24) & 0xff;

        if (a < 10) {
            queue.add(new Point(x, y));
            return;
        }

        
        if (isWhiteLike(argb) || isSimilarToAnyCorner(argb, cornerColors, 115)) {
            queue.add(new Point(x, y));
        }
    }

    private boolean isWhiteLike(int argb) {
        int r = (argb >> 16) & 0xff;
        int g = (argb >> 8) & 0xff;
        int b = argb & 0xff;

        return r > 215 && g > 215 && b > 215;
    }

    private boolean isSimilarToAnyCorner(int argb, int[] cornerColors, int threshold) {
        for (int c : cornerColors) {
            if (colorDistance(argb, c) <= threshold) {
                return true;
            }
        }

        return false;
    }

    private int colorDistance(int c1, int c2) {
        int r1 = (c1 >> 16) & 0xff;
        int g1 = (c1 >> 8) & 0xff;
        int b1 = c1 & 0xff;

        int r2 = (c2 >> 16) & 0xff;
        int g2 = (c2 >> 8) & 0xff;
        int b2 = c2 & 0xff;

        int dr = r1 - r2;
        int dg = g1 - g2;
        int db = b1 - b2;

        return (int) Math.sqrt(dr * dr + dg * dg + db * db);
    }

    private BufferedImage cropTransparentPadding(BufferedImage src, int padding) {
        int w = src.getWidth();
        int h = src.getHeight();

        int minX = w;
        int minY = h;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int a = (src.getRGB(x, y) >> 24) & 0xff;

                if (a > 10) {
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return src;
        }

        minX = Math.max(0, minX - padding);
        minY = Math.max(0, minY - padding);
        maxX = Math.min(w - 1, maxX + padding);
        maxY = Math.min(h - 1, maxY + padding);

        BufferedImage cropped = src.getSubimage(
                minX,
                minY,
                maxX - minX + 1,
                maxY - minY + 1
        );

        BufferedImage copy = new BufferedImage(
                cropped.getWidth(),
                cropped.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = copy.createGraphics();
        g2.drawImage(cropped, 0, 0, null);
        g2.dispose();

        return copy;
    }

    private Image resizeImageToFit(BufferedImage source, int boxW, int boxH) {
        if (source == null) {
            return null;
        }

        BufferedImage output = new BufferedImage(boxW, boxH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double scale = Math.min(
                boxW * 1.0 / source.getWidth(),
                boxH * 1.0 / source.getHeight()
        );

        int newW = Math.max(1, (int) Math.round(source.getWidth() * scale));
        int newH = Math.max(1, (int) Math.round(source.getHeight() * scale));

        int x = (boxW - newW) / 2;
        int y = (boxH - newH) / 2;

        g2.drawImage(source, x, y, newW, newH, null);
        g2.dispose();

        return output;
    }

    private String chuanHoaVaiTro(String vaiTro) {
        if (vaiTro == null || vaiTro.isBlank()) return "ADMIN";

        String v = vaiTro.trim().toUpperCase();

        if (v.equals("ADMIN")
                || v.equals("QUẢN LÝ")
                || v.equals("QUAN LY")
                || v.equals("QL")) {
            return "ADMIN";
        }
        if (v.equals("WAREHOUSE") || v.equals("KHO") || v.equals("NHÂN VIÊN KHO") || v.equals("NHAN VIEN KHO")) return "WAREHOUSE";
        if (v.equals("ACCOUNTANT") || v.equals("KẾ TOÁN") || v.equals("KE TOAN")) return "ACCOUNTANT";

        return "STAFF";
    }

    private String tenVaiTroHienThi(String vaiTro) {
        String v = chuanHoaVaiTro(vaiTro);
        if ("ADMIN".equals(v)) return "Admin";
        if ("WAREHOUSE".equals(v)) return "Nhân viên kho";
        if ("ACCOUNTANT".equals(v)) return "Kế toán";
        return "Nhân viên bán hàng";
    }

    private String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();

            for (byte b : hash) {
                String s = Integer.toHexString(0xff & b);

                if (s.length() == 1) {
                    hex.append('0');
                }

                hex.append(s);
            }

            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Không thể mã hóa mật khẩu SHA-256.", e);
        }
    }

    private String taoMaTaiKhoanTuDong() {
        return "TK-" + java.util.UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }

    private void ghiLichSu(String taiKhoan, String vaiTro, String hanhDong) {
        String sql = """
                INSERT INTO LichSuHoatDong(taiKhoan, vaiTro, hanhDong)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) return;

            ps.setString(1, taiKhoan);
            ps.setString(2, vaiTro);
            ps.setString(3, hanhDong);
            ps.executeUpdate();

        } catch (Exception ignored) {
        }
    }

    private static class LoginResult {
        String taiKhoan;
        String vaiTro;
        String maNhanVien;

        LoginResult(String taiKhoan, String vaiTro, String maNhanVien) {
            this.taiKhoan = taiKhoan;
            this.vaiTro = vaiTro;
            this.maNhanVien = maNhanVien;
        }
    }

    private class RoleCardButton extends JButton {
        private final String role;
        private final String subText;
        private boolean hover;

        RoleCardButton(String role, String subText) {
            
            super("");

            this.role = role;
            this.subText = subText;

            setText("");
            setFocusPainted(false);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = 28;

            g2.setColor(new Color(10, 20, 45, hover ? 42 : 26));
            g2.fillRoundRect(10, 12, w - 16, h - 14, arc + 12, arc + 12);

            g2.setColor(new Color(0, 168, 255, hover ? 58 : 34));
            g2.fillRoundRect(6, 7, w - 12, h - 11, arc + 12, arc + 12);

            LinearGradientPaint card = new LinearGradientPaint(
                    12,
                    8,
                    w - 12,
                    h - 18,
                    new float[]{0f, 0.55f, 1f},
                    new Color[]{
                            hover ? new Color(206, 232, 255) : new Color(222, 240, 255),
                            new Color(240, 248, 255),
                            hover ? new Color(187, 221, 255) : new Color(211, 232, 252)
                    }
            );

            g2.setPaint(card);
            g2.fillRoundRect(12, 8, w - 24, h - 18, arc, arc);

            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(80, 55, 130, hover ? 105 : 72));
            g2.drawLine(w - 112, 26, w - 36, 104);

            g2.setColor(new Color(128, 92, 184, hover ? 115 : 80));
            g2.drawLine(42, h - 42, 160, h - 16);

            g2.setStroke(new BasicStroke(hover ? 2.6f : 2.0f));
            g2.setColor(BORDER);
            g2.drawRoundRect(12, 8, w - 25, h - 19, arc, arc);

            g2.setStroke(new BasicStroke(1.1f));
            g2.setColor(new Color(108, 92, 180, hover ? 165 : 105));
            g2.drawRoundRect(18, 14, w - 37, h - 31, arc - 8, arc - 8);

            Icon icon = getIcon();

            if (icon != null) {
                int iconX = (w - icon.getIconWidth()) / 2;
                int iconY = 58;
                icon.paintIcon(this, g2, iconX, iconY);
            }

            g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
            FontMetrics fmRole = g2.getFontMetrics();

            int roleX = (w - fmRole.stringWidth(role)) / 2;
            int roleY = h - 78;

            g2.setColor(Color.BLACK);
            g2.drawString(role, roleX, roleY);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            FontMetrics fmSub = g2.getFontMetrics();

            int subX = (w - fmSub.stringWidth(subText)) / 2;
            int subY = h - 45;

            g2.setColor(TEXT_MUTED);
            g2.drawString(subText, subX, subY);

            g2.dispose();
        }
    }

    private class LoginFormButton extends JButton {
        private final boolean primary;
        private boolean hover;

        LoginFormButton(String text, boolean primary) {
            super(text);
            this.primary = primary;

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = 18;

            g2.setColor(new Color(10, 20, 45, hover ? 34 : 20));
            g2.fillRoundRect(3, 4, w - 6, h - 6, arc, arc);

            if (primary) {
                LinearGradientPaint gp = new LinearGradientPaint(
                        0,
                        0,
                        w,
                        h,
                        new float[]{0f, 0.55f, 1f},
                        new Color[]{
                                hover ? new Color(47, 128, 237) : BLUE,
                                NAVY_3,
                                NAVY_2
                        }
                );

                g2.setPaint(gp);
                g2.fillRoundRect(1, 1, w - 3, h - 4, arc, arc);

                g2.setColor(new Color(116, 185, 255, hover ? 185 : 130));
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(2, 2, w - 5, h - 6, arc, arc);

            } else {
                LinearGradientPaint gp = new LinearGradientPaint(
                        0,
                        0,
                        w,
                        h,
                        new float[]{0f, 1f},
                        new Color[]{
                                Color.WHITE,
                                new Color(244, 247, 252)
                        }
                );

                g2.setPaint(gp);
                g2.fillRoundRect(1, 1, w - 3, h - 4, arc, arc);

                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(2, 2, w - 5, h - 6, arc, arc);
            }

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private class SimpleRoleIcon implements Icon {
        private final boolean admin;
        private final int size;

        SimpleRoleIcon(boolean admin, int size) {
            this.admin = admin;
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

            g2.setColor(new Color(0, 168, 255, 56));
            g2.fillOval(x - 4, y - 4, size + 8, size + 8);

            g2.setColor(NAVY_2);
            g2.fillOval(x, y, size, size);

            g2.setColor(Color.WHITE);
            g2.fillOval(x + 12, y + 12, size - 24, size - 24);

            g2.setColor(admin ? new Color(34, 44, 86) : GOLD);
            g2.fillRoundRect(x + 32, y + 52, 44, 30, 16, 16);

            g2.setColor(new Color(242, 224, 202));
            g2.fillOval(x + 38, y + 28, 32, 32);

            g2.setColor(TEXT_DARK);
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(x, y, size, size);

            g2.dispose();
        }
    }


    private class SuccessCheckIcon implements Icon {
        private final int size;

        SuccessCheckIcon(int size) {
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

            g2.setColor(new Color(20, 140, 80));
            g2.fillOval(x, y, size, size);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int x1 = x + size / 4;
            int y1 = y + size / 2;
            int x2 = x + size / 2 - 4;
            int y2 = y + size * 3 / 4 - 5;
            int x3 = x + size * 3 / 4 + 6;
            int y3 = y + size / 3;

            g2.drawLine(x1, y1, x2, y2);
            g2.drawLine(x2, y2, x3, y3);

            g2.dispose();
        }
    }

    private class LoginBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            LinearGradientPaint base = new LinearGradientPaint(
                    0,
                    0,
                    w,
                    h,
                    new float[]{0f, 0.52f, 1f},
                    new Color[]{
                            new Color(250, 252, 255),
                            new Color(236, 246, 255),
                            new Color(198, 225, 252)
                    }
            );

            g2.setPaint(base);
            g2.fillRect(0, 0, w, h);

            RadialGradientPaint blueGlow = new RadialGradientPaint(
                    new java.awt.geom.Point2D.Float(w * 0.82f, h * 0.25f),
                    Math.max(w, h) * 0.65f,
                    new float[]{0f, 0.65f, 1f},
                    new Color[]{
                            new Color(47, 128, 237, 80),
                            new Color(47, 128, 237, 24),
                            new Color(47, 128, 237, 0)
                    }
            );

            g2.setPaint(blueGlow);
            g2.fillRect(0, 0, w, h);

            g2.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(18, 20, 45, 36));

            for (int i = -h; i < w; i += 145) {
                g2.drawLine(i, h, i + h, 0);
            }

            for (int i = 0; i < 5; i++) {
                int y = (int) (h * 0.60 + i * 30);

                java.awt.geom.Path2D.Double wave = new java.awt.geom.Path2D.Double();
                wave.moveTo(-40, y);
                wave.curveTo(w * 0.25, y - 55, w * 0.45, y + 45, w * 0.72, y - 14);
                wave.curveTo(w * 0.88, y - 45, w + 40, y + 15, w + 60, y - 20);

                g2.setColor(new Color(47, 128, 237, 26 - i * 3));
                g2.setStroke(new BasicStroke(1.1f));
                g2.draw(wave);
            }

            g2.dispose();
        }
    }
}