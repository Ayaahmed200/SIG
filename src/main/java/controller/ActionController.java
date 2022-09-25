/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import model.InvoiceHeader;
import model.InvoiceHeaderTableModel;
import model.InvoiceLine;
import model.InvoiceLineTableModel;
import view.CreateNewInvoice;
import view.CreateNewLine;
import view.SIGFrame;

/**
 *
 * @author aahmed200
 */
public class ActionController {

    private InvoiceHeaderTableModel store_invoice_header_table_model;
    private InvoiceLineTableModel store_invoice_line_table_model;
    public static SimpleDateFormat date_formatter = new SimpleDateFormat("dd-MM-yyyy");
    private LoaderController loader;
    private int max_id = 0;

    private SIGFrame frame;

    int selected_invoice_index = 0;
    int selected_line_index = 0;

    public ActionController() {
    }

    public ActionController(LoaderController loader, SIGFrame frame) {
        this.loader = loader;
        this.frame = frame;
        this.fillTables("InvoiceHeader.csv", "InvoiceLine.csv");
    }

    public void fillTables(String header_file_path, String line_file_path) {
        try {
            loader.loadData(header_file_path, line_file_path);

            store_invoice_header_table_model = loader.getInvoiceHeaderModel();
            store_invoice_line_table_model = loader.getInvoiceLineModel();
            this.refreshTables();
            this.refreshData();

            max_id = loader.max_id;
            this.autoSelectFirstInvoice();
        } catch (Exception e) {
            System.out.println("Error loading files");
        }
    }

    public void showCreateInvoice(ActionEvent e) {
        CreateNewInvoice create_invoice_form = new CreateNewInvoice(this);
        create_invoice_form.setVisible(true);
    }

    public void showCreateLine(ActionEvent e) {
        CreateNewLine create_Line_form = new CreateNewLine(this);
        create_Line_form.setVisible(true);
    }

    public void CancelCreateInvoice(ActionEvent evt, CreateNewInvoice create_invoice_frame) {
        create_invoice_frame.dispose();
    }

    public void CancelCreateLine(ActionEvent evt, CreateNewLine create_line_frame) {
        create_line_frame.dispose();
    }

    public void clickInvoiceTable(MouseEvent evt) {
        JTable invoices_table = (JTable) evt.getSource();
        Point point = evt.getPoint();
        int row = invoices_table.rowAtPoint(point);
        InvoiceHeaderTableModel invoices_table_model = (InvoiceHeaderTableModel) invoices_table.getModel();
        InvoiceHeader invoice_header = invoices_table_model.getInvoice_headers().get(row);
        ArrayList<InvoiceLine> invoice_lines = invoice_header.getLines();
        selected_invoice_index = row;

        store_invoice_line_table_model.setInvoice_lines(invoice_lines);
        //refresh line table only to keep highlight
        store_invoice_line_table_model.fireTableDataChanged();
        frame.getInvoiceLineTable().setModel(store_invoice_line_table_model);
        this.refreshData();
    }

    public void clickLineTable(MouseEvent evt) {
        JTable lines_table = (JTable) evt.getSource();
        Point point = evt.getPoint();
        int row = lines_table.rowAtPoint(point);
        selected_line_index = row;
    }

