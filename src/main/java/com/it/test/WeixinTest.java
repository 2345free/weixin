package com.it.test;

import java.io.IOException;

import org.apache.http.ParseException;

import com.it.menu.Menu;
import com.it.po.AccessToken;
import com.it.util.WeixinUtil;

import net.sf.json.JSONObject;

public class WeixinTest {
	public static void main(String[] args) {
		try {
//			String tq=WeixinUtil.getWeather(URLEncoder.encode("姝︽眽","utf-8"));
//			System.out.println(tq);
//			System.out.println(URLDecoder
//					.decode("\u8bf7\u8f93\u5165\u57ce\u5e02\u540d\u79f0\u6216\u57ce\u5e02\u4ee3\u7801","utf-8"));
			
			createMenu();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void createMenu() throws ParseException, IOException{
		AccessToken token = WeixinUtil.getAccessToken();
		System.out.println("票据"+token.getToken());
		System.out.println("有效时间"+token.getExpiresIn());
		
		//String path = "D:/imooc.jpg";
		//String mediaId = WeixinUtil.upload(path, token.getToken(), "thumb");
		//System.out.println(mediaId);
		
		//String result = WeixinUtil.translate("my name is laobi");
		//String result = WeixinUtil.translateFull("");
		//System.out.println(result);
		
		//鍒涘缓鑿滃崟
		Menu menu = WeixinUtil.initMenu();
		int result = WeixinUtil.createMenu(token.getToken(), JSONObject.fromObject(menu).toString());
		System.out.println(result);
	}
	
}
