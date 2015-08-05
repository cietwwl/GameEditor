package com.pip.game.editor.property;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.pip.game.data.Currency;
import com.pip.game.data.DataObject;
import com.pip.game.data.DataObjectCategory;
import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.item.DropGroup;
import com.pip.game.data.item.DropItem;
import com.pip.game.data.item.DropNode;
import com.pip.game.data.item.Item;
import com.pip.game.data.quest.Quest;
import com.pip.game.editor.EditorPlugin;

public class ChooseDropGroupDialog  extends Dialog {
    private Text textQuest;
    private Combo comboCopy;
    private Combo comboTaskFlag;
    private Text textDropRate;
    private Text dropQuantityMax;
    private Text dropQuantityMin;
    private Text text;
    private String searchCondition;
    
    class TreeLabelProvider extends LabelProvider {
        public String getText(Object element) {
            if (element instanceof ProjectData) {
                return "��Ŀ";
            } else if (element instanceof Integer) {
                switch(((Integer)element).intValue()){
                case DropItem.DROP_TYPE_DROPGROUP:
                    return "������";
                case DropItem.DROP_TYPE_EQUI:
                    return "װ��";
                case DropItem.DROP_TYPE_ITEM:
                    return "��Ʒ";
                case DropItem.DROP_TYPE_MONEY:
                    return "��Ǯ";
                case DropItem.DROP_TYPE_EXP:
                    return "����";
                default:
                    // ��չ���ҵ���
                    Currency c = (Currency)ProjectData.getActiveProject().findDictObject(Currency.class, ((Integer)element).intValue());
                    return c.title;
                }
            }
            return super.getText(element);
        }
        
