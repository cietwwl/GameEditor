package com.pip.game.editor.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.Currency;
import com.pip.game.data.DataObject;
import com.pip.game.data.DataObjectCategory;
import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.item.DropGroup;
import com.pip.game.data.item.DropItem;
import com.pip.game.data.item.Item;
import com.pip.game.data.item.SubDropGroup;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.EditorPlugin;

/**
 * ������༭��
 * 
 * @author Joy Yan
 * 
 */
public class DropGroupEditor extends DefaultDataObjectEditor implements ISelectionChangedListener, SelectionListener {
    private Text textDropRate;
    private Text textMaxMonsterLevel;
    private Text textMinMonsterLevel;
    
    class ItemTreeLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return super.getText(element);
        }

        public Image getImage(Object element) {
            if (element instanceof DataObjectCategory) {
                return EditorPlugin.getDefault().getImageRegistry().get("itemtype");
            } else if (element instanceof Item) {
                return EditorPlugin.getDefault().getImageRegistry().get("item");
            } else if (element instanceof DropGroup) {
                return EditorPlugin.getDefault().getImageRegistry().get("dropgroup");
            }
            return null;
        }
    }

    class ItemTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        public Object[] getChildren(Object element) {
            ProjectData proj = ProjectData.getActiveProject();
            if (element instanceof Integer) {
                // ���ڵ㣬����
                List<DataObjectCategory> cateList = null;
                switch (((Integer)element).intValue()) {
                case DropItem.DROP_TYPE_ITEM:
                    cateList = proj.getCategoryListByType(Item.class);
                    break;
                case DropItem.DROP_TYPE_EQUI:
                    cateList = proj.getCategoryListByType(Equipment.class);
                    break;
                case DropItem.DROP_TYPE_DROPGROUP:
                    cateList = proj.getCategoryListByType(DropGroup.class);
                    break;
                }
                if (searchCondition == null || searchCondition.length() == 0) {
                    return cateList.toArray();
                }
                List<DataObjectCategory> matchList = new ArrayList<DataObjectCategory>();
                for (DataObjectCategory cate : cateList) {
                    if (hasChildren(cate)) {
                        matchList.add(cate);
                    }
                }
                return matchList.toArray();
            } else if (element instanceof DataObjectCategory) {
                ArrayList ret = new ArrayList();
                DataObjectCategory cate = (DataObjectCategory)element;
                for(DataObjectCategory cate2 : cate.cates) {
                    if (searchCondition == null || searchCondition.length() == 0) {
                        ret.add(cate2);
                    } else if (hasChildren(cate2)) {
                        ret.add(cate2);
                    }
                }
                for (DataObject obj : cate.objects) {
                    if (matchCondition(obj)) {
                        ret.add(obj);
                    }
                }
                // ����
                return ret.toArray();
            }
            return new Object[0];
        }

        public Object getParent(Object element) {
            if (element instanceof DataObjectCategory) {
                Class cls = ((DataObjectCategory)element).dataClass;
                if (cls == Item.class) {
                    return new Integer(DropItem.DROP_TYPE_ITEM);
                } else if (cls == Equipment.class) {
                    return new Integer(DropItem.DROP_TYPE_EQUI);
                } else if (cls == DropGroup.class) {
                    return new Integer(DropItem.DROP_TYPE_DROPGROUP);
                }
            } else if (element instanceof Equipment) {
                return ((Equipment)element).owner.findCategory(Equipment.class, ((Equipment)element).getCategoryName());
            } else if (element instanceof Item) {
                return ((Item)element).owner.findCategory(Equipment.class, ((Item)element).getCategoryName());
            } else if (element instanceof DropGroup) {
                return ((DropGroup)element).owner.findCategory(DropGroup.class, ((DropGroup)element).getCategoryName());
            }
            return null;
        }

        public boolean hasChildren(Object element) {
            return getChildren(element).length > 0;
        }
        
        private boolean matchCondition(DataObject dobj) {
            if (searchCondition == null || searchCondition.length() == 0) {
                return true;
            }
            if (dobj.title.indexOf(searchCondition) >= 0 || String.valueOf(dobj.id).indexOf(searchCondition) >= 0) {
                return true;
            }
            return false;
        }
    }

    public static final String ID = "com.pip.game.editor.item.DropGroupEditor"; //$NON-NLS-1$

    private static final String[] COMBO_DROP_TYPE = { "�������", "��ͨ����" };
    private static final String[] COMBO_ITEM_TYPE = { "1.��Ʒ", "2.װ��", "3.������", "4.��Ǯ", "5.����ֵ" };
    
    private String[] dropTypeNames;
    private int[] dropTypeIDs;

    // ��������

    // ������ID
    private Text textID;
    // ����������
    private Text textTitle;
    // �����������������
    private Text textQuantityMin;
    // �����������������
    private Text textQuantityMax;
    // ���������� 0:������� 1.��ͨ����
    private Combo comboDropType;

    // �ȼ��б�༭

    // �ȼ���Χ�б�
    private ListViewer levelRangeList;
    // ����ȼ���Χ����
    private Text textLevelMax;
    // ����ȼ���Χ����
    private Text textLevelMin;
    // ����ְҵ����
    private Combo comboJob;
    // ���һ���ȼ���Χ��
    private Button buttonAddGroup;
    // ɾ��һ���ȼ���Χ��
    private Button buttonDelGroup;
    // ����һ���ȼ���Χ��
    private Button buttonUpdateGroup;

    // ������Ʒ�б�

    // ��ʾ������Ȩ�صı�ǩ
    private Label subGroupWeight;
    // ������Ʒ�б�
    protected DropGroupItemListEditor dropItemEditor;

    // ���������Ʒ����
    
    // ��������ѡ�� 1.��Ʒ 2.װ�� 3.������ 4.��Ǯ 5.����
    private Combo comboType;
    // �������
    // ��ѡ��Ʒ�б�
    private TreeViewer itemTreeViewer;
    private Tree itemTree;
    // �����Ʒ��ť
    private Button buttonAddDropItem;
    private Composite worldDropComposite;
    private Label labelSearchCondition;
    private Text textSearchCondition;
    private String searchCondition = "";
    private Button cbValid;


    /**
     * editor��ʼ��
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
    }

    public void createPartControl(Composite parent) {
        // ���������У� "1.��Ʒ", "2.װ��", "3.������", "4.��Ǯ", "5.����ֵ" ������������չ����
        List<DataObject> clist = ProjectData.getActiveProject().getDictDataListByType(Currency.class);
        dropTypeNames = new String[COMBO_ITEM_TYPE.length + clist.size()];
        dropTypeIDs = new int[COMBO_ITEM_TYPE.length + clist.size()];
        for (int i = 0; i < COMBO_ITEM_TYPE.length; i++) {
            dropTypeNames[i] = COMBO_ITEM_TYPE[i];
            dropTypeIDs[i] = i;
        }
        for (int i = 0; i < clist.size(); i++) {
            Currency c = (Currency)clist.get(i);
            dropTypeNames[i + COMBO_ITEM_TYPE.length] = c.toString();
            dropTypeIDs[i + COMBO_ITEM_TYPE.length] = c.id;
        }
        
        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 1;
        gridLayout.numColumns = 9;
        container.setLayout(gridLayout);

        // ��������

        final Label label = new Label(container, SWT.NONE);
        label.setText("������ID��");

        textID = new Text(container, SWT.BORDER);
        textID.setEditable(false);
        textID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setLayoutData(new GridData());
        label_1.setText("���������ƣ�");

        textTitle = new Text(container, SWT.BORDER);
        textTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textTitle.addModifyListener(this);

        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setLayoutData(new GridData());
        label_2.setText("����������");

        final Composite composite = new Composite(container, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        final GridLayout gridLayout_3 = new GridLayout();
        gridLayout_3.numColumns = 3;
        gridLayout_3.verticalSpacing = 0;
        gridLayout_3.marginWidth = 0;
        gridLayout_3.marginHeight = 0;
        gridLayout_3.horizontalSpacing = 0;
        composite.setLayout(gridLayout_3);

        textQuantityMin = new Text(composite, SWT.BORDER);
        textQuantityMin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textQuantityMin.addModifyListener(this);

        final Label label_3 = new Label(composite, SWT.NONE);
        label_3.setText("-");

        textQuantityMax = new Text(composite, SWT.BORDER);
        textQuantityMax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textQuantityMax.addModifyListener(this);

        final Label label_4 = new Label(container, SWT.NONE);
        label_4.setLayoutData(new GridData());
        label_4.setText("���������ͣ�");

        comboDropType = new Combo(container, SWT.READ_ONLY);
        comboDropType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
//                int type = comboDropType.getSelectionIndex();
//                GridData gd = (GridData)worldDropComposite.getLayoutData();
//                if (type == DropGroup.GROUP_TYPE_NORMAL) {
//                    gd.exclude = true;
//                } else {
//                    gd.exclude = false;
//                }
//                worldDropComposite.getParent().layout();
            }
        });
        comboDropType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboDropType.setItems(COMBO_DROP_TYPE);
        comboDropType.addModifyListener(this);

        cbValid = new Button(container, SWT.CHECK);
        cbValid.setText("��Ч");
        cbValid.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                setDirty(true);
            }
        });

        worldDropComposite = new Composite(container, SWT.NONE);
        final GridData gd_worldDropComposite = new GridData(SWT.FILL, SWT.FILL, false, false, 9, 1);
        worldDropComposite.setLayoutData(gd_worldDropComposite);
        final GridLayout gridLayout_6 = new GridLayout();
        gridLayout_6.numColumns = 7;
        worldDropComposite.setLayout(gridLayout_6);

        final Label label_7 = new Label(worldDropComposite, SWT.NONE);
        label_7.setText("���ù��Ｖ������(��)��");

        textMinMonsterLevel = new Text(worldDropComposite, SWT.BORDER);
        final GridData gd_textMinMonsterLevel = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textMinMonsterLevel.setLayoutData(gd_textMinMonsterLevel);
        textMinMonsterLevel.addModifyListener(this);

        final Label label_8 = new Label(worldDropComposite, SWT.NONE);
        label_8.setText("���ù��Ｖ������(��)��");

        textMaxMonsterLevel = new Text(worldDropComposite, SWT.BORDER);
        final GridData gd_textMaxMonsterLevel = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textMaxMonsterLevel.setLayoutData(gd_textMaxMonsterLevel);
        textMaxMonsterLevel.addModifyListener(this);

        final Label label_9 = new Label(worldDropComposite, SWT.NONE);
        label_9.setText("�����ʣ�");

        textDropRate = new Text(worldDropComposite, SWT.BORDER);
        final GridData gd_textDropRate = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textDropRate.setLayoutData(gd_textDropRate);
        textDropRate.addModifyListener(this);
        
        final Label label_10 = new Label(worldDropComposite, SWT.NONE);
        label_10.setText("%%");

        final SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 9, 1));

        // �����б�༭

        final Composite compositeLevel = new Composite(sashForm, SWT.NONE);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 4;
        compositeLevel.setLayout(gridLayout_1);

        final Label label_5 = new Label(compositeLevel, SWT.NONE);
        label_5.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
        label_5.setText("�ȼ��б�");

        levelRangeList = new ListViewer(compositeLevel, SWT.BORDER);
        levelRangeList.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 4, 1));
        levelRangeList.setContentProvider(new ListContentProvider());
        levelRangeList.addSelectionChangedListener(this);

        final Label label_6 = new Label(compositeLevel, SWT.NONE);
        label_6.setText("����");

        textLevelMin = new Text(compositeLevel, SWT.BORDER);
        textLevelMin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label label_13 = new Label(compositeLevel, SWT.NONE);
        label_13.setText("-");

        textLevelMax = new Text(compositeLevel, SWT.BORDER);
        textLevelMax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label label_14 = new Label(compositeLevel, SWT.NONE);
        label_14.setLayoutData(new GridData());
        label_14.setText("ְҵ��");

        comboJob = new Combo(compositeLevel, SWT.READ_ONLY);
        comboJob.setItems(ProjectData.getActiveProject().config.PLAYER_CLAZZ);
        final GridData gd_comboJob = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        comboJob.setLayoutData(gd_comboJob);
        comboJob.select(0);

        final Composite composite_1 = new Composite(compositeLevel, SWT.NONE);
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
        final GridLayout gridLayout_5 = new GridLayout();
        gridLayout_5.marginWidth = 0;
        gridLayout_5.numColumns = 3;
        composite_1.setLayout(gridLayout_5);

        buttonAddGroup = new Button(composite_1, SWT.NONE);
        buttonAddGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        buttonAddGroup.setText("���");
        buttonAddGroup.addSelectionListener(this);

        buttonUpdateGroup = new Button(composite_1, SWT.NONE);
        buttonUpdateGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        buttonUpdateGroup.setText("����");
        buttonUpdateGroup.addSelectionListener(this);

        buttonDelGroup = new Button(composite_1, SWT.NONE);
        buttonDelGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        buttonDelGroup.setText("ɾ��");
        buttonDelGroup.addSelectionListener(this);

        // �����б�༭

        final Composite compositeDrop = new Composite(sashForm, SWT.NONE);
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 3;
        compositeDrop.setLayout(gridLayout_2);

        subGroupWeight = new Label(compositeDrop, SWT.NONE);
        subGroupWeight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        subGroupWeight.setText("�����б�");

        final Label label_12 = new Label(compositeDrop, SWT.NONE);
        label_12.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        label_12.setText("[����Ҽ�������������ֵ]");

        final Button buttonAverage = new Button(compositeDrop, SWT.NONE);
        buttonAverage.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                dropItemEditor.average();
            }
        });
        final GridData gd_buttonAverage = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        buttonAverage.setLayoutData(gd_buttonAverage);
        buttonAverage.setText("���ֵ�����");

        initDropItemEditor(compositeDrop);
        // �����Ʒ����

        final Composite compositeItemList = new Composite(sashForm, SWT.NONE);
        final GridLayout gridLayout_4 = new GridLayout();
        gridLayout_4.numColumns = 2;
        compositeItemList.setLayout(gridLayout_4);

        final Label label_11 = new Label(compositeItemList, SWT.NONE);
        label_11.setText("�������ͣ�");

        comboType = new Combo(compositeItemList, SWT.READ_ONLY);
        comboType.setVisibleItemCount(15);
        comboType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboType.setItems(dropTypeNames);
        comboType.select(0);
        comboType.addSelectionListener(this);

        labelSearchCondition = new Label(compositeItemList, SWT.NONE);
        labelSearchCondition.setText("������");

        textSearchCondition = new Text(compositeItemList, SWT.BORDER);
        textSearchCondition.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.character == '\r') {
                	searchCondition = textSearchCondition.getText();
                    itemTreeViewer.refresh();
                }
            }
        });
        final GridData gd_textSearchCondition = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textSearchCondition.setLayoutData(gd_textSearchCondition);

        itemTreeViewer = new ItemTreeViewer(compositeItemList, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        itemTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)itemTreeViewer.getSelection();
                if (!sel.isEmpty() && sel.getFirstElement() instanceof DataObject) {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    DataListView view = (DataListView)page.findView(DataListView.ID);
                    if (view != null) {
                        view.editObject((DataObject)sel.getFirstElement());
                    }
                }
            }
        });
        itemTreeViewer.setLabelProvider(new ItemTreeLabelProvider());
        itemTreeViewer.setContentProvider(new ItemTreeContentProvider());
        itemTree = itemTreeViewer.getTree();
        final GridData gd_itemTree = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        itemTree.setLayoutData(gd_itemTree);
        itemTreeViewer.setInput(new Integer(DropItem.DROP_TYPE_ITEM));

        buttonAddDropItem = new Button(compositeItemList, SWT.NONE);
        final GridData gd_buttonAddDropItem = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
        buttonAddDropItem.setLayoutData(gd_buttonAddDropItem);
        buttonAddDropItem.setText("���");
        buttonAddDropItem.addSelectionListener(this);

        sashForm.setWeights(new int[] { 154, 482, 285 });

        DropGroup dropGroup = (DropGroup) editObject;
        setCurrentData(dropGroup);
        if (dropGroup.subGroup.size() > 0) {
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(100);
                        getSite().getShell().getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                levelRangeList.setSelection(new StructuredSelection(((DropGroup)editObject).subGroup.get(0)));
                                dropItemEditor.redraw();
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }.start();
        }

        setDirty(false);
        setPartName(this.getEditorInput().getName());
        saveStateToUndoBuffer();
    }
    
    public void initDropItemEditor(Composite parent){
        final ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrolledComposite.getVerticalBar().setPageIncrement(500);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        
        dropItemEditor = new DropGroupItemListEditor(scrolledComposite, SWT.NONE); 
        dropItemEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        dropItemEditor.addModifyListener(this);
        
        scrolledComposite.setContent(dropItemEditor);
    }
    /**
     * ��һ���������������ʾ������Ԫ��
     * 
     * @param group
     *            ���������ݶ���
     */
    private void setCurrentData(DropGroup group) {
        textID.setText(String.valueOf(group.id));
        textTitle.setText(group.title);
        textQuantityMin.setText(String.valueOf(group.quantityMin));
        textQuantityMax.setText(String.valueOf(group.quantityMax));
        comboDropType.select(group.groupType);
        textMinMonsterLevel.setText(String.valueOf(group.minMonsterLevel));
        textMaxMonsterLevel.setText(String.valueOf(group.maxMonsterLevel));
        textDropRate.setText(String.valueOf(group.dropRate));
        cbValid.setSelection(group.valid);

        levelRangeList.setInput(group.subGroup);
    }
    
    /**
     * ���浱ǰ�༭���ݡ�
     */
    protected void saveData() throws Exception {
        DropGroup dataDef = (DropGroup)editObject;
        dataDef.title = textTitle.getText();
        try {
            dataDef.quantityMin = Integer.parseInt(textQuantityMin.getText());
            dataDef.quantityMax = Integer.parseInt(textQuantityMax.getText());
        } catch (Exception e) {
            throw new Exception("���������ʽ����");
        }
        dataDef.groupType = comboDropType.getSelectionIndex();
        if (dataDef.groupType == DropGroup.GROUP_TYPE_WORLD) {
            try {
                dataDef.minMonsterLevel = Integer.parseInt(textMinMonsterLevel.getText());
                dataDef.maxMonsterLevel = Integer.parseInt(textMaxMonsterLevel.getText());
                dataDef.dropRate = Integer.parseInt(textDropRate.getText());
            } catch (Exception e) {
                throw new Exception("��������������������");
            }
        }
        dataDef.valid = cbValid.getSelection();
    }

    /**
     * �����û�ָ��������ʾ��ǰָ���ȼ���Χ�ĵ�����
     * 
     * @param subDrop
     */
    protected void setSubDropGroup(SubDropGroup subDrop) {
        if (subDrop == null) {
            dropItemEditor.setInput( genSubDropGroup(0, 0, 0));
            return;
        }

        textLevelMax.setText(String.valueOf(subDrop.levelMax));
        textLevelMin.setText(String.valueOf(subDrop.levelMin));
        comboJob.select(subDrop.job + 1);

        dropItemEditor.setInput(subDrop);
    }

    /**
     * ��ʾ������Ϣ��ʾ��
     * 
     * @param message
     */
    private void showMessage(String message) {
        MessageDialog.openError(getSite().getShell(), "��ʾ��", message);
    }

    /**
     * �б�ѡ���¼�
     */
    public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection seleted = (IStructuredSelection) event.getSelection();
        if (event.getSource() == levelRangeList) {
            /* �ȼ���Χѡ���б� */
            SubDropGroup subDropGroup = (SubDropGroup) seleted.getFirstElement();
            setSubDropGroup(subDropGroup);
        }
    }

    private void hideControl(Control obj) {
        obj.setVisible(false);
        ((GridData) obj.getLayoutData()).exclude = true;
        obj.getParent().layout();
    }

    private void showControl(Control obj) {
        obj.setVisible(true);
        ((GridData) obj.getLayoutData()).exclude = false;
        obj.getParent().layout();
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    /**
     * ��ť������ѡ�����Ϣ����
     */
    public void widgetSelected(SelectionEvent e) {
        try {
            if (e.getSource() == buttonAddGroup) {
                /* ��ӵȼ���Χ */
                onAddGroup();
            } else if (e.getSource() == buttonDelGroup) {
                /* ɾ���ȼ���Χ */
                onDeleteGroup();
            } else if (e.getSource() == buttonUpdateGroup) {
                /* ���µȼ���Χ */
                onUpdateGroup();
            } else if (e.getSource() == comboType) {
                /* ���������޸ģ������б� */
                int type = dropTypeIDs[comboType.getSelectionIndex()];
                if (type == DropItem.DROP_TYPE_ITEM || type == DropItem.DROP_TYPE_EQUI
                        || type == DropItem.DROP_TYPE_DROPGROUP) {
                    showControl(itemTree);
                    showControl(labelSearchCondition);
                    showControl(textSearchCondition);
                    itemTreeViewer.setInput(new Integer(type));
                } else {
                    hideControl(itemTree);
                    hideControl(labelSearchCondition);
                    hideControl(textSearchCondition);
                }
            } else if (e.getSource() == buttonAddDropItem) {
                /* ��ӵ�����Ŀ */
                onAddItem();
            }
        } catch (Exception e1) {
            showMessage("����\n" + e1.toString());
        }
    }

    /**
     * ִ����ӵȼ���Χ��Ϣ����
     */
    private void onAddGroup() {
        int levelMin = Integer.parseInt(textLevelMin.getText());
        int levelMax = Integer.parseInt(textLevelMax.getText());
        int job = comboJob.getSelectionIndex() - 1;

        DropGroup group = (DropGroup) editObject;

        if (levelMin > levelMax) {
            showMessage("����ĵȼ�����С�����ޣ�");
            return;
        }

        if (levelMin < DropGroup.LEVEL_MIN || levelMax > DropGroup.LEVEL_MAX) {
            showMessage("�ȼ��������Ʒ�Χ��");
            return;
        }

        if (!group.isRangeValid(levelMin, levelMax, job, -1)) {
            showMessage("�ȼ���Χ�������غϣ�");
            return;
        }

        SubDropGroup sub = genSubDropGroup(levelMax, levelMin, job);
        group.subGroup.add(sub);
        
        levelRangeList.refresh();
        levelRangeList.setSelection(new StructuredSelection(sub));

        setDirty(true);
    }

    protected SubDropGroup genSubDropGroup(int levelMax, int levelMin, int job){
    	  SubDropGroup sub = new SubDropGroup();
          sub.levelMax = levelMax;
          sub.levelMin = levelMin;
          sub.job = job;
          return sub;
    }
    /**
     * ɾ���ȼ���Χ��Ϣ����
     */
    private void onDeleteGroup() {
        IStructuredSelection selected = (IStructuredSelection) levelRangeList.getSelection();
        SubDropGroup subGroup = (SubDropGroup) selected.getFirstElement();
        if (subGroup != null) {
            ((DropGroup) editObject).subGroup.remove(subGroup);
            levelRangeList.refresh();
            levelRangeList.setSelection(new StructuredSelection());

            setDirty(true);
        }
    }

    /**
     * ���µȼ���Χ��Ϣ����
     */
    private void onUpdateGroup() {
        IStructuredSelection selected = (IStructuredSelection) levelRangeList.getSelection();
        int index = levelRangeList.getList().getSelectionIndex();
        SubDropGroup subGroup = (SubDropGroup) selected.getFirstElement();
        if (subGroup == null) {
            return;
        }
        
        int levelMin = Integer.parseInt(textLevelMin.getText());
        int levelMax = Integer.parseInt(textLevelMax.getText());
        int job = comboJob.getSelectionIndex() - 1;

        if (levelMin > levelMax) {
            showMessage("����ĵȼ�����С�����ޣ�");
            return;
        }

        /* �жϵȼ���û�г���ϵͳ���� */
        if (levelMin < DropGroup.LEVEL_MIN || levelMax > DropGroup.LEVEL_MAX) {
            showMessage("�ȼ��������Ʒ�Χ��");
            return;
        }

        /* �жϵȼ���Χ�Ƿ����غ� */
        DropGroup group = (DropGroup) editObject;
        if (!group.isRangeValid(levelMin, levelMax, job, index)) {
            showMessage("�ȼ���Χ�������غϣ�");
            return;
        }

        subGroup.levelMax = levelMax;
        subGroup.levelMin = levelMin;
        subGroup.job = job;
        
        levelRangeList.refresh();

        setDirty(true);
    }
    
    /*
     * ��ǰѡ�е��ӵ����������һ��������Ʒ��
     * @param group ������
     * @param owner �ӵ�����
     * @param type ������Ŀ����
     * @param obj ���������Ʒ��װ���������
     */
    private void tryAddDropItem(DropGroup group, SubDropGroup owner, int type, DataObject obj) throws Exception {
        if (obj instanceof DropGroup) {
            if (!group.isGroupValid((DropGroup)obj)) {
                throw new Exception("�еݹ���õĵ����飡");
            }
        }

        DropItem drop = owner.getNewDropItem();
        drop.dropID = obj.id;
        drop.dropType = type;
        drop.quantityMax = 1;
        drop.quantityMin = 1;
        drop.dropWeight = 0;
        drop.dropObj = obj;
        owner.dropGroup.add(drop);
    }

    /**
     * ���һ��������Ʒ
     */
    private void onAddItem() {
        int dropType = dropTypeIDs[comboType.getSelectionIndex()];

        // ����ѡ�е��ӵ�����
        IStructuredSelection selSubGroup = (IStructuredSelection) levelRangeList.getSelection();
        SubDropGroup selection = (SubDropGroup) selSubGroup.getFirstElement();
        if (selection == null) {
            showMessage("��ѡ��һ����Ч�ĵȼ���Χ��");
            return;
        }
        
        // ����ѡ�е���Ʒ/װ��/���������
        if (dropType == DropItem.DROP_TYPE_ITEM || dropType == DropItem.DROP_TYPE_EQUI || dropType == DropItem.DROP_TYPE_DROPGROUP) {
            StructuredSelection sel = (StructuredSelection)itemTreeViewer.getSelection();
            Iterator itor = sel.iterator();
            while (itor.hasNext()) {
                Object obj = itor.next();
                if (obj instanceof Item || obj instanceof DropGroup) {
                    try {
                        tryAddDropItem((DropGroup)editObject, selection, dropType, (DataObject)obj);
                    } catch (Exception e) {
                        showMessage(e.getMessage());
                    }
                }
            }
        } else {
            DropItem drop = selection.getNewDropItem();
            drop.dropID = -1;
            drop.dropType = dropType;
            drop.quantityMax = 1;
            drop.quantityMin = 1;
            drop.dropWeight = 0;
            drop.dropObj = null;
            selection.dropGroup.add(drop);
        }

        dropItemEditor.setInput(selection);

        setDirty(true);
    }

    /**
     * �ȼ���Χ�б����������ṩ��
     */
    class ListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List inputData = (List) inputElement;
                return inputData.toArray();
            }
            return null;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    /**
     * ������Ʒ�б����������ṩ��
     * 
     */
    class ItemListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                ProjectData projData = ProjectData.getActiveProject();
                List<DropItem> inputData = (List<DropItem>)inputElement;
                
                /* ���ݵ�����Ʒ���ͣ���֯�б����� */
                for (DropItem drop : inputData) {
                    switch (drop.dropType) {
                    case DropItem.DROP_TYPE_ITEM: /* ������Ʒ */
                        if (drop.dropObj == null) {
                            drop.dropObj = projData.findItem(drop.dropID);
                        }
                        break;
                    case DropItem.DROP_TYPE_EQUI: /* ����װ�� */
                        if (drop.dropObj == null) {
                            drop.dropObj = projData.findEquipment(drop.dropID);
                        }
                        break;
                    case DropItem.DROP_TYPE_DROPGROUP: /* ��������� */
                        if (drop.dropObj == null) {
                            drop.dropObj = projData.findObject(DropGroup.class, drop.dropID);
                        }
                        break;
                    }
                }
                return inputData.toArray();
            }
            return new Object[0];
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

}
