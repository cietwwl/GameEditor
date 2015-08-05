package com.pip.game.data.vehicle;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.editor.util.AnimatePreviewer;

public class ChooseVehicleTemplateDialog extends Dialog {

    public final static int REFRESH_BUTTONID = 1000;
    /**
     * ����ָ��������ģʽ
     */
    public final static byte COUNT_TYPE  = 2;
    
    /**
     * ��һģʽ
     */
    public final static byte ONE_TYPE  = 1;
    
    /**
     * ʹ������
     */
    private  byte type;
    
    /**
     * ģ��ָ������
     */
    private int count = 1;
    
    
    /**
     * ģ��ָ��x
     */
    private int x  = 1;
    /**
     * ģ��ָ��y
     */
    private int y = 1;
    
    
    /**
     * ���������Ի���
     */
    private Text countText;
    
    
    /**
     * x��Χ
     */
    private Text xText;
    
    /**
     * y��Χ
     */
    private Text yText;
    
    
    /**
     * y��Χ
     */
    private List<RefreshVehicle> refreshVehicleList = new ArrayList<RefreshVehicle>();
    
    private 
    class ListRefeshVehicleContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            List el = (List)inputElement;
            return el.toArray();
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    class ListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            List<DataObject> list = ((ProjectData)inputElement).getDataListByType(Vehicle.class);
            List<DataObject> retList = new ArrayList<DataObject>();
            for (int i = 0; i < list.size(); i++) {
                Vehicle t = (Vehicle)list.get(i);
                if (matchCondition(t)) {
                    retList.add(t);
                }
            }
            return retList.toArray();
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    private ListViewer listViewer;
    private org.eclipse.swt.widgets.List list;
    private Text text;
    private String searchCondition;
    private int selectedTemplate = -1;
    private AnimatePreviewer previewer;
    
    private ListViewer orderListViewer;
    
    public int getSelectedTemplate() {
        return selectedTemplate;
    }

    public void setSelectedTemplate(int t) {
        this.selectedTemplate = t;
    }
    
    
    public List<RefreshVehicle> getRefreshVehicle(){
        return refreshVehicleList;
    }
    
    public void setRefreshList(List<RefreshVehicle> refreshVehicleList){
        this.refreshVehicleList = refreshVehicleList;
    }
    
