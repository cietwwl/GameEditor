package com.pip.sanguo.data.quest.pqe;

import java.util.HashMap;

public class PQEUtils {
	/**
	 * 所有比较操作符。
	 */
	public static int[] COMPARE_OPS = new int[] {
		ParserConstants.EQ, ParserConstants.NE, ParserConstants.LT, 
		ParserConstants.LE, ParserConstants.GT, ParserConstants.GE
	};

	/**
	 * 所有系统变量的数组。
	 */
	public static String[] SYSTEM_FUNCS = new String[] {
		"_LEVEL", "_MONEY","_HPPercent","_MPPercent", "_HP", "_MAXHP", "_MP", "_MAXMP", "_STR", "_STA", "_AGI",
		"_INT", "_SEX", "_SEXNAME", "_CLASS", "_CLASSNAME", "_NAME", "_MAPID", "_X",
		"_Y", "_LASTKILLERNPC", "_LASTKILLERNPCNAME", "_LASTKILLERPLAYER",
		"_LASTKILLERPLAYERNAME", "_LASTCHATMESSAGE", "_FINISHCHOICE"
	};
	
    /**
     * 所有系统变量的自然语言表述。
     */
    public static HashMap<String, String> varDesc = new HashMap<String, String>();
    static {
    	varDesc.put("_LEVEL", "级别");
    	varDesc.put("_MONEY", "金钱");
    	varDesc.put("_HPPercent", "生命%");
    	varDesc.put("_MPPercent", "内力%");
    	varDesc.put("_HP", "生命");
    	varDesc.put("_MAXHP", "最大生命");
    	varDesc.put("_MP", "内力");
    	varDesc.put("_MAXMP", "最大内力");
    	varDesc.put("_STR", "力量");
    	varDesc.put("_STA", "耐力");
    	varDesc.put("_AGI", "敏捷");
    	varDesc.put("_INT", "智力");
    	varDesc.put("_SEX", "性别ID");
    	varDesc.put("_SEXNAME", "性别");
    	varDesc.put("_CLASS", "职业ID");
    	varDesc.put("_CLASSNAME", "职业");
    	varDesc.put("_NAME", "名称");
    	varDesc.put("_MAPID", "场景ID");
    	varDesc.put("_X", "X坐标");
    	varDesc.put("_Y", "Y坐标");
    	varDesc.put("_LASTKILLERNPC", "上次杀死怪物ID");
    	varDesc.put("_LASTKILLERNPCNAME", "上次杀死怪物名称");
    	varDesc.put("_LASTKILLERPLAYER", "上次杀死玩家ID");
    	varDesc.put("_LASTKILLERPLAYERNAME", "上次杀死玩家名称");
    	varDesc.put("_LASTCHATMESSAGE", "上次发送的聊天消息");
    	varDesc.put("_FINISHCHOICE", "选择的任务奖励分支");
    }
    
