/*
 * EditorWindow.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package com.operationaldynamics.markerpen;

import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextIter;
import org.gnome.gtk.TextTag;

class Serializer
{
    static String extractToFile(TextBuffer buffer) {
        StringBuilder str;
        TextIter pointer;

        str = new StringBuilder();

        pointer = buffer.getIterStart();

        while (true) {
            for (TextTag format : Format.tags) {
                if (pointer.endsTag(format)) {
                    str.append("_");
                }
            }

            if (pointer.isEnd()) {
                break;
            }

            for (TextTag format : Format.tags) {
                if (pointer.beginsTag(format)) {
                    str.append("_");
                }
            }

            str.append(pointer.getChar());

            pointer.forwardChar();
        }

        return str.toString();
    }
}
