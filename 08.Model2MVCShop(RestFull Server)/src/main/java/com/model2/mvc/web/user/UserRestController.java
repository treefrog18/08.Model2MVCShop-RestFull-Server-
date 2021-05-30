package com.model2.mvc.web.user;

import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.user.UserService;


//==> 회원관리 RestController
@RestController
@RequestMapping("/user/*")
public class UserRestController {
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	///Field
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	//setter Method 구현 않음
		
	public UserRestController(){
		System.out.println(this.getClass());
	}
	
	@RequestMapping( value="json/getUser/{userId}", method=RequestMethod.GET )
	public User getUser( @PathVariable String userId ) throws Exception{
		
		System.out.println("/user/json/getUser : GET");
		
		//Business Logic
		return userService.getUser(userId);
	}

	@RequestMapping( value="json/login", method=RequestMethod.POST )
	public User login(	@RequestBody User user,
									HttpSession session ) throws Exception{
	
		System.out.println("/user/json/login : POST");
		//Business Logic
		System.out.println("::"+user);
		User dbUser=userService.getUser(user.getUserId());
		
		if( user.getPassword().equals(dbUser.getPassword())){
			session.setAttribute("user", dbUser);
		}
		
		return dbUser;
	}
	
	@RequestMapping( value="json/addUser", method=RequestMethod.GET )
	public Map addUser( @RequestParam String userId,
			@RequestParam String userName,
			@RequestParam String password) throws Exception{
		
		System.out.println("/user/json/addUser : GET");
		System.out.println(userId);
		System.out.println(userName);
		System.out.println(password);
				
		//Business Logic
		User user = new User();
		user.setUserId(userId);
		user.setUserName(userName);
		user.setPassword(password);
		userService.addUser(user);
		User returnUser = userService.getUser(user.getUserId());
		System.out.println(returnUser);
		
		Map map = new HashMap();
		map.put("returnUser", returnUser);
		map.put("message", "회원가입이 완료되었다.");
		
		return map;
	}
	
	@RequestMapping( value="json/addUser", method=RequestMethod.POST)
	public User addUser ( @RequestBody User user ) throws Exception{
		System.out.println("/user/json/addUser : POST");
		System.out.println("::"+user);
		userService.addUser(user);
		User addUser = userService.getUser(user.getUserId());
		return addUser;
	}
	
	@RequestMapping( value="json/updateUser", method=RequestMethod.POST)
	public Map updateUser(@RequestBody User user) throws Exception{
		
		System.out.println("/user/json/updateUser : POST");
		System.out.println("::"+user);
		Map map = new HashMap();
		String message = "수정이 완료되었다";
		User before = userService.getUser(user.getUserId());
		userService.updateUser(user);
		System.out.println("업뎃완료");
		User after = userService.getUser(user.getUserId());
		map.put("before", before);
		map.put("after", after);
		map.put("message", message);
		
		return map;
	}
	
	@RequestMapping( value="json/listUser", method=RequestMethod.POST )
	public Map listUser( @RequestBody Search search) throws Exception{
		
		System.out.println("/user/json/listUser : POST");
		System.out.println(search);
				
		//Business Logic
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		Map<String , Object> map=userService.getUserList(search);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		map.put("resultPage", resultPage);
		map.put("search", search);

		return map;
	}
	

}