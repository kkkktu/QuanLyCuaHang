package GUI;

import database.DBConnection;

import javax.swing.*;
import javax.imageio.ImageIO;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {

    

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private SidebarItem btnDashboard;
    private SidebarItem btnNhanVien;
    private SidebarItem btnThongTinNhanVien;
    private SidebarItem btnKhachHang;
    private SidebarItem btnHoaDon;
    private SidebarItem btnNhaCungCap;
    private SidebarItem btnHangHoa;
    private SidebarItem btnDoanhThu;

    private JLabel lblCurrentPage;
    private JLabel lblCurrentPageSub;
    private JLabel lblUserName;
    private JLabel lblUserRole;
    private JLabel lblDateTime;

    private Timer refreshTimer;
    private JPanel dashboardOverviewHolder;
    private Timer dashboardRefreshTimer;

    private String currentTaiKhoan;
    private String currentVaiTro;
    private String currentMaNhanVien;

    private final DecimalFormat moneyFormat = new DecimalFormat("#,###");

    private final Color BG_DARK = new Color(14, 15, 17);
    private final Color BG_DARK_2 = new Color(22, 23, 26);
    private final Color SIDEBAR_DARK = new Color(11, 12, 14);
    private final Color CARD_DARK = new Color(32, 33, 36);
    private final Color CARD_DARK_2 = new Color(42, 39, 35);
    private final Color GOLD = new Color(192, 149, 94);
    private final Color GOLD_LIGHT = new Color(223, 196, 162);
    private final Color GOLD_DARK = new Color(124, 88, 49);
    private final Color TEXT_LIGHT = new Color(245, 239, 230);
    private final Color TEXT_MUTED = new Color(170, 163, 153);
    private final Color BORDER = new Color(84, 72, 56);

    private final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    
    private final Color LIGHT_BG_1 = new Color(248, 250, 252); // #F8FAFC
    private final Color LIGHT_BG_2 = new Color(241, 244, 248); // #F1F4F8
    private final Color LIGHT_BG_3 = new Color(233, 237, 243); // #E9EDF3
    private final Color NAVY_BLACK = new Color(2, 8, 20);      // #020814
    private final Color NAVY_DARK = new Color(6, 26, 58);      // #061A3A
    private final Color NAVY_MID = new Color(8, 36, 90);       // #08245A
    private final Color NAVY_ROYAL = new Color(10, 47, 111);   // #0A2F6F
    private final Color NAVY_ACTIVE = new Color(18, 59, 140);  // #123B8C
    private final Color CHAMPAGNE = new Color(216, 170, 69);   // #D8AA45
    private final Color CHAMPAGNE_LIGHT = new Color(244, 210, 122); // #F4D27A
    private final Color CHAMPAGNE_DARK = new Color(138, 100, 30);   // #8A641E
    private final Color HEADING_NAVY = new Color(11, 23, 54);  // #0B1736
    private final Color TEXT_SLATE = new Color(100, 116, 139); // #64748B
    private final Color CARD_LIGHT = Color.WHITE;
    private final Color CARD_LIGHT_2 = new Color(248, 250, 252);
    private final Color CARD_STROKE = new Color(216, 224, 236);

    public MainFrame() {
        this(
                System.getProperty("APP_TAI_KHOAN", "admin"),
                System.getProperty("APP_VAI_TRO", "ADMIN"),
                System.getProperty("APP_MA_NHAN_VIEN", "")
        );
    }

    public MainFrame(String vaiTro) {
        this("admin", vaiTro, "");
    }

    public MainFrame(String taiKhoan, String vaiTro, String maNhanVien) {
        this.currentTaiKhoan = taiKhoan == null || taiKhoan.isBlank() ? "admin" : taiKhoan;
        this.currentVaiTro = chuanHoaVaiTro(vaiTro);
        this.currentMaNhanVien = maNhanVien == null ? "" : maNhanVien;

        setTitle("BILUXURY FASHION - Quản lý cửa hàng");
        setSize(1360, 800);
        setMinimumSize(new Dimension(1180, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initLookAndFeel();
        initUI();
        startAutoRefresh();
    }

    private static String chuanHoaVaiTro(String vaiTro) {
        if (vaiTro == null || vaiTro.isBlank()) return "ADMIN";
        String v = vaiTro.trim().toUpperCase();
        if (v.equals("ADMIN") || v.equals("QUẢN LÝ") || v.equals("QUAN LY") || v.equals("QL")) return "ADMIN";
        if (v.equals("WAREHOUSE") || v.equals("KHO") || v.equals("NHÂN VIÊN KHO") || v.equals("NHAN VIEN KHO")) return "WAREHOUSE";
        if (v.equals("ACCOUNTANT") || v.equals("KẾ TOÁN") || v.equals("KE TOAN")) return "ACCOUNTANT";
        return "STAFF";
    }

    private boolean laAdmin() { return "ADMIN".equalsIgnoreCase(currentVaiTro); }
    private boolean laNhanVienKho() { return "WAREHOUSE".equalsIgnoreCase(currentVaiTro); }
    private boolean laKeToan() { return "ACCOUNTANT".equalsIgnoreCase(currentVaiTro); }
    private boolean laNhanVienBanHang() { return "STAFF".equalsIgnoreCase(currentVaiTro); }
    private boolean coQuyenKho() { return laAdmin() || laNhanVienKho(); }
    private boolean coQuyenDoanhThu() { return laAdmin() || laKeToan() || laNhanVienBanHang(); }
    private boolean coQuyenHoaDon() { return laAdmin() || laNhanVienBanHang() || laKeToan(); }
    private boolean coQuyenKhachHang() { return laAdmin() || laNhanVienBanHang(); }

    private String tenVaiTroHienThi() {
        if (laAdmin()) return "Quản trị viên";
        if (laNhanVienKho()) return "Nhân viên kho";
        if (laKeToan()) return "Kế toán";
        return "Nhân viên bán hàng";
    }

    private String tenVaiTroNgan() {
        if (laAdmin()) return "ADMIN";
        if (laNhanVienKho()) return "NHÂN VIÊN KHO";
        if (laKeToan()) return "KẾ TOÁN";
        return "NHÂN VIÊN BÁN HÀNG";
    }

    private void initLookAndFeel() {
        UIManager.put("Label.font", FONT_NORMAL);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("TextField.font", FONT_NORMAL);
        UIManager.put("PasswordField.font", FONT_NORMAL);
        UIManager.put("Table.font", FONT_NORMAL);
        UIManager.put("Table.rowHeight", 30);
        UIManager.put("TableHeader.font", FONT_BOLD);
        UIManager.put("OptionPane.messageFont", FONT_NORMAL);
        UIManager.put("OptionPane.buttonFont", FONT_BOLD);
    }

    private void initUI() {
        JPanel root = new BackgroundPanel();
        root.setLayout(new BorderLayout());
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createMainArea(), BorderLayout.CENTER);
        setContentPane(root);
        if (laKeToan()) {
            showPage("DoanhThu");
        } else {
            showPage("Dashboard");
        }
    }

    private Image loadLogoImage(int w, int h) {
        try {
            BufferedImage source = loadImageAnyPath("logo.png");

            if (source == null) {
                System.out.println("Không tìm thấy logo.png");
                System.out.println("Thư mục đang chạy: " + System.getProperty("user.dir"));
                return null;
            }

           
            source = removeLogoLightBackground(source);
            return resizeImageToFit(source, w, h);

        } catch (Exception e) {
            System.out.println("Lỗi load logo.png: " + e.getMessage());
            return null;
        }
    }

    private BufferedImage loadImageAnyPath(String fileName) {
        try {
            
            java.net.URL url = getClass().getResource("/image/" + fileName);
            if (url != null) {
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

            // Trường hợp project mở ở thư mục cha, ví dụ: JAVÀ/kk/src/image/logo.png
            File f3 = new File(current, "kk/src/image/" + fileName);
            if (f3.exists()) return f3;

            File f4 = new File(current, "kkk/src/image/" + fileName);
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

    private BufferedImage removeLogoLightBackground(BufferedImage source) {
        if (source == null) {
            return null;
        }

        BufferedImage output = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);

                int a = (argb >>> 24) & 0xff;
                int r = (argb >>> 16) & 0xff;
                int g = (argb >>> 8) & 0xff;
                int b = argb & 0xff;

                int max = Math.max(r, Math.max(g, b));
                int min = Math.min(r, Math.min(g, b));

               
                boolean laNenSang = r >= 232 && g >= 232 && b >= 232 && (max - min) <= 24;

                if (laNenSang) {
                    output.setRGB(x, y, 0x00000000);
                } else {
                    output.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
                }
            }
        }

        return output;
    }

    private Image resizeImageToFit(BufferedImage source, int boxW, int boxH) {
        if (source == null) return null;

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

    public void lamMoiSauKhiHoaDonThayDoi() {
        lamMoiDuLieuLienQuanHoaDon(contentPanel);
        refreshDashboardOverview();
    }

    private void lamMoiDuLieuLienQuanHoaDon(Component component) {
        if (component == null) return;
        if (component instanceof DoanhThuPanel) {
            ((DoanhThuPanel) component).lamMoiDuLieu();
        }
        if (component instanceof SanPhamPanel) {
            ((SanPhamPanel) component).lamMoiDuLieuTuSQL();
        }
        if (component instanceof KhachHangPanel) {
            ((KhachHangPanel) component).lamMoiDuLieuTuSQL();
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                lamMoiDuLieuLienQuanHoaDon(child);
            }
        }
    }

    private JPanel createSidebar() {
        JPanel sidebar = new SidebarGradientPanel();
        sidebar.setPreferredSize(new Dimension(292, 0));
        sidebar.setBorder(new EmptyBorder(26, 22, 26, 22));
        sidebar.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        top.add(createLogoBlock());
        top.add(Box.createVerticalStrut(30));

        btnDashboard = new SidebarItem("TỔNG QUAN HỆ THỐNG", "Dashboard");
        btnDashboard.setIcon(null);
        btnDashboard.setIconTextGap(0);
        btnDashboard.setHorizontalAlignment(SwingConstants.CENTER);
        btnDashboard.setBorder(new EmptyBorder(0, 8, 0, 8));
        btnKhachHang = new SidebarItem("QUẢN LÝ KHÁCH HÀNG", "KhachHang");
        btnHoaDon = new SidebarItem(laKeToan() ? "XEM HÓA ĐƠN" : "QUẢN LÝ HÓA ĐƠN", "HoaDon");
        btnHangHoa = new SidebarItem(laNhanVienBanHang() ? "XEM SẢN PHẨM" : "QUẢN LÝ SẢN PHẨM", "HangHoa");
        btnDoanhThu = new SidebarItem("QUẢN LÝ DOANH THU", "DoanhThu");

        top.add(btnDashboard);
        top.add(Box.createVerticalStrut(22));
        top.add(createSectionLabel("CHỨC NĂNG CHÍNH"));

        if (laAdmin() || laKeToan()) {
            btnNhanVien = new SidebarItem("QUẢN LÝ NHÂN VIÊN", "NhanVien");
            top.add(btnNhanVien);
            top.add(Box.createVerticalStrut(10));
        }

        if (coQuyenKhachHang()) {
            top.add(btnKhachHang);
            top.add(Box.createVerticalStrut(10));
        }
        if (coQuyenHoaDon()) {
            top.add(btnHoaDon);
            top.add(Box.createVerticalStrut(10));
        }
        if (laAdmin() || laNhanVienBanHang() || laNhanVienKho()) {
            top.add(btnHangHoa);
            top.add(Box.createVerticalStrut(10));
        }
        if (coQuyenDoanhThu()) {
            top.add(btnDoanhThu);
        }

        if (coQuyenKho()) {
            top.add(Box.createVerticalStrut(22));
            top.add(createSectionLabel("NGUỒN LỰC"));
            btnNhaCungCap = new SidebarItem("QUẢN LÝ NHÀ CUNG CẤP", "NhaCungCap");
            btnNhaCungCap.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnNhaCungCap.setIconTextGap(6);
            btnNhaCungCap.setBorder(new EmptyBorder(0, 8, 0, 8));
            top.add(btnNhaCungCap);
        }

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        JLabel badge = new JLabel("  " + tenVaiTroNgan() + "  ");
        badge.setOpaque(true);
        badge.setBackground(new Color(216, 170, 69, 34));
        badge.setForeground(CHAMPAGNE_LIGHT);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 170, 69, 100), 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel footer = new JLabel("© 2025 BiLuxury Fashion");
        footer.setFont(new Font("Segoe UI", Font.BOLD, 11));
        footer.setForeground(new Color(234, 240, 255));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel footer2 = new JLabel("Premium Admin v2.0");
        footer2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer2.setForeground(new Color(176, 193, 224));
        footer2.setAlignmentX(Component.LEFT_ALIGNMENT);

        bottom.add(badge);
        bottom.add(Box.createVerticalStrut(10));
        bottom.add(footer);
        bottom.add(Box.createVerticalStrut(3));
        bottom.add(footer2);

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel createLogoBlock() {
    JPanel block = new JPanel(new BorderLayout(14, 0));
    block.setOpaque(false);
    block.setMaximumSize(new Dimension(245, 90));
    block.setPreferredSize(new Dimension(245, 90));
    block.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Logo ở góc trái
    LogoPanel logo = new LogoPanel();
    logo.setPreferredSize(new Dimension(74, 74));
    logo.setMinimumSize(new Dimension(74, 74));
    logo.setMaximumSize(new Dimension(74, 74));

    JPanel text = new JPanel();
    text.setOpaque(false);
    text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

    JLabel line1 = new JLabel("BILUXURY");
    line1.setFont(new Font("Segoe UI", Font.BOLD, 25));
    line1.setForeground(Color.WHITE);
    line1.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel line2 = new JLabel("FASHION");
    line2.setFont(new Font("Segoe UI", Font.BOLD, 15));
    line2.setForeground(CHAMPAGNE_LIGHT);
    line2.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel line3 = new JLabel("MEN'S WEAR MANAGEMENT");
    line3.setFont(new Font("Segoe UI", Font.PLAIN, 10));
    line3.setForeground(new Color(220, 231, 255));
    line3.setAlignmentX(Component.LEFT_ALIGNMENT);

    text.add(Box.createVerticalStrut(8));
    text.add(line1);
    text.add(Box.createVerticalStrut(2));
    text.add(line2);
    text.add(Box.createVerticalStrut(3));
    text.add(line3);
    text.add(Box.createVerticalGlue());

    block.add(logo, BorderLayout.WEST);
    block.add(text, BorderLayout.CENTER);

    return block;
}
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(174, 195, 231));
        label.setBorder(new EmptyBorder(4, 10, 10, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(245, 28));
        return label;
    }

    private JPanel createMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.add(createTopBar(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 26, 8, 26));

        contentPanel.add(createDashboardPage(), "Dashboard");
        if (laAdmin() || laKeToan()) {
            contentPanel.add(wrapContentPanel(new NhanVienPanel(currentVaiTro)), "NhanVien");
        }
        if (coQuyenKho()) {
            contentPanel.add(wrapContentPanel(new NhaCungCapPanel(currentVaiTro)), "NhaCungCap");
        }
        if (coQuyenKhachHang()) {
            contentPanel.add(wrapContentPanel(new KhachHangPanel(currentVaiTro)), "KhachHang");
        }
        if (coQuyenHoaDon()) {
            contentPanel.add(wrapContentPanel(new HoaDonPanel(currentVaiTro, currentMaNhanVien)), "HoaDon");
        }
        if (laAdmin() || laNhanVienBanHang() || laNhanVienKho()) {
            contentPanel.add(wrapContentPanel(new SanPhamPanel(currentVaiTro)), "HangHoa");
        }
        if (coQuyenDoanhThu()) {
            contentPanel.add(wrapContentPanel(new DoanhThuPanel()), "DoanhThu");
        }

        main.add(contentPanel, BorderLayout.CENTER);
        main.add(createBottomClockBar(), BorderLayout.SOUTH);
        return main;
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setPreferredSize(new Dimension(0, 76));
        topBar.setBorder(new EmptyBorder(14, 28, 8, 28));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        lblCurrentPage = new JLabel("TRUNG TÂM HỆ THỐNG");
        lblCurrentPage.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblCurrentPage.setForeground(HEADING_NAVY);

        lblCurrentPageSub = new JLabel("THEO DÕI VÀ QUẢN LÝ TOÀN BỘ HỆ THỐNG");
        lblCurrentPageSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCurrentPageSub.setForeground(TEXT_SLATE);

        left.add(lblCurrentPage);
        left.add(Box.createVerticalStrut(3));
        left.add(lblCurrentPageSub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JButton btnBell = new BellButton();
        btnBell.setPreferredSize(new Dimension(42, 42));
        btnBell.addActionListener(e -> hienThiThongBao());

        JButton btnHelp = new HelpButton();
        btnHelp.setPreferredSize(new Dimension(42, 42));
        btnHelp.addActionListener(e -> hienThiHuongDanTheoVaiTro());

        JPanel avatar = new AvatarPanel();
        avatar.setPreferredSize(new Dimension(42, 42));

        JPanel userBox = new JPanel();
        userBox.setOpaque(false);
        userBox.setLayout(new BoxLayout(userBox, BoxLayout.Y_AXIS));

        lblUserName = new JLabel(laAdmin() ? "Admin" : currentTaiKhoan);
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUserName.setForeground(HEADING_NAVY);

        lblUserRole = new JLabel(tenVaiTroHienThi());
        lblUserRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUserRole.setForeground(TEXT_SLATE);

        userBox.add(lblUserName);
        userBox.add(lblUserRole);

        JButton logout = createLogoutButton();
        logout.addActionListener(e -> {
            int oldState = getExtendedState();
            boolean dangFullManHinh = (oldState & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
            Dimension oldSize = getSize();

            if (refreshTimer != null) refreshTimer.stop();
            ghiLichSu("Đăng xuất hệ thống");
            dispose();

            LoginFrame loginFrame = new LoginFrame();
            if (dangFullManHinh) loginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            else {
                loginFrame.setSize(oldSize);
                loginFrame.setLocationRelativeTo(null);
            }
            loginFrame.setVisible(true);
        });

        right.add(btnBell);
        if (laAdmin()) {
            right.add(btnHelp);
        }
        right.add(createVerticalDivider());
        right.add(avatar);
        right.add(userBox);
        right.add(Box.createHorizontalStrut(4));
        right.add(logout);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createBottomClockBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, LIGHT_BG_1, getWidth(), 0, new Color(238, 242, 247)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 44));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(216, 170, 69, 80)),
                new EmptyBorder(0, 26, 0, 26)
        ));

        lblDateTime = new JLabel();
        lblDateTime.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDateTime.setForeground(HEADING_NAVY);

        JLabel welcome = new JLabel("BiLuxury Fashion Management System");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 12));
        welcome.setForeground(TEXT_SLATE);

        bar.add(lblDateTime, BorderLayout.WEST);
        bar.add(welcome, BorderLayout.EAST);
        updateDateTime();
        return bar;
    }

    private void updateDateTime() {
        if (lblDateTime == null) return;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy  |  HH:mm:ss");
        lblDateTime.setText(now.format(fmt));
    }

    private JPanel createVerticalDivider() {
        JPanel sep = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(216, 224, 236));
                g2.fillRect(0, 8, 1, getHeight() - 16);
                g2.dispose();
            }
        };
        sep.setOpaque(false);
        sep.setPreferredSize(new Dimension(1, 44));
        return sep;
    }

    private JButton createLogoutButton() {
        JButton btn = new GradientLogoutButton("ĐĂNG XUẤT");
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setPreferredSize(new Dimension(132, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setMargin(new Insets(7, 14, 7, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel wrapContentPanel(JPanel panel) {
        styleLightContentPanel(panel);
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1),
                new EmptyBorder(16, 18, 18, 18)
        ));

        card.add(panel, BorderLayout.CENTER);
        outer.add(card, BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> styleLightContentPanel(panel));
        return outer;
    }

    private void styleScrollBar(JScrollPane scroll) {
        Color textDark = new Color(2, 8, 20);
        Color lineBlack = Color.BLACK;

        scroll.setOpaque(true);
        scroll.setBackground(Color.WHITE);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(Color.WHITE);

        if (scroll.getBorder() instanceof TitledBorder tb) {
            tb.setTitleColor(textDark);
            tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
            tb.setBorder(BorderFactory.createLineBorder(lineBlack, 1));
        } else {
            scroll.setBorder(BorderFactory.createLineBorder(lineBlack, 1));
        }

        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getHorizontalScrollBar().setUnitIncrement(18);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(16, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 16));
        scroll.getVerticalScrollBar().setBackground(Color.WHITE);
        scroll.getHorizontalScrollBar().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new CustomScrollBarUI());
    }

    private String layTextNutSach(String text) {
        return text == null ? "" : text.trim();
    }

    private ButtonIconType xacDinhLoaiNut(String text) {
        String lower = layTextNutSach(text).toLowerCase();
        if (lower.contains("xóa")) return ButtonIconType.DELETE;
        if (lower.contains("hủy") || lower.contains("đóng")) return ButtonIconType.CANCEL;
        if (lower.contains("sửa")) return ButtonIconType.EDIT;
        if (lower.contains("thêm") || lower.contains("tạo")) return ButtonIconType.ADD;
        if (lower.contains("thanh toán") || lower.contains("xác nhận")) return ButtonIconType.PAYMENT;
        if (lower.contains("tìm") || lower.contains("lọc")) return ButtonIconType.SEARCH;
        if (lower.contains("xuất doanh thu") || lower.contains("in")) return ButtonIconType.PRINT;
        if (lower.contains("biểu đồ") || lower.contains("bieu do")) return ButtonIconType.VIEW;
        if (lower.contains("hiển thị") || lower.contains("tất cả")) return ButtonIconType.LIST;
        if (lower.contains("làm mới")) return ButtonIconType.REFRESH;
        if (lower.contains("sắp hết")) return ButtonIconType.WARNING;
        if (lower.contains("xem") || lower.contains("mở")) return ButtonIconType.VIEW;
        return ButtonIconType.NONE;
    }

    private Icon taoIconNut(String text) {
        ButtonIconType type = xacDinhLoaiNut(text);
        return type == ButtonIconType.NONE ? null : new ButtonActionIcon(type, 15);
    }

    private enum ButtonIconType {
        ADD, EDIT, DELETE, CANCEL, SEARCH, LIST, REFRESH, WARNING, PAYMENT, PRINT, VIEW, NONE
    }

