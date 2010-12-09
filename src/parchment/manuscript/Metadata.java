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
package parchment.manuscript;

/**
 * Meta information about the document. This is very minimal - just what is
 * needed at a global level. Details about authors, copyright, etc are just
 * normal text and belong on a cover sheet.
 * 
 * @author Andrew Cowie
 */
// immutable
public class Metadata
{
    private final String documentTitle;

    private final String authorName;

    private final String spellingLang;

    public Metadata(final String documentTitle, final String authorName, final String spellingLang) {
        this.documentTitle = documentTitle;
        this.authorName = authorName;
        this.spellingLang = spellingLang;
    }

    public Metadata() {
        this.documentTitle = "Untitled";
        this.authorName = "";
        this.spellingLang = "en_CA";
    }

    public String getDocumentTitle() {
        return this.documentTitle;
    }

    public Metadata changeDocumentTitle(String value) {
        return new Metadata(value, this.authorName, this.spellingLang);
    }

    /**
     * This is NOT a locale. It is a language code and a country code, spaced
     * by an '_' which indicates the spelling dictionary to be loaded, ie
     * Australian English (en_AU) and Canadian French (fr_CA).
     */
    public String getSpellingLanguage() {
        return this.spellingLang;
    }

    public Metadata changeSpellingLanguage(String value) {
        return new Metadata(this.documentTitle, this.authorName, value);
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public Metadata changeAuthorName(String value) {
        return new Metadata(this.documentTitle, value, this.spellingLang);
    }
}
