package com.pip.game.editor.property;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ChooseShapeDialog extends Dialog implements VerifyListener, SelectionListener  {
    private Text txtParam1;
    private Text txtParam0;
    private Combo comboType;
    private ShapeData area = new ShapeData();
    private Label lblParam0;
    private Label lblType;
    private Label lblParam1;
	private Button btnAnchorType;
    public ShapeData getArea() {
        return area;
    }
    public void setArea(ShapeData area) {
        this.area = area;
    }
    protected ChooseShapeDialog(Shell parentShell) {
        super(parentShell);
    }
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentContainer = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        parentContainer.setLayout(gridLayout);
        
        Composite container = new Composite(parentContainer, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        container.setLayout(gridLayout);

        lblType = new Label(container, SWT.NONE);
        lblType.setText("类型:");

        comboType = new Combo(container, SWT.NONE);
        final GridData gd_comboType = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboType.setLayoutData(gd_comboType);
        comboType.setItems(new String[]{"圆形","矩形","扇形","正前方矩形"});
        comboType.addSelectionListener(this);

        lblParam0 = new Label(container, SWT.NONE);
        final GridData gd_lblParam0 = new GridData();
        lblParam0.setLayoutData(gd_lblParam0);
        lblParam0.setText("半径:");

        txtParam0 = new Text(container, SWT.BORDER);
        final GridData gd_txtParam0 = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gd_txtParam0.widthHint = 40;
        txtParam0.setLayoutData(gd_txtParam0);
        txtParam0.addVerifyListener(this);
        
        lblParam1 = new Label(container, SWT.NONE);
        final GridData gd_lblParam1 = new GridData();
        lblParam1.setLayoutData(gd_lblParam1);
        lblParam1.setText("角度:");

        txtParam1 = new Text(container, SWT.BORDER);
        txtParam1.addVerifyListener(this);
        
        final GridData gd_txtParam1 = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gd_txtParam1.widthHint = 40;
        txtParam1.setLayoutData(gd_txtParam1);

        btnAnchorType = new Button(container, SWT.CHECK);
        btnAnchorType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
        btnAnchorType.setText("以自己为锚点");
        btnAnchorType.addSelectionListener(this);
        
        setCurrentData();
        return container;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "确定", true);
        createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
    }
    
	public void setCurrentData(){
        if (area == null){
        	area = new ShapeData();
        }
        comboType.select(area.areaType);
        switch (area.areaType) {
            case ShapeData.AREA_TYPE_ROUND:
                lblParam0.setText("半径:");
                lblParam1.setVisible(false);
                txtParam1.setVisible(false);
                txtParam0.setText(String.valueOf(area.values[0]));
                break;
            case ShapeData.AREA_TYPE_RECT:
            case ShapeData.AREA_TYPE_FRONT_RECT:
                lblParam0.setText("长:");
                lblParam1.setText("宽:");
                txtParam0.setText(String.valueOf(area.values[0]));
                txtParam1.setText(String.valueOf(area.values[1]));
                lblParam1.setVisible(true);
                txtParam1.setVisible(true);
                break;
            case ShapeData.AREA_TYPE_SECTOR:
                lblParam0.setText("半径:");
                lblParam1.setText("角度:");                
                txtParam0.setText(String.valueOf(area.values[0]));
                txtParam1.setText(String.valueOf(area.values[1]));
                lblParam1.setVisible(true);
                txtParam1.setVisible(true);
                break;
        }
        
        btnAnchorType.setSelection(area.anchorType == 1);
    }
    
    public void saveData(){
        area.areaType = (byte)comboType.getSelectionIndex();
        area.values = new int[2];
        if(txtParam0.getText().length()>0){
            area.values[0] = Integer.parseInt(txtParam0.getText());
        }
        if(txtParam1.getText().length()>0){
        	
            if(area.areaType == area.AREA_TYPE_ROUND){
                area.values[1] = 360;               
            }else{
                area.values[1] = Integer.parseInt(txtParam1.getText());                            
            }
        }
        area.anchorType = btnAnchorType.getSelection()? (byte)1: (byte)0;
    }
    
    @Override
    protected Point getInitialSize() {
        return new Point(320, 169);
    }
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("选择区域");
    }
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            saveData();
        }
        super.buttonPressed(buttonId);
    }
    public void verifyText(VerifyEvent e) {
        if(e.getSource() == txtParam1){
            String inStr = e.text;
            try {
                int v =  Integer.parseInt(inStr);
                if(area.areaType == ShapeData.AREA_TYPE_ROUND){
                    e.doit =( v>=0);
                }else if (area.areaType == ShapeData.AREA_TYPE_SECTOR){
                    e.doit =( v>= 0 && v<= 360);
                }else if (area.areaType == ShapeData.AREA_TYPE_RECT){
                    e.doit =( v>=0);
                }else if (area.areaType == ShapeData.AREA_TYPE_FRONT_RECT){
                    e.doit =( v>=0);
                }else{
                    e.doit = false;
                }
            }catch(Exception ex) {
                e.doit = false;
            }
        }
        else if (e.getSource() == txtParam0) {
            String inStr = e.text;
            try {
                int v = Integer.parseInt(inStr);
                e.doit = true;
            }catch (Exception ex) {
                e.doit = false;
            }
        }
        
    }
	public void widgetSelected(SelectionEvent e) {
		if(e.getSource() == comboType){
			  switch (comboType.getSelectionIndex()) {
	            case ShapeData.AREA_TYPE_ROUND:
	                lblParam0.setText("半径:");
	                lblParam1.setVisible(false);
	                txtParam1.setVisible(false);
	                break;
	            case ShapeData.AREA_TYPE_RECT:
	            case ShapeData.AREA_TYPE_FRONT_RECT:
	                lblParam0.setText("长:");
	                lblParam1.setText("宽:");
	                lblParam1.setVisible(true);
	                txtParam1.setVisible(true);
	                break;
	            case ShapeData.AREA_TYPE_SECTOR:
	                lblParam0.setText("半径:");
	                lblParam1.setText("角度:");                
	                lblParam1.setVisible(true);
	                txtParam1.setVisible(true);
	                break;
	        }
		}
	}
	public void widgetDefaultSelected(SelectionEvent e) {
		
	}
}
