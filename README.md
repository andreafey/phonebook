[![Build Status](https://travis-ci.org/andreafey/phonebook.svg)](https://travis-ci.org/andreafey/phonebook)

#Phonebook

This is a phonebook application with both command-line utility functions and an interactive console. It offers rudimentary data storage - name and phone number only - with the ability to modify the phone number or remove the entry. It is implemented in Scala.

## Project Notes

This project is complete but could use additional testing. Its feature set is small, but it is fast and correct. The phonebook lookup is implemented using a prefix trie, so once the data has been loaded it should be extremely fast even at scale. The command line utilities are less performant as the entire file is read for every action.

Project is compatible with Java 7 & 8.

## Usage

Phonebook application which can be run either as a command line utility or as an interactive command line application.

### Command Line Utility

```
Usage:
    phonebook create <file>.pb          Creates empty phonebook
    phonebook open [<file>.pb]          Opens an interactive phonebook; creates empty phonebook if needed
  
Supports all commands below without the phonebook prefix or -b file switch
    phonebook lookup <name> [-b <file>.pb]     
    phonebook add '<name>' '123 456 4323' [-b <file>.pb]
    phonebook change '<name>' '232 987 3940' [-b <file>.pb]
    phonebook remove '<name>' [-b <file>.pb]
    phonebook reverse-lookup '312 432 4252' [-b <file>.pb]
```

### Command Line Application

In order to use the interactive application, you must first create a phone book from the command line utility. 

Start up the application by executing phonebook.Phonebook.class with arguments "open <file>.pb".

```
Available Commands:
   list, help,
   lookup <str>, reverse-lookup <str>, remove <str>,
   add '<name>' '<number>', change '<name>' '<number>'
   [Enter] to quit"
```

#### From Eclipse Scala IDE

    Right-click on the Phonebook.scala file
      > Run Configurations > Arguments > create foo.pb
      OR 
      > Run Configurations > Arguments > open foo.pb

#### From SBT

    $ sbt "phonebook create foo.pb"
    OR
    $ sbt "phonebook open foo.pb"
    OR 
    $ sbt
    > phonebook open foo.pb

#### Sample session
```
created phonebook foo.pb
Enter a command (help for command list):
add 'Big Bird' '1 800 YELLOWME'   
entry added
list
Big Bird 1 800 YELLOWME
add 'Ernie' '555-1233'
entry added
add 'Cookie Monster' '456 2356'
entry added
add 'Ernest Hemmingway' '678 345 4545'
entry added
list
Big Bird 1 800 YELLOWME
Cookie Monster 456 2356
Ernest Hemmingway 678 345 4545
Ernie 555-1233
lookup ern
cmdargs:  ern
search ern
Ernest Hemmingway 678 345 4545
Ernie 555-1233

Changes saved. Goodbye.
```
