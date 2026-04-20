package GUI;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("Phần Mềm Quản Lý Cửa Hàng");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // --- Màn hình chính (Menu) ---
        JPanel menuPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JButton btnNhanVien = new JButton("Quản Lý Nhân Viên");
        btnNhanVien.setFont(new Font("Arial", Font.BOLD, 24));
        btnNhanVien.setBackground(new Color(52, 152, 219));

        JButton btnKhachHang = new JButton("Quản Lý Khách Hàng");
        btnKhachHang.setFont(new Font("Arial", Font.BOLD, 24));
        btnKhachHang.setBackground(new Color(46, 204, 113));

        menuPanel.add(btnNhanVien);
        menuPanel.add(btnKhachHang);

        // Header cho Menu
        JPanel menuWrapper = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("CHỌN CHỨC NĂNG QUẢN LÝ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 30));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        menuWrapper.add(lblTitle, BorderLayout.NORTH);
        menuWrapper.add(menuPanel, BorderLayout.CENTER);

        // --- Khởi tạo các màn hình con ---
        // Bọc Panel bằng 1 JPanel có chứa nút "Quay lại" ở trên cùng
        JPanel wrapNhanVien = createPanelWithBackBtn(new NhanVienPanel());
        JPanel wrapKhachHang = createPanelWithBackBtn(new KhachHangPanel());

        // --- Thêm vào CardLayout ---
        mainPanel.add(menuWrapper, "Menu");
        mainPanel.add(wrapNhanVien, "NhanVien");
        mainPanel.add(wrapKhachHang, "KhachHang");

        // --- Sự kiện chuyển đổi màn hình ---
        btnNhanVien.addActionListener(e -> cardLayout.show(mainPanel, "NhanVien"));
        btnKhachHang.addActionListener(e -> cardLayout.show(mainPanel, "KhachHang"));

        add(mainPanel);
    }

    private JPanel createPanelWithBackBtn(JPanel contentPanel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("← Quay lại Trang Chủ");
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setBackground(new Color(231, 76, 60));
        
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));
        
        pnlTop.add(btnBack);
        
        wrapper.add(pnlTop, BorderLayout.NORTH);
        wrapper.add(contentPanel, BorderLayout.CENTER);
        
        return wrapper;
    }
}
