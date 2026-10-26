# Tasks

## 1. Next-occurrence day count

- [x] 1.1 Rewrite `daysUntilBirthday(Person, Calendar today)` in `NotificationService` to count days from `toLocalDate(today)` to the next occurrence. Build the birthday as `LocalDate.of(year, month, 1).plusDays(day - 1)`, use next year when this year's date has passed, return `-1` when it is today, and use `ChronoUnit.DAYS.between` for the count. Update its Javadoc to remove the one-month and year-boundary caveats, and remove the `Month` import if it is now unused. Verify with `mvn -q compile`.
- [x] 1.2 Add tests to `NotificationServiceTest`: `daysUntilBirthday` gives 3 on 30 Dec 2026 for a 2 Jan birthday; 35 for a birthday 35 days ahead; 4 on 25 Feb 2027 for a 29 Feb birthday (treated as 1 Mar 2027); -1 on 1 Mar 2027 for a 29 Feb birthday; and 3 on 26 Feb 2028 for a 29 Feb birthday (leap year). Add a `sendNotification` test: with period 4 on 29 Dec 2026, a 2 Jan birthday produces one delivery with `daysLeft` 4. Add a period-40 group test where a birthday exactly 40 days away is delivered with `daysLeft` 40. Verify `mvn -q test` passes, including all existing tests.

## 2. Integration check

- [x] 2.1 Run `mvn -q package` and confirm the WAR builds and every `NotificationServiceTest` test passes, and that `git diff --stat -- src` shows only `NotificationService.java` and `NotificationServiceTest.java`.

## Workflow follow-up

- Archive the change after review, so the `birthday-reminders` "Reminder window" requirement gains the new scenarios.
