package com.pip.game.editor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.DataObject;
import com.pip.game.data.GiftGroup;
import com.pip.game.data.ProjectData;
import com.pip.game.data.Shop;
import com.pip.game.data.item.Item;
import com.pip.game.editor.property.ItemCellEditor;
import com.pip.util.AutoSelectAll;

public class GiftGroupEditor extends DefaultDataObjectEditor {
    /*
     * ��Ʒ�б�༭���ơ�
     */
    private Text textNeedVar;
    class ItemListCellModifier implements ICellModifier {
        public boolean canModify(Object element, String property) {
            Object[] info = (Object[])element;
            GiftGroup.GiftDef gd = (GiftGroup.GiftDef)info[0];
            int index = ((Integer)info[1]).intValue();
            int columnIndex = Integer.parseInt(property.substring(1));
            if (index >= gd.needItems.size() + gd.giveItems.size()) {
                // ���һ��
                return columnIndex == 0;
            } else {
                return true;
            }
        }
        
        public Object getValue(Object element, String property) {
            Object[] info = (Object[])element;
            GiftGroup.GiftDef gd = (GiftGroup.GiftDef)info[0];
            int index = ((Integer)info[1]).intValue();
            int columnIndex = Integer.parseInt(property.substring(1));
            if (index >= gd.needItems.size() + gd.giveItems.size()) {
                // ���һ��
                if (columnIndex == 0) {
                    return new Integer(2);
                }
            } else {
                Shop.BuyRequirement item = null;
                boolean isNeed = false;
                if (index < gd.needItems.size()) {
                    item = gd.needItems.get(index);
                    isNeed = true;
                } else if (index < gd.needItems.size() + gd.giveItems.size()) {
                    item = gd.giveItems.get(index - gd.needItems.size());
                    isNeed = false;
                }
                switch (columnIndex) {
                case 0:     // ����/����
                    if (isNeed) {
                        return 0;
                    } else {
                        return 1;
                    }
                case 1:     // ����
                    switch (item.type) {
                    case Shop.TYPE_MONEY:
                        return 0;
                    case Shop.TYPE_ITEM:
                        return 1;
                    case Shop.TYPE_VARIABLE:
                        return 2;
                    }
                    return "";
                case 2:     // ����
                    return String.valueOf(item.amount);
                case 3:     // ��Ʒ
                    if (item.type == Shop.TYPE_ITEM) {
                        if (item.item == null) {
                            return -1;
                        } else {
                            return item.item.id;
                        }
                    } else {
                        return -1;
                    }
                case 4:     // ������
                    if (item.type == Shop.TYPE_VARIABLE) {
                        return item.varName;
                    } else {
                        return "";
                    }
                }
            }
            return "";
        }

