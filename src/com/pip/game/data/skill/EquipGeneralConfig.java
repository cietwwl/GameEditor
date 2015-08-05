package com.pip.game.data.skill;

public class EquipGeneralConfig extends EffectConfig {
    
    BuffConfig config;
    
    public EquipGeneralConfig(BuffConfig config){
        this.config = config;
    }
    
    public String getTypeName() {
        return "";
    }
    
    public int getType(){
        return -3;
    }
    
    public void setLevelCount(int max) {
    }
    
    public int getParamCount(){
        return 1;
    }
    
    public String getParamName(int index){
        if(index == 0){
        return "特效价值";
        }
        return "";
    }
    
    public Class getParamClass(int index) {
        return Integer.class;
    }
    
    public Object getParam(int index) {
        if(index == 0){
        return config.value;
        }
        return null;
    }

    @Override
    public String getJavaInterface() throws Exception {
        //不产生接口
        return "-1";
    }
}
