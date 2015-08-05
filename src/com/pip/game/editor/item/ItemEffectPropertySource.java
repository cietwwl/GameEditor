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
 * ��Ʒʹ��Ч�����Ա༭�������ṩ��
 */
public class ItemEffectPropertySource implements IPropertySource {
    /**
     * ��Ҫ�޸ĵ�Ч������
     */
    protected ItemEffect effect;
    /**
     * �����ı༭��
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
     * �����������ʹ�����Ӧ�����Ա༭����
     *      integer          -  ����
     *      float            -  ������
     *      string           -  �ַ���
     *      location         -  ��ͼλ�ã������ʽΪ����ͼID,����X,����Y(�������ʽ����ѡ)
     *      leveltable       -  ���������õ���ֵ�������ʽΪ��1����ֵ,2����ֵ,3����ֵ........
     *      item             -  ��Ʒ��װ��
     *      choice(xxx,xxx)  -  ѡ��������0��ʾ��һ��ѡ��1��ʾ�ڶ���ѡ���Դ�����
     *      dictobj(<classname>)  -  ѡ��һ���ֵ����ݶ��󣬱������ID
     *      dataobj(<classname>)  -  ѡ��һ�����ݶ��󣬱������ID
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
     * �����û����Ե�id��չ�����͵�һ����Ԫ��
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
     * �����������Ͱ�ʵ�ʲ���ֵת��Ϊ�ɱ༭�Ķ���
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
     * ���ض�Ӧ���Ե�ȡֵ
     */
    public Object getPropertyValue(Object id) {
        ItemEffectConfig config = ProjectData.getActiveProject().config.findItemEffectConfig(effect.effectType);
        ItemEffectParam paramDef = config.getParamDef((String)id);
        return loadPropertyValue(paramDef.dataType, effect.param.get(id));
    }

    public boolean isPropertySet(Object id) {return false;}

    public void resetPropertyValue(Object id) {}
    
    /**
     * �����������Ͱѱ༭�Ķ���ת��Ϊʵ�ʲ���ֵ��
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
     * �û��޸����ݲ���
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
