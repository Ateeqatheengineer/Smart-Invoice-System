import java.util.ArrayList;

// Abstract base class - sab invoice types is se extend karte hain
public abstract class Invoice {

    protected String customerName;
    protected String invoiceType;
     protected ArrayList<InvoiceItem> items; // Multiple items support

    public Invoice(String customerName, String invoiceType) {
        this.customerName = customerName;
        this.invoiceType  = invoiceType;
        this.items        = new ArrayList<>();
    }

    // Item add karo invoice mein after saving in user panel
    public void addItem(InvoiceItem item) {
        items.add(item);
    }

    // Subtotal = sab items ka total
    public double getSubtotal() {
        double total = 0;
        for (InvoiceItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    // Abstract methods - har subclass apna hisaab lagayega
    public abstract double calculateTotal();
    public abstract void display();

    // Getters
    public String getCustomerName() { return customerName; }
    public String getInvoiceType()  { return invoiceType; }
    public ArrayList<InvoiceItem> getItems() { return items; }
}
