---
layout: page
title: TutBook
---

[![CI Status](https://github.com/AY2526S1-CS2103T-W09-3/tp/workflows/Java%20CI/badge.svg)](https://github.com/AY2526S1-CS2103T-W09-3/tp/actions)
[![codecov](https://codecov.io/gh/AY2526S1-CS2103T-W09-3/tp/branch/master/graph/badge.svg)](https://app.codecov.io/gh/AY2526S1-CS2103T-W09-3/tp)

![Ui](images/Ui.png)

**TutBook is a desktop app for managing tuition-centre contacts, classes, and sessions.**
It’s optimised for fast keyboard-driven workflows via a CLI, with a supportive JavaFX GUI.

---

## Get started

- **Users:** See the [_Quick Start_ section of the User Guide](UserGuide.html#quick-start).
- **Developers:** Start with the [Developer Guide](DeveloperGuide.html).

**Shortcuts**

- [Releases](https://github.com/AY2526S1-CS2103T-W09-3/tp/releases)
- [Issue Tracker](https://github.com/AY2526S1-CS2103T-W09-3/tp/issues)
- [CI](https://github.com/AY2526S1-CS2103T-W09-3/tp/actions) • [Coverage](https://app.codecov.io/gh/AY2526S1-CS2103T-W09-3/tp)

---

## Key features (CLI-first)

- **Contacts with roles:** Students, Tutors, Parents in one place
  - Filter and search quickly (`filter ro/STUDENT`, `find Alice`)
  - Link parents ↔ students for family views
- **Classes:** Create, rename, delete; assign tutors; enrol students
  - List all classes and rosters at a glance
  - **Remove from class:** `unjoin n/NAME c/CLASS`
- **Sessions:** Per-class schedule with attendance
  - Add/delete sessions with date/time and optional location
  - **List sessions:** `listsessions c/CLASS`
  - **View a session:** `viewsession c/CLASS s/SESSION`
  - Mark attendance per session for enrolled students
- **Focused views:**
  - **List class students:** `liststudents c/CLASS`
  - **List parents** globally or for a child

> See the [User Guide](UserGuide.html#quick-start) for full command syntax, examples, and edge cases.

---

## Why TutBook?

- **Speed:** Keyboard-first commands outperform point-and-click for routine admin
- **Clarity:** Role chips and structured class/session views keep context visible
- **Safety:** JSON persistence with validation and relationship clean-up on deletes

---

## Acknowledgements

- Built atop the SE-EDU AB3 codebase and project structure
- Libraries: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)
- Icons: [Material Design Icons](https://materialdesignicons.com/)
- Diagram styling: [PlantUML Standard Library](https://plantuml.com/stdlib)
