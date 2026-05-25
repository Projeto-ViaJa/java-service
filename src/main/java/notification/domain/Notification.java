package notification.domain;

import notification.exception.DomainException;

public class Notification {
    private String channelId;
    private Long id;
    private String userId;
    private String messageText;

    public Notification(String channelId, Long id, String userId, String messageText) {
        this.channelId = channelId;
        this.id = id;
        this.userId = userId;
        this.messageText = messageText;

        if (channelId.isEmpty() || channelId == null) {
            throw new DomainException("channelId is null or empty");
        }
        if (id == null || id <= 0) {
            throw new DomainException("id is null or empty");
        }
        if (userId == null || userId.isEmpty()) {
            throw new DomainException("userId is null or empty");
        }
        if (messageText == null || messageText.isEmpty()) {
            throw new DomainException("messageText is null or empty");
        }
    }

}
