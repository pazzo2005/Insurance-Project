package com.akinluyi.notifications.web;

import com.akinluyi.notifications.store.Notification;
import com.akinluyi.notifications.store.NotificationStore;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Exposes recorded notifications so the event flow is observable. */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationStore store;

    public NotificationController(NotificationStore store) {
        this.store = store;
    }

    @GetMapping
    public List<Notification> all() {
        return store.findAll();
    }
}
