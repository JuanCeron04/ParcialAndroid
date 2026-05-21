package com.example.parcial.data.repository

import com.example.parcial.data.local.CharacterDao
import com.example.parcial.data.model.Character
import com.example.parcial.data.remote.RickAndMortyApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepository @Inject constructor(
    private val api: RickAndMortyApi,
    private val dao: CharacterDao
) {
    fun getCharacters(): Flow<Result<List<Character>>> = flow {
        // First, emit what we have in cache
        val localData = dao.getAllCharacters().first()
        if (localData.isNotEmpty()) {
            emit(Result.success(localData))
        }

        try {
            val remoteResponse = api.getCharacters()
            dao.clearAll()
            dao.insertCharacters(remoteResponse.results)
            emit(Result.success(remoteResponse.results))
        } catch (e: Exception) {
            if (localData.isEmpty()) {
                emit(Result.failure(e))
            }
        }
    }

    suspend fun getCharacter(id: Int): Result<Character> {
        return try {
            val localCharacter = dao.getCharacterById(id)
            if (localCharacter != null) {
                Result.success(localCharacter)
            } else {
                val remoteCharacter = api.getCharacter(id)
                Result.success(remoteCharacter)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}