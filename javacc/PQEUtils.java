package com.pip.sanguo.data.quest.pqe;

import java.util.HashMap;

public class PQEUtils {
	/**
	 * ���бȽϲ�������
	 */
	public static int[] COMPARE_OPS = new int[] {
		ParserConstants.EQ, ParserConstants.NE, ParserConstants.LT, 
		ParserConstants.LE, ParserConstants.GT, ParserConstants.GE
	};

	/**
	 * ����ϵͳ���������顣
	 */
	public static String[] SYSTEM_FUNCS = new String[] {
		"_LEVEL", "_MONEY","_HPPercent","_MPPercent", "_HP", "_MAXHP", "_MP", "_MAXMP", "_STR", "_STA", "_AGI",
		"_INT", "_SEX", "_SEXNAME", "_CLASS", "_CLASSNAME", "_NAME", "_MAPID", "_X",
		"_Y", "_LASTKILLERNPC", "_LASTKILLERNPCNAME", "_LASTKILLERPLAYER",
		"_LASTKILLERPLAYERNAME", "_LASTCHATMESSAGE", "_FINISHCHOICE"
	};
	
    /**
     * ����ϵͳ��������Ȼ���Ա�����
     */
    public static HashMap<String, String> varDesc = new HashMap<String, String>();
    static {
    	varDesc.put("_LEVEL", "����");
    	varDesc.put("_MONEY", "��Ǯ");
    	varDesc.put("_HPPercent", "����%");
    	varDesc.put("_MPPercent", "����%");
    	varDesc.put("_HP", "����");
    	varDesc.put("_MAXHP", "�������");
    	varDesc.put("_MP", "����");
    	varDesc.put("_MAXMP", "�������");
    	varDesc.put("_STR", "����");
    	varDesc.put("_STA", "����");
    	varDesc.put("_AGI", "����");
    	varDesc.put("_INT", "����");
    	varDesc.put("_SEX", "�Ա�ID");
    	varDesc.put("_SEXNAME", "�Ա�");
    	varDesc.put("_CLASS", "ְҵID");
    	varDesc.put("_CLASSNAME", "ְҵ");
    	varDesc.put("_NAME", "����");
    	varDesc.put("_MAPID", "����ID");
    	varDesc.put("_X", "X����");
    	varDesc.put("_Y", "Y����");
    	varDesc.put("_LASTKILLERNPC", "�ϴ�ɱ������ID");
    	varDesc.put("_LASTKILLERNPCNAME", "�ϴ�ɱ����������");
    	varDesc.put("_LASTKILLERPLAYER", "�ϴ�ɱ�����ID");
    	varDesc.put("_LASTKILLERPLAYERNAME", "�ϴ�ɱ���������");
    	varDesc.put("_LASTCHATMESSAGE", "�ϴη��͵�������Ϣ");
    	varDesc.put("_FINISHCHOICE", "ѡ�����������֧");
    }
    
