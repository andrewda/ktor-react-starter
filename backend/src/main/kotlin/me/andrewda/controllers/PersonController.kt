package me.andrewda.controllers

import me.andrewda.models.NewPerson
import me.andrewda.models.People
import me.andrewda.models.Person
import me.andrewda.utils.Database
import me.andrewda.utils.query

object PersonController {
    suspend fun create(person: NewPerson) = query {
        val byteContent = person.image?.toByteArray()
        val blob = if (byteContent != null) {
            Database.connection.connector().createBlob().apply {
                setBytes(1, byteContent)
            }
        } else {
            null
        }

        Person.new {
            name = person.name ?: ""
            image = blob
            bio = person.bio ?: ""
            slug = person.slug ?: ""
        }
    }

    suspend fun patch(slug: String, newPerson: NewPerson) = query {
        val person = Person.find { People.slug eq slug }.firstOrNull() ?: return@query null

        if (newPerson.name != null) person.name = newPerson.name
        if (newPerson.bio != null) person.bio = newPerson.bio
        if (newPerson.slug != null) person.slug = newPerson.slug
        if (newPerson.image != null) {
            val byteContent = newPerson.image.toByteArray()
            val blob = Database.connection.connector().createBlob().apply {
                setBytes(1, byteContent)
            }

            person.image = blob
        }

        person
    }

    suspend fun findAll() = query { Person.all().toList() }

    suspend fun findById(id: Int) = query { Person.findById(id) }

    suspend fun findBySlug(slug: String) = query { Person.find { People.slug eq slug }.firstOrNull() }
}
