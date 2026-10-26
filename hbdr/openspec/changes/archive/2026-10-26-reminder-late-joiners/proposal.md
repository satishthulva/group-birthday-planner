# Proposal

## Why

`NotificationService` runs each group's reminder check at most once per day, tracked by an in-memory per-group flag (`lastReminderRunDate`). A member added to a group after that day's run is not considered until the next day, and because a reminder fires only on the exact day that is `N` days before a birthday (`daysLeft == N`), that member never hears about birthdays that are already inside the reminder window. Late joiners silently miss reminders.

## What Changes

- Replace the per-group, per-day "already ran" flag with an in-memory **notification ledger** that records which recipient has been told about which birthday occurrence in which group.
- Change the reminder trigger from "exactly `N` days before" to "any time within the reminder window (1..`N` days before) for recipients not yet in the ledger". Members present when the reminder first fires are notified as today, and late joiners are notified on the next 15-minute tick.
- Compute the day count in the reminder email ("in X days") from the actual days left, not the fixed `N`.
- When the service starts, seed the ledger for birthday occurrences already partway through their window (`daysLeft < N`) with the current members, so a restart does not re-notify everyone.
- A new member whose own birthday is inside the window is handled by the same path: the rest of the group gets a late reminder about it.
- Remove `lastReminderRunDate` and its `toDateString` helper. This also removes the incorrect `"YYYY-MM-DD"` `SimpleDateFormat` pattern, which formats week-year and day-of-year.

Known and accepted limitations (not addressed):
- Members added while the service is down are treated as already notified at startup and miss reminders for occurrences already partway through their window.
- A restart on day `N` after that day's send re-sends to everyone, the same as current behavior.
- The ledger is not persisted: the database is not wired at runtime, and adding persistence is out of scope.
- The existing birthday date math does not handle windows that cross the year boundary (late December to early January). That is not fixed here.

## Capabilities

### New Capabilities
- `birthday-reminders`: when and to whom the system sends advance birthday reminder emails for members of a friend group, including late joiners and restart behavior.

### Modified Capabilities
<!-- None: no specs exist yet. -->

## Impact

- Code: `com.novice.hbdr.service.impl.NotificationService` only. Its public surface (`run`, `shutdown`, `findPeopleWithUpcomingBirthday`) is unchanged.
- Tests: new unit tests for `NotificationService` under `src/test/java`, written in JUnit 3 `TestCase` style.
- Build: the test-scoped JUnit dependency in `pom.xml` goes from 3.8.1 to 4.13.2, because Maven's default surefire plugin no longer runs JUnit 3.8.1. Nothing outside tests uses JUnit.
- No schema, API or configuration changes, and no other dependency changes.
- Behavior: members can now receive a reminder on a day other than exactly `N` days before the birthday, with a subject such as "in 2 days".
