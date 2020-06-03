package ie.daithi.quizmaster.service

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("dev")
class StubCloudService: CloudService {
    override fun uploadImage(imageUri: String): String {
        return imageUri
    }

    override fun uploadAudio(audioUri: String): String {
        return "/demo/demo.wav"
    }

    override fun uploadVideo(videoUri: String): String {
        return "/demo/demo.mov"
    }
}