        public void modify(Object element, String property, Object value) {
            TableItem ti = (TableItem)element;
            Object[] info = (Object[])ti.getData();
            GiftGroup.GiftDef gd = (GiftGroup.GiftDef)info[0];
            int index = ((Integer)info[1]).intValue();
            int columnIndex = Integer.parseInt(property.substring(1));
            if (index >= gd.needItems.size() + gd.giveItems.size()) {
                // ���һ��
                if (columnIndex == 0) {
                    int action = ((Integer)value).intValue();
                    if (action != 2) {
                        // ������Ŀ
                        Shop.BuyRequirement item = new Shop.BuyRequirement();
                        item.type = Shop.TYPE_ITEM;
                        item.amount = 1;
                        if (action == 0) {
                            gd.needItems.add(item);
                        } else {
                            gd.giveItems.add(item);
                        }
                        itemTableViewer.refresh();
                        setDirty(true);
                    }
                }
            } else {
                Shop.BuyRequirement item = null;
                boolean isNeed = false;
                if (index < gd.needItems.size()) {
                    item = gd.needItems.get(index);
                    isNeed = true;
                } else if (index < gd.needItems.size() + gd.giveItems.size()) {
                    item = gd.giveItems.get(index - gd.needItems.size());
                    isNeed = false;
                }
                switch (columnIndex) {
                case 0:     // ����/����
                    int newAction = ((Integer)value).intValue();
                    if (newAction == 2) {
                        // ɾ��
                        if (isNeed) {
                            gd.needItems.remove(item);
                        } else {
                            gd.giveItems.remove(item);
                        }
                        itemTableViewer.refresh();
                        setDirty(true);
                    } else if (isNeed && newAction == 1) {
                        // �������ת�������
                        gd.needItems.remove(item);
                        gd.giveItems.add(item);
                        itemTableViewer.refresh();
                        setDirty(true);
                    } else if (!isNeed && newAction == 2) {
                        // �Ӹ����ת�������
                        gd.giveItems.remove(item);
                        gd.needItems.add(item);
                        itemTableViewer.refresh();
                        setDirty(true);
                    }
                    break;
                case 1:     // ����
                    int newIndex = ((Integer)value).intValue();
                    int newType = new int[] { Shop.TYPE_MONEY, Shop.TYPE_ITEM, Shop.TYPE_VARIABLE }[newIndex];
                    if (newType != item.type) {
                        item.type = newType;
                        itemTableViewer.update(ti.getData(), null);
                        setDirty(true);
                    }
                    break;
                case 2:     // ����
                    int newAmount = Integer.parseInt((String)value);
                    if (newAmount != item.amount) {
                        item.amount = newAmount;
                        itemTableViewer.update(ti.getData(), null);
                        setDirty(true);
                    }
                    break;
                case 3:     // ��Ʒ
                    int newItemID = ((Integer)value).intValue();
                    Item newItem = ProjectData.getActiveProject().findItemOrEquipment(newItemID);
                    if (newItem != item.item) {
                        item.item = newItem;
                        itemTableViewer.update(ti.getData(), null);
                        setDirty(true);
                    }
                    break;
                case 4:    // ������
                    String newVarName = (String)value;
                    if (!newVarName.equals(item.varName)) {
                        item.varName = newVarName;
                        itemTableViewer.update(ti.getData(), null);
                        setDirty(true);
                    }
                    break;
                case 5:    // ��������
                    String newVarDesc = (String)value;
                    if (!newVarDesc.equals(item.varDesc)) {
                        item.varDesc = newVarDesc;
                        itemTableViewer.update(ti.getData(), null);
                        setDirty(true);
                    }
                    break;
                }
            }
        }
    }

