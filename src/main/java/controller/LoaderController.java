/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import model.InvoiceHeader;
import model.InvoiceHeaderTableModel;
import model.InvoiceLine;
import model.InvoiceLineTableModel;
import view.SIGFrame;

/**
 *
 * @author aahmed200
 */
public class LoaderController {

    JFileChooser invoice_loader = new JFileChooser();
    JFileChooser invoice_lines_loader = new JFileChooser();

    int invoices_col_count = 3;
    int lines_col_count = 4;
    int max_id = 0;

    ArrayList<InvoiceHeader> invoices;
    ArrayList<InvoiceLine> invoices_lines;

    SIGFrame frame;

    public LoaderController(SIGFrame frame) {
        this.frame = frame;
        invoices = new ArrayList<>();
        invoices_lines = new ArrayList<>();
    }

    public void loadInvoicesData(String header_file_name) throws Exception, ParseException, IOException {
        File header_file = null;
        if (header_file_name != null) {
            header_file = new File(header_file_name);
        } else {
            int result = invoice_loader.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                header_file = invoice_loader.getSelectedFile();
            }
        }

        if (!".csv".equals((header_file.toString()).substring((header_file.toString()).lastIndexOf(".")))) {
            throw new Exception();
        } else {
            FileInputStream fls = null;
            List<String> lines = Files.lines(Paths.get(header_file.getAbsolutePath())).collect(Collectors.toList());
            String[][] data = new String[lines.size()][invoices_col_count];

            for (int i = 0; i < lines.size(); i++) {
                data[i] = lines.get(i).split(",");
            }

            for (int i = 0; i < data.length; i++) {
                invoices.add(new InvoiceHeader(
                        Integer.parseInt(data[i][0]),
                        ActionController.date_formatter.parse(data[i][1]),
                        data[i][2]
                ));
            }
            max_id = Integer.parseInt(data[lines.size() - 1][0]);
            if (fls != null) {
                fls.close();
            }
        }
    }

    public void loadInvoicesLinesData(String lines_file_name) throws Exception, IOException {
        File lines_file = null;
        if (lines_file_name != null) {
            lines_file = new File(lines_file_name);
        } else {
            int result = invoice_loader.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                lines_file = invoice_loader.getSelectedFile();
            }
        }

        if (!".csv".equals((lines_file.toString()).substring((lines_file.toString()).lastIndexOf(".")))) {
            throw new Exception();
        } else {
            FileInputStream fls = null;
            List<String> lines = Files.lines(Paths.get(lines_file.getAbsolutePath())).collect(Collectors.toList());
            String[][] data = new String[lines.size()][lines_col_count];
            for (int i = 0; i < lines.size(); i++) {
                data[i] = lines.get(i).split(",");
            }

            for (int i = 0; i < data.length; i++) {
                InvoiceHeader invoice = null;
                for (int j = 0; j < invoices.size() && invoice == null; j++) {
                    if (invoices.get(j).getNum() == Integer.parseInt(data[i][0])) {
                        invoice = invoices.get(j);
                    }
                }
                if (invoice != null) {
                    invoices_lines.add(new InvoiceLine(
                            data[i][1],
                            Integer.parseInt(data[i][3]),
                            Double.parseDouble(data[i][2]),
                            invoice
                    ));
                }
            }

            for (InvoiceHeader invoice : invoices) {
                ArrayList<InvoiceLine> filtered_invoice_lines = new ArrayList<>();
                for (InvoiceLine invoices_line : invoices_lines) {
                    if (invoices_line.getInvoice_header().getNum() == invoice.getNum()) {
                        filtered_invoice_lines.add(invoices_line);
                    }
                }
                invoice.setLines(filtered_invoice_lines);
            }
            if (fls != null) {
                fls.close();
            }
        }
    }

    public void loadData(String header_file, String lines_file) throws Exception {
        invoices = new ArrayList<>();
        invoices_lines = new ArrayList<>();
        try {
            loadInvoicesData(header_file);
            loadInvoicesLinesData(lines_file);
            this.log();
        } catch (IOException io) {
            JOptionPane.showMessageDialog(this.frame, "File not found, make sure the file you selected still exists", "File not found", JOptionPane.ERROR_MESSAGE);
            throw new Exception();
        } catch (ParseException pe) {
            JOptionPane.showMessageDialog(this.frame, "Wrong date format, should be dd-MM-yyyy ex: 19-04-1963", "Wrong date format", JOptionPane.ERROR_MESSAGE);
            throw new Exception();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.frame, "Wrong file format, validate data and make sure loaded file is csv", "Wrong file format", JOptionPane.ERROR_MESSAGE);
            throw new Exception();
        }
    }

    public InvoiceHeaderTableModel getInvoiceHeaderModel() throws Exception {
        return new InvoiceHeaderTableModel(invoices);
    }

    public InvoiceLineTableModel getInvoiceLineModel() throws Exception {
        return new InvoiceLineTableModel(!invoices.isEmpty() ? invoices.get(0).getLines() : new ArrayList<InvoiceLine>());
    }

    private void log() {
        String tab = "  ";
        System.out.println("-----------Logging loaded data start-----------");
        System.out.println("");
        for (InvoiceHeader invoice : invoices) {
            System.out.println("Invoice num: " + invoice.getNum());
            System.out.println("{");
            System.out.println(tab + "Date: " + ActionController.date_formatter.format(invoice.getDate()) + ",  Customer name: " + invoice.getName());
            System.out.println(tab + "Items: [");
            for (InvoiceLine invoice_line : invoice.getLines()) {
                System.out.println(tab + tab + "Item name: " + invoice_line.getName() + ",  Price: " + invoice_line.getPrice() + ", Count: " + invoice_line.getCount());
            }
            System.out.println(tab + "]");
            System.out.println("}");
            System.out.println("");
        }
        System.out.println("-----------Logging loaded data end-----------");
    }
}
