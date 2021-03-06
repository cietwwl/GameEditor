package com.pip.game.editor.item;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.Shop;
import com.pip.game.data.Shop.BuyRequirement;
import com.pip.game.data.item.DropGroup;
import com.pip.game.data.item.Formula;
import com.pip.game.data.item.Item;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.property.ChooseDropGroupDialog2;
import com.pip.game.editor.property.ChooseItemDialog;
import com.pip.util.AutoSelectAll;

/**
 * 打造配方编辑器。
 */
public class FormulaEditor extends DefaultDataObjectEditor {
	protected Text textMovePoint;
    private Text textMaxAmount;
    private Combo comboProductType;
    private Text textRequirement;
    protected Text textMoney;
    protected Text textLevel;
    private Text textMinAmount;
    private Text textDescription;
    private Text textTitle;
    private Text textID;
    
    public static final String ID = "com.pip.game.editor.item.FormulaEditor"; //$NON-NLS-1$
    private Label labelItemName;
    private Label labelDropGroupName;
    private Label labelItem;
    private Label labelDropGroup;
    private Button buttonChooseDropGroup;
    protected Composite container;
    protected Label labelLevel;
    protected Label labelMovePoint;

    /**
     * Create contents of the editor part
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 8;
        container.setLayout(gridLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("ID：");

        textID = new Text(container, SWT.BORDER);
        final GridData gd_textID = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textID.setLayoutData(gd_textID);
        textID.addFocusListener(AutoSelectAll.instance);
        textID.addModifyListener(this);

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setLayoutData(new GridData());
        label_1.setText("名称：");

        textTitle = new Text(container, SWT.BORDER);
        final GridData gd_textTitle = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textTitle.setLayoutData(gd_textTitle);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);

        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setLayoutData(new GridData());
        label_2.setText("描述：");

        textDescription = new Text(container, SWT.BORDER);
        final GridData gd_textDescription = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textDescription.setLayoutData(gd_textDescription);
        textDescription.addFocusListener(AutoSelectAll.instance);
        textDescription.addModifyListener(this);

        labelLevel = new Label(container, SWT.NONE);
        labelLevel.setLayoutData(new GridData());
        labelLevel.setText("技能等级：");

        textLevel = new Text(container, SWT.BORDER);
        final GridData gd_textLevel = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textLevel.setLayoutData(gd_textLevel);
        textLevel.addFocusListener(AutoSelectAll.instance);
        textLevel.addModifyListener(this);

        final Label label_3 = new Label(container, SWT.NONE);
        label_3.setLayoutData(new GridData());
        label_3.setText("消耗金钱：");

        textMoney = new Text(container, SWT.BORDER);
        final GridData gd_textMoney = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textMoney.setLayoutData(gd_textMoney);
        textMoney.addFocusListener(AutoSelectAll.instance);
        textMoney.addModifyListener(this);

        labelMovePoint = new Label(container, SWT.NONE);
        labelMovePoint.setLayoutData(new GridData());
        labelMovePoint.setText("消耗行动力：");

        textMovePoint = new Text(container, SWT.BORDER);
        final GridData gd_textMovePoint = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textMovePoint.setLayoutData(gd_textMovePoint);
        textMovePoint.addFocusListener(AutoSelectAll.instance);
        textMovePoint.addModifyListener(this);

        final Label label_21 = new Label(container, SWT.NONE);
        label_21.setText("其他消耗：");

        textRequirement = new Text(container, SWT.BORDER);
        textRequirement.setEditable(false);
        final GridData gd_textRequirement = new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1);
        textRequirement.setLayoutData(gd_textRequirement);

        final Button buttonEditRequirement = new Button(container, SWT.NONE);
        buttonEditRequirement.setLayoutData(new GridData());
        buttonEditRequirement.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                if (EditFormulaRequirementsDialog.open(getSite().getShell(), (Formula)editObject)) {
                    Formula dataDef = (Formula)editObject;
                    textRequirement.setText(BuyRequirement.toString(dataDef.requirements, false, true));
                    updateMoneyField();
                    setDirty(true);
                }
            }
        });
        buttonEditRequirement.setText("编辑...");

        final Label label_5 = new Label(container, SWT.NONE);
        label_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        label_5.setText("产出类型：");

        comboProductType = new Combo(container, SWT.READ_ONLY);
        comboProductType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                onTypeChanged();
            }
        });
        comboProductType.setItems(new String[] {"物品/装备", "掉落组"});
        final GridData gd_comboProductType = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboProductType.setLayoutData(gd_comboProductType);
        comboProductType.addModifyListener(this);

        final Label label_6 = new Label(container, SWT.NONE);
        label_6.setText("产出数量：");

        textMinAmount = new Text(container, SWT.BORDER);
        final GridData gd_textMinAmount = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textMinAmount.setLayoutData(gd_textMinAmount);
        textMinAmount.addFocusListener(AutoSelectAll.instance);
        textMinAmount.addModifyListener(this);

        textMaxAmount = new Text(container, SWT.BORDER);
        final GridData gd_textMaxAmount = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textMaxAmount.setLayoutData(gd_textMaxAmount);
        textMaxAmount.addFocusListener(AutoSelectAll.instance);
        textMaxAmount.addModifyListener(this);

        labelItem = new Label(container, SWT.NONE);
        labelItem.setText("产出物品：");

        labelItemName = new Label(container, SWT.NONE);
        final GridData gd_labelItemName = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gd_labelItemName.widthHint = 206;
        labelItemName.setLayoutData(gd_labelItemName);

        final Button buttonChooseItem = new Button(container, SWT.NONE);
        buttonChooseItem.setLayoutData(new GridData());
        buttonChooseItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                ChooseItemDialog dlg = new ChooseItemDialog(getSite().getShell());
                Formula dataDef = (Formula)editObject;
//                dlg.setSelectedItem(dataDef.itemID);
                if (dlg.open() == ChooseItemDialog.OK) {
                    dataDef.itemID = dlg.getSelectedItem();
                    if(dataDef.itemID == -1){
                        labelItemName.setText("未指定");
                    }else{
                        labelItemName.setText(dataDef.owner.findItemOrEquipment(dataDef.itemID).toString());
                    }
                    setDirty(true);
                }
            }
        });
        buttonChooseItem.setText("选择...");

        labelDropGroup = new Label(container, SWT.NONE);
        final GridData gd_labelDropGroup = new GridData();
        labelDropGroup.setLayoutData(gd_labelDropGroup);
        labelDropGroup.setText("产出掉落组：");

        labelDropGroupName = new Label(container, SWT.NONE);
        final GridData gd_labelDropGroupName = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        gd_labelDropGroupName.widthHint = 214;
        labelDropGroupName.setLayoutData(gd_labelDropGroupName);

        buttonChooseDropGroup = new Button(container, SWT.NONE);
        buttonChooseDropGroup.setLayoutData(new GridData());
        buttonChooseDropGroup.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                ChooseDropGroupDialog2 dlg = new ChooseDropGroupDialog2(getSite().getShell());
                Formula dataDef = (Formula)editObject;
                dlg.setSelectedDropGroup(dataDef.groupID);
                if (dlg.open() == ChooseDropGroupDialog2.OK) {
                    dataDef.groupID = dlg.getSelectedDropGroup();
                    labelDropGroupName.setText(dataDef.owner.findObject(DropGroup.class, dataDef.groupID).toString());
                    setDirty(true);
                }
            }
        });
        buttonChooseDropGroup.setText("选择...");
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        
        updateView();
        
        setDirty(false);
        setPartName(this.getEditorInput().getName());
        saveStateToUndoBuffer();
    }
    
    protected void onTypeChanged() {
        int sel = comboProductType.getSelectionIndex();
        if (sel == Formula.PRODUCT_DROPGROUP) {
            labelItem.setText("显示物品：");
            labelDropGroup.setVisible(true);
            labelDropGroupName.setVisible(true);
            buttonChooseDropGroup.setVisible(true);
        } else {
            labelItem.setText("产出物品：");
            labelDropGroup.setVisible(false);
            labelDropGroupName.setVisible(false);
            buttonChooseDropGroup.setVisible(false);
        }

    }
    protected void updateView() {
        // 设置初始值
        Formula dataDef = (Formula)editObject;
        textID.setText(String.valueOf(dataDef.id));
        textTitle.setText(dataDef.title);
        textDescription.setText(dataDef.description);
        
        textLevel.setText(String.valueOf(dataDef.level));
        updateMoneyField();
        textMovePoint.setText(String.valueOf(dataDef.movePoint));
        
        textRequirement.setText(BuyRequirement.toString(dataDef.requirements, false, true));
        comboProductType.select(dataDef.productType);
        textMinAmount.setText(String.valueOf(dataDef.minAmount));
        textMaxAmount.setText(String.valueOf(dataDef.maxAmount));
        
        Item itemObj = (Item)dataDef.owner.findItemOrEquipment(dataDef.itemID);
        if (itemObj == null) {
            labelItemName.setText("无");
        } else {
            labelItemName.setText(itemObj.toString());
        }
        
        DropGroup groupObj = (DropGroup)dataDef.owner.findObject(DropGroup.class, dataDef.groupID);
        if (groupObj == null) {
            labelDropGroupName.setText("无");
        } else {
            labelDropGroupName.setText(groupObj.toString());
        }
        onTypeChanged();
    }
    
    protected void updateMoneyField() {
        Formula dataDef = (Formula)editObject;
        int money = 0;
        for (BuyRequirement br : dataDef.requirements) {
            if (br.type == Shop.TYPE_MONEY && br.deduct) {
                money = br.amount;
                break;
            }
        }
        textMoney.setText(String.valueOf(money));
    }
    
    /**
     * 保存当前编辑数据。
     */
    protected void saveData() throws Exception {
        Formula dataDef = (Formula)editObject;

        // 读取输入：对象ID、标题、描述。。。。。
        try {
            dataDef.id = Integer.parseInt(textID.getText());
        } catch (Exception e) {
            throw new Exception("请输入正确的ID。");
        }
        dataDef.title = textTitle.getText().trim();
        dataDef.description = textDescription.getText();
        try {
            dataDef.level = Integer.parseInt(textLevel.getText());
        } catch (Exception e) {
            throw new Exception("请输入正确的级别。");
        }
        
        // 金钱和荣誉
        try {
            setPrice(Shop.TYPE_MONEY, Integer.parseInt(textMoney.getText()));
        } catch (Exception e) {
            throw new Exception("请输入正确的金钱。");
        }
        try {
        } catch (Exception e) {
            throw new Exception("请输入正确的荣誉。");
        }
        try {
            dataDef.movePoint = Integer.parseInt(textMovePoint.getText());
        } catch (Exception e) {
            throw new Exception("请输入正确的行动力。");
        }
        
        dataDef.productType = comboProductType.getSelectionIndex();
        try {
            dataDef.minAmount = Integer.parseInt(textMinAmount.getText());
            if (dataDef.minAmount < 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception("请输入正确的数量。");
        }
        try {
            dataDef.maxAmount = Integer.parseInt(textMaxAmount.getText());
            if (dataDef.maxAmount < 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception("请输入正确的数量。");
        }
        if (dataDef.minAmount > dataDef.maxAmount) {
            throw new Exception("最小数量不能大于最大数量。");
        }
        
        // 检查输入合法性
        DataObject dobj = ProjectData.getActiveProject().findObject(dataDef.getClass(), dataDef.id);
        if (dobj != null && dobj != getSaveTarget()) {
            throw new Exception("ID重复，请重新输入。");
        }
        if (dataDef.title.length() == 0) {
            throw new Exception("请输入标题。");
        }
        Item itemObj = dataDef.owner.findItemOrEquipment(dataDef.itemID);
        if (itemObj == null) {
            throw new Exception("必须选择一个物品。");
        }
        if (dataDef.productType == Formula.PRODUCT_DROPGROUP) {
            if (dataDef.owner.findObject(DropGroup.class, dataDef.groupID) == null) {
                throw new Exception("必须选择一个掉落组。");
            }
        } else {
            dataDef.groupID = -1;
        }
    }
    
    /**
     * 设置指定类型货币的价格。
     * @param item 物品
     * @param type 货币类型
     * @param value 新价格
     * @return 如果价格和原来的相同，返回false
     */
    protected boolean setPrice(int type, int value) {
        Formula dataDef = (Formula)editObject;
        for (Shop.BuyRequirement req : dataDef.requirements) {
            if (req.type == type && req.deduct) {
                if (req.amount == value) {
                    return false;
                } else {
                    req.amount = value;
                    return true;
                }
            }
        }
        if (value == 0) {
            return false;
        } else {
            Shop.BuyRequirement req = new Shop.BuyRequirement();
            req.type = type;
            req.amount = value;
            req.deduct = true;
            dataDef.requirements.add(req);
            return true;
        }
    }
}
