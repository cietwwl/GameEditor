package com.pip.game.editor.shop;

import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;

import com.pip.game.data.CmccConfig;
import com.pip.game.data.DataObject;
import com.pip.game.data.Faction;
import com.pip.game.data.ProjectData;
import com.pip.game.data.Shop;
import com.pip.game.data.item.Item;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.property.ItemCellEditor;
import com.pip.util.AutoSelectAll;

public class ShopEditor extends DefaultDataObjectEditor {
    /*
     * ��Ʒ�б�༭���ơ�
     */
    public class ItemListCellModifier implements ICellModifier {
        public boolean canModify(Object element, String property) {
            if (element instanceof Shop.ShopItem) {
                Shop.ShopItem item = (Shop.ShopItem)element;
                int index = Integer.parseInt(property.substring(1));
                if (item.item == null) {
                    return index == 0;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }
        
        public Object getValue(Object element, String property) {
            if (element instanceof Shop.ShopItem) {
                Shop.ShopItem item = (Shop.ShopItem)element;
                int index = Integer.parseInt(property.substring(1));
                switch (index) {
                case 0:    // ��Ʒ����
                    if (item.item == null) {
                        return new Integer(0);
                    } else {
                        return new Integer(item.item.id);
                    }
                case 1:    // ����
                    return String.valueOf(item.count);
                case 2:    // ˢ�¼��
                    return String.valueOf(item.refresh / 1000);
                case 3:    // �ۿ���
                    return String.valueOf(item.discount);
                case 4:    // ���ƹ�������
                    return String.valueOf(item.buyLimit);
                case 5:    // ��Ǯ�۸�
                    return String.valueOf(getItemPrice(item, Shop.TYPE_MONEY));
                case 6:    // i�Ҽ۸�
                    return String.valueOf(getItemPrice(item, Shop.TYPE_IMONEY) / 3600f);
                case 7:    // �Ƿ�����ʹ�ð�Ԫ��
                    return new Boolean(item.allowUseBindImoney);
                case 8:    // ��������
                {
                    return item.requirements;
                }
                default:
                    return "";
                }
            }
            return "";
        }

        public void modify(Object element, String property, Object value) {
            TableItem ti = (TableItem)element;
            if (ti.getData() instanceof Shop.ShopItem) {
                Shop.ShopItem item = (Shop.ShopItem)ti.getData();
                int index = Integer.parseInt(property.substring(1));
                switch (index) {
                case 0:    // ��Ʒ����
                {
                    int newItemID = ((Integer)value).intValue();
                    Item newItem = ((Shop)editObject).owner.findItemOrEquipment(newItemID);
                    if (newItem == null) {
                        return;
                    }
                    if (item.item != null) {
                        // �޸���Ʒ
                        if (item.item.id == newItemID) {
                            return;
                        }
                        item.item = newItem;
                        
                        // ��������Ʒ��ʱ�����������Ƽ��۸�Ϊ���ۼ۸��2��
                        setItemPrice(item, Shop.TYPE_MONEY, newItem.price * 2);
                        itemListViewer.update(item, null);
                    } else {
                        // �����Ʒ
                        item = new Shop.ShopItem();
                        item.item = newItem;
                        setItemPrice(item, Shop.TYPE_MONEY, newItem.price * 2);
                        ((Shop)editObject).items.add(item);
                        itemListViewer.refresh();
                    }
                    setDirty(true);
                    break;
                }
                case 1:    // ����
                {
                    try {
                        int newValue = Integer.parseInt((String)value);
                        if (newValue < 0 || newValue == item.count) {
                            return;
                        }
                        item.count = newValue;
                        itemListViewer.update(item, null);
                        setDirty(true);
                    } catch (Exception e) {
                        return;
                    }
                    break;
                }
                case 2:    // ˢ�¼��
                {
                    try {
                        int newValue = Integer.parseInt((String)value);
                        if (newValue < 0 || newValue == item.refresh / 1000) {
                            return;
                        }
                        item.refresh = newValue * 1000;
                        itemListViewer.update(item, null);
                        setDirty(true);
                    } catch (Exception e) {
                        return;
                    }
                    break;
                }
                case 3:    // �ۿ���
                {
                    try {
                        int newValue = Integer.parseInt((String)value);
                        if (newValue < 0 || newValue == item.discount) {
                            return;
                        }
                        item.discount = newValue;
                        itemListViewer.update(item, null);
                        setDirty(true);
                    } catch (Exception e) {
                        return;
                    }
                    break;
                }
                case 4:    // ���ƹ�������
                {
                    try {
                        int newValue = Integer.parseInt((String)value);
                        if (newValue < 0 || newValue == item.buyLimit) {
                            return;
                        }
                        item.buyLimit = newValue;
                        itemListViewer.update(item, null);
                        setDirty(true);
                    } catch (Exception e) {
                        return;
                    }
                    break;
                }
                case 5:    // ��Ǯ�۸�
                {
                    try {
                        int newValue = Integer.parseInt((String)value);
                        if (newValue < 0) {
                            return;
                        }
                        if (!setItemPrice(item, Shop.TYPE_MONEY, newValue)) {
                            return;
                        }
                        itemListViewer.update(item, null);
                        setDirty(true);
                    } catch (Exception e) {
                        return;
                    }
                    break;
                }
                case 6:    // i�Ҽ۸�
                {
                    try {
                        float newValue = Float.parseFloat((String)value);
                        if (newValue < 0) {
                            return;
                        }
                        if (!setItemPrice(item, Shop.TYPE_IMONEY, (int)(newValue * 3600))) {
                            return;
                        }
                        itemListViewer.update(item, null);
                        setDirty(true);
                    } catch (Exception e) {
                        return;
                    }
                    break;
                }
                case 7:    // �Ƿ�����ʹ�ð�Ԫ��
                {
                    boolean newv = ((Boolean)value).booleanValue();
                    if (newv != item.allowUseBindImoney) {
                        item.allowUseBindImoney = newv;
                        itemListViewer.update(item, null);
                        setDirty(true);
                    }
                    break;
                }
                case 8:    // ��������
                {
                    if (value != null) {
                        itemListViewer.update(item, null);
                        setDirty(true);
                    }
                    break;
                }
                }
            }
        }
    }
    /*
     * �̵���Ʒ�б��ı��ṩ�ߡ�
     */
    public class ShopItemListLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            Shop.ShopItem item = (Shop.ShopItem) element;
            if (item.item == null) {
                // ��һ��������������Ʒ�ģ����⴦��
                if (columnIndex == 0) {
                    return "���������...";
                } else {
                    return "";
                }
            }
            switch (columnIndex) {
            case 0:    // ��Ʒ����
                return item.item.toString();
            case 1:    // ����
                return String.valueOf(item.count);
            case 2:    // ˢ�¼��
                return String.valueOf(item.refresh / 1000);
            case 3:    // �ۿ���
                return String.valueOf(item.discount);
            case 4:    // ���ƹ�������
                return String.valueOf(item.buyLimit);
            case 5:    // ��Ǯ�۸�
                return String.valueOf(getItemPrice(item, Shop.TYPE_MONEY));
            case 6:    // i�Ҽ۸�
                return String.valueOf(getItemPrice(item, Shop.TYPE_IMONEY) / 3600f);
            case 7:    // �Ƿ�����ʹ�ð�Ԫ��
                return item.allowUseBindImoney ? "��" : "��";
            case 8:    // ��������
            {
                return Shop.BuyRequirement.toString(item.requirements, false, false);
            }
            default:
                return "";
            }
        }

