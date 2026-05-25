package notification.controller;

import lombok.extern.slf4j.Slf4j;
import notification.domain.Notification;
import notification.dto.RequestNotificationDto;
import notification.exception.RequestException;
import notification.mapper.NotificationMapper;
import notification.persistance.InsertNotificationJdbc;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController("/notification")
public class NotificationController {
    InsertNotificationJdbc insertNotificationJdbc;
    @PostMapping("/create")
    public ResponseEntity<Object> saveNotification(@RequestBody RequestNotificationDto requestNotificationDto) {
        Notification notificationDomain = NotificationMapper.toDomain(requestNotificationDto);
        try{
            insertNotificationJdbc.saveNotification(notificationDomain);
            log.info(" Caso de uso de criação de notificação concluido e persistido");
        return ResponseEntity.ok("<<<<<<<<<< Request usecase insert notification OK ");
        } catch (Exception e) {
            throw new RequestException(e.getMessage());
        }
    }
}
