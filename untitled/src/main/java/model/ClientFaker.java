package model;


import com.github.javafaker.Faker;
import domain.Client;


public class ClientFaker {

    private final Faker faker = new Faker();

    public Client generateRandomClient() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        return new Client(firstName, lastName);
    }
}
