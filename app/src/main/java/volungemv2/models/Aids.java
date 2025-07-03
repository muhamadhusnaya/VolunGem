package volungemv2.models;
// Class untuk merepresentasikan stok bantuan
// yang akan digunakan dalam aplikasi VolungemV2

public class Aids {
    private int idAid;
    private String category;
    private String itemName;
    private int quantity;
    private String location;

    public Aids(int idAid, String category, String itemName, int quantity, String location) {
        this.idAid = idAid;
        this.category = category;
        this.itemName = itemName;
        this.quantity = quantity;
        this.location = location;
    }

    // Getter and Setter methods
    public int getIdAid() { return idAid; }
    public void setIdAid(int idAid) { this.idAid = idAid; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
