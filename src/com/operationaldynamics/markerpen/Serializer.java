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
            /*
             * Close markup for formats that are now ending
             */

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

            /*
             * While a single newline terminates a paragraph in Markdown, for
             * aesthetic purposes we'll double tap it to create a blank line
             * in the output textfile.
             */

            if (pointer.endsLine()) {
                str.append("\n");
            }

            /*
             * Open markup that represents formats that are now beginning.
             */

            if (pointer.beginsTag(Format.italics)) {
                str.append("_");
            }
            if (pointer.beginsTag(Format.bold)) {
                str.append("**");
            }
            if (pointer.beginsTag(Format.mono)) {
                str.append("`");
            }

            /*
             * Finally, add the TextBuffer's content at this position, and
             * move to the next character.
             */

            str.append(pointer.getChar());

            pointer.forwardChar();
        }

        return str.toString();
    }
}
