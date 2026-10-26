# Proposal

## Why

Near the end of the year, reminders for birthdays in early January are never sent. The days-left calculation places the birthday in the current calendar year, so on 30 December a 2 January birthday looks like it has already passed. The same calculation also gives up on birthdays more than one calendar month ahead. Together these break the `birthday-reminders` "Reminder window" requirement whenever the window crosses 31 December.

## What Changes

- Count the days left until a member's **next** birthday: this year's date if it hasn't happened yet, otherwise next year's. Before, only birthdays later in the current year, at most one month ahead, were counted.
- A birthday on the current day still counts as "not upcoming", so no reminder is sent on the day itself.
- A 29 February birthday in a non-leap year is still treated as 1 March, as it is today.
- The fix applies to both reminder sending and the upcoming-birthdays listing (`findPeopleWithUpcomingBirthday`), since both use the same calculation.
- The `birthday-reminders` spec gains year-boundary and leap-day scenarios under "Reminder window".

## Capabilities

### New Capabilities
<!-- None -->

### Modified Capabilities
- `birthday-reminders`: "Reminder window" gains scenarios saying the window works across the year boundary and saying how 29 February birthdays are handled in non-leap years.

## Impact

- Code: `com.novice.hbdr.service.impl.NotificationService` only (the `daysUntilBirthday` helper and its Javadoc).
- Tests: `NotificationServiceTest` gains year-boundary, longer-than-a-month and leap-day cases.
- Behavior: late-December reminders for early-January birthdays now go out. The upcoming-birthdays endpoint also lists them. Reminder periods longer than about a month now work, where before they were silently cut short.
- No API, schema, configuration or dependency changes.
