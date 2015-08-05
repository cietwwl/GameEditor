package com.pip.game.data.ant;

import java.io.File;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.pip.game.data.ProjectData;
import com.pip.game.data.i18n.I18NProcessor;
import com.pip.game.data.i18n.LocaleConfig;

public class DateCatalog extends Task{
    protected String source;
    protected String localeID;
    
    @Override
    public void execute() throws BuildException {
        ProjectData proj = new ProjectData();
        ProjectData.setActiveProject(proj);  //light add 
        try {
            proj.load(new File(source));
            List<LocaleConfig> locales=LocaleConfig.getLocales(proj);
            
            LocaleConfig locale = null;
            // ????locale??
            for (int i = 0; i < locales.size(); i ++) {
                if (locales.get(i).id.equals(localeID)) {
                    locale = locales.get(i);
                    break;
                }
            }
            
            // ??locale?????????????
            if (locale == null) {
                throw new BuildException("¦Ä??????locale??????data?????i18n.xml??????id???" + localeID + "????locale??");
            }
            
            I18NProcessor proc=new I18NProcessor(proj,locale);
            proc.process(true,true);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }

    public void setSource(String source){
        this.source = source;
    }
    
    public void setLocaleID(String localeID) {
        this.localeID = localeID;
    }
   }


