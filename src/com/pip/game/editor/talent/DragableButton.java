package com.pip.game.editor.talent;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.pip.game.editor.EditorApplication;
import com.pipimage.image.PipImage;

public class DragableButton extends Composite {

    public static final String KEY_FOCUS_DATA = "focusBtn";
    public static final String KEY_PARENT = "parent";
    public static final String KEY_PARENT_LEVEL = "parentLevel";
    public static final String KEY_ID = "ID";
    public static final String KEY_NEED_POINTS = "needPoints";
    public static final String KEY_IS_FINAL_SKILL = "isFinalSkill";
    

    protected static int[] xs = new int[6];
    protected static int[] ys = new int[8];
    static {
        for (int i = 0; i < xs.length; i++) {
            xs[i] = 170 + 70 * i;
        }
        for (int i = 0; i < ys.length; i++) {
            ys[i] = 50 + 50 * i;
        }
    }
    protected Button button;

    public DragableButton(Composite parent, int style, String name) {
        super(parent, style);
        this.setData(new ArrayList<DragableButton>());// children list
        makeDragable(this, name);
        this.moveAbove(null);
    }

    protected void makeDragable(final Composite composite, String name) {
        composite.setEnabled(false);
        composite.setLayout(new FillLayout());
        button = new Button(composite, SWT.PUSH);
        button.setText(name);
        composite.pack();
        composite.setLocation(50, 10);
        final Point[] offset = new Point[1];
        Listener listener = new Listener() {
            public void handleEvent(Event event) {
                if (!event.doit || composite.isDisposed()) {
                    return;
                }
                switch (event.type) {
                    case SWT.MouseDown:
                        Rectangle rect = composite.getBounds();
                        if (rect.contains(event.x, event.y)) {
                            Point pt1 = composite.toDisplay(0, 0);
                            Point pt2 = composite.getParent().toDisplay(event.x, event.y);
                            offset[0] = new Point(pt2.x - pt1.x, pt2.y - pt1.y);
                            event.doit = false;
                            composite.moveAbove(null);
                            composite.getParent().setData(KEY_FOCUS_DATA, composite);
                            Event focusEvt = new Event();
                            composite.getParent().notifyListeners(TalentTreeOperator.EVENT_ID_FOCUS_CHANGED, focusEvt);
                            composite.getParent().redraw();
                        }
                        break;
                    case SWT.MouseMove:
                        if (offset[0] != null) {
                            Point pt = offset[0];
                            if (event.x - pt.x < 0 || event.y - pt.y < 0) {
                                return;
                            }
                            rect = composite.getBounds();
                            Rectangle pRect = composite.getParent().getClientArea();
                            if (event.x - pt.x + rect.width > pRect.width
                                    || event.y - pt.y + rect.height > pRect.height) {
                                return;
                            }
                            composite.setLocation(event.x - pt.x, event.y - pt.y);
                            composite.getParent().redraw();
                            composite.getParent().notifyListeners(TalentTreeOperator.EVENT_ID_MODIFIED, null);
                        }
                        break;
                    case SWT.MouseUp:
                        int px = 0;
                        int py = 0;
                        if (composite instanceof DragableButton) {
                            DragableButton dBtn = (DragableButton) composite;
                            DragableButton parentBtn = (DragableButton) dBtn.getData(KEY_PARENT);
                            if (parentBtn != null) {
                                Point p = parentBtn.getLocation();
                                px = p.x;
                                py = p.y;
                                Rectangle rec = composite.getBounds();
                                if (rec.contains(event.x, event.y)) {
                                    // 右边
                                    if (event.x > px && event.y < py + event.x - px && event.y > py - event.x + px) {
                                        int x=0, y=0;
                                        for (int i = 0; i < xs.length - 1; i++) {
                                            if (event.x < xs[i + 1] && event.x > xs[i]) {
                                                x = xs[i] + 15;
                                            }
                                        }
                                        for (int i = 0; i < ys.length - 1; i++) {
                                            if (py < ys[i + 1] && py > ys[i]) {
                                                y = ys[i] + 15;
                                            }
                                        }
                                        // composite.setLocation(event.x, py);
                                        composite.setLocation(x, y);
                                        composite.getParent().redraw();
                                        composite.getParent().notifyListeners(TalentTreeOperator.EVENT_ID_MODIFIED,
                                                null);
                                    }// 左边
                                    else if (event.x < px && event.y < py + px - event.x && event.y > py - px + event.x) {
                                        int x=0, y=0;
                                        for (int i = 0; i < xs.length - 1; i++) {
                                            if (event.x < xs[i + 1] && event.x > xs[i]) {
                                                x = xs[i] + 15;
                                            }
                                        }
                                        for (int i = 0; i < ys.length - 1; i++) {
                                            if (py < ys[i + 1] && py > ys[i]) {
                                                y = ys[i] + 15;
                                            }
                                        }
//                                        composite.setLocation(event.x, py);
                                        composite.setLocation(x, y);
                                        composite.getParent().redraw();
                                        composite.getParent().notifyListeners(TalentTreeOperator.EVENT_ID_MODIFIED,
                                                null);
                                    }// 下边
                                    else if (event.y > py && event.x < px + event.y - py && event.x > px - event.y + py) {
                                        int x=0, y=0;
                                        for (int i = 0; i < xs.length - 1; i++) {
                                            if (px < xs[i + 1] && px > xs[i]) {
                                                x = xs[i] + 15;
                                            }
                                        }
                                        for (int i = 0; i < ys.length - 1; i++) {
                                            if (event.y < ys[i + 1] && event.y > ys[i]) {
                                                y = ys[i] + 15;
                                            }
                                        }
//                                        composite.setLocation(px, event.y);
                                        composite.setLocation(x, y);
                                        composite.getParent().redraw();
                                        composite.getParent().notifyListeners(TalentTreeOperator.EVENT_ID_MODIFIED,
                                                null);
                                    }// 上边，不允许，直接给扔到下边对应位置
                                    else if (event.y < py && event.x < px + py - event.y && event.x > px - py + event.y) {
                                        int x=0, y=0;
                                        for (int i = 0; i < xs.length - 1; i++) {
                                            if (px < xs[i + 1] && px > xs[i]) {
                                                x = xs[i] + 15;
                                            }
                                        }
                                        for (int i = 0; i < ys.length - 1; i++) {
                                            if (py + py - event.y < ys[i + 1] && py + py - event.y > ys[i]) {
                                                y = ys[i] + 15;
                                            }
                                        }
//                                        composite.setLocation(px, py + py - event.y);
                                        composite.setLocation(x, y);
                                        composite.getParent().redraw();
                                        composite.getParent().notifyListeners(TalentTreeOperator.EVENT_ID_MODIFIED,
                                                null);
                                    }
                                }
                            }else{
                                Rectangle rec = composite.getBounds();
                                if (rec.contains(event.x, event.y)) {
                                int x=0,y=0;
                                for (int i = 0; i < xs.length - 1; i++) {
                                    if (event.x < xs[i + 1] && event.x > xs[i]) {
                                        x = xs[i] + 15;
                                    }
                                }
                                for (int i = 0; i < ys.length - 1; i++) {
                                    if (event.y < ys[i + 1] && event.y > ys[i]) {
                                        y = ys[i] + 15;
                                    }
                                }
//                                composite.setLocation(px, py + py - event.y);
                                composite.setLocation(x, y);
                                composite.getParent().redraw();
                                composite.getParent().notifyListeners(TalentTreeOperator.EVENT_ID_MODIFIED,
                                        null);
                                }
                            }
                        }
                        offset[0] = null;
                        break;
                }
            }
        };
        composite.getParent().addListener(SWT.MouseDown, listener);
        composite.getParent().addListener(SWT.MouseUp, listener);
        composite.getParent().addListener(SWT.MouseMove, listener);
    }

    public String getText() {
        return button.getText();
    }

    public void setImage(Image img) {
        button.setImage(img);
    }

}
