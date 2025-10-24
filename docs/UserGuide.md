---
layout: page
title: User Guide
---

TutBook is a **desktop app for managing tuition centre contacts, optimized for use via a Command Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI). This release represents a significant enhancement over AB3, transforming it into a comprehensive tuition centre management system.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `17` or above installed in your Computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2526S1-CS2103T-W09-3/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your TutBook.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar tutbook.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `listclass` : Lists all classes.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 ro/student` : Adds a student named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[t/TAG]…​` can be used as ` ` (i.e. 0 times), `t/friend`, `t/friend t/family` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `listclass`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help : `help`

Shows a message explaining how to access the help page.

Format: `help`

![help message](images/helpMessageNew.png)

### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS ro/PERSON_TYPE [t/TAG]…​`

* `PERSON_TYPE` can be `student`, `tutor`, or `parent`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags (including 0)
</div>

Examples:
* `add n/Ms Lim p/91234567 e/mslim@example.com a/Clementi Ave 2 ro/tutor t/experienced`
* `add n/Yi Hao p/98765432 e/johnd@example.com a/31 John street, block 123, #01-01 ro/student`

![add command](images/addCommand.png)

### Listing all persons : `list`

Shows a list of all persons in the address book.

Format: `list`

![list command](images/listCommand.png)

### Filtering persons by role : `filter`

Filters and displays persons by their role (student, tutor, or parent).

Format: `filter ro/PERSON_TYPE`

* Shows only persons matching the specified role

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
Commands like `edit` and `delete` operate on the index of the **currently displayed list**. After using `filter`, the indexes will change. For example, `delete 1` will delete the first person in the *filtered list*, not the first person in the main address book.
</div> <br>

Examples:
* `filter ro/tutor` shows all tutors
* `filter ro/student` shows all students

![filter command](images/filterCommand.png)

### Editing a person : `edit`

Edits an existing person in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, the existing tags of the person will be removed i.e adding of tags is not cumulative.
* You can remove all the person’s tags by typing `t/` without
    specifying any tags after it.

Examples:
*  `edit 1 p/87438807 e/alex@gmail.com` Edits the phone number and email address of the 1st person to be `87438807` and `alex@gmail.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

![edit command](images/editCommandReal.png)

### Locating persons by name: `find`

Finds persons whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive. e.g `hans` will match `Hans`
* The order of the keywords does not matter. e.g. `Hans Bo` will match `Bo Hans`
* Only the name is searched.
* Only full words will be matched e.g. `Han` will not match `Hans`
* Persons matching at least one keyword will be returned (i.e. `OR` search).
  e.g. `Hans Bo` will return `Hans Gruber`, `Bo Yang`

Examples:
* `find John` returns `john` and `John Doe`
* `find david damian` returns `David Li`, `Damian`<br>
  ![result for 'find david damian'](images/findCommand.png)

### Deleting a person : `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Linking a parent to a child : `link`

Links a parent to a student (child), establishing a parent-child relationship.

Format: `link parent/PARENT_NAME child/CHILD_NAME`

* Both parent and child must already exist in the address book
* The parent must have the role `parent` and the child must have the role `student`
* Names are case-sensitive

Examples:
* `link parent/Bernice Yu child/Damian` links parent Bernice Yu to child Damian

![link command](images/linkCommand.png)

### Listing children : `children`

Lists all students, or lists children of a specific parent.

Format: `children [n/PARENT_NAME]`

* Without parameters, lists all students in the address book
* With `n/PARENT_NAME`, lists only the children linked to that parent

Examples:
* `children` lists all students
* `children n/Bernice Yu` lists all children of Bernice Yu

![children command](images/childrenCommand.png)

### Adding a class : `addclass`

Adds a new tuition class to the system.

Format: `addclass c/CLASS_NAME [tu/TUTOR_NAME]`

* `CLASS_NAME` can contain spaces
* `TUTOR_NAME` is optional; if provided, the tutor must already exist in the address book

