package com.pip.game.editor.quest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.pip.game.data.Currency;
import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.quest.QuestRewardItem;
import com.pip.game.editor.util.ItemChooser;

public class QuestRewardItemDialog extends Dialog {
    protected Combo comboType;
    protected Text textAmount;
    protected Label labelCount, labelItem;
    protected ItemChooser itemChooser;
    protected QuestRewardItem rewardItem;
    private boolean isExtends;
    private int[] typeMaps;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public QuestRewardItemDialog(Shell parentShell, QuestRewardItem reward, boolean isExtends) {
        super(parentShell);
        this.rewardItem = reward;
        this.isExtends = isExtends;
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        List<String> listItems = new ArrayList<String>();
        List<Integer> itemTypes = new ArrayList<Integer>();
        listItems.add("金钱");
        itemTypes.add(QuestRewardItem.REWARD_MONEY);
        listItems.add("经验");
        itemTypes.add(QuestRewardItem.REWARD_EXP);
        listItems.add("物品");
        itemTypes.add(QuestRewardItem.REWARD_ITEM);
        listItems.add("元宝");
        itemTypes.add(QuestRewardItem.REWARD_IMONEY);
        List<DataObject> currencies = ProjectData.getActiveProject().getDictDataListByType(Currency.class);
        for (DataObject dobj : currencies) {
            listItems.add(dobj.title);
            itemTypes.add(dobj.id);
        }
        String[] typeItems = new String[listItems.size()];
        listItems.toArray(typeItems);
        typeMaps = new int[itemTypes.size()];
        for (int i = 0; i < itemTypes.size(); i++) {
            typeMaps[i] = itemTypes.get(i);
        }
        
        Composite container = (Composite) super.createDialogArea(parent);
        if(isExtends) {
            return container;
        }
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setText("奖励类型：");

        comboType = new Combo(container, SWT.READ_ONLY);
        comboType.setVisibleItemCount(15);
        comboType.setItems(typeItems);
        final GridData gd_comboType = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboType.setLayoutData(gd_comboType);
        comboType.addModifyListener(new ModifyListener() {
        	public void modifyText(final ModifyEvent e) {
        		onTypeChanged();
        	}
        });

        labelCount = new Label(container, SWT.NONE);
        final GridData gd_labelCount = new GridData();
        labelCount.setLayoutData(gd_labelCount);
        labelCount.setText("奖励数量：");

        textAmount = new Text(container, SWT.BORDER);
        final GridData gd_textAmount = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textAmount.setLayoutData(gd_textAmount);

        labelItem = new Label(container, SWT.NONE);
        labelItem.setText("奖励物品：");

        itemChooser = new ItemChooser(container, SWT.NONE);
        itemChooser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        
        // 设置初始数据
        for (int i = 0; i < typeMaps.length; i++) {
            if (typeMaps[i] == rewardItem.rewardType) {
                comboType.select(i);
                break;
            }
        }
        if (rewardItem.rewardType == QuestRewardItem.REWARD_ITEM) {
        	textAmount.setText(String.valueOf(rewardItem.itemCount));
        	itemChooser.setItemID(rewardItem.rewardValue);
        } else {
        	textAmount.setText(String.valueOf(rewardItem.rewardValue));
        	labelItem.setVisible(false);
        	itemChooser.setVisible(false);
        }
        
        return container;
    }
    
    protected void onTypeChanged() {
    	int rewardType = typeMaps[comboType.getSelectionIndex()];
    	if (rewardType == QuestRewardItem.REWARD_ITEM) {
    		labelItem.setVisible(true);
    		itemChooser.setVisible(true);
    	} else {
    		labelItem.setVisible(false);
    		itemChooser.setVisible(false);
    	}
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "确定", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(500, 225);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("任务奖励");
    }

    
    protected void buttonPressed(int buttonId) {
        if(isExtends) {
            super.buttonPressed(buttonId);
            return;
        }
        if (buttonId == IDialogConstants.OK_ID) {
            rewardItem.rewardType = typeMaps[comboType.getSelectionIndex()];
    		if (rewardItem.rewardType == QuestRewardItem.REWARD_ITEM) {
    			rewardItem.rewardValue = itemChooser.getItemID();
    			
    			//选择物品无效
    			if(rewardItem.rewardValue == -1){
    			    MessageDialog.openError(getShell(), "错误", "选择物品无效。");
    			    return;
    			}
    			
    			try {
    				rewardItem.itemCount = Integer.parseInt(textAmount.getText());
    			} catch (Exception e) {
    				MessageDialog.openError(getShell(), "错误", "数量输入错误。");
    			}
	        } else {
    			try {
    				rewardItem.rewardValue = Integer.parseInt(textAmount.getText());
    			} catch (Exception e) {
    				MessageDialog.openError(getShell(), "错误", "数量输入错误。");
    			}
	        }
        }
        super.buttonPressed(buttonId);
    }
}
