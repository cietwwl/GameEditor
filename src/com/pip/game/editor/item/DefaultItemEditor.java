package com.pip.game.editor.item;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.game.data.item.Item;
import com.pip.game.data.item.ItemEffect;
import com.pip.game.data.item.ItemEffectConfig;
import com.pip.game.data.quest.Quest;
import com.pip.game.data.quest.QuestInfo;
import com.pip.game.data.quest.pqe.ExpressionList;
import com.pip.game.editor.AbstractDataObjectEditor;
import com.pip.game.editor.DefaultDataObjectEditor;
import com.pip.game.editor.quest.ExpressionDialog;
import com.pip.game.editor.quest.TemplateManager;
import com.pip.game.editor.util.IconChooser;
import com.pip.propertysheet.PropertySheetEntry;
import com.pip.propertysheet.PropertySheetViewer;
import com.pip.util.AutoSelectAll;
import com.pip.util.SWTUtils;
import com.pip.util.Utils;

/**
 * 缺省物品属性编辑控件
 */
public class DefaultItemEditor extends AbstractDataObjectEditor implements ISelectionChangedListener, SelectionListener {
    
    private Combo comboRoundColdDownCrossBattle;
    private Text textColdDownRound;
    private Text textUseCount;
    private Text textRoundColdDownGroup;
    protected Text textUseConfirm;
    protected Text prompt;
    protected Combo comboUseClazz;
    protected Text textDistance;
    protected Text textSchedule;
    protected static final String[] COMBO_AVILIABLE = {"否","战斗中可用","非战斗中可用","任何时候可以使用"};
    protected static final String[] COMBO_AUTOUSE = {"不自动","默认自动","自定义自动","后台自动(有效)"};
    protected static final String[] COMBO_AREA = {"无目标","自己","己方队友","敌方","全部"};
    protected Text textMaxOwnCount;
    /**
     * 是否绑定
     */
    protected Combo comboBind;
    
    /**
     * 出售价格
     */
    protected Text textPrice;
    
    /**
     * 物品冷却时间
     */
    protected Text textColdDownTime;
    /**
     * 冷却组
     */
    protected Text textColdDownGroup;
    
    /**
     * 是否消耗
     */
    protected Combo comboWaste;
    
    /**
     * 物品品质
     */
    protected Combo comboQuality;
    /**
     * 时间类型
     */
    protected Combo comboTimeType;
    /**
     * 使用者等级下限
     */
    protected Text textPlayerLevel;
    /**
     * 使用者等级上限
     */
    protected Text textPlayerMaxLevel;
    /**
     * 使用范围
     */
    protected Combo comboArea;
    /**
     * 是否可以出售
     */
    protected Combo comboSale;
    /**
     * 物品等级
     */
    protected Text textLevel;
    
    /**
     * 使用时效
     */
    protected Text textTime;
    /**
     * 是否自动使用
     */
    protected Combo comboAutoUse;
    /**
     * 是否可用
     */
    protected Combo comboAvailable;
    /**
     * 是否是任务物品
     */
    protected Combo comboTaskFlag;
    /**
     * 物品在每个物品栏的堆叠数量
     */
    protected Text textAddtion;
    /**
     * 物品附加条件 
     */
    protected Text textCondition;
    /**
     * 物品附加条件Button
     */
    protected Button buttonEditCondition;
    
    /**
     * 是否是实例类型
     */
    protected Combo comboInstance;
    /**
     * 属性编辑
     */
    protected PropertySheetViewer propertyEditor;
    /**
     * 使用效果列表
     */
    protected Combo comboProperty;
    /**
     * 物品现有效果
     */
    protected ListViewer effectList;
    
    /**
     * 新建
     */
    protected Action addAction;
    
    /**
     * 删除
     */
    protected Action deleteAction;
    
    /**
     * 图标选择
     */
    protected IconChooser iconChooser;
    
    /**
     * 是否允许丢弃
     */
    protected Combo comboCanDelete;
    /**
     * 是否允许转移至仓库
     */
    protected Combo comboMovable;
    
    /**
     * 效果类型列表。
     */
    protected String[] effectTypeNames;
    protected int[] effectTypeIDs;
    protected Group basicPropertyGroup;
    protected Group timePropertyGroup;
    protected Group usePropertyGroup;
    protected Group useEffectGroup;
    protected Group specialGroup;
    
