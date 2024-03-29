package in.user.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import in.user.bidings.ActivateAccount;
import in.user.bidings.Login;
import in.user.bidings.User;
import in.user.entity.UserMaster;
import in.user.repo.UserMasterRepo;
import in.user.utils.EmailUtils;

@Service
public class UserMgmtServiceImpl implements UserMgmtService {
	@Autowired
	private UserMasterRepo userMasterRepo;
	
	@Autowired
	private EmailUtils emailUtils;
	
	
	
	@Override
	public boolean saveUser(User user) {
		UserMaster entity = new UserMaster();
		BeanUtils.copyProperties(user, entity);
		
		String pwd = generatePwd();
		entity.setPassword(pwd);
		entity.setAccStatus("In-Active");
		userMasterRepo.save(entity);
		
		UserMaster save = userMasterRepo.save(entity);
		String subject = "registration success";
		String fileName ="REG-EMAIL-BODY.txt";
		String body =readRegEmailBody(entity.getFullname(),entity.getPassword(),fileName);
		emailUtils.sendEmail(user.getEmail(), subject, body);
		
		return save.getUserId()!=null;
	}

	@Override
	public boolean activateUserAcc(ActivateAccount activateacc) {
		
		UserMaster entity = new UserMaster();
		entity.setEmail(activateacc.getEmail());
		entity.setPassword(activateacc.getTempPwd());
		
		Example<UserMaster> of = Example.of(entity);
		
		List<UserMaster> findAll = userMasterRepo.findAll(of);
		
		if(findAll.isEmpty()) {
			return false;
		}else {
			UserMaster userMaster = findAll.get(0);
			userMaster.setPassword(activateacc.getNewPed());
			userMaster.setAccStatus("Acive");
			userMasterRepo.save(userMaster);
			return true;
		}
	}

	@Override
	public User getUserById(Integer userId) {
		Optional<UserMaster> findById = userMasterRepo.findById(userId);
		if(findById.isPresent()) {UserMaster userMaster = findById.get();
		User user = new User();
		BeanUtils.copyProperties(userMaster, user);
		return user;
		}
		
		return null;
	}


	@Override
	public List<User> getAllUsers() {
		List<UserMaster> findAll = userMasterRepo.findAll();
		List<User> users = new ArrayList<>();
		for(UserMaster entity : findAll) {
			
			User user = new User();
			BeanUtils.copyProperties(entity, user);
			
		}
		
		return users;
	}

	@Override
	public boolean deleteUserById(Integer userId) {
		try {
		userMasterRepo.deleteById(userId);
		return true;
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		return false;
	}

	@Override
	public boolean changeAccountStatus(Integer userId, String accStatus) {
		Optional<UserMaster> findId = userMasterRepo.findById(userId);
		if (findId.isPresent()) {
			UserMaster userMaster = findId.get();
			userMaster.setAccStatus(accStatus);
			return true;
		}
		return false;
	}

	@Override
	public String login(Login login) {
		UserMaster entity = new UserMaster();
		entity.setEmail(login.getEmail());
		entity.setPassword(login.getPassword());
		
		Example<UserMaster> of = Example.of(entity);
		List<UserMaster> findAll = userMasterRepo.findAll(of);
		
		if(findAll.isEmpty()) {
			return "Invalid credentals";
		}else {
			UserMaster userMaster = findAll.get(0);
			if(userMaster.getAccStatus().equals("Active")) {
				return "SUCCESS";
			}else {
				return "Account not activated";
				
			}
			
		}
	}

	@Override
	public String forgotPwd(String email) {
		/*
		 * UserMaster entity = new UserMaster(); entity.setEmail(email);
		 * 
		 * Example<UserMaster> of = Example.of(entity); List<UserMaster> findAll =
		 * userMasterRepo.findAll(of); if(findAll.isEmpty()) { return
		 * "Please create an account";
		 * 
		 * } else {String password = findAll.get(0).getPassword(); //send email also
		 * return password; }
		 */
		UserMaster entity = userMasterRepo.findByEmail(email);
		if(entity ==null) {
			return "Invalid email";
		}
		String subject = "Forgot Password";
		String fileName ="RECOVER-PWD.txt";
		String body =readRegEmailBody(entity.getFullname(),entity.getPassword(),fileName);
		 boolean sendEmail = emailUtils.sendEmail(email, subject, body);
		if(sendEmail) {
			return "Password sent to your registired email";
		}
		 return null;
	}

	  private String generatePwd() {

	    // create a string of uppercase and lowercase characters and numbers
	    String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
	    String numbers = "0123456789";

	    // combine all strings
	    String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

	    // create random string builder
	    StringBuilder sb = new StringBuilder();

	    // create an object of Random class
	    Random random = new Random();

	    // specify length of random string
	    int length = 6;

	    for(int i = 0; i < length; i++) {

	      // generate random index number
	      int index = random.nextInt(alphaNumeric.length());

	      // get character specified by index
	      // from the string
	      char randomChar = alphaNumeric.charAt(index);

	      // append the character to string builder
	      sb.append(randomChar);
	    }

	    String randomString = sb.toString();
	   // System.out.println("Random String is: " + randomString);

	  return sb.toString();
	  }
	  
	  private String readRegEmailBody(String fullname, String pwd,String fileName) {
		 
		  String url ="";
		  String mailBody ="";
		  try {
			  FileReader fr = new FileReader(fileName);
			  BufferedReader br = new BufferedReader(fr);
			  
			  StringBuffer buffer = new StringBuffer();
			  String line = br.readLine();
			  while(line!= null) {
				  buffer.append(line);
				  line = br.readLine();
			  }
			  br.close();
			  mailBody = buffer.toString();
			  
			  mailBody = mailBody.replace("{FULLNAME}", fullname);
			  mailBody = mailBody.replace("{TEMP-PWD}", pwd);
			  mailBody = mailBody.replace("{URL}", url);
			  mailBody = mailBody.replace("{PWD}", pwd);
		  }catch(Exception e) {
			  
			  
		  }
		  

		  
		  return mailBody;
		  
		  
	  }

}
