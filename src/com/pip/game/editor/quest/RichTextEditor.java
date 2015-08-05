package com.pip.game.editor.quest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.map.GameMapInfo;
import com.pip.game.data.map.GameMapNPC;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.QuestVariable;
import com.pip.game.data.quest.pqe.PQEUtils;
import com.pip.game.editor.EditorApplication;
import com.pip.game.editor.EditorPlugin;
import com.pip.game.editor.property.ChooseHeadDialogs;
import com.pip.game.editor.property.ChooseItemDialog;
import com.pip.game.editor.property.ChooseLocationDialog;
import com.pip.game.editor.property.ChooseNPCDialog;
import com.pip.mapeditor.MapTileSelector;
import com.swtdesigner.ResourceManager;

/**
 * 混合格式文件增强编辑器，带有修改格式和预览的功能。
 * @author lighthu
 */
public class RichTextEditor extends Composite implements SelectionListener, ModifyListener {
    private Text editControl;
    private RichTextPreviewer previewer;
    private MenuItem[] colorMenuItems;
    private ToolItem npcItem, locationItem, headChooseItem;
    private MenuItem chooseItemItem;
    private List<MenuItem> variableMenuItems = new ArrayList<MenuItem>();
    private List<String> variables = new ArrayList<String>();
    private QuestInfo questInfo;

    /**
     * Create the composite
     * @param parent
     * @param style
     */
    public RichTextEditor(Composite parent, QuestInfo qinfo) {
        super(parent, SWT.NONE);
        this.questInfo = qinfo;

        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);

        editControl = new Text(this, SWT.MULTI | SWT.BORDER);
        editControl.addModifyListener(this);
        editControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final ToolBar toolBar = new ToolBar(this, SWT.NONE);

        final ToolItem colorItem = new ToolItem(toolBar, SWT.DROP_DOWN);
        colorItem.setToolTipText("修改字体颜色");
        colorItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/fontcolor.gif"));

        final Menu colorPopup = new Menu(toolBar);
        addDropDown(colorItem, colorPopup);
        
        colorMenuItems = new MenuItem[MapTileSelector.thumbColors.length];
        for (int i = 0; i < MapTileSelector.thumbColors.length; i++) {
            MenuItem mi = new MenuItem(colorPopup, SWT.PUSH);
            mi.setImage(MapTileSelector.colorMenuImages[i]);
            mi.setText(MapTileSelector.thumbColorNames[i]);
            mi.addSelectionListener(this);
            colorMenuItems[i] = mi;
        }

        npcItem = new ToolItem(toolBar, SWT.PUSH);
        npcItem.setToolTipText("插入NPC");
        npcItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/mapeditor/npc.gif"));
        npcItem.addSelectionListener(this);
        
        locationItem = new ToolItem(toolBar, SWT.PUSH);
        locationItem.setToolTipText("插入地图位置");
        locationItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/flag.gif"));
        locationItem.addSelectionListener(this);

        final ToolItem variableItem = new ToolItem(toolBar, SWT.DROP_DOWN);
        variableItem.setToolTipText("插入变量");
        variableItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/expr.gif"));

        headChooseItem = new ToolItem(toolBar, SWT.PUSH);
        headChooseItem.setToolTipText("插入头像");
        headChooseItem.setImage(ResourceManager.getPluginImage(EditorPlugin.getDefault(), "icons/chooseHead.gif"));
        headChooseItem.addSelectionListener(this);
        
        final Menu variablePopup = new Menu(toolBar);
        addDropDown(variableItem, variablePopup);
        
        chooseItemItem = new MenuItem(variablePopup, SWT.PUSH);
        chooseItemItem.setText("物品数量...");
        chooseItemItem.addSelectionListener(this);
        
        // 创建插入变量下拉框
        if (questInfo != null) {
            // 局部变量
            for (QuestVariable var : questInfo.variables) {
                MenuItem mi = new MenuItem(variablePopup, SWT.PUSH);
                mi.setText(var.name);
                mi.addSelectionListener(this);
                variableMenuItems.add(mi);
                variables.add("${" + var.name + "}");
            }
        }
            
        // 全局变量
        String[] globals = new String[] { 
            "_NAME", "_SEXNAME", "_CLASSNAME", "_FACTIONNAME", "_LEVEL", "_MONEY", "_HP", "_MAXHP", "_MP", "_MAXMP", 
            "_STR", "_STA", "_AGI", "_INT", "_LASTCHATMESSAGE"
        };
        for (String var : globals) {
            MenuItem mi = new MenuItem(variablePopup, SWT.PUSH);
            mi.setText(ProjectData.getActiveProject().config.pqeUtils.SYSTEM_VARS_MAP.get(var).description);
            mi.addSelectionListener(this);
            variableMenuItems.add(mi);
            variables.add("${" + var + "}");
        }

