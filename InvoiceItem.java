// Ek invoice mein multiple items ho sakti hain
// Har item ka naam, quantity aur price alag hoga
public class InvoiceItem {

    private String itemName;
    private int quantity;
    private double price;

    public InvoiceItem(String itemName, int quantity, double price) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    public double getSubtotal() {
        return quantity * price;
    }

    // Getters
    public String getItemName() { return itemName; }
    public int getQuantity()    { return quantity; }
    public double getPrice()    { return price; }
}
