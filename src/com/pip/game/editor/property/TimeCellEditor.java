package com.pip.game.editor.property;



import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TimeCellEditor extends CellEditor{
    private Composite container;
    private DateTime time;
    private DateTime date;
    public TimeCellEditor(Composite parent){
        super(parent);
    }
    @Override
    protected Control createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout gd = new GridLayout(2, false);
        gd.horizontalSpacing = gd.verticalSpacing = 0;
        gd.marginWidth = gd.marginHeight = 0;
        container.setLayout(gd);
         date = new DateTime(container, SWT.DATE );
         time= new DateTime(container, SWT.TIME | SWT.SHORT);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        return container;
    }

    @Override
    protected Object doGetValue() {
        if(date == null){
            return "null date";
        }
        return String.format("%d/%2d/%2d/%2d/%2d", date.getYear(), date.getMonth()+1, date.getDay(),time.getHours(),time.getMinutes());
    }

    @Override
    protected void doSetFocus() {

        
    }

    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(date != null && (value instanceof String));
        String[] dateTime=new String[5];
        dateTime=(((String)value).split("/"));
        date.setYear(Integer.parseInt((dateTime[0]).trim()));
        date.setMonth((Integer.parseInt((dateTime[1]).trim()))-1);
        date.setDay(Integer.parseInt((dateTime[2]).trim()));
        time.setHours(Integer.parseInt((dateTime[3]).trim()));
        time.setMinutes(Integer.parseInt((dateTime[4]).trim()));
        
        
    }
    
  
}
