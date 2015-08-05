/**
 * 
 */
package com.pip.game.data.effects0;

/**
 * 效果不支持某些技能的伤害类型,或者不支持某些条件下的Buff,抛出的异常
 * @author jhkang<br/>
 */
public class EffectRejectException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5596435265736095897L;

    /**
     * 
     */
    public EffectRejectException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public EffectRejectException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public EffectRejectException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public EffectRejectException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
