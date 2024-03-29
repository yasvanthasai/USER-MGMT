package in.user.service;

import java.util.List;

import in.user.bidings.ActivateAccount;
import in.user.bidings.Login;
import in.user.bidings.User;

public interface UserMgmtService {
	
	public boolean saveUser(User user);
	
	public boolean activateUserAcc(ActivateAccount activateacc);
	
	public User getUserById(Integer userId);
	
	public List<User> getAllUsers();
	
	public boolean changeAccountStatus(Integer userId, String accStatus);
	
	public String login(Login login);
	
	public String forgotPwd(String email);

	public boolean deleteUserById(Integer userId);
	
	
	
	
}
