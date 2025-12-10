package domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Rental implements Identifiable<Integer>, Serializable {
    private Integer id;
    private Car car;
    private Integer carId;
    private Integer clientId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Rental(Integer id,Integer carId, Integer clientId,  LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.carId = carId;
        this.clientId = clientId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Rental(Integer carId, Integer clientId, LocalDateTime startDate, LocalDateTime endDate) {
        this(null, carId, clientId, startDate, endDate);
    }


    @Override public Integer getId() { return id; }
    @Override public void setId(Integer id) { this.id = id; }
    public Car getCar() { return car; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public Integer getCarId() { return carId; }
    public Integer getClientId() { return clientId; }
    public void setCarId(Integer carId) { this.carId = carId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }


    public void setCar(Car car) {
        this.car = car;
        if (car != null) {
            this.carId = car.getId();
        }
    }

    public boolean overlapsWith(Rental other) {
        if (!this.car.getId().equals(other.car.getId())) {
            return false;
        }
        return this.startDate.isBefore(other.endDate) && this.endDate.isAfter(other.startDate);
    }

    @Override
    public String toString() {
        return "Rental{" + "id=" + id + ", carId=" + car.getId() + ", start=" + startDate.format(FORMATTER) + ", end=" + endDate.format(FORMATTER) + '}';
    }

    public String toFileString() {
        return id + "," + carId+ "," + clientId+ "," + startDate.format(FORMATTER) + "," + endDate.format(FORMATTER);
    }


}