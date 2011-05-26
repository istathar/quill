/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010-2011 Operational Dynamics Consulting, Pty Ltd
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

import java.util.List;

import org.freedesktop.cairo.Context;
import org.freedesktop.cairo.Surface;
import org.gnome.gdk.EventFocus;
import org.gnome.gdk.RGBA;
import org.gnome.gtk.Grid;
import org.gnome.gtk.Label;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.Scrollbar;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.StateFlags;
import org.gnome.gtk.Viewport;
import org.gnome.gtk.Widget;

import quill.textbase.AttributionSegment;
import quill.textbase.ChapterSegment;
import quill.textbase.Component;
import quill.textbase.DivisionSegment;
import quill.textbase.HeadingSegment;
import quill.textbase.ImageSegment;
import quill.textbase.LeaderSegment;
import quill.textbase.ListitemSegment;
import quill.textbase.NormalSegment;
import quill.textbase.PoeticSegment;
import quill.textbase.PreformatSegment;
import quill.textbase.QuoteSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Special;
import quill.textbase.SpecialSegment;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Left hand side of a PrimaryWindow for editing the main body of a chapter.
 * 
 * @author Andrew Cowie
 */
class MainSeriesEditorWidget extends SeriesEditorWidget
{
    private Component component;

    MainSeriesEditorWidget(PrimaryWindow primary) {
        super(primary);
        final Viewport port;

        /*
         * Set the background color of the entire EditorWidget to white in
         * order to hide the upper side of the horizontal Scrollbars in the
         * preformatted blocks. Finding that this was the right place was
         * traumatic, but it turns out that the Viewport has the underlying
         * [org.gnome.gdk] Window where the drawing happens. Annoyingly,
         * calling modifyBackground() on the ScolledWindow didn't work.
         */

        port = (Viewport) super.getChild();
        port.overrideBackground(StateFlags.NORMAL, RGBA.WHITE);
    }

    Component getComponent() {
        return component;
    }

    protected void createEditorForSegment(final int index, final Segment segment) {
        final Grid grid;
        final Editor editor;
        final EditorTextView view;
        final HeadingBox heading;
        final ImageDisplayBox image;
        final NormalListitemBox listitem;
        final Scrollbar bar;
        final ScrolledWindow wide;
        final List<Editor> editors;

        grid = super.getTop();

        if (segment instanceof NormalSegment) {
            view = new NormalEditorTextView(this, segment);

            editor = view;
            grid.attach(view, 0, index, 3, 1);
        } else if (segment instanceof QuoteSegment) {
            view = new QuoteEditorTextView(this, segment);

            editor = view;
            grid.attach(view, 0, index, 3, 1);
        } else if (segment instanceof PoeticSegment) {
            view = new PoeticEditorTextView(this, segment);

            editor = view;
            grid.attach(view, 0, index, 3, 1);
        } else if (segment instanceof ListitemSegment) {
            listitem = new NormalListitemBox(this, segment);

            editor = listitem;
            grid.attach(listitem, 0, index, 3, 1);
        } else if (segment instanceof AttributionSegment) {
            view = new AttributionEditorTextView(this, segment);

            editor = view;
            grid.attach(view, 0, index, 3, 1);
        } else if (segment instanceof PreformatSegment) {
            editor = setupPreformat(grid, index, segment);
        } else if (segment instanceof ImageSegment) {
            image = new ImageDisplayBox(this, segment);

            // FUTURE works, but needs upgrading
            editor = image.getEditor();
            grid.attach(image, 0, index, 3, 1);
        } else if (segment instanceof HeadingSegment) {
            editor = setupHeading(grid, index, segment, "Section");

            /*
             * heading = new SectionHeadingBox(this, segment);
             * 
             * // FUTURE correct now, but will change when we do labels editor
             * = heading.getTextView(); grid.attach(heading, 0, index, 3, 1);
             */
        } else if (segment instanceof LeaderSegment) {
            view = new LeaderEditorTextView(this, segment);

            editor = view;
            grid.attach(view, 0, index, 3, 1);
        } else if (segment instanceof ChapterSegment) {
            editor = setupHeading(grid, index, segment, "Chapter");
        } else if (segment instanceof DivisionSegment) {
            heading = new PartHeadingBox(this, segment);

            editor = heading.getTextView();
            grid.attach(heading, 0, index, 3, 1);
        } else if (segment instanceof SpecialSegment) {
            Widget widget;
            // TODO placeholder; improve!
            editor = null;
            widget = new SpecialHeadingBox(this, segment);
            grid.attach(widget, 0, index, 3, 1);
        } else {
            throw new AssertionError("Unknown Segment type");
        }

        editors = super.getEditors();
        editors.add(index, editor);
    }

