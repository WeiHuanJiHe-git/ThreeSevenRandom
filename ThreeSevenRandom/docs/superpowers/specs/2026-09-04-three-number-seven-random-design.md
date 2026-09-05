# Three Number Seven Random — Design

## Goal
Create a tiny native Android app that launches immediately, generates three cryptographically strong random integers, and shows quotient/remainder information only when a value is greater than seven.

## Rules
- Range: 1–999 inclusive, uniformly sampled and centralized as constants.
- Launch performs one roll automatically.
- Tapping anywhere performs another roll.
- For n <= 7, quotient and remainder display `-`.
- For n > 7, quotient = n / 7 and remainder = n % 7.
- Standard modulo applies: 14 => quotient 2, remainder 0.

## Architecture
- `RandomEngine`: pure-Java randomness and arithmetic, independently testable without Android SDK.
- `MainActivity`: programmatic Android Views only; no XML layout, Compose, AndroidX, network, persistence, or background work.
- One activity, one screen, no runtime permissions.

## Randomness
Use `SecureRandom` in production. Do not falsely claim mathematical/physical true randomness; Android's OS-backed CSPRNG is the practical high-quality source available to an ordinary app without external hardware.

## Performance
No startup I/O, network, database, image decode, dependency injection, or asynchronous initialization. UI is created synchronously from platform widgets.
