package ddd.leave.application.service;

import ddd.leave.domain.leave.entity.valueobject.Approver;
import ddd.leave.domain.leave.entity.Leave;
import ddd.leave.domain.leave.service.LeaveDomainService;
import ddd.leave.domain.person.entity.Person;
import ddd.leave.domain.person.repository.po.PersonPO;
import ddd.leave.domain.person.service.PersonDomainService;
import ddd.leave.domain.rule.entity.ApprovalRule;
import ddd.leave.domain.rule.service.ApprovalRuleDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveApplicationService{

    @Autowired
    LeaveDomainService leaveDomainService;
    @Autowired
    PersonDomainService personDomainService;
    @Autowired
    ApprovalRuleDomainService approvalRuleDomainService;

    /**
     * what：跨领域的应用服务，完成领域服务的组合和编排
     *
     * how：由于领域核心逻辑已经很好地沉淀到了领域层中，领域层的这些核心逻辑可以高度复用。
     * 应用服务只需要灵活地组合和编排这些不同聚合的领域服务，就可以很容易地适配前端业务的变化。
     * 因此应用层不会积累太多的业务逻辑代码，所以会变得很薄，代码维护起来也会容易得多。
     *
     * when：创建一个请假申请并为审批人生成任务
     * @param leave
     */
    public void createLeaveInfo(Leave leave){
        //get approval leader max level by rule  审批规则领域服务：获取请假审批规则
        int leaderMaxLevel = approvalRuleDomainService.getLeaderMaxLevel(leave.getApplicant().getPersonType(), leave.getType().toString(), leave.getDuration());
        //find next approver  人员领域服务：根据请假审批规则获取请假审批人
        Person approver = personDomainService.findFirstApprover(leave.getApplicant().getPersonId(), leaderMaxLevel);
        //创建请假单领域服务
        leaveDomainService.createLeave(leave, leaderMaxLevel, Approver.fromPerson(approver));
    }

    /**
     * 更新请假单基本信息
     * @param leave
     */
    public void updateLeaveInfo(Leave leave){
        leaveDomainService.updateLeaveInfo(leave);
    }

    /**
     * 提交审批，更新请假单信息
     * @param leave
     */
    public void submitApproval(Leave leave){
        //find next approver
        Person approver = personDomainService.findNextApprover(leave.getApprover().getPersonId(), leave.getLeaderMaxLevel());
        leaveDomainService.submitApproval(leave, Approver.fromPerson(approver));
    }

    public Leave getLeaveInfo(String leaveId){
        return leaveDomainService.getLeaveInfo(leaveId);
    }

    public List<Leave> queryLeaveInfosByApplicant(String applicantId){
        return leaveDomainService.queryLeaveInfosByApplicant(applicantId);
    }

    public List<Leave> queryLeaveInfosByApprover(String approverId){
        return leaveDomainService.queryLeaveInfosByApprover(approverId);
    }
}