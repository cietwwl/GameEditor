package com.pip.game.data;

import org.jdom.Element;

/**
 * @author wpjiang
 *  技能刷新出来的npc
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
    * 数量
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
       return super.toString() + " " + count + "个, x" + x + ", y" + y;
   }
}
