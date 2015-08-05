package com.pip.game.editor.equipment;

import java.awt.Color;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.pip.game.data.ProjectData;
import com.pip.game.data.equipment.AttributeCalculator;
import com.pip.game.data.equipment.EquipmentPrefix;
import com.pip.game.editor.skill.DescriptionPattern;
import com.pip.image.workshop.editor.ImageViewer;
import com.pip.util.SWTUtils;
import com.swtdesigner.ResourceManager;
import com.swtdesigner.SWTResourceManager;

/**
 * ͼ�λ���קʽǰ׺�������Ա����༭����
 * @author lighthu
 */
public class PrefixAttributesEditor extends Canvas implements PaintListener, DisposeListener {
    private EquipmentPrefix prefix;
    private boolean[] lockFlag = new boolean[ProjectData.getActiveProject().config.attrCalc.ATTRIBUTES.length];
    private Rectangle[] barBounds;
    private int[] barWidth;
    private float[] percents;
    private int dragIndex;
    private Point dragStartPoint;
    private int dragStartWidth;
    
    private Image bufferImg;
    private ModifyListener listener;
    
    public PrefixAttributesEditor(Composite parent, int style) {
        super(parent, style | SWT.NO_BACKGROUND);
        addPaintListener(this);
        setBackground(parent.getBackground());

        addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent e) {
                if (dragIndex == -1) {
                    // û����ק���ı����ָ��
                    int anchor = getAnchorAt(e.x, e.y);
                    if (anchor == -1) {
                        setCursor(ImageViewer.cursorArrow);
                    } else {
                        setCursor(ImageViewer.getCursorOfAnchor(1));
                    }
                } else {
                    // ������ק�������±���
                    int neww = dragStartWidth + e.x - dragStartPoint.x;
                    if (neww < 0) {
                        neww = 0;
                    } else if (neww > barBounds[dragIndex].width) {
                        neww = barBounds[dragIndex].width;
                    }
                    if (neww != barWidth[dragIndex]) {
                        prefix.setPriorByPercent(dragIndex, (float)neww / barBounds[dragIndex].width, lockFlag);
                        redraw();
                        fireModified();
                    }
//                    redraw();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent e) {
                if (e.button == 1) {
                    int anchor = getAnchorAt(e.x, e.y);
                    if (anchor != -1 && !lockFlag[anchor]) {
                        dragIndex = anchor;
                        dragStartPoint = new Point(e.x, e.y);
                        dragStartWidth = barWidth[dragIndex];
                    }
                } else if (e.button == 3) {
                    for (int i = 0; i < barBounds.length; i++) {
                        if (barBounds[i].contains(e.x, e.y)) {
                            lockFlag[i] = !lockFlag[i];
                            redraw();
                        }
                    }
                }
            }
            
            public void mouseUp(MouseEvent e) {
                if (e.button == 1) {
                    dragIndex = -1;
//                    redraw();
                }
            }
            
            public void mouseDoubleClick(MouseEvent e) {
                if (e.button == 1) {
                    for (int i = 0; i < barBounds.length; i++) {
                        if (barBounds[i].contains(e.x, e.y)) {
                            onEdit(i);
                        }
                    }
                }
            }
        });
        addDisposeListener(this);
    }
    
    public void setInput(EquipmentPrefix prefix) {
        this.prefix = prefix;
        dragIndex = -1;
        redraw();
    }
    
    private void onEdit(int index) {
        if (lockFlag[index]) {
            return;
        }
        InputDialog dlg = new InputDialog(getShell(), "����", "������ٷֱ�", String.valueOf(percents[index] * 100.0f), new IInputValidator() {
            public String isValid(String newText) {
                try {
                    float f = Float.parseFloat(newText);
                    if (f < 0.0f) {
                        return "����С��0��";
                    } else if (f > 100.0f) {
                        return "���ܴ���100%��";
                    }
                    return null;
                } catch (Exception e) {
                    return "��ʽ���� ";
                }
            }
        });
        if (dlg.open() == Dialog.OK) {
            float newper = Float.parseFloat(dlg.getValue()) / 100.0f;
            prefix.setPriorByPercent(index, newper, lockFlag);
            redraw();
            fireModified();
        }
    }
    
    private int getAnchorAt(int x, int y) {
        for (int i = 0; i < barBounds.length; i++) {
            Rectangle r = barBounds[i];
            int w = barWidth[i];
            Rectangle rect = new Rectangle(r.x + w - 4, r.y, 8, r.height);
            if (rect.contains(x, y)) {
                return i;
            }
        }
        return -1;
    }
    
    public void addModifyListener(ModifyListener l) {
        listener = l;
    }
    
    private void fireModified() {
        if (listener != null) {
            Event e = new Event();
            e.widget = this;
            ModifyEvent event = new ModifyEvent(e);
            listener.modifyText(event);
        }
    }
    
    public void paintControl(PaintEvent e) {
        GC bufferGC = null;
        try {
            Point size = getSize();
            
            if (bufferImg != null && (bufferImg.getBounds().width != size.x || bufferImg.getBounds().height != size.y)) {
                bufferImg.dispose();
                bufferImg = null;
            }
            if (bufferImg == null) {
                bufferImg = new Image(getDisplay(), size.x, size.y);
            }
            bufferGC = new GC(bufferImg);
            bufferGC.setClipping(0, 0, size.x, size.y);
            bufferGC.setBackground(getBackground());
            bufferGC.fillRectangle(0, 0, size.x, size.y);
            
            layoutBars(bufferGC, size);
            paintHeader(bufferGC);
            paintBars(bufferGC);
            paintPercents(bufferGC);
            
            e.gc.drawImage(bufferImg, 0, 0);
        } catch (Throwable e1) {
            e1.printStackTrace();
        } finally {
            if (bufferGC != null) {
                bufferGC.dispose();
            }
        }
    }
    
    private void paintHeader(GC gc) {
        gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
        for (int i = 0; i < ProjectData.getActiveProject().config.attrCalc.ATTRIBUTES.length; i++) {
            String t = ProjectData.getActiveProject().config.attrCalc.ATTRIBUTES[i].shortName;
            int th = gc.textExtent(t).y;
            gc.drawText(t, 2, barBounds[i].y + (barBounds[i].height - th) / 2);
        }
    }
    
    private void paintBars(GC gc) {
        gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        for (int i = 0; i < barBounds.length; i++) {
            gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
            gc.fillRectangle(barBounds[i].x + 1, barBounds[i].y + 1, barBounds[i].width - 1, barBounds[i].height - 1);
            if (lockFlag[i]) {
                SWTUtils.drawRoundRect(gc, barBounds[i], barWidth[i], 0xDADADA, 0xF9F9F9, 0xE6E6E6);
            } else {
                SWTUtils.drawRoundRect(gc, barBounds[i], barWidth[i], 0x9ADFFE, 0xF2F9FE, 0xD6F0FD);
            }
        }
    }
    
    private void paintPercents(GC gc) {
        gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
        for (int i = 0; i < ProjectData.getActiveProject().config.attrCalc.ATTRIBUTES.length; i++) {
            String t = DescriptionPattern.formatPercent(percents[i] * 100.0f);
            int th = gc.textExtent(t).y;
            int tx = barBounds[i].x + (barBounds[i].width - gc.textExtent(t).x) / 2;
            gc.drawText(t, tx, barBounds[i].y + (barBounds[i].height - th) / 2, true);
        }
    }
    
    private void layoutBars(GC gc, Point size) {
        int tw = gc.textExtent("��������").x;
        int bw = size.x - tw - 9;
        float[] pers = prefix.getPercents();
        int bh = ((size.y - 4) / pers.length) - 4;
        barBounds = new Rectangle[pers.length];
        barWidth = new int[pers.length];
        for (int i = 0; i < pers.length; i++) {
            barBounds[i] = new Rectangle(tw + 8, 4 + (bh + 4) * i, bw, bh);
            barWidth[i] = (int)(pers[i] * bw);
        }
        percents = pers;
    }
    
    public void widgetDisposed(DisposeEvent e) {
        if (bufferImg != null) {
            bufferImg.dispose();
        }
    }
}
