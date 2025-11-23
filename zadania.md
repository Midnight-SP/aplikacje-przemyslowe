# Zadanie 1

**Pytanie** (1 punkt)  
**Wymaga recenzji autora**

Firma TechCorp potrzebuje prostego systemu do zarządzania informacjami o swoich pracownikach. System ma umożliwić podstawowe operacje analityczne na danych pracowników, takie jak wyszukiwanie, sortowanie i generowanie statystyk.

## Wymagania

### Model danych

Stwórz odpowiedni model obiektowy reprezentujący strukturę firmy. Każdy pracownik posiada następujące informacje:

- Imię i nazwisko
- Adres email (unikalny identyfikator)
- Nazwa firmy, w której pracuje
- Stanowisko służbowe
- Wysokość wynagrodzenia

W firmie występują następujące stanowiska w hierarchii od najwyższego do najniższego: Prezes, Wiceprezes, Manager, Programista oraz Stażysta. Każde stanowisko ma przypisaną bazową stawkę wynagrodzenia odpowiednio: 25000, 18000, 12000, 8000 oraz 3000.

Stanowiska służbowe należy zaimplementować jako enum z polami przechowującymi bazową pensję i poziom w hierarchii. Klasy modelu muszą nadpisywać metody `equals()` i `hashCode()` (oparte na emailu) oraz `toString()`. Stosuj enkapsulację z prywatnymi polami i publicznymi metodami dostępowymi.

### Funkcjonalność systemu

System musi umożliwiać wykonanie następujących operacji:

**Zarządzanie pracownikami:**
- Dodawanie nowego pracownika do systemu z walidacją unikalności adresu email przed dodaniem
- Wyświetlanie listy wszystkich pracowników w systemie

**Operacje analityczne:**
- Wyszukiwanie pracowników zatrudnionych w konkretnej firmie - zaimplementuj jako operację filtrowania kolekcji z wykorzystaniem Stream API.
- Prezentacja pracowników w kolejności alfabetycznej według nazwiska - użyj Comparator do zdefiniowania porządku sortowania.
- Grupowanie pracowników według zajmowanego stanowiska - operacja powinna zwrócić strukturę Map, gdzie kluczem jest stanowisko, a wartością lista pracowników na tym stanowisku.
- Zliczanie liczby pracowników na każdym stanowisku - wynik w formie Map mapującej stanowisko na liczbę pracowników.

**Statystyki finansowe:**
- Obliczanie średniego wynagrodzenia w całej organizacji - operacja agregująca dane finansowe wszystkich pracowników.
- Identyfikacja pracownika z najwyższym wynagrodzeniem - operacja znajdowania maksimum z wykorzystaniem Optional do obsługi potencjalnie pustej kolekcji.

## Oddanie

Jako odpowiedź prześlij link do repozytorium.

## Kryteria recenzji

- **Model danych i podstawy programowania obiektowego (20%)**
- **Zarządzanie danymi i walidacja (15%)**
- **Operacje analityczne z wykorzystaniem Stream API (25%)**
- **Operacje statystyczne i obsługa Optional (15%)**
- **Jakość techniczna i czytelność kodu (25%)**

Twoja odpowiedź zostanie oceniona przez autora kursu na podstawie powyższych kryteriów. Końcowa ocena będzie przyznana po zakończeniu recenzji.

---

# Zadanie 2

**Pytanie** (1 punkt)  
**Wymaga recenzji autora**

## Zadanie: Integracja z plikami CSV i REST API (1 punkt)

### Kontekst

System zarządzania pracownikami wymaga rozszerzenia o import danych z plików CSV oraz synchronizację z API pod adresem https://jsonplaceholder.typicode.com/users. Dodatkowo system musi generować statystyki analityczne dla poszczególnych firm.

### Wymagania funkcjonalne

**Import z pliku CSV:**

Należy zaimplementować klasę `ImportService` z metodą `importFromCsv` przyjmującą ścieżkę do pliku. Plik CSV ma strukturę: `firstName, lastName, email, company, position, salary` z nagłówkiem w pierwszej linii. Metoda pomija nagłówek i puste linie, parsuje każdy wiersz do obiektu `Employee` i dodaje go do `EmployeeService`. Walidacja musi sprawdzać czy stanowisko istnieje w enumie `Position` oraz czy wynagrodzenie jest dodatnie. Błędne wiersze należy zapisać z numerem linii i opisem błędu, ale kontynuować import pozostałych. Metoda zwraca obiekt `ImportSummary` zawierający liczbę zaimportowanych pracowników i listę błędów.

**Integracja z REST API:**

Należy zaimplementować klasę `ApiService` z metodą `fetchEmployeesFromApi`, która wykonuje zapytanie GET do podanego API, parsuje odpowiedź JSON używając biblioteki Gson i zwraca listę obiektów `Employee`. API zwraca tablicę z polami: `name` (pełne imię do rozdzielenia na firstName i lastName), `email` oraz `company.name`. Wszystkim użytkownikom z API przypisać stanowisko PROGRAMISTA i bazową stawkę tego stanowiska. Metoda rzuca `ApiException` przy błędach HTTP lub problemach z parsowaniem.

**Operacje analityczne:**

Należy dodać do `EmployeeService` dwie metody analityczne wykorzystujące Stream API. Pierwsza metoda `validateSalaryConsistency` zwraca listę pracowników z wynagrodzeniem niższym niż bazowa stawka ich stanowiska. Druga metoda `getCompanyStatistics` zwraca mapę, gdzie kluczem jest nazwa firmy, a wartością obiekt `CompanyStatistics` zawierający liczbę pracowników w firmie, średnie wynagrodzenie oraz pełne imię i nazwisko osoby z najwyższym wynagrodzeniem.

### Struktura projektu

```
src/
├── model/
│   ├── Employee.java
│   ├── Position.java
│   ├── ImportSummary.java
│   └── CompanyStatistics.java
├── service/
│   ├── EmployeeService.java
│   ├── ImportService.java
│   └── ApiService.java
├── exception/
│   ├── InvalidDataException.java
│   └── ApiException.java
└── Main.java
```

Pakiet `model` zawiera klasy domenowe reprezentujące encje biznesowe. Pakiet `service` zawiera logikę biznesową w dedykowanych serwisach, gdzie każdy serwis ma jedną jasno określoną odpowiedzialność. Pakiet `exception` zawiera wyjątki checked dla różnych kategorii błędów.

### Wymagania techniczne

