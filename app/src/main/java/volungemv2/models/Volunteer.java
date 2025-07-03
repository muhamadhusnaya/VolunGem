package volungemv2.models;
// Class untuk merepresentasikan seorang relawan
// yang merupakan turunan dari kelas Person

public class Volunteer extends Person {
    private int id;
    private String contact;
    private String team;
    private String task;

    public Volunteer(int id, String name, int age, String gender, String contact, String team, String task) {
        super(name, age, gender);  // Memanggil konstruktor kelas Person
        this.id = id;
        this.contact = contact;
        this.team = team;
        this.task = task;
        
    }

    // Getter and Setter methods
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }
}
