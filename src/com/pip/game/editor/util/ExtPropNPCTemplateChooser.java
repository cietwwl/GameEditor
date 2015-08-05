/**
 * 
 */
package com.pip.game.editor.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.jdom.Element;

import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.data.RefreshNpc;
import com.pip.util.Utils;

/**
 * @author jhkang
 *
 */
public class ExtPropNPCTemplateChooser extends ExtProp {

    private NPCTemplateChooser npcTemplateChooser;
    
    public static byte usetype = 2;
    /**
     * 可以指定个数的模式
     */
    public final static byte COUNT_TYPE  = 2;
    
    /**
     * 单一模式
     */
    public final static byte ONE_TYPE  = 1;
    
    public ExtPropNPCTemplateChooser() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.pip.game.editor.util.ExtProp#duplicate()
     */
    @Override
    public ExtProp duplicate() {
        ExtPropNPCTemplateChooser p = new ExtPropNPCTemplateChooser();
        this.copyTo(p);
        return p;
    }

    /* (non-Javadoc)
     * @see com.pip.game.editor.util.ExtProp#getValue()
     */
    @Override
    public String getValue() {
        if(npcTemplateChooser == null || npcTemplateChooser.isDisposed()==true){
            return value;
        }
        String string = "";
        if(usetype == ONE_TYPE){
            string = npcTemplateChooser.getTemplateID()+"";
        }else{
            StringBuffer stringBuffer = new StringBuffer();
            for(int i = 0; i< npcTemplateChooser.getRefreshNpcList().size(); i++){
                RefreshNpc npc = npcTemplateChooser.getRefreshNpcList().get(i);
                if(stringBuffer.length() != 0){
                    stringBuffer.append(';');
                }
                stringBuffer.append(Integer.toString(npc.id));
                stringBuffer.append(',');
                stringBuffer.append(Integer.toString(npc.getCount()));
                stringBuffer.append(',');
                stringBuffer.append(Integer.toString(npc.getX()));
                stringBuffer.append(',');
                stringBuffer.append(Integer.toString(npc.getY()));
            }
            string = stringBuffer.toString();
        }
        return string;
    }

    /* (non-Javadoc)
     * @see com.pip.game.editor.util.ExtProp#setValue(java.lang.String)
     */
    @Override
    public boolean setValue(String v) {
        if(v == null || v.equals("")){
            return false;
        }
        if(usetype == ONE_TYPE){
            npcTemplateChooser.setTemplateID(Integer.parseInt(v));
        }else{
            List<RefreshNpc> npcList = new ArrayList<RefreshNpc>();
            
            String[] npc = Utils.splitString(v, ';');
            int[][] numLength = new int[npc.length][4];
            for(int i = 0; i < npc.length; i++){
                String[] temp = Utils.splitString(npc[i], ',');
                for(int k = 0; k < temp.length; k++){
                    if(k == 0){
                        numLength[i][0] = Utils.parseHex(temp[0].trim());
                    }else{
                        try{
                            numLength[i][k] = Integer.parseInt(temp[k].trim());
                        }catch (Exception e) {
                            numLength[i][k] = 1;
                        }
                    }
                }
            }
            for(int i = 0; i < npc.length; i++){
                NPCTemplate template = (NPCTemplate) ProjectData.getActiveProject().findObject(NPCTemplate.class, numLength[i][0]);
                if(template != null){
                    RefreshNpc npc1 = new RefreshNpc(ProjectData.getActiveProject());
                    npc1.id = template.id;
                    npc1.title = template.title;
                    npc1.setCount(numLength[i][1]);
                    npc1.setX(numLength[i][2]);
                    npc1.setY(numLength[i][3]);
                    npcList.add(npc1);
                }
            }
            
            npcTemplateChooser.setRefreshNpcList(npcList);
        }
        
        return true;
    }

    @Override
    public void createControl(Composite container) {
        super.createControl(container);
        npcTemplateChooser = new NPCTemplateChooser(container, SWT.NONE, usetype);
        npcTemplateChooser.setModifyListener(new ModifyListener(){

            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }
        });
        setValue(value);
    }
    
    @Override
    public void load(Element prop) {
        super.load(prop);
        usetype = Byte.parseByte(prop.getAttributeValue("usetype"));
    }
    
}