Wykorzystać `BufferedReader` z try-with-resources do czytania CSV. Parsowanie przez `split` i `trim`. Obsłużyć `IOException` i `IllegalArgumentException` przy walidacji Position. Do HTTP użyć `HttpClient` z Java 11 lub nowszego. Do JSON użyć biblioteki Gson (dodać do pom.xml: groupId `com.google.code.gson`, artifactId `gson`, version `2.10.1`). Parsować `JsonArray` z Gson i ekstrahować pola metodami `get` oraz `getAsString`.

Wszystkie operacje na kolekcjach pracowników implementować przez Stream API. Metoda `getCompanyStatistics` powinna używać `Collectors.groupingBy` do grupowania pracowników według nazwy firmy, a następnie dla każdej grupy obliczać statystyki wykorzystując operacje `count`, `average` i `max`. Klasa `CompanyStatistics` powinna mieć konstruktor przyjmujący wszystkie pola oraz nadpisaną metodę `toString`.

### Oddanie

Link do repozytorium z kodem, plikiem pom.xml, employees.csv i README z instrukcją uruchomienia.

## Kryteria recenzji

- **Import CSV z walidacją (30%)** - Poprawne wczytywanie pliku z pominięciem nagłówka i pustych linii, walidacja stanowiska i wynagrodzenia, raportowanie błędów z numerami linii bez przerywania procesu importu.
- **Integracja z REST API (30%)** - Wykonanie zapytania HTTP GET, deserializacja odpowiedzi JSON przy użyciu Gson, mapowanie pól API na obiekty Employee, obsługa błędów przez ApiException.
- **Operacje analityczne (25%)** - Implementacja validateSalaryConsistency i getCompanyStatistics z wykorzystaniem Stream API, poprawne grupowanie i agregacja danych.
- **Struktura i jakość kodu (15%)** - Zgodność z wymaganą strukturą pakietów, właściwa hierarchia wyjątków, anglojęzyczne nazwy zmiennych, komentarze przy kluczowej logice, czytelność kodu.

Twoja odpowiedź zostanie oceniona przez autora kursu na podstawie powyższych kryteriów. Końcowa ocena będzie przyznana po zakończeniu recenzji.

---

# Zadanie 3

**Pytanie** (1 punkt)  
**Wymaga recenzji autora**

## Zadanie 3: Testy jednostkowe i automatyzacja buildu

### Kontekst

System zarządzania pracownikami wymaga profesjonalnego podejścia do jakości kodu. Zadaniem jest przygotowanie testów jednostkowych oraz skonfigurowanie wybranego narzędzia buildowego (Maven LUB Gradle) z raportowaniem pokrycia kodu.

### Cele edukacyjne

Po wykonaniu tego zadania oczekiwane umiejętności obejmują:

- Konfigurację projektu z JUnit 5 w wybranym narzędziu buildowym
- Pisanie efektywnych testów jednostkowych z użyciem mockowania
- Generowanie i interpretację raportów pokrycia kodu
- Uruchamianie testów i budowanie projektu z linii komend

### Wymagania funkcjonalne

#### 1. Wybór i konfiguracja narzędzia buildowego

Należy wybrać JEDNO narzędzie: Maven LUB Gradle

Wymagana konfiguracja obejmuje:

- JUnit 5 (Jupiter) - najnowszą stabilną wersję
- Mockito z integracją JUnit 5
- Plugin do uruchamiania testów
- Plugin JaCoCo do raportowania pokrycia kodu

Aspekty do rozważenia:

- Różnice między JUnit 4 a JUnit 5 w kontekście konfiguracji
- Rola dedykowanego pluginu do uruchamiania testów
- Mechanizm działania JaCoCo w kontekście śledzenia wykonanych linii kodu

#### 2. Testy dla EmployeeService

Analiza wymagana przed implementacją:

- Identyfikacja najbardziej krytycznych metod w EmployeeService
- Określenie potencjalnych problemów w każdej z tych metod
- Rozpoznanie danych wejściowych mogących spowodować nieoczekiwane zachowanie

Scenariusze wymagające pokrycia testami:

- Dodawanie pracownika - obsługa powtarzających się emaili i wartości null
- Wyszukiwanie po firmie - zachowanie przy nieistniejącej firmie
- Średnie wynagrodzenie - obsługa pustej listy pracowników
- Maksymalne wynagrodzenie - typ zwracany i jego znaczenie przy pustej liście
- Walidacja wynagrodzeń - identyfikacja nieprawidłowości

Zagadnienia projektowe:

- Konwencja nazewnictwa metod testowych zapewniająca czytelność w długim okresie
- Przygotowanie danych testowych równoważące czytelność i kompletność
- Zasadność użycia metody `@BeforeEach` do inicjalizacji wspólnych danych

#### 3. Testy dla ImportService

Kluczowe wyzwanie: Testowanie operacji na plikach

Aspekty wymagające uwagi:

- Podejście do tworzenia plików CSV dla celów testowych
- Zapewnienie przenośności testów między różnymi systemami operacyjnymi
- Gwarancja izolacji testów i braku wzajemnych zależności

Scenariusze do implementacji:

- Poprawny import - weryfikacja trafiania danych do systemu
- Niepoprawne stanowisko - decyzja o przerwaniu lub kontynuacji importu
- Ujemne wynagrodzenie - określenie sposobu obsługi przez system
- Weryfikacja podsumowania - zawartość obiektu ImportSummary

Wskazówka techniczna: JUnit 5 udostępnia mechanizm do pracy z tymczasowymi zasobami. Dokumentacja frameworka zawiera informacje o rozwiązaniach dotyczących katalogów tymczasowych.

#### 4. Testy dla ApiService z mockowaniem

Fundamentalne zagadnienie: Uzasadnienie unikania prawdziwych żądań HTTP w testach jednostkowych

Punkty do analizy:

- Konsekwencje niedostępności API podczas uruchamiania testów
- Możliwość testowania obsługi błędów przy zawsze działającym API
- Zależność testów od zewnętrznych serwisów i jej implikacje

Koncepcja mockowania:

- Definicja mock object i różnice względem prawdziwego obiektu
- Identyfikacja obiektów w ApiService wymagających mockowania
- Mechanizm konfiguracji wartości zwracanych przez mocki

Scenariusze:

- Poprawna odpowiedź JSON - symulacja odpowiedzi bez prawdziwego API
- Błąd HTTP (404, 500) - weryfikacja rzucania wyjątków
- Parsowanie danych - poprawność mapowania z JSON do Employee

Aspekt weryfikacji: Mockito oferuje różne sposoby weryfikacji - analiza czy wystarczające jest sprawdzenie wartości zwracanej, czy konieczna jest również weryfikacja wywołań konkretnych metod.

#### 5. Raportowanie pokrycia kodu z JaCoCo

Cel: Minimum 70% pokrycia dla pakietu service