    class ItemTableLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            Object[] info = (Object[])element;
            GiftGroup.GiftDef gd = (GiftGroup.GiftDef)info[0];
            int index = ((Integer)info[1]).intValue();
            Shop.BuyRequirement item = null;
            boolean isNeed = false;
            if (index < gd.needItems.size()) {
                item = gd.needItems.get(index);
                isNeed = true;
            } else if (index < gd.needItems.size() + gd.giveItems.size()) {
                item = gd.giveItems.get(index - gd.needItems.size());
                isNeed = false;
            }
            if (item == null) {
                // ���һ��
                if (columnIndex == 0) {
                    return "�����...";
                } else {
                    return "";
                }
            }
            switch (columnIndex) {
            case 0:     // ����/����
                if (isNeed) {
                    return "����";
                } else {
                    return "����";
                }
            case 1:     // ����
                switch (item.type) {
                case Shop.TYPE_MONEY:
                    return "��Ǯ";
                case Shop.TYPE_ITEM:
                    return "��Ʒ";
                case Shop.TYPE_VARIABLE:
                    return "���Ա���";
                }
                return "";
            case 2:     // ����
                return String.valueOf(item.amount);
            case 3:     // ��Ʒ
                if (item.type == Shop.TYPE_ITEM) {
                    if (item.item == null) {
                        return "";
                    } else {
                        return item.item.toString();
                    }
                } else {
                    return "";
                }
            case 4:     // ������
                if (item.type == Shop.TYPE_VARIABLE) {
                    return item.varName;
                } else {
                    return "";
                }
            case 5:     // ��������
                if (item.type == Shop.TYPE_VARIABLE) {
                    return item.varDesc;
                } else {
                    return "";
                }
            }
            return "";
        }
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
    
    class ItemTableContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            GiftGroup.GiftDef gd = (GiftGroup.GiftDef)inputElement;
            int count = gd.giveItems.size() + gd.needItems.size() + 1;
            Object[] ret = new Object[count];
            for (int i = 0; i < count; i++) {
                ret[i] = new Object[] { gd, i };
            }
            return ret;
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    class GiftListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            GiftGroup dataDef = (GiftGroup)inputElement;
            return dataDef.gifts.toArray();
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    private Table itemTable;
    private Text textEndLevel;
    private Text textBeginLevel;
    private List giftList;
    private Text textBagFullMessage;
    private Text textGiveMessage;
    private Text textNeedMessage;
    private Text textTimeMessage;
    private Text textTimeSpaceMessage;
    private Text textRepeatMessage;
    private Text textMaxMessage;
    private Text textGiftDesc;
    private Text textGroupTitle;
    private Text textErrorMessage;
    private Text textTimeSpace;
    private Text textRepeatTimes;
    private Combo comboCycleType;
    private Text textEndDate;
    private Text textBeginDate;
    private Text textBeginDay;
    private Text textEndDay;
    private Text textBeginTime;
    private Text textEndTime;
    private Text textCycleAmount;
    private Text textMaxTimes;
    private Text textTitle;
    private Text textID;
    
    public static final String ID = "com.pip.game.editor.GiftGroupEditor"; //$NON-NLS-1$
    private Button buttonValid;
    private ListViewer giftListViewer;
    private TableViewer itemTableViewer;
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * Create contents of the editor part
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 9;
        container.setLayout(gridLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("ID��");

        textID = new Text(container, SWT.BORDER);
        final GridData gd_textID = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textID.setLayoutData(gd_textID);
        textID.addFocusListener(AutoSelectAll.instance);
        textID.addModifyListener(this);

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setLayoutData(new GridData());
        label_1.setText("���ƣ�");

        textTitle = new Text(container, SWT.BORDER);
        final GridData gd_textTitle = new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1);
        textTitle.setLayoutData(gd_textTitle);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);

        buttonValid = new Button(container, SWT.CHECK);
        buttonValid.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(final SelectionEvent e) {
            }
            public void widgetSelected(final SelectionEvent e) {
                setDirty(true);
            }
        });
        final GridData gd_buttonValid = new GridData();
        buttonValid.setLayoutData(gd_buttonValid);
        buttonValid.setText("�Ƿ���Ч");

        final Label label_3 = new Label(container, SWT.NONE);
        label_3.setLayoutData(new GridData());
        label_3.setText("��ʼ���ڣ�");

        textBeginDate = new Text(container, SWT.BORDER);
        final GridData gd_textBeginDate = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textBeginDate.setLayoutData(gd_textBeginDate);
        textBeginDate.addFocusListener(AutoSelectAll.instance);
        textBeginDate.addModifyListener(this);

        final Label label_7 = new Label(container, SWT.NONE);
        label_7.setLayoutData(new GridData());
        label_7.setText("�������ڣ�");

        textEndDate = new Text(container, SWT.BORDER);
        final GridData gd_textEndDate = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textEndDate.setLayoutData(gd_textEndDate);
        textEndDate.addFocusListener(AutoSelectAll.instance);
        textEndDate.addModifyListener(this);

        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
        label_2.setText("ע�����ڸ�ʽYYYYMMDD���ձ�ʾ������");

        final Label label_13 = new Label(container, SWT.NONE);
        label_13.setText("�ۼ���ȡ�������ޣ�");

        textMaxTimes = new Text(container, SWT.BORDER);
        final GridData gd_textMaxTimes = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textMaxTimes.setLayoutData(gd_textMaxTimes);
        textMaxTimes.addFocusListener(AutoSelectAll.instance);
        textMaxTimes.addModifyListener(this);

        final Label label_14 = new Label(container, SWT.NONE);
        label_14.setText("��ȡ���ڣ�");

        textCycleAmount = new Text(container, SWT.BORDER);
        final GridData gd_textCycleAmount = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textCycleAmount.setLayoutData(gd_textCycleAmount);
        textCycleAmount.addFocusListener(AutoSelectAll.instance);
        textCycleAmount.addModifyListener(this);

        comboCycleType = new Combo(container, SWT.READ_ONLY);
        comboCycleType.setItems(new String[] {"Сʱ", "��", "��", "��"});
        final GridData gd_comboCycleType = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboCycleType.setLayoutData(gd_comboCycleType);
        comboCycleType.addModifyListener(this);

        final Label label_5 = new Label(container, SWT.NONE);
        label_5.setText("������ȡ�������ޣ�");

        textRepeatTimes = new Text(container, SWT.BORDER);
        final GridData gd_textRepeatTimes = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textRepeatTimes.setLayoutData(gd_textRepeatTimes);
        textRepeatTimes.addFocusListener(AutoSelectAll.instance);
        textRepeatTimes.addModifyListener(this);

        final Label label_4 = new Label(container, SWT.NONE);
        label_4.setText("��ȡ���(��)��");

        textTimeSpace = new Text(container, SWT.BORDER);
        final GridData gd_textTimeSpace = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textTimeSpace.setLayoutData(gd_textTimeSpace);
        textTimeSpace.addFocusListener(AutoSelectAll.instance);
        textTimeSpace.addModifyListener(this);

        final Label label_6 = new Label(container, SWT.NONE);
        label_6.setText("������ʼ�գ�");

        textBeginDay = new Text(container, SWT.BORDER);
        final GridData gd_textBeginDay = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textBeginDay.setLayoutData(gd_textBeginDay);
        textBeginDay.addFocusListener(AutoSelectAll.instance);
        textBeginDay.addModifyListener(this);

        final Label label_71 = new Label(container, SWT.NONE);
        label_71.setText("���ڽ�����(��)��");

        textEndDay = new Text(container, SWT.BORDER);
        textEndDay.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        textEndDay.addFocusListener(AutoSelectAll.instance);
        textEndDay.addModifyListener(this);

        final Label label_72 = new Label(container, SWT.NONE);
        label_72.setText("��ʼʱ�䣺");

        textBeginTime = new Text(container, SWT.BORDER);
        textBeginTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textBeginTime.addFocusListener(AutoSelectAll.instance);
        textBeginTime.addModifyListener(this);

        final Label label_73 = new Label(container, SWT.NONE);
        label_73.setText("����ʱ�䣺");

        textEndTime = new Text(container, SWT.BORDER);
        textEndTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textEndTime.addFocusListener(AutoSelectAll.instance);
        textEndTime.addModifyListener(this);

        final Label label_11 = new Label(container, SWT.NONE);
        label_11.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 9, 1));
        label_11.setText("��ȡ�������ޡ���ȡ�����������ʼ�ա����ڽ����տ���-1��ʾ�����ơ���ʼʱ��ͽ���ʱ��ĸ�ʽΪHHMM���ձ�ʾ�����ơ�");

        final Label label_15 = new Label(container, SWT.NONE);
        label_15.setText("��Ʒ����⣺");

        textGroupTitle = new Text(container, SWT.BORDER);
        final GridData gd_textGroupTitle = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
        textGroupTitle.setLayoutData(gd_textGroupTitle);
        textGroupTitle.addFocusListener(AutoSelectAll.instance);
        textGroupTitle.addModifyListener(this);

        final Label label_16 = new Label(container, SWT.NONE);
        label_16.setText("��Ʒ������");

        textGiftDesc = new Text(container, SWT.BORDER);
        final GridData gd_textGiftDesc = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
        textGiftDesc.setLayoutData(gd_textGiftDesc);
        textGiftDesc.addFocusListener(AutoSelectAll.instance);
        textGiftDesc.addModifyListener(this);

        final Label label_12 = new Label(container, SWT.NONE);
        label_12.setText("������Ϣ��");

        textErrorMessage = new Text(container, SWT.BORDER);
        final GridData gd_textErrorMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
        textErrorMessage.setLayoutData(gd_textErrorMessage);
        textErrorMessage.addFocusListener(AutoSelectAll.instance);
        textErrorMessage.addModifyListener(this);

        final Label label_17 = new Label(container, SWT.NONE);
        label_17.setText("�ﵽ�ܴ������ޣ�");

        textMaxMessage = new Text(container, SWT.BORDER);
        final GridData gd_textMaxMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
        textMaxMessage.setLayoutData(gd_textMaxMessage);
        textMaxMessage.addFocusListener(AutoSelectAll.instance);
        textMaxMessage.addModifyListener(this);

        final Label label_18 = new Label(container, SWT.NONE);
        label_18.setText("�ﵽ���ڴ������ޣ�");

        textRepeatMessage = new Text(container, SWT.BORDER);
        final GridData gd_textRepeatMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
        textRepeatMessage.setLayoutData(gd_textRepeatMessage);
        textRepeatMessage.addFocusListener(AutoSelectAll.instance);
        textRepeatMessage.addModifyListener(this);

        final Label label_181 = new Label(container, SWT.NONE);
        label_181.setText("û����С���ʱ�䣺");

        textTimeSpaceMessage = new Text(container, SWT.BORDER);
        final GridData gd_textTimeSpaceMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
        textTimeSpaceMessage.setLayoutData(gd_textTimeSpaceMessage);
        textTimeSpaceMessage.addFocusListener(AutoSelectAll.instance);
        textTimeSpaceMessage.addModifyListener(this);

        final Label label_19 = new Label(container, SWT.NONE);
        label_19.setText("û����ȡʱ�䣺");

        textTimeMessage = new Text(container, SWT.BORDER);
        final GridData gd_textTimeMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
        textTimeMessage.setLayoutData(gd_textTimeMessage);
        textTimeMessage.addFocusListener(AutoSelectAll.instance);
        textTimeMessage.addModifyListener(this);

        final Label label_20 = new Label(container, SWT.NONE);
        label_20.setText("������Ʒ���㣺");

        textNeedMessage = new Text(container, SWT.BORDER);
        final GridData gd_textNeedMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
        textNeedMessage.setLayoutData(gd_textNeedMessage);
        textNeedMessage.addFocusListener(AutoSelectAll.instance);
        textNeedMessage.addModifyListener(this);

        final Label label_8 = new Label(container, SWT.NONE);
        label_8.setText("������������㣺");

        textNeedVar = new Text(container, SWT.BORDER);
        final GridData gd_textNeedVar = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
        textNeedVar.setLayoutData(gd_textNeedVar);
        textNeedVar.addFocusListener(AutoSelectAll.instance);
        textNeedVar.addModifyListener(this);

        final Label label_21 = new Label(container, SWT.NONE);
        label_21.setText("�����ɹ���");

        textGiveMessage = new Text(container, SWT.BORDER);
        final GridData gd_textGiveMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
        textGiveMessage.setLayoutData(gd_textGiveMessage);
        textGiveMessage.addFocusListener(AutoSelectAll.instance);
        textGiveMessage.addModifyListener(this);

        final Label label_22 = new Label(container, SWT.NONE);
        label_22.setText("��������");

        textBagFullMessage = new Text(container, SWT.BORDER);
        final GridData gd_textBagFullMessage = new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1);
        textBagFullMessage.setLayoutData(gd_textBagFullMessage);
        textBagFullMessage.addFocusListener(AutoSelectAll.instance);
        textBagFullMessage.addModifyListener(this);

        final Label label_23 = new Label(container, SWT.WRAP);
        label_23.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 9, 1));
        label_23.setText("������������ֿɴ������������ĸ�ʽΪ${������}�����õı���������\nbeginlevel��С����endlevel��󼶱�needitem������Ʒ��giveitem�һ���Ʒ��acount����ȡ������\nrcount��������ȡ������max�����ȡ������repeat���������ȡ������needtime��ȡʱ�䷶Χ��timespace��С��ȡ�����");

        giftListViewer = new ListViewer(container, SWT.BORDER);
        giftListViewer.setContentProvider(new GiftListContentProvider());
        giftListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                StructuredSelection sel = (StructuredSelection)giftListViewer.getSelection();
                if (sel.isEmpty()) {
                    itemTable.setVisible(false);
                    return;
                }
                GiftGroup.GiftDef gd = (GiftGroup.GiftDef)sel.getFirstElement();
                textBeginLevel.setText(String.valueOf(gd.beginLevel));
                textEndLevel.setText(String.valueOf(gd.endLevel));
                itemTable.setVisible(true);
                itemTableViewer.setInput(gd);
            }
        });
        giftList = giftListViewer.getList();
        final GridData gd_giftList = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        giftList.setLayoutData(gd_giftList);

        itemTableViewer = new TableViewer(container, SWT.FULL_SELECTION | SWT.BORDER);
        itemTableViewer.setLabelProvider(new ItemTableLabelProvider());
        itemTableViewer.setContentProvider(new ItemTableContentProvider());
        itemTable = itemTableViewer.getTable();
        itemTable.setVisible(false);
        itemTable.setLinesVisible(true);
        itemTable.setHeaderVisible(true);
        final GridData gd_itemTable = new GridData(SWT.FILL, SWT.FILL, true, true, 6, 2);
        itemTable.setLayoutData(gd_itemTable);

        final TableColumn giveColumn = new TableColumn(itemTable, SWT.NONE);
        giveColumn.setWidth(100);
        giveColumn.setText("����/����");

        final TableColumn typeColumn = new TableColumn(itemTable, SWT.NONE);
        typeColumn.setWidth(100);
        typeColumn.setText("����");

        final TableColumn amountColumn = new TableColumn(itemTable, SWT.NONE);
        amountColumn.setWidth(100);
        amountColumn.setText("����");

        final TableColumn itemColumn = new TableColumn(itemTable, SWT.NONE);
        itemColumn.setWidth(100);
        itemColumn.setText("��Ʒ");
        
        final TableColumn varNameColumn = new TableColumn(itemTable, SWT.NONE);
        varNameColumn.setWidth(100);
        varNameColumn.setText("����");

        final TableColumn varDescColumn = new TableColumn(itemTable, SWT.NONE);
        varDescColumn.setWidth(150);
        varDescColumn.setText("��������");
        
        itemTableViewer.setColumnProperties(new String[] {
                "c0", "c1", "c2", "c3", "c4", "c5"
        });
        itemTableViewer.setCellModifier(new ItemListCellModifier());
        itemTableViewer.setCellEditors(new CellEditor[] {
                new ComboBoxCellEditor(itemTable, new String[] {
                    "����", "����", "ɾ��"
                }) {
                    public int getStyle() {
                        return SWT.READ_ONLY;
                    }
                },
                new ComboBoxCellEditor(itemTable, new String[] {
                    "��Ǯ", "��Ʒ", "���Ա���"
                }) {
                    public int getStyle() {
                        return SWT.READ_ONLY;
                    }
                },
                new TextCellEditor(itemTable),
                new ItemCellEditor(itemTable),
                new TextCellEditor(itemTable),
                new TextCellEditor(itemTable)
        });

        final Composite composite = new Composite(container, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 7;
        composite.setLayout(gridLayout_1);

        textBeginLevel = new Text(composite, SWT.BORDER);
        final GridData gd_textBeginLevel = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textBeginLevel.setLayoutData(gd_textBeginLevel);
        textBeginLevel.addFocusListener(AutoSelectAll.instance);

        final Label label_24 = new Label(composite, SWT.NONE);
        label_24.setText("-");

        textEndLevel = new Text(composite, SWT.BORDER);
        final GridData gd_textEndLevel = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textEndLevel.setLayoutData(gd_textEndLevel);
        textEndLevel.addFocusListener(AutoSelectAll.instance);

        final Label label_25 = new Label(composite, SWT.NONE);
        label_25.setText("��");

        final Button buttonAdd = new Button(composite, SWT.NONE);
        buttonAdd.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                int beginLevel = 0, endLevel = 0;
                try {
                    beginLevel = Integer.parseInt(textBeginLevel.getText());
                    endLevel = Integer.parseInt(textEndLevel.getText());
                    if (beginLevel <= 0 || beginLevel > 200 || endLevel <= 0 || endLevel > 200) {
                        throw new Exception();
                    }
                } catch (Exception e1) {
                    MessageDialog.openError(getSite().getShell(), "����", "�����������");
                    return;
                }
                GiftGroup dataDef = (GiftGroup)editObject;
                GiftGroup.GiftDef gd = new GiftGroup.GiftDef();
                while (true) {
                    gd.id++;
                    boolean found = false;
                    for (GiftGroup.GiftDef gd1 : dataDef.gifts) {
                        if (gd1.id == gd.id) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        break;
                    }
                }
                gd.beginLevel = beginLevel;
                gd.endLevel = endLevel;
                dataDef.gifts.add(gd);
                setDirty(true);
                giftListViewer.refresh();
            }
        });
        buttonAdd.setText("���");

        final Button buttonUpdate = new Button(composite, SWT.NONE);
        buttonUpdate.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                int beginLevel = 0, endLevel = 0;
                try {
                    beginLevel = Integer.parseInt(textBeginLevel.getText());
                    endLevel = Integer.parseInt(textEndLevel.getText());
                    if (beginLevel <= 0 || beginLevel > 200 || endLevel <= 0 || endLevel > 200) {
                        throw new Exception();
                    }
                } catch (Exception e1) {
                    MessageDialog.openError(getSite().getShell(), "����", "�����������");
                    return;
                }
                StructuredSelection sel = (StructuredSelection)giftListViewer.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                GiftGroup.GiftDef gd1 = (GiftGroup.GiftDef)sel.getFirstElement();
                gd1.beginLevel = beginLevel;
                gd1.endLevel = endLevel;
                setDirty(true);
                giftListViewer.refresh();
            }
        });
        buttonUpdate.setText("����");

        final Button buttonDelete = new Button(composite, SWT.NONE);
        buttonDelete.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                GiftGroup dataDef = (GiftGroup)editObject;
                StructuredSelection sel = (StructuredSelection)giftListViewer.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                GiftGroup.GiftDef gd1 = (GiftGroup.GiftDef)sel.getFirstElement();
                dataDef.gifts.remove(gd1);
                setDirty(true);
                giftListViewer.refresh();
            }
        });
        buttonDelete.setText("ɾ��");
        
        // ���ó�ʼֵ
        updateView();
        
        setDirty(false);
        setPartName(this.getEditorInput().getName());
        saveStateToUndoBuffer();
    }
    
    private void updateView() {
        // ���ó�ʼֵ
        GiftGroup dataDef = (GiftGroup)editObject;
        textID.setText(String.valueOf(dataDef.id));
        textTitle.setText(dataDef.title);
        buttonValid.setSelection(dataDef.valid);
        if (dataDef.beginDate == null) {
            textBeginDate.setText("");
        } else {
            textBeginDate.setText(dateFormat.format(dataDef.beginDate));
        }
        if (dataDef.endDate == null) {
            textEndDate.setText("");
        } else {
            textEndDate.setText(dateFormat.format(dataDef.endDate));
        }
        textMaxTimes.setText(String.valueOf(dataDef.maxTimes));
        textCycleAmount.setText(String.valueOf(dataDef.cycle.amount));
        comboCycleType.select(dataDef.cycle.type);
        textRepeatTimes.setText(String.valueOf(dataDef.repeatTimes));
        textTimeSpace.setText(String.valueOf(dataDef.timeSpace));
        textBeginDay.setText(String.valueOf(dataDef.beginDay));
        textEndDay.setText(String.valueOf(dataDef.endDay));
        textBeginDay.setEnabled(dataDef.supportDaySetting());
        textEndDay.setEnabled(dataDef.supportDaySetting());
        textBeginTime.setText(minuteToStr(dataDef.beginTime));
        textEndTime.setText(minuteToStr(dataDef.endTime));
        
        textGroupTitle.setText(dataDef.groupMessage);
        textGiftDesc.setText(dataDef.giftMessage);
        textErrorMessage.setText(dataDef.errorMessage);
        textMaxMessage.setText(dataDef.maxExceedMessage);
        textRepeatMessage.setText(dataDef.repeatExceedMessage);
        textTimeSpaceMessage.setText(dataDef.timeSpaceMessage);
        textTimeMessage.setText(dataDef.timeErrorMessage);
        textNeedMessage.setText(dataDef.needItemMessage);
        textNeedVar.setText(dataDef.needVarMessage);
        textGiveMessage.setText(dataDef.giveOKMessage);
        textBagFullMessage.setText(dataDef.bagFullMessage);
        
        giftListViewer.setInput(dataDef);
    }
    
    /**
     * ���浱ǰ�༭���ݡ�
     */
    protected void saveData() throws Exception {
        GiftGroup dataDef = (GiftGroup)editObject;

        // ��ȡ����
        try {
            dataDef.id = Integer.parseInt(textID.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ��ID��");
        }
        dataDef.title = textTitle.getText().trim();
        dataDef.valid = buttonValid.getSelection();
        try {
            dataDef.beginDate = strToDate(textBeginDate.getText());
            if (dataDef.beginDate == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception("��������ȷ����ʼ���ڣ�����Ϊ�գ���");
        }
        try {
            dataDef.endDate = strToDate(textEndDate.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ�Ľ������ڡ�");
        }
        try {
            dataDef.maxTimes = Integer.parseInt(textMaxTimes.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ�������ȡ������");
        }
        try {
            dataDef.cycle.type = comboCycleType.getSelectionIndex();
            dataDef.cycle.amount = Integer.parseInt(textCycleAmount.getText());
            if (dataDef.cycle.amount <= 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception("��������ȷ�����ڡ�");
        }
        try {
            dataDef.repeatTimes = Integer.parseInt(textRepeatTimes.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ��������ȡ������");
        }
        try {
            dataDef.timeSpace = Integer.parseInt(textTimeSpace.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ��������ȡ�����");
        }
        
        if (dataDef.supportDaySetting()) {
            try {
                dataDef.beginDay = Integer.parseInt(textBeginDay.getText());
                dataDef.endDay = Integer.parseInt(textEndDay.getText());
            } catch (Exception e) {
                throw new Exception("��������ȷ���������ơ�");
            }
        } else {
            dataDef.beginDay = -1;
            dataDef.endDay = -1;
        }
        try {
            dataDef.beginTime = strToMinute(textBeginTime.getText());
            dataDef.endTime = strToMinute(textEndTime.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ��ʱ�����ơ�");
        }
        
        dataDef.groupMessage = textGroupTitle.getText();
        dataDef.giftMessage = textGiftDesc.getText();
        dataDef.errorMessage = textErrorMessage.getText();
        dataDef.maxExceedMessage = textMaxMessage.getText();
        dataDef.repeatExceedMessage = textRepeatMessage.getText();
        dataDef.timeSpaceMessage = textTimeSpaceMessage.getText();
        dataDef.timeErrorMessage = textTimeMessage.getText();
        dataDef.needItemMessage = textNeedMessage.getText();
        dataDef.needVarMessage = textNeedVar.getText();
        dataDef.giveOKMessage = textGiveMessage.getText();
        dataDef.bagFullMessage = textBagFullMessage.getText();
        
        // �������Ϸ���
        DataObject dobj = ProjectData.getActiveProject().findObject(dataDef.getClass(), dataDef.id);
        if (dobj != null && dobj != getSaveTarget()) {
            throw new Exception("ID�ظ������������롣");
        }
        if (dataDef.title.length() == 0) {
            throw new Exception("��������⡣");
        }
    }
    
    public void modifyText(final ModifyEvent e) {
        super.modifyText(e);
        if (e.getSource() == textCycleAmount || e.getSource() == comboCycleType) {
            int t = comboCycleType.getSelectionIndex();
            int a = 0;
            try {
                a = Integer.parseInt(textCycleAmount.getText());
            } catch (Exception e1) {
            }
            textBeginDay.setEnabled(GiftGroup.supportDaySetting(t, a));
            textEndDay.setEnabled(GiftGroup.supportDaySetting(t, a));
        }
    }
    
    private String minuteToStr(int min) {
        if (min == -1) {
            return "";
        }
        int h = min / 60;
        int m = min % 60;
        return h + ":" + m;
    }
    
    private int strToMinute(String str) throws Exception {
        if (str.length() == 0) {
            return -1;
        }
        String[] sec = str.split(":");
        if (sec.length != 2) {
            throw new Exception();
        }
        int h = Integer.parseInt(sec[0]);
        int m = Integer.parseInt(sec[1]);
        if (h < 0 || h > 24) {
            throw new Exception();
        }
        if (m < 0 || m > 59) {
            throw new Exception();
        }
        if (h == 24 && m != 0) {
            throw new Exception();
        }
        return h * 60 + m;
    }
    
    private Date strToDate(String str) throws Exception {
        if (str.length() == 0) {
            return null;
        }
        return dateFormat.parse(str);
    }
}