        public Image getImage(Object element) {
            if (element instanceof DataObjectCategory) {
                return EditorPlugin.getDefault().getImageRegistry().get("itemtype");
            } 
            else if (element instanceof Item || element instanceof Equipment) {
                return EditorPlugin.getDefault().getImageRegistry().get("item");
            }
            else if (element instanceof DropGroup) {
                return EditorPlugin.getDefault().getImageRegistry().get("dropgroup");
            }
            else if (element instanceof DropItem) {
                return EditorPlugin.getDefault().getImageRegistry().get("dropitem");
            }
            else if (element instanceof Integer) {
                return EditorPlugin.getDefault().getImageRegistry().get("rootnode");
            }
            return null;
        }
    }
    class TreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        private Integer itemNode = new Integer(DropItem.DROP_TYPE_ITEM);
        private Integer equNode = new Integer(DropItem.DROP_TYPE_EQUI);
        private Integer dropGroupNode = new Integer(DropItem.DROP_TYPE_DROPGROUP);
        
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
        public void dispose() {
        }
        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof ProjectData) {
                List<Integer> retList = new ArrayList<Integer>();
                retList.add(itemNode);
                retList.add(equNode);
                retList.add(dropGroupNode);
                retList.add(new Integer(DropItem.DROP_TYPE_MONEY));
                retList.add(new Integer(DropItem.DROP_TYPE_EXP));
                List<DataObject> clist = ProjectData.getActiveProject().getDictDataListByType(Currency.class);
                for (DataObject c : clist) {
                    retList.add(new Integer(((Currency)c).id));
                }
                return retList.toArray();
            } else if (parentElement instanceof Integer) {
                int type = ((Integer)parentElement).intValue();
                Object[] arr;
                if (type == DropItem.DROP_TYPE_ITEM) {
                    //��Ʒ���ڵ�
                    arr = ProjectData.getActiveProject().getCategoryListByType(Item.class).toArray();
                } else if (type == DropItem.DROP_TYPE_EQUI) {
                    //װ�����ڵ�
                    arr = ProjectData.getActiveProject().getCategoryListByType(Equipment.class).toArray();
                } else if (type == DropItem.DROP_TYPE_DROPGROUP) {
                    //��������ڵ�
                    arr = ProjectData.getActiveProject().getCategoryListByType(DropGroup.class).toArray();
                } else {
                    return new Object[0];
                }
                List<Object> retList = new ArrayList<Object>();
                for (Object o : arr) {
                    if (getChildren(o).length > 0) {
                        retList.add(o);
                    }
                }
                return retList.toArray();
            } else if (parentElement instanceof DataObjectCategory) {
                if (searchCondition == null || searchCondition.length() == 0) {
                    List<Object> retList = new ArrayList<Object>();
                    for (DataObjectCategory cate : ((DataObjectCategory)parentElement).cates) {
                        retList.add(cate);
                    }
                    for (DataObject dobj : ((DataObjectCategory)parentElement).objects) {
                        retList.add(dobj);
                    }
                    return retList.toArray();
                } else {
                    List<DataObject> list = ((DataObjectCategory)parentElement).objects;
                    List<Object> retList = new ArrayList<Object>();
                    for (DataObjectCategory cate : ((DataObjectCategory)parentElement).cates) {
                        if (getChildren(cate).length > 0) {
                            retList.add(cate);
                        }
                    }
                    for (DataObject dobj : list) {
                        if (matchCondition(dobj)) {
                            retList.add(dobj);
                        }
                    }
                    return retList.toArray();            
                }
            }
            return new Object[0];
        }
        
        public Object getParent(Object element) {
            if (element instanceof ProjectData) {
                return null;
            } else if (element instanceof Integer) {
                return ProjectData.getActiveProject();
            } else if (element instanceof DataObjectCategory) {
            	DataObjectCategory cate = (DataObjectCategory)element;
            	if (cate.parent != null) {
            		return cate.parent;
            	}
                Class cls = ((DataObjectCategory)element).dataClass;
                if (isSubClass(cls, Equipment.class)) {
                    return equNode;
                } else if (isSubClass(cls, Item.class)) {
                    return itemNode;
                } else if (isSubClass(cls, DropGroup.class)) {
                    return dropGroupNode;
                }
            } else if (element instanceof Equipment) {
                return ProjectData.getActiveProject().findCategory(Equipment.class, ((Equipment)element).getCategoryName());
            } else if (element instanceof Item) {
                return ProjectData.getActiveProject().findCategory(Item.class, ((Item)element).getCategoryName());
            } else if (element instanceof DropGroup) {
                return dropGroupNode;
            }
            return null;
        }
        public boolean hasChildren(Object element) {
            return getChildren(element).length > 0;
        }
    }
    private TreeViewer treeViewer;
    private Tree tree;
    private DropNode selectedObject;
    private Button buttonChooseQuest;
    private int questID = -1;
    
    private boolean isSubClass(Class c1, Class c2) {
        try {
            c1.asSubclass(c2);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * ѡ����Ŀid
     * @return
     */
    public DropNode getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedItem(DropNode selectedItem) {
        this.selectedObject = selectedItem;
    }
    
    private boolean matchCondition(DataObject dataobj) {
        if (searchCondition == null || searchCondition.length() == 0) {
            return true;
        }
        if (dataobj.title.indexOf(searchCondition) >= 0 || String.valueOf(dataobj.id).indexOf(searchCondition) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseDropGroupDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        container.setLayout(gridLayout);

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setText("����������");

        dropQuantityMin = new Text(container, SWT.BORDER);
        dropQuantityMin.setText("1");
        final GridData gd_dropQuantityMin = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dropQuantityMin.setLayoutData(gd_dropQuantityMin);

        dropQuantityMax = new Text(container, SWT.BORDER);
        dropQuantityMax.setText("1");
        final GridData gd_dropQuantityMax = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dropQuantityMax.setLayoutData(gd_dropQuantityMax);

        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setText("���伸�ʣ�");

        textDropRate = new Text(container, SWT.BORDER);
        final GridData gd_dropRate = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textDropRate.setLayoutData(gd_dropRate);
        textDropRate.setText("100%");

        new Label(container, SWT.NONE);

        final Label label_5 = new Label(container, SWT.NONE);
        label_5.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        label_5.setText("ʾ����1% = �ٷ�֮һ   1%% = ���֮һ   1%%% = �����֮һ");

        final Label label_3 = new Label(container, SWT.NONE);
        label_3.setText("�Ƿ�����");

        comboTaskFlag = new Combo(container, SWT.READ_ONLY);
        comboTaskFlag.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        comboTaskFlag.setItems(new String[]{"��","��"});
        comboTaskFlag.select(0);
        comboTaskFlag.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                buttonChooseQuest.setEnabled(comboTaskFlag.getSelectionIndex() == 1);
            }
        });

        final Label label_4 = new Label(container, SWT.NONE);
        label_4.setText("��������");

        final Composite composite = new Composite(container, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.verticalSpacing = 0;
        gridLayout_1.marginWidth = 0;
        gridLayout_1.marginHeight = 0;
        gridLayout_1.horizontalSpacing = 0;
        gridLayout_1.numColumns = 2;
        composite.setLayout(gridLayout_1);

        textQuest = new Text(composite, SWT.READ_ONLY | SWT.BORDER);
        final GridData gd_textQuest = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textQuest.setLayoutData(gd_textQuest);

        buttonChooseQuest = new Button(composite, SWT.NONE);
        buttonChooseQuest.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                ChooseQuestDialog dlg = new ChooseQuestDialog(getShell());
                dlg.setSelectedQuest(questID);
                if (dlg.open() == Dialog.OK) {
                    questID = dlg.getSelectedQuest();
                    textQuest.setText(Quest.toString(ProjectData.getActiveProject(), questID));
                }
            }
        });
        buttonChooseQuest.setText("...");

        final Label label_6 = new Label(container, SWT.NONE);
        label_6.setText("�Ƿ���");

        comboCopy = new Combo(container, SWT.READ_ONLY);
        comboCopy.setItems(new String[] {"��", "��"});
        final GridData gd_comboShare = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        comboCopy.setLayoutData(gd_comboShare);
        comboCopy.select(0);

        final Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData());
        label.setText("���ң�");

        text = new Text(container, SWT.BORDER);
        text.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                searchCondition = text.getText();
                StructuredSelection sel = (StructuredSelection)treeViewer.getSelection();
                Object selObj = sel.isEmpty() ? null : sel.getFirstElement();
                treeViewer.refresh();
                treeViewer.expandAll();
                if (selObj != null) {
                    sel = new StructuredSelection(selObj);
                    treeViewer.setSelection(sel);
                }
            }
        });
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        treeViewer = new TreeViewer(container, SWT.BORDER);
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                Object selObj = sel.getFirstElement();
                if (selObj instanceof Item || selObj instanceof DropGroup || 
                        (selObj instanceof Integer && ((Integer)selObj).intValue() > DropItem.DROP_TYPE_DROPGROUP )) {
                    buttonPressed(IDialogConstants.OK_ID);
                } else {
                    if (treeViewer.getExpandedState(selObj)) {
                        treeViewer.collapseToLevel(selObj, 1);
                    } else {
                        treeViewer.expandToLevel(selObj, 1);
                    }
                }
            }
        });
        treeViewer.setLabelProvider(new TreeLabelProvider());
        treeViewer.setContentProvider(new TreeContentProvider());
        tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        treeViewer.setInput(ProjectData.getActiveProject());
        // treeViewer.expandAll();
        
        /**
         * ���ѡ�����Ĭ��ֵ������Ҫ�趨Ĭ��ֵ
         */
        if (selectedObject != null) {
            questID = selectedObject.taskId;
            textQuest.setText(Quest.toString(ProjectData.getActiveProject(), questID));
            try {
                switch(selectedObject.type){
                    case DropItem.DROP_TYPE_DROPGROUP:{
                        DataObject drip = ProjectData.getActiveProject().findObject(DropGroup.class, selectedObject.id);
                        if (drip != null) {
                            searchCondition = drip.title;
                            StructuredSelection sel = new StructuredSelection(drip);
                            treeViewer.setSelection(sel);
                            treeViewer.expandToLevel(drip, 1);
                        }
                        break;
                    }
                    case DropItem.DROP_TYPE_EQUI:{
                        DataObject drip = ProjectData.getActiveProject().findEquipment(selectedObject.id);
                        if (drip != null) {
                            searchCondition = drip.title;
                            StructuredSelection sel = new StructuredSelection(drip);
                            treeViewer.setSelection(sel);
                            treeViewer.expandToLevel(drip, 1);
                        }
                        break;
                    }
                    case DropItem.DROP_TYPE_ITEM:{
                        Item item = ProjectData.getActiveProject().findItem(selectedObject.id);
                        if (item != null) {
                            searchCondition = item.title;
                            StructuredSelection sel = new StructuredSelection(item);
                            treeViewer.setSelection(sel);
                            treeViewer.expandToLevel(item, 1);
                        }
                        break;
                    }
                    default: {
                        StructuredSelection sel = new StructuredSelection(new Integer(selectedObject.type));
                        treeViewer.setSelection(sel);
                        break;
                    }
                }
                
                if(searchCondition != null){
                    text.setText(searchCondition);
                }
                dropQuantityMax.setText(String.valueOf(selectedObject.quantityMax));
                dropQuantityMin.setText(String.valueOf(selectedObject.quantityMin));
                textDropRate.setText(String.valueOf(selectedObject.getRateString()));
                comboTaskFlag.select(selectedObject.isTask ? 1 : 0);
                if (selectedObject.isTask) {
                    DataObject currentQuest = ProjectData.getActiveProject().findObject(Quest.class, selectedObject.taskId);
                    if (currentQuest != null) {
                    }
                }
                comboCopy.select(selectedObject.copy ? 1 : 0);
            } catch (Exception e) {
            }
        }

        return container;
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "ȷ��", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "ȡ��", false);
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(622, 644);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("ѡ�����");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            StructuredSelection sel = (StructuredSelection)treeViewer.getSelection();
            if (sel.isEmpty()) {
                MessageDialog.openInformation(getShell(), "��ʾ", "��ѡ��һ��������Ʒ��");
                return;
            }
            if (selectedObject == null) {
                selectedObject = new DropNode();
            }
            Object selObj = sel.getFirstElement();
            if (selObj instanceof Equipment) {
                selectedObject.id = ((Equipment)selObj).id;
                selectedObject.type = DropItem.DROP_TYPE_EQUI;
            } else if (selObj instanceof Item) {
                selectedObject.id = ((Item)selObj).id;
                selectedObject.type = DropItem.DROP_TYPE_ITEM;
            } else if (selObj instanceof DropGroup) {
                selectedObject.id = ((DropGroup)selObj).id;
                selectedObject.type = DropItem.DROP_TYPE_DROPGROUP;
            } else if (selObj instanceof Integer && ((Integer)selObj).intValue() > DropItem.DROP_TYPE_DROPGROUP) {
                selectedObject.type = ((Integer)selObj).intValue();
            } else {
                MessageDialog.openInformation(getShell(), "��ʾ", "��ѡ��һ��������Ʒ��");
                return;
            }
                
            try {
                selectedObject.quantityMin = Integer.parseInt(dropQuantityMin.getText());
                selectedObject.quantityMax = Integer.parseInt(dropQuantityMax.getText());
                
                int dropRate = selectedObject.getDropRate(textDropRate.getText());
                switch(dropRate){
                    case DropNode.ERROR_SYMBOL:{
                        throw new Exception("������ʷ��Ŵ���");
                    }
                    case DropNode.ERROR_VALUE:{
                        throw new Exception("���������ֵ����");
                    }
                    case DropNode.ERROR_OUT_OF_RANGE:{
                        throw new Exception("������ʳ�����Χ��");
                    }
                }
                selectedObject.dropRate = dropRate;
                
                if(selectedObject.quantityMin < 1 || selectedObject.quantityMax < 1
                        || selectedObject.quantityMin > selectedObject.quantityMax){
                    throw new Exception("����������ʽ����");
                }
            } catch (Exception e) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), "����", e.getMessage());
                return;
            }
                
            selectedObject.isTask = comboTaskFlag.getSelectionIndex() == 1;
            if (selectedObject.isTask) {
                selectedObject.taskId = questID;
                if (selectedObject.taskId <= 0) {
                    /* �û�û��ѡ��һ��������ʾ */
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), "��ʾ��", "��ѡ��һ��������߰�����״̬ѡΪ��");
                    return;
                }
            } else{
                selectedObject.taskId = -1;
            }
            selectedObject.copy = comboCopy.getSelectionIndex() == 1;
        }
        super.buttonPressed(buttonId);
    }

}
