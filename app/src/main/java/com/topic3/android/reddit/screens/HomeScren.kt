import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.topic3.android.reddit.R
import com.topic3.android.reddit.components.JoinedToast
import com.topic3.android.reddit.components.TextPost
import com.topic3.android.reddit.domain.model.PostModel
import com.topic3.android.reddit.domain.model.PostType
import com.topic3.android.reddit.screens.HomeScreenItemType
import com.topic3.android.reddit.screens.TrendingTopicModel
import com.topic3.android.reddit.screens.TrendingTopics
import com.topic3.android.reddit.screens.mapHomeScreenItems
import com.topic3.android.reddit.viewmodel.MainViewModel
import java.util.*
import kotlin.concurrent.schedule

private val trendingItems = listOf( TrendingTopicModel(
    "Compose Tutorial",
    R.drawable.jetpack_composer
),
    TrendingTopicModel(
        "Compose Animations",
        R.drawable.jetpack_compose_animations
    ),
    TrendingTopicModel(
        "Compose Migration",
        R.drawable.compose_migration_crop
    ),
    TrendingTopicModel(
        "DataStore Tutorial",
        R.drawable.data_storage
    ),
    TrendingTopicModel(
        "Android Animations",
        R.drawable.android_animations
    ),
    TrendingTopicModel(
        "Deep Links in Android",
        R.drawable.deeplinking
    ),
)

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val posts: List<PostModel>
            by viewModel.allPosts.observeAsState(listOf())

    var isToastVisible by remember { mutableStateOf(false) }
    val onJoinClickAction: (Boolean) -> Unit ={joined ->
        isToastVisible=joined
        if (isToastVisible){
            Timer().schedule(3000){isToastVisible = false}
        }
    }
    val homeScreenItem = mapHomeScreenItems(posts)
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(
            modifier = Modifier
                .background(color = MaterialTheme.colors.secondary),
            content = {
                items(
                    items = homeScreenItem,
                    itemContent = { item ->
                        if (item.type == HomeScreenItemType.TRENDING) {
                            TrendingTopics(
                                trendingTopics = trendingItems,
                                modifier = Modifier.padding(
                                    top = 16.dp, bottom = 6.dp
                                )
                            )
                        } else if (item.post != null) {

                            val post = item.post
                            if (post.type== PostType.TEXT){
                                TextPost(
                                    post = post,
                                    onJoinButtonClick = onJoinClickAction
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    })
            }
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ){
            JoinedToast(visible = isToastVisible)
        }
    }
}

private fun mapHomeScreenItems(
    posts: List<PostModel>
):List<HomeScreenItem>{
    val homeScreenItems= mutableListOf<HomeScreenItem>()

    homeScreenItems.add(
        HomeScreenItem(HomeScreenItemType.TRENDING)
    )

    posts.forEach{post->
        homeScreenItems.add(
            HomeScreenItem(HomeScreenItemType.POST, post)
        )
    }
    return homeScreenItems
}

private data class HomeScreenItem(
    val type: HomeScreenItemType,
    val post: PostModel? = null
)

private enum class HomeScreenItemType{
    TRENDING,
    POST
}

private data class TrendingTopicModel(
    val text: String,
    @DrawableRes val imageRes: Int = 0
)