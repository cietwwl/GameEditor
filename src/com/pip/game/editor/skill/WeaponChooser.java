package com.pip.game.editor.skill;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.pip.game.data.skill.BuffConfig;

/**
 * ѡ���������͵Ŀؼ�����һ��int���������ݡ����ȫ��������ѡ�У�����int[0]��
 * @author lighthu
 */
public class WeaponChooser extends Composite {
    private ModifyListener listener = null;
    public Button getButtonSword() {
        return buttonSword;
    }

    public void setButtonSword(Button buttonSword) {
        this.buttonSword = buttonSword;
    }

    public Button getButtonKnife() {
        return buttonKnife;
    }

    public void setButtonKnife(Button buttonKnife) {
        this.buttonKnife = buttonKnife;
    }

    public Button getButtonAxe() {
        return buttonAxe;
    }

    public void setButtonAxe(Button buttonAxe) {
        this.buttonAxe = buttonAxe;
    }

    public Button getButtonSpear() {
        return buttonSpear;
    }

    public void setButtonSpear(Button buttonSpear) {
        this.buttonSpear = buttonSpear;
    }

    public Button getButtonPolearm() {
        return buttonPolearm;
    }

    public void setButtonPolearm(Button buttonPolearm) {
        this.buttonPolearm = buttonPolearm;
    }

    public Button getButtonFan() {
        return buttonFan;
    }

    public void setButtonFan(Button buttonFan) {
        this.buttonFan = buttonFan;
    }

    public Button getButtonBow() {
        return buttonBow;
    }

    public void setButtonBow(Button buttonBow) {
        this.buttonBow = buttonBow;
    }

    private Button buttonSword;
    private Button buttonKnife;
    private Button buttonAxe;
    private Button buttonSpear;
    private Button buttonPolearm;
    private Button buttonFan;
    private Button buttonBow;
    
    private EventHandler eh = new EventHandler();
    private class EventHandler extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            fireModified();
        }
    }
    
    /**
     * Create the composite
     * @param parent
     * @param style
     */
    public WeaponChooser(Composite parent, int style) {
        super(parent, SWT.BORDER_SOLID);
        this.addPaintListener(new PaintListener(){

            public void paintControl(PaintEvent e) {
                paintBorder(e.gc);
            }
        });
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 8;
        setLayout(gridLayout);
        
        Label label = new Label(this, SWT.NONE);
        label.setText("��������");
        

        buttonSword = new Button(this, SWT.CHECK);
        buttonSword.addSelectionListener(eh);
        buttonSword.setText("��");

        buttonKnife = new Button(this, SWT.CHECK);
        buttonKnife.addSelectionListener(eh);
        buttonKnife.setText("��");

        buttonAxe = new Button(this, SWT.CHECK);
        buttonAxe.addSelectionListener(eh);
        buttonAxe.setText("��");

        buttonSpear = new Button(this, SWT.CHECK);
        buttonSpear.addSelectionListener(eh);
        buttonSpear.setText("ǹ");

        buttonPolearm = new Button(this, SWT.CHECK);
        buttonPolearm.addSelectionListener(eh);
        buttonPolearm.setText("����");

        buttonFan = new Button(this, SWT.CHECK);
        buttonFan.addSelectionListener(eh);
        buttonFan.setText("��");

        buttonBow = new Button(this, SWT.CHECK);
        buttonBow.addSelectionListener(eh);
        buttonBow.setText("��");
    }
    
    protected void paintBorder(GC gc) {
        gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
        Rectangle rect = this.getBounds();
        gc.drawRoundRectangle(0,0, rect.width - 1, rect.height - 1, 10,10);
    }

    @Override
    protected void checkSubclass () {
    }
    
    public void addModifyListener(ModifyListener l) {
        this.listener = l;
    }
    
    private void fireModified() {
        if (listener != null) {
            Event e = new Event();
            e.widget = this;
            ModifyEvent event = new ModifyEvent(e);
            listener.modifyText(event);
        }
    }
    
    public void setWeapons(int[] weapon) {
        if (weapon.length == 0) {
            buttonSword.setSelection(true);
            buttonKnife.setSelection(true);
            buttonAxe.setSelection(true);
            buttonSpear.setSelection(true);
            buttonPolearm.setSelection(true);
            buttonFan.setSelection(true);
            buttonBow.setSelection(true);
        } else {
            buttonSword.setSelection(false);
            buttonKnife.setSelection(false);
            buttonAxe.setSelection(false);
            buttonSpear.setSelection(false);
            buttonPolearm.setSelection(false);
            buttonFan.setSelection(false);
            buttonBow.setSelection(false);
            for (int t : weapon) {
                switch (t) {
                case BuffConfig.MINORTYPE_SWORD:
                    buttonSword.setSelection(true);
                    break;
                case BuffConfig.MINORTYPE_KNIFE:
                    buttonKnife.setSelection(true);
                    break;
                case BuffConfig.MINORTYPE_AXE:
                    buttonAxe.setSelection(true);
                    break;
                case BuffConfig.MINORTYPE_SPEAR:
                    buttonSpear.setSelection(true);
                    break;
                case BuffConfig.MINORTYPE_POLEARM:
                    buttonPolearm.setSelection(true);
                    break;
                case BuffConfig.MINORTYPE_FAN:
                    buttonFan.setSelection(true);
                    break;
                case BuffConfig.MINORTYPE_BOW:
                    buttonBow.setSelection(true);
                    break;
                }
            }
        }
    }
    
    public int[] getWeapons() {
        List<Integer> wps = new ArrayList<Integer>();
        if (buttonSword.getSelection()) {
            wps.add(BuffConfig.MINORTYPE_SWORD);
        }
        if (buttonKnife.getSelection()) {
            wps.add(BuffConfig.MINORTYPE_KNIFE);
        }
        if (buttonAxe.getSelection()) {
            wps.add(BuffConfig.MINORTYPE_AXE);
        }
        if (buttonSpear.getSelection()) {
            wps.add(BuffConfig.MINORTYPE_SPEAR);
        }
        if (buttonPolearm.getSelection()) {
            wps.add(BuffConfig.MINORTYPE_POLEARM);
        }
        if (buttonFan.getSelection()) {
            wps.add(BuffConfig.MINORTYPE_FAN);
        }
        if (buttonBow.getSelection()) {
            wps.add(BuffConfig.MINORTYPE_BOW);
        }
        if (wps.size() == 7) {
            return new int[0];
        } else {
            int[] ret = new int[wps.size()];
            for (int i = 0; i < wps.size(); i++) {
                ret[i] = wps.get(i);
            }
            return ret;
        }
    }
}
