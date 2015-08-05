package com.pip.game.editor.skill;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import com.pip.game.data.ProjectData;
import com.pip.game.data.item.ItemDefData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.EffectConfig;
import com.pip.game.data.skill.EffectConfigSet;
import com.pip.game.data.skill.EffectParamRef;
import com.pip.game.data.skill.SkillConfig;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.property.BuffSetCellEditor;
import com.pip.game.editor.property.ChangeCellEditor;
import com.pip.game.editor.property.ChooseYesNoCellEditor;
import com.pip.game.editor.property.ItemSetCellEditor;
import com.pip.game.editor.property.LocationCellEditor;
import com.pip.game.editor.property.ShapeCellEditor;
import com.pip.game.editor.property.ShapeData;
import com.pip.game.editor.property.SkillSetCellEditor;
import com.pip.game.editor.property.StateCellEditor;
import com.pip.game.editor.property.StringCellEditor;
import com.pip.game.editor.util.FloatCellEditorValidator;
import com.pip.game.editor.util.IntegerCellEditorValidator;
import com.swtdesigner.SWTResourceManager;

/**
 * 效果集合编辑器。
 * @author lighthu
 */
public class EffectConfigSetEditor extends Composite {
    /*
     * 参数表编辑控制。
     */
    class ParamListCellModifier implements ICellModifier {
        public boolean canModify(Object element, String property) {
            int index = Integer.parseInt(property.substring(1));
            return index > 0;
        }
        