Zagadnienia teoretyczne:

- Znaczenie metryki "70% pokrycia linii"
- Relacja między pokryciem 100% a idealnymi testami
- Różnice między pokryciem linii, gałęzi i ścieżek

Konfiguracja do ustalenia:

- Lokalizacja generowania raportu przez plugin JaCoCo
- Format raportu (HTML, XML, CSV)
- Automatyzacja generowania raportu po wykonaniu testów

### Struktura projektu

```
project-root/
├── pom.xml (lub build.gradle)
├── src/
│   ├── main/java/...
│   └── test/
│       ├── java/
│       │   └── service/
│       │       ├── EmployeeServiceTest.java
│       │       ├── ImportServiceTest.java
│       │       └── ApiServiceTest.java
│       └── resources/
│           └── (opcjonalne pliki testowe)
└── README.md
```

### Polecenia do uruchomienia

Należy ustalić standardowe komendy dla wybranego narzędzia obejmujące:

- Uruchamianie wyłącznie testów
- Uruchamianie testów z jednoczesnym generowaniem raportu pokrycia
- Uruchamianie pełnego cyklu budowania z weryfikacją

## Kryteria recenzji

- **Poprawna konfiguracja narzędzia buildowego + JUnit 5 + JaCoCo (25%)**
- **Testy EmployeeService (30%)**
- **Testy ImportService w tym obsługa błędów (20%)**
- **Testy ApiService z użyciem mocków (15%)**
- **Pokrycie ≥70% (10%)**

Twoja odpowiedź zostanie oceniona przez autora kursu na podstawie powyższych kryteriów. Końcowa ocena będzie przyznana po zakończeniu recenzji.

---

# Zadanie 4

**Pytanie** (1 punkt)  
**Wymaga recenzji autora**

## Zadanie 4: Migracja do Spring Boot (1 punkt)

### Kontekst

System zarządzania pracownikami wymaga refaktoryzacji do architektury Spring Boot z wykorzystaniem mechanizmu Dependency Injection. Dotychczasowa implementacja oparta na ręcznym tworzeniu obiektów i przekazywaniu zależności zostanie zastąpiona zarządzaniem przez kontener Spring. Dodatkowo system zostanie rozszerzony o możliwość definiowania pracowników bezpośrednio jako beany Spring w pliku konfiguracyjnym XML, co pozwoli poznać alternatywny sposób konfiguracji aplikacji Spring oprócz adnotacji.

### Wymagania funkcjonalne

#### 1. Konfiguracja projektu Spring Boot

Należy dodać do pom.xml lub build.gradle zależności Spring Boot w wersji 3.x, które obejmują `spring-boot-starter` oraz `spring-boot-starter-test`. Dotychczasowa zależność Gson z poprzedniego zadania powinna zostać zachowana, ponieważ jest nadal wykorzystywana do parsowania odpowiedzi z REST API.

Należy utworzyć plik `application.properties` w katalogu `src/main/resources` zawierający kluczowe parametry konfiguracyjne aplikacji:

```properties
app.api.url=https://jsonplaceholder.typicode.com/users
app.import.csv-file=employees.csv
logging.level.root=INFO
```

#### 2. Refaktoryzacja serwisów jako Spring Beany

Należy przekształcić wszystkie istniejące klasy serwisów w komponenty zarządzane przez Spring Container poprzez zastosowanie odpowiednich adnotacji stereotypowych.

- **EmployeeService** - Oznacz adnotacją `@Service`. Spring automatycznie utworzy jej instancję jako singleton bean. Usuń wszelkie statyczne referencje oraz ręczne tworzenie instancji tej klasy.

- **ImportService** - Oznacz adnotacją `@Service`. Przyjmuj `EmployeeService` jako zależność przez konstruktor. Spring automatycznie wstrzyknie odpowiednią instancję.

- **ApiService** - Oznacz adnotacją `@Service`. Przyjmuj przez konstruktor `HttpClient` i `Gson`. Wstrzyknij adres URL API używając `@Value("${app.api.url}")`.

#### 3. Definicja pracowników jako beany w pliku XML

Utwórz plik konfiguracyjny XML o nazwie `employees-beans.xml` w katalogu `src/main/resources`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="
           http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd
           http://www.springframework.org/schema/util
           http://www.springframework.org/schema/util/spring-util.xsd">
    
    <bean id="employee1" class="com.techcorp.employee.model.Employee">
        <constructor-arg value="Jan"/>
        <constructor-arg value="Kowalski"/>
        <constructor-arg value="jan.kowalski@techcorp.com"/>
        <constructor-arg value="TechCorp"/>
        <constructor-arg value="MANAGER"/>
        <constructor-arg value="12500"/>
    </bean>
    
    <bean id="employee2" class="com.techcorp.employee.model.Employee">
        <constructor-arg value="Anna"/>
        <constructor-arg value="Nowak"/>
        <constructor-arg value="anna.nowak@techcorp.com"/>
        <constructor-arg value="TechCorp"/>
        <constructor-arg value="PROGRAMISTA"/>
        <constructor-arg value="8500"/>
    </bean>
    
    <util:list id="xmlEmployees" value-type="com.techcorp.employee.model.Employee">
        <ref bean="employee1"/>
        <ref bean="employee2"/>
    </util:list>
</beans>
```

Aby załadować tę konfigurację XML, użyj adnotacji `@ImportResource("classpath:employees-beans.xml")` w głównej klasie aplikacji.

#### 4. Konfiguracja zewnętrznych zależności jako beany

Utwórz klasę `AppConfig` oznaczoną adnotacją `@Configuration`:

```java
@Configuration
public class AppConfig {
    
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
    
    @Bean
    public Gson gson() {
        return new Gson();
    }
}
```

#### 5. Klasa startowa aplikacji

Utwórz klasę `EmployeeManagementApplication`:

```java
@SpringBootApplication
@ImportResource("classpath:employees-beans.xml")
public class EmployeeManagementApplication implements CommandLineRunner {
    
    private final EmployeeService employeeService;
    private final ImportService importService;
    private final ApiService apiService;
    private final List<Employee> xmlEmployees;
    
