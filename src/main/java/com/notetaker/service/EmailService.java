package com.notetaker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.notetaker.dto.MailBody;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String sendermail;

	public EmailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void sendSimpleMessage(MailBody mailBody) {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, false);
			
			helper.setTo(mailBody.getTo());
			helper.setSubject(mailBody.getSubject());
			helper.setText(mailBody.getText(), true); // 🔥 false = plain text (NOT HTML)
			
			javaMailSender.send(message);
			
		} catch (Exception e) {
			log.error("error while sending email");
			
		}
	}
}