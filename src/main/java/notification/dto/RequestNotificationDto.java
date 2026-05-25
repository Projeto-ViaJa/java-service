package notification.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RequestNotificationDto {
    private String channelId;
    private Long id;
    private String userId;
    private String messageText;
}
