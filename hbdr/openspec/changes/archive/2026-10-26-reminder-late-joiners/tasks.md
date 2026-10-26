# Tasks

## 1. Test seams and shared date helper

- [x] 1.1 Extract the duplicated `Calendar` arithmetic into `daysUntilBirthday(Person, Calendar today)`, with `birthdayC` cloned from `today` and the arithmetic otherwise unchanged. Make `isPersonBirthdayWithInReminderPeriod` delegate to it. Verify with `mvn -q compile`, and confirm `findPeopleWithUpcomingBirthday` still calls `Calendar.getInstance()` and returns the same results.
- [x] 1.2 Add the package-private `deliver(Person birthdayPerson, List<String> recipientEmails, int daysLeft)` seam, which by default builds the email and calls `sendEmail`. Change `prepareEmail` to take `daysLeft` and use it in the subject and body. Verify with `mvn -q compile`.
- [x] 1.3 Upgrade the test-scoped JUnit dependency in `pom.xml` to 4.13.2, then create `src/test/java/com/novice/hbdr/service/impl/NotificationServiceTest.java` (JUnit 3 `TestCase`) with stub `GroupService`/`Configuration` and a recording subclass that overrides `deliver`. Add unit tests for `daysUntilBirthday` covering birthday in N days, N+1 days, today, and next month. Verify `mvn -q test` passes.

## 2. Ledger and unified reminder check

- [x] 2.1 Add the private immutable `NotificationKey` class (groupId, birthdayPersonEmail, occurrenceDate as `LocalDate`, recipientEmail) with `equals`/`hashCode`, and a `ConcurrentHashMap.newKeySet()` ledger field. Verify with `mvn -q compile`.
- [x] 2.2 Replace `sendNotification()` and `runReminder` with `sendNotification(Calendar today)`: prune keys whose occurrence is before today, then for each group and each member P with `1 <= daysLeft <= N` (group period, or the default when -1), deliver to the members other than P who are not in the ledger and record them. Have `run()` pass `Calendar.getInstance()`. Verify with `mvn -q compile`.
- [x] 2.3 Remove `lastReminderRunDate`, `isNotifierRunForDay`, `registerNotifierRunForDay`, `toDateString` and the unused `isPersonBirthdaySoon`. Verify that `grep -n "lastReminderRunDate\|toDateString\|isPersonBirthdaySoon" src/main/java` returns nothing and that `mvn -q compile` succeeds.
- [x] 2.4 Add tests for the spec scenarios: day-N send excludes the birthday person; repeated ticks on the same day send once; a later day in the window does not re-send; a member added after the day-N send gets one reminder with the actual `daysLeft` and the existing members get nothing; a new member whose birthday is 2 days away triggers a reminder to the others only; a birthday N+1 days away or today sends nothing; a group with period -1 uses the configured default; next year's occurrence sends again. Verify `mvn -q test` passes.

## 3. Startup seeding

- [x] 3.1 On the first `sendNotification(today)` call, before any delivery, seed the ledger with all current members except P for every occurrence with `1 <= daysLeft < N`. Guard it with a `volatile boolean seeded`. Verify with `mvn -q compile`.
- [x] 3.2 Add tests: a first tick 2 days before a birthday sends nothing to existing members; a first tick exactly N days before sends to all except P; a member added after the first tick, mid-window, still gets the reminder on the next tick; the accepted gap, where a member present at the first mid-window tick gets nothing. Verify `mvn -q test` passes.

## 4. Integration check

- [x] 4.1 Run `mvn -q package` and confirm that the WAR builds and all `NotificationServiceTest` tests pass, and that `NotificationService`'s public surface (`run`, `shutdown`, `findPeopleWithUpcomingBirthday`) and the `EngineModule` wiring are unchanged (`git diff --stat` shows only `NotificationService.java`, the new test file under `src/`, and the JUnit version in `pom.xml`).

## Workflow follow-up

- Archive the change after review, so that `openspec/specs/birthday-reminders/spec.md` is created.
- Track the year-boundary birthday-math bug as a separate change.
