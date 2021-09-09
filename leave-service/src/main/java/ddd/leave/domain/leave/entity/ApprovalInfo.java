package ddd.leave.domain.leave.entity;

import ddd.leave.domain.leave.entity.valueobject.ApprovalType;
import ddd.leave.domain.leave.entity.valueobject.Approver;
import lombok.Data;

/**
 * 审批意见实体
 */
@Data
public class ApprovalInfo {

    String approvalInfoId;
    Approver approver;//审批人
    ApprovalType approvalType;
    String msg;
    long time;

}
