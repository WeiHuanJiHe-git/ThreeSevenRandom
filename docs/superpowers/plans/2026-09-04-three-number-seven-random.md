# Three Number Seven Random Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a tiny native Android app that displays three secure random numbers plus quotient/remainder rows for values greater than seven.

**Architecture:** Keep arithmetic/random generation in a pure-Java `RandomEngine` and keep the Android layer to one platform `Activity` that creates its UI programmatically. Avoid all external runtime libraries.

**Tech Stack:** Java 17, Android platform Views, SecureRandom, Android Gradle Plugin 8.8.2.

**Spec:** `docs/superpowers/specs/2026-09-04-three-number-seven-random-design.md`

## Global Constraints
- Random range is 1–999 inclusive.
- Exactly three values per roll.
- Only values greater than 7 expose quotient/remainder.
- No AndroidX, Compose, network, database, analytics, or runtime permissions.

---

### Task 1: Rule engine
**Files:** `app/src/main/java/com/fonuhuo/sevenrandom/RandomEngine.java`, `tools/test/com/fonuhuo/sevenrandom/RandomEngineTest.java`
- [x] Write tests for 10, 8, 14, 7, 3 and range guarantees.
- [x] Run tests before implementation and confirm failure because `RandomEngine` is missing.
- [x] Implement `RandomEngine` with production `SecureRandom` and test-only `Random` injection.
- [x] Run the pure-Java tests and confirm they pass.

### Task 2: Android UI
**Files:** `app/src/main/java/com/fonuhuo/sevenrandom/MainActivity.java`, `app/src/main/AndroidManifest.xml`, `app/src/main/res/values/styles.xml`
- [x] Create a single platform Activity with three value columns.
- [x] Generate once on launch and again when the root view is tapped.
- [x] Keep the screen free of network, storage, or other permissions.

### Task 3: Build configuration
**Files:** `settings.gradle`, `build.gradle`, `app/build.gradle`, `gradle.properties`, `app/proguard-rules.pro`
- [x] Configure a dependency-light Android application.
- [x] Enable release shrinking.
- [ ] Build APK in current environment (blocked: Android SDK/Gradle cannot be downloaded because container DNS is unavailable).
