#!/bin/bash
#
# compile.sh
# Build the source code
#
# Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
# 
# The code in this file, and the library it is a part of, are made available
# to you by the authors under the terms of the "GNU General Public Licence,
# version 2". See the LICENCE file for the terms governing usage and
# redistribution.
#

source .config

if [ ! -d  tmp/classes ] ; then
	echo -e "MKDIR\ttmp/"
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
	echo -n "JAVAC"
	sed -e 's/^/\t/' < tmp/list-core
	${JAVAC} \
		-classpath ${GNOME_JARS}:${XOM_JARS} \
		-d tmp/classes \
		-sourcepath src \
		`cat tmp/list-core`
	if [ $? -ne 0 ] ; then
		exit $?
	fi
	touch tmp/build-core
fi

if [ -s tmp/list-tests ] ; then
	echo -n "JAVAC"
	sed -e 's/^/\t/' < tmp/list-tests	
	${JAVAC} \
		-classpath ${GNOME_JARS}:${XOM_JARS}:${JUNIT_JARS} \
		-d tmp/classes \
		-sourcepath src:tests \
		`cat tmp/list-tests`
	if [ $? -ne 0 ] ; then
		exit $?
	fi
	touch tmp/build-tests
fi

