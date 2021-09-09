package ddd.leave.domain.leave.entity;

import ddd.leave.domain.leave.entity.valueobject.Applicant;
import ddd.leave.domain.leave.entity.valueobject.Approver;
import ddd.leave.domain.leave.entity.valueobject.LeaveType;
import ddd.leave.domain.leave.entity.valueobject.Status;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 请假单聚合根，引用实体和值对象，可以组合聚合内的多个实体。
 */
@Data
public class Leave {

    String id;
    Applicant applicant;
    /**
     *
     * 审批人值对象:
     * 1. 里边包含了简单 数据查询和转换服务(你比如把Person对象转为Approver，可以知道Approver数据来源于Person聚合)
     * 2. 这类值对象的数据来源于其它聚合，不可修改，可重复使用。
     * 3. 将这种对象设计为值对象而不是实体，可以提高系统性能，降低数据库实体关联的复杂度.
     * 4. 值对象只做整体替换、不可修改的特性，在值对象中基本不会有修改或新增的方法
     */
    Approver approver;
    LeaveType type;
    Status status;
    Date startTime;
    Date endTime;
    long duration;
    //审批领导的最大级别
    int leaderMaxLevel;
    //审批意见实体，用于记录审批意见，拥有自己的属性和值对象(比如审批人Approver)
    ApprovalInfo currentApprovalInfo;
    List<ApprovalInfo> historyApprovalInfos;

    public long getDuration() {
        return endTime.getTime() - startTime.getTime();
    }

    public Leave addHistoryApprovalInfo(ApprovalInfo approvalInfo) {
        if (null == historyApprovalInfos)
            historyApprovalInfos = new ArrayList<>();
        this.historyApprovalInfos.add(approvalInfo);
        return this;
    }

    public Leave create(){
        this.setStatus(Status.APPROVING);
        this.setStartTime(new Date());
        return this;
    }

    public Leave agree(Approver nextApprover){
        this.setStatus(Status.APPROVING);
        this.setApprover(nextApprover);
        return this;
    }

    public Leave reject(Approver approver){
        this.setApprover(approver);
        this.setStatus(Status.REJECTED);
        this.setApprover(null);
        return this;
    }

    public Leave finish(){
        this.setApprover(null);
        this.setStatus(Status.APPROVED);
        this.setEndTime(new Date());
        this.setDuration(this.getEndTime().getTime() - this.getStartTime().getTime());
        return this;
    }
}
