package com.pip.game.editor.talent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.talent.TalentTree;
import com.pip.game.editor.DataObjectLabelProvider;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.ProjectDataListProvider;
import com.pip.game.editor.skill.DescriptionPattern;

public class TalentTreeEditor extends DefaultDataObjectEditor {

    public static final String ID = "com.pip.game.editor.talent.TalentTreeEditor"; //$NON-NLS-1$
    
    protected TalentTreeOperator operator;
    private List skillDescList;
    private TreeViewer skillTreeViewer;

    @Override
    public void createPartControl(Composite parent) {
        SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        createSkillList(sashForm);
        SashForm sashForm2 = new SashForm(sashForm, SWT.VERTICAL);
        sashForm2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        Composite editeAreaComp = new ScrolledComposite(sashForm2, SWT.V_SCROLL);
        createEditeArea(editeAreaComp);
        editeAreaComp.pack();
        
        Group gp = new Group(sashForm2, SWT.NONE);
        skillDescList = new List(gp, SWT.V_SCROLL);
        skillDescList.add("请选择一个技能");
        skillDescList.setBackground(gp.getBackground());
        gp.setText("技能描述");
        gp.setLayout(new FillLayout());

        sashForm2.setWeights(new int[] { 4, 1 });

        sashForm.setWeights(new int[] { 1, 3 });
    }

    protected void createEditeArea(Composite parent) {
        operator = new TalentTreeOperator();
        operator.init(parent);
        parent.addListener(TalentTreeOperator.EVENT_ID_MODIFIED, new Listener(){
           public void handleEvent(Event evt){
               setDirty(true);
           }
        });
        parent.addListener(TalentTreeOperator.EVENT_ID_FOCUS_CHANGED, new Listener(){
           public void handleEvent(Event evt){
               updateSkillDesc();
           }
        });
        TalentTree treeData= (TalentTree) editObject;
        operator.load(treeData.getRootElement());
        setPartName(saveTarget.getTitle());
    }

    protected void updateSkillDesc() {
//        Control btn = operator.getFocusBtn();
        skillDescList.removeAll();
        Control control = operator.getFocusBtn(); 
        if(control == null){
            return;
        }
        int id = Integer.parseInt((String) control.getData(DragableButton.KEY_ID));
        
        DataObject obj = ProjectData.getActiveProject().findObject(SkillConfig.class, id);
        SkillConfig dataDef = (SkillConfig)obj;
        if(dataDef == null){
            return;
        }
        DescriptionPattern pat = new DescriptionPattern(dataDef);
        for (int i = 0; i < dataDef.maxLevel; i++) {
            skillDescList.add(pat.generate(i + 1));
        }
    }

    private void createSkillList(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(1, false));
        
        Button button = new Button(composite, SWT.PUSH);
        button.setText("添加");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                addTalent();
            }
        });
        
        skillTreeViewer = new TreeViewer(composite, SWT.BORDER | SWT.MULTI);
        skillTreeViewer.setLabelProvider(new DataObjectLabelProvider());
        ProjectDataListProvider p = new ProjectDataListProvider(SkillConfig.class);
        p.needNewCateLabel = false;
        skillTreeViewer.setContentProvider(p);
        skillTreeViewer.setInput(new Object());
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.widthHint = SWT.DEFAULT;
        gridData.heightHint = SWT.DEFAULT;
        skillTreeViewer.getTree().setLayoutData(gridData);
        skillTreeViewer.addDoubleClickListener(new IDoubleClickListener(){

            public void doubleClick(DoubleClickEvent event) {
                addTalent();
            }
            
        });
        
    }

    protected void addTalent() {
        ISelection sel = skillTreeViewer.getSelection();
        TreeSelection treeSel = (TreeSelection) sel;
        if(treeSel == null || treeSel.isEmpty() || treeSel.getFirstElement() instanceof SkillConfig == false){
            MessageDialog.openInformation(getSite().getShell(), "Info", "请选择要添加的技能");
            return;
        }
        SkillConfig skill = (SkillConfig) treeSel.getFirstElement();
        operator.addTalent(skill.id, skill.iconID, skill.title);
        setDirty(true);
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        Element root = new Element("TalentTree");
        root.addAttribute("id", saveTarget.getId()+"");
        root.addAttribute("name", saveTarget.getTitle());
        if (saveTarget.getWholeCategoryName() != null) {
            root.addAttribute("category", saveTarget.getWholeCategoryName());
        }
        operator.save(root);
        ((TalentTree)saveTarget).setRootElement(root);
        try {
            ProjectData.getActiveProject().saveDataList(TalentTree.class);
            setDirty(false);
        }
        catch (Exception e) {
            MessageDialog.openError(getSite().getShell(), "保存天赋树错误", e.toString());
            e.printStackTrace();
        }
    }
    
}
