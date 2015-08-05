package com.pip.game.editor.property;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


import com.pip.game.data.DataObject;
import com.pip.game.data.GameArea;
import com.pip.game.data.GameAreaInfo;
import com.pip.game.data.GameMesh;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.map.GameMapObject;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.area.GameMapViewer;
import com.pip.game.editor.quest.GameAreaCache;
import com.pip.image.workshop.WorkshopPlugin;
import com.pip.mango.jni.GLGraphics;
import com.pip.mapeditor.MapEditor;
import com.pip.mapeditor.data.GameMap;
import com.pip.mapeditor.data.MapFile;
import com.pip.mapeditor.tool.IMapEditTool;
import com.swtdesigner.SWTResourceManager;

public class ChooseNPCDialog extends Dialog {
    private HashMap<GameArea, MapFile> mapCache = new HashMap<GameArea, MapFile>();
    
    private Text text;
    private String searchCondition;
    
    public final static byte MORENPC = 1;
    
    public final static byte ONENPC = 2;
    
    /**
     * �Ի������� 1Ϊѡ�񷵻�1��npcId; 2Ϊ���м����npc���ƣ���������
     */
    private byte type;
    /**
     * ���ص��ַ�������
     */
    private List<GameMapObject> npc = new  ArrayList<GameMapObject>();
    /**
     * Ҫչʾ��npc�����б�
     */
    private ListViewer listview;
    
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
            if (element instanceof GameMapNPC) {
                return ((GameMapNPC)element).id + ": " + ((GameMapNPC)element).name;
            }
            return super.getText(element);
        }
        public Image getImage(Object element) {
            if (element instanceof GameArea) {
                return WorkshopPlugin.getDefault().getImageRegistry().get("animate");
            } else if (element instanceof GameMapInfo) {
                return WorkshopPlugin.getDefault().getImageRegistry().get("map");
            } else if (element instanceof GameMapNPC) {
                return EditorPlugin.getDefault().getImageRegistry().get("npcicon");
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
                        if (getChildren(mi).length > 0) {
                            retList.add(mi);
                        }
                    }
                    return retList.toArray();
                }
            } else if (parentElement instanceof GameMapInfo) {
                // ��������һ��ڵ���NPC
                List<GameMapNPC> list = new ArrayList<GameMapNPC>();
                for (GameMapObject obj : ((GameMapInfo)parentElement).objects) {
                    if (obj instanceof GameMapNPC) {
                        GameMapNPC npc = (GameMapNPC)obj;
                        if (matchCondition(npc)) {
                            list.add(npc);
                        }
                    }
                }
                return list.toArray();
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
            } else if (element instanceof GameMapNPC) {
                return ((GameMapNPC)element).owner;
            }
            return null;
        }
        public boolean hasChildren(Object element) {
            return (element instanceof ProjectData || element instanceof GameArea || element instanceof GameMapInfo);
        }
    }
    private TreeViewer treeViewer;
    private int selectedNPC = -1;
    private GameMapViewer mapViewer;
    private GameMapInfo mapInfo;
    private MapFile mapFile;
    private GameMap gameMap;
    private boolean updating;
    
    private static int lastSelectedNPC = -1;
    
    /**
     * @return ����ѡ�е�npc, ģʽ2Ϊһ��npcID��
     */
    public int getSelectedNPC() {
        if(type == ONENPC){
            return selectedNPC;
        }
        return -1;
    }
    
    /**
     * @return ģʽ1Ϊһ��npc
     */
    public List<GameMapObject> getSelectMoreNpc(){
        return npc;
    } 
    public void setSelectedNPC(int selectedNPC) {
        setSelectedMoreNPC(new String[]{"" + selectedNPC});
//        this.selectedNPC = selectedNPC;
    }
    
    public void setSelectedMoreNPC(String[] npcId) {
        //this.selectedNPC = selectedNPC;
        for(int i = 0; i < npcId.length; i++){
            if(npcId[i] != null && !npcId[i].equals("")){
                if(GameMapObject.findByID(ProjectData.getActiveProject(), Integer.parseInt(npcId[i])) != null){
                    npc.add(GameMapObject.findByID(ProjectData.getActiveProject(), Integer.parseInt(npcId[i])));
                }
            }
        }
    }
    /**
     * @param ���ö��npc
     */
    public void setSelectNpc(List<GameMapObject> npc){
        this.npc = npc;
    }
    private boolean matchCondition(GameMapNPC npc) {
        if (searchCondition == null || searchCondition.length() == 0) {
            return true;
        }
        if (npc.name.indexOf(searchCondition) >= 0 || String.valueOf(npc.id).indexOf(searchCondition) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public ChooseNPCDialog(Shell parentShell, byte type) {
        super(parentShell);
        this.type  = type;
    }
    
    class ListContentProvider implements IStructuredContentProvider{

        public Object[] getElements(Object inputElement) {
            List el = (List)inputElement;
            return el.toArray();
        }

        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
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
    
    /**
     * Create contents of the dialog
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        
        
        Composite parentContainer = (Composite) super.createDialogArea(parent);
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
        
        
        Composite listContainer = new Composite(parentContainer, SWT.BORDER);
        listContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        listview = new ListViewer(listContainer, SWT.FILL | SWT.BORDER | SWT.V_SCROLL);
        listview.setContentProvider(new ListContentProvider());
        listview.setLabelProvider(new ListLabelProvider());
        listview.setInput(this.npc);
        final GridData gd_npcTemplateList = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_npcTemplateList.exclude = true;
        listview.getList().setBounds(0, 0, 1000, 800);
        listview.getList().setLayoutData(gd_npcTemplateList);
         
        listview.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                npc.remove(sel.getFirstElement());
                if(npc.size() == 0){
                    selectedNPC = -1;
                    lastSelectedNPC = -1;
                }
                listview.refresh();
                /*Object selObj = sel.getFirstElement();
                if (selObj instanceof ForbidItem) {
                    this.NpcName.remove(selObj);
                    listview.refresh();
                }*/
            }
                
        }
        );
        
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

        treeViewer = new TreeViewer(container, SWT.BORDER | SWT.V_SCROLL);
        final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gridData.heightHint = 200;
        treeViewer.getTree().setLayoutData(gridData);
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                if (sel.isEmpty()) {
                    return;
                }
                Object selObj = sel.getFirstElement();
                if (selObj instanceof GameMapNPC) {
                    /*buttonPressed(IDialogConstants.OK_ID);*/
                    selectNPC(selectedNPC);
                    if(type == ONENPC){
                        npc.clear();
                    }
                    npc.add(GameMapObject.findByID(ProjectData.getActiveProject(), selectedNPC));
                    listview.refresh();
                } else {
                    if (treeViewer.getExpandedState(selObj)) {
                        treeViewer.collapseToLevel(selObj, 1);
                    } else {
                        treeViewer.expandToLevel(selObj, 1);
                    }
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
        treeViewer.setInput(ProjectData.getActiveProject());
//        treeViewer.expandAll();
        
        mapViewer = new GameMapViewer(container, SWT.NONE);
        GridData gd_mapViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gd_mapViewer.heightHint = 600;
        mapViewer.setLayoutData(gd_mapViewer);

        if (selectedNPC != -1 && selectedNPC != 0) {
            selectNPC(selectedNPC);
        } else if (lastSelectedNPC != -1) {
            selectNPC(lastSelectedNPC);
        }

        return container;
    }
    
    protected void selectNPC(int npcID) {
        try {
            // �������NPC��tree�е�λ��
            GameMapNPC npc = (GameMapNPC)GameMapObject.findByID(ProjectData.getActiveProject(), npcID);
            if (npc != null) {
                searchCondition = npc.name;
                text.setText(searchCondition);
                text.selectAll();
                StructuredSelection sel = new StructuredSelection(npc);
                treeViewer.setSelection(sel);
            }
        } catch (Exception e) {
        }
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
        return new Point(728, 644);
    }
    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        if(type == MORENPC){
            newShell.setText("��ѡģʽѡ��NPC");
        }else if(type == ONENPC){
            newShell.setText("��ģʽѡ��NPC");
        }
        
        newShell.setSize(1200, 800);
    }
    
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            lastSelectedNPC = selectedNPC;
            
