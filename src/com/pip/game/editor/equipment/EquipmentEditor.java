package com.pip.game.editor.equipment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import aequipmentCodes.EquipmentShow;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.AttributeCalculator;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.equipment.EquipmentAttribute;
import com.pip.game.data.equipment.EquipmentPrefix;
import com.pip.game.data.item.Item;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.quest.RichTextPreviewer;
import com.pip.game.editor.skill.DescriptionPattern;
import com.pip.game.editor.skill.IValueChanged;
import com.pip.game.editor.util.IconChooser;
import com.pip.propertysheet.PropertySheetEntry;
import com.pip.propertysheet.PropertySheetViewer;
import com.pip.util.AutoSelectAll;

public class EquipmentEditor extends DefaultDataObjectEditor implements IValueChanged {
    // ǰ׺�б�������ṩ����һ�������Զ���ǰ׺��
    protected Text textWordCount;
    protected Text textMaxHoleCount;
    protected Text textHoleCount;
    class PrefixListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            List<DataObject> ps = ((Equipment)editObject).owner.getDataListByType(EquipmentPrefix.class);
            Object[] ret = new Object[ps.size() + 2];
            EquipmentPrefix p = new EquipmentPrefix(((Equipment)editObject).owner);
            p.id = -2;
            p.title = "�½�ǰ׺";
            ret[0] = p;
            ret[1] = new EquipmentPrefix(((Equipment)editObject).owner);
            for (int i = 0; i < ps.size(); i++) {
                ret[i + 2] = ps.get(i);
            }
            return ret;
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    public static final String ID = "com.pip.sanguo.editor.equipment.EquipmentEditor";
    
    /** װ���;ö� */
    protected Text textDurability;
    /** ְҵ */
    protected Combo comboJob;
    /** �������� */
    protected Button buttonAstrictPower;
    /** �������� */
    protected Button buttonAstrictAgility;
    /** �������� */
    protected Button buttonAstrictStamina;
    /** �������� */
    protected Button buttonAstrictInteligence;
    /** ������������ */
    protected Text textAstrictInteligence;
    /** ������������ */
    protected Text textAstrictStamina;
    /** ������������ */
    protected Text textAstrictAgility;
    /** ������������ */
    protected Text textAstrictPower;
    /** װ������ */
    protected Combo comboEquiType;
    /**װ�������� */
    protected Label label_101;
    protected Combo comboOwner;
    /** �������� */
    protected Combo comboWeaponType;
    /** ʹ��ʱЧ */
    protected Text textTimeEffect;
    /** ʱЧ���� */
    protected Combo comboTimeType;
    /** װ���۸� */
    protected Text textPrice;
    /** ������ */
    protected Combo comboBind;
    /** ��ҵȼ����� */
    protected Text textPlayerLevel;
    /** ��ҵȼ����� */
    protected Text textPlayerMaxLevel;
    /** װ���ȼ� */
    protected Text textLevel;
    /** װ������ */
    protected Text textTitle;
    /** װ��ID */
    protected Text textID;
    /** װ��Ʒ�� */
    protected Combo comboQuality;
    /** װ��������ֵ */
    protected PropertySheetViewer propertyEditor;
    
    protected String[] ownerValue = {"����","����","���з�"};
    
    protected SelectionEventHandle eventHandle = new SelectionEventHandle();
    protected Composite attributeComposite;
    protected Composite prefixComposite;
    protected ComboViewer prefixComboViewer;
    protected Composite previewComposite;
    protected Combo prefixMaxQualityCombo;
    protected Combo prefixMinQualityCombo;
    protected Text prefixMaxLevelText;
    protected Text prefixMinLevelText;
    protected Combo prefixCombo;
    protected PrefixAttributesEditor prefixAttrEditor;
    protected RichTextPreviewer previewer;
    protected Group group;
    
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
    
    // ���ڱ༭�ı���ǰ׺����
    protected EquipmentPrefix prefix;
    protected boolean updating = false;

    protected Button buttonCanSell;

    protected Button buttonShowRandom;
    protected Button judgeStarButton;
    protected Button judgePotentialButton;
    protected IconChooser iconChooser;
    
    public void createPartControl(Composite parent){
        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 11;
        container.setLayout(gridLayout);

        // �������Ա༭
        
        final Label idLabel = new Label(container, SWT.NONE);
        idLabel.setText("ID��");

        textID = new Text(container, SWT.BORDER);
        textID.setEditable(false);
        textID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData());
        label.setText("���ƣ�");

        textTitle = new Text(container, SWT.BORDER);
        textTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textTitle.addModifyListener(this);

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setLayoutData(new GridData());
        label_1.setText("��Ʒ�ȼ���");

