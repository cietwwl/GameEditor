package com.pip.game.editor.area;

import java.util.Arrays;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pip.game.data.map.GameRelivePoint;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.editor.property.ConstraintsPropertyDescriptor;
import com.pip.game.editor.property.LocationPropertyDescriptor;

/**
 * 场景复活点属性页。
 * @author lighthu
 */
public class GameRelivePointPropertySource implements IPropertySource {
    private GameAreaEditor owner;
    private GameRelivePoint relivePoint;
    
    public GameRelivePointPropertySource(GameAreaEditor owner, GameRelivePoint relivePoint) {
        this.owner = owner;
        this.relivePoint = relivePoint;
    }
    
    public Object getEditableValue() {
        return this;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[4];
        ret[0] = new PropertyDescriptor("type", "类型");
        ret[1] = new PropertyDescriptor("id", "ID");
        ret[2] = new ConstraintsPropertyDescriptor("condition", "使用条件");
        ret[3] = new LocationPropertyDescriptor("jumpPosition", "跳转位置");
        return ret;
    }

    public Object getPropertyValue(Object id) {
        if ("type".equals(id)) {
            return "复活点";
        } else if ("id".equals(id)) {
            return relivePoint.getGlobalID() + "(0x" + Integer.toHexString(relivePoint.getGlobalID()) + ")";
        } else if ("condition".equals(id)) {
            return relivePoint.condition.toString();
        } else if ("jumpPosition".equals(id)) {
            int[] value = new int[3];
            System.arraycopy(relivePoint.jumpPosition, 0, value, 0, 3);
            return value;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public boolean isPropertySet(Object id) {
        return false;
    }

    public void resetPropertyValue(Object id) {}

    public void setPropertyValue(Object id, Object value) {
        if ("condition".equals(id)) {
            String newValue = (String)value;
            if (!newValue.equals(relivePoint.condition.toString())) {
                relivePoint.condition = ExpressionList.fromString(newValue);
                owner.setDirty(true);
            }
        } else if ("jumpPosition".equals(id)) {
            int[] newValue = (int[])value;
            if (!Arrays.equals(newValue, relivePoint.jumpPosition)) {
                relivePoint.jumpPosition = newValue;
                owner.setDirty(true);
            }
        }
    }
}
