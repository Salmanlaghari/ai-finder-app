package com.princelaghari.ailatestfinder.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.princelaghari.ailatestfinder.data.datasource.MockDataSource
import com.princelaghari.ailatestfinder.domain.model.AiTool
import com.princelaghari.ailatestfinder.domain.repository.AiToolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFirestoreRepositoryImpl @Inject constructor() : AiToolRepository {

    private val TAG = "FirestoreRepo"

    override fun getAiTools(): Flow<List<AiTool>> {
        // Safe access to Firebase Firestore
        val firestore: FirebaseFirestore? = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase not initialized. Falling back to local dataset.", e)
            null
        }

        if (firestore == null) {
            // Safe fallback flow yielding high-quality mock data, offloaded to IO dispatcher
            return flow {
                emit(MockDataSource.aiTools)
            }.flowOn(Dispatchers.IO)
        }

        return callbackFlow {
            val listenerRegistration = firestore.collection("ai_tools")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error fetching from Firestore, falling back to mock data.", error)
                        trySend(MockDataSource.aiTools)
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
                                AiTool(id, name, category, description, imageUrl, toolUrl)
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to parse document: ${doc.id}", e)
                                null
                            }
                        }
                        if (tools.isNotEmpty()) {
                            trySend(tools)
                        } else {
                            trySend(MockDataSource.aiTools)
                        }
                    } else {
                        // Empty snapshot, return mock data as default catalog
                        trySend(MockDataSource.aiTools)
                    }
                }

            awaitClose {
                listenerRegistration.remove()
            }
        }.flowOn(Dispatchers.IO) // Offload snapshot operations to background IO threads
    }
}
