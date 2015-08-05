package com.pip.game.editor.shop;

import org.eclipse.jface.viewers.CellEditor;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.Shop;
import com.pip.game.data.TeleportSet;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.property.LocationCellEditor;
import com.pip.util.AutoSelectAll;

public class TeleportSetEditor extends DefaultDataObjectEditor {
    /*
     * �����б�༭���ơ�
     */
    class ItemListCellModifier implements ICellModifier {
        public boolean canModify(Object element, String property) {
            if (element instanceof TeleportSet.Teleport) {
                TeleportSet.Teleport item = (TeleportSet.Teleport)element;
                int index = Integer.parseInt(property.substring(1));
                if (item.mapID == 0) {
                    return index == 0;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }
        
        public Object getValue(Object element, String property) {
            if (element instanceof TeleportSet.Teleport) {
                TeleportSet.Teleport item = (TeleportSet.Teleport)element;
                int index = Integer.parseInt(property.substring(1));
                switch (index) {
                case 0:    // Ŀ��λ��
                    return new int[] { item.mapID, item.x, item.y };
                case 1:    // ��������
                    return item.name;
                case 2:    // ��Ǯ�۸�
                    return String.valueOf(getItemPrice(item, Shop.TYPE_MONEY));
                case 3:    // i�Ҽ۸�
                    return String.valueOf(getItemPrice(item, Shop.TYPE_IMONEY) / 3600f);
                case 4:    // ��������
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
            if (ti.getData() instanceof TeleportSet.Teleport) {
                TeleportSet.Teleport item = (TeleportSet.Teleport)ti.getData();
                int index = Integer.parseInt(property.substring(1));
                switch (index) {
                case 0:    // Ŀ��λ��
                {
                    int[] newValue = (int[])value;
                    if (item.mapID != 0) {
                        // �޸�
                        if (item.mapID == newValue[0] && item.x == newValue[1] && item.y == newValue[2]) {
                            return;
                        }
                        item.mapID = newValue[0];
                        item.x = newValue[1];
                        item.y = newValue[2];
                        itemListViewer.update(item, null);
                    } else {
                        // ���
                        if (newValue[0] == 0) {
                            return;
                        }
                        item = new TeleportSet.Teleport();
                        item.mapID = newValue[0];
                        item.x = newValue[1];
                        item.y = newValue[2];
                        item.name = GameMapInfo.toString(ProjectData.getActiveProject(), item.mapID);
                        ((TeleportSet)editObject).items.add(item);
                        itemListViewer.refresh();
                    }
                    setDirty(true);
                    break;
                }
                case 1:    // ����
                {
                    if (!item.name.equals(value)) {
                        item.name = (String)value;
                        itemListViewer.update(item, null);
                        setDirty(true);
                    }
                    break;
                }
                case 2:    // ��Ǯ�۸�
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
                case 3:    // i�Ҽ۸�
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
                case 4:    // ��������
                {
                    if (value != null) {
                        itemListViewer.update(item, null);
                        setDirty(true);
                    }
                    break;
                }
                default :
                    break;
                }
            }
        }
    }
    /*
     * �������б��ı��ṩ�ߡ�
     */
    class ItemListLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            TeleportSet.Teleport item = (TeleportSet.Teleport) element;
            if (item.mapID == 0) {
                // ��һ�������������ģ����⴦��
                if (columnIndex == 0) {
                    return "���������...";
                } else {
                    return "";
                }
            }
            switch (columnIndex) {
            case 0:    // Ŀ��λ��
                return GameMapInfo.locationToString(ProjectData.getActiveProject(), new int[] { item.mapID, item.x, item.y }, false);
            case 1:    // ����
                return item.name;
            case 2:    // ��Ǯ�۸�
                return String.valueOf(getItemPrice(item, Shop.TYPE_MONEY));
            case 3:    // i�Ҽ۸�
                return String.valueOf(getItemPrice(item, Shop.TYPE_IMONEY) / 3600f);
            case 4:    // ��������
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
    class ItemListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            TeleportSet ts = (TeleportSet)inputElement;
            Object[] ret = new Object[ts.items.size() + 1];
            ts.items.toArray(ret);
            TeleportSet.Teleport newItem = new TeleportSet.Teleport();
            ret[ret.length - 1] = newItem;
            return ret;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private TableViewer itemListViewer;
    private Table itemList;
    private TableColumn locationColumn, nameColumn, moneyPriceColumn, imoneyPriceColumn, honorPriceColumn, conditionColumn;
    private Text textDesc;
    private Text textTitle;
    private Text textID;
    public static final String ID = "com.pip.game.editor.shop.TeleportSetEditor"; //$NON-NLS-1$

    /**
     * Create contents of the editor part
     * 
     * @param parent
     */                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
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
        final GridData gd_textDesc = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        textDesc.setLayoutData(gd_textDesc);
        textDesc.addFocusListener(AutoSelectAll.instance);
        textDesc.addModifyListener(this);

        itemListViewer = new TableViewer(container, SWT.FULL_SELECTION | SWT.BORDER);
        itemListViewer.setLabelProvider(new ItemListLabelProvider());
        itemListViewer.setContentProvider(new ItemListContentProvider());
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

        locationColumn = new TableColumn(itemList, SWT.NONE);
        locationColumn.setWidth(160);
        locationColumn.setText("Ŀ��λ��");

        nameColumn = new TableColumn(itemList, SWT.NONE);
        nameColumn.setWidth(160);
        nameColumn.setText("��ʾ����");

        moneyPriceColumn = new TableColumn(itemList, SWT.NONE);
        moneyPriceColumn.setWidth(60);
        moneyPriceColumn.setText("��Ǯ�۸�");

        imoneyPriceColumn = new TableColumn(itemList, SWT.NONE);
        imoneyPriceColumn.setWidth(60);
        imoneyPriceColumn.setText("Ԫ���۸�");
//
//        honorPriceColumn = new TableColumn(itemList, SWT.NONE);
//        honorPriceColumn.setWidth(60);
//        honorPriceColumn.setText("�����۸�");

        conditionColumn = new TableColumn(itemList, SWT.NONE);
        conditionColumn.setWidth(300);
        conditionColumn.setText("��������");
        
        itemListViewer.setColumnProperties(new String[] {
                "c0", "c1", "c2", "c3", "c4"
        });
        itemListViewer.setCellModifier(new ItemListCellModifier());
        itemListViewer.setCellEditors(new CellEditor[] {
                new LocationCellEditor(itemList),
                new TextCellEditor(itemList),
                new TextCellEditor(itemList),
                new TextCellEditor(itemList),
                new BuyRequirementsCellEditor(itemList)
        });

        // ���ó�ʼֵ
        TeleportSet dataDef = (TeleportSet) editObject;
        textID.setText(String.valueOf(dataDef.id));
        textTitle.setText(dataDef.title);
        textDesc.setText(dataDef.description);
        itemListViewer.setInput(dataDef);

        setDirty(false);
        setPartName(this.getEditorInput().getName());
        saveStateToUndoBuffer();
    }

    /**
     * ���浱ǰ�༭���ݡ�
     */
    protected void saveData() throws Exception {
        TeleportSet dataDef = (TeleportSet) editObject;
        
        // ��ȡ���룺����ID�����⡢����
        try {
            dataDef.id = Integer.parseInt(textID.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ��ID��");
        }
        dataDef.title = textTitle.getText().trim();
        dataDef.description = textDesc.getText();

        // �������Ϸ���
        DataObject dobj = ProjectData.getActiveProject().findObject(dataDef.getClass(), dataDef.id);
        if (dobj != null && dobj != getSaveTarget()) {
            throw new Exception("ID�ظ������������롣");
        }
        if (dataDef.title.length() == 0) {
            throw new Exception("��������⡣");
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
    private int getItemPrice(TeleportSet.Teleport item, int type) {
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
    private boolean setItemPrice(TeleportSet.Teleport item, int type, int value) {
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
     * �ѵ�ǰѡ�е���Ʒ����
     */
    private void onMoveUpItem() {
        int sel = itemList.getSelectionIndex();
        if (sel > 0) {
            TeleportSet shop = (TeleportSet)editObject;
            TeleportSet.Teleport item = shop.items.remove(sel);
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
        TeleportSet shop = (TeleportSet)editObject;
        if (sel != -1 && sel < shop.items.size() - 1) {
            TeleportSet.Teleport item = shop.items.remove(sel);
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
        TeleportSet shop = (TeleportSet)editObject;
        if (sel != -1 && sel < shop.items.size()) {
            shop.items.remove(sel);
            itemListViewer.refresh();
            itemList.setSelection(sel);
            setDirty(true);
        }
    }
}