    // Konstruktor z wstrzykiwaniem zależności
    public EmployeeManagementApplication(
            EmployeeService employeeService,
            ImportService importService,
            ApiService apiService,
            @Qualifier("xmlEmployees") List<Employee> xmlEmployees) {
        this.employeeService = employeeService;
        this.importService = importService;
        this.apiService = apiService;
        this.xmlEmployees = xmlEmployees;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Demonstracja funkcjonalności
        // 1. Import z CSV
        // 2. Dodanie pracowników z XML
        // 3. Pobranie z API
        // 4. Statystyki
        // 5. Walidacja wynagrodzeń
    }
    
    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }
}
```

### Struktura projektu

```
project-root/
├── pom.xml (lub build.gradle)
├── src/
│   ├── main/
│   │   ├── java/com.techcorp.employee/
│   │   │   ├── EmployeeManagementApplication.java
│   │   │   ├── config/
│   │   │   │   └── AppConfig.java
│   │   │   ├── model/
│   │   │   ├── service/
│   │   │   └── exception/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── employees.csv
│   │       └── employees-beans.xml
│   └── test/
│       └── java/com.techcorp.employee/
│           └── service/
└── README.md
```

### Wymagania techniczne

- Wszystkie klasy serwisów zarządzane przez Spring Container jako beany
- Zabrania się używania operatora `new` dla klas z adnotacjami stereotypowymi
- Zależności wstrzykiwać wyłącznie przez konstruktor
- Parametry konfiguracyjne pobierane z `application.properties` przez `@Value`
- Klasa Employee musi mieć publiczny konstruktor z wszystkimi parametrami
- Pracownicy z XML dostępni jako bean `List<Employee>` o nazwie `xmlEmployees`
- Testy jednostkowe z poprzedniego zadania działają bez modyfikacji

### Polecenia do uruchomienia

**Maven:**
- `mvn spring-boot:run` - uruchomienie aplikacji
- `mvn test` - wykonanie testów
- `mvn package` - budowanie pakietu JAR

**Gradle:**
- `gradle bootRun` - uruchomienie aplikacji
- `gradle test` - wykonanie testów
- `gradle build` - budowanie projektu

### Oddanie

Link do repozytorium z zaktualizowanym kodem zawierającym:
- Plik konfiguracyjny pom.xml lub build.gradle z zależnościami Spring Boot
- Plik application.properties z parametrami konfiguracyjnymi
- Plik employees-beans.xml z definicjami beanów pracowników
- Przykładowy plik employees.csv
- README dokumentujący proces migracji

## Kryteria recenzji

- **Konfiguracja Spring Boot i adnotacje serwisów (25%)** - Poprawne zależności, wszystkie serwisy z `@Service`, wstrzykiwanie przez konstruktor, `application.properties` kompletny.
- **Konfiguracja beanów w klasie AppConfig (20%)** - Klasa z `@Configuration`, metody z `@Bean` dla HttpClient i Gson.
- **Definicja pracowników jako beanów w XML (30%)** - Plik XML ze strukturą beanów, `@ImportResource` w głównej klasie.
- **Klasa startowa i demonstracja funkcjonalności (25%)** - `@SpringBootApplication`, `@ImportResource`, `CommandLineRunner`, demonstracja wszystkich funkcjonalności.

Twoja odpowiedź zostanie oceniona przez autora kursu na podstawie powyższych kryteriów. Końcowa ocena będzie przyznana po zakończeniu recenzji.

---

# Zadanie 5

**Pytanie** (1 punkt)  
**Wymaga recenzji autora**

## System zarządzania pracownikami - REST API

System zarządzania pracownikami wymaga udostępnienia funkcjonalności poprzez interfejs programistyczny (API) zwracający dane w formacie JSON. Zadaniem jest stworzenie kontrolerów REST, które umożliwią innym aplikacjom konsumowanie danych systemu.

### Wymagania funkcjonalne

#### 1. Konfiguracja Spring Web

Dodać zależność `spring-boot-starter-web` do pom.xml lub build.gradle.

Konfiguracja w `application.properties`:

```properties
server.port=8080
spring.application.name=employee-management-api
spring.jackson.serialization.write-dates-as-timestamps=false
```

#### 2. Obiekty transferu danych (DTO)

Utworzyć klasy DTO oddzielające model wewnętrzny od reprezentacji API:

- **EmployeeDTO** - pola: `firstName`, `lastName`, `email`, `company`, `position`, `salary`, `status`
- **CompanyStatisticsDTO** - pola: `companyName`, `employeeCount`, `averageSalary`, `highestSalary`, `topEarnerName`
- **ErrorResponse** - pola: `message`, `timestamp`, `status`, `path`

#### 3. Kontroler REST dla pracowników

Klasa `EmployeeController` z `@RestController` i `@RequestMapping("/api/employees")`:

- `GET /api/employees` - zwraca listę wszystkich pracowników (`200 OK`)
- `GET /api/employees/{email}` - zwraca konkretnego pracownika (`200 OK` lub `404 Not Found`)
- `GET /api/employees?company=X` - filtruje po firmie
- `POST /api/employees` - tworzy pracownika (`201 Created` z nagłówkiem `Location`)
- `PUT /api/employees/{email}` - aktualizuje pracownika (`200 OK` lub `404 Not Found`)
- `DELETE /api/employees/{email}` - usuwa pracownika (`204 No Content` lub `404 Not Found`)

Wszystkie metody zwracają `ResponseEntity<T>`.

#### 4. Kontroler REST dla statystyk

Klasa `StatisticsController` z `@RestController` i `@RequestMapping("/api/statistics")`:

- `GET /api/statistics/salary/average` - średnie wynagrodzenie jako `Map<String, Double>`
- `GET /api/statistics/salary/average?company=X` - średnie dla firmy
- `GET /api/statistics/company/{companyName}` - szczegółowe statystyki firmy
- `GET /api/statistics/positions` - liczba pracowników na stanowisku
- `GET /api/statistics/status` - rozkład według statusu zatrudnienia

#### 5. Obsługa błędów

Klasa `GlobalExceptionHandler` z `@RestControllerAdvice` i metody z `@ExceptionHandler` dla:

- `EmployeeNotFoundException` → 404 Not Found
- `DuplicateEmailException` → 409 Conflict
- `InvalidDataException` → 400 Bad Request
- `IllegalArgumentException` → 400 Bad Request
- `Exception` → 500 Internal Server Error

#### 6. Status zatrudnienia

Dodać enum `EmploymentStatus` z wartościami: `ACTIVE`, `ON_LEAVE`, `TERMINATED`.

Dodatkowe endpointy:

- `PATCH /api/employees/{email}/status` - zmienia status (`200 OK`)
- `GET /api/employees/status/{status}` - lista pracowników o danym statusie (`200 OK`)

#### 7. Testy API z MockMvc

Napisać testy używające `@WebMvcTest` i `MockMvc`:

- Test GET wszystkich - weryfikacja 200 i zawartości JSON
- Test GET po emailu - weryfikacja zwróconych danych
- Test GET nieistniejącego - weryfikacja 404
- Test POST - weryfikacja 201 i nagłówka Location
- Test POST z duplikatem - weryfikacja 409
- Test DELETE - weryfikacja 204
- Test filtrowania po firmie
- Test PATCH zmiany statusu

### Struktura projektu

```
src/
├── main/
│   ├── java/com.techcorp.employee/
│   │   ├── EmployeeManagementApplication.java
│   │   ├── controller/
│   │   │   ├── EmployeeController.java
│   │   │   └── StatisticsController.java
│   │   ├── dto/
│   │   │   ├── EmployeeDTO.java
│   │   │   ├── CompanyStatisticsDTO.java
│   │   │   └── ErrorResponse.java
│   │   ├── service/
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── EmployeeNotFoundException.java
│   │   │   ├── DuplicateEmailException.java
│   │   │   └── InvalidDataException.java
│   │   └── model/
│   │       ├── Employee.java
│   │       ├── Position.java
│   │       └── EmploymentStatus.java (nowy)
│   └── resources/
│       └── application.properties
└── test/
    └── java/com.techcorp.employee/
        └── controller/
            ├── EmployeeControllerTest.java
            └── StatisticsControllerTest.java