        textLevel = new Text(container, SWT.BORDER);
        textLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,2,1));
        textLevel.addModifyListener(this);

        final Label label_3 = new Label(container, SWT.NONE);
        label_3.setLayoutData(new GridData());
        label_3.setText("װ��Ʒ�ʣ�");

        comboQuality = new Combo(container, SWT.READ_ONLY);
        comboQuality.setVisibleItemCount(10);
        comboQuality.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboQuality.setItems(ProjectData.getActiveProject().config.COMBO_QUALITY);
        comboQuality.addSelectionListener(eventHandle);
        comboQuality.addModifyListener(this);

        final Label label_9 = new Label(container, SWT.NONE);
        label_9.setLayoutData(new GridData());
        label_9.setText("װ����λ��");

        comboEquiType = new Combo(container, SWT.READ_ONLY);
        comboEquiType.setVisibleItemCount(20);
        comboEquiType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboEquiType.setItems(ProjectData.getActiveProject().config.COMBO_PLACE);
        comboEquiType.addSelectionListener(eventHandle);
        comboEquiType.addModifyListener(this);

        label_101 = new Label(container,SWT.NONE);
        label_101.setLayoutData(new GridData());
        label_101.setText("װ��������");
        
        comboOwner = new Combo(container,SWT.READ_ONLY);
        comboOwner.setVisibleItemCount(20);
        comboOwner.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        comboOwner.setItems(ownerValue);
        comboOwner.addSelectionListener(eventHandle);
        comboOwner.addModifyListener(this);
        
        final Label label_13 = new Label(container, SWT.NONE);
        label_13.setLayoutData(new GridData());
        label_13.setText("�;öȣ�");

        textDurability = new Text(container, SWT.BORDER);
        textDurability.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textDurability.addModifyListener(this);

        final Label label_6 = new Label(container, SWT.NONE);
        label_6.setLayoutData(new GridData());
        label_6.setText("���ۼ۸�");

        buttonCanSell = new Button(container, SWT.CHECK);
        buttonCanSell.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                if (!updating) {
                    setDirty(true);
                }
                textPrice.setEnabled(buttonCanSell.getSelection());
            }
        });

        textPrice = new Text(container, SWT.BORDER);
        textPrice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textPrice.addModifyListener(this);

        final Label label_5 = new Label(container, SWT.NONE);
        label_5.setLayoutData(new GridData());
        label_5.setText("�����ͣ�");

        comboBind = new Combo(container, SWT.READ_ONLY);
        comboBind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboBind.setItems(ProjectData.getActiveProject().config.COMBO_BIND);
        comboBind.addModifyListener(this);

        final Label label_7 = new Label(container, SWT.NONE);
        label_7.setLayoutData(new GridData());
        label_7.setText("ʱЧ���ͣ�");

        comboTimeType = new Combo(container, SWT.READ_ONLY);
        comboTimeType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboTimeType.setItems(ProjectData.getActiveProject().config.COMBO_TIME_TYPE);
        comboTimeType.addSelectionListener(eventHandle);
        comboTimeType.addModifyListener(this);

        final Label label_8 = new Label(container, SWT.NONE);
        label_8.setLayoutData(new GridData());
        label_8.setText("ʹ��ʱЧ��");

        textTimeEffect = new Text(container, SWT.BORDER);
        textTimeEffect.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textTimeEffect.addModifyListener(this);

        buttonShowRandom = new Button(container, SWT.CHECK);
        final GridData gd_buttonShowRandom = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        buttonShowRandom.setLayoutData(gd_buttonShowRandom);
        buttonShowRandom.setText("��ʾΪ�������");
        buttonShowRandom.addSelectionListener(eventHandle);

        final Label label_16 = new Label(container, SWT.NONE);
        label_16.setText("��ʯ������");

        textHoleCount = new Text(container, SWT.BORDER);
        final GridData gd_textHoleCount = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        textHoleCount.setLayoutData(gd_textHoleCount);
        textHoleCount.addFocusListener(AutoSelectAll.instance);
        textHoleCount.addModifyListener(this);

        final Label label_17 = new Label(container, SWT.NONE);
        label_17.setText("��������");

        textMaxHoleCount = new Text(container, SWT.BORDER);
        final GridData gd_textMaxHoleCount = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textMaxHoleCount.setLayoutData(gd_textMaxHoleCount);
        textMaxHoleCount.addFocusListener(AutoSelectAll.instance);
        textMaxHoleCount.addModifyListener(this);

        final Composite composite = new Composite(container, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 2;
        composite.setLayout(gridLayout_2);

        judgeStarButton = new Button(composite, SWT.CHECK);
        judgeStarButton.setText("��������Ǽ�");
        judgeStarButton.addSelectionListener(eventHandle);

        judgePotentialButton = new Button(composite, SWT.CHECK);
        judgePotentialButton.setText("�����������");
        judgePotentialButton.addSelectionListener(eventHandle);

        final Label label_18 = new Label(container, SWT.NONE);
        label_18.setText("����������");

        textWordCount = new Text(container, SWT.BORDER);
        textWordCount.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        textWordCount.addFocusListener(AutoSelectAll.instance);
        textWordCount.addModifyListener(this);
        
        final Label label_19 = new Label(container, SWT.NONE);
        label_19.setText("�������ͣ�");
        comboWeaponType = new Combo(container, SWT.READ_ONLY);
        comboWeaponType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboWeaponType.setItems(ProjectData.getActiveProject().config.COMBO_WEAPON_TYPE);
        comboWeaponType.addSelectionListener(eventHandle);
        comboWeaponType.addModifyListener(this);
        
        final Label label_20 = new Label(container, SWT.NONE);
        label_20.setText("ͼ�꣺");
        iconChooser = new IconChooser(container, SWT.NONE, ProjectData.getActiveProject().config.iconSeries.get("item"));
        iconChooser.setHandler(this);
        iconChooser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        // װ�����������༭
        
        group = new Group(container, SWT.NONE);
        GridData gd_group = new GridData(SWT.FILL, SWT.FILL, false, false, 11, 1);
        gd_group.widthHint = 299;
        group.setLayoutData(gd_group);
        
        group.setText("װ������");
        GridLayout groupLayout = new GridLayout();
        groupLayout.numColumns = 12;
        group.setLayout(groupLayout);

        final Label label_2 = new Label(group, SWT.NONE);
        label_2.setText("����");

        textPlayerLevel = new Text(group, SWT.BORDER);
        textPlayerLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textPlayerLevel.addModifyListener(this);

        final Label label_12 = new Label(group, SWT.NONE);
        label_12.setLayoutData(new GridData());
        label_12.setText("ְҵ��");

        comboJob = new Combo(group, SWT.READ_ONLY);
        comboJob.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboJob.setItems(ProjectData.getActiveProject().config.PLAYER_CLAZZ);
        comboJob.addModifyListener(this);

        buttonAstrictPower = new Button(group, SWT.CHECK);
        buttonAstrictPower.setLayoutData(new GridData());
        buttonAstrictPower.setText(ProjectData.getActiveProject().config.PLAYER_ATTR[0] + "��");
        buttonAstrictPower.addSelectionListener(eventHandle);

        textAstrictPower = new Text(group, SWT.BORDER);
        textAstrictPower.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textAstrictPower.addModifyListener(this);

        buttonAstrictAgility = new Button(group, SWT.CHECK);
        buttonAstrictAgility.setLayoutData(new GridData());
        buttonAstrictAgility.setText(ProjectData.getActiveProject().config.PLAYER_ATTR[1] + "��");
        buttonAstrictAgility.addSelectionListener(eventHandle);

        textAstrictAgility = new Text(group, SWT.BORDER);
        textAstrictAgility.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textAstrictAgility.addModifyListener(this);

        buttonAstrictStamina = new Button(group, SWT.CHECK);
        buttonAstrictStamina.setLayoutData(new GridData());
        buttonAstrictStamina.setText(ProjectData.getActiveProject().config.PLAYER_ATTR[2] + "��");
        buttonAstrictStamina.addSelectionListener(eventHandle);

        textAstrictStamina = new Text(group, SWT.BORDER);
        textAstrictStamina.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textAstrictStamina.addModifyListener(this);

        buttonAstrictInteligence = new Button(group, SWT.CHECK);
        buttonAstrictInteligence.setLayoutData(new GridData());
        buttonAstrictInteligence.setText(ProjectData.getActiveProject().config.PLAYER_ATTR[3] + "��");
        buttonAstrictInteligence.addSelectionListener(eventHandle);

        textAstrictInteligence = new Text(group, SWT.BORDER);
        textAstrictInteligence.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textAstrictInteligence.addModifyListener(this);
        
        // ���Ա���һ��PropertySheet���༭
        
        attributeComposite = new Composite(container, SWT.NONE);
        attributeComposite.setLayout(new FillLayout());
        final GridData gd_attributeComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1);
        attributeComposite.setLayoutData(gd_attributeComposite);

        propertyEditor = new PropertySheetViewer(attributeComposite, SWT.BORDER, true);
        PropertySheetEntry rootEntry = new PropertySheetEntry();
        propertyEditor.setRootEntry(rootEntry);

        // ǰ׺�༭����
        
        prefixComposite = new Composite(container, SWT.NONE);
        final GridData gd_prefixComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
        prefixComposite.setLayoutData(gd_prefixComposite);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 4;
        prefixComposite.setLayout(gridLayout_1);

        final Label label_4 = new Label(prefixComposite, SWT.NONE);
        label_4.setText("ǰ׺��");

        prefixComboViewer = new ComboViewer(prefixComposite, SWT.READ_ONLY);
        prefixComboViewer.setContentProvider(new PrefixListContentProvider());
        prefixCombo = prefixComboViewer.getCombo();
        prefixCombo.setVisibleItemCount(20);
        final GridData gd_prefixCombo = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        prefixCombo.setLayoutData(gd_prefixCombo);
        prefixComboViewer.setInput(this);
        prefixComboViewer.addSelectionChangedListener(eventHandle);

        final Label label_10 = new Label(prefixComposite, SWT.NONE);
        label_10.setText("���ü���");

        prefixMinLevelText = new Text(prefixComposite, SWT.BORDER);
        final GridData gd_prefixMinLevelText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        prefixMinLevelText.setLayoutData(gd_prefixMinLevelText);
        prefixMinLevelText.addModifyListener(this);

        final Label label_11 = new Label(prefixComposite, SWT.NONE);
        label_11.setText("-");

        prefixMaxLevelText = new Text(prefixComposite, SWT.BORDER);
        final GridData gd_prefixMaxLevelText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        prefixMaxLevelText.setLayoutData(gd_prefixMaxLevelText);
        prefixMaxLevelText.addModifyListener(this);

        final Label label_14 = new Label(prefixComposite, SWT.NONE);
        label_14.setText("����Ʒ�ʣ�");

        prefixMinQualityCombo = new Combo(prefixComposite, SWT.READ_ONLY);
        prefixMinQualityCombo.setVisibleItemCount(10);
        final GridData gd_prefixMinQualityCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
        prefixMinQualityCombo.setLayoutData(gd_prefixMinQualityCombo);
        prefixMinQualityCombo.setItems(ProjectData.getActiveProject().config.COMBO_QUALITY);
        prefixMinQualityCombo.addModifyListener(this);

        final Label label_15 = new Label(prefixComposite, SWT.NONE);
        label_15.setText("-");

        prefixMaxQualityCombo = new Combo(prefixComposite, SWT.READ_ONLY);
        prefixMaxQualityCombo.setVisibleItemCount(10);
        final GridData gd_prefixMaxQualityCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
        prefixMaxQualityCombo.setLayoutData(gd_prefixMaxQualityCombo);
        prefixMaxQualityCombo.setItems(ProjectData.getActiveProject().config.COMBO_QUALITY);
        prefixMaxQualityCombo.addModifyListener(this);
        
        prefixAttrEditor = GetPrefixEditor(prefixComposite);
        prefixAttrEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        prefixAttrEditor.addModifyListener(this);

        // װ��Ч��Ԥ������
        
        previewComposite = new Composite(container, SWT.NONE);
        previewComposite.setLayout(new GridLayout(1, false));
        final GridData gd_previewComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        previewComposite.setLayoutData(gd_previewComposite);
        
        previewer = new RichTextPreviewer(previewComposite, SWT.NONE);
        previewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        setCurrentData((Equipment)editObject);
        setDirty(false);
        setPartName(this.getEditorInput().getName());
        
        Button button = new Button(previewComposite, SWT.PUSH);
        button.setText("װ��Ԥ��");
        button.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e) {
                try{
                EquipmentShow es = new EquipmentShow();
                int level = Integer.parseInt(textLevel.getText());
                int quality = comboQuality.getSelectionIndex();
                int place = comboEquiType.getSelectionIndex();
                Equipment equ = (Equipment)editObject;
                equ.level = level;
                equ.quality = quality;
                equ.place = place;
                if (equ.getType() == Equipment.EQUI_TYPE_WEAPON) {
                    equ.weaponType = comboWeaponType.getSelectionIndex();
                }
                int point = (int) editObject.DataCalc.getShownValue(equ);
                int eqL = Equipment.getImageLevel(point, place);
                es.setLevel(eqL);
                es.setPart(place);
                es.setWeaponType(((Equipment)editObject).weaponType);
                es.show(getSite().getShell().getDisplay());
                }catch(Exception ee){
                    MessageDialog.openError(getSite().getShell(), "����", ee.toString());
                    ee.printStackTrace();
                }
            }
        });
    }
    
    public PrefixAttributesEditor GetPrefixEditor(Composite container){
        PrefixAttributesEditor pEditor = new PrefixAttributesEditor(container, SWT.NONE);
        return pEditor;
    }
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        prefix = (EquipmentPrefix)((Equipment)editObject).prefix.duplicate();
    }
    
    protected void saveData() throws Exception {
        saveData(false);
    }
    
    protected void saveData(boolean ignoreError) throws Exception {
        Equipment equipment = (Equipment)editObject;
        
        equipment.title = textTitle.getText();
        if(equipment.title == null || "".equals(equipment.title)) {
            if (!ignoreError) {
                throw new Exception("װ�����Ʋ���Ϊ�գ�");
            }
        }
        try {
            equipment.level = Integer.parseInt(textLevel.getText());
        } catch (Exception e1) {
            if (!ignoreError) {
                throw new Exception("װ�������ʽ����");
            }
        }
        equipment.quality = comboQuality.getSelectionIndex();
        equipment.place = comboEquiType.getSelectionIndex();
        equipment.iconIndex = iconChooser.getIconIndex();
        equipment.equipmentType = Equipment.getType(equipment.place);
        equipment.equipmentOwner = comboOwner.getSelectionIndex();
        if (equipment.equipmentType == Equipment.EQUI_TYPE_WEAPON) {
            equipment.weaponType = comboWeaponType.getSelectionIndex();
        } else {
            equipment.weaponType = -1;
        }
        
        try {
            equipment.durability = Integer.parseInt(textDurability.getText());
        } catch (NumberFormatException e) {
            if (!ignoreError) {
                throw new Exception("װ���;ö����ݸ�ʽ����");
            }
        }
        equipment.sale = buttonCanSell.getSelection();
        try {
            equipment.price = Integer.parseInt(textPrice.getText());
        } catch (NumberFormatException e) {
            if (!ignoreError) {
                throw new Exception("���ۼ۸����ݸ�ʽ����");
            }
        }
        if(equipment.price < 0){
            if (!ignoreError) {
                throw new Exception("���ۼ۸������������");
            }
        }
        
        equipment.bind = comboBind.getSelectionIndex();
        try {
            equipment.holeCount = Integer.parseInt(textHoleCount.getText());
        } catch (NumberFormatException e) {
            if (!ignoreError) {
                throw new Exception("��ʯ�����������");
            }
        }
        try {
            equipment.maxHoleCount = Integer.parseInt(textMaxHoleCount.getText());
        } catch (NumberFormatException e) {
            if (!ignoreError) {
                throw new Exception("���ʯ�����������");
            }
        }
        equipment.canJudgeStar = judgeStarButton.getSelection();
        equipment.canJudgePotential = judgePotentialButton.getSelection();
        try {
            equipment.markCharCount = Integer.parseInt(textWordCount.getText());
        } catch (NumberFormatException e) {
            if (!ignoreError) {
                throw new Exception("���������������");
            }
        }
        
        // ʱЧ����
        equipment.timeType = comboTimeType.getSelectionIndex();
        if(equipment.timeType > Equipment.TIME_TYPE_UNDEFINE){
            try {
                if(equipment.timeType == Equipment.TIME_TYPE_RELATIVELY){
                    equipment.time = Integer.parseInt(textTimeEffect.getText());
                } else if (equipment.timeType == Item.TIME_TYPE_ABSOLUTELY) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
                    Date date = format.parse(textTimeEffect.getText());
                    long t = date.getTime();
                    equipment.time = (int)(t/1000);
                }
            } catch (Exception e) {
                if (!ignoreError) {
                    throw new Exception("ʱ���ʽ�����ʽ����");
                }
            }
        }
        
        equipment.showRandom = buttonShowRandom.getSelection();
        
        // װ������
        
        try {
            equipment.playerLevel = Integer.parseInt(textPlayerLevel.getText());
        } catch (NumberFormatException e) {
            if (!ignoreError) {
                throw new Exception("��Ҽ������ݸ�ʽ����");
            }
        }
        equipment.job = comboJob.getSelectionIndex() - 1;
        
        // ����������
        if (buttonAstrictPower.getSelection()) {
            try {
                equipment.astrictPower = Integer.parseInt(textAstrictPower.getText());
            } catch (NumberFormatException e) {
                if (!ignoreError) {
                    throw new Exception("�����������ݸ�ʽ����");
                }
            }
        } else{
            equipment.astrictPower = -1;
        }
        
        // ����������
        if (buttonAstrictAgility.getSelection()) {
            try {
                equipment.astrictAgility = Integer.parseInt(textAstrictAgility.getText());
            } catch (NumberFormatException e) {
                if (!ignoreError) {
                    throw new Exception("�����������ݸ�ʽ����");
                }
            }
        } else{
            equipment.astrictAgility = -1;
        }
        
        // ����������
        if (buttonAstrictStamina.getSelection()) {
            try {
                equipment.astrictStamina = Integer.parseInt(textAstrictStamina.getText());
            } catch (NumberFormatException e) {
                if (!ignoreError) {
                    throw new Exception("�����������ݸ�ʽ����");
                }
            }
        } else{
            equipment.astrictStamina = -1;
        }
        
        // ����������
        if (buttonAstrictInteligence.getSelection()) {
            try {
                equipment.astrictInteligence = Integer.parseInt(textAstrictInteligence.getText());
            } catch (NumberFormatException e) {
                if (!ignoreError) {
                    throw new Exception("�����������ݸ�ʽ����");
                }
            }
        } else {
            equipment.astrictInteligence = -1;
        }
        
        // ���ǰ׺���޸ģ����Ա���ǰ׺
        try {
            prefix.minLevel = Integer.parseInt(prefixMinLevelText.getText());
            prefix.maxLevel = Integer.parseInt(prefixMaxLevelText.getText());
        } catch (Exception e) {
            if (!ignoreError) {
                throw new Exception("ǰ׺�������ݸ�ʽ����");
            }
        }
        prefix.minQuality = prefixMinQualityCombo.getSelectionIndex();
        prefix.maxQuality = prefixMaxQualityCombo.getSelectionIndex();
        if (ignoreError) {
            return;
        }
        if (prefix.id == -2) {
            // �½�
            String newname = null;
            InputDialog dlg = new InputDialog(getSite().getShell(), "�½�ǰ׺", "��������ǰ׺������", "��ǰ׺", new IInputValidator() {
                public String isValid(String newText) {
                    if (newText.trim().length() == 0) {
                        return "���������ơ�";
                    }
                    return null;
                }
            });
            if (dlg.open() == Dialog.OK) {
                newname = dlg.getValue().trim();
            } else {
                throw new Exception("������ȡ����");
            }
            
            // ����Ϊһ���µ�ǰ׺
            EquipmentPrefix newp = (EquipmentPrefix)equipment.owner.newObject(EquipmentPrefix.class, DataListView.getSelectObject());
            int newid = newp.id;
            newp.update(prefix);
            newp.id = newid;
            newp.title = newname;
            equipment.owner.saveDataList(EquipmentPrefix.class);
            equipment.prefix = newp;
            prefix.update(newp);
            
            // ��Ҫ����ǰ׺��
            updating = true;
            prefixComboViewer.refresh();
            selectPrefix(prefix);
            updating = false;
        } else if (prefix.id == -1) {
            // �������ã�ֱ�ӱ���
            equipment.prefix = new EquipmentPrefix(equipment.owner);
            equipment.prefix.update(prefix);
        } else {
            // �޸���Ԥ����ǰ׺
            EquipmentPrefix oldp = (EquipmentPrefix)equipment.owner.findObject(EquipmentPrefix.class, prefix.id);
            if (prefix.isChanged(oldp)) {
                String msg = "���Ѿ��޸���ǰ׺\"" + oldp.title + "\"�����ã��Ƿ񱣴棿���ѡ���ǣ�����ʹ�ô�ǰ׺��װ����" +
                    "�Զ���仯�����ѡ����޸ĺ��ǰ׺����ֻ��Ӱ����һ��װ����";
                boolean result = MessageDialog.openQuestion(getSite().getShell(), "����", msg);
                if (result) {
                    // ����ǰ׺
                    oldp.update(prefix);
                    equipment.owner.saveDataList(EquipmentPrefix.class);
                    equipment.prefix = oldp;
                } else {
                    // �滻Ϊ�½�ǰ׺�������Ͳ���Ӱ������װ����
                    prefix.id = -1;
                    prefix.title = "�½�ǰ׺";
                    equipment.prefix = new EquipmentPrefix(equipment.owner);
                    equipment.prefix.update(prefix);
                    
                    // ��Ҫ����ǰ׺��
                    updating = true;
                    prefixComboViewer.refresh();
                    selectPrefix(prefix);
                    updating = false;
                }
            } else {
                equipment.prefix = oldp;
            }
        }
    }
    
    /**
     * ��ʾ��ǰ�༭������ֵ
     * @param equi
     */
    protected void setCurrentData(Equipment equi) {
        updating = true;

        // ��������
        textID.setText(String.valueOf(equi.id));
        textTitle.setText(equi.title);
        textLevel.setText(String.valueOf(equi.level));
        comboQuality.select(equi.quality);
        comboEquiType.select(equi.place);
        iconChooser.setIcon(equi.iconIndex);
        if(equi.equipmentOwner != 0){
            comboOwner.select(equi.equipmentOwner);
        }else {
            comboOwner.select(0);
        }
        if("����".equals(comboEquiType.getText()) || "����".equals(comboEquiType.getText())) {
            comboWeaponType.select(equi.weaponType);
        } else {
            comboWeaponType.setEnabled(false);
        }
        
        textDurability.setText(String.valueOf(equi.durability));
        buttonCanSell.setSelection(equi.sale);
        textPrice.setEnabled(equi.sale);
        textPrice.setText(String.valueOf(equi.price));
        
        comboBind.select(equi.bind);
        comboTimeType.select(equi.timeType);
        if (equi.timeType != Equipment.TIME_TYPE_UNDEFINE) {
            textTimeEffect.setText(String.valueOf(equi.time));
        } else{
            textTimeEffect.setEditable(false);
        }
        buttonShowRandom.setSelection(equi.showRandom);
        textHoleCount.setText(String.valueOf(equi.holeCount));
        textMaxHoleCount.setText(String.valueOf(equi.maxHoleCount));
        judgeStarButton.setSelection(equi.canJudgeStar);
        judgePotentialButton.setSelection(equi.canJudgePotential);
        textWordCount.setText(String.valueOf(equi.markCharCount));
        
        // װ������
        textPlayerLevel.setText(String.valueOf(equi.playerLevel));
        comboJob.select(equi.job + 1);
        if (equi.astrictPower > 0) {
            buttonAstrictPower.setSelection(true);
            textAstrictPower.setText(String.valueOf(equi.astrictPower));
        } else{
            textAstrictPower.setEditable(false);
        }
        if (equi.astrictAgility > 0) {
            buttonAstrictAgility.setSelection(true);
            textAstrictAgility.setText(String.valueOf(equi.astrictAgility));
        } else{
            textAstrictAgility.setEditable(false);
        }
        if (equi.astrictStamina > 0) {
            buttonAstrictStamina.setSelection(true);
            textAstrictStamina.setText(String.valueOf(equi.astrictStamina));
        } else{
            textAstrictStamina.setEditable(false);
        }
        if (equi.astrictInteligence > 0) {
            buttonAstrictInteligence.setSelection(true);
            textAstrictInteligence.setText(String.valueOf(equi.astrictInteligence));
        } else{
            textAstrictInteligence.setEditable(false);
        }
        
        // ���Ա༭��
        propertyEditor.setInput(new Object[] { new EquipmentPropertySource(equi, this) });

        // ǰ׺�༭��
        selectPrefix(prefix);
        prefixMinLevelText.setText(String.valueOf(prefix.minLevel));
        prefixMaxLevelText.setText(String.valueOf(prefix.maxLevel));
        prefixMinQualityCombo.select(prefix.minQuality);
        prefixMaxQualityCombo.select(prefix.maxQuality);
        prefixAttrEditor.setInput(prefix);
        
        updating = false;

        // Ԥ��
        updatePreview();
    }
    
    // ѡ��ָ����ǰ׺
    protected void selectPrefix(EquipmentPrefix p) {
        if (p.id == -2) {
            prefixComboViewer.setSelection(new StructuredSelection(prefixComboViewer.getElementAt(0)));
        } else if (p.id == -1) {
            prefixComboViewer.setSelection(new StructuredSelection(prefixComboViewer.getElementAt(1)));
        } else {
            p = (EquipmentPrefix)((Equipment)editObject).owner.findObject(EquipmentPrefix.class, p.id);
            prefixComboViewer.setSelection(new StructuredSelection(p));
        }
    }
    
    // �ȼ���Ʒ�ʡ���λ�޸ĺ����¼���۸�
    protected void refreshPriceAndDurability() {
        Equipment equ = (Equipment)editObject;
        equ.recalcPriceAndDurability();
        updating = true;
        textPrice.setText(String.valueOf(equ.price));
        textDurability.setText(String.valueOf(equ.durability));
        updating = false;
        updatePreview();
    }
    
    // �ȼ���Ʒ�ʡ���λ��ǰ׺�޸ĺ����¼�����������
    protected void refreshAttributes() {
        Equipment equ = (Equipment)editObject;
        EquipmentPrefix oldPrefix = equ.prefix;
        equ.prefix = prefix;
        equ.generateAttributes();
        equ.prefix = oldPrefix;
        propertyEditor.setInput( new Object[]{ new EquipmentPropertySource(equ, this) });
        updatePreview();
    }
    
    // ���ֹ��޸�����ʱ�����㸽��Ʒ��
    protected void updateExtraQuality() {
        Equipment equ = (Equipment)editObject;
        editObject.DataCalc.calculateExtraQuality(equ);
        propertyEditor.setInput( new Object[]{ new EquipmentPropertySource(equ, this) });
    }
    
    // ���ֹ��޸�����ʱ������ǰ׺����
    protected void updatePrefixData() {
        Equipment equ = (Equipment)editObject;        
        prefix.updatePriors(equ, equ.appendAttributes);        
        prefixAttrEditor.setInput(prefix);
    }
    
    // ����װ��Ԥ��
    protected void updatePreview() {
        if (updating) {
            return;
        }
        Equipment equ = (Equipment)editObject;
        try {
            saveData(true);
        } catch (Exception e) {
        }
        previewer.setText(getPreviewText(equ));
    }
    
    protected String getPreviewText(Equipment equ) {
        StringBuilder sb = new StringBuilder();
        
        // ����
        sb.append("<c" + Integer.toHexString(ProjectData.getActiveProject().config.QUALITY_COLOR[equ.quality]) + ">");
        sb.append(textTitle.getText());
        sb.append("</c>\n");
        
        // ��λ
        sb.append(ProjectData.getActiveProject().config.PLACE_NAMES[equ.place]);
        sb.append("\n");
        
        // ������
        sb.append(ProjectData.getActiveProject().config.COMBO_BIND[equ.bind]);
        sb.append("\n");
        
        // �;�
        if (equ.durability > 0) {
            sb.append("�;ã�" + equ.durability + "\n");
        }
        
        // ��������
        if (equ.showRandom) {
            sb.append("\n ");
            sb.append("<c00BB00>�������</c>\n");
        } else {
            sb.append(" \n");
            for (int i = 0; i < equ.owner.config.attrCalc.ATTRIBUTES.length; i++) {
                EquipmentAttribute attr = equ.owner.config.attrCalc.ATTRIBUTES[i];
                int value = equ.getAttribute(i);
                if (value > 0) {
                    sb.append("<c00BB00>");
                    sb.append(attr.shortName);
                    sb.append(" +");
                    sb.append(value);
                    sb.append("</c>\n");
                }
            }
        }
        
        // ����Ч��
        if (equ.buffID != -1) {
            BuffConfig bc = (BuffConfig)equ.owner.findObject(BuffConfig.class, equ.buffID);
            if (bc == null) {
                sb.append("<cff0000>��Ч������Ч</c>\n");
            } else if (equ.buffLevel < 1 || equ.buffLevel > bc.maxLevel) {
                sb.append("<cff0000>��Ч������Ч</c>\n");
            } else {
                DescriptionPattern pat = new DescriptionPattern(bc);
                String desc = pat.generate(equ.buffLevel);
                sb.append("<c00bb00>" + desc + "</c>\n");
            }
        }
        
        // װ������
        sb.append(" \n");
        sb.append("����ȼ���" + equ.playerLevel + "\n");
        if (equ.job >= 0) {
            sb.append("ְҵ��" + ProjectData.getActiveProject().config.PLAYER_CLAZZ[equ.job] + "\n");
        }
        if (equ.astrictPower > 0) {
            sb.append("��Ҫ������" + equ.astrictPower + "\n");
        }
        if (equ.astrictAgility > 0) {
            sb.append("��Ҫ���ݣ�" + equ.astrictAgility + "\n");
        }
        if (equ.astrictInteligence > 0) {
            sb.append("��Ҫ������" + equ.astrictInteligence + "\n");
        }
        if (equ.astrictStamina > 0) {
            sb.append("��Ҫ������" + equ.astrictStamina + "\n");
        }
        
        // �۸�
        if (equ.sale) {
            sb.append("�ۼۣ�" + equ.price + "\n");
        } else {
            sb.append("���ɳ���\n");
        }
        
        // ʱЧ
        if (equ.timeType == Equipment.TIME_TYPE_RELATIVELY) {
            sb.append("��Чʱ�䣺");
            sb.append(DescriptionPattern.formatSecond(equ.time));
            sb.append("\n");
        } else if (equ.timeType == Equipment.TIME_TYPE_ABSOLUTELY) {
            sb.append("��Ч���ڣ�");
            SimpleDateFormat df = new SimpleDateFormat("yyyy��MM��dd��");
            sb.append(df.format(new Date(equ.time)));
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    public void modifyText(final ModifyEvent e) {
        super.modifyText(e);
        if (updating) {
            return;
        }
        Equipment equ = (Equipment)editObject;
        if (e.widget == textLevel) {
            try {
                equ.level = Integer.parseInt(textLevel.getText());
                updatePrefixData();
                refreshPriceAndDurability();
                refreshAttributes();
            } catch (Exception ee) {
            }
        } else if(e.widget == this.textPlayerLevel) {
            if("".equals(textPlayerLevel.getText())) {
                equ.playerLevel = 0;
            } else {
                equ.playerLevel = Integer.parseInt(textPlayerLevel.getText());
            }
            updatePrefixData();
            refreshPriceAndDurability();
            refreshAttributes();
        } else if (e.widget == comboQuality) {
            equ.quality = comboQuality.getSelectionIndex();
            refreshPriceAndDurability();
            refreshAttributes();
        } else if (e.widget == comboEquiType) {
            int place = comboEquiType.getSelectionIndex();
            equ.place = place;
            equ.equipmentType = Equipment.getType(place);
            if (equ.equipmentType == Equipment.EQUI_TYPE_WEAPON) {
                if (equ.weaponType == -1) {
                    equ.weaponType = 0;
               		comboWeaponType.select(equ.weaponType);
                }
            }
            equ.resetIcon();
            iconChooser.setIcon(equ.iconIndex);
            refreshPriceAndDurability();
            refreshAttributes();
        } else if (e.widget == comboWeaponType) {
            equ.weaponType = comboWeaponType.getSelectionIndex();
            equ.resetIcon();
            iconChooser.setIcon(equ.iconIndex);
            updatePreview();
        } else if (e.widget == prefixAttrEditor) {
            refreshAttributes();
        } else {
            updatePreview();
        }
    }
    
    /**
     * ����ѡ���¼�����ӿ�
     * 
     * @author Joy
     */
    class SelectionEventHandle extends SelectionAdapter implements ISelectionChangedListener {
        public void widgetSelected(SelectionEvent event) {
            if (event.getSource() == comboTimeType) {
                /* ʱЧ����  */
                if (comboTimeType.getSelectionIndex() == Equipment.TIME_TYPE_UNDEFINE) {
                    textTimeEffect.setEditable(false);
                } else {
                    textTimeEffect.setEditable(true);
                }
            } else if (event.getSource() == buttonAstrictPower) {
                /* ��������  */
                if (buttonAstrictPower.getSelection()) {
                    textAstrictPower.setEditable(true);
                } else{
                    textAstrictPower.setEditable(false);
                }
            } else if (event.getSource() == buttonAstrictAgility) {
                /* �������� */
                if (buttonAstrictAgility.getSelection()) {
                    textAstrictAgility.setEditable(true);
                } else {
                    textAstrictAgility.setEditable(false);
                }
            } else if (event.getSource() == buttonAstrictStamina) {
                /* �������� */
                if (buttonAstrictStamina.getSelection()) {
                    textAstrictStamina.setEditable(true);
                } else {
                    textAstrictStamina.setEditable(false);
                }
            } else if (event.getSource() == buttonAstrictInteligence) {
                /* �������� */
                if (buttonAstrictInteligence.getSelection()) {
                    textAstrictInteligence.setEditable(true);
                } else {
                    textAstrictInteligence.setEditable(false);
                }
            } else if (event.getSource() == comboQuality || event.getSource() == comboEquiType) {
                int quality = comboQuality.getSelectionIndex();
                int place = comboEquiType.getSelectionIndex();
                if (quality == Equipment.QUALITY_WHITE || Equipment.getType(place) == Equipment.EQUI_TYPE_JEWELRY) {
                    judgeStarButton.setSelection(false);
                } else {
                    judgeStarButton.setSelection(true);
                }
                if (quality == Equipment.QUALITY_WHITE) {
                    judgePotentialButton.setSelection(false);
                } else {
                    judgePotentialButton.setSelection(true);
                }
                if("����".equals(comboEquiType.getText())||"����".equals(comboEquiType.getText())) {
                    comboWeaponType.setEnabled(true);
                    comboWeaponType.select(((Equipment)editObject).weaponType);
                } else {
                    comboWeaponType.setEnabled(false);
                }
            }
            setDirty(true);
            updatePreview();
        }
        
        public void selectionChanged(SelectionChangedEvent event) {
            if (updating) {
                return;
            }
            if (event.getSource() == prefixComboViewer) {
                StructuredSelection sel = (StructuredSelection)prefixComboViewer.getSelection();
                EquipmentPrefix p = (EquipmentPrefix)sel.getFirstElement();
                if (prefix.id != p.id) {
                    if (p.id >= 0) {
                        prefix.update(p);
                        refreshAttributes();
                        prefixAttrEditor.setInput(prefix);
                        prefixMinLevelText.setText(String.valueOf(prefix.minLevel));
                        prefixMaxLevelText.setText(String.valueOf(prefix.maxLevel));
                        prefixMinQualityCombo.select(prefix.minQuality);
                        prefixMaxQualityCombo.select(prefix.maxQuality);
                    } else {
                        prefix.id = p.id;
                    }
                    setDirty(true);
                }
            }
        }
    }
    
    // ���Ա༭���з����ֶ��ı䣬��Ҫ����ǰ׺����
    public void valueChanged(String id) {
        if (id.equals(Equipment.PROPNAME_EXTRARATE) || id.equals(Equipment.PROPNAME_BUFFID) || 
                id.equals(Equipment.PROPNAME_BUFFLEVEL)) {
            // �޸��˸���Ʒ�ʣ���������
            refreshPriceAndDurability();
            refreshAttributes();
        } else {
            // �޸������ԣ����㸽��Ʒ�ʺ�ǰ׺����
            refreshPriceAndDurability();
            updateExtraQuality();
            updatePrefixData();
            updatePreview();
        }
        setDirty(true);
    }

    public void valueError(String errorMessage) {
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
            ((Equipment)editObject).owner.saveDataList(Equipment.class);
            setDirty(false);
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            DataListView view = (DataListView)page.findView(DataListView.ID);
            view.refresh(saveTarget);
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.openError(getSite().getShell(), "����", e.toString());
            monitor.setCanceled(true);
        }
    }
}