        /**
         * 取得某个格子的编辑目标对象。
         */
        public Object getValue(Object element, String property) {
            int index = Integer.parseInt(property.substring(1));
            int level = ((Integer)element).intValue();
            if (index == 0) {
                return null;
                
            }
            try {
                // 目前除location编辑器外，所有编辑器都接收String类型的数据
                EffectParamRef paramRef = editObject.getParamAt(index - 1);
                if (paramRef.getParamClass() == int[].class) {
                    return paramRef.getParamValue(level);
                } else {
                    return String.valueOf(paramRef.getParamValue(level));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 修改参数。
         */
        public void modify(Object element, String property, Object value) {
            TableItem ti = (TableItem)element;
            if (ti.getData() instanceof Integer) {
                int level = ((Integer)ti.getData()).intValue();
                int index = Integer.parseInt(property.substring(1));
                if (index == 0) {
                    return;
                }
                try {
                    EffectParamRef paramRef = editObject.getParamAt(index - 1);
                    boolean modified = false;
                    Class cls = paramRef.getParamClass();
                    // 目前所有编辑器都返回String类型的数据，这里需要根据类型转换
                    // 编辑整数或浮点数时，如果输入值以字符a开头，则特殊处理。如果在第一行输入，
                    // 则设置整列为新值，如果在第二行后输入，则后面的行等比递增。
                    if (cls == Integer.class) {
                        String s = (String)value;
                        if (s.startsWith("a")) {
                            modified = paramRef.autoSetParamValues(level, new Integer(s.substring(1)));
                        } else {
                            modified = paramRef.setParamValue(level, new Integer((String)value));
                        }
                    } else if (cls == Float.class) {
                        String s = (String)value;
                        if (s.startsWith("a")) {
                            modified = paramRef.autoSetParamValues(level, new Float(s.substring(1)));
                        } else {
                            modified = paramRef.setParamValue(level, new Float((String)value));
                        }
                    } else if (cls == String.class) {
                        modified = paramRef.autoSetParamValues(level, value);
                    } else if (cls == BuffConfig.class) {
                        modified = paramRef.autoSetParamValues(level, new Integer((String)value));
                    } else if (cls == SkillConfig[].class) {
                        modified = paramRef.autoSetParamValues(level, value);
                    } else if (cls == BuffCell.class) {
                        modified = paramRef.autoSetParamValues(level, value);
                    } else if (cls == ChangeCell.class) {
                        modified = paramRef.autoSetParamValues(level, value);
                    } else if (cls == ParamIndicator.class) {
                        modified = paramRef.autoSetParamValues(level, value);
                    } else if (cls == int[].class) {
                        modified = paramRef.autoSetParamValues(level, value);
                    } else if (cls == MultiConditions.class) {
                        modified = paramRef.autoSetParamValues(level, value);
                    } else if (cls == ChooseYesOrNo.class) {
                        modified = paramRef.autoSetParamValues(level, (String)value);
					} else if (cls == ItemDefData[].class) {
						modified = paramRef.autoSetParamValues(level, (String)value);
					} else if (cls == ShapeData.class) {
						modified = paramRef.autoSetParamValues(level, (String)value);
					} else if(cls.newInstance() instanceof StringCell){
                        modified = paramRef.autoSetParamValues(level, value);
                    } else {
                        modified = paramRef.setParamValue(level, value);
                    }
                    if (modified) {
                        fireModified();
                        paramListViewer.refresh();
//                        paramListViewer.update(ti.getData(), null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * 参数表文本。
     */
    class ParamListLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            int level = ((Integer)element).intValue();
            if (columnIndex == 0) {
                return level + "级";
            } else {
                try {
                    EffectParamRef paramRef = editObject.getParamAt(columnIndex - 1);
                    Class cls = paramRef.getParamClass();
                    if (cls == BuffConfig.class) {
                        int id = ((Integer)paramRef.getParamValue(level)).intValue();
                        BuffConfig bc = (BuffConfig)ProjectData.getActiveProject().findObject(BuffConfig.class, id);
                        if (bc != null) {
                            return bc.toString();
                        } else {
                            return "无效BUFF";
                        }
                    } else if (cls == SkillConfig[].class) {
                        String idList = (String)paramRef.getParamValue(level);
                        return SkillConfig.toString(ProjectData.getActiveProject(), idList);
                    } else if (cls == BuffCell.class) {
                        String idList = (String)paramRef.getParamValue(level);
                        return BuffConfig.toString(ProjectData.getActiveProject(), idList);
                    } else if (cls == ParamIndicator.class) {
                        return ((ParamIndicator)paramRef.getParamValue(level)).toString(ProjectData.getActiveProject());
                    } else if (cls == int[].class) {
                        int[] loc = (int[])paramRef.getParamValue(level);
                        return GameMapInfo.locationToString(ProjectData.getActiveProject(), loc, false);
//                    } else if(cls==MultiConditions.class){
//                       return (String)paramRef.getParamValue(level);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
                   } else if (cls == ItemDefData[].class){
                        String strValue = (String)paramRef.getParamValue(level);
                        List<ItemDefData> list = ItemDefData.parseFromString(strValue);
                        if(list!=null){
                            return ItemDefData.getDescString(list, true);
                        }else{
                            return "无效物品";
                        }
                    } else if (cls == ShapeData.class){
                        String strValue = (String)paramRef.getParamValue(level);
                        ShapeData data = ShapeData.parseShapeDataFromString(strValue);
                        if(data!=null){
                        	return data.toString();	
                        }else{
                        	return "";
                        }
                    } else {
                        return String.valueOf(paramRef.getParamValue(level));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }
        }
        public Image getColumnImage(Object element, int columnIndex) {
            if (columnIndex == 0) {
                try {
                    return EditorPlugin.getDefault().getImageRegistry().get("empty");
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }
    }
    
    /*
     * 参数表内容，每行的对象是值为 i + 1的Integer。
     */
    class ParamListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            Object[] ret = new Object[editObject.getLevelCount()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new Integer(i + 1);
            }
            return ret;
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    /*
     * 可用类型表内容。每行是值为i的Integer。
     */
    class TypeListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            Object[] ret = new Object[allowedEffects.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new Integer(i);
            }
            return ret;
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    /*
     * 可用类型表文本
     */
    class TypeListLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            int index = ((Integer)element).intValue();
            int type = allowedEffects[index];
            return getTypeName(type);
        }
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
    
    /*
     * 已选效果表内容。每行是值为i的Integer。
     */
    class SelectedListContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            Object[] ret = new Object[editObject.effects.size()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new Integer(i);
            }
            return ret;
        }
        public void dispose() {
        }
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }
    
    /*
     * 已选效果表文本。
     */
    class SelectedListLabelProvider extends LabelProvider {
        public String getText(Object element) {
            int index = ((Integer)element).intValue();
            try {
                String str = editObject.effects.get(index).getTypeName();
                if (str.length() == 0) {
                    str = "<通用参数>";
                }
                str = filterName(str);
                return str;
            } catch(Exception e) {
                e.printStackTrace();
            }
            return "出错了...";
        }
        public Image getImage(Object element) {
            return null;
        }
    }

    private TableViewer typeListViewer;
    private TableViewer paramListViewer;
    private Table paramList;
    private Table typeList;
    private EffectConfigSet editObject = new EffectConfigSet();
    private ModifyListener listener = null;
    private int[] allowedEffects = null;
    private ListViewer selectedEffectListViewer;
    private org.eclipse.swt.widgets.List selectedEffectList;
    private int columnCount = 0;
    private int highlightStart = 0;
    private int highlightStop = 0;
    
    public int mode = 0; // 0 - 用于技能，1 - 用于BUFF

    /**
     * Create the composite
     * @param parent
     * @param style
     */
    public EffectConfigSetEditor(Composite parent, int style) {
        super(parent, style);
        
        allowedEffects = new int[ProjectData.getActiveProject().effectConfigManager.TYPE_CLASSES.size()];
        Set<Integer> clzes = ProjectData.getActiveProject().effectConfigManager.TYPE_CLASSES.keySet();
        Integer[] keys = new Integer[clzes.size()];
        clzes.toArray(keys);
        for( int i=0; i<allowedEffects.length; i++){
            allowedEffects[i] = keys[i].intValue();
        }
        Arrays.sort(allowedEffects);
        
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        setLayout(gridLayout);

        selectedEffectListViewer = new ListViewer(this, SWT.BORDER);
        selectedEffectListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                updateHighlight();
                paramListViewer.refresh();
            }
        });
        selectedEffectListViewer.setContentProvider(new SelectedListContentProvider());
        selectedEffectListViewer.setLabelProvider(new SelectedListLabelProvider());
        selectedEffectList = selectedEffectListViewer.getList();
        final GridData gd_selectedEffectList = new GridData(SWT.FILL, SWT.FILL, false, false);
        gd_selectedEffectList.heightHint = 140;
        selectedEffectList.setLayoutData(gd_selectedEffectList);
        selectedEffectListViewer.setInput(this);
        selectedEffectListViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)selectedEffectListViewer.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                int index = ((Integer)sel.getFirstElement()).intValue();
                if (index == 0) {
                    return;
                }
                removeEffect(index);
                fireModified();
            }
        });

