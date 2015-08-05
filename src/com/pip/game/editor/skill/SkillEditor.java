package com.pip.game.editor.skill;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.autocoding.SkillTypeFilteringEffect;
import com.pip.game.data.effects0.Effect_CHANGE_PARAM;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.EffectConfig;
import com.pip.game.data.skill.EffectConfigSet;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.util.IconChooser;
import com.pip.util.AutoSelectAll;

public class SkillEditor extends DefaultDataObjectEditor implements SelectionListener {
    class BuffListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            List<DataObject> buffs = ProjectData.getActiveProject().getDataListByType(BuffConfig.class);
            List<Object> ret = new ArrayList<Object>();
            SkillConfig dataDef = (SkillConfig)editObject;
            ret.add("��");
            for (DataObject dobj : buffs) {
                BuffConfig buff = (BuffConfig)dobj;
                if (buff.buffType == BuffConfig.BUFF_TYPE_STATIC) {
                    if (dataDef.type == SkillConfig.TYPE_BUFF && buff.isAreaBuff) {
                        ret.add(dobj);
                    } else if (dataDef.type == SkillConfig.TYPE_PASSIVE && !buff.isAreaBuff) {
                        ret.add(dobj);
                    }
                }
            }
            return ret.toArray();
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    protected Combo comboPrepareAni;
    protected Combo comboBuff;
    protected SkillAnimationChooser textHitAni;//��SkillEditorEx�����hitAnimationPlayer���
    protected SkillAnimationChooser textPrepareAni;
    protected SkillAnimationChooser textCastAni;
    /**
     * �켣����
     */
    protected SkillAnimationChooser locusAni;
    protected Combo comboCastAni;
    protected Label l_DamageType;
    protected Combo comboDamageType;
    protected Text textCDGroup;
    protected Combo comboClazz;
    protected Combo comboMaxLevel;
    protected Combo comboTargetType;
    protected Combo comboType;
    public static final String ID = "com.pip.game.editor.skill.SkillEditor"; //$NON-NLS-1$
    public boolean updating = false;
    public boolean isUpdating() {
        return updating;
    }
    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    protected Text textDesc;
    
    protected Text textTitle;
    protected Text testAssistAction;
    protected Text textID;
    protected IconChooser iconChooser;
    protected WeaponChooser weaponChooser;
    protected EffectConfigSetEditor effectEditor;
    protected Button buttonRideUse;
    protected Button buttonVisible;
    
    private Button buttonShareCD;
    private Button buttonUseBuffs;
    
    protected ComboViewer buffComboViewer;
    protected Button buttonAutoLearn;
    protected Spinner targetCntSpinner;
    protected SkillExtPropManager extPropManager;
    protected Button buttonAnalyse;

    public SkillExtPropManager getExtPropManager() {
        return extPropManager;
    }
    public void setExtPropManager(SkillExtPropManager extPropManager) {
        this.extPropManager = extPropManager;
    }
    @Override
    public void createPartControl(Composite parent) {
        SkillConfig dataDef = (SkillConfig) editObject;
        extPropManager = new SkillExtPropManager();
        extPropManager.convertExtData2ExtPropControl(dataDef.extPropEntries.editProps);
        dataDef.extPropEntries = extPropManager;
        
        Composite container = new Composite(parent, SWT.NONE);
//        RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
//        rowLayout.fill = true;
//        rowLayout.wrap = false;
//        container.setLayout(rowLayout);
        GridLayout gridLayout0 = new GridLayout(1, false);
        gridLayout0.marginWidth = 5;
        gridLayout0.marginHeight = 5;
        gridLayout0.verticalSpacing = 0;
        gridLayout0.horizontalSpacing = 0;
        container.setLayout(gridLayout0);
        

        /////////////////row 0
        Composite row0 = new Composite(container, SWT.NONE);
        RowLayout row0Layout = new RowLayout(SWT.HORIZONTAL);
        row0Layout.center = true;
        row0.setLayout(row0Layout);
        final Label label = new Label(row0, SWT.NONE);
        label.setText("ID:");

        textID = new Text(row0, SWT.BORDER);
        textID.setText("   "+dataDef.id);
        textID.addFocusListener(AutoSelectAll.instance);
        textID.addModifyListener(this);

        final Label label_1 = new Label(row0, SWT.NONE);
        label_1.setText("����:");

        textTitle = new Text(row0, SWT.BORDER);
        textTitle.setText("��������");
        textTitle.setText(dataDef.title);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);