```

### Wymagania techniczne

- Kontrolery używają `@RestController` (`@Controller` + `@ResponseBody`)
- Metody oznaczane: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`
- Parametry: `@PathVariable`, `@RequestParam`, `@RequestBody`
- Wszystkie metody zwracają `ResponseEntity<T>`
- DTO oddzielone od modelu domenowego
- Jackson automatycznie serializuje/deserializuje JSON
- Testy: `@WebMvcTest(NazwaKontrolera.class)`
- Serwisy mockowane przez `@MockBean`
- MockMvc do wykonywania żądań HTTP

### Przykłady testowania

```bash
# Uruchomienie
mvn spring-boot:run

# GET wszystkich
curl http://localhost:8080/api/employees

# POST nowy pracownik
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jan","lastName":"Kowalski","email":"jan@example.com","company":"TechCorp","position":"PROGRAMISTA","salary":8000,"status":"ACTIVE"}'

# PUT aktualizacja
curl -X PUT http://localhost:8080/api/employees/jan@example.com \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jan","lastName":"Kowalski","email":"jan@example.com","company":"TechCorp","position":"MANAGER","salary":12000,"status":"ACTIVE"}'

# PATCH zmiana statusu
curl -X PATCH http://localhost:8080/api/employees/jan@example.com/status \
  -H "Content-Type: application/json" \
  -d '{"status":"ON_LEAVE"}'

# DELETE
curl -X DELETE http://localhost:8080/api/employees/jan@example.com

# GET statystyki
curl http://localhost:8080/api/statistics/company/TechCorp
```

### Oddanie

Link do repozytorium z kodem, testami oraz README zawierającym listę wszystkich endpointów z przykładami żądań curl lub Postman oraz instrukcję uruchomienia.

## Kryteria recenzji

| Kryterium | Waga |
|-----------|------|
| **Kontrolery REST z operacjami CRUD (25%)** | Wszystkie endpointy z poprawnymi adnotacjami, `ResponseEntity` z odpowiednimi kodami HTTP |
| **Klasy DTO i mapowanie (20%)** | DTO poprawnie zdefiniowane, oddzielone od modelu, mapowanie działa w obu kierunkach |
| **Obsługa błędów (20%)** | `@RestControllerAdvice` z `@ExceptionHandler`, spójne `ErrorResponse` z właściwymi kodami HTTP |
| **Status zatrudnienia i endpointy (10%)** | Enum `EmploymentStatus`, endpoint PATCH, endpoint GET po statusie, statystyki |
| **Testy z MockMVC (25%)** | `@WebMvcTest`, scenariusze pozytywne i negatywne, weryfikacja przez `jsonPath()` |

Twoja odpowiedź zostanie oceniona przez autora kursu na podstawie powyższych kryteriów. Końcowa ocena będzie przyznana po zakończeniu recenzji.

---

# Zadanie 6

**Pytanie** (1 punkt)  
**Wymaga recenzji autora**

## Zadanie: Obsługa plików w aplikacji

### Kontekst

System zarządzania pracownikami wymaga możliwości przesyłania i pobierania plików przez API. Użytkownicy muszą móc importować dane z plików CSV i XML bez dostępu do serwera, generować raporty do pobrania oraz przesyłać dokumenty związane z pracownikami. Zadaniem jest rozszerzenie API o endpointy obsługujące upload i download plików z odpowiednią walidacją i obsługą błędów.

### Wymagania funkcjonalne

#### 1. Konfiguracja obsługi plików

W pliku `application.properties`:

```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
spring.servlet.multipart.enabled=true
app.upload.directory=uploads/
app.reports.directory=reports/
```

Katalogi `uploads/` i `reports/` tworzone automatycznie przy starcie aplikacji (w klasie z `@Component` implementującej `CommandLineRunner`).

#### 2. Serwis do zarządzania plikami

Utworzyć serwis `FileStorageService` z `@Service`:

- Metoda zapisywania pliku (`MultipartFile` → nazwa zapisanego pliku)
- Metoda odczytywania pliku (nazwa → `Resource`)
- Metoda usuwania pliku
- Metoda walidacji (rozszerzenie i rozmiar)
- Ścieżki katalogów wstrzykiwane przez `@Value`

#### 3. Upload plików CSV i XML

Kontroler `FileUploadController` z `@RequestMapping("/api/files")`:

- `POST /api/files/import/csv` - przyjmuje CSV, waliduje, zapisuje, importuje, zwraca `ImportSummary` (200 OK)
- `POST /api/files/import/xml` - analogicznie dla XML

Pliki przyjmowane przez `@RequestParam("file") MultipartFile file`.

#### 4. Generowanie i download raportów

- `GET /api/files/export/csv` - generuje CSV wszystkich pracowników (`ResponseEntity<Resource>`)
- `GET /api/files/export/csv?company=X` - CSV dla firmy
- `GET /api/files/reports/statistics/{companyName}` - raport PDF (użyć iText lub Apache PDFBox)

Nagłówki:
- `Content-Type: text/csv` lub `application/pdf`
- `Content-Disposition: attachment; filename="employees.csv"`

#### 5. Upload dokumentów pracowniczych

Model `EmployeeDocument`:
- Pola: `id`, `employeeEmail`, `fileName`, `originalFileName`, `fileType` (enum: CONTRACT, CERTIFICATE, ID_CARD, OTHER), `uploadDate`, `filePath`
- Metadane w pamięci (mapa w serwisie, bez JPA)

