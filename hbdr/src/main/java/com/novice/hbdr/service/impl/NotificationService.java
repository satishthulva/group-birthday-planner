package com.novice.hbdr.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;

import com.google.inject.Inject;
import com.novice.hbdr.Configuration;
import com.novice.hbdr.datamodels.Group;
import com.novice.hbdr.datamodels.GroupID;
import com.novice.hbdr.datamodels.Month;
import com.novice.hbdr.datamodels.Person;
import com.novice.hbdr.service.GroupService;

/**
 * Service to send notifications to individual friend groups
 * 
 * @author satish
 */
public class NotificationService implements Runnable {

	private final GroupService groupService;

	private final Configuration configuration;

	@Inject
	public NotificationService(GroupService groupService, Configuration configuration) {
		this.groupService = groupService;
		this.configuration = configuration;
	}

	/**
	 * indicates whether the service is running or not
	 */
	private boolean isShutdown = false;

	/**
	 * Find whether the notification service tried to find whether there is a
	 * need to send notification for the group for given day or not ?
	 * 
	 * @param date
	 *            Date of interest
	 * @param groupID
	 *            Unique id of the group
	 * @return <code>true</code>, if notification service tried to find whether
	 *         there is a need to send notification for the group for given day
	 *         or not ?
	 */
	private boolean isNotifierRunForDay(Date date, GroupID groupID) {
		// TODO : read the data from cache or persistent store
		return false;
	}

	/**
	 * Register that the notifier is run for the day for given group
	 * 
	 * @param date
	 *            Date of interest
	 * @param groupID
	 *            Unique id of the group
	 */
	private void registerNotifierRunForDay(Date date, GroupID groupID) {
		// TODO : write the data to cache or persistent store
	}

	/**
	 * Find the groups with notification to be sent and send the notification
	 */
	private void sendNotification() {
		Date date = new Date();
		for (Group group : groupService.findGroups()) {
			if (isNotifierRunForDay(date, group.getId()))
				continue;

			runReminder(group);

			registerNotifierRunForDay(date, group.getId());
		}
	}

	/**
	 * Send birthday reminder email for the relevant people in group, if there
	 * is birthday coming up soon
	 * 
	 * @param group
	 *            friend group
	 */
	private void runReminder(Group group) {
		int reminderPeriodInDays = group.getReminderPeriodInDays();
		if (reminderPeriodInDays == -1)
			reminderPeriodInDays = configuration.findDefaultReminderPeriod();

		List<Person> persons = group.getMembers();

		for (int i = persons.size(); i-- > 0;) {
			Person person = persons.get(i);

			if (isPersonBirthdaySoon(person, reminderPeriodInDays)) {
				remindAboutPersonBirthday(persons, person, reminderPeriodInDays);
			}
		}
	}

	/**
	 * It's <code>bdayPerson</code>s' birthday in
	 * <code>reminderPeriodInDays</code>. Remind everybody in that persons'
	 * clique.
	 * 
	 * @param all
	 *            All persons in the bday persons' clique( including that person
	 *            )
	 * @param bdayPerson
	 *            The person of interest
	 * @param reminderPeriodInDays
	 *            Number of days in advance to send notification email
	 */
	private void remindAboutPersonBirthday(List<Person> all, Person bdayPerson, int reminderPeriodInDays) {
		List<String> emails = new ArrayList<>();

		for (int i = all.size(); i-- > 0;) {
			Person p = all.get(i);
			if (p.equals(bdayPerson))
				continue;

			emails.add(p.getEmail());
		}

		sendEmail(bdayPerson, emails, reminderPeriodInDays);
	}

