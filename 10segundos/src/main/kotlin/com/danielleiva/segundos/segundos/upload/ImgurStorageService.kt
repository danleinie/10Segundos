package com.danielleiva.segundos.segundos.upload

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.net.URL
import java.util.*


interface ImgurStorageService : BasicImageStorageService<ImgurImageAttribute, String, String>

@Service
class ImgurStorageServiceImpl(
        private val imgurService: ImgurService
)
    : ImgurStorageService {

    // El par que se devuelve es la URL de la imagen
    // y el hash de borrado
    val logger: Logger = LoggerFactory.getLogger(ImgurStorageService::class.java)


    override fun store(file: MultipartFile) : Optional<ImgurImageAttribute> {

        if (!file.isEmpty) {
            var imgReq =
                    NewImageReq(Base64.getEncoder().encodeToString(file.bytes),
                            /*imgToBase64Data(file),*/
                    file.originalFilename.toString())
            var imgRes = imgurService.upload(imgReq)
            if(imgRes.isPresent)
                return Optional.of(ImgurImageAttribute(imgRes.get().data.id, imgRes.get().data.deletehash))
        }

        return Optional.empty()

    }


    override fun loadAsResource(id: String) : Optional<MediaTypeUrlResource> {
        var response = imgurService.get(id)
        if (response.isPresent) {
            var resource = MediaTypeUrlResource(response.get().data.type, URI.create(response.get().data.link))
            if (resource.exists() || resource.isReadable)
                return Optional.of(resource)
        }

        return Optional.empty()

    }

    override fun delete(deletehash: String) : Unit {
        logger.debug("Eliminando la imagen $deletehash")
        imgurService.delete(deletehash)
    }

    fun getUrl(id : String) : Optional<URL> {
        var resource: Optional<MediaTypeUrlResource>
        try {
            resource = loadAsResource(id)
            if (resource.isPresent) {
                return Optional.of(resource.get().url)
            }
            return Optional.empty()
        }catch (ex: ImgurImageNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Imagen no encontrada")
        }
    }


}

class MediaTypeUrlResource(
        val mediaType: String, var uri: URI) : UrlResource(uri)