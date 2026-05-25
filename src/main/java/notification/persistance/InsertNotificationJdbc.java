package notification.persistance;

import lombok.extern.slf4j.Slf4j;
import notification.domain.Notification;
import notification.usecase.InsertNotification;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.util.Optional;
@Slf4j
public class InsertNotificationJdbc implements InsertNotification {
    @Autowired
    public Connection query;

    @Override
    public Optional<Object> saveNotification(Notification notification) {
        String sql = """
               
                
                
                """;
        try {
                query.prepareStatement(sql);
                log.info("<<<<<<<<<<<<< Executando inserção de notificação na base de dados");
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return Optional.empty();
    }
}
