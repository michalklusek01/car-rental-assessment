package validators;

import exception.ValidationException;
import model.ReservationRequest;

import java.time.LocalDateTime;
import java.util.Objects;

public class ReservationValidator {

    public ReservationValidator() {
    }

    public void validateReservation(final ReservationRequest newReservationRequest) {
        if (Objects.isNull(newReservationRequest)) throw new ValidationException("Request is null");

        LocalDateTime newReservationStartDate = newReservationRequest.start();

        if (Objects.isNull(newReservationRequest.carType())) throw new ValidationException("Car type is required");
        if (Objects.isNull(newReservationStartDate)) throw new ValidationException("Start date is required");
        if (newReservationStartDate.isBefore(LocalDateTime.now())) throw new ValidationException("Start date cannot be before the current date");
        if (newReservationRequest.numberOfReservationDays() <= 0) throw new ValidationException("Days must be > 0");
    }
}
