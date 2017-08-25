package org.truenewx.support.log.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注方法在日志体系中排除，不记录日志
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LogExcluded {

    /**
     * 为空时标注方法整个排除，非空时仅排除指定参数
     * 
     * @return 要排除的参数名称集
     */
    String[] parameters() default {};

}
