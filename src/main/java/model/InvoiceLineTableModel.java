/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author aahmed200
 */
public class InvoiceLineTableModel  extends AbstractTableModel {

    private ArrayList<InvoiceLine> invoice_lines;

    private final String[] col_names = {"No.","Item Name", "Count", "Item Price", "Item Total"};

    public InvoiceLineTableModel(ArrayList<InvoiceLine> invoice_lines) {
        this.invoice_lines = invoice_lines;
    }

    public ArrayList<InvoiceLine> getInvoice_lines() {
        return invoice_lines;
    }

    public void setInvoice_lines(ArrayList<InvoiceLine> invoice_lines) {
        this.invoice_lines = invoice_lines;
    }

    public void addInvoiceLine(InvoiceLine invoice_line) {
        this.invoice_lines.add(invoice_line);
    }
    
    public void removeInvoiceLine(int index) {
        this.invoice_lines.remove(index);
    }

    @Override
    public int getRowCount() {
        return this.invoice_lines.size();
    }

    @Override
    public int getColumnCount() {
        return this.col_names.length;
    }

    @Override
    public Object getValueAt(int row_index, int col_index) {
        InvoiceLine invoice_line = this.invoice_lines.get(row_index);
        switch (col_index) {
            case 0:
                return invoice_line.getInvoice_header().getNum();
            case 1:
                return invoice_line.getName();
            case 2:
                return invoice_line.getCount();
            case 3:
                return invoice_line.getPrice();
            case 4:
                return invoice_line.getTotal();
        }
        return "";
    }
    
    @Override
    public void setValueAt(Object aValue, int row_index, int col_index) {
        InvoiceLine invoice_line = invoice_lines.get(row_index);
        switch (col_index) {
            case 1:
                invoice_line.setName((String) aValue);
                break;
            case 2:
                invoice_line.setCount(Integer.parseInt((String) aValue));
                break;
            case 3:
                invoice_line.setPrice(Double.parseDouble((String) aValue));
                break;
        }
        this.fireTableDataChanged();
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1 || columnIndex == 2  || columnIndex == 3;
    }

    @Override
    public String getColumnName(int col_index) {
        return col_names[col_index];
    }
}