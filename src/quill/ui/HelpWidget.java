/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
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

import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.ShadowType;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.VBox;

import static org.freedesktop.bindings.Internationalization._;
import static org.gnome.gtk.Alignment.LEFT;
import static org.gnome.gtk.Alignment.TOP;
import static org.gnome.gtk.PolicyType.AUTOMATIC;
import static org.gnome.gtk.SizeGroupMode.HORIZONTAL;

/**
 * Display help about how to use the EditorWidgets. As the list of
 * accelerators grows this will likely have to change drastically. A possible
 * improvements would be to only display those accelerators which are
 * applicable to the currently pressed modifiers (Inkscpae style).
 * 
 * @author Andrew Cowie
 */
/*
 * We don't actually want this to be in a scrolling Viewport; the idea is a
 * fixed single notebook page of text. The dynamics of Labels, however, make
 * this difficult since they demand space rather than wrapping based on
 * allocation. Thus this should either be replaced by a TextView (or a
 * WebView!), or a custom Label be cooked up.
 */
class HelpWidget extends ScrolledWindow
{
    private final ScrolledWindow scroll;

    private final HBox top;

    private VBox vbox;

    private final SizeGroup group;

    HelpWidget() {
        super();
        final String[][] views, actions, editing, markup;

        scroll = this;
        top = new HBox(true, 0);

        scroll.addWithViewport(top);
        scroll.setPolicy(AUTOMATIC, AUTOMATIC);
        scroll.setShadowType(ShadowType.NONE);

        views = new String[][] {
                new String[] {
                        "F1", _("Help"), _("This help screen.")
                },
                new String[] {
                        "F2",
                        _("Preview"),
                        _("A preview of what the current page will look like in its final rendered form.")
                },
                new String[] {
                        "F3",
                        _("Outline"),
                        _("An outline of the document's chapters and section headings allowing you to navigate.")
                },
                new String[] {
                        "F5", _("Editor"), _("Edit your manuscript!")
                },
                new String[] {
                        "F6",
                        _("Stylesheet"),
                        _("Change the settings being used to render your document.")
                },
                new String[] {
                        "F11",
                        _("Fullscreen"),
                        _("Work in fullscreen mode, giving you a better view of your document.")
                }
        };

        actions = new String[][] {
                new String[] {
                        "Ctrl+S", _("Save"), _("_Save the document.")
                }, new String[] {
                        "Ctrl+P", _("Print"), _("Render the document to PDF so you can _print it.")
                }
        };

        editing = new String[][] {
                new String[] {
                        "Ctrl+C", _("Copy"), ""
                }, new String[] {
                        "Ctrl+X", _("Cut"), ""
                }, new String[] {
                        "Ctrl+V", _("Paste"), ""
                }, new String[] {
                        "Ctrl+Z", _("Undo"), ""
                }, new String[] {
                        "Ctrl+Y", _("Redo"), ""
                }, new String[] {
                        "Ctrl+PgUp", _("Next"), _("Go forward to the next chapter.")
                }, new String[] {
                        "Ctrl+PgDn", _("Previous"), _("Go back to the previous chapter.")
                }, new String[] {
                        "Ins", _("Insert Segment"), _("Insert a new block into the current chapter.")
                }, new String[] {
                        "Menu", _("Check Spelling"), _("Suggest alternate words.")
                }
        };

        markup = new String[][] {
                new String[] {
                        "Ctrl+Shift+\nSpace",
                        _("Clear!"),
                        _("Remove any inline formatting from the currently selected range.")
                },
                new String[] {
                        "Ctrl+I", _("Italics"), _("Make the text appear in _italics.")
                },
                new String[] {
                        "Ctrl+B", _("Bold"), _("Make the text appear in _bold face.")
                },
                new String[] {
                        "Ctrl+Shift+F", _("File"), _("A path on the _filesystem.")
                },
                new String[] {
                        "Ctrl+Shift+T",
                        _("Class or Type"),
                        _("The name of an object-oriented class or similar _type.")
                },
                new String[] {
                        "Ctrl+Shift+M",
                        _("Method or Function"),
                        _("The name of a _method or function call.")
                },
                new String[] {
                        "Ctrl+Shift+A",
                        _("Application"),
                        _("The proper name of a program, _application suite, or project.")
                },
                new String[] {
                        "Ctrl+Shift+O",
                        _("Command"),
                        _("The name of a c_ommand or program you'd run from the command line.")
                },
                new String[] {
                        "Ctrl+Shift+L",
                        _("Code fragment"),
                        _("An inline source code frament or _literal value.")
                },
                new String[] {
                        "Ctrl+Shift+H", _("Highlight"), _("Mark an inline work-in-progress comment.")
                },
        };

        group = new SizeGroup(HORIZONTAL);

        vbox = new VBox(false, 0);

        addHeading(_("View selection"));
        for (String[] line : views) {
            addHelpLine(line[0], line[1], line[2]);
        }

        addHeading(_("Actions"));
        for (String[] line : actions) {
            addHelpLine(line[0], line[1], line[2]);
        }

        addHeading(_("Editing"));
        for (String[] line : editing) {
            addHelpLine(line[0], line[1], line[2]);
        }

        top.packStart(vbox, false, false, 0);

        /*
         * Start second column
         */

        vbox = new VBox(false, 0);

        addHeading(_("Markup keys"));
        for (String[] line : markup) {
            addHelpLine(line[0], line[1], line[2]);
        }

        top.packStart(vbox, false, false, 0);
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

    private void addHelpLine(String keys, String title, String description) {
        Label label;
        HBox spread;
        StringBuilder str;

        spread = new HBox(false, 0);

        label = new Label(keys);
        label.setAlignment(LEFT, TOP);
        spread.packStart(label, false, false, 0);
        group.add(label);

        str = new StringBuilder();
        str.append("<b>");
        str.append(title);
        str.append("</b>");
        if (description != "") {
            str.append("\n");
            str.append(description);
        }

        label = new Label();
        label.setLabel(str.toString());
        label.setUseMarkup(true);
        label.setUseUnderline(true);
        label.setLineWrap(true);
        label.setSizeRequest(200, -1);
        label.setAlignment(LEFT, TOP);
        spread.packStart(label, false, false, 6);

        vbox.packStart(spread, false, false, 6);
    }
}
