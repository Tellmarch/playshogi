package com.playshogi.website.gwt.client.util;

import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.Element;
import jsinterop.base.Js;

public class ElementWidget extends Widget {
    public ElementWidget(Element element) {
        setElement(Js.<com.google.gwt.dom.client.Element>uncheckedCast(element));
    }
}
