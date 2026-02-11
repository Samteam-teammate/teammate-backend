package sejong.alom.teammate.domain.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import sejong.alom.teammate.domain.member.dto.ProfileListFetchRequest;
import sejong.alom.teammate.domain.member.entity.Profile;

public interface ProfileRepositoryCustom {
	Page<Profile> searchProfiles(ProfileListFetchRequest request, Pageable pageable);
}
