package com.it.po;

import lombok.Data;

/**
 * ��Ϣ����
 * @author Stephen
 *
 */
@Data
public class BaseMessage {
	//���շ�΢�ź�
	private String ToUserName;
	//���ͷ�΢�ź�
	private String FromUserName;
	//����ʱ��
	private long CreateTime;
	//��Ϣ����
	private String MsgType;
}
