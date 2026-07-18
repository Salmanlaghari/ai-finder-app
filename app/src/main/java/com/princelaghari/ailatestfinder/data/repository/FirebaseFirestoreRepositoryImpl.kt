package com.princelaghari.ailatestfinder.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.princelaghari.ailatestfinder.data.datasource.MockDataSource
import com.princelaghari.ailatestfinder.data.local.dao.AiToolDao
import com.princelaghari.ailatestfinder.data.local.entity.AiToolEntity
import com.princelaghari.ailatestfinder.domain.model.AiTool
import com.princelaghari.ailatestfinder.domain.repository.AiToolRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFirestoreRepositoryImpl @Inject constructor(
    private val aiToolDao: AiToolDao
) : AiToolRepository {

    private val TAG = "FirestoreRepo"

    @Suppress("UNCHECKED_CAST")
    override fun getAiTools(): Flow<List<AiTool>> {
        // Safe access to Firebase Firestore
        val firestore: FirebaseFirestore? = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase not initialized. Falling back to local dataset.", e)
            null
        }

        // Return unified reactive stream: emits database cache immediately, then syncs with Firebase
        return flow {
            // Step 1: Immediately emit local Room cache on subscription
            val cachedEntities = aiToolDao.getAllAiTools()
            if (cachedEntities.isNotEmpty()) {
                emit(cachedEntities.map { it.toDomain() })
            } else {
                // If cache is empty, load our massive robust Mock list first so app opens instantly with hundreds of entries
                val defaultEntities = MockDataSource.aiTools.map { AiToolEntity.fromDomain(it) }
                aiToolDao.insertAll(defaultEntities)
                emit(MockDataSource.aiTools)
            }

            // Step 2: Set up live background sync with Firestore if initialized
            if (firestore != null) {
                val remoteFlow = callbackFlow<List<AiTool>> {
                    val listenerRegistration = firestore.collection("ai_tools")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Log.e(TAG, "Error fetching from Firestore.", error)
                                return@addSnapshotListener
                            }

                            if (snapshot != null && !snapshot.isEmpty) {
                                val tools = snapshot.documents.mapNotNull { doc ->
                                    try {
                                        val id = doc.id
                                        val name = doc.getString("name") ?: ""
                                        val category = doc.getString("category") ?: ""
                                        val description = doc.getString("description") ?: ""
                                        val imageUrl = doc.getString("imageUrl") ?: ""
                                        val toolUrl = doc.getString("toolUrl") ?: ""

                                        // Parse expanded premium attributes safely with default fallbacks
                                        val pricing = doc.getString("pricing") ?: "Freemium"
                                        val platforms = (doc.get("platforms") as? List<String>) ?: listOf("Web")
                                        val developer = doc.getString("developer") ?: "AI Community"
                                        val company = doc.getString("company") ?: "AI Corp"
                                        val status = doc.getString("status") ?: "Verified"
                                        val launchYear = doc.getString("launchYear") ?: "2024"
                                        val tags = (doc.get("tags") as? List<String>) ?: emptyList()
                                        val alternatives = (doc.get("alternatives") as? List<String>) ?: emptyList()

                                        AiTool(
                                            id = id,
                                            name = name,
                                            category = category,
                                            description = description,
                                            imageUrl = imageUrl,
                                            toolUrl = toolUrl,
                                            pricing = pricing,
                                            platforms = platforms,
                                            developer = developer,
                                            company = company,
                                            status = status,
                                            launchYear = launchYear,
                                            tags = tags,
                                            alternatives = alternatives
                                        )
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Failed to parse document: ${doc.id}", e)
                                        null
                                    }
                                }
                                if (tools.isNotEmpty()) {
                                    // Cache newly received items in Room Database to keep replica in absolute sync
                                    val entities = tools.map { AiToolEntity.fromDomain(it) }
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            aiToolDao.deleteAll()
                                            aiToolDao.insertAll(entities)
                                            Log.d(TAG, "Room local cache successfully updated to perfectly replicate the primary Firestore database (${tools.size} tools).")
                                        } catch (e: Exception) {
                                            Log.e(TAG, "Failed to update Room cache from Firestore", e)
                                        }
                                    }
                                    trySend(tools)
                                }
                            }
                        }

                    awaitClose {
                        listenerRegistration.remove()
                    }
                }

                emitAll(remoteFlow)
            }
        }.flowOn(Dispatchers.IO) // Enforce offloading to IO threads
    }
}
