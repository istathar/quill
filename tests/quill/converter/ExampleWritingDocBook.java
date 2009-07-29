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
package quill.converter;

import java.io.IOException;

import quill.docbook.Book;
import quill.docbook.Chapter;
import quill.docbook.Italics;
import quill.docbook.Paragraph;
import quill.docbook.Section;
import quill.docbook.Title;

import static textview.LoremIpsum.text;

public class ExampleWritingDocBook
{
    public static void main(String[] args) throws IOException {
        final Book book;
        final Chapter chapter;
        Section section;
        Paragraph para;

        book = new Book();

        chapter = new Chapter();
        chapter.add(new Title("Chapter 1"));
        book.add(chapter);

        section = new Section();
        section.add(new Title("Start"));
        chapter.add(section);

        para = new Paragraph();
        para.add("In the beginning...");
        section.add(para);

        para = new Paragraph();
        para.add("And then we indeed had a very ");
        para.add(new Italics("yummy"));
        para.add(" delicious absolutely brilliant delightful breakfast.");
        section.add(para);

        String[] paras;

        paras = text.split("\n");

        for (String blob : paras) {
            para = new Paragraph();
            para.add(blob);
            section.add(para);
        }

        if (true) {
            for (int i = 1; i <= 70; i++) {
                System.err.print(i / 10);
            }
            System.err.println();
            for (int i = 1; i <= 70; i++) {
                System.err.print(i % 10);
            }
            System.err.println("\n");
            System.err.flush();
        }

        book.toXML(System.out);
    }
}