Examples:
* `addclass c/Sec2-Math-A` creates a class without a tutor
* `addclass c/CS2101 tu/Alex Yeoh` creates a class and assigns Alex Yeoh as the tutor

![add class command](images/addClassCommand.png)

### Listing all classes : `listclass`

Lists all tuition classes with their enrolled students.

Format: `listclass`

![list class command](images/listClassCommand.png)

### Editing a class : `editclass`

Renames an existing class.

Format: `editclass o/OLD_CLASS_NAME c/NEW_CLASS_NAME`

* Class names can contain spaces
* All students and tutor assignments are preserved


Examples:
* `editclass o/Sec3 Math c/Sec4 Math` renames the class from Sec3 Math to Sec4 Math

![edit class command](images/editClassCommand.png)

### Deleting a class : `deleteclass`

Deletes a class from the system.

Format: `deleteclass CLASS_NAME`

* Class name can contain spaces

Examples:
* `deleteclass Sec3-Math-A` deletes the class Sec3-Math-A

### Joining a class : `join`

Adds a student or tutor to a class.

Format: `join n/NAME c/CLASS`

* The person and class must already exist in the address book
* Students will be enrolled in the class
* Tutors will be assigned to teach the class

Examples:
* `join n/Damian c/Sec4 Math` enrolls student Damian in Sec4 Math
* `join n/Ms Lim c/Sec2-Math-A` assigns tutor Ms Lim to Sec2-Math-A

![join command](images/joinCommand.png)

### Marking attendance : `attend`

Marks attendance for a person in a specific session.

Format: `attend n/NAME s/SESSION_ID st/STATUS`

* `STATUS` must be either `PRESENT` or `ABSENT`

Examples:
* `attend n/John Doe s/1 st/PRESENT` marks John Doe as present in session 1

### Clearing all entries : `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

TutBook data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data file

TutBook data are saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes to the data file makes its format invalid, TutBook will discard all data and start with an empty data file at the next run. Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause the TutBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous TutBook home folder.<br>

**Q**: Can I undo a delete or clear command?<br>
**A**: No. All destructive commands (like `delete`, `deleteclass`, and `clear`) are irreversible and cannot be undone.<br>

**Q**: Why is the `link` command failing?<br>
**A**: This is typically due to two reasons: 1) The names do not exactly match (the command is case-sensitive). 2) The persons do not have the correct roles. The parent must have the parent role, and the child must have the student role. Use `list` or `filter ro/parent` to check.<br>

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE e/EMAIL a/ADDRESS ro/PERSON_TYPE [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 ro/student`
**Add Class** | `addclass c/CLASS_NAME [tu/TUTOR_NAME]` <br> e.g., `addclass c/Sec2-Math-A tu/Ms Lim`
**Attend** | `attend n/NAME s/SESSION_ID st/STATUS` <br> e.g., `attend n/John Doe s/1 st/PRESENT`
**Children** | `children [n/PARENT_NAME]` <br> e.g., `children n/John Doe`
**Clear** | `clear`
**Delete** | `delete INDEX` <br> e.g., `delete 3`
**Delete Class** | `deleteclass CLASS_NAME` <br> e.g., `deleteclass Sec3-Math-A`
**Edit** | `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [ro/PERSON_TYPE [t/TAGS]​` <br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Edit Class** | `editclass o/OLD_CLASS_NAME c/NEW_CLASS_NAME` <br> e.g., `editclass o/Sec2-Math-A c/Sec3-Math-A`
**Exit** | `exit`
**Filter** | `filter ro/STUDENT|TUTOR|PARENT` <br> e.g., `filter ro/student`
**Find** | `find KEYWORD [MORE_KEYWORDS]` <br> e.g., `find James Jake`
**Help** | `help`
**Join** | `join n/NAME c/CLASS` <br> e.g., `join n/John Doe c/Sec2-Math-A`
**Link** | `link parent/PARENT_NAME child/CHILD_NAME` <br> e.g., `link parent/John Doe child/Jane Doe`
**List** | `list`
**List Class** | `listclass`
