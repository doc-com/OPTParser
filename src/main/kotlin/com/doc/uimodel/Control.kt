package com.doc.uimodel

import com.doc.TermDefinition
import org.openehr.schemas.v1.CDVQUANTITY
import org.openehr.schemas.v1.COBJECT
import org.openehr.schemas.v1.CQUANTITYITEM
import org.openehr.schemas.v1.IntervalOfInteger
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
    val quantityItems: List<CQUANTITYITEM>?,
    val codeList: List<String>?
) {

    data class Builder(
        val complexObject: COBJECT,
        val path: String,
        val orderInParent: Int,
        val term: TermDefinition,
        var type: String? = null,
        var quantityItems: List<CQUANTITYITEM>? = null,
        var codeList: List<String>? = null
    ) {

        fun type(type: String) = apply { this.type = type }

        fun quantityItems(quantity: COBJECT) = apply {
            val cdvquantity: CDVQUANTITY = CDVQUANTITY.Factory.parse(quantity.toString())
            this.quantityItems = cdvquantity.listArray.toList()
        }

        fun codeList(codeList: Array<String>, termDefinitions: Map<String, TermDefinition>) = apply {
            val codeValues: MutableList<String> = mutableListOf()
            codeList.forEach { code: String ->
                termDefinitions[code]?.let { codeValues.add(it.text) }
            }
            this.codeList = codeValues
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
            quantityItems,
            codeList
        )
    }
}