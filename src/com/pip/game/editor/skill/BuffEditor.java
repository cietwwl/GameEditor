package com.pip.game.editor.skill;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.autocoding.BuffFilteringEffect;
import com.pip.game.data.effects0.Effect_CHANGE_PARAM;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.DynamicGeneralConfig;
import com.pip.game.data.skill.EffectConfig;
import com.pip.game.data.skill.EffectConfigSet;
import com.pip.game.data.skill.EquipGeneralConfig;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.data.skill.StaticGeneralConfig;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.util.IconChooser;
import com.pip.util.AutoSelectAll;

public class BuffEditor extends DefaultDataObjectEditor implements SelectionListener {
    protected Text textGroupID;
    public Combo getComboMergeStrategy() {
        return comboMergeStrategy;
    }

    public void setComboMergeStrategy(Combo comboMergeStrategy) {
        this.comboMergeStrategy = comboMergeStrategy;
    }

    protected Combo comboMergeStrategy;
    protected IconChooser iconChooser;
    protected Combo comboMaxLevel;
    protected Text textDesc;
    protected Text textTitle;
    protected Text textID;
    protected Button buttonGood;
    protected Button buttonDispelable;
    public static final String ID = "com.pip.sanguo.editor.skill.BuffEditor"; //$NON-NLS-1$

    protected WeaponChooser weaponChooser;
    protected EffectConfigSetEditor effectEditor;
    protected boolean updating = false;
    protected Button areaBuffButton;
    protected Button buttonOffline;
    protected Label typeLabel;
    protected Button buttonKeepOnDie;
    public BuffExtPropManager getExtPropManager() {
        return extPropManager;
    }

    public void setExtPropManager(BuffExtPropManager extPropManager) {
        this.extPropManager = extPropManager;
    }

    protected BuffExtPropManager extPropManager;
    protected Button buttonFindConflict;
    protected Button buttonFindRelate;

