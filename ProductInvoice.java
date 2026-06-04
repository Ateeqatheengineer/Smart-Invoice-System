// Product Invoice: Tax + Discount lagti hai
public class ProductInvoice extends Invoice {

    public ProductInvoice(String customerName) {
        super(customerName, "Product");
    }

    @Override
    public double calculateTotal() {
        double subtotal = getSubtotal();
        double tax      = subtotal * (TaxSettings.productTaxPercent / 100.0);
        double discount = subtotal * (TaxSettings.productDiscountPercent / 100.0);
        return subtotal + tax - discount;
    }

    @Override
    public void display() {
        System.out.println("---- Product Invoice ----");
        System.out.println("Customer: " + customerName);
        for (InvoiceItem item : items) {
            System.out.println("  " + item.getItemName() +
                " x" + item.getQuantity() +
                " @ " + item.getPrice() +
                " = " + item.getSubtotal());
        }
        System.out.println("Subtotal : " + getSubtotal());
        System.out.println("Tax (" + TaxSettings.productTaxPercent + "%): " +
            getSubtotal() * TaxSettings.productTaxPercent / 100.0);
        System.out.println("Discount (" + TaxSettings.productDiscountPercent + "%): " +
            getSubtotal() * TaxSettings.productDiscountPercent / 100.0);
        System.out.println("Total    : " + calculateTotal());
    }
}
