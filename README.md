# ChatAppPart2 (QuickChat)

PROG5121 Programming 1A: Portfolio of Evidence
Student: [Amyoli Louw] | Student number: [ST10535093]

A Java console application built with Maven. It combines registration and login (Part 1), sending messages (Part 2) and stored-message features (Part 3). Only a logged-in user can access QuickChat.

## Features

**Registration and login**
- Username must contain an underscore and be no more than 5 characters.
- Password must be at least 8 characters with a capital letter, a number and a special character.
- Cell phone number must be in South African international format (e.g. `+27838968976`).
- Login is checked against the registered details, and the user sees a success or error message.

**Sending messages**
- Welcome message and numeric menu:
  1. Send Messages
  2. Show recently sent messages
  3. Quit
  4. Stored Messages
- A `while` loop keeps the menu running until the user chooses Quit.
- The user sets how many messages to enter, and a `for` loop enforces that limit.
- Each message gets a random ten-digit message ID, a message number and an uppercase message hash (first two ID digits, message number, first and last words, e.g. `00:0:HITONIGHT`).
- The recipient must be in international format, and the message must be 250 characters or fewer.
- For each message the user can send it, disregard it or store it.
- Sent messages are displayed in this order: Message ID, Message Hash, Recipient, Message. The total sent is shown at the end.

**Arrays and stored messages**
- The program fills arrays for sent, disregarded and stored messages, plus parallel arrays of message IDs and hashes. Nothing is hard-coded.
- Stored messages are saved to `storedMessages.json` and read back into the stored-messages array when QuickChat starts.
- The Stored Messages menu (option 4) lets the user:
  1. Display the sender and recipient of all stored messages.
  2. Display the longest stored message.
  3. Search for a message ID and show the recipient and message.
  4. Search for all messages stored for a particular recipient.
  5. Delete a message using its message hash (the JSON file is updated too).
  6. Display a report of the full details of all stored messages.

## Project structure

```
ChatAppPart2/
├── pom.xml
├── .github/workflows/TestJava.yml
└── src/
    ├── main/java/com/mycompany/chatapppart2/
    │   ├── Main.java        (registration, login, starts QuickChat)
    │   ├── Login.java       (validation and authentication)
    │   ├── QuickChat.java   (menus and message entry flow)
    │   └── Message.java     (validation, ID, hash, send/store, arrays, JSON)
    └── test/java/com/mycompany/chatapppart2/
        ├── LoginTest.java
        ├── MessageTest.java
        └── StoredMessagesTest.java
```

## Requirements

- Java 17 (or newer)
- Maven
- Internet access on the first build (to download Gson and JUnit)

## How to run

From the project folder (the one containing `pom.xml`):

```
mvn clean compile exec:java -Dexec.mainClass=com.mycompany.chatapppart2.Main
```

Or open the project in NetBeans and run `Main.java`.

## How to run the tests

```
mvn clean test
```

The tests use JUnit 5 and check both successful and unsuccessful cases:
- `LoginTest`: username, password and cell number validation, registration, and login success and failure.
- `MessageTest`: recipient format, message length, message ID, hash creation, and the send, disregard and store choices.
- `StoredMessagesTest`: the arrays, the longest message, search by message ID, search by recipient, delete by hash, the reports, and reading the JSON file back, using the five test messages from the PoE.

## GitHub Actions

The workflow in `.github/workflows/TestJava.yml` runs `mvn -B test` on every push and pull request.

## Libraries and attribution

- Gson (https://github.com/google/gson) is used to read and write the stored messages as JSON.
- JUnit 5 is used for unit testing.
- [https://www.youtube.com/watch?v=Ihy0QziLDf0&list=PLZPZq0r_RZOO1zkgO4bIdfuLpizCeHYKv]

## Notes

- Message numbers in the app start at 1. The unit tests use controlled IDs and numbers so the results are predictable.
- Recipient numbers are accepted when they start with `+` followed by 10 to 13 digits.
- Stored messages are kept in `storedMessages.json` in the project folder.
