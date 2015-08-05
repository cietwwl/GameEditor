package com.pip.game.editor.equipment;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.equipment.AttributeCalculator;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.editor.skill.IValueChanged;

public class EquipmentPropertySource implements IPropertySource{
    private Equipment equipment;
    
    private IValueChanged changedHandle;
    
    public EquipmentPropertySource(Equipment equipment, IValueChanged changedHandle){
        this.equipment = equipment;
        this.changedHandle = changedHandle;
    }
    
    
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return equipment.getPropertyDescriptors();
    }

    public Object getPropertyValue(Object id) {
        if (id.equals(Equipment.PROPNAME_BASICRATE)) {
           float v = equipment.DataCalc.getQualityAddTion(equipment.quality);
           return String.valueOf(v);
        } else if (id.equals(Equipment.PROPNAME_EXTRARATE)) {
            return String.valueOf(equipment.extraQuality);
        } else if (id.equals(Equipment.PROPNAME_EQUVALUE)) {
            return String.valueOf((int)equipment.getValue());
        } else if (id.equals(Equipment.PROPNAME_EQUSHOWNVALUE)) {
            return String.valueOf((int)equipment.getShownValue());
        }  else if (id.equals(Equipment.PROPNAME_BUFFID)) {
            return new Integer(equipment.buffID);
        } else if (id.equals(Equipment.PROPNAME_BUFFLEVEL)) {
            return String.valueOf(equipment.buffLevel);
        } else if (id.equals(Equipment.PROPNAME_BUFFVALUE)) {
            return String.valueOf(equipment.getBuffValue());
        }
        int index = equipment.owner.config.attrCalc.findIndexOfAttribute((String)id);
        if (index == -1) {
            return "0.0";
        } else {
            return String.valueOf(equipment.appendAttributes[index]);
        }
    }
    
    public void setPropertyValue(Object id, Object value) {
        try {
            if (id.equals(Equipment.PROPNAME_EXTRARATE)) {
                float newValue = Float.parseFloat((String)value);
                if (newValue != equipment.extraQuality) {
                    equipment.extraQuality = newValue;
                    changedHandle.valueChanged(Equipment.PROPNAME_EXTRARATE);
                }
                return;
            } else if (id.equals(Equipment.PROPNAME_BUFFID)) {
                int newValue = ((Integer)value).intValue();
                if (newValue != equipment.buffID) {
                    equipment.buffID = newValue;
                    changedHandle.valueChanged(Equipment.PROPNAME_BUFFID);
                }
                return;
            } else if (id.equals(Equipment.PROPNAME_BUFFLEVEL)) {
                int newValue = Integer.parseInt((String)value);
                if (newValue != equipment.buffLevel) {
                    equipment.buffLevel = newValue;
                    changedHandle.valueChanged(Equipment.PROPNAME_BUFFLEVEL);
                }
                return;
            }
            int index = equipment.owner.config.attrCalc.findIndexOfAttribute((String)id);
            if (index == -1) {
                return;
            }
            float newValue = Float.parseFloat((String)value);
            
            // 当设定值和当前值不同时，通知Editor数据已经修改 
            if (newValue != equipment.appendAttributes[index]) {
                equipment.appendAttributes[index] = newValue;
                changedHandle.valueChanged((String)id);
            }
        } catch (NumberFormatException e) {
            changedHandle.valueError("输入数据格式错误！");
        }
    }

    
    public boolean isPropertySet(Object id) {
        return false;
    }

    public void resetPropertyValue(Object id) {
    }
    
    public Object getEditableValue() {
        return null;
    }
}
