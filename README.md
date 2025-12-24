# Generic Library Catalog System

A versatile, console-based library management application designed to demonstrate the practical implementation of **Java Generics**. This system efficiently manages different types of library items (Books, DVDs, Magazines) within a single, type-safe catalog structure.

## 📖 Overview

This project was developed as part of the CS 1103 Programming 2 course (Unit 6). The primary goal was to utilize generic classes and methods to create a flexible architecture that maximizes code reusability while ensuring strict type safety and data integrity.

### Key Features

*   **Generic Architecture:**
    *   Uses a `GenericCatalog<T>` class to manage collections.
    *   Implements an abstract `LibraryItem<ID>` base class allowing flexible ID types (e.g., `Integer` for DVDs, `String` for Books).
*   **Smart Search Engine:**
    *   **Omnibox Search:** Search by Title, Author, or ID simultaneously.
    *   **Type Filtering:** Supports flags like `-b` (Books), `-d` (DVDs), and `-m` (Magazines) to narrow results.
*   **Robust Data Integrity:**
    *   **Duplicate Prevention:** Proactively checks for duplicate content (Title + Author + Type) before addition.
    *   **ID Suggestions:** Auto-scans existing records to suggest the next available numeric ID.
    *   **Input Validation:** Enforces logic rules (e.g., DVD IDs must be integers; Authors must have valid names).
*   **User Experience:**
    *   **Global Cancellation:** Enter `-1` at any prompt to cancel the current operation.
    *   **Ambiguous Match Resolution:** If a deletion search yields multiple results, the user is prompted to select the specific item to remove.

## 🛠️ Technologies

*   **Language:** Java (JDK 21+)
*   **Concepts:** Generics (Bounded Types, Wildcards), Stream API, Functional Interfaces (`Predicate`), Exception Handling.

## 🚀 How to Run

### Method 1: Using the Pre-Compiled JAR
1.  Download the latest `.jar` from the [Releases](https://github.com/mwdiss/GenericLibraryCatalog/releases) page.
2.  Open your terminal or command prompt.
3.  Run the following command:
    ```bash
    java -jar GenericLibraryCatalog.jar
    ```

### Method 2: From Source
1.  Clone this repository.
2.  Navigate to the `src` directory.
3.  Compile the code:
    ```bash
    javac com/GenericLibrary/GenericLibrary.java
    ```
4.  Run the application:
    ```bash
    java com.GenericLibrary.GenericLibrary
    ```

## 🧩 Project Structure

*   `GenericLibrary.java`: Contains the main entry point and UI logic.
*   `GenericCatalog<T>`: The core generic collection manager.
*   `LibraryItem<ID>`: The abstract base class for all items.
*   `Book`, `Dvd`, `Magazine`: Concrete implementations with specific ID types.

## 📝 Usage Example

```text
=== Generic Library Catalog System ===
[1. Add Item | 2. Remove Item | 3. View Catalog]
Choice: 1

Item Type (1=Book, 2=DVD, 3=Magazine): 1
Item Title: Effective Java
Author (3+ letters): Joshua Bloch
>> Suggested ID: 101
Unique ID: 978-0134685991
>> Success: Item added.
```
