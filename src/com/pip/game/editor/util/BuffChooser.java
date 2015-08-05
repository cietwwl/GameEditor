package com.pip.game.editor.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.editor.GenericChooseDialog;
import com.pip.game.editor.property.ChooseDataObjectDialog;

/**
 * 可选择Buff和等级。
 * @author lighthu
 */
public class BuffChooser extends Composite {
    private int buffId = -1;
    private int buffLevel = 1;
	private Text textID;
	private ModifyListener modifyListener;
	Button browseButton;
	
	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public BuffChooser(Composite parent, int style) {
		super(parent, style);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		textID = new Text(this, SWT.BORDER);
		textID.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		textID.setEditable(false);

		browseButton = new Button(this, SWT.NONE);
		browseButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		browseButton.setText("...");
		browseButton.addSelectionListener(new SelectionAdapter(){
		    public void widgetSelected(SelectionEvent e) {
		        selectBuff();
		    }
		});
	}
	
	private void selectBuff(){
        ChooseDataObjectDialog dlg = new ChooseDataObjectDialog(textID.getShell(), BuffConfig.class, "选择Buff");
        if (dlg.open() == Dialog.OK) {
            if(dlg.getSelectedDataObject() != null) {
                BuffConfig buff = (BuffConfig)dlg.getSelectedDataObject();
                List<String> levelNames = new ArrayList<String>();
                for (int i = 1; i <= buff.maxLevel; i++) {
                    levelNames.add(i + "级");
                }
                GenericChooseDialog dlg2 = new GenericChooseDialog(textID.getShell(), "选择Buff等级", levelNames);
                if (dlg2.open() == Dialog.OK) {
                    String levelName = (String)dlg2.getSelection();
                    int level = Integer.parseInt(levelName.substring(0, levelName.length() - 1));
                    setBuff(buff.id, level);
                } else {
                    setBuff(-1, 1);
                }
            } else {
                setBuff(-1, 1);
            }
        }
	}
	
	public void setBuff(int id, int level) {
	    BuffConfig buff = (BuffConfig)ProjectData.getActiveProject().findObject(BuffConfig.class, id);
	    if (buff != null) {
	        buffId = id;
	        buffLevel = level;
	        String text = buff.toString() + " " + level + "级";
	        textID.setText(text);
	        textID.setToolTipText(textID.getText());
	        if(modifyListener!=null){
	            modifyListener.modifyText(null);
	        }
	    } else {
	        buffId = id;
            buffLevel = level;
	        textID.setText("无");
            if(modifyListener!=null){
                modifyListener.modifyText(null);
            }
	    }
	}
	
	public int getBuffID() {
	    return buffId;
	}
	
	public int getBuffLevel() {
	    return buffLevel;
	}

	/**
	 * will call back as modifyListener.modifyText(null);
	 * @param modifyListener
	 */
    public void setModifyListener(ModifyListener modifyListener) {
        this.modifyListener = modifyListener;
    }
}