        final Label label_3 = new Label(row0, SWT.NONE);
        label_3.setText("����:");

        comboType = new Combo(row0, SWT.READ_ONLY);
        comboType.setItems(new String[] {"������������", "������������", "��������", "�⻷����", "�����"});
        comboType.setEnabled(false);
        
        final Label label_33 = new Label(row0, SWT.NONE);
        label_33.setText("��������:");
        
        testAssistAction = new Text(row0, SWT.BORDER);
        testAssistAction.setText("��������");
        testAssistAction.setText(""+dataDef.assistAction);
        testAssistAction.addFocusListener(AutoSelectAll.instance);
        testAssistAction.addModifyListener(this);
        
        
    
        
        
        
        
        final Label label_4 = new Label(row0, SWT.NONE);
        label_4.setText("Ŀ������:");

        comboTargetType = new Combo(row0, SWT.READ_ONLY);
        comboTargetType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                if (!updating) {
                    SkillConfig dataDef = (SkillConfig) editObject;
                    dataDef.targetType = comboTargetType.getSelectionIndex();
                    if(dataDef.targetType<=1){
                        targetCntSpinner.setSelection(1);
                        targetCntSpinner.setEnabled(false);
                    }else{
                        targetCntSpinner.setEnabled(true);
                    }
                    resetEffectEditor();
                    setDirty(true);
                }
            }
        });
        comboTargetType.setItems(new String[] {"����Ŀ��", "�Լ�", "Ŀ�긽��Ⱥ��", "�Լ�����Ⱥ��"});
        
        final Label tgtCntLabel = new Label(row0, SWT.NONE);
        tgtCntLabel.setText("Ŀ�귶Χ����");
        tgtCntLabel.setToolTipText("��Ŀ��Ϊ���ĵ�һ����Χ�ڵ�N����λ����Ч��λ�����ȼ��Ծ���Ŀ��Խ��Խ����");
        targetCntSpinner = new Spinner(row0, SWT.BORDER);
        targetCntSpinner.setMinimum(1);
        targetCntSpinner.setSelection(dataDef.affectScope);
        targetCntSpinner.setEnabled(false);
        targetCntSpinner.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                if(!updating){
                    SkillConfig dataDef = (SkillConfig) editObject;
                    dataDef.affectScope = targetCntSpinner.getSelection();
                    setDirty(true);
                }
            }
        });

        final Label label_5 = new Label(row0, SWT.NONE);
        label_5.setText("��߼���:");

        comboMaxLevel = new Combo(row0, SWT.READ_ONLY);
        comboMaxLevel.setVisibleItemCount(20);
        comboMaxLevel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                if (!updating) {
                    SkillConfig dataDef = (SkillConfig) editObject;
                    int maxLevel = comboMaxLevel.getSelectionIndex() + 1;
                    dataDef.setMaxLevel(maxLevel);
                    resetEffectEditor();
                    setDirty(true);
                }
            }
        });
        comboMaxLevel.setItems(new String[] {"1��", "2��", "3��", "4��", "5��", "6��", "7��", "8��", "9��", "10��", "11��", "12��", "13��", "14��", "15��", "16��", "17��", "18��", "19��", "20��"});

        final Label label_6 = new Label(row0, SWT.NONE);
        label_6.setText("ְҵ:");

        comboClazz = new Combo(row0, SWT.READ_ONLY);
        comboClazz.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                if (!updating) {
                    setDirty(true);
                }
            }
        });
        comboClazz.setItems(ProjectData.getActiveProject().config.PLAYER_CLAZZ_RAW);
        
        //////////row 1
        Composite row1 = new Composite(container, SWT.NONE);
        row1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gridLayout = new GridLayout(3, false);
        row1.setLayout(gridLayout);
        
        final Label label_2 = new Label(row1, SWT.NONE);
        label_2.setText("����");

        textDesc = new Text(row1, SWT.BORDER);
        textDesc.addFocusListener(AutoSelectAll.instance);
        textDesc.addModifyListener(this);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.grabExcessHorizontalSpace = true;
        textDesc.setLayoutData(gridData);
        

        final Button buttonDesc = new Button(row1, SWT.NONE);
        buttonDesc.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                SkillConfig dataDef = (SkillConfig)editObject;
                DescriptionPattern pat = new DescriptionPattern(dataDef);
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < dataDef.maxLevel; i++) {
                    if (i > 0) {
                        buf.append("\r\n");
                    }
                    buf.append(pat.generate(i + 1));
                }
                MessageDialog.openInformation(getSite().getShell(), "������Ϣ", buf.toString());
            }
        });
        
        buttonDesc.setText("��������");

        ///////////////////////row 2
        Composite row2 = new Composite(container, SWT.NONE);
        RowLayout row2layout = new RowLayout(SWT.HORIZONTAL);
        row2layout.center = true;
        row2.setLayout(row2layout);
        create2(row2);
         
        /////////////////////rowWeapon
        Composite rowWeapon = new Composite(container, SWT.NONE);
        RowLayout rowWeaponlayout = new RowLayout(SWT.HORIZONTAL);
        rowWeaponlayout.center = true;
        rowWeapon.setLayout(rowWeaponlayout);
        createWeapon(rowWeapon);
        
        ///////////////////////row 3
        Composite row3 = new Composite(container, SWT.NONE);
        RowLayout row3layout = new RowLayout(SWT.HORIZONTAL);
