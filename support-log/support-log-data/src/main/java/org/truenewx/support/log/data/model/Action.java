package org.truenewx.support.log.data.model;

import org.truenewx.data.model.ValueModel;

/**
 * 操作
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class Action implements ValueModel {

    private String caption;

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(final String caption) {
        this.caption = caption;
    }

    public String getType() {
        String type = getClass().getSimpleName();
        final String actionSimpleClassName = Action.class.getSimpleName();
        if (type.endsWith(actionSimpleClassName)) {
            type = type.substring(0, type.length() - actionSimpleClassName.length());
            type = type.toUpperCase();
        }
        return type;
    }

}
