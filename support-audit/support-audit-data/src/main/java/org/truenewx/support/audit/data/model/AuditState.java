package org.truenewx.support.audit.data.model;

import org.truenewx.core.annotation.Caption;
import org.truenewx.core.enums.annotation.EnumValue;

/**
 * 审核状态
 *
 * @author jianglei
 * @since JDK 1.8
 */
public enum AuditState {

    @Caption("已撤销")
    @EnumValue("CC")
    CANCELED(0),

    @Caption("已废弃")
    @EnumValue("AB")
    ABANDONED(0),

    @Caption("未申请")
    @EnumValue("UA")
    UNAPPLIED(0),

    @Caption("待审核")
    @EnumValue("PD")
    PENDING(1),

    @Caption("一审通过")
    @EnumValue("P1")
    PASSED_1(2),

    @Caption("审核通过")
    @EnumValue("PL")
    PASSED_LAST(0),

    @Caption("审核拒绝")
    @EnumValue("R1")
    REJECTED_1(0),

    @Caption("二审拒绝")
    @EnumValue("R2")
    REJECTED_2(1);

    /**
     * 所处审核级别
     */
    private byte level;

    private AuditState(final int level) {
        this.level = (byte) level;
    }

    /**
     *
     * @return 所处审核级别，0-不可审核
     */
    public byte getLevel() {
        return this.level;
    }

}
