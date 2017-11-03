package org.truenewx.support.unstructured.core;

/**
 * 非结构化存储异常代码类
 *
 * @author jianglei
 *
 */
public class UnstructuredExceptionCodes {

    private UnstructuredExceptionCodes() {
    }

    /**
     * 授权类型无对应的方针
     */
    public static final String NO_POLICY_FOR_AUTHORIZE_TYPE = "error.unstructured.no_policy_for_authorize_type";

    /**
     * 没有写权限
     */
    public static final String NO_WRITE_PERMISSION = "error.unstructured.no_write_permission";
    /**
     * 没有读权限
     */
    public static final String NO_READ_PERMISSION = "error.unstructured.no_read_permission";

}