//        GridLayout row3layout = new GridLayout(1, false);
//        row3layout.marginWidth = 5;
//        row3layout.marginHeight = 5;
//        row3layout.verticalSpacing = 0;
//        row3layout.horizontalSpacing = 0;
//        row3.setLayout(row3layout);

        row3layout.center = true;
        row3.setLayout(row3layout);
        create3(row3);
        ///////////////////////row 4
        Composite row4 = new Composite(container, SWT.NONE);
        RowLayout row4layout = new RowLayout(SWT.HORIZONTAL);
        row4layout.center = true;
        row4.setLayout(row4layout);
        create4(row4);
        ///////////////////////row extend attributes
//        Composite rowExt = new Composite(container, SWT.NONE);
       
        Composite rowPreSkill = new Composite(container, SWT.NONE);
        rowPreSkill.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gridLayoutPreSkill = new GridLayout(3, false);
        rowPreSkill.setLayout(gridLayoutPreSkill);
  
        Group rowExt = new Group(container, SWT.NONE);
        rowExt.setText("��չ����");
        GridData extLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        rowExt.setLayoutData(extLayoutData);
        extPropManager.createPartControl(rowExt);
        extPropManager.setEditor(this);
//        extPropMngr.createPartControl(rowExt, parentEl);
        ///////////////////row 9 (last row)
        Composite tableComp = new Composite(container, SWT.BORDER);
        tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        FillLayout fl = new FillLayout();
        fl.marginHeight = fl.marginWidth = 0;
        tableComp.setLayout(fl);
        create9(tableComp);
    }
    protected void create2(Composite container){
        final Label label_7 = new Label(container, SWT.NONE);
        label_7.setText("ͼ��:");
        
        iconChooser = new IconChooser(container, SWT.NONE, ProjectData.getActiveProject().config.iconSeries.get("skill"));
        iconChooser.setHandler(this);

        final Label label_8 = new Label(container, SWT.NONE);
        label_8.setText("��ȴ��:");

        textCDGroup = new Text(container, SWT.BORDER);
        textCDGroup.addModifyListener(this);
        l_DamageType = new Label(container, SWT.NONE);
        l_DamageType.setText("Ч������:");

        comboDamageType = new Combo(container, SWT.READ_ONLY);
        comboDamageType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                if (!updating) {
                    setDirty(true);
                }
            }
        });
        comboDamageType.setVisibleItemCount(10);
        comboDamageType.setItems(new String[] {"����", "����", "����", "����", "����", "����", "��ǿ","��������"});


        buttonRideUse = new Button(container, SWT.CHECK);
        buttonRideUse.setText("�������");
        buttonRideUse.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                if (!updating) {
                    setDirty(true);
                }
            }
        });

        buttonVisible = new Button(container, SWT.CHECK);
        buttonVisible.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                if (!updating) {
                    setDirty(true);
                }
            }
        });
        buttonVisible.setText("��װ��");

        buttonAutoLearn = new Button(container, SWT.CHECK);
        buttonAutoLearn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                if (!updating) {
                    setDirty(true);
                }
            }
        });
        buttonAutoLearn.setText("�Զ�ѧϰ1��");
        
        buttonAnalyse = new Button(container, SWT.NONE);
        buttonAnalyse.setText("���ҹ���BUFF");
        buttonAnalyse.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                // ����Ӱ�챾���ܲ�����BUFF���Լ������ܹ�����BUFF
                SkillConfig skill = (SkillConfig)getEditObject();
                List<DataObject> buffs = skill.owner.getDataListByType(BuffConfig.class);
                List<BuffConfig> relateBuffs = new ArrayList<BuffConfig>();
                for (int i = 0; i < buffs.size(); i++) {
                    BuffConfig buff = (BuffConfig)buffs.get(i);
                    boolean match = false;
                    for (EffectConfig eff : buff.effects.getAllEffects()) {
                        if (eff instanceof Effect_CHANGE_PARAM) {
                            ParamIndicator[] inds = (ParamIndicator[])eff.getParam(0);
                            for (ParamIndicator ind : inds) {
                                if ((ind.type == ParamIndicator.TYPE_SKILL_ACTIVE || ind.type == ParamIndicator.TYPE_SKILL_PASSIVE) && ind.id == skill.id) {
                                    match = true;
                                    break;
                                }
                            }
                            if (match) {
                                break;
                            }
                        }
                    }
                    if ((skill.type == SkillConfig.TYPE_BUFF || skill.type == SkillConfig.TYPE_PASSIVE) && skill.passiveBuff == buff.id) {
                        match = true;
                    }
                    for (EffectConfig eff : skill.effects.getAllEffects()) {
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
                
                if (relateBuffs.size() == 0) {
                    MessageDialog.openInformation(getSite().getShell(), "��Ϣ", "û���ҵ��ʹ˼����й�����BUFF��");
                    return;
                }
                
                // ���������˵�
                MenuManager mgr = new MenuManager();
                for (BuffConfig buff : relateBuffs) {
                    mgr.add(new ViewBuffAction(buff));
                }
                
                Menu menu = mgr.createContextMenu(buttonAnalyse);
                Rectangle bounds = buttonAnalyse.getBounds();
                Point topLeft = new Point(bounds.x, bounds.y + bounds.height);
                topLeft = buttonAnalyse.getParent().toDisplay(topLeft);
                menu.setLocation(topLeft.x, topLeft.y);
                menu.setVisible(true);
            }
        });
    }
    
    protected void createWeapon(Composite container){
        weaponChooser = new WeaponChooser(container, SWT.NONE);
        weaponChooser.addModifyListener(this);
    }
    
    protected void create3(Composite container){
        final Label label_12 = new Label(container, SWT.NONE);
        label_12.setText("��ӦBUFF:");

        buffComboViewer = new ComboViewer(container, SWT.READ_ONLY);
        buffComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                if (!updating) {
                    setDirty(true);
                }
            }
        });
        buffComboViewer.setContentProvider(new BuffListContentProvider());
        comboBuff = buffComboViewer.getCombo();
        comboBuff.setVisibleItemCount(20);
        buffComboViewer.setInput(this);
    }
    protected void create4(Composite container){
        final Label label_13 = new Label(container, SWT.NONE);
        label_13.setText("׼������:");

        comboPrepareAni = new Combo(container, SWT.READ_ONLY);
        comboPrepareAni.setItems(new String[] { "��", "�ӵ�", "���", "ʩ��1", "ʩ��2", "ʩ��3" });
        comboPrepareAni.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                if (!updating) {
                    setDirty(true);
                }
            }
        });
        comboPrepareAni.setVisibleItemCount(10);

        textPrepareAni = new SkillAnimationChooser(container, SWT.BORDER, 0);
        textPrepareAni.addModifyListener(this);
        new Label(container, SWT.NONE);

        final Label label_10 = new Label(container, SWT.NONE);
        label_10.setText("���ֶ���:");

        comboCastAni = new Combo(container, SWT.READ_ONLY);
        comboCastAni.setItems(new String[] { "��", "�ӵ�", "���", "ʩ��1", "ʩ��2", "ʩ��3" });
        comboCastAni.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                if (!updating) {
                    setDirty(true);
                }
            }
        });
        comboCastAni.setVisibleItemCount(10);

        textCastAni = new SkillAnimationChooser(container, SWT.BORDER, 0);
        textCastAni.addModifyListener(this);
        new Label(container, SWT.NONE);

        final Label label_11 = new Label(container, SWT.NONE);
        label_11.setText("���ж���:");

        textHitAni = new SkillAnimationChooser(container, SWT.BORDER, 0);
        textHitAni.addModifyListener(this);
        
        final Label label_12 = new Label(container, SWT.NONE);
        label_12.setText("�켣����:");

        locusAni = new SkillAnimationChooser(container, SWT.BORDER, 0);
        locusAni.addModifyListener(this);
        
        
    }
    protected void create9(Composite container){
        effectEditor = new EffectConfigSetEditor(container, SWT.NONE);
        effectEditor.mode = 0;
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
     * �������е�ֵ���õ�������
     */
    protected void updateView() {
        // ���ó�ʼֵ
        SkillConfig dataDef = (SkillConfig) editObject;
        updating = true;
        updateControlState();
        
        textID.setText(String.format("%4d", dataDef.id));
        textTitle.setText(dataDef.title);
        testAssistAction.setText(dataDef.assistAction+"");
        textDesc.setText(dataDef.description);

        iconChooser.setIcon(dataDef.iconID);
//        weaponChooser.setWeapons(dataDef.requireWeapon);

        int sel = Math.getExponent(dataDef.type);
        comboType.select(sel);
        comboTargetType.select(dataDef.targetType);
        comboMaxLevel.select(dataDef.maxLevel - 1);
        comboClazz.select(dataDef.clazz);
        if(dataDef.cdGroup != null &&  dataDef.cdGroup.length() > 0){
            textCDGroup.setText("    " + dataDef.cdGroup);
        }else{
            textCDGroup.setText("    0");
        }
       
        comboDamageType.select(dataDef.damageType);
        
        // ׼���������ͷŶ��������ֶΣ����2�ֽڱ�ʾ�������ͣ���2�ֽڱ�ʾ���Ӷ���ID
        short value = (short)dataDef.prepareAnimation;
        comboPrepareAni.select(value + 1);
        value = (short)(dataDef.prepareAnimation >> 16);
        textPrepareAni.setAnimationID(value);
        value = (short)dataDef.castAnimation;
        comboCastAni.select(value + 1);
        value = (short)(dataDef.castAnimation >> 16);
        textCastAni.setAnimationID(value);
        
        textHitAni.setAnimationID(dataDef.hitAnimation);
        locusAni.setAnimationID(dataDef.locusAnimation);
        comboBuff.select(0);
        for (int i = 1; i < comboBuff.getItemCount(); i++) {
            Object obj = buffComboViewer.getElementAt(i);
            if (obj instanceof BuffConfig && ((BuffConfig)obj).id == dataDef.passiveBuff) {
                comboBuff.select(i);
            }
        }
        buttonRideUse.setSelection(dataDef.rideUse);
        buttonVisible.setSelection(dataDef.visible);
     /*   buttonShareCD.setSelection(dataDef.shareCD);
        buttonUseBuffs.setSelection(dataDef.useBuffs);*/
        buttonAutoLearn.setSelection(dataDef.autoLearn);
        
        StringBuffer stringBuffer = new StringBuffer();
        List<SkillConfig> skillCongfigList = dataDef.getPreSkillConfig();
        for(int i = 0; i < skillCongfigList.size(); i++){
           if(stringBuffer.length() == 0){
               stringBuffer.append(skillCongfigList.get(i).toString());
           }else{
               stringBuffer.append(';' + skillCongfigList.get(i).toString());
           }
        }
        
        resetEffectEditor();
        
        updating = false;
    }
    
    protected void resetEffectEditor() {
        SkillConfig dataDef = (SkillConfig) editObject;
        
        try {
            effectEditor.setAllowedEffects(SkillTypeFilteringEffect.filter(dataDef.type));
        }catch (Exception e) {
            MessageDialog.openInformation(getSite().getShell(), "���˿�ʹ�õ�Ч��ʱ����", e.toString());
            e.printStackTrace();
        }
        
        EffectConfigSet newSet = new EffectConfigSet();
        newSet.setLevelCount(dataDef.effects.getLevelCount());
        newSet.addGeneralEffect(dataDef.getGeneralConfig());
        for (EffectConfig eff : dataDef.effects.getAllEffects()) {
            newSet.addEffect(eff);
        }
        effectEditor.setEditObject(newSet);
    }
    
    protected void updateControlState() {
        SkillConfig dataDef = (SkillConfig) editObject;
        boolean isPassive = dataDef.type == SkillConfig.TYPE_BUFF || dataDef.type == SkillConfig.TYPE_PASSIVE;
        comboBuff.setEnabled(isPassive);
        textHitAni.setEnabled(!isPassive);
        comboPrepareAni.setEnabled(!isPassive);
        comboCastAni.setEnabled(!isPassive);
        comboDamageType.setEnabled(!isPassive);
        textCDGroup.setEnabled(!isPassive);
        comboTargetType.setEnabled(!isPassive);
        buttonRideUse.setEnabled(!isPassive);
        buttonVisible.setEnabled(!isPassive);
    }

    /**
     * ���浱ǰ�༭���ݡ�
     */
    protected void saveData() throws Exception {
        SkillConfig dataDef = (SkillConfig) editObject;
        
        // ��ȡ����:����ID�����⡢����
        try {
            dataDef.id = Integer.parseInt(textID.getText().trim());
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
        
        dataDef.iconID = iconChooser.getIconIndex();
//        dataDef.requireWeapon = weaponChooser.getWeapons();

        dataDef.targetType = comboTargetType.getSelectionIndex();
        dataDef.clazz = comboClazz.getSelectionIndex();
        dataDef.cdGroup = textCDGroup.getText().trim();
        dataDef.damageType = comboDamageType.getSelectionIndex();
        
        // ׼���������ͷŶ��������ֶΣ����2�ֽڱ�ʾ�������ͣ���2�ֽڱ�ʾ���Ӷ���ID
        int high = textPrepareAni.getAnimationID();
        int low = comboPrepareAni.getSelectionIndex() - 1;
        dataDef.prepareAnimation = (high << 16) | (low & 0xFFFF);
        high = textCastAni.getAnimationID();
        low = comboCastAni.getSelectionIndex() - 1;
        dataDef.castAnimation = (high << 16) | (low & 0xFFFF);
        
        dataDef.hitAnimation = textHitAni.getAnimationID();
        dataDef.locusAnimation = locusAni.getAnimationID();
        StructuredSelection sel = (StructuredSelection)buffComboViewer.getSelection();
        if (sel.isEmpty()) {
            dataDef.passiveBuff = -1;
        } else {
            Object obj = sel.getFirstElement();
            if (obj instanceof BuffConfig) {
                dataDef.passiveBuff = ((BuffConfig)obj).id;
            } else {
                dataDef.passiveBuff = -1;
            }
        }
        
        dataDef.rideUse = buttonRideUse.getSelection();
        dataDef.visible = buttonVisible.getSelection();
        dataDef.autoLearn = buttonAutoLearn.getSelection();
        dataDef.effects.clear();
        for (EffectConfig eff : effectEditor.getEditObject().getAllEffects()) {
            if (eff.getType() != -1) {
                dataDef.effects.addEffect(eff);
            }
        }
        
        // �������ɴ���
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        dataDef.generateJava(pw, "a", "a");
    }

    /*
     * ����undo״̬
     */
    protected Object saveState() {
        try {
            saveData();
        } catch (Exception e) {
        }
        return editObject.save();
    }

    /**
     * �ı��޸ĺ������޸ı�־��
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
            setDirty(true);
        }
    }
    
    private class ViewBuffAction extends Action {
        private BuffConfig buff;
        
        public ViewBuffAction(BuffConfig buff) {
            super(buff.toString());
            this.buff = buff;
        }
        public void run() {
            DataListView.tryEditObject(buff);
        }
    }
}
