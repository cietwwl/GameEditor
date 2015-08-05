package com.pip.game.editor.property;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pip.game.data.map.GameMapExit;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.MultiTargetMapExit;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.editor.skill.IValueChanged;
import com.pip.propertysheet.PropertySheetEntry;
import com.pip.propertysheet.PropertySheetViewer;

public class MultiTargetMapExitDialog extends Dialog implements ISelectionChangedListener,IValueChanged{
    
    public ListViewer exitsGroupListViewer;
    public PropertySheetViewer exitPropertySheetViewer;
    GameMapInfo gameMapInfo;
    MultiTargetMapExit mapExit;
    
    class ListContentProvider implements IStructuredContentProvider{

        public Object[] getElements(Object inputElement) {
            List el = (List)inputElement;
            return el.toArray();
        }

        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    }
    
    class MyGameMapExitPropertySource implements IPropertySource{
        GameMapExit mapExit;
        IValueChanged changedHandle;
        
        public MyGameMapExitPropertySource(GameMapExit mapExit,IValueChanged changedHandle){
            this.mapExit = mapExit;
            this.changedHandle = changedHandle;
        }

        public Object getEditableValue() {
            return this;
        }

        public IPropertyDescriptor[] getPropertyDescriptors() {
            IPropertyDescriptor[] ret = new IPropertyDescriptor[11];
            ret[0] = new PropertyDescriptor("type", "类型");
            ret[1] = new PropertyDescriptor("id", "ID");
            ret[2] = new LocationPropertyDescriptor("targetLocation", "目标位置");
            ret[3] = new ComboBoxPropertyDescriptor("showName", "显示名称", new String[] { "是", "否" });
            ret[4] = new ComboBoxPropertyDescriptor("exitType", "通道类型", new String[] { "普通", "记录当前位置", "返回记录位置", "寻路用","自定义" });
            ret[5] = new TextPropertyDescriptor("positionVarName", "变量名称");
            ret[6] = new ConstraintsPropertyDescriptor("constraints", "通过限制");
            ret[7] = new TextPropertyDescriptor("constraintsDes", "限制描述");
            ret[8] = new TextPropertyDescriptor("name", "传送门名称");
            
            ret[9] = new ComboBoxPropertyDescriptor("whichFloor", "所在地图层", new String[] { "地面", "天空" });
            ret[10] = new MirrorSetPropertyDescriptor("mirrorSet", "所在相位", mapExit.owner);
            
            return ret;
        }

        public Object getPropertyValue(Object id) {
            if ("type".equals(id)) {
                return "传送点";
            } else if ("id".equals(id)) {
                //return mapExit.getGlobalID() + "(0x" + Integer.toHexString(mapExit.getGlobalID()) + ")";
                return mapExit.id;
            } else if ("targetLocation".equals(id)) {
                return new int[] { mapExit.targetMap,mapExit.targetX, mapExit.targetY };
            } else if ("showName".equals(id)) {
                return mapExit.showName ? 0 : 1;
            } else if ("exitType".equals(id)) {
                return new Integer(mapExit.exitType);
            } else if ("positionVarName".equals(id)) {
                return mapExit.positionVarName;
            } else if ("constraints".equals(id)) {
                return mapExit.constraints.toString();
            } else if ("constraintsDes".equals(id)) {
                return mapExit.constraintsDes;
            } else if("name".equals(id)){
                return mapExit.name;
            } else if("whichFloor".equals(id)) {
                return mapExit.layer;
            }else if ("mirrorSet".equals(id)) {
                return new Long(mapExit.mirrorSet);
            }else {
                throw new IllegalArgumentException();
            }
        }

        public boolean isPropertySet(Object id) {
            return false;
        }

        public void resetPropertyValue(Object id) {
            
        }

