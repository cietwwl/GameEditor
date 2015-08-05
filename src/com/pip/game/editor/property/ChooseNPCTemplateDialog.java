package com.pip.game.editor.property;

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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.DataObject;
import com.pip.game.data.NPCTemplate;
import com.pip.game.data.ProjectData;
import com.pip.game.data.RefreshNpc;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.property.ChooseNPCDialog.ListContentProvider;
import com.pip.game.editor.property.ChooseNPCDialog.ListLabelProvider;
import com.pip.game.editor.util.AnimatePreviewer;

public class ChooseNPCTemplateDialog extends Dialog {
    
    public final static int REFRESH_BUTTONID = 1000;
    /**
     * 可以指定个数的模式
     */
    public final static byte COUNT_TYPE  = 2;
    
    /**
     * 单一模式
     */
    public final static byte ONE_TYPE  = 1;
    
    /**
     * 使用类型
     */
    private  byte type;
    
    /**
     * 模板指定数量
     */
    private int count = 1;
    
    
    /**
     * 模板指定x
     */
    private int x  = 1;
    /**
     * 模板指定y
     */
    private int y = 1;
    
    
    /**
     * 增加数量对话框
     */
    private Text countText;
    
    
    /**
     * x范围
     */
    private Text xText;
    
    /**
     * y范围
     */
    private Text yText;
    
    
    /**
     * y范围
     */
    private List<RefreshNpc> refreshNpcList = new ArrayList<RefreshNpc>();
    
    private 
    class ListRefeshNpcContentProvider implements IStructuredContentProvider {
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
            List<DataObject> list = ((ProjectData)inputElement).getDataListByType(NPCTemplate.class);
            List<DataObject> retList = new ArrayList<DataObject>();
            for (int i = 0; i < list.size(); i++) {
                NPCTemplate t = (NPCTemplate)list.get(i);
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
    
    
    public List<RefreshNpc> getRefreshNpc(){
        return refreshNpcList;
    }
    
    public void setRefreshList(List<RefreshNpc> refreshNpcList){
        this.refreshNpcList = refreshNpcList;
    }
    
    private boolean matchCondition(NPCTemplate t) {
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
    public ChooseNPCTemplateDialog(Shell parentShell, byte type) {
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
            label2.setText("数量： ");
    
            countText = new Text(listContainer, SWT.BORDER);
            countText.addModifyListener(new ModifyListener() {
                public void modifyText(final ModifyEvent e) {
                    try{
                        count = Integer.parseInt(countText.getText());
                    }catch(Exception error){
                        MessageDialog.openInformation(getShell(), "提示：", "数量不对");
                    }
                }
            });
            countText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            
            
            final Label labelx = new Label(listContainer, SWT.NONE);
            labelx.setText("x：");
    
            xText = new Text(listContainer, SWT.BORDER);
            xText.addModifyListener(new ModifyListener() {
                public void modifyText(final ModifyEvent e) {
                    try{
                        x = Integer.parseInt(xText.getText());
                    }catch(Exception error){
                        MessageDialog.openInformation(getShell(), "提示：", "数字x不对");
                    }
                }
            });
            xText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            
            final Label labely = new Label(listContainer, SWT.NONE);
            labely.setText("y：");
    
            yText = new Text(listContainer, SWT.BORDER);
            yText.addModifyListener(new ModifyListener() {
                public void modifyText(final ModifyEvent e) {
                    try{
                        y = Integer.parseInt(yText.getText());
                    }catch(Exception error){
                        MessageDialog.openInformation(getShell(), "提示：", "数字y不对");
                    }
                }
            });
            yText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            
            
            orderListViewer = new ListViewer(listContainer, SWT.FILL | SWT.BORDER | SWT.V_SCROLL);
            orderListViewer.setContentProvider(new ListRefeshNpcContentProvider());
            orderListViewer.setLabelProvider(new ListLabelProvider());
            orderListViewer.setInput(this.refreshNpcList);
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
                    refreshNpcList.remove(sel.getFirstElement());
                    if(refreshNpcList.size() == 0){
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
        label.setText("查找：");
        
        
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
                    RefreshNpc npc = new RefreshNpc(ProjectData.getActiveProject());
                    npc.id = ((NPCTemplate)sel.getFirstElement()).id; 
                    npc.title = ((NPCTemplate)sel.getFirstElement()).title;
                    refreshNpcList.add(npc);
                    orderListViewer.refresh();
                    StructuredSelection selnpc = new StructuredSelection(npc);
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
                // 查找这个NPC在tree中的位置
                NPCTemplate t = (NPCTemplate)ProjectData.getActiveProject().findObject(NPCTemplate.class, selectedTemplate);
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
        createButton(parent, IDialogConstants.OK_ID, "确定", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
        if(type == COUNT_TYPE){
            createButton(parent, REFRESH_BUTTONID, "刷新数量", false);
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
            newShell.setText("选择NPC模板");
        }else{
            newShell.setText("选择NPC模板（需要指定目标数量）");
        }
        newShell.setSize(1200, 800);
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            StructuredSelection sel = (StructuredSelection)listViewer.getSelection();
            if (sel.isEmpty() || !(sel.getFirstElement() instanceof NPCTemplate)) {
                selectedTemplate = -1;
            } else {
                selectedTemplate = ((NPCTemplate)sel.getFirstElement()).id;
            }
        }else if(buttonId == REFRESH_BUTTONID){
            StructuredSelection sel = (StructuredSelection)orderListViewer.getSelection();
            if(sel.isEmpty() || !(sel.getFirstElement() instanceof RefreshNpc)){
                MessageDialog.openInformation(getShell(), "提示：", "没有目标");
            }else{
                RefreshNpc refreshNpc = (RefreshNpc)sel.getFirstElement();
                try{
                    int count = Integer.parseInt(countText.getText());
                    int x = Integer.parseInt(xText.getText());
                    int y = Integer.parseInt(yText.getText());
                    refreshNpc.setCount(count);
                    refreshNpc.setX(x);
                    refreshNpc.setY(y);
                    orderListViewer.refresh();
                }catch (Exception e) {
                    MessageDialog.openInformation(getShell(), "提示：", "更新的数据不对");
                    
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
        NPCTemplate t = (NPCTemplate)sel.getFirstElement();
        if(t.image != null){
            previewer.setAnimateFile(t.image.getAnimateFile(0));
            selectedTemplate = t.id;
            //text.setText(t.title);
            parentContainer.layout();
        }
        else{
            //TODO 把动画预览部分设置为空
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