        previewer = new RichTextPreviewer(this, SWT.NONE);
        previewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }
    
    public String getText() {
        return editControl.getText();
    }
    
    public void setText(String str) {
        editControl.setText(str);
    }

    private static void addDropDown(final ToolItem item, final Menu menu) {
        item.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                // if (event.detail == SWT.ARROW) {
                    Rectangle rect = item.getBounds();
                    Point pt = new Point(rect.x, rect.y + rect.height);
                    pt = item.getParent().toDisplay(pt);
                    menu.setLocation(pt.x, pt.y);
                    menu.setVisible(true);
                // }
            }
        });
    }

    public void modifyText(final ModifyEvent e) {
        previewer.setText(editControl.getText());
    }

    public void widgetDefaultSelected(SelectionEvent e) {}

    public void widgetSelected(SelectionEvent e) {
        for (int i = 0; i < colorMenuItems.length; i++) {
            if (e.getSource() == colorMenuItems[i]) {
                int color = MapTileSelector.thumbColors[i];
                addTag("<font color=\"" + Integer.toHexString(color) + "\">", "</font>");
                return;
            }
        }
        if (e.getSource() == npcItem) {
            // 选择一个NPC
            ChooseNPCDialog dlg = new ChooseNPCDialog(getShell(), ChooseNPCDialog.ONENPC);
           
            if (dlg.open() == Dialog.OK) {
                Integer temp = (Integer) dlg.getSelectedNPC();
                if(temp != -1){
                    int npcid = temp;
                    GameMapNPC npc = (GameMapNPC)GameMapNPC.findByID(ProjectData.getActiveProject(), npcid);
                    if (npc.owner.name.contains(":")) {
                        MessageDialog.openError(getShell(), "错误", "场景名称中不能包含':'符号。");
                        return;
                    }
                    String showName = npc.name;
                    if (showName.contains("|")) {
                        showName = npc.name.substring(0, showName.indexOf('|'));
                    }
                    String mapName = npc.owner.name;
                    if (mapName.contains("|")) {
                        mapName = mapName.substring(0, mapName.indexOf('|'));
                    }
                    String str = "<n>" + npc.getGlobalID() + "," + showName + "(" + mapName + ":" +
                        (npc.x / 8) + "," + (npc.y / 8) + ")</n>";
                    addString(str);
                }
            }
        } else if (e.getSource() == locationItem) {
            // 选择一个位置
            ChooseLocationDialog dlg = new ChooseLocationDialog(getShell(),null,editControl.getText());
            if (dlg.open() == Dialog.OK && dlg.getLocation()[0] != -1) {
                int[] location = dlg.getLocation();
                GameMapInfo mi = GameMapInfo.findByID(ProjectData.getActiveProject(), location[0]);
                if (mi.name.contains(":")) {
                    MessageDialog.openError(getShell(), "错误", "场景名称中不能包含':'符号。");
                    return;
                }
                String showName = mi.name;
                int pos1 = showName.indexOf('(');
                int pos2 = showName.indexOf('|');
                int splitPos = -1;
                if (pos1 == -1) {
                    splitPos = pos2;
                } else {
                    if (pos2 == -1) {
                        splitPos = pos1;
                    } else {
                        splitPos = Math.min(pos1, pos2);
                    }
                }
                if (splitPos != -1) {
                    showName = mi.name.substring(0, splitPos);
                }
                String str = "<l>" + mi.getGlobalID() + "," + showName + ":" + (location[1] / 8) + "," + 
                    (location[2] / 8) + "</l>";
                addString(str);
            }
        }else if(e.getSource() == headChooseItem){
            //选择一个头像
            ChooseHeadDialogs dlgs = new ChooseHeadDialogs(getShell());
            if(dlgs.open() == Dialog.OK && dlgs.getSelectedAnimation() != null){
                String str = dlgs.getHeadString();
                addString(str);
            }
        }
        for (int i = 0; i < variableMenuItems.size(); i++) {
            if (e.getSource() == variableMenuItems.get(i)) {
                addString(variables.get(i));
                return;
            }
        }
        if (e.getSource() == chooseItemItem) {
            ChooseItemDialog dlg = new ChooseItemDialog(getShell());
            if(dlg.open() == IDialogConstants.OK_ID){
                addString("${GetItemCount(" + dlg.getSelectedItem() + ")}");
            }
        }
    }
    
    private void addTag(String start, String end) {
        editControl.removeModifyListener(this);
        Point pt = editControl.getSelection();
        editControl.setSelection(pt.x);
        editControl.insert(start);
        editControl.addModifyListener(this);
        editControl.setSelection(pt.y + start.length());
        editControl.insert(end);
    }
    
    private void addString(String str) {
        Point pt = editControl.getSelection();
        editControl.insert(str);
    }
}
