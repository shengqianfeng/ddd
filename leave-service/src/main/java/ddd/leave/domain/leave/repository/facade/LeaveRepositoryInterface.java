package ddd.leave.domain.leave.repository.facade;

import ddd.leave.domain.leave.repository.po.LeaveEventPO;
import ddd.leave.domain.leave.repository.po.LeavePO;

import java.util.List;

/**
 * 面向领域服务的Leave仓储接口
 */
public interface LeaveRepositoryInterface {

    void save(LeavePO leavePO);

    void saveEvent(LeaveEventPO leaveEventPO);

    LeavePO findById(String id);

    List<LeavePO> queryByApplicantId(String applicantId);

    List<LeavePO> queryByApproverId(String approverId);

}