package ddd.leave.domain.leave.service;

import ddd.leave.domain.leave.entity.Leave;
import ddd.leave.domain.leave.entity.valueobject.ApprovalType;
import ddd.leave.domain.leave.entity.valueobject.Approver;
import ddd.leave.domain.leave.event.LeaveEvent;
import ddd.leave.domain.leave.event.LeaveEventType;
import ddd.leave.domain.leave.repository.facade.LeaveRepositoryInterface;
import ddd.leave.domain.leave.repository.po.LeavePO;
import ddd.leave.infrastructure.common.event.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 请假聚合内的领域服务类(一个聚合可以设计一个领域服务类，管理聚合内所有的领域服务)
 * when：如果一个业务行为由多个实体对象参与完成，我们就将这部分业务逻辑放在领域服务中实现。
 * what: 领域服务与实体方法的区别是：
 *              实体方法: 完成单一实体自身的业务逻辑，是相对简单的原子业务逻辑
 *              领域服务:  则是多个实体组合出的相对复杂的业务逻辑。
 *              两者都在领域层，实现领域模型的核心业务能力。
 * Tips:
 * ♻️ 在领域服务或实体方法中，我们应尽量避免调用其它聚合的领域服务或引用其它聚合的实体或值对象，这种操作会增加聚合的耦合度
 *
 *
 */
@Service
@Slf4j
public class LeaveDomainService {

    @Autowired
    EventPublisher eventPublisher;
    @Autowired
    LeaveRepositoryInterface leaveRepositoryInterface;
    @Autowired
    LeaveFactory leaveFactory;

    /**
     *
     * 创建聚合根Leave，当聚合根被创建时，聚合内的所有依赖对象将会被同时创建
     * @param leave
     * @param leaderMaxLevel
     * @param approver
     */
    @Transactional
    public void createLeave(Leave leave, int leaderMaxLevel, Approver approver) {
        leave.setLeaderMaxLevel(leaderMaxLevel);
        leave.setApprover(approver);
        leave.create();
        leaveRepositoryInterface.save(leaveFactory.createLeavePO(leave));
        LeaveEvent event = LeaveEvent.create(LeaveEventType.CREATE_EVENT, leave);
        leaveRepositoryInterface.saveEvent(leaveFactory.createLeaveEventPO(event));
        eventPublisher.publish(event);
    }

    @Transactional
    public void updateLeaveInfo(Leave leave) {
        LeavePO po = leaveRepositoryInterface.findById(leave.getId());
        if (null == po) {
            throw new RuntimeException("leave does not exist");
        }
        leaveRepositoryInterface.save(leaveFactory.createLeavePO(leave));
    }

    /**
     * 领域事件的执行逻辑
     * @param leave
     * @param approver
     */
    @Transactional
    public void submitApproval(Leave leave, Approver approver) {
        LeaveEvent event;
        if ( ApprovalType.REJECT == leave.getCurrentApprovalInfo().getApprovalType()) {
            //reject, then the leave is finished with REJECTED status
            leave.reject(approver);
            event = LeaveEvent.create(LeaveEventType.REJECT_EVENT, leave);
        } else {
            if (approver != null) {
                //agree and has next approver
                leave.agree(approver);
                event = LeaveEvent.create(LeaveEventType.AGREE_EVENT, leave);
            } else {
                //agree and hasn't next approver, then the leave is finished with APPROVED status
                leave.finish();
                event = LeaveEvent.create(LeaveEventType.APPROVED_EVENT, leave);
            }
        }
        leave.addHistoryApprovalInfo(leave.getCurrentApprovalInfo());
        //1、完成业务数据持久化
        //2、完成事件数据持久化
        leaveRepositoryInterface.save(leaveFactory.createLeavePO(leave));
        leaveRepositoryInterface.saveEvent(leaveFactory.createLeaveEventPO(event));
        //3、完成领域事件发布
        eventPublisher.publish(event);
    }

    public Leave getLeaveInfo(String leaveId) {
        LeavePO leavePO = leaveRepositoryInterface.findById(leaveId);
        return leaveFactory.getLeave(leavePO);
    }

    public List<Leave> queryLeaveInfosByApplicant(String applicantId) {
        List<LeavePO> leavePOList = leaveRepositoryInterface.queryByApplicantId(applicantId);
        return leavePOList.stream()
                .map(leavePO -> leaveFactory.getLeave(leavePO))
                .collect(Collectors.toList());
    }

    public List<Leave> queryLeaveInfosByApprover(String approverId) {
        List<LeavePO> leavePOList = leaveRepositoryInterface.queryByApproverId(approverId);
        return leavePOList.stream()
                .map(leavePO -> leaveFactory.getLeave(leavePO))
                .collect(Collectors.toList());
    }
}