    private Editor setupHeading(final Grid grid, final int index, final Segment segment,
        final String text) {
        final EditorTextView title;
        final Label label;

        title = new HeadingEditorTextView(this, segment);
        title.setExpandHorizontal(true);
        grid.attach(title, 0, index, 2, 1);

        label = new Label();
        label.setWidthChars(20);
        label.setUseMarkup(true);
        label.setLabel("<span color='gray'>" + text + "</span>");

        grid.attach(label, 2, index, 1, 1);

        return title;
    }

    private Editor setupPreformat(final Grid grid, final int index, final Segment segment) {
        final Scrollbar bar;
        final EditorTextView view;
        final ScrolledWindow wide;

        view = new PreformatEditorTextView(this, segment);
        view.setExpandHorizontal(true);
        wide = new ScrolledWindow();
        wide.setPolicy(PolicyType.ALWAYS, PolicyType.NEVER);
        wide.add(view);

        /*
         * Having set up horizontal scrollbars for code blocks, we want to
         * make them a bit less obtrusive in normal use.
         */

        bar = wide.getHScrollbar();
        view.connect(new Widget.FocusInEvent() {
            public boolean onFocusInEvent(Widget source, EventFocus event) {
                bar.setSensitive(true);
                return false;
            }
        });
        view.connect(new Widget.FocusOutEvent() {
            public boolean onFocusOutEvent(Widget source, EventFocus event) {
                bar.setValue(0);
                bar.setSensitive(false);
                return false;
            }
        });

        bar.setSensitive(false);

        bar.connect(new Widget.Draw() {
            public boolean onDraw(Widget source, Context cr) {
                final Surface surface;

                if (bar.getSensitive()) {
                    return false;
                }

                cr.setSource(RGBA.WHITE);
                cr.paint();

                surface = cr.getTarget();
                surface.flush();

                return true;
            }
        });

        grid.attach(wide, 0, index, 3, 1);

        return view;
    }

    /*
     * Convenience wrappers
     */

    void initialize(Component component) {
        final Series series;

        if (component == this.component) {
            return;
        }

        series = component.getSeriesMain();
        super.initializeSeries(series);

        this.component = component;
    }

    void advanceTo(Component replacement) {
        final Series series;

        if (replacement == this.component) {
            return;
        }

        series = replacement.getSeriesMain();
        super.advanceTo(series);

        this.component = replacement;
    }

    void reverseTo(Component replacement) {
        final Series series;

        if (replacement == this.component) {
            return;
        }

        series = replacement.getSeriesMain();
        super.reveseTo(series);

        this.component = replacement;
    }

    void propegateTextualChange(final PrimaryWindow primary, final Series former,
        final Series replacement) {
        final Component apres;

        apres = component.updateMain(replacement);
        primary.update(this, component, apres);
    }

    void propegateStructuralChange(final PrimaryWindow primary, final Series former,
        final Series replacement) {
        final Component apres;

        apres = component.updateMain(replacement);
        primary.update(this, component, apres);
    }

    private static final InsertMenuDetails[] details;

    private static final UncommonMenuDetails[] uncommon;

    static {
        details = new InsertMenuDetails[] {
            new InsertMenuDetails(NormalSegment.class, _("Text _paragraphs"), _("normal wrapped text")),
            new InsertMenuDetails(PreformatSegment.class, _("_Program code"),
                    _("formating preserved; monospaced")),
            new InsertMenuDetails(QuoteSegment.class, _("Block _quote"),
                    _("normal wrapped text, but indented")),
            new InsertMenuDetails(PoeticSegment.class, _("Poe_m"), _("formating preserved")),
            new InsertMenuDetails(ListitemSegment.class, _("_List item"), _("normal wrapped text")),
            new InsertMenuDetails(AttributionSegment.class, _("_Attribution"),
                    _("smaller wrapped text, offset right")),
            new InsertMenuDetails(HeadingSegment.class, _("Section _heading"),
                    _("bold text, single line"))
        };

        uncommon = new UncommonMenuDetails[] {
            new UncommonMenuDetails(Special.NOTE, _("End_note Anchor"),
                    _("Note that will be grouped by chapter")),
            new UncommonMenuDetails(Special.CITE, _("_Reference Citation"),
                    _("Reference that will appear at the end of the work")),
        };

    }

    InsertMenuDetails[] getMenuDetailsInsert() {
        return details;
    }

    UncommonMenuDetails[] getMenuDetailsUncommon() {
        return uncommon;
    }
}
