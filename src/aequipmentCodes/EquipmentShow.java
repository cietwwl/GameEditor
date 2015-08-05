package aequipmentCodes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.pip.game.data.ProjectConfig;
import com.pip.game.data.ProjectData;
import com.pip.image.workshop.WorkshopPlugin;
import com.pip.image.workshop.editor.AnimateViewer;
import com.pip.image.workshop.editor.BodyDef;
import com.pipimage.image.EquipHookMap;
import com.pipimage.image.PipAni4AniFramePiece;
import com.pipimage.image.PipAnimate;
import com.pipimage.image.PipAnimateFrame;
import com.pipimage.image.PipAnimateFrameRef;
import com.pipimage.image.PipAnimateSet;

public class EquipmentShow {

    public EquipmentShow() {

    }

    Shell shell;

    public void show(Display dsp) {
        if (dsp != null) {
            shell = new Shell(dsp);
        }
        else {
            shell = new Shell();
        }
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 10;
        shell.setLayout(gridLayout);
        shell.setText("动画预览");
        createPartContorl(shell);
        shell.open();
        // do {
        // if (shell.isDisposed())
        // break;
        // if (!Display.getDefault().readAndDispatch())
        // Display.getDefault().sleep();
        // } while (true);
        // Display.getDefault().dispose();
    }

    private String path[] = new String[5];
    private Text testPath;    
    private Text lightTestPath;
    private String lightPath;
    private AnimateViewer bodyAnimateViewer;
    private PipAnimateSet bodyAniSet = new PipAnimateSet();
    /**
     * 素体定义(hk)
     */
    private BodyDef bodyDef;
    /**
     * 挂接点和装配文件的映射;List的元素是装配文件的名称字符串;下标是挂接点的序号
     */
    private ArrayList<String>[] equips4hook;
    /**
     * 保存某个挂接点下,有哪些eqp文件
     */
    private ArrayList<EquipHookMap>[] equipHookMaps;
    /**
     * List的元素是 装配文件的名称(.eqp):装备动画名称(.cts)<br/> 下标是挂接点的序号
     */
    private ArrayList<String>[] equipAndCtsNamePare4hook;

