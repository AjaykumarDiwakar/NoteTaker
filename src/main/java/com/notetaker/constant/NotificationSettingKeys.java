package com.notetaker.constant;

import java.util.Map;

public class NotificationSettingKeys {

	public static final String ALLOW_EMAIL_NOTIFICATIONS = "allow_email_noti";
	public static final String EMAIL_ON_ADD_NOTE_KEY = "email_on_add_note";
	public static final String EMAIL_ON_DELETE_NOTE_KEY = "email_on_delete_note";
	public static final String EMAIL_ON_UPDATE_NOTE_KEY = "email_on_update_note";
	public static final String EMAIL_ON_LOGIN_KEY = "email_on_login";
	public static final String EMAIL_ON_USER_REGISTRATION = "email_on_user_register";

	private static final Map<String, Boolean> emailPreferenceMap = Map.of(ALLOW_EMAIL_NOTIFICATIONS,false,EMAIL_ON_ADD_NOTE_KEY, false,
			EMAIL_ON_DELETE_NOTE_KEY, false, EMAIL_ON_UPDATE_NOTE_KEY, false, EMAIL_ON_LOGIN_KEY, false);

	public static final Map<String, Boolean> SETTING_FOR_ADMIN = Map.of(EMAIL_ON_USER_REGISTRATION, false);

	public static final Map<NotificationCategory, Map<String, Boolean>> SETTING_MAP = Map.of(NotificationCategory.EMAIL,
			emailPreferenceMap);
}
