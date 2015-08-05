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
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.GenericChooseDialog;
import com.pip.game.editor.property.ChooseDataObjectDialog;

/**
 * 可选择技能和等级。
 * @author lighthu
 */
public class SkillChooser extends Composite {
    private int skillId = -1;
    private int skillLevel = 1;
	private Text textID;
	private ModifyListener modifyListener;
	Button browseButton;
	
	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public SkillChooser(Composite parent, int style) {
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
		        selectSkill();
		    }
		});
	}
	
	private void selectSkill(){
        ChooseDataObjectDialog dlg = new ChooseDataObjectDialog(textID.getShell(), SkillConfig.class, "选择技能");
        if (dlg.open() == Dialog.OK) {
            if(dlg.getSelectedDataObject() != null) {
                SkillConfig skill = (SkillConfig)dlg.getSelectedDataObject();
                List<String> levelNames = new ArrayList<String>();
                for (int i = 1; i <= skill.maxLevel; i++) {
                    levelNames.add(i + "级");
                }
                GenericChooseDialog dlg2 = new GenericChooseDialog(textID.getShell(), "选择技能等级", levelNames);
                if (dlg2.open() == Dialog.OK) {
                    String levelName = (String)dlg2.getSelection();
                    int level = Integer.parseInt(levelName.substring(0, levelName.length() - 1));
                    setSkill(skill.id, level);
                } else {
                    setSkill(-1, 1);
                }
            } else {
                setSkill(-1, 1);
            }
        }
	}
	
	public void setSkill(int id, int level) {
	    SkillConfig skill = (SkillConfig)ProjectData.getActiveProject().findObject(SkillConfig.class, id);
	    if (skill != null) {
	        skillId = id;
	        skillLevel = level;
	        String text = skill.toString() + " " + level + "级";
	        textID.setText(text);
	        textID.setToolTipText(textID.getText());
	        if(modifyListener!=null){
	            modifyListener.modifyText(null);
	        }
	    } else {
	        skillId = id;
            skillLevel = level;
	        textID.setText("无");
            if(modifyListener!=null){
                modifyListener.modifyText(null);
            }
	    }
	}
	
	public int getSkillID() {
	    return skillId;
	}
	
	public int getSkillLevel() {
	    return skillLevel;
	}

	/**
	 * will call back as modifyListener.modifyText(null);
	 * @param modifyListener
	 */
    public void setModifyListener(ModifyListener modifyListener) {
        this.modifyListener = modifyListener;
    }
}
