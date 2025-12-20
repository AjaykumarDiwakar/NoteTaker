package com.notetaker.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.notetaker.constant.NoteTakerConstants;
import com.notetaker.constant.NotificationCategory;
import com.notetaker.constant.NotificationSettingKeys;
import com.notetaker.dto.MailBody;
import com.notetaker.entity.NotesDetail;
import com.notetaker.entity.NotificationTemplates;
import com.notetaker.repository.NotificationSettingRepository;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

	private final JavaMailSender javaMailSender;

	@Autowired
	private NotificationService notificationService;

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
			helper.setText(mailBody.getText(), true);
			javaMailSender.send(message);

		} catch (Exception e) {
			log.error("error while sending email");

		}
	}

	public void sendEventBasedEmailNotification(String userId, String email, String name, String role, Object[] args,
			String eventName) {
		NotificationTemplates tem = notificationService.getNotificationTemplateByEventName(eventName,
				NotificationCategory.EMAIL.name());
		if (tem == null) {
			log.info("no email template found for add note event");
			return;
		}
		MailBody mailBody = MailBody.builder().to(email).subject(MessageFormat.format(tem.getSubject(), args))
				.text(MessageFormat.format(tem.getMessageText(), args)).build();
		sendSimpleMessage(mailBody);
		notificationService.addNotificationHistoryInDbForEmail(mailBody, userId, eventName, role);

	}
}