package com.pip.game.editor.util;

import java.io.*;
import java.util.*;

import com.pip.util.PropertiesEx;

/**
 * �������á������ļ����û�HOMEĿ¼�µ�sanguoeditor.properties��
 * @author lighthu
 */
public class Settings {
    /**
     * ��ǰ��ĿĿ¼��
     */
	public static File workingDir = new File(".");
	/**
	 * ����Java�ļ�·����
	 */
	public static File exportClassDir = new File(".");
	/**
	 * BUFF��ǰ׺��
	 */
	public static String buffClassPrefix = "AutoGeneratedBuff_";
	/**
	 * BUFF�������
	 */
	public static String buffPackage = "cybertron.core.buff";
	
	/**
     * ������ǰ׺��
     */
    public static String questClassPrefix = "AutoGeneratedQuest_";
    /**
     * �����������
     */
    public static String questPackage = "optimus.quest.auto";
    
    /**
     * AI��ǰ׺��
     */
    public static String aiClassPrefix = "AutoGeneratedAI_";
    /**
     * AI�������
     */
    public static String aiPackage = "optimus.ai.auto";
    
    /**
     * ��Ʒʹ��������ǰ׺��
     */
    public static String itemClassPrefix = "AutoGeneratedItem_";
    /**
     * ��Ʒ�������
     */
    public static String itemPackage = "optimus.item.auto";
	/**
	 * Skill��ǰ׺��
	 */
	public static String skillClassPrefix = "AutoGeneratedSkill_";
	/**
	 * Skill�������
	 */
	public static String skillPackage = "cybertron.core.skill";
	public static String propertiesFileName = "sanguoeditor.properties";
	
	/**
	 * �ͻ��˹���Ŀ¼
	 */
	public static String clientProjDir = "";
	public static String clientProjDir2 = "";
    
	
	public static void loadSetting() {
		String home = System.getProperty("user.home");
		FileInputStream fis = null;
		try {
			PropertiesEx props = new PropertiesEx();
			fis = new FileInputStream(new File(home, propertiesFileName));
			props.load(fis, "GBK");
			workingDir = new File(props.getProperty("working_dir", "."));
			exportClassDir = new File(props.getProperty("export_class_dir", "."));
			buffClassPrefix = props.getProperty("buff_class_prefix", "");
			buffPackage = props.getProperty("buff_package", "");
            skillClassPrefix = props.getProperty("skill_class_prefix", "");
            skillPackage = props.getProperty("skill_package", "");
            questPackage = props.getProperty("quest_package", "");
            aiPackage = props.getProperty("ai_package", "");
            clientProjDir = props.getProperty("client_proj_dir", "");
            clientProjDir2 = props.getProperty("client_proj_dir2", "");
		} catch (IOException e) {
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void saveSetting() {
		String home = System.getProperty("user.home");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(home, propertiesFileName));
			PropertiesEx props = new PropertiesEx();
			props.setProperty("working_dir", workingDir.getAbsolutePath());
            props.setProperty("export_class_dir", exportClassDir.getAbsolutePath());
            props.setProperty("buff_class_prefix", buffClassPrefix);
            props.setProperty("buff_package", buffPackage);
            props.setProperty("skill_class_prefix", skillClassPrefix);
            props.setProperty("skill_package", skillPackage);
            props.setProperty("quest_package", questPackage);
            props.setProperty("ai_package", aiPackage);          
            props.setProperty("client_proj_dir", clientProjDir);
            props.setProperty("client_proj_dir2", clientProjDir2);
			props.save(fos, "GBK");
		} catch (IOException e) {
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}
}