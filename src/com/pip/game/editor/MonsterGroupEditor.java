package com.pip.game.editor;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.DataObject;
import com.pip.game.data.DataObjectCategory;
import com.pip.game.data.MonsterGroup;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.data.item.Monster;
import com.pip.game.data.item.SubMonsterGroup;
import com.pip.game.editor.item.ItemTreeViewer;
import com.pip.game.editor.property.CheckBoxCellEditor;
import com.pip.util.AutoSelectAll;
import com.pip.util.Utils;

public class MonsterGroupEditor extends DefaultDataObjectEditor implements ISelectionChangedListener, SelectionListener{

    protected static final String[] COMBO_ITEM_TYPE = { "1.����", "2.������" };
    
    protected MonsterGroup editorObj;
    
    public static final String ID = "com.pip.game.editor.MonsterGroupEditor"; //$NON-NLS-1$
    protected Text textID;
    protected Text textTitle;
    protected TableViewer tableViewer;
    protected Table tableMonsters;
//    private Combo comboLevel;
    protected Button canCheckout;
    
    //��ѡnpc����
    protected TreeViewer itemTreeViewer;
    protected Tree itemTree;
    
    //ս���е�վλ
    //��������Ϊ����1,2,3,4,5��ʾ�����һ��վλ��11,12,13,14,15��ʾ����ڶ���վλ
    //0��ʾĬ��ֵ��ʹ�õ���Ĭ��վλ
    protected String[] positionList = new String[]{
       "0", 
       "1", "2", "3", "4", "5", 
       "6", "7", "8", "9", "10"
    };
    protected CCombo positionCombo;
    
    //��Ҹ�����Χ�б�
    protected ListViewer playerCountRangeList;   
    public ListViewer getPlayerCountRangeList() {
        return playerCountRangeList;
    }

    public void setPlayerCountRangeList(ListViewer playerCountRangeList) {
        this.playerCountRangeList = playerCountRangeList;
    }

    protected org.eclipse.swt.widgets.List levelList;
    public Text getTextCountMax() {
        return textCountMax;
    }

    public void setTextCountMax(Text textCountMax) {
        this.textCountMax = textCountMax;
    }

    public Text getTextCountMin() {
        return textCountMin;
    }

    public void setTextCountMin(Text textCountMin) {
        this.textCountMin = textCountMin;
    }

    // ������Ҹ�����Χ����
    protected Text textCountMax;
    // ������Ҹ�����Χ����
    protected Text textCountMin;
    // ���һ����Ҹ�����Χ��
    protected Button buttonAddGroup;
    public Button getButtonAddGroup() {
        return buttonAddGroup;
    }

    public void setButtonAddGroup(Button buttonAddGroup) {
        this.buttonAddGroup = buttonAddGroup;
    }

    // ɾ��һ����Ҹ�����Χ��
    protected Button buttonDelGroup;
    public Button getButtonUpdateGroup() {
        return buttonUpdateGroup;
    }

    public void setButtonUpdateGroup(Button buttonUpdateGroup) {
        this.buttonUpdateGroup = buttonUpdateGroup;
    }

    // ����һ����Ҹ�����Χ��
    protected Button buttonUpdateGroup;
    
    protected Button buttonMonsterAdd;

    // �������ѡ�� 1.���� 2.������
    protected Combo comboType;
    
    protected Text textChaseDistance;
    protected Text textEyeshot;
    protected Text textSpeed;
    protected Text textWalkSpeed;
    protected Text textBattleDistance;
    
    public class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
                
