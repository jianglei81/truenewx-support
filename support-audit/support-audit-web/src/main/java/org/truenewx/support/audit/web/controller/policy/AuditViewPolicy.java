package org.truenewx.support.audit.web.controller.policy;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;
import org.truenewx.core.exception.HandleableException;
import org.truenewx.support.audit.data.model.AuditApplymentUnity;
import org.truenewx.support.audit.data.model.Auditor;

/**
 * 审核视图方针
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface AuditViewPolicy<U extends AuditApplymentUnity<T, A>, T extends Enum<T>, A extends Auditor<T>> {

    T getType();

    /**
     * 获取申请者名称
     *
     * @param applicantId
     *            申请者id
     *
     * @return 申请者名称
     */
    String getApplicantName(int applicantId);

    /**
     *
     * @return 在申请清单中显示的内容字段集合
     */
    String[] getListContentFields();

    /**
     * 获取显示内容模型
     *
     * @param applicantId
     *            申请者id
     * @param content
     *            申请内容
     *
     * @return 显示内容模型
     *
     *         Map<String, Object> getContentModel(int applicantId, Map<String,
     *         Object> content);
     */

    Object getListContent(U applyment);

    Map<String, Object> getDetailContentModel(U applyment);

    /**
     * 获取显示内容模型
     *
     * @param applicantId
     *            申请者id
     * @param content
     *            申请内容
     *
     * @return 显示内容模型
     *
     *         Map<String, Object> getContentModel(int applicantId, Map<String,
     *         Object> content);
     */

    /**
     * 准备通过审核。在决定审核通过之后，真正通过审核之前，可插入页面进行一些准备工作
     *
     * @return 准备通过审核时展示的视图和模型
     * @throws HandleableException
     */
    ModelAndView preparePass(U applyment) throws HandleableException;

    /**
     * 获取审核通过时的附加数据，即从准备审核通过的页面中取得的数据
     *
     * @param request
     *            HTTP请求
     * @return 审核通过时的附加数据
     */
    Object getPassAddition(HttpServletRequest request);

}
