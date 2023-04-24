package com.ecommerce.core.util;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.entities.EmailConfig;
import com.ecommerce.core.repositories.EmailConfigRepository;
import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class MailService {

	public static final String MAIL_FROM = "ptdv-cskhttnc@mobifone.vn";

	private static final Logger logger = LoggerFactory.getLogger(MailService.class);
	public static final String EMAIL_HTML_RESET_PASSWORD = "html/reset-password";
	public static final String EMAIL_HTML_CONFIRM_RESET_PASSWORD = "html/confirm-forgot-password";

	@Autowired
	protected JavaMailSender javaMailSender;
	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private EmailConfigRepository emailRepo;

	public void	sendHtmlMail(String from, String[] to, String[] cc, String subject, String textContent) {
		try {
			List<EmailConfig> configs = emailRepo.findAll();
			if(configs != null && !configs.isEmpty()) {
				JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			    mailSender.setHost(configs.get(0).getHost());
			    mailSender.setPort(Integer.parseInt(configs.get(0).getPort()));
				// must set username as email pattern
			    mailSender.setUsername(configs.get(0).getEmail());
			    mailSender.setPassword(configs.get(0).getPassword());

			    Properties props = mailSender.getJavaMailProperties();
			    props.put("mail.transport.protocol", "smtp");
			    props.put("mail.smtp.auth", "true");
			    props.put("mail.smtp.starttls.enable", "true");
			    props.put("mail.debug", "true");

			    // Prepare message using a Spring helper
				final MimeMessage message = mailSender.createMimeMessage();
				final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, "UTF-8");
				mimeMessageHelper.setTo(to);
				mimeMessageHelper.setFrom(configs.get(0).getEmail());
	            if (cc != null)
	                mimeMessageHelper.setCc(cc);
				// mimeMessageHelper.setFrom(from);
				mimeMessageHelper.setSubject(subject);
				mimeMessageHelper.setText(textContent, true);
				// Send email
				mailSender.send(message);
			}
		} catch (MessagingException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void sendResetPasswordMail(String[] to, String username, String password) {
		try {
			HashMap<String, String> hash_map = new HashMap<String, String>();
			hash_map.put("username", username);
			hash_map.put("password", password);
			// Prepare the evaluation context
			final Context ctx = new Context(LocaleContextHolder.getLocale());
			for (Map.Entry<String, String> entry : hash_map.entrySet()) {
				ctx.setVariable(entry.getKey(), entry.getValue());
			}
			final String textContent = this.templateEngine.process(EMAIL_HTML_RESET_PASSWORD, ctx);
			this.sendHtmlMail(MAIL_FROM, to, new String[] {}, "Mật khẩu đăng nhập hệ thống Quản lý Minh chứng.", textContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendConfirmForgotPasswordMail(String[] to, String username, String resetPasswordToken) {
		try {
			HashMap<String, String> hash_map = new HashMap<String, String>();
			hash_map.put("username", username);
			hash_map.put("confirmUrl", ConstantDefine.FRONTEND_PATH.RESET_PASSWORD_URL + resetPasswordToken);
			// Prepare the evaluation context
			final Context ctx = new Context(LocaleContextHolder.getLocale());
			for (Map.Entry<String, String> entry : hash_map.entrySet()) {
				ctx.setVariable(entry.getKey(), entry.getValue());
			}
			final String textContent = this.templateEngine.process(EMAIL_HTML_CONFIRM_RESET_PASSWORD, ctx);
			this.sendHtmlMail(MAIL_FROM, to, new String[] {}, "Xác nhận yêu cầu cấp lại mật khẩu mới", textContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void	sendHtmlMail1(String from, String[] to, String[] cc, String subject, String textContent,List< MultipartFile> atttachment) {
		try {
			List<EmailConfig> configs = emailRepo.findAll();
			if(configs != null && !configs.isEmpty()) {
				JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
				mailSender.setHost(configs.get(0).getHost());
				mailSender.setPort(Integer.parseInt(configs.get(0).getPort()));
				// must set username as email pattern
				mailSender.setUsername(configs.get(0).getEmail());
				mailSender.setPassword(configs.get(0).getPassword());

				Properties props = mailSender.getJavaMailProperties();
				props.put("mail.transport.protocol", "smtp");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.debug", "true");

				// Prepare message using a Spring helper
				final MimeMessage message = mailSender.createMimeMessage();
				final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,true,  CharEncoding.UTF_8);
				mimeMessageHelper.setTo(to);
				mimeMessageHelper.setFrom(configs.get(0).getEmail());
				if (cc != null)
					mimeMessageHelper.setCc(cc);
				// mimeMessageHelper.setFrom(from);
				mimeMessageHelper.setSubject(subject);
				mimeMessageHelper.setText(textContent, true);
//				đính kèm file
//				FileSystemResource fileSystemResolver= new FileSystemResource(new File(atttachment.toString()));
//				mimeMessageHelper.addAttachment(atttachment.getOriginalFilename(),atttachment);

				if(atttachment.size() > 0) {
					for (MultipartFile a: atttachment){
						mimeMessageHelper.addAttachment(a.getOriginalFilename(),a);
					}

				}
				// Send email
				mailSender.send(message);
			}
		} catch (MessagingException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
