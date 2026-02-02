import exception.NoAvailabilityException;
import exception.ReservationNotFoundException;
import exception.ValidationException;
import model.CarType;
import model.Reservation;
import model.ReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InMemoryReservationRepository;
import repository.ReservationRepository;
import service.CarRentalService;
import validators.ReservationValidator;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CarRentalServiceTest {

    private ReservationRepository repository;
    private CarRentalService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReservationRepository();

        Map<CarType, Integer> capacity = new EnumMap<>(CarType.class);
        capacity.put(CarType.SEDAN, 1);
        capacity.put(CarType.SUV, 1);
        capacity.put(CarType.VAN, 2);

        service = new CarRentalService(repository, new ReservationValidator(), capacity);
    }

    @Test
    void shouldReserveCarWhenAvailable() {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 10, 0);
        ReservationRequest request = new ReservationRequest(CarType.SEDAN, start, 2);

        Reservation reservation = service.reserve(request);

        assertTrue(reservation.getId() > 0);
        assertEquals(CarType.SEDAN, reservation.getCarType());
        assertEquals(start, reservation.getStart());
        assertEquals(start.plusDays(2), reservation.getEnd());
        assertEquals(1, service.getReservationsByType(CarType.SEDAN).size());
    }

    @Test
    void shouldRejectWhenStartDateIsInThePast() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        ReservationRequest request = new ReservationRequest(CarType.SUV, start, 1);

        assertThrows(ValidationException.class, () -> service.reserve(request));
    }

    @Test
    void shouldRejectWhenReservationDaysIsNotPositive() {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 10, 0);
        ReservationRequest request = new ReservationRequest(CarType.SUV, start, -1);

        assertThrows(ValidationException.class, () -> service.reserve(request));
    }

    @Test
    void shouldRejectReservationWhenCapacityExceededAndOverlaps() {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 10, 0);

        service.reserve(new ReservationRequest(CarType.VAN, start, 3));
        service.reserve(new ReservationRequest(CarType.VAN, start.plusHours(1), 1));

        assertThrows(NoAvailabilityException.class, () ->
                service.reserve(new ReservationRequest(CarType.VAN, start.plusHours(2), 1))
        );

        assertEquals(2, service.getReservationsByType(CarType.VAN).size());
    }

    @Test
    void shouldAllowBackToBackReservations() {
        LocalDateTime start1 = LocalDateTime.of(2030, 1, 1, 10, 0);

        service.reserve(new ReservationRequest(CarType.SUV, start1, 2));

        LocalDateTime start2 = start1.plusDays(2);
        assertDoesNotThrow(() ->
                service.reserve(new ReservationRequest(CarType.SUV, start2, 1))
        );

        assertEquals(2, service.getReservationsByType(CarType.SUV).size());
    }

    @Test
    void shouldNotBlockDifferentCarTypes() {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 10, 0);

        service.reserve(new ReservationRequest(CarType.SEDAN, start, 2));

        assertDoesNotThrow(() -> service.reserve(new ReservationRequest(CarType.SUV, start, 1)));

        assertEquals(1, service.getReservationsByType(CarType.SUV).size());
    }

    @Test
    void shouldGetReservationById() {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 10, 0);
        Reservation created = service.reserve(new ReservationRequest(CarType.SEDAN, start, 1));

        Reservation found = service.getReservation(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals(created.getCarType(), found.getCarType());
        assertEquals(created.getStart(), found.getStart());
        assertEquals(created.getEnd(), found.getEnd());
    }

    @Test
    void shouldThrowWhenReservationNotFound() {
        assertThrows(ReservationNotFoundException.class, () -> service.getReservation(9999L));
    }
}
