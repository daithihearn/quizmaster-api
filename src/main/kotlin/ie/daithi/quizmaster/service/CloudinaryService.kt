package ie.daithi.quizmaster.service

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("prod")
class CloudinaryService(
        private val cloudinary: Cloudinary) : CloudService {

    override fun uploadImage(imageUri: String): String {
        val publicId = "quizzes/images/${DigestUtils.md5Hex(imageUri)}"

        // Can we check if it already exists here?

        val params = ObjectUtils.asMap(
                "public_id", publicId,
                "overwrite", true,
                "resource_type", "image"
        )

        return cloudinary.uploader().upload(imageUri, params)["secure_url"] as String
    }

    override fun uploadAudio(audioUri: String): String {
        val publicId = "quizzes/audio/${DigestUtils.md5Hex(audioUri)}"

        // Can we check if it already exists here?

        val params = ObjectUtils.asMap(
                "public_id", publicId,
                "overwrite", true,
                "resource_type", "auto"
        )

        return cloudinary.uploader().upload(audioUri, params)["secure_url"] as String
    }

    override fun uploadVideo(videoUri: String): String {
        val publicId = "quizzes/videos/${DigestUtils.md5Hex(videoUri)}"

        // Can we check if it already exists here?

        val params = ObjectUtils.asMap(
                "public_id", publicId,
                "overwrite", true,
                "resource_type", "video"
        )

        return cloudinary.uploader().upload(videoUri, params)["secure_url"] as String
    }

}