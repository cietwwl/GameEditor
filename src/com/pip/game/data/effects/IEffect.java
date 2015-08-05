/**
 * 
 */
package com.pip.game.data.effects;

import java.io.PrintWriter;

/**
 * @author jhkang
 *
 */
public interface IEffect {
    public void generateJava4buff(PrintWriter out, String packageName, String classPrefix) ;
    public void generateJava4skill(PrintWriter out, String packageName, String classPrefix) ;
    
    public String getJavaInterface4buff();
    public float getSkillDamage(int level);
    
    public int getId();
}
