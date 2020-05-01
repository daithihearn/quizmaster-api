package ie.daithi.quizmaster.service

interface CloudService {
    fun uploadImage(imageUri: String): String
}