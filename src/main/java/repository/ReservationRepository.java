package repository;

import model.CarType;
import model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    public Reservation save(final Reservation reservation);
    public List<Reservation> findAllReservationsByCarType(final CarType carType);
    public Optional<Reservation> getReservationById(final long id);
}