private void styleLightContentPanel(Component comp) {
    Color bg = Color.WHITE;
    Color bgAlt = new Color(248, 250, 252);
    Color headerBg = new Color(226, 232, 240);
    Color textDark = new Color(2, 8, 20);
    Color lineBlack = Color.BLACK;
    Color selectBg = new Color(216, 224, 236);

    if (comp instanceof JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(bg);
        panel.setForeground(textDark);
    }

    if (comp instanceof JLabel label) {
        label.setForeground(textDark);
        Font f = label.getFont();
        int style = f != null ? f.getStyle() : Font.PLAIN;
        int size = f != null ? Math.max(f.getSize(), 13) : 13;
        label.setFont(new Font("Segoe UI", style, size));
    }

    if (comp instanceof JTextField txt && !(comp instanceof JPasswordField)) {
        txt.setBackground(bg);
        txt.setForeground(textDark);
        txt.setCaretColor(textDark);
        txt.setOpaque(true);
        txt.setHorizontalAlignment(SwingConstants.LEFT);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineBlack, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    if (comp instanceof JPasswordField pwd) {
        pwd.setBackground(bg);
        pwd.setForeground(textDark);
        pwd.setCaretColor(textDark);
        pwd.setOpaque(true);
        pwd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineBlack, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    if (comp instanceof JTextArea area) {
        area.setBackground(bg);
        area.setForeground(textDark);
        area.setCaretColor(textDark);
        area.setOpaque(true);
        area.setBorder(BorderFactory.createLineBorder(lineBlack, 1));
    }

    if (comp instanceof JComboBox<?> combo) {
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        combo.setBackground(bg);
        combo.setForeground(textDark);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setOpaque(true);
        combo.setFocusable(false);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                Component c = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );
                c.setBackground(isSelected ? selectBg : bg);
                c.setForeground(textDark);
                if (c instanceof JLabel l) {
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                }
                return c;
            }
        });
        combo.setBorder(BorderFactory.createLineBorder(lineBlack, 1));
    }

    if (comp instanceof JCheckBox chk) {
        chk.setOpaque(true);
        chk.setBackground(bg);
        chk.setForeground(textDark);
        chk.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    
    if (comp instanceof JButton btn) {
        String cleanText = layTextNutSach(btn.getText());

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Font oldFont = btn.getFont();
        int size = oldFont != null ? Math.max(oldFont.getSize(), 13) : 13;
        btn.setFont(new Font("Segoe UI", Font.BOLD, size));

        btn.setMargin(new Insets(9, 18, 9, 18));
        btn.setText(cleanText);

        
        if (btn.getIcon() != null) {
            btn.setIconTextGap(8);
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        }

        
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(7, 12, 7, 12)
        ));
    }

    if (comp instanceof JTable table) {
        table.setBackground(bg);
        table.setForeground(textDark);
        table.setGridColor(lineBlack);
        table.setSelectionBackground(selectBg);
        table.setSelectionForeground(textDark);
        table.setShowGrid(true);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(Math.max(table.getRowHeight(), 30));
        table.setFillsViewportHeight(true);

        table.getTableHeader().setBackground(headerBg);
        table.getTableHeader().setForeground(textDark);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        table.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(headerBg);
                setForeground(textDark);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setBorder(BorderFactory.createLineBorder(lineBlack, 1));
                return this;
            }
        });

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);

                if (isSelected) {
                    setBackground(selectBg);
                    setForeground(textDark);
                } else {
                    setBackground(row % 2 == 0 ? bg : bgAlt);
                    setForeground(textDark);
                }

                setBorder(BorderFactory.createLineBorder(lineBlack, 1));
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                return this;
            }
        });
    }

    if (comp instanceof JScrollPane scroll) {
        styleScrollBar(scroll);
    }

    if (comp instanceof JSplitPane split) {
        split.setOpaque(true);
        split.setBackground(bg);
        split.setForeground(textDark);
        split.setBorder(BorderFactory.createLineBorder(lineBlack, 1));
    }

    if (comp instanceof JComponent jc) {
        if (jc.getBorder() instanceof TitledBorder tb) {
            tb.setTitleColor(textDark);
            tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
            tb.setBorder(BorderFactory.createLineBorder(lineBlack, 1));
        }
    }

    if (comp instanceof Container container) {
        for (Component child : container.getComponents()) {
            styleLightContentPanel(child);
        }
    }
}

    private JPanel createDashboardPage() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setOpaque(false);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.setPreferredSize(new Dimension(1180, 700));
        body.setMaximumSize(new Dimension(1180, 720));

        JPanel hero = createHeroBanner();
        hero.setPreferredSize(new Dimension(0, 132));

        dashboardOverviewHolder = new JPanel(new BorderLayout());
        dashboardOverviewHolder.setOpaque(false);
        dashboardOverviewHolder.add(createDashboardOverview(), BorderLayout.CENTER);
        if (dashboardRefreshTimer == null) {
            dashboardRefreshTimer = new Timer(5000, e -> refreshDashboardOverview());
            dashboardRefreshTimer.start();
        }

        body.add(hero, BorderLayout.NORTH);
        body.add(dashboardOverviewHolder, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        page.add(body, gbc);
        return page;
    }

    private void refreshDashboardOverview() {
        if (dashboardOverviewHolder == null) return;
        dashboardOverviewHolder.removeAll();
        dashboardOverviewHolder.add(createDashboardOverview(), BorderLayout.CENTER);
        dashboardOverviewHolder.revalidate();
        dashboardOverviewHolder.repaint();
    }

    public void lamMoiTongQuanHeThong() {
        refreshDashboardOverview();
    }

    private JPanel createHeroBanner() {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 255, 255),
                        getWidth(), getHeight(), new Color(246, 242, 232)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);

                
                g2.setColor(new Color(15, 23, 42, 22));
                g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, 28, 28);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, 28, 28);

                g2.setColor(new Color(216, 170, 69, 95));
                g2.setStroke(new BasicStroke(1.6f));
                g2.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 6, 28, 28);

                
                java.awt.geom.Path2D wave = new java.awt.geom.Path2D.Double();
                wave.moveTo(-30, getHeight() * 0.68);
                wave.curveTo(getWidth() * 0.22, getHeight() * 0.38, getWidth() * 0.42, getHeight() * 0.88, getWidth() * 0.68, getHeight() * 0.48);
                wave.curveTo(getWidth() * 0.82, getHeight() * 0.26, getWidth() * 0.95, getHeight() * 0.40, getWidth() + 40, getHeight() * 0.18);
                g2.setStroke(new BasicStroke(6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(244, 210, 122, 85));
                g2.draw(wave);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(216, 170, 69, 155));
                g2.draw(wave);

                
                g2.setColor(new Color(216, 170, 69, 22));
                g2.fillOval(getWidth() - 250, -105, 360, 360);
                g2.setColor(new Color(6, 26, 58, 18));
                g2.fillOval(getWidth() - 105, getHeight() - 115, 150, 150);

                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(22, 34, 22, 34));

        
        JPanel center = new JPanel(new BorderLayout(0, 6));
        center.setOpaque(false);

        JLabel title = new JLabel("BILUXURY FASHION", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(HEADING_NAVY);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel sub = new JLabel("Premium Men’s Wear Management System", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sub.setForeground(NAVY_ROYAL);
        sub.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel marquee = createMarqueeLabel();
        JPanel middle = new JPanel();
        middle.setOpaque(false);
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        marquee.setAlignmentX(Component.CENTER_ALIGNMENT);
        middle.add(Box.createVerticalGlue());
        middle.add(title);
        middle.add(Box.createVerticalStrut(4));
        middle.add(sub);
        middle.add(Box.createVerticalStrut(8));
        middle.add(marquee);
        middle.add(Box.createVerticalGlue());

        center.add(middle, BorderLayout.CENTER);

        hero.add(center, BorderLayout.CENTER);
        return hero;
    }

    private JLabel createMarqueeLabel() {
        String msg = "     BILUXURY FASHION - QUẢN TRỊ THỜI TRANG NAM CHUYÊN NGHIỆP     •     THEO DÕI TỒN KHO - DOANH THU - HÓA ĐƠN THEO THỜI GIAN THỰC     •     XIN CHÀO " + tenVaiTroNgan() + " - " + currentTaiKhoan + "     •     WEAR THE LUXURY EVERY DAY     ";
        JLabel label = new JLabel(msg, SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(CHAMPAGNE_DARK);
        label.setOpaque(false);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setPreferredSize(new Dimension(820, 24));
        final String[] text = {msg};
        Timer t = new Timer(180, e -> {
            text[0] = text[0].substring(1) + text[0].charAt(0);
            label.setText(text[0]);
        });
        t.start();
        return label;
    }

    private JPanel createQuickAccessGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);
        
        grid.setPreferredSize(new Dimension(0, 430));
        grid.setMinimumSize(new Dimension(0, 400));

        if (laAdmin() || laKeToan()) {
            grid.add(new ActionCard("QUẢN LÝ NHÂN VIÊN", "Theo dõi hồ sơ, thông tin làm việc, lương và ca làm của nhân viên.", "NhanVien", "nhanvien.png", "NV"));
        }
        if (coQuyenKhachHang()) {
            grid.add(new ActionCard("QUẢN LÝ KHÁCH HÀNG", laAdmin() ? "Tra cứu, thêm, cập nhật thông tin và điểm tích lũy khách hàng." : "Tra cứu, thêm và cập nhật thông tin khách hàng khi bán hàng.", "KhachHang", "khachhang.png", "KH"));
        }
        if (coQuyenHoaDon()) {
            String tieuDeHD = laKeToan() ? "XEM HÓA ĐƠN" : "QUẢN LÝ HÓA ĐƠN";
            String moTaHD = laKeToan() ? "Chỉ xem danh sách hóa đơn, chi tiết hóa đơn và thông tin thanh toán." : "Tạo hóa đơn, cập nhật trạng thái, xem chi tiết và in hóa đơn.";
            grid.add(new ActionCard(tieuDeHD, moTaHD, "HoaDon", "hoadon.png", "HD"));
        }
        if (laAdmin() || laNhanVienBanHang() || laNhanVienKho()) {
            grid.add(new ActionCard(laNhanVienBanHang() ? "XEM SẢN PHẨM" : "QUẢN LÝ SẢN PHẨM", laNhanVienKho() ? "Thêm, sửa, xóa, nhập hàng và theo dõi tồn kho sản phẩm." : (laAdmin() ? "Quản lý sản phẩm, tồn kho, giá nhập, giá bán và nhà cung cấp." : "Xem danh sách sản phẩm, giá bán và số lượng tồn kho."), "HangHoa", "sanpham.png", "SP"));
        }
        if (coQuyenDoanhThu()) {
            String tieuDeDT = laNhanVienBanHang() ? "XEM DOANH THU HÔM NAY" : "QUẢN LÝ DOANH THU";
            String moTaDT = laNhanVienBanHang()
                    ? "Nhân viên bán hàng chỉ được xem doanh thu ngày hôm nay và xuất báo cáo ngày hôm nay."
                    : "Theo dõi doanh thu hôm nay, theo khoảng ngày và xuất báo cáo khi cần.";
            grid.add(new ActionCard(tieuDeDT, moTaDT, "DoanhThu", "doanhthu.png", "DT"));
        }
        if (coQuyenKho()) grid.add(new ActionCard("QUẢN LÝ NHÀ CUNG CẤP", "Quản lý nguồn hàng, thông tin công ty, liên hệ và mô tả nhà cung cấp.", "NhaCungCap", "nhacungcap.png", "NCC"));
        return grid;
    }


    private JPanel createDashboardOverview() {
        DashboardMetricData data = loadDashboardMetricData();

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setOpaque(false);

        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.add(createDashboardStatCard("DOANH THU HÔM NAY", moneyFormat.format(data.doanhThuHomNay) + "đ", "Hôm nay", CHAMPAGNE_DARK));
        stats.add(createDashboardStatCard("HÓA ĐƠN HÔM NAY", data.soHoaDonHomNay + " đơn", "Hôm nay", HEADING_NAVY));
        stats.add(createDashboardStatCard("CẢNH BÁO HẾT HÀNG", data.soSanPhamSapHet + " sản phẩm", "Khẩn cấp", new Color(210, 65, 55)));
        stats.add(createDashboardStatCard("SẢN PHẨM KINH DOANH", data.soSanPhamDangBan + " mẫu", "Hoạt động", new Color(15, 128, 88)));
        root.add(stats, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 14, 0));
        bottom.setOpaque(false);
        bottom.add(createDashboardListCard("!  Cảnh báo tồn kho", data.lowStockRows, true, () -> showDashboardLowStockDialog()));
        bottom.add(createDashboardListCard("Sản phẩm bán chạy", data.bestSellerRows, false, () -> showDashboardBestSellerDialog()));
        root.add(bottom, BorderLayout.CENTER);

        return root;
    }

    private JPanel createDashboardStatCard(String title, String value, String badge, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout(8, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 23, 42, 16));
                g2.fillRoundRect(4, 5, getWidth() - 8, getHeight() - 8, 18, 18);
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, getWidth(), getHeight(), new Color(248, 250, 252));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, 18, 18);
                g2.setColor(new Color(216, 224, 236));
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 5, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));
        card.setPreferredSize(new Dimension(0, 110));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(TEXT_SLATE);

        JLabel lblBadge = new JLabel("  " + badge + "  ");
        lblBadge.setOpaque(true);
        if ("Khẩn cấp".equalsIgnoreCase(badge)) {
            lblBadge.setBackground(new Color(255, 221, 87));
            lblBadge.setForeground(new Color(127, 29, 29));
        } else if ("Hoạt động".equalsIgnoreCase(badge)) {
            lblBadge.setBackground(new Color(52, 211, 153));
            lblBadge.setForeground(new Color(6, 78, 59));
        } else {
            lblBadge.setBackground(new Color(96, 165, 250));
            lblBadge.setForeground(Color.WHITE);
        }
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadge.setBorder(new EmptyBorder(4, 8, 4, 8));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblBadge, BorderLayout.EAST);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lblValue.setForeground(valueColor == null ? HEADING_NAVY : valueColor);

        card.add(top, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private JPanel createDashboardListCard(String title, java.util.List<DashboardRow> rows, boolean lowStock, Runnable xemTatCaAction) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 23, 42, 16));
                g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, 18, 18);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, 18, 18);
                g2.setColor(new Color(216, 224, 236));
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 5, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        card.setPreferredSize(new Dimension(0, 360));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(HEADING_NAVY);
        JButton xemTatCa = createTinyDashboardButton("Xem tất cả");
        xemTatCa.addActionListener(e -> {
            if (xemTatCaAction != null) xemTatCaAction.run();
        });
        header.add(lbl, BorderLayout.WEST);
        header.add(xemTatCa, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        if (rows == null || rows.isEmpty()) {
            JLabel empty = new JLabel(lowStock ? "Không có sản phẩm sắp hết." : "Chưa có dữ liệu bán chạy.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            empty.setForeground(TEXT_SLATE);
            empty.setBorder(new EmptyBorder(18, 8, 0, 8));
            list.add(empty);
        } else {
            int i = 1;
            for (DashboardRow row : rows) {
                if (i > 5) break;
                list.add(createDashboardRow(row, i++, lowStock));
                list.add(Box.createVerticalStrut(8));
            }
        }
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JButton createTinyDashboardButton(String text) {
        JButton btn = new JButton(text);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(NAVY_ROYAL);
        btn.setBackground(new Color(236, 244, 255));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 224, 236), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createDashboardRow(DashboardRow row, int index, boolean lowStock) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(true);
        item.setBackground(new Color(248, 250, 252));
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel rank = new JLabel(String.valueOf(index), SwingConstants.CENTER);
        rank.setOpaque(true);
        rank.setBackground(lowStock ? new Color(255, 241, 242) : new Color(236, 244, 255));
        rank.setForeground(lowStock ? new Color(190, 18, 60) : NAVY_ROYAL);
        rank.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rank.setPreferredSize(new Dimension(34, 34));

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(row.title);
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
        name.setForeground(HEADING_NAVY);
        JLabel sub = new JLabel(row.subTitle);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(TEXT_SLATE);
        textBox.add(name);
        textBox.add(Box.createVerticalStrut(2));
        textBox.add(sub);

        JLabel value = new JLabel(row.value, SwingConstants.CENTER);
        value.setOpaque(true);
        value.setBackground(lowStock ? new Color(254, 226, 226) : new Color(209, 250, 229));
        value.setForeground(lowStock ? new Color(185, 28, 28) : new Color(4, 120, 87));
        value.setFont(new Font("Segoe UI", Font.BOLD, 11));
        value.setBorder(new EmptyBorder(5, 12, 5, 12));

        item.add(rank, BorderLayout.WEST);
        item.add(textBox, BorderLayout.CENTER);
        item.add(value, BorderLayout.EAST);
        return item;
    }

    private DashboardMetricData loadDashboardMetricData() {
        DashboardMetricData data = new DashboardMetricData();
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("""
                    SELECT COALESCE(SUM(CASE WHEN hd.thanhToan - ISNULL(tra.tienHoanTra, 0) < 0 THEN 0 ELSE hd.thanhToan - ISNULL(tra.tienHoanTra, 0) END), 0) AS doanhThu,
                           COUNT(DISTINCT hd.maHoaDon) AS soHoaDon
                    FROM HoaDon hd
                    LEFT JOIN (SELECT maHoaDon, SUM(tienHoanTra) AS tienHoanTra FROM PhieuTraHang GROUP BY maHoaDon) tra
                           ON tra.maHoaDon = hd.maHoaDon
                    WHERE hd.trangThai IN (N'Đã thanh toán', N'Trả một phần hàng')
                      AND CAST(COALESCE(hd.ngayThanhToan, hd.ngayLap) AS DATE) = CAST(GETDATE() AS DATE)
                    """)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        data.doanhThuHomNay = rs.getDouble("doanhThu");
                        data.soHoaDonHomNay = rs.getInt("soHoaDon");
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("""
                    SELECT COUNT(*) AS soLuong
                    FROM SanPhamKichCo
                    WHERE trangThai = N'Đang bán' AND soLuongTon <= 5
                    """)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) data.soSanPhamSapHet = rs.getInt("soLuong");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("""
                    SELECT COUNT(DISTINCT maSanPham) AS soLuong
                    FROM SanPhamKichCo
                    WHERE trangThai = N'Đang bán'
                    """)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) data.soSanPhamDangBan = rs.getInt("soLuong");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("""
                    SELECT TOP 5 sp.tenSanPham, kc.kichCo, kc.soLuongTon
                    FROM SanPhamKichCo kc
                    JOIN SanPham sp ON sp.maSanPham = kc.maSanPham
                    WHERE kc.trangThai = N'Đang bán' AND kc.soLuongTon <= 5
                    ORDER BY kc.soLuongTon ASC, sp.tenSanPham ASC
                    """)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data.lowStockRows.add(new DashboardRow(
                                rs.getString("tenSanPham"),
                                "Size: " + rs.getString("kichCo"),
                                rs.getInt("soLuongTon") + " tồn"
                        ));
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("""
                    WITH tra AS (
                        SELECT maHoaDon, maSanPham, ISNULL(kichCo, 'M') AS kichCo, SUM(soLuongTra) AS soLuongTra, SUM(tienGocTra) AS tienGocTra
                        FROM PhieuTraHang
                        GROUP BY maHoaDon, maSanPham, ISNULL(kichCo, 'M')
                    )
                    SELECT TOP 5 sp.tenSanPham,
                           SUM(ct.soLuong - ISNULL(tra.soLuongTra, 0)) AS soBan,
                           SUM((ct.soLuong * ct.donGia) - ISNULL(tra.tienGocTra, 0)) AS doanhThu
                    FROM HoaDon hd
                    JOIN ChiTietHoaDon ct ON ct.maHoaDon = hd.maHoaDon
                    JOIN SanPham sp ON sp.maSanPham = ct.maSanPham
                    LEFT JOIN tra ON tra.maHoaDon = ct.maHoaDon AND tra.maSanPham = ct.maSanPham AND tra.kichCo = ISNULL(ct.kichCo, 'M')
                    WHERE hd.trangThai IN (N'Đã thanh toán', N'Trả một phần hàng')
                    GROUP BY sp.tenSanPham
                    HAVING SUM(ct.soLuong - ISNULL(tra.soLuongTra, 0)) > 100
                    ORDER BY SUM(ct.soLuong - ISNULL(tra.soLuongTra, 0)) DESC, SUM((ct.soLuong * ct.donGia) - ISNULL(tra.tienGocTra, 0)) DESC
                    """)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data.bestSellerRows.add(new DashboardRow(
                                rs.getString("tenSanPham"),
                                rs.getInt("soBan") + " đã bán · DT: " + moneyFormat.format(rs.getDouble("doanhThu")) + "đ",
                                "+" + rs.getInt("soBan")
                        ));
                    }
                }
            }
        } catch (Exception e) {
            data.lowStockRows.clear();
            data.bestSellerRows.clear();
        }
        return data;
    }

    private void showDashboardLowStockDialog() {
        String[] cols = {"Mã SP", "Tên sản phẩm", "Size", "Tồn kho", "Trạng thái"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("""
                SELECT sp.maSanPham, sp.tenSanPham, kc.kichCo, kc.soLuongTon, kc.trangThai
                FROM SanPhamKichCo kc
                JOIN SanPham sp ON sp.maSanPham = kc.maSanPham
                WHERE kc.trangThai = N'Đang bán' AND kc.soLuongTon <= 5
                ORDER BY kc.soLuongTon ASC, sp.tenSanPham ASC
                """)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) m.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5)});
            }
        } catch (Exception ex) { AppDialog.showError(this, "Lỗi", "Không đọc được cảnh báo tồn kho: " + ex.getMessage()); return; }
        showDashboardTableDialog("Tất cả sản phẩm sắp hết - " + m.getRowCount() + " sản phẩm", m);
    }

    private void showDashboardBestSellerDialog() {
        String[] cols = {"Mã SP", "Tên sản phẩm", "Đã bán", "Doanh thu"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("""
                WITH tra AS (
                    SELECT maHoaDon, maSanPham, ISNULL(kichCo, 'M') AS kichCo, SUM(soLuongTra) AS soLuongTra, SUM(tienGocTra) AS tienGocTra
                    FROM PhieuTraHang
                    GROUP BY maHoaDon, maSanPham, ISNULL(kichCo, 'M')
                )
                SELECT sp.maSanPham, sp.tenSanPham,
                       SUM(ct.soLuong - ISNULL(tra.soLuongTra, 0)) AS soBan,
                       SUM((ct.soLuong * ct.donGia) - ISNULL(tra.tienGocTra, 0)) AS doanhThu
                FROM HoaDon hd
                JOIN ChiTietHoaDon ct ON ct.maHoaDon = hd.maHoaDon
                JOIN SanPham sp ON sp.maSanPham = ct.maSanPham
                LEFT JOIN tra ON tra.maHoaDon = ct.maHoaDon AND tra.maSanPham = ct.maSanPham AND tra.kichCo = ISNULL(ct.kichCo, 'M')
                WHERE hd.trangThai IN (N'Đã thanh toán', N'Trả một phần hàng')
                GROUP BY sp.maSanPham, sp.tenSanPham
                HAVING SUM(ct.soLuong - ISNULL(tra.soLuongTra, 0)) > 100
                ORDER BY soBan DESC, doanhThu DESC
                """)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) m.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getInt(3), moneyFormat.format(rs.getDouble(4)) + "đ"});
            }
        } catch (Exception ex) { AppDialog.showError(this, "Lỗi", "Không đọc được sản phẩm bán chạy: " + ex.getMessage()); return; }
        showDashboardTableDialog("Tất cả sản phẩm bán chạy - " + m.getRowCount() + " sản phẩm", m);
    }

    private void showDashboardTableDialog(String titleText, DefaultTableModel m) {
        JDialog dialog = new JDialog(this, titleText, true);
        dialog.setSize(920, 600);
        dialog.setMinimumSize(new Dimension(820, 520));
        dialog.setLocationRelativeTo(this);
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(HEADING_NAVY);
        JTable tbl = new JTable(m);
        tbl.setRowHeight(34);
        tbl.setFillsViewportHeight(true);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbl.setAutoCreateRowSorter(true);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tbl.getTableHeader().setBackground(new Color(226, 232, 240));
        tbl.getTableHeader().setForeground(HEADING_NAVY);
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) tbl.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setGridColor(new Color(216, 224, 236));
        tbl.setSelectionBackground(new Color(18, 59, 140));
        tbl.setSelectionForeground(Color.WHITE);
        tbl.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        if (tbl.getColumnModel().getColumnCount() == 5) {
            int[] widths = {105, 330, 105, 105, 150};
            for (int i = 0; i < widths.length; i++) {
                tbl.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
                tbl.getColumnModel().getColumn(i).setMinWidth(i == 1 ? 180 : 80);
            }
        } else if (tbl.getColumnModel().getColumnCount() == 4) {
            int[] widths = {115, 380, 120, 170};
            for (int i = 0; i < widths.length; i++) {
                tbl.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
                tbl.getColumnModel().getColumn(i).setMinWidth(i == 1 ? 220 : 90);
            }
        } else {
            for (int i = 0; i < tbl.getColumnModel().getColumnCount(); i++) {
                tbl.getColumnModel().getColumn(i).setPreferredWidth(i == 1 ? 320 : 130);
                tbl.getColumnModel().getColumn(i).setMinWidth(80);
            }
        }
        JScrollPane sp = new JScrollPane(tbl);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        styleScrollBar(sp);
        JButton close = new JButton("Đóng");
        close.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        close.setFont(new Font("Segoe UI", Font.BOLD, 14));
        close.setBackground(CHAMPAGNE);
        close.setForeground(HEADING_NAVY);
        close.setFocusPainted(false);
        close.setOpaque(true);
        close.setContentAreaFilled(true);
        close.setPreferredSize(new Dimension(150, 44));
        close.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CHAMPAGNE_DARK, 1),
                new EmptyBorder(8, 18, 8, 18)
        ));
        close.addActionListener(e -> dialog.dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        bottom.add(close);
        root.add(title, BorderLayout.NORTH);
        root.add(sp, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private static class DashboardMetricData {
        double doanhThuHomNay;
        int soHoaDonHomNay;
        int soSanPhamSapHet;
        int soSanPhamDangBan;
        java.util.List<DashboardRow> lowStockRows = new java.util.ArrayList<>();
        java.util.List<DashboardRow> bestSellerRows = new java.util.ArrayList<>();
    }

    private static class DashboardRow {
        final String title;
        final String subTitle;
        final String value;

        DashboardRow(String title, String subTitle, String value) {
            this.title = title == null ? "" : title;
            this.subTitle = subTitle == null ? "" : subTitle;
            this.value = value == null ? "" : value;
        }
    }

    private static final String[][] PAGE_META = {
            {"Dashboard", "TRUNG TÂM HỆ THỐNG", "THEO DÕI VÀ QUẢN LÝ TOÀN BỘ HỆ THỐNG"},
            {"NhanVien", "QUẢN LÝ NHÂN VIÊN", "THEO DÕI HỒ SƠ, LƯƠNG VÀ CA LÀM NHÂN VIÊN"},
            {"ThongTinNhanVien", "XEM THÔNG TIN NHÂN VIÊN", "XEM HỒ SƠ CÁ NHÂN, LIÊN HỆ, CHỨC VỤ VÀ CA LÀM"},
            {"KhachHang", "QUẢN LÝ KHÁCH HÀNG", "TRA CỨU, THÊM VÀ CẬP NHẬT THÔNG TIN KHÁCH HÀNG"},
            {"HoaDon", "QUẢN LÝ HÓA ĐƠN", "TẠO HÓA ĐƠN, XÁC NHẬN THANH TOÁN VÀ IN HÓA ĐƠN"},
            {"NhaCungCap", "QUẢN LÝ NHÀ CUNG CẤP", "QUẢN LÝ NGUỒN HÀNG, THÔNG TIN CÔNG TY VÀ LIÊN HỆ"},
            {"HangHoa", "QUẢN LÝ SẢN PHẨM", "THEO DÕI SẢN PHẨM, GIÁ BÁN VÀ SỐ LƯỢNG TỒN KHO"},
            {"DoanhThu", "QUẢN LÝ DOANH THU", "THEO DÕI DOANH THU THEO KHOẢNG THỜI GIAN"}
    };

    private void showPage(String pageName) {
        if (!coDuocMoTrang(pageName)) {
            AppDialog.showWarning(this, "Không có quyền", "Vai trò " + tenVaiTroHienThi() + " không có quyền truy cập chức năng này!");
            return;
        }
        lamMoiTrangKhiMo(pageName);
        cardLayout.show(contentPanel, pageName);
        setSelectedMenu(pageName);
        updatePageTitle(pageName);
    }

    private void lamMoiTrangKhiMo(String pageName) {
        if (contentPanel == null || pageName == null) return;
        lamMoiComponentTheoTrang(contentPanel, pageName);
    }

    private void lamMoiComponentTheoTrang(Component component, String pageName) {
        if (component == null) return;

        if ("NhaCungCap".equals(pageName) && component instanceof NhaCungCapPanel panel) {
            panel.lamMoiDuLieuTuSQL();
            return;
        }
        if ("HangHoa".equals(pageName) && component instanceof SanPhamPanel panel) {
            panel.lamMoiDuLieuTuSQL();
            return;
        }
        if ("KhachHang".equals(pageName) && component instanceof KhachHangPanel panel) {
            panel.lamMoiDuLieuTuSQL();
            return;
        }
        if ("NhanVien".equals(pageName) && component instanceof NhanVienPanel panel) {
            panel.lamMoiDuLieuTuSQL();
            return;
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                lamMoiComponentTheoTrang(child, pageName);
            }
        }
    }

    private boolean coDuocMoTrang(String pageName) {
        if ("Dashboard".equals(pageName)) return true;
        if ("NhanVien".equals(pageName)) return laAdmin() || laKeToan();
        if ("NhaCungCap".equals(pageName)) return coQuyenKho();
        if ("KhachHang".equals(pageName)) return coQuyenKhachHang();
        if ("HoaDon".equals(pageName)) return coQuyenHoaDon();
        if ("HangHoa".equals(pageName)) return laAdmin() || laNhanVienBanHang() || laNhanVienKho();
        if ("DoanhThu".equals(pageName)) return coQuyenDoanhThu();
        return false;
    }

    private void setSelectedMenu(String pageName) {
        if (btnDashboard != null) btnDashboard.setSelectedState("Dashboard".equals(pageName));
        if (btnNhanVien != null) btnNhanVien.setSelectedState("NhanVien".equals(pageName));
        if (btnKhachHang != null) btnKhachHang.setSelectedState("KhachHang".equals(pageName));
        if (btnHoaDon != null) btnHoaDon.setSelectedState("HoaDon".equals(pageName));
        if (btnNhaCungCap != null) btnNhaCungCap.setSelectedState("NhaCungCap".equals(pageName));
        if (btnHangHoa != null) btnHangHoa.setSelectedState("HangHoa".equals(pageName));
        if (btnDoanhThu != null) btnDoanhThu.setSelectedState("DoanhThu".equals(pageName));
    }

    private void updatePageTitle(String pageName) {
        for (String[] meta : PAGE_META) {
            if (meta[0].equals(pageName)) {
                if ("HoaDon".equals(pageName) && laKeToan()) {
                    lblCurrentPage.setText("XEM HÓA ĐƠN");
                    lblCurrentPageSub.setText("CHỈ XEM DANH SÁCH VÀ CHI TIẾT HÓA ĐƠN");
                } else if ("HangHoa".equals(pageName) && laNhanVienBanHang()) {
                    lblCurrentPage.setText("XEM SẢN PHẨM");
                    lblCurrentPageSub.setText("XEM DANH SÁCH, GIÁ BÁN VÀ TỒN KHO SẢN PHẨM");
                } else {
                    lblCurrentPage.setText(meta[1]);
                    lblCurrentPageSub.setText(meta[2]);
                }
                return;
            }
        }
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(1000, e -> updateDateTime());
        refreshTimer.start();
    }

    private void hienThiThongBao() {
        JDialog dialog = new JDialog(this, "Thông báo hoạt động", true);
        dialog.setSize(520, 430);
        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                new EmptyBorder(20, 22, 20, 22)
        ));

        JLabel title = new JLabel("THÔNG BÁO HOẠT ĐỘNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.BLACK);
        title.setBorder(new EmptyBorder(0, 0, 14, 0));

        DefaultListModel<String> model = new DefaultListModel<>();
        napThongBao(model);

        JList<String> list = new JList<>(model);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        list.setBackground(Color.WHITE);
        list.setForeground(Color.BLACK);
        list.setSelectionBackground(new Color(216, 224, 236));
        list.setSelectionForeground(Color.BLACK);
        list.setFixedCellHeight(38);
        list.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(list);
        styleScrollBar(scroll);

        JButton close = new JButton("ĐÓNG");
        close.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        close.setFont(FONT_BOLD);
        close.setBackground(Color.WHITE);
        close.setForeground(Color.BLACK);
        close.setFocusPainted(false);
        close.setOpaque(true);
        close.setContentAreaFilled(true);
        close.setPreferredSize(new Dimension(100, 36));
        close.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        close.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(true);
        bottom.setBackground(Color.WHITE);
        bottom.add(close);

        root.add(title, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private void napThongBao(DefaultListModel<String> model) {
        String sql = """
                SELECT TOP 20 taiKhoan, hanhDong, thoiGian
                FROM LichSuHoatDong
                ORDER BY thoiGian DESC
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String tk = rs.getString("taiKhoan");
                String hd = rs.getString("hanhDong");
                Timestamp ts = rs.getTimestamp("thoiGian");
                String timeText = ts != null ? " - " + tinhThoiGian(ts.toLocalDateTime()) : "";
                model.addElement(tk + ": " + hd + timeText);
            }
            if (model.isEmpty()) model.addElement("Chưa có thông báo hoạt động nào.");
        } catch (Exception e) {
            model.addElement("Không đọc được thông báo: " + e.getMessage());
        }
    }

    private String tinhThoiGian(LocalDateTime time) {
        Duration d = Duration.between(time, LocalDateTime.now());
        long minutes = d.toMinutes();
        long hours = d.toHours();
        long days = d.toDays();
        if (minutes < 1) return "vừa xong";
        if (minutes < 60) return minutes + " phút trước";
        if (hours < 24) return hours + " giờ trước";
        return days + " ngày trước";
    }

    private void ghiLichSu(String hanhDong) {
        String sql = """
                INSERT INTO LichSuHoatDong
                (taiKhoan, vaiTro, hanhDong)
                VALUES (?, ?, ?)
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentTaiKhoan);
            ps.setString(2, currentVaiTro);
            ps.setString(3, hanhDong);
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
    }


    private class SidebarGradientPanel extends JPanel {
        SidebarGradientPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, NAVY_DARK, 0, getHeight(), NAVY_BLACK);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(216, 170, 69, 24));
            g2.fillOval(-95, 80, 210, 210);
            g2.setColor(new Color(244, 210, 122, 18));
            g2.fillOval(getWidth() - 110, getHeight() - 180, 220, 220);
            g2.setColor(new Color(216, 170, 69, 70));
            g2.fillRect(getWidth() - 1, 0, 1, getHeight());
            g2.dispose();
        }
    }

    private class GradientLogoutButton extends JButton {
        GradientLogoutButton(String text) {
            super(text);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, new Color(29, 78, 216), getWidth(), getHeight(), NAVY_DARK);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(new Color(216, 170, 69, 150));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class SidebarMenuIcon implements Icon {
        private final String pageName;
        private final int size;

        SidebarMenuIcon(String pageName, int size) {
            this.pageName = pageName == null ? "" : pageName;
            this.size = size;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(CHAMPAGNE_LIGHT);
            int s = size;
            int cx = x + s / 2;
            int cy = y + s / 2;

            switch (pageName) {
                case "Dashboard" -> {
                    g2.drawRoundRect(x + 2, y + 2, s - 4, s - 4, 4, 4);
                    g2.drawLine(x + 2, y + 8, x + s - 2, y + 8);
                    g2.drawLine(x + 8, y + 8, x + 8, y + s - 2);
                }
                case "NhanVien", "ThongTinNhanVien" -> {
                    g2.drawOval(cx - 4, y + 2, 8, 8);
                    g2.drawRoundRect(x + 3, y + 12, s - 6, s - 6, 8, 8);
                }
                case "KhachHang" -> {
                    g2.drawOval(x + 2, y + 3, 7, 7);
                    g2.drawOval(x + s - 9, y + 3, 7, 7);
                    g2.drawRoundRect(x + 1, y + 12, s - 2, s - 7, 8, 8);
                }
                case "HoaDon" -> {
                    g2.drawRoundRect(x + 3, y + 2, s - 6, s - 4, 3, 3);
                    g2.drawLine(x + 6, y + 7, x + s - 6, y + 7);
                    g2.drawLine(x + 6, y + 12, x + s - 7, y + 12);
                }
                case "HangHoa" -> {
                    g2.drawRoundRect(x + 3, y + 4, s - 6, s - 8, 4, 4);
                    g2.drawLine(x + 6, y + 7, x + s - 6, y + 7);
                    g2.drawLine(x + 6, y + s - 6, x + s - 6, y + s - 6);
                }
                case "DoanhThu" -> {
                    g2.drawLine(x + 3, y + s - 4, x + s - 3, y + s - 4);
                    g2.drawLine(x + 5, y + s - 5, x + 5, y + 9);
                    g2.drawLine(cx, y + s - 5, cx, y + 5);
                    g2.drawLine(x + s - 5, y + s - 5, x + s - 5, y + 12);
                }
                case "NhaCungCap" -> {
                    g2.drawRoundRect(x + 2, y + 5, s - 4, s - 7, 3, 3);
                    g2.drawLine(x + 4, y + 9, x + s - 4, y + 9);
                    g2.drawLine(x + 6, y + 3, x + s - 6, y + 3);
                }
                default -> g2.drawOval(x + 3, y + 3, s - 6, s - 6);
            }
            g2.dispose();
        }
    }

    private class SidebarItem extends JButton {
        private boolean selected = false;
        private boolean hover = false;

        SidebarItem(String text, String pageName) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(new Color(234, 240, 255));
            setHorizontalAlignment(SwingConstants.LEFT);
            setMaximumSize(new Dimension(245, 48));
            setPreferredSize(new Dimension(245, 48));
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 16, 0, 16));
            setContentAreaFilled(false);
            setOpaque(false);
            setIcon(new SidebarImageMenuIcon(pageName, 24));
            setIconTextGap(10);
            setHorizontalTextPosition(SwingConstants.RIGHT);
            addActionListener(e -> showPage(pageName));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; if (!selected) setForeground(Color.WHITE); repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; if (!selected) setForeground(new Color(234, 240, 255)); repaint(); }
            });
        }

        void setSelectedState(boolean selected) {
            this.selected = selected;
            setForeground(selected ? Color.WHITE : new Color(234, 240, 255));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selected) {
                GradientPaint gp = new GradientPaint(0, 0, NAVY_ACTIVE, getWidth(), getHeight(), CHAMPAGNE);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(216, 170, 69, 90));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
            } else {
                GradientPaint gp = new GradientPaint(0, 0, new Color(9, 39, 90, hover ? 210 : 160),
                        getWidth(), getHeight(), new Color(3, 16, 36, hover ? 235 : 180));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(28, 63, 120, hover ? 190 : 115));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }


    private class SidebarImageMenuIcon implements Icon {
        private final Image image;
        private final Icon fallback;
        private final int size;

        SidebarImageMenuIcon(String pageName, int size) {
            this.size = size;
            this.image = loadMenuImage(iconFileForPage(pageName), size + 6, size + 6);
            this.fallback = new SidebarMenuIcon(pageName, size);
        }

        private String iconFileForPage(String pageName) {
            if ("NhanVien".equals(pageName) || "ThongTinNhanVien".equals(pageName)) return "nhanvien.png";
            if ("KhachHang".equals(pageName)) return "khachhang.png";
            if ("HoaDon".equals(pageName)) return "hoadon.png";
            if ("HangHoa".equals(pageName)) return "sanpham.png";
            if ("DoanhThu".equals(pageName)) return "doanhthu.png";
            if ("NhaCungCap".equals(pageName)) return "nhacungcap.png";
            if ("Dashboard".equals(pageName)) return "dashboard.png";
            return "dashboard.png";
        }

        @Override public int getIconWidth() { return size + 8; }
        @Override public int getIconHeight() { return size + 8; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (image == null) {
                fallback.paintIcon(c, g, x, y);
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            int box = size + 8;
            GradientPaint gp = new GradientPaint(x, y, NAVY_MID, x + box, y + box, NAVY_BLACK);
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, box, box, 10, 10);
            g2.setColor(new Color(216, 170, 69, 135));
            g2.drawRoundRect(x, y, box - 1, box - 1, 10, 10);
            g2.drawImage(image, x + 4, y + 4, size, size, c);
            g2.dispose();
        }
    }

    private class ActionCard extends JPanel {
        private boolean hover = false;

        ActionCard(String title, String desc, String pageName, String iconFileName, String fallbackText) {
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(18, 22, 16, 22));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            ImageCircleIcon icon = new ImageCircleIcon(iconFileName, fallbackText);
            icon.setPreferredSize(new Dimension(72, 72));

            JLabel lblTitle = new JLabel("<html><div style='text-align:left;'>" + title + "</div></html>", SwingConstants.LEFT);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
            lblTitle.setForeground(HEADING_NAVY);
            lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
            lblTitle.setVerticalAlignment(SwingConstants.CENTER);
            lblTitle.setBorder(new EmptyBorder(0, 14, 0, 0));

            JPanel topRow = new JPanel(new BorderLayout(14, 0));
            topRow.setOpaque(false);
            topRow.add(icon, BorderLayout.WEST);
            topRow.add(lblTitle, BorderLayout.CENTER);

            JTextArea lblDesc = new JTextArea(desc);
            lblDesc.setLineWrap(true);
            lblDesc.setWrapStyleWord(true);
            lblDesc.setEditable(false);
            lblDesc.setOpaque(false);
            lblDesc.setFocusable(false);
            lblDesc.setHighlighter(null);
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblDesc.setForeground(TEXT_SLATE);
            lblDesc.setBorder(new EmptyBorder(12, 0, 0, 0));
            lblDesc.setRows(2);
            lblDesc.setPreferredSize(new Dimension(10, 54));
            lblDesc.setMinimumSize(new Dimension(10, 48));

            JLabel go = new JLabel("MỞ CHỨC NĂNG →", SwingConstants.LEFT);
            go.setFont(new Font("Segoe UI", Font.BOLD, 14));
            go.setForeground(new Color(36, 60, 155));
            go.setHorizontalAlignment(SwingConstants.LEFT);
            go.setBorder(new EmptyBorder(8, 0, 0, 0));

            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setOpaque(false);
            centerPanel.add(topRow, BorderLayout.NORTH);
            centerPanel.add(lblDesc, BorderLayout.CENTER);
            centerPanel.add(go, BorderLayout.SOUTH);
            add(centerPanel, BorderLayout.CENTER);

            MouseAdapter openAction = new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { showPage(pageName); }
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            };
            addMouseListener(openAction);
            icon.addMouseListener(openAction);
            centerPanel.addMouseListener(openAction);
            lblTitle.addMouseListener(openAction);
            lblDesc.addMouseListener(openAction);
            go.addMouseListener(openAction);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(new Color(15, 23, 42, hover ? 34 : 22));
            g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, 24, 24);

            GradientPaint gp = new GradientPaint(0, 0, CARD_LIGHT, getWidth(), getHeight(), CARD_LIGHT_2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, 24, 24);
            g2.setColor(hover ? new Color(230, 200, 120) : CARD_STROKE);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 5, 24, 24);
            g2.setColor(new Color(216, 170, 69, hover ? 38 : 20));
            g2.fillOval(getWidth() - 92, -38, 128, 128);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private Image loadMenuImage(String fileName, int w, int h) {
        try {
            BufferedImage source = readMenuImage(fileName);
            if (source == null || source.getWidth() <= 0 || source.getHeight() <= 0) return null;
            return scaleHighQuality(source, w, h);
        } catch (Exception e) {
            return null;
        }
    }

    private BufferedImage readMenuImage(String fileName) {
        try {
            BufferedImage img = loadImageAnyPath(fileName);
            if (img != null) return img;
        } catch (Exception e) {
            System.out.println("Lỗi load ảnh menu " + fileName + ": " + e.getMessage());
        }
        return null;
    }

    private BufferedImage scaleHighQuality(BufferedImage source, int targetW, int targetH) {
        BufferedImage scaled = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.drawImage(source, 0, 0, targetW, targetH, null);
        g2.dispose();
        return scaled;
    }

    private class ImageCircleIcon extends JPanel {
        private final Image image;
        private final String fallbackText;

        ImageCircleIcon(String fileName, String fallbackText) {
            this.fallbackText = fallbackText == null ? "" : fallbackText;
            this.image = loadMenuImage(fileName, 62, 62);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

            int size = Math.min(getWidth(), getHeight()) - 6;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            GradientPaint gp = new GradientPaint(x, y, NAVY_MID, x + size, y + size, NAVY_BLACK);
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, size, size, 20, 20);
            g2.setColor(new Color(216, 170, 69, 105));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(x + 1, y + 1, size - 2, size - 2, 20, 20);

            if (image != null) {
                int imgSize = size - 12;
                int imgX = x + (size - imgSize) / 2;
                int imgY = y + (size - imgSize) / 2;
                g2.drawImage(image, imgX, imgY, imgSize, imgSize, this);
            } else {
                g2.setColor(CHAMPAGNE_LIGHT);
                g2.setFont(new Font("Segoe UI", Font.BOLD, fallbackText.length() >= 3 ? 16 : 22));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(fallbackText)) / 2;
                int ty = (getHeight() + fm.getAscent()) / 2 - 4;
                g2.drawString(fallbackText, tx, ty);
            }

            g2.dispose();
        }
    }


    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, LIGHT_BG_1, getWidth(), getHeight(), LIGHT_BG_3);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(216, 170, 69, 22));
            g2.fillOval(getWidth() - 260, -160, 440, 440);
            g2.setColor(new Color(29, 78, 216, 18));
            g2.fillOval(-180, getHeight() - 220, 360, 360);
            g2.dispose();
        }
    }

    private class LogoPanel extends JPanel {
    private final Image logoImage;

    LogoPanel() {
        setOpaque(false);
        logoImage = loadLogoImage(66, 66);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int size = Math.min(getWidth(), getHeight()) - 4;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

     
        GradientPaint gp = new GradientPaint(
                x, y, NAVY_MID,
                x + size, y + size, NAVY_BLACK
        );
        g2.setPaint(gp);
        g2.fillRoundRect(x, y, size, size, 20, 20);

        g2.setColor(new Color(216, 170, 69, 150));
        g2.setStroke(new BasicStroke(1.6f));
        g2.drawRoundRect(x + 1, y + 1, size - 2, size - 2, 20, 20);

        if (logoImage != null) {
            int imgSize = size - 12;
            int imgX = x + (size - imgSize) / 2;
            int imgY = y + (size - imgSize) / 2;
            g2.drawImage(logoImage, imgX, imgY, imgSize, imgSize, this);
        } else {
            
            g2.setColor(CHAMPAGNE_LIGHT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();
            String text = "BL";
            int tx = x + (size - fm.stringWidth(text)) / 2;
            int ty = y + (size + fm.getAscent()) / 2 - 5;
            g2.drawString(text, tx, ty);
        }

        g2.dispose();
    }
}

    private class LogoLargePanel extends JPanel {
        private final Image logoImage;

        LogoLargePanel() {
            setOpaque(false);
            logoImage = loadLogoImage(150, 150);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (logoImage != null) {
                int x = (getWidth() - logoImage.getWidth(this)) / 2;
                int y = (getHeight() - logoImage.getHeight(this)) / 2;
                g.drawImage(logoImage, x, y, this);
            }
        }
    }

    private Image loadAvatarImage(String fileName, int w, int h) {
        try {
            BufferedImage source = readMenuImage(fileName);
            if (source == null || source.getWidth() <= 0 || source.getHeight() <= 0) return null;
            return scaleHighQuality(source, w, h);
        } catch (Exception e) {
            return null;
        }
    }

    private class AvatarPanel extends JPanel {
        private final Image avatarImage;

        AvatarPanel() {
            setOpaque(false);
            String avatarFile = laAdmin() ? "admin.png" : "nhanvien.png";
            avatarImage = loadAvatarImage(avatarFile, 38, 38);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(new Color(192, 149, 94, 60));
            g2.fillOval(x, y, size, size);
            g2.setColor(GOLD);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x + 1, y + 1, size - 3, size - 3);

            if (avatarImage != null) {
                int imgSize = size - 8;
                int imgX = x + (size - imgSize) / 2;
                int imgY = y + (size - imgSize) / 2;
                Shape oldClip = g2.getClip();
                g2.setClip(new java.awt.geom.Ellipse2D.Double(x + 3, y + 3, size - 6, size - 6));
                g2.drawImage(avatarImage, imgX, imgY, imgSize, imgSize, this);
                g2.setClip(oldClip);
            } else {
                veIconNguoi(g2, x, y, size);
            }
            g2.dispose();
        }

        private void veIconNguoi(Graphics2D g2, int x, int y, int size) {
            int head = Math.max(9, size / 4);
            int headX = x + (size - head) / 2;
            int headY = y + size / 5;
            g2.setColor(new Color(30, 24, 20));
            g2.fillOval(headX, headY, head, head);
            int bodyW = size / 2;
            int bodyH = size / 3;
            int bodyX = x + (size - bodyW) / 2;
            int bodyY = y + size / 2;
            g2.fillRoundRect(bodyX, bodyY, bodyW, bodyH, bodyH, bodyH);
        }
    }

    private class BellButton extends JButton {
        BellButton() {
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
            setText("");
            setIcon(new BellIcon(CHAMPAGNE, 22));
            setBackground(CARD_LIGHT);
            setForeground(CHAMPAGNE);
            setFocusPainted(false);
            setOpaque(true);
            setContentAreaFilled(true);
            setBorderPainted(true);
            setMargin(new Insets(5, 5, 5, 5));
            setBorder(BorderFactory.createLineBorder(new Color(216, 170, 69, 110), 1));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    private class BellIcon implements Icon {
        private final Color color;
        private final int size;

        BellIcon(Color color, int size) {
            this.color = color;
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
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = x + size / 2;
            int top = y + 4;
            int bellW = size - 7;
            int bellH = size - 9;
            int left = cx - bellW / 2;

            g2.drawArc(left, top + 2, bellW, bellH, 0, 180);
            g2.drawLine(left, top + bellH / 2 + 2, left, top + bellH - 2);
            g2.drawLine(left + bellW, top + bellH / 2 + 2, left + bellW, top + bellH - 2);
            g2.drawLine(left - 2, top + bellH - 2, left + bellW + 2, top + bellH - 2);
            g2.fillOval(cx - 3, y + size - 5, 6, 5);
            g2.drawLine(cx, y + 2, cx, y + 5);

            g2.dispose();
        }
    }

    private class ButtonActionIcon implements Icon {
        private final ButtonIconType type;
        private final int size;

        ButtonActionIcon(ButtonIconType type, int size) {
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
                    // Icon xóa mới: vòng tròn có dấu X, nhìn gọn và đồng bộ hơn icon thùng rác cũ.
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
                    // Icon danh sách giống nút "Hiển thị tất cả" bên hóa đơn
                    for (int i = 0; i < 3; i++) {
                        int yy = y + 4 + i * 5;
                        g2.fillOval(x + 2, yy - 1, 2, 2);
                        g2.drawLine(x + 6, yy, x + s - 2, yy);
                    }
                }
                case PRINT -> {
                   
                    g2.drawRect(x + 4, y + 2, s - 8, 5);
                    g2.drawRoundRect(x + 2, y + 6, s - 4, 7, 3, 3);
                    g2.drawRect(x + 4, y + 11, s - 8, s - 12);
                    g2.drawLine(x + 6, y + 14, x + s - 6, y + 14);
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
                case PAYMENT -> {
                    g2.drawRoundRect(x + 2, y + 4, s - 4, s - 8, 3, 3);
                    g2.drawLine(x + 3, y + 8, x + s - 3, y + 8);
                    g2.drawLine(x + 5, y + s - 5, x + 10, y + s - 5);
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

    private class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = Color.BLACK;
            trackColor = Color.WHITE;
        }

        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(18, 48);
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
            g2.setColor(Color.BLACK);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
                    thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);
            g2.setColor(new Color(80, 80, 80));
            g2.drawRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
                    thumbBounds.width - 5, thumbBounds.height - 5, 10, 10);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.WHITE);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.setColor(Color.BLACK);
            g2.drawRect(trackBounds.x, trackBounds.y, trackBounds.width - 1, trackBounds.height - 1);
            g2.dispose();
        }
    }

    private class HelpButton extends JButton {
        HelpButton() {
            setText("?");
            setFont(new Font("Segoe UI", Font.BOLD, 22));
            setForeground(CHAMPAGNE_DARK);
            setBackground(Color.WHITE);
            setFocusPainted(false);
            setBorder(BorderFactory.createLineBorder(new Color(216, 170, 69, 130), 1));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setToolTipText("Tài khoản ngoại lệ");
        }
    }

    private void hienThiHuongDanTheoVaiTro() {
        AppDialog.showInfo(this, "Tài khoản ngoại lệ", taoNoiDungTaiKhoanNgoaiLe());
    }

    private String taoNoiDungTaiKhoanNgoaiLe() {
        return "TÀI KHOẢN NGOẠI LỆ THEO VAI TRÒ\n\n" +
                "Quản lý / Admin: admin / 12345\n" +
                "Nhân viên bán hàng: banhang / 12345\n" +
                "Nhân viên kho: kho / 12345\n" +
                "Kế toán: ketoan / 12345\n\n" +
                "Lưu ý: tên tài khoản ngoại lệ phải nhập đúng chữ thường. Ví dụ: kho đúng, KHo sai.";
    }

    private String taoNoiDungHuongDan() {
        if (laAdmin()) {
            return "HƯỚNG DẪN DÀNH CHO ADMIN\n\n" +
                    "1. Tổng quan hệ thống: xem nhanh các chức năng chính và mở nhanh từng màn hình.\n" +
                    "2. Quản lý nhân viên: thêm, sửa, xóa, tìm kiếm nhân viên; dùng để quản lý hồ sơ, ca làm và thông tin liên hệ.\n" +
                    "3. Quản lý khách hàng: thêm khách mới, cập nhật thông tin, tra cứu khách hàng và theo dõi điểm tích lũy.\n" +
                    "4. Quản lý hóa đơn: tạo hóa đơn, thêm sản phẩm vào hóa đơn, xác nhận thanh toán, in hóa đơn, trả hàng hoặc hủy/xóa theo đúng trạng thái.\n" +
                    "5. Quản lý sản phẩm: thêm/sửa/xóa sản phẩm, nhập hàng, theo dõi tồn kho, giá bán, kích cỡ và nhà cung cấp.\n" +
                    "6. Quản lý doanh thu: lọc doanh thu theo ngày/khoảng ngày, xem chi tiết doanh thu từng ngày và xuất báo cáo.\n" +
                    "7. Quản lý nhà cung cấp: thêm/sửa/xóa nhà cung cấp, xem thông tin liên hệ và chi tiết nhập hàng.";
        }
        if (laNhanVienKho()) {
            return "HƯỚNG DẪN DÀNH CHO NHÂN VIÊN KHO\n\n" +
                    "1. Quản lý sản phẩm: kiểm tra danh sách sản phẩm, tồn kho, kích cỡ, giá nhập/giá bán và trạng thái hàng.\n" +
                    "2. Thêm sản phẩm: nhập đầy đủ tên sản phẩm, đơn vị, giá, tồn kho, loại, thương hiệu, xuất xứ, mã nhà cung cấp rồi bấm Thêm.\n" +
                    "3. Sửa sản phẩm: chọn dòng sản phẩm trên bảng, chỉnh thông tin ở form nhập rồi bấm Sửa.\n" +
                    "4. Xóa sản phẩm: chọn sản phẩm cần xóa, kiểm tra kỹ dữ liệu rồi bấm Xóa.\n" +
                    "5. Nhập hàng: dùng nút Nhập hàng để bổ sung tồn kho cho sản phẩm.\n" +
                    "6. Quản lý nhà cung cấp: thêm/sửa/xóa thông tin công ty, số điện thoại, email, địa chỉ, người liên hệ và mô tả nguồn hàng.";
        }
        if (laKeToan()) {
            return "HƯỚNG DẪN DÀNH CHO KẾ TOÁN\n\n" +
                    "1. Xem hóa đơn: mở màn hình Xem hóa đơn để tra cứu danh sách hóa đơn và xem chi tiết sản phẩm trong từng hóa đơn.\n" +
                    "2. Quyền hóa đơn: kế toán chỉ được xem, không được thêm, sửa, xóa, hủy, trả hàng hoặc xác nhận thanh toán hóa đơn.\n" +
                    "3. Quản lý doanh thu: khi vào hệ thống có thể mở doanh thu hôm nay, xem số hóa đơn, tổng doanh thu và doanh thu thực.\n" +
                    "4. Xuất doanh thu: tại popup doanh thu hôm nay hoặc màn hình doanh thu, bấm Xuất doanh thu để lưu/in báo cáo phục vụ đối soát.";
        }
        return "HƯỚNG DẪN DÀNH CHO NHÂN VIÊN BÁN HÀNG\n\n" +
                "1. Quản lý khách hàng: thêm khách mới, tra cứu khách cũ và cập nhật thông tin khi bán hàng.\n" +
                "2. Quản lý hóa đơn: tạo hóa đơn, chọn khách hàng, thêm sản phẩm, xác nhận thanh toán và in hóa đơn đã thanh toán.\n" +
                "3. Xem sản phẩm: chỉ xem danh sách sản phẩm, giá bán, kích cỡ và tồn kho để tư vấn cho khách.\n" +
                "4. Nhân viên bán hàng không được vào quản lý doanh thu và không được thêm/sửa/xóa sản phẩm.";
    }

    private class DoanhThuPanel extends JPanel {
        private JTextField txtTuNgay;
        private JTextField txtDenNgay;
        private JLabel lblKetQua;
        private JLabel lblSoKetQua;
        private JTable table;
        private DefaultTableModel model;
        private java.time.LocalDate ngayLocTu = null;
        private java.time.LocalDate ngayLocDen = null;
        private double tongDoanhThuBoLoc = 0;
        private double doanhThuThucBoLoc = 0;
        private int soHoaDonBoLoc = 0;
        private int soLuongSanPhamBoLoc = 0;

        private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private final DateTimeFormatter inputDateFmt = DateTimeFormatter.ofPattern("d/M/yyyy");

        DoanhThuPanel() {
            setLayout(new BorderLayout(12, 12));
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(10, 10, 10, 10));
            if (laAdmin() || laKeToan()) createAdminUI(); else createStaffUI();
        }

        private JLabel createDarkLabel(String text) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setForeground(Color.BLACK);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        }

        private void styleTextField(JTextField txt) {
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txt.setHorizontalAlignment(SwingConstants.CENTER);
            txt.setBackground(Color.WHITE);
            txt.setForeground(Color.BLACK);
            txt.setCaretColor(Color.BLACK);
            txt.setOpaque(true);
            txt.setPreferredSize(new Dimension(138, 38));
            txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK, 1),
                    new EmptyBorder(6, 10, 6, 10)
            ));
        }

        private JButton createGoldButton(String text) {
            JButton btn = new JButton(text);
            btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setBackground(GOLD_LIGHT);
            btn.setForeground(Color.BLACK);
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
            btn.setMargin(new Insets(8, 14, 8, 14));
            btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(150, 42));
            return btn;
        }

        private void createAdminUI() {
            JPanel top = new JPanel(new BorderLayout(12, 12));
            top.setOpaque(false);

            JPanel filter = new JPanel(new GridBagLayout());
            filter.setOpaque(true);
            filter.setBackground(Color.WHITE);
            filter.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK, 1),
                    new EmptyBorder(12, 14, 12, 14)
            ));

            txtTuNgay = new JTextField(10);
            txtDenNgay = new JTextField(10);
            styleTextField(txtTuNgay);
            styleTextField(txtDenNgay);

            JButton btnLocKhoang = createGoldButton("Lọc khoảng");
            JButton btnTatCa = createGoldButton("Hiển thị tất cả");
            JButton btnXuat = createGoldButton("Xuất doanh thu");
            JButton btnBieuDo = createGoldButton("Biểu đồ");

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.insets = new Insets(4, 8, 4, 8);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.NONE;

            gbc.gridx = 0;
            filter.add(createDarkLabel("Từ ngày:"), gbc);

            gbc.gridx = 1;
            filter.add(txtTuNgay, gbc);

            gbc.gridx = 2;
            filter.add(createDarkLabel("Đến ngày:"), gbc);

            gbc.gridx = 3;
            filter.add(txtDenNgay, gbc);

            gbc.gridx = 4;
            filter.add(btnLocKhoang, gbc);

            gbc.gridx = 5;
            filter.add(btnTatCa, gbc);

            gbc.gridx = 6;
            filter.add(btnXuat, gbc);

            gbc.gridx = 7;
            filter.add(btnBieuDo, gbc);

            JLabel hint = new JLabel("Định dạng ngày: dd/MM/yyyy. Ví dụ: 22/5/2025 đến 22/5/2026");
            hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            hint.setForeground(new Color(60, 60, 60));
            hint.setBorder(new EmptyBorder(4, 10, 0, 0));

            JPanel filterWrap = new JPanel(new BorderLayout(0, 4));
            filterWrap.setOpaque(false);
            filterWrap.add(filter, BorderLayout.CENTER);
            filterWrap.add(hint, BorderLayout.SOUTH);

            lblKetQua = new JLabel("Tổng doanh thu tất cả các ngày: 0 VNĐ");
            lblKetQua.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblKetQua.setForeground(Color.BLACK);
            lblKetQua.setBorder(new EmptyBorder(16, 4, 14, 4));

            top.add(filterWrap, BorderLayout.NORTH);
            top.add(lblKetQua, BorderLayout.SOUTH);

            model = new DefaultTableModel(new String[]{
                    "Ngày", "Số hóa đơn", "Số lượng SP",
                    "Tổng doanh thu", "Doanh thu thực"
            }, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };

            table = taoBangDoanhThu(model);
            JScrollPane scroll = new JScrollPane(table);
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            styleScrollBar(scroll);

            lblSoKetQua = createResultCountLabel();
            JPanel tableWrap = new JPanel(new BorderLayout(0, 6));
            tableWrap.setOpaque(true);
            tableWrap.setBackground(Color.WHITE);
            tableWrap.add(lblSoKetQua, BorderLayout.NORTH);
            tableWrap.add(scroll, BorderLayout.CENTER);

            add(top, BorderLayout.NORTH);
            add(tableWrap, BorderLayout.CENTER);

            btnLocKhoang.addActionListener(e -> locTheoKhoangNgay());
            btnTatCa.addActionListener(e -> {
                txtTuNgay.setText("");
                txtDenNgay.setText("");
                ngayLocTu = null;
                ngayLocDen = null;
                loadDoanhThuRange(null, null);
            });
            btnXuat.addActionListener(e -> xuatDoanhThuBang());
            btnBieuDo.addActionListener(e -> hienBieuDoDoanhThu());

            table.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() >= 1 && table.getSelectedRow() >= 0) {
                        int r = table.convertRowIndexToModel(table.getSelectedRow());
                        String ngay = model.getValueAt(r, 0).toString();
                        hienHopDoanhThu(ngay, false);
                    }
                }
            });

            loadDoanhThuRange(null, null);
            if (laKeToan()) {
                SwingUtilities.invokeLater(() -> hienHopDoanhThu(LocalDateTime.now().format(dateFmt), false));
            }
        }

        private void createStaffUI() {
            JPanel root = new JPanel(new BorderLayout(12, 12));
            root.setOpaque(true);
            root.setBackground(Color.WHITE);
            root.setBorder(new EmptyBorder(30, 30, 30, 30));
            add(root, BorderLayout.CENTER);

            JButton btnMo = createGoldButton("Xem doanh thu hôm nay");
            btnMo.setText("Nhân viên bán hàng chỉ được xem doanh thu ngày hôm nay");
            btnMo.setPreferredSize(new Dimension(430, 46));
            JLabel note = new JLabel("Nhân viên bán hàng chỉ được xem doanh thu ngày hôm nay.", SwingConstants.CENTER);
            note.setFont(new Font("Segoe UI", Font.BOLD, 20));
            note.setForeground(Color.BLACK);
            root.add(note, BorderLayout.CENTER);
            root.add(btnMo, BorderLayout.SOUTH);

            btnMo.addActionListener(e -> hienHopDoanhThu(LocalDateTime.now().format(dateFmt), true));
        }


        private JLabel createResultCountLabel() {
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

        private void capNhatLabelKetQuaDoanhThu() {
            if (lblSoKetQua != null) {
                lblSoKetQua.setText("Số kết quả tìm được: " + model.getRowCount()
                        + " ngày | " + soHoaDonBoLoc + " hóa đơn | "
                        + soLuongSanPhamBoLoc + " sản phẩm");
            }
        }

        private void hienBieuDoDoanhThu() {
            JDialog dialog = new JDialog(MainFrame.this, "Biểu đồ doanh thu", true);
            dialog.setSize(980, 660);
            dialog.setLocationRelativeTo(MainFrame.this);

            JPanel root = new JPanel(new BorderLayout(12, 12));
            root.setBackground(Color.WHITE);
            root.setBorder(new EmptyBorder(18, 20, 18, 20));

            JLabel title = new JLabel("BIỂU ĐỒ DOANH THU", SwingConstants.CENTER);
            title.setFont(new Font("Segoe UI", Font.BOLD, 24));
            title.setForeground(HEADING_NAVY);

            JComboBox<String> cboKieu = new JComboBox<>(new String[]{"Theo tháng", "Theo năm"});
            cboKieu.setFont(new Font("Segoe UI", Font.BOLD, 13));
            cboKieu.setPreferredSize(new Dimension(160, 38));
            cboKieu.setBackground(Color.WHITE);
            cboKieu.setForeground(HEADING_NAVY);

            JPanel filter = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            filter.setOpaque(false);
            filter.add(createDarkLabel("Chọn kiểu xem:"));
            filter.add(cboKieu);

            JPanel top = new JPanel(new BorderLayout(0, 10));
            top.setOpaque(false);
            top.add(title, BorderLayout.NORTH);
            top.add(filter, BorderLayout.CENTER);

            RevenueChartPanel chart = new RevenueChartPanel();
            chart.setData(loadChartDataByMode("Theo tháng"));

            JButton btnDong = createGoldButton("Đóng");
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            actions.setOpaque(false);
            actions.add(btnDong);

            cboKieu.addActionListener(e -> chart.setData(loadChartDataByMode(String.valueOf(cboKieu.getSelectedItem()))));
            btnDong.addActionListener(e -> dialog.dispose());

            root.add(top, BorderLayout.NORTH);
            root.add(chart, BorderLayout.CENTER);
            root.add(actions, BorderLayout.SOUTH);
            dialog.setContentPane(root);
            dialog.setVisible(true);
        }

        private java.util.List<ChartPoint> loadChartDataByMode(String mode) {
            java.util.List<ChartPoint> data = new java.util.ArrayList<>();
            String selectGroup;
            String groupBy;
            String orderBy;
            if ("Theo tháng".equals(mode)) {
                selectGroup = "RIGHT('0' + CAST(MONTH(ngayBaoCao) AS VARCHAR(2)), 2) + '/' + CAST(YEAR(ngayBaoCao) AS VARCHAR(4)) AS nhom";
                groupBy = "YEAR(ngayBaoCao), MONTH(ngayBaoCao)";
                orderBy = "YEAR(ngayBaoCao), MONTH(ngayBaoCao)";
            } else if ("Theo năm".equals(mode)) {
                selectGroup = "CAST(YEAR(ngayBaoCao) AS VARCHAR(4)) AS nhom";
                groupBy = "YEAR(ngayBaoCao)";
                orderBy = "YEAR(ngayBaoCao)";
            } else {
                selectGroup = "CONVERT(VARCHAR(10), ngayBaoCao, 103) AS nhom";
                groupBy = "ngayBaoCao";
                orderBy = "ngayBaoCao";
            }

            String where = " WHERE trangThai IN (N'Đã thanh toán', N'Trả một phần hàng') ";
            if (ngayLocTu != null) where += " AND ngayBaoCao >= ? ";
            if (ngayLocDen != null) where += " AND ngayBaoCao <= ? ";

            String sql = "WITH traHD AS (\n" +
                    "    SELECT maHoaDon, SUM(tienHoanTra) AS tienHoanTra\n" +
                    "    FROM PhieuTraHang\n" +
                    "    GROUP BY maHoaDon\n" +
                    "), base AS (\n" +
                    "    SELECT hd.maHoaDon, hd.trangThai,\n" +
                    "           CAST(COALESCE(hd.ngayThanhToan, hd.ngayLap) AS DATE) AS ngayBaoCao,\n" +
                    "           CASE WHEN hd.thanhToan - ISNULL(traHD.tienHoanTra, 0) < 0 THEN 0 ELSE hd.thanhToan - ISNULL(traHD.tienHoanTra, 0) END AS doanhThuThuc\n" +
                    "    FROM HoaDon hd\n" +
                    "    LEFT JOIN traHD ON traHD.maHoaDon = hd.maHoaDon\n" +
                    ")\n" +
                    "SELECT " + selectGroup + ",\n" +
                    "       COALESCE(SUM(doanhThuThuc), 0) AS doanhThu\n" +
                    "FROM base\n" +
                    where + "\n" +
                    "GROUP BY " + groupBy + "\n" +
                    "ORDER BY " + orderBy;

            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                int index = 1;
                if (ngayLocTu != null) ps.setDate(index++, java.sql.Date.valueOf(ngayLocTu));
                if (ngayLocDen != null) ps.setDate(index++, java.sql.Date.valueOf(ngayLocDen));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data.add(new ChartPoint(rs.getString("nhom"), rs.getDouble("doanhThu")));
                    }
                }
            } catch (Exception ex) {
                AppDialog.showError(this, "Lỗi", "Không đọc được dữ liệu biểu đồ: " + ex.getMessage());
            }
            return data;
        }

        private class RevenueChartPanel extends JPanel {
            private java.util.List<ChartPoint> data = new java.util.ArrayList<>();

            RevenueChartPanel() {
                setOpaque(true);
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(216, 224, 236), 1),
                        new EmptyBorder(18, 18, 18, 18)
                ));
            }

            void setData(java.util.List<ChartPoint> data) {
                this.data = data == null ? new java.util.ArrayList<>() : data;
                repaint();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int left = 70;
                int right = 30;
                int top = 35;
                int bottom = 70;
                int chartW = Math.max(1, w - left - right);
                int chartH = Math.max(1, h - top - bottom);

                GradientPaint bg = new GradientPaint(left, top, new Color(248, 250, 252), left, top + chartH, new Color(239, 246, 255));
                g2.setPaint(bg);
                g2.fillRoundRect(left, top, chartW, chartH, 18, 18);
                g2.setColor(new Color(203, 213, 225));
                g2.drawRoundRect(left, top, chartW, chartH, 18, 18);
                g2.setColor(new Color(226, 232, 240));
                for (int gy = 1; gy <= 4; gy++) {
                    int yy = top + gy * chartH / 5;
                    g2.drawLine(left + 10, yy, left + chartW - 10, yy);
                }

                if (data.isEmpty()) {
                    g2.setColor(TEXT_SLATE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    String msg = "Chưa có dữ liệu doanh thu để vẽ biểu đồ";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
                    g2.dispose();
                    return;
                }

                double max = 0;
                for (ChartPoint p : data) max = Math.max(max, p.value);
                if (max <= 0) max = 1;

                int n = data.size();
                int gap = 12;
                int barW = Math.max(28, Math.min(82, (chartW - gap * (n + 1)) / Math.max(1, n)));
                int x = left + gap;
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                for (ChartPoint p : data) {
                    int barH = (int) Math.round((p.value / max) * (chartH - 48));
                    int y = top + chartH - barH - 28;
                    GradientPaint gp = new GradientPaint(x, y, new Color(244, 210, 122), x, y + barH, new Color(18, 59, 140));
                    g2.setPaint(gp);
                    g2.fillRoundRect(x, y, barW, barH, 10, 10);
                    g2.setColor(new Color(11, 23, 54));
                    g2.drawRoundRect(x, y, barW, barH, 10, 10);

                    String val = moneyFormat.format(p.value);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.setColor(HEADING_NAVY);
                    g2.drawString(val, x + (barW - fm.stringWidth(val)) / 2, y - 6);

                    String label = p.label;
                    if (label.length() > 10) label = label.substring(0, 10);
                    fm = g2.getFontMetrics();
                    g2.setColor(TEXT_SLATE);
                    g2.drawString(label, x + (barW - fm.stringWidth(label)) / 2, top + chartH - 8);
                    x += barW + gap;
                }
                g2.dispose();
            }
        }

        private static class ChartPoint {
            final String label;
            final double value;
            ChartPoint(String label, double value) {
                this.label = label == null ? "" : label;
                this.value = value;
            }
        }

        private JTable taoBangDoanhThu(DefaultTableModel m) {
            JTable t = new JTable(m);
            t.setRowHeight(34);
            t.setBackground(Color.WHITE);
            t.setForeground(Color.BLACK);
            t.setGridColor(Color.BLACK);
            t.setSelectionBackground(new Color(216, 224, 236));
            t.setSelectionForeground(Color.BLACK);
            t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            t.setFillsViewportHeight(true);
            t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            t.setShowGrid(true);
            t.setShowHorizontalLines(true);
            t.setShowVerticalLines(true);

            t.getTableHeader().setPreferredSize(new Dimension(0, 40));
            t.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable tb, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    super.getTableCellRendererComponent(tb, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBackground(new Color(226, 232, 240));
                    setForeground(Color.BLACK);
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    return this;
                }
            });

            t.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable tb, Object value, boolean selected, boolean focus, int row, int column) {
                    super.getTableCellRendererComponent(tb, value, selected, focus, row, column);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    if (selected) {
                        setBackground(new Color(216, 224, 236));
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
            int[] widths = {170, 180, 160, 260, 260};
            for (int i = 0; i < widths.length && i < t.getColumnModel().getColumnCount(); i++) {
                t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
                t.getColumnModel().getColumn(i).setMinWidth(Math.min(widths[i], 110));
            }
            return t;
        }

        private void locTheoKhoangNgay() {
            String tuText = txtTuNgay.getText().trim();
            String denText = txtDenNgay.getText().trim();

            java.time.LocalDate tuNgay = null;
            java.time.LocalDate denNgay = null;

            try {
                if (!tuText.isBlank()) {
                    tuNgay = java.time.LocalDate.parse(tuText, inputDateFmt);
                }
                if (!denText.isBlank()) {
                    denNgay = java.time.LocalDate.parse(denText, inputDateFmt);
                }

                if ((tuNgay == null && denNgay == null)
                        || (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay))) {
                    throw new IllegalArgumentException();
                }

            } catch (Exception ex) {
                AppDialog.showError(
                        this,
                        "Sai định dạng",
                        "Ngày lọc chưa đúng định dạng dd/MM/yyyy hoặc lọc ngày chưa hợp lý.\nVí dụ: 22/05/2025."
                );
                return;
            }

            ngayLocTu = tuNgay;
            ngayLocDen = denNgay;
            loadDoanhThuRange(tuNgay, denNgay);
        }

        private java.time.LocalDate docNgayTuText(String text, String tenO, boolean batBuoc) {
            if (text == null || text.isBlank()) {
                return null;
            }
            try {
                return java.time.LocalDate.parse(text.trim(), inputDateFmt);
            } catch (Exception e) {
                return null;
            }
        }

        private String taoMoTaKhoangNgay(java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
            if (tuNgay == null && denNgay == null) return "tất cả các ngày";
            if (tuNgay != null && denNgay != null) return "từ " + tuNgay.format(dateFmt) + " đến " + denNgay.format(dateFmt);
            if (tuNgay != null) return "từ " + tuNgay.format(dateFmt) + " trở đi";
            return "đến " + denNgay.format(dateFmt);
        }

        private void loadDoanhThuRange(java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
            model.setRowCount(0);
            tongDoanhThuBoLoc = 0;
            doanhThuThucBoLoc = 0;
            soHoaDonBoLoc = 0;
            soLuongSanPhamBoLoc = 0;

            
            String where = " WHERE hd.trangThai IN (N'Đã thanh toán', N'Trả một phần hàng') ";
            if (tuNgay != null) where += " AND CAST(COALESCE(hd.ngayThanhToan, hd.ngayLap) AS DATE) >= ? ";
            if (denNgay != null) where += " AND CAST(COALESCE(hd.ngayThanhToan, hd.ngayLap) AS DATE) <= ? ";

            String sql = """
                    WITH tra AS (
                        SELECT maHoaDon, maSanPham, ISNULL(kichCo, 'M') AS kichCo,
                               SUM(soLuongTra) AS soLuongTra,
                               SUM(tienGocTra) AS tienGocTra,
                               SUM(tienHoanTra) AS tienHoanTra
                        FROM PhieuTraHang
                        GROUP BY maHoaDon, maSanPham, ISNULL(kichCo, 'M')
                    ),
                    chi AS (
                        SELECT ct.maHoaDon,
                               SUM(ct.soLuong - ISNULL(tra.soLuongTra, 0)) AS soLuongConLai,
                               SUM((ct.soLuong * ct.donGia) - ISNULL(tra.tienGocTra, 0)) AS tongTienSauTra
                        FROM ChiTietHoaDon ct
                        LEFT JOIN tra
                            ON tra.maHoaDon = ct.maHoaDon
                           AND tra.maSanPham = ct.maSanPham
                           AND tra.kichCo = ISNULL(ct.kichCo, 'M')
                        GROUP BY ct.maHoaDon
                    ),
                    traHD AS (
                        SELECT maHoaDon, SUM(tienHoanTra) AS tienHoanTra
                        FROM PhieuTraHang
                        GROUP BY maHoaDon
                    )
                    SELECT
                        CONVERT(VARCHAR(10), CAST(COALESCE(hd.ngayThanhToan, hd.ngayLap) AS DATE), 103) AS ngay,
                        COUNT(DISTINCT hd.maHoaDon) AS soHoaDon,
                        COALESCE(SUM(chi.soLuongConLai), 0) AS soLuong,
                        COALESCE(SUM(chi.tongTienSauTra), 0) AS tongDoanhThu,
                        COALESCE(SUM(CASE WHEN hd.thanhToan - ISNULL(traHD.tienHoanTra, 0) < 0 THEN 0 ELSE hd.thanhToan - ISNULL(traHD.tienHoanTra, 0) END), 0) AS doanhThuThuc
                    FROM HoaDon hd
                    LEFT JOIN chi ON hd.maHoaDon = chi.maHoaDon
                    LEFT JOIN traHD ON hd.maHoaDon = traHD.maHoaDon
                    """ + where + """
                    GROUP BY CAST(COALESCE(hd.ngayThanhToan, hd.ngayLap) AS DATE)
                    ORDER BY CAST(COALESCE(hd.ngayThanhToan, hd.ngayLap) AS DATE) ASC
                    """;

            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                int index = 1;
                if (tuNgay != null) ps.setDate(index++, java.sql.Date.valueOf(tuNgay));
                if (denNgay != null) ps.setDate(index++, java.sql.Date.valueOf(denNgay));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        double tongNgay = rs.getDouble("tongDoanhThu");
                        double thuc = rs.getDouble("doanhThuThuc");
                        int soHoaDon = rs.getInt("soHoaDon");
                        int soLuong = rs.getInt("soLuong");

                        tongDoanhThuBoLoc += tongNgay;
                        doanhThuThucBoLoc += thuc;
                        soHoaDonBoLoc += soHoaDon;
                        soLuongSanPhamBoLoc += soLuong;

                        model.addRow(new Object[]{
                                rs.getString("ngay"),
                                soHoaDon,
                                soLuong,
                                moneyFormat.format(tongNgay) + " VNĐ",
                                moneyFormat.format(thuc) + " VNĐ"
                        });
                    }
                }

                lblKetQua.setText("Tổng doanh thu " + taoMoTaKhoangNgay(tuNgay, denNgay) + ": " + moneyFormat.format(doanhThuThucBoLoc) + " VNĐ");
                capNhatLabelKetQuaDoanhThu();
            } catch (Exception e) {
                AppDialog.showError(this, "Lỗi", "Lỗi đọc doanh thu: " + e.getMessage());
            }
        }

        private DoanhThuNgay tinhDoanhThuNgay(String ngayText, boolean chiNhanVienHienTai) {
            java.time.LocalDate ngay = java.time.LocalDate.parse(ngayText, dateFmt);
            String sql = """
                    WITH tra AS (
                        SELECT maHoaDon, maSanPham, ISNULL(kichCo, 'M') AS kichCo,
                               SUM(soLuongTra) AS soLuongTra,
                               SUM(tienGocTra) AS tienGocTra,
                               SUM(tienHoanTra) AS tienHoanTra
                        FROM PhieuTraHang
                        GROUP BY maHoaDon, maSanPham, ISNULL(kichCo, 'M')
                    ),
                    chi AS (
                        SELECT ct.maHoaDon,
                               SUM((ct.soLuong * ct.donGia) - ISNULL(tra.tienGocTra, 0)) AS tongTienSauTra
                        FROM ChiTietHoaDon ct
                        LEFT JOIN tra
                            ON tra.maHoaDon = ct.maHoaDon
                           AND tra.maSanPham = ct.maSanPham
                           AND tra.kichCo = ISNULL(ct.kichCo, 'M')
                        GROUP BY ct.maHoaDon
                    ),
                    traHD AS (
                        SELECT maHoaDon, SUM(tienHoanTra) AS tienHoanTra
                        FROM PhieuTraHang
                        GROUP BY maHoaDon
                    )
                    SELECT
                        COUNT(DISTINCT hd.maHoaDon) AS soHoaDon,
                        COALESCE(SUM(chi.tongTienSauTra), 0) AS tongDoanhThu,
                        0 AS tongGiamGia,
                        COALESCE(SUM(CASE WHEN hd.thanhToan - ISNULL(traHD.tienHoanTra, 0) < 0 THEN 0 ELSE hd.thanhToan - ISNULL(traHD.tienHoanTra, 0) END), 0) AS doanhThuThuc
                    FROM HoaDon hd
                    LEFT JOIN chi ON hd.maHoaDon = chi.maHoaDon
                    LEFT JOIN traHD ON hd.maHoaDon = traHD.maHoaDon
                    WHERE hd.trangThai IN (N'Đã thanh toán', N'Trả một phần hàng')
                      AND CAST(COALESCE(hd.ngayThanhToan, hd.ngayLap) AS DATE) = ?
                    """;
            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, java.sql.Date.valueOf(ngay));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new DoanhThuNgay(
                                ngayText,
                                rs.getDouble("tongDoanhThu"),
                                rs.getDouble("tongGiamGia"),
                                rs.getDouble("doanhThuThuc"),
                                rs.getInt("soHoaDon")
                        );
                    }
                }
            } catch (Exception e) {
                AppDialog.showError(this, "Lỗi", "Không đọc được chi tiết doanh thu: " + e.getMessage());
            }
            return new DoanhThuNgay(ngayText, 0, 0, 0, 0);
        }

        private void hienHopDoanhThu(String ngayText, boolean staff) {
            DoanhThuNgay d = tinhDoanhThuNgay(ngayText, staff);
            JDialog dialog = new JDialog(MainFrame.this, "Chi tiết doanh thu", true);
            dialog.setSize(650, 490);
            dialog.setLocationRelativeTo(MainFrame.this);

            JPanel root = new JPanel(new BorderLayout(12, 12));
            root.setBackground(Color.WHITE);
            root.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK, 1),
                    new EmptyBorder(20, 22, 20, 22)
            ));

            JLabel title = new JLabel("BILUXURY - CHI NHÁNH SƠN TÂY", SwingConstants.CENTER);
            title.setForeground(Color.BLACK);
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            root.add(title, BorderLayout.NORTH);

            JPanel info = new JPanel(new GridLayout(4, 2, 12, 12));
            info.setBackground(Color.WHITE);
            info.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK, 1),
                    new EmptyBorder(18, 18, 18, 18)
            ));
            addRevenueInfo(info, "Tên cửa hàng:", "Biluxury - Chi nhánh Sơn Tây");
            addRevenueInfo(info, "Ngày:", d.ngay);
            addRevenueInfo(info, "Số hóa đơn:", String.valueOf(d.soHoaDon));
            addRevenueInfo(info, "Tổng doanh thu:", moneyFormat.format(d.tongDoanhThu) + " VNĐ");
            root.add(info, BorderLayout.CENTER);

            JLabel real = new JLabel("Doanh thu thực: " + moneyFormat.format(d.doanhThuThuc) + " VNĐ", SwingConstants.CENTER);
            real.setFont(new Font("Segoe UI", Font.BOLD, 24));
            real.setForeground(Color.BLACK);

            JButton btnXuat = createGoldButton("Xuất doanh thu");
            JButton btnBieuDo = createGoldButton("Biểu đồ");
            JButton btnDong = createGoldButton("Đóng");
            JPanel bottom = new JPanel(new BorderLayout(0, 12));
            bottom.setOpaque(true);
            bottom.setBackground(Color.WHITE);
            bottom.add(real, BorderLayout.NORTH);
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
            actions.setOpaque(true);
            actions.setBackground(Color.WHITE);
            actions.add(btnXuat);
            actions.add(btnDong);
            bottom.add(actions, BorderLayout.SOUTH);
            root.add(bottom, BorderLayout.SOUTH);

            btnXuat.addActionListener(e -> xuatDoanhThuNgay(d));
            btnDong.addActionListener(e -> dialog.dispose());
            dialog.setContentPane(root);
            dialog.setVisible(true);
        }

        private void addRevenueInfo(JPanel p, String k, String v) {
            JLabel key = createDarkLabel(k);
            key.setHorizontalAlignment(SwingConstants.LEFT);
            JLabel val = createDarkLabel(v);
            val.setHorizontalAlignment(SwingConstants.RIGHT);
            p.add(key);
            p.add(val);
        }

        private void xuatDoanhThuBang() {
            StringBuilder sb = new StringBuilder("BÁO CÁO DOANH THU THEO KHOẢNG THỜI GIAN\n");
            if (ngayLocTu != null || ngayLocDen != null) {
                sb.append("Từ ngày: ").append(ngayLocTu == null ? "" : ngayLocTu.format(dateFmt)).append("\n");
                sb.append("Đến ngày: ").append(ngayLocDen == null ? "" : ngayLocDen.format(dateFmt)).append("\n");
            } else {
                sb.append("Khoảng ngày: Tất cả\n");
            }
            sb.append("Ngày: Tổng hợp\n");
            sb.append("Số hóa đơn: ").append(soHoaDonBoLoc).append("\n");
            sb.append("Tổng số lượng SP: ").append(soLuongSanPhamBoLoc).append("\n");
            sb.append("Tổng doanh thu: ").append(moneyFormat.format(tongDoanhThuBoLoc)).append(" VNĐ\n");
            sb.append("Doanh thu thực: ").append(moneyFormat.format(doanhThuThucBoLoc)).append(" VNĐ\n");
            sb.append("\n");
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    sb.append(model.getColumnName(c)).append(": ").append(model.getValueAt(r, c)).append("\t");
                }
                sb.append("\n");
            }
            String tenFile = "doanh_thu_tong_hop.pdf";
            luuFileDoanhThu(tenFile, sb.toString(), thuMucDoanhThuTheoBoLoc());
        }

        private void xuatDoanhThuNgay(DoanhThuNgay d) {
            String content = "BILUXURY - CHI NHÁNH SƠN TÂY\n"
                    + "Ngày: " + d.ngay + "\n"
                    + "Số hóa đơn: " + d.soHoaDon + "\n"
                    + "Tổng doanh thu: " + moneyFormat.format(d.tongDoanhThu) + " VNĐ\n"
                    + "Doanh thu thực: " + moneyFormat.format(d.doanhThuThuc) + " VNĐ\n";
            luuFileDoanhThu("doanh_thu_" + d.ngay.replace("/", "-") + ".pdf", content, d.ngay.replace("/", "-"));
        }

        private String thuMucDoanhThuTheoBoLoc() {
            if (ngayLocTu != null && ngayLocDen != null) {
                if (ngayLocTu.equals(ngayLocDen)) return ngayLocTu.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                return ngayLocTu.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        + "_den_" + ngayLocDen.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            }
            if (ngayLocTu != null) return "tu_" + ngayLocTu.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            if (ngayLocDen != null) return "den_" + ngayLocDen.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return "tonghop";
        }

        private void luuFileDoanhThu(String tenFileMacDinh, String content, String tenThuMuc) {
            try {
                File root = new File("doanhthu");
                File dateDir = new File(root, tenThuMuc == null || tenThuMuc.isBlank() ? "tonghop" : tenThuMuc);
                java.nio.file.Files.createDirectories(dateDir.toPath());

                String tenFile = tenFileMacDinh == null || tenFileMacDinh.isBlank()
                        ? "doanh_thu.pdf"
                        : tenFileMacDinh.trim().replaceAll("[\\/:*?\"<>|]", "_");
                if (!tenFile.toLowerCase().endsWith(".pdf")) tenFile += ".pdf";

                String duoiThoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss_SSS"));
                int dot = tenFile.toLowerCase().lastIndexOf(".pdf");
                if (dot >= 0) {
                    tenFile = tenFile.substring(0, dot) + "_" + duoiThoiGian + tenFile.substring(dot);
                } else {
                    tenFile = tenFile + "_" + duoiThoiGian + ".pdf";
                }

                File out = new File(dateDir, tenFile);
                PdfExporter.exportTextToPdf(out, content);
                showDoanhThuSuccess("Xuất doanh thu thành công");
            } catch (Exception ex) {
                AppDialog.showError(this, "Lỗi", "Không lưu được file: " + ex.getMessage());
            }
        }


        private void lamMoiDuLieu() {
            loadDoanhThuRange(ngayLocTu, ngayLocDen);
        }

        private void showDoanhThuSuccess(String message) {
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), message, Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setSize(450, 180);
            dialog.setResizable(false);

            JPanel root = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    LinearGradientPaint base = new LinearGradientPaint(
                            0, 0, w, h,
                            new float[]{0f, 0.52f, 1f},
                            new Color[]{
                                    new Color(250, 252, 255),
                                    new Color(236, 246, 255),
                                    new Color(198, 225, 252)
                            }
                    );
                    g2.setPaint(base);
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
            };
            root.setBorder(new EmptyBorder(22, 28, 22, 28));

            JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
            row.setOpaque(false);

            JLabel icon = new JLabel(new DoanhThuSuccessIcon(42));
            JLabel text = new JLabel(message);
            text.setFont(new Font("Segoe UI", Font.BOLD, 22));
            text.setForeground(new Color(11, 23, 54));

            row.add(icon);
            row.add(text);

            root.add(row, new GridBagConstraints());

            dialog.setContentPane(root);
            dialog.setLocationRelativeTo(this);

            Timer timer = new Timer(900, e -> {
                ((Timer) e.getSource()).stop();
                dialog.dispose();
            });
            timer.setRepeats(false);
            timer.start();

            dialog.setVisible(true);
        }

        private class DoanhThuSuccessIcon implements Icon {
            private final int size;

            DoanhThuSuccessIcon(int size) {
                this.size = size;
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(20, 140, 80));
                g2.fillOval(x, y, size, size);

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(4.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                g2.drawLine(x + size / 4, y + size / 2, x + size / 2 - 3, y + size * 3 / 4 - 4);
                g2.drawLine(x + size / 2 - 3, y + size * 3 / 4 - 4, x + size * 3 / 4 + 5, y + size / 3);

                g2.dispose();
            }
        }

        private class DoanhThuNgay {
            String ngay;
            double tongDoanhThu, tongGiamGia, doanhThuThuc;
            int soHoaDon;
            DoanhThuNgay(String ngay, double tongDoanhThu, double tongGiamGia, double doanhThuThuc, int soHoaDon) {
                this.ngay = ngay;
                this.tongDoanhThu = tongDoanhThu;
                this.tongGiamGia = tongGiamGia;
                this.doanhThuThuc = doanhThuThuc;
                this.soHoaDon = soHoaDon;
            }
        }
    }
}