//            StructuredSelection sel = (StructuredSelection)listview.getSelection();
//            if (sel.isEmpty() || !(sel.getFirstElement() instanceof GameMapNPC)) {
//                selectedNPC = -1;
//            } else {
//                selectedNPC = ((GameMapNPC)sel.getFirstElement()).getGlobalID();
//                lastSelectedNPC = selectedNPC;
//            }
        }
        super.buttonPressed(buttonId);
    }
    
    private void updateMapViewer() {
        if (updating) {
            return;
        }
        StructuredSelection sel = (StructuredSelection)treeViewer.getSelection();
        if (sel.isEmpty()) {
            selectedNPC = -1;
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
                selectedNPC = -1;
                mapInfo = (GameMapInfo)sel.getFirstElement();
                mapFile = mapCache.get(mapInfo.owner);
                if (mapFile == null) {
                    mapFile = new MapFile();
                    mapFile.load(mapInfo.owner.getFile(0));
                }
                mapViewer.setInput(mapFile.getMaps().get(mapInfo.id), mapInfo, mapInfo.owner.owner.config.mapFormats.get(0));
                mapViewer.setTool(new PickupNPCTool());
                mapViewer.redraw();
            } else if (sel.getFirstElement() instanceof GameMapNPC) {
                GameMapNPC npc = (GameMapNPC)sel.getFirstElement();
                selectedNPC = npc.getGlobalID();
                mapInfo = npc.owner;
                mapFile = mapCache.get(mapInfo.owner);
                if (mapFile == null) {
                    mapFile = new MapFile();
                    mapFile.load(mapInfo.owner.getFile(0));
                }
                mapViewer.setInput(mapFile.getMaps().get(mapInfo.id), mapInfo, mapInfo.owner.owner.config.mapFormats.get(0));
                mapViewer.setTool(new PickupNPCTool());
                mapViewer.redraw();
            }
        } catch (Exception e) {
            MessageDialog.openError(getShell(), "����", e.toString());
        }
    }
    
    /**
     * ѡ��NPC���ߡ�
     * @author lighthu
     */
    class PickupNPCTool implements IMapEditTool {
        /**
         * ȱʡ���췽��
         */
        public PickupNPCTool() {
        }
        
        /**
         * ��갴���¼�����ǰ������λ�ñ�ѡ��
         * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
         * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
         * @param mask ����״̬����
         */
        public void mouseDown(int x, int y, int mask) {
            GameMapNPC npc = detectObject(x, y);
            if (npc != null && npc.getGlobalID() != selectedNPC) {
                selectedNPC = npc.getGlobalID();
                updating = true;
                try {
                    selectNPC(selectedNPC);
                } finally {
                    updating = false;
                }
                mapViewer.redraw();
            }
        }
        
        // ���ĳһλ�õĶ���
        private GameMapNPC detectObject(int x, int y) {
            for (GameMapObject obj : mapViewer.getMapInfo().objects) {
                if (obj instanceof GameMapNPC) {
                    GameMapNPC npc = (GameMapNPC)obj;
                    if (getObjectBounds(npc).contains(x, y)) {
                        return npc;
                    }
                }
            }
            return null;
        }
        
        // ȡ��һ����ͼ��������
        private Rectangle getObjectBounds(GameMapNPC npc) {
            if(mapViewer.getCachedNPCImage(npc)==null){
                return new Rectangle(npc.x,npc.y, 0,0);
            }
            Rectangle bounds = mapViewer.getCachedNPCImage(npc).getBounds(0, ((GameMesh)npc.template.image).getMeshConfig().getScalar());
            bounds.x += npc.x;
            bounds.y += npc.y;
            return bounds;
        }
        
        /**
         * ���̧���¼���
         * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
         * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
         * @param mask ����״̬����
         */
        public void mouseUp(int x, int y, int mask) {
        }
        
        /**
         * ����ƶ��¼������϶���NPC�����ƶ���
         * @param x ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
         * @param y ���λ���ڵ�ͼ�е����λ�ã�������Ļ���꣩
         */
        public void mouseMove(int x, int y) {
        }
        
        /**
         * ���Ƶ�ǰ����
         * @param gc
         */
        public void draw(GC gc) {
            // ���Ƶ�ǰλ��
            for (GameMapObject obj : mapInfo.objects) {
                if (obj instanceof GameMapNPC && obj.getGlobalID() == selectedNPC) {
                    Rectangle bounds = getObjectBounds((GameMapNPC)obj);
                    mapViewer.map2screen(bounds);
                    gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    gc.drawRectangle(bounds);
                }
            }
        }
        
        /**
         * ���Ƶ�ǰ����
         * @param gc
         */
        public void draw(GLGraphics gc) {
            // ���Ƶ�ǰλ��
            for (GameMapObject obj : mapInfo.objects) {
                if (obj instanceof GameMapNPC && obj.getGlobalID() == selectedNPC) {
                    Rectangle bounds = getObjectBounds((GameMapNPC)obj);
                    mapViewer.map2screen(bounds);
                    gc.setColor(SWTResourceManager.getColor(SWT.COLOR_RED));
                    gc.drawRect(bounds);
                }
            }
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
