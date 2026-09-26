package com.zelix.klassmaster.script;

import com.zelix.klassmaster.exceptions.ParserLookaheadMarker;

public class ZkmScriptParserLookaheadSuccess extends Error {
    private ZkmScriptParserLookaheadSuccess() {
    }

    public ZkmScriptParserLookaheadSuccess(ParserLookaheadMarker parserLookaheadMarker) {
        this();
    }
}
