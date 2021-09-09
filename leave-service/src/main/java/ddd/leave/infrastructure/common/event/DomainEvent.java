package ddd.leave.infrastructure.common.event;

import lombok.Data;

import java.util.Date;

/**
 * 统一的领域事件基类 DomainEvent
 * 包含：事件 ID、时间戳、事件源以及事件相关的业务数据
 */
@Data
public class DomainEvent {

    String id;
    Date timestamp;
    String source;
    String data;
}
