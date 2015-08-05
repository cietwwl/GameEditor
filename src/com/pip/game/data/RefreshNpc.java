package com.pip.game.data;

import org.jdom.Element;

/**
 * @author wpjiang
 *  ����ˢ�³�����npc
 */
public class RefreshNpc extends NPCTemplate{
   public RefreshNpc(ProjectData owner) {
        super(owner);
    }

   public int getCount() {
    return count;
}


public void setCount(int count) {
    this.count = count;
}


public int getX() {
    return x;
}


public void setX(int x) {
    this.x = x;
}


public int getY() {
    return y;
}


public void setY(int y) {
    this.y = y;
}

/**
    * ����
     */
   private int count = 1;
   /**
    * x
     */
   private int x = 1;
   /**
    * y
     */
   private int y = 1;
   
   
   public String toString() {
       return super.toString() + " " + count + "��, x" + x + ", y" + y;
   }
}
