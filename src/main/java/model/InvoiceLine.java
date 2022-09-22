/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author aahmed200
 */
public class InvoiceLine {
    //private int invoice_number;
    private String name;
    private int count;
    private double price;
    private InvoiceHeader invoice_header;

  

    public InvoiceLine(String name, int count, double Price, InvoiceHeader invoice_header) {
        this.name = name;
        this.count = count;
        this.price = Price;
        this.invoice_header = invoice_header;
    }

    public double getTotal(){
    return count * price;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double Price) {
        this.price = Price;
    }

    public InvoiceHeader getInvoice_header() {
        return invoice_header;
    }

    public void setInvoice_header(InvoiceHeader invoice_header) {
        this.invoice_header = invoice_header;
    }

    @Override
    public String toString() {
        return getInvoice_header().getNum() + "," + name + "," + price + "," + count;
    }

    public static String toCSV(ArrayList<InvoiceLine> invoices_lines) {
        String data = "";
        
        for (InvoiceLine invoices_line : invoices_lines) {
            data += invoices_line.toString() + "\n";
        }
        return data;
    }
}
