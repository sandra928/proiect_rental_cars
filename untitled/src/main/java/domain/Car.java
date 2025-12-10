package domain;

import java.io.Serializable;

public class Car implements Identifiable<Integer>, Serializable {
    private Integer id;
    private String brand;
    private String model;

    public Car(Integer id, String brand, String model) {
        this.id = id;
        this.brand = brand;
        this.model = model;
    }

    public Car(String brand, String model) {
        this(null, brand, model);
    }

    @Override public Integer getId() { return id; }
    @Override public void setId(Integer id) { this.id = id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }

    @Override
    public String toString() {
        return "Car{" + "id=" + id + ", brand='" + brand + '\'' + ", model='" + model + '\'' + '}';
    }

    public String toFileString() {
        return id + "," + brand + "," + model;
    }

    public static Car fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid Car data format: " + line);
        }
        return new Car(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                parts[2].trim()
        );
    }
}