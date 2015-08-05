/**
 * 
 */
package com.pip.game.data.autocoding;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.pip.game.data.skill.EffectConfig;
import com.pip.util.Utils;

/**
 * @author jhkang
 * 0    parent class
 * 1    effect id,type,class name postfix
 * 2    type names(show in editor): format "abc", "bce"
 * 3    parameter names: format "change_physical_ap_value", "change_physical_ap_percent"
 * 4    support buff: format true|false
 * 5    support skill type: format SkillConfig.TYPE_ATTACK|SkillConfig.TYPE_OTHER
 * 6    java interface for buff
 */
public class AutoEffectFromPreDefined {
    public static void main(String[] arg) throws Exception{
        String pattern = readTemplate();
        HashMap<String, String> interfaceMapping = BuffGetJavaInterface.makeInterfaceMapping();
        List<Field> fields = getFiledNames();
        int last = 0;
        for(Field field:fields){
          ///fix buff support code
          String p1type = field.getName();
          File f = new File("E:/workspace/Game-Editor1.0/src/com/pip/game/data/effects0/Effect_"+p1type+".java");
          String content = Utils.loadFileContent(f);
          String replacement = "import com.pip.game.data.skill.SkillConfig;\n" +
          		"import com.pip.game.data.skill.BuffConfig;";
//          content = content.replace("import com.pip.game.data.skill.SkillConfig;", replacement);
          replacement = "public static int supportBuff = BuffConfig.BUFF_TYPE_DYNAMIC|BuffConfig.BUFF_TYPE_STATIC|BuffConfig.BUFF_TYPE_EQUIP;";
//          content = content.replace("public static boolean supportBuff = true;", replacement);
          replacement = "public static int supportBuff = 0;";
//          content = content.replace("public static boolean supportBuff = false;", replacement);
          replacement = "public static int supportedSkillType";
          content = content.replace("private static int supportedSkillType", replacement);
          
          Utils.saveFileContent(f, content);
          ///fix buff support code
          
//            int idx = field.getInt(EffectConfig.class);
//            if(idx<=last){
//                throw new Exception("Wrong order");
//            }
//            last = idx;
//            String p0parent = EffectConfig.TYPE_CLASSES[idx - 1].getSimpleName();
//            String p1type = field.getName();
//            String p2typeNames = concat(EffectConfig.TYPE_NAMES[idx - 1]);
//            String p3paramNames = concat(EffectConfig.TYPE_PARAMS[idx - 1]);
//            String p4supportBuff = getBuffSupport(idx);
//            String p5supportSkill = getSkillSupport(idx);
//            String p6javaInterface = interfaceMapping.get(p1type);
//            if(p6javaInterface == null){
//                p6javaInterface = "public String getJavaInterface() throws Exception{\n"+ 
//                    "        throw new Exception(\"此效果不支持buff\");"+
//                    "\n    }\n";
//            }else{
//                p6javaInterface = "public String getJavaInterface(){\n"+
//                "        "+p6javaInterface+";\n    }\n";
//            }
//            String ret = MessageFormat.format(pattern, p0parent, p1type, p2typeNames, p3paramNames, p4supportBuff, p5supportSkill, p6javaInterface);
////            System.out.println(ret);
//            File f = new File("E:/workspace/Game-Editor1.0/src/com/pip/game/data/effects0/Effect_"+p1type+".java");
//            Utils.saveFileContent(f, ret);
        }
        System.out.println("done");
        System.out.println("Total generated : "+fields.size());
    }

    private static String getSkillSupport(int p1type) throws Exception{
        int vi = p1type;
        ArrayList<String> found = new ArrayList<String>();
        if(true){
            throw new Exception("Code changed, need fix");
        }
//        for(int i=0; i<SkillTypeFilteringEffect.skillTypes.length; i++){
//            for(int j=0; j<SkillTypeFilteringEffect.effectsMapping[i].length; j++){
//                if(SkillTypeFilteringEffect.effectsMapping[i][j] == vi){
//                    found.add(SkillTypeFilteringEffect.skillTypes[i]);
//                    break;
//                }
//            }
//        }
        String ret = "";
        for(String str:found){
            ret += str+"|";
        }
        if(ret.length()>0){
            return ret.substring(0, ret.length() - 1);
        }else{
            return "0";
        }
    }
    public static List<Field> getFiledNames() throws Exception{
        Class clz = EffectConfig.class;
        Field[] fields = clz.getFields();
        int modifiers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;
        List<Field> names = new ArrayList<Field>();
        for(Field f:fields){
            if(f.getModifiers() == modifiers && f.getType().getName().equals("int")){
                names.add(f);
            }
        }
        return names;
    }
    private static String getBuffSupport(int p1type) throws Exception {
        int ret = Arrays.binarySearch(BuffFilteringEffect.supportBuffEffects, p1type);
        return ret>=0?Boolean.TRUE.toString():Boolean.FALSE.toString();
    }

    private static String concat(String[] strings) {
        String ret = "";
        int last = strings.length - 1;
        int idx = 0;
        for(String elem:strings){
            ret += "\""+elem+"\"";
            if(idx < last){
                ret += ", ";
            }
            idx ++;
        }
        return ret;
    }

    private static String readTemplate() throws IOException {
        File f = new File("E:/workspace/Game-Editor1.0/src/com/pip/game/data/effects0/_effect.template");
        return Utils.loadFileContent(f);
//        return "abc'{'{0} ee{1}";
    }
}
