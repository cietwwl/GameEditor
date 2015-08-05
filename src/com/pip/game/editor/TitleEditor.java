package com.pip.game.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.DataObject;
import com.pip.game.data.Faction;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.Title;
import com.pip.game.data.skill.BuffConfig;
import com.pip.util.AutoSelectAll;

public class TitleEditor extends DefaultDataObjectEditor {
    private Text textBuffLevel;
    class BuffListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            Object[] arr = ((ProjectData)inputElement).getDataListByType(BuffConfig.class).toArray();
            List list = new ArrayList();
            list.add("û��Ч��");
            for (Object o : arr) {
                BuffConfig buff = (BuffConfig)o;
                if (buff.buffType == BuffConfig.BUFF_TYPE_EQUIP) {
                    list.add(buff);
                }
            }
            return list.toArray();
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    class FactionListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return ((ProjectData)inputElement).getDictDataListByType(Faction.class).toArray();
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private Combo comboFactionCtrl;
    private Combo comboBuffCtrl;
    private Combo comboType;
    private ComboViewer comboFaction;
    private Text textSalary;
    private Text textPrice;
    private Combo comboLevel;
    private Text textDescription;
    private Text textTitle;
    private Text textID;
    
    public static final String ID = "com.pip.sanguo.editor.TitleEditor"; //$NON-NLS-1$
    private ComboViewer comboBuff;

