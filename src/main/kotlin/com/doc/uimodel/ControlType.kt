package com.doc.uimodel

enum class ControlType(val type: String) {
    FREE_TEXT("FREE_TEXT"),
    LONG_TEXT("LONG_TEXT"),
    INTERNAL_CODED_TEXT("INTERNAL_CODED_TEXT"),
    EXTERNAL_CODED_TEXT("EXTERNAL_CODED_TEXT"),
    CONSTRAINED_TEXT("CONSTRAINED_TEXT"),
    QUANTITY("QUANTITY"),
    COUNT("COUNT"),
    DATE("DATE"),
    DATETIME("DATETIME"),
    ORDINAL("ORDINAL"),
    BOOLEAN_CHECK("BOOLEAN_CHECK"),
    DURATION("DURATION"),
    INTEGER_INTERVAL("INTEGER_INTERVAL"),
    QUANTITY_INTERVAL("QUANTITY_INTERVAL"),
    DATETIME_INTERVAL("DATETIME_INTERVAL"),
    MULTIMEDIA("MULTIMEDIA"),
    URI("URI"),
    PROPORTION("PROPORTION"),
    IDENTIFIER("IDENTIFIER"),
    PARSABLE("PARSABLE")
}