    /**
     * Create contents of the editor part
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        BuffConfig dataDef = (BuffConfig) editObject;
        extPropManager = new BuffExtPropManager();
        extPropManager.convertExtData2ExtPropControl(dataDef.extPropEntries.editProps);
        dataDef.extPropEntries = extPropManager;
        
        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 10;
        container.setLayout(gridLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("ID：");

        textID = new Text(container, SWT.BORDER);
        final GridData gd_textID = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textID.setLayoutData(gd_textID);
        textID.addFocusListener(AutoSelectAll.instance);
        textID.addModifyListener(this);

        final Label label_7 = new Label(container, SWT.NONE);
        label_7.setText("类型：");

        typeLabel = new Label(container, SWT.NONE);
        final GridData gd_typeLabel = new GridData(SWT.FILL, SWT.CENTER, false, false);
        typeLabel.setLayoutData(gd_typeLabel);
        typeLabel.setText("未知类型");

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setText("标题：");

        textTitle = new Text(container, SWT.BORDER);
        final GridData gd_textTitle = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textTitle.setLayoutData(gd_textTitle);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);

        final Label label_3 = new Label(container, SWT.NONE);
        label_3.setText("最高级别：");

        comboMaxLevel = new Combo(container, SWT.READ_ONLY);
        comboMaxLevel.setVisibleItemCount(20);
        comboMaxLevel.setItems(new String[] {"1级", "2级", "3级", "4级", "5级", "6级", "7级", "8级", "9级", "10级", "11级", "12级", "13级", "14级", "15级", "16级", "17级", "18级", "19级", "20级"});
        final GridData gd_comboMaxLevel = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboMaxLevel.setLayoutData(gd_comboMaxLevel);
        comboMaxLevel.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                if (!updating) {
                    BuffConfig dataDef = (BuffConfig) editObject;
                    int maxLevel = comboMaxLevel.getSelectionIndex() + 1;
                    dataDef.setMaxLevel(maxLevel);
                    resetEffectEditor();
                    setDirty(true);
                }
            }
        });

        final Label label_4 = new Label(container, SWT.NONE);
        label_4.setText("图标：");

        iconChooser = new IconChooser(container, SWT.NONE, ProjectData.getActiveProject().config.iconSeries.get("buff"));
        iconChooser.setHandler(this);
        final GridData gd_iconChooser = new GridData(SWT.FILL, SWT.CENTER, true, false);
        iconChooser.setLayoutData(gd_iconChooser);

        buttonGood = new Button(container, SWT.CHECK);
        buttonGood.setLayoutData(new GridData());
        buttonGood.setText("是否良性");
        buttonGood.addSelectionListener(this);

        buttonDispelable = new Button(container, SWT.CHECK);
        buttonDispelable.setLayoutData(new GridData());
        buttonDispelable.setText("可驱散");
        buttonDispelable.addSelectionListener(this);

        areaBuffButton = new Button(container, SWT.CHECK);
        areaBuffButton.setLayoutData(new GridData());
        areaBuffButton.setText("光环");
        areaBuffButton.addSelectionListener(this);

        buttonKeepOnDie = new Button(container, SWT.CHECK);
        final GridData gd_buttonKeepOnDie = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        buttonKeepOnDie.setLayoutData(gd_buttonKeepOnDie);
        buttonKeepOnDie.setText("死亡后保持");
        buttonKeepOnDie.addSelectionListener(this);
        
        new Label(container, SWT.NONE);

        final Label label_8 = new Label(container, SWT.NONE);
        label_8.setText("互斥组ID：");

        textGroupID = new Text(container, SWT.BORDER);
        final GridData gd_textGroupID = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textGroupID.setLayoutData(gd_textGroupID);
        textGroupID.addFocusListener(AutoSelectAll.instance);
        textGroupID.addModifyListener(this);

        buttonFindConflict = new Button(container, SWT.NONE);
        buttonFindConflict.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                // 查找是否有何本buff互斥组ID相同的其他BUFF
                BuffConfig thisBuff = (BuffConfig)getEditObject();
                if (thisBuff.groupID == 0) {
                    MessageDialog.openInformation(getSite().getShell(), "信息", "互斥组ID为0表示没有互斥。");
                    return;
                }
                List<DataObject> buffs = thisBuff.owner.getDataListByType(BuffConfig.class);
                List<BuffConfig> relateBuffs = new ArrayList<BuffConfig>();
                for (int i = 0; i < buffs.size(); i++) {
                    BuffConfig buff = (BuffConfig)buffs.get(i);
                    if (buff.id != thisBuff.id && buff.groupID == thisBuff.groupID) {
                        relateBuffs.add(buff);
                    }
                }
                
                if (relateBuffs.size() == 0) {
                    MessageDialog.openInformation(getSite().getShell(), "信息", "没有找到和此BUFF有相同互斥组ID的BUFF。");
                    return;
                }
                
                // 创建弹出菜单
                MenuManager mgr = new MenuManager();
                for (BuffConfig buff : relateBuffs) {
                    mgr.add(new ViewBuffAction(buff));
                }
                
                Menu menu = mgr.createContextMenu(buttonFindConflict);
                Rectangle bounds = buttonFindConflict.getBounds();
                Point topLeft = new Point(bounds.x, bounds.y + bounds.height);
                topLeft = buttonFindConflict.getParent().toDisplay(topLeft);
                menu.setLocation(topLeft.x, topLeft.y);
                menu.setVisible(true);
            }
        });
        buttonFindConflict.setText("查找冲突");

        buttonFindRelate = new Button(container, SWT.NONE);
        buttonFindRelate.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                // 查找影响本BUFF参数的BUFF，以及本BUFF可能给再创建的BUFF
                BuffConfig thisBuff = (BuffConfig)getEditObject();
                List<DataObject> buffs = thisBuff.owner.getDataListByType(BuffConfig.class);
                List<BuffConfig> relateBuffs = new ArrayList<BuffConfig>();
                for (int i = 0; i < buffs.size(); i++) {
                    BuffConfig buff = (BuffConfig)buffs.get(i);
                    boolean match = false;
                    for (EffectConfig eff : buff.effects.getAllEffects()) {
                        if (eff instanceof Effect_CHANGE_PARAM) {
                            ParamIndicator[] inds = (ParamIndicator[])eff.getParam(0);
                            for (ParamIndicator ind : inds) {
                                if ((ind.type == ParamIndicator.TYPE_BUFF_OWNER || ind.type == ParamIndicator.TYPE_BUFF_SOURCE) && ind.id == thisBuff.id) {
                                    match = true;
                                    break;
                                }
                            }
                            if (match) {
                                break;
                            }
                        }
                    }
                    
                    for (EffectConfig eff : thisBuff.effects.getAllEffects()) {
                        for (int j = 0; j < eff.getParamCount(); j++) {
                            if (eff.getParamClass(j) == BuffConfig.class) {
                                int[] ids = (int[])eff.getParam(j);
                                for (int id : ids) {
                                    if (id == buff.id) {
                                        match = true;
                                        break;
                                    }
                                }
                                if (match) {
                                    break;
                                }
                            }
                        }
                        if (match) {
                            break;
                        }
                    }
                    
                    if (match) {
                        relateBuffs.add(buff);
                    }
                }
                
                // 查找引用本BUFF的技能
                List<DataObject> skills = thisBuff.owner.getDataListByType(SkillConfig.class);
                List<SkillConfig> relateSkills = new ArrayList<SkillConfig>();
                for (int i = 0; i < skills.size(); i++) {
                    SkillConfig skill = (SkillConfig)skills.get(i);
                    boolean match = false;
                    if ((skill.type == SkillConfig.TYPE_BUFF || skill.type == SkillConfig.TYPE_PASSIVE) && skill.passiveBuff == thisBuff.id) {
                        match = true;
                    }
                    for (EffectConfig eff : skill.effects.getAllEffects()) {
                        for (int j = 0; j < eff.getParamCount(); j++) {
                            if (eff.getParamClass(j) == BuffConfig.class) {
                                int[] ids = (int[])eff.getParam(j);
                                for (int id : ids) {
                                    if (id == thisBuff.id) {
                                        match = true;
                                        break;
                                    }
                                }
                                if (match) {
                                    break;
                                }
                            }
                        }
                        if (match) {
                            break;
                        }
                    }
                    if (match) {
                        relateSkills.add(skill);
                    }
                }
                
                if (relateBuffs.size() == 0 && relateSkills.size() == 0) {
                    MessageDialog.openInformation(getSite().getShell(), "信息", "没有找到和此BUFF有关联的技能或BUFF。");
                    return;
                }
                
                // 创建弹出菜单
                MenuManager mgr = new MenuManager();
                for (BuffConfig buff : relateBuffs) {
                    mgr.add(new ViewBuffAction(buff));
                }
                for (SkillConfig skill : relateSkills) {
                    mgr.add(new ViewSkillAction(skill));
                }
                
                Menu menu = mgr.createContextMenu(buttonFindRelate);
                Rectangle bounds = buttonFindRelate.getBounds();
                Point topLeft = new Point(bounds.x, bounds.y + bounds.height);
                topLeft = buttonFindRelate.getParent().toDisplay(topLeft);
                menu.setLocation(topLeft.x, topLeft.y);
                menu.setVisible(true);
            }
        });
        buttonFindRelate.setText("查找关联技能/BUFF");

        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setText("描述：");

        textDesc = new Text(container, SWT.BORDER);
        final GridData gd_textDesc = new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1);
        gd_textDesc.widthHint = 300;
        textDesc.setLayoutData(gd_textDesc);
        textDesc.addFocusListener(AutoSelectAll.instance);
        textDesc.addModifyListener(this);

        final Button buttonTestDesc = new Button(container, SWT.NONE);
        buttonTestDesc.setLayoutData(new GridData());
        buttonTestDesc.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                BuffConfig dataDef = (BuffConfig)editObject;
                DescriptionPattern pat = new DescriptionPattern(dataDef);
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < dataDef.maxLevel; i++) {
                    if (i > 0) {
                        buf.append("\r\n");
                    }
                    buf.append(pat.generate(i + 1));
                }
                MessageDialog.openInformation(getSite().getShell(), "描述信息", buf.toString());
            }
        });
        buttonTestDesc.setText("测试描述");

        final Label label_5 = new Label(container, SWT.NONE);
        label_5.setLayoutData(new GridData());
        label_5.setText("合并逻辑：");

        comboMergeStrategy = new Combo(container, SWT.READ_ONLY);
        comboMergeStrategy.setItems(new String[] {"总是不合并", "可叠加多层", "高级覆盖低级", "同来源覆盖", "总是覆盖"});
        final GridData gd_comboMergeStrategy = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboMergeStrategy.setLayoutData(gd_comboMergeStrategy);
        comboMergeStrategy.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                if (!updating) {
                    setDirty(true);
                }
            }
        });

        weaponChooser = new WeaponChooser(container, SWT.NONE);
        weaponChooser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 10, 1));
        weaponChooser.addModifyListener(this);
        
        Group rowExt = new Group(container, SWT.NONE);
        rowExt.setText("扩展属性");
        GridData extLayoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 10, 1);
        rowExt.setLayoutData(extLayoutData);
        extPropManager.createPartControl(rowExt);
        extPropManager.setEditor(this);
        
        
        final Label label_6 = new Label(container, SWT.NONE);
        label_6.setText("效果：");
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);

        buttonOffline = new Button(container, SWT.CHECK);
        final GridData gd_buttonOffline = new GridData();
        buttonOffline.setLayoutData(gd_buttonOffline);
        buttonOffline.setText("即使下线也计时");
        buttonOffline.addSelectionListener(this);

        effectEditor = new EffectConfigSetEditor(container, SWT.NONE);
        effectEditor.mode = 1;
        final GridData gd_effectEditor = new GridData(SWT.FILL, SWT.FILL, true, true, 10, 1);
        effectEditor.setLayoutData(gd_effectEditor);
        effectEditor.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                if (!updating) {
                    setDirty(true);
                }
            }
        });

        updateView();
        setDirty(false);
        setPartName(this.getEditorInput().getName());
        saveStateToUndoBuffer();
    }
    
    /*
     * 把数据中的值设置到界面上
     */
    protected void updateView() {
        // 设置初始值
        BuffConfig dataDef = (BuffConfig) editObject;
        updating = true;
        if (dataDef.buffType == BuffConfig.BUFF_TYPE_STATIC) {
            comboMergeStrategy.setEnabled(false);
            buttonGood.setEnabled(false);
            buttonDispelable.setEnabled(false);
            areaBuffButton.setEnabled(true);
            iconChooser.setEnabled(dataDef.isAreaBuff);
        } else if (dataDef.buffType == BuffConfig.BUFF_TYPE_EQUIP) {
            comboMergeStrategy.setEnabled(true);
            buttonGood.setEnabled(false);
            buttonDispelable.setEnabled(false);
            areaBuffButton.setEnabled(false);
            iconChooser.setEnabled(false);
        } else {
            comboMergeStrategy.setEnabled(true);
            buttonGood.setEnabled(true);
            buttonDispelable.setEnabled(true);
            areaBuffButton.setEnabled(false);
            iconChooser.setEnabled(true);
        }
        textID.setText(String.valueOf(dataDef.id));
        switch (dataDef.buffType) {
        case BuffConfig.BUFF_TYPE_DYNAMIC:
            typeLabel.setText("临时BUFF");
            break;
        case BuffConfig.BUFF_TYPE_STATIC:
            typeLabel.setText("永久BUFF");
            break;
        case BuffConfig.BUFF_TYPE_EQUIP:
            typeLabel.setText("装备BUFF");
            break;
        }
        textTitle.setText(dataDef.title);
        textDesc.setText(dataDef.description);
        comboMergeStrategy.select(dataDef.mergeStrategy);
        iconChooser.setIcon(dataDef.iconID);
        comboMaxLevel.select(dataDef.maxLevel - 1);
        buttonGood.setSelection(dataDef.good);
        buttonDispelable.setSelection(dataDef.dispelable);
        buttonKeepOnDie.setSelection(dataDef.keepOnDie);
        areaBuffButton.setSelection(dataDef.isAreaBuff);
        //西游已经不再用这个，而轩辕需要扩展武器，又继承这，无奈，出此下策
//        weaponChooser.setWeapons(dataDef.requireWeapon);
        buttonOffline.setSelection(dataDef.updateEvenOffline);
        textGroupID.setText(String.valueOf(dataDef.groupID));

        resetEffectEditor();
        updating = false;
    }
    
