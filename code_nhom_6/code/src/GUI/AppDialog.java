package GUI;

import database.DBConnection;
import model.HoaDon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.print.PrinterException;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public final class AppDialog {

    private static final Color BG = new Color(18, 17, 15);
    private static final Color CARD = new Color(33, 31, 29);
    private static final Color CARD_2 = new Color(42, 39, 35);
    private static final Color BORDER = new Color(112, 84, 52);
    private static final Color GOLD = new Color(192, 149, 94);
    private static final Color GOLD_LIGHT = new Color(223, 196, 162);
    private static final Color TEXT = new Color(245, 239, 230);
    private static final Color TEXT_MUTED = new Color(182, 172, 158);
    private static final Color SUCCESS = new Color(84, 160, 110);
    private static final Color WARNING = new Color(205, 157, 76);
    private static final Color ERROR = new Color(190, 78, 66);

    
    private static final int DETAIL_DIALOG_WIDTH = 980;
    private static final int DETAIL_CONTENT_WIDTH = 920;

    public static final int INVOICE_PRINT_CANCEL = -1;
    public static final int INVOICE_PRINT_BEFORE_RETURN = 0;
    public static final int INVOICE_PRINT_AFTER_RETURN = 1;

    private AppDialog() {
    }

    public static void showSuccess(Component parent, String title, String message) {
        showMessageDialog(parent, title, message, StatusType.SUCCESS, "OK", null, false);
    }

    public static void showInfo(Component parent, String title, String message) {
        showMessageDialog(parent, title, message, StatusType.INFO, "OK", null, false);
    }

    public static void showWarning(Component parent, String title, String message) {
        showMessageDialog(parent, title, message, StatusType.WARNING, "Đã hiểu", null, false);
    }

    public static void showError(Component parent, String title, String message) {
        showMessageDialog(parent, title, message, StatusType.ERROR, "Đóng", null, false);
    }

    public static boolean showConfirm(Component parent, String title, String message) {
        final boolean[] accepted = {false};
        showMessageDialog(parent, title, message, StatusType.CONFIRM, "Đồng ý", () -> accepted[0] = true, true);
        return accepted[0];
    }

    public static boolean showOkCancelConfirm(Component parent, String title, String message) {
        final boolean[] accepted = {false};
        showMessageDialog(parent, title, message, StatusType.CONFIRM, "OK", () -> accepted[0] = true, true);
        return accepted[0];
    }

    public static int showInvoicePrintChoice(Component parent) {
        final int[] result = {INVOICE_PRINT_CANCEL};

        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Chọn kiểu in hóa đơn", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(760, 340);
        dialog.setResizable(false);

        JPanel root = new NoticeBackgroundPanel();
        root.setLayout(new GridBagLayout());
        root.setBorder(new EmptyBorder(22, 30, 24, 30));

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setPreferredSize(new Dimension(690, 255));
        box.setMaximumSize(new Dimension(690, 255));

        JLabel icon = new JLabel("?", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18, 59, 140));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(new Color(244, 210, 122));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icon.setFont(new Font("Segoe UI", Font.BOLD, 30));
        icon.setForeground(Color.WHITE);
        icon.setOpaque(false);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        icon.setPreferredSize(new Dimension(54, 54));
        icon.setMaximumSize(new Dimension(54, 54));

        JLabel titleLabel = new JLabel("Hóa đơn này đã trả một phần hàng", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(11, 23, 54));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setMaximumSize(new Dimension(690, 34));

        JLabel msgLabel = new JLabel("Bạn muốn in hóa đơn theo thời điểm nào?", SwingConstants.CENTER);
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        msgLabel.setForeground(new Color(38, 53, 85));
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        msgLabel.setMaximumSize(new Dimension(690, 30));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(690, 48));

        JButton before = createNoticePrimaryButton("In hóa đơn trước trả hàng");
        JButton after = createNoticePrimaryButton("In hóa đơn sau trả hàng");
        JButton cancel = createNoticeSecondaryButton("Hủy");
        resizeChoiceButton(before, 230);
        resizeChoiceButton(after, 220);
        resizeChoiceButton(cancel, 110);

        before.addActionListener(e -> {
            result[0] = INVOICE_PRINT_BEFORE_RETURN;
            dialog.dispose();
        });
        after.addActionListener(e -> {
            result[0] = INVOICE_PRINT_AFTER_RETURN;
            dialog.dispose();
        });
        cancel.addActionListener(e -> dialog.dispose());

        buttons.add(before);
        buttons.add(after);
        buttons.add(cancel);

        box.add(Box.createVerticalGlue());
        box.add(icon);
        box.add(Box.createVerticalStrut(16));
        box.add(titleLabel);
        box.add(Box.createVerticalStrut(8));
        box.add(msgLabel);
        box.add(Box.createVerticalStrut(26));
        box.add(buttons);
        box.add(Box.createVerticalGlue());

        root.add(box, new GridBagConstraints());
        dialog.setContentPane(root);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return result[0];
    }

    private static void resizeChoiceButton(JButton button, int width) {
        Dimension d = new Dimension(width, 42);
        button.setPreferredSize(d);
        button.setMinimumSize(d);
        button.setMaximumSize(d);
    }


    private static void showMessageDialog(Component parent,
                                          String title,
                                          String message,
                                          StatusType type,
                                          String okText,
                                          Runnable confirmAction,
                                          boolean hasCancel) {

        
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        java.util.List<String> lines = tachDongThongBao(message, 58);
        int lineHeight = lines.size() <= 1 ? 34 : 27;
        int messageHeight = Math.max(84, Math.min(210, lines.size() * lineHeight + 22));
        int boxHeight = Math.max(hasCancel ? 245 : 225, 45 + 14 + messageHeight + 14 + 50 + 34);
        int dialogHeight = Math.min(520, boxHeight + 92);

        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(false);
        dialog.setSize(hasCancel ? 700 : 700, dialogHeight);
        dialog.setResizable(false);

        JPanel root = new NoticeBackgroundPanel();
        root.setLayout(new GridBagLayout());
        root.setBorder(new EmptyBorder(20, 30, 24, 30));

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setPreferredSize(new Dimension(620, boxHeight));
        box.setMaximumSize(new Dimension(620, boxHeight));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(getNoticeTitleColor(type));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setMaximumSize(new Dimension(620, 45));
        titleLabel.setPreferredSize(new Dimension(620, 45));

        JPanel messageBox = new JPanel();
        messageBox.setOpaque(false);
        messageBox.setLayout(new BoxLayout(messageBox, BoxLayout.Y_AXIS));
        messageBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageBox.setPreferredSize(new Dimension(620, messageHeight));
        messageBox.setMaximumSize(new Dimension(620, messageHeight));

        messageBox.add(Box.createVerticalGlue());
        for (String line : lines) {
            JLabel lineLabel = new JLabel(line, SwingConstants.CENTER);
            lineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            lineLabel.setForeground(new Color(11, 23, 54));
            lineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            lineLabel.setHorizontalAlignment(SwingConstants.CENTER);
            lineLabel.setPreferredSize(new Dimension(620, lineHeight));
            lineLabel.setMaximumSize(new Dimension(620, lineHeight));
            messageBox.add(lineLabel);
        }
        messageBox.add(Box.createVerticalGlue());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(620, 44));
        buttons.setPreferredSize(new Dimension(620, 44));

        if (hasCancel) {
            JButton cancel = createNoticeSecondaryButton("Hủy");
            cancel.addActionListener(e -> dialog.dispose());
            buttons.add(cancel);
        }

        JButton ok = createNoticePrimaryButton(okText == null || okText.isBlank() ? "Đóng" : okText);
        ok.addActionListener(e -> {
            if (confirmAction != null) {
                confirmAction.run();
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
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }



    private static java.util.List<String> tachDongThongBao(String message, int maxChars) {
        java.util.List<String> result = new java.util.ArrayList<>();
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

    private static Color getNoticeTitleColor(StatusType type) {
        return switch (type) {
            case SUCCESS -> new Color(22, 101, 52);
            case ERROR -> new Color(150, 40, 40);
            case WARNING, CONFIRM -> new Color(150, 40, 40);
            case INFO -> new Color(18, 59, 140);
        };
    }

    private static JButton createNoticePrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(36, 86, 205),
                        getWidth(), getHeight(), new Color(3, 24, 64)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(new Color(70, 135, 255, 170));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setUI(new BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(145, 42));
        btn.setMinimumSize(new Dimension(145, 42));
        btn.setMaximumSize(new Dimension(145, 42));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private static JButton createNoticeSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(248, 250, 252));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(11, 23, 54));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setUI(new BasicButtonUI());
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(new Color(11, 23, 54));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(125, 42));
        btn.setMinimumSize(new Dimension(125, 42));
        btn.setMaximumSize(new Dimension(125, 42));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private static class NoticeBackgroundPanel extends JPanel {
        NoticeBackgroundPanel() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth();
            int h = getHeight();

            LinearGradientPaint bg = new LinearGradientPaint(
                    0, 0, w, h,
                    new float[]{0f, 0.45f, 1f},
                    new Color[]{
                            new Color(248, 252, 255),
                            new Color(220, 237, 255),
                            new Color(176, 211, 249)
                    }
            );
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
    }

    public static void showHoaDonDetails(Component parent, HoaDon hd) {
        if (hd == null) {
            showError(parent, "Lỗi", "Không tìm thấy hóa đơn cần xem.");
            return;
        }

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int dialogHeight = Math.min(760, screen.height - 40);
        int dialogWidth = Math.min(1040, screen.width - 60);

        JDialog dialog = createBaseDialog(parent, "Chi tiết hóa đơn", dialogWidth, dialogHeight);

        JPanel root = (JPanel) dialog.getContentPane();
        root.setLayout(new BorderLayout());
        Color detailBg = new Color(248, 250, 252);
        root.setBackground(detailBg);

        root.add(createHeader(dialog, "Chi tiết hóa đơn · " + hd.getMaHoaDon()), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(true);
        content.setBackground(detailBg);
        content.setBorder(new EmptyBorder(14, 20, 10, 20));

        JPanel topContent = new JPanel();
        topContent.setOpaque(false);
        topContent.setLayout(new BoxLayout(topContent, BoxLayout.Y_AXIS));

        JLabel caption = new JLabel("Thông tin chi tiết các sản phẩm trong hóa đơn");
        caption.setForeground(new Color(71, 85, 105));
        caption.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        caption.setAlignmentX(Component.LEFT_ALIGNMENT);
        topContent.add(caption);
        topContent.add(Box.createVerticalStrut(10));

        JPanel summary = new JPanel(new GridLayout(2, 2, 12, 10));
        summary.setOpaque(false);
        summary.setAlignmentX(Component.LEFT_ALIGNMENT);
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        summary.setPreferredSize(new Dimension(DETAIL_CONTENT_WIDTH, 150));

        summary.add(createInfoCardCompact("Mã khách hàng", safe(hd.getMaKhachHang())));
        summary.add(createInfoCardCompact("Mã nhân viên", safe(hd.getMaNhanVien())));

        String ngayLap = "";
        if (hd.getNgayLap() != null) {
            ngayLap = hd.getNgayLap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
        summary.add(createInfoCardCompact("Ngày lập", ngayLap));
        summary.add(createInfoCardCompact("Trạng thái", safe(hd.getTrangThai())));

        topContent.add(summary);

        String ghiChuHoaDon = hd.getGhiChu() == null ? "" : hd.getGhiChu().trim();
        if (!ghiChuHoaDon.isEmpty()) {
            topContent.add(Box.createVerticalStrut(10));

            JPanel note = new JPanel(new BorderLayout(0, 5));
            note.setOpaque(true);
            note.setBackground(Color.WHITE);
            note.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(216, 224, 236), 1),
                    new EmptyBorder(8, 12, 8, 12)
            ));
            note.setAlignmentX(Component.LEFT_ALIGNMENT);
            note.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));

            JLabel title = new JLabel("Ghi chú");
            title.setForeground(new Color(11, 23, 54));
            title.setFont(new Font("Segoe UI", Font.BOLD, 13));

            JLabel text = new JLabel(toHtml(ghiChuHoaDon, 14, 1.25, 820));
            text.setForeground(new Color(71, 85, 105));
            text.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            note.add(title, BorderLayout.NORTH);
            note.add(text, BorderLayout.CENTER);
            topContent.add(note);
        }

        content.add(topContent, BorderLayout.NORTH);

        JTable table = createHoaDonDetailTable(hd);
        JScrollPane tableScroll = new JScrollPane(table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        styleLightDetailScrollPane(tableScroll);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tableScroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(216, 224, 236), 1));
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableScroll.setPreferredSize(new Dimension(DETAIL_CONTENT_WIDTH, 245));
        tableScroll.setMinimumSize(new Dimension(600, 220));

        content.add(tableScroll, BorderLayout.CENTER);

        JPanel bottomContent = new JPanel();
        bottomContent.setOpaque(false);
        bottomContent.setLayout(new BoxLayout(bottomContent, BoxLayout.Y_AXIS));

        JPanel totalPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        totalPanel.setOpaque(false);
        totalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        totalPanel.setPreferredSize(new Dimension(DETAIL_CONTENT_WIDTH, 86));

        totalPanel.add(createTotalCardCompact("Tổng tiền", formatMoney(hd.getTongTien()) + " VNĐ"));
        totalPanel.add(createTotalCardCompact("Giảm giá", formatMoney(hd.getGiamGia()) + " VNĐ"));
        String labelThanhToan = HoaDon.TRANG_THAI_TRA_MOT_PHAN.equals(hd.getTrangThai()) ? "Còn lại sau trả" : "Thanh toán";
        double soTienHienThi = HoaDon.TRANG_THAI_TRA_MOT_PHAN.equals(hd.getTrangThai()) ? hd.getThanhToanSauTraHang() : hd.getThanhToan();
        totalPanel.add(createTotalCardCompact(labelThanhToan, formatMoney(soTienHienThi) + " VNĐ"));

        bottomContent.add(totalPanel);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(14, 20, 0, 20));

        JButton close = createPrimaryButton("Đóng");
        close.setPreferredSize(new Dimension(150, 40));
        close.addActionListener(e -> dialog.dispose());
        footer.add(close);

        bottomContent.add(footer);
        content.add(bottomContent, BorderLayout.SOUTH);

        root.add(content, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> tableScroll.getVerticalScrollBar().setValue(0));
        dialog.setVisible(true);
    }

    private static JTable createHoaDonDetailTable(HoaDon hd) {
        String[] cols = {"Mã SP", "Kích cỡ", "Số lượng", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        for (Map.Entry<String, double[]> e : hd.getChiTiet().entrySet()) {
            int soLuong = (int) e.getValue()[0];
            double donGia = e.getValue()[1];
            double thanhTien = soLuong * donGia;

            model.addRow(new Object[]{
                    HoaDon.layMaSanPhamTuKey(e.getKey()),
                    HoaDon.layKichCoTuKey(e.getKey()),
                    soLuong,
                    formatMoney(donGia) + " VNĐ",
                    formatMoney(thanhTien) + " VNĐ"
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(new Color(11, 23, 54));
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(216, 224, 236));
        table.setSelectionBackground(new Color(18, 59, 140));
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setPreferredScrollableViewportSize(new Dimension(900, 160));

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 38));
        header.setBackground(new Color(6, 26, 58));
        header.setForeground(new Color(244, 210, 122));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setOpaque(true);
        headerRenderer.setBackground(new Color(6, 26, 58));
        headerRenderer.setForeground(new Color(244, 210, 122));
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setBorder(BorderFactory.createLineBorder(new Color(216, 224, 236), 1));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);

                if (isSelected) {
                    setBackground(new Color(18, 59, 140));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    setForeground(new Color(11, 23, 54));
                }

                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(105);
        table.getColumnModel().getColumn(2).setPreferredWidth(105);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(190);

        return table;
    }

    public static void showHoaDonPrintPreview(Component parent, HoaDon hd) {
        if (hd == null) {
            showError(parent, "Lỗi", "Không tìm thấy hóa đơn cần in.");
            return;
        }

        if (HoaDon.TRANG_THAI_CHO.equals(hd.getTrangThai())) {
            showWarning(parent, "Không thể in hóa đơn", "Hóa đơn này chưa thanh toán nên không thể in.");
            return;
        }

        if (HoaDon.TRANG_THAI_HUY.equals(hd.getTrangThai())) {
            showWarning(parent, "Không thể in hóa đơn", "Hóa đơn này đã hủy nên không thể in.");
            return;
        }

        JDialog dialog = createBaseDialog(parent, "Xem / In hóa đơn", 760, 720);
        JPanel root = (JPanel) dialog.getContentPane();
        root.setLayout(new BorderLayout());
        root.setBackground(BG);

        root.add(createHeader(dialog, "Xem / In hóa đơn · " + hd.getMaHoaDon()), BorderLayout.NORTH);

        JTextArea txtPreview = new JTextArea(taoNoiDungInHoaDon(hd));
        txtPreview.setEditable(false);
        txtPreview.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtPreview.setForeground(TEXT);
        txtPreview.setBackground(CARD);
        txtPreview.setCaretColor(GOLD_LIGHT);
        txtPreview.setBorder(new EmptyBorder(18, 18, 18, 18));

        JScrollPane scroll = new JScrollPane(txtPreview);
        styleDarkScrollPane(scroll);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scroll.getViewport().setBackground(CARD);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(18, 22, 12, 22));
        center.add(scroll, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 20, 18, 20));

        JButton print = createPrimaryButton("In");
        print.setPreferredSize(new Dimension(140, 42));
        print.addActionListener(e -> {
            File saved = luuHoaDonPdf(dialog, hd);
            if (saved != null) {
                dialog.dispose();
            }
        });

        JButton close = createSecondaryButton("Đóng");
        close.setPreferredSize(new Dimension(140, 42));
        close.addActionListener(e -> dialog.dispose());

        footer.add(print);
        footer.add(close);
        root.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static File luuHoaDonPdf(Component parent, HoaDon hd) {
        if (hd != null && HoaDon.TRANG_THAI_TRA_MOT_PHAN.equals(hd.getTrangThai())) {
            int choice = showInvoicePrintChoice(parent);
            if (choice == INVOICE_PRINT_BEFORE_RETURN) {
                return luuHoaDonPdf(parent, hd, false);
            }
            if (choice == INVOICE_PRINT_AFTER_RETURN) {
                return luuHoaDonPdf(parent, hd, true);
            }
            return null;
        }
        return luuHoaDonPdf(parent, hd, false);
    }

    public static File luuHoaDonPdf(Component parent, HoaDon hd, boolean inSauTraHang) {
        if (hd == null) {
            showError(parent, "Lỗi", "Không tìm thấy hóa đơn cần in.");
            return null;
        }

        if (HoaDon.TRANG_THAI_CHO.equals(hd.getTrangThai())) {
            showWarning(parent, "Không thể in hóa đơn", "Hóa đơn này chưa thanh toán nên không thể in.");
            return null;
        }

        if (HoaDon.TRANG_THAI_HUY.equals(hd.getTrangThai()) || HoaDon.TRANG_THAI_HET_HANG.equals(hd.getTrangThai())) {
            showWarning(parent, "Không thể in hóa đơn", "Không thể in hóa đơn này.");
            return null;
        }

        try {
            HoaDon hdMoiNhat = docHoaDonMoiNhatTheoMa(hd.getMaHoaDon());
            if (hdMoiNhat != null) {
                hd = hdMoiNhat;
            }

            LocalDateTime mocNgay = hd.getNgayThanhToan() != null
                    ? hd.getNgayThanhToan()
                    : (hd.getNgayLap() != null ? hd.getNgayLap() : LocalDateTime.now());

            String tenThuMucNgay = mocNgay.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            File root = new File("hoadon");
            File dateDir = new File(root, tenThuMucNgay);
            java.nio.file.Files.createDirectories(dateDir.toPath());

            String thoiGianIn = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss_SSS"));
            String suffix = inSauTraHang ? "_sau_tra_hang_" : "_";
            String tenFile = safeFileName(hd.getMaHoaDon()) + suffix + thoiGianIn + ".pdf";
            File out = new File(dateDir, tenFile);
            String noiDung = inSauTraHang ? taoNoiDungInHoaDonSauTraHang(hd) : taoNoiDungInHoaDon(hd);
            PdfExporter.exportTextToPdf(out, noiDung);

            showInlineSuccess(parent, "In hóa đơn thành công");
            return out;
        } catch (Exception ex) {
            showError(parent, "Lỗi in hóa đơn", "Không thể lưu file hóa đơn: " + ex.getMessage());
            return null;
        }
    }

   
    public static File luuHoaDonTxt(Component parent, HoaDon hd) {
        return luuHoaDonPdf(parent, hd);
    }


    private static void showInlineSuccess(Component parent, String message) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, message, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(420, 180);
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
                    Path2D.Double wave = new Path2D.Double();
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

        JLabel icon = new JLabel(new SuccessCheckIcon(42));
        JLabel text = new JLabel(message);
        text.setFont(new Font("Segoe UI", Font.BOLD, 22));
        text.setForeground(new Color(11, 23, 54));

        row.add(icon);
        row.add(text);

        root.add(row, new GridBagConstraints());
        dialog.setContentPane(root);
        dialog.setLocationRelativeTo(parent);

        Timer timer = new Timer(900, e -> {
            ((Timer) e.getSource()).stop();
            dialog.dispose();
        });
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    private static class SuccessCheckIcon implements Icon {
        private final int size;

        SuccessCheckIcon(int size) {
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

    private static HoaDon docHoaDonMoiNhatTheoMa(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) return null;
        try {
            for (HoaDon item : HoaDon.docTuSQL()) {
                if (item != null && maHoaDon.equalsIgnoreCase(item.getMaHoaDon())) {
                    return item;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static String safeFileName(String name) {
        if (name == null || name.isBlank()) return "file";
        return name.trim().replaceAll("[\\/:*?\"<>|]", "_");
    }

    private static String taoNoiDungInHoaDon(HoaDon hd) {
        StringBuilder sb = new StringBuilder();

        sb.append("====================================================\n");
        sb.append("                 BILUXURY FASHION\n");
        sb.append("                HÓA ĐƠN BÁN HÀNG\n");
        sb.append("====================================================\n\n");

        sb.append("Mã hóa đơn   : ").append(hd.getMaHoaDon()).append("\n");
        sb.append("Mã khách hàng: ").append(hd.getMaKhachHang()).append("\n");
        sb.append("Mã nhân viên : ").append(hd.getMaNhanVien()).append("\n");
        sb.append("Ngày lập     : ").append(hd.getNgayLapText()).append("\n");

        if (hd.getNgayThanhToan() != null) {
            sb.append("Ngày thanh toán: ").append(hd.getNgayThanhToanText()).append("\n");
        }

        sb.append("Trạng thái   : ").append(hd.getTrangThai()).append("\n");

        if (hd.getGhiChu() != null && !hd.getGhiChu().isBlank()) {
            sb.append("Ghi chú      : ").append(hd.getGhiChu()).append("\n");
        }

        sb.append("\n----------------------------------------------------\n");
        sb.append(String.format("%-12s %8s %8s %14s %14s %14s\n", "Mã SP", "Size", "SL", "Đơn giá", "Giảm giá", "Thành tiền"));
        sb.append("----------------------------------------------------\n");

        double tongGiamGiaSanPham = 0;
        for (Map.Entry<String, double[]> e : hd.getChiTiet().entrySet()) {
            int soLuong = (int) e.getValue()[0];
            double donGia = e.getValue()[1];
            String maSP = HoaDon.layMaSanPhamTuKey(e.getKey());
            String kichCo = HoaDon.layKichCoTuKey(e.getKey());
            double giamGiaDong = tinhGiamGiaSanPham(maSP, soLuong, donGia);
            double thanhTien = Math.max(0, soLuong * donGia - giamGiaDong);
            tongGiamGiaSanPham += giamGiaDong;

            sb.append(String.format("%-12s %8s %8d %,14.0f %,14.0f %,14.0f\n",
                    maSP, kichCo, soLuong, donGia, giamGiaDong, thanhTien));
        }

        sb.append("----------------------------------------------------\n");
        sb.append(String.format("Tổng tiền      : %,28.0f VNĐ\n", hd.getTongTien()));
        sb.append(String.format("Giảm giá       : %,28.0f VNĐ\n", tongGiamGiaSanPham));
        sb.append(String.format("Điểm sử dụng   : %,28d điểm\n", hd.getDiemSuDung()));
        sb.append(String.format("Tiền đổi điểm  : %,28.0f VNĐ\n", hd.getTienDoiDiem()));
        sb.append(String.format("Thanh toán     : %,28.0f VNĐ\n", hd.getThanhToan()));

        sb.append("\n====================================================\n");
        sb.append("             Cảm ơn quý khách đã mua hàng!\n");
        sb.append("====================================================\n");

        return sb.toString();
    }

    private static String taoNoiDungInHoaDonSauTraHang(HoaDon hd) {
        StringBuilder sb = new StringBuilder();

        sb.append("====================================================\n");
        sb.append("                 BILUXURY FASHION\n");
        sb.append("        HÓA ĐƠN BÁN HÀNG SAU TRẢ HÀNG\n");
        sb.append("====================================================\n\n");

        sb.append("Mã hóa đơn   : ").append(hd.getMaHoaDon()).append("\n");
        sb.append("Mã khách hàng: ").append(hd.getMaKhachHang()).append("\n");
        sb.append("Mã nhân viên : ").append(hd.getMaNhanVien()).append("\n");
        sb.append("Ngày lập     : ").append(hd.getNgayLapText()).append("\n");

        if (hd.getNgayThanhToan() != null) {
            sb.append("Ngày thanh toán: ").append(hd.getNgayThanhToanText()).append("\n");
        }

        sb.append("Trạng thái   : ").append(hd.getTrangThai()).append("\n");
        sb.append("Ghi chú      : Hóa đơn sau trả hàng - đã loại bỏ sản phẩm khách trả.\n");

        if (hd.getGhiChu() != null && !hd.getGhiChu().isBlank()) {
            sb.append("Ghi chú gốc  : ").append(hd.getGhiChu()).append("\n");
        }

        sb.append("\n----------------------------------------------------\n");
        sb.append(String.format("%-12s %8s %8s %14s %14s %14s\n", "Mã SP", "Size", "SL", "Đơn giá", "Giảm giá", "Thành tiền"));
        sb.append("----------------------------------------------------\n");

        Map<String, double[]> chiTietConLai = hd.getChiTietConLaiSauTraHang();
        double tongGiamGiaSauTra = 0;
        for (Map.Entry<String, double[]> e : chiTietConLai.entrySet()) {
            int soLuong = (int) e.getValue()[0];
            double donGia = e.getValue()[1];
            String maSP = HoaDon.layMaSanPhamTuKey(e.getKey());
            String kichCo = HoaDon.layKichCoTuKey(e.getKey());
            double giamGiaDong = tinhGiamGiaSanPham(maSP, soLuong, donGia);
            double thanhTien = Math.max(0, soLuong * donGia - giamGiaDong);
            tongGiamGiaSauTra += giamGiaDong;

            sb.append(String.format("%-12s %8s %8d %,14.0f %,14.0f %,14.0f\n",
                    maSP, kichCo, soLuong, donGia, giamGiaDong, thanhTien));
        }

        double tongTienSauTra = hd.getTongTienSauTraHang();
        double thanhToanSauTra = hd.getThanhToanSauTraHang();

        sb.append("----------------------------------------------------\n");
        sb.append(String.format("Tổng tiền      : %,28.0f VNĐ\n", tongTienSauTra));
        sb.append(String.format("Giảm giá       : %,28.0f VNĐ\n", tongGiamGiaSauTra));
        sb.append(String.format("Điểm sử dụng   : %,28d điểm\n", 0));
        sb.append(String.format("Tiền đổi điểm  : %,28.0f VNĐ\n", 0.0));
        sb.append(String.format("Tiền hoàn trả  : %,28.0f VNĐ\n", hd.getTienHoanTra()));
        sb.append(String.format("Thanh toán     : %,28.0f VNĐ\n", thanhToanSauTra));

        sb.append("\n====================================================\n");
        sb.append("             Cảm ơn quý khách đã mua hàng!\n");
        sb.append("====================================================\n");

        return sb.toString();
    }

    private static double tinhGiamGiaSanPham(String maSanPham, int soLuong, double donGia) {
        if (maSanPham == null || maSanPham.isBlank() || soLuong <= 0 || donGia <= 0) {
            return 0;
        }
        double phanTram = layPhanTramGiamGiaSanPham(maSanPham);
        if (phanTram <= 0) {
            return 0;
        }
        return Math.round(soLuong * donGia * phanTram / 100.0);
    }

    private static double layPhanTramGiamGiaSanPham(String maSanPham) {
        String sql = "SELECT ISNULL(giamGiaBan, 0) AS giamGiaBan FROM SanPham WHERE maSanPham = ?";
        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSanPham.trim().toUpperCase());
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Math.max(0, Math.min(100, rs.getDouble("giamGiaBan")));
                }
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    private static JPanel createInfoCardCompact(String label, String value) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 224, 236), 1),
                new EmptyBorder(10, 18, 10, 18)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel val = new JLabel(value);
        val.setForeground(new Color(11, 23, 54));
        val.setFont(new Font("Segoe UI", Font.BOLD, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(lbl, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(val, gbc);

        return panel;
    }

    private static JPanel createTotalCardCompact(String label, String value) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 224, 236), 1),
                new EmptyBorder(11, 18, 11, 18)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel val = new JLabel(value);
        val.setForeground(new Color(138, 100, 30));
        val.setFont(new Font("Segoe UI", Font.BOLD, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(lbl, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(val, gbc);

        return panel;
    }

    private static JDialog createBaseDialog(Component parent, String title, int width, int height) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setSize(width, height);
        dialog.setMinimumSize(new Dimension(Math.min(width, 520), Math.min(height, 360)));
        dialog.setLocationRelativeTo(parent);
        dialog.setContentPane(new JPanel());
        return dialog;
    }

    private static JPanel createHeader(JDialog dialog, String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(16, 22, 14, 18)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(GOLD_LIGHT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton close = new JButton("×");
        close.setFont(new Font("Segoe UI", Font.BOLD, 28));
        close.setForeground(new Color(210, 200, 185));
        close.setBackground(BG);
        close.setBorder(null);
        close.setFocusPainted(false);
        close.setContentAreaFilled(false);
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.addActionListener(e -> dialog.dispose());

        header.add(lblTitle, BorderLayout.WEST);
        header.add(close, BorderLayout.EAST);

        return header;
    }

    private static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(new Color(28, 20, 10));
        btn.setBackground(new Color(214, 174, 117));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setUI(new BasicButtonUI());
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 201, 151), 1),
                new EmptyBorder(8, 20, 8, 20)
        ));
        return btn;
    }

    private static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(TEXT);
        btn.setBackground(CARD_2);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setUI(new BasicButtonUI());
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 20, 8, 20)
        ));
        return btn;
    }


    private static void styleLightDetailScrollPane(JScrollPane scroll) {
        Color lightBg = new Color(248, 250, 252);
        scroll.setOpaque(true);
        scroll.setBackground(lightBg);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(lightBg);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(18);
        scroll.getVerticalScrollBar().setUI(new GoldScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new GoldScrollBarUI());
    }

    private static void styleDarkScrollPane(JScrollPane scroll) {
        scroll.setOpaque(true);
        scroll.setBackground(BG);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(18, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 18));
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getHorizontalScrollBar().setUnitIncrement(18);
        scroll.getVerticalScrollBar().setUI(new GoldScrollBarUI());
        scroll.getHorizontalScrollBar().setUI(new GoldScrollBarUI());
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String formatMoney(double value) {
        return String.format("%,.0f", value);
    }

    private static String toHtml(String text, int fontSize, double lineHeight, int width) {
        String escaped = safe(text)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");

        return "<html><div style='width:" + width + "px; font-size:" + fontSize
                + "px; line-height:" + lineHeight + ";'>" + escaped + "</div></html>";
    }

    private enum StatusType {
        SUCCESS, INFO, WARNING, ERROR, CONFIRM
    }

    private static class StatusCircleIcon implements Icon {
        private final StatusType type;
        private final int size;

        StatusCircleIcon(StatusType type, int size) {
            this.type = type;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color color = switch (type) {
                case SUCCESS -> SUCCESS;
                case INFO -> GOLD;
                case WARNING, CONFIRM -> WARNING;
                case ERROR -> ERROR;
            };

            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 55));
            g2.fillOval(x, y, size, size);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.6f));
            g2.drawOval(x + 1, y + 1, size - 2, size - 2);

            switch (type) {
                case SUCCESS -> drawCheck(g2, x, y, size);
                case ERROR -> drawCross(g2, x, y, size, Color.WHITE, 3.2f);
                case INFO -> drawInfo(g2, x, y, size);
                case WARNING -> drawExclamation(g2, x, y, size);
                case CONFIRM -> drawQuestion(g2, x, y, size);
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        private void drawCheck(Graphics2D g2, int x, int y, int size) {
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(4.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D path = new Path2D.Double();
            path.moveTo(x + size * 0.28, y + size * 0.55);
            path.lineTo(x + size * 0.44, y + size * 0.70);
            path.lineTo(x + size * 0.74, y + size * 0.34);
            g2.draw(path);
        }

        private void drawCross(Graphics2D g2, int x, int y, int size, Color color, float stroke) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine((int) (x + size * 0.32), (int) (y + size * 0.32),
                    (int) (x + size * 0.68), (int) (y + size * 0.68));
            g2.drawLine((int) (x + size * 0.68), (int) (y + size * 0.32),
                    (int) (x + size * 0.32), (int) (y + size * 0.68));
        }

        private void drawInfo(Graphics2D g2, int x, int y, int size) {
            g2.setColor(Color.WHITE);
            g2.fillOval((int) (x + size * 0.46), (int) (y + size * 0.22),
                    (int) (size * 0.09), (int) (size * 0.09));
            g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine((int) (x + size * 0.5), (int) (y + size * 0.38),
                    (int) (x + size * 0.5), (int) (y + size * 0.68));
        }

        private void drawExclamation(Graphics2D g2, int x, int y, int size) {
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine((int) (x + size * 0.5), (int) (y + size * 0.25),
                    (int) (x + size * 0.5), (int) (y + size * 0.58));
            g2.fillOval((int) (x + size * 0.46), (int) (y + size * 0.70),
                    (int) (size * 0.09), (int) (size * 0.09));
        }

        private void drawQuestion(Graphics2D g2, int x, int y, int size) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, (int) (size * 0.58)));
            FontMetrics fm = g2.getFontMetrics();
            String s = "?";
            g2.drawString(s, x + (size - fm.stringWidth(s)) / 2, y + (int) (size * 0.68));
        }
    }

    private static class GoldScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = GOLD;
            trackColor = CARD;
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

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(GOLD);
            g2.fillRoundRect(r.x + 2, r.y + 3, r.width - 4, r.height - 6, 8, 8);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(CARD);
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }
}