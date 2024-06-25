package ui;

import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public interface GameHandler {
    void updateGame(LoadGameMessage game);
    void printMessage(NotificationMessage message);
}
