/**
 * 
 */
package com.pip.game.editor.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.data.ProjectData;
import com.pip.game.data.extprop.ExtPropEntries;
import com.pip.game.data.extprop.ExtPropEntry;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.util.Utils;

/**
 * 
 * ��չ���Ա༭����,���������ļ����ɱ༭�ؼ�.<br/>
 * Ŀǰֻ�ڼ��ܱ༭����ʹ��;<br/>
 * ����ExtPropEnties����data����,��������SWT��صİ�,���д����װ,�����༭��ʹ��
 * @author jhkang
 *
 */
public abstract class ExtPropManager extends ExtPropEntries {
    
    public ExtPropManager(){
    }
    /**
     * ���������ļ��е�����.
     * @param subPath4conf
     * @param propTemplates
     */
    public static void setup(String subPath4conf, Map<String, ExtProp>propTemplates){
        File baseDir = ProjectData.getActiveProject().baseDir;
        File conf = new File(baseDir, subPath4conf);
        try {
            loadConf(conf, propTemplates);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Composite composite;
    public Composite getComposite() {
        return composite;
    }
    public void setComposite(Composite composite) {
        this.composite = composite;
    }
    public static void loadConf(File extConf, Map<String, ExtProp> propTemplates) throws Exception{
        Document doc = Utils.loadDOM(extConf);
        List<Element> props = doc.getRootElement().getChildren(PROP_ENTRY_NAME);
        String unhandleType = "";
        for(Element prop:props){
            String dataType = prop.getAttributeValue(PROP_ATT_DATA_TYPE);
            ExtProp extProp = null;
            if(dataType.equals("itemChooser")){
                extProp = new ExtPropItemChooser();
            }else if(dataType.equals("spinner")){
                extProp = new ExtPropSpinner();
            }else if(dataType.equals("string")){
                extProp = new ExtPropString();
            }else if(dataType.equals("npcTemplateChooser")){
                extProp = new ExtPropNPCTemplateChooser();
            }else if(dataType.equals("checkButton")){
                extProp = new ExtPropCheckButton();
            }else if(dataType.equals("combo")){
                extProp=new ExpPropCombo();
            }else{
                unhandleType += "\n"+dataType;
            }
            if(extProp != null){
                extProp.load(prop);
                propTemplates.put(extProp.getKey(), extProp);
            }
        }
        if(unhandleType.equals("")==false){
            throw new Exception("ExtPropManager.loadConf, unknown type\n"+unhandleType);
        }
    }
    
    /**
     * ��ȡ�����б�������ģ�弯�ϵķ���.�����е�����ģ��Ӧ���Ǿ�̬����
     * @return
     */
    protected abstract Map<String, ExtProp> getTemplates();
    
    /**
     * ����ģ���¡(�༭������ʾ����,��������),�����ʵ��ֵ.
     * @param parentEl
     */
    public void convertExtData2ExtPropControl(Map<String, ExtPropEntry> props) {
        for(ExtProp template:getTemplates().values()){
            ExtProp instProp = template.duplicate();
            if(props.containsKey(template.getKey())){
                ExtPropEntry entry = props.get(template.key);
                instProp.setLoadedValue(entry.getValue());
            }else{
                instProp.setLoadedValue(template.defaultValue);
            }
            editProps.put(instProp.key, instProp);
        }
    }
    /**
     * 
     * @param parent composite which will be filled with controls, layout specified in caller won't take effect
     * @param parentEl 
     */
    public void createPartControl(Composite parent){
        RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
        rowLayout.center = true;
        parent.setLayout(rowLayout);
        composite = parent;
        for(ExtPropEntry prop:editProps.values()){
            ((ExtProp)prop).createControl(parent);
        }
//        Label devShow = new Label(parent, SWT.NONE);
//        devShow.setText("showup");
//        Iterator<Entry<String, ExtProp>> it = props.entrySet().iterator();
//        while(it.hasNext()){
//            Entry<String, ExtProp> entry = it.next();
//            entry.getValue().createControl(parent);
//        }
    }
    
    public void setEditor(DefaultDataObjectEditor ed) {
        for(ExtPropEntry prop:editProps.values()){
            ((ExtProp)prop).setHandler(ed);
        }        
    }
    /**
     * ��ģ���ж����������䵽ret��
     * @param ret
     */
    public void fillWithProps(ExtPropEntries ret) {
        for(ExtProp ep:getTemplates().values()){
            ExtProp inst = ep.duplicate();
            inst.setLoadedValue(ep.defaultValue);
            ret.editProps.put(ep.getKey(), inst);
        }
    }
}
