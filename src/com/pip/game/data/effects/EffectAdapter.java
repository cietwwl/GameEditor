/**
 * 
 */
package com.pip.game.data.effects;

import java.io.PrintWriter;

import com.pip.game.data.skill.EffectConfig;

/**
 * @author jhkang
 *
 */
public abstract class EffectAdapter extends EffectConfig{

    public EffectAdapter(){
        
    }
    /* (non-Javadoc)
     * @see com.pip.game.data.effects.IEffect#generateJava4buff(java.io.PrintWriter, java.lang.String, java.lang.String)
     */
    public void generateJava4buff(PrintWriter out, String packageName, String classPrefix) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.pip.game.data.effects.IEffect#generateJava4skill(java.io.PrintWriter, java.lang.String, java.lang.String)
     */
    public void generateJava4skill(PrintWriter out, String packageName, String classPrefix) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.pip.game.data.effects.IEffect#getJavaInterface4buff()
     */
    public String getJavaInterface4buff() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.pip.game.data.effects.IEffect#getSkillDamage(int)
     */
    public float getSkillDamage(int level) {
        // TODO Auto-generated method stub
        return 0;
    }

    public static int getId()
    {
            throw new IllegalStateException("此效果尚未实现,不能使用");
    }
}
