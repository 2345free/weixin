package com.it.po;

import lombok.Data;

@Data
public class AccessToken {
	//��ȡ����ƾ֤
	private String token;
	//ƾ֤��Чʱ�䣬��λ����
	private int expiresIn;
}
