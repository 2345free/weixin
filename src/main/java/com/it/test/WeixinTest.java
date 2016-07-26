package com.it.test;

import com.it.menu.Menu;
import com.it.po.AccessToken;
import com.it.util.MessageUtil;
import com.it.util.WeixinUtil;

import net.sf.json.JSONObject;

public class WeixinTest {
	public static void main(String[] args) {
		try {
			AccessToken token = WeixinUtil.getAccessToken();
			System.out.println("票据"+token.getToken());
			System.out.println("有效时间"+token.getExpiresIn());
			
			//String path = "D:/imooc.jpg";
			//String mediaId = WeixinUtil.upload(path, token.getToken(), "thumb");
			//System.out.println(mediaId);
			
			//String result = WeixinUtil.translate("my name is laobi");
			//String result = WeixinUtil.translateFull("");
			//System.out.println(result);
			
			//创建菜单
			Menu menu = WeixinUtil.initMenu();
			int result = WeixinUtil.createMenu(token.getToken(), JSONObject.fromObject(menu).toString());
			System.out.println(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
