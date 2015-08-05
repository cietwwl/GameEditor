package com.pip.game.editor.item;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.item.Item;
import com.pip.game.editor.AbstractDataObjectEditor;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.util.IconChooser;
import com.pip.util.AutoSelectAll;

/**
 * 宝石属性编辑控件。
 */
public class JewelItemEditor extends AbstractDataObjectEditor implements SelectionListener, ModifyListener {
    /**
     * 宝石等级。
     */
    protected Combo comboJewelLevel;
    /**
     * 宝石对应属性。
     */
    private Combo comboJewelAttr;
    /**
     * 是否绑定
     */
    private Combo comboBind;
    /**
     * 出售价格
     */
    private Text textPrice;
    /**
     * 物品品质
     */
    private Combo comboQuality;
    /**
     * 是否可以出售
     */
    private Combo comboSale;
    /**
     * 物品等级
     */
    private Text textLevel;
    /**
     * 物品在每个物品栏的堆叠数量
     */
    private Text textAddtion;
    /**
     * 图标选择
     */
    private IconChooser iconChooser;
    
    /**
     * 加属性值
     */
    private Text textJewelValue;

    public JewelItemEditor(Composite parent, int style, DefaultDataObjectEditor owner) {
        super(parent, style, owner);

        String[] attrs = new String[ProjectData.getActiveProject().config.attrCalc.ATTRIBUTES.length];
        for (int i = 0; i < ProjectData.getActiveProject().config.attrCalc.ATTRIBUTES.length; i++) {
            attrs[i] = ProjectData.getActiveProject().config.attrCalc.ATTRIBUTES[i].name;
        }
        
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        setLayout(gridLayout);

        final Label label_7 = new Label(this, SWT.NONE);
        label_7.setText("宝石等级：");

        comboJewelLevel = new Combo(this, SWT.READ_ONLY);
        comboJewelLevel.setItems(new String[] {"1级", "2级", "3级", "4级", "5级", "6级", "7级", "8级", "9级", "10级","11级", "12级", "13级", "14级", "15级", "16级", "17级", "18级", "19级", "20级"});
        comboJewelLevel.setVisibleItemCount(10);
        comboJewelLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboJewelLevel.addSelectionListener(this);

        final Label label_9 = new Label(this, SWT.NONE);
        label_9.setLayoutData(new GridData());
        label_9.setText("物品等级：");

        textLevel = new Text(this, SWT.BORDER);
        textLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        textLevel.addFocusListener(AutoSelectAll.instance);
        textLevel.addModifyListener(this);

        final Label label_13 = new Label(this, SWT.NONE);
        label_13.setLayoutData(new GridData());
        label_13.setText("物品品质：");

        comboQuality = new Combo(this, SWT.READ_ONLY);
        comboQuality.setVisibleItemCount(10);
        final GridData gd_comboQuality = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboQuality.setLayoutData(gd_comboQuality);
        comboQuality.setItems(ProjectData.getActiveProject().config.COMBO_QUALITY);
        comboQuality.addSelectionListener(this);

        final Label label_22 = new Label(this, SWT.NONE);
        label_22.setLayoutData(new GridData());
        label_22.setText("物品图标：");
        
        iconChooser = new IconChooser(this, SWT.NONE, ProjectData.getActiveProject().config.iconSeries.get("item"));
        iconChooser.setHandler(owner);
        iconChooser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label label_6 = new Label(this, SWT.NONE);
        label_6.setLayoutData(new GridData());
        label_6.setText("宝石属性：");

        comboJewelAttr = new Combo(this, SWT.READ_ONLY);
        comboJewelAttr.setVisibleItemCount(30);
        final GridData gd_comboJewelAttr = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboJewelAttr.setLayoutData(gd_comboJewelAttr);
        comboJewelAttr.setItems(attrs);
        comboJewelAttr.addSelectionListener(this);
        
        final Label label_value = new Label(this,SWT.NONE);
        label_value.setLayoutData(new GridData());
        label_value.setText("加属性值");
        
        textJewelValue = new Text(this, SWT.BORDER);
        textJewelValue.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));
        textJewelValue.addFocusListener(AutoSelectAll.instance);
        textJewelValue.addModifyListener(owner);
        

        final Label label_5 = new Label(this, SWT.NONE);
        label_5.setLayoutData(new GridData());
        label_5.setText("堆叠数量：");

        textAddtion = new Text(this, SWT.BORDER);
        textAddtion.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        textAddtion.addFocusListener(AutoSelectAll.instance);
        textAddtion.addModifyListener(owner);

        final Label label_4 = new Label(this, SWT.NONE);
        label_4.setLayoutData(new GridData());
        label_4.setText("是否绑定：");

        comboBind = new Combo(this, SWT.READ_ONLY);
        comboBind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        comboBind.setItems(ProjectData.getActiveProject().config.COMBO_BIND);
        comboBind.addSelectionListener(this);

        final Label label_16 = new Label(this, SWT.NONE);
        label_16.setLayoutData(new GridData());
        label_16.setText("能否出售：");

        comboSale = new Combo(this, SWT.READ_ONLY);
        comboSale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        comboSale.addSelectionListener(this);
        comboSale.setItems(ProjectConfig.COMBO_YES_NO);

        final Label label_2 = new Label(this, SWT.NONE);
        label_2.setLayoutData(new GridData());
        label_2.setText("出售价格：");
        
        textPrice = new Text(this, SWT.BORDER);
        textPrice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        textPrice.addFocusListener(AutoSelectAll.instance);
        textPrice.addModifyListener(owner);
    }
    
    /**
     * 把界面上当前输入的值保存到编辑对象中。
     * @throws Exception
     */
    public void save() throws Exception {
        Item itemDataDef = (Item)owner.getEditObject();
        itemDataDef.bind = comboBind.getSelectionIndex();
        itemDataDef.playerLevel = comboJewelLevel.getSelectionIndex() + 1;
        try {
            itemDataDef.addition = Integer.parseInt(textAddtion.getText());
        } catch (NumberFormatException e) {
            throw new Exception("叠加数量输入格式错误！");
        }
        itemDataDef.iconIndex = iconChooser.getIconIndex();
        if (itemDataDef.iconIndex < 0) {
            throw new Exception("请选择正确的图标！");
        }
        itemDataDef.quality = comboQuality.getSelectionIndex();
        itemDataDef.sale = comboSale.getSelectionIndex() == Item.ATTRIBUTE_VALUE_YES;
        if (itemDataDef.sale) {
            try {
                itemDataDef.price = Integer.parseInt(textPrice.getText());
            } catch (NumberFormatException e) {
                throw new Exception("价格输入格式错误！");
            }
        }
        try {
            itemDataDef.level = Integer.parseInt(textLevel.getText());
        } catch (Exception e) {
            throw new Exception("级别输入错误！");
        }
        itemDataDef.extPropEntries.setValue("jewelattr", String.valueOf(comboJewelAttr.getSelectionIndex()));
        itemDataDef.extPropEntries.setValue("jewelattrvValue", String.valueOf(textJewelValue.getText()));
        itemDataDef.instance = false;
        itemDataDef.taskFlag = false;
        itemDataDef.available = Item.AVAILABLE_NO;
    }
    
    /**
     * 把编辑对象的值设置到界面中。
     * @throws Exception
     */
    public void load() throws Exception {
        Item editObject = (Item)owner.getEditObject();
        
        comboBind.select(editObject.bind);
        comboJewelLevel.select(editObject.playerLevel - 1);
        textAddtion.setText(String.valueOf(editObject.addition));
        iconChooser.setIcon(editObject.iconIndex);
        comboQuality.select(editObject.quality);
        if (editObject.sale) {
            comboSale.select(Item.ATTRIBUTE_VALUE_YES);
            textPrice.setText(String.valueOf(editObject.price));            
        } else {
            comboSale.select(Item.ATTRIBUTE_VALUE_NO);
            textPrice.setEditable(false);
        }
        textLevel.setText(String.valueOf(editObject.level));
        comboJewelAttr.select(editObject.extPropEntries.getValueAsInt("jewelattr"));
        
        try{
            textJewelValue.setText(editObject.extPropEntries.getValueAsString("jewelattrvValue"));
        }catch(NullPointerException e){
            textJewelValue.setText("0");
        }
        updatePreview();
    }
    
    protected void updatePreview() {
        Item editObject = (Item)owner.getEditObject();
        int jewelLevel = editObject.level;
        int attrType = comboJewelAttr.getSelectionIndex();
        float value = editObject.DataCalc.getJewelValue(jewelLevel);
        float attrValue = editObject.DataCalc.getAttributeValue(attrType, editObject);
        int addValue = Math.round(value / attrValue);
        String desc = ProjectData.getActiveProject().config.attrCalc.ATTRIBUTES[attrType].name + " +" + addValue;
        ((ItemEditor)owner).textDescription.setText(desc);
    }
    
    public void modifyText(ModifyEvent e) {
        if (e.getSource() == textLevel) {
            try {
                Item editObject = (Item)owner.getEditObject();
                editObject.level = Integer.parseInt(textLevel.getText());
                owner.setDirty(true);
                updatePreview();
            } catch (Exception e1) {
            }
        }
    }
    
    public void widgetDefaultSelected(SelectionEvent e) {}
    
    /**
     * 下拉列表框选择消息处理
     */
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() == comboSale) {
            if (comboSale.getSelectionIndex() == Item.ATTRIBUTE_VALUE_YES) {
                textPrice.setEditable(true);
            } else if (comboSale.getSelectionIndex() == Item.ATTRIBUTE_VALUE_NO) {
                textPrice.setEditable(false);
            }
        } else if (e.getSource() == comboJewelAttr) {
            updatePreview();
        }
        owner.setDirty(true);
    }
}