    protected void resetEffectEditor() {
        BuffConfig dataDef = (BuffConfig) editObject;
        
        try{
        effectEditor.setAllowedEffects(BuffFilteringEffect.filterBuffEffects(dataDef.buffType));
        }catch(Exception e){
            MessageDialog.openError(getSite().getShell(), "过滤可使用效果出错", e.toString());
            e.printStackTrace();
        }
        
        EffectConfigSet newSet = new EffectConfigSet();
        newSet.setLevelCount(dataDef.effects.getLevelCount());
        newSet.addGeneralEffect(dataDef.getGeneralConfig());
        for (EffectConfig eff : dataDef.effects.getAllEffects()) {
            if(eff.getType() != dataDef.owner.effectConfigManager.getTypeId(DynamicGeneralConfig.class) && 
                    eff.getType() != dataDef.owner.effectConfigManager.getTypeId(StaticGeneralConfig.class)&& 
                    eff.getType() != dataDef.owner.effectConfigManager.getTypeId(EquipGeneralConfig.class)) {
                newSet.addEffect(eff);                
            }
        }
        effectEditor.setEditObject(newSet);
    }

    /**
     * 保存当前编辑数据。
     */
    protected void saveData() throws Exception {
        BuffConfig dataDef = (BuffConfig) editObject;
        
        // 读取输入：对象ID、标题、描述
        try {
            dataDef.id = Integer.parseInt(textID.getText());
        } catch (Exception e) {
            throw new Exception("请输入正确的ID。");
        }
        dataDef.title = textTitle.getText().trim();
        dataDef.description = textDesc.getText();

        // 检查输入合法性
        DataObject dobj = ProjectData.getActiveProject().findObject(dataDef.getClass(), dataDef.id);
        if (dobj != null && dobj != getSaveTarget()) {
            throw new Exception("ID重复，请重新输入。");
        }
        if (dataDef.title.length() == 0) {
            throw new Exception("请输入标题。");
        }
        if (dataDef.buffType == BuffConfig.BUFF_TYPE_DYNAMIC && dataDef.iconID != -1 && dataDef.description.trim().length() == 0) {
            throw new Exception("你为此临时BUFF设置了图标，但是没有设置描述。");
        }
        
        dataDef.mergeStrategy = comboMergeStrategy.getSelectionIndex();
        dataDef.iconID = iconChooser.getIconIndex();
        dataDef.good = buttonGood.getSelection();
        dataDef.dispelable = buttonDispelable.getSelection();
        dataDef.keepOnDie = buttonKeepOnDie.getSelection();
        dataDef.isAreaBuff = areaBuffButton.getSelection();
        if (dataDef.buffType == BuffConfig.BUFF_TYPE_STATIC) {
            if (!dataDef.isAreaBuff) {
                dataDef.iconID = -1;
            }
        } else if (dataDef.buffType == BuffConfig.BUFF_TYPE_EQUIP) {
            dataDef.isAreaBuff = false;
            dataDef.iconID = -1;
        }
//        dataDef.requireWeapon = weaponChooser.getWeapons();
        dataDef.updateEvenOffline = buttonOffline.getSelection();

        dataDef.effects.clear();
        for (EffectConfig eff : effectEditor.getEditObject().getAllEffects()) {
            if (eff.getType() != -1) {
                dataDef.effects.addEffect(eff);
            }
        }
        try {
            dataDef.groupID = Integer.parseInt(textGroupID.getText());
        } catch (Exception e) {
            throw new Exception("请输入正确的互斥组ID。");
        }

        // 测试生成代码
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        dataDef.generateJava(pw, "a", "a");
    }

