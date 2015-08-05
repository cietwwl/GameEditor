package com.pip.game.editor.skill;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Document;
import org.jdom.Element;

import com.pip.game.editor.util.ExtProp;
import com.pip.game.editor.util.ExtPropManager;
import com.pip.util.Utils;

public class BuffExtPropManager extends ExtPropManager{

    public static HashMap<String, ExtProp> propTemplates = new LinkedHashMap<String, ExtProp>();
    private static boolean confLoaded = false;
    
    /**
     * 
     */
    public BuffExtPropManager() {
        if(!confLoaded){
            setup("Skill/buffExtPropConf.xml", propTemplates);
            confLoaded = true;
        }
    }
    public static void main(String[] args) throws Exception{
        final ExtPropManager extPropMngr = new SkillExtPropManager();
        
        final File data = new File("E:/workspace/Game1.0-Data/data/Skill/devSkillProp.xml");
        Element rootEl = Utils.loadDOM(data).getRootElement();
        
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        
        extPropMngr.loadExtData(rootEl);
        extPropMngr.createPartControl(shell);
        
        shell.setSize(600, 300);
        shell.open();
        
        shell.addDisposeListener(new DisposeListener(){

            public void widgetDisposed(DisposeEvent e) {
                
                //save
                Document doc = new Document(new Element("root"));
                extPropMngr.saveToDom(doc.getRootElement());
                try {
                    Utils.saveDOM(doc, data);
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
                
            }
            
        });
        
        while (!shell.isDisposed()) {
          if (!display.readAndDispatch())
            display.sleep();
        }

        display.dispose();
    }
    
    @Override
    protected Map<String, ExtProp> getTemplates() {
        return propTemplates;
    }

}
