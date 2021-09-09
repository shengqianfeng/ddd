package ddd.leave.domain.leave.entity.valueobject;

import ddd.leave.domain.person.entity.Person;
import ddd.leave.domain.person.repository.po.PersonPO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 审批人值对象(数据来源于person聚合)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Approver {

    String personId;//审批人ID
    String personName;//审批人Name
    int level;//管理级别

    /**
     * 组装审批人
     * 切记：值对象只做整体替换、不可修改的特性，在值对象中基本不会有修改或新增的方法
     * @param person
     * @return
     */
    public static Approver fromPerson(Person person){
        Approver approver = new Approver();
        approver.setPersonId(person.getPersonId());
        approver.setPersonName(person.getPersonName());
        approver.setLevel(person.getRoleLevel());
        return approver;
    }

}
