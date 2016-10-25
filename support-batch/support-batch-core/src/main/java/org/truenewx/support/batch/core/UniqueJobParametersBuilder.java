package org.truenewx.support.batch.core;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.stereotype.Component;
import org.truenewx.core.util.StringUtil;

/**
 * 唯一性作业参数集的构建器
 *
 * @author jianglei
 * @since JDK 1.7
 */
@Component
public class UniqueJobParametersBuilder extends JobParametersBuilder {

    public static final String UUID = "uuid";

    @Override
    public JobParameters toJobParameters() {
        addString(UUID, StringUtil.uuid32());
        return super.toJobParameters();
    }

}
