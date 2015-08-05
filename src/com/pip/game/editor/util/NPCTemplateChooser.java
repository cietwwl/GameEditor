/**
 * 
 */
package com.pip.game.editor.util;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.data.RefreshNpc;
import com.pip.game.editor.property.ChooseNPCTemplateDialog;

/**
 * @author jhkang
 *
 */
public class NPCTemplateChooser extends Composite {

    private int templateId = -1;
    private Text textID;
    private ModifyListener modifyListener;
    
   /* private Text count;*/
    
    private List<RefreshNpc> refreshNpcList;
    
    /**
     * 可以指定个数的模式
     */
    public final static byte COUNT_TYPE  = 2;
    
    /**
     * 单一模式
     */
    public final static byte ONE_TYPE  = 1;
    
    /**
     * 使用类型
     */
    private  byte type;
    
    /**
     * Create the composite
     * @param parent
     * @param style
     */
    public NPCTemplateChooser(Composite parent, int style, byte type){
       
        super(parent, style);
        this.type = type;
        final GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.numColumns = 2;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);

        textID = new Text(this, SWT.BORDER);
        textID.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        textID.setText("                 ");
        textID.setEditable(false);

        final Button browseButton = new Button(this, SWT.NONE);
        browseButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
        browseButton.setText("...");
        browseButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e) {
                selectItem();
            }
        });
        
    }
    
    private void selectItem(){
        ChooseNPCTemplateDialog itemDialog = new ChooseNPCTemplateDialog(Display.getCurrent().getActiveShell(), type);
        if(type != ONE_TYPE){
            itemDialog.setRefreshList(refreshNpcList);
        }else{
            itemDialog.setSelectedTemplate(templateId);
        }
        if(itemDialog.open() == IDialogConstants.OK_ID){
            if(type == ONE_TYPE){
                setTemplateID(itemDialog.getSelectedTemplate());
            }else{
                setRefreshNpcList(itemDialog.getRefreshNpc());
            }
        }
    }
    
    public void setRefreshNpcList(List<RefreshNpc> refreshNpcList){
        this.refreshNpcList = refreshNpcList;
        StringBuffer name = new StringBuffer();              
        for(int i = 0; i < refreshNpcList.size(); i++){
            int id = refreshNpcList.get(i).getId();
            if(name.length() != 0){
                name.append(';');
            }
            name.append(NPCTemplate.toString(ProjectData.getActiveProject(), id));
            name.append("数量:");
            name.append(refreshNpcList.get(i).getCount());
            
            name.append("x:");
            name.append(refreshNpcList.get(i).getX());
            
            name.append("y:");
            name.append(refreshNpcList.get(i).getY());
        }
        textID.setText(name.toString());
        textID.redraw();
        textID.setToolTipText(textID.getText());
        if(modifyListener!=null){
            modifyListener.modifyText(null);
        }
    }
    
    public List<RefreshNpc> getRefreshNpcList(){
        return refreshNpcList;
    }
    
    public void setTemplateID(int id) {
        templateId = id;
        String name = NPCTemplate.toString(ProjectData.getActiveProject(), templateId);
        if(name.length() < 2){
            name = "                   " + name; 
        }
        textID.setText(name);
        textID.redraw();
        textID.setToolTipText(textID.getText());
        if(modifyListener!=null){
            modifyListener.modifyText(null);
        }
    }
    
    public int getTemplateID() {
        return templateId;
    }

    
    /**
     * will call back as modifyListener.modifyText(null);
     * @param modifyListener
     */
    public void setModifyListener(ModifyListener modifyListener) {
        this.modifyListener = modifyListener;
    }
    
}