    private boolean matchCondition(Vehicle t) {
        if (searchCondition == null || searchCondition.length() == 0) {
            return true;
        }
        if (t.title.indexOf(searchCondition) >= 0 || String.valueOf(t.id).indexOf(searchCondition) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseVehicleTemplateDialog(Shell parentShell, byte type) {
        super(parentShell);
        this.type = type;
    }
    
    Composite parentContainer;
    
    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        parentContainer = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        parentContainer.setLayout(gridLayout);
        
        Composite container = new Composite(parentContainer, SWT.NONE);
        final GridData gd_textPreDesc = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_textPreDesc.widthHint = 1000;
        gd_textPreDesc.heightHint = 800;
        container.setLayoutData(gd_textPreDesc);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);
        
        if(type == COUNT_TYPE){
            Composite listContainer = new Composite(parentContainer, SWT.BORDER);
            listContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            GridLayout gridLayoutList = new GridLayout();
            gridLayoutList.numColumns = 2;
            listContainer.setLayout(gridLayoutList);
            
            final Label label2 = new Label(listContainer, SWT.NONE);
            label2.setText("������ ");
    
            countText = new Text(listContainer, SWT.BORDER);
            countText.addModifyListener(new ModifyListener() {
                public void modifyText(final ModifyEvent e) {
                    try{
                        count = Integer.parseInt(countText.getText());
                    }catch(Exception error){
                        MessageDialog.openInformation(getShell(), "��ʾ��", "��������");
                    }
                }
            });
            countText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            
            
            final Label labelx = new Label(listContainer, SWT.NONE);
            labelx.setText("x��");
    
            xText = new Text(listContainer, SWT.BORDER);
            xText.addModifyListener(new ModifyListener() {
                public void modifyText(final ModifyEvent e) {
                    try{
                        x = Integer.parseInt(xText.getText());
                    }catch(Exception error){
                        MessageDialog.openInformation(getShell(), "��ʾ��", "����x����");
                    }
                }
            });
            xText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            
            final Label labely = new Label(listContainer, SWT.NONE);
            labely.setText("y��");
    
            yText = new Text(listContainer, SWT.BORDER);
            yText.addModifyListener(new ModifyListener() {
                public void modifyText(final ModifyEvent e) {
                    try{
                        y = Integer.parseInt(yText.getText());
                    }catch(Exception error){
                        MessageDialog.openInformation(getShell(), "��ʾ��", "����y����");
                    }
                }
            });
            yText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            
            
            orderListViewer = new ListViewer(listContainer, SWT.FILL | SWT.BORDER | SWT.V_SCROLL);
            orderListViewer.setContentProvider(new ListRefeshVehicleContentProvider());
            orderListViewer.setLabelProvider(new ListLabelProvider());
            orderListViewer.setInput(this.refreshVehicleList);
            orderListViewer.getList().setBounds(0, 100, 1000, 600);
            final GridData gd_List = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
            gd_List.widthHint = 600;
            gd_List.exclude = true;
            orderListViewer.getList().setLayoutData(gd_List);
            
            orderListViewer.addDoubleClickListener(new IDoubleClickListener() {
                public void doubleClick(final DoubleClickEvent event) {
                    StructuredSelection sel = (StructuredSelection)event.getSelection();
                    if (sel.isEmpty()) {
                        return;
                    }
                    refreshVehicleList.remove(sel.getFirstElement());
                    if(refreshVehicleList.size() == 0){
                       selectedTemplate = -1;
                    }
                    orderListViewer.refresh();
                }
                    
            }
            );
            countText.setText(Integer.toString(count));
            xText.setText(Integer.toString(x));
            yText.setText(Integer.toString(y));
        }
        final Label label = new Label(container, SWT.NONE);
        label.setText("���ң�");
        
        
        text = new Text(container, SWT.BORDER);
        text.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                searchCondition = text.getText();
                StructuredSelection sel = (StructuredSelection)listViewer.getSelection();
                Object selObj = sel.isEmpty() ? null : sel.getFirstElement();
                listViewer.refresh();
                if (selObj != null) {
                    sel = new StructuredSelection(selObj);
                    listViewer.setSelection(sel);
                }
            }
        });
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        listViewer = new ListViewer(container, SWT.V_SCROLL | SWT.BORDER);
        listViewer.setContentProvider(new ListContentProvider());
        list = listViewer.getList();
        final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gridData.heightHint = 150;
        list.setLayoutData(gridData);
        
        listViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                if(type == ONE_TYPE){
                    buttonPressed(IDialogConstants.OK_ID);
                }else{
                    RefreshVehicle vehicle = new RefreshVehicle(ProjectData.getActiveProject());
                    vehicle.id = ((Vehicle)sel.getFirstElement()).id; 
                    vehicle.title = ((Vehicle)sel.getFirstElement()).title;
                    refreshVehicleList.add(vehicle);
                    orderListViewer.refresh();
                    StructuredSelection selVehicle = new StructuredSelection(vehicle);
                    orderListViewer.setSelection(sel);
                }
            }
        });
        
        listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                updatePreviewer();
            }
        });
       
        listViewer.setInput(ProjectData.getActiveProject());

        previewer = new AnimatePreviewer(container, SWT.NONE);
        previewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        previewer.setListVisible(false);
        previewer.setEditEnable(false);
        
        if (selectedTemplate != -1) {
            try {
                // �������Vehicle��tree�е�λ��
                Vehicle t = (Vehicle)ProjectData.getActiveProject().findObject(Vehicle.class, selectedTemplate);
                if (t != null) {
                    searchCondition = t.title;
                    text.setText(searchCondition);
                    text.selectAll();
                    StructuredSelection sel = new StructuredSelection(t);
                    listViewer.setSelection(sel);
                }
            } catch (Exception e) {
            }
        }
        
      
        return container;
    }

    /**
     * Create contents of the button bar
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "ȷ��", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "ȡ��", false);
        if(type == COUNT_TYPE){
            createButton(parent, REFRESH_BUTTONID, "ˢ������", false);
        }
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(520, 644);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        if(type == ONE_TYPE){
            newShell.setText("ѡ���ؾ�ģ��");
        }else{
            newShell.setText("ѡ���ؾ�ģ�壨��Ҫָ��Ŀ��������");
        }
        newShell.setSize(1200, 800);
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            StructuredSelection sel = (StructuredSelection)listViewer.getSelection();
            if (sel.isEmpty() || !(sel.getFirstElement() instanceof Vehicle)) {
                selectedTemplate = -1;
            } else {
                selectedTemplate = ((Vehicle)sel.getFirstElement()).id;
            }
        }else if(buttonId == REFRESH_BUTTONID){
            StructuredSelection sel = (StructuredSelection)orderListViewer.getSelection();
            if(sel.isEmpty() || !(sel.getFirstElement() instanceof RefreshVehicle)){
                MessageDialog.openInformation(getShell(), "��ʾ��", "û��Ŀ��");
            }else{
                RefreshVehicle refreshVehicle = (RefreshVehicle)sel.getFirstElement();
                try{
                    int count = Integer.parseInt(countText.getText());
                    int x = Integer.parseInt(xText.getText());
                    int y = Integer.parseInt(yText.getText());
                    refreshVehicle.setCount(count);
                    refreshVehicle.setX(x);
                    refreshVehicle.setY(y);
                    orderListViewer.refresh();
                }catch (Exception e) {
                    MessageDialog.openInformation(getShell(), "��ʾ��", "���µ����ݲ���");
                    
                }
            }
            
        }
        super.buttonPressed(buttonId);
    }

    private void updatePreviewer() {
        StructuredSelection sel = (StructuredSelection)listViewer.getSelection();
        if (sel.isEmpty()) {
            previewer.setAnimateFile(null);
            return;
        }
        Vehicle t = (Vehicle)sel.getFirstElement();
        if(t.image != null){
            previewer.setAnimateFile(t.image.getAnimateFile(0));
            selectedTemplate = t.id;
            //text.setText(t.title);
            parentContainer.layout();
        }
        else{
            //TODO �Ѷ���Ԥ����������Ϊ��
        }
    }
    
    class ListLabelProvider implements ILabelProvider{

        public Image getImage(Object element) {
            return null;
        }

        public String getText(Object element) {
            return element.toString();
        }

        public void addListener(ILabelProviderListener listener) {}

        public void dispose() {}

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {}
    }
}
