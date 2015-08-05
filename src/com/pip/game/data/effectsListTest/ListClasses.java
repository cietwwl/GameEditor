/**
 * 
 */
package com.pip.game.data.effectsListTest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import com.pip.game.data.effects.DemoEffect;
import com.pip.game.data.effects.EffectAdapter;
import com.pip.game.data.skill.EffectConfig;

/**
 * @author jhkang
 * 
 */
public class ListClasses {

    /**
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static void main(String[] args) throws ClassNotFoundException, IOException, SecurityException,
            NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        List<Class> list = getClasses("com.pip.game.data.effects");
        for (Class clz : list) {
            if (clz.isInterface() || clz.equals(EffectAdapter.class)) {
                continue;
            }
            Method getId = clz.getMethod("getId");
            Object obj = getId.invoke(clz);
            System.out.println(clz + " id " + obj);
        }
        Class<String>[] clzes = new Class[1];
        Class<String> clz = clzes[0];
    }

    private static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = EffectAdapter.class.getClassLoader();        
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String fileName = resource.getFile();
            String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
            dirs.add(new File(fileNameDecoded));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClasses(file, packageName + "." + fileName));
            }
            else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                Class _class;
                try {
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                }
                catch (ExceptionInInitializerError e) {
                    // happen, for example, in classes, which depend on
                    // Spring to inject some beans, and which fail,
                    // if dependency is not fulfilled
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false,
                            Thread.currentThread().getContextClassLoader());
                }
                classes.add(_class);
            }
        }
        return classes;
    }

    public static void embed() {
        try {
            List<Class> toEmbed = new ArrayList<Class>();
            toEmbed.add(DemoEffect.class);
//            List<Class> list = getClasses("com.pip.game.data.effects");
//            for (Class clz : list) {
//                if (clz.isInterface() || clz.equals(EffectAdapter.class)) {
//                    continue;
//                }
//                toEmbed.add(clz);
//            }
            
//            int originLenth = EffectConfig.TYPE_NAMES.length;
//            String[][] typeNames = new String[originLenth+toEmbed.size()][];
//            Class[]   typeClasses = new Class[typeNames.length];
//            String[][] typeParams = new String[typeNames.length][];
//            int idx = originLenth;
//            for(Class clz:toEmbed){
//                Method getTypeNamesMethod = clz.getMethod("getTypeNames");
//                Object obj = getTypeNamesMethod.invoke(clz);
//                typeNames[idx] = (String[]) obj;
//                
//                Method getTypeParamesMethod = clz.getMethod("getTypeParames");
//                Object obj2 = getTypeParamesMethod.invoke(clz);
//                typeParams[idx] = (String[]) obj2;
//                
//                typeClasses[idx] = clz;
//                
//                idx ++;
//            }
//            System.arraycopy(EffectConfig.TYPE_NAMES, 0, typeNames, 0, originLenth);
//            System.arraycopy(EffectConfig.TYPE_CLASSES, 0, typeClasses, 0, originLenth);
//            System.arraycopy(EffectConfig.TYPE_PARAMS, 0, typeParams, 0, originLenth);
//            EffectConfig.TYPE_NAMES = typeNames;
//            EffectConfig.TYPE_CLASSES = typeClasses;
//            EffectConfig.TYPE_PARAMS = typeParams;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
