package com.pip.game.data.map;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import com.pip.game.editor.area.GameMapViewer;
import com.pip.mango.jni.GLGraphics;
import com.pip.mango.jni.GLUtils;
import com.pip.mapeditor.MapEditor;
import com.pipimage.image.PipImage;

/**
 * 地图上的玩家 用于模拟地图玩家行走，检查碰撞
 **/
public class GameMapPlayer extends GameMapObject {
    /** 玩家图片 */
    protected PipImage image;
    /** 是否显示 */
    public boolean show;

    protected GameMapViewer view;
    /** 天空遮挡 */
    public boolean skyBlock;

    int width;
    int height;

    public GameMapPlayer(GameMapViewer view) {
        this.view = view;
        image = new PipImage();
        try {
            image.load(getClass().getResourceAsStream("/com/pip/game/editor/area/player.pip"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.x = 300;
        this.y = 300;
    }
    /**
     * 绘制玩家
     */
    public void draw(GC gc, int offx, int offy, Rectangle visibleRange, int timer, double ratio) {
        if (!show) {
            return;
        }
        int frame = (timer % 10) / 5;
        Image img = image.getImageDraw(frame).createSWTImage(gc.getDevice(), 0);
        Rectangle imgSize = img.getBounds();
        width = imgSize.width;
        height = imgSize.height;
        gc.drawImage(img, 0, 0, width, height, x + offx, y + offy, (int) (width * ratio), (int) (height * ratio));
    }
    
    /**
     * 绘制玩家
     */
    public void draw(GLGraphics gc, int offx, int offy, Rectangle visibleRange, int timer, double ratio) {
        if (!show) {
            return;
        }
        int frame = (timer % 10) / 5;
        Image img = MapEditor.imageCache.get(image, frame, 0);
        if (img == null) {
            img = image.getImageDraw(frame).createSWTImage(null, 0);
            MapEditor.imageCache.add(image, frame, 0, img);
        }
        Rectangle imgSize = img.getBounds();
        width = imgSize.width;
        height = imgSize.height;
        gc.drawTexture(GLUtils.loadImage(img), 0, 0, x + offx, y + offy, (float) (width * ratio), (float) (height * ratio));
    }

    /**
     * 检查鼠标是否选中该对象
     */
    public GameMapObject detect(int x, int y) {
        if (!show) {
            return null;
        }
        if (x > this.x && x < this.x + width && y > this.y && y < this.y + height) {
            return this;
        }
        return null;
    }

    public void onKeyDown(int keyCode) {
        if (!show) {
            return;
        }
        int step = 4;
        int tx = x;
        int ty = y;
        switch (keyCode) {
            case SWT.ARROW_UP:
                ty -= step;
                break;
            case SWT.ARROW_DOWN:
                ty += step;
                break;
            case SWT.ARROW_LEFT:
                tx -= step;
                break;
            case SWT.ARROW_RIGHT:
                tx += step;
                break;
        }
        if (!view.checkBlock(tx + width / 2, ty + height - height / 6, skyBlock)) {
            x = tx;
            y = ty;
        }
    }
}
