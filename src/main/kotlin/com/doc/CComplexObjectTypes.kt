package com.doc

enum class CComplexObjectTypes(val type: String?) {
    C_COMPLEX_OBJECT("C_COMPLEX_OBJECT"),
    C_ARCHETYPE_ROOT("C_ARCHETYPE_ROOT"),
    ARCHETYPE_SLOT("ARCHETYPE_SLOT"),
    C_SINGLE_ATTRIBUTE("C_SINGLE_ATTRIBUTE"),
    COMPOSITION(org.openehr.schemas.v1.COMPOSITION::class.simpleName),
    OBSERVATION(org.openehr.schemas.v1.OBSERVATION::class.simpleName),
    SECTION(org.openehr.schemas.v1.SECTION::class.simpleName),
    EVALUATION(org.openehr.schemas.v1.EVALUATION::class.simpleName),
    ACTION(org.openehr.schemas.v1.ACTION::class.simpleName),
    INSTRUCTION(org.openehr.schemas.v1.INSTRUCTION::class.simpleName),
    ACTIVITY(org.openehr.schemas.v1.ACTIVITY::class.simpleName),
    HISTORY(org.openehr.schemas.v1.HISTORY::class.simpleName),
    EVENT(org.openehr.schemas.v1.EVENT::class.simpleName),
    POINT_EVENT("POINT_EVENT"),
    ITEM_TREE("ITEM_TREE"),
    ITEM_LIST("ITEM_LIST"),
    ITEM_STRUCTURE("ITEM_STRUCTURE"),
    CLUSTER(org.openehr.schemas.v1.CLUSTER::class.simpleName),
    ELEMENT(org.openehr.schemas.v1.ELEMENT::class.simpleName),
    C_DV_QUANTITY("C_DV_QUANTITY"),
    DV_COUNT("DV_COUNT"),
    DV_CODED_TEXT("DV_CODED_TEXT"),
    DV_TEXT("DV_TEXT"),
    DV_DATE_TIME("DV_DATE_TIME"),
    DV_DATE("DV_DATE"),
    C_DV_ORDINAL("C_DV_ORDINAL"),
    DV_ORDINAL("DV_ORDINAL"),
    DV_BOOLEAN("DV_BOOLEAN"),
    DV_DURATION("DV_DURATION"),
    DV_PARSABLE("DV_PARSABLE"),
    //RM_ATTRIBUTE_NAMES
    DEFINING_CODE("defining_code"),
    VALUE("value"),
    MAGNITUDE("magnitude"),
    NULL_FLAVOUR("null_flavour")

}