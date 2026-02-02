package service;

import exception.NoAvailabilityException;
import exception.ReservationNotFoundException;
import exception.ValidationException;
import model.CarType;
import model.Reservation;
import model.ReservationRequest;
import repository.ReservationRepository;
import validators.ReservationValidator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class CarRentalService {
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<CarType, Integer> capacity = new EnumMap<>(CarType.class);
    private final ReservationRepository reservationRepository;
    private final ReservationValidator reservationValidator;

    public CarRentalService(ReservationRepository reservationRepository, ReservationValidator reservationValidator) {
        this.reservationRepository = reservationRepository;
        this.reservationValidator = reservationValidator;
        capacity.put(CarType.SUV, 3);
        capacity.put(CarType.VAN, 2);
        capacity.put(CarType.SEDAN, 5);
    }

    public CarRentalService(ReservationRepository reservationRepository, ReservationValidator reservationValidator, Map<CarType, Integer> capacity) {
        this.reservationRepository = Objects.requireNonNull(reservationRepository);
        this.reservationValidator = Objects.requireNonNull(reservationValidator);
        this.capacity.putAll(capacity);
    }

    public synchronized Reservation reserve(final ReservationRequest reservationRequest) {
        reservationValidator.validateReservation(reservationRequest);

        LocalDateTime start = reservationRequest.start();
        LocalDateTime end = start.plusDays(reservationRequest.numberOfReservationDays());

        CarType type = reservationRequest.carType();

        Integer capacityForType = capacity.get(type);
        if (Objects.isNull(capacityForType)) {
            throw new IllegalArgumentException("No capacity configured for " + type);
        }

        long overlapping = reservationRepository.findAllReservationsByCarType(type).stream()
                .filter(r -> r.overlaps(start, end))
                .count();

        if (overlapping >= capacityForType) {
            throw new NoAvailabilityException("No availability for this car type");
        }

        long id = idGenerator.incrementAndGet();
        Reservation reservation = new Reservation(id, reservationRequest.carType(), start, end);
        reservationRepository.save(reservation);
        return reservation;
    }

    public List<Reservation> getReservationsByType(final CarType carType) {
        if (Objects.isNull(carType)) throw new ValidationException("Car type cannot be null");
        return reservationRepository.findAllReservationsByCarType(carType);
    }

    public Reservation getReservation(long id) {
        return reservationRepository.getReservationById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation with ID " + id + " not found"));
    }
}
