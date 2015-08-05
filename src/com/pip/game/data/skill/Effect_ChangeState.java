package com.pip.game.data.skill;

import com.pip.game.editor.property.StateCellEditor;
import com.pip.util.Utils;

/**
 * 效果：改变状态
 * 
 * @author yqwang
 */
public class Effect_ChangeState extends EffectConfig {
    private int type;
    private int[] stateList = new int[0];
    public Effect_ChangeState(int t) {
        type = t;
    }
    
    /**
     * 取得效果类型ID
     */
    public int getType() {
        return type;
    }
    
    /**
     * 取得参数个数
     */
    public int getParamCount() {
        return 1;
    }
    
    /**
     * 取得参数的名字
     */
    public String getParamName(int index) {
        return "状态";
    }
    
    /**
     * 取得参数的类型。
     * @return 可能是Integer, Float或String
     */
    public Class getParamClass(int index) {
        return StateCellEditor.class;
    }
    
    /**
     * 取得某个参数各级别的参数值
     * @return 可能是int[], float[]或String[]
     */
    public Object getParam(int index) {
        return stateList;
    }

    @Override
    public void setLevelCount(int max) {
        stateList = Utils.realloc(stateList, max);
    }

    @Override
    public String getJavaInterface() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
