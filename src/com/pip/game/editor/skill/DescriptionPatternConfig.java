package com.pip.game.editor.skill;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.data.quest.pqe.PQEUtils.SystemFunc;
import com.pip.game.data.quest.pqe.PQEUtils.SystemVar;
import com.pip.util.Utils;

public class DescriptionPatternConfig {
    public Map<String, String> varToBuffCodeMap = new HashMap<String, String>();
    public Map<String, String> varToSkillCodeMap = new HashMap<String, String>();
    
    public DescriptionPatternConfig(File baseDir) {
        try {
            Document doc = Utils.loadDOM(new File(baseDir, "desc_conf.xml"));
            List list = doc.getRootElement().getChildren("variable");
            for (int i = 0; i < list.size(); i++) {
                Element elem = (Element)list.get(i);
                String varName = elem.getAttributeValue("name");
                String buffCode = elem.getAttributeValue("buffcode");
                String skillCode = elem.getAttributeValue("skillcode");
                varToBuffCodeMap.put(varName, buffCode);
                varToSkillCodeMap.put(varName, skillCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