    /**
     * Create contents of the editor part
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        container.setLayout(gridLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("ID��");

        textID = new Text(container, SWT.BORDER);
        final GridData gd_textID = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textID.setLayoutData(gd_textID);
        textID.addFocusListener(AutoSelectAll.instance);
        textID.addModifyListener(this);

        final Label label_1 = new Label(container, SWT.NONE);
        label_1.setLayoutData(new GridData());
        label_1.setText("���ƣ�");

        textTitle = new Text(container, SWT.BORDER);
        final GridData gd_textTitle = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textTitle.setLayoutData(gd_textTitle);
        textTitle.addFocusListener(AutoSelectAll.instance);
        textTitle.addModifyListener(this);

        final Label label_2 = new Label(container, SWT.NONE);
        label_2.setLayoutData(new GridData());
        label_2.setText("������");

        textDescription = new Text(container, SWT.BORDER);
        final GridData gd_textDescription = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textDescription.setLayoutData(gd_textDescription);
        textDescription.addFocusListener(AutoSelectAll.instance);
        textDescription.addModifyListener(this);

        final Label label_3 = new Label(container, SWT.NONE);
        label_3.setLayoutData(new GridData());
        label_3.setText("���ͣ�");

        comboType = new Combo(container, SWT.READ_ONLY);
        comboType.setItems(new String[] {"�����ƺ�", "���γƺ�", "���ҳƺ�"});
        comboType.select(0);
        final GridData gd_comboType = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboType.setLayoutData(gd_comboType);
        comboType.addModifyListener(this);

        final Label label_7 = new Label(container, SWT.NONE);
        label_7.setLayoutData(new GridData());
        label_7.setText("����");

        String[] items = new String[ProjectData.getActiveProject().config.LEVEL_EXP.length];
        for (int i = 0; i < ProjectData.getActiveProject().config.LEVEL_EXP.length; i++) {
            items[i] = String.valueOf(i);
        }

        comboLevel = new Combo(container, SWT.READ_ONLY);
        comboLevel.setVisibleItemCount(50);
        comboLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboLevel.setItems(items);
        comboLevel.addModifyListener(this);

        final Label label_4 = new Label(container, SWT.NONE);
        label_4.setLayoutData(new GridData());
        label_4.setText("��Ӫ��");

        comboFaction = new ComboViewer(container, SWT.READ_ONLY);
        comboFaction.setContentProvider(new FactionListContentProvider());
        comboFactionCtrl = comboFaction.getCombo();
        final GridData gd_comboFactionCtrl = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboFactionCtrl.setLayoutData(gd_comboFactionCtrl);
        comboFactionCtrl.setVisibleItemCount(10);
        comboFaction.setInput(ProjectData.getActiveProject());
        comboFactionCtrl.addModifyListener(this);

        final Label label_13 = new Label(container, SWT.NONE);
        label_13.setText("�һ��۸�");

        textPrice = new Text(container, SWT.BORDER);
        final GridData gd_textPrice = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textPrice.setLayoutData(gd_textPrice);
        textPrice.addFocusListener(AutoSelectAll.instance);
        textPrice.addModifyListener(this);

        final Label label_14 = new Label(container, SWT.NONE);
        label_14.setText("ٺ»��");

        textSalary = new Text(container, SWT.BORDER);
        final GridData gd_textSalary = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textSalary.setLayoutData(gd_textSalary);
        textSalary.addFocusListener(AutoSelectAll.instance);
        textSalary.addModifyListener(this);

        final Label label_5 = new Label(container, SWT.NONE);
        label_5.setText("����Ч����");

        comboBuff = new ComboViewer(container, SWT.READ_ONLY);
        comboBuff.setContentProvider(new BuffListContentProvider());
        comboBuffCtrl = comboBuff.getCombo();
        comboBuffCtrl.setVisibleItemCount(20);
        final GridData gd_comboBuffCtrl = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboBuffCtrl.setLayoutData(gd_comboBuffCtrl);
        comboBuffCtrl.addModifyListener(this);
        comboBuff.setInput(ProjectData.getActiveProject());

        final Label label_6 = new Label(container, SWT.NONE);
        label_6.setText("Ч������");

        textBuffLevel = new Text(container, SWT.BORDER);
        final GridData gd_textBuffLevel = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textBuffLevel.setLayoutData(gd_textBuffLevel);
        textBuffLevel.addFocusListener(AutoSelectAll.instance);
        textBuffLevel.addModifyListener(this);
        
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        
        // ���ó�ʼֵ
        updateView();
        
        setDirty(false);
        setPartName(this.getEditorInput().getName());
        saveStateToUndoBuffer();
    }
    
    private void updateView() {
        // ���ó�ʼֵ
        Title dataDef = (Title)editObject;
        textID.setText(String.valueOf(dataDef.id));
        textTitle.setText(dataDef.title);
        textDescription.setText(dataDef.description);
        comboType.select(dataDef.type);
        comboLevel.select(dataDef.level);
        textSalary.setText(String.valueOf(dataDef.salary));
        textPrice.setText(String.valueOf(dataDef.price));
        comboFaction.setSelection(new StructuredSelection(dataDef.faction));
        BuffConfig buff = (BuffConfig)ProjectData.getActiveProject().findObject(BuffConfig.class, dataDef.buffID);
        if (buff != null) {
            comboBuff.setSelection(new StructuredSelection(buff));
        }
        textBuffLevel.setText(String.valueOf(dataDef.buffLevel));
    }
    
    /**
     * ���浱ǰ�༭���ݡ�
     */
    protected void saveData() throws Exception {
        Title dataDef = (Title)editObject;

        // ��ȡ���룺����ID�����⡢���������͡����𡢼۸�ٺ»����Ӫ������Ч��
        try {
            dataDef.id = Integer.parseInt(textID.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ��ID��");
        }
        dataDef.title = textTitle.getText().trim();
        dataDef.description = textDescription.getText();
        dataDef.type = comboType.getSelectionIndex();
        dataDef.level = comboLevel.getSelectionIndex();
        try {
            dataDef.price = Integer.parseInt(textPrice.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ�ļ۸�");
        }
        try {
            dataDef.salary = Integer.parseInt(textSalary.getText());
        } catch (Exception e) {
            throw new Exception("��������ȷ��ٺ»��");
        }
        StructuredSelection sel = (StructuredSelection)comboFaction.getSelection();
        if (sel.isEmpty()) {
            throw new Exception("��ѡ��һ����Ӫ��������ʾ�����ơ�");
        }
        dataDef.faction = (Faction)sel.getFirstElement();
        sel = (StructuredSelection)comboBuff.getSelection();
        BuffConfig buffConfig = null;
        if (sel.isEmpty()) {
            dataDef.buffID = -1;
        } else {
            Object selobj = sel.getFirstElement();
            if (selobj instanceof BuffConfig) {
                dataDef.buffID = ((BuffConfig)selobj).id;
                buffConfig = (BuffConfig)selobj;
            } else {
                dataDef.buffID = -1;
            }
        }
        if (dataDef.buffID != -1) {
            try {
                dataDef.buffLevel = Integer.parseInt(textBuffLevel.getText());
            } catch (Exception e) {
                throw new Exception("��������ȷ��Ч������");
            }
            if (buffConfig.maxLevel < dataDef.buffLevel) {
                throw new Exception("��������ȷ��Ч������");
            }
        }
        
        // �������Ϸ���
        DataObject dobj = ProjectData.getActiveProject().findObject(dataDef.getClass(), dataDef.id);
        if (dobj != null && dobj != getSaveTarget()) {
            throw new Exception("ID�ظ������������롣");
        }
        if (dataDef.title.length() == 0) {
            throw new Exception("��������⡣");
        }
    }
}
