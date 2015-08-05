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
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.DataObject;
import com.pip.game.data.GameArea;
import com.pip.game.data.GameAreaInfo;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.data.map.GamePatrolPath;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.area.GameMapViewer;
import com.pip.game.editor.property.ChooseNPCDialog.PickupNPCTool;
import com.pip.game.editor.property.ChooseNPCDialog.TreeContentProvider;
import com.pip.game.editor.property.ChooseNPCDialog.TreeLabelProvider;
import com.pip.game.editor.property.ChoosePatrolPathDialog.PickupPatrolPathTool;
import com.pip.game.editor.quest.GameAreaCache;
import com.pip.image.workshop.WorkshopPlugin;
import com.pip.mango.jni.GLGraphics;
import com.pip.mapeditor.MapViewer;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.data.MapFile;
import com.pip.mapeditor.tool.IMapEditTool;
import com.swtdesigner.SWTResourceManager;

public class ChoosePathDialog extends Dialog {
    
    
    class TreeLabelProvider extends LabelProvider {
        public String getText(Object element) {
            if (element instanceof ProjectData) {
                return "项目";
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
                // 根节点是ProjectData，第一层子节点是所有的游戏关卡
                List<DataObject> list = ((ProjectData)parentElement).getDataListByType(GameArea.class);
                List<DataObject> retList = new ArrayList<DataObject>();
                for (int i = 0; i < list.size(); i++) {
                    if (getChildren(list.get(i)).length > 0) {
                        retList.add(list.get(i));
                    }
                }
                return retList.toArray();
            } else if (parentElement instanceof GameArea) {
                // 关卡的下一层节点是游戏场景
                GameAreaInfo areaInfo = GameAreaCache.getAreaInfo(((GameArea)parentElement).id);
                if (areaInfo == null) {
                    return new Object[0];
                } else {
                    List<GameMapInfo> retList = new ArrayList<GameMapInfo>();
                    for (GameMapInfo mi : areaInfo.maps) {                       
                            retList.add(mi);                        
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
            return (element instanceof ProjectData || element instanceof GameArea);
        }
    }

private HashMap<GameArea, MapFile> mapCache = new HashMap<GameArea, MapFile>();
    
    private Text text;
    private TreeViewer treeViewer;
    private ListViewer listViewer;

    private int patrolPathId;
    private GameMapViewer mapViewer;
    private GameMapInfo mapInfo;
    private MapFile mapFile;
    private GameMap gameMap;
    private GamePatrolPath selectedObject;
    
    public int getPathId() {
        return patrolPathId;
    }

    public void setPathId(int sel) {
        this.patrolPathId = sel;
      
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public ChoosePathDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        text = new Text(container, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        text.setEditable(false);   
        
        treeViewer = new TreeViewer(container, SWT.BORDER | SWT.V_SCROLL);
        treeViewer.getTree().setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        treeViewer.setLabelProvider(new TreeLabelProvider());
        treeViewer.setContentProvider(new TreeContentProvider());
        treeViewer.setInput(ProjectData.getActiveProject());
        
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
        
        mapViewer = new GameMapViewer(container, SWT.NONE);
        GridData gd_mapViewer = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_mapViewer.heightHint = 500;
        mapViewer.setLayoutData(gd_mapViewer);
        
       
//        if(patrolPathId >= 0) {
//            for (GameMapObject obj : mapViewer.getMapInfo().objects) {
//                if (obj instanceof GamePatrolPath) {
//                    for(int i=0; i< ((GamePatrolPath)obj).path.size(); i++) {
//                        if((patrolPathId & 0xF) == ((GamePatrolPath)obj).id) {
//                            selectedObject = (GamePatrolPath)obj;
//                            text.setText(String.valueOf(patrolPathId));
//                            break;
//                        }
//                    }
//                }
//            }            
//        }        
                
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
        newShell.setText("选择镜头路径");
    }
    
    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);
    }
    

    
    private void updateMapViewer() {
       
        StructuredSelection sel = (StructuredSelection)treeViewer.getSelection();
        if (sel.isEmpty()) {
            
            mapInfo = null;
            mapFile = null;
            gameMap = null;
            mapViewer.setInput(null, null, null);
            mapViewer.setTool(null);
            mapViewer.redraw();
            return;
        }
        try {
            if (sel.getFirstElement() instanceof GameMapInfo) {
                
                mapInfo = (GameMapInfo)sel.getFirstElement();
                mapFile = mapCache.get(mapInfo.owner);
                if (mapFile == null) {
                    mapFile = new MapFile();
                    mapFile.load(mapInfo.owner.getFile(0));
                }
                mapViewer.setInput(mapFile.getMaps().get(mapInfo.id), mapInfo, mapInfo.owner.owner.config.mapFormats.get(0));
                mapViewer.setTool(new PickupPatrolPathTool());
                mapViewer.redraw();
            } 
        } catch (Exception e) {
            MessageDialog.openError(getShell(), "错误", e.toString());
        }
    }

    /**
     * 设置位置工具。
     * @author lighthu
     */
    class PickupPatrolPathTool implements IMapEditTool {
        // 是否正在拖动
        private boolean isDragging;
        // 最近一次检测到的鼠标位置
        private int lastX, lastY;
    
        /**
         * 缺省构造方法
         */
        public PickupPatrolPathTool() {
        }
        
        /**
         * 鼠标按下事件，当前鼠标点中位置被选择。
         * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
         * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
         * @param mask 按键状态掩码
         */
        public void mouseDown(int x, int y, int mask) {
            isDragging = true;                           
//            mapViewer.redraw();
        }
        
        /**
         * 鼠标抬起事件。
         * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
         * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
         * @param mask 按键状态掩码
         */
        public void mouseUp(int x, int y, int mask) {
            isDragging = false;
            selectedObject = detectObject(x, y);
            if(selectedObject != null) {
                patrolPathId = selectedObject.getGlobalID();                   
                text.setText(String.valueOf(patrolPathId));
            } else {
                patrolPathId = -1;
                text.setText("未指定");
            }
            mapViewer.redraw();
        }
        
        /**
         * 鼠标移动事件。被拖动的NPC跟随移动。
         * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
         * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
         */
        public void mouseMove(int x, int y) {
            lastX = x;
            lastY = y;
//            if (isDragging) {
                selectedObject = detectObject(x, y); 
//            }
            mapViewer.redraw();
        }
        
        /**
         * 绘制当前工具
         * @param gc
         */
        public void draw(GC gc) {
            // 绘制当前位置
                for (GameMapObject obj : mapViewer.getMapInfo().objects) {
                if (obj instanceof GamePatrolPath) {
                    
                    GamePatrolPath path = (GamePatrolPath)obj;
                    
                    for (int i = 0; i < path.path.size() - 1; i++) {
                        int[] point1 = path.path.get(i);
                        int[] point2 = path.path.get(i + 1);
                        
                        Rectangle rect = new Rectangle(point1[0] - 2, point1[1] - 2, 5, 5);
                        mapViewer.map2screen(rect);
                        
                        if(selectedObject == path) {
                            gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));                        
                        } else {
                            gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                        }
                        
                        gc.fillRectangle(rect);
                        
                        Point pt1 = new Point(point1[0], point1[1]);
                        Point pt2 = new Point(point2[0], point2[1]);
                        mapViewer.map2screen(pt1);
                        mapViewer.map2screen(pt2);
                        if(selectedObject != obj) {
                            gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                        } else {
                            gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                        }
                        gc.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
                    }
                    
                    if(path.path.size() > 0) {
                        int[] point1 = path.path.get(path.path.size() - 1);
                        Rectangle rect = new Rectangle(point1[0] - 2, point1[1] - 2, 5, 5);
                        mapViewer.map2screen(rect);
                        if(selectedObject == path) {
                            gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));    
                        } else {
                            gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                        }                    
                        gc.fillRectangle(rect);
                    }  
                }
            }      
        }
        
        /**
         * 绘制当前工具
         * @param gc
         */
        public void draw(GLGraphics gc) {
            // 绘制当前位置
                for (GameMapObject obj : mapViewer.getMapInfo().objects) {
                if (obj instanceof GamePatrolPath) {
                    
                    GamePatrolPath path = (GamePatrolPath)obj;
                    
                    for (int i = 0; i < path.path.size() - 1; i++) {
                        int[] point1 = path.path.get(i);
                        int[] point2 = path.path.get(i + 1);
                        
                        Rectangle rect = new Rectangle(point1[0] - 2, point1[1] - 2, 5, 5);
                        mapViewer.map2screen(rect);
                        
                        if(selectedObject == path) {
                            gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));                        
                        } else {
                            gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                        }
                        
                        gc.fillRect(rect);
                        
                        Point pt1 = new Point(point1[0], point1[1]);
                        Point pt2 = new Point(point2[0], point2[1]);
                        mapViewer.map2screen(pt1);
                        mapViewer.map2screen(pt2);
                        if(selectedObject != obj) {
                            gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                        } else {
                            gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));
                        }
                        gc.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
                    }
                    
                    if(path.path.size() > 0) {
                        int[] point1 = path.path.get(path.path.size() - 1);
                        Rectangle rect = new Rectangle(point1[0] - 2, point1[1] - 2, 5, 5);
                        mapViewer.map2screen(rect);
                        if(selectedObject == path) {
                            gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));    
                        } else {
                            gc.setColor(SWTResourceManager.getColor(SWT.COLOR_BLACK));
                        }                    
                        gc.fillRect(rect);
                    }  
                }
            }      
        }
        
        /**
         * 键按下事件。
         */
        public void onKeyDown(int keyCode) {}
        
        /**
         * 键松开事件。
         */
        public void onKeyUp(int keyCode) {}

        /**
         * 得到工具右键菜单。
         */
        public Menu getMenu() {
            return null;
        }
        
        public GamePatrolPath detectObject(int x, int y) {
            for (GameMapObject obj : mapViewer.getMapInfo().objects) {
                if (obj instanceof GamePatrolPath) {
                    for(int i=0; i< ((GamePatrolPath)obj).path.size() - 1; i++) {
                        int[] point1 = ((GamePatrolPath)obj).path.get(i);
                        int[] point2 = ((GamePatrolPath)obj).path.get(i + 1);
                        if(point1[0] == point2[0] && point1[1] == point2[1]) {
                            continue;
                        }
                        if(isNearest(x, y, point1[0], point1[1], point2[0], point2[1], 8)) {
                            return ((GamePatrolPath)obj);
                        }
                    }
                }
            }
            return null;
        }        
        
        public boolean isNearest(int x, int y, int startX, int startY, int endX, int endY, int nearest) {
            int[] param = makeLineParam(startX, startY, endX, endY);
            int a = param[0];
            int b = param[1];
            int c = param[2];
            double delta = Math.abs( (a*x+b*y+c) / Math.sqrt(x^2+y^2) );
            if(delta < nearest){
                return true;
            }
            
            return false;
            
        }
        
        /**
         * 获得两点确定的直线的标准式参数Ax+By+C = 0; ret[]{A,B,C}
         * @param x
         * @param y
         * @param x2
         * @param y2
         * @return
         */
        public int[] makeLineParam(int x, int y, int x2, int y2){
            int a = y - y2;
            int b = x2 - x;
            int c = x*y2 - x2*y;
            return new int[]{a,b,c};
        }
    }

}
