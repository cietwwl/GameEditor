package com.pip.game.editor.quest;

import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.pip.game.editor.quest.QuestFlowViewer.ComputeSizeIterator;
import com.pip.game.editor.quest.QuestFlowViewer.PaintIterator;
import com.swtdesigner.SWTResourceManager;

/**
 * ��ϸ�ʽ�ļ��鿴�����
 * @author lighthu
 */
public class RichTextPreviewer extends Composite implements PaintListener {
    // �ı�����
    protected String text = "";
    // ����
    private Image bufferImg;
    
    public RichTextPreviewer(Composite parent, int style) {
        super(parent, SWT.NONE | SWT.NO_BACKGROUND);
        addPaintListener(this);
    }
    
    public void setText(String t) {
        text = t;
        redraw();
    }
    
    /**
     * ���ƿؼ���
     */
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
            try {
                paintContent(bufferGC);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            e.gc.drawImage(bufferImg, 0, 0);
        } catch (Throwable e1) {
            e1.printStackTrace();
        } finally {
            if (bufferGC != null) {
                bufferGC.dispose();
            }
        }
    }

    /**
     * �ڻ����ϻ������ݡ�
     */
    public void paintContent(GC gc) {
        // ���Ʊ���
        Point size = getSize();
        gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        gc.setFont(getFont());
        gc.fillRectangle(0, 0, size.x, size.y);

        // �����ı�
        int x = 4;
        int y = 4;
        List<TextSegment> segs = formatText(gc, text, size.x - 8);
        int lh = gc.getFontMetrics().getHeight() + 1;
        int currentLine = 0;
        int xpos = 0;
        for (TextSegment seg : segs) {
            if (seg.lineNo != currentLine) {
                currentLine++;
                xpos = 0;
            }
            int clr = seg.color;
            gc.setForeground(SWTResourceManager.getColor((clr >> 16) & 0xFF, (clr >> 8) & 0xFF, clr & 0xFF));
            gc.drawText(seg.content, x + xpos, y + lh * currentLine);
            xpos += gc.textExtent(seg.content).x;
        }
    }
    
    /**
     * ��ʽ���ı���
     * @param gc
     * @param text
     * @param maxWidth
     * @return
     */
    private static List<TextSegment> formatText(GC gc, String text, int maxWidth) {
        char[] arr = text.toCharArray();
        int count = arr.length;
        List<TextSegment> ret = new ArrayList<TextSegment>();
        int currentLine = 0;
        int xpos = 0;
        List<String> tagStack = new ArrayList<String>();
        List<Integer> colorStack = new ArrayList<Integer>();
        int color = SWT.COLOR_BLACK;
        int state = 0;      // 0 - ��ͨ�ı���1 - �ڱ���С�2 - �ڱ�������
        StringBuffer buf = new StringBuffer();
        
        for (int i = 0; i < count; i++) {
            char ch = arr[i];
            if (ch == '\r') {
                // ȫ�ֺ���\r��ֻ����\n
                continue;
            }
            if (state == 0) {
                if (ch == '\n') {
                    // ���У�������ǰ��
                    if (buf.length() > 0) {
                        ret.add(new TextSegment(currentLine, color, buf.toString()));
                        buf.setLength(0);
                    }
                    currentLine++;
                    xpos = 0;
                } else if (ch == '<') {
                    // �����ǣ�������ǰ��
                    if (buf.length() > 0) {
                        ret.add(new TextSegment(currentLine, color, buf.toString()));
                        buf.setLength(0);
                    }
                    state = 1;
                } else if (ch == '$' && i < count - 1 && arr[i + 1] == '{') {
                    // ���������
                    state = 2;
                    i++;
                } else {
                    // ��ͨ�ı��������ȣ���������������
                    int tw = gc.textExtent("" + ch).x;
                    if (tw + xpos > maxWidth) {
                        if (buf.length() > 0) {
                            ret.add(new TextSegment(currentLine, color, buf.toString()));
                            buf.setLength(0);
                        }
                        currentLine++;
                        xpos = tw;
                    } else {
                        xpos += tw;
                    }
                    buf.append(ch);
                }
            } else if (state == 1) {
                if (ch == '>') {
                    state = 0;
                    String tagName = buf.toString();
                    if (tagName.length() == 0) {
                        continue;
                    }
                    buf.setLength(0);
                    if (!tagName.startsWith("/")) {
                        // ������ʼ���
                        if (tagName.startsWith("font")) {
                            tagStack.add("font");
                            colorStack.add(color);
                            int i1 = tagName.indexOf("color=\"");
                            i1 += "color=\"".length();
                            int i2 = tagName.indexOf('"', i1);
                            color = Integer.parseInt(tagName.substring(i1, i2), 16);
                        } else if (tagName.startsWith("n")) {
                            tagStack.add(tagName);
                            colorStack.add(color);
                            color = 0xFF0000;
                            
                            // <n>123,���(���:12,12)</n>�������ҵ���һ��,ǰ�����ݺ���
                            while (i < count && ch != ',') {
                                i++;
                                ch = arr[i];
                            }
                        } else if (tagName.startsWith("l")) {
                            tagStack.add(tagName);
                            colorStack.add(color);
                            color = 0xFF0000;
                            
                            // <l>123,���:12,12</l>�������ҵ���һ��,ǰ�����ݺ���
                            while (i < count && ch != ',') {
                                i++;
                                ch = arr[i];
                            }
                        }
                    } else {
                        // ����������
                        tagName = tagName.substring(1);
                        int tss = tagStack.size();
                        if (tss == 0) {
                            continue;
                        }
                        String oldTag = tagStack.get(tss - 1);
                        if (!oldTag.equals(tagName)) {
                            continue;
                        }
                        color = colorStack.remove(tss - 1);
                        tagStack.remove(tss - 1);
                    }
                } else {
                    buf.append(ch);
                }
            } else if (state == 2) {
                if (ch == '}') {
                    // ������ʾΪ???
                    int tw = gc.textExtent("???").x;
                    if (tw + xpos > maxWidth) {
                        if (buf.length() > 0) {
                            ret.add(new TextSegment(currentLine, color, buf.toString()));
                            buf.setLength(0);
                        }
                        currentLine++;
                        xpos = tw;
                    } else {
                        xpos += tw;
                    }
                    buf.append("???");
                    state = 0;
                }
            }
        }
        if (state == 0 && buf.length() > 0) {
            ret.add(new TextSegment(currentLine, color, buf.toString()));
            buf.setLength(0);
        }
        return ret;
    }
    
    // ��ʽ������ı���
    private static class TextSegment {
        public int lineNo;          // �к�
        public int color;           // ��ɫ
        public String content;      // �ı�����
        
        public TextSegment() {}
        
        public TextSegment(int l, int c, String con) {
            lineNo = l;
            color = c;
            content = con;
        }
    }
}
