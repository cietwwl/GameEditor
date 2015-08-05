package com.pip.game.editor;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import scryer.ogre.material.MaterialConfig;
import scryer.ogre.material.MaterialGroup;
import scryer.ogre.mesh.MeshConfig;
import scryer.ogre.ps.ParticleSystemManager;

import com.pip.game.data.Currency;
import com.pip.game.data.DataObject;
import com.pip.game.data.GameMesh;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.NPCType;
import com.pip.game.data.ProjectData;
import com.pip.game.data.item.DropGroup;
import com.pip.game.data.item.DropItem;
import com.pip.game.data.item.DropNode;
import com.pip.game.data.quest.Quest;
import com.pip.game.editor.property.ChooseAIDialog;
import com.pip.game.editor.property.ChooseDropGroupDialog;
import com.pip.game.editor.property.ChooseMultiQuestDialog;
import com.pip.game.editor.util.SpriteChooser;
import com.pip.util.AutoSelectAll;

/**
 * 通用NPC模板编辑器，只包含NPC模板的基本属性，项目扩展属性通过扩展的AbstractDataObjectEditor派生类来实现。
 * 
 * @author lighthu
 */
public class NPCTemplateEditor extends DefaultDataObjectEditor {
    public static final String ID = "com.pip.game.editor.NPCTemplateEditor"; //$NON-NLS-1$

