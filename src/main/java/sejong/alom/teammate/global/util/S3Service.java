package sejong.alom.teammate.global.util;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Template s3Template;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucket;

	public String upload(MultipartFile file, String dirName) {
		String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
		try {
			var resource = s3Template.upload(bucket, fileName, file.getInputStream());
			return resource.getURL().toString();
		} catch (IOException e) {
			throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
		}
	}

	public void delete(String fileUrl) {
		String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
		s3Template.deleteObject(bucket, key);
	}
}
