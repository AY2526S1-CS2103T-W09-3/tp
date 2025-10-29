---
layout: page
title: User Guide
---

# TutBook User Guide

TutBook helps tuition centre administrators **manage students, tutors, parents, and classes efficiently**.
It combines the speed of a Command Line Interface (CLI) with the convenience of a Graphical User Interface (GUI).

This guide will help you **set up TutBook**, understand its features, and make the most out of its commands — even if you are new to CLI-based applications.

* Table of Contents
{:toc}

---

## About This Guide

This User Guide is written for **tuition centre admins** with minimal technical background.
It covers how to install TutBook, perform common tasks, and troubleshoot issues.

### Conventions Used

* Commands are shown in `monospace`.
* Parameters are in `UPPER_CASE` (e.g., `add n/NAME`).
* Square brackets `[ ]` mark optional elements.
* Ellipsis `…` means the parameter can appear multiple times.
* Tips appear in blue boxes, and cautions in yellow boxes.

---

## Getting Started

Follow these steps to set up TutBook on your computer.

1. **Install Java 17 or above.**
 Download it [here](https://www.java.com/en/download/help/download_options.html).
 **Mac users:** Follow [this guide](https://se-education.org/guides/tutorials/javaInstallationMac.html) for the correct JDK setup.

2. **Download TutBook.**
 Get the latest `.jar` file from the [Releases page](https://github.com/AY2526S1-CS2103T-W09-3/tp/releases).

3. **Set up your TutBook folder.**
 Copy the `.jar` file into a folder of your choice. This will be your TutBook **home folder**.

4. **Run the application.**
 Open a command terminal, `cd` into the folder, and run:

 ```
 java -jar tutbook.jar
 ```

 A window like the one below should appear:
 ![UI](images/Ui.png)

5. **Try a few example commands.**
 * `help` — Opens the help window.
 * `list` — Lists all contacts.
 * `add n/John Doe p/98765432 e/johnd@example.com a/John Street ro/student` — Adds a student.
 * `delete 3` — Deletes the 3rd contact.
 * `exit` — Exits the app.

6. **Refer to [Features](#features)** for details on each command.

---

## User Interface Overview

 ![UI Overview](images/AnnotatedUi.png)

This is the main interface of TutBook. It consists of:

* People List
* Class/Session List
* This section switches intelligently with your command
* Command box
* This is where you enter commands to interact with TutBook

---

## Features

### Notes on Command Format

<div markdown="block" class="alert alert-info">

**Command syntax:**

* `UPPER_CASE` → parameters (e.g. `add n/NAME`)
* `[ ]` → optional items
* `…` → can appear multiple times
* Parameters can be in any order
* Extra parameters for non-parameter commands are ignored

</div>

---

### Viewing Help — `help`

Use this command to view instructions within the app.

**Format:** `help`
![help message](images/helpMessageNew.png)

---

### Adding a Person — `add`

Add a student, tutor, or parent to your contact list.

**Format:**

```
add n/NAME p/PHONE e/EMAIL a/ADDRESS ro/PERSON_TYPE [t/TAG]…
```

**Parameters:**

* `PERSON_TYPE`: `student`, `tutor`, or `parent`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags, including none.
</div>

**Examples:**

* `add n/Ms Lim p/91234567 e/mslim@example.com a/Clementi Ave 2 ro/tutor t/experienced`
* `add n/Yi Hao p/98765432 e/johnd@example.com a/31 John Street ro/student`

![add command](images/addCommand.png)

---

### Listing All Persons — `list`

Displays all persons currently stored in TutBook.

**Format:** `list`
![list command](images/listCommand.png)

---

### Filtering by Role — `filter`

Focus on a specific group, such as only students or tutors.

**Format:**

```
filter ro/PERSON_TYPE
```

**Example:**
`filter ro/tutor` shows only tutors.
`filter ro/student` shows only students.

<div markdown="span" class="alert alert-warning">

:exclamation: **Caution:**
Commands like `edit` and `delete` operate on the index of the **currently
displayed list**. After using `filter`, the indexes will change.
For example, `delete 1` will delete the first person in the *filtered list*, not
the first person in the main address book.

</div>

![filter command](images/filterCommand.png)

---

### Editing a Person — `edit`

Update contact information for an existing person.

**Format:**

```
edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…
```

**Examples:**

* `edit 1 p/87438807 e/alex@gmail.com`
* `edit 2 n/Betsy Crower t/`

**Notes:**

* This command edits the person at the specified `INDEX`. The index refers to
the index number shown in the displayed person list. The index **must be a
positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* When editing tags, the existing tags of the person will be removed i.e adding
of tags is not cumulative.
* You can remove all the person’s tags by typing `t/` without
specifying any tags after it.

![edit command](images/editCommand.png)

---

### Finding a Person — `find`

Search for persons by name.

**Format:** `find KEYWORD [MORE_KEYWORDS]`

* Case-insensitive
* Matches full words
* Returns results containing any keyword

**Example:**
`find David Damian` returns `David Li` and `Damian`.

![find command](images/findCommand.png)

---

### Deleting a Person — `delete`

Remove a person from your contact list.

**Format:** `delete INDEX`

**Example:**
`list` followed by `delete 2` deletes the 2nd listed person.

`find Betsy` followed by `delete 1` deletes the 1st person in the results of the
`find` command.

---

### Linking Parent and Child — `link`

Connects a parent to a student.

**Format:**

```
link parent/PARENT_NAME child/CHILD_NAME
```

* Both parent and child must already exist in the address book
* The parent must have the role `parent` and the child must have the role `student`
* Names are case-sensitive

**Example:**
`link parent/Bernice Yu child/Damian`

![link command](images/linkCommand.png)

### Listing children : `childrenof`

Lists children of a specific parent.

Format: `childrenof n/PARENT_NAME`

* Displays all students linked to the specified parent

Example:

* `childrenof n/Bernice Yu` lists all children of Bernice Yu

![children command](images/childrenCommand.png)

---

### Adding a Class — `addclass`

Create a new tuition class and add to the address book.

**Format:**

```
addclass c/CLASS_NAME [tu/TUTOR_NAME]
```

* `CLASS_NAME` can contain spaces
* `TUTOR_NAME` is optional; if provided, the tutor must already exist in the address book

**Examples:**

* `addclass c/Sec2-Math-A`
* `addclass c/CS2101 tu/Alex Yeoh`

![add class command](images/addClassCommand.png)

---

### Listing Classes — `listclass`

Displays all classes and their enrolled students.
**Format:** `listclass`

![list class command](images/listClassCommand.png)

---

### Editing a Class — `editclass`

Rename an existing class while keeping all students and tutors.

**Format:**

```
editclass o/OLD_CLASS_NAME c/NEW_CLASS_NAME
```

**Example:**
`editclass o/Sec3 Math c/Sec4 Math`

![edit class command](images/editClassCommand.png)

---

### Deleting a Class — `deleteclass`

Remove a class permanently.
Format: `deleteclass c/CLASS_NAME`

**Example:**

* `deleteclass c/Sec3-Math-A` deletes the class Sec3-Math-A

---

### Joining a Class — `join`

Add a tutor or student to an existing class.
**Format:**

```
join n/NAME c/CLASS
```

* The person and class must already exist in the address book
* Students will be enrolled in the class
* Tutors will be assigned to teach the class

**Examples:**

* `join n/Damian c/Sec4 Math`
* `join n/Ms Lim c/Sec2-Math-A`

![join command](images/joinCommand.png)

---

### Removing from a Class — `unjoin`

Remove a student or tutor from a class.
**Format:**

```
unjoin n/NAME c/CLASS
```

* The person must be enrolled or assigned to the class
* Works for both students and tutors

**Examples:**

* `unjoin n/Damian c/Sec4 Math`
* `unjoin n/Ms Lim c/Sec2-Math-A`

---

### Adding a Session — `addsession`

Record a session for a class.
**Format:**

```
addsession c/CLASS_NAME s/SESSION_NAME dt/DATETIME [l/LOCATION]
```

* The class must already exist
* `DATETIME` format: `YYYY-MM-DD HH:mm` (e.g., 2024-03-15 14:30)
* Location is optional
* Session names must be unique within a class

**Example:**
`addsession c/Math101 s/Week 3 Tutorial dt/2024-03-15 14:30 l/COM1-B103`

---

### Deleting a Session — `deletesession`

Remove a session and its attendance records.
**Format:**

```
deletesession c/CLASS_NAME s/SESSION_NAME
```

* The session must exist in the specified class

**Example:**
`deletesession c/Math101 s/Week 3 Tutorial`

---

### Viewing a Session — `viewsession`

Check details and attendance for a class session.
**Format:**

```
viewsession c/CLASS_NAME s/SESSION_NAME
```

* Displays date, time, location and remarks for the session

`viewsession c/Physics s/Lab Session 1`

---

### Listing Sessions — `listsessions`

View all sessions for a class in chronological order.
**Format:**

```
listsessions c/CLASS_NAME
```

* Displays date, time, and location for each session

**Example:**
`listsessions c/Math101`

---

### Listing Parents — `parentsof`

Show parents of a specific child.
**Format:**

```
parentsof n/CHILD_NAME
```

**Example:**
`parentsof n/Damian`

---

### Marking Attendance — `attend`

Record attendance for a class session.
**Format:**

```
attend n/NAME c/CLASS_NAME s/SESSION_NAME st/STATUS
```

* The student must be enrolled in the specified class
* The session must exist in the class
* `STATUS` must be either `PRESENT` or `ABSENT`
* Attendance records can be viewed with [`viewsession`](#viewsession)

**Example:**
`attend n/John Doe c/Math101 s/Week 3 Tutorial st/PRESENT`

---

### Clearing All Data — `clear`

Deletes all data in TutBook.
**Format:** `clear`

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
This action is **irreversible**. Please be careful when using this command.
</div>

### Exiting the Program — `exit`

Closes TutBook.
**Format:** `exit`

---

### Saving and Editing Data

TutBook automatically saves your data after every change.
Files are stored in `[JAR file location]/data/addressbook.json`.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If you edit the JSON file incorrectly, TutBook will reset to an empty file. Always back up before editing.
Furthermore, certain edits can cause the TutBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

---

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE e/EMAIL a/ADDRESS ro/PERSON_TYPE [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 ro/student`
**Add Class** | `addclass c/CLASS_NAME [tu/TUTOR_NAME]` <br> e.g., `addclass c/Sec2-Math-A tu/Ms Lim`
**Add Session** | `addsession c/CLASS_NAME s/SESSION_NAME dt/DATETIME [l/LOCATION]` <br> e.g., `addsession c/Math101 s/Week 3 Tutorial dt/2024-03-15 14:30 l/COM1-B103`
**Attend** | `attend n/NAME c/CLASS_NAME s/SESSION_NAME st/STATUS` <br> e.g., `attend n/John Doe c/Math101 s/Week 3 Tutorial st/PRESENT`
**Children** | `childrenof n/PARENT_NAME` <br> e.g., `childrenof n/John Doe`
**Clear** | `clear`
**Delete** | `delete INDEX` <br> e.g., `delete 3`
**Delete Class** | `deleteclass c/CLASS_NAME` <br> e.g., `deleteclass c/Sec3-Math-A`
**Delete Session** | `deletesession c/CLASS_NAME s/SESSION_NAME` <br> e.g., `deletesession c/Math101 s/Week 3 Tutorial`
**Edit** | `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [ro/PERSON_TYPE [t/TAGS]​` <br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Edit Class** | `editclass o/OLD_CLASS_NAME c/NEW_CLASS_NAME` <br> e.g., `editclass o/Sec2-Math-A c/Sec3-Math-A`
**Exit** | `exit`
**Filter** | `filter ro/STUDENT\|TUTOR\|PARENT` <br> e.g., `filter ro/student`
**Find** | `find KEYWORD [MORE_KEYWORDS]` <br> e.g., `find James Jake`
**Help** | `help`
**Join** | `join n/NAME c/CLASS` <br> e.g., `join n/John Doe c/Sec2-Math-A`
**Link** | `link parent/PARENT_NAME child/CHILD_NAME` <br> e.g., `link parent/John Doe child/Jane Doe`
**List** | `list`
**List Class** | `listclass`
**List Parents** | `listparents [n/CHILD_NAME]` <br> e.g., `listparents n/Damian`
**List Session** | `listsession c/CLASS_NAME` <br> e.g., `listsession c/Math101`
**Remove From** | `unjoin n/NAME c/CLASS` <br> e.g., `unjoin n/Damian c/Sec4 Math`
**View Session** | `viewsession c/CLASS_NAME s/SESSION_NAME` <br> e.g., `viewsession c/Math101 s/Week 3 Tutorial`

---

## Troubleshooting

| Problem | Possible Cause | Solution |
|----------|----------------|-----------|
| `link` command fails | Wrong roles | Check spelling; ensure parent/child roles are correct |
| GUI opens off-screen | Moved app between displays | Delete `preferences.json` before restarting |
| `help` window not appearing | Window minimized | Restore manually from taskbar |
| App fails to start | Java not installed | Install Java 17 or above |

---

## FAQ

**Q:** How do I transfer my data to another computer?
**A:** Install TutBook on the new computer and replace its empty data file with your saved `addressbook.json` from the old system.

**Q:** Can I undo a delete or clear command?
**A:** No. Destructive commands are irreversible.

**Q:** Can try the app without fully committing to it?
**A:** Yes, [sample data](./sample-data/README.md) is pre-loaded for you to explore features. Use the `clear` command to start fresh.

---

*TutBook v1.0 – Empowering tuition centre management through simplicity and speed.*
