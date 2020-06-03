package ie.daithi.quizmaster.service

interface CloudService {
    fun uploadImage(imageUri: String): String
    fun uploadAudio(audioUri: String): String
    fun uploadVideo(videoUri: String): String
}