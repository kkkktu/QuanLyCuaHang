import GUI.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Thiết lập Look and Feel cho giống hệ điều hành hiện tại (Windows)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chạy GUI trong luồng an toàn của Swing
        SwingUtilities.invokeLater(() -> {
            GUI.LoginFrame loginFrame = new GUI.LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
