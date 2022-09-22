/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import controller.ActionController;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author aahmed200
 */
public class InvoiceHeaderTableModel  extends AbstractTableModel {

    private ArrayList<InvoiceHeader> invoice_headers;
    private final String[] col_names = {"No.", "Date", "Customer", "Total"};

    public InvoiceHeaderTableModel(ArrayList<InvoiceHeader> invoice_headers) {
        this.invoice_headers = invoice_headers;
    }

    public ArrayList<InvoiceHeader> getInvoice_headers() {
        return invoice_headers;
    }

    public void setInvoice_headers(ArrayList<InvoiceHeader> invoice_headers) {
        this.invoice_headers = invoice_headers;
    }
    
    public void addInvoiceHeader(InvoiceHeader invoice_header) {
        this.invoice_headers.add(invoice_header);
    }
    
    public void removeInvoiceHeader(int index) {
        this.invoice_headers.remove(index);
    }

    @Override
    public int getRowCount() {
        return this.invoice_headers.size();
    }

    @Override
    public int getColumnCount() {
        return this.col_names.length;
    }

    @Override
    public Object getValueAt(int row_index, int col_index) {
        InvoiceHeader invoice_header = this.invoice_headers.get(row_index);
        switch (col_index) {
            case 0:
                return invoice_header.getNum();
            case 1:
                return ActionController.date_formatter.format(invoice_header.getDate());
            case 2:
                return invoice_header.getName();
            case 3:
                return invoice_header.getTotalInvoices();
        }
        return "";
    }
    
    @Override
    public void setValueAt(Object aValue, int row_index, int col_index) {
        try {
            InvoiceHeader invoice_header = invoice_headers.get(row_index);
            switch (col_index) {
                case 1:
                    invoice_header.setDate(ActionController.date_formatter.parse((String) aValue));
                    break;
                case 2:
                    invoice_header.setName((String) aValue);
                    break;
            }
            this.fireTableDataChanged();
        } catch (ParseException ex) {
            Logger.getLogger(InvoiceHeaderTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1 || columnIndex == 2;
    }

    @Override
    public String getColumnName(int col_index) {
        return col_names[col_index];
    }
}