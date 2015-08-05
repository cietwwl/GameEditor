package com.pip.game.data.effectExt;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.EffectConfig;
import com.pip.util.Utils;
/**
 * 如何定制效果的示例类
 * @author jhkang
 *
 */
public class CustomerEffectDemo extends EffectConfig  {
    private int[] paramData = new int[]{3};
    ////////////////////////////////embed access methods
    /**
     * 由于历史原因,构造函数上有个type
     */
    public CustomerEffectDemo(int type) {//force constructor match create code
    }

//    /**
//     * 编辑器启动时会加载内置效果和项目扩展的效果.此ID必须唯一,且和配置文件中的一致
//     * @return
//     */
//    public static int getId(){
//        return 1000;
//    }
//    
//    /**
//     * 在编辑界面显示的名称;第0个是右侧列表上显示的;第1个是简称,在表格列名上做前缀
//     * @return
//     */
//    public static String[] getTypeNames(){
//        return new String[]{ "测试定制效果", "测试效果" };
//    }
//    
//    /**
//     * 取得参数关键字,在程序运行中使用
//     * @return
//     */
//    public static String[] getTypeParames(){
//        return new String[]{  };
//    }
//    
//    /**
//     * 此效果支持的Buff
//     */
//    public static int supportBuff = BuffConfig.BUFF_TYPE_DYNAMIC|BuffConfig.BUFF_TYPE_STATIC|BuffConfig.BUFF_TYPE_EQUIP;
//    
//    /**
//     * 此效果支持的技能
//     */
//    public static int supportedSkillType = SkillConfig.TYPE_PASSIVE;
    ////////////////////////////////

    @Override
    public Object getParam(int index) {
        return paramData;
    }

    @Override
    public Class getParamClass(int index) {
        return Integer.class;
    }

    @Override
    public int getParamCount() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParamName(int index) {
        return "参数1";
    }

    @Override
    public int getType() {
        return ProjectData.getActiveProject().effectConfigManager.getTypeId(this.getClass());
    }

    @Override
    public void setLevelCount(int max) {
        paramData = Utils.realloc(paramData, max);
    }

    @Override
    public String getJavaInterface() {
        // TODO Auto-generated method stub
        return null;
    }
    
    

}
