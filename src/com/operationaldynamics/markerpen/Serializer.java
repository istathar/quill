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

class Serializer
{
    static String extractToFile(TextBuffer buffer) {
        StringBuilder str;
        TextIter pointer;

        str = new StringBuilder();

        pointer = buffer.getIterStart();

        while (true) {
            if (pointer.endsTag(Format.mono)) {
                str.append("`");
            }
            if (pointer.endsTag(Format.bold)) {
                str.append("**");
            }
            if (pointer.endsTag(Format.italics)) {
                str.append("_");
            }

            if (pointer.isEnd()) {
                break;
            }

            if (pointer.beginsTag(Format.italics)) {
                str.append("_");
            }
            if (pointer.beginsTag(Format.bold)) {
                str.append("**");
            }
            if (pointer.beginsTag(Format.mono)) {
                str.append("`");
            }

            str.append(pointer.getChar());

            pointer.forwardChar();
        }

        return str.toString();
    }
}
