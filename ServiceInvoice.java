// Service Invoice: Fixed service charge lagti hai
public class ServiceInvoice extends Invoice {

    public ServiceInvoice(String customerName) {
        super(customerName, "Service");
    }

    @Override
    public double calculateTotal() {
        return getSubtotal() + TaxSettings.serviceCharge;
    }

    @Override
    public void display() {
        System.out.println("---- Service Invoice ----");
        System.out.println("Customer: " + customerName);
        for (InvoiceItem item : items) {
            System.out.println("  " + item.getItemName() +
                " x" + item.getQuantity() +
                " @ " + item.getPrice() +
                " = " + item.getSubtotal());
        }
        System.out.println("Subtotal       : " + getSubtotal());
        System.out.println("Service Charge : " + TaxSettings.serviceCharge);
        System.out.println("Total          : " + calculateTotal());
    }
}
