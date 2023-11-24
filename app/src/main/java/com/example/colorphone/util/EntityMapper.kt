package com.example.colorphone.util

interface EntityMapper<E,M> {
    fun mapFromEntity(entity :E):M
    fun mapToEntity(domainModel :M): E
    fun mapFromList(list: List<E>):List<M>
}