#
# Makefile
#
# Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
# 
# The code in this file, and the library it is a part of, are made available
# to you by the authors under the terms of the "GNU General Public Licence,
# version 2". See the LICENCE file for the terms governing usage and
# redistribution.
#

#
# FIXME This is just a placeholder. It's not a real build system. It's not even
# a real Makefile; it's just here to show us what assumptions we're currently
# making in our IDEs.
#

JAVAC=/opt/icedtea6-bin-1.4.1/bin/javac -g
GTK_JAR=/home/andrew/workspace/java-gnome/tmp/gtk-4.0.jar
XOM_JAR=/home/andrew/workspace/xom/tmp/classes
JUNIT_JAR=/usr/share/junit/lib/junit.jar:/home/andrew/workspace/java-gnome/tmp/tests

all: build

build: compile-core compile-tests

compile-core: dirs list
	$(JAVAC) \
	-classpath $(GTK_JAR):$(XOM_JAR) \
	-d tmp/classes \
	-sourcepath src \
	@tmp/list-core

compile-tests: dirs list
	$(JAVAC) \
	-classpath $(GTK_JAR):$(XOM_JAR):$(JUNIT_JAR) \
	-d tmp/classes \
	-sourcepath src \
	@tmp/list-tests

list: dirs
	find src -type f -name '*.java' > tmp/list-core
	find tests -type f -name '*.java' > tmp/list-tests

dirs: tmp/classes

tmp/classes:
	mkdir -p tmp/classes