Endpointy:
- `POST /api/files/documents/{email}` - upload dokumentu (201 Created)
- `GET /api/files/documents/{email}` - lista dokumentów pracownika
- `GET /api/files/documents/{email}/{documentId}` - pobierz dokument
- `DELETE /api/files/documents/{email}/{documentId}` - usuń dokument (204 No Content)

Pliki w `uploads/documents/{email}/`.

#### 6. Upload i wyświetlanie zdjęć pracowników

- `POST /api/files/photos/{email}` - upload zdjęcia (JPG, PNG, max 2MB)
- `GET /api/files/photos/{email}` - pobierz zdjęcie (lub 404)

Dodać pole `photoFileName` do modelu `Employee`.  
Zdjęcia w `uploads/photos/`.

#### 7. Walidacja i obsługa błędów plików

Dedykowane wyjątki:
- `FileStorageException` → 500 Internal Server Error
- `InvalidFileException` → 400 Bad Request
- `FileNotFoundException` → 404 Not Found
- `MaxUploadSizeExceededException` → 413 Payload Too Large

Walidacja:
- Rozszerzenie pliku
- Rozmiar (`MultipartFile.getSize()`)
- Typ MIME (`MultipartFile.getContentType()`)

#### 8. Testy z MockMultipartFile

- Test uploadu CSV - `MockMultipartFile`, żądanie `multipart()`, weryfikacja 200 OK
- Test zbyt dużego pliku - weryfikacja 413
- Test nieprawidłowego rozszerzenia - weryfikacja 400
- Test downloadu CSV - weryfikacja nagłówków i zawartości
- Test uploadu dokumentu - weryfikacja 201 i metadanych

### Struktura projektu

```
src/
├── main/
│   ├── java/com.techcorp.employee/
│   │   ├── EmployeeManagementApplication.java
│   │   ├── controller/
│   │   │   ├── EmployeeController.java
│   │   │   ├── StatisticsController.java
│   │   │   └── FileUploadController.java (nowy)
│   │   ├── service/
│   │   │   ├── EmployeeService.java
│   │   │   ├── ImportService.java
│   │   │   ├── FileStorageService.java (nowy)
│   │   │   └── ReportGeneratorService.java (nowy)
│   │   ├── model/
│   │   │   ├── Employee.java (z polem photoFileName)
│   │   │   ├── EmployeeDocument.java (nowy)
│   │   │   └── DocumentType.java (nowy enum)
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── FileStorageException.java (nowy)
│   │   │   ├── InvalidFileException.java (nowy)
│   │   │   └── FileNotFoundException.java (nowy)
│   │   └── dto/
│   └── resources/
│       └── application.properties
└── test/
    └── java/com.techcorp.employee/
        ├── controller/
        │   └── FileUploadControllerTest.java (nowy)
        └── service/
            └── FileStorageServiceTest.java (nowy)
```

### Wymagania techniczne

- Kontroler przyjmuje pliki: `@RequestParam("file") MultipartFile file`
- Zwracanie plików: `ResponseEntity<Resource>`
- Nagłówki HTTP przez `HttpHeaders`
- Zapis plików: `Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING)`
- Odczyt: `new UrlResource(filePath.toUri())`
- Testy: `MockMultipartFile`, metoda `multipart()` w MockMvc

### Uwaga

Nie używaj zmiennej **BAOS**

### Przykłady testowania

```bash
# Upload CSV
curl -X POST http://localhost:8080/api/files/import/csv \
  -F "file=@employees.csv"

# Download raportu CSV
curl http://localhost:8080/api/files/export/csv \
  --output employees_export.csv

# Upload dokumentu
curl -X POST http://localhost:8080/api/files/documents/jan@example.com \
  -F "file=@contract.pdf" \
  -F "type=CONTRACT"

# Lista dokumentów
curl http://localhost:8080/api/files/documents/jan@example.com

# Upload zdjęcia
curl -X POST http://localhost:8080/api/files/photos/jan@example.com \
  -F "file=@photo.jpg"

# Pobranie zdjęcia
curl http://localhost:8080/api/files/photos/jan@example.com \
  --output photo.jpg
```

### Oddanie

Link do repozytorium z kodem, testami, przykładowymi plikami CSV i XML oraz README zawierającym listę endpointów do obsługi plików z przykładami curl, instrukcję konfiguracji katalogów oraz opis architektury przechowywania plików.

## Kryteria recenzji

- **Upload plików CSV/XML i import danych (25%)** - Endpointy POST, walidacja formatu i rozmiaru, `ImportSummary`
- **Generowanie i download raportów (25%)** - Raporty CSV i PDF, nagłówki HTTP, parametryzacja
- **Dokumenty pracownicze (20%)** - Model `EmployeeDocument`, endpointy CRUD, organizacja plików
- **Zdjęcia pracowników i walidacja (15%)** - Upload zdjęć, walidacja JPG/PNG (max 2MB), pole `photoFileName`
- **Testy z MockMultipartFile (15%)** - Testy uploadów, downloadów, błędów

Twoja odpowiedź zostanie oceniona przez autora kursu na podstawie powyższych kryteriów. Końcowa ocena będzie przyznana po zakończeniu recenzji.

# Zadanie 7
Interfejs użytkownika webowego (1 punkt)
## Kontekst

System zarządzania pracownikami posiada API REST umożliwiające programistyczną integrację, ale wymaga również interfejsu graficznego dla użytkowników końcowych. Zadaniem jest stworzenie aplikacji webowej z dynamicznymi widokami HTML, formularzami oraz kontrolerami MVC, które będą renderować strony zamiast zwracać JSON.

## Wymagania funkcjonalne

### 1. Konfiguracja Thymeleaf

Dodać zależność spring-boot-starter-thymeleaf do projektu.

Konfiguracja w application.properties:
```
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML
```
Wyłączenie cache pozwala na widoczność zmian w widokach bez restartu aplikacji podczas developmentu. W produkcji cache powinien być włączony dla wydajności.

### 2. Struktura widoków i wspólny layout

Utworzyć katalog src/main/resources/templates/ zawierający szablony HTML. Wszystkie widoki powinny dziedziczyć ze wspólnego layoutu zawierającego nawigację i podstawową strukturę strony.

Plik layout.html powinien zawierać sekcję <head> z linkami do stylów CSS, nawigację z linkami do głównych sekcji aplikacji (Pracownicy, Departamenty, Statystyki, Pliki) oraz sekcję <main> gdzie będzie wstawiany konkretny widok. Można użyć Thymeleaf Layout Dialect lub prostych fragmentów.

Podstawowy plik style.css w katalogu src/main/resources/static/css/ powinien zawierać style dla tabel z obramowaniem i efektem hover, style dla formularzy z wyraźnymi polami input i przyciskami, style dla komunikatów sukcesu i błędów oraz podstawowy układ strony z centrowaniem contentu.

