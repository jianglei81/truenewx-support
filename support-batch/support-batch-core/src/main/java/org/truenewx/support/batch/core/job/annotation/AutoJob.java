package org.truenewx.support.batch.core.job.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;
import org.truenewx.core.Strings;

/**
 * 自动生成简单作业
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface AutoJob {
    /**
     *
     * @return 当前Tasklet的Bean名
     */
    String value() default Strings.EMPTY;

    /**
     *
     * @return 自动生成的作业名称
     */
    String jobName() default Strings.EMPTY;
}
