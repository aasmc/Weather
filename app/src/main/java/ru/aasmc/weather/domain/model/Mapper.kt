package ru.aasmc.weather.domain.model

interface Mapper<Domain, Entity> {

    fun mapToDomain(type: Entity): Domain

    fun mapFromDomain(type: Domain): Entity

}