package com.pip.game.data.extprop;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

/**
 * 
 * @author jhkang
 *
 */
public class ExtPropEntries {

    public static final String PROP_ENTRY_NAME = "prop";
    public static final String PROP_ATT_DATA_TYPE = "dataType";
    
    /**
     * 扩展属性集合
     */
    public Map<String, ExtPropEntry> editProps = new LinkedHashMap<String, ExtPropEntry>();

    public ExtPropEntries() {
        super();
    }

    /**
     * 加载实际数据文件中的key:value
     * @param parentEl
     */
    public void loadExtData(Element parentEl) {
        if(parentEl == null){
            return;
        }
        List<Element> props = parentEl.getChildren(PROP_ENTRY_NAME);
        for(Element prop:props){
            ExtPropEntry entry = new ExtPropEntry();
            entry.load(prop);
            editProps.put(entry.key, entry);
        }
    }

    public void clear() {
        editProps.clear();
    }
    
    public void setValue(String key, String value) {
        ExtPropEntry entry = new ExtPropEntry();
        entry.key = key;
        entry.value = value;
        editProps.put(key, entry);
    }
    
    public int getValueAsInt(String key){
        ExtPropEntry ep = editProps.get(key);
        if(ep != null){
            return Integer.parseInt(ep.value);
        }else{
            return 0;
        }
        
    }
    
    public float getValueAsFloat(String key){
        ExtPropEntry ep = editProps.get(key);
        if(ep != null){
            return Float.parseFloat(ep.value);
        }else{
            return 0;
        }
        
    }
    
    public String getValueAsString(String key){
        ExtPropEntry ep = editProps.get(key);
        return ep.value;
    }
    /**
     * result:<br/>
     * <pre>
     * parentNode
     *      prop key="" value=""
     *      prop
     *      ...
     * parentNoeEnd
     * </pre>
     * @param parent
     */
    public void saveToDom(Element parent){
        Iterator<ExtPropEntry> it = editProps.values().iterator();
        while(it.hasNext()){
            ExtPropEntry entry = it.next();
            Element propEl = new Element(PROP_ENTRY_NAME);
            propEl.addAttribute("key", entry.getKey());
            propEl.addAttribute("value", entry.value);
            parent.getMixedContent().add(propEl);
        }
    }
    
    public void copyFrom(ExtPropEntries o){
        editProps.clear();
        for(ExtPropEntry entry:o.editProps.values()){
            ExtPropEntry newEntry = new ExtPropEntry();
            newEntry.key = new String(entry.key);
            newEntry.value = new String(entry.value);
            this.editProps.put(newEntry.key, newEntry);
        }
    }
    
}