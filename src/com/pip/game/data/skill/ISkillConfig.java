package com.pip.game.data.skill;

import java.io.PrintWriter;

import com.pip.game.data.effects0.EffectRejectException;

/**
 * Ϊ�༭����չ��ʹ��
 * 
 * @author ybai
 *
 */
public interface ISkillConfig {
    public int getId();
    
    public String getTitle();
    
    public String getClassName(String classPrefix);
    
    public void generateJava(PrintWriter out, String packageName, String classPrefix) throws EffectRejectException;
}
