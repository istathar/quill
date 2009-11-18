/*
 * Segment.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

import java.io.FileNotFoundException;

import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Label;
import org.gnome.gtk.Stock;

/**
 * Segments are the block level grouping mechanism in textbase. Concrete
 * instances of this represent the entities we model as UI blocks each with
 * their own customized EditorWidget.
 * 
 * <p>
 * Note that some of these logical structures may correspond to more than one
 * underlying DocBook block level element; see {@link NormalSegment}.
 * 
 * <p>
 * Segment itself does not provide a chaining or sequencing mechanism; impose
 * that locally with a Segment[] (or a more complicated data structure as
 * necessary) locally, or if passing it further, wrapping it in a Series.
 * 
 * @author Andrew Cowie
 */
public abstract class Segment
{
    private Series parent;

    protected Segment() {}

    void setParent(Series series) {
        this.parent = series;
    }

    public Series getParent() {
        return parent;
    }

    /**
     * The internal representation of the text body of this Segment.
     */
    private TextChain chain;

    public TextChain getText() {
        return chain;
    }

    public void setText(TextChain chain) {
        this.chain = chain;
        chain.setEnclosingSegment(this);
    }

    abstract Segment createSimilar();

    /*
     * FIXME Using Pixbuf here is wrong. We should be using something less
     * instantiated GTK specific; perhaps a filename wrapper?
     */
    private Pixbuf image;

    /*
     * This is called by RenderEngine...
     */
    public Pixbuf getImage() {
        return image;
    }

    public void setImage(Pixbuf pixbuf) {
        this.image = pixbuf;
    }

    public void setImage(String filename) {
        /*
         * Totally wrong place for this
         */
        try {
            this.image = new Pixbuf(filename);
        } catch (FileNotFoundException e) {
            this.image = Gtk.renderIcon(new Label(), Stock.CANCEL, IconSize.DIALOG);
        }
    }
}
