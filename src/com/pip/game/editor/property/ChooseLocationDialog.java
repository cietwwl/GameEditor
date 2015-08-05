package com.pip.game.editor.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.pip.game.data.DataObject;
import com.pip.game.data.GameArea;
import com.pip.game.data.GameAreaInfo;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.quest.Quest;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.area.GameMapViewer;
import com.pip.game.editor.quest.ExpressionDialog;
import com.pip.game.editor.quest.GameAreaCache;
import com.pip.game.editor.quest.TemplateManager;
import com.pip.image.workshop.WorkshopPlugin;
import com.pip.mango.jni.GLGraphics;
import com.pip.mango.jni.GLUtils;
import com.pip.mapeditor.MapEditor;
import com.pip.mapeditor.MapViewer;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.data.MapFile;
import com.pip.mapeditor.tool.IMapEditTool;
import com.pip.util.AutoSelectAll;
import com.pip.util.Utils;

public class ChooseLocationDialog extends Dialog {
    private HashMap<GameArea, MapFile> mapCache = new HashMap<GameArea, MapFile>();
    
    private Text text;
    private Text textCondition;
    private String searchCondition;
    
    private String condition;
    private int[] defaultConditionMapXY;
    private boolean tipOldSelectLocation = false; 
    class TreeLabelProvider extends LabelProvider {
        public String getText(Object element) {
            if (element instanceof ProjectData) {
                return "��Ŀ";
            }
            if (element instanceof GameArea) {
                return element.toString();
            }
            if (element instanceof GameMapInfo) {
                return ((GameMapInfo)element).id + ": " + ((GameMapInfo)element).name;
            }
            return super.getText(element);
        }
        public Image getImage(Object element) {
            if (element instanceof GameArea) {
                return WorkshopPlugin.getDefault().getImageRegistry().get("animate");
            } else if (element instanceof GameMapInfo) {
                return WorkshopPlugin.getDefault().getImageRegistry().get("map");
            }
            return null;
        }
    }
    class TreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
        public void dispose() {
        }
        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof ProjectData) {
                // ���ڵ���ProjectData����һ���ӽڵ������е���Ϸ�ؿ�
                List<DataObject> list = ((ProjectData)parentElement).getDataListByType(GameArea.class);
                List<DataObject> retList = new ArrayList<DataObject>();
                for (int i = 0; i < list.size(); i++) {
                    if (getChildren(list.get(i)).length > 0) {
                        retList.add(list.get(i));
                    }
                }
                return retList.toArray();
            } else if (parentElement instanceof GameArea) {
                // �ؿ�����һ��ڵ�����Ϸ����
                GameAreaInfo areaInfo = GameAreaCache.getAreaInfo(((GameArea)parentElement).id);
                if (areaInfo == null) {
                    return new Object[0];
                } else {
                    List<GameMapInfo> retList = new ArrayList<GameMapInfo>();
                    for (GameMapInfo mi : areaInfo.maps) {
                        if (matchCondition(mi)) {
                            retList.add(mi);
                        }
                    }
                    return retList.toArray();
                }
            }
            return new Object[0];
        }
        public Object getParent(Object element) {
            if (element instanceof ProjectData) {
                return null;
            } else if (element instanceof GameArea) {
                return ((GameArea)element).owner;
            } else if (element instanceof GameMapInfo) {
                return ((GameMapInfo)element).owner;
            }
            return null;
        }
        public boolean hasChildren(Object element) {
            return (element instanceof ProjectData || element instanceof GameArea || element instanceof GameMapInfo);
        }
    }
    private TreeViewer treeViewer;
    private Tree tree;
    private int[] location = new int[3];
    private GameMapViewer mapViewer;
    private GameMapInfo mapInfo;
    private MapFile mapFile;
    private GameMap gameMap;
    
    public int[] getLocation() {
        return location;
    }

    public void setLocation(int[] sel) {
        this.location = sel;
    }
    
    public String getCondition() {
        return condition;
    }
    
    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    private boolean matchCondition(GameMapInfo map) {
        if (searchCondition == null || searchCondition.length() == 0) {
            return true;
        }
        if (map.name.indexOf(searchCondition) >= 0 || String.valueOf(map.getGlobalID()).indexOf(searchCondition) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseLocationDialog(Shell parentShell, String condition) {
        super(parentShell);
        
        this.condition = condition;
    }
    public ChooseLocationDialog(Shell parentShell,String condition,String defaultCondition){
        super(parentShell);
        this.condition = condition;
//        <l>48,����ķѧ԰:93,75</l>
        if(defaultCondition != null && defaultCondition.length() > 7){
            int begin = 3;
            int pos = defaultCondition.indexOf(',');
            int defaultConditionMapGlobalId = Integer.parseInt(defaultCondition.substring(begin, pos));
            begin = pos + 1;
            pos = defaultCondition.indexOf(':');
            String defaultConditionMapName = defaultCondition.substring(begin, pos);
            begin = pos + 1;
            this.defaultConditionMapXY = Utils.stringToIntArray(defaultCondition.substring(begin, defaultCondition.length() - 4),',');
            tipOldSelectLocation = true;
            location[0] = defaultConditionMapGlobalId;
        }
    }
    
    public ChooseLocationDialog(Shell parentShell) {
        this(parentShell, null);
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        container.setLayout(gridLayout);

        final Label label = new Label(container, SWT.NONE);
        label.setText("���ң�");

        text = new Text(container, SWT.BORDER);
        text.addModifyListener(new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                searchCondition = text.getText();
                StructuredSelection sel = (StructuredSelection)treeViewer.getSelection();
                Object selObj = sel.isEmpty() ? null : sel.getFirstElement();
                treeViewer.refresh();
                treeViewer.expandAll();
                if (selObj != null) {
                    sel = new StructuredSelection(selObj);
                    treeViewer.setSelection(sel);
                }
            }
        });
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        new Label(container, SWT.NONE);

        treeViewer = new TreeViewer(container, SWT.BORDER);
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                Object selObj = sel.getFirstElement();
                if (treeViewer.getExpandedState(selObj)) {
                    treeViewer.collapseToLevel(selObj, 1);
                } else {
                    treeViewer.expandToLevel(selObj, 1);
                }
            }
        });
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                updateMapViewer();
            }
        });
        treeViewer.setLabelProvider(new TreeLabelProvider());
        treeViewer.setContentProvider(new TreeContentProvider());
        tree = treeViewer.getTree();
        final GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        gd_tree.heightHint = 400;
        tree.setLayoutData(gd_tree);
        treeViewer.setInput(ProjectData.getActiveProject());
        treeViewer.expandAll();
        
        mapViewer = new GameMapViewer(container, SWT.NONE);
        GridData gd_mapViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        gd_mapViewer.heightHint = 500;
        mapViewer.setLayoutData(gd_mapViewer);
        
        try {
            // ���������ͼ��tree�е�λ��
            GameMapInfo map = GameMapInfo.findByID(ProjectData.getActiveProject(), location[0]);
            if (map != null) {
                searchCondition = map.name;
                text.setText(searchCondition);
                text.selectAll();
                StructuredSelection sel = new StructuredSelection(map);
                treeViewer.setSelection(sel);
            } else {
                //linux�£������ִ����䣬�ῴ����mapViewer
                treeViewer.collapseAll();
            }
        } catch (Exception e) {
        }
        
        //�����༭
        if(condition != null) {
            final Label label_14 = new Label(container, SWT.NONE);
            label_14.setText("�����༭��");
            textCondition = new Text(container, SWT.BORDER);
            final GridData gd_textCondition = new GridData(SWT.FILL, SWT.CENTER, true, false);
            textCondition.setLayoutData(gd_textCondition);
            textCondition.addFocusListener(AutoSelectAll.instance);
            textCondition.setEnabled(false);
            textCondition.setText(ExpressionList.toNatureString(condition));
            
            final Button button_6 = new Button(container, SWT.NONE);
            button_6.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(final SelectionEvent e) {                
                    String newExpr = ExpressionDialog.open(textCondition.getShell(), condition, 
                            new QuestInfo(new Quest(ProjectData.getActiveProject())), TemplateManager.CONTEXT_SET_CONDITION);
                    if (newExpr != null) {
                        condition = newExpr;
                        textCondition.setText(ExpressionList.toNatureString(condition));
                    }
                }
            });
            
            button_6.setText("�༭...");
            new Label(container, SWT.NONE);
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
    }

    /**
     * Return the initial size of the dialog
     */
    @Override
    protected Point getInitialSize() {
        return new Point(920, 800);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("ѡ�񳡾�");
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            StructuredSelection sel = (StructuredSelection)treeViewer.getSelection();
            if (sel.isEmpty() || !(sel.getFirstElement() instanceof GameMapInfo)) {
                location = new int[] { -1, 0, 0 };
            }
        }
        super.buttonPressed(buttonId);
    }
    
    private void updateMapViewer() {
        StructuredSelection sel = (StructuredSelection)treeViewer.getSelection();
        if (sel.isEmpty() || !(sel.getFirstElement() instanceof GameMapInfo)) {
            mapInfo = null;
            mapFile = null;
            gameMap = null;
            mapViewer.setInput(null, null, null);
            mapViewer.setTool(null);
            mapViewer.redraw();
            return;
        }
        try {
            mapInfo = (GameMapInfo)sel.getFirstElement();
            mapFile = mapCache.get(mapInfo.owner);
            if (mapFile == null) {
                mapFile = new MapFile();
                mapFile.load(mapInfo.owner.getFile(0));
            }
            gameMap = mapFile.getMaps().get(mapInfo.id);
            mapViewer.setInput(mapFile.getMaps().get(mapInfo.id), mapInfo, mapInfo.owner.owner.config.mapFormats.get(0));
            mapViewer.setTool(new PickupLocationTool());
            mapViewer.redraw();
            if(tipOldSelectLocation){
                location[1] = defaultConditionMapXY[0] * gameMap.parent.getCellSize();
                location[2] = defaultConditionMapXY[1] * gameMap.parent.getCellSize();
                tipOldSelectLocation = false;
            }
        } catch (Exception e) {
            MessageDialog.openError(getShell(), "����", e.toString());
        }
    }

    /**
     * ����λ�ù��ߡ�
     * @author lighthu
     */
    class PickupLocationTool implements IMapEditTool {
        // �Ƿ������϶�
        private boolean isDragging;
        // ���һ�μ�⵽�����λ��
        private int lastX, lastY;
    
        /**
         * ȱʡ���췽��
         */
        public PickupLocationTool() {
        }
        
        /**
         * ��갴���¼�����ǰ������λ�ñ�ѡ��
         * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
         * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
         * @param mask ����״̬����
         */
        public void mouseDown(int x, int y, int mask) {
            isDragging = true;
            location[0] = mapInfo.getGlobalID();
            location[1] = x;
            location[2] = y;
            mapViewer.redraw();
        }
        
        /**
         * ���̧���¼���
         * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
         * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
         * @param mask ����״̬����
         */
        public void mouseUp(int x, int y, int mask) {
            isDragging = false;
            location[0] = mapInfo.getGlobalID();
            location[1] = x;
            location[2] = y;
            mapViewer.redraw();
        }
        
        /**
         * ����ƶ��¼������϶���NPC�����ƶ���
         * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
         * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
         */
        public void mouseMove(int x, int y) {
            lastX = x;
            lastY = y;
            if (isDragging) {
                location[1] = x;
                location[2] = y;
            }
            mapViewer.redraw();
        }
        
        /**
         * ���Ƶ�ǰ����
         * @param gc
         */
        public void draw(GC gc) {
            // ���Ƶ�ǰλ��
            if (location[0] == mapInfo.getGlobalID()) {
                Image img = EditorPlugin.getDefault().getImageRegistry().get("flag");
                Point pt = new Point(location[1], location[2]);
                mapViewer.map2screen(pt);
                gc.drawImage(img, pt.x - 10, pt.y - 33);
                
                String str = mapInfo.name + "(" + location[1] + "," + location[2] + ")";
                Point size = mapViewer.getSize();
                Point ts = gc.textExtent(str);
                gc.setForeground(MapViewer.invert(mapViewer.getBackground()));
                gc.setBackground(mapViewer.getBackground());
                gc.drawRectangle(1, size.y - ts.y - 8, ts.x + 7, ts.y + 6);
                gc.drawText(str, 4, size.y - ts.y - 5);
            }
    
            // ��������
            String coordStr = lastX + "," + lastY + "," + (lastX / gameMap.parent.getCellSize()) + "," + (lastY / gameMap.parent.getCellSize());
            Point size = mapViewer.getSize();
            Point ts = gc.textExtent(coordStr);
            gc.setForeground(MapViewer.invert(mapViewer.getBackground()));
            gc.setBackground(mapViewer.getBackground());
            gc.drawRectangle(size.x - ts.x - 9, size.y - ts.y - 8, ts.x + 7, ts.y + 6);
            gc.drawText(coordStr, size.x - ts.x - 5, size.y - ts.y - 5);
        }
        
        /**
         * ���Ƶ�ǰ����
         * @param gc
         */
        public void draw(GLGraphics gc) {
            // ���Ƶ�ǰλ��
            if (location[0] == mapInfo.getGlobalID()) {
                Image img = EditorPlugin.getDefault().getImageRegistry().get("flag");
                Point pt = new Point(location[1], location[2]);
                mapViewer.map2screen(pt);
                gc.drawTexture(GLUtils.loadImage(img), 0, 0, pt.x - 10, pt.y - 33);
                
                String str = mapInfo.name + "(" + location[1] + "," + location[2] + ")";
                Point size = mapViewer.getSize();
                Point ts = gc.textExtent(str);
                gc.setColor(MapViewer.invert(mapViewer.getBackground()));
                gc.drawRect(1, size.y - ts.y - 8, ts.x + 7, ts.y + 6);
                gc.drawText(str, 4, size.y - ts.y - 5);
            }
    
            // ��������
            String coordStr = lastX + "," + lastY + "," + (lastX / gameMap.parent.getCellSize()) + "," + (lastY / gameMap.parent.getCellSize());
            Point size = mapViewer.getSize();
            Point ts = gc.textExtent(coordStr);
            gc.setColor(MapViewer.invert(mapViewer.getBackground()));
            gc.drawRect(size.x - ts.x - 9, size.y - ts.y - 8, ts.x + 7, ts.y + 6);
            gc.drawText(coordStr, size.x - ts.x - 5, size.y - ts.y - 5);
        }
        
        /**
         * �������¼���
         */
        public void onKeyDown(int keyCode) {}
        
        /**
         * ���ɿ��¼���
         */
        public void onKeyUp(int keyCode) {}

        /**
         * �õ������Ҽ��˵���
         */
        public Menu getMenu() {
            return null;
        }

        public void mouseDoubleClick(int x, int y) {
            // TODO Auto-generated method stub
            
        }
    }
    @Override
    public boolean close() {
        this.mapViewer.dispose();
        this.mapViewer = null;
        this.mapFile = null;
        this.mapInfo = null;
        MapEditor.imageCache.clear();
        System.gc();
        return super.close();
    }
}
