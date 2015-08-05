package com.pip.game.editor.talent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.SkillConfig;
import com.pip.util.Utils;
import com.pipimage.image.PipImage;

/**
 * 
 * @author jhkang
 * 
 */
public class TalentTreeOperator {

    protected Composite container;
    protected Combo parentSelector;
    protected Button isfinalSkill;

    public static final int EVENT_ID_MODIFIED = -2;
    public static final int EVENT_ID_FOCUS_CHANGED = -1;

    /**
     * 会出现多个树,所以记录它们的根节点
     */
    protected ArrayList<DragableButton> treeHeads = new ArrayList<DragableButton>();
    private Spinner parentLevel;
    private Spinner treePointsSpinner;

    /**
     * @param args
     */
    public static void main(String[] args) {
        Display display = new Display();
        final Shell shell = new Shell(display);
        // final Composite composite = new Composite (shell, SWT.NONE);
        final TalentTreeOperator main = new TalentTreeOperator();
        main.init(shell);
        main.load();

        shell.setSize(450, 300);
        shell.layout();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    public void init(Composite parent) {
        container = parent;
        container.addListener(EVENT_ID_FOCUS_CHANGED, new Listener() {
            public void handleEvent(Event event) {
                focusChanged();
            }
        });
        container.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                paintPathToChildren(e.gc);
            }
        });
        Composite skillTreeContainer = new Composite(container, SWT.NONE);
        skillTreeContainer.setLayout(new RowLayout());
        skillTreeContainer.setLocation(0, 0);
        createAddRemoveBtns(skillTreeContainer);
        skillTreeContainer.pack();

        // right
        Composite rightComp = new Composite(container, SWT.NONE);
        RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
        rowLayout.center = true;
        rightComp.setLayout(rowLayout);
        createOps4talent(rightComp);
        rightComp.setLocation(140, 0);
        rightComp.pack();
        // right end

    }

    private void createOps4talent(Composite rightComp) {
        Button button = new Button(rightComp, SWT.PUSH);
        button.setText("删除");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                doDelete();
            }
        });

        Label label = new Label(rightComp, SWT.NONE);
        label.setText("前置技能");

        parentSelector = new Combo(rightComp, SWT.READ_ONLY);
        parentSelector.setItems(new String[] { "无" });
        parentSelector.setEnabled(false);
        parentSelector.select(0);
        parentSelector.setVisibleItemCount(10);
        parentSelector.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                changeParent();
            }
        });

        Label label2 = new Label(rightComp, SWT.NONE);
        label2.setText("前置技能级别");
        parentLevel = new Spinner(rightComp, SWT.BORDER);
        parentLevel.setIncrement(1);
        parentLevel.setMinimum(0);
        parentLevel.setEnabled(false);
        parentLevel.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                changeParentLevel();
            }
        });

        Label label3 = new Label(rightComp, SWT.NONE);
        label3.setText("本技能须投入点数");
        label3.setToolTipText("前面的技能须要点满N点,这个才能点.这个意思.");
        treePointsSpinner = new Spinner(rightComp, SWT.BORDER);
        treePointsSpinner.setIncrement(1);
        treePointsSpinner.setMinimum(0);
        treePointsSpinner.setEnabled(false);
        treePointsSpinner.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                Control focusBtn = getFocusBtn();
                if (focusBtn == null) {
                    return;
                }
                int treePoints = Integer.parseInt(focusBtn.getData(DragableButton.KEY_NEED_POINTS).toString());
                if (treePoints != treePointsSpinner.getSelection()) {
                    treePoints = treePointsSpinner.getSelection();
                    focusBtn.setData(DragableButton.KEY_NEED_POINTS, treePoints);
                    container.notifyListeners(EVENT_ID_MODIFIED, null);
                }
            }
        });
        
        
        Label label4 = new Label(rightComp, SWT.NONE);
        label4.setText("是否是最终技能:");
        isfinalSkill = new Button(rightComp, SWT.CHECK);
        isfinalSkill.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e) {
                boolean flag = ((Button)e.getSource()).getSelection();
                int i = flag?1:0;
                Control focusControl = (Control) container.getData(DragableButton.KEY_FOCUS_DATA);
//                DragableButton db = (DragableButton)e.getSource();
                focusControl.setData(DragableButton.KEY_IS_FINAL_SKILL, i);
                container.notifyListeners(EVENT_ID_MODIFIED, null);
                container.redraw();
            }
        });
    }

    protected void doDelete() {
        Control focusControl = getFocusBtn();
        if (focusControl == null) {
            MessageDialog.openInformation(container.getShell(), "提示", "请先选择一个技能");
            return;
        }
        Control parent = getParentBtn(focusControl);
        if (parent != null) {
            ArrayList<DragableButton> list = getChildren(parent);
            list.remove(focusControl);
        }
        else {
            treeHeads.remove(focusControl);
        }
        remove(focusControl);
        container.redraw();
        container.notifyListeners(EVENT_ID_MODIFIED, null);
        container.setData(DragableButton.KEY_FOCUS_DATA, null);
        focusChanged();
    }

    private void remove(Control focusControl) {
        ArrayList<DragableButton> list = getChildren(focusControl);
        for (DragableButton btn : list) {
            remove(btn);
        }
        focusControl.dispose();
    }

    protected void changeParentLevel() {
        int level = parentLevel.getSelection();
        Control focusControl = (Control) container.getData(DragableButton.KEY_FOCUS_DATA);
        if (focusControl == null) {
            MessageDialog.openInformation(container.getShell(), "提示", "请先选择一个技能");
            return;
        }
        Control parentControl = (Control) focusControl.getData(DragableButton.KEY_PARENT);
        if (parentControl == null) {
            // MessageDialog.openInformation(container.getShell(), "提示",
            // "选中的技能没有前置技能");
            return;
        }
        if (level == getParentLevel(focusControl)) {
            return;
        }
        setParentLevel(focusControl, level);
        container.notifyListeners(EVENT_ID_MODIFIED, null);
    }

    private void setParentLevel(Control focusControl, int level) {
        focusControl.setData(DragableButton.KEY_PARENT_LEVEL, level);
    }

    protected void changeParent() {
        DragableButton focusControl = (DragableButton) container.getData(DragableButton.KEY_FOCUS_DATA);
        if (focusControl == null) {
            MessageDialog.openInformation(container.getShell(), "提示", "请先选择一个技能");
            return;
        }
        String toParentName = parentSelector.getText();
        // 选中"前置技能",置为无父节点
        Control toParent = (Control) parentSelector.getData(toParentName);

        if (toParent != null && (focusControl == toParent || hasCircle(focusControl, toParent))) {
            MessageDialog.openInformation(container.getShell(), "提示", "不能将下属技能设置为前置技能");
            return;
        }

        // 从原父节点中移除
        Control exParent = (Control) focusControl.getData(DragableButton.KEY_PARENT);
        if (exParent != null) {
            ArrayList<Control> children = (ArrayList) exParent.getData();
            children.remove(focusControl);
        }
        else {
            treeHeads.remove(focusControl);
        }

        if (toParent == null) {
            treeHeads.add(focusControl);
            parentSelector.select(0);
            parentLevel.setSelection(0);
            parentLevel.setEnabled(false);
        }
        else {
            parentLevel.setEnabled(true);
            ArrayList children = (ArrayList) toParent.getData();
            children.add(focusControl);
        }
        setParent(focusControl, toParent);
        container.notifyListeners(EVENT_ID_MODIFIED, null);
        container.redraw();
    }

    private void setParent(DragableButton focusControl, Control toParent) {
        focusControl.setData(DragableButton.KEY_PARENT, toParent);
    }

    /**
     * 检查node是否是cycleNode的子节点
     * 
     * @param cycleNode
     * @param node
     * @return
     */
    private boolean hasCircle(Control cycleNode, Control node) {
        ArrayList<DragableButton> children = getChildren(cycleNode);
        for (Control btn : children) {
            if (btn == node) {
                return true;
            }
            return hasCircle(btn, node);
        }
        return false;
    }

    private ArrayList<DragableButton> getChildren(Control parent) {
        return (ArrayList) parent.getData();
    }

    protected void paintPathToChildren(GC gc) {
        gc.setAntialias(SWT.ON);
        gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
        
        for(int i=0;i<6;i++){
            gc.drawLine(170+70*i, 50, 170+70*i, 400);
        }
        for(int i=0;i<8;i++){
            gc.drawLine(170, 50+50*i, 520, 50+50*i);
        }
        gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));

        for (Control treeHead : treeHeads) {
            walkChildren(treeHead, gc);
        }
        paintFocus(gc);

    }

    
    protected static void walkChildren(Control node, GC gc) {
        ArrayList<Control> children = (ArrayList) node.getData();
        int x1 = node.getLocation().x + node.getBounds().width / 2;
        int y1 = node.getLocation().y + node.getBounds().height / 2;
        for (Control btn : children) {
            int x2 = btn.getLocation().x + btn.getBounds().width / 2;
            int y2 = btn.getLocation().y + btn.getBounds().height / 2;
            
            double  H = 5;  // 箭头高度    
            double  L = 5; // 底边的一半   
            int x3  = 0;
            int y3  = 0;
            int x4  = 0;
            int y4  = 0;
            int x_2 = (x1 + x2)/2;
            int y_2 = (y1 + y2)/2;
            double  awrad  =  Math.atan(L  /  H);  // 箭头角度    
            double  arraow_len =  Math.sqrt(L  *  L  +  H  *  H); // 箭头的长度    
            double[] arrXY_1   =  rotateVec(x_2  -  x1, y_2  -  y1, awrad,  true , arraow_len);
            double[] arrXY_2   =  rotateVec(x_2  -  x1, y_2  -  y1,  - awrad,  true , arraow_len);
            double x_3 =  x_2  -  arrXY_1[ 0 ];  // (x3,y3)是第一端点    
            double y_3 =  y_2  -  arrXY_1[ 1 ];
            double x_4 =  x_2  -  arrXY_2[ 0 ]; // (x4,y4)是第二端点    
            double y_4 =  y_2  -  arrXY_2[ 1 ];

            Double X3 = new Double(x_3);
            x3 = X3.intValue();
            Double Y3 = new Double(y_3);
            y3 = Y3.intValue();
            Double X4 = new Double(x_4);
            x4 = X4.intValue();
            Double Y4 = new Double(y_4);
            y4 = Y4.intValue();
            // 画线 
            gc.drawLine(x1, y1, x2, y2);
            // 画箭头的一半 
            gc.drawLine(x_2, y_2, x3, y3);
            // 画箭头的另一半 
            gc.drawLine(x_2, y_2, x4, y4);
            //String l=String.valueOf(parentLevel.getText());
            

            // int[] pointArray = new int[] { btn.getLocation().x, y2,
            // btn.getLocation().x - btn.getBounds().width / 4,
            // btn.getLocation().y + btn.getBounds().height / 4,
            // btn.getLocation().x - btn.getBounds().width / 4,
            // btn.getLocation().y + btn.getBounds().height / 4 * 3 };
            //int angle1 = (int) Math.atan((y2-y1) / (x2 - x1));
            // gc.drawPolygon(pointArray);
            walkChildren(btn, gc);
        }
    }
    
    protected static double[] rotateVec( int px,  int py,  double ang,  boolean isChLen,
            double newLen) {

        double  mathstr[]  =   new   double [ 2 ];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度    
        double vx = px  *  Math.cos(ang)  -  py  *  Math.sin(ang);
        double vy = px  *  Math.sin(ang)  +  py  *  Math.cos(ang);
        if (isChLen) {
           double d = Math.sqrt(vx  *  vx  +  vy  *  vy);
           vx = vx  /  d  *  newLen;
           vy = vy  /  d  *  newLen;
           mathstr[ 0 ] = vx;
           mathstr[ 1 ] = vy;
        }
        return  mathstr;
   } 

    protected void paintFocus(GC gc) {
        Control focusControl = (Control) container.getData(DragableButton.KEY_FOCUS_DATA);
        if (focusControl != null) {
            Rectangle rect = focusControl.getBounds();
            int pad = 2;
            rect.x -= pad;
            rect.y -= pad;
            rect.width += pad << 1;
            rect.height += pad << 1;
            gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
            gc.drawRoundRectangle(rect.x, rect.y, rect.width, rect.height, pad << 1, pad << 1);
        }
    }

    private void createAddRemoveBtns(Composite parent) {
        if (parent instanceof Shell) {
            Button button = new Button(parent, SWT.PUSH);
            button.setText("添加");
            button.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    addTalent(0, 0, "测试");
                }
            });
            Button saveButton = new Button(parent, SWT.PUSH);
            saveButton.setText("保存");
            saveButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    save();
                }
            });
        }
    }

    protected void save() {
        Element root = new Element("TalentTree");
        save(root);
        Document doc = new Document(root);
        try {
            File f = new File("E:\\tempTalentTree.xml");
            Utils.saveDOM(doc, f);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(Element root) {
        DragableButton[] data = new DragableButton[treeHeads.size()];
        treeHeads.toArray(data);
        Arrays.sort(data, new Comparator<DragableButton>() {
            public int compare(DragableButton o1, DragableButton o2) {
                return o1.getLocation().y - o2.getLocation().y;
            }
        });
        for (DragableButton ctrl : data) {
            save(ctrl, root);
        }
    }

    private void save(DragableButton ctrl, Element parent) {
        Element node = new Element("node");
        node.addAttribute("id", ctrl.getData(DragableButton.KEY_ID).toString());
        node.addAttribute("parentLevel", getParentLevel(ctrl) + "");
        node.addAttribute("needPoints", ctrl.getData(DragableButton.KEY_NEED_POINTS).toString());
        node.addAttribute("x", ctrl.getLocation().x + "");
        node.addAttribute("y", ctrl.getLocation().y + "");
        int isFinalSkill;
        try{
            isFinalSkill = Integer.parseInt(ctrl.getData(DragableButton.KEY_IS_FINAL_SKILL).toString());
        }catch(Exception e){
            isFinalSkill = 0;
        }
        
        node.addAttribute("isFinalSkill",String.valueOf(isFinalSkill));
        parent.addContent(node);
        ArrayList<DragableButton> children = getChildren(ctrl);
        for (DragableButton c : children) {
            save(c, node);
        }
    }

    protected void load() {
        try {
            File f = new File("E:\\tempTalentTree.xml");
            Document doc = Utils.loadDOM(f);
            Element rootElement = doc.getRootElement();
            load(rootElement);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(Element rootElement) {
        treePointsSpinner.setSelection(0);
        load(rootElement, null);
    }

    protected void load(Element node, DragableButton parentBtn) {
        List<Element> children = node.getChildren("node");
        for (Element childNode : children) {
            String id = childNode.getAttributeValue("id");
            DataObject obj = ProjectData.getActiveProject().findObject(SkillConfig.class,
                    Integer.parseInt(id));
            SkillConfig skill = (SkillConfig) obj;
            String name = skill.title;
            int iconId = skill.iconID;
            parentSelector.add(name);
            DragableButton btn = new DragableButton(container, SWT.NONE, name);
            if (iconId >= 0) {
                PipImage pimg[] = ProjectData.getActiveProject().config.iconSeries.get("item");
                Image img = pimg[iconId / 1000].getImageDraw(iconId % 1000).createSWTImage(
                        Display.getCurrent().getActiveShell().getDisplay(), 0);
                btn.setImage(img);
                btn.setSize(btn.computeSize(-1, -1));
            }
            btn.setData(DragableButton.KEY_ID, id);
            String vs = childNode.getAttributeValue("needPoints");
            int needPoints = 0;
            if (vs != null && vs.equals("") == false) {
                needPoints = Integer.parseInt(vs);
            }
            btn.setData(DragableButton.KEY_NEED_POINTS, needPoints);
            parentSelector.setData(name, btn);
            int x = Integer.parseInt(childNode.getAttributeValue("x"));
            int y = Integer.parseInt(childNode.getAttributeValue("y"));
            btn.setLocation(x, y);

            int parentLevel = Integer.parseInt(childNode.getAttributeValue("parentLevel"));
            setParentLevel(btn, parentLevel);

            if (parentBtn != null) {
                List childrenList = getChildren(parentBtn);
                childrenList.add(btn);
            }
            else {
                treeHeads.add(btn);
            }
            String index = childNode.getAttributeValue("isFinalSkill");
            if(index == null || "0".equals(index)){
                btn.setData(DragableButton.KEY_IS_FINAL_SKILL,0);
//                isfinalSkill.setSelection(false);
            }else if("1".equals(index)){
                btn.setData(DragableButton.KEY_IS_FINAL_SKILL,1);
//                isfinalSkill.setSelection(true);
            }
            setParent(btn, parentBtn);
            
            load(childNode, btn);
        }
    }

    public void addTalent(int id, int iconID, String title) {
        DragableButton btn = new DragableButton(container, SWT.NONE, title);
        if (iconID >= 0) {
            PipImage[] pimg = ProjectData.getActiveProject().config.iconSeries.get("item");
            Image img = pimg[iconID / 1000].getImageDraw(iconID % 1000).createSWTImage(Display.getCurrent().getActiveShell().getDisplay(), 0);
            btn.setImage(img);
            btn.setSize(btn.computeSize(-1, -1));
        }
        btn.setData(DragableButton.KEY_ID, String.valueOf(id));
        Rectangle rect = container.getClientArea();
        int y = rect.width / 2;
        int x = rect.height / 2;
        btn.setLocation(x, y);
        btn.setData(DragableButton.KEY_NEED_POINTS, 0);

        treeHeads.add(btn);
        container.setData(DragableButton.KEY_FOCUS_DATA, btn);
        focusChanged();
        container.redraw();
        // update parent selector
        parentSelector.add(btn.getText());
        parentSelector.setEnabled(true);
        parentSelector.setData(btn.getText(), btn);
    }

    protected void focusChanged() {
        Control focusControl = getFocusBtn();
        if (focusControl == null) {
            parentSelector.setEnabled(false);
            parentLevel.setEnabled(false);
            treePointsSpinner.setEnabled(false);
            return;
        }
        int isFinalSkill;
        try{
            isFinalSkill = Integer.parseInt(focusControl.getData(DragableButton.KEY_IS_FINAL_SKILL).toString());
        }catch(Exception e){
            isFinalSkill = 0;
        }
        if(isFinalSkill == 1){
            isfinalSkill.setSelection(true);
        }else{
            isfinalSkill.setSelection(false);
        }
        int treePoints = Integer.parseInt(focusControl.getData(DragableButton.KEY_NEED_POINTS).toString());
        treePointsSpinner.setEnabled(true);
        treePointsSpinner.setSelection(treePoints);
        Control parentBtn = getParentBtn(focusControl);
        if (parentBtn == null) {
            parentSelector.select(0);
            parentLevel.setSelection(0);
            parentLevel.setEnabled(false);
        }
        else {
            DragableButton talent = (DragableButton) parentBtn;
            int idx = parentSelector.indexOf(talent.getText());
            parentSelector.select(idx);
            parentSelector.setEnabled(true);
            parentLevel.setEnabled(true);
            int level = getParentLevel(focusControl);
            parentLevel.setSelection(level);
        }

    }

    private int getParentLevel(Control ctrl) {
        Integer integer = (Integer) ctrl.getData(DragableButton.KEY_PARENT_LEVEL);
        if (integer == null) {
            return 0;
        }
        else {
            return integer.intValue();
        }
    }

    private Control getParentBtn(Control ctrl) {
        return (Control) ctrl.getData(DragableButton.KEY_PARENT);
    }

    public Control getFocusBtn() {
        return (Control) container.getData(DragableButton.KEY_FOCUS_DATA);
    }
}