    /**
     * 所有函数的自然语言表述。
     */
    public static HashMap<String, String> funcDesc = new HashMap<String, String>();
    static {
    	funcDesc.put("Set", "设置 {0} 的值为 {1}");
    	funcDesc.put("Inc", "{0} 增加  {1}");
    	funcDesc.put("Dec", "{0} 减少 {1}");
    	funcDesc.put("Random", "随机数");
    	funcDesc.put("If", "如果 {0} 成立，执行 {1}，否则执行 {2}");
    	funcDesc.put("AssignTask", "强制接受任务 {0}");
    	funcDesc.put("EndTask", "选择奖励 {0} 结束任务");
    	funcDesc.put("HasTask", "拥有任务 {0}");
    	funcDesc.put("TaskFinished", "完成过任务 {0}");
    	funcDesc.put("GetReward", "获得任务奖励 {0}");
    	funcDesc.put("CanFinish", "任务已完成");
    	funcDesc.put("Chat", "{0} 说： {1}");
    	funcDesc.put("Message", "消息：{0}");
    	funcDesc.put("Question", "询问：{0}");
    	funcDesc.put("OpenUI", "打开界面 {0}");
    	funcDesc.put("Flash", "屏幕闪烁");
    	funcDesc.put("RefreshNPC", "刷新NPC {0}");
    	funcDesc.put("GotoMap", "传送到 {0}:{1},{2}");
    	funcDesc.put("Logout", "强制退出");
    	funcDesc.put("MoveNPC", "移动NPC {0} 到 {1}:{2},{3}");
    	funcDesc.put("FindNPC", "NPC {0} 在 {1} 像素内");
    	funcDesc.put("FindPlayer", "玩家 {0} 在 {1} 像素内");
    	funcDesc.put("GetItemCount", "物品 {0} 的数量");
    	funcDesc.put("HasItem", "拥有 {1} 个 物品 {0}");
    	funcDesc.put("AddItem", "添加 {1} 个物品 {0}");
    	funcDesc.put("RemoveItem", "删除 {1} 个物品 {0}");
    	funcDesc.put("E_Approach", "检测到玩家接近 {0}:{1},{2},{3}");
    	funcDesc.put("E_EnterMap", "检测到玩家进入场景 {0}");
    	funcDesc.put("E_Kill", "检测到玩家杀死怪物 {0} ");
    	funcDesc.put("E_KillPlayer", "检测到玩家杀死玩家 {0}");
    	funcDesc.put("E_UseSkill", "检测到玩家使用技能 {0}");
    	funcDesc.put("E_TouchNPC", "检测到玩家和NPC {0} 对话");
    	funcDesc.put("E_Killed", "检测到玩家被怪物杀死");
    	funcDesc.put("E_KilledByPlayer", "检测到玩家被其他玩家杀死");
    	funcDesc.put("E_UseItem", "检测到玩家使用物品 {0}");
    	funcDesc.put("E_Chat", "检测到玩家发送聊天消息");
    	funcDesc.put("E_OpenUI", "检测到玩家打开界面 {0}");
    	funcDesc.put("E_AnswerQuestion", "检测到玩家选择 {1}");
    	funcDesc.put("E_CloseChat", "检测到玩家关闭对话 {0}");
    	funcDesc.put("E_CloseMessage", "检测到玩家关闭消息 {0}");
    	funcDesc.put("E_TaskFinish", "检测到任务结束");
    	funcDesc.put("E_TimeEnd", "计时器到达指定时间{0}");
    	funcDesc.put("E_TickCount", "计数器到达指定数量{0}");
    	funcDesc.put("E_AddBuff", "增加buff{0}");
        funcDesc.put("E_RemoveBuff", "删除buff{0}");
        funcDesc.put("E_HasBuff", "拥有buffer{0}");
        funcDesc.put("E_MateTeam", "夫妻组队");
        funcDesc.put("E_MastTeam", "师徒组队");
        funcDesc.put("E_FriendTeam", "兄弟组队");
        
        funcDesc.put("E_HasPetLevel", "是否有宠物{0}:宠物级别{1}");
        funcDesc.put("E_HasIllusion", "是否幻化");
        funcDesc.put("E_HasMounts", "是否有坐骑");
        funcDesc.put("E_HasClan", "是否有血盟");
        funcDesc.put("E_EquLevel", "装备级别达到{0}");
        funcDesc.put("E_HpMpSurplus", "血量蓝量{0}，剩余到{1}");
        funcDesc.put("E_PetRefinery", "宠物炼化{0}");
        funcDesc.put("E_RoleIllusion", "角色幻化{0}");
        funcDesc.put("E_Battle", "切磋中胜利失败{0}");
        funcDesc.put("E_ChangeNpcAttribute", "hp{0}：mp{1}：阵营{2): 性别{3}");
        funcDesc.put("E_AlterEyeSight", "范围{0}：色度{1}");
        funcDesc.put("SendMail", "发件人{0}标题{1}内容{2}物品{3}{4}个");
        funcDesc.put("SetCameraPath", "路径{0}速度{1}");
    }

    /**
     * 把字符串常量解释为Java字符串。
     */
    public static String translateStringConstant(String str) {
        // 字符串常量必然开头和结尾都是"
        StringBuffer buf = new StringBuffer();
        char[] data = str.toCharArray();
        for (int i = 1; i < data.length - 1; i++) {
            char ch = data[i];
            if (ch == '\\') {
                switch (data[i + 1]) {
                case 'n':
                    buf.append("\n");
                    break;
                case 'r':
                    buf.append("\r");
                    break;
                case 't':
                    buf.append("\t");
                    break;
                default:
                    buf.append(data[i + 1]);
                    break;
                }
                i++;
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * 解释整型常量。
     */
    public static int translateNumberConstant(String str) {
        if (str.startsWith("0x") || str.startsWith("0X")) {
        	// 16进制
        	str = str.substring(2);
        	if (str.length() < 8) {
        		return Integer.parseInt(str, 16);
        	} else {
        		int low = Integer.parseInt(str.substring(1), 16);
        		int high = Integer.parseInt(str.substring(0, 1), 16);
        		return (high << 28) | low;
        	}
        } else {
            return Integer.parseInt(str);
        }
    }

    /**
     * 把Java字符串转换为表达式中的格式。
     */
    public static String reverseConv(String msg) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < msg.length(); i++) {
            switch (msg.charAt(i)) {
            case '\n':
                buf.append("\\n");
                break;
            case '\r':
                buf.append("\\r");
                break;
            case '\t':
                buf.append("\\t");
                break;
            case '"':
                buf.append("\\\"");
                break;
            case '\\':
                buf.append("\\\\");
                break;
            default:
                buf.append(msg.charAt(i));
                break;
            }
        }
        return buf.toString();
    }

    /**
     * 得到操作符的字符串表现。
     * @param op
     * @return
     */
    public static String op2str(int op) {
        switch (op) {
        case ParserConstants.EQ:
        	return "==";
        case ParserConstants.NE:
        	return "!=";
        case ParserConstants.LT:
        	return "<";
        case ParserConstants.LE:
        	return "<=";
        case ParserConstants.GT:
        	return ">";
        case ParserConstants.GE:
        	return ">=";
        default:
        	return "";
        }
    }
    
    /**
     * 得到操作符的名称。
     */
    public static String op2nstr(int op) {
        switch (op) {
        case ParserConstants.EQ:
        	return "等于";
        case ParserConstants.NE:
        	return "不等于";
        case ParserConstants.LT:
        	return "小于";
        case ParserConstants.LE:
        	return "跌至";
        case ParserConstants.GT:
        	return "大于";
        case ParserConstants.GE:
        	return "达到";
        default:
        	return "";
        }
    }
}
