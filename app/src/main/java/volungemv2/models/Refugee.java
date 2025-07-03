package volungemv2.models;
// Class untuk merepresentasikan seorang pengungsi
// yang merupakan turunan dari kelas Person

public class Refugee extends Person {
    private int idRefugee;
    private String complain;
    private String address;

    public Refugee(int idRefugee, String name, int age, String gender, String complain, String address) {
        super(name, age, gender);  // Memanggil konstruktor kelas Person
        this.idRefugee = idRefugee;
        this.complain = complain;
        this.address = address;
    }

    // Getter and Setter methods
    public int getIdRefugee() { return idRefugee; }
    public void setIdRefugee(int idRefugee) { this.idRefugee = idRefugee; }

    public String getComplain() { return complain; }
    public void setComplain(String complain) { this.complain = complain; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
