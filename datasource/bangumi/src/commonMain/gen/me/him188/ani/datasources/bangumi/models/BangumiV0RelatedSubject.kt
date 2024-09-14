/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport",
)

package me.him188.ani.datasources.bangumi.models


import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 
 *
 * @param id
 * @param staff
 * @param nameCn
 * @param name
 * @param image 
 */
@Serializable

data class BangumiV0RelatedSubject(

    @SerialName(value = "id") @Required val id: kotlin.Int,

    @SerialName(value = "staff") @Required val staff: kotlin.String,

    @SerialName(value = "name_cn") @Required val nameCn: kotlin.String,

    @SerialName(value = "name") val name: kotlin.String? = null,

    @SerialName(value = "image") val image: kotlin.String? = null

)
