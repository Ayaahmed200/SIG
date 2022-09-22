/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.table.TableModel;
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

    public void loadInvoicesData() {
        int result = invoice_loader.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = invoice_loader.getSelectedFile().getPath();
            FileInputStream fls = null;
            try {
                fls = new FileInputStream(path);
                int size = fls.available();
                byte[] b = new byte[size];
                fls.read(b);
                String[] lines = new String(b).split("\\r?\\n");
                String[][] data = new String[lines.length][invoices_col_count];
                for (int i = 0; i < lines.length; i++) {
                    data[i] = lines[i].split(",");
                }

                for (int i = 0; i < data.length; i++) {
                    invoices.add(new InvoiceHeader(
                            Integer.parseInt(data[i][0]),
                            ActionController.date_formatter.parse(data[i][1]),
                            data[i][2]
                    ));
                }
                max_id = Integer.parseInt(data[lines.length - 1][0]);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } catch (ParseException ex) {
                Logger.getLogger(LoaderController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (fls != null) {
                        fls.close();
                    }
                } catch (IOException e) {
                }
            }

        }
    }

    public void loadInvoicesLinesData() {
        int result = invoice_lines_loader.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = invoice_lines_loader.getSelectedFile().getPath();
            FileInputStream fls = null;
            try {
                fls = new FileInputStream(path);
                int size = fls.available();
                byte[] b = new byte[size];
                fls.read(b);
                String[] lines = new String(b).split("\\r?\\n");
                String[][] data = new String[lines.length][lines_col_count];
                for (int i = 0; i < lines.length; i++) {
                    data[i] = lines[i].split(",");
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
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } finally {
                try {
                    if (fls != null) {
                        fls.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }
    
    public void loadData() {
        invoices = new ArrayList<>();
        invoices_lines = new ArrayList<>();
        loadInvoicesData();
        loadInvoicesLinesData();
    }
    
    public InvoiceHeaderTableModel getInvoiceHeaderModel() {
        return new InvoiceHeaderTableModel(invoices);
    }
    
    public InvoiceLineTableModel getInvoiceLineModel() {
        return new InvoiceLineTableModel(invoices.get(0).getLines());
    }
}
