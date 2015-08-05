package com.pip.game.editor.ai;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.pip.game.data.AI.AIData;
import com.pip.game.data.AI.AIRule;
import com.pip.game.data.AI.AIRuleConfig;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.quest.TemplateManager;
import com.pip.propertysheet.PropertySheetEntry;
import com.pip.propertysheet.PropertySheetViewer;
import com.pip.util.AutoSelectAll;

public class AIEditor extends DefaultDataObjectEditor{
    public static final String ID = "com.pip.game.editor.ai.AIEditor"; 
    
    public AIData aiData;
    public AIRule rule;
    
    public class RuleListCellModifier implements ICellModifier {
        public boolean canModify(Object element, String property) {
            return false;
        }

        public Object getValue(Object element, String property) {
            if (element instanceof String) {
                return element;
            }
            else if (element instanceof AIRuleConfig) {
                return ((AIRuleConfig) element).getName();
            }

            return null;
        }

        public void modify(Object element, String property, Object value) {
        }
    }

    class RuleListContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {

            Object[] ret = new Object[rule.aiRules.size() + 1];
            for (int i = 0; i < rule.aiRules.size(); i++) {
                ret[i] = rule.aiRules.get(i);
            }
            ret[rule.aiRules.size()] = "新建阶段...";
            return ret;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    // **********************
    class RuleListCombatContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            Object[] ret = new Object[rule.combatStatus.size() + 1];
            for (int i = 0; i < rule.combatStatus.size(); i++) {
                ret[i] = rule.combatStatus.get(i);
            }
            ret[rule.combatStatus.size()] = "新建阶段...";
            return ret;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    class RuleListCommonContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            Object[] ret = new Object[rule.commonStatus.size() + 1];
            for (int i = 0; i < rule.commonStatus.size(); i++) {
                ret[i] = rule.commonStatus.get(i);
            }
            ret[rule.commonStatus.size()] = "新建阶段...";
            return ret;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    // *******************
    public class RuleListLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof String) {
                return "新建阶段...";
            }
            else if (element instanceof AIRuleConfig) {
                return ((AIRuleConfig) element).getName();
            }

