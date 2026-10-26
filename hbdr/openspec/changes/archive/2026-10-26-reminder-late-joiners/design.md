# Design

## Context

See proposal.md (Why) for the problem. The current state of `NotificationService` that shapes the approach:

- `run()` loops every 15 minutes and calls `sendNotification()`. That method skips any group whose entry in `lastReminderRunDate` equals today; otherwise it calls `runReminder(group)` and records today.
- `runReminder` sends one email per birthday person P with `isPersonBirthdaySoon(P, N) == true` (`daysLeft == N`) to every other member. The subject and body use `N`.
- `isPersonBirthdayWithInReminderPeriod` already implements the window test (`0 <= daysLeft <= N`, with a "today is before the birthday" guard, so in practice days 1..N). Only `findPeopleWithUpcomingBirthday` uses it. Both checks repeat the same `Calendar` arithmetic and call `Calendar.getInstance()` internally.
- `GroupService.findGroups()` rebuilds `Group` objects from an in-memory map on every call, so membership changes are visible on the next tick. The service is a Guice singleton started by `NotificationServiceContextListener`.
- Delivery goes straight to `Transport.send` inside the private `sendEmail`, and its errors are logged and swallowed. There are no tests in the project yet. JUnit 3.8.1 is available, and the code targets Java 8.

## Goals / Non-Goals

**Goals:**
- Meet the `birthday-reminders` spec with changes confined to `NotificationService`.
- Make the per-tick logic deterministic and unit-testable without a mail server or the real clock.

**Non-Goals:**
- Changing the birthday date arithmetic, including the year-boundary bug. The existing arithmetic is extracted as-is, not corrected.
- Persisting the ledger, adding retry or bounce handling, or changing the email template beyond the day count.
- Changing `findPeopleWithUpcomingBirthday` output.

## Decisions

### 1. An in-memory ledger replaces the per-group day flag
A thread-safe `Set<NotificationKey>` (`ConcurrentHashMap.newKeySet()`) replaces `lastReminderRunDate`. `NotificationKey` is a small private immutable value class with `equals`/`hashCode` over:

```
(GroupID groupId, String birthdayPersonEmail, LocalDate occurrenceDate, String recipientEmail)
```

- People are identified by email. `GroupServiceImpl` already treats email as the user ID (`new UserID(person.getEmail())`), and `Person.equals` also includes the name, which could change.
- `occurrenceDate = today + daysLeft` (a `java.time.LocalDate`, used only as a key). Each year's birthday gets a new key, which satisfies "next year's occurrence" without any reset logic.
- *Alternative considered:* keep a per-group flag and add a "members seen" set. Rejected: it can't express "notified about P's birthday but not Q's", and it still depends on the exact-day trigger.

### 2. One unified check per tick, with no special case for day N
For each group, each member P with `1 <= daysLeft <= N` is processed like this:

```
pending = members - {P} - { r : (group, P, occurrence, r) in ledger }
if pending not empty:
    deliver(P, pending, daysLeft)
    add (group, P, occurrence, r) for each r in pending
```

On day N the ledger is empty for that occurrence, so everyone except P is pending, which matches today's behavior. On later days only new members are pending. A new member's own birthday has no ledger entries, so the other members are pending. These are the late-joiner and new-member scenarios from the spec, with no branching. The group-level "ran today" skip is removed: each tick is cheap and depends only on what is in the ledger.

### 3. Startup seeding on the first tick
On the first call to `sendNotification`, before any sends, the ledger is seeded: for each group and each P with `1 <= daysLeft < N`, every current member except P is added for that occurrence. Day-N occurrences are not seeded, so a service started on day N still sends.

- Seeding runs on the first tick, not in the constructor, so it uses the same `today` and the same `findGroups()` snapshot as the first real check, and the constructor stays side-effect-free.
- A `volatile boolean seeded` guard is enough. Only the notification thread calls `sendNotification`.
- *Alternative considered:* seed every in-window occurrence, including day N. Rejected: a restart on the morning of day N would suppress that day's reminder entirely, which is worse than today's duplicate-on-restart behavior.

### 4. Mark recipients as notified whatever the delivery outcome
`sendEmail` swallows failures. Ledger entries are added after `deliver` returns, whether or not it succeeded. This keeps today's at-most-once behavior (today the group is marked whether or not the send succeeded) and avoids re-sending to the valid recipients of a batch every 15 minutes because one address is bad.

### 5. Prune past occurrences
At the start of each tick, keys with `occurrenceDate` before today are removed (`removeIf`). This bounds the ledger to occurrences currently in a window: roughly (members × birthdays in the window), which is tiny.

### 6. Shared days-left helper with an injectable "today"
The duplicated `Calendar` arithmetic is extracted into one private helper, `int daysUntilBirthday(Person p, Calendar today)`. It returns the days left (1..) when the birthday is later this year within the existing one-month-ahead logic, and `-1` otherwise. The arithmetic is unchanged, and `birthdayC` is cloned from `today` so the existing time-of-day guard still holds.
- `isPersonBirthdayWithInReminderPeriod` delegates to this helper. `isPersonBirthdaySoon` is deleted because nothing uses it any more.
- `sendNotification()` becomes `sendNotification(Calendar today)` (package-private). `run()` passes `Calendar.getInstance()`, and tests pass a fixed date.

### 7. A package-private delivery seam for tests
`remindAboutPersonBirthday`'s send is routed through a package-private method `void deliver(Person birthdayPerson, List<String> recipientEmails, int daysLeft)`, which by default builds the email and calls the existing `sendEmail`. Tests use an anonymous subclass of `NotificationService` that overrides `deliver` and records the calls. `prepareEmail` takes `daysLeft` in place of `reminderPeriodInDays`, so both the subject and the body report the actual days left.
- *Alternative considered:* inject a `Mailer`/sender interface through Guice. Rejected for scope: it changes wiring and `Configuration`, while the override seam stays inside the class.

### 8. Remove `toDateString`
Its only callers belong to the removed per-group flag, so it goes, along with the incorrect `"YYYY-MM-DD"` pattern.

## Risks / Trade-offs

- [The ledger is lost on restart, and members added while the service is down are seeded as already notified] → Accepted limitation, listed in the proposal and covered by a spec scenario.
- [A restart on day N after that day's send re-sends to all] → Same as current behavior. Not mitigated.
- [Marking failed sends as notified means a transient SMTP outage loses that reminder] → Same at-most-once behavior as today. Retries are out of scope.
- [Year-boundary bug: `daysUntilBirthday` returns -1 for windows that cross 31 December] → Unchanged from today and noted as a known issue. Because the helper is now isolated, a later fix is a single-method change.
- [The `deliver` seam is overridable from the same package] → Package-private only, and documented as a test seam.

## Migration Plan

No data or configuration migration. Deploy as a normal WAR redeploy. On first start, the seeding prevents a burst of duplicate reminders for in-progress windows. To roll back, revert and redeploy; the previous version has no state to reconcile.
