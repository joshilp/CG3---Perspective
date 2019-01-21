All tasks have been implemented for this assignment. My PageJ will render a Dinosaur (trex.obj sourced from TurboSquid.com).

objreader, Clipping, depthcueable with fog, etc all implemented correctly and fully working. Only issue comes from Assignment 1, where you can see a few holes in the filled polygons.

Simp and OBJ files are in the src directory, and the directory above because running Main within Eclise seems to use a different working directory than when run from command line.

To run my project via command line, open a command line INSIDE src directory.

To compile (while inside src):		javac client/Main.java

To run:		java client.Main PageA

You can remove PageA and it will default to first page.
You can change PageA to any pages from A through J, and it will render that page. From this page, you can still hit Next Page and it will go to next appropriate page.

Extra feature:
If you type in gibberish instead of a page, it will default to first page.