    private void loadPipAnimate() {
        boolean isLoad = true;
        for (int i = 0; i < path.length; i++) {
            if (path[i] == null) {
                isLoad = false;
                return;
            }
        }
        if (isLoad) {
            checkFileExist();
            File pasFile = null;
            pasFile = new File(path[0] + path[1] + path[2] + "/atk.cts");

//            System.out.println("路径=" + pasFile.getPath());
            // 动画
            try {
                bodyAniSet.load(pasFile);
                System.out.println("bodyAniSet.load");
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }

            bodyAnimateViewer.setInput(bodyAniSet.getAnimate(0));

            // 挂接点
            File bodyFile = new File(path[0] + path[1] + path[2] + "/atk.hk");
            bodyDef = new BodyDef();
            try {
                bodyDef.loadHooks(bodyFile);
            }
            catch (IOException e1) {
                MessageDialog.openError(shell, "错误", "读取atk.hk文件异常" + e1.toString());
                return;
            }
            equips4hook = new ArrayList[bodyDef.hooks.size()];
            equipAndCtsNamePare4hook = new ArrayList[bodyDef.hooks.size()];
            equipHookMaps = new ArrayList[equips4hook.length];
            for (int i = 0; i < equips4hook.length; i++) {
                equips4hook[i] = new ArrayList<String>();
                equipAndCtsNamePare4hook[i] = new ArrayList<String>();
                equipHookMaps[i] = new ArrayList<EquipHookMap>();
            }

            bodyDef.embedHookPieces(bodyAniSet, -2, false);
            bodyAniTree.setInput(bodyAniSet);
            // 装备
            String equipsPath = testPath.getText();
            File equipsFile = new File(equipsPath);
            if (!equipsFile.exists()) {
                MessageDialog.openError(shell, "错误", "装备文件不存在");
                return;
            }
            
            //光效
            String lightsPath = lightTestPath.getText();
            File lightsFile = null;
            if(lightsPath != null && lightsPath.trim().equals("") == false) {
                lightsFile = new File(lightsPath);
                if (!lightsFile.exists()) {
                    MessageDialog.openError(shell, "错误", "光效文件不存在");
                    return;
                }                
            }

            List<File> files = findEqpFiles(equipsFile, ".eqp");
            if(lightsFile != null) {
                List<File> lightFiles = findEqpFiles(lightsFile, ".eqp");
                files.addAll(lightFiles);
            }
            
            for (File eqpFile : files) {
                EquipHookMap eqp2hook = new EquipHookMap();
                try {
                    eqp2hook.load(eqpFile.getParentFile().getAbsolutePath() + File.separator+"equ.eqp");
                }
                catch (IOException e) {
//                    e.printStackTrace();
                    MessageDialog.openError(shell, "错误", "读取.eqp出错");
                }
                File equipFile = null;
                equipFile = new File(eqpFile.getParentFile().getAbsolutePath() + File.separator + eqp2hook.getEquipCtsName());
                PipAnimateSet equipAniSet = new PipAnimateSet();
                try {
                    equipAniSet.load(equipFile);
                }
                catch (IOException e) {
                    MessageDialog.openError(shell, "错误", "读取装备动画文件出错");
//                    e.printStackTrace();
                }
                int idx = eqp2hook.getBodyIndex(bodyFile.getName());
                int bindHookId = eqp2hook.getHookId(idx);
                int cnt = bodyAniSet.getFrameCount();
                for (int i = 0; i < cnt; i++) {
                    PipAni4AniFramePiece hookPiece = bodyAniSet.getFrame(i).getHook(bindHookId);
                    if (hookPiece != null) {
                        hookPiece.setVisible(true);
                    }
                }
                int i = 0;
                for (PipAni4AniFramePiece hook : bodyDef.hooks) {
                    if (hook.getImageID() == bindHookId) {
                        equipHookMaps[i].add(eqp2hook);
                        equipHookMaps[i].get(idx).doEquip(bodyAniSet, equipAniSet, bodyFile.getName());
                        break;
                    }
                    i++;
                }
            }
            bodyAnimateViewer.redraw();
            bodyAnimateViewer.setCurrentFrame(0);
            bodyAnimateViewer.play();

        }
    }

