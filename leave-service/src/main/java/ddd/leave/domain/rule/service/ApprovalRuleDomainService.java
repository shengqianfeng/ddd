package ddd.leave.domain.rule.service;

import ddd.leave.domain.rule.entity.ApprovalRule;
import ddd.leave.domain.rule.repository.facade.ApprovalRuleRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovalRuleDomainService {

    @Autowired
    ApprovalRuleRepositoryInterface repositoryInterface;

    /**
     * Rule领域服务：获取请假审批规则
     * ☆ 聚合解耦规则：
     * 以下三个参数都是Leave实体的，但是千万不要把leave实体作为参数传进来。
     * 因为Leave和Rule属于不同的聚合领域，不方便后期微服务单独拆分。
     * 所以，直接将需要的参数单独一个个传进来，就做到了不同领域实体的引用解耦。
     * ♻️也就是说，A领域服务中，不要出现B领域的实体对象。
     * @param personType 人员类型
     * @param leaveType  请假类型
     * @param duration   请假时长
     * @return
     */
    public int getLeaderMaxLevel(String personType, String leaveType, long duration) {
        ApprovalRule rule = new ApprovalRule();
        rule.setPersonType(personType);
        rule.setLeaveType(leaveType);
        rule.setDuration(duration);
        return repositoryInterface.getLeaderMaxLevel(rule);
    }
}
