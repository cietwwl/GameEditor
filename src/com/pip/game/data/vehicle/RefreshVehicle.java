package com.pip.game.data.vehicle;

import com.pip.game.data.ProjectData;

public class RefreshVehicle extends Vehicle {

    public RefreshVehicle(ProjectData owner) {
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
    * ÊýÁ¿
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
       return super.toString() + " " + count + "¸ö, x" + x + ", y" + y;
   }
}
