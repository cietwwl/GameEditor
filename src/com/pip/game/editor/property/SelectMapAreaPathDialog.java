package com.pip.game.editor.property;

import java.util.HashMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.editor.area.GameMapViewer;
import com.pip.mango.jni.GLGraphics;
import com.pip.mapeditor.data.MapFile;
import com.pip.mapeditor.tool.IMapEditTool;
import com.swtdesigner.SWTResourceManager;

public class SelectMapAreaPathDialog extends Dialog {    
    private Text text;
    private int[] area = new int[5]; //mapid, x, y, w, h
    private GameMapViewer mapViewer;
    private GameMapInfo mapInfo;
    private MapFile mapFile;
    
    private static HashMap<Integer, MapFile> mapCache = new HashMap<Integer, MapFile>();
    
    public int[] getArea() {
        return area;
    }

    public void setArea(int[] sel) {
        this.area = sel;
    }

    /**
     * Create the dialog
     * @param parentShell
     */
    public SelectMapAreaPathDialog(Shell parentShell) {
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
        
        text.setText(area[1] + ", " + area[2] + ", " + area[3] + ", " + area[4]);
        
        mapViewer = new GameMapViewer(container, SWT.NONE);
        GridData gd_mapViewer = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_mapViewer.heightHint = 500;
        mapViewer.setLayoutData(gd_mapViewer);        

        mapInfo = GameMapInfo.findByID(ProjectData.getActiveProject(), area[0]);
        
        mapFile = mapCache.get(new Integer(area[0]));
        if(mapFile == null) {
            mapFile = new MapFile();
            try {
                mapFile.load(mapInfo.owner.getFile(0));
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }            
        }

        mapViewer.setInput(mapFile.getMaps().get(mapInfo.id), mapInfo, mapInfo.owner.owner.config.mapFormats.get(0));
        mapViewer.setTool(new PickupPatrolPathTool());
        mapViewer.redraw();
        
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
        newShell.setText("设置一个矩形区域");
    }
    
    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);
    }
    
    //东
    public static final Cursor LC_CURSOR_SIZEE = new Cursor(null, SWT.CURSOR_SIZEE);
    //南
    public static final Cursor LC_CURSOR_SIZES = new Cursor(null, SWT.CURSOR_SIZES);
    //西
    public static final Cursor LC_CURSOR_SIZEW = new Cursor(null, SWT.CURSOR_SIZEW);
    //北
    public static final Cursor LC_CURSOR_SIZEN = new Cursor(null, SWT.CURSOR_SIZEN);
    //东南
    public static final Cursor LC_CURSOR_SIZESE = new Cursor(null, SWT.CURSOR_SIZESE);
    //西南
    public static final Cursor LC_CURSOR_SIZESW = new Cursor(null, SWT.CURSOR_SIZESW);
    //东北
    public static final Cursor LC_CURSOR_SIZENE = new Cursor(null, SWT.CURSOR_SIZENE);
    //西北
    public static final Cursor LC_CURSOR_SIZENW = new Cursor(null, SWT.CURSOR_SIZENW);
    //箭头
    public static final Cursor LC_CURSOR_ARROW = new Cursor(null, SWT.CURSOR_ARROW);
    //移动
    public static final Cursor LC_CURSOR_SIZEALL = new Cursor(null, SWT.CURSOR_SIZEALL);
    
    /**
     * 设置位置工具。
     * @author lighthu
     */
    class PickupPatrolPathTool implements IMapEditTool {
        // 最近一次检测到的鼠标位置
        private int lastX, lastY;
        
        private boolean isMouseDown;
        private final int delta = 2; //距离矩形边界2个像素，则视为到达
        
        private int detect;
        
        private final int DETECT_NONE    = 0;
        private final int DETECT_NORTH   = 1;
        private final int DETECT_SOUTH   = 2;
        private final int DETECT_EAST    = 3;
        private final int DETECT_WEST    = 4;    
        private final int DETECT_NW      = 5;
        private final int DETECT_NE      = 6;
        private final int DETECT_SE      = 7;
        private final int DETECT_SW      = 8;
        private final int DETECT_ALL     = 9;
        
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
            mapViewer.redraw();            
            isMouseDown = true;
            
            if(detect == DETECT_NONE) {
                //新建
                area[1] = x;
                area[2] = y;
                area[3] = 0;
                area[4] = 0;
            }
        }
        
        /**
         * 鼠标抬起事件。
         * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
         * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
         * @param mask 按键状态掩码
         */
        public void mouseUp(int x, int y, int mask) {
            isMouseDown = false;
            mapViewer.setCursor(LC_CURSOR_ARROW);
            mapViewer.redraw();
        }
        
        /**
         * 鼠标移动事件。被拖动的NPC跟随移动。
         * @param x 鼠标位置在地图中的相对位置（不是屏幕坐标）
         * @param y 鼠标位置在地图中的相对位置（不是屏幕坐标）
         */
        public void mouseMove(int x, int y) {
            if(isMouseDown) {
                if(detect == 0) {
                    //新建
                    if(x > area[1] && y > area[2]) {
                        area[3] = x - area[1];
                        area[4] = y - area[2];
                    }                    
                } else if(detect == DETECT_ALL) {
                    //移动
                    area[1] += x - lastX;
                    area[2] += y - lastY;
                } else {
                    Rectangle rect = new Rectangle(area[1], area[2], area[3], area[4]);
                    this.doScale(mapViewer.getCursor(), x - lastX, y - lastY, rect);
                    area[1] = rect.x;
                    area[2] = rect.y;
                    area[3] = rect.width;
                    area[4] = rect.height;
                }
                text.setText(area[1] + ", " + area[2] + ", " + area[3] + ", " + area[4]);
            } else {
                mapViewer.setCursor(getCursor(x, y));                
            }
            
            lastX = x;
            lastY = y;
            mapViewer.redraw();
        }
        
        /**
         * 绘制当前工具
         * @param gc
         */
        public void draw(GC gc) {            
            Rectangle rect = new Rectangle(area[1], area[2], area[3], area[4]);
            mapViewer.map2screen(rect);
            gc.setAlpha(100);
            gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED)); 
            gc.fillRectangle(rect);
            
            gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));            
            gc.drawRectangle(rect);
        }
        
        /**
         * 绘制当前工具
         * @param gc
         */
        public void draw(GLGraphics gc) {
            Rectangle rect = new Rectangle(area[1], area[2], area[3], area[4]);
            mapViewer.map2screen(rect);
            gc.setColor(0x80FF0000);
            gc.fillRect(rect);
            gc.setColor(0xFF000000);
            gc.drawRect(rect);
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
        
        private boolean rectIn(int x1, int y1, int w1, int h1, int x2, int y2){
            if(x1 <= x2 && x1 + w1 >= x2 && y1 <= y2 && y1 + h1 >= y2){
                return true;
            }else{
                return false;
            }
        }
                
        public int detectCursor(int x, int y) {
            if(rectIn(area[1] - delta, area[2] - delta, delta * 2, delta * 2, x, y)) {
                return DETECT_NW;
            } else if(rectIn(area[1] + area[3] - delta, area[2] - delta, delta * 2, delta * 2, x, y)) {
                return DETECT_NE;
            } else if(rectIn(area[1] - delta, area[2] + area[4] - delta, delta * 2, delta * 2, x, y)) {
                return DETECT_SW;
            } else if(rectIn(area[1] + area[3] - delta, area[2] + area[4] - delta, delta * 2, delta * 2, x, y)) {
                return DETECT_SE;
            } else if(rectIn(area[1], area[2] - delta, area[3], delta * 2 + 1, x, y)) {
                return DETECT_NORTH;
            } else if(rectIn(area[1], (area[2] + area[4]) - delta, area[3], delta * 2 + 1, x, y)) {
                return DETECT_SOUTH;
            } else if(rectIn(area[1] - delta, area[2], delta * 2 + 1, area[4], x, y)) {
                return DETECT_WEST;
            } else if(rectIn((area[1] + area[3]) - delta, area[2], delta * 2 + 1, area[4], x, y)) {
                return DETECT_EAST;
            } else if(rectIn(area[1], area[2], area[3], area[4], x, y)) {
                return DETECT_ALL;
            } else {
                return DETECT_NONE;
            }
        }
        
        /**
         * sRect[8]
         * 东，南， 西，北，东南，西南，东北，西北
         * @return
         */
        public Cursor getCursor(int x, int y) {
            detect = detectCursor(x, y);
            switch(detect) {
                case DETECT_NONE:
                    return LC_CURSOR_ARROW;
                case DETECT_NORTH:
                    return LC_CURSOR_SIZEN;
                case DETECT_SOUTH:
                    return LC_CURSOR_SIZES;
                case DETECT_EAST:
                    return LC_CURSOR_SIZEE;
                case DETECT_WEST:
                    return LC_CURSOR_SIZEW;
                case DETECT_NW:
                    return LC_CURSOR_SIZENW;
                case DETECT_NE:
                    return LC_CURSOR_SIZENE;
                case DETECT_SE:
                    return LC_CURSOR_SIZESE;
                case DETECT_SW:
                    return LC_CURSOR_SIZESW;
                case DETECT_ALL:
                    return LC_CURSOR_SIZEALL;
                default:
                    return LC_CURSOR_ARROW;                        
            }
        }
        
        //计算输出rect
        public void doScale(Cursor cursor, int ex, int ey, Rectangle rect){     
            int x, y, w, h;
            x = rect.x;
            y = rect.y;
            w = rect.width;
            h = rect.height;
            
            if (cursor == LC_CURSOR_SIZEE) {//东
                if(w > 10 || ex < 0){
                    w = w + ex;
                 }          
            } else if(cursor == LC_CURSOR_SIZES) {//南
                if(h > 10 || ey > 0 ){
                     h = h + ey ;                
                 }
            } else if(cursor == LC_CURSOR_SIZEW) {//西
                if(w > 10 || ex > 0){
                    w = w - ex ;
                    x = x + ex ;
                 }
            } else if(cursor == LC_CURSOR_SIZEN) {//北
                if(h > 10 || ey < 0){
                     h = h - ey ;
                     y = y + ey;                 
                 }
            } else if(cursor == LC_CURSOR_SIZESE) {//东南
                 if((w > 10 && h > 10) || ex > 0 && ey > 0){
                     w = w + ex;
                     h = h + ey;                 
                 }
            } else if(cursor == LC_CURSOR_SIZESW) {//西南
                 if((w > 10 && h > 10) || ex < 0 || y > 0){
                     w = w - ex ;
                     h = h + ey;
                     x = x + ex ;
                     
                 }
            } else if(cursor == LC_CURSOR_SIZENE) {//东北
                 if((w > 10 && h > 10) || ex > 0 || ey < 0){
                     w = w + ex;
                     h = h - ey ;
                     y = y + ey ;                
                 }
            } else if(cursor == LC_CURSOR_SIZENW) {//西北
                 if((w > 10 && h > 10) || ex < 0 || ey < 0){
                     w = w - ex ;
                     h = h - ey ;
                     x = x + ex ;
                     y = y + ey ;
                     
                 }
            }
            rect.x = x;
            rect.y = y;
            rect.width = w;
            rect.height = h;    
        }
    }

}
