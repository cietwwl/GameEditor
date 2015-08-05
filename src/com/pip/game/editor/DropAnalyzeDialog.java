package com.pip.game.editor;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.pip.game.data.DataObject;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.item.DropGroup;
import com.pip.game.data.item.DropItem;
import com.pip.game.data.item.DropNode;
import com.pip.game.data.item.Item;
import com.pip.game.data.item.SubDropGroup;
import com.pip.game.data.quest.Quest;
import com.swtdesigner.SWTResourceManager;

public class DropAnalyzeDialog extends Dialog {
    private static final DecimalFormat percentFormat = new DecimalFormat("####.###"); 
    
    class TreeLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof String) {
                if (columnIndex == 0) {
                    return (String)element;
                } else if (columnIndex == 1) {
                    if ("���Դ���".equals(element)) {
                        return String.valueOf(totalCount);
                    } else if ("��Ǯ".equals(element)) {
                        return String.valueOf((double)dropMoney / totalCount);
                    } else if ("����".equals(element)) {
                        return String.valueOf((double)dropExp / totalCount);
                    } else if ("��Ʒ".equals(element)) {
                        double t = (double)getTotal(normalItems) / totalCount * 100;
                        return percentFormat.format(t) + "%";
                    } else if ("��װ".equals(element)) {
                        double t = (double)getTotal(whiteEqus) / totalCount * 100;
                        return percentFormat.format(t) + "%";
                    } else if ("��װ".equals(element)) {
                        double t = (double)getTotal(greenEqus) / totalCount * 100;
                        return percentFormat.format(t) + "%";
                    } else if ("��װ".equals(element)) {
                        double t = (double)getTotal(blueEqus) / totalCount * 100;
                        return percentFormat.format(t) + "%";
                    } else if ("��װ".equals(element)) {
                        double t = (double)getTotal(purpleEqus) / totalCount * 100;
                        return percentFormat.format(t) + "%";
                    }
                    return "";
                } else if (columnIndex == 2) {
                    if ("���Դ���".equals(element)) {
                        return "";
                    } else if ("��Ǯ".equals(element)) {
                        return String.valueOf((double)dropMoney / totalCount);
                    } else if ("����".equals(element)) {
                        return "";
                    } else if ("��Ʒ".equals(element)) {
                        double t = (double)getTotalPrice(normalItems) / totalCount;
                        return percentFormat.format(t);
                    } else if ("��װ".equals(element)) {
                        double t = (double)getTotalPrice(whiteEqus) / totalCount;
                        return percentFormat.format(t);
                    } else if ("��װ".equals(element)) {
                        double t = (double)getTotalPrice(greenEqus) / totalCount;
                        return percentFormat.format(t);
                    } else if ("��װ".equals(element)) {
                        double t = (double)getTotalPrice(blueEqus) / totalCount;
                        return percentFormat.format(t);
                    } else if ("��װ".equals(element)) {
                        double t = (double)getTotalPrice(purpleEqus) / totalCount;
                        return percentFormat.format(t);
                    }
                    return "";
                } else {
                    return "";
                }
            } else if (element instanceof DropResultItem) {
                DropResultItem item = (DropResultItem)element;
                if (columnIndex == 0) {
                    return ((DropResultItem)element).item.title;
                } else if (columnIndex == 1) {
                    double t = (double)((DropResultItem)element).dropTime / totalCount * 100;
                    return percentFormat.format(t) + "%";
                } else if (columnIndex == 2) {
                    if (item.item.sale) {
                        return String.valueOf(item.item.price);
                    } else {
                        return "";
                    }
                } else if (columnIndex == 3) {
                    return ((DropResultItem)element).from;
                }
            }
            return "";
        }
        
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
    
    class TreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
        public void dispose() {
        }
        public Object[] getElements(Object inputElement) {
            return new String[] { "���Դ���", "��Ǯ", "����", "��Ʒ", "��װ", "��װ", "��װ", "��װ" };
        }
        public Object[] getChildren(Object parentElement) {
            if ("��Ʒ".equals(parentElement)) {
                return normalItems.toArray();
            } else if ("��װ".equals(parentElement)) {
                return whiteEqus.toArray();
            } else if ("��װ".equals(parentElement)) {
                return greenEqus.toArray();
            } else if ("��װ".equals(parentElement)) {
                return blueEqus.toArray();
            } else if ("��װ".equals(parentElement)) {
                return purpleEqus.toArray();
            } else {
                return new Object[0];
            }
        }
        public Object getParent(Object element) {
            return null;
        }
        public boolean hasChildren(Object element) {
            if ("��Ʒ".equals(element)) {
                return true;
            } else if ("��װ".equals(element)) {
                return true;
            } else if ("��װ".equals(element)) {
                return true;
            } else if ("��װ".equals(element)) {
                return true;
            } else if ("��װ".equals(element)) {
                return true;
            } else {
                return false;
            }
        }
    }

    class ItemTreeViewer extends TreeViewer {
        public ItemTreeViewer(Composite parent, int style) {
            super(parent, style);
            getTree().setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
            getTree().setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        }
        
        protected ViewerRow getViewerRowFromItem(Widget item) {
            ViewerRow ret = super.getViewerRowFromItem(item);
            TreeItem ti = (TreeItem)item;
            Object obj = ti.getData();
            if (obj instanceof DropResultItem) {
                int clr = ProjectData.getActiveProject().config.QUALITY_COLOR[((DropResultItem)obj).item.quality];
                ti.setForeground(SWTResourceManager.getColor(clr >> 16, (clr >> 8) & 0xFF, clr & 0xFF));
            }
            return ret;
        }
    }
    
    private Tree dropTree;
    public String prefix;
    public String packageName;
    public String folder;
    private TreeViewer dropListViewer;
    
    private NPCTemplate npc;
    private boolean taskItem;
    private int playerLevel;
    private int playerClazz;
    
    static class DropResultItem {
        int itemID;     // ��ƷID
        int dropTime;   // �������
        String from;    // ԭ��
        Item item;      // ��ʱ����
        
        public boolean equals(Object o) {
            if (o == null || !(o instanceof DropResultItem)) {
                return false;
            }
            DropResultItem oo = (DropResultItem)o;
            return itemID == oo.itemID && from.equals(oo.from);
        }
        
        public int hashCode() {
            return itemID ^ from.hashCode();
        }
    }
    
    private int totalCount;
    private long dropMoney;
    private long dropExp;
    private List<DropResultItem> dropItems;
    private HashMap<DropResultItem, DropResultItem> dropItemMap;
    private static Random rand = new Random();
    
    // ���η������
    private List<DropResultItem> normalItems;
    private List<DropResultItem> whiteEqus;
    private List<DropResultItem> greenEqus;
    private List<DropResultItem> blueEqus;
    private List<DropResultItem> purpleEqus;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public DropAnalyzeDialog(Shell parentShell, NPCTemplate template, boolean t, int lvl, int clz) {
        super(parentShell);
        npc = template;
        taskItem = t;
        this.playerLevel = lvl;
        this.playerClazz = clz;
    }
    
    private int getTotal(List<DropResultItem> list) {
        int total = 0;
        for (DropResultItem dri : list) {
            total += dri.dropTime;
        }
        return total;
    }
    
    private int getTotalPrice(List<DropResultItem> list) {
        int total = 0;
        for (DropResultItem dri : list) {
            if (dri.item.sale) {
                total += dri.dropTime * dri.item.price;
            }
        }
        return total;
    }
    
    private void sort(List<DropResultItem> list) {
        int size = list.size();
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                DropResultItem item1 = list.get(i);
                DropResultItem item2 = list.get(j);
                if (item1.dropTime < item2.dropTime) {
                    list.set(i, item2);
                    list.set(j, item1);
                }
            }
        }
    }
    
    private void analyze2() {
        normalItems = new ArrayList<DropResultItem>();
        whiteEqus = new ArrayList<DropResultItem>();
        greenEqus = new ArrayList<DropResultItem>();
        blueEqus = new ArrayList<DropResultItem>();
        purpleEqus = new ArrayList<DropResultItem>();
        for (DropResultItem dri : dropItems) {
            Item item = ProjectData.getActiveProject().findItemOrEquipment(dri.itemID);
            if (item == null) {
                MessageDialog.openError(getShell(), "����", "��Ʒδ�ҵ���" + dri.itemID);
                continue;
            }
            dri.item = item;
            if (item instanceof Equipment) {
                if (item.quality == Item.QUALITY_WHITE) {
                    whiteEqus.add(dri);
                } else if (item.quality == Item.QUALITY_GREEN) {
                    greenEqus.add(dri);
                } else if (item.quality == Item.QUALITY_BLUE) {
                    blueEqus.add(dri);
                } else {
                    purpleEqus.add(dri);
                }
            } else {
                normalItems.add(dri);
            }
        }
        sort(normalItems);
        sort(whiteEqus);
        sort(greenEqus);
        sort(blueEqus);
        sort(purpleEqus);
    }
    
    private void analyze() {
        totalCount = 0;
        dropMoney = 0;
        dropExp = 0;
        dropItems = new ArrayList<DropResultItem>();
        dropItemMap = new HashMap<DropResultItem, DropResultItem>();
        go();
    }
    
    private void go() {
        List<DataObject> allGroup = ProjectData.getActiveProject().getDataListByType(DropGroup.class);
        List<DropGroup> worldDropGroup = new ArrayList<DropGroup>();
        if (!taskItem) {
            for (DataObject dobj : allGroup) {
                DropGroup dg = (DropGroup)dobj;
                if (dg.groupType == DropGroup.GROUP_TYPE_WORLD) {
                    worldDropGroup.add(dg);
                }
            }
        }
        for (int i = 0; i < 10000; i++) {
            dropMoney += npc.getMoney();
            dropExp += npc.getExp();
            for (DropNode node : npc.dropGroups) {
                if (rand.nextInt(1000000) < node.dropRate) {
                    int count = node.quantityMin;
                    if (node.quantityMax > node.quantityMin) {
                        count += rand.nextInt(node.quantityMax - node.quantityMin + 1);
                    }
                    for (int j = 0; j < count; j++) {
                        hitDropNode(node);
                    }
                }
            }
            
            // �������������
            for (DropGroup dg : worldDropGroup) {
                if (npc.level >= dg.minMonsterLevel && npc.level <= dg.maxMonsterLevel &&
                        rand.nextInt(1000000) < dg.dropRate * 100) {
                    hitDropGroup(dg, playerClazz, playerLevel, null);
                }
            }
        }
        totalCount += 10000;
    }
    
    private void hitDropNode(DropNode node) {
        if (taskItem && !node.isTask) {
            return;
        }
        if (!taskItem && node.isTask) {
            return;
        }
        if (node.type == DropItem.DROP_TYPE_ITEM || node.type == DropItem.DROP_TYPE_EQUI) {
            DropResultItem dri = new DropResultItem();
            dri.itemID = node.id;
            dri.dropTime = 1;
            if (node.isTask) {
                Quest quest = (Quest)ProjectData.getActiveProject().findObject(Quest.class, node.taskId);
                if (quest == null) {
                    dri.from = "��Ч����";
                } else {
                    dri.from = "����" + quest.toString();
                }
            } else {
                dri.from = "";
            }
            addDropResultItem(dri);
        } else if (node.type == DropItem.DROP_TYPE_DROPGROUP) {
            DropGroup dg = (DropGroup)ProjectData.getActiveProject().findObject(DropGroup.class, node.id);
            if (dg == null) {
                MessageDialog.openError(getShell(), "����", "�����鲻���ڣ�" + node.id);
                return;
            }
            String taskInfo = null;
            if (node.isTask) {
                Quest quest = (Quest)ProjectData.getActiveProject().findObject(Quest.class, node.taskId);
                if (quest == null) {
                    taskInfo = "��Ч����";
                } else {
                    taskInfo = quest.toString();
                }
            }
            hitDropGroup(dg, playerClazz, playerLevel, taskInfo);
        }
    }
    
    private void hitDropGroup(DropGroup group, int clazz, int level, String taskInfo) {
        SubDropGroup fitGroup = null;
        for (SubDropGroup sdg : group.subGroup) {
            if (sdg.levelMin <= level && sdg.levelMax >= level) {
                if (sdg.job == -1 || sdg.job == clazz) {
                    fitGroup = sdg;
                    break;
                }
            }
        }
        if (fitGroup == null) {
            return;
        }
        int count = group.quantityMin;
        if (group.quantityMax > group.quantityMin) {
            count += rand.nextInt(group.quantityMax - group.quantityMin + 1);
        }
        for (int i = 0; i < count; i++) {
            hitSubDropGroup(group, fitGroup, clazz, level, taskInfo);
        }
    }
    
    private void hitSubDropGroup(DropGroup parent, SubDropGroup group, int clazz, int level, String taskInfo) {
        int totalWeight = 0;
        for (DropItem item : group.dropGroup) {
            totalWeight += item.dropWeight;
        }
        if (totalWeight == 0) {
            throw new IllegalArgumentException("������" + parent + "Ϊ�գ�");
        }
        int dropWeight = rand.nextInt(totalWeight);
        for (DropItem item : group.dropGroup) {
            dropWeight -= item.dropWeight;
            if (dropWeight < 0) {
                int count = item.quantityMin;
                if (item.quantityMax > item.quantityMin) {
                    count += rand.nextInt(item.quantityMax - item.quantityMin + 1);
                }
                switch (item.dropType) {
                case DropItem.DROP_TYPE_ITEM:
                case DropItem.DROP_TYPE_EQUI:
                    DropResultItem dri = new DropResultItem();
                    dri.itemID = item.dropID;
                    dri.dropTime = count;
                    if (taskInfo != null) {
                        dri.from = taskInfo;
                    } else {
                        dri.from = "�����飺" + parent.toString();
                    }
                    addDropResultItem(dri);
                    break;
                case DropItem.DROP_TYPE_DROPGROUP:
                    DropGroup nextGroup = (DropGroup)ProjectData.getActiveProject().findObject(DropGroup.class, item.dropID);
                    for (int i = 0; i < count; i++) {
                        hitDropGroup(nextGroup, clazz, level, taskInfo);
                    }
                    break;
                case DropItem.DROP_TYPE_MONEY:
                    dropMoney += count;
                    break;
                case DropItem.DROP_TYPE_EXP:
                    dropExp += count;
                    break;
                }
                break;
            }
        }
    }
    
    private void addDropResultItem(DropResultItem dri) {
        DropResultItem dri2 = dropItemMap.get(dri);
        if (dri2 == null) {
            dropItemMap.put(dri, dri);
            dropItems.add(dri);
        } else {
            dri2.dropTime += dri.dropTime;
        }
    }
    
    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        analyze();
        analyze2();

        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        dropListViewer = new ItemTreeViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        dropTree = dropListViewer.getTree();
        dropTree.setHeaderVisible(true);
        final GridData gd_dropTree = new GridData(SWT.FILL, SWT.FILL, true, true);
        dropTree.setLayoutData(gd_dropTree);

        final TreeColumn itemColumn = new TreeColumn(dropTree, SWT.NONE);
        itemColumn.setWidth(218);
        itemColumn.setText("��Ʒ");

        final TreeColumn dropRateColumn = new TreeColumn(dropTree, SWT.NONE);
        dropRateColumn.setWidth(100);
        dropRateColumn.setText("������");

        final TreeColumn valueColumn = new TreeColumn(dropTree, SWT.NONE);
        valueColumn.setWidth(100);
        valueColumn.setText("��ֵ");
        
        final TreeColumn fromColumn = new TreeColumn(dropTree, SWT.NONE);
        fromColumn.setWidth(205);
        fromColumn.setText("����");

        dropListViewer.setLabelProvider(new TreeLabelProvider());
        dropListViewer.setContentProvider(new TreeContentProvider());
        dropListViewer.setInput(this);
        
        final Button repeatButton = new Button(container, SWT.NONE);
        repeatButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                go();
                analyze2();
                dropListViewer.refresh();
            }
        });
        final GridData gd_repeatButton = new GridData(SWT.FILL, SWT.CENTER, false, false);
        repeatButton.setLayoutData(gd_repeatButton);
        repeatButton.setText("����һ���");
        
        return container;
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "ȷ��", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "ȡ��", false);
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(867, 747);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("�����ʷ���");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            File f = new File(folder);
            if (!f.exists() || !f.isDirectory()) {
                MessageDialog.openError(getShell(), "����", "Ŀ��Ŀ¼����ȷ��");
                return;
            }
            if (packageName.length() == 0) {
                MessageDialog.openError(getShell(), "����", "�������������");
                return;
            }
            if (prefix.length() == 0) {
                MessageDialog.openError(getShell(), "����", "������������ǰ׺��");
                return;
            }
        }
        super.buttonPressed(buttonId);
    }
}
