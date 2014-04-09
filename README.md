[![Build Status](https://travis-ci.org/andreafey/conway.svg)](https://travis-ci.org/andreafey/conway)

Phonebook
=========

Scala phonebook implementation

# Usage

Phonebook application which can be run either as a command line utility or as an interactive command line application.

## Command Line Utility

'''
Usage:
	 phonebook create <file>.pb          Creates empty phonebook
   phonebook open [<file>.pb]          Opens an interactive phonebook; creates empty phonebook if needed
  
Supports all commands below without the phonebook prefix or -b file switch
    phonebook lookup <name> [-b <file>.pb]     
    phonebook add '<name>' '123 456 4323' [-b <file>.pb]
    phonebook change '<name>' '232 987 3940' [-b <file>.pb]
    phonebook remove '<name>' [-b <file>.pb]
    phonebook reverse-lookup '312 432 4252' [-b <file>.pb]
'''

## Command Line Application

In order to use the interactive application, you must first create a phone book from the command line utility. 

Start up the application by executing phonebook.Phonebook.class with arguments "open <file>.pb".

'''
Available Commands:
   list, help,
   lookup <str>, reverse-lookup <str>, remove <str>,
   add '<name>' '<number>', change '<name>' '<number>'
   [Enter] to quit"
'''

### IDE (Eclipse Scala IDE)

Right-click on the Phonebook.scala file
  > Run Configurations > Arguments > create foo.pb

Right-click on the Phonebook.scala file
  > Run Configurations > Arguments > open foo.pb

TODO

### SBT

TODO
