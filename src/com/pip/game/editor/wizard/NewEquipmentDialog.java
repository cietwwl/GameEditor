package com.pip.game.editor.wizard;


import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.DataObject;
import com.pip.game.data.DataObjectCategory;
import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.equipment.EquipmentPrefix;
import com.pip.game.data.item.Item;

public class NewEquipmentDialog extends Dialog {
    class ListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return ((java.util.List)inputElement).toArray();
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    private Composite randomComposite;
    private ComboViewer comboRandomTypeViewer;
    private Combo comboPlace;
    private Combo comboQuality;
    private Combo comboBindType;
    private Text textLevel;
    private Text textRequireLevel;
    private Text textRandomName;
    
    private EquipmentPrefix defaultPrefix = new EquipmentPrefix(ProjectData.getActiveProject());
    private ArrayList<EquipmentPrefix> matchedPrefixes;
    
    // װ������
    public String name;
    // �½�װ����������
    public DataObjectCategory equiType;
    // ��λ
    public int place;
    // ��Ʒ�ȼ�
    public int level = 1;
    // ��װ���ȼ�
    public int requireLevel = 1;
    // ��ƷƷ��
    public int quality = Item.QUALITY_WHITE;
    // ������
    public int bindType = 1;
    // ѡ��ǰ׺
    public EquipmentPrefix[] prefixes = new EquipmentPrefix[] { defaultPrefix };
    
    Button defaultPrefixButton;
    Button[] prefixButtons;
    private Group prefixGroup;
    private DataObjectCategory defaultType;
    
    /**
     * Create the dialog
     * @param parentShell
     */
    public NewEquipmentDialog(Shell parentShell, DataObjectCategory defaultType) {
        super(parentShell);
        this.defaultType = defaultType;
        equiType = defaultType;
    }
    
    private void rebuildPrefixList() {
        if (prefixButtons != null) {
            getSelectedPrefixes();
            for (Button b : prefixButtons) {
                b.dispose();
            }
        }
        findMatchedPrefixes(level, quality);
        prefixButtons = new Button[matchedPrefixes.size()];
        for (int i = 0; i < matchedPrefixes.size(); i++) {
            prefixButtons[i] = new Button(prefixGroup, SWT.CHECK);
            prefixButtons[i].setText(matchedPrefixes.get(i).title);
            prefixButtons[i].setToolTipText(matchedPrefixes.get(i).getHintString());
        }
        updatePrefixButtons();
        prefixGroup.layout();
    }
    
    private void findMatchedPrefixes(int level, int quality) {
        java.util.List<DataObject> list = ProjectData.getActiveProject().getDataListByType(EquipmentPrefix.class);
        matchedPrefixes = new ArrayList<EquipmentPrefix>();
        for (DataObject p : list) {
            EquipmentPrefix pp = (EquipmentPrefix)p;
            if (level >= pp.minLevel && level <= pp.maxLevel && quality >= pp.minQuality && quality <= pp.maxQuality) {
                matchedPrefixes.add(pp);
            }
        }
    }
    
    private void getSelectedPrefixes() {
        ArrayList<EquipmentPrefix> selected = new ArrayList<EquipmentPrefix>();
        if (defaultPrefixButton.getSelection()) {
            selected.add(defaultPrefix);
        }
        for (int i = 0; i < prefixButtons.length; i++) {
            if (prefixButtons[i].getSelection()) {
                selected.add(matchedPrefixes.get(i));
            }
        }
        prefixes = new EquipmentPrefix[selected.size()];
        selected.toArray(prefixes);
    }
    
