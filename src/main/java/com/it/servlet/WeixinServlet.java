package com.it.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;

import com.it.util.CheckUtil;
import com.it.util.MessageUtil;
import com.it.util.WeixinUtil;

public class WeixinServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	/**
	 * 验证消息是否是来自微信服务器的
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。*/
		String signature = req.getParameter("signature");
		
		String timestamp = req.getParameter("timestamp");
		/*随机数*/
		String nonce = req.getParameter("nonce");
		/*随机字符串*/
		String echostr = req.getParameter("echostr");
		
		PrintWriter out = resp.getWriter();
		/*
		 * 检验signature,通过则原样返回echostr参数内容
		 */
		if(CheckUtil.checkSignature(signature, timestamp, nonce)){
			out.print(echostr);
		}
	}
	
	
	/**
	 * 消息的接收与响应
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		try {
			/*
			 * 微信服务器响应的xml文档,要转换成map
			 */
			Map<String, String> map = MessageUtil.xmlToMap(req);
			//开发者微信号
			String fromUserName = map.get("FromUserName");
			//发送方帐号（一个OpenID）
			String toUserName = map.get("ToUserName");
			//消息类型
			String msgType = map.get("MsgType");
			//文本消息内容
			String content = map.get("Content");
			
			/*
			 * 文本消息处理逻辑
			 */
			String message = null;
			if(MessageUtil.MESSAGE_TEXT.equals(msgType)){
				if("1".equals(content)){
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.firstMenu());
				}else if("2".equals(content)){
					message = MessageUtil.initNewsMessage(toUserName, fromUserName);
				}else if("3".equals(content)){
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.threeMenu());
				}else if("?".equals(content) || "？".equals(content)){
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
				}else if(content.startsWith("翻译")){
					String word = content.replaceAll("^翻译", "").trim();
					if("".equals(word)){
						message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.threeMenu());
					}else{
						message = MessageUtil.initText(toUserName, fromUserName, WeixinUtil.translate(word));
					}
				}
				/*
				 * 菜单点击事件消息处理
				 */
			}else if(MessageUtil.MESSAGE_EVNET.equals(msgType)){
				String eventType = map.get("Event");
				/*
				 * 关注事件
				 */
				if(MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)){
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
				/*
				 * 点击菜单拉取消息时的事件推送
				 */
				}else if(MessageUtil.MESSAGE_CLICK.equals(eventType)){
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
				/*
				 * 点击菜单跳转链接时的事件推送
				 */
				}else if(MessageUtil.MESSAGE_VIEW.equals(eventType)){
					String url = map.get("EventKey");
					message = MessageUtil.initText(toUserName, fromUserName, url);
				/*
				 * scancode_push：扫码推事件的事件推送
				 */
				}else if(MessageUtil.MESSAGE_SCANCODE.equals(eventType)){
					String key = map.get("EventKey");
					message = MessageUtil.initText(toUserName, fromUserName, key);
				}
			/*
			 * location_select：弹出地理位置选择器的事件推送
			 */
			}else if(MessageUtil.MESSAGE_LOCATION.equals(msgType)){
				String label = map.get("Label");
				message = MessageUtil.initText(toUserName, fromUserName, label);
			}
			
			System.out.println(message);
			
			out.print(message);
		} catch (DocumentException e) {
			e.printStackTrace();
		}finally{
			out.close();
		}
	}
}
