package com.pip.game.data.skill;

import java.io.PrintWriter;

/**
 * Ϊ�༭����չ��ʹ��
 * 
 * @author ybai
 *
 */
public interface IBuffConfig {
    public int getId();
    
    public String getTitle();
    
    public String getClassName(String classPrefix);
    
    public void generateJava(PrintWriter out, String packageName, String classPrefix);
}