    public DefaultItemEditor(Composite parent, int style, DefaultDataObjectEditor owner) {
        super(parent, style, owner);
        
        // 取得所有效果类型
        ItemEffectConfig[] ecs = ProjectData.getActiveProject().config.itemEffects;
        effectTypeNames = new String[ecs.length];
        effectTypeIDs = new int[ecs.length];
        for (int i = 0; i < ecs.length; i++) {
            effectTypeNames[i] = ecs[i].title;
            effectTypeIDs[i] = ecs[i].id;
        }
        
        final GridLayout gridLayout = new GridLayout();
        setLayout(gridLayout);
        
        createActions();

        basicPropertyGroup = new Group(this, SWT.NONE);
        basicPropertyGroup.setText("基本属性");
        basicPropertyGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 6;
        basicPropertyGroup.setLayout(gridLayout_1);
        
        final Label label_13 = new Label(basicPropertyGroup, SWT.NONE);
        label_13.setText("物品品质：");

        comboQuality = new Combo(basicPropertyGroup, SWT.READ_ONLY);
        comboQuality.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboQuality.setVisibleItemCount(10);
        comboQuality.setItems(ProjectData.getActiveProject().config.COMBO_QUALITY);
        comboQuality.addModifyListener(owner);

        final Label label_9 = new Label(basicPropertyGroup, SWT.NONE);
        label_9.setText("物品等级：");

        textLevel = new Text(basicPropertyGroup, SWT.BORDER);
        textLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textLevel.addModifyListener(owner);

        final Label label_5 = new Label(basicPropertyGroup, SWT.NONE);
        label_5.setText("堆叠数量：");

        textAddtion = new Text(basicPropertyGroup, SWT.BORDER);
        textAddtion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        textAddtion.addModifyListener(owner);

        final Label label_4 = new Label(basicPropertyGroup, SWT.NONE);
        label_4.setText("是否绑定：");

        comboBind = new Combo(basicPropertyGroup, SWT.READ_ONLY);
        comboBind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        comboBind.setItems(ProjectData.getActiveProject().config.COMBO_BIND);
        comboBind.addModifyListener(owner);

        final Label label_16 = new Label(basicPropertyGroup, SWT.NONE);
        label_16.setText("能否出售：");

        comboSale = new Combo(basicPropertyGroup, SWT.READ_ONLY);
        comboSale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboSale.addSelectionListener(this);
        comboSale.setItems(ProjectConfig.COMBO_YES_NO);
        comboSale.addModifyListener(owner);

        final Label label_2 = new Label(basicPropertyGroup, SWT.NONE);
        label_2.setText("出售价格：");
        
        textPrice = new Text(basicPropertyGroup, SWT.BORDER);
        textPrice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textPrice.setText("1");
        textPrice.addFocusListener(AutoSelectAll.instance);
        textPrice.addModifyListener(owner);

        final Label label_22 = new Label(basicPropertyGroup, SWT.NONE);
        label_22.setText("物品图标：");
        
        iconChooser = new IconChooser(basicPropertyGroup, SWT.NONE, ProjectData.getActiveProject().config.iconSeries.get("item"));
        iconChooser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        iconChooser.setHandler(owner);

        final Label label_19 = new Label(basicPropertyGroup, SWT.NONE);
        label_19.setText("是否实例类型：");

        comboInstance = new Combo(basicPropertyGroup, SWT.READ_ONLY);
        comboInstance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboInstance.setItems(ProjectConfig.COMBO_YES_NO);
        comboInstance.addSelectionListener(this);

        final Label label_6 = new Label(basicPropertyGroup, SWT.NONE);
        label_6.setText("是否任务物品：");

        comboTaskFlag = new Combo(basicPropertyGroup, SWT.READ_ONLY);
        comboTaskFlag.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboTaskFlag.setItems(ProjectConfig.COMBO_YES_NO);
        comboTaskFlag.addSelectionListener(this);
        comboTaskFlag.addModifyListener(owner);

        final Label label_7 = new Label(basicPropertyGroup, SWT.NONE);
        label_7.setText("是否可使用：");

        comboAvailable = new Combo(basicPropertyGroup, SWT.READ_ONLY);
        comboAvailable.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboAvailable.addSelectionListener(this);
        comboAvailable.setItems(COMBO_AVILIABLE);
        comboAvailable.addModifyListener(owner);
//        new Label(basicPropertyGroup, SWT.NONE);
//        new Label(basicPropertyGroup, SWT.NONE);
//        new Label(basicPropertyGroup, SWT.NONE);
//        new Label(basicPropertyGroup, SWT.NONE);
        
        final Label label_30 = new Label(basicPropertyGroup, SWT.NONE);
        label_30.setLayoutData(new GridData());
        label_30.setText("是否允许丢弃：");
        
        comboCanDelete = new Combo(basicPropertyGroup, SWT.READ_ONLY);
        comboCanDelete.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboCanDelete.addSelectionListener(this);
        comboCanDelete.setItems(ProjectConfig.COMBO_YES_NO);
        
        final Label label_31 = new Label(basicPropertyGroup, SWT.NONE);
        label_31.setLayoutData(new GridData());
        label_31.setText("是否允许转移至仓库：");
        
        comboMovable = new Combo(basicPropertyGroup, SWT.READ_ONLY);
        comboMovable.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboMovable.addSelectionListener(this);
        comboMovable.setItems(ProjectConfig.COMBO_YES_NO);
        
        final Label label_own = new Label(basicPropertyGroup, SWT.NONE);
        label_own.setText("最多拥有数量");
        
        textMaxOwnCount = new Text(basicPropertyGroup, SWT.BORDER);
        textMaxOwnCount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textMaxOwnCount.addModifyListener(owner);

        timePropertyGroup = new Group(this, SWT.NONE);
        timePropertyGroup.setText("时效性");
        timePropertyGroup.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.numColumns = 4;
        timePropertyGroup.setLayout(gridLayout_2);

        final Label label_14 = new Label(timePropertyGroup, SWT.NONE);
        label_14.setText("时效类型：");

        comboTimeType = new Combo(timePropertyGroup, SWT.READ_ONLY);
        comboTimeType.addSelectionListener(this);
        comboTimeType.setItems(ProjectData.getActiveProject().config.COMBO_TIME_TYPE);
        comboTimeType.addModifyListener(owner);

        final Label label_15 = new Label(timePropertyGroup, SWT.NONE);
        label_15.setText("生效时间(相对时效填写秒数，绝对时效填写日期，格式yyyy-MM-dd HH:mm:ss)：");

        textTime = new Text(timePropertyGroup, SWT.BORDER);
        textTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textTime.addModifyListener(owner);

        specialGroup = new Group(this, SWT.NONE);
        specialGroup.setText("特殊设置");
        final GridData gd_specialGroup = new GridData(SWT.FILL, SWT.FILL, true, false);
        specialGroup.setLayoutData(gd_specialGroup);
        specialGroup.setLayout(new GridLayout());

        usePropertyGroup = new Group(this, SWT.NONE);
        usePropertyGroup.setText("使用设置");
        usePropertyGroup.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
        final GridLayout gridLayout_3 = new GridLayout();
        gridLayout_3.numColumns = 6;
        usePropertyGroup.setLayout(gridLayout_3);
        
        final Label label_3 = new Label(usePropertyGroup, SWT.NONE);
        label_3.setLayoutData(new GridData());
        label_3.setText("是否消耗：");
        
        comboWaste = new Combo(usePropertyGroup, SWT.READ_ONLY);
        comboWaste.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboWaste.addSelectionListener(this);
        comboWaste.setItems(ProjectConfig.COMBO_YES_NO);

        final Label label_8 = new Label(usePropertyGroup, SWT.NONE);
        label_8.setLayoutData(new GridData());
        label_8.setText("自动使用：");

        comboAutoUse = new Combo(usePropertyGroup, SWT.READ_ONLY);
        comboAutoUse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboAutoUse.setItems(COMBO_AUTOUSE);
        comboAutoUse.addSelectionListener(this);
        comboAutoUse.addModifyListener(owner);

        final Label label_10 = new Label(usePropertyGroup, SWT.NONE);
        label_10.setLayoutData(new GridData());
        label_10.setText("使用范围：");

        comboArea = new Combo(usePropertyGroup, SWT.READ_ONLY);
        comboArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboArea.setItems(COMBO_AREA);
        comboArea.addModifyListener(owner);

        final Label label_24 = new Label(usePropertyGroup, SWT.NONE);
        label_24.setText("使用职业：");

        comboUseClazz = new Combo(usePropertyGroup, SWT.READ_ONLY);
        comboUseClazz.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboUseClazz.setItems(ProjectData.getActiveProject().config.PLAYER_CLAZZ);
        comboUseClazz.select(0);
        comboUseClazz.addModifyListener(owner);

        final Label label_11 = new Label(usePropertyGroup, SWT.NONE);
        label_11.setText("等级下限：");

        textPlayerLevel = new Text(usePropertyGroup, SWT.BORDER);
        textPlayerLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textPlayerLevel.addModifyListener(owner);

        final Label label_23 = new Label(usePropertyGroup, SWT.NONE);
        label_23.setLayoutData(new GridData());
        label_23.setText("使用距离（码）：");

        textDistance = new Text(usePropertyGroup, SWT.BORDER);
        textDistance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textDistance.addModifyListener(owner);

        final Label label_12 = new Label(usePropertyGroup, SWT.NONE);
        label_12.setText("冷却组(逗号分隔)：");

        textColdDownGroup = new Text(usePropertyGroup, SWT.BORDER);
        textColdDownGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textColdDownGroup.addModifyListener(owner);

        final Label label_17 = new Label(usePropertyGroup, SWT.NONE);
        label_17.setText("冷却时间(毫秒)：");

        textColdDownTime = new Text(usePropertyGroup, SWT.BORDER);
        textColdDownTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textColdDownTime.addModifyListener(owner);

        final Label label_20 = new Label(usePropertyGroup, SWT.NONE);
        label_20.setText("施法时间(毫秒)：");

        textSchedule = new Text(usePropertyGroup, SWT.BORDER);
        textSchedule.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textSchedule.addModifyListener(owner);

        final Label label = new Label(usePropertyGroup, SWT.NONE);
        label.setText("回合制冷却组：");

        textRoundColdDownGroup = new Text(usePropertyGroup, SWT.BORDER);
        final GridData gd_textRoundColdDownGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textRoundColdDownGroup.setLayoutData(gd_textRoundColdDownGroup);
        textRoundColdDownGroup.addModifyListener(owner);

        final Label label_1 = new Label(usePropertyGroup, SWT.NONE);
        label_1.setText("冷却回合数：");

        textColdDownRound = new Text(usePropertyGroup, SWT.BORDER);
        final GridData gd_textColdDownRound = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textColdDownRound.setLayoutData(gd_textColdDownRound);
        textColdDownRound.addModifyListener(owner);

        final Label label_18 = new Label(usePropertyGroup, SWT.NONE);
        label_18.setText("冷却是否跨战斗：");

        comboRoundColdDownCrossBattle = new Combo(usePropertyGroup, SWT.READ_ONLY);
        comboRoundColdDownCrossBattle.setItems(new String[] {"否", "是"});
        comboRoundColdDownCrossBattle.select(0);
        final GridData gd_comboRoundColdDownCrossBattle = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboRoundColdDownCrossBattle.setLayoutData(gd_comboRoundColdDownCrossBattle);
        comboRoundColdDownCrossBattle.addModifyListener(owner);
        
        final Label label_count = new Label(usePropertyGroup, SWT.NONE);
        label_count.setText("使用次数");
        
        textUseCount = new Text(usePropertyGroup, SWT.BORDER);
        final GridData gd_textUseCount = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textUseCount.setLayoutData(gd_textUseCount);
        textUseCount.addModifyListener(owner);
        
        new Label(usePropertyGroup, SWT.NONE);
        new Label(usePropertyGroup, SWT.NONE);
        new Label(usePropertyGroup, SWT.NONE);
        new Label(usePropertyGroup, SWT.NONE);

        final Label label_25 = new Label(usePropertyGroup, SWT.NONE);
        label_25.setLayoutData(new GridData());
        label_25.setText("使用确认：");

        textUseConfirm = new Text(usePropertyGroup, SWT.BORDER);
        textUseConfirm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
        textUseConfirm.addFocusListener(AutoSelectAll.instance);
        textUseConfirm.addModifyListener(owner);

        final Label label_27 = new Label(usePropertyGroup, SWT.NONE);
        label_27.setText("使用附加条件：");

        final Composite composite = new Composite(usePropertyGroup, SWT.NONE);
        final GridLayout gridLayout_4 = new GridLayout();
        gridLayout_4.numColumns = 2;
        gridLayout_4.verticalSpacing = 0;
        gridLayout_4.marginWidth = 0;
        gridLayout_4.marginHeight = 0;
        gridLayout_4.horizontalSpacing = 0;
        composite.setLayout(gridLayout_4);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));

        textCondition = new Text(composite, SWT.BORDER);
        textCondition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textCondition.setEditable(false);
        textCondition.addFocusListener(AutoSelectAll.instance);

        buttonEditCondition = new Button(composite, SWT.NONE);
        buttonEditCondition.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                onEditCondition();
            }
        });
        buttonEditCondition.setText("...");

        final Label label_28 = new Label(usePropertyGroup, SWT.NONE);
        label_28.setText("使用附加条件提示：");
        
        prompt = new Text(usePropertyGroup, SWT.BORDER);
        prompt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
        prompt.addFocusListener(AutoSelectAll.instance);
        prompt.addModifyListener(owner);
        
        final Label label_29 = new Label(usePropertyGroup, SWT.NONE);
        label_29.setText("等级上限：");
        
        textPlayerMaxLevel = new Text(usePropertyGroup, SWT.BORDER);
        textPlayerMaxLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
        textPlayerMaxLevel.addFocusListener(AutoSelectAll.instance);
        textPlayerMaxLevel.addModifyListener(owner);
        
        // 创建右键菜单
        MenuManager mgr = new MenuManager();
        mgr.add(addAction);
        mgr.add(deleteAction);

        useEffectGroup = new Group(this, SWT.NONE);
        useEffectGroup.setText("使用效果");
        useEffectGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        final GridLayout groupGridLayout = new GridLayout();
        groupGridLayout.numColumns = 2;
        useEffectGroup.setLayout(groupGridLayout);

        effectList = new ListViewer(useEffectGroup, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
        final GridData gd_effectList = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2);
        gd_effectList.heightHint = 200;
        effectList.getList().setLayoutData(gd_effectList);
        effectList.setContentProvider(new ListContentProvider());
        effectList.addSelectionChangedListener(this);
        Menu menu = mgr.createContextMenu(effectList.getList());
        effectList.getList().setMenu(menu);
        

        comboProperty = new Combo(useEffectGroup, SWT.READ_ONLY);
        comboProperty.setVisibleItemCount(20);
        comboProperty.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboProperty.setItems(effectTypeNames);
        comboProperty.addSelectionListener(this);
        
        propertyEditor = new PropertySheetViewer(useEffectGroup, SWT.NONE | SWT.V_SCROLL | SWT.WRAP, true);
        PropertySheetEntry rootEntry = new PropertySheetEntry();
        propertyEditor.setRootEntry(rootEntry);
        propertyEditor.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ((GridData)propertyEditor.getControl().getLayoutData()).exclude = false;
        Tree t = (Tree)propertyEditor.getControl();
        t.getColumn(0).setWidth(200);
        t.getColumn(1).setWidth(300);
        
        SWTUtils.showControl(specialGroup, false);
    }
    
    /**
     * 把界面上当前输入的值保存到编辑对象中。
     * @throws Exception
     */
    public void save() throws Exception {
        Item itemDataDef = (Item)owner.getEditObject();
        itemDataDef.bind = comboBind.getSelectionIndex();
        
        itemDataDef.instance = comboInstance.getSelectionIndex() == 1;
        try {
            /* 当物品是一个实例类型的时候，堆叠数量只能为1 */
            int addition = Integer.parseInt(textAddtion.getText());
            if(itemDataDef.instance && addition > 1){
                throw new Exception("当物品是一个实例类型的时候，堆叠数量只能为1！");
            }
            else if(addition <= 0){
                throw new Exception("叠加数量必须大于0！");
            }
            if(itemDataDef.addition > addition){
                MessageDialog.openConfirm(null, "警告!!!", "如果此物品已上线，此修改将会导致玩家背包，仓库的存储问题，请谨慎修改！！！");
            }
            itemDataDef.addition = addition;
        } catch (NumberFormatException e) {
            throw new Exception("叠加数量输入格式错误！");
        }
        
        itemDataDef.taskFlag = comboTaskFlag.getSelectionIndex() == Item.ATTRIBUTE_VALUE_YES;
        
        itemDataDef.iconIndex = iconChooser.getIconIndex();
        if(itemDataDef.iconIndex < 0){
            throw new Exception("请选择正确的图标！");
        }
        itemDataDef.quality = comboQuality.getSelectionIndex();
        
        itemDataDef.sale = comboSale.getSelectionIndex() == Item.ATTRIBUTE_VALUE_YES;
        if(itemDataDef.sale){
            try {
                itemDataDef.price = Integer.parseInt(textPrice.getText());
                if (itemDataDef.price <= 0) {
                    throw new Exception("价格必须大于0！");
                }
            } catch (NumberFormatException e) {
                throw new Exception("价格输入格式错误！");
            }
        }
        try {
            itemDataDef.level = Integer.parseInt(textLevel.getText());
        } catch (NumberFormatException e) {
            throw new Exception("物品等级输入格式错误！");
        }
        itemDataDef.timeType = comboTimeType.getSelectionIndex();
        if (itemDataDef.timeType > Item.TIME_TYPE_UNDEFINE) {
            try {
                if(itemDataDef.timeType == Item.TIME_TYPE_RELATIVELY){
                    itemDataDef.time = Integer.parseInt(textTime.getText());
                } else if(itemDataDef.timeType == Item.TIME_TYPE_ABSOLUTELY){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = format.parse(textTime.getText());
                    long t = date.getTime();
                    itemDataDef.time = (int)(t/1000);
                }
            } catch (Exception e) {
                throw new Exception("时间格式输入格式错误！");
            }
        }
        
        itemDataDef.available = comboAvailable.getSelectionIndex();
        if (itemDataDef.available != Item.AVAILABLE_NO) {
            itemDataDef.useClazz = comboUseClazz.getSelectionIndex();
            itemDataDef.useConfirm = textUseConfirm.getText().trim();
            itemDataDef.additionalPrompt = prompt.getText().trim();
            try {
                itemDataDef.playerMaxLevel = Integer.parseInt(textPlayerMaxLevel.getText());
            } catch (NumberFormatException e) {
                throw new Exception("物品等级上限输入格式错误！");
            }
            itemDataDef.waste = comboWaste.getSelectionIndex() == Item.ATTRIBUTE_VALUE_YES;
            
            itemDataDef.area = comboArea.getSelectionIndex();
            try {
                itemDataDef.coldDownGroup = Utils.stringToIntArray(textColdDownGroup.getText(), ',');
            } catch (Exception e) {
                throw new Exception("冷却组ID输入错误！");
            }
            try {
                itemDataDef.coldDownTime = Integer.parseInt(textColdDownTime.getText());
            } catch (NumberFormatException e) {
                throw new Exception("冷却时间输入格式错误！");
            }
            
            try {
                itemDataDef.roundColdDownGroup = Utils.stringToIntArray(textRoundColdDownGroup.getText(), ',');
            } catch (Exception e) {
                throw new Exception("冷却组ID输入错误！");
            }
            try {
                itemDataDef.coldDownRound = Integer.parseInt(textColdDownRound.getText());
            } catch (NumberFormatException e) {
                throw new Exception("冷却回合数输入格式错误！");
            }
            
            try{
                itemDataDef.useCount = Integer.parseInt(textUseCount.getText());
            }catch (NumberFormatException e) {
                throw new Exception("使用次数输入格式错误！");
            }
            itemDataDef.coldDownRoundCrossBattle = comboRoundColdDownCrossBattle.getSelectionIndex() == 1;
            
            itemDataDef.autoUse = comboAutoUse.getSelectionIndex() - 1;
            try {
                itemDataDef.schedule = Integer.parseInt(textSchedule.getText());
            } catch (NumberFormatException e) {
                throw new Exception("施法时间输入格式错误！");
            }
            
            try {
                itemDataDef.playerLevel = Integer.parseInt(textPlayerLevel.getText());
            } catch (NumberFormatException e) {
                throw new Exception("玩家等级输入格式错误！");
            }
            try {
                itemDataDef.distance = (int)(Float.parseFloat(textDistance.getText()) * 8);
            } catch (Exception e1) {
                throw new Exception("使用距离输入格式错误！");
            }
        }
        itemDataDef.canDelete = comboCanDelete.getSelectionIndex() == Item.ATTRIBUTE_VALUE_YES;
        itemDataDef.movable = comboMovable.getSelectionIndex() == Item.ATTRIBUTE_VALUE_YES;
        itemDataDef.maxOwnCount = Integer.parseInt(textMaxOwnCount.getText());
    }
    
    /**
     * 把编辑对象的值设置到界面中。
     * @throws Exception
     */
    public void load() throws Exception {
        Item dataDef = (Item)owner.getEditObject();
        
        textCondition.setText(dataDef.additionalCondition.toNatureString());
        textAddtion.setText(String.valueOf(dataDef.addition));
        comboTaskFlag.select(dataDef.taskFlag ? Item.ATTRIBUTE_VALUE_YES : Item.ATTRIBUTE_VALUE_NO);
        comboBind.select(dataDef.bind);
        comboQuality.select(dataDef.quality);
        textPlayerLevel.setText(String.valueOf(dataDef.playerLevel));
        comboInstance.select(dataDef.instance ? Item.ATTRIBUTE_VALUE_YES : Item.ATTRIBUTE_VALUE_NO);
        iconChooser.setIcon(dataDef.iconIndex);
        if (dataDef.sale) {
            comboSale.select(Item.ATTRIBUTE_VALUE_YES);
            textPrice.setText(String.valueOf(dataDef.price));            
        } else{
            comboSale.select(Item.ATTRIBUTE_VALUE_NO);
            textPrice.setEditable(false);
        }
        
        comboAvailable.select(dataDef.available);
        if (dataDef.available == Item.AVAILABLE_NO) {
            SWTUtils.showControl(usePropertyGroup, false);
            SWTUtils.showControl(useEffectGroup, false);
        } else {
            SWTUtils.showControl(usePropertyGroup, true);
            SWTUtils.showControl(useEffectGroup, true);
        }
        
        comboUseClazz.select(dataDef.useClazz);
        textUseConfirm.setText(dataDef.useConfirm);
        prompt.setText(dataDef.additionalPrompt);
        textPlayerMaxLevel.setText(String.valueOf(dataDef.playerMaxLevel));
        comboWaste.select(dataDef.waste ? Item.ATTRIBUTE_VALUE_YES : Item.ATTRIBUTE_VALUE_NO);
        comboArea.select(dataDef.area);
        textLevel.setText(String.valueOf(dataDef.level));
        textPlayerLevel.setText(String.valueOf(dataDef.playerLevel));
        
        textColdDownGroup.setText(Utils.intArrayToString(dataDef.coldDownGroup, ','));
        textColdDownTime.setText(String.valueOf(dataDef.coldDownTime));

        textRoundColdDownGroup.setText(Utils.intArrayToString(dataDef.roundColdDownGroup, ','));
        textColdDownRound.setText(String.valueOf(dataDef.coldDownRound));
        comboRoundColdDownCrossBattle.select(dataDef.coldDownRoundCrossBattle ? 1 : 0);

        comboAutoUse.select(dataDef.autoUse + 1);
        textSchedule.setText(String.valueOf(dataDef.schedule));
        textDistance.setText(String.valueOf(dataDef.distance / 8f));
        
        comboTimeType.select(dataDef.timeType);
        if (dataDef.timeType == Item.TIME_TYPE_UNDEFINE) {
            textTime.setEditable(false);
        } else if (dataDef.timeType == Item.TIME_TYPE_ABSOLUTELY) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
            StringBuffer result = new StringBuffer();
            format.format(new Date((long)dataDef.time * 1000), result, new FieldPosition(0));
            textTime.setText(result.toString());
        } else{
            textTime.setText(String.valueOf(dataDef.time));
        }
        
        comboCanDelete.select(dataDef.canDelete ? Item.ATTRIBUTE_VALUE_YES : Item.ATTRIBUTE_VALUE_NO);
        comboMovable.select(dataDef.movable ? Item.ATTRIBUTE_VALUE_YES : Item.ATTRIBUTE_VALUE_NO);
        textUseCount.setText(String.valueOf(dataDef.useCount));
        textMaxOwnCount.setText(String.valueOf(dataDef.maxOwnCount));
        effectList.setInput(dataDef.effects);
        effectList.refresh();
    }
    
    // 弹出公式编辑器来编辑触发条件
    protected void onEditCondition() {
        Item item = (Item)owner.getEditObject();
        String newExpr = ExpressionDialog.open(getShell(), item.additionalCondition.toString(), 
                new QuestInfo(new Quest(ProjectData.getActiveProject())), TemplateManager.CONTEXT_SET_CONDITION);
        if (newExpr != null) {
            item.additionalCondition = ExpressionList.fromString(newExpr);
            textCondition.setText(item.additionalCondition.toNatureString());
            owner.setDirty(true);
        }
    }
    /**
     * 创建使用效果列表的弹出菜单项
     */
    protected void createActions() {
        addAction = new Action("新建") {
            public void run() {
                onAdd();
            }
        };
        deleteAction = new Action("删除") {
            public void run() {
                onDelete();
            }
        };
    }

    /**
     * 为物品增加一个使用效果
     */
    public void onAdd() {
        Item item = (Item)owner.getEditObject();
        ItemEffect newEffect = new ItemEffect();
        item.effects.add(newEffect);
        effectList.refresh();
        effectList.setSelection(new StructuredSelection(newEffect));
        owner.setDirty(true);
    }
    
    /**
     * 删除当前选中效果
     */
    public void onDelete(){
        Item item = (Item)owner.getEditObject();
        IStructuredSelection selected = (IStructuredSelection)effectList.getSelection();
        ItemEffect e = (ItemEffect)selected.getFirstElement();
        if (e != null) {
            item.effects.remove(e);
            effectList.refresh();
            owner.setDirty(true);
        }
    }
    
    /**
     * 列表选择消息处理
     * @param e
     */
    public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selected = (IStructuredSelection)event.getSelection();
        if (event.getSource() == effectList) {
            ItemEffect e = (ItemEffect)selected.getFirstElement();
            if(e != null){
                for (int i = 0; i < effectTypeIDs.length; i++) {
                    if (effectTypeIDs[i] == e.effectType) {
                        comboProperty.select(i);
                        break;
                    }
                }
                propertyEditor.setInput(new Object[]{new ItemEffectPropertySource(e, owner)});
            }
        }
    }
    
    public void widgetDefaultSelected(SelectionEvent e) {}
    
    /**
     * 下拉列表框选择消息处理
     */
    public void widgetSelected(SelectionEvent e) {
        Item item = (Item)owner.getEditObject();
        if (e.getSource() == comboProperty) {
            IStructuredSelection selected = (IStructuredSelection)effectList.getSelection();
            ItemEffect curEffect = (ItemEffect)selected.getFirstElement();
            /*用户选择使用效果类型*/
            if(curEffect != null){                
                curEffect.effectType = effectTypeIDs[comboProperty.getSelectionIndex()];
                curEffect.resetParam();
                propertyEditor.setInput(new Object[]{new ItemEffectPropertySource(curEffect, owner)});
                effectList.refresh();
                owner.setDirty(true);
            }
        } else if (e.getSource() == comboAvailable) {
            if (comboAvailable.getSelectionIndex() == Item.AVAILABLE_NO) {
                SWTUtils.showControl(usePropertyGroup, false);
                SWTUtils.showControl(useEffectGroup, false);
            } else {
                SWTUtils.showControl(usePropertyGroup, true);
                SWTUtils.showControl(useEffectGroup, true);
            }
        } else if (e.getSource() == comboTimeType) {
            if (comboTimeType.getSelectionIndex() > Item.ATTRIBUTE_VALUE_NO) {
                /* 当没有使用时效时，时效不可编辑 */
                textTime.setEditable(true);
                MessageDialog.openConfirm(null, "警告!!!", "如果此物品已上线，此修改将会导致玩家背包，仓库的存储问题，请谨慎修改！！！");
            } else{
                textTime.setEditable(false);
            }
        } else if (e.getSource() == comboSale) {
            if (comboSale.getSelectionIndex() == Item.ATTRIBUTE_VALUE_YES) {
                textPrice.setEditable(true);
            } else if (comboSale.getSelectionIndex() == Item.ATTRIBUTE_VALUE_NO) {
                textPrice.setEditable(false);
            }
        } else {
            owner.setDirty(true);
        }
    }
    
    class ListContentProvider implements IStructuredContentProvider{

        public Object[] getElements(Object inputElement) {
            List el = (List)inputElement;
            return el.toArray();
        }

        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    }
}


