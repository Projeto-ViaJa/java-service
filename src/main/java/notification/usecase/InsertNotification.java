package notification.usecase;

import notification.domain.Notification;

import java.util.Optional;

public interface InsertNotification {
    Optional<Object> saveNotification(Notification notification);
}