    /**
     * NPC类型列表。
     */
    public class NPCTypeContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return ((ProjectData) inputElement).getDictDataListByType(NPCType.class).toArray();
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    /**
     * 通用表数据源，在本类中用于掉落组列表。
     */
    class GenericTableContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                return ((List) inputElement).toArray();
            }
            return new Object[0];
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    /**
     * 掉落组列表文字显示。
     */
    class DropNodeLabelProvider implements ITableLabelProvider {
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof DropNode) {
                DropNode node = (DropNode) element;
                switch (columnIndex) {
                case 0: {/* 类型 */
                    switch (((DropNode) element).type) {
                    case DropItem.DROP_TYPE_DROPGROUP:
                        return "掉落组";
                    case DropItem.DROP_TYPE_EQUI:
                        return "装备";
                    case DropItem.DROP_TYPE_ITEM:
                        return "物品";
                    case DropItem.DROP_TYPE_MONEY:
                        return "金钱";
                    case DropItem.DROP_TYPE_EXP:
                        return "经验";
                    default:
                        // 扩展货币掉落
                        Currency c = (Currency)ProjectData.getActiveProject().findDictObject(Currency.class, ((DropNode)element).type);
                        return c.title;
                    }
                }
                case 1: {/* 名称 */
                    if (node.type == DropItem.DROP_TYPE_ITEM) {
                        return ProjectData.getActiveProject().findItem(node.id).toString();
                    }
                    else if (node.type == DropItem.DROP_TYPE_DROPGROUP) {
                        return ProjectData.getActiveProject().findObject(DropGroup.class, node.id).toString();
                    }
                    else if (node.type == DropItem.DROP_TYPE_EQUI) {
                        return ProjectData.getActiveProject().findEquipment(node.id).toString();
                    }
    
                    return "";
                }
                case 2: {/* 掉落几率 */
                    return String.valueOf(node.getRateString());
                }
                case 3: {/* 掉落数量 */
                    return node.quantityMin + "-" + node.quantityMax;
                }
                case 4: {/* 任务掉落 */
                    return node.isTask ? "是" : "否";
                }
                case 5: {/* 相关任务 */
                    DataObject quest = ProjectData.getActiveProject().findObject(Quest.class, node.taskId);
                    if (quest != null) {
                        return quest.toString();
                    }
                    break;
                }
                case 6: {/* 是否复制 */
                    return node.copy ? "是" : "否";
                }
                }
            }
            return "";
        }

        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
        }
    }

    protected Text textID;
    protected Text textTitle;
    protected Text textDescription;
    public Combo comboClazz;
    public Combo comboLevel;

    private SpriteChooser spriteChooser;
    public ComboViewer comboType;
    protected Combo comboTypeCtrl;
    protected Text aiText;
    protected Button buttonAI;
    public Combo comboDifficulty;
    private Text textQuest;
    public Combo comboMaterial;

    private TableViewer dropGroupTable;
    protected Table table;
    private Action addDropGroup;
    private Action delDropGroup;
    private Combo comboAnalyzeClazz;
    private Combo comboAnalyzeLevel;
    

    protected ScrolledComposite containerScroll;
    private Button buttonChangePass;
    private Button buttonIsRandomRefresh;
    
    public Group baseGroup;

    protected Composite container;
    /**
     * Create contents of the editor part
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        createActions();
        String[] levelItems = new String[ProjectData.getActiveProject().config.LEVEL_EXP.length];
        for (int i = 0; i < ProjectData.getActiveProject().config.LEVEL_EXP.length; i++) {
            levelItems[i] = String.valueOf(i);
        }

        containerScroll = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        containerScroll.setExpandHorizontal(true);
        containerScroll.setExpandVertical(true);

        container = new Composite(containerScroll, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        containerScroll.setContent(container);
        containerScroll.setMinSize(1000, 1500);

        // 基本属性编辑
        baseGroup = new Group(container, SWT.NONE);
        baseGroup.setText("基本属性");
        GridData baseGroup_gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2);
        baseGroup_gd.heightHint = 405;
        baseGroup.setLayoutData(baseGroup_gd);
        GridLayout baseLayout = new GridLayout();
        baseLayout.numColumns = 5;
        baseGroup.setLayout(baseLayout);

        final Label label = new Label(baseGroup, SWT.NONE);
        label.setLayoutData(new GridData());
        label.setText("ID：");

        textID = new Text(baseGroup, SWT.BORDER);
        final GridData gd_textID = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        textID.setLayoutData(gd_textID);
        textID.addFocusListener(AutoSelectAll.instance);
        textID.addModifyListener(this);

        final Label label_1 = new Label(baseGroup, SWT.NONE);
        label_1.setLayoutData(new GridData());
        label_1.setText("名称：");

        textTitle = new Text(baseGroup, SWT.BORDER);
        final GridData gd_textTitle = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        textTitle.setLayoutData(gd_textTitle);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);

        final Label label_2 = new Label(baseGroup, SWT.NONE);
        label_2.setLayoutData(new GridData());
        label_2.setText("描述：");

        textDescription = new Text(baseGroup, SWT.BORDER);
        final GridData gd_textDescription = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
        textDescription.setLayoutData(gd_textDescription);
        textDescription.addFocusListener(AutoSelectAll.instance);
        textDescription.addModifyListener(this);

        final Label label_3 = new Label(baseGroup, SWT.FILL);
        label_3.setLayoutData(new GridData());
        label_3.setText("类型：");

        comboType = new ComboViewer(baseGroup, SWT.READ_ONLY);
        comboType.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                NPCTemplate npc = (NPCTemplate)getEditObject();
                npc.type = (NPCType)((StructuredSelection)comboType.getSelection()).getFirstElement();
                setDirty(true);
            }
        });
        comboType.setContentProvider(new NPCTypeContentProvider());
        comboTypeCtrl = comboType.getCombo();
        comboTypeCtrl.setVisibleItemCount(10);
        comboTypeCtrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboType.setInput(ProjectData.getActiveProject());

        final Label label_7 = new Label(baseGroup, SWT.NONE);
        label_7.setLayoutData(new GridData());
        label_7.setText("级别：");

        comboLevel = new Combo(baseGroup, SWT.READ_ONLY);
        comboLevel.setVisibleItemCount(50);
        comboLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        comboLevel.setItems(levelItems);
        comboLevel.addModifyListener(this);
        comboLevel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                NPCTemplate npc = (NPCTemplate)getEditObject();
                npc.level = comboLevel.getSelectionIndex();
                setDirty(true);
            }
        });

        final Label label_21 = new Label(baseGroup, SWT.NONE);
        label_21.setLayoutData(new GridData());
        label_21.setText("职业：");

        comboClazz = new Combo(baseGroup, SWT.READ_ONLY);
        comboClazz.setItems(ProjectData.getActiveProject().config.PLAYER_CLAZZ_RAW);
        final GridData gd_comboClazz = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboClazz.setLayoutData(gd_comboClazz);
        comboClazz.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                NPCTemplate npc = (NPCTemplate)getEditObject();
                npc.clazz = comboClazz.getSelectionIndex();
                setDirty(true);
            }
        });
        
        final Label label_22 = new Label(baseGroup, SWT.NONE);
        label_22.setLayoutData(new GridData());
        label_22.setText("难度：");

        comboDifficulty =  new Combo(baseGroup, SWT.READ_ONLY);
        comboDifficulty.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        // 新加了3个难度，涉及到的项目需要在NPCAttribute中添加新的值
        comboDifficulty.setItems(new String[] { "简单", "普通", "中等", "困难", "精英" , "特殊", "头领", "魔王"});
        comboDifficulty.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent event) {
                NPCTemplate npc = (NPCTemplate)getEditObject();
                npc.difficulty = comboDifficulty.getSelectionIndex();
                setDirty(true);
            }
        });

//        final Label label_4 = new Label(baseGroup, SWT.NONE);
//        label_4.setLayoutData(new GridData());
//        label_4.setText("图片：");

        spriteChooser = new SpriteChooser(this, ParticleEffectManager.getPsManager());
        spriteChooser.createAnimationLabel(baseGroup, new GridData());
        spriteChooser.createAnimationChooser(baseGroup, new GridData(SWT.FILL, SWT.CENTER, true, false));
        spriteChooser.createMeshLabel(baseGroup, new GridData());
        spriteChooser.createMeshChooser(baseGroup, new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        
//        aniChooser = new AnimationChooser(baseGroup, SWT.NONE);
//        aniChooser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//        aniChooser.addModifyListener(new ModifyListener() {
//            public void modifyText(ModifyEvent e) {
//                updatePreviewer();
//            }
//        });
//        aniChooser.setHandler(this);
//
//        final Label label_5 = new Label(baseGroup, SWT.NONE);
//        label_5.setLayoutData(new GridData());
//        label_5.setText("模型：");
//
//        meshChooser = new MeshChooser(baseGroup, SWT.NONE);
//        meshChooser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
//        meshChooser.addModifyListener(new ModifyListener() {
//            public void modifyText(ModifyEvent e) {
//                updateMeshPreviewer();
//            }
//        });
//        meshChooser.setHandler(this);
        
        final Label label_31 = new Label(baseGroup, SWT.NONE);
        label_31.setText("AI：");

        aiText = new Text(baseGroup, SWT.BORDER);
        final GridData gd_textai = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        aiText.setLayoutData(gd_textai);
        aiText.setEnabled(false);
        aiText.addModifyListener(this);

        buttonAI = new Button(baseGroup, SWT.NONE);
        final GridData gd_buttonAI = new GridData();
        buttonAI.setLayoutData(gd_buttonAI);
        buttonAI.setText("...");
        buttonAI.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ChooseAIDialog dlg = new ChooseAIDialog(aiText.getShell());

                dlg.setSelAi(((NPCTemplate) getEditObject()).aiDataID);

                if (dlg.open() == Dialog.OK) {
                    if (dlg.getSelectedAIData() != null) {
                        aiText.setText("" + dlg.getSelectedAIData().id);
                    }
                }
                setDirty(true);

            }
        });
        final Label label_23 = new Label(baseGroup, SWT.NONE);
        label_23.setLayoutData(new GridData());
        label_23.setText("材质：");

        comboMaterial =  new Combo(baseGroup, SWT.READ_ONLY);
        comboMaterial.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        comboMaterial.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent event) {
                NPCTemplate npc = (NPCTemplate)getEditObject();
                npc.materialNameIndex = comboMaterial.getSelectionIndex();
                String materialName = comboMaterial.getItem(npc.materialNameIndex);
                spriteChooser.setMaterialName(materialName);
                npc.materialName = materialName;
                setDirty(true);
            }
        });
        
        spriteChooser.createPreviewer(baseGroup, new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        
        buttonChangePass = new Button(baseGroup, SWT.CHECK);
        buttonChangePass.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(final SelectionEvent e) {
            }
            public void widgetSelected(final SelectionEvent e) {
                setDirty(true);
            }
        });
        final GridData gd_buttonChangePass = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        buttonChangePass.setLayoutData(gd_buttonChangePass);
        buttonChangePass.setText("改变地图通过性");
        
        
        buttonIsRandomRefresh = new Button(baseGroup, SWT.CHECK);
        buttonIsRandomRefresh.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(final SelectionEvent e) {
            }
            public void widgetSelected(final SelectionEvent e) {
                setDirty(true);
            }
        });
        final GridData gd_buttonIsRandomRefresh = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
        buttonIsRandomRefresh.setLayoutData(gd_buttonIsRandomRefresh);
        buttonIsRandomRefresh.setText("是否随机刷新(采集npc有效)");
        
        

        final Label label_6 = new Label(baseGroup, SWT.NONE);
        label_6.setLayoutData(new GridData());
        label_6.setText("关联任务：");

        textQuest = new Text(baseGroup, SWT.BORDER);
        textQuest.setEditable(false);
        final GridData gd_textQuest = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textQuest.setLayoutData(gd_textQuest);

        final Button buttonQuest = new Button(baseGroup, SWT.NONE);
        buttonQuest.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                ChooseMultiQuestDialog dlg = new ChooseMultiQuestDialog(Display.getCurrent().getActiveShell());
                dlg.setSelectedQuest(textQuest.getText());
                if (dlg.open() == ChooseMultiQuestDialog.OK) {
                    textQuest.setText(dlg.getSelectedQuests());
                    setDirty(true);
                }
            }
        });
        buttonQuest.setText("...");

        // 掉落组编辑器
        final Group dropGroup = new Group(container, SWT.NONE);
        dropGroup.setText("掉落组"); 
        dropGroup.setLayout(new FillLayout());
        final GridData gd_dropGroup = new GridData(SWT.FILL, SWT.FILL, true, true); 
        gd_dropGroup.widthHint = 230;
        dropGroup.setLayoutData(gd_dropGroup);
        
        dropGroupTable = new TableViewer(dropGroup, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER); 
        table = dropGroupTable.getTable();
        table.setHeaderVisible(true); 
        table.setLinesVisible(true);
        dropGroupTable.setLabelProvider(new DropNodeLabelProvider());
        dropGroupTable.setContentProvider(new GenericTableContentProvider());
        dropGroupTable.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                IStructuredSelection sel = (IStructuredSelection)dropGroupTable.getSelection();
                if (!sel.isEmpty()) { 
                    onDoubleClick(sel); 
                }
            }
        });
        MenuManager mgr = new MenuManager(); 
        mgr.add(addDropGroup);
        mgr.add(delDropGroup); 
        Menu menu = mgr.createContextMenu(dropGroupTable.getControl());
        dropGroupTable.getControl().setMenu(menu);
        
        final TableColumn newColumnTableColumn = new TableColumn(table, SWT.CENTER); 
        newColumnTableColumn.setWidth(60);
        newColumnTableColumn.setText("类型");
        
        final TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.CENTER); 
        newColumnTableColumn_3.setWidth(150);
        newColumnTableColumn_3.setText("名称");
        
        final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.CENTER); 
        newColumnTableColumn_1.setWidth(80);
        newColumnTableColumn_1.setText("掉落几率");
         
        final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.CENTER); 
        newColumnTableColumn_2.setWidth(80);
        newColumnTableColumn_2.setText("掉落数量");
         
        final TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.CENTER); 
        newColumnTableColumn_4.setWidth(80);
        newColumnTableColumn_4.setText("任务掉落");
         
        final TableColumn newColumnTableColumn_5 = new TableColumn(table, SWT.CENTER); 
        newColumnTableColumn_5.setWidth(100);
        newColumnTableColumn_5.setText("相关任务");
         
        final TableColumn newColumnTableColumn_6 = new TableColumn(table, SWT.CENTER); 
        newColumnTableColumn_6.setWidth(80);
        newColumnTableColumn_6.setText("是否共享");
        
        final Composite composite2 = new Composite(container, SWT.NONE);
        composite2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false)); 
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 5; 
        composite2.setLayout(gridLayout_2);
        
        final Button dropAnalyseButton1 = new Button(composite2, SWT.NONE);
        dropAnalyseButton1.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                onAnalyzeDrop1(); 
            }
        });
        dropAnalyseButton1.setText("任务掉率分析");
        
        final Button dropAnalyseButton2 = new Button(composite2, SWT.NONE);
        dropAnalyseButton2.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                onAnalyzeDrop2();
            }
        }); 
        dropAnalyseButton2.setText("物品掉率分析");
        
        comboAnalyzeLevel = new Combo(composite2, SWT.READ_ONLY);
        comboAnalyzeLevel.setVisibleItemCount(50);
        comboAnalyzeLevel.setItems(levelItems);
        comboAnalyzeLevel.select(0); 
        final GridData gd_comboAnalyzeLevel = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboAnalyzeLevel.setLayoutData(gd_comboAnalyzeLevel);
        
        comboAnalyzeClazz = new Combo(composite2, SWT.READ_ONLY);
        comboAnalyzeClazz.setItems(ProjectData.getActiveProject().config.PLAYER_CLAZZ_RAW);
        comboAnalyzeClazz.select(0); 
        final GridData gd_comboAnalyzeClazz = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboAnalyzeClazz.setLayoutData(gd_comboAnalyzeClazz);
        
       
//        // 创建扩展属性编辑器
//        Class cls = ProjectData.getActiveProject().config.getExEditorbyClass(this.getEditObject().getClass());
//        if (cls != null) {
//            try {
//                Constructor c = cls.getConstructor(Composite.class, int.class, DefaultDataObjectEditor.class);
//                extendEditor = (AbstractDataObjectEditor)c.newInstance(exGroup, SWT.NONE, this);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

     
//        updateView();
        setDirty(false);
        setPartName(this.getEditorInput().getName());
        saveStateToUndoBuffer();
        
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    protected void updateView() {
        // 设置初始值
        NPCTemplate dataDef = (NPCTemplate) editObject;
        textID.setText(String.valueOf(dataDef.id));
        textTitle.setText(dataDef.title);
        textDescription.setText(dataDef.description);
        
        comboTypeCtrl.select(dataDef.type == null ? 0 : dataDef.owner.getDictObjectIndex(dataDef.type));
        spriteChooser.setSelectedObject(dataDef.image);
        final int fMaterialNameIndx = dataDef.materialNameIndex;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                spriteChooser.setMaterialName(getMaterialNameByIndex(fMaterialNameIndx));
            }
        },1000);
        comboLevel.select(dataDef.level);
        comboClazz.select(dataDef.clazz);
        comboDifficulty.select(dataDef.difficulty);
        aiText.setText(String.valueOf(dataDef.aiDataID));
        dropGroupTable.setInput(dataDef.dropGroups);
        buttonChangePass.setSelection(dataDef.changePath);
        buttonIsRandomRefresh.setSelection(dataDef.isRandomRefresh);
        textQuest.setText(dataDef.questIDs);
        if(dataDef.image instanceof GameMesh){
            GameMesh gamemesh = (GameMesh)dataDef.image;
            MeshConfig meshConfig = MeshConfig.getBufferedMeshConfig(gamemesh.getAnimateFile(0));
            if(meshConfig != null){
                MaterialConfig materialConfig =  meshConfig.getMaterialConfig();
                MaterialGroup materialGroup = materialConfig.getGroup();
                List<String> materialItems = materialGroup.getAllMaterialItemNames();
                String[] materialNames = new String[materialItems.size()];
                for(int i = 0 ; i < materialItems.size() ; i++){
                    materialNames[i] = materialItems.get(i);
                }
                comboMaterial.setItems(materialNames);
                comboMaterial.select(dataDef.materialNameIndex);
            }
        }else{
            comboMaterial.setEnabled(false);
        }

    }

    /**
     * 为NPC新增一个掉落组
     */
    private void onAdd() {
        ChooseDropGroupDialog dropDialog = new ChooseDropGroupDialog(getSite().getShell());
        if (dropDialog.open() == IDialogConstants.OK_ID) {
            DropNode node = (DropNode) dropDialog.getSelectedObject().duplicate();

            NPCTemplate dataDef = (NPCTemplate) editObject;
            dataDef.dropGroups.add(node);
            dropGroupTable.refresh();

            setDirty(true);
        }
    }

    /**
     * 为NPC删除一个掉落组
     */
    private void onDelete() {
        StructuredSelection selected = (StructuredSelection) dropGroupTable.getSelection();
        Object[] sels = selected.toArray();
        for (int i = 0; i < sels.length; i++) {
            DropNode selGroup = (DropNode) sels[i];
            NPCTemplate dataDef = (NPCTemplate) editObject;
            dataDef.dropGroups.remove(selGroup);
            dropGroupTable.refresh();
            setDirty(true);
        }
    }

    /**
     * 列表双击事件
     */
    private void onDoubleClick(Object selected) {
        StructuredSelection sel = (StructuredSelection) selected;
        DropNode selGroup = (DropNode) sel.getFirstElement();

        ChooseDropGroupDialog dropDialog = new ChooseDropGroupDialog(getSite().getShell());
        dropDialog.setSelectedItem(selGroup);
        if (dropDialog.open() == IDialogConstants.OK_ID) {
            DropNode selNewGroup = dropDialog.getSelectedObject();
            if (selNewGroup.equals(selGroup)) {
                selGroup.update(selNewGroup);
                dropGroupTable.refresh();
                setDirty(true);
            }
        }
    }

    /**
     * 创建掉落列表菜单
     */
    protected void createActions() {
        addDropGroup = new Action("新增") {
            public void run() {
                onAdd();
            }
        };

        delDropGroup = new Action("删除") {
            public void run() {
                onDelete();
            }
        };
    }

    /**
     * 保存当前编辑数据。
     */
    protected void saveData() throws Exception {
        NPCTemplate dataDef = (NPCTemplate) editObject;

        try {
            dataDef.id = Integer.parseInt(textID.getText());
        } catch (Exception e) {
            throw new Exception("请输入正确的ID。");
        }
        dataDef.title = textTitle.getText().trim();
        dataDef.description = textDescription.getText();
        try {
            dataDef.image = spriteChooser.getSelectedObject();
            String materialName = spriteChooser.getMaterialName();
            if(dataDef.image instanceof GameMesh){
            	if(materialName != null){
            	    dataDef.materialName = materialName;
            	    dataDef.materialNameIndex = getMaterialNameIndexByName(materialName);
            	}else{
            	    dataDef.materialNameIndex = 0;
            	    dataDef.materialName = getMaterialNameByIndex(dataDef.materialNameIndex);
            	}
                comboMaterial.setEnabled(true);
                comboMaterial.setItems(getMaterialNames());
                comboMaterial.select(dataDef.materialNameIndex);
            }
        } catch (Exception e) {
            throw new Exception("请选择一个图片。");
        }
        try {
            StructuredSelection sel = (StructuredSelection) comboType.getSelection();
            dataDef.type = (NPCType) sel.getFirstElement();
            if (dataDef.type == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception("请选择一个类型。");
        }
        dataDef.level = comboLevel.getSelectionIndex();
        dataDef.clazz = comboClazz.getSelectionIndex();
        dataDef.difficulty = comboDifficulty.getSelectionIndex();
        dataDef.changePath = buttonChangePass.getSelection();
        dataDef.aiDataID = Integer.parseInt(aiText.getText());
        dataDef.isRandomRefresh = buttonIsRandomRefresh.getSelection();
        
        dataDef.questIDs = textQuest.getText();

        // 检查输入合法性
        DataObject dobj = ProjectData.getActiveProject().findObject(dataDef.getClass(), dataDef.id);
        if (dobj != null && dobj != getSaveTarget()) {
            throw new Exception("ID重复，请重新输入。");
        }
        if (dataDef.title.length() == 0) {
            throw new Exception("请输入标题。");
        }
    }

    private void onAnalyzeDrop1() {
        try {
            DropAnalyzeDialog dlg = new DropAnalyzeDialog(getSite().getShell(), (NPCTemplate) editObject, true,
                    comboAnalyzeLevel.getSelectionIndex() + 1, comboAnalyzeClazz.getSelectionIndex());
            dlg.open();
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.openError(getSite().getShell(), "错误", e.toString());
        }
    }

    private void onAnalyzeDrop2() {
        try {
            DropAnalyzeDialog dlg = new DropAnalyzeDialog(getSite().getShell(), (NPCTemplate) editObject, false,
                    comboAnalyzeLevel.getSelectionIndex() + 1, comboAnalyzeClazz.getSelectionIndex());
            dlg.open();
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.openError(getSite().getShell(), "错误", e.toString());
        }
    }
    public int getMaterialNameIndexByName(String materialName){
        int ret = 0;
        String[] materialNames = getMaterialNames();
        if(materialNames != null){
            for(int i = 0 ; i < materialNames.length ; i++){
                if(materialNames[i].equals(materialName)){
                    ret = i ;
                    break;
                }
            }
        }
        return ret;
    }
    
    public String getMaterialNameByIndex(int materialNameIndex){
        String ret = null;
        String[] materialNames = getMaterialNames();
        if(materialNames != null){
            for(int i = 0 ; i < materialNames.length ; i++){
                if(i == materialNameIndex){
                    ret = materialNames[i];
                    break;
                }
            }
        }
        return ret;
    }
 
    public String[] getMaterialNames(){
        String[] ret = null;
        NPCTemplate dataDef = (NPCTemplate) editObject;
        if(dataDef.image instanceof GameMesh){
            GameMesh gamemesh = (GameMesh)dataDef.image;
            MeshConfig meshConfig = MeshConfig.getBufferedMeshConfig(gamemesh.getAnimateFile(0));
            if(meshConfig != null){
                MaterialConfig materialConfig =  meshConfig.getMaterialConfig();
                MaterialGroup materialGroup = materialConfig.getGroup();
                List<String> materialItems = materialGroup.getAllMaterialItemNames();
                ret = new String[materialItems.size()];
                materialItems.toArray(ret);
            }
        }
        return ret;
    }
}
