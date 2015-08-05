package com.pip.game.data.skill;

import com.pip.game.data.ProjectData;

public class DynamicGeneralConfig extends EffectConfig {
    
    public BuffConfig config;
    
    public DynamicGeneralConfig(BuffConfig config){
        this.config = config;
        this.manager = ProjectData.getActiveProject().effectConfigManager;
    }
    
    public String getTypeName() {
        return "";
    }
    
    public int getType(){
        return -2;
    }
    
    public void setLevelCount(int max) {
    }
    
    public int getParamCount(){
        return 4;
    }
    
    public String getParamName(int index){
        if(index == 0){
            return "����ʱ��(����)";
        }
        else if(index == 1){
            return "����ʱ��(�غ�)";
        }
        else if(index == 2){
            return "����ʱ��(����)";
        }
        else if(index == 3){
            return "����ʱ��(����)";
        }
        return "";
    }
    
    public Class getParamClass(int index) {
        return Integer.class;
    }
    
    public Object getParam(int index) {
        if(index == 0)
            return config.duration;
        if(index == 1)
            return config.round_times;
        if(index == 2)
            return config.battle_times;
        if(index == 3)
            return config.times; 
        return null;
    }



    @Override
    public String getJavaInterface() throws Exception {
      //�������ӿ�
        return "-1";
    }

}
