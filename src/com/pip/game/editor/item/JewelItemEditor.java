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
 * ��ʯ���Ա༭�ؼ���
 */
public class JewelItemEditor extends AbstractDataObjectEditor implements SelectionListener, ModifyListener {
    /**
     * ��ʯ�ȼ���
     */
    protected Combo comboJewelLevel;
    /**
     * ��ʯ��Ӧ���ԡ�
     */
    private Combo comboJewelAttr;
    /**
     * �Ƿ��
     */
    private Combo comboBind;
    /**
     * ���ۼ۸�
     */
    private Text textPrice;
    /**
     * ��ƷƷ��
     */
    private Combo comboQuality;
    /**
     * �Ƿ���Գ���
     */
    private Combo comboSale;
    /**
     * ��Ʒ�ȼ�
     */
    private Text textLevel;
    /**
     * ��Ʒ��ÿ����Ʒ���Ķѵ�����
     */
    private Text textAddtion;
    /**
     * ͼ��ѡ��
     */
    private IconChooser iconChooser;
    
    /**
     * ������ֵ
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
        label_7.setText("��ʯ�ȼ���");

        comboJewelLevel = new Combo(this, SWT.READ_ONLY);
        comboJewelLevel.setItems(new String[] {"1��", "2��", "3��", "4��", "5��", "6��", "7��", "8��", "9��", "10��","11��", "12��", "13��", "14��", "15��", "16��", "17��", "18��", "19��", "20��"});
        comboJewelLevel.setVisibleItemCount(10);
        comboJewelLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboJewelLevel.addSelectionListener(this);

        final Label label_9 = new Label(this, SWT.NONE);
        label_9.setLayoutData(new GridData());
        label_9.setText("��Ʒ�ȼ���");

        textLevel = new Text(this, SWT.BORDER);
        textLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        textLevel.addFocusListener(AutoSelectAll.instance);
        textLevel.addModifyListener(this);

        final Label label_13 = new Label(this, SWT.NONE);
        label_13.setLayoutData(new GridData());
        label_13.setText("��ƷƷ�ʣ�");

        comboQuality = new Combo(this, SWT.READ_ONLY);
        comboQuality.setVisibleItemCount(10);
        final GridData gd_comboQuality = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboQuality.setLayoutData(gd_comboQuality);
        comboQuality.setItems(ProjectData.getActiveProject().config.COMBO_QUALITY);
        comboQuality.addSelectionListener(this);

        final Label label_22 = new Label(this, SWT.NONE);
        label_22.setLayoutData(new GridData());
        label_22.setText("��Ʒͼ�꣺");
        
        iconChooser = new IconChooser(this, SWT.NONE, ProjectData.getActiveProject().config.iconSeries.get("item"));
        iconChooser.setHandler(owner);
        iconChooser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label label_6 = new Label(this, SWT.NONE);
        label_6.setLayoutData(new GridData());
        label_6.setText("��ʯ���ԣ�");

        comboJewelAttr = new Combo(this, SWT.READ_ONLY);
        comboJewelAttr.setVisibleItemCount(30);
        final GridData gd_comboJewelAttr = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboJewelAttr.setLayoutData(gd_comboJewelAttr);
        comboJewelAttr.setItems(attrs);
        comboJewelAttr.addSelectionListener(this);
        
        final Label label_value = new Label(this,SWT.NONE);
        label_value.setLayoutData(new GridData());
        label_value.setText("������ֵ");
        
        textJewelValue = new Text(this, SWT.BORDER);
        textJewelValue.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false));
        textJewelValue.addFocusListener(AutoSelectAll.instance);
        textJewelValue.addModifyListener(owner);
        

        final Label label_5 = new Label(this, SWT.NONE);
        label_5.setLayoutData(new GridData());
        label_5.setText("�ѵ�������");

        textAddtion = new Text(this, SWT.BORDER);
        textAddtion.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        textAddtion.addFocusListener(AutoSelectAll.instance);
        textAddtion.addModifyListener(owner);

        final Label label_4 = new Label(this, SWT.NONE);
        label_4.setLayoutData(new GridData());
        label_4.setText("�Ƿ�󶨣�");

        comboBind = new Combo(this, SWT.READ_ONLY);
        comboBind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        comboBind.setItems(ProjectData.getActiveProject().config.COMBO_BIND);
        comboBind.addSelectionListener(this);

        final Label label_16 = new Label(this, SWT.NONE);
        label_16.setLayoutData(new GridData());
        label_16.setText("�ܷ���ۣ�");

        comboSale = new Combo(this, SWT.READ_ONLY);
        comboSale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        comboSale.addSelectionListener(this);
        comboSale.setItems(ProjectConfig.COMBO_YES_NO);

        final Label label_2 = new Label(this, SWT.NONE);
        label_2.setLayoutData(new GridData());
        label_2.setText("���ۼ۸�");
        
        textPrice = new Text(this, SWT.BORDER);
        textPrice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        textPrice.addFocusListener(AutoSelectAll.instance);
        textPrice.addModifyListener(owner);
    }
    
    /**
     * �ѽ����ϵ�ǰ�����ֵ���浽�༭�����С�
     * @throws Exception
     */
    public void save() throws Exception {
        Item itemDataDef = (Item)owner.getEditObject();
        itemDataDef.bind = comboBind.getSelectionIndex();
        itemDataDef.playerLevel = comboJewelLevel.getSelectionIndex() + 1;
        try {
            itemDataDef.addition = Integer.parseInt(textAddtion.getText());
        } catch (NumberFormatException e) {
            throw new Exception("�������������ʽ����");
        }
        itemDataDef.iconIndex = iconChooser.getIconIndex();
        if (itemDataDef.iconIndex < 0) {
            throw new Exception("��ѡ����ȷ��ͼ�꣡");
        }
        itemDataDef.quality = comboQuality.getSelectionIndex();
        itemDataDef.sale = comboSale.getSelectionIndex() == Item.ATTRIBUTE_VALUE_YES;
        if (itemDataDef.sale) {
            try {
                itemDataDef.price = Integer.parseInt(textPrice.getText());
            } catch (NumberFormatException e) {
                throw new Exception("�۸������ʽ����");
            }
        }
        try {
            itemDataDef.level = Integer.parseInt(textLevel.getText());
        } catch (Exception e) {
            throw new Exception("�����������");
        }
        itemDataDef.extPropEntries.setValue("jewelattr", String.valueOf(comboJewelAttr.getSelectionIndex()));
        itemDataDef.extPropEntries.setValue("jewelattrvValue", String.valueOf(textJewelValue.getText()));
        itemDataDef.instance = false;
        itemDataDef.taskFlag = false;
        itemDataDef.available = Item.AVAILABLE_NO;
    }
    
    /**
     * �ѱ༭�����ֵ���õ������С�
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
     * �����б��ѡ����Ϣ����
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
