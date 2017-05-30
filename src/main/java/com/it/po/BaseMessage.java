package com.it.po;

import lombok.Data;

/**
 * 消息父类
 * @author Stephen
 *
 */
@Data
public class BaseMessage {
	//接收方微信号
	private String ToUserName;
	//发送方微信号
	private String FromUserName;
	//创建时间
	private long CreateTime;
	//消息类型
	private String MsgType;
}
