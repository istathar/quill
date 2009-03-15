/*
 * HelpWidget.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.VBox;

import static org.gnome.gtk.Alignment.LEFT;
import static org.gnome.gtk.Alignment.TOP;
import static org.gnome.gtk.SizeGroupMode.HORIZONTAL;

/**
 * Display help about how to use the EditorWidgets. As the list of
 * accelerators grows this will likely have to change drastically. A possible
 * improvements would be to only display those accelerators which are
 * applicable to the currently pressed modifiers (Inkscpae style).
 * 
 * <p>
 * There is nothing that requires that this display the equivalent DocBook
 * tags - see Bold and Italic for an idea of the limitations of this -
 * currently but it seemed a nice idea for starters.
 * 
 * @author Andrew Cowie
 */
class HelpWidget extends VBox
{
    private final VBox vbox;

    private final SizeGroup group;

    HelpWidget() {
        super(false, 0);
        final String[][] views, markup;
        vbox = this;

        views = new String[][] {
                new String[] {
                        "F1", "", "Help", "This help screen"
                },
                new String[] {
                        "F2",
                        "",
                        "Preview",
                        "A preview of what the current page will look like in its final rendered form."
                },
                new String[] {
                        "F3",
                        "",
                        "Outline",
                        "An outline of the documents chapters and section headings allowing you to navigate."
                }
        };

        markup = new String[][] {
                new String[] {
                        "Ctrl+Shift+Space",
                        "",
                        "Clear!",
                        "Remove any inline formatting from the currently selected range."
                },
                new String[] {
                        "Ctrl+I", "emphasis", "Italics", "Make the text appear in italics."
                },
                new String[] {
                        "Ctrl+B", "emphasis role=\"bold\"", "Bold", "Make the text appear in bold face."
                },
                new String[] {
                        "Ctrl+Shift+F", "filename", "File", "A path on the filesystem."
                },
                new String[] {
                        "Ctrl+Shift+T",
                        "type",
                        "Class or Type",
                        "The name of an object-oriented class or similar type."
                },
                new String[] {
                        "Ctrl+Shift+M",
                        "function",
                        "Method or Function",
                        "The name of a method or function call."
                },
        };

        group = new SizeGroup(HORIZONTAL);

        addHeading("View selection");
        for (String[] line : views) {
            addHelpLine(line[0], line[2], line[3], line[1]);
        }

        addHeading("Markup keys");
        for (String[] line : markup) {
            addHelpLine(line[0], line[2], line[3], line[1]);
        }
    }

    private void addHeading(String title) {
        final Label heading;

        heading = new Label();
        heading.setUseMarkup(true);
        heading.setLineWrap(true);
        heading.setLabel("<span size='xx-large'>" + title + "</span>");
        heading.setAlignment(LEFT, TOP);

        vbox.packStart(heading, false, false, 6);
    }

    private void addHelpLine(String keys, String title, String description, String element) {
        Label label;
        HBox spread;
        StringBuilder str;

        spread = new HBox(false, 0);

        label = new Label(keys);
        label.setAlignment(LEFT, TOP);
        spread.packStart(label, false, true, 0);
        group.add(label);

        str = new StringBuilder();
        str.append("<b>");
        str.append(title);
        str.append("</b>\n");
        str.append(description);

        if (!element.equals("")) {
            str.append("\n<span font='Deja Vu Sans Mono'>&lt;");
            str.append(element);
            str.append("&gt;</span>");
        }

        label = new Label();
        label.setLabel(str.toString());
        label.setUseMarkup(true);
        label.setLineWrap(true);
        label.setAlignment(LEFT, TOP);
        spread.packStart(label, true, true, 6);

        vbox.packStart(spread, false, false, 6);
    }
}
