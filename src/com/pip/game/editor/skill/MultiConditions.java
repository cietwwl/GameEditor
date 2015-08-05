package com.pip.game.editor.skill;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

import com.pip.game.data.DataObject;
import com.pip.game.data.ProjectData;
import com.pip.game.data.autocoding.BuffGetJavaInterface;
import com.pip.game.data.skill.BuffConfig;
import com.pip.game.data.skill.EffectConfig;
import com.pip.game.data.skill.EffectConfigSet;
import com.pip.game.data.skill.EffectParamRef;
import com.pip.game.data.skill.IBuffConfig;
import com.pip.game.data.skill.SkillConfig;
import com.pip.util.Utils;

public class MultiConditions{
    public String conditionText = "";
    
    public String natrualText = "";
    
    public String toString() {
        return conditionText;
    }
}
