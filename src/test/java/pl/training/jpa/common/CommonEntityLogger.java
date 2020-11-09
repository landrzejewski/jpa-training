package pl.training.jpa.common;

import lombok.extern.java.Log;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

@Log
public class CommonEntityLogger {

    @PreUpdate
    @PrePersist
    @PreRemove
    public void handleLifecycleEvent(Object entity) {
        log.info(entity.toString());
    }

}
