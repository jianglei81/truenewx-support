package org.truenewx.support.batch.core.converter;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.converter.JobParametersConverter;

/**
 * 基于类型的作业参数集转换器
 *
 * @author jianglei
 * @since JDK 1.7
 */
public class ClassBasedJobParametersConverter implements JobParametersConverter {

    private Class<? extends JobParametersBuilder> builderType;

    public ClassBasedJobParametersConverter() {
        this.builderType = JobParametersBuilder.class;
    }

    public ClassBasedJobParametersConverter(
            final Class<? extends JobParametersBuilder> builderType) {
        this.builderType = builderType;
    }

    @Override
    public JobParameters getJobParameters(final Properties properties) {
        JobParametersBuilder builder;
        try {
            builder = this.builderType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (properties != null) {
            for (final Entry<Object, Object> entry : properties.entrySet()) {
                final String key = entry.getKey().toString();
                final Object value = entry.getValue();
                if (value instanceof String) {
                    builder.addString(key, (String) value);
                } else if (value instanceof Double || value instanceof Float) {
                    builder.addDouble(key, ((Number) value).doubleValue());
                } else if (value instanceof Number) {
                    builder.addLong(key, ((Number) value).longValue());
                } else if (value instanceof Date) {
                    builder.addDate(key, (Date) value);
                }
            }
        }
        return builder.toJobParameters();
    }

    @Override
    public Properties getProperties(final JobParameters params) {
        final Properties properties = new Properties();
        if (params != null) {
            final Map<String, JobParameter> parameters = params.getParameters();
            for (final Entry<String, JobParameter> entry : parameters.entrySet()) {
                final Object value = entry.getValue().getValue();
                if (value != null) {
                    properties.put(entry.getKey(), value);
                }
            }
        }
        return properties;
    }

}
