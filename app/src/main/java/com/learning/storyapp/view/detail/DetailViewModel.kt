import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.learning.storyapp.data.repository.UserRepository

class DetailViewModel(private val userRepository: UserRepository): ViewModel() {

    private val storyId = MutableLiveData<String>()

    fun setStoryId(id: String) {
        storyId.value = id
    }
    val detailStory = storyId.switchMap {
        userRepository.getDetailStory(it)
    }
}