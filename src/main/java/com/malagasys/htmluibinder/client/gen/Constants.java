package com.malagasys.htmluibinder.client.gen;

import java.util.Set;

import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet.Builder;

public class Constants {
    public final static Set<String> TRANSFERABLE_ATTRIBUTES;
    static {
        // https://developer.mozilla.org/fr/docs/Web/HTML/Attributes
        Builder<String> builder = ImmutableSet.builder();
        builder.add("accesskey");
        builder.add("align");
        builder.add("alt");
        builder.add("autocomplete");
        builder.add("autofocus");
        builder.add("autosave");
        builder.add("checked");
        builder.add("class");
        builder.add("cols");
        builder.add("contenteditable");
        builder.add("contextmenu");
        builder.add("disabled");
        builder.add("download");
        builder.add("draggable");
        builder.add("dropzone");
        builder.add("height");
        builder.add("hidden");
        builder.add("href");
        builder.add("list");
        builder.add("max");
        builder.add("maxlength");
        builder.add("min");
        builder.add("multiple");
        builder.add("name");
        builder.add("pattern");
        builder.add("placeholder");
        builder.add("readonly");
        builder.add("required");
        builder.add("rows");
        builder.add("spellcheck");
        builder.add("size");
        builder.add("srcset");
        builder.add("step");
        builder.add("style");
        builder.add("tabindex");
        builder.add("target");
        builder.add("title");
        builder.add("value");
        builder.add("width");
        builder.add("wrap");
        TRANSFERABLE_ATTRIBUTES = builder.build();
    }

    private Constants() {
    }
}
