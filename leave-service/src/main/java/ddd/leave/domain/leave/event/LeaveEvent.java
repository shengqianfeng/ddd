package ddd.leave.domain.leave.event;

import com.alibaba.fastjson.JSON;
import ddd.leave.domain.leave.entity.Leave;
import ddd.leave.infrastructure.common.event.DomainEvent;
import ddd.leave.infrastructure.util.IdGenerator;
import lombok.Data;

import java.util.Date;

/**
 * 请假领域事件实体：
 * 1 领域事件实体在聚合仓储内完成持久化，但是事件实体的生命周期不受聚合根管理。
 * 2 领域事件的执行逻辑如下：
 * 第一步：执行业务逻辑，产生领域事件。
 * 第二步：完成业务数据持久化。
 *  <pre>
 *      leaveRepositoryInterface.save(leaveFactory.createLeavePO(leave));
 * </pre>
 * 第三步：完成事件数据持久化。
 * <pre>
 *    leaveRepositoryInterface.saveEvent(leaveFactory.createLeaveEventPO(event));
 * </pre>
 * 第四步：完成领域事件发布。
 * <pre>
 *     eventPublisher.publish(event);
 * </pre>
 * 领域事件处理逻辑代码详见 LeaveDomainService 中 submitApproval 领域服务，里面有请假提交审批事件的完整处理逻辑。
 */
@Data
public class LeaveEvent extends DomainEvent {

    LeaveEventType leaveEventType;

    public static LeaveEvent create(LeaveEventType eventType, Leave leave){
        LeaveEvent event = new LeaveEvent();
        event.setId(IdGenerator.nextId());
        event.setLeaveEventType(eventType);
        event.setTimestamp(new Date());
        event.setData(JSON.toJSONString(leave));
        return event;
    }
}
