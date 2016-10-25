package org.truenewx.support.audit.service.state;

import org.truenewx.core.exception.BusinessException;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;
import org.truenewx.support.audit.service.AuditApplymentUnityService;

/**
 * 申请者动作
 *
 * @author jianglei
 * @since JDK 1.8
 */
abstract class ApplicantAction<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>>
        extends AuditTransitAction<U, T, A> {

    @SuppressWarnings("unchecked")
    protected U get(final int applicantId, final long id) throws BusinessException {
        return (U) getService(AuditApplymentUnityService.class).load(applicantId, id);
    }

}
