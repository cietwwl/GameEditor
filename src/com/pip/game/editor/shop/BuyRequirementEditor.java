package com.pip.game.editor.shop;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.Currency;
import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.Rank;
import com.pip.game.data.Shop;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.util.ItemChooser;

/**
 * 编辑一个购买需求的组件。
 * @author lighthu
 */
public class BuyRequirementEditor extends Composite {
    private Combo comboDictObject;
    private Text textVarDesc;
    private Text textVarValue;
    private Text textVarName;
    private Text textAmount;
    private ItemChooser itemChooser;
    private Combo comboType;
    private Label labelItem, labelAmount, labelDictObject;
    private Button buttonDeduct;
    private Label labelVarName;
    private Label labelVarValue;
    private Label labelVarDesc;

    private Shop.BuyRequirement editObject;

    private static final int[] TYPE_MAPPING = { Shop.TYPE_MONEY, Shop.TYPE_IMONEY, Shop.TYPE_ITEM, Shop.TYPE_VARIABLE, Shop.TYPE_LEVEL, Shop.TYPE_CONSUMECODE };
    private static final String[] TYPE_NAMES = { "金钱", "元宝", "物品", "属性变量", "级别", "消费代码" };
    
    /**
     * Create the composite
     * @param parent
     * @param style
     */
    public BuyRequirementEditor(Composite parent, int style, Shop.BuyRequirement editObj) {
        super(parent, style);
        this.editObject = editObj;
        
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 15;
        setLayout(gridLayout);

        final Label label = new Label(this, SWT.NONE);
        label.setText("类型：");

        comboType = new Combo(this, SWT.READ_ONLY);
        comboType.setVisibleItemCount(10);
        List<DataObject> objs = ProjectData.getActiveProject().getDictDataListByType(Currency.class);
        String[] items = new String[objs.size() + TYPE_NAMES.length];
        System.arraycopy(TYPE_NAMES, 0, items, 0, TYPE_NAMES.length);
        for (int i = 0; i < objs.size(); i++) {
            items[i + TYPE_NAMES.length] = objs.get(i).title;
        }
        comboType.setItems(items);
        final GridData gd_comboType = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboType.setLayoutData(gd_comboType);
        comboType.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                updateType();
            }
        });

        labelItem = new Label(this, SWT.NONE);
        final GridData gd_labelItem = new GridData();
        labelItem.setLayoutData(gd_labelItem);
        labelItem.setText("物品：");

        itemChooser = new ItemChooser(this, SWT.NONE);
        final GridData gd_itemChooser = new GridData(SWT.FILL, SWT.CENTER, true, false);
        itemChooser.setLayoutData(gd_itemChooser);

        labelAmount = new Label(this, SWT.NONE);
        labelAmount.setLayoutData(new GridData());
        labelAmount.setText("数量：");

        textAmount = new Text(this, SWT.BORDER);
        final GridData gd_textAmount = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textAmount.setLayoutData(gd_textAmount);

        buttonDeduct = new Button(this, SWT.CHECK);
        buttonDeduct.setLayoutData(new GridData());
        buttonDeduct.setText("扣除");

        labelVarName = new Label(this, SWT.NONE);
        labelVarName.setLayoutData(new GridData());
        labelVarName.setText("变量名：");

        textVarName = new Text(this, SWT.BORDER);
        final GridData gd_textVarName = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textVarName.setLayoutData(gd_textVarName);

        labelVarValue = new Label(this, SWT.NONE);
        labelVarValue.setLayoutData(new GridData());
        labelVarValue.setText("达到：");

        textVarValue = new Text(this, SWT.BORDER);
        final GridData gd_textVarValue = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textVarValue.setLayoutData(gd_textVarValue);

        labelVarDesc = new Label(this, SWT.NONE);
        labelVarDesc.setLayoutData(new GridData());
        labelVarDesc.setText("描述：");

        textVarDesc = new Text(this, SWT.BORDER);
        final GridData gd_textVarDesc = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textVarDesc.setLayoutData(gd_textVarDesc);

        labelDictObject = new Label(this, SWT.NONE);
        labelDictObject.setLayoutData(new GridData());
        labelDictObject.setText("选择：");

        comboDictObject = new Combo(this, SWT.READ_ONLY);
        final GridData gd_comboDictObject = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboDictObject.setLayoutData(gd_comboDictObject);
        
        update();
    }
    
    private void updateType() {
        int sel = comboType.getSelectionIndex();
        int type;
        Currency currency = null;
        if (sel < TYPE_NAMES.length) {
            type = TYPE_MAPPING[sel];
        } else {
            List<DataObject> objs = ProjectData.getActiveProject().getDictDataListByType(Currency.class);
            currency = (Currency)objs.get(sel - TYPE_NAMES.length);
            type = currency.type;
        }
        switch (type) {
        case Shop.TYPE_IMONEY:
        case Shop.TYPE_MONEY:
            showControl(labelAmount);
            showControl(textAmount);
            hideControl(labelItem);
            hideControl(itemChooser);
            showControl(buttonDeduct);
            hideControl(labelVarName);
            hideControl(textVarName);
            hideControl(labelVarValue);
            hideControl(textVarValue);
            hideControl(labelVarDesc);
            hideControl(textVarDesc);
            hideControl(labelDictObject);
            hideControl(comboDictObject);
            break;
        case Shop.TYPE_ITEM:
            showControl(labelAmount);
            showControl(textAmount);
            showControl(labelItem);
            showControl(itemChooser);
            showControl(buttonDeduct);
            hideControl(labelVarName);
            hideControl(textVarName);
            hideControl(labelVarValue);
            hideControl(textVarValue);
            hideControl(labelVarDesc);
            hideControl(textVarDesc);
            hideControl(labelDictObject);
            hideControl(comboDictObject);
            break;
        case Shop.TYPE_VARIABLE:
            hideControl(labelAmount);
            hideControl(textAmount);
            hideControl(labelItem);
            hideControl(itemChooser);
            hideControl(buttonDeduct);
            showControl(labelVarName);
            showControl(textVarName);
            showControl(labelVarValue);
            showControl(textVarValue);
            showControl(labelVarDesc);
            showControl(textVarDesc);
            hideControl(labelDictObject);
            hideControl(comboDictObject);
            break;
        case Shop.TYPE_LEVEL:
            showControl(labelAmount);
            showControl(textAmount);
            hideControl(labelItem);
            hideControl(itemChooser);
            hideControl(buttonDeduct);
            hideControl(labelVarName);
            hideControl(textVarName);
            hideControl(labelVarValue);
            hideControl(textVarValue);
            hideControl(labelVarDesc);
            hideControl(textVarDesc);
            hideControl(labelDictObject);
            hideControl(comboDictObject);
            break;
        case Shop.TYPE_CONSUMECODE:
            hideControl(labelAmount);
            hideControl(textAmount);
            hideControl(labelItem);
            hideControl(itemChooser);
            hideControl(buttonDeduct);
            hideControl(labelVarName);
            showControl(textVarName);
            hideControl(labelVarValue);
            hideControl(textVarValue);
            hideControl(labelVarDesc);
            hideControl(textVarDesc);
            hideControl(labelDictObject);
            hideControl(comboDictObject);
            break;
        default:
            if (currency.type == Currency.CURRENCY_NUMBER) {
                showControl(labelAmount);
                showControl(textAmount);
                hideControl(labelItem);
                hideControl(itemChooser);
                showControl(buttonDeduct);
                hideControl(labelVarName);
                hideControl(textVarName);
                hideControl(labelVarValue);
                hideControl(textVarValue);
                hideControl(labelVarDesc);
                hideControl(textVarDesc);
                hideControl(labelDictObject);
                hideControl(comboDictObject);
            } else {
                hideControl(labelAmount);
                hideControl(textAmount);
                hideControl(labelItem);
                hideControl(itemChooser);
                hideControl(buttonDeduct);
                hideControl(labelVarName);
                hideControl(textVarName);
                hideControl(labelVarValue);
                hideControl(textVarValue);
                hideControl(labelVarDesc);
                hideControl(textVarDesc);
                showControl(labelDictObject);
                showControl(comboDictObject);
                
                // 设置选择项目
                try {
                    List<DataObject> cands = ProjectData.getActiveProject().getDictDataListByType(currency.dictObjectClass);
                    String[] items = new String[cands.size()];
                    for (int i = 0; i < cands.size(); i++) {
                        items[i] = cands.get(i).toString();
                    }
                    comboDictObject.setItems(items);
                    if (cands.size() > 0) {
                        comboDictObject.select(0);
                    } else {
                        comboDictObject.select(-1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        }
        layout();
    }
    
    private void hideControl(Control obj) {
        obj.setVisible(false);
        ((GridData)obj.getLayoutData()).exclude = true;
    }
    
    private void showControl(Control obj) {
        obj.setVisible(true);
        ((GridData)obj.getLayoutData()).exclude = false;
    }

    /**
     * 初始化界面数值。
     */
    public void update() {
        boolean found = false;
        for (int i = 0; i < TYPE_MAPPING.length; i++) {
            if (editObject.type == TYPE_MAPPING[i]) {
                comboType.select(i);
                found = true;
                break;
            }
        }
        if (!found) {
            List<DataObject> objs = ProjectData.getActiveProject().getDictDataListByType(Currency.class);
            for (int i = 0; i < objs.size(); i++) {
                Currency currency = (Currency)objs.get(i);
                if (currency.id == editObject.type) {
                    comboType.select(i + TYPE_NAMES.length);
                    break;
                }
            }
        }
        updateType();

        // 根据类型设置值
        if (editObject.type == Shop.TYPE_MONEY) {
            buttonDeduct.setSelection(editObject.deduct);
            textAmount.setText(String.valueOf(editObject.amount));
        } else if (editObject.type == Shop.TYPE_IMONEY) {
            buttonDeduct.setSelection(editObject.deduct);
            textAmount.setText(String.valueOf(editObject.amount / 3600.0f));
        } else if (editObject.type == Shop.TYPE_ITEM) {
            if (editObject.item != null) {
                itemChooser.setItemID(editObject.item.id);
            }
            buttonDeduct.setSelection(editObject.deduct);
            textAmount.setText(String.valueOf(editObject.amount));
        } else if (editObject.type == Shop.TYPE_VARIABLE) {
            textVarName.setText(editObject.varName);
            textVarValue.setText(String.valueOf(editObject.amount));
            textVarDesc.setText(editObject.varDesc);
        } else if (editObject.type == Shop.TYPE_LEVEL) {
            textAmount.setText(String.valueOf(editObject.amount));
        } else if (editObject.type == Shop.TYPE_CONSUMECODE) {
            textVarName.setText(editObject.varName);
        } else {
            Currency currency = (Currency)ProjectData.getActiveProject().findDictObject(Currency.class, editObject.type);
            if (currency.type == Currency.CURRENCY_NUMBER) {
                buttonDeduct.setSelection(editObject.deduct);
                textAmount.setText(String.valueOf(editObject.amount));
            } else {
                try {
                    List<DataObject> cands = ProjectData.getActiveProject().getDictDataListByType(currency.dictObjectClass);
                    for (int i = 0; i < cands.size(); i++) {
                        if (cands.get(i).id == editObject.amount) {
                            comboDictObject.select(i);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 保存当前的结果。
     */
    public void save() {
        int sel = comboType.getSelectionIndex();
        Currency currency = null;
        if (sel < TYPE_NAMES.length) {
            editObject.type = TYPE_MAPPING[sel];
        } else {
            List<DataObject> objs = ProjectData.getActiveProject().getDictDataListByType(Currency.class);
            currency = (Currency)objs.get(sel - TYPE_NAMES.length);
            editObject.type = currency.id;
        }
        switch (editObject.type) {
        case Shop.TYPE_MONEY:
            try {
                editObject.amount = Integer.parseInt(textAmount.getText());
            } catch (Exception e) {
                editObject.amount = 0;
            }
            editObject.item = null;
            editObject.deduct = buttonDeduct.getSelection();
            break;
        case Shop.TYPE_IMONEY:
            try {
                editObject.amount = (int)(Float.parseFloat(textAmount.getText()) * 3600);
            } catch (Exception e) {
                editObject.amount = 0;
            }
            editObject.item = null;
            editObject.deduct = buttonDeduct.getSelection();
            break;
        case Shop.TYPE_ITEM:
            try {
                editObject.amount = Integer.parseInt(textAmount.getText());
            } catch (Exception e) {
                editObject.amount = 0;
            }
            editObject.item = ProjectData.getActiveProject().findItemOrEquipment(itemChooser.getItemID());
            editObject.deduct = buttonDeduct.getSelection();
            break;
        case Shop.TYPE_VARIABLE:
            editObject.varName = textVarName.getText();
            try {
                editObject.amount = Integer.parseInt(textVarValue.getText());
            } catch (Exception e) {
                editObject.amount = 0;
            }
            editObject.varDesc = textVarDesc.getText();
            editObject.deduct = false;
            break;
        case Shop.TYPE_LEVEL:
            try {
                editObject.amount = Integer.parseInt(textAmount.getText());
            } catch (Exception e) {
                editObject.amount = 0;
            }
            editObject.item = null;
            editObject.deduct = false;
            break;
        case Shop.TYPE_CONSUMECODE:
            editObject.varName = textVarName.getText();
            editObject.deduct = false;
            break;
        default:
            if (currency.type == Currency.CURRENCY_NUMBER) {
                try {
                    editObject.amount = Integer.parseInt(textAmount.getText());
                } catch (Exception e) {
                    editObject.amount = 0;
                }
                editObject.item = null;
                editObject.deduct = buttonDeduct.getSelection();
            } else {
                try {
                    List<DataObject> cands = ProjectData.getActiveProject().getDictDataListByType(currency.dictObjectClass);
                    editObject.amount = cands.get(comboDictObject.getSelectionIndex()).id;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editObject.item = null;
                editObject.deduct = false;
            }
            break;
        }
    }
}
