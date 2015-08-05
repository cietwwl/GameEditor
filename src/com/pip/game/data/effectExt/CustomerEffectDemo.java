package com.pip.game.data.effectExt;

import com.pip.game.data.ProjectData;
import com.pip.game.data.skill.EffectConfig;
import com.pip.util.Utils;
/**
 * ��ζ���Ч����ʾ����
 * @author jhkang
 *
 */
public class CustomerEffectDemo extends EffectConfig  {
    private int[] paramData = new int[]{3};
    ////////////////////////////////embed access methods
    /**
     * ������ʷԭ��,���캯�����и�type
     */
    public CustomerEffectDemo(int type) {//force constructor match create code
    }

//    /**
//     * �༭������ʱ���������Ч������Ŀ��չ��Ч��.��ID����Ψһ,�Һ������ļ��е�һ��
//     * @return
//     */
//    public static int getId(){
//        return 1000;
//    }
//    
//    /**
//     * �ڱ༭������ʾ������;��0�����Ҳ��б�����ʾ��;��1���Ǽ��,�ڱ����������ǰ׺
//     * @return
//     */
//    public static String[] getTypeNames(){
//        return new String[]{ "���Զ���Ч��", "����Ч��" };
//    }
//    
//    /**
//     * ȡ�ò����ؼ���,�ڳ���������ʹ��
//     * @return
//     */
//    public static String[] getTypeParames(){
//        return new String[]{  };
//    }
//    
//    /**
//     * ��Ч��֧�ֵ�Buff
//     */
//    public static int supportBuff = BuffConfig.BUFF_TYPE_DYNAMIC|BuffConfig.BUFF_TYPE_STATIC|BuffConfig.BUFF_TYPE_EQUIP;
//    
//    /**
//     * ��Ч��֧�ֵļ���
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
        return "����1";
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
