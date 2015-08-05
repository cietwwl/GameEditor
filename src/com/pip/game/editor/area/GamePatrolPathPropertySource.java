package com.pip.game.editor.area;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.map.GamePatrolPath;

/**
 * 场景出口属性页。
 * @author lighthu
 */
public class GamePatrolPathPropertySource implements IPropertySource {
    private GameAreaEditor owner;
    private GamePatrolPath patrolPath;
    
    public GamePatrolPathPropertySource(GameAreaEditor owner, GamePatrolPath patrolPath) {
        this.owner = owner;
        this.patrolPath = patrolPath;
    }
    
    public Object getEditableValue() {
        return this;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = new IPropertyDescriptor[2 + patrolPath.path.size()];
        ret[0] = new PropertyDescriptor("type", "类型");
        ret[1] = new PropertyDescriptor("id", "ID");
        
        for(int i=0; i<patrolPath.path.size(); i++) {
            ret[i + 2] = new PropertyDescriptor("point" + i, "路点" + i + "坐标");
        }
        return ret;
    }

    public Object getPropertyValue(Object id) {
        if ("type".equals(id)) {
            return "传送点";
        } else if ("id".equals(id)) {
            return patrolPath.getGlobalID() + "(0x" + Integer.toHexString(patrolPath.getGlobalID()) + ")";
        } else {
            for(int i=0; i<patrolPath.path.size(); i++) {
                String nid = "point" + i;
                if(nid.equals(id)) {
                    int[] point = patrolPath.path.get(i);
                    return point[0] + "," + point[1];
                }
            }
            throw new IllegalArgumentException();
        }
    }

    public boolean isPropertySet(Object id) {
        return false;
    }

    public void resetPropertyValue(Object id) {}

    public void setPropertyValue(Object id, Object value) {
        for(int i=0; i<patrolPath.path.size(); i++) {
            String nid = "point" + i;
            if(nid.equals(id)) {
                String newValue = (String)value;
                String[] xy = newValue.split(",");
                int[] point = patrolPath.path.get(i);
                point[0] = Integer.parseInt(xy[0]);
                point[1] = Integer.parseInt(xy[1]);                
                owner.setDirty(true);
            }
        }        
    }
}
