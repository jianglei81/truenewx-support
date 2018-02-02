package org.truenewx.support.log.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在记录操作日志的排除设置
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
     *
     * @return 是否排除整个方法，默认为true
     */
    boolean value() default true;

    /**
     * 要排除的参数名称集，为空时表示不排除
     *
     * @return 要排除的参数名称集
     */
    String[] excluded() default {};

}
