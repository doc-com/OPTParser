package com.doc.uimodel

import com.doc.CComplexObjectTypes
import com.doc.TermDefinition
import org.apache.xmlbeans.SimpleValue
import org.openehr.schemas.v1.*
import java.net.URLDecoder
import java.util.*

class Control private constructor(
    val id: String?,
    val path: String?,
    val rmTypeName: String?,
    val occurrences: IntervalOfInteger?,
    val label: String?,
    val description: String?,
    val orderInParent: Int?,
    val termDefinition: TermDefinition?,
    val type: String?,
    val formalism: List<String>?,
    val range: Interval?,
    val datePattern: String?,
    val quantityItems: List<CQUANTITYITEM>?,
    val ordinalList: List<OrdinalTuple>?,
    val codeList: List<String>?,
    val codedItems: List<TerminologyTuple>?,
    val referenceSetUri: TerminologyReference?
) {

    data class Builder(
        val complexObject: COBJECT,
        val path: String,
        val orderInParent: Int,
        val term: TermDefinition,
        var type: String? = null,
        var formalism: List<String>? = null,
        var range: Interval? = null,
        var datePattern: String? = null,
        var quantityItems: List<CQUANTITYITEM>? = null,
        var ordinalList: List<OrdinalTuple>? = null,
        var codeList: List<String>? = null,
        var codedItems: List<TerminologyTuple>? = null,
        var referenceSetUri: TerminologyReference? = null
    ) {

        fun type(type: String) = apply { this.type = type }

        fun nullFlavour(nullFlavor: CATTRIBUTE) = apply {
            val codedText: DVCODEDTEXT
        }

        fun formalism(complexObject: CCOMPLEXOBJECT) {
            if (!complexObject.attributesArray.isNullOrEmpty() && !complexObject.attributesArray[0].childrenArray.isNullOrEmpty()) {
                val strObjectSelect = complexObject.attributesArray[0].childrenArray[0].selectChildren(
                    "http://schemas.openehr.org/v1",
                    "item"
                )
                if (!strObjectSelect.isNullOrEmpty()) {
                    val str: CSTRING = CSTRING.Factory.parse(strObjectSelect[0].toString())
                    this.formalism = str.listArray.toList()
                }
            }
        }

        fun codedText(complexObject: CCOMPLEXOBJECT, section: Section) = apply {
            if (complexObject.attributesArray.isNotEmpty()) {
                if (complexObject.attributesArray[0].rmAttributeName == CComplexObjectTypes.DEFINING_CODE.type) {
                    val codePhrase: CCODEPHRASE =
                        CCODEPHRASE.Factory.parse(complexObject.attributesArray[0].childrenArray[0].toString())

                    val referenceSetQuery =
                        codePhrase.selectChildren("http://schemas.openehr.org/v1", "referenceSetUri")
                    if (referenceSetQuery.isNotEmpty()) {
                        val referenceSetUri = (referenceSetQuery[0] as SimpleValue).stringValue
                        if (referenceSetUri.contains("ecl")) {
                            this.type(ControlType.CONSTRAINED_TEXT.type)
                            this.referenceSetUri(referenceSetUri)
                        }

                        if (referenceSetUri.contains("list")) {
                            this.type(ControlType.EXTERNAL_CODED_TEXT.type)
                            this.codeList(referenceSetUri)
                        }
                    }

                    if (codePhrase.codeListArray != null && codePhrase.codeListArray.isNotEmpty()) {
                        this.type(ControlType.INTERNAL_CODED_TEXT.type)
                        this.codeList(codePhrase.codeListArray, section.termDefinitions)
                    }
                }
            }
        }

        fun datePattern(complexObject: CCOMPLEXOBJECT) = apply {
            when (complexObject.rmTypeName) {
                CComplexObjectTypes.DV_DATE_TIME.type -> {
                    val filter =
                        complexObject.attributesArray.filter { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == CComplexObjectTypes.VALUE.type }
                    if (!filter.isNullOrEmpty()) {
                        val attribute = filter[0]
                        if (attribute.childrenArray.isNotEmpty()) {
                            val dateTime: CDATETIME = CDATETIME.Factory.parse(attribute.childrenArray[0].toString())
                            if (dateTime.isSetPattern) this.datePattern = dateTime.pattern
                        }
                    }
                }

                CComplexObjectTypes.DV_DATE.type -> {
                    val filter =
                        complexObject.attributesArray.filter { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == CComplexObjectTypes.VALUE.type }
                    if (!filter.isNullOrEmpty()) {
                        val attribute = filter[0]
                        if (attribute.childrenArray.isNotEmpty()) {
                            val dateTime: CDATE = CDATE.Factory.parse(attribute.childrenArray[0].toString())
                            if (dateTime.isSetPattern) this.datePattern = dateTime.pattern
                        }
                    }
                }
            }
        }

        fun range(complexObject: CCOMPLEXOBJECT) = apply {
            when (complexObject.rmTypeName) {

                CComplexObjectTypes.DV_COUNT.type -> {
                    val filter =
                        complexObject.attributesArray.filter { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == CComplexObjectTypes.MAGNITUDE.type }
                    if (!filter.isNullOrEmpty()) {
                        val attribute = filter[0]
                        if (attribute.childrenArray.isNotEmpty()) {
                            val integer: CINTEGER =
                                CINTEGER.Factory.parse(attribute.childrenArray[0].toString())
                            if (integer.isSetRange) this.range = integer.range
                        }
                    }
                }

                CComplexObjectTypes.DV_DURATION.type -> {
                    val filter =
                        complexObject.attributesArray.filter { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == CComplexObjectTypes.VALUE.type }
                    if (!filter.isNullOrEmpty()) {
                        val attribute = filter[0]
                        if (attribute.childrenArray.isNotEmpty()) {
                            val duration: CDURATION = CDURATION.Factory.parse(attribute.childrenArray[0].toString())
                            if (duration.isSetRange) this.range = duration.range
                        }
                    }
                }
            }
        }

        fun quantityItems(quantity: COBJECT) = apply {
            val cdvquantity: CDVQUANTITY = CDVQUANTITY.Factory.parse(quantity.toString())
            this.quantityItems = cdvquantity.listArray.toList()
        }

        fun ordinalList(ordinal: CDVORDINAL, termDefinitions: Map<String, TermDefinition>?) = apply {
            val ordinalList: MutableList<OrdinalTuple> = mutableListOf()
            if (ordinal.listArray.isNotEmpty() && termDefinitions != null)
                ordinal.listArray.forEach {
                    ordinalList.add(
                        OrdinalTuple(
                            it.value,
                            it.symbol.definingCode.terminologyId.value,
                            termDefinitions[it.symbol.definingCode.codeString]?.text ?: "",
                            it.symbol.definingCode.codeString
                        )
                    )
                }
            this.ordinalList = ordinalList
        }

        fun codeList(codeList: Array<String>, termDefinitions: Map<String, TermDefinition>) = apply {
            val codeValues: MutableList<String> = mutableListOf()
            codeList.forEach { code: String ->
                termDefinitions[code]?.let { codeValues.add(it.text) }
            }
            this.codeList = codeValues
        }

        fun codeList(referenceSetList: String) = apply {
            val decoded = URLDecoder.decode(referenceSetList, java.nio.charset.StandardCharsets.UTF_8.toString())
            val list = decoded.substring(decoded.indexOf("=list") + 6).split("|").dropLast(1)
            val codeValues: MutableList<TerminologyTuple> = mutableListOf()
            list.forEach { tuple: String ->
                val split = tuple.split("/")
                codeValues.add(TerminologyTuple(split[0].trim(), split[1].trim()))
            }
            this.codedItems = codeValues
        }

        fun referenceSetUri(referenceSetUri: String) = apply {
            val split = referenceSetUri.substring(referenceSetUri.indexOf(":")).split("=")
            val termSplit = split[0].substring(0, split[0].indexOf("?")).split("/")
            val terminologyId: String = termSplit[0]
            val terminologyRelease: String = termSplit[1]
            val referenceSet: String = split[1]
            this.referenceSetUri = TerminologyReference(terminologyId, terminologyRelease, referenceSet)
        }

        fun build() = Control(
            UUID.randomUUID().toString(),
            path,
            complexObject.rmTypeName,
            complexObject.occurrences,
            term.text,
            term.description,
            orderInParent,
            term,
            type,
            formalism,
            range,
            datePattern,
            quantityItems,
            ordinalList,
            codeList,
            codedItems,
            referenceSetUri
        )
    }
}