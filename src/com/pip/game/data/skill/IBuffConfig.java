package com.pip.game.data.skill;

import java.io.PrintWriter;

/**
 * 为编辑器扩展而使用
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
