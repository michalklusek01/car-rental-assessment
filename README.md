# Car Rental – Technical Assessment (Java 17)

A small in-memory **Car Rental reservation** system written in **Java 17**.
The focus is on clean business logic, simple OOP design and **unit tests**.

---

## What the system does
- Creates a reservation for a selected **car type** (`SEDAN`, `SUV`, `VAN`)
- Reservation has a **start date/time** and a **duration in days**
- Each car type has a limited **capacity** (number of cars available at the same time)
- If capacity is exceeded for the requested time range, the system rejects the reservation
- After creating a reservation, the service returns the **created Reservation including its generated `id`**
  (so it can be later used for `getReservation(id)`)

---

## Key rules / assumptions
- **End date/time** is calculated as:
  - `end = start + numberOfDays`
- **Overlap rule** (two reservations conflict when their time ranges intersect):
  - `newStart < existingEnd && existingStart < newEnd`
- **Back-to-back reservations are allowed**:
  - if one ends exactly when the next starts (`end == start`), it is NOT a conflict
- **Start date validation**:
  - start date/time cannot be before the current date/time (basic business rule)

---

## Default capacity
`CarRentalService` default capacities:
- `SEDAN` = 5
- `SUV` = 3
- `VAN` = 2

For unit tests, capacity can be injected using the constructor that accepts a map.

---

## Project structure
- `model`
  - `CarType` – supported car types
  - `Reservation` – reservation data + overlap check (includes `id`)
  - `ReservationRequest` – input data for reservation (record)
- `service`
  - `CarRentalService` – use case logic (availability check, id generation, saving)
- `repository`
  - `ReservationRepository` – repository interface
  - `InMemoryReservationRepository` – in-memory storage
- `validators`
  - `ReservationValidator` – request validation (required fields, start date rule, days > 0)
- `exception`
  - `ValidationException` – invalid request / invalid arguments
  - `NoAvailabilityException` – capacity exceeded for the requested time range
  - `ReservationNotFoundException` – reservation not found by id

---

## How to run tests
From the project root:

```bash
mvn test
```

Or run tests directly in IntelliJ (right-click test class → Run).

---

## What I would improve with more time
- Improve error messages (include requested time range, car type, overlapping count vs capacity)
- Add cancel/update operations and related tests
- If this was used in a real service (multiple instances), enforce availability rules using a database transaction/locking
  (instead of JVM-level synchronization)

---