        public String getColumnText(Object element, int columnIndex) {
            if(element instanceof Monster) {
                Monster m = (Monster) element;
                switch (columnIndex) {
                    case 0:
                        return "����ID:" + String.valueOf(m.monsterId);
                    case 1:
                        return m.title;
                    case 2:
                        return String.valueOf(m.rate);
                    case 3:
                        return m.isPlayerSide ? "TRUE" : "FALSE";
                    case 4:
                        return String.valueOf(m.position);
                }
            } else  if(element instanceof MonsterGroup) {
                MonsterGroup m = (MonsterGroup) element;
                switch (columnIndex) {
                    case 0:
                        return "������ID:" + String.valueOf(m.id);
                    case 1:
                        return m.title;
                    case 2:
                        return String.valueOf(m.rate);
                    case 3:
                        return "FALSE";
                }
            }

            return "";
        }
    }

    public class ContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            if(inputElement instanceof SubMonsterGroup) {
                SubMonsterGroup subMonsterGroup = (SubMonsterGroup)inputElement;
                
                //�ϲ���������
                Object[] ret = new Object[subMonsterGroup.monsterGroup.size() + subMonsterGroup.monsterGrp.size()];          
                if(ret.length > 0) {
                    if(subMonsterGroup.monsterGroup.size() > 0) {
                        System.arraycopy(subMonsterGroup.monsterGroup.toArray(), 0, ret, 0, subMonsterGroup.monsterGroup.size());
                    }
                    
                    if(subMonsterGroup.monsterGrp.size() > 0) {
                        System.arraycopy(subMonsterGroup.monsterGrp.toArray(), 0, ret, subMonsterGroup.monsterGroup.size(), subMonsterGroup.monsterGrp.size());    
                    }                    
                          
                    return ret;  
                }
                return new Object[]{};
            }
            return new Object[]{};
            
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    public class MyCellModifier implements ICellModifier{  
        private TableViewer tv;
        private Table table;
         
        public MyCellModifier(TableViewer tv) {
            this.tv = tv;
        }  
        public boolean canModify(Object element, String property) {
            if(property.equals("c3") || property.equals("c4") || property.equals("c5"))
                return true;
            return false;
        }  
  
        public Object getValue(Object element, String property) {  
            if(element instanceof Monster) {
                Monster monster = (Monster)element;
                if("c1".equals(property)) {
                    return String.valueOf(monster.monsterId);
                } else if("c2".equals(property)) {
                    return monster.title;
                } else if("c3".equals(property)) {
                    return String.valueOf(monster.rate);
                } else if("c4".equals(property)) {
                    return new Boolean(monster.isPlayerSide);
                } else if("c5".equals(property)) {
                    return new Integer(monster.position);
                }
            } else if(element instanceof MonsterGroup) {
                MonsterGroup mg = (MonsterGroup)element;
                if("c1".equals(property)) {
                    return String.valueOf(mg.id);
                } else if("c2".equals(property)) {
                    return mg.title;
                } else if("c3".equals(property)) {
                    return String.valueOf(mg.rate);
                } else if("c4".equals(property)) {
                    return new Boolean(false);
                } else if("c5".equals(property)) {
                    return new Integer(0);
                }
            }

            return null;
        }
  
        public void modify(Object element, String property, Object value) {
            TableItem item = (TableItem) element;
            Object data = item.getData();
            if(data instanceof Monster) {
                Monster monster = ((Monster)data);
                if("c3".equals(property)) {
                    try {
                        int rate = Integer.parseInt(value.toString());
                        if(rate != monster.rate) {
                            monster.rate = rate;
                            setDirty(true);
                        }
                      
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                } else if("c4".equals(property)) {
                    try {
                        boolean isPlayerSide = Boolean.parseBoolean(value.toString());
                        if(isPlayerSide) {
                            monster.isPlayerSide = true;
                        } else {
                            monster.isPlayerSide = false;
                        }
                        setDirty(true);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                } else if("c5".equals(property)) {
                    try {
                        int position = Integer.parseInt(value.toString());
                        monster.position = position;
                        setDirty(true);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
               
            } else if(data instanceof MonsterGroup) {
                MonsterGroup mg = ((MonsterGroup)data);
                
                try {
                    int rate = Integer.parseInt(value.toString());
                    if(rate != mg.rate) {
                        mg.rate = rate;
                        setDirty(true);
                    }
                  
                }catch(Exception e) {
                    
                }
            }

            //tv.update(item.getData(), null);            
            tv.refresh(data);
            
        }  
         
    }  
    
    public class ItemTreeLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return super.getText(element);
        }

        public Image getImage(Object element) {
            if (element instanceof DataObjectCategory) {
                return EditorPlugin.getDefault().getImageRegistry().get("itemtype");
            } else if (element instanceof NPCTemplate) {
                return EditorPlugin.getDefault().getImageRegistry().get("item");
            }
            return null;
        }
    }

    public class ItemTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object inputElement) {
            ProjectData proj = ProjectData.getActiveProject();
            ArrayList cates = null;
            if(inputElement instanceof Integer) {
                switch (((Integer)inputElement).intValue()) {
                    case 0:
                        cates = new ArrayList();
                        proj.getAllMonsterNpc(cates);
                        break;
                    case 1:
                        cates = (ArrayList)proj.getCategoryListByType(MonsterGroup.class);
                        break;
                }
            }

            return cates.toArray();
        }

        public Object[] getChildren(Object element) {
            ProjectData proj = ProjectData.getActiveProject();
            if (element instanceof DataObjectCategory) {
                DataObjectCategory cate = (DataObjectCategory)element;
                //�ϲ���������
                Object[] ret = new Object[cate.objects.size() + cate.cates.size()];
                System.arraycopy(cate.objects.toArray(), 0, ret, 0, cate.objects.size());
                System.arraycopy(cate.cates.toArray(), 0, ret, cate.objects.size(), cate.cates.size());
                return ret;
            }
            return null;

        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return getChildren(element) != null;
        }
    }
    
    public MonsterGroupEditor() {
        String monsterPostionDesc = ProjectData.getActiveProject().config.monsterPositionDescription;
        if(monsterPostionDesc != null){
            positionList = Utils.splitString(monsterPostionDesc, ',');
        }
    }

    /**
     * editor��ʼ��
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        editorObj = (MonsterGroup) super.editObject;
    }
    
    public void createPartControl(Composite parent) {
        
        parent.setLayout(new GridLayout(8, false));
        {
            Label lblId = new Label(parent, SWT.NONE);
            lblId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            lblId.setText("ID: ");
        }
        {
            textID = new Text(parent, SWT.BORDER);
            {
                GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
                gridData.widthHint = 11;
                textID.setLayoutData(gridData);
            }
        }
        {
            Label label = new Label(parent, SWT.NONE);
            label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            label.setText("����: ");
        }
        {
            textTitle = new Text(parent, SWT.BORDER);
            {
                GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
                gridData.widthHint = 332;
                textTitle.setLayoutData(gridData);                
            }
        }
        {
            Label label = new Label(parent, SWT.NONE);
            label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            label.setText("͵Ϯ����ս��: ");
            canCheckout = new Button(parent, SWT.CHECK);
            canCheckout.addSelectionListener(this);
        }
        
        final SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL | SWT.FILL);        
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));
        
        //����ȼ�
        final Composite compositeLevel = new Composite(sashForm, SWT.NONE);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 4;
        compositeLevel.setLayout(gridLayout_1);
        
        final Label label_5 = new Label(compositeLevel, SWT.NONE);
        label_5.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
        label_5.setText("��Ҹ�����");
        
        playerCountRangeList = new ListViewer(compositeLevel, SWT.BORDER);
        playerCountRangeList.setContentProvider(new ListContentProvider());
        playerCountRangeList.addSelectionChangedListener(this);
        playerCountRangeList.setInput(editorObj);

        levelList = playerCountRangeList.getList();
        {
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, true, 4, 1);
            gridData.heightHint = 513;
            gridData.widthHint = 68;
            levelList.setLayoutData(gridData);

        }        

        final Label label_6 = new Label(compositeLevel, SWT.NONE);
        label_6.setText("��Ҹ�����");

        textCountMin = new Text(compositeLevel, SWT.BORDER);
        textCountMin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label label_13 = new Label(compositeLevel, SWT.NONE);
        label_13.setText("-");

        textCountMax = new Text(compositeLevel, SWT.BORDER);
        textCountMax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
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
                
        //���õĹ���
        final Composite compositeMonsterGroup = new Composite(sashForm, SWT.NONE);
        final GridLayout gridLayout_4 = new GridLayout();
        gridLayout_4.numColumns = 4;
        compositeMonsterGroup.setLayout(gridLayout_4);
        {
            Label label = new Label(compositeMonsterGroup, SWT.NONE);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
            label.setText("�������б�: ");
        }        
        {
            tableViewer = new TableViewer(compositeMonsterGroup, SWT.FULL_SELECTION);
            tableViewer.setLabelProvider(new TableLabelProvider());
            tableViewer.setContentProvider(new ContentProvider());            
            tableMonsters = tableViewer.getTable();
            tableMonsters.setLinesVisible(true);
            tableMonsters.setHeaderVisible(true);
            tableMonsters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
            
            tableMonsters.addKeyListener(new KeyAdapter(){
                public void keyReleased(KeyEvent e) {
                    if(e.keyCode == SWT.DEL){
                        //ɾ��
                        SubMonsterGroup selSmg = (SubMonsterGroup)tableViewer.getInput();
                        //selSmg.monsterGroup.remove(tableMonsters.getSelectionIndex());
                        
                        Object selData = tableViewer.getElementAt(tableMonsters.getSelectionIndex());
                        if(selData instanceof Monster) {
                            selSmg.monsterGroup.remove((Monster)selData);
                        } else if(selData instanceof MonsterGroup) {
                            selSmg.monsterGrp.remove((MonsterGroup)selData);
                        }
                        
                        tableViewer.refresh();
                        setDirty(true);
                    }
                }
            });
            
            {
                TableColumn columnMonsterId = new TableColumn(tableMonsters, SWT.NONE);
                columnMonsterId.setResizable(false);
                columnMonsterId.setWidth(85);
                columnMonsterId.setText("����ID");
            }
            {
                TableColumn columnTitle = new TableColumn(tableMonsters, SWT.NONE);
                columnTitle.setResizable(false);
                columnTitle.setWidth(129);
                columnTitle.setText("��������");
            }
            {
                TableColumn columnRate = new TableColumn(tableMonsters, SWT.NONE);
                columnRate.setWidth(100);
                columnRate.setText("���ּ���");
            }
            {
                TableColumn columnPlayer = new TableColumn(tableMonsters, SWT.NONE);
                columnPlayer.setWidth(100);
                columnPlayer.setText("�����һ��");
            }
            {
                TableColumn columnRate = new TableColumn(tableMonsters, SWT.NONE);
                columnRate.setWidth(100);
                columnRate.setText("վλ");
            }
            {
                CellEditor[] eds = new CellEditor[5];
                eds[0] = new TextCellEditor(tableMonsters);
                eds[1] = new TextCellEditor(tableMonsters);
                eds[2] = new TextCellEditor(tableMonsters);
                eds[3] = new CheckBoxCellEditor(tableMonsters);
                eds[4] = new ComboBoxCellEditor(tableMonsters, positionList);
                positionCombo = (CCombo)eds[4].getControl();
                positionCombo.setEditable(false);
                
                String[] pros = {"c1","c2","c3","c4", "c5"};
                tableViewer.setColumnProperties(pros);
                tableViewer.setCellEditors(eds);
                tableViewer.setCellModifier(new MyCellModifier(tableViewer));
                
                final Text text = (Text) eds[2].getControl();// ���õ�3��ֻ��������ֵ
                text.addVerifyListener(new VerifyListener() {
                    public void verifyText(VerifyEvent e) {
                        String inStr = e.text;
                        if (inStr.length() > 0) {
                            try {
                                Integer.parseInt(inStr);
                                e.doit = true;
                            }catch(Exception ex) {
                                e.doit = false;
                            }
                        } else {
                            e.doit = false;
                        }
                    }
                });
                tableViewer.setInput(new Object());
            }
        }
        final Label label_133 = new Label(compositeMonsterGroup, SWT.NONE);
        label_133.setText("�ٶ�(����/��)��");

        textSpeed = new Text(compositeMonsterGroup, SWT.BORDER);
        textSpeed.setText(String.valueOf(editorObj.speed));
        final GridData gd_textSpeed = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textSpeed.setLayoutData(gd_textSpeed);
        textSpeed.addFocusListener(AutoSelectAll.instance);
        textSpeed.addModifyListener(this);        

        final Label label_26 = new Label(compositeMonsterGroup, SWT.NONE);
        label_26.setText("Ѳ���ٶȣ�");

        textWalkSpeed = new Text(compositeMonsterGroup, SWT.BORDER);
        textWalkSpeed.setText(String.valueOf(editorObj.walkSpeed));
        final GridData gd_textWalkSpeed = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textWalkSpeed.setLayoutData(gd_textWalkSpeed);
        textWalkSpeed.addFocusListener(AutoSelectAll.instance);
        textWalkSpeed.addModifyListener(this);        

        final Label label_14 = new Label(compositeMonsterGroup, SWT.NONE);
        label_14.setLayoutData(new GridData());
        label_14.setText("��Ұ(��)��");

        textEyeshot = new Text(compositeMonsterGroup, SWT.BORDER);
        textEyeshot.setText(String.valueOf(editorObj.eyeshot / 8.0));
        final GridData gd_textEyeshot = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textEyeshot.setLayoutData(gd_textEyeshot);
        textEyeshot.addFocusListener(AutoSelectAll.instance);
        textEyeshot.addModifyListener(this);        

        final Label label_15 = new Label(compositeMonsterGroup, SWT.NONE);
        label_15.setLayoutData(new GridData());
        label_15.setText("׷������(��)��");

        textChaseDistance = new Text(compositeMonsterGroup, SWT.BORDER);
        textChaseDistance.setText(String.valueOf(editorObj.chaseDistance / 8.0));
        final GridData gd_textChaseDistance = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        textChaseDistance.setLayoutData(gd_textChaseDistance);
        textChaseDistance.addFocusListener(AutoSelectAll.instance);
        textChaseDistance.addModifyListener(this);        
        
        final Label label_155 = new Label(compositeMonsterGroup, SWT.NONE);
        label_155.setLayoutData(new GridData());
        label_155.setText("ս����Χ(��)��");

        textBattleDistance = new Text(compositeMonsterGroup, SWT.BORDER);
        textBattleDistance.setText(String.valueOf(editorObj.battleDistance / 8.0));
        final GridData gd_textBattleDistance = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        textBattleDistance.setLayoutData(gd_textBattleDistance);
        textBattleDistance.addFocusListener(AutoSelectAll.instance);
        textBattleDistance.addModifyListener(this);
        
        //ѡ����ӵĹ���Npc        
        final Composite compositeNpcList = new Composite(sashForm, SWT.NONE);
        final GridLayout gridLayout_6 = new GridLayout();
        gridLayout_6.numColumns = 2;
        compositeNpcList.setLayout(gridLayout_6);
        
        comboType = new Combo(compositeNpcList, SWT.READ_ONLY);
        comboType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        comboType.setItems(COMBO_ITEM_TYPE);
        comboType.select(0);
        comboType.addSelectionListener(this);
        
        {
            Label label = new Label(compositeNpcList, SWT.NONE);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
            label.setText("�����б�:(ֻ��ʾ����Ϊ�����NPCģ��)");
            label.setToolTipText("������ģ������ͺ�������ѡ���������Ա�ˢ��");
        }
        {
            itemTreeViewer = new ItemTreeViewer(compositeNpcList, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
            itemTreeViewer.setLabelProvider(new ItemTreeLabelProvider());
            itemTreeViewer.setContentProvider(new ItemTreeContentProvider());
            itemTree = itemTreeViewer.getTree();
            final GridData gd_itemTree = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
            itemTree.setLayoutData(gd_itemTree);
            itemTreeViewer.setInput(0);
            
            
            itemTree.addMouseListener(new MouseListener() {
                public void mouseDoubleClick(MouseEvent e) {
                    // ���
                    onAddItem();
                }

                public void mouseDown(MouseEvent e) {
                }

                public void mouseUp(MouseEvent e) {
                }
            });
        }
        {
            buttonMonsterAdd = new Button(compositeNpcList, SWT.NONE);
            buttonMonsterAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
            buttonMonsterAdd.setText("���");
            buttonMonsterAdd.addSelectionListener(this);
        }
        sashForm.setWeights(new int[] { 154, 482, 285 });
        
        init();
        textTitle.addModifyListener(this);
        setPartName(this.getEditorInput().getName());
        
        if(levelList.getItemCount() > 0) {
            levelList.setSelection(0);
            setSubMonsterGroup((SubMonsterGroup)playerCountRangeList.getElementAt(0));
        }
        
        tableViewer.refresh();
    }
    
    protected void init() {
        MonsterGroup obj = (MonsterGroup) editObject;
        textID.setText(String.valueOf(obj.id));
        textID.setEditable(false);
        textTitle.setText(obj.title);
        
         canCheckout.setSelection(obj.canCheckout);
//        comboLevel.select(obj.groupLevel - 1);
    }

    protected void saveData() throws Exception {
        MonsterGroup mg = (MonsterGroup)editObject;

        
        //У�����վλ
        
        IStructuredSelection selected = (IStructuredSelection) playerCountRangeList.getSelection();
        SubMonsterGroup subGroup = (SubMonsterGroup) selected.getFirstElement();
        if (subGroup != null) {
            int count = tableMonsters.getItemCount();
            subGroup.monsterGroup.clear();
            subGroup.monsterGrp.clear();
            
            boolean isSetPosition = false; //�Ƿ����õ�վλ
            boolean isContainMonsterGroup = false; //�Ƿ�����˹�����
            boolean hasPosition0 = false; //���������վλ���Ͳ�����Ĭ��ֵ
            boolean hasDuplicatePos = false; //�Ƿ�����ظ�վλ������
            HashSet<Integer> posSet = new HashSet<Integer>();
            for(int i =0;i<count;i++){
                TableItem tableItem = tableMonsters.getItem(i);
                Object data = tableItem.getData();                
                if(data instanceof Monster) {                    
                    Monster m= (Monster)data;
                    subGroup.monsterGroup.add(m);
                    if(m.position != 0) {
                        isSetPosition = true;
                        
                        if(posSet.contains(m.position)) {
                            hasDuplicatePos = true;
                        } else {
                            posSet.add(m.position);
                        }
                    } else {
                        hasPosition0 = true;
                    }
                } else if (data instanceof MonsterGroup) {
                    MonsterGroup mgrp = (MonsterGroup)data;
                    subGroup.monsterGrp.add(mgrp);
                    isContainMonsterGroup = true;
                }              
            }
            if(isSetPosition) {
                if(isContainMonsterGroup) {
                    //������վλ���Ͳ�������������������ˣ�����վλ���޷�����
                    throw new Exception("������վλ���������ڹ��������������������");
                } else if(hasPosition0) {
                    throw new Exception("ֻҪ������һ����0��վλ����������վλ���������÷�0վλ");
                } else if(hasDuplicatePos) {
                    //�������õ�վλ�����ظ�
                    throw new Exception("���������ظ�վλ����");
                }
            }
            
            try {
                mg.speed = Integer.parseInt(textSpeed.getText());
            } catch (Exception e) {
                throw new Exception("��������ȷ���ٶȡ�");
            }
            try {
                mg.walkSpeed = Integer.parseInt(textWalkSpeed.getText());
            } catch (Exception e) {
                throw new Exception("��������ȷ���ٶȡ�");
            }
            try {
                mg.eyeshot = (int)(Double.parseDouble(textEyeshot.getText()) * 8);
            } catch (Exception e) {
                throw new Exception("��������ȷ����Ұ��");
            }
            try {
                mg.chaseDistance = (int)(Double.parseDouble(textChaseDistance.getText()) * 8);
            } catch (Exception e) {
                throw new Exception("��������ȷ��׷�����롣");
            }
            
            try {
                mg.battleDistance = (int)(Double.parseDouble(textBattleDistance.getText()) * 8);
            } catch (Exception e) {
                throw new Exception("��������ȷ��ս����Χ��");
            }
            
            mg.id = Integer.parseInt(textID.getText());
            mg.title = textTitle.getText();
            
            mg.canCheckout = canCheckout.getSelection();
//            mg.groupLevel = comboLevel.getSelectionIndex() + 1;
        }
        
    }

    protected Object saveState() {
        editObject.title = textTitle.getText();
        return editObject.save();
    }
    
    /**
     * �����¼�����
     */
    public void doSave(IProgressMonitor monitor) {
        // Do the Save operation
        try {
            saveData();
            // ����������Բ�����XML�ļ�
            saveTarget.update(editObject);
            ProjectData.getActiveProject().saveDataList(MonsterGroup.class);
            setDirty(false);
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            DataListView view = (DataListView)page.findView(DataListView.ID);
            view.refresh(saveTarget);
        } catch (Exception e) {
            MessageDialog.openError(getSite().getShell(), "����", e.toString());
            monitor.setCanceled(true);
        }
    }

    protected void loadState(Object stateObj) {
    }

    public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void widgetSelected(SelectionEvent e) {
        try {
            if (e.getSource() == buttonAddGroup) {
                /* �����Ҹ�����Χ */
                onAddGroup();
            } else if (e.getSource() == buttonDelGroup) {
                /* ɾ����Ҹ�����Χ */
                onDeleteGroup();
            } else if (e.getSource() == buttonUpdateGroup) {
                /* ������Ҹ�����Χ */
                onUpdateGroup();
            } else if (e.getSource() == comboType) {
                /* ��������޸ģ������б� */
                if(comboType.getSelectionIndex() == 0) {
                    itemTreeViewer.setInput(new Integer(comboType.getSelectionIndex()));
                } else {
                    itemTreeViewer.setInput(new Integer(comboType.getSelectionIndex()));
                }
            } else if (e.getSource() == buttonMonsterAdd) {
                /* ��ӹ��� */
                onAddItem();
            } else if(e.getSource() == canCheckout) {
                setDirty(true);
            }
        } catch (Exception e1) {
            showMessage("����\n" + e1.toString());
        }
        
    }
    
    /**
     * ִ�������Ҹ�����Χ��Ϣ����
     */
    protected void onAddGroup() {
        int countMin = Integer.parseInt(textCountMin.getText());
        int countMax = Integer.parseInt(textCountMax.getText());

        MonsterGroup group = (MonsterGroup) editObject;

        if (countMin > countMax) {
            showMessage("�������Ҹ�������С�����ޣ�");
            return;
        }

        if (countMin < MonsterGroup.COUNT_MIN || countMax > MonsterGroup.COUNT_MAX) {
            showMessage("��Ҹ����������Ʒ�Χ��");
            return;
        }
        
        if (!group.isRangeValid(countMin, countMax, -1)) {
            showMessage("��Ҹ�����Χ�������غϣ�");
            return;
        }
        
        onAddGroupReslut(countMax, countMin, group);
    }
    
    public void onAddGroupReslut(int levelMax, int levelMin, MonsterGroup group){
        SubMonsterGroup sub = new SubMonsterGroup(null);
        sub.countMax = levelMax;
        sub.countMin = levelMin;
        group.subMonsterGroup.add(sub);
                        
        playerCountRangeList.setSelection(new StructuredSelection(sub));

        playerCountRangeList.setInput(group);
        
        levelList.setSelection(levelList.getItemCount() - 1);
        
        playerCountRangeList.refresh();
        
        tableViewer.setInput(sub);
        tableViewer.refresh();
        
        setDirty(true);
        
    }
    /**
     * ɾ����Ҹ�����Χ��Ϣ����
     */
    protected void onDeleteGroup() {
        IStructuredSelection selected = (IStructuredSelection) playerCountRangeList.getSelection();
        SubMonsterGroup subGroup = (SubMonsterGroup) selected.getFirstElement();
        int oldSelIndex = levelList.getSelectionIndex();
        if (subGroup != null) {
            ((MonsterGroup) editObject).subMonsterGroup.remove(subGroup);
            playerCountRangeList.refresh();
            playerCountRangeList.setSelection(new StructuredSelection());

            if(oldSelIndex > levelList.getItemCount() - 1) {
                levelList.setSelection(0);
                tableViewer.setInput(playerCountRangeList.getElementAt(0));
            } else {
                levelList.setSelection(oldSelIndex);
                tableViewer.setInput(playerCountRangeList.getElementAt(oldSelIndex));
            }
            
            setDirty(true);
        }
    }

    /**
     * ������Ҹ�����Χ��Ϣ����
     */
    protected void onUpdateGroup() {
        IStructuredSelection selected = (IStructuredSelection) playerCountRangeList.getSelection();
        int index = playerCountRangeList.getList().getSelectionIndex();
        SubMonsterGroup subGroup = (SubMonsterGroup) selected.getFirstElement();
        if (subGroup == null) {
            return;
        }
        
        int countMin = Integer.parseInt(textCountMin.getText());
        int countMax = Integer.parseInt(textCountMax.getText());

        if (countMin > countMax) {
            showMessage("�������Ҹ�������С�����ޣ�");
            return;
        }

        /* �ж���Ҹ�����û�г���ϵͳ���� */
        if (countMin < MonsterGroup.COUNT_MIN || countMax > MonsterGroup.COUNT_MAX) {
            showMessage("��Ҹ����������Ʒ�Χ��");
            return;
        }

        /* �ж���Ҹ�����Χ�Ƿ����غ� */
        MonsterGroup group = (MonsterGroup) editObject;
        if (!group.isRangeValid(countMin, countMax, index)) {
            showMessage("��Ҹ�����Χ�������غϣ�");
            return;
        }

        subGroup.countMax = countMax;
        subGroup.countMin = countMin;
        
        playerCountRangeList.refresh();

        setDirty(true);
    }
    
    /**
     * ���һ��������
     */
    protected void onAddItem() {
        // ����ѡ�е��ӹ�����
        IStructuredSelection selSubGroup = (IStructuredSelection) playerCountRangeList.getSelection();
        SubMonsterGroup selection = (SubMonsterGroup) selSubGroup.getFirstElement();
        if (selection == null) {
            showMessage("��ѡ��һ����Ч����Ҹ�����Χ��");
            return;
        }

        StructuredSelection sel = (StructuredSelection)itemTreeViewer.getSelection();
        Iterator itor = sel.iterator();
        while (itor.hasNext()) {
            Object obj = itor.next();
            if (obj instanceof NPCTemplate || obj instanceof MonsterGroup) {
                try {
                    tryAddDropItem((MonsterGroup)editObject, selection, (DataObject)obj);
                    setDirty(true);
                } catch (Exception e) {
                    showMessage(e.getMessage());
                }
            }
        }
        
        tableViewer.refresh();
        
    }
    
    /*
     * ��ǰѡ�е��ӹ����������һ��������Ʒ��
     * @param group ������
     * @param owner �ӹ�����
     * @param type ������Ŀ����
     * @param obj ����
     */
    private void tryAddDropItem(MonsterGroup group, SubMonsterGroup owner, DataObject obj) throws Exception {
        if (obj instanceof MonsterGroup) {
            if (!group.isGroupValid((MonsterGroup)obj)) {
                throw new Exception("�еݹ���õĹ����飡");
            }
        }

        if(obj instanceof NPCTemplate) {
            Monster m = owner.getNewMonsterItem();
            NPCTemplate npc = (NPCTemplate)obj;
            
            m.monsterId = npc.id;
            m.title = npc.title;
            m.rate = 100;
            m.npcTemplate = npc;
            owner.monsterGroup.add(m);
            tableViewer.add(m);
            
            m.monsterId = obj.id;
            
        } else if(obj instanceof MonsterGroup) {
            owner.monsterGrp.add((MonsterGroup)obj);
            tableViewer.add(obj);
        }
        
    }
    
    /**
     * ��ʾ������Ϣ��ʾ��
     * 
     * @param message
     */
    public void showMessage(String message) {
        MessageDialog.openError(getSite().getShell(), "��ʾ��", message);
    }
    
    /**
     * ��Ҹ�����Χ�б����������ṩ��
     */
    public class ListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof MonsterGroup) {
                MonsterGroup inputData = (MonsterGroup) inputElement;
                if(inputData.subMonsterGroup.size() > 0) {
                    return inputData.subMonsterGroup.toArray();
                }
                return new Object[]{};
                
            }
            return new Object[]{};
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    /**
     * �б�ѡ���¼�
     */
    public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection seleted = (IStructuredSelection) event.getSelection();
        if (event.getSource() == playerCountRangeList) {
            /* ��Ҹ�����Χѡ���б� */
            SubMonsterGroup subMonsterGroup = (SubMonsterGroup) seleted.getFirstElement();
            setSubMonsterGroup(subMonsterGroup);
        }
    }
    
    /**
     * �����û�ָ��������ʾ��ǰָ����Ҹ�����Χ�ĵ�����
     * 
     * @param subDrop
     */
    protected void setSubMonsterGroup(SubMonsterGroup subMonsterGroup) {
        if(subMonsterGroup != null) {
            textCountMax.setText(String.valueOf(subMonsterGroup.countMax));
            textCountMin.setText(String.valueOf(subMonsterGroup.countMin));

            tableViewer.setInput(subMonsterGroup);
        }

    }
}