    public void OkCreateInvoice(ActionEvent evt, CreateNewInvoice create_invoice_frame, String customer_name, String invoice_date) {
        try {
            if (customer_name.isBlank()) {
                JOptionPane.showMessageDialog(this.frame, "All fields are required", "Wrong data format", JOptionPane.ERROR_MESSAGE);
            } else {
                Date date = date_formatter.parse(invoice_date);
                InvoiceHeader new_invoice = new InvoiceHeader(++max_id, date, customer_name);
                store_invoice_header_table_model.addInvoiceHeader(new_invoice);
                this.refreshTables();
                create_invoice_frame.dispose();

                if (store_invoice_header_table_model.getInvoice_headers().size() == 1) {
                    this.autoSelectFirstInvoice();
                }
            }
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this.frame, "Wrong date format, should be dd-MM-yyyy ex: 19-04-1963", "Wrong date format", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void OkCreateLine(ActionEvent evt, CreateNewLine create_new_line_frame, String item_name, String count_item, String price_item) {
        try {
            if (store_invoice_header_table_model.getInvoice_headers().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Can't add a new line when there are no headers first", "Can't create item", JOptionPane.ERROR_MESSAGE);
            } else if (count_item.isBlank() || price_item.isBlank() || item_name.isBlank()) {
                JOptionPane.showMessageDialog(this.frame, "All fields are required", "Wrong data format", JOptionPane.ERROR_MESSAGE);
            } else {
                InvoiceHeader selected_invoice = store_invoice_header_table_model.getInvoice_headers().get(selected_invoice_index);
                InvoiceLine new_line = new InvoiceLine(item_name, Integer.parseInt(count_item), Double.parseDouble(price_item), selected_invoice);

                store_invoice_line_table_model.addInvoiceLine(new_line);
                this.refreshTables();

                create_new_line_frame.dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this.frame, "Wrong count and/or price format, must be a number/floating point number", "Wrong data format", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteInvoice(ActionEvent evt) {
        if (store_invoice_header_table_model.getInvoice_headers().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Can't delete invoice header when table is empty", "Can't delete item", JOptionPane.ERROR_MESSAGE);
        } else {
            store_invoice_header_table_model.removeInvoiceHeader(selected_invoice_index);
            this.autoSelectFirstInvoice();
        }
    }

    public void deleteInvoiceLine(ActionEvent evt) {
        if (store_invoice_line_table_model.getInvoice_lines().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Can't delete invoice line when table is empty", "Can't delete item", JOptionPane.ERROR_MESSAGE);
        } else {
            store_invoice_line_table_model.removeInvoiceLine(selected_line_index);
            this.refreshTables();
            this.refreshData();
        }
    }

    public void save() {
        ArrayList<InvoiceHeader> invoice_headers = store_invoice_header_table_model.getInvoice_headers();
        ArrayList<InvoiceLine> invoice_lines = new ArrayList<>();
        for (InvoiceHeader invoice_header : invoice_headers) {
            for (InvoiceLine invoice_header_line : invoice_header.getLines()) {
                invoice_lines.add(invoice_header_line);
            }
        }
        String invoice_headers_data = InvoiceHeader.toCSV(invoice_headers);
        String invoice_lines_data = InvoiceLine.toCSV(invoice_lines);

        if (invoice_headers.isEmpty() || invoice_lines.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invoice header and/or lines tables are empty", "Can't export files", JOptionPane.ERROR_MESSAGE);
        } else {
            System.out.println("-----------Logging exported data start-----------");
            System.out.println(invoice_headers_data);
            System.out.println(invoice_lines_data);
            System.out.println("-----------Logging exported data end-----------");
            JFileChooser fc = new JFileChooser();
            int result = fc.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File header_file = fc.getSelectedFile();
                result = fc.showSaveDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File line_file = fc.getSelectedFile();

                    if (header_file.getName().equals(line_file.getName())) {
                        JOptionPane.showMessageDialog(frame, "Files names can't be the same", "Wrong file format", JOptionPane.ERROR_MESSAGE);
                    } else {
                        try {
                            if (!".csv".equals((header_file.getPath()).substring((header_file.getPath()).lastIndexOf(".")))) {
                                throw new Exception();
                            }
                            if (!".csv".equals((line_file.getPath()).substring((line_file.getPath()).lastIndexOf(".")))) {
                                throw new Exception();
                            }
                            FileWriter invoice_header_file_writer = new FileWriter(header_file);
                            invoice_header_file_writer.write(invoice_headers_data);
                            invoice_header_file_writer.flush();
                            invoice_header_file_writer.close();

                            FileWriter invoice_line_file_writer = new FileWriter(line_file);
                            invoice_line_file_writer.write(invoice_lines_data);
                            invoice_line_file_writer.flush();
                            invoice_line_file_writer.close();
                        } catch (IOException io) {
                            JOptionPane.showMessageDialog(frame, "File/Directory not found, make sure file name/format/location are in order", "Not found", JOptionPane.ERROR_MESSAGE);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(frame, "File should be of type .csv", "Wrong file format", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }
    }

    private void autoSelectFirstInvoice() {
        ArrayList<InvoiceHeader> invoice_headers = store_invoice_header_table_model.getInvoice_headers();
        ArrayList<InvoiceLine> invoice_lines = new ArrayList<>();
        if (invoice_headers.size() > 0) {
            InvoiceHeader invoice_header = invoice_headers.get(0);
            invoice_lines = invoice_header.getLines();
        }
        selected_invoice_index = 0;

        store_invoice_line_table_model.setInvoice_lines(invoice_lines);
        this.refreshTables();
        this.refreshData();
    }

    private void refreshTables() {
        store_invoice_header_table_model.fireTableDataChanged();
        frame.getInvoiceHeaderTable().setModel(store_invoice_header_table_model);
        store_invoice_line_table_model.fireTableDataChanged();
        frame.getInvoiceLineTable().setModel(store_invoice_line_table_model);
    }

    private void refreshData() {
        ArrayList<InvoiceHeader> invoice_headers = store_invoice_header_table_model.getInvoice_headers();
        if (invoice_headers.size() > 0) {
            InvoiceHeader invoice_header = invoice_headers.get(selected_invoice_index);

            frame.getInvoiceNumberLabel().setText(String.valueOf(invoice_header.getNum()));
            frame.getInvoiceDateLabel().setText(ActionController.date_formatter.format(invoice_header.getDate()));
            frame.getInvoiceCustomerNameLabel().setText(invoice_header.getName());
            frame.getInvoiceTotalLabel().setText(String.valueOf(invoice_header.getTotalInvoices()));
        } else {
            frame.getInvoiceNumberLabel().setText("");
            frame.getInvoiceDateLabel().setText("");
            frame.getInvoiceCustomerNameLabel().setText("");
            frame.getInvoiceTotalLabel().setText("");
        }
    }
}
