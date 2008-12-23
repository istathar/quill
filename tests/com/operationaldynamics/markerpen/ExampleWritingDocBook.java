/*
 * ExampleWritingDocBook.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package com.operationaldynamics.markerpen;

import java.io.IOException;

import docbook.BookDocument;
import docbook.Chapter;
import docbook.Document;
import docbook.Italics;
import docbook.Paragraph;
import docbook.Section;

public class ExampleWritingDocBook
{
    public static void main(String[] args) throws IOException {
        final Document doc;
        final Chapter one;
        Section section;
        Paragraph para;

        doc = new BookDocument();

        one = new Chapter("Chapter 1");
        doc.add(one);

        section = new Section("Start");
        one.add(section);

        para = new Paragraph("In the beginning...");
        section.add(para);

        para = new Paragraph("And then we had a ");
        para.add(new Italics("yummy"));
        para.add(" breakfast");

        section.add(para);

        System.out.write(doc.toXML());
    }
}
