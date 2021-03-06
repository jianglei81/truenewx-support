package org.truenewx.support.unstructured.core.aliyun;

import com.aliyun.oss.OSS;
import com.aliyuncs.IAcsClient;

/**
 * 阿里云账户信息
 *
 * @author jianglei
 *
 */
public interface AliyunAccount {

    String getAccountId();

    String getOssRegion();

    String getOssEndpoint();

    OSS getOssClient();

    IAcsClient getAcsClient();

}
