package com.doc

enum class CComplexObjectTypes(val type: String?) {
    CCOMPLEXOBJECT("C_COMPLEX_OBJECT"),
    COMPOSITION(org.openehr.schemas.v1.COMPOSITION::class.simpleName),
    OBSERVATION(org.openehr.schemas.v1.OBSERVATION::class.simpleName),
    SECTION(org.openehr.schemas.v1.SECTION::class.simpleName),
    EVALUATION(org.openehr.schemas.v1.EVALUATION::class.simpleName),
    ACTION(org.openehr.schemas.v1.ACTION::class.simpleName),
    INSTRUCTION(org.openehr.schemas.v1.INSTRUCTION::class.simpleName),
    HISTORY(org.openehr.schemas.v1.HISTORY::class.simpleName),
    EVENT(org.openehr.schemas.v1.EVENT::class.simpleName),
    ITEM_TREE("ITEM_TREE"),
    ELEMENT(org.openehr.schemas.v1.ELEMENT::class.simpleName),
    CDVQUANTITY("C_DV_QUANTITY"),
    DVCODEDTEXT("DV_CODED_TEXT")
}