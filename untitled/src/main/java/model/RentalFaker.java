package model;

import domain.Rental;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class RentalFaker {
    private final Random random = new Random();

    public Rental generateRandomRental(List<Integer> existingCarIds, List<Integer> existingClientIds) {
        if (existingCarIds.isEmpty() || existingClientIds.isEmpty()) {
            throw new IllegalArgumentException("Nu există ID-uri pentru a crea o închiriere.");
        }

        int carId = existingCarIds.get(random.nextInt(existingCarIds.size()));
        int clientId = existingClientIds.get(random.nextInt(existingClientIds.size()));

        LocalDateTime startDate = LocalDateTime.now().minusDays(random.nextInt(365)).plusDays(random.nextInt(30));
        int durationDays = random.nextInt(14) + 1;
        LocalDateTime endDate = startDate.plusDays(durationDays);

        return new Rental(carId, clientId, startDate, endDate);
    }
}
