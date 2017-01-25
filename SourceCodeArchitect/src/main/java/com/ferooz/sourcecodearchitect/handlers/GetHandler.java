package com.ferooz.sourcecodearchitect.handlers;

import org.eclipse.jdt.core.dom.ASTNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ferooz on 12/01/17.
 */
public class GetHandler {
    private  static Map<Integer, Handler> handlerMap;
    static {
        handlerMap = new HashMap<>();
    }
}
