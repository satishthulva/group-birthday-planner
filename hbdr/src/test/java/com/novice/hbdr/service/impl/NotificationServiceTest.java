package com.novice.hbdr.service.impl;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.novice.hbdr.Configuration;
import com.novice.hbdr.Mailer;
import com.novice.hbdr.datamodels.Birthday;
import com.novice.hbdr.datamodels.Gender;
import com.novice.hbdr.datamodels.Group;
import com.novice.hbdr.datamodels.GroupDetails;
import com.novice.hbdr.datamodels.GroupID;
import com.novice.hbdr.datamodels.Month;
import com.novice.hbdr.datamodels.Person;
import com.novice.hbdr.datamodels.UserID;
import com.novice.hbdr.service.GroupService;

/**
 * Tests for {@link NotificationService}
 *
 * @author satish
 */
public class NotificationServiceTest extends TestCase {

	private static final int DEFAULT_REMINDER_PERIOD = 4;

	private StubGroupService groupService;

	private RecordingNotificationService service;

	@Override
	protected void setUp() {
		groupService = new StubGroupService();
		service = new RecordingNotificationService(groupService, new StubConfiguration());
	}

	public void testDaysUntilBirthdayInReminderPeriod() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		assertEquals(4, service.daysUntilBirthday(personWithBirthdayIn("p", today, 4), today));
	}

	public void testDaysUntilBirthdayJustOutsideReminderPeriod() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		assertEquals(5, service.daysUntilBirthday(personWithBirthdayIn("p", today, 5), today));
	}

	public void testDaysUntilBirthdayToday() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		assertEquals(-1, service.daysUntilBirthday(personWithBirthdayIn("p", today, 0), today));
	}

	public void testDaysUntilBirthdayNextMonth() {
		Calendar today = day(2026, Calendar.OCTOBER, 30);
		assertEquals(3, service.daysUntilBirthday(personWithBirthdayIn("p", today, 3), today));
	}

	public void testDaysUntilBirthdayInNextYear() {
		Calendar today = day(2026, Calendar.DECEMBER, 30);
		assertEquals(3, service.daysUntilBirthday(birthdayOn("p", 2, Month.January), today));
	}

	public void testDaysUntilBirthdayMoreThanAMonthAhead() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		assertEquals(35, service.daysUntilBirthday(personWithBirthdayIn("p", today, 35), today));
	}

	public void testDaysUntilLeapDayBirthdayInNonLeapYearCountsToFirstOfMarch() {
		Calendar today = day(2027, Calendar.FEBRUARY, 25);
		assertEquals(4, service.daysUntilBirthday(birthdayOn("p", 29, Month.February), today));
	}

	public void testDaysUntilLeapDayBirthdayOnFirstOfMarchInNonLeapYear() {
		Calendar today = day(2027, Calendar.MARCH, 1);
		assertEquals(-1, service.daysUntilBirthday(birthdayOn("p", 29, Month.February), today));
	}

	public void testDaysUntilLeapDayBirthdayInLeapYear() {
		Calendar today = day(2028, Calendar.FEBRUARY, 26);
		assertEquals(3, service.daysUntilBirthday(birthdayOn("p", 29, Month.February), today));
	}

	public void testRemindsAboutBirthdayAcrossYearBoundary() {
		Calendar today = day(2026, Calendar.DECEMBER, 29);
		groupService.setGroups(group("g", 4, birthdayOn("p", 2, Month.January), person("a")));

		service.sendNotification(today);

		assertEquals(1, service.deliveries.size());
		assertDelivery(service.deliveries.get(0), "p", 4, "a");
	}

	public void testRemindsWithReminderPeriodLongerThanAMonth() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		groupService.setGroups(group("g", 40, personWithBirthdayIn("p", today, 40), person("a")));

		service.sendNotification(today);

		assertEquals(1, service.deliveries.size());
		assertDelivery(service.deliveries.get(0), "p", 40, "a");
	}

	public void testRemindsEveryoneButBirthdayPersonOnFirstDayOfWindow() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		Person p = personWithBirthdayIn("p", today, 4);
		groupService.setGroups(group("g", 4, p, person("a"), person("b")));

		service.sendNotification(today);

		assertEquals(1, service.deliveries.size());
		assertDelivery(service.deliveries.get(0), "p", 4, "a", "b");
	}

	public void testRemindsOnlyOnceOnSameDay() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		groupService.setGroups(group("g", 4, personWithBirthdayIn("p", today, 4), person("a")));

		service.sendNotification(today);
		service.sendNotification(today);

		assertEquals(1, service.deliveries.size());
	}

	public void testDoesNotRemindAgainOnLaterDayInWindow() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		groupService.setGroups(group("g", 4, personWithBirthdayIn("p", today, 4), person("a")));

		service.sendNotification(today);
		service.sendNotification(plusDays(today, 1));

		assertEquals(1, service.deliveries.size());
	}

	public void testRemindsMemberAddedAfterReminderWasSent() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		Person p = personWithBirthdayIn("p", today, 4);
		groupService.setGroups(group("g", 4, p, person("a")));
		service.sendNotification(today);

		groupService.setGroups(group("g", 4, p, person("a"), person("m")));
		service.sendNotification(today);
		service.sendNotification(plusDays(today, 1));

		assertEquals(2, service.deliveries.size());
		assertDelivery(service.deliveries.get(1), "p", 4, "m");
	}

	public void testRemindsMemberAddedOnLaterDayInWindowWithActualDaysLeft() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		Person p = personWithBirthdayIn("p", today, 4);
		groupService.setGroups(group("g", 4, p, person("a")));
		service.sendNotification(today);

		groupService.setGroups(group("g", 4, p, person("a"), person("m")));
		service.sendNotification(plusDays(today, 2));

		assertEquals(2, service.deliveries.size());
		assertDelivery(service.deliveries.get(1), "p", 2, "m");
	}

	public void testRemindsOthersAboutNewMembersBirthdayWithinWindow() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		groupService.setGroups(group("g", 4, person("a"), person("b")));
		service.sendNotification(today);

		groupService.setGroups(group("g", 4, person("a"), person("b"), personWithBirthdayIn("m", today, 2)));
		service.sendNotification(today);

		assertEquals(1, service.deliveries.size());
		assertDelivery(service.deliveries.get(0), "m", 2, "a", "b");
	}

	public void testDoesNotRemindAboutBirthdayOutsideWindow() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		groupService.setGroups(group("g", 4, personWithBirthdayIn("p", today, 5),
				personWithBirthdayIn("q", today, 0), person("a")));

		service.sendNotification(today);

		assertEquals(0, service.deliveries.size());
	}

	public void testUsesDefaultReminderPeriodWhenGroupHasNone() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		groupService.setGroups(group("g", -1, personWithBirthdayIn("p", today, DEFAULT_REMINDER_PERIOD),
				personWithBirthdayIn("q", today, DEFAULT_REMINDER_PERIOD + 1), person("a")));

		service.sendNotification(today);

		assertEquals(1, service.deliveries.size());
		assertDelivery(service.deliveries.get(0), "p", DEFAULT_REMINDER_PERIOD, "a", "q");
	}

	public void testRemindsAgainForNextYearsBirthday() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		groupService.setGroups(group("g", 4, personWithBirthdayIn("p", today, 4), person("a")));

		service.sendNotification(today);
		service.sendNotification(day(2027, Calendar.OCTOBER, 10));

		assertEquals(2, service.deliveries.size());
		assertDelivery(service.deliveries.get(1), "p", 4, "a");
	}

	public void testFirstCheckMidWindowDoesNotRemindExistingMembers() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		groupService.setGroups(group("g", 4, personWithBirthdayIn("p", today, 2), person("a"), person("b")));

		service.sendNotification(today);

		assertEquals(0, service.deliveries.size());
	}

	public void testFirstCheckOnFirstDayOfWindowRemindsEveryoneButBirthdayPerson() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		groupService.setGroups(group("g", 4, personWithBirthdayIn("p", today, 4), person("a"), person("b")));

		service.sendNotification(today);

		assertEquals(1, service.deliveries.size());
		assertDelivery(service.deliveries.get(0), "p", 4, "a", "b");
	}

	public void testRemindsMemberAddedAfterFirstCheckMidWindow() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		Person p = personWithBirthdayIn("p", today, 2);
		groupService.setGroups(group("g", 4, p, person("a")));
		service.sendNotification(today);

		groupService.setGroups(group("g", 4, p, person("a"), person("m")));
		service.sendNotification(today);

		assertEquals(1, service.deliveries.size());
		assertDelivery(service.deliveries.get(0), "p", 2, "m");
	}

	public void testMemberAddedWhileServiceWasDownIsNotReminded() {
		Calendar today = day(2026, Calendar.OCTOBER, 10);
		// "m" joined while the service was down, so is already a member at the first check
		groupService.setGroups(group("g", 4, personWithBirthdayIn("p", today, 2), person("a"), person("m")));

		service.sendNotification(today);
		service.sendNotification(plusDays(today, 1));

		assertEquals(0, service.deliveries.size());
	}

	/**
	 * Assert the delivery is about given persons' birthday, made to given recipients
	 */
	private static void assertDelivery(Delivery delivery, String bdayPersonName, int daysLeft, String... recipientNames) {
		assertEquals(bdayPersonName + "@example.com", delivery.bdayPersonEmail);
		assertEquals(daysLeft, delivery.daysLeft);

		List<String> expected = new ArrayList<>();
		for (String name : recipientNames)
			expected.add(name + "@example.com");

		List<String> actual = new ArrayList<>(delivery.recipientEmails);
		Collections.sort(expected);
		Collections.sort(actual);
		assertEquals(expected, actual);
	}

	/**
	 * @return group with given id and members
	 */
	private static Group group(String id, int reminderPeriodInDays, Person... members) {
		return new Group(new GroupID(id), id, reminderPeriodInDays, new ArrayList<>(Arrays.asList(members)));
	}

	/**
	 * @return calendar instance representing noon of given day
	 */
	private static Calendar day(int year, int month, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month, dayOfMonth, 12, 0, 0);
		return calendar;
	}

	/**
	 * @return calendar instance representing given number of days after given day
	 */
	private static Calendar plusDays(Calendar day, int days) {
		Calendar calendar = (Calendar) day.clone();
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar;
	}

	/**
	 * @return person with given name, whose birthday is given number of days after given day
	 */
	private static Person personWithBirthdayIn(String name, Calendar today, int days) {
		Calendar birthday = plusDays(today, days);
		return new Person(name, name, new Birthday(birthday.get(Calendar.DAY_OF_MONTH),
				Month.values()[birthday.get(Calendar.MONTH)]), name + "@example.com", Gender.UNKNOWN);
	}

	/**
	 * @return person with given name, whose birthday is on given day of month
	 */
	private static Person birthdayOn(String name, int dayOfMonth, Month month) {
		return new Person(name, name, new Birthday(dayOfMonth, month), name + "@example.com", Gender.UNKNOWN);
	}

	/**
	 * @return person with given name, whose birthday is far from any day used in these tests
	 */
	private static Person person(String name) {
		return new Person(name, name, new Birthday(15, Month.March), name + "@example.com", Gender.UNKNOWN);
	}

	/**
	 * A single delivery made by the notification service
	 */
	private static class Delivery {
		private final String bdayPersonEmail;
		private final List<String> recipientEmails;
		private final int daysLeft;

		Delivery(String bdayPersonEmail, List<String> recipientEmails, int daysLeft) {
			this.bdayPersonEmail = bdayPersonEmail;
			this.recipientEmails = recipientEmails;
			this.daysLeft = daysLeft;
		}
	}

	/**
	 * Notification service which records deliveries instead of sending emails
	 */
	private static class RecordingNotificationService extends NotificationService {
		private final List<Delivery> deliveries = new ArrayList<>();

		RecordingNotificationService(GroupService groupService, Configuration configuration) {
			super(groupService, configuration);
		}

		@Override
		void deliver(Person bdayPerson, List<String> recipientEmails, int daysLeft) {
			deliveries.add(new Delivery(bdayPerson.getEmail(), new ArrayList<>(recipientEmails), daysLeft));
		}
	}

	/**
	 * Group service backed by a mutable list of groups
	 */
	private static class StubGroupService implements GroupService {
		private final List<Group> groups = new ArrayList<>();

		void setGroups(Group... groups) {
			this.groups.clear();
			this.groups.addAll(Arrays.asList(groups));
		}

		@Override
		public GroupID registerGroup(String name, List<UserID> members, int reminderPeriodInDays) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Group findGroup(GroupID groupID) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<Group> findGroups() {
			return new ArrayList<>(groups);
		}

		@Override
		public List<GroupDetails> findGroups(UserID userID) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Configuration providing only the default reminder period
	 */
	private static class StubConfiguration implements Configuration {
		@Override
		public File findStorageRoot() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Mailer getMailer() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int findDefaultReminderPeriod() {
			return DEFAULT_REMINDER_PERIOD;
		}

		@Override
		public Connection getDatabaseConnection() {
			throw new UnsupportedOperationException();
		}
	}
}