    /**
     * ���к�������Ȼ���Ա�����
     */
    public static HashMap<String, String> funcDesc = new HashMap<String, String>();
    static {
    	funcDesc.put("Set", "���� {0} ��ֵΪ {1}");
    	funcDesc.put("Inc", "{0} ����  {1}");
    	funcDesc.put("Dec", "{0} ���� {1}");
    	funcDesc.put("Random", "�����");
    	funcDesc.put("If", "��� {0} ������ִ�� {1}������ִ�� {2}");
    	funcDesc.put("AssignTask", "ǿ�ƽ������� {0}");
    	funcDesc.put("EndTask", "ѡ���� {0} ��������");
    	funcDesc.put("HasTask", "ӵ������ {0}");
    	funcDesc.put("TaskFinished", "��ɹ����� {0}");
    	funcDesc.put("GetReward", "��������� {0}");
    	funcDesc.put("CanFinish", "���������");
    	funcDesc.put("Chat", "{0} ˵�� {1}");
    	funcDesc.put("Message", "��Ϣ��{0}");
    	funcDesc.put("Question", "ѯ�ʣ�{0}");
    	funcDesc.put("OpenUI", "�򿪽��� {0}");
    	funcDesc.put("Flash", "��Ļ��˸");
    	funcDesc.put("RefreshNPC", "ˢ��NPC {0}");
    	funcDesc.put("GotoMap", "���͵� {0}:{1},{2}");
    	funcDesc.put("Logout", "ǿ���˳�");
    	funcDesc.put("MoveNPC", "�ƶ�NPC {0} �� {1}:{2},{3}");
    	funcDesc.put("FindNPC", "NPC {0} �� {1} ������");
    	funcDesc.put("FindPlayer", "��� {0} �� {1} ������");
    	funcDesc.put("GetItemCount", "��Ʒ {0} ������");
    	funcDesc.put("HasItem", "ӵ�� {1} �� ��Ʒ {0}");
    	funcDesc.put("AddItem", "��� {1} ����Ʒ {0}");
    	funcDesc.put("RemoveItem", "ɾ�� {1} ����Ʒ {0}");
    	funcDesc.put("E_Approach", "��⵽��ҽӽ� {0}:{1},{2},{3}");
    	funcDesc.put("E_EnterMap", "��⵽��ҽ��볡�� {0}");
    	funcDesc.put("E_Kill", "��⵽���ɱ������ {0} ");
    	funcDesc.put("E_KillPlayer", "��⵽���ɱ����� {0}");
    	funcDesc.put("E_UseSkill", "��⵽���ʹ�ü��� {0}");
    	funcDesc.put("E_TouchNPC", "��⵽��Һ�NPC {0} �Ի�");
    	funcDesc.put("E_Killed", "��⵽��ұ�����ɱ��");
    	funcDesc.put("E_KilledByPlayer", "��⵽��ұ��������ɱ��");
    	funcDesc.put("E_UseItem", "��⵽���ʹ����Ʒ {0}");
    	funcDesc.put("E_Chat", "��⵽��ҷ���������Ϣ");
    	funcDesc.put("E_OpenUI", "��⵽��Ҵ򿪽��� {0}");
    	funcDesc.put("E_AnswerQuestion", "��⵽���ѡ�� {1}");
    	funcDesc.put("E_CloseChat", "��⵽��ҹرնԻ� {0}");
    	funcDesc.put("E_CloseMessage", "��⵽��ҹر���Ϣ {0}");
    	funcDesc.put("E_TaskFinish", "��⵽�������");
    	funcDesc.put("E_TimeEnd", "��ʱ������ָ��ʱ��{0}");
    	funcDesc.put("E_TickCount", "����������ָ������{0}");
    	funcDesc.put("E_AddBuff", "����buff{0}");
        funcDesc.put("E_RemoveBuff", "ɾ��buff{0}");
        funcDesc.put("E_HasBuff", "ӵ��buffer{0}");
        funcDesc.put("E_MateTeam", "�������");
        funcDesc.put("E_MastTeam", "ʦͽ���");
        funcDesc.put("E_FriendTeam", "�ֵ����");
        
        funcDesc.put("E_HasPetLevel", "�Ƿ��г���{0}:���Ｖ��{1}");
        funcDesc.put("E_HasIllusion", "�Ƿ�û�");
        funcDesc.put("E_HasMounts", "�Ƿ�������");
        funcDesc.put("E_HasClan", "�Ƿ���Ѫ��");
        funcDesc.put("E_EquLevel", "װ������ﵽ{0}");
        funcDesc.put("E_HpMpSurplus", "Ѫ������{0}��ʣ�ൽ{1}");
        funcDesc.put("E_PetRefinery", "��������{0}");
        funcDesc.put("E_RoleIllusion", "��ɫ�û�{0}");
        funcDesc.put("E_Battle", "�д���ʤ��ʧ��{0}");
        funcDesc.put("E_ChangeNpcAttribute", "hp{0}��mp{1}����Ӫ{2): �Ա�{3}");
        funcDesc.put("E_AlterEyeSight", "��Χ{0}��ɫ��{1}");
        funcDesc.put("SendMail", "������{0}����{1}����{2}��Ʒ{3}{4}��");
        funcDesc.put("SetCameraPath", "·��{0}�ٶ�{1}");
    }

    /**
     * ���ַ�����������ΪJava�ַ�����
     */
    public static String translateStringConstant(String str) {
        // �ַ���������Ȼ��ͷ�ͽ�β����"
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
     * �������ͳ�����
     */
    public static int translateNumberConstant(String str) {
        if (str.startsWith("0x") || str.startsWith("0X")) {
        	// 16����
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
     * ��Java�ַ���ת��Ϊ���ʽ�еĸ�ʽ��
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
     * �õ����������ַ������֡�
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
     * �õ������������ơ�
     */
    public static String op2nstr(int op) {
        switch (op) {
        case ParserConstants.EQ:
        	return "����";
        case ParserConstants.NE:
        	return "������";
        case ParserConstants.LT:
        	return "С��";
        case ParserConstants.LE:
        	return "����";
        case ParserConstants.GT:
        	return "����";
        case ParserConstants.GE:
        	return "�ﵽ";
        default:
        	return "";
        }
    }
}
