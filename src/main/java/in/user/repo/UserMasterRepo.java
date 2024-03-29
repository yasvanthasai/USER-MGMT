package in.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import in.user.entity.UserMaster;

public interface UserMasterRepo extends JpaRepository<UserMaster, Integer> {
	
	public UserMaster findByEmail(String email);
}
