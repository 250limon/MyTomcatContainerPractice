package observors;

import event.Event;

public interface Observer {
    void handle(Event event);
}