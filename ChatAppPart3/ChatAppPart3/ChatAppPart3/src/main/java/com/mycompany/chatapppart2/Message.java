package com.mycompany.chatapppart2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds one QuickChat message, the arrays of all messages, and the behaviour related to them.
 *
 * Attribution: JSON reading/writing uses the Gson library
 * (https://github.com/google/gson/blob/main/UserGuide.md).
 * Add any other source you researched here.
 */
public class Message {

    public static final int MAX_LENGTH = 250;
    private static final String DEFAULT_STORAGE_PATH = "storedMessages.json";
    private static final SecureRandom RANDOM = new SecureRandom();

    // ---------- Arrays (populated by the program, never hard-coded) ----------
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();

    // Parallel arrays: index i in each describes the same message
    private static final List<Message> allMessages = new ArrayList<>();
    private static final List<String> messageIds = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();

    private static int totalSent = 0;
    private static String storagePath = DEFAULT_STORAGE_PATH;

    private final String messageId;
    private final int messageNumber;
    private final String recipient;
    private final String text;
    private final String messageHash;
    private String sender = "Unknown";

    /** Normal constructor: generates a random ten-digit ID. */
    public Message(int messageNumber, String recipient, String text) {
        this(generateMessageID(), messageNumber, recipient, text);
    }

    /** Constructor with a known ID (used by unit tests and developer entries). */
    public Message(String messageId, int messageNumber, String recipient, String text) {
        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    // ---------- ID ----------

    /** Random ten-digit ID kept as a String so leading zeroes survive. */
    public static String generateMessageID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /** True when the ID is exactly ten digits. */
    public boolean checkMessageID() {
        return messageId != null && messageId.matches("\\d{10}");
    }

    // ---------- Recipient ----------

    /** International format: '+' then digits (e.g. +27718693002). */
    public static String validateRecipient(String recipient) {
        if (recipient != null && recipient.matches("\\+\\d{10,13}")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    public String checkRecipientCell() {
        return validateRecipient(recipient);
    }

    // ---------- Length ----------

    public static String validateMessageLength(String text) {
        if (text.length() <= MAX_LENGTH) {
            return "Message ready to send.";
        }
        int excess = text.length() - MAX_LENGTH;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    public String checkMessageLength() {
        return validateMessageLength(text);
    }

    // ---------- Hash ----------

    /** Format  first2OfID:messageNumber:FIRSTLAST  in uppercase. */
    public String createMessageHash() {
        String[] words = text.trim().split("\\s+");
        String first = stripPunctuation(words[0]);
        String last = stripPunctuation(words[words.length - 1]);
        String prefix = messageId.substring(0, 2);
        return (prefix + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    /** Removes non-letter/digit characters from both ends of a word. */
    private static String stripPunctuation(String word) {
        return word.replaceAll("^[^A-Za-z0-9]+", "").replaceAll("[^A-Za-z0-9]+$", "");
    }

    // ---------- Send / Disregard / Store ----------

    /** choice: 1 = send, 2 = disregard, 3 = store. */
    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalSent++;
                record(this);
                return "Message successfully sent.";
            case 2:
                disregardedMessages.add(this);
                record(this);
                return "Press 0 to delete the message.";
            case 3:
                record(this);
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid choice.";
        }
    }

    /** Adds the message ID and hash to the parallel arrays (once per message). */
    private static void record(Message m) {
        if (!messageIds.contains(m.messageId)) {
            allMessages.add(m);
            messageIds.add(m.messageId);
            messageHashes.add(m.messageHash);
        }
    }

    // ---------- Reporting (sent messages) ----------

    public String getDetails() {
        return "Message ID: " + messageId + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + text;
    }

    public String getFullDetails() {
        return "Message ID: " + messageId + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Sender: " + sender + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + text;
    }

    /** Returns every message sent in this run, in the required order. */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.getDetails()).append("\n\n");
        }
        return sb.toString();
    }

    public static int returnTotalMessagess() {
        return totalSent;
    }

    // ---------- Array getters (copies, so callers cannot change the arrays) ----------

    public static List<String> getSentMessageTexts() { return textsOf(sentMessages); }
    public static List<String> getDisregardedMessageTexts() { return textsOf(disregardedMessages); }
    public static List<String> getStoredMessageTexts() { return textsOf(storedMessages); }
    public static List<String> getMessageIds() { return new ArrayList<>(messageIds); }
    public static List<String> getMessageHashes() { return new ArrayList<>(messageHashes); }

    private static List<String> textsOf(List<Message> list) {
        List<String> texts = new ArrayList<>();
        for (Message m : list) {
            texts.add(m.text);
        }
        return texts;
    }

    // ---------- Stored-message features (menu option 4) ----------

    /** a. Sender and recipient of every stored message. */
    public static String displaySendersAndRecipients() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Sender: ").append(m.sender)
              .append(" | Recipient: ").append(m.recipient).append("\n");
        }
        return sb.toString().trim();
    }

    /** Text of the longest message across all messages. */
    public static String displayLongestMessage() {
        Message longest = findLongest(allMessages);
        return longest == null ? "No messages found." : longest.text;
    }

    /** b. Full details of the longest stored message. */
    public static String longestStoredMessageDetails() {
        Message longest = findLongest(storedMessages);
        return longest == null ? "No stored messages." : longest.getFullDetails();
    }

    private static Message findLongest(List<Message> list) {
        Message longest = null;
        for (Message m : list) {
            if (longest == null || m.text.length() > longest.text.length()) {
                longest = m;
            }
        }
        return longest;
    }

    /** c. Searches the parallel arrays for a message ID. Returns the message or null. */
    public static Message findByMessageID(String id) {
        for (int i = 0; i < messageIds.size(); i++) {
            if (messageIds.get(i).equals(id)) {
                return allMessages.get(i);
            }
        }
        return null;
    }

    public static String searchByMessageID(String id) {
        Message found = findByMessageID(id);
        return found == null ? "Message ID not found." : found.text;
    }

    /** d. All sent or stored message texts for one recipient. */
    public static List<String> searchByRecipient(String recipient) {
        List<String> results = new ArrayList<>();
        for (Message m : sentMessages) {
            if (m.recipient.equals(recipient)) {
                results.add(m.text);
            }
        }
        for (Message m : storedMessages) {
            if (m.recipient.equals(recipient)) {
                results.add(m.text);
            }
        }
        return results;
    }

    /** e. Deletes a stored message using its hash and updates the JSON file. */
    public static String deleteMessageByHash(String hash) {
        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).messageHash.equals(hash)) {
                Message removed = storedMessages.remove(i);
                int index = messageIds.indexOf(removed.messageId);
                if (index >= 0) {
                    allMessages.remove(index);
                    messageIds.remove(index);
                    messageHashes.remove(index);
                }
                saveStoredMessages();
                return "Message: \"" + removed.text + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    /** f. Report listing the full details of all stored messages. */
    public static String reportOfStoredMessages() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append(m.getFullDetails()).append("\n\n");
        }
        return sb.toString().trim();
    }

    // ---------- Session / settings ----------

    /** Clears every array and counter (used by unit tests). */
    public static void resetSession() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        allMessages.clear();
        messageIds.clear();
        messageHashes.clear();
        totalSent = 0;
        storagePath = DEFAULT_STORAGE_PATH;
    }

    public static void setStoragePath(String path) {
        storagePath = path;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    // ---------- JSON storage ----------

    /** Adds this message to the stored array and saves the array to the JSON file. */
    public boolean storeMessage() {
        if (!storedMessages.contains(this)) {
            storedMessages.add(this);
        }
        return saveStoredMessages();
    }

    /** Writes the whole stored-messages array to the JSON file. */
    private static boolean saveStoredMessages() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray array = new JsonArray();
        for (Message m : storedMessages) {
            JsonObject obj = new JsonObject();
            obj.addProperty("messageId", m.messageId);
            obj.addProperty("messageNumber", m.messageNumber);
            obj.addProperty("messageHash", m.messageHash);
            obj.addProperty("sender", m.sender);
            obj.addProperty("recipient", m.recipient);
            obj.addProperty("message", m.text);
            obj.addProperty("status", "STORED");
            array.add(obj);
        }
        try {
            Files.write(Paths.get(storagePath), gson.toJson(array).getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
            return false;
        }
    }

    /** Reads the JSON file into the stored-messages array. Returns how many were loaded. */
    public static int loadStoredMessages() {
        storedMessages.clear();
        Path path = Paths.get(storagePath);
        if (!Files.exists(path)) {
            return 0;
        }
        try {
            String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            if (json.trim().isEmpty()) {
                return 0;
            }
            JsonArray array = JsonParser.parseString(json).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                int number = obj.has("messageNumber") ? obj.get("messageNumber").getAsInt() : 0;
                Message m = new Message(obj.get("messageId").getAsString(), number,
                        obj.get("recipient").getAsString(), obj.get("message").getAsString());
                if (obj.has("sender")) {
                    m.setSender(obj.get("sender").getAsString());
                }
                storedMessages.add(m);
                record(m);
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not read stored messages: " + e.getMessage());
        }
        return storedMessages.size();
    }

    // ---------- Getters ----------

    public String getMessageId() { return messageId; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getText() { return text; }
    public String getMessageHash() { return messageHash; }
    public String getSender() { return sender; }
}
