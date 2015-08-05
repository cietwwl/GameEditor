/**
 * 
 */
package com.pip.game.data.effects;

import java.io.PrintWriter;

/**
 * @author jhkang
 *
 */
public class DemoEffect extends EffectAdapter {
    private static final int id = 999;
    private int type;
    public DemoEffect(int t){
        type = t;
    }

    @Override
    public void generateJava4buff(PrintWriter out, String packageName, String classPrefix) {
        out.println("        System.out.println(\"DemoEffect.generateJava4 buff()\");");
    }

    @Override
    public void generateJava4skill(PrintWriter out, String packageName, String classPrefix) {
        out.println("        System.out.println(\"DemoEffect.generateJava4 skill()\")");
    }
   @Override
    public String getJavaInterface4buff() {
        return super.getJavaInterface4buff();
    }

    @Override
    public float getSkillDamage(int level) {
        // TODO Auto-generated method stub
        return super.getSkillDamage(level);
    }
    
    public static int getId(){
        return id;
    }

    @Override
    public Object getParam(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class getParamClass(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getParamCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getParamName(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setLevelCount(int max) {
        // TODO Auto-generated method stub
        
    }
    public static String[] getTypeNames(){
        return new String[]{"DemoEffect", "Demo"};
    }
    
    public static String[] getTypeParames(){
        return new String[]{"DemoP1", "demoP2"};
    }

    @Override
    public String getJavaInterface() {
        // TODO Auto-generated method stub
        return null;
    }
}
