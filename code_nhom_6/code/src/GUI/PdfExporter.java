package GUI;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class PdfExporter {

    

    private static final int IMG_W = 1240;
    private static final int IMG_H = 1754;
    private static final int PDF_W = 595;
    private static final int PDF_H = 842;

    private static final int MARGIN_LEFT = 70;
    private static final int MARGIN_RIGHT = 70;
    private static final int MARGIN_TOP = 58;
    private static final int MARGIN_BOTTOM = 82;

    private static final Color NAVY_BLACK = new Color(2, 8, 20);
    private static final Color NAVY_DARK = new Color(6, 26, 58);
    private static final Color NAVY_MID = new Color(8, 36, 90);
    private static final Color NAVY_ROYAL = new Color(11, 47, 107);
    private static final Color GOLD = new Color(216, 170, 69);
    private static final Color GOLD_LIGHT = new Color(244, 210, 122);
    private static final Color GOLD_SOFT = new Color(236, 218, 184);
    private static final Color PAPER = new Color(250, 247, 241);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(31, 41, 55);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(222, 211, 193);
    private static final Color ROW_ALT = new Color(250, 252, 255);

    private PdfExporter() {
    }

    public static File exportTextToPdf(File out, String content) throws IOException {
        return exportTextToPdf(out, null, content);
    }

    public static File exportTextToPdf(File out, String title, String content) throws IOException {
        if (out == null) {
            throw new IOException("File xuất PDF không hợp lệ.");
        }
        File parent = out.getParentFile();
        if (parent != null) {
            java.nio.file.Files.createDirectories(parent.toPath());
        }

        File pdfFile = ensurePdfExtension(out);
        String normalized = normalize(content == null ? "" : content);
        List<BufferedImage> pages;

        if (normalized.contains("HÓA ĐƠN BÁN HÀNG") || normalized.contains("HOA DON BAN HANG")) {
            pages = renderInvoicePages(parseInvoice(normalized));
        } else if (normalized.contains("CHI TIẾT NHẬP HÀNG NHÀ CUNG CẤP")
                || normalized.contains("CHI TIET NHAP HANG NHA CUNG CAP")) {
            pages = renderSupplierImportPages(parseSupplierImport(normalized));
        } else if (normalized.contains("BÁO CÁO DOANH THU") || normalized.contains("BAO CAO DOANH THU")
                || normalized.contains("Tổng doanh thu") || normalized.contains("Doanh thu thực")) {
            pages = renderRevenuePages(parseRevenue(normalized));
        } else {
            pages = renderLuxuryTextPages(title, normalized);
        }

        byte[][] imageBytes = new byte[pages.size()][];
        for (int i = 0; i < pages.size(); i++) {
            imageBytes[i] = toJpegBytes(pages.get(i));
        }
        writeImagePdf(pdfFile, imageBytes);
        return pdfFile;
    }

    private static File ensurePdfExtension(File file) {
        String path = file.getAbsolutePath();
        if (path.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            return file;
        }
        return new File(path + ".pdf");
    }

    

    private static List<BufferedImage> renderInvoicePages(InvoiceDoc doc) {
        List<BufferedImage> pages = new ArrayList<>();
        PageCanvas pc = newPage();
        drawHeroHeader(pc.g, "BILUXURY FASHION", "HÓA ĐƠN BÁN HÀNG", doc.get("Mã hóa đơn"), "Premium Men's Wear");

        int y = 255;
        int cardW = (IMG_W - MARGIN_LEFT - MARGIN_RIGHT - 24) / 2;
        int cardH = 78;
        drawInfoCard(pc.g, MARGIN_LEFT, y, cardW, cardH, "Mã hóa đơn", doc.get("Mã hóa đơn"));
        drawInfoCard(pc.g, MARGIN_LEFT + cardW + 24, y, cardW, cardH, "Trạng thái", doc.get("Trạng thái"));
        y += cardH + 18;
        drawInfoCard(pc.g, MARGIN_LEFT, y, cardW, cardH, "Mã khách hàng", doc.get("Mã khách hàng"));
        drawInfoCard(pc.g, MARGIN_LEFT + cardW + 24, y, cardW, cardH, "Mã nhân viên", doc.get("Mã nhân viên"));
        y += cardH + 18;
        drawInfoCard(pc.g, MARGIN_LEFT, y, cardW, cardH, "Ngày lập", doc.get("Ngày lập"));
        drawInfoCard(pc.g, MARGIN_LEFT + cardW + 24, y, cardW, cardH, "Ngày thanh toán", emptyToDash(doc.get("Ngày thanh toán")));
        y += cardH + 34;

        if (!empty(doc.get("Ghi chú"))) {
            int noteH = 76;
            drawNoteCard(pc.g, MARGIN_LEFT, y, IMG_W - MARGIN_LEFT - MARGIN_RIGHT, noteH, "Ghi chú", doc.get("Ghi chú"));
            y += noteH + 30;
        }

        y = drawSectionTitle(pc.g, "Chi tiết sản phẩm", y);
        int[] widths = {190, 105, 80, 190, 190, 235};
        String[] headers = {"Mã sản phẩm", "Size", "SL", "Đơn giá", "Giảm giá", "Thành tiền"};
        y = drawTableHeader(pc.g, MARGIN_LEFT, y, widths, headers);

        int rowH = 52;
        int index = 0;
        for (InvoiceItem item : doc.items) {
            if (y + rowH > IMG_H - 370) {
                pc.g.dispose();
                pages.add(pc.page);
                pc = newPage();
                drawHeroHeader(pc.g, "BILUXURY FASHION", "HÓA ĐƠN BÁN HÀNG", doc.get("Mã hóa đơn"), "Premium Men's Wear");
                y = 260;
                y = drawSectionTitle(pc.g, "Chi tiết sản phẩm tiếp theo", y);
                y = drawTableHeader(pc.g, MARGIN_LEFT, y, widths, headers);
            }
            String[] row = {item.maSP, item.kichCo, item.soLuong, item.donGia, item.giamGia, item.thanhTien};
            drawTableRow(pc.g, MARGIN_LEFT, y, widths, row, index % 2 == 1);
            y += rowH;
            index++;
        }

        y += 28;
        int summaryW = 520;
        int summaryH = invoiceSummaryHeight();

        
        if (y + summaryH + 130 > IMG_H - MARGIN_BOTTOM) {
            pc.g.dispose();
            pages.add(pc.page);
            pc = newPage();
            drawHeroHeader(pc.g, "BILUXURY FASHION", "HÓA ĐƠN BÁN HÀNG", doc.get("Mã hóa đơn"), "Premium Men's Wear");
            y = 260;
            y = drawSectionTitle(pc.g, "Tổng kết hóa đơn", y);
        }

        int summaryX = IMG_W - MARGIN_RIGHT - summaryW;
        int summaryY = y;
        drawInvoiceSummary(pc.g, summaryX, summaryY, summaryW, doc);

        int thankY = Math.max(summaryY + summaryH + 36, IMG_H - 164);
        if (thankY + 86 > IMG_H - MARGIN_BOTTOM) {
            thankY = IMG_H - 176;
        }
        drawThankYou(pc.g, MARGIN_LEFT, thankY, IMG_W - MARGIN_LEFT - MARGIN_RIGHT);
        pc.g.dispose();
        pages.add(pc.page);
        addPageNumbers(pages, pickFont("Segoe UI", Font.PLAIN, 16));
        return pages;
    }

    private static int invoiceSummaryHeight() {
        return 47 * 5 + 72;
    }

    private static void drawInvoiceSummary(Graphics2D g, int x, int y, int w, InvoiceDoc doc) {
        int rowH = 47;
        int h = invoiceSummaryHeight();
        fillRound(g, x, y, w, h, 26, WHITE, new Color(214, 198, 166));

        g.setColor(NAVY_DARK);
        g.fillRoundRect(x, y, w, 58, 26, 26);
        g.fillRect(x, y + 30, w, 28);
        g.setFont(pickFont("Segoe UI", Font.BOLD, 22));
        g.setColor(GOLD_LIGHT);
        g.drawString("Tổng thanh toán", x + 26, y + 38);

        int cy = y + 76;
        drawTotalLine(g, x + 26, cy, w - 52, "Tổng tiền", doc.total("Tổng tiền"), false); cy += rowH;
        drawTotalLine(g, x + 26, cy, w - 52, "Giảm giá", doc.total("Giảm giá"), false); cy += rowH;
        drawTotalLine(g, x + 26, cy, w - 52, "Điểm sử dụng", doc.total("Điểm sử dụng"), false); cy += rowH;
        drawTotalLine(g, x + 26, cy, w - 52, "Tiền đổi điểm", doc.total("Tiền đổi điểm"), false); cy += rowH;

        g.setColor(new Color(244, 210, 122, 70));
        g.fillRoundRect(x + 18, cy - 29, w - 36, 56, 18, 18);
        drawTotalLine(g, x + 26, cy, w - 52, "THANH TOÁN", doc.total("Thanh toán"), true);
    }

    

    private static List<BufferedImage> renderRevenuePages(RevenueDoc doc) {
        List<BufferedImage> pages = new ArrayList<>();
        PageCanvas pc = newPage();
        drawHeroHeader(pc.g, "BILUXURY FASHION", "BÁO CÁO DOANH THU", doc.fieldOr("Ngày", "Tổng hợp"), "Revenue Report");

        int y = 260;
        if (!doc.fields.isEmpty()) {
            y = drawRevenueCards(pc.g, doc, y);
        }

        if (!doc.rows.isEmpty()) {
            y += 16;
            y = drawSectionTitle(pc.g, "Bảng doanh thu", y);
            pc = drawRevenueTable(pages, pc, doc, y);
        } else {
            y += 30;
            y = drawSectionTitle(pc.g, "Chi tiết báo cáo", y);
            for (Map.Entry<String, String> e : doc.fields.entrySet()) {
                if (y + 58 > IMG_H - MARGIN_BOTTOM) {
                    pc.g.dispose();
                    pages.add(pc.page);
                    pc = newPage();
                    drawHeroHeader(pc.g, "BILUXURY FASHION", "BÁO CÁO DOANH THU", doc.fieldOr("Ngày", "Tổng hợp"), "Revenue Report");
                    y = 260;
                }
                drawKeyValueLine(pc.g, MARGIN_LEFT, y, IMG_W - MARGIN_LEFT - MARGIN_RIGHT, e.getKey(), e.getValue());
                y += 58;
            }
        }

        pc.g.dispose();
        pages.add(pc.page);
        addPageNumbers(pages, pickFont("Segoe UI", Font.PLAIN, 16));
        return pages;
    }

    private static int drawRevenueCards(Graphics2D g, RevenueDoc doc, int y) {
        int gap = 18;
        int w = (IMG_W - MARGIN_LEFT - MARGIN_RIGHT - gap) / 2;
        drawInfoCard(g, MARGIN_LEFT, y, w, 78, "Ngày", doc.fieldOr("Ngày", "-"));
        drawInfoCard(g, MARGIN_LEFT + w + gap, y, w, 78, "Số hóa đơn", doc.fieldOr("Số hóa đơn", "0"));
        y += 102;

        int kpiW = (IMG_W - MARGIN_LEFT - MARGIN_RIGHT - gap) / 2;
        drawKpiCard(g, MARGIN_LEFT, y, kpiW, 112, "Tổng doanh thu", doc.fieldOr("Tổng doanh thu", "0 VNĐ"), false);
        drawKpiCard(g, MARGIN_LEFT + kpiW + gap, y, kpiW, 112, "Doanh thu thực", doc.fieldOr("Doanh thu thực", "0 VNĐ"), true);
        return y + 144;
    }

    private static PageCanvas drawRevenueTable(List<BufferedImage> pages, PageCanvas pc, RevenueDoc doc, int y) {
        List<String> cols = new ArrayList<>(doc.rows.get(0).keySet());
        int n = Math.min(cols.size(), 8);
        cols = new ArrayList<>(cols.subList(0, n));

        int tableW = IMG_W - MARGIN_LEFT - MARGIN_RIGHT;
        int[] widths = calcRevenueWidths(cols, tableW);
        String[] headers = cols.toArray(new String[0]);
        y = drawTableHeader(pc.g, MARGIN_LEFT, y, widths, headers);

        int rowH = 54;
        int index = 0;
        for (Map<String, String> r : doc.rows) {
            if (y + rowH > IMG_H - MARGIN_BOTTOM - 30) {
                pc.g.dispose();
                pages.add(pc.page);
                pc = newPage();
                drawHeroHeader(pc.g, "BILUXURY FASHION", "BÁO CÁO DOANH THU", doc.fieldOr("Ngày", "Tổng hợp"), "Revenue Report");
                y = 260;
                y = drawSectionTitle(pc.g, "Bảng doanh thu tiếp theo", y);
                y = drawTableHeader(pc.g, MARGIN_LEFT, y, widths, headers);
            }
            String[] values = new String[cols.size()];
            for (int i = 0; i < cols.size(); i++) values[i] = r.getOrDefault(cols.get(i), "");
            drawTableRow(pc.g, MARGIN_LEFT, y, widths, values, index % 2 == 1);
            y += rowH;
            index++;
        }
        return pc;
    }

    private static int[] calcRevenueWidths(List<String> cols, int tableW) {
        int n = cols.size();
        int[] widths = new int[n];
        if (n == 0) return widths;
        int[] weights = new int[n];
        int totalWeight = 0;
        for (int i = 0; i < n; i++) {
            String c = cols.get(i).toLowerCase(Locale.ROOT);
            int wt;
            if (c.contains("ngày") || c.contains("gio") || c.contains("giờ")) wt = 12;
            else if (c.contains("hóa đơn") || c.contains("hoa don")) wt = 13;
            else if (c.contains("số lượng") || c.contains("so luong")) wt = 13;
            else wt = 16;
            weights[i] = wt;
            totalWeight += wt;
        }
        int used = 0;
        for (int i = 0; i < n; i++) {
            widths[i] = Math.max(105, tableW * weights[i] / totalWeight);
            used += widths[i];
        }
        widths[n - 1] += tableW - used;
        return widths;
    }


   

    private static List<BufferedImage> renderSupplierImportPages(SupplierImportDoc doc) {
        List<BufferedImage> pages = new ArrayList<>();
        PageCanvas pc = newPage();

        drawHeroHeader(
                pc.g,
                "BILUXURY FASHION",
                "CHI TIẾT NHẬP HÀNG",
                doc.fieldOr("Mã NCC", "NCC"),
                "Supplier Import Report"
        );

        int y = 255;
        int gap = 18;
        int cardW = (IMG_W - MARGIN_LEFT - MARGIN_RIGHT - gap) / 2;

        drawInfoCard(pc.g, MARGIN_LEFT, y, cardW, 78, "Mã NCC", doc.fieldOr("Mã NCC", "-"));
        drawInfoCard(pc.g, MARGIN_LEFT + cardW + gap, y, cardW, 78, "Tên công ty", doc.fieldOr("Tên công ty", "-"));
        y += 96;

        drawInfoCard(pc.g, MARGIN_LEFT, y, cardW, 78, "Người liên hệ", doc.fieldOr("Người liên hệ", "-"));
        drawInfoCard(pc.g, MARGIN_LEFT + cardW + gap, y, cardW, 78, "Tháng xem", doc.fieldOr("Tháng xem", "Tất cả"));
        y += 112;

        int kpiW = (IMG_W - MARGIN_LEFT - MARGIN_RIGHT - gap * 2) / 3;
        drawKpiCard(pc.g, MARGIN_LEFT, y, kpiW, 100, "Số dòng nhập", doc.fieldOr("Số dòng nhập", String.valueOf(doc.rows.size())), false);
        drawKpiCard(pc.g, MARGIN_LEFT + kpiW + gap, y, kpiW, 100, "Tổng số lượng", doc.fieldOr("Tổng số lượng nhập", "0"), false);
        drawKpiCard(pc.g, MARGIN_LEFT + (kpiW + gap) * 2, y, kpiW, 100, "Ngày in", doc.fieldOr("Ngày in", "-"), true);
        y += 136;

        y = drawSectionTitle(pc.g, "Danh sách sản phẩm nhập", y);

        int[] widths = {90, 105, 100, 395, 76, 96, 126, 172};
        String[] headers = {"Mã NCC", "Liên hệ", "Mã SP", "Tên SP", "Size", "SL", "Giá nhập", "Ngày nhập"};
        y = drawTableHeader(pc.g, MARGIN_LEFT, y, widths, headers);

        int rowH = 54;
        int index = 0;

        if (doc.rows.isEmpty()) {
            String[] emptyRow = {doc.fieldOr("Mã NCC", "-"), "", "", "Không có dữ liệu nhập hàng", "", "", "", ""};
            drawTableRow(pc.g, MARGIN_LEFT, y, widths, emptyRow, false);
            y += rowH;
        } else {
            for (SupplierImportRow row : doc.rows) {
                if (y + rowH > IMG_H - MARGIN_BOTTOM - 40) {
                    pc.g.dispose();
                    pages.add(pc.page);

                    pc = newPage();
                    drawHeroHeader(
                            pc.g,
                            "BILUXURY FASHION",
                            "CHI TIẾT NHẬP HÀNG",
                            doc.fieldOr("Mã NCC", "NCC"),
                            "Supplier Import Report"
                    );

                    y = 260;
                    y = drawSectionTitle(pc.g, "Danh sách sản phẩm nhập tiếp theo", y);
                    y = drawTableHeader(pc.g, MARGIN_LEFT, y, widths, headers);
                }

                String[] values = {
                        row.maNCC,
                        row.nguoiLienHe,
                        row.maSP,
                        row.tenSP,
                        row.kichCo,
                        row.soLuongNhap,
                        row.giaNhap,
                        row.ngayNhap
                };

                drawTableRow(pc.g, MARGIN_LEFT, y, widths, values, index % 2 == 1);
                y += rowH;
                index++;
            }
        }

        pc.g.dispose();
        pages.add(pc.page);
        addPageNumbers(pages, pickFont("Segoe UI", Font.PLAIN, 16));
        return pages;
    }

   

    private static List<BufferedImage> renderLuxuryTextPages(String title, String content) {
        List<BufferedImage> pages = new ArrayList<>();
        PageCanvas pc = newPage();
        drawHeroHeader(pc.g, "BILUXURY FASHION", empty(title) ? "TÀI LIỆU XUẤT PDF" : title.trim(), "", "Premium Men's Wear");

        Font bodyFont = pickFont("Segoe UI", Font.PLAIN, 22);
        int y = 260;
        int usableWidth = IMG_W - MARGIN_LEFT - MARGIN_RIGHT;
        int lineHeight = 34;

        pc.g.setFont(bodyFont);
        pc.g.setColor(TEXT);
        FontMetrics fm = pc.g.getFontMetrics();
        String[] rawLines = normalize(content).split("\n", -1);

        for (String raw : rawLines) {
            List<String> lines = wrapLine(raw, fm, usableWidth);
            for (String line : lines) {
                if (y + lineHeight > IMG_H - MARGIN_BOTTOM) {
                    pc.g.dispose();
                    pages.add(pc.page);
                    pc = newPage();
                    drawHeroHeader(pc.g, "BILUXURY FASHION", empty(title) ? "TÀI LIỆU XUẤT PDF" : title.trim(), "", "Premium Men's Wear");
                    pc.g.setFont(bodyFont);
                    pc.g.setColor(TEXT);
                    fm = pc.g.getFontMetrics();
                    y = 260;
                }
                pc.g.drawString(line, MARGIN_LEFT, y);
                y += lineHeight;
            }
        }

        pc.g.dispose();
        pages.add(pc.page);
        addPageNumbers(pages, pickFont("Segoe UI", Font.PLAIN, 16));
        return pages;
    }




    private static SupplierImportDoc parseSupplierImport(String content) {
        SupplierImportDoc doc = new SupplierImportDoc();

        for (String raw : content.split("\n")) {
            String line = raw.trim();
            if (line.isEmpty() || line.equalsIgnoreCase("BILUXURY FASHION")
                    || line.equalsIgnoreCase("CHI TIẾT NHẬP HÀNG NHÀ CUNG CẤP")) {
                continue;
            }

            if (line.startsWith("NCC_ROW")) {
                String[] parts = line.split("\t", -1);
                if (parts.length >= 10) {
                    
                    doc.rows.add(new SupplierImportRow(
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim(),
                            parts[5].trim(),
                            parts[6].trim(),
                            formatVnd(parts[7].trim()),
                            parts[9].trim()
                    ));
                } else if (parts.length >= 9) {
                    
                    doc.rows.add(new SupplierImportRow(
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim(),
                            parts[5].trim(),
                            parts[6].trim(),
                            formatVnd(parts[7].trim()),
                            parts[8].trim()
                    ));
                }
                continue;
            }

            String[] kv = splitColon(line);
            if (kv != null) {
                doc.fields.put(cleanKey(kv[0]), kv[1].trim());
            }
        }

        if (!doc.fields.containsKey("Số dòng nhập")) {
            doc.fields.put("Số dòng nhập", String.valueOf(doc.rows.size()));
        }

        return doc;
    }

    private static InvoiceDoc parseInvoice(String content) {
        InvoiceDoc doc = new InvoiceDoc();
        Pattern itemPatternWithDiscount = Pattern.compile("^(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+([0-9.,]+)\\s+([0-9.,]+)\\s+([0-9.,]+)$");
        Pattern itemPattern = Pattern.compile("^(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+([0-9.,]+)\\s+([0-9.,]+)$");
        Pattern oldItemPattern = Pattern.compile("^(\\S+)\\s+(\\d+)\\s+([0-9.,]+)\\s+([0-9.,]+)$");
        for (String raw : content.split("\n")) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("=") || line.startsWith("-")
                    || line.contains("BILUXURY") || line.contains("HÓA ĐƠN")
                    || line.startsWith("Mã SP") || line.contains("Cảm ơn")) {
                continue;
            }

            Matcher withDiscount = itemPatternWithDiscount.matcher(line);
            if (withDiscount.matches()) {
                doc.items.add(new InvoiceItem(
                        withDiscount.group(1),
                        withDiscount.group(2),
                        withDiscount.group(3),
                        formatVnd(withDiscount.group(4)),
                        formatVnd(withDiscount.group(5)),
                        formatVnd(withDiscount.group(6))
                ));
                continue;
            }

            Matcher m = itemPattern.matcher(line);
            if (m.matches()) {
                doc.items.add(new InvoiceItem(m.group(1), m.group(2), m.group(3), formatVnd(m.group(4)), "0 VNĐ", formatVnd(m.group(5))));
                continue;
            }
            Matcher old = oldItemPattern.matcher(line);
            if (old.matches()) {
                doc.items.add(new InvoiceItem(old.group(1), "M", old.group(2), formatVnd(old.group(3)), "0 VNĐ", formatVnd(old.group(4))));
                continue;
            }

            String[] kv = splitColon(line);
            if (kv == null) continue;
            String key = cleanKey(kv[0]);
            String value = kv[1].trim();
            if (key.equalsIgnoreCase("Tong tien")) key = "Tổng tiền";
            if (key.equalsIgnoreCase("Giam gia")) key = "Giảm giá";
            if (key.equalsIgnoreCase("Diem su dung")) key = "Điểm sử dụng";
            if (key.equalsIgnoreCase("Tien doi diem")) key = "Tiền đổi điểm";
            if (key.equalsIgnoreCase("Thanh toan")) key = "Thanh toán";

            if (key.equals("Tổng tiền") || key.equals("Giảm giá") || key.equals("Điểm sử dụng")
                    || key.equals("Tiền đổi điểm") || key.equals("Thanh toán")) {
                doc.totals.put(key, value.trim());
            } else {
                doc.fields.put(key, value.trim());
            }
        }
        return doc;
    }

    private static RevenueDoc parseRevenue(String content) {
        RevenueDoc doc = new RevenueDoc();
        for (String raw : content.split("\n")) {
            String line = raw.trim();
            if (line.isEmpty() || line.equalsIgnoreCase("BÁO CÁO DOANH THU") || line.contains("BILUXURY -")) {
                continue;
            }

            if (line.contains("\t") && line.contains(":")) {
                Map<String, String> row = new LinkedHashMap<>();
                for (String part : line.split("\t")) {
                    String[] kv = splitColon(part.trim());
                    if (kv != null) {
                        String key = cleanKey(kv[0]);
                        if (!key.equalsIgnoreCase("Tổng giảm giá")) {
                            row.put(key, kv[1].trim());
                        }
                    }
                }
                if (!row.isEmpty()) doc.rows.add(row);
                continue;
            }

            String[] kv = splitColon(line);
            if (kv != null) {
                String key = cleanKey(kv[0]);
                if (!key.equalsIgnoreCase("Giờ cụ thể") && !key.equalsIgnoreCase("Tổng giảm giá")) {
                    doc.fields.put(key, kv[1].trim());
                }
            }
        }

        if (!doc.rows.isEmpty() && doc.fields.isEmpty()) {
            doc.fields.put("Ngày", "Tổng hợp");
            doc.fields.put("Số hóa đơn", String.valueOf(doc.rows.size()));
        }
        return doc;
    }

    private static String[] splitColon(String line) {
        int idx = line.indexOf(':');
        if (idx < 0) return null;
        return new String[]{line.substring(0, idx).trim(), line.substring(idx + 1).trim()};
    }

    private static String cleanKey(String key) {
        key = key == null ? "" : key.trim();
        while (key.endsWith(":")) key = key.substring(0, key.length() - 1).trim();
        return key;
    }

    private static String formatVnd(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.isEmpty()) return s;
        if (s.toUpperCase(Locale.ROOT).contains("VN")) return s;
        return s + " VNĐ";
    }

    

    private static PageCanvas newPage() {
        BufferedImage img = new BufferedImage(IMG_W, IMG_H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        setupGraphics(g);
        g.setColor(PAPER);
        g.fillRect(0, 0, IMG_W, IMG_H);

        g.setColor(new Color(255, 255, 255, 150));
        g.fillRoundRect(34, 34, IMG_W - 68, IMG_H - 68, 34, 34);
        g.setColor(new Color(230, 221, 204));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(34, 34, IMG_W - 68, IMG_H - 68, 34, 34);
        return new PageCanvas(img, g);
    }

    private static void drawHeroHeader(Graphics2D g, String brand, String title, String code, String subtitle) {
        int x = MARGIN_LEFT;
        int y = MARGIN_TOP;
        int w = IMG_W - MARGIN_LEFT - MARGIN_RIGHT;
        int h = 160;

        GradientPaint gp = new GradientPaint(x, y, NAVY_BLACK, x + w, y + h, NAVY_ROYAL);
        g.setPaint(gp);
        g.fillRoundRect(x, y, w, h, 34, 34);

        g.setColor(new Color(255, 255, 255, 22));
        g.fillOval(x + w - 220, y - 80, 300, 300);
        g.setColor(new Color(244, 210, 122, 34));
        g.fillOval(x + w - 315, y + 74, 160, 160);

        int badgeX = x + 26;
        int badgeY = y + 22;
        int badgeW = 112;
        int badgeH = 116;
        g.setColor(new Color(255, 255, 255, 240));
        g.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 30, 30);
        g.setColor(new Color(244, 210, 122, 140));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(badgeX, badgeY, badgeW, badgeH, 30, 30);

        BufferedImage realLogo = loadBiluxuryLogo();
        if (realLogo != null) {
            drawImageFit(g, realLogo, badgeX + 10, badgeY + 10, 92, 96);
        } else {
            drawBiluxuryMonogramLogo(g, badgeX + 10, badgeY + 4, 92, 104);
        }

        g.setFont(pickFont("Segoe UI", Font.BOLD, 27));
        g.setColor(WHITE);
        g.drawString(brand, x + 160, y + 56);

        g.setFont(pickFont("Segoe UI", Font.PLAIN, 17));
        g.setColor(new Color(221, 234, 255));
        g.drawString(subtitle == null ? "" : subtitle, x + 162, y + 84);

        g.setColor(GOLD_LIGHT);
        g.fillRoundRect(x + 162, y + 101, 218, 5, 5, 5);

        g.setFont(pickFont("Segoe UI", Font.BOLD, 32));
        g.setColor(GOLD_LIGHT);
        int titleX = x + w - 44 - g.getFontMetrics().stringWidth(title);
        g.drawString(title, titleX, y + 68);

        if (!empty(code)) {
            g.setFont(pickFont("Segoe UI", Font.BOLD, 18));
            g.setColor(new Color(255, 255, 255, 235));
            String t = code;
            int pillW = Math.max(170, g.getFontMetrics().stringWidth(t) + 42);
            int pillX = x + w - 44 - pillW;
            int pillY = y + 94;
            g.setColor(new Color(255, 255, 255, 30));
            g.fillRoundRect(pillX, pillY, pillW, 42, 18, 18);
            g.setColor(new Color(244, 210, 122, 140));
            g.drawRoundRect(pillX, pillY, pillW, 42, 18, 18);
            g.setColor(WHITE);
            drawCentered(g, t, pillX + pillW / 2, pillY + 27);
        }
    }


    private static BufferedImage loadBiluxuryLogo() {
        String[] paths = {
                "src/image/logo.png",
                "image/logo.png",
                "kkk/src/image/logo.png",
                "kk/src/image/logo.png"
        };

        try {
            java.net.URL url = PdfExporter.class.getResource("/image/logo.png");
            if (url != null) {
                return ImageIO.read(url);
            }
        } catch (Exception ignored) {
        }

        File current = new File(System.getProperty("user.dir"));
        for (int level = 0; level < 8 && current != null; level++) {
            for (String path : paths) {
                File f = new File(current, path);
                if (f.exists() && f.isFile()) {
                    try {
                        return ImageIO.read(f);
                    } catch (Exception ignored) {
                    }
                }
            }
            current = current.getParentFile();
        }

        return null;
    }

    private static void drawImageFit(Graphics2D g, BufferedImage img, int x, int y, int w, int h) {
        if (img == null) {
            return;
        }

        double scale = Math.min(w * 1.0 / img.getWidth(), h * 1.0 / img.getHeight());
        int drawW = Math.max(1, (int) Math.round(img.getWidth() * scale));
        int drawH = Math.max(1, (int) Math.round(img.getHeight() * scale));
        int drawX = x + (w - drawW) / 2;
        int drawY = y + (h - drawH) / 2;

        g.drawImage(img, drawX, drawY, drawW, drawH, null);
    }

    private static void drawBiluxuryMonogramLogo(Graphics2D g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        setupGraphics(g2);

        
        int crownBaseY = y + 18;
        Path2D crown = new Path2D.Double();
        crown.moveTo(x + 18, crownBaseY + 18);
        crown.lineTo(x + 28, crownBaseY - 1);
        crown.lineTo(x + 38, crownBaseY + 12);
        crown.lineTo(x + 46, crownBaseY - 12);
        crown.lineTo(x + 56, crownBaseY + 10);
        crown.lineTo(x + 66, crownBaseY + 0);
        crown.lineTo(x + 76, crownBaseY + 18);
        crown.closePath();
        GradientPaint crownGp = new GradientPaint(x, crownBaseY - 12, GOLD_LIGHT, x, crownBaseY + 24, new Color(167, 116, 28));
        g2.setPaint(crownGp);
        g2.fill(crown);
        g2.setColor(new Color(96, 62, 16));
        g2.setStroke(new BasicStroke(2f));
        g2.draw(crown);
        g2.fillOval(x + 42, crownBaseY - 18, 8, 8);
        g2.fillOval(x + 24, crownBaseY - 4, 7, 7);
        g2.fillOval(x + 62, crownBaseY - 4, 7, 7);

        Font bFont = new Font("Times New Roman", Font.BOLD, 98);
        Font lFont = new Font("Times New Roman", Font.BOLD, 102);
        if ("Dialog".equalsIgnoreCase(bFont.getFamily())) {
            bFont = new Font(Font.SERIF, Font.BOLD, 98);
            lFont = new Font(Font.SERIF, Font.BOLD, 102);
        }

        Shape bShape = createGlyphShape(g2, bFont, "B", x + 16, y + 96);
        Shape lShape = createGlyphShape(g2, lFont, "L", x + 3, y + 104);

        fillLogoShape(g2, bShape, new Color(16, 16, 18));
        fillLogoShape(g2, lShape, new Color(220, 185, 103));

        
        Path2D sweep = new Path2D.Double();
        sweep.moveTo(x + 14, y + 86);
        sweep.curveTo(x + 26, y + 94, x + 42, y + 95, x + 58, y + 92);
        sweep.curveTo(x + 46, y + 98, x + 34, y + 112, x + 18, y + 120);
        g2.setStroke(new BasicStroke(4.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setPaint(new GradientPaint(x, y + 82, GOLD_LIGHT, x + 55, y + 125, new Color(160, 108, 26)));
        g2.draw(sweep);

        g2.dispose();
    }

    private static Shape createGlyphShape(Graphics2D g, Font font, String text, int x, int y) {
        GlyphVector gv = font.createGlyphVector(g.getFontRenderContext(), text);
        return gv.getOutline(x, y);
    }

    private static void fillLogoShape(Graphics2D g, Shape shape, Color darkFill) {
        GradientPaint goldStroke = new GradientPaint(0, (float) shape.getBounds2D().getY(), GOLD_LIGHT,
                0, (float) shape.getBounds2D().getMaxY(), new Color(145, 94, 23));
        g.setStroke(new BasicStroke(8.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setPaint(goldStroke);
        g.draw(shape);
        g.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(255, 235, 172, 180));
        g.draw(shape);
        g.setColor(darkFill);
        g.fill(shape);
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(110, 70, 18, 160));
        g.draw(shape);
    }

    private static int drawSectionTitle(Graphics2D g, String title, int y) {
        g.setFont(pickFont("Segoe UI", Font.BOLD, 23));
        g.setColor(NAVY_DARK);
        g.drawString(title, MARGIN_LEFT, y);
        g.setColor(GOLD);
        g.fillRoundRect(MARGIN_LEFT, y + 14, 130, 5, 5, 5);
        return y + 36;
    }

    private static void drawInfoCard(Graphics2D g, int x, int y, int w, int h, String label, String value) {
        fillRound(g, x, y, w, h, 20, WHITE, BORDER);
        g.setFont(pickFont("Segoe UI", Font.PLAIN, 15));
        g.setColor(TEXT_MUTED);
        g.drawString(label, x + 20, y + 27);
        g.setFont(pickFont("Segoe UI", Font.BOLD, 21));
        g.setColor(NAVY_DARK);
        drawStringFit(g, emptyToDash(value), x + 20, y + 58, w - 40);
    }

    private static void drawNoteCard(Graphics2D g, int x, int y, int w, int h, String label, String value) {
        fillRound(g, x, y, w, h, 18, new Color(255, 252, 244), new Color(229, 205, 159));
        g.setFont(pickFont("Segoe UI", Font.BOLD, 15));
        g.setColor(new Color(138, 100, 30));
        g.drawString(label, x + 18, y + 27);
        g.setFont(pickFont("Segoe UI", Font.PLAIN, 18));
        g.setColor(TEXT);
        drawStringFit(g, value, x + 18, y + 56, w - 36);
    }

    private static void drawKpiCard(Graphics2D g, int x, int y, int w, int h, String label, String value, boolean primary) {
        Color bg = primary ? NAVY_DARK : WHITE;
        Color border = primary ? GOLD : BORDER;
        fillRound(g, x, y, w, h, 24, bg, border);
        g.setFont(pickFont("Segoe UI", Font.PLAIN, 15));
        g.setColor(primary ? new Color(222, 232, 255) : TEXT_MUTED);
        g.drawString(label, x + 18, y + 31);
        g.setFont(pickFont("Segoe UI", Font.BOLD, 22));
        g.setColor(primary ? GOLD_LIGHT : NAVY_DARK);
        drawStringFit(g, emptyToDash(value), x + 18, y + 74, w - 36);
    }

    private static int drawTableHeader(Graphics2D g, int x, int y, int[] widths, String[] headers) {
        int h = 50;
        int totalW = sum(widths);
        g.setColor(NAVY_DARK);
        g.fillRoundRect(x, y, totalW, h, 18, 18);
        g.fillRect(x, y + 20, totalW, h - 20);

        g.setFont(pickFont("Segoe UI", Font.BOLD, 17));
        g.setColor(GOLD_LIGHT);
        int cx = x;
        for (int i = 0; i < widths.length; i++) {
            drawStringFit(g, headers[i], cx + 16, y + 32, widths[i] - 28);
            if (i < widths.length - 1) {
                g.setColor(new Color(255, 255, 255, 60));
                g.drawLine(cx + widths[i], y + 10, cx + widths[i], y + h - 10);
                g.setColor(GOLD_LIGHT);
            }
            cx += widths[i];
        }
        return y + h;
    }

    private static void drawTableRow(Graphics2D g, int x, int y, int[] widths, String[] values, boolean alt) {
        int h = 52;
        int totalW = sum(widths);
        g.setColor(alt ? ROW_ALT : WHITE);
        g.fillRect(x, y, totalW, h);
        g.setColor(new Color(226, 226, 226));
        g.drawLine(x, y + h, x + totalW, y + h);

        g.setFont(pickFont("Segoe UI", Font.PLAIN, 17));
        int cx = x;
        for (int i = 0; i < widths.length && i < values.length; i++) {
            String v = emptyToDash(values[i]);
            if (i == 0) {
                g.setColor(NAVY_DARK);
                g.setFont(pickFont("Segoe UI", Font.BOLD, 17));
            } else {
                g.setColor(TEXT);
                g.setFont(pickFont("Segoe UI", Font.PLAIN, 17));
            }
            drawStringFit(g, v, cx + 16, y + 33, widths[i] - 30);
            cx += widths[i];
        }
    }

    private static void drawTotalLine(Graphics2D g, int x, int baselineY, int w, String label, String value, boolean strong) {
        g.setFont(pickFont("Segoe UI", strong ? Font.BOLD : Font.PLAIN, strong ? 22 : 18));
        g.setColor(strong ? NAVY_DARK : TEXT_MUTED);
        g.drawString(label, x, baselineY);

        g.setFont(pickFont("Segoe UI", Font.BOLD, strong ? 24 : 19));
        g.setColor(strong ? new Color(138, 100, 30) : TEXT);
        String v = emptyToDash(value);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(v, x + w - fm.stringWidth(v), baselineY);
    }

    private static void drawKeyValueLine(Graphics2D g, int x, int y, int w, String key, String value) {
        fillRound(g, x, y, w, 48, 16, WHITE, BORDER);
        g.setFont(pickFont("Segoe UI", Font.BOLD, 17));
        g.setColor(NAVY_DARK);
        g.drawString(key, x + 18, y + 31);
        g.setFont(pickFont("Segoe UI", Font.PLAIN, 17));
        g.setColor(TEXT);
        String v = emptyToDash(value);
        FontMetrics fm = g.getFontMetrics();
        drawStringFit(g, v, x + w - 18 - Math.min(fm.stringWidth(v), w / 2), y + 31, w / 2);
    }

    private static void drawThankYou(Graphics2D g, int x, int y, int w) {
        fillRound(g, x, y, w, 86, 24, new Color(255, 251, 242), new Color(226, 207, 168));
        g.setFont(pickFont("Segoe UI", Font.BOLD, 21));
        g.setColor(NAVY_DARK);
        drawCentered(g, "Cảm ơn quý khách đã mua hàng tại Biluxury!", x + w / 2, y + 37);
        g.setFont(pickFont("Segoe UI", Font.PLAIN, 15));
        g.setColor(TEXT_MUTED);
        drawCentered(g, "Hóa đơn được xuất tự động từ hệ thống quản lý cửa hàng", x + w / 2, y + 64);
    }

    private static void fillRound(Graphics2D g, int x, int y, int w, int h, int arc, Color fill, Color border) {
        g.setColor(new Color(0, 0, 0, 18));
        g.fillRoundRect(x + 3, y + 5, w, h, arc, arc);
        g.setColor(fill);
        g.fillRoundRect(x, y, w, h, arc, arc);
        g.setColor(border);
        g.setStroke(new BasicStroke(1.4f));
        g.drawRoundRect(x, y, w, h, arc, arc);
    }

    private static void addPageNumbers(List<BufferedImage> pages, Font footerFont) {
        int total = pages.size();
        for (int i = 0; i < total; i++) {
            BufferedImage page = pages.get(i);
            Graphics2D g = page.createGraphics();
            setupGraphics(g);
            g.setFont(footerFont);
            g.setColor(TEXT_MUTED);
            String text = "Trang " + (i + 1) + " / " + total;
            drawCentered(g, text, IMG_W / 2, IMG_H - 42);
            g.dispose();
        }
    }

    private static void drawCentered(Graphics2D g, String text, int centerX, int baselineY) {
        FontMetrics fm = g.getFontMetrics();
        int x = centerX - fm.stringWidth(text) / 2;
        g.drawString(text, x, baselineY);
    }

    private static void drawStringFit(Graphics2D g, String text, int x, int baselineY, int maxWidth) {
        if (text == null) text = "";
        Font original = g.getFont();
        FontMetrics fm = g.getFontMetrics();
        if (fm.stringWidth(text) <= maxWidth) {
            g.drawString(text, x, baselineY);
            return;
        }

        int style = original.getStyle();
        String family = original.getFamily();
        int size = original.getSize();
        while (size > 9) {
            size--;
            Font smaller = new Font(family, style, size);
            g.setFont(smaller);
            fm = g.getFontMetrics();
            if (fm.stringWidth(text) <= maxWidth) {
                g.drawString(text, x, baselineY);
                g.setFont(original);
                return;
            }
        }

        
        g.drawString(text, x, baselineY);
        g.setFont(original);
    }

    private static int sum(int[] values) {
        int s = 0;
        for (int v : values) s += v;
        return s;
    }

    private static void setupGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private static Font pickFont(String name, int style, int size) {
        Font f = new Font(name, style, size);
        if (f.getFamily() == null || f.getFamily().equalsIgnoreCase("Dialog")) {
            return new Font(Font.SANS_SERIF, style, size);
        }
        return f;
    }

    private static String normalize(String content) {
        if (content == null) return "";
        return content.replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace("\t", "\t");
    }

    private static List<String> wrapLine(String line, FontMetrics fm, int maxWidth) {
        List<String> result = new ArrayList<>();
        if (line == null || line.isEmpty()) {
            result.add("");
            return result;
        }
        String current = line;
        while (fm.stringWidth(current) > maxWidth) {
            int cut = findCutIndex(current, fm, maxWidth);
            if (cut <= 0) cut = Math.min(current.length(), 80);
            result.add(current.substring(0, cut));
            current = current.substring(cut).stripLeading();
            if (current.isEmpty()) break;
        }
        if (!current.isEmpty()) result.add(current);
        return result;
    }

    private static int findCutIndex(String s, FontMetrics fm, int maxWidth) {
        int cut = 0;
        for (int i = 1; i <= s.length(); i++) {
            if (fm.stringWidth(s.substring(0, i)) > maxWidth) break;
            cut = i;
        }
        int space = s.lastIndexOf(' ', cut);
        if (space > 20) return space;
        return cut;
    }

    private static boolean empty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String emptyToDash(String s) {
        return empty(s) ? "-" : s.trim();
    }

    // =========================================================
    // PDF WRITER
    // =========================================================

    private static byte[] toJpegBytes(BufferedImage img) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            ImageIO.write(img, "jpg", baos);
            return baos.toByteArray();
        }
        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.94f);
        }
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(img, null, null), param);
        } finally {
            writer.dispose();
        }
        return baos.toByteArray();
    }

    private static void writeImagePdf(File out, byte[][] imageBytes) throws IOException {
        int pageCount = imageBytes.length;
        int objCount = 2 + pageCount * 3;
        int[] pageObj = new int[pageCount];
        int[] imageObj = new int[pageCount];
        int[] contentObj = new int[pageCount];
        for (int i = 0; i < pageCount; i++) {
            pageObj[i] = 3 + i * 3;
            imageObj[i] = 4 + i * 3;
            contentObj[i] = 5 + i * 3;
        }

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        long[] offsets = new long[objCount + 1];
        writeAscii(pdf, "%PDF-1.4\n%âãÏÓ\n");

        offsets[1] = pdf.size();
        writeAscii(pdf, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        StringBuilder kids = new StringBuilder();
        for (int id : pageObj) kids.append(id).append(" 0 R ");
        offsets[2] = pdf.size();
        writeAscii(pdf, "2 0 obj\n<< /Type /Pages /Kids [ ");
        writeAscii(pdf, kids.toString());
        writeAscii(pdf, "] /Count " + pageCount + " >>\nendobj\n");

        for (int i = 0; i < pageCount; i++) {
            String imageName = "Im" + (i + 1);
            String stream = "q\n" + PDF_W + " 0 0 " + PDF_H + " 0 0 cm\n/" + imageName + " Do\nQ\n";
            byte[] streamBytes = stream.getBytes(StandardCharsets.US_ASCII);

            offsets[pageObj[i]] = pdf.size();
            writeAscii(pdf, pageObj[i] + " 0 obj\n");
            writeAscii(pdf, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + PDF_W + " " + PDF_H + "] ");
            writeAscii(pdf, "/Resources << /XObject << /" + imageName + " " + imageObj[i] + " 0 R >> >> ");
            writeAscii(pdf, "/Contents " + contentObj[i] + " 0 R >>\nendobj\n");

            offsets[imageObj[i]] = pdf.size();
            writeAscii(pdf, imageObj[i] + " 0 obj\n");
            writeAscii(pdf, "<< /Type /XObject /Subtype /Image /Width " + IMG_W + " /Height " + IMG_H);
            writeAscii(pdf, " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length " + imageBytes[i].length + " >>\nstream\n");
            pdf.write(imageBytes[i]);
            writeAscii(pdf, "\nendstream\nendobj\n");

            offsets[contentObj[i]] = pdf.size();
            writeAscii(pdf, contentObj[i] + " 0 obj\n");
            writeAscii(pdf, "<< /Length " + streamBytes.length + " >>\nstream\n");
            pdf.write(streamBytes);
            writeAscii(pdf, "endstream\nendobj\n");
        }

        int xref = pdf.size();
        writeAscii(pdf, "xref\n0 " + (objCount + 1) + "\n");
        writeAscii(pdf, "0000000000 65535 f \n");
        for (int i = 1; i <= objCount; i++) {
            writeAscii(pdf, String.format(Locale.US, "%010d 00000 n \n", offsets[i]));
        }
        writeAscii(pdf, "trailer\n<< /Size " + (objCount + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n");

        try (OutputStream fos = new FileOutputStream(out)) {
            pdf.writeTo(fos);
        }
    }

    private static void writeAscii(ByteArrayOutputStream out, String text) throws IOException {
        out.write(text.getBytes(StandardCharsets.ISO_8859_1));
    }

    private static class PageCanvas {
        final BufferedImage page;
        final Graphics2D g;
        PageCanvas(BufferedImage page, Graphics2D g) {
            this.page = page;
            this.g = g;
        }
    }

    private static class InvoiceDoc {
        final Map<String, String> fields = new LinkedHashMap<>();
        final Map<String, String> totals = new LinkedHashMap<>();
        final List<InvoiceItem> items = new ArrayList<>();
        String get(String key) { return fields.getOrDefault(key, ""); }
        String total(String key) { return totals.getOrDefault(key, "0 VNĐ"); }
    }

    private static class InvoiceItem {
        final String maSP, kichCo, soLuong, donGia, giamGia, thanhTien;
        InvoiceItem(String maSP, String kichCo, String soLuong, String donGia, String giamGia, String thanhTien) {
            this.maSP = maSP;
            this.kichCo = kichCo;
            this.soLuong = soLuong;
            this.donGia = donGia;
            this.giamGia = giamGia;
            this.thanhTien = thanhTien;
        }
    }


    private static class SupplierImportDoc {
        final Map<String, String> fields = new LinkedHashMap<>();
        final List<SupplierImportRow> rows = new ArrayList<>();

        String fieldOr(String key, String fallback) {
            return fields.getOrDefault(key, fallback);
        }
    }

    private static class SupplierImportRow {
        final String maNCC;
        final String nguoiLienHe;
        final String maSP;
        final String tenSP;
        final String kichCo;
        final String soLuongNhap;
        final String giaNhap;
        final String ngayNhap;

        SupplierImportRow(String maNCC, String nguoiLienHe, String maSP, String tenSP,
                          String kichCo, String soLuongNhap, String giaNhap, String ngayNhap) {
            this.maNCC = maNCC;
            this.nguoiLienHe = nguoiLienHe;
            this.maSP = maSP;
            this.tenSP = tenSP;
            this.kichCo = kichCo;
            this.soLuongNhap = soLuongNhap;
            this.giaNhap = giaNhap;
            this.ngayNhap = ngayNhap;
        }
    }

    private static class RevenueDoc {
        final Map<String, String> fields = new LinkedHashMap<>();
        final List<Map<String, String>> rows = new ArrayList<>();
        String fieldOr(String key, String fallback) { return fields.getOrDefault(key, fallback); }
    }
}
