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

ifdef V
else
MAKEFLAGS=-s
REDIRECT=>/dev/null
endif

.PHONY: all build clean test

all: build

build:
	sh ./build.sh

clean:
	rm -r tmp/

test: build
	$(JAVA) \
	-classpath $(GNOME_JARS):$(XOM_JARS):$(JUNIT_JARS):tmp/classes \
	UnitTests

