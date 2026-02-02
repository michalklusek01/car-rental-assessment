package repository;

import model.CarType;
import model.Reservation;

import java.util.*;

public class InMemoryReservationRepository implements ReservationRepository{
    private final List<Reservation> allReservations = Collections.synchronizedList(new ArrayList<>());

    @Override
    public Reservation save(final Reservation reservation){
        Objects.requireNonNull(reservation, "Reservation must be not null");
        allReservations.add(reservation);
        return reservation;
    }

    @Override
    public List<Reservation> findAllReservationsByCarType(final CarType carType){
        Objects.requireNonNull(carType, "CarType must be not null");
        return this.allReservations.stream()
                .filter(reservation -> reservation.getCarType() == carType)
                .toList();
    }

    @Override
    public Optional<Reservation> getReservationById(long id) {
        return allReservations.stream()
                .filter(r -> r.getId() == id)
                .findFirst();
    }


}
