package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        PoemEntity::class,
        ArtworkEntity::class,
        GameScoreEntity::class,
        ParentalSettingsEntity::class,
        FriendRequestEntity::class,
        ReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun poemDao(): PoemDao
    abstract fun artworkDao(): ArtworkDao
    abstract fun gameScoreDao(): GameScoreDao
    abstract fun parentalDao(): ParentalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ishaqxadey_doom_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedInitialData(database)
                }
            }
        }

        private suspend fun seedInitialData(database: AppDatabase) {
            // Seed current user
            val currentUser = UserEntity(
                id = "user_current",
                name = "Ayaan & Safiya",
                email = "family@ishaqxadey.com",
                avatar = "🦁",
                bio = "We love drawing, solving math puzzles, and learning poems together!",
                isChild = true,
                isParentVerified = true,
                parentPin = "1234",
                xp = 420,
                stars = 48,
                gems = 95,
                learningScore = 96,
                streakDays = 6,
                badges = "First Art,Math Genius,Bookworm,Bubble Master,Star Explorer",
                role = "child"
            )
            database.userDao().insertUser(currentUser)

            // Seed other community users
            val users = listOf(
                UserEntity(id = "user_2", name = "Zayd Explorer", email = "zayd@kids.com", avatar = "🚀", bio = "Future Astronaut & Space Explorer!", xp = 680, stars = 72, gems = 140, learningScore = 98, streakDays = 12, role = "child"),
                UserEntity(id = "user_3", name = "Hodan Artist", email = "hodan@kids.com", avatar = "🎨", bio = "Master of Creative Studio & Colors", xp = 540, stars = 61, gems = 110, learningScore = 94, streakDays = 8, role = "child"),
                UserEntity(id = "user_4", name = "Khadija Reader", email = "khadija@kids.com", avatar = "🦉", bio = "I love reading Islamic and Bedtime poems", xp = 390, stars = 40, gems = 80, learningScore = 91, streakDays = 4, role = "child")
            )
            users.forEach { database.userDao().insertUser(it) }

            // Seed poems across 7 categories
            val poems = listOf(
                PoemEntity(
                    title = "A Friend Like You",
                    author = "Ayaan S.",
                    category = "Friendship",
                    content = "A friend is like a shining star,\nGuiding you from near and far.\nWe share our toys, we laugh and play,\nAnd brighten up each sunny day!\nWith smiles and kindness in our heart,\nTrue friends will never grow apart.",
                    isFavorite = true,
                    readsCount = 34
                ),
                PoemEntity(
                    title = "The Whispering Trees",
                    author = "Safiya M.",
                    category = "Nature",
                    content = "The tall green trees touch the azure sky,\nWatching the gentle white clouds drift by.\nThe little birds sing on branches so high,\nWhispering secrets to breezes nearby.\nThank you earth for the grass and trees,\nAnd the gentle, cooling, dancing breeze!",
                    isFavorite = true,
                    readsCount = 42
                ),
                PoemEntity(
                    title = "Alphabet Adventure",
                    author = "Teacher Amina",
                    category = "Educational",
                    content = "A is for Apple, red and sweet,\nB is for Ball that bounces down the street.\nC is for Cat that loves to purr,\nD is for Dog with fluffy fur!\nFrom A to Z we learn with glee,\nKnowledge will set our spirits free!",
                    isFavorite = false,
                    readsCount = 29
                ),
                PoemEntity(
                    title = "Stars in the Night",
                    author = "Uncle Farhan",
                    category = "Bedtime",
                    content = "Close your eyes, little dreamer dear,\nThe golden moon is shining clear.\nThe twinkling stars are singing a song,\nTo guide your dreams the whole night long.\nSleep softly now till morning light,\nMay peaceful sleep keep you safe tonight.",
                    isFavorite = true,
                    readsCount = 56
                ),
                PoemEntity(
                    title = "The Playful Kitten",
                    author = "Hodan A.",
                    category = "Children's",
                    content = "Pitter-patter go tiny paws,\nA playful kitten with velvet claws.\nChasing butterflies round the lawn,\nPlaying joyfully till the dawn.\nCurling up in a cozy ball,\nThe cutest, sweetest friend of all!",
                    isFavorite = false,
                    readsCount = 18
                ),
                PoemEntity(
                    title = "Bismillah to Begin the Day",
                    author = "Islamic Heritage",
                    category = "Islamic",
                    content = "With Bismillah I start each day,\nIn every single thing I say.\nWith Alhamdu Lillah I give my praise,\nFor blessed nights and sunny days.\nKindness to all and words so sweet,\nGreeting everyone we meet with Salam!",
                    isFavorite = true,
                    readsCount = 67
                ),
                PoemEntity(
                    title = "Fly High Little Dreamer",
                    author = "Coach Rashid",
                    category = "Inspirational",
                    content = "No mountain is ever too high to climb,\nJust take one step, one at a time.\nBelieve in your heart, stand proud and strong,\nYou have a voice, you have a song!\nWith curiosity, passion, and care,\nYou can achieve anything anywhere!",
                    isFavorite = false,
                    readsCount = 24
                )
            )
            database.poemDao().insertPoems(poems)

            // Seed initial safe social posts
            val posts = listOf(
                PostEntity(
                    authorId = "user_3",
                    authorName = "Hodan Artist",
                    authorAvatar = "🎨",
                    content = "I drew a rainbow rocket in Creative Studio today! What colors do you like best? 🚀✨",
                    type = "artwork",
                    likesCount = 15,
                    likedByUser = true,
                    reactions = "❤️:8,🌟:14,👏:6,🎉:9,🚀:11"
                ),
                PostEntity(
                    authorId = "user_2",
                    authorName = "Zayd Explorer",
                    authorAvatar = "🚀",
                    content = "Just scored 120 points on Bubble Pop and unlocked the Star Explorer badge! Keep practicing everyone! ⭐",
                    type = "achievement",
                    likesCount = 22,
                    likedByUser = false,
                    reactions = "❤️:12,🌟:20,👏:15,🎉:18,🚀:14"
                ),
                PostEntity(
                    authorId = "user_4",
                    authorName = "Khadija Reader",
                    authorAvatar = "🦉",
                    content = "My favorite bedtime poem is 'Stars in the Night'. Hearing the audio narration helps me relax after school. 🌙📖",
                    type = "status",
                    likesCount = 9,
                    likedByUser = false,
                    reactions = "❤️:7,🌟:10,👏:5,🎉:4,🚀:2"
                )
            )
            posts.forEach { database.postDao().insertPost(it) }

            // Seed comments
            database.commentDao().insertComment(
                CommentEntity(postId = 1, authorName = "Ayaan & Safiya", authorAvatar = "🦁", content = "Your rainbow rocket looks amazing Hodan! 🌈")
            )
            database.commentDao().insertComment(
                CommentEntity(postId = 1, authorName = "Zayd Explorer", authorAvatar = "🚀", content = "Super creative color choices!")
            )

            // Seed parental settings
            database.parentalDao().saveParentalSettings(
                ParentalSettingsEntity(
                    id = 1,
                    dailyScreenTimeMinutes = 60,
                    minutesUsedToday = 18,
                    contentFilterStrictness = "Strict",
                    requireFriendApproval = true,
                    notificationsEnabled = true,
                    parentPin = "1234"
                )
            )

            // Seed a pending friend request for Parent approval testing
            database.parentalDao().insertFriendRequest(
                FriendRequestEntity(
                    senderName = "Bilal Kid",
                    senderAvatar = "🦊",
                    targetUserId = "user_current",
                    status = "pending"
                )
            )
        }
    }
}
