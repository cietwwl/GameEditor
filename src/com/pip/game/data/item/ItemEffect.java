package com.pip.game.data.item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jdom.Attribute;
import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.editor.item.ItemEffectPropertySource;

/**
 * ��Ʒʹ��Ч����
 */
public class ItemEffect implements Cloneable {
    /**
     * ʹ��Ч������
     */
    public int effectType;

    /**
     * ʹ��Ч�������б�
     */
    public Map<String, String> param = new HashMap<String, String>();

    public void load(Element elem) {
        if (elem == null) {
            return;
        }
        effectType = Integer.parseInt(elem.getAttributeValue("type"));
        List list = elem.getAttributes();
        for (int i = 0; i < list.size(); i++) {
            Attribute attr = (Attribute) list.get(i);
            if (attr.getName().equals("type")) {
                continue;
            }
            param.put(attr.getName(), attr.getValue());
        }
    }

    public Element save() {
        Element elem = new Element("effect");
        elem.addAttribute(new Attribute("type", String.valueOf(effectType)));
        Iterator<String> keys = param.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            elem.addAttribute(new Attribute(key, param.get(key)));
        }
        return elem;
    }

    /**
     * ���¸�����������ȱʡֵ��
     */
    public void resetParam() {
        param.clear();
        ItemEffectConfig config = ProjectData.getActiveProject().config.findItemEffectConfig(effectType);
        for (ItemEffectParam paramDef : config.paramDefs) {
            param.put(paramDef.name, paramDef.defaultValue);
        }
    }

    public String toString() {
        ItemEffectConfig config = ProjectData.getActiveProject().config.findItemEffectConfig(effectType);
        return effectType + ". " + config.title;
    }

    /**
     * ��¡��
     */
    public Object clone() {
        ItemEffect ret = new ItemEffect();
        ret.effectType = effectType;
        ret.param.putAll(param);
        return ret;
    }
}
