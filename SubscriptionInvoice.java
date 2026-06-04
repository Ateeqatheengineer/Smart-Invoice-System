// Subscription Invoice: Koi extra charge nahi
public class SubscriptionInvoice extends Invoice {

    public SubscriptionInvoice(String customerName) {
        super(customerName, "Subscription");
    }

    @Override
    public double calculateTotal() {
        return getSubtotal();
    }

    @Override
    public void display() {
        System.out.println("---- Subscription Invoice ----");
        System.out.println("Customer: " + customerName);
        for (InvoiceItem item : items) {
            System.out.println("  " + item.getItemName() +
                " x" + item.getQuantity() +
                " @ " + item.getPrice() +
                " = " + item.getSubtotal());
        }
        System.out.println("No extra charges.");
        System.out.println("Total: " + calculateTotal());
    }
}