        paramListViewer = new TableViewer(this, SWT.FULL_SELECTION | SWT.BORDER) {
            protected ViewerRow getViewerRowFromItem(Widget item) {
                ViewerRow ret = super.getViewerRowFromItem(item);
                for (int i = 0; i < columnCount; i++) {
                    if (i >= highlightStart && i < highlightStop) {
                        ret.getCell(i + 1).setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    } else {
                        ret.getCell(i + 1).setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                    }
                }
                return ret;
            }
        };
        paramListViewer.setLabelProvider(new ParamListLabelProvider());
        paramListViewer.setContentProvider(new ParamListContentProvider());
        paramList = paramListViewer.getTable();
        paramList.setLinesVisible(true);
        paramList.setHeaderVisible(true);
        final GridData gd_paramList = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);

        paramList.setLayoutData(gd_paramList);

        final TableColumn levelColumn = new TableColumn(paramList, SWT.NONE);
        levelColumn.setWidth(57);
        levelColumn.setText("级别");

        typeListViewer = new TableViewer(this, SWT.FULL_SELECTION | SWT.BORDER);
        typeListViewer.setContentProvider(new TypeListContentProvider());
        typeListViewer.setLabelProvider(new TypeListLabelProvider());
        typeList = typeListViewer.getTable();
        typeList.setLinesVisible(true);
        typeList.setHeaderVisible(true);
        final GridData gd_typeList = new GridData(SWT.FILL, SWT.FILL, false, true);
        gd_typeList.widthHint = 234;
        typeList.setLayoutData(gd_typeList);
        typeListViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)typeListViewer.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                int index = ((Integer)sel.getFirstElement()).intValue();
                int type = allowedEffects[index];
                addEffect(type);
                fireModified();
            }
        });
        typeListViewer.setInput(this);

        final TableColumn effectTypeColumn = new TableColumn(typeList, SWT.NONE);
        effectTypeColumn.setWidth(230);
        effectTypeColumn.setText("可用效果");

        editObject.setLevelCount(10);
        refresh();
    }
    
    public void setAllowedEffects(int[] arr) {
        allowedEffects = new int[arr.length];
        System.arraycopy(arr, 0, allowedEffects, 0, arr.length);
        for (int i = 0; i < allowedEffects.length - 1; i++) {
            for (int j = i + 1; j < allowedEffects.length; j++) {
                String name1 = getTypeName(allowedEffects[i]);
                String name2 = getTypeName(allowedEffects[j]);
                if (name1.compareTo(name2) > 0) {
                    int t = allowedEffects[i];
                    allowedEffects[i] = allowedEffects[j]; 
                    allowedEffects[j] = t;
                }
            }
        }
        typeListViewer.refresh();
    }
    
    public void addModifyListener(ModifyListener l) {
        this.listener = l;
    }
    
    private void fireModified() {
        if (listener != null) {
            Event e = new Event();
            e.widget = this;
            ModifyEvent event = new ModifyEvent(e);
            listener.modifyText(event);
        }
    }
    
    public void setEditObject(EffectConfigSet newSet) {
         editObject = newSet;
         refresh();
    }
    
    public EffectConfigSet getEditObject() {
        return editObject;
    }
    
    public void refresh() {
        updateTypeList();
        updateParamList();
    }
    
    private void updateTypeList() {
        selectedEffectListViewer.refresh();
    }
    
    private void updateHighlight() {
        List<EffectParamRef> allParams = editObject.getAllParams();
        
        columnCount = allParams.size();
        int selIndex = this.selectedEffectList.getSelectionIndex();
        highlightStart = 0;
        highlightStop = 0;
        int pos = 0;
        for (int i = 0; i < editObject.effects.size(); i++) {
            int thisLen = editObject.effects.get(i).getParamCount();
            if (i == selIndex) {
                highlightStart = pos;
                highlightStop = pos + thisLen;
            }
            pos += thisLen;
        }
    }
    
    private void updateParamList() {
        List<EffectParamRef> allParams = editObject.getAllParams();
        updateHighlight();
        
        // 重新设置参数列
        String[] propertyNames = new String[allParams.size() + 1];
        CellEditor[] columnEditors = new CellEditor[allParams.size() + 1];
        propertyNames[0] = "c0";
        columnEditors[0] = new TextCellEditor(paramList);
        Label l = new Label(this.getShell(), SWT.NONE);
        for (int i = 0; i < allParams.size(); i++) {
            String pname = allParams.get(i).getParamName();
            if (i + 1 < paramList.getColumnCount()) {
                paramList.getColumn(i + 1).setText((i + 1) + "." + pname);
            } else {
                TableColumn newColumn = new TableColumn(paramList, SWT.NONE);
                newColumn.setText((i + 1) + "." + pname);
                l.setText(" "+newColumn.getText()+" ");
                newColumn.setWidth(l.computeSize(-1, -1).x );
//                newColumn.setWidth(100);
            }
            propertyNames[i + 1] = "c" + (i + 1);
            try {
                Class cls = allParams.get(i).getParamClass();
                if (cls == Integer.class) {
                    columnEditors[i + 1] = new TextCellEditor(paramList);
                    columnEditors[i + 1].setValidator(new IntegerCellEditorValidator());
                }
                else if (cls == Float.class) {
                    columnEditors[i + 1] = new TextCellEditor(paramList);
                    columnEditors[i + 1].setValidator(new FloatCellEditorValidator());
                }
                else if (cls == String.class) {
                    columnEditors[i + 1] = new TextCellEditor(paramList);
                }
                else if (cls == BuffConfig.class) {
                    columnEditors[i + 1] = new BuffConfigCellEditor(paramList, ProjectData.getActiveProject());
                }
                else if (cls == SkillConfig[].class) {
                    columnEditors[i + 1] = new SkillSetCellEditor(paramList);
                }
                else if (cls == ParamIndicator.class) {
                    columnEditors[i + 1] = new ParamIndicatorCellEditor(paramList);
                }
                else if (cls == MultiConditions.class) {
                    columnEditors[i + 1] = new ConditionsCellEditor(paramList);
                }
                else if (cls == BuffCell.class) {
                    columnEditors[i + 1] = new BuffSetCellEditor(paramList);
                }
                else if (cls == ChangeCell.class) {
                    columnEditors[i + 1] = new ChangeCellEditor(paramList);
                }
                else if (cls == ChooseYesOrNo.class) {
                    columnEditors[i + 1] = new ChooseYesNoCellEditor(paramList);
                }
                else if (cls == int[].class) {
                    columnEditors[i + 1] = new LocationCellEditor(paramList);
                }
                else if (cls == StateCellEditor.class) {
                    columnEditors[i + 1] = new StateCellEditor(paramList);
                }
                else if (cls == ItemDefData[].class){
                    columnEditors[i + 1] = new ItemSetCellEditor(paramList, true);
                }
                else if (cls == ShapeData.class){
                    columnEditors[i + 1] = new ShapeCellEditor(paramList);
                }
                else if (cls.newInstance() instanceof StringCell) {
                    StringCellEditor sce = new StringCellEditor(paramList);
                    sce.setLabels(((StringCell) cls.newInstance()).getConfig());
                    columnEditors[i + 1] = sce;
                }
                else {
                    columnEditors[i + 1] = new TextCellEditor(paramList);
                }
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        l.dispose();
        while (paramList.getColumnCount() > allParams.size() + 1) {
            paramList.getColumn(paramList.getColumnCount() - 1).dispose();
        }
        paramListViewer.setColumnProperties(propertyNames);
        paramListViewer.setCellModifier(new ParamListCellModifier());
        paramListViewer.setCellEditors(columnEditors);
        
        paramListViewer.setInput(editObject);
        paramListViewer.refresh();
    }
    
    private void addEffect(int type) {
        try {
            EffectConfig eff = ProjectData.getActiveProject().effectConfigManager.create(type, editObject.getLevelCount());
            editObject.addEffect(eff);
            refresh();
        } catch (Exception e) {
            MessageDialog.openError(getShell(), "Error", "创建效果出错:\n"+e);
            e.printStackTrace();
        }
    }
    
    private void removeEffect(int index) {
        editObject.effects.remove(index);
        refresh();
    }
    
    private String getTypeName(int type) {
        Class clz = ProjectData.getActiveProject().effectConfigManager.TYPE_CLASSES.get(type);
        try{
            String name = ProjectData.getActiveProject().effectConfigManager.getTypeNames(clz)[0];
            name = filterName(name);
            return name;
        } catch(Exception e) {
            return "出错了...";
        }
    }
    
    private String filterName(String name) {
        if (name.contains("|")) {
            return name.split("\\|")[mode];
        }
        return name;
    }
    
    public void setLeftPanelShow(boolean show){
		if (typeList.getLayoutData() instanceof GridData) {
			((GridData) typeList.getLayoutData()).exclude = !show;
		}
		if (selectedEffectList.getLayoutData() instanceof GridData) {
			((GridData) selectedEffectList.getLayoutData()).exclude = !show;
		}
		this.typeList.setVisible(show);
		this.selectedEffectList.setVisible(show);
    }
}
