package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Đăng nhập hệ thống");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center screen
        setLayout(new BorderLayout());

        JPanel panelCenter = new JPanel(new GridBagLayout());
        panelCenter.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.BLUE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCenter.add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        txtUsername = new JTextField(15);
        panelCenter.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCenter.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        txtPassword = new JPasswordField(15);
        panelCenter.add(txtPassword, gbc);

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(50, 150, 250));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        
        JPanel panelBottom = new JPanel();
        panelBottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panelBottom.add(btnLogin);

        add(lblTitle, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);

        // Bắt sự kiện khi ấn phím Enter trong ô mật khẩu
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLogin.doClick();
            }
        });
        
        // Bắt sự kiện khi ấn phím Enter trong ô tài khoản
        txtUsername.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPassword.requestFocus();
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());

                if (username.equals("admin") && password.equals("12345")) {
                    dispose(); // Đóng LoginFrame
                    
                    // Mở MainFrame
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
