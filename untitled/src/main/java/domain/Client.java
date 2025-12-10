package domain;

import domain.Identifiable;

public class Client implements Identifiable<Integer> {
    private Integer id;
    private String firstName;
    private String lastName;


    public Client(Integer id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;

    }

    public Client(String firstName, String lastName) {
        this(null, firstName, lastName);
    }



    @Override public Integer getId() { return id; }
    @Override public void setId(Integer id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }


    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }


    @Override
    public String toString() {
        return "Client{id=" + id + ", Nume='" + lastName + "', Prenume='" + firstName + " }";
    }
}