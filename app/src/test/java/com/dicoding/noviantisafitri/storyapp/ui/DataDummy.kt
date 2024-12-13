import com.dicoding.noviantisafitri.storyapp.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoriesResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0 until 100) {
            val stories = ListStoryItem(
                id = "story-$i",
                name = "User $i",
                description = "Dummy story description for user $i",
                photoUrl = "https://example.com/story/photo/$i.jpg",
                createdAt = "2024-12-10T10:00:00Z",
                lat = 0.0 + i,
                lon = 0.0 - i
            )
            items.add(stories)
        }
        return items
    }
}