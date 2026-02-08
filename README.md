# 🚗 Sistem de Gestiune Închirieri Auto (Car Rental System)

O aplicație desktop robustă pentru gestionarea unei afaceri de închirieri auto, dezvoltată în **Java** și **JavaFX**. Proiectul este construit pe o **Arhitectură Stratificată (Layered Architecture)**, demonstrând principii solide de Software Engineering și flexibilitate în stocarea datelor (SQL, Fișiere, Memorie).

## 📌 Descriere Proiect
Acest proiect a fost conceput pentru a evidenția separarea clară a logicii de business față de interfața cu utilizatorul și accesul la date. Aplicația permite operatorilor să gestioneze flota de mașini, baza de clienți și contractele de închiriere, oferind în același timp rapoarte și statistici.

### Funcționalități Cheie
* **Operațiuni CRUD:** Gestionare completă (Adăugare, Citire, Actualizare, Ștergere) pentru Mașini, Clienți și Închirieri.
* **Interfață Duală (Polimorfism UI):**
    * 🖥️ **GUI (JavaFX):** Interfață grafică modernă, cu tabele și formulare intuitive.
    * 💻 **Consolă (CLI):** Interfață text pentru operațiuni rapide și administrare server-side.
* **Persistență Flexibilă a Datelor:**
    * **Bază de date SQL:** Stocare persistentă folosind **SQLite** și **JDBC**.
    * **Fișiere:** Suport pentru serializare binară (`.bin`) și fișiere text (`.txt`).
    * **In-Memory:** Pentru testare rapidă fără dependențe externe.
* **Validarea Datelor:** Validatori personalizați pentru a asigura integritatea informațiilor introduse.
* **Statistici și Rapoarte:**
    * Cele mai închiriate mașini.
    * Clienți fideli.
    * Venituri per perioadă.

## 🛠️ Tehnologii Folosite
* **Limbaj:** Java (JDK 17+)
* **Build System:** Maven
* **Interfață Grafică:** JavaFX
* **Bază de Date:** SQLite (JDBC)
* **Testing:** JUnit 5
* **Utilitare:** JavaFaker (pentru generarea datelor de test)

## 🏗️ Arhitectură și Design Patterns
Aplicația respectă o arhitectură modulară strictă:

1.  **Domain Layer:** Clase POJO (`Car`, `Client`, `Rental`) care definesc entitățile principale.
2.  **Repository Layer:** Abstractizarea accesului la date folosind **Repository Pattern**.
    * Interfața `IRepository<ID, T>` asigură contractul pentru operațiile de stocare.
    * Clasele concrete (`SQLRepository`, `BinaryFileRepository`, etc.) pot fi schimbate ușor fără a afecta restul aplicației.
    * **Factory Pattern:** Clasa `RepositoryFactory` decide la runtime ce tip de stocare să inițializeze.
3.  **Service Layer:** Conține logica de business, calculele și validările.
4.  **UI Layer:** Complet decuplat de logică; comunică doar cu Service-ul.

## ⚙️ Configurare
Comportamentul aplicației este controlat prin fișierul `src/main/resources/settings.properties`. Poți schimba tipul de stocare sau interfața fără a recompila codul.

```properties
# Optiuni Stocare: memory, text, binary, sql
Repository_Car = sql
Repository_Rental = sql
Repository_Client = sql

# Conexiune Baza de Date
Db_Url = jdbc:sqlite:rental.db

# Mod Pornire: gui sau console
Start_Mode = gui
