package com.pip.game.editor.wizard;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.pip.game.data.DataObjectCategory;
import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.AttributeCalculator;
import com.pip.game.data.equipment.Equipment;
import com.pip.game.data.equipment.EquipmentPrefix;
import com.pip.game.editor.DataListView;
import com.pip.game.editor.EditorApplication;

public class NewEquipmentWizard implements Runnable {

    public void run() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DataListView view = (DataListView) page.findView(DataListView.ID);
        
        // 新建装备对话框
        Object[] obj = view.getSelectedObjects();
        DataObjectCategory type = null;
        if (obj.length > 0) {
            if (obj[0] instanceof DataObjectCategory) {
                type = (DataObjectCategory)obj[0];
            } else if (obj[0] instanceof Equipment) {
                Equipment equ = (Equipment)obj[0];
                type = equ.owner.findCategory(Equipment.class, equ.getCategoryName());
            }
        }
        NewEquipmentDialog dlg = new NewEquipmentDialog(shell, type);
        
        if (dlg.open() != NewEquipmentDialog.OK) {
            return;
        }
        
        ProjectData proj = ProjectData.getActiveProject();
        try {
            // 批量生成装备
            DataObjectCategory equiType = dlg.equiType;
            for (int i = 0; i < dlg.prefixes.length; i++) {
                // 每个前缀新建一个装备
                Equipment equi = proj.newEquipment(equiType, DataListView.getSelectObject());
                EquipmentPrefix prefix = dlg.prefixes[i];
                
                if (prefix.id == -1) {
                    equi.title = dlg.name;                        
                } else {
                    equi.title = prefix.title + dlg.name;
                }
                equi.level = dlg.level;
                equi.playerLevel = dlg.requireLevel;
                equi.place = dlg.place;
                equi.equipmentType = Equipment.getType(dlg.place);
                equi.quality = dlg.quality;
                equi.bind = dlg.bindType;
                equi.prefix = prefix;
                
                // 重新设置图标
                equi.resetIcon();
                
                // 根据前缀分配生成属性
                equi.generateAttributes();
                
                // 计算价格和耐久度
                equi.recalcPriceAndDurability();
                
                // 缺省白装不能鉴定星级
                if (equi.quality == Equipment.QUALITY_WHITE) {
                    equi.canJudgeStar = false;
                } else {
                    equi.canJudgeStar = true;
                }
                
                // 缺省白装和饰品不能鉴定资质
                if (equi.quality == Equipment.QUALITY_WHITE) {
                    equi.canJudgePotential = false;
                } else if (Equipment.getType(equi.place) == Equipment.EQUI_TYPE_JEWELRY ||
                        Equipment.getType(equi.place) == Equipment.EQUI_TYPE_HORSE) {
                    equi.canJudgePotential = false;
                } else {
                    equi.canJudgePotential = true;
                }
            }
            
            if (view != null) {
                view.refresh(Equipment.class);
            }
            
            // 保存本类型数据列表
            proj.saveDataList(Equipment.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