    private void updatePrefixButtons() {
        defaultPrefixButton.setSelection(false);
        for (Button b : prefixButtons) {
            b.setSelection(false);
        }
        for (EquipmentPrefix p : prefixes) {
            if (p == defaultPrefix) {
                defaultPrefixButton.setSelection(true);
            }
            for (int i = 0; i < matchedPrefixes.size(); i++) {
                if (matchedPrefixes.get(i) == p) {
                    prefixButtons[i].setSelection(true);
                }
            }
        }
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        container.setLayout(gridLayout);
        
        randomComposite = new Composite(container, SWT.NONE);
        randomComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        final GridLayout randomLayout = new GridLayout();
        randomComposite.setLayout(randomLayout);

        final Label label_2 = new Label(randomComposite, SWT.NONE);
        label_2.setLayoutData(new GridData());
        label_2.setText("װ�����ƣ�");

        textRandomName = new Text(randomComposite, SWT.BORDER);
        textRandomName.setText("��װ��");
        textRandomName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label label_11 = new Label(randomComposite, SWT.NONE);
        label_11.setLayoutData(new GridData());
        label_11.setText("ѡ����飺");

        comboRandomTypeViewer = new ComboViewer(randomComposite, SWT.READ_ONLY);
        comboRandomTypeViewer.setContentProvider(new ListContentProvider());
        comboRandomTypeViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboRandomTypeViewer.setInput(ProjectData.getActiveProject().getCategoryListByType(Equipment.class));
        if (defaultType == null) {
            comboRandomTypeViewer.getCombo().select(0);
        } else {
            comboRandomTypeViewer.setSelection(new StructuredSelection(defaultType));
        }

        final Label label_5 = new Label(randomComposite, SWT.NONE);
        label_5.setLayoutData(new GridData());
        label_5.setText("װ����λ��");

        comboPlace = new Combo(randomComposite, SWT.READ_ONLY);
        comboPlace.setVisibleItemCount(20);
        comboPlace.setItems(ProjectData.getActiveProject().config.COMBO_PLACE);
        comboPlace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboPlace.select(1);

        final Label label_4 = new Label(randomComposite, SWT.NONE);
        label_4.setLayoutData(new GridData());
        label_4.setText("��Ʒ�ȼ���");

        textLevel = new Text(randomComposite, SWT.BORDER);
        textLevel.setText(String.valueOf(level));
        textLevel.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                try {
                    level = Integer.parseInt(textLevel.getText());
                    rebuildPrefixList();
                } catch (Exception e1) {
                }
            }
        });
        final GridData gd_textLevel = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textLevel.setLayoutData(gd_textLevel);

        final Label label = new Label(randomComposite, SWT.NONE);
        label.setText("��װ���ȼ���");

        textRequireLevel = new Text(randomComposite, SWT.BORDER);
        textRequireLevel.setText("1");
        final GridData gd_textRequireLevel = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textRequireLevel.setLayoutData(gd_textRequireLevel);

        final Label label_6 = new Label(randomComposite, SWT.NONE);
        label_6.setLayoutData(new GridData());
        label_6.setText("Ʒ�ʣ�");

        comboQuality = new Combo(randomComposite, SWT.READ_ONLY);
        comboQuality.setVisibleItemCount(10);
        comboQuality.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboQuality.setItems(ProjectData.getActiveProject().config.COMBO_QUALITY);
        comboQuality.select(quality);
        comboQuality.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e) {
                onSelect(comboQuality);
            }
        });

        final Label label_1 = new Label(randomComposite, SWT.NONE);
        label_1.setText("�����ͣ�");

        comboBindType = new Combo(randomComposite, SWT.READ_ONLY);
        comboBindType.setItems(new String[] {"����", "װ����", "ʰȡ��"});
        comboBindType.select(0);
        final GridData gd_comboBindType = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboBindType.setLayoutData(gd_comboBindType);

        ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        
        prefixGroup = new Group(scrolledComposite, SWT.NONE);
        prefixGroup.setText("ǰ׺");
        final GridData gd_group = new GridData(SWT.FILL, SWT.FILL, true, true);
        prefixGroup.setLayoutData(gd_group);
        GridLayout gl_group = new GridLayout();
        gl_group.numColumns = 5;
        prefixGroup.setLayout(gl_group);

        scrolledComposite.setContent(prefixGroup);
        Point point = prefixGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        prefixGroup.setSize(point);
        scrolledComposite.setMinSize(400,220);
        scrolledComposite.setSize(400,220);

        defaultPrefixButton = new Button(prefixGroup, SWT.CHECK);
        defaultPrefixButton.setText("��ǰ׺");
        defaultPrefixButton.setSelection(true);
        
        rebuildPrefixList();
        
        final Button buttonSelectAll = new Button(randomComposite, SWT.NONE);
        buttonSelectAll.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                for (Button btn : prefixButtons) {
                    btn.setSelection(true);
                }
            }
        });
        buttonSelectAll.setText("ѡ��ȫ��");
        
        return container;
    }
    
    private void onSelect(Control source) {
        if (source == comboQuality) {
            quality = comboQuality.getSelectionIndex();
            rebuildPrefixList();
        }
    }
    
    private void getCurrentData() throws Exception{
        name = textRandomName.getText();
        if (name.length() == 0) {
            throw new Exception("װ�����Ʋ���Ϊ�գ�");
        }
        
        IStructuredSelection typeSelected = (IStructuredSelection)comboRandomTypeViewer.getSelection();
        //����ѡ��һ��װ����������
//        if (typeSelected.isEmpty()) {
//            throw new Exception("��ѡ��һ��װ���������ͣ�");
//        } else {                    
//            equiType = (DataObjectCategory)typeSelected.getFirstElement();
//        } //���ĸ�Ŀ¼ѡ������ĸ�Ŀ¼�´���
        place = comboPlace.getSelectionIndex();
        try {
            level = Integer.parseInt(textLevel.getText());
        } catch (Exception e) {
            throw new Exception("��Ʒ�ȼ���ʽ����");
        }
        if (level < 1) {
            throw new Exception("��Ʒ�ȼ��������0��");
        }
        try {
            requireLevel = Integer.parseInt(textRequireLevel.getText());
        } catch (Exception e) {
            throw new Exception("��װ���ȼ���ʽ����");
        }
        if (requireLevel < 1) {
            throw new Exception("��װ���ȼ��������0��");
        }
        quality = comboQuality.getSelectionIndex();
        bindType = comboBindType.getSelectionIndex();
        
        // ����Ҫѡ��һ��ǰ׺
        getSelectedPrefixes();
        if (prefixes.length == 0) {
            throw new Exception("����Ҫѡ��һ��ǰ׺��");
        }
    }
    
    /**
     * Create contents of the button bar
     * @param parent
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "ȷ��", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "ȡ��", false);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("�½�װ��");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            try {
                getCurrentData();
            } catch (Exception e) {
                MessageDialog.openInformation(getShell(), "��ʾ��", e.getMessage());
                return;
            }
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }
    
}
