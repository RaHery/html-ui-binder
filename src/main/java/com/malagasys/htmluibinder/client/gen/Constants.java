package com.malagasys.htmluibinder.client.gen;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Constants {
    public final static Set<String> TRANSFERABLE_ATTRIBUTES;
    static {
        // https://developer.mozilla.org/fr/docs/Web/HTML/Attributes
        HashSet<String> attrs = new HashSet<String>();
        attrs.add("accesskey");
        attrs.add("align");
        attrs.add("alt");
        attrs.add("autocomplete");
        attrs.add("autofocus");
        attrs.add("autosave");
        attrs.add("checked");
        attrs.add("class");
        attrs.add("cols");
        attrs.add("contenteditable");
        attrs.add("contextmenu");
        attrs.add("disabled");
        attrs.add("download");
        attrs.add("draggable");
        attrs.add("dropzone");
        attrs.add("height");
        attrs.add("hidden");
        attrs.add("href");
        attrs.add("list");
        attrs.add("max");
        attrs.add("maxlength");
        attrs.add("min");
        attrs.add("multiple");
        attrs.add("name");
        attrs.add("pattern");
        attrs.add("placeholder");
        attrs.add("readonly");
        attrs.add("required");
        attrs.add("rows");
        attrs.add("spellcheck");
        attrs.add("size");
        attrs.add("srcset");
        attrs.add("step");
        attrs.add("style");
        attrs.add("tabindex");
        attrs.add("target");
        attrs.add("title");
        attrs.add("value");
        attrs.add("width");
        attrs.add("wrap");

        TRANSFERABLE_ATTRIBUTES = Collections.unmodifiableSet(attrs);
    }

    private Constants() {
    }
}