### 3. Kontroler MVC dla pracowników

Klasa EmployeeViewController z adnotacją @Controller i @RequestMapping("/employees") obsługuje widoki dla pracowników. Różnica między @Controller a @RestController polega na tym, że metody zwracają nazwy widoków zamiast danych JSON, a Spring renderuje odpowiedni szablon Thymeleaf.

Endpoint GET /employees zwraca widok z listą wszystkich pracowników. Metoda przyjmuje obiekt Model do którego dodaje listę pracowników przez model.addAttribute("employees", employeeService.getAllEmployees()) i zwraca String "employees/list" wskazujący na szablon templates/employees/list.html.

Endpoint GET /employees/add zwraca formularz dodawania nowego pracownika. Metoda dodaje do modelu pusty obiekt Employee przez model.addAttribute("employee", new Employee()) który będzie bindowany do formularza, oraz listę wszystkich możliwych wartości enum Position i EmploymentStatus potrzebnych do wypełnienia selectów w formularzu.

Endpoint POST /employees/add przetwarza formularz używając @ModelAttribute Employee employee co automatycznie binduje pola z formularza do obiektu. Metoda zapisuje pracownika przez serwis, dodaje komunikat sukcesu do RedirectAttributes używając redirectAttributes.addFlashAttribute("message", "Pracownik dodany pomyślnie") i wykonuje przekierowanie return "redirect:/employees". Wzorzec Post-Redirect-Get zapobiega ponownemu wysłaniu formularza przy odświeżeniu strony.

Endpoint GET /employees/edit/{email} zwraca formularz edycji pracownika. Metoda pobiera pracownika po emailu, dodaje go do modelu oraz listy enumów i zwraca widok formularza edycji.

Endpoint POST /employees/edit przetwarza aktualizację danych używając @ModelAttribute i przekierowuje po zapisie.

Endpoint GET /employees/delete/{email} usuwa pracownika i przekierowuje do listy z komunikatem. Dla operacji DELETE można użyć formularza z ukrytym polem _method i filtra HiddenHttpMethodFilter, ale prostsza implementacja przez GET jest akceptowalna w tym zadaniu.

Endpoint GET /employees/search wyświetla formularz wyszukiwania po firmie z polem tekstowym.

Endpoint POST /employees/search przetwarza wyszukiwanie przyjmując @RequestParam("company") String company, filtruje pracowników i zwraca widok z wynikami.

### 4. Widoki Thymeleaf dla pracowników

Plik templates/employees/list.html wyświetla tabelę z pracownikami. Nagłówek zawiera link do formularza dodawania oraz ewentualny komunikat flash. Tabela używa th:each="employee : ${employees}" do iteracji po kolekcji pracowników, th:text="${employee.firstName}" do wyświetlania wartości pól oraz th:href="@{/employees/edit/{email}(email=${employee.email})}" do generowania dynamicznych linków. Kolumna akcji zawiera linki do edycji i usuwania.

Plik templates/employees/add-form.html zawiera formularz z atrybutami th:action="@{/employees/add}", th:object="${employee}" bindującym formularz do obiektu oraz th:field="*{firstName}" dla każdego pola co automatycznie generuje atrybuty name, id i value oraz binduje wartości w obie strony. Select dla Position używa th:each="pos : ${positions}" do iteracji po enumie i th:value="${pos}" oraz th:text="${pos}" do wypełnienia opcji.

Plik templates/employees/edit-form.html jest analogiczny do add-form ale action wskazuje na /employees/edit a pola są wypełnione wartościami edytowanego pracownika.

Plik templates/employees/search-form.html zawiera prosty formularz z jednym polem tekstowym dla nazwy firmy.

Plik templates/employees/search-results.html wyświetla wyniki wyszukiwania w analogicznej tabeli jak list.html ale z dodatkową informacją o kryterium wyszukiwania.

### 5. Nowa funkcjonalność: Departamenty

Wprowadzić nową encję domenową reprezentującą departament w firmie. Klasa Department powinna zawierać pola: id jako Long, name jako String, location jako String opisujący lokalizację biura departamentu, budget jako double reprezentujący budżet departamentu oraz managerEmail jako String wskazujący na pracownika będącego managerem departamentu.

Serwis DepartmentService zarządza departamentami przechowując je w pamięci w mapie gdzie kluczem jest id. Serwis oferuje metody CRUD analogiczne do EmployeeService: dodawanie nowego departamentu z automatycznym generowaniem id, pobieranie wszystkich departamentów, pobieranie po id, aktualizację oraz usuwanie.

Kontroler DepartmentViewController z mapowaniem /departments obsługuje widoki dla departamentów implementując analogiczne endpointy jak dla pracowników: listę, formularz dodawania, formularz edycji oraz usuwanie. Formularz departamentu zawiera select do wyboru managera z listy wszystkich pracowników na stanowisku MANAGER lub wyższym.

Widoki w katalogu templates/departments/ zawierają list.html z tabelą departamentów pokazującą nazwę, lokalizację, budżet i nazwisko managera (pobrane z EmployeeService po emailu), form.html z polami dla wszystkich atrybutów departamentu oraz selectem dla managera oraz details.html pokazujący szczegóły departamentu wraz z listą pracowników przypisanych do tego departamentu (rozszerzenie modelu Employee o pole departmentId).

### 6. Upload plików przez formularz HTML

Rozszerzyć kontroler o endpoint GET /employees/import zwracający formularz z polem input typu file do wyboru pliku CSV lub XML oraz radio buttons lub select do wyboru typu pliku. Atrybut enctype="multipart/form-data" w tagu form jest wymagany dla uploadów plików.

Endpoint POST /employees/import przyjmuje @RequestParam("file") MultipartFile file oraz @RequestParam("fileType") String fileType, wywołuje odpowiednią metodę ImportService i przekierowuje do listy pracowników z komunikatem zawierającym ImportSummary (liczba zaimportowanych, liczba błędów). W przypadku błędów można wyświetlić szczegółową listę błędów w osobnym widoku.

Analogicznie endpoint GET /departments/documents/{id} wyświetla listę dokumentów związanych z departamentem z możliwością uploadu nowych plików oraz downloadu istniejących. Upload wykorzystuje endpoint z poprzedniego zadania ale wywołany z formularza HTML zamiast przez API.

### 7. Wyświetlanie statystyk

