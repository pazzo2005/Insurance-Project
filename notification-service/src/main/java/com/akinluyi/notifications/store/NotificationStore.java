package com.akinluyi.notifications.store;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/** Thread-safe in-memory store of recorded notifications. */
@Component
public class NotificationStore {

    private final List<Notification> notifications = new CopyOnWriteArrayList<>();

    public void record(Notification notification) {
        notifications.add(notification);
    }

    public List<Notification> findAll() {
        return List.copyOf(notifications);
    }

    public int size() {
        return notifications.size();
    }
}
