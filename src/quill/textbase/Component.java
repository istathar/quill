/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2011 Operational Dynamics Consulting, Pty Ltd
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
package quill.textbase;

/**
 * Our in-memory representation of a loaded Chapter. This is an array of
 * Segments, broken up into the portion that is the main body, the endnotes
 * (if any), and the references (if any).
 * 
 * 
 * This was originally Series and returned by Chapter's
 * {@link parchment.manuscript.Chapter#loadDocument()}, but we've devolved
 * Series to be specifically an array of Segments with Component now being
 * returned by that loader.
 * 
 * @author Andrew Cowie
 */
// immutable
public class Component
{
    private final Series mainbody;

    private final Series endnotes;

    private final Series references;

    public Component(Series mainbody, Series endnotes, Series references) {
        this.mainbody = mainbody;
        this.endnotes = endnotes;
        this.references = references;
    }

    public Series getSeriesMain() {
        return mainbody;
    }

    public Series getSeriesEndnotes() {
        return endnotes;
    }

    public Series getSeriesReferences() {
        return references;
    }

    public Component updateMain(Series series) {
        return new Component(series, this.endnotes, this.references);
    }

    public Component updateEndnotes(Series series) {
        return new Component(this.mainbody, series, this.references);
    }

    public Component updateReferences(Series series) {
        return new Component(this.mainbody, this.endnotes, series);
    }
}
