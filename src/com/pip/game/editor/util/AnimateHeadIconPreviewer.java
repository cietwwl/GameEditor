package com.pip.game.editor.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.pip.game.data.Animation;
import com.pip.game.data.AnimationFormat;
import com.pip.game.editor.AnimationEditor;
import com.pip.image.workshop.editor.AnimateViewer;
import com.pip.mapeditor.SetupHeadIconAreaDialog;
import com.pip.mapeditor.data.NPCImageInfo;
import com.pipimage.image.PipAnimateSet;

public class AnimateHeadIconPreviewer extends Composite{
    private AnimationEditor owner;
    private int format;
    private AnimateViewer animateViewer;
    private PipAnimateSet currentAnimate;

    public AnimateHeadIconPreviewer(Composite parent, AnimationEditor owner, int format) {
        super(parent, SWT.NONE);
        this.owner = owner;
        this.format = format;
        
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        setLayout(gridLayout);
        
        animateViewer = new AnimateViewer(this, SWT.NONE);
        animateViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        Button button = new Button(this, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        button.setText("编辑头像...");
        button.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e) {
                showEditeHeadIconDialog();
            } 
        });
    }
    
    /*
     * 重新设置预览文件。
     */
    public void setupFile() {
        Animation aniObj = (Animation)owner.getEditObject();
        File f = aniObj.getAnimateFile(format);
        if (f == null) {
            animateViewer.setInput(null);
        } else {
            // 载入动画
            try {
                currentAnimate = new PipAnimateSet();
                currentAnimate.load(f);
                animateViewer.setInput(currentAnimate.getAnimate(0));
                if (animateViewer.isPlaying()) {
                    animateViewer.stop();
                }
                animateViewer.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /*
     * 重新设置剪裁区域
     */
    public void setupHeadArea() {
        Animation aniObj = (Animation)owner.getEditObject();
        AnimationFormat aniFormat = aniObj.owner.config.animationFormats.get(format);
        Rectangle rect = new Rectangle((int)(aniObj.headAreaX * aniFormat.scale),
                (int)(aniObj.headAreaY * aniFormat.scale), aniFormat.headWidth, aniFormat.headHeight);
        animateViewer.setVisibleArea(rect);
    }

    protected void showEditeHeadIconDialog() {
        Animation aniObj = (Animation)owner.getEditObject();
        AnimationFormat aniFormat = aniObj.owner.config.animationFormats.get(format);
        NPCImageInfo info = new NPCImageInfo();
        info.cx = new int[] { (int)(aniObj.headAreaX * aniFormat.scale) };
        info.cy = new int[] { (int)(aniObj.headAreaY * aniFormat.scale) };
        info.cw = new int[] { aniFormat.headWidth };
        info.ch = new int[] { aniFormat.headHeight };
        SetupHeadIconAreaDialog dlg = new SetupHeadIconAreaDialog(this.getShell(), currentAnimate.getAnimate(0), info);
        int ret = dlg.open();
        if (ret == SetupHeadIconAreaDialog.OK) {
            Rectangle[] areas = dlg.getSelectedArea();
            if (areas != null) {
                if (areas[0].x > Byte.MAX_VALUE || areas[0].y > Byte.MAX_VALUE || areas[0].x < Byte.MIN_VALUE || areas[0].y < Byte.MIN_VALUE) {
                    MessageDialog.openError(getShell(), "错误", "数值越界:" + areas[0]);
                    return;
                }
                aniObj.headAreaX = (short)(areas[0].x / aniFormat.scale);
                aniObj.headAreaY = (short)(areas[0].y / aniFormat.scale);
                owner.setDirty(true);
                owner.headAreaChanged();
            }
        }
    }
} 
