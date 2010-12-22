/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package quill.ui;

import java.io.File;
import java.io.IOException;

import org.freedesktop.enchant.Dictionary;
import org.freedesktop.enchant.Enchant;

import parchment.manuscript.Manuscript;

import static java.lang.System.arraycopy;

/**
 * Check the spelling of a word against <i>two</i> Dictionaries: the spell
 * checking dictionary specified by the Enchant language tag in the Manuscript
 * Metadata, and the document's personal word list.
 * 
 * @author Andrew Cowie
 */
/*
 * Effectively, this implements Dictionary
 */
class SpellChecker
{
    private final Dictionary dict;

    private final Dictionary list;

    /**
     * The path of the document word list.
     */
    private final String filename;

    SpellChecker(final Manuscript manuscript, final String lang) {
        File target;

        if (Enchant.existsDictionary(lang)) {
            dict = Enchant.requestDictionary(lang);
        } else {
            dict = null;
        }

        filename = manuscript.getDirectory() + "/" + manuscript.getBasename() + ".dic";

        try {
            target = new File(filename);
            if (!target.exists()) {
                target.createNewFile();
            }
            list = Enchant.requestPersonalWordList(filename);
        } catch (IOException ioe) {
            throw new AssertionError("Can't open document word list " + filename);
        }
    }

    boolean check(String word) {
        boolean result;

        result = false;

        if (dict != null) {
            result = dict.check(word);
        }

        if (result) {
            return true;
        }

        if (list != null) {
            result = list.check(word);
        }

        return result;
    }

    /**
     * Add the given word to the user's word list that goes along with the
     * system dictionary
     */
    void addToSystem(final String word) {
        if (dict != null) {
            dict.add(word);
        }
    }

    /**
     * Add the given word to the document's private word list.
     */
    void addToDocument(final String word) {
        if (list != null) {
            list.add(word);
        } else {
            throw new AssertionError();
        }
    }

    boolean isSystemValid() {
        return (dict != null);
    }

    boolean isDocumentValid() {
        return (list != null);
    }

    /**
     * Puts words from the document's local list before dictionary suggests.
     * Is that best?
     * 
     * A String[] with suggestions, on null if there aren't any.
     */
    String[] suggest(final String word) {
        String[] one, two, result;
        int a, b;

        one = null;
        two = null;

        if (list != null) {
            one = list.suggest(word);
        }

        if (dict != null) {
            two = dict.suggest(word);
        }

        if (one != null) {
            a = one.length;
        } else {
            a = 0;
        }
        if (two != null) {
            b = two.length;
        } else {
            b = 0;
        }

        if ((a == 0) && (b == 0)) {
            return null;
        }

        result = new String[a + b];

        if (a > 0) {
            arraycopy(one, 0, result, 0, a);
        }
        if (b > 0) {
            arraycopy(two, 0, result, a, b);
        }

        return result;
    }

    /**
     * Get the name of the document's personal word list.
     */
    String getDocumentFilename() {
        return filename;
    }
}