    public void setFilePath() {
        testPath.setText(path[0] + path[1] + path[2] + path[3] + path[4]);
        if(lightLevel != -1) {
            lightPath = lightPathName[comboRoleWeaponType.getSelectionIndex()][lightLevel];
            lightTestPath.setText(path[0] + path[1] + path[2] + File.separator + "guangxiao" + File.separator + lightPath);
        }
        loadPipAnimate();
    }

//    private String[] roleTypeStr = { "人", "仙", "妖", "怪" };
    private String[] roleSexStr = { "男", "女" };
    // private String[] RoleEquipPart = {"girdle", "head",
    // "jacket","pants","wristlet","weapon"};
    // private String[] RoleEquipPartChn = { "腰带", "头部", "上衣","裤子","护腕","武器"};
    private String[] RoleEquipLevel = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
            "14", "15", };

    //西游专用, 以后想办法再挪出来吧
    private String[] RoleLightLevel = { "01", "02", "03", "04", "05",};
    private String[][] lightPathName = {
            { "01", "02", "03", "04", "005",}, //斧
            { "01", "02", "03", "04", "005",}, //锤
            { "01", "002", "03", "04", "005",}, //法杖
            { "01", "002", "03", "04", "005",}, //杵
            { "01", "002", "03", "04", "005",}, //刀
            { "01", "002", "03", "04", "005",}, //剑
            { "01", "002", "03", "04", "005",},  //钩
            { "01", "0002", "0003", "0004", "0005",}, //如意
            
    };
    
    private Combo comboRoleWeaponType;
    
    private void createPartContorl(Composite parent) {
        testPath = new Text(shell, SWT.SINGLE | SWT.BORDER);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 10;
        testPath.setLayoutData(gridData);
        ProjectData proj = ProjectData.getActiveProject();
        String str = proj.baseDir +"\\" +proj.config.pipLibDir+"../"+"avatar";
        try{
        path[0] = new File(str).getCanonicalPath();
        }catch(IOException e){
            e.printStackTrace();
        }
        Label labelRoleType = new Label(parent, SWT.NONE);
        labelRoleType.setText("种族：");
        final Combo comboRoleType = new Combo(parent, SWT.BORDER);
        comboRoleType.setItems(new String[] { "氏人", "玄仙", "幻妖", "灵怪" });
        comboRoleType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                String ss = comboRoleType.getText();
                path[1] = File.separator + ProjectData.getActiveProject().config.jobMap.get(comboRoleType.getText());
                setFilePath();
            }
        });

        Label labelRoleSex = new Label(parent, SWT.NONE);
        labelRoleSex.setText("性别：");
        final Combo comboRoleSex = new Combo(parent, SWT.BORDER);
        comboRoleSex.setItems(roleSexStr);
        comboRoleSex.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                path[2] = File.separator + comboRoleSex.getText();
                setFilePath();
            }
        });
        
        Label labelRoleEquipPart = new Label(parent, SWT.NONE);
        labelRoleEquipPart.setText("装备部位："); 
        final Combo comboRoleEquipPart = new Combo(parent, SWT.BORDER);
        Label labelRoleWeaponPart = new Label(parent, SWT.NONE);
        
        labelRoleWeaponPart.setText("武器类型：");
        comboRoleWeaponType = new Combo(parent, SWT.BORDER);
        if(equipPart >=0 && "武器".equals(proj.config.COMBO_PLACE[equipPart])) {
            comboRoleWeaponType.setEnabled(true);
        } else {
            comboRoleWeaponType.setEnabled(false);
        }
        
        comboRoleEquipPart.setVisibleItemCount(proj.config.COMBO_PLACE.length);
        comboRoleEquipPart.setItems(proj.config.COMBO_PLACE);
        comboRoleEquipPart.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                String pathTemp = ProjectData.getActiveProject().config.bodyPartMap.get(ProjectData.getActiveProject().config.COMBO_PLACE[comboRoleEquipPart
                        .getSelectionIndex()]).dirKey;
                if("weapon".equals(pathTemp)) {
                    comboRoleWeaponType.setEnabled(true);
                    comboRoleWeaponType.select(weaponType);
                } else {
                    comboRoleWeaponType.setEnabled(false);
                    path[3] = File.separator + pathTemp;
                    if (pathTemp == "") {
                        MessageDialog.openError(shell, "错误", "此装备部位没有动画");
                        return;
                    }
                    setFilePath();
                }

            }
        });
        if (equipPart != -1) {
            comboRoleEquipPart.select(equipPart);
            String pathTemp = proj.config.bodyPartMap.get(proj.config.COMBO_PLACE[comboRoleEquipPart
                    .getSelectionIndex()]).dirKey;
            path[3] = File.separator + pathTemp;
        }
        
        //武器类型
        comboRoleWeaponType.setVisibleItemCount(proj.config.COMBO_WEAPON_TYPE.length);
        comboRoleWeaponType.setItems(proj.config.COMBO_WEAPON_TYPE);
        comboRoleWeaponType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                String pathTemp = ProjectData.getActiveProject().config.bodyPartMap.get(ProjectData.getActiveProject().config.COMBO_WEAPON_TYPE[comboRoleWeaponType.getSelectionIndex()]).dirKey;
                path[3] = File.separator + pathTemp;
                if (pathTemp == "") {
                    MessageDialog.openError(shell, "错误", "此武器位没有动画");
                    return;
                }
                setFilePath();
            }
        });        

        Label labelRoleEquipLevel = new Label(parent, SWT.NONE);
        labelRoleEquipLevel.setText("装备等级：");
        final Combo comboRoleEquipLevel = new Combo(parent, SWT.BORDER);
        comboRoleEquipLevel.setVisibleItemCount(20);
        comboRoleEquipLevel.setItems(RoleEquipLevel);
        comboRoleEquipLevel.select(equipLevel);
        comboRoleEquipLevel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                path[4] = File.separator + comboRoleEquipLevel.getText();
                setFilePath();
            }
        });
        if (equipLevel != -1) {
            comboRoleEquipLevel.select(equipLevel);
            String pathTemp = RoleEquipLevel[equipLevel];
            path[4] = File.separator + pathTemp;
        }
        testPath.setText(path[0] + path[1] + path[2] + path[3] + path[4]);
        // //////////////
        
        lightTestPath = new Text(shell, SWT.SINGLE | SWT.BORDER);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 10;
        lightTestPath.setLayoutData(gridData);
        Label labelRoleLightLevel = new Label(parent, SWT.NONE);
        labelRoleLightLevel.setText("光效等级：");
        final Combo comboRoleLightLevel = new Combo(parent, SWT.BORDER);
        if(equipPart >=0 && "武器".equals(proj.config.COMBO_PLACE[equipPart])) {
            comboRoleLightLevel.setEnabled(true);
        } else {
            comboRoleLightLevel.setEnabled(false);
        }
        comboRoleLightLevel.setVisibleItemCount(5);
        comboRoleLightLevel.setItems(RoleLightLevel);
        comboRoleLightLevel.select(lightLevel);
        comboRoleLightLevel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                lightLevel = comboRoleLightLevel.getSelectionIndex();                
                setFilePath();
                loadPipAnimate();
            }
        });
        

        GridData gridData3 = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData3.horizontalSpan = 10;
        SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
        sashForm.setLayoutData(gridData3);
        createLeft(sashForm);
        createRight(sashForm);
        sashForm.setWeights(new int[] { 1, 1 });
    }

    private TreeViewer bodyAniTree;

    AnimateContentTreeProvider animateContentTreeProvider = new AnimateContentTreeProvider();

    private void createRight(SashForm sashForm) {
        bodyAniTree = new TreeViewer(sashForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        bodyAniTree.setContentProvider(animateContentTreeProvider);
        final AnimateLabelProvider animateLabelProvider = new AnimateLabelProvider();
        bodyAniTree.setLabelProvider(animateLabelProvider);
        bodyAniTree.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent arg0) {
                bodyAniTreeSelChange();
            }
        });
    }

    private void createLeft(SashForm sashForm) {
        bodyAnimateViewer = new AnimateViewer(sashForm, SWT.NONE);
        bodyAnimateViewer.setInput(null);
        GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData2.horizontalSpan = 8;
        gridData2.widthHint = SWT.DEFAULT;
        gridData2.heightHint = SWT.DEFAULT;
        bodyAnimateViewer.setLayoutData(gridData2);
    }

    private List<File> findEqpFiles(File dir, String str) {
        if (dir.isDirectory() == false) {
            return null;
        }
        List<File> files = new ArrayList<File>();

        String[] filesName = dir.list();
        for (String fileName : filesName) {
            if (new File(dir, fileName).isDirectory()) {
                files.addAll(findEqpFiles(new File(dir, fileName), str));
            }
            else {
                if (fileName.endsWith(str)) {
                    files.add(new File(dir, fileName));
                }
            }
        }

        return files;
    }

    /**
     * 混杂的TreeProvider,给动画树和装备树用
     */
    public class AnimateContentTreeProvider implements ITreeContentProvider {

        public void dispose() {
        }

        public Object[] getChildren(Object arg0) {
            if (arg0 instanceof List) {
                return ((List) arg0).toArray();
            }
            else if (arg0 instanceof PipAnimateSet) {
                PipAnimateSet animateSet = (PipAnimateSet) arg0;
                int cnt = animateSet.getAnimateCount();
                PipAnimate[] ret = new PipAnimate[cnt];
                for (int i = 0; i < cnt; i++) {
                    ret[i] = (animateSet.getAnimate(i));
                }
                return ret;
            }
            else if (arg0 instanceof PipAnimate) {
                PipAnimate animate = (PipAnimate) arg0;
                int cnt = animate.getFrameCount();
                PipAnimateFrameRef[] frameRefs = new PipAnimateFrameRef[cnt];
                for (int i = 0; i < cnt; i++) {
                    frameRefs[i] = animate.getFrame(i);
                }
                return frameRefs;
            }
            else if (arg0 instanceof List) {// hooks
                return ((List) arg0).toArray();
            }
            else if (arg0 instanceof PipAni4AniFramePiece) {
                int idx = 0;
                for (idx = 0; idx < bodyDef.hooks.size(); idx++) {
                    if (bodyDef.hooks.get(idx).getImageID() == ((PipAni4AniFramePiece) arg0).getImageID())
                        break;
                }
                List eqpsList = equipAndCtsNamePare4hook[idx];
                return eqpsList.toArray();
            }
            // else if(arg0 instanceof String){
            // String txt = (String)arg0;
            // return leveledCts4eqp.get(txt).toArray();
            // }
            return null;
        }

        public Object[] getElements(Object arg0) {
            return getChildren(arg0);
        }

        public Object getParent(Object arg0) {
            if (arg0 instanceof PipAnimate) {
                return ((PipAnimate) arg0).getParent();
            }
            else if (arg0 instanceof PipAnimateFrameRef) {
                return ((PipAnimateFrameRef) arg0).getParent();
            }
            return null;
        }

        public boolean hasChildren(Object arg0) {
            if (arg0 instanceof List) {
                return ((List) arg0).size() > 0;
            }
            else if (arg0 instanceof PipAnimateSet) {
                return ((PipAnimateSet) arg0).getAnimateCount() > 0;
            }
            else if (arg0 instanceof PipAnimate) {
                return ((PipAnimate) arg0).getFrameCount() > 0;
            }
            else if (arg0 instanceof PipAni4AniFramePiece) {
                int idx = 0;
                for (idx = 0; idx < bodyDef.hooks.size(); idx++) {
                    if (bodyDef.hooks.get(idx).getImageID() == ((PipAni4AniFramePiece) arg0).getImageID())
                        break;
                }
                List eqpsList = equips4hook[idx];
                if (eqpsList == null || eqpsList.size() == 0) {
                    return false;
                }
                else {
                    return true;
                }
            }
            else if (arg0 instanceof String) {
                // String txt = (String)arg0;
                // return txt.indexOf(".eqp")>0 && txt.indexOf(".cts")>0;
                // return txt.indexOf("(equ.cts)")>0;
            }
            return false;
        }

        public void inputChanged(Viewer arg0, Object arg1, Object arg2) {

        }
    }

    /**
     * 混杂的LabelProvider,给动画树和装备树用
     */
    public class AnimateLabelProvider extends LabelProvider {
        public Image getImage(Object obj) {
            if (obj instanceof PipAnimateSet) {
                return WorkshopPlugin.getDefault().getImageRegistry().get("grid");
            }
            else if (obj instanceof PipAnimate) {
                return WorkshopPlugin.getDefault().getImageRegistry().get("animate");
            }
            else if (obj instanceof PipAnimateFrameRef) {
                return WorkshopPlugin.getDefault().getImageRegistry().get("image");
            }
            return null;
        }

        public String getText(Object obj) {
            if (obj instanceof PipAnimateSet) {
                // int idx = bodyAniSets.indexOf(obj);
                // return eqp2hook.getHookFileName(idx);
            }
            else if (obj instanceof PipAnimate) {
                int aniIdxInAniSet = 0;
                // 取此动画在动画组中的下标
                PipAnimate pa = (PipAnimate) obj;
                PipAnimateSet pas = pa.getParent();
                int cnt = pas.getAnimateCount();
                for (int i = 0; i < cnt; i++) {
                    if (pas.getAnimate(i) == obj) {
                        aniIdxInAniSet = i;
                    }
                }
                // 返回下标和动画名称
                return aniIdxInAniSet + ":" + ((PipAnimate) obj).getName();
            }
            else if (obj instanceof PipAnimateFrameRef) {
                int frameIdxInAniSet = ((PipAnimateFrameRef) obj).getFrame();
                return frameIdxInAniSet + ":" + ((PipAnimateFrameRef) obj).realize().getName();
            }
            else if (obj instanceof PipAni4AniFramePiece) {
                return ((PipAni4AniFramePiece) obj).name;
            }
            else if (obj instanceof String) {
                return (String) obj;
            }
            return obj.toString();
        }
    }

    private int selAnimateIndex = -1;
    private int selFrameRefIndex = -1;
    private PipAnimateFrame curBodyFrame;
    private int equipLevel, equipPart = -1, weaponType = -1, lightLevel = -1;

    protected void bodyAniTreeSelChange() {
        IStructuredSelection sel = (IStructuredSelection) bodyAniTree.getSelection();
        if (sel.isEmpty()) {
            return;
        }
        Object selObj = sel.getFirstElement();
        if (selObj instanceof PipAnimateSet) {
            bodyAniSet = (PipAnimateSet) selObj;
            changeSelBody();
            bodyAnimateViewer.stop();
            bodyAnimateViewer.setInput(null);
            bodyAnimateViewer.redraw();
        }
        else if (selObj instanceof PipAnimate) {
            PipAnimate selAnimate = (PipAnimate) selObj;
            bodyAniSet = selAnimate.getParent();
            selAnimateIndex = bodyAniSet.getAnimateIndex(selAnimate);
            selFrameRefIndex = -1;
            changeSelBody();
            bodyAnimateViewer.setInput(selAnimate);
            bodyAnimateViewer.setCurrentFrame(0);
            bodyAnimateViewer.play();
        }
        else if (selObj instanceof PipAnimateFrameRef) {
            PipAnimateFrameRef selFrameRef = (PipAnimateFrameRef) selObj;
            PipAnimate selAnimate = selFrameRef.getParent();
            bodyAniSet = selAnimate.getParent();
            selAnimateIndex = bodyAniSet.getAnimateIndex(selAnimate);
            selFrameRefIndex = selAnimate.getFrameIndex(selFrameRef);
            changeSelBody();
            curBodyFrame = ((PipAnimateFrameRef) selObj).realize();
            bodyAnimateViewer.stop();
            PipAnimate pa = (PipAnimate) animateContentTreeProvider.getParent(selObj);
            bodyAnimateViewer.setInput(pa);
            int cnt = pa.getFrameCount();
            int frameIdxInAni = 0;
            for (int i = 0; i < cnt; i++) {
                if (pa.getFrame(i) == selObj) {
                    frameIdxInAni = i;
                    break;
                }
            }
            bodyAnimateViewer.setCurrentFrame(frameIdxInAni);
            bodyAnimateViewer.redraw();
            // runHookAniDriver();
        }
    }

    private void changeSelBody() {
        // TODO Auto-generated method stub
    }

    public void setLevel(int eqL) {
        equipLevel = eqL - 1;
    }

    public void setPart(int eqP) {
        equipPart = eqP;
    }
    
    public void setWeaponType(int weaponType) {
        this.weaponType = weaponType;
    }

    private void checkFileExist() {
        if (!new File(path[0], path[1]).exists()) {
            MessageDialog.openError(shell, "错误", "文件夹   " + path[1] + "  不存在");
            return;
        }
        else if (!new File(path[0] + path[1], path[2]).exists()) {
            MessageDialog.openError(shell, "错误", "文件夹   " + path[2] + "  不存在");
            return;
        }
        else if (!new File(path[0] + path[1] + path[2], path[3]).exists()) {
            MessageDialog.openError(shell, "错误", "文件夹   " + path[3] + "  不存在");
            return;
        }
        else if (!new File(path[0] + path[1] + path[2] + path[3], path[4]).exists()) {
            MessageDialog.openError(shell, "错误", "文件夹   " + path[4] + "  不存在");
            return;
        }
    }
}
