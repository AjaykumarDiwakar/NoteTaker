package com.notetaker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.notetaker.dto.MailBody;

@Service
public class EmailService {

	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String sendermail;

	public EmailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void sendSimpleMessage(MailBody mailBody) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(mailBody.getTo());
		message.setFrom(sendermail);
		message.setSubject(mailBody.getSubject());
		message.setText(mailBody.getText());

		javaMailSender.send(message);
	}
}