package com.novice.hbdr.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import com.novice.hbdr.datamodels.GroupDetails;
import com.novice.hbdr.datamodels.GroupID;
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

	/**
	 * Records who has been reminded about which birthday occurrence, in which group
	 */
	private final Set<NotificationKey> notified = ConcurrentHashMap.newKeySet();

	/**
	 * indicates whether reminders already sent before the service started are accounted for
	 */
	private volatile boolean seeded = false;

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
	 * Find the birthdays within reminder period in each group and remind the
	 * members who are not reminded about them yet
	 *
	 * @param today
	 *            The day to send reminders for
	 */
	void sendNotification(Calendar today) {
		LocalDate todayDate = toLocalDate(today);
		notified.removeIf(key -> key.occurrenceDate.isBefore(todayDate));

		List<Group> groups = groupService.findGroups();

		if (!seeded) {
			seedNotified(groups, today);
			seeded = true;
		}

		for (Group group : groups) {
			int reminderPeriodInDays = findReminderPeriod(group);

			for (Person bdayPerson : group.getMembers()) {
				int daysLeft = daysUntilBirthday(bdayPerson, today);
				if (daysLeft < 1 || daysLeft > reminderPeriodInDays)
					continue;

				remindAboutPersonBirthday(group, bdayPerson, todayDate.plusDays(daysLeft), daysLeft);
			}
		}
	}

	/**
	 * Treat current members as already reminded about the birthdays whose
	 * reminder period started before today, so that a restart doesn't remind
	 * them again. Birthdays whose reminder period starts today are left out, so
	 * they are reminded as usual.
	 *
	 * @param groups
	 *            All friend groups
	 * @param today
	 *            The day the service started sending reminders
	 */
	private void seedNotified(List<Group> groups, Calendar today) {
		LocalDate todayDate = toLocalDate(today);

		for (Group group : groups) {
			int reminderPeriodInDays = findReminderPeriod(group);

			for (Person bdayPerson : group.getMembers()) {
				int daysLeft = daysUntilBirthday(bdayPerson, today);
				if (daysLeft < 1 || daysLeft >= reminderPeriodInDays)
					continue;

				LocalDate occurrenceDate = todayDate.plusDays(daysLeft);
				for (Person p : group.getMembers()) {
					if (!p.getEmail().equals(bdayPerson.getEmail()))
						notified.add(toKey(group, bdayPerson, occurrenceDate, p.getEmail()));
				}
			}
		}
	}

	/**
	 * @return Number of days in advance the reminder has to be sent for given group
	 */
	private int findReminderPeriod(Group group) {
		int reminderPeriodInDays = group.getReminderPeriodInDays();
		if (reminderPeriodInDays == -1)
			reminderPeriodInDays = configuration.findDefaultReminderPeriod();

		return reminderPeriodInDays;
	}

	/**
	 * It's <code>bdayPerson</code>s' birthday in <code>daysLeft</code> days.
	 * Remind everybody in that persons' clique who is not reminded yet.
	 *
	 * @param group
	 *            The bday persons' clique
	 * @param bdayPerson
	 *            The person of interest
	 * @param occurrenceDate
	 *            Date of the upcoming birthday
	 * @param daysLeft
	 *            Number of days left till the birthday
	 */
	private void remindAboutPersonBirthday(Group group, Person bdayPerson, LocalDate occurrenceDate, int daysLeft) {
		List<String> emails = new ArrayList<>();

		for (Person p : group.getMembers()) {
			if (p.getEmail().equals(bdayPerson.getEmail()))
				continue;

			if (!notified.contains(toKey(group, bdayPerson, occurrenceDate, p.getEmail())))
				emails.add(p.getEmail());
		}

		if (emails.isEmpty())
			return;

		deliver(bdayPerson, emails, daysLeft);

		// recorded regardless of delivery outcome, to remind at most once
		for (String email : emails)
			notified.add(toKey(group, bdayPerson, occurrenceDate, email));
	}

	/**
	 * @return Key identifying the reminder to <code>recipientEmail</code> about given occurrence of <code>bdayPerson</code>s' birthday
	 */
	private NotificationKey toKey(Group group, Person bdayPerson, LocalDate occurrenceDate, String recipientEmail) {
		return new NotificationKey(group.getId(), bdayPerson.getEmail(), occurrenceDate, recipientEmail);
	}

	/**
	 * @return Day represented by given calendar instance
	 */
	private LocalDate toLocalDate(Calendar calendar) {
		return LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * Deliver reminder about the upcoming birthday of bdayPerson to given
	 * emails. Package-private so tests can intercept the delivery.
	 *
	 * @param bdayPerson
	 *            Person whose birthday is coming up soon
	 * @param recipientEmails
	 *            Emails of people to deliver notification email
	 * @param daysLeft
	 *            Number of days left till the birthday
	 */
	void deliver(Person bdayPerson, List<String> recipientEmails, int daysLeft) {
		sendEmail(bdayPerson, recipientEmails, daysLeft);
	}

	/**
	 * Send reminder email to all given emails about the upcoming birthday of
	 * bdayPerson
	 *
	 * @param bdayPerson
	 *            Person whose birthday is coming up soon
	 * @param emails
	 *            Emails of people to deliver notification email
	 * @param daysLeft
	 *            Number of days left till the birthday
	 */
	private void sendEmail(Person bdayPerson, List<String> emails, int daysLeft) {
		Email email = prepareEmail(bdayPerson, emails, daysLeft);
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
	private Email prepareEmail(Person bdayPerson, List<String> emails, int daysLeft) {
		String subject = bdayPerson.getName() + "s' Birthday in " + daysLeft + " days !";

		String body = "<p>Hey folks,<br/><br/>It's <b>" + bdayPerson.getName() + "</b>s' birthday on <b>"
				+ bdayPerson.getBirthday().getDayOfMonth() + ", " + bdayPerson.getBirthday().getMonth().name()
				+ "</b>. <br/><br/>" + "Let's discuss what we can do to make it memorable and fun.<br/><br/>"
				+ "<p><i>PS : This is an auto-generated mail. Please ignore if this is not relevant for you.</i><br/><br/>"
				+ "<p>--<br/>Thanks & Regards,<br/>Birthday Reminder Daemon!<br/>";

		return new Email(subject, body, emails);
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
	 * Identifies a reminder sent to a recipient about one occurrence of a
	 * persons' birthday, in a group
	 *
	 * @author satish
	 */
	private static final class NotificationKey {
		private final GroupID groupID;
		private final String bdayPersonEmail;
		private final LocalDate occurrenceDate;
		private final String recipientEmail;

		NotificationKey(GroupID groupID, String bdayPersonEmail, LocalDate occurrenceDate, String recipientEmail) {
			this.groupID = groupID;
			this.bdayPersonEmail = bdayPersonEmail;
			this.occurrenceDate = occurrenceDate;
			this.recipientEmail = recipientEmail;
		}

		@Override
		public int hashCode() {
			return Objects.hash(groupID, bdayPersonEmail, occurrenceDate, recipientEmail);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof NotificationKey))
				return false;
			NotificationKey other = (NotificationKey) obj;
			return Objects.equals(groupID, other.groupID) && Objects.equals(bdayPersonEmail, other.bdayPersonEmail)
					&& Objects.equals(occurrenceDate, other.occurrenceDate)
					&& Objects.equals(recipientEmail, other.recipientEmail);
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

	/**
	 * Find all the people with upcoming birthdays
	 * @return	all the people with upcoming birthdays
	 */
	public Map<GroupDetails, List<Person>> findPeopleWithUpcomingBirthday() {
		List<Group> groups = groupService.findGroups();
		int defaultReminderConfig = configuration.findDefaultReminderPeriod();
		
		Map<GroupDetails, List<Person>> personsMap = new HashMap<>();
		
		for(Group group : groups) {
			int birthDayReminderDays = group.getReminderPeriodInDays() != -1 ? group.getReminderPeriodInDays() : defaultReminderConfig;
			for(Person person : group.getMembers()) {
				if(isPersonBirthdayWithInReminderPeriod(person, birthDayReminderDays)) {
					List<Person> groupMembers = personsMap.get(group);
					if(groupMembers == null) {
						groupMembers = new ArrayList<>();
						personsMap.put(group, groupMembers);
					}
					groupMembers.add(person);
				}
			}
		}
		
		return personsMap;
	}

	/**
	 * Find out whether it's given persons' birthday in less than <code>reminderPeriodInDays</code> days.
	 * 
	 * @param person	Person of interest
	 * @param reminderPeriodInDays	Number of days in advance to the persons' birthday
	 *
	 * @return <code>true</code>, if it's given persons' birthday in less than
	 *         <code>reminderPeriodInDays</code> days. <code>false</code>,
	 *         otherwise
	 */
	private boolean isPersonBirthdayWithInReminderPeriod(Person person, int reminderPeriodInDays) {
		int daysLeftTillBDay = daysUntilBirthday(person, Calendar.getInstance());
		return daysLeftTillBDay >= 0 && daysLeftTillBDay <= reminderPeriodInDays;
	}

	/**
	 * Find the number of days left till the next occurrence of given persons'
	 * birthday, this year or the next.
	 *
	 * @param person
	 *            Person of interest
	 * @param today
	 *            The day to count from
	 * @return number of days left till the birthday, <code>-1</code> if the
	 *         birthday is today
	 */
	int daysUntilBirthday(Person person, Calendar today) {
		LocalDate todayDate = toLocalDate(today);

		LocalDate birthday = birthdayInYear(person, todayDate.getYear());
		if (birthday.equals(todayDate))
			return -1;

		if (birthday.isBefore(todayDate))
			birthday = birthdayInYear(person, todayDate.getYear() + 1);

		return (int) ChronoUnit.DAYS.between(todayDate, birthday);
	}

	/**
	 * Find given persons' birthday in given year. A day beyond the end of the
	 * month rolls over to the next month, so 29th February is 1st March in a
	 * non-leap year.
	 *
	 * @param person
	 *            Person of interest
	 * @param year
	 *            Year of interest
	 * @return given persons' birthday in given year
	 */
	private LocalDate birthdayInYear(Person person, int year) {
		return LocalDate.of(year, person.getBirthday().getMonth().ordinal() + 1, 1)
				.plusDays(person.getBirthday().getDayOfMonth() - 1);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (!isShutdown) {
			sendNotification(Calendar.getInstance());

			try {
				Thread.sleep(1000 * 60 * 15);// sleep for 15 minutes and send
												// reminder
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
