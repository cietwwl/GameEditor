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
 * 扩展属性编辑工具,工具配置文件生成编辑控件.<br/>
 * 目前只在技能编辑器中使用;<br/>
 * 由于ExtPropEnties处于data包下,不能引用SWT相关的包,故有此类包装,供给编辑器使用
 * @author jhkang
 *
 */
public abstract class ExtPropManager extends ExtPropEntries {
    
    public ExtPropManager(){
    }
    /**
     * 加载配置文件中的属性.
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
     * 获取子类中保存属性模板集合的方法.子类中的属性模板应该是静态属性
     * @return
     */
    protected abstract Map<String, ExtProp> getTemplates();
    
    /**
     * 按照模板克隆(编辑界面显示属性,比如名称),再填充实际值.
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
     * 将模板中定义的属性填充到ret中
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
