/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import controller.ActionController;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author aahmed200
 */
public class InvoiceHeader {
    private int num;
    private Date date;
    private String name;
    private ArrayList<InvoiceLine> lines;

    public InvoiceHeader(int num, Date date, String name) {
        this.num = num;
        this.date = date;
        this.name = name;
        lines = new ArrayList<>();
    }

    public double getTotalInvoices() {
        double total = 0.0;
        for (int i = 0; i < lines.size(); i++) {
            total = total + lines.get(i).getTotal();
        }
        return total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<InvoiceLine> getLines() {
        return lines;
    }
    
    public void setLines(ArrayList<InvoiceLine> lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return num + "," + ActionController.date_formatter.format(date) + "," + name;
    }
    
    public static String toCSV(ArrayList<InvoiceHeader> invoices_headers) {
        String data = "";
        
        for (InvoiceHeader invoices_header : invoices_headers) {
            data += invoices_header.toString() + "\n";
        }
        return data;
    }
}
