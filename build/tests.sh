#!/bin/bash
#
# tests.sh
#
# Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
# 
# The code in this file, and the library it is a part of, are made available
# to you by the authors under the terms of the "GNU General Public Licence
# version 2". See the LICENCE file for the terms governing usage and
# redistribution.
#

source .config

if [ ! -f tmp/stamp/build-tests ] ; then
	touch -d "2001-01-01" tmp/stamp/build-tests
fi
find tests -type f -name '*.java' -newer tmp/stamp/build-tests > tmp/list-tests

if [ -s tmp/list-tests ] ; then
	echo -n "${JAVAC_CMD}"
	sed -e 's/^/\t/' < tmp/list-tests	
	${JAVAC} \
		-classpath ${GNOME_JARS}:${XOM_JARS}:${JUNIT_JARS}:tmp/classes \
		-d tmp/unittests \
		-sourcepath src:tests \
		`cat tmp/list-tests`
	if [ $? -ne 0 ] ; then
		exit $?
	fi
	touch tmp/stamp/build-tests
fi

echo -e "${JAVA_CMD}\tUnitTests"
exec ${JAVA} \
	-classpath ${GNOME_JARS}:${XOM_JARS}:${JUNIT_JARS}:tmp/classes:tmp/unittests \
	UnitTests