    /*
     * 保存undo状态
     */
    protected Object saveState() {
        try {
            saveData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editObject.save();
    }

    /*
     * 恢复保存的转台
     */
    protected void loadState(Object stateObj) {
        editObject.load((Element) stateObj);
        updateView();
    }

    /**
     * 文本修改后设置修改标志。
     */
    public void modifyText(final ModifyEvent e) {
        if (!updating) {
            setDirty(true);
        }
    }

    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        if (!updating) {
            if (e.getSource() == areaBuffButton) {
                boolean isAreaBuff = areaBuffButton.getSelection();
                if (isAreaBuff) {
                    iconChooser.setEnabled(true);
                    comboMergeStrategy.select(BuffConfig.MERGE_LEVEL);
                } else {
                    iconChooser.setEnabled(false);
                    comboMergeStrategy.select(BuffConfig.MERGE_LEVEL);
                }
            }
            setDirty(true);
        }
    }
    
    public class ViewBuffAction extends Action {
        private BuffConfig buff;
        
        public ViewBuffAction(BuffConfig buff) {
            super(buff.toString());
            this.buff = buff;
        }
        public void run() {
            DataListView.tryEditObject(buff);
        }
    }
    
    public class ViewSkillAction extends Action {
        private SkillConfig skill;
        
        public ViewSkillAction(SkillConfig skill) {
            super(skill.toString() + "(技能)");
            this.skill = skill;
        }
        public void run() {
            DataListView.tryEditObject(skill);
        }
    }
}
