package in.user.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.user.bidings.ActivateAccount;
import in.user.bidings.Login;
import in.user.bidings.User;
import in.user.service.UserMgmtService;

@RestController
public class UserRestController {
	
	@Autowired
	private UserMgmtService service;
	
	@PostMapping("/user")
	public ResponseEntity<String> userReg(@RequestBody User user){
		boolean saveUser = service.saveUser(user);
		if(saveUser) {
			return new ResponseEntity<>("Registration Success", HttpStatus.CREATED);
			
		}else {
			return new ResponseEntity<>("Registration Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	@PostMapping("/activate")
	public ResponseEntity activateAcount(@RequestBody ActivateAccount acc) {
		
		boolean isActivated = service.activateUserAcc(acc);
		if(isActivated) {
			return new ResponseEntity<>("Account Activated", HttpStatus.OK);
			
		}else {
			return new ResponseEntity<>("Invalid Temporary Pwd", HttpStatus.BAD_REQUEST);
		}
	}
	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers(){
		List<User> allUsers = service.getAllUsers();
		return new ResponseEntity<>(allUsers,HttpStatus.OK);
	}
	
	@GetMapping("/users/{userId}")
	public ResponseEntity<User> getAllUserById(@PathVariable Integer userId){
		User user = service.getUserById(userId);
		return new  ResponseEntity<User>(user,HttpStatus.OK);
		
		
	}
	@DeleteMapping("/user/{userId}")
	public ResponseEntity<String> deleteUserById(@PathVariable Integer userId){
		
		boolean isDeleted = service.deleteUserById(userId);
		if(isDeleted) {
			return new ResponseEntity<>("Deleted",HttpStatus.OK);
		}else {
			return new ResponseEntity<>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	@GetMapping("/status/{userId}/{status}")
	public ResponseEntity<String> statusChange(@PathVariable Integer userId, @PathVariable String status){
		boolean isChanged = service.changeAccountStatus(userId, status);
		if(isChanged) {
			return new ResponseEntity<>("status changed",HttpStatus.OK);
			
		}else {
			return new ResponseEntity<>("failed to changed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	} 
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Login login){
		
		String status = service.login(login);
		return new ResponseEntity<>(status,HttpStatus.OK);
		
	}
	/*
	 * @GetMapping("/forgotPwd/{email}") public ResponseEntity<String>
	 * forgotPwd(@PathVariable String email){ String status =
	 * service.forgotPwd(email); return new ResponseEntity<>(status,HttpStatus.OK);
	 * }
	 */
	
	@GetMapping("/forgotPwd/{email}")
	public String forgotPwd(@PathVariable String email){
		String status = service.forgotPwd(email);
		return status;
	}
	
	
	
	
	
	
	
	
	
	
	

}