	/**
	 * Send reminder email to all given emails about the upcoming birthday of
	 * bdayPerson
	 * 
	 * @param bdayPerson
	 *            Person whose birthday is coming up soon
	 * @param emails
	 *            Emails of people to deliver notification email
	 * @param reminderPeriodInDays
	 *            Number of days in advance to send reminder email
	 */
	private void sendEmail(Person bdayPerson, List<String> emails, int reminderPeriodInDays) {
		Email email = prepareEmail(bdayPerson, emails, reminderPeriodInDays);
		Session session = configuration.getMailer().getSession();

		MimeMessage message = new MimeMessage(session);
		try {
			InternetAddress dests[] = new InternetAddress[email.people.size()];
			int i = 0;
			for (String one : email.people) {
				dests[i++] = new InternetAddress(one);
			}

			message.setRecipients(Message.RecipientType.TO, dests);
			message.setSubject(email.subject, "UTF-8");

			Multipart mp = new MimeMultipart();

			MimeBodyPart mbp = constructBodyPart(email);

			mp.addBodyPart(mbp);

			message.setContent(mp);
			message.setSentDate(new Date());

			Transport.send(message);
		} catch (SendFailedException e) {
			Address[] invalid = e.getInvalidAddresses();
			Address[] validUnSent = e.getValidUnsentAddresses();

			StringBuffer errMessage = new StringBuffer();

			if (invalid != null && invalid.length > 0) {
				errMessage.append("Invalid email address" + (invalid.length > 1 ? "es" : "") + " : ");

				errMessage.append(getCommaSeparatedAddresses(invalid));

				errMessage.append(". ");
			}

			if (validUnSent != null && validUnSent.length > 0) {
				errMessage.append(
						"Valid email address" + (validUnSent.length > 1 ? "es" : "") + "but not able to send : ");

				errMessage.append(getCommaSeparatedAddresses(validUnSent));
			}

			System.err.println(errMessage.toString());

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Prepare mail content to send reminder mail for
	 *         <code>bdayPerson</code>s' birthday
	 */
	private Email prepareEmail(Person bdayPerson, List<String> emails, int reminderPeriodInDays) {
		String subject = bdayPerson.getName() + "s' Birthday in " + reminderPeriodInDays + " days !";

		String body = "<p>Hey folks,<br/><br/>It's <b>" + bdayPerson.getName() + "</b>s' birthday on <b>"
				+ bdayPerson.getBirthday().getDayOfMonth() + ", " + bdayPerson.getBirthday().getMonth().name()
				+ "</b>. <br/><br/>" + "Let's discuss what we can do to make it memorable and fun.<br/><br/>"
				+ "<p><i>PS : This is an auto-generated mail. Please ignore if this is not relevant for you.</i><br/><br/>"
				+ "<p>--<br/>Thanks & Regards,<br/>Birthday Reminder Daemon!<br/>";

		return new Email(subject, body, emails);
	}

	/**
	 * Find out whether it's given persons' birthday in
	 * <code>reminderPeriodInDays</code> days.
	 * 
	 * @param person
	 *            Person of interest
	 * @param reminderPeriodInDays
	 *            Number of days in advance to the persons' birthday
	 * 
	 *            NOTE : there is an assumption that it won't be more than 27
	 *            days
	 * 
	 * @return <code>true</code>, if it's given persons' birthday in
	 *         <code>reminderPeriodInDays</code> days. <code>false</code>,
	 *         otherwise
	 */
	private boolean isPersonBirthdaySoon(Person person, int reminderPeriodInDays) {
		Calendar todayC = Calendar.getInstance();

		Calendar birthdayC = Calendar.getInstance();
		birthdayC.set(Calendar.MONTH, person.getBirthday().getMonth().ordinal());
		birthdayC.set(Calendar.DAY_OF_MONTH, person.getBirthday().getDayOfMonth());

		if (!todayC.before(birthdayC))
			return false;

		int todayMonth = todayC.get(Calendar.MONTH);
		int bdayMonth = birthdayC.get(Calendar.MONTH);

		int monthDiff = bdayMonth - todayMonth;

		if (monthDiff > 1)
			return false;

		int daysLeftTillBDay = 0;

		if (monthDiff == 0) {
			daysLeftTillBDay = birthdayC.get(Calendar.DAY_OF_MONTH) - todayC.get(Calendar.DAY_OF_MONTH);
		} else {
			daysLeftTillBDay = Month.values()[todayMonth].getNumberOfDaysInMonth(todayC.get(Calendar.YEAR))
					- todayC.get(Calendar.DAY_OF_MONTH) + 1;
			daysLeftTillBDay += birthdayC.get(Calendar.DAY_OF_MONTH) - 1;
		}

		return daysLeftTillBDay == reminderPeriodInDays;
	}

	/**
	 * Main details of reminder email we send
	 * 
	 * @author satishbabu
	 */
	private static class Email {
		/**
		 * Subject of the email
		 */
		private String subject;
		/**
		 * The main content
		 */
		private String body;
		/**
		 * Whom should we send it to
		 */
		private List<String> people;

		public Email(String subject, String body, List<String> people) {
			super();
			this.subject = subject;
			this.body = body;
			this.people = people;
		}

		/**
		 * @return the subject
		 */
		@SuppressWarnings("unused")
		public String getSubject() {
			return subject;
		}

		/**
		 * @return the body
		 */
		@SuppressWarnings("unused")
		public String getBody() {
			return body;
		}

		/**
		 * @return the people
		 */
		@SuppressWarnings("unused")
		public List<String> getPeople() {
			return people;
		}

	}

	/**
	 * @return A presentable comma separated list of addresses
	 */
	private String getCommaSeparatedAddresses(Address[] addresses) {
		StringBuffer errMessage = new StringBuffer();

		if (addresses != null && addresses.length > 0) {
			for (int i = 0; i < addresses.length; i += 1) {
				if (i > 0)
					errMessage.append(", ");

				errMessage.append(addresses[i].toString());
			}
		}

		return errMessage.toString();
	}

	/**
	 * Construct the Email body as multipart/alternative MIME type content with
	 * plain text version and html version. Add html version at last to give it
	 * more priority.
	 * 
	 * @param email
	 * @return Email body, just the way mail client likes it
	 * @throws MessagingException
	 */
	private MimeBodyPart constructBodyPart(Email email) throws MessagingException {
		MimeBodyPart mbp = new MimeBodyPart();

		Multipart multipartAlt = new MimeMultipart("alternative");

		String htmlVersion = (email.body != null ? email.body : "");
		String plainTextVersion = Jsoup.parse(htmlVersion).text();

		// adding plain first
		MimeBodyPart plain = new MimeBodyPart();
		plain.setContent(plainTextVersion, "text/plain;charset=utf-8");

		multipartAlt.addBodyPart(plain);

		// adding html last
		MimeBodyPart html = new MimeBodyPart();
		html.setContent(htmlVersion, "text/html;charset=utf-8");

		multipartAlt.addBodyPart(html);

		// setting contenet type to multipart/alternative
		mbp.setContent(multipartAlt, "multipart/alternative");

		return mbp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (!isShutdown) {
			sendNotification();

			try {
				Thread.sleep(1000 * 60 * 15);// sleep for 15 minutes and send reminder
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Request to shutdown the service
	 */
	public void shutdown() {
		this.isShutdown = true;
	}

}
