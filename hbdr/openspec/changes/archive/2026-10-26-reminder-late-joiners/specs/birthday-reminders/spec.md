# Spec Delta

## Purpose

Defines when and to whom the system sends advance birthday reminder emails for members of a friend group, so the rest of the group can plan ahead without the birthday person being told.

## ADDED Requirements

### Requirement: Reminder window
A group's reminder window for a member's birthday is the period starting `N` days before the birthday and ending the day before it, where `N` is the group's reminder period, or the system default when the group has none. Reminders about that birthday SHALL be sent only while it is inside this window.

#### Scenario: Birthday enters the window
- **WHEN** a group has reminder period 4 and a member's birthday is exactly 4 days away
- **THEN** a reminder about that birthday is sent

#### Scenario: Birthday not yet in the window
- **WHEN** a group has reminder period 4 and a member's birthday is 5 days away
- **THEN** no reminder about that birthday is sent

#### Scenario: Birthday is today
- **WHEN** a member's birthday is today
- **THEN** no reminder about that birthday is sent

#### Scenario: Group without its own reminder period
- **WHEN** a group has no reminder period set and a member's birthday is exactly the system default number of days away
- **THEN** a reminder about that birthday is sent

### Requirement: Birthday person is excluded
A reminder about a member's birthday SHALL be sent to the group's other members and SHALL NOT be sent to the birthday person.

#### Scenario: Recipients of a reminder
- **WHEN** a reminder is sent about member P's birthday in a group with members P, A and B
- **THEN** A and B receive it and P does not

### Requirement: Each recipient is notified once per birthday occurrence
While the service keeps running, each member SHALL receive at most one reminder for a given group and a given occurrence of another member's birthday.

#### Scenario: Repeated checks on the same day
- **WHEN** the reminder check runs several times on a day when a birthday is inside the window
- **THEN** each recipient receives that reminder only once

#### Scenario: Later days in the window
- **WHEN** a recipient has already received a reminder for a birthday and the check runs again on a later day inside the same window
- **THEN** that recipient does not receive it again

#### Scenario: Next year's occurrence
- **WHEN** a recipient was reminded about a member's birthday last year and the birthday enters the window again this year
- **THEN** the recipient receives a reminder for this year's occurrence

### Requirement: Late joiners receive pending reminders
A member who joins a group while a birthday is inside the reminder window SHALL receive that reminder within one check interval, provided the service was running when they joined.

#### Scenario: Member added after the day's reminder was sent
- **WHEN** a reminder about P's birthday was sent to the group 4 days before it and member M is added to the group later that day
- **THEN** M receives the reminder about P's birthday at the next check, and the existing members do not receive it again

#### Scenario: Member added on a later day in the window
- **WHEN** member M is added to the group 2 days before P's birthday
- **THEN** M receives the reminder about P's birthday at the next check

### Requirement: New member's own birthday inside the window
When a new member's own birthday is already inside the group's reminder window when they join, the group's other members SHALL receive a reminder about it, provided the service was running when the member joined.

#### Scenario: New member with an imminent birthday
- **WHEN** member M, whose birthday is 2 days away, is added to a group with members A and B and the group's reminder period is 4
- **THEN** A and B receive a reminder about M's birthday at the next check, and M does not

### Requirement: Reminder states the actual days remaining
The reminder SHALL name the birthday person and the birthday date, and state the number of days actually remaining until the birthday on the day it is sent.

#### Scenario: Reminder sent at the start of the window
- **WHEN** a reminder is sent 4 days before the birthday
- **THEN** it says the birthday is in 4 days

#### Scenario: Late reminder
- **WHEN** a late joiner receives a reminder 2 days before the birthday
- **THEN** it says the birthday is in 2 days

### Requirement: Restart does not re-notify partway through the window
When the service starts, members already in a group SHALL be treated as already notified about birthdays that are inside the window but fewer than `N` days away. Birthdays exactly `N` days away SHALL be handled as usual.

#### Scenario: Restart in the middle of the window
- **WHEN** the service restarts 2 days before P's birthday, the group's reminder period is 4, and the group's members are unchanged
- **THEN** no reminder about P's birthday is sent to the existing members

#### Scenario: Start on the first day of the window
- **WHEN** the service starts on the day that is exactly 4 days before P's birthday, the group's reminder period is 4, and no reminder has been sent yet that day
- **THEN** the group's members other than P receive the reminder

#### Scenario: Member added while the service was down
- **WHEN** member M is added while the service is stopped and the service starts 2 days before P's birthday, with a reminder period of 4
- **THEN** M does not receive a reminder about P's birthday (accepted limitation)
