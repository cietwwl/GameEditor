/**
 * 
 */
package com.pip.game.data.autocoding;

import java.io.File;
import java.io.PrintWriter;

import com.pip.game.data.skill.EffectConfig;
import com.pip.util.Utils;

/**
 * @author jhkang
 *
 */
public class AutoEmbedCodeGen {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        AutoEffectFromPreDefined.main(null);
        embedBuffEnhance();
        embedBuffFnished();
        embedBuffPostDamage();
        embedBuffPostHit();
        embedBuffPreDamage();
        embedBuffPreHit();
        embedSkillFnished();
        embedSkillPostDamage();
        embedSkillPreDamage();
        embedSkillPreHit();
    }
    public static void embedBuffPreHit() throws Exception{
        String methodSignature = "    public void genBuffPreHit(PrintWriter out, String p1, String p2, String p3, String p4,String p5, String p6)";
        embed(BuffPreHit.class, methodSignature);
    }
    
    public static void embedBuffEnhance() throws Exception{
        String methodSignature = "    public void genBuffEnhance(PrintWriter out, String p1, String p2)";  
        embed(BuffEnhance.class, methodSignature);
    }
    public static void embedBuffFnished() throws Exception{
        String methodSignature = "    public void genBuffFinished(PrintWriter out, String p1, String p2, String p3, String p4,"+
                "String p5, String p6)";
        embed(BuffFinished.class, methodSignature);
    }
    
    public static void embedBuffPostDamage() throws Exception{
        String methodSignature = "    public void genBuffPostDamage(PrintWriter out, String p1, String p2, String p3)";
        embed(BuffPostDamage.class, methodSignature);
    }
    public static void embedBuffPostHit() throws Exception{
        String methodSignature = "    public void genBuffPostHit(PrintWriter out, String p1, String p2)";
        embed(BuffPostHit.class, methodSignature);
    }
    public static void embedBuffPreDamage() throws Exception{
        String methodSignature = "    public void genBuffPreDamage(PrintWriter out, String p1, String p2, String p3)";
        embed(BuffPostDamage.class, methodSignature);
    }
    
    public static void embedSkillFnished() throws Exception{
        String methodSignature = "    public void genSkillFinished(PrintWriter out, String p1, String p2, String p3, String p4,"+
                "String p5, String p6)";
        embed(SkillFinished.class, methodSignature);
    }
    
    public static void embedSkillPostDamage() throws Exception{
        String methodSignature = "    public void genSkillPostDamage(PrintWriter out, String p1, String p2, String p3)";
        embed(SkillPostDamage.class, methodSignature);
    }
    
    public static void embedSkillPreDamage() throws Exception{
        String methodSignature = "    public void genSkillPreDamage(PrintWriter out,  int damageType, String p1, String p2)";
        embed(SkillPreDamage.class, methodSignature);
    }
    
    public static void embedSkillPreHit() throws Exception{
        String methodSignature = "    public void genSkillPreHit(PrintWriter out, int damageType, String p1)";
        embed(SkillPreHit.class, methodSignature);
    }
    //====================================
    public static void embed(Class clz, String methodSignature) throws Exception{
        String dir = "E:/workspace//Game-Editor1.0/src";
        String path = clz.getName().replace(".", "/")+".java";
        String content = Utils.loadFileContent( new File(dir+"/"+path) );
//        System.out.println(content);
        String[] lines = content.split("\n");
        StringBuffer codeBuff = new StringBuffer();
        boolean inBlock = false;
        String type = null;
        for(String line:lines){
            String trim = line.trim();
            if(trim.startsWith("case")){
                inBlock = true;
                if(trim.indexOf("EffectConfig")>0){
                    type = trim.split("EffectConfig")[1].replace(".", "").replace(":", "");
                }else{
                    type = trim.split("EffectConfig")[1].replace(".", "").replace(":", "");
                }
            }else if(trim.startsWith("break")){
                inBlock = false;
                String code = codeBuff.toString();
                System.out.println(type);
//                System.out.println(code);
                File f = new File("E:/workspace/Game-Editor1.0/src/com/pip/game/data/effects0/Effect_"+type+".java");
                String destFileContent = Utils.loadFileContent(f);
                int lastBraceIndex = destFileContent.lastIndexOf("}");
                destFileContent = destFileContent.substring(0, lastBraceIndex);
                destFileContent += "\n"+methodSignature + "{\n"+code +"    }"+"\n}";
                Utils.saveFileContent(f, destFileContent);
//                System.out.println(destFileContent);
                codeBuff.delete(0, codeBuff.length());
            }else if(inBlock){
                codeBuff.append("        "+trim+"\n");
            }
        }
    }

}
