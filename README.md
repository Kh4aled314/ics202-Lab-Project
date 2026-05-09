# KFUPM Clinic System

A Java command-line clinic management system for handling patients, appointments, walk-ins, urgent patients, visit logs, search, and undo operations.

## Features

* Add, find, and delete patients
* Create, find, cancel, and view appointments by day or time range
* Manage walk-in patients
* Track urgent patients by priority
* Serve the next patient and record a visit log
* Search visit notes with naive search or KMP
* Undo the last action

## Commands

Run `HELP` inside the program to see the full command list.

Main commands include:

* `ADD_PATIENT <id> <name> <phone>`
* `FIND_PATIENT <id>`
* `DELETE_PATIENT <id>`
* `ADD_APPT <patientId> <date YYYY-MM-DD> <time HH:MM> <doctor>`
* `CANCEL_APPT <appointmentId>`
* `FIND_APPT <appointmentId>`
* `VIEW_DAY <date>`
* `VIEW_RANGE <date> <startTime> <endTime>`
* `ADD_WALKIN <patientId>`
* `VIEW_WALKINS`
* `ADD_URGENT <patientId> <severity 1..5>`
* `PEEK_URGENT`
* `VIEW_URGENTS`
* `SERVE_NEXT <doctor> <note>`
* `PRINT_LOG`
* `SEARCH_LOG_NAIVE <pattern>`
* `SEARCH_LOG_KMP <pattern>`
* `UNDO`
* `EXIT`

## Project structure

```text
src/
└── kfupm/
    └── clinic/
        ├── ClinicSystem.java
        ├── api/
        ├── ds/
        ├── model/
        ├── parser/
        └── service/
```

## Requirements

* Java 17 or newer

## Run

From the project root:

```bash
javac -d out $(find src -name "*.java")
java -cp out kfupm.clinic.ClinicSystem
```

## Usage example

```text
HELP
ADD_PATIENT P001 "Ali Ahmed" 0501234567
ADD_APPT P001 2026-05-10 09:30 Dr.Sara
VIEW_DAY 2026-05-10
EXIT
```

## Notes

* Dates use `YYYY-MM-DD`
* Times use `HH:MM`
* Severity is from 1 to 5, where 5 is most urgent
* The system prints status messages for success and failure cases

## License

No license file is present in the repository.