        public Image getColumnImage(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return EditorPlugin.getDefault().getImageRegistry().get("empty");
            }
            return null;
        }
    }

    /*
     * �̵���Ʒ�б������ṩ�ߡ�
     */
    public class ShopItemListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            Shop shop = (Shop)inputElement;
            Object[] ret = new Object[shop.items.size() + 1];
            shop.items.toArray(ret);
            Shop.ShopItem newItem = new Shop.ShopItem();
            ret[ret.length - 1] = newItem;
            return ret;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    protected TableViewer itemListViewer;
    protected Table itemList;
    protected TableColumn itemColumn, countColumn, refreshColumn, discountColumn, buyLimitColumn,
            moneyPriceColumn, imoneyPriceColumn, bindimoneyPriceColumn, conditionColumn;
    private Text textDesc;
    private Text textTitle;
    private Text textID;
    private Combo comboFaction;
    protected Composite container;
    public static final String ID = "com.pip.game.editor.shop.ShopEditor"; //$NON-NLS-1$

    /**
     * Create contents of the editor part
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        container.setLayout(gridLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("ID��");

        textID = new Text(container, SWT.BORDER);
        final GridData gd_textID = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textID.setLayoutData(gd_textID);
        textID.addFocusListener(AutoSelectAll.instance);
        textID.addModifyListener(this);

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setText("���⣺");

        textTitle = new Text(container, SWT.BORDER);
        final GridData gd_textTitle = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textTitle.setLayoutData(gd_textTitle);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);

        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setText("������");

        textDesc = new Text(container, SWT.BORDER);
        final GridData gd_textDesc = new GridData(SWT.FILL, SWT.CENTER, true, false,3,1);
        gd_textDesc.heightHint = 50;
        textDesc.setLayoutData(gd_textDesc);
        textDesc.addFocusListener(AutoSelectAll.instance);
        textDesc.addModifyListener(this);
        
        final Label label_3 = new Label(container,SWT.NONE);
        label_3.setText("��Ӫ��");
        
        List<DataObject> factions = ProjectData.getActiveProject().getDictDataListByType(Faction.class);
        String[] labels = new String[factions.size()];
        for (int i = 0; i < factions.size(); i++) {
            labels[i] = factions.get(i).toString();
        }
        
        comboFaction = new Combo(container,SWT.NONE);
        final GridData gd_comboFaction = new GridData(SWT.FILL,SWT.CENTER,true,false,3,1);
        comboFaction.setLayoutData(gd_comboFaction);
        comboFaction.setItems(labels);
        comboFaction.addModifyListener(this);

        itemListViewer = new TableViewer(container, SWT.FULL_SELECTION | SWT.BORDER);
        itemListViewer.setLabelProvider(new ShopItemListLabelProvider());
        itemListViewer.setContentProvider(new ShopItemListContentProvider());
        itemList = itemListViewer.getTable();
        itemList.setLinesVisible(true);
        itemList.setHeaderVisible(true);
        final GridData gd_itemList = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
        itemList.setLayoutData(gd_itemList);
        itemList.addListener(SWT.KeyDown, new Listener() {
            public void handleEvent(Event event) {
                if ((event.stateMask & SWT.SHIFT) != 0) {
                    if (event.keyCode == SWT.ARROW_UP) {
                        onMoveUpItem();
                        event.doit = false;
                    } else if (event.keyCode == SWT.ARROW_DOWN) {
                        onMoveDownItem();
                        event.doit = false;
                    }
                }
                if (event.keyCode == SWT.DEL) {
                    onDelItem();
                }
            }
        });

        itemColumn = new TableColumn(itemList, SWT.NONE);
        itemColumn.setWidth(160);
        itemColumn.setText("��Ʒ");

        countColumn = new TableColumn(itemList, SWT.NONE);
        countColumn.setWidth(40);
        countColumn.setText("����");

        refreshColumn = new TableColumn(itemList, SWT.NONE);
        refreshColumn.setWidth(70);
        refreshColumn.setText("ˢ��(��)");

        discountColumn = new TableColumn(itemList, SWT.NONE);
        discountColumn.setWidth(70);
        discountColumn.setText("�ۿ���%");

        buyLimitColumn = new TableColumn(itemList, SWT.NONE);
        buyLimitColumn.setWidth(80);
        buyLimitColumn.setText("���ƹ���");

        moneyPriceColumn = new TableColumn(itemList, SWT.NONE);
        moneyPriceColumn.setWidth(80);
        moneyPriceColumn.setText("��Ǯ�۸�");

        imoneyPriceColumn = new TableColumn(itemList, SWT.NONE);
        imoneyPriceColumn.setWidth(80);
        imoneyPriceColumn.setText("Ԫ���۸�");

        bindimoneyPriceColumn = new TableColumn(itemList, SWT.NONE);
        bindimoneyPriceColumn.setWidth(160);
        bindimoneyPriceColumn.setText("����ʹ�ð�Ԫ����");
        
        conditionColumn = new TableColumn(itemList, SWT.NONE);
        conditionColumn.setWidth(300);
        conditionColumn.setText("��������");
        
        itemListViewer.setColumnProperties(new String[] {
                "c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8"
        });
        itemListViewer.setCellModifier(new ItemListCellModifier());
        itemListViewer.setCellEditors(new CellEditor[] {
                new ItemCellEditor(itemList),
                new TextCellEditor(itemList),
                new TextCellEditor(itemList),
                new TextCellEditor(itemList),
                new TextCellEditor(itemList),
                new TextCellEditor(itemList),
                new TextCellEditor(itemList),
                new CheckboxCellEditor(itemList),
                new BuyRequirementsCellEditor(itemList)
        });

        
        // ���ó�ʼֵ
        Shop dataDef = (Shop) editObject;
        textID.setText(String.valueOf(dataDef.id));
        textTitle.setText(dataDef.title);
        textDesc.setText(dataDef.description);
        comboFaction.select(dataDef.faction);
        itemListViewer.setInput(dataDef);

        setDirty(false);
        setPartName(this.getEditorInput().getName());
        saveStateToUndoBuffer();
        
//        for (ShopItem item : dataDef.items) {
//            if (getItemPrice(item, Shop.TYPE_IMONEY) > 0) {
//                System.out.print(item.item.title);
//                System.out.print("\t");
//                System.out.print(getItemPrice(item, Shop.TYPE_IMONEY) * 1.0f / 36);
//                System.out.print("\t");
//                System.out.print(item.item.description);
//                System.out.print("\t");
//                System.out.println("�x��->�����u��->" + dataDef.title);
//            }
//        }
    }

    /**
     * ���浱ǰ�༭���ݡ�
     */
    protected void saveData() throws Exception {
        Shop dataDef = (Shop) editObject;
        
        // ��ȡ���룺����ID�����⡢����
        try {
            dataDef.id = Integer.parseInt(textID.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ��ID��");
        }
        dataDef.title = textTitle.getText().trim();
        dataDef.description = textDesc.getText();
        dataDef.faction = comboFaction.getSelectionIndex();

        // �������Ϸ���
        DataObject dobj = ProjectData.getActiveProject().findObject(dataDef.getClass(), dataDef.id);
        if (dobj != null && dobj != getSaveTarget()) {
            throw new Exception("ID�ظ������������롣");
        }
        if (dataDef.title.length() == 0) {
            throw new Exception("��������⡣");
        }
        HashSet<Integer> usedItems = new HashSet<Integer>();
        for (Shop.ShopItem item : dataDef.items) {
            if (usedItems.contains(item.item.id)) {
                throw new Exception("һ���̵��в���������ظ�����Ʒ��");
            }
            usedItems.add(item.item.id);
        }
        
        // ������������Ѵ��룬������Ѵ������Ʒ��ƥ���Ƿ���ȷ
        String errorInfo = "";
        CmccConfig cmccConfig = ProjectData.getActiveProject().cmccConfig;
        for (Shop.ShopItem item : dataDef.items) {
            int iprice = getItemPrice(item, Shop.TYPE_IMONEY);
            String code = getItemConsumeCode(item);
            String thisError = null;
            if (iprice == 0 && code != null) {
                thisError = item.item.title + "�����������Ѵ��룬��û������Ԫ���۸�"; 
            } else if (code != null) {
                if (cmccConfig == null) {
                    thisError = item.item.title + "��cmcc_config.xmlû����ȷ���룬�޷���֤���Ѵ��롣";
                } else if (cmccConfig.getPrice(code) * 360 != iprice) {
                    if (cmccConfig.getPrice(code) == 0) {
                        thisError = item.item.title + "�����Ѵ���" + code + "��Ч��";
                    } else {
                        thisError = item.item.title + "�����Ѵ���" + code + "�ļ۸�Ϊ" + (cmccConfig.getPrice(code) * 36 / 10) + "������д��Ԫ���۸�Ϊ" + iprice + "��";
                    }
                } else if (!item.item.title.equals(cmccConfig.getItemName(code))) {
                    thisError = item.item.title + "�����Ѵ���" + code + "������Ϊ" + cmccConfig.getItemName(code) + "��";
                }
            }
            if (thisError != null) {
                if (errorInfo.length() > 0) {
                    errorInfo += "\n";
                }
                errorInfo += thisError;
            }
        }
        if (errorInfo.length() > 0) {
            errorInfo = "�������Ѵ��������ȷ���Ƿ������\n" + errorInfo;
            if (!MessageDialog.openConfirm(getSite().getShell(), "���Ѵ������", errorInfo)) {
                throw new Exception("������ȡ����");
            }
        }
    }

    /*
     * ����undo״̬
     */
    protected Object saveState() {
        return editObject.save();
    }

    /*
     * �ָ������ת̨
     */
    protected void loadState(Object stateObj) {
        editObject.load((Element) stateObj);
        itemListViewer.refresh();
    }

    /**
     * ȡ��һ����Ʒָ�����ͻ��ҵļ۸�
     * @param item ��Ʒ
     * @param type ��������
     * @return ���һ������������Ϊ�۳����һ�������ƥ�䣬������������
     */
    private int getItemPrice(Shop.ShopItem item, int type) {
        for (Shop.BuyRequirement req : item.requirements) {
            if (req.type == type && req.deduct) {
                return req.amount;
            }
        }
        return 0;
    }
    
    /**
     * ����һ����Ʒ��ָ�����ͻ��ҵļ۸�
     * @param item ��Ʒ
     * @param type ��������
     * @param value �¼۸�
     * @return ����۸��ԭ������ͬ������false
     */
    private boolean setItemPrice(Shop.ShopItem item, int type, int value) {
        for (Shop.BuyRequirement req : item.requirements) {
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
            item.requirements.add(req);
            return true;
        }
    }

    /*
     * ȡ��һ����Ʒ�����Ѵ��루���ָ���˵Ļ�����
     * @param item ��Ʒ
     */
    private String getItemConsumeCode(Shop.ShopItem item) {
        for (Shop.BuyRequirement req : item.requirements) {
            if (req.type == Shop.TYPE_CONSUMECODE) {
                return req.varName;
            }
        }
        return null;
    }
    
    /*
     * �ѵ�ǰѡ�е���Ʒ����
     */
    private void onMoveUpItem() {
        int sel = itemList.getSelectionIndex();
        if (sel > 0) {
            Shop shop = (Shop)editObject;
            Shop.ShopItem item = shop.items.remove(sel);
            shop.items.add(sel - 1, item);
            itemListViewer.refresh();
            itemList.setSelection(sel - 1);
            setDirty(true);
        }
    }
    
    /*
     * �ѵ�ǰѡ�е���Ʒ����
     */
    private void onMoveDownItem() {
        int sel = itemList.getSelectionIndex();
        Shop shop = (Shop)editObject;
        if (sel != -1 && sel < shop.items.size() - 1) {
            Shop.ShopItem item = shop.items.remove(sel);
            shop.items.add(sel + 1, item);
            itemListViewer.refresh();
            itemList.setSelection(sel + 1);
            setDirty(true);
        }
    }
    
    /*
     * ɾ��ѡ�е���Ʒ
     */
    private void onDelItem() {
        int sel = itemList.getSelectionIndex();
        Shop shop = (Shop)editObject;
        if (sel != -1 && sel < shop.items.size()) {
            shop.items.remove(sel);
            itemListViewer.refresh();
            itemList.setSelection(sel);
            setDirty(true);
        }
    }
}
