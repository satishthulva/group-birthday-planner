# Design

## Context

`NotificationService.daysUntilBirthday(Person, Calendar today)` is the only place that computes days until a birthday. Its callers are the reminder check (`sendNotification` and the startup seeding) and `isPersonBirthdayWithInReminderPeriod`, which backs `findPeopleWithUpcomingBirthday`. It clones `today`, sets the birthday's month and day in the **same year**, returns `-1` unless that is strictly after `today`, and returns `-1` when the birthday is more than one calendar month ahead. The `Calendar` instances are lenient, so a 29 February birthday in a non-leap year rolls to 1 March. See proposal.md for why this needs fixing.

## Goals / Non-Goals

**Goals:**
- `daysUntilBirthday` returns the exact number of days to the next occurrence, whether that is in this year or next, with no limit on how far ahead it is.
- Keep the method's signature and the `-1` meaning "birthday is today" unchanged, so callers don't change.

**Non-Goals:**
- Any wider move to `java.time` (`run()` still passes `Calendar.getInstance()`, and `Person`/`Birthday` stay as they are).
- Changing how a 29 February birthday is treated, or checking birthday data for days that don't exist.

## Decisions

### 1. Count calendar days with `java.time`
Convert `today` to a `LocalDate` with the existing `toLocalDate` helper, build the birthday in `today`'s year, and if it's not after today, use next year instead (unless it is today, which returns `-1`). The result is `ChronoUnit.DAYS.between(todayDate, next)`.

```
next = birthdayIn(year)
if next == todayDate        -> -1
if next before todayDate    -> next = birthdayIn(year + 1)
return DAYS.between(todayDate, next)
```

- Comparing dates rather than timestamps removes the hidden reliance on both `Calendar`s having the same time of day.
- *Alternative considered:* keep the `Calendar` arithmetic and add a year-wrap branch. Rejected: the month-by-month day counting (`monthDiff`, `Month.getNumberOfDaysInMonth`) is the source of both limits. A plain day count is shorter and has no limits to work around.

### 2. Keep the existing handling of days that don't exist in a year
`birthdayIn(year)` is `LocalDate.of(year, month, 1).plusDays(dayOfMonth - 1)`. This rolls over exactly as the lenient `Calendar.set` did, so 29 February in a non-leap year becomes 1 March, and the code needs no leap-year special case.
- *Alternative considered:* `MonthDay.atYear`, which clamps 29 February to 28 February. Rejected: it changes which day those members are reminded about, which is out of scope.

### 3. Drop the one-month limit
With an exact day count, the `monthDiff > 1` cut-off and its Javadoc caveat ("won't be more than 27 days") go away. Each caller still compares the result with its own reminder period, so the window doesn't widen beyond that period.

## Risks / Trade-offs

- [`findPeopleWithUpcomingBirthday` now lists early-January birthdays in late December, and honours reminder periods longer than a month] → This is intended and matches the spec. No caller depends on the old cut-off.
- [`Month.getNumberOfDaysInMonth` may no longer be used in `NotificationService`] → Drop the now-unused `Month` import. The enum itself stays, because other code uses it.
- [Ledger occurrence dates near the year end] → `occurrenceDate = today + daysLeft` already lands in the next year correctly, so no ledger change is needed.
