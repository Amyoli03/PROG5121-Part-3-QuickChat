package com.mycompany.chatapppart2;

import java.util.List;
import java.util.Scanner;

/** QuickChat menus and message-entry flow. Called from Main after a successful login. */
public class QuickChat {

    /** Runs QuickChat with a default sender name. */
    public static void run(Scanner scanner) {
        run(scanner, "User");
    }

    /** Runs the QuickChat menu. Only call this when login succeeded. */
    public static void run(Scanner scanner, String sender) {
        Message.loadStoredMessages(); // read previously stored messages from the JSON file

        System.out.println("\nWelcome to QuickChat.");
        int limit = readInt(scanner, "Enter the number of messages you wish to send: ");
        int processed = 0;
        int choice = 0;

        // while loop: keeps the menu running until the user chooses Quit
        while (choice != 3) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            choice = readInt(scanner, "Enter your choice: ");

            if (choice == 1) {
                if (processed >= limit) {
                    System.out.println("Message limit reached.");
                } else {
                    processed = enterMessages(scanner, processed, limit, sender);
                }
            } else if (choice == 2) {
                showSentMessages();
            } else if (choice == 3) {
                System.out.println("Goodbye.");
            } else if (choice == 4) {
                storedMessagesMenu(scanner);
            } else {
                System.out.println("Invalid option. Please choose 1, 2, 3 or 4.");
            }
        }
    }

    private static void showSentMessages() {
        String sent = Message.printMessages();
        System.out.println(sent.isEmpty() ? "No messages have been sent yet." : sent.trim());
    }

    /** for loop: allows only the user-defined number of messages. */
    private static int enterMessages(Scanner scanner, int processed, int limit, String sender) {
        for (int i = processed; i < limit; i++) {
            System.out.println("Creating message " + (i + 1) + " of " + limit + " ...");

            String recipient = askRecipient(scanner);
            String text = askText(scanner);
            Message msg = new Message(i + 1, recipient, text);
            msg.setSender(sender);

            System.out.println("Message ID generated: " + msg.getMessageId());
            int action = readInt(scanner, "1) Send  2) Disregard  3) Store : ");
            System.out.println(msg.SentMessage(action));

            if (action == 1) {
                System.out.println(msg.getDetails());
            } else if (action == 2) {
                readInt(scanner, "Enter 0 to confirm: ");
            }
            processed++;
        }
        System.out.println("Message limit reached.");
        System.out.println("Total messages successfully sent: " + Message.returnTotalMessagess());
        return processed;
    }

    // ---------- Stored Messages menu (option 4) ----------

    private static void storedMessagesMenu(Scanner scanner) {
        int option = 0;
        while (option != 7) {
            System.out.println("\nStored Messages");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for messages stored for a recipient");
            System.out.println("5) Delete a message using its hash");
            System.out.println("6) Display a report of all stored messages");
            System.out.println("7) Back to main menu");
            option = readInt(scanner, "Enter your choice: ");

            switch (option) {
                case 1:
                    System.out.println(Message.displaySendersAndRecipients());
                    break;
                case 2:
                    System.out.println(Message.longestStoredMessageDetails());
                    break;
                case 3:
                    searchByID(scanner);
                    break;
                case 4:
                    searchByRecipient(scanner);
                    break;
                case 5:
                    deleteByHash(scanner);
                    break;
                case 6:
                    System.out.println(Message.reportOfStoredMessages());
                    break;
                case 7:
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1 to 7.");
            }
        }
    }

    private static void searchByID(Scanner scanner) {
        System.out.print("Enter the message ID: ");
        Message found = Message.findByMessageID(scanner.nextLine().trim());
        if (found == null) {
            System.out.println("Message ID not found.");
        } else {
            System.out.println("Recipient: " + found.getRecipient());
            System.out.println("Message: " + found.getText());
        }
    }

    private static void searchByRecipient(Scanner scanner) {
        System.out.print("Enter the recipient number: ");
        List<String> results = Message.searchByRecipient(scanner.nextLine().trim());
        if (results.isEmpty()) {
            System.out.println("No messages found for that recipient.");
        } else {
            for (String text : results) {
                System.out.println(text);
            }
        }
    }

    private static void deleteByHash(Scanner scanner) {
        System.out.print("Enter the message hash: ");
        System.out.println(Message.deleteMessageByHash(scanner.nextLine().trim().toUpperCase()));
    }

    // ---------- Input helpers ----------

    private static String askRecipient(Scanner scanner) {
        while (true) {
            System.out.print("Enter recipient cell number (with international code, e.g. +27...): ");
            String number = scanner.nextLine().trim();
            String result = Message.validateRecipient(number);
            System.out.println(result);
            if (result.startsWith("Cell phone number successfully")) {
                return number;
            }
        }
    }

    private static String askText(Scanner scanner) {
        while (true) {
            System.out.print("Enter your message (max 250 characters): ");
            String text = scanner.nextLine();
            if (text.trim().isEmpty()) {
                System.out.println("Message cannot be empty.");
                continue;
            }
            System.out.println(Message.validateMessageLength(text));
            if (text.length() <= Message.MAX_LENGTH) {
                return text;
            }
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}
