package no.ntnu.beardblaster

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.GamePlayer
import no.ntnu.beardblaster.commons.leaderboard.AbstractLeaderBoardRepository
import no.ntnu.beardblaster.commons.leaderboard.BeardScore

class LeaderBoardRepository(private val db: FirebaseFirestore = Firebase.firestore) :
    AbstractLeaderBoardRepository<Game> {
    private val TAG = "LeaderBoardRepository"
    private val BEARDS_COLLECTION = "beards"

    override fun getTopTenBeards(): Flow<State<List<BeardScore>>> = flow {
        emit(State.loading<List<BeardScore>>())

        val snapshot = db
            .collection(BEARDS_COLLECTION)
            .orderBy("beardLength", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()

        if (!snapshot.isEmpty) {
            val leaderBoardList: MutableList<BeardScore> = mutableListOf()
            for (doc: DocumentSnapshot in snapshot.documents) {
                val score = doc.toObject<BeardScore>()
                if (score != null) {
                    leaderBoardList.add(score)
                }
            }
            emit(State.success(leaderBoardList.toList()))
        } else {
            emit(State.failed<List<BeardScore>>("Could not fetch leaderboard"))
        }
    }

    override fun updateBeardLength(user: GamePlayer, newLength: Float): Flow<State<BeardScore>> =
        flow {
            emit(State.loading<BeardScore>())
            val doc = BeardScore(
                newLength,
                user.displayName,
                user.id
            )
            val documentReference = db.collection(BEARDS_COLLECTION)
                .document(user.id)

            documentReference.set(doc).await()
            Log.d(
                TAG,
                "Posted new beardlength $newLength cm of user ${user.displayName} successfully"
            )

            emit(State.success(doc))
        }
}
