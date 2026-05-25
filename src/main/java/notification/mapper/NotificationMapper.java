package notification.mapper;

import notification.domain.Notification;
import notification.dto.RequestNotificationDto;
import notification.exception.DomainException;

public class NotificationMapper {
    private String channelId;
    private Long id;
    private String userId;
    private String messageText;

    public static Notification toDomain(RequestNotificationDto notification) {
        Notification notificationDomain = new Notification(
                notification.getChannelId(),
                notification.getId(),
                notification.getUserId(),
                notification.getMessageText()
        );
        return notificationDomain;
    }
}
