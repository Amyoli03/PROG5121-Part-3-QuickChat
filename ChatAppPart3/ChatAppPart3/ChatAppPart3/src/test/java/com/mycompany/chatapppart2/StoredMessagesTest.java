package com.mycompany.chatapppart2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/** Part 3 tests using the five test messages from the PoE. */
public class StoredMessagesTest {

    private static final String TEST_FILE = "testStoredPart3.json";
    private static final String MSG1 = "Did you get the cake?";
    private static final String MSG2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG3 = "Yohoooo, I am at your gate.";
    private static final String MSG4 = "It is dinner time!";
    private static final String MSG5 = "Ok, I am leaving without you.";

    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;
    private Message message5;

    @BeforeEach
    void setUp() {
        Message.resetSession();
        Message.setStoragePath(TEST_FILE);
        new File(TEST_FILE).delete();

        message1 = new Message("1000000001", 1, "+27834557896", MSG1);
        message2 = new Message("2000000002", 2, "+27838884567", MSG2);
        message3 = new Message("3000000003", 3, "+27834484567", MSG3);
        message4 = new Message("0838884567", 4, "0838884567", MSG4); // developer entry
        message5 = new Message("5000000005", 5, "+27838884567", MSG5);

        message1.SentMessage(1); // Sent
        message2.SentMessage(3); // Stored
        message3.SentMessage(2); // Disregard
        message4.SentMessage(1); // Sent
        message5.SentMessage(3); // Stored
    }

    @AfterEach
    void tearDown() { new File(TEST_FILE).delete(); }

    @Test
    void sentMessagesArrayCorrectlyPopulated() {
        assertEquals(Arrays.asList(MSG1, MSG4), Message.getSentMessageTexts());
    }

    @Test
    void disregardedMessagesArrayCorrectlyPopulated() {
        assertEquals(Arrays.asList(MSG3), Message.getDisregardedMessageTexts());
    }

    @Test
    void storedMessagesArrayCorrectlyPopulated() {
        assertEquals(Arrays.asList(MSG2, MSG5), Message.getStoredMessageTexts());
    }

    @Test
    void messageIdAndHashArraysCorrectlyPopulated() {
        assertEquals(5, Message.getMessageIds().size());
        assertEquals(5, Message.getMessageHashes().size());
        assertTrue(Message.getMessageIds().contains("0838884567"));
        assertEquals(message2.getMessageHash(), Message.getMessageHashes().get(1));
    }

    @Test
    void displayLongestMessage() {
        assertEquals(MSG2, Message.displayLongestMessage());
    }

    @Test
    void longestStoredMessageDetailsContainLongestText() {
        assertTrue(Message.longestStoredMessageDetails().contains(MSG2));
    }

    @Test
    void searchForMessageIdFindsMessage() {
        assertEquals(MSG4, Message.searchByMessageID("0838884567"));
    }

    @Test
    void searchForUnknownMessageIdFails() {
        assertEquals("Message ID not found.", Message.searchByMessageID("9999999999"));
    }

    @Test
    void searchByRecipientReturnsSentAndStoredMessages() {
        assertEquals(Arrays.asList(MSG2, MSG5), Message.searchByRecipient("+27838884567"));
    }

    @Test
    void searchByUnknownRecipientReturnsNothing() {
        assertTrue(Message.searchByRecipient("+27000000000").isEmpty());
    }

    @Test
    void deleteMessageUsingHash() {
        String result = Message.deleteMessageByHash(message2.getMessageHash());
        assertEquals("Message: \"" + MSG2 + "\" successfully deleted.", result);
        assertEquals(Arrays.asList(MSG5), Message.getStoredMessageTexts());
        assertFalse(Message.getMessageIds().contains("2000000002"));
    }

    @Test
    void deleteWithUnknownHashFails() {
        assertEquals("Message hash not found.", Message.deleteMessageByHash("NOPE"));
        assertEquals(2, Message.getStoredMessageTexts().size());
    }

    @Test
    void displayReportOfSentMessages() {
        String expected =
                "Message ID: 1000000001\nMessage Hash: " + message1.getMessageHash()
                + "\nRecipient: +27834557896\nMessage: " + MSG1 + "\n\n"
                + "Message ID: 0838884567\nMessage Hash: " + message4.getMessageHash()
                + "\nRecipient: 0838884567\nMessage: " + MSG4 + "\n\n";
        assertEquals(expected, Message.printMessages());
    }

    @Test
    void reportOfStoredMessagesShowsFullDetails() {
        String report = Message.reportOfStoredMessages();
        assertTrue(report.contains(message2.getMessageHash()));
        assertTrue(report.contains("Recipient: +27838884567"));
        assertTrue(report.contains(MSG2));
        assertTrue(report.contains(MSG5));
    }

    @Test
    void sendersAndRecipientsAreListed() {
        String result = Message.displaySendersAndRecipients();
        assertTrue(result.contains("Recipient: +27838884567"));
        assertEquals(2, result.split("\n").length);
    }

    @Test
    void storedMessagesAreReadBackFromJsonFile() {
        Message.resetSession();
        Message.setStoragePath(TEST_FILE);
        assertEquals(2, Message.loadStoredMessages());
        assertEquals(Arrays.asList(MSG2, MSG5), Message.getStoredMessageTexts());
    }

    @Test
    void deletingAMessageUpdatesTheJsonFile() {
        Message.deleteMessageByHash(message2.getMessageHash());
        Message.resetSession();
        Message.setStoragePath(TEST_FILE);
        assertEquals(1, Message.loadStoredMessages());
        assertEquals(Arrays.asList(MSG5), Message.getStoredMessageTexts());
    }
}
