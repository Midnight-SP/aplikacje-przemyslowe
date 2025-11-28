# Zadanie 8

## Kontekst

Obecna wersja systemu przechowuje dane w pamięci RAM (w listach i mapach wewnątrz serwisów). Oznacza to, że każdy restart aplikacji powoduje utratę wszystkich wprowadzonych danych. Aby system stał się w pełni funkcjonalny, należy wprowadzić mechanizm trwałego zapisu danych w relacyjnej bazie danych.

W tym zadaniu zrezygnujemy z przechowywania danych w kolekcjach Java na rzecz bazy danych H2. Komunikacja z bazą odbędzie się przy użyciu Spring JDBC (`JdbcTemplate`) oraz wzorca projektowego DAO (Data Access Object), co pozwoli zrozumieć niskopoziomowe operacje SQL przed przejściem do bardziej zaawansowanych narzędzi ORM.

---

## Wymagania funkcjonalne

1. Konfiguracja bazy danych H2 i JDBC

- Dodać zależności do `pom.xml` lub `build.gradle`:
    - `spring-boot-starter-jdbc`
    - `com.h2database:h2` (scope/runtime)

- Skonfigurować połączenie w `application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:employeedb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
```

2. Inicjalizacja schematu bazy danych

- Utworzyć plik `src/main/resources/schema.sql`, który Spring Boot wykona przy starcie aplikacji.
- Plik powinien zawierać instrukcję tworzącą tabelę `employees`, odwzorowującą model `Employee`:

Wymagane kolumny:
- `id` — klucz główny (`BIGINT AUTO_INCREMENT`)
- `first_name`, `last_name` — `VARCHAR`
- `email` — `VARCHAR`, `UNIQUE`
- `salary` — `DECIMAL`
- `position`, `company`, `status` — `VARCHAR`
- `department_id` — `BIGINT` (klucz obcy, może być NULL)
- `photo_file_name` — `VARCHAR`

- Należy również zmodyfikować klasę modelu `Employee`, dodając pole `id` typu `Long`.

Przykład minimalnego DDL (do dopasowania do modelu projektu):
```sql
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    salary DECIMAL(19,2),
    position VARCHAR(255),
    company VARCHAR(255),
    status VARCHAR(255),
    department_id BIGINT,
    photo_file_name VARCHAR(255)
);
```

3. Implementacja wzorca DAO

- Stworzyć interfejs `EmployeeDAO` z metodami:
    - `List<Employee> findAll()`
    - `Optional<Employee> findByEmail(String email)`
    - `void save(Employee employee)` — obsługuje zarówno `INSERT` (gdy `id` jest `null`) jak i `UPDATE` (gdy `id` istnieje)
    - `void delete(String email)`
    - `void deleteAll()` — przydatne do czyszczenia bazy przed importem

- Zaimplementować klasę `JdbcEmployeeDAO` oznaczoną `@Repository`. Wstrzyknąć `JdbcTemplate` (lub `NamedParameterJdbcTemplate`). Użyć `RowMapper<Employee>` do mapowania wyników zapytań na obiekty Java.
- Przy zapisie enumów używać `enum.name()`, przy odczycie `Enum.valueOf(...)`.

4. Refaktoryzacja `EmployeeService`

- Usunąć wewnętrzną listę `List<Employee>` z serwisu.
- EmployeeService powinien delegować operacje do wstrzykniętego beana `EmployeeDAO`.
- Metody analityczne (filtry, sortowanie) mogą działać w Javie, pobierając najpierw wszystkie dane przez `dao.findAll()`.
- CRUD (dodawanie, edycja, usuwanie) powinny operować bezpośrednio na bazie danych.
- Import z CSV powinien być transakcyjny (`@Transactional`) i przed wstawieniem nowych rekordów wywoływać `dao.deleteAll()` lub implementować upsert.

5. Nowa funkcjonalność: statystyki SQL

- Dodać do interfejsu DAO metodę:
    - `List<CompanyStatistics> getCompanyStatistics()`
- Zaimplementować ją w `JdbcEmployeeDAO` używając zapytania SQL z `GROUP BY`, które zwróci:
    - nazwę firmy,
    - liczbę pracowników (`COUNT`),
    - średnią pensję (`AVG`),
    - maksymalną pensję (`MAX`).
- Uwaga: pobranie osoby z najwyższym wynagrodzeniem w jednym zapytaniu grupującym jest trudniejsze — można uprościć `CompanyStatistics` tak, by zawierał tylko kwotę max pensji, lub wykonać dodatkowe podzapytanie.

6. Testy integracyjne DAO

- Utworzyć klasę testową `JdbcEmployeeDAOTest` oznaczoną `@JdbcTest`. Adnotacja ta konfiguruje in-memory DB i zapewnia rollback po każdym teście.
- Przetestować: zapis pracownika i odczyt po emailu, aktualizację, usuwanie oraz poprawne mapowanie enumów.

---

## Struktura projektu (sugerowana)

```
src/
├── main/
│   ├── java/com.techcorp.employee/
│   │   ├── dao/
│   │   │   ├── EmployeeDAO.java
│   │   │   └── JdbcEmployeeDAO.java
│   │   ├── service/
│   │   │   └── EmployeeService.java
│   │   └── ... (reszta)
│   └── resources/
│       ├── schema.sql
│       └── application.properties
└── test/
        └── java/com.techcorp.employee/
                └── dao/
                        └── JdbcEmployeeDAOTest.java
```

## Oddanie

- Link do repozytorium z kodem źródłowym, plikiem `schema.sql` oraz testami.
- W README podać instrukcję jak połączyć się z konsolą H2 (domyślnie http://localhost:8080/h2-console) oraz dane logowania.

## Kryteria recenzji

- Konfiguracja H2 i `schema.sql` (20%): poprawne `application.properties` i DDL tworzący tabelę `employees`.
- Implementacja DAO (30%): interfejs DAO i `JdbcEmployeeDAO` z `JdbcTemplate`, poprawne mapowanie (`RowMapper`), obsługa CRUD i enumów.
- Integracja z serwisem (25%): `EmployeeService` deleguje do DAO, brak kolekcji w pamięci.
- Statystyki SQL (10%): logika statystyk w zapytaniu SQL (`GROUP BY`).
- Testy integracyjne @JdbcTest (15%): obecność testów weryfikujących mapowanie i operacje DAO.

Powodzenia.