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
	 * ��֤��Ϣ�Ƿ�������΢�ŷ�������
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*΢�ż���ǩ����signature����˿�������д��token�����������е�timestamp������nonce������*/
		String signature = req.getParameter("signature");
		
		String timestamp = req.getParameter("timestamp");
		/*�����*/
		String nonce = req.getParameter("nonce");
		/*����ַ���*/
		String echostr = req.getParameter("echostr");
		
		PrintWriter out = resp.getWriter();
		/*
		 * ����signature,ͨ����ԭ������echostr��������
		 */
		if(CheckUtil.checkSignature(signature, timestamp, nonce)){
			out.print(echostr);
		}
	}
	
	
	/**
	 * ��Ϣ�Ľ�������Ӧ
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		try {
			/*
			 * ΢�ŷ�������Ӧ��xml�ĵ�,Ҫת����map
			 */
			Map<String, String> map = MessageUtil.xmlToMap(req);
			//������΢�ź�
			String fromUserName = map.get("FromUserName");
			//���ͷ��ʺţ�һ��OpenID��
			String toUserName = map.get("ToUserName");
			//��Ϣ����
			String msgType = map.get("MsgType");
			//�ı���Ϣ����
			String content = map.get("Content");
			
			/*
			 * �ı���Ϣ�����߼�
			 */
			String message = null;
			if(MessageUtil.MESSAGE_TEXT.equals(msgType)){
				if("1".equals(content)){
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.firstMenu());
				}else if("2".equals(content)){
					message = MessageUtil.initNewsMessage(toUserName, fromUserName);
				}else if("3".equals(content)){
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.threeMenu());
				}else if("?".equals(content) || "��".equals(content)){
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
				}else if(content.startsWith("����")){
					String word = content.replaceAll("^����", "").trim();
					if("".equals(word)){
						message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.threeMenu());
					}else{
						message = MessageUtil.initText(toUserName, fromUserName, WeixinUtil.translate(word));
					}
				}
				/*
				 * �˵�����¼���Ϣ����
				 */
			}else if(MessageUtil.MESSAGE_EVNET.equals(msgType)){
				String eventType = map.get("Event");
				/*
				 * ��ע�¼�
				 */
				if(MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)){
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
				/*
				 * ����˵���ȡ��Ϣʱ���¼�����
				 */
				}else if(MessageUtil.MESSAGE_CLICK.equals(eventType)){
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
				/*
				 * ����˵���ת����ʱ���¼�����
				 */
				}else if(MessageUtil.MESSAGE_VIEW.equals(eventType)){
					String url = map.get("EventKey");
					message = MessageUtil.initText(toUserName, fromUserName, url);
				/*
				 * scancode_push��ɨ�����¼����¼�����
				 */
				}else if(MessageUtil.MESSAGE_SCANCODE.equals(eventType)){
					String key = map.get("EventKey");
					message = MessageUtil.initText(toUserName, fromUserName, key);
				}
			/*
			 * location_select����������λ��ѡ�������¼�����
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
