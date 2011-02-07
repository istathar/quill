#!/bin/bash
#
# tests.sh
#
# Copyright © 2009-2011 Operational Dynamics Consulting, Pty Ltd
# 
# The code in this file, and the library it is a part of, are made available
# to you by the authors under the terms of the "GNU General Public Licence
# version 2". See the LICENCE file for the terms governing usage and
# redistribution.
#

source .config

#
# Compile test case code.
#

find tests -type f -name '*.java' | perl -n -e '
	chomp;
	$java = $_;
	s{tests}{tmp/unittests};
	s{\.java}{\.class};
	$class = $_;

	@source = stat($java);
	@target = stat($class);

	if ($source[9] > $target[9]) {
		print $java . "\n";
	}
' > tmp/stamp/list-tests

if [ -s tmp/stamp/list-tests ] ; then
	echo -n "${JAVAC_CMD}"
	sed -e 's/^/\t/' < tmp/stamp/list-tests	
	${JAVAC} \
		-classpath ${GNOME_JARS}:${XOM_JARS}:${JUNIT_JARS}:tmp/classes:tmp/unittests \
		-d tmp/unittests \
		`cat tmp/stamp/list-tests`
	if [ $? -ne 0 ] ; then
		exit 1
	fi
fi

#
# Cleanup.
#

rm -f tmp/stamp/list-tests

#
# Run tests.
#

echo -e "${JAVA_CMD}\tUnitTests"
exec ${JAVA} \
	-classpath ${GNOME_JARS}:${XOM_JARS}:${JUNIT_JARS}:tmp/classes:tmp/unittests \
	UnitTests

