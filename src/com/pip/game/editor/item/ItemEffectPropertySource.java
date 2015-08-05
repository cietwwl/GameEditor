package com.pip.game.editor.item;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.ProjectData;
import com.pip.game.data.item.ItemEffect;
import com.pip.game.data.item.ItemEffectConfig;
import com.pip.game.data.item.ItemEffectParam;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.property.ConditionalLocationPropertyDescriptor;
import com.pip.game.editor.property.DataObjectPropertyDescriptor;
import com.pip.game.editor.property.DictObjectPropertyDescriptor;
import com.pip.game.editor.property.ItemPropertyDescriptor;
import com.pip.game.editor.property.LevelTablePropertyDescriptor;

/**
 * 物品使用效果属性编辑框内容提供类
 */
public class ItemEffectPropertySource implements IPropertySource {
    /**
     * 需要修改的效果数据
     */
    protected ItemEffect effect;
    /**
     * 所属的编辑器
     */
    protected DefaultDataObjectEditor handle;
    
    public ItemEffectPropertySource(ItemEffect effect, DefaultDataObjectEditor editor){
        this.effect = effect;
        this.handle = editor;
    }

    public Object getEditableValue() {
        return null;
    }
    
    /*
     * 根据数据类型创建对应的属性编辑器：
     *      integer          -  整数
     *      float            -  浮点数
     *      string           -  字符串
     *      location         -  地图位置，保存格式为：地图ID,像素X,像素Y(条件表达式，可选)
     *      leveltable       -  按级别配置的数值表，保存格式为：1级数值,2级数值,3级数值........
     *      item             -  物品或装备
     *      choice(xxx,xxx)  -  选择索引，0表示第一个选择，1表示第二个选择，以此类推
     *      dictobj(<classname>)  -  选择一个字典数据对象，保存对象ID
     *      dataobj(<classname>)  -  选择一个数据对象，保存对象ID
     */
    protected IPropertyDescriptor createPropertyDescriptor(String name, String title, String dataType) {
        if (dataType.equals("integer") || dataType.equals("float") || dataType.equals("string")) {
            return new TextPropertyDescriptor(name, title);
        } else if (dataType.equals("location")) {
            return new ConditionalLocationPropertyDescriptor(name, title);
        } else if (dataType.equals("leveltable")) {
            return new LevelTablePropertyDescriptor(name, title);
        } else if (dataType.equals("item")) {
            return new ItemPropertyDescriptor(name, title);
        } else if (dataType.startsWith("choice(")) {
            String[] choices = dataType.substring(7, dataType.length() - 1).split(",");
            return new ComboBoxPropertyDescriptor(name, title, choices);
        } else if (dataType.startsWith("dictobj(")) {
            String className = dataType.substring(8, dataType.length() - 1);
            try {
                Class dictCls = ProjectData.getActiveProject().config.getProjectClassLoader().loadClass(className);
                return new DictObjectPropertyDescriptor(name, title, dictCls);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("class not found: " + className);
            }
        } else if (dataType.startsWith("dataobj(")) {
            String className = dataType.substring(8, dataType.length() - 1);
            try {
                Class dictCls = ProjectData.getActiveProject().config.getProjectClassLoader().loadClass(className);
                return new DataObjectPropertyDescriptor(name, title, dictCls);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("class not found: " + className);
            }
        } else {
            throw new IllegalArgumentException("invalid data type: " + dataType);
        }
    }
    
    /**
     * 返回用户属性的id和展现类型的一个二元组
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] ret = null;
        ItemEffectConfig config = ProjectData.getActiveProject().config.findItemEffectConfig(effect.effectType);
        ret = new IPropertyDescriptor[config.paramDefs.length];
        for (int i = 0; i < config.paramDefs.length; i++) {
            ItemEffectParam param = config.paramDefs[i];
            ret[i] = createPropertyDescriptor(param.name, param.title, param.dataType);
        }
        return ret;
    }
    
    /**
     * 根据数据类型把实际参数值转换为可编辑的对象。
     */
    protected Object loadPropertyValue(String dataType, String value) {
        if (dataType.equals("integer") || dataType.equals("float") || dataType.equals("string")) {
            if(value == null){
                return "0";
            }
            return value;
        } else if (dataType.equals("location")) {
            Object[] info = new Object[4];
            int pos = value.indexOf('(');
            if (pos == -1) {
                info[3] = "";
            } else {
                info[3] = value.substring(pos + 1, value.length() - 1);
                value = value.substring(0, pos);
            }
            String[] secs = value.split(",");
            info[0] = new Integer(secs[0]);
            info[1] = new Integer(secs[1]);
            info[2] = new Integer(secs[2]);
            return info;
        } else if (dataType.equals("leveltable")) {
            return value;
        } else if (dataType.equals("item")) {
            return new Integer(value);
        } else if (dataType.startsWith("choice(")) {
            return new Integer(value);
        } else if (dataType.startsWith("dictobj(")) {
            return new Integer(value);
        } else if (dataType.startsWith("dataobj(")) {
            return new Integer(value);
        } else {
            throw new IllegalArgumentException("invalid data type: " + dataType);
        }
    }
    
    /**
     * 返回对应属性的取值
     */
    public Object getPropertyValue(Object id) {
        ItemEffectConfig config = ProjectData.getActiveProject().config.findItemEffectConfig(effect.effectType);
        ItemEffectParam paramDef = config.getParamDef((String)id);
        return loadPropertyValue(paramDef.dataType, effect.param.get(id));
    }

    public boolean isPropertySet(Object id) {return false;}

    public void resetPropertyValue(Object id) {}
    
    /**
     * 根据数据类型把编辑的对象转换为实际参数值。
     */
    protected String savePropertyValue(String dataType, Object value) {
        if (dataType.equals("integer") || dataType.equals("float") || dataType.equals("string")) {
            return value.toString();
        } else if (dataType.equals("location")) {
            Object[] info = (Object[])value;
            String loc = info[0] + "," + info[1] + "," + info[2];
            String condition = (String)info[3];
            if (condition.length() > 0) {
                loc += "(" + condition + ")";
            }
            return loc;
        } else if (dataType.equals("leveltable")) {
            return value.toString();
        } else if (dataType.equals("item")) {
            return value.toString();
        } else if (dataType.startsWith("choice(")) {
            return value.toString();
        } else if (dataType.startsWith("dictobj(")) {
            return value.toString();
        } else if (dataType.startsWith("dataobj(")) {
            return value.toString();
        } else {
            throw new IllegalArgumentException("invalid data type: " + dataType);
        }
    }
    
    /**
     * 用户修改数据操作
     */
    public void setPropertyValue(Object id, Object value) {
        ItemEffectConfig config = ProjectData.getActiveProject().config.findItemEffectConfig(effect.effectType);
        ItemEffectParam paramDef = config.getParamDef((String)id);
        String newValue = savePropertyValue(paramDef.dataType, value);
        if (!newValue.equals(effect.param.get(id))) {
            effect.param.put((String)id, newValue);
            handle.setDirty(true);
        }
    }
}
