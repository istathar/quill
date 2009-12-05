#
# Makefile
#
# Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
# 
# The code in this file, and the library it is a part of, are made available
# to you by the authors under the terms of the "GNU General Public Licence
# version 2". See the LICENCE file for the terms governing usage and
# redistribution.
#

ifdef V
MAKEFLAGS=-R
else
MAKEFLAGS=-s -R
REDIRECT=>/dev/null
endif

.PHONY: all build clean test

all: compile

compile:
	build/compile.sh

clean:
	rm -r tmp/

test: compile
	build/tests.sh

