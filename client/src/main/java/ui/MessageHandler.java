package ui;

import webSocketMessages.serverMessages.NotificationMessage;

public interface MessageHandler {
    void handleMessage(NotificationMessage message);
}
