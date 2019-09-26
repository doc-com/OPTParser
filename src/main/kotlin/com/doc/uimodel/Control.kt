package com.doc.uimodel

import com.doc.TermDefinition
import com.sun.media.jfxmedia.track.Track
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
    val range: Interval?,
    var datePattern: String?,
    val quantityItems: List<CQUANTITYITEM>?,
    var ordinalList: List<OrdinalTuple>?,
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
        var range: Interval? = null,
        var datePattern: String? = null,
        var quantityItems: List<CQUANTITYITEM>? = null,
        var ordinalList: List<OrdinalTuple>? = null,
        var codeList: List<String>? = null,
        var codedItems: List<TerminologyTuple>? = null,
        var referenceSetUri: TerminologyReference? = null
    ) {

        fun type(type: String) = apply { this.type = type }

        fun datePattern(datePattern: String) = apply { this.datePattern = datePattern }

        fun range(interval: Interval) {
            this.range = interval
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