Kontroler StatisticsViewController z mapowaniem /statistics oferuje endpoint GET /statistics wyświetlający dashboard ze statystykami. Widok zawiera sekcję z ogólnymi statystykami (liczba pracowników, średnie wynagrodzenie, liczba departamentów), sekcję z tabelą statystyk per firma (wykorzystanie CompanyStatistics z serwisu) oraz sekcję z rozkładem pracowników po stanowiskach wyświetlaną jako lista lub prosta wizualizacja tekstowa.

Można dodać endpoint GET /statistics/company/{name} wyświetlający szczegółową stronę ze statystykami konkretnej firmy zawierającą wykresy lub bardziej rozbudowane analizy. Na tym etapie wystarczą proste tabele bez JavaScript, ale można wspomnieć w README o możliwości rozszerzenia o wykresy używając bibliotek jak Chart.js.

### 8. Walidacja formularzy i wyświetlanie błędów

Kontrolery powinny sprawdzać poprawność danych z formularzy. W metodach POST można używać BindingResult zaraz po parametrze @ModelAttribute do przechwytywania błędów bindowania. Jeśli bindingResult.hasErrors() jest true, metoda zwraca ponownie widok formularza zachowując wprowadzone dane i wyświetlając błędy.

W widokach Thymeleaf błędy wyświetlane są przez th:if="${#fields.hasErrors('firstName')}" sprawdzający czy dane pole ma błąd oraz th:errors="*{firstName}" wyświetlający komunikat błędu. Ogólne komunikaty błędów wyświetlane są przez th:if="${#fields.hasAnyErrors()}".

Flash messages przekazywane przez RedirectAttributes wyświetlane są w layoutcie lub na początku każdej strony używając th:if="${message}" i th:text="${message}" z odpowiednim stylowaniem CSS dla komunikatów sukcesu (zielone tło) i błędów (czerwone tło).

Struktura projektu
```
src/
├── main/
│   ├── java/com.techcorp.employee/
│   │   ├── controller/
│   │   │   ├── EmployeeController.java (REST z zadania 5)
│   │   │   ├── FileUploadController.java (z zadania 6)
│   │   │   ├── EmployeeViewController.java (nowy MVC)
│   │   │   ├── DepartmentViewController.java (nowy MVC)
│   │   │   └── StatisticsViewController.java (nowy MVC)
│   │   ├── service/
│   │   │   ├── EmployeeService.java
│   │   │   └── DepartmentService.java (nowy)
│   │   └── model/
│   │       ├── Employee.java (z polem departmentId)
│   │       └── Department.java (nowy)
│   └── resources/
│       ├── templates/
│       │   ├── layout.html
│       │   ├── index.html (strona główna)
│       │   ├── employees/
│       │   │   ├── list.html
│       │   │   ├── add-form.html
│       │   │   ├── edit-form.html
│       │   │   ├── search-form.html
│       │   │   ├── search-results.html
│       │   │   └── import-form.html
│       │   ├── departments/
│       │   │   ├── list.html
│       │   │   ├── form.html
│       │   │   └── details.html
│       │   └── statistics/
│       │       └── index.html
│       ├── static/
│       │   └── css/
│       │       └── style.css
│       └── application.properties
└── test/
    └── java/com.techcorp.employee/
        └── controller/
            ├── EmployeeViewControllerTest.java (nowy)
            └── DepartmentViewControllerTest.java (nowy)
```
## Wymagania techniczne

Kontrolery MVC używają adnotacji @Controller zamiast @RestController. Metody zwracają nazwy widoków jako String co powoduje renderowanie szablonu Thymeleaf zamiast zwracanie danych JSON. Parametr Model wstrzykiwany do metod służy do przekazywania danych do widoku przez model.addAttribute().

Formularze bindowane do obiektów używając th:object i th:field. Atrybuty th:field automatycznie generują name, id oraz bindują wartości w obie strony (wyświetlanie i przechwytywanie). Przekierowania po POST używają wzorca Post-Redirect-Get dla uniknięcia ponownego wysłania formularza przy refresh.

Flash messages przekazywane przez RedirectAttributes.addFlashAttribute() przetrwają jedno przekierowanie i będą dostępne w widoku docelowym. URL-e generowane przez @{/path} co automatycznie dodaje context path aplikacji.

Testy kontrolerów MVC używają @WebMvcTest i MockMvc analogicznie jak dla REST API, ale weryfikują zwracane nazwy widoków używając andExpect(view().name("employees/list")) oraz obecność atrybutów w modelu przez andExpect(model().attributeExists("employees")).

## Przykłady dostępu

Po uruchomieniu aplikacji otwórz przeglądarkę i przejdź do:

- http://localhost:8080/ - strona główna
- http://localhost:8080/employees - lista pracowników
- http://localhost:8080/employees/add - formularz dodawania
- http://localhost:8080/departments - lista departamentów
- http://localhost:8080/statistics - dashboard statystyk
- http://localhost:8080/employees/import - formularz importu plików

Link do repozytorium z kodem kontrolerów MVC, widoków Thymeleaf, pliku CSS oraz README zawierającym screenshoty głównych stron aplikacji, opis nawigacji między sekcjami oraz instrukcję uruchomienia i testowania przez przeglądarkę.

## Kryteria recenzji:
Kontrolery MVC i routing (25%)
Wszystkie kontrolery używają @Controller i zwracają nazwy widoków. Przekazywanie danych przez Model. Obsługa formularzy z @ModelAttribute. Poprawne przekierowania po POST z flash messages. Wzorzec Post-Redirect-Get.
Widoki Thymeleaf (30%)
Wszystkie wymagane widoki utworzone z prawidłową składnią Thymeleaf. Używanie th:each, th:text, th:href, th:object, th:field. Wspólny layout z nawigacją. Dynamiczne generowanie URL przez @{...}. Wyświetlanie komunikatów i błędów.
Formularze z bindowaniem (20%)
Formularze poprawnie bindowane do obiektów. Pola th:field generują atrybuty i wartości. Selecty dla enumów. Walidacja danych i wyświetlanie błędów. Upload plików przez formularz HTML z enctype="multipart/form-data".
Departamenty jako nowa funkcjonalność (15%)
Model Department i serwis DepartmentService utworzone. Widoki CRUD dla departamentów. Select do wyboru managera z listy pracowników. Szczegóły departamentu z listą przypisanych pracowników. Pole departmentId w Employee.
Style CSS i UX (10%)
Podstawowy plik CSS ze stylami dla tabel, formularzy, komunikatów. Efekt hover na tabelach. Wyraźne przyciski i pola. Nawigacja między sekcjami. Komunikaty sukcesu i błędów z odpowiednimi kolorami. Responsywny układ.
Twoja odpowiedź zostanie oceniona przez autora kursu na podstawie powyższych kryteriów. Końcowa ocena będzie przyznana po zakończeniu recenzji.