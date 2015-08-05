package com.pip.game.data.skill;

public class StaticGeneralConfig extends EffectConfig {

    BuffConfig config;
    
    public StaticGeneralConfig(BuffConfig config){
        this.config = config;
    }
    
    public String getTypeName() {
        return "";
    }
    
    public int getType(){
        return -1;
    }
    
    public void setLevelCount(int max) {
    }
    
    public int getParamCount(){
        return 0;
    }
    
    public String getParamName(int index){
        return "";
    }
    
    public Class getParamClass(int index) {
        return Integer.class;
    }
    
    public Object getParam(int index) {
        return 0;
    }

    @Override
    public String getJavaInterface() throws Exception {
        //不产生接口
        return "-1";
    }

}