        public void setPropertyValue(Object id, Object value) {
            if ("targetLocation".equals(id)) {
                int[] newValue = (int[])value;
                if (newValue[0] != mapExit.targetMap || newValue[1] != mapExit.targetX || newValue[2] != mapExit.targetY) {
                    mapExit.targetMap = newValue[0];
                    mapExit.targetX = newValue[1];
                    mapExit.targetY = newValue[2];
                    updatePreview();
                }
            } else if ("showName".equals(id)) {
                boolean newValue = ((Integer)value).intValue() == 0;
                if (newValue != mapExit.showName) {
                    mapExit.showName = newValue;
                    updatePreview();
                }
            } else if ("exitType".equals(id)) {
                int newValue = ((Integer)value).intValue();
                if (mapExit.exitType != newValue) {
                    mapExit.exitType = newValue;
                    updatePreview();
                }
            } else if ("positionVarName".equals(id)) {
                String newValue = (String)value;
                if (!newValue.equals(mapExit.positionVarName)) {
                    mapExit.positionVarName = newValue;
                    updatePreview();
                }
            } else if ("constraints".equals(id)) {
                String newValue = (String)value;
                if (!newValue.equals(mapExit.constraints.toString())) {
                    mapExit.constraints = ExpressionList.fromString(newValue);
                    updatePreview();
                }
            }  else if ("constraintsDes".equals(id)) {
                String newValue = (String)value;
                if (!newValue.equals(mapExit.constraintsDes)) {
                    mapExit.constraintsDes = newValue;
                    updatePreview();
                }
            }   else if("name".equals(id)){
                String newValue = (String)value;
                if (!newValue.equals(mapExit.name)) {
                    mapExit.name = newValue;
                    updatePreview();
                } 
            } else if("whichFloor".equals(id)) {
                mapExit.layer = ((Integer)value).intValue();
                updatePreview();
            } else if ("mirrorSet".equals(id)) {
                long newValue = ((Long)value).longValue();
                if (newValue != mapExit.mirrorSet) {
                    mapExit.mirrorSet = newValue;
                    updatePreview();
                }
            }
        }
        
    }

    public MultiTargetMapExitDialog(Shell parent,GameMapInfo gameMapInfo,MultiTargetMapExit mapExit) {
        super(parent);
        this.gameMapInfo = gameMapInfo;
        this.mapExit = mapExit;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        
        final Group exitGroup = new Group(container,SWT.NONE);
        exitGroup.setText("传送门列表");
        exitGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,4,1));
        final GridLayout groupGridLayout = new GridLayout();
        groupGridLayout.numColumns = 3;
        exitGroup.setLayout(groupGridLayout);
        
        MenuManager mgr = new MenuManager();
        mgr.add(new Action("添加"){
            public void run(){
                onAdd();
            }
        });
        mgr.add(new Action("删除"){
            public void run(){
                onDelete();
            }
        });
        mgr.add(new Action("清空所有"){
            public void run(){
                onClearAll();
            }
        });
        
        exitsGroupListViewer = new ListViewer(exitGroup, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
        final GridData gd_effectList = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 2);
        gd_effectList.widthHint = 200;
        gd_effectList.heightHint = 400;
        exitsGroupListViewer.getList().setLayoutData(gd_effectList);
        exitsGroupListViewer.setContentProvider(new ListContentProvider());
        exitsGroupListViewer.addSelectionChangedListener(this);
        Menu menu = mgr.createContextMenu(exitsGroupListViewer.getList());
        exitsGroupListViewer.getList().setMenu(menu);
        
        exitPropertySheetViewer = new PropertySheetViewer(exitGroup, SWT.NONE | SWT.V_SCROLL | SWT.WRAP, true);
        PropertySheetEntry rootEntry = new PropertySheetEntry();
        exitPropertySheetViewer.setRootEntry(rootEntry);
        exitPropertySheetViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,1,2));
        ((GridData)exitPropertySheetViewer.getControl().getLayoutData()).exclude = false;
        Tree t = (Tree)exitPropertySheetViewer.getControl();
        t.getColumn(0).setWidth(200);
        t.getColumn(1).setWidth(300);
        
        exitsGroupListViewer.setInput(mapExit.exitList);
        exitsGroupListViewer.refresh();
        
        return container;
    }
    
    public void onAdd(){
        GameMapExit exit = new GameMapExit();
        exit.owner = gameMapInfo;
        exit.id = mapExit.exitList.size();
        mapExit.exitList.add(exit);
        exitsGroupListViewer.refresh();
        exitsGroupListViewer.setSelection(new StructuredSelection(exit));
        updatePreview();
    }
    
    public void onDelete(){
        IStructuredSelection selected = (IStructuredSelection)exitsGroupListViewer.getSelection();
        GameMapExit e = (GameMapExit)selected.getFirstElement();
        if (e != null) {
            mapExit.exitList.remove(e);
            exitsGroupListViewer.refresh();
            updatePreview();
        }
    }
    
    public void onClearAll(){
        mapExit.exitList.clear();
        exitsGroupListViewer.refresh();
        updatePreview();
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "确定", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
    }
    
    @Override
    protected Point getInitialSize() {
        return new Point(785, 520);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("编辑多目标传送点");
    }

    public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selected = (IStructuredSelection)event.getSelection();
        if (event.getSource() == exitsGroupListViewer) {
            GameMapExit e = (GameMapExit)selected.getFirstElement();
            if(e != null){
                exitPropertySheetViewer.setInput(new Object[] { new MyGameMapExitPropertySource (e,this)});
            }
        }        
    }
    
    private void updatePreview() {
        exitsGroupListViewer.refresh();
    }

    public void valueChanged(String id) {
        updatePreview();
    }

    public void valueError(String errorMessage) {
        //nth
    }
}
