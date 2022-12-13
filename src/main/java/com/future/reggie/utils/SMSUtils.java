package com.future.reggie.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

/**
 * 短信发送工具类
 */
public class SMSUtils {

	/**
	 * 发送短信
	 * @param signName 签名
	 * @param templateCode 模板
	 * @param phoneNumbers 手机号
	 * @param param 参数
	 */
	public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param){
		//传入 阿里云短信服务注册时获得的 accesskey id/secret
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5tE4HK56U5jAyN5wscMs", "k2aNswO5h5kZdNfMCBRt71bSAJZUvy");
		IAcsClient client = new DefaultAcsClient(profile);

		SendSmsRequest request = new SendSmsRequest();
		request.setSysRegionId("cn-hangzhou");
		//phoneNumbers 为要发送短信的手机号码
		request.setPhoneNumbers(phoneNumbers);
		//signName 为注册短信服务时的签名名称  （郭睿开发测试 20009799305）
		request.setSignName(signName);
		//templateCode 为注册短信服务时的模板编号（SMS_263555312）
		request.setTemplateCode(templateCode);
		//param 为生成的验证码，用来替换code
		request.setTemplateParam("{\"code\":\""+param+"\"}");
		try {
			SendSmsResponse response = client.getAcsResponse(request);
			System.out.println("短信发送成功");
		}catch (ClientException e) {
			e.printStackTrace();
		}
	}

}
