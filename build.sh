#!/bin/bash
#
# build.sh
#
# Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
# 
# The code in this file, and the library it is a part of, are made available
# to you by the authors under the terms of the "GNU General Public Licence,
# version 2". See the LICENCE file for the terms governing usage and
# redistribution.
#

set -x

#
# source all the Make variables as Shell variables
# FIXME just write shell variables :)
#
eval `cat .config | sed -e '/^#/d' -e '/^$/d' -e 's/=/="/' -e 's/$/"/'`

if [ ! -d  tmp/classes ] ; then
	mkdir -p tmp/classes
fi

if [ -f tmp/build-core ] ; then
	find src -type f -name '*.java' -newer tmp/build-core > tmp/list-core
else
	find src -type f -name '*.java' > tmp/list-core
fi

if [ -f tmp/build-tests ] ; then
	find tests -type f -name '*.java' -newer tmp/build-tests > tmp/list-tests
else
	find tests -type f -name '*.java' > tmp/list-tests
fi

if [ -s tmp/list-core ] ; then
	$JAVAC \
		-classpath $GNOME_JARS:$XOM_JARS \
		-d tmp/classes \
		-sourcepath src \
		`cat tmp/list-core`
	if [ $? -ne 0 ] ; then
		exit $?
	fi
	touch tmp/build-core
fi

if [ -s tmp/list-tests ] ; then
	$JAVAC \
		-classpath $GNOME_JARS:$XOM_JARS:$JUNIT_JARS \
		-d tmp/classes \
		-sourcepath src:tests \
		`cat tmp/list-tests`
	if [ $? -ne 0 ] ; then
		exit $?
	fi
	touch tmp/build-tests
fi

if [ $* ] ; then
	${JAVA} \
	-classpath ${GNOME_JARS}:${XOM_JARS}:${JUNIT_JARS}:tmp/classes \
	UnitTests
fi

