package com.doc

import org.openehr.schemas.v1.*

class Constants {
    companion object {

        val typeValuesMap: Map<String?, String> =
            mapOf(
                CARCHETYPEROOT::class.simpleName to "C_ARCHETYPE_ROOT",
                CCOMPLEXOBJECT::class.simpleName to "C_COMPLEX_OBJECT",
                DVCODEDTEXT::class.simpleName to "DV_CODED_TEXT",
                DVQUANTITY::class.simpleName to "DV_QUANTITY",
                CDVQUANTITY::class.simpleName to "C_DV_QUANTITY"
            )
    }
}