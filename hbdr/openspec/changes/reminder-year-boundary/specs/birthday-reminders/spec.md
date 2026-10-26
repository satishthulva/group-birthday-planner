# Spec Delta

## MODIFIED Requirements

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

#### Scenario: Window crosses the year boundary
- **WHEN** a group has reminder period 4, today is 29 December, and a member's birthday is 2 January
- **THEN** a reminder about that birthday is sent, saying it is in 4 days

#### Scenario: Reminder period longer than a month
- **WHEN** a group has reminder period 40 and a member's birthday is exactly 40 days away
- **THEN** a reminder about that birthday is sent, saying it is in 40 days

#### Scenario: 29 February birthday in a non-leap year
- **WHEN** a member's birthday is 29 February, the coming year is not a leap year, and the group has reminder period 4
- **THEN** the birthday is treated as 1 March, and a reminder is sent 4 days before 1 March