            return "";
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }

    public Table ruleList;
    public TableViewer ruleListViewer;
    public PropertySheetViewer propertyEditor;
    // ******************
    public Button creatureType;
    public Button mapType;
    
    public Button combatStatus;
    public Button commonStatus;
    public PropertySheetViewer insertPointEditor;
    public RuleListCombatContentProvider combatContentProvider = new RuleListCombatContentProvider();
    public RuleListCommonContentProvider commonContentProvider = new RuleListCommonContentProvider();
    
    public Text textAI;
    public Text textID;
    public Text textTitle;
    public Text textDescription;
    
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        aiData = ((AIData) editObject);
        rule = ((AIData) editObject).aiRule;
    }
    
    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 8;
        container.setLayout(gridLayout);
        
        final Label label = new Label(container, SWT.NONE);
        label.setText("ID：");

        textID = new Text(container, SWT.BORDER);
        final GridData gd_textID = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        textID.setLayoutData(gd_textID);
        textID.addFocusListener(AutoSelectAll.instance);
        textID.setEditable(false);        
        textID.setText("" + aiData.id);

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setLayoutData(new GridData());
        label_1.setText("名称：");

        textTitle = new Text(container, SWT.BORDER);
        final GridData gd_textTitle = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        textTitle.setLayoutData(gd_textTitle);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);
        textTitle.setText(aiData.title);
        
        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setLayoutData(new GridData());
        label_2.setText("描述：");

        textDescription = new Text(container, SWT.BORDER);
        final GridData gd_textDescription = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        textDescription.setLayoutData(gd_textDescription);
        textDescription.addFocusListener(AutoSelectAll.instance);
        textDescription.addModifyListener(this);
        textDescription.setText(aiData.description);
        
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        
        // *************
        // AI类型
        Group aiTypeGroup = new Group(container, SWT.FILL);
        aiTypeGroup.setText("AI类型");
        GridData aiTypeGroup_gd = new GridData(SWT.FILL, SWT.TOP, true, false, 8, 1);
        aiTypeGroup_gd.heightHint = 50;
        aiTypeGroup.setLayoutData(aiTypeGroup_gd);

        GridLayout aiGroupLayout = new GridLayout();
        aiGroupLayout.numColumns = 1;
        aiTypeGroup.setLayout(aiGroupLayout);

        final Composite aiparamComposite = new Composite(aiTypeGroup, SWT.NONE);
        final GridData ai_gd_paramComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        ai_gd_paramComposite.widthHint = 200;
        aiparamComposite.setLayoutData(ai_gd_paramComposite);
        aiparamComposite.setLayout(new FillLayout());
        
        
        
        creatureType = new Button(aiTypeGroup, SWT.RADIO);
        creatureType.setText("怪物AI");
           
        creatureType.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Object o = e.getSource();
                Button btn = (Button) o;
                if (btn.getSelection()) {
                    aiData.type = AIData.CREATURE_AI;
                    setDirty(true);
                    setupInsertPointEditor();
                    setupStageEditor();
                }
            }
        });

        /*GridData combatStatus_gd = new GridData(SWT.END, SWT.CENTER, false, false, 1, 1);
        mapType.setLayoutData(combatStatus_gd);*/
        
        mapType = new Button(aiTypeGroup, SWT.RADIO);
        mapType.setText("场景AI");
        mapType.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Object o = e.getSource();
                Button btn = (Button) o;
                if (btn.getSelection()) {
                    aiData.type = AIData.MAP_AI;
                    setDirty(true);
                    setupInsertPointEditor();
                    setupStageEditor();
                }
            }
        });
        
        if(aiData.type == AIData.CREATURE_AI)
        {
            creatureType.setSelection(true);
        }
        else{
            mapType.setSelection(true);
        }
        /*GridData noCombatStatus_gd = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        commonStatus.setLayoutData(noCombatStatus_gd);*/
        
        // *************
        // 插入点Group开始
        Group insertPointGroup = new Group(container, SWT.FILL);
        insertPointGroup.setText("插入点");

        GridData insertPointGroup_gd = new GridData(SWT.FILL, SWT.TOP, true, false, 8, 1);
        insertPointGroup_gd.heightHint = 130;
        insertPointGroup.setLayoutData(insertPointGroup_gd);

        GridLayout insertGroupLayout = new GridLayout();
        insertGroupLayout.numColumns = 1;
        insertPointGroup.setLayout(insertGroupLayout);

        final Composite paramComposite = new Composite(insertPointGroup, SWT.NONE);
        final GridData gd_paramComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_paramComposite.widthHint = 200;
        paramComposite.setLayoutData(gd_paramComposite);
        paramComposite.setLayout(new FillLayout());

        insertPointEditor = new PropertySheetViewer(paramComposite, SWT.FILL | SWT.FULL_SELECTION | SWT.BORDER, true);
        PropertySheetEntry rootEntry = new PropertySheetEntry();
        insertPointEditor.setRootEntry(rootEntry);
        // 插入点Group结束

        // 状态Group开始
        Group statusGroup = new Group(container, SWT.FILL);
        statusGroup.setText("状态");

        GridData statusGroup_gd = new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1);
        statusGroup_gd.heightHint = 300;
        statusGroup.setLayoutData(statusGroup_gd);

        GridLayout statusGroupLayout = new GridLayout();
        statusGroupLayout.numColumns = 2;
        statusGroup.setLayout(statusGroupLayout);

        combatStatus = new Button(statusGroup, SWT.RADIO);
        combatStatus.setText("战斗状态");
        combatStatus.setSelection(true);
        combatStatus.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Object o = e.getSource();
                Button btn = (Button) o;
                if (btn.getSelection()) {
                    ruleListViewer.setContentProvider(combatContentProvider);
                    ruleListViewer.refresh();
                    ruleList.setSelection(0);
                    setupStageEditor();
                }
            }
        });

        GridData combatStatus_gd = new GridData(SWT.END, SWT.CENTER, false, false, 1, 1);
        combatStatus.setLayoutData(combatStatus_gd);
        commonStatus = new Button(statusGroup, SWT.RADIO);
        commonStatus.setText("非战斗状态");
        commonStatus.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object o = e.getSource();
                Button btn = (Button) o;
                if (btn.getSelection()) {
                    ruleListViewer.setContentProvider(commonContentProvider);
                    ruleListViewer.refresh();
                    ruleList.setSelection(0);
                    setupStageEditor();
                }
            }
        });
        GridData noCombatStatus_gd = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        commonStatus.setLayoutData(noCombatStatus_gd);

        ruleListViewer = new TableViewer(statusGroup, SWT.FULL_SELECTION | SWT.BORDER);
        // ruleListViewer.setContentProvider(new RuleListContentProvider());
        ruleListViewer.setContentProvider(combatContentProvider);
        ruleListViewer.setLabelProvider(new RuleListLabelProvider());
        ruleList = ruleListViewer.getTable();
        ruleList.setLinesVisible(true);
        ruleList.setHeaderVisible(true);
        final GridData gd_ruleList = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
        ruleList.setLayoutData(gd_ruleList);

        final TableColumn ruleColumn = new TableColumn(ruleList, SWT.NONE);
        ruleColumn.setWidth(141);
        ruleColumn.setText("阶段");

        new TableColumn(ruleList, SWT.NONE);

        ruleListViewer.setColumnProperties(new String[] { "c0" });
        ruleListViewer.setCellModifier(new RuleListCellModifier());
        ruleListViewer.setCellEditors(new TextCellEditor[] { new TextCellEditor(ruleList) {
            public int getStyle() {
                return SWT.READ_ONLY;
            }
        } });

        ruleListViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                setupStageEditor();
            }
        });
        ruleListViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection) event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }

                Object obj = sel.getFirstElement();
                if (obj instanceof String) {
                    // 新建阶段
                    InputDialog dlg = new InputDialog(ruleListViewer.getControl().getShell(), "新建阶段", "请输入新阶段的名称：", "新阶段", new IInputValidator() {
                        public String isValid(String newText) {
                            if (newText.trim().length() == 0) {
                                return "阶段名称不能为空。";
                            }
                            else {
                                return null;
                            }
                        }
                    });
                    if (dlg.open() != InputDialog.OK) {
                        return;
                    }
                    String newname = dlg.getValue();
                    AIRuleConfig newAIRuleConfig = new AIRuleConfig(rule, newname);
                    if (combatStatus.getSelection()) {
                        rule.addCombatRuleConfig(newAIRuleConfig);
                    }
                    else if (commonStatus.getSelection()) {
                        rule.addCommonRuleConfig(newAIRuleConfig);
                    }
                    // rule.addRuleConfig(newAIRuleConfig);

                    ruleListViewer.refresh();
                }

            }

        });
        ruleListViewer.getTable().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.DEL) {
                    Object selData = ruleListViewer.getElementAt(ruleListViewer.getTable().getSelectionIndex());
                    if (selData instanceof AIRuleConfig) {
                        if (combatStatus.getSelection()) {
                            rule.combatStatus.remove(selData);
                        }
                        else if (commonStatus.getSelection()) {
                            rule.commonStatus.remove(selData);
                        }
                        // rule.aiRules.remove(selData);
                        // if (rule.aiRules.size() == 0) {
                        // rule.variables.clear();
                        // }
                        setDirty(true);
                        ruleListViewer.refresh();
                    }
                }
            }
        });
        final Composite paramComposite2 = new Composite(statusGroup, SWT.NONE);
        final GridData gd_paramComposite2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_paramComposite2.widthHint = 200;
        paramComposite2.setLayoutData(gd_paramComposite2);
        paramComposite2.setLayout(new FillLayout());

        propertyEditor = new PropertySheetViewer(paramComposite2, SWT.FILL | SWT.FULL_SELECTION | SWT.BORDER, true);
        PropertySheetEntry arootEntry = new PropertySheetEntry();
        propertyEditor.setRootEntry(arootEntry);

        ruleListViewer.setInput(this);
        ruleList.setSelection(0);
        setupStageEditor();
        setupInsertPointEditor();
    
        textAI = new Text(container, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        textAI.setEditable(false);
        final GridData gd_textAI = new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1);
        gd_textAI.heightHint = 130;
        textAI.setLayoutData(gd_textAI);
        textAI.addFocusListener(AutoSelectAll.instance);
        textAI.addModifyListener(this);
        
        updateView();
        setPartName(this.getEditorInput().getName());
        this.setDirty(false);
    }
    
    protected int getContextMask() {
        if (aiData.type == AIData.CREATURE_AI) {
            return TemplateManager.CONTEXT_SET_CREATURE_AI;
        } else {
            return TemplateManager.CONTEXT_SET_MAP_AI;
        }
    }
    
    public void setupInsertPointEditor() {
        if (aiData.type == AIData.CREATURE_AI) {
            insertPointEditor.setInput(new Object[] { new AIRuleInsertPointConfigPropertySource(AIEditor.this, rule.insertPoints,
                    insertPointEditor, getContextMask()) });
        } else {
            insertPointEditor.setInput(new Object[] { new MapAIRuleInsertPointConfigPropertySource(AIEditor.this, rule.insertPoints,
                    insertPointEditor, getContextMask()) });
        }
    }
    
    public void setupStageEditor() {
        propertyEditor.setInput(new Object[0]);
        if (commonStatus.getSelection()) {
            int sel = ruleList.getSelectionIndex();
            if (sel >= 0 && sel < rule.commonStatus.size()) {
                propertyEditor.setInput(new Object[] { new AIRuleConfigPropertySource(AIEditor.this, rule.commonStatus.get(sel),
                        propertyEditor, AIRuleConfigPropertySource.COMMON_STATUS, getContextMask()) });
            }
        } else {
            int sel = ruleList.getSelectionIndex();
            if (sel >= 0 && sel < rule.combatStatus.size()) {
                propertyEditor.setInput(new Object[] { new AIRuleConfigPropertySource(AIEditor.this, rule.combatStatus.get(sel),
                        propertyEditor, AIRuleConfigPropertySource.COMBAT_STATUS, getContextMask()) });
            }
        }
    }
    
    protected void updateView() {
        AIData dataDef = (AIData)editObject;
        textAI.setText(dataDef.aiRule.getAIRuleDesc());
    }
    
    @Override
    protected void saveData() throws Exception {
        AIData tdg = (AIData) editObject;

        tdg.id = this.editObject.getId();
        tdg.title = this.editObject.title;
        tdg.description = this.editObject.description;
        tdg.type = this.aiData.type;
    }

    @Override
    protected Object saveState() {
        return null;
    }
    
    public void modifyText(final ModifyEvent e) {
        super.modifyText(e);
        
        if(e.getSource() == textTitle) {
            aiData.title = textTitle.getText();
        } else if(e.getSource() == textDescription) {
            aiData.description = textDescription.getText();
        }
    }
}
