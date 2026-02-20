package com.krish.AgariBackend.service;

import com.krish.AgariBackend.entity.Order;
import com.krish.AgariBackend.entity.OrderItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
public class InvoiceService {

    public byte[] generateInvoicePdf(Order order) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();

            // Fonts
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);

            // Title
            Paragraph title = new Paragraph("AgriMart - Order Invoice", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Order Details
            document.add(new Paragraph("Order ID: " + order.getId(), bodyFont));
            document.add(new Paragraph("Date: " + order.getOrderDate(), bodyFont));
            document.add(new Paragraph(
                    "Customer: " + (order.getUser() != null ? order.getUser().getUsername() : "Guest"), bodyFont));
            document.add(new Paragraph("Email: " + (order.getUser() != null ? order.getUser().getEmail() : "N/A"),
                    bodyFont));
            document.add(new Paragraph("Address: " + order.getAddress() + ", " + order.getCity(), bodyFont));
            document.add(new Paragraph("\n"));

            // Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 4, 2, 2, 2 });

            // Table Header
            addTableHeader(table, "Product", tableHeaderFont);
            addTableHeader(table, "Quantity", tableHeaderFont);
            addTableHeader(table, "Price", tableHeaderFont);
            addTableHeader(table, "Total", tableHeaderFont);

            // Table Rows
            for (OrderItem item : order.getItems()) {
                table.addCell(new Phrase(item.getProduct().getName(), bodyFont));
                table.addCell(new Phrase(String.valueOf(item.getQuantity()), bodyFont));
                table.addCell(new Phrase(String.valueOf(item.getPrice()), bodyFont));
                table.addCell(new Phrase(String.valueOf(item.getTotalPrice()), bodyFont));
            }

            document.add(table);

            // Total Amount
            Paragraph total = new Paragraph("\nTotal Amount: " + order.getTotalPrice(), headerFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addTableHeader(PdfPTable table, String headerTitle, Font font) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPhrase(new Phrase(headerTitle, font));
        table.addCell(header);
    }
}
