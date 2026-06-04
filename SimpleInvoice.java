public class SimpleInvoice extends Invoice {

    private String id;
    private String username;
    private String total;
    private String date;

    public SimpleInvoice(String id, String username, String type, String total, String date) {
        super(username, type); 
        this.id = id;
        this.username = username;
        this.total = total;
        this.date = date;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getTotal() { return total; }
    public String getDate() { return date; }


    public double calculateTotal() {
        return Double.parseDouble(total);
    }

    
    public void display() {
        System.out.println("Invoice " + id